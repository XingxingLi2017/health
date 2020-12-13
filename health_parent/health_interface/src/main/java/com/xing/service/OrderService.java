package com.xing.service;

import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.entity.Result;

import java.util.List;
import java.util.Map;

public interface OrderService {
    /***
     * place order by param map
     * @param map
     * @return
     * @throws Exception
     */
    public Result order(Map<String, Object> map) throws Exception;

    /***
     * find order by id
     * @param id
     * @return
     */
    public Map<String, Object> findById(Integer id);

    /***
     * get orders by page bean
     * @param pageBean
     */
    public PageResult findPage(QueryPageBean pageBean);

    /***
     * order multiple packages for one member
     * @param formData
     * @param setmealIds
     * @return
     */
    Result orderByBatch(Map formData, List<Integer> setmealIds);

    void confirmOrderById(Integer id);
    void cancelOrderById(Integer id);
}
