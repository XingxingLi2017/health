package com.xing.dao;

import com.github.pagehelper.Page;
import com.xing.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    public void add(Order order);
    public List<Order> findByCondition(Order order);

    /***
     * find detail order info. include memeber , setmeal details
     * @param id
     * @return
     */
    public Map findById4Detail(Integer id);
    public Integer findOrderCountByDate(String date);
    public Integer findOrderCountAfterDate(String date);
    public Integer findVisitsCountByDate(String date);
    public Integer findVisitsCountAfterDate(String date);
    public void updateById(Order o);

    /***
     * get checkup package rank
     * @return
     */
    public List<Map> findHotSetmeal();

    /***
     * find orders by name or email
     * @param queryString
     * @return
     */
    Page<Map> findByQueryString(String queryString);
}
