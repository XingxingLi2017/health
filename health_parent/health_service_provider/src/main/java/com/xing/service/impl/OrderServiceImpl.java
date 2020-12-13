package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xing.constant.DistributedLockConstant;
import com.xing.constant.MessageConstant;
import com.xing.dao.MemberDao;
import com.xing.dao.OrderDao;
import com.xing.dao.OrderSettingDao;
import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.entity.Result;
import com.xing.pojo.Member;
import com.xing.pojo.Order;
import com.xing.pojo.OrderSetting;
import com.xing.pojo.Setmeal;
import com.xing.service.OrderService;
import com.xing.utils.DateUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/***
 * service about orders
 */
@Service(interfaceClass = com.xing.service.OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private OrderSettingDao orderSettingDao;

    /***
     * place order by param map , map data :
     * orderDate, email, setmealId, name, telephone, idCard, sex, orderType
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public Result order(Map<String, Object> map) throws Exception {
        // check if the orderDate has set up for appointment
        String orderDate = (String) map.get("orderDate");
        Date dateOrder = DateUtils.parseString2Date(orderDate);

        // distributed lock , start to get lock
        InterProcessMutex lock = new InterProcessMutex(zkClient, DistributedLockConstant.LOCK_BASE_DIR);
        if(zkClient.getState() != CuratorFrameworkState.STARTED) {
            zkClient.start();
            zkClient.blockUntilConnected();
        }
        lock.acquire();

        Order o;
        try {
            OrderSetting orderSetting = orderSettingDao.findByOrderDate(dateOrder);
            if(orderSetting == null) {
                return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
            }
            // check if the reservation capacity
            if(orderSetting.getNumber() <= orderSetting.getReservations()) {
                return new Result(false, MessageConstant.ORDER_FULL);
            }

            // check if the user make the same reservation on same date
            Member member = memberDao.findByEmail((String)map.get("email"));
            if(member != null) {
                // the same person can not order the same setmeal on the same day
                Order condition = new Order(member.getId() , dateOrder, Integer.parseInt((String)map.get("setmealId")));
                List<Order> list = orderDao.findByCondition(condition);
                if(list != null && list.size() > 0) {
                    return new Result(false, MessageConstant.HAS_ORDERED);
                }
            } else {
                // if the user is not a member, register automatically
                member = new Member();
                member.setName((String)map.get("name"));
                member.setPhoneNumber((String)map.get("telephone"));
                member.setIdCard((String)map.get("idCard"));
                member.setSex((String)map.get("sex"));
                member.setRegTime(new Date());
                member.setEmail((String)map.get("email"));
                memberDao.add(member);
            }

            // place order , update reserved number
            o = new Order();
            o.setMemberId(member.getId());
            o.setSetmealId(Integer.parseInt((String)map.get("setmealId")));
            o.setOrderDate(dateOrder);
            o.setOrderType((String)map.get("orderType"));
            o.setOrderStatus(Order.ORDERSTATUS_SUBMITTED); // haven't visit the office
            o.setMemberStatus(Order.MEMBERSTATUS_NO);
            orderDao.add(o);

            // update reservation number
            orderSetting.setReservations(orderSetting.getReservations() + 1);
            orderSettingDao.editReservationsByOrderDate(orderSetting);
        } finally {
            // release lock
            lock.release();
        }
        return new Result(true, MessageConstant.ORDER_SUCCESS, o.getId());
    }

    @Override
    public Map<String, Object> findById(Integer id) {
        Map map = orderDao.findById4Detail(id);
        if(map != null) {
            Date orderDate = (Date)map.get("orderDate");
            try {
                map.put("orderDate" , DateUtils.parseDate2String(orderDate));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public PageResult findPage(QueryPageBean pageBean) {
        Integer currentPage = pageBean.getCurrentPage();
        Integer pageSize = pageBean.getPageSize();
        String queryString = pageBean.getQueryString();
        if(queryString != null) {
            queryString = queryString.trim();
        }

        PageHelper.startPage(currentPage, pageSize);
        Page<Map> page = orderDao.findByQueryString(queryString);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = null;
        for(Map order : page.getResult()) {
            Member m = memberDao.findById((Integer)order.get("member_id"));
            map = new HashMap<>();
            try {
                map.put("orderDate", DateUtils.parseDate2String((Date)order.get("orderDate")));
                map.put("name", m.getName());
                map.put("email", m.getEmail());
                map.put("orderType", order.get("orderType"));
                map.put("orderStatus", order.get("orderStatus"));
                map.put("memberStatus", order.get("memberStatus"));
                map.put("id", order.get("id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(map);
        }
        return new PageResult(page.getTotal(), list);
    }

    @Override
    public Result orderByBatch(Map formData, List<Integer> setmealIds) {
        Result res = new Result(false, "You didn't select any packages.");

        for(Integer setmealId : setmealIds) {
            Map map = new HashMap(formData);
            map.put("setmealId", setmealId+"");
            try {
                res = order(map);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(res.getMessage());
            }
            if(!res.isFlag()) {
                throw new RuntimeException(res.getMessage());
            }
        }
        return res;
    }

    @Override
    public void confirmOrderById(Integer id) {
        Order o = new Order();
        o.setId(id);
        o.setOrderStatus(Order.ORDERSTATUS_YES);
        orderDao.updateById(o);
    }

    @Override
    public void cancelOrderById(Integer id) {
        Order o = new Order();
        o.setId(id);
        o.setOrderStatus(Order.ORDERSTATUS_NO);
        orderDao.updateById(o);
        List<Order> list = orderDao.findByCondition(o);
        o = list.get(0);
        Date orderDate = o.getOrderDate();
        try {
            int reservations = orderDao.findOrderCountByDate(DateUtils.parseDate2String(orderDate));
            orderSettingDao.editReservationsByOrderDate(new OrderSetting(orderDate, reservations));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
