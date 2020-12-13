package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xing.dao.OrderSettingDao;
import com.xing.pojo.OrderSetting;
import com.xing.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = com.xing.service.OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    OrderSettingDao orderSettingDao;

    @Override
    public void add(List<OrderSetting> list) {
        if(list == null || list.size() <= 0) return;

        for(OrderSetting orderSetting : list) {
            long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
            // if existing , update , otherwise , insert new
            if(count > 0) {
                orderSettingDao.editNumberByOrderDate(orderSetting);
            } else {
                orderSettingDao.add(orderSetting);
            }
        }
    }

    @Override
    public List<OrderSetting> getOrderSettingByMonth(String dateStr) {
        // dateStr = yyyy-MM
        String startDate = dateStr + "-1";
        String endDate = dateStr + "-31";
        Map<String,String> map = new HashMap<>();
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        return list;
    }

    @Override
    public void editNumberByOrderDate(OrderSetting orderSetting) {
        long count  = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if(count > 0) {
            orderSettingDao.editNumberByOrderDate(orderSetting);
        } else {
            orderSettingDao.add(orderSetting);
        }
    }
}
