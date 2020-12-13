package com.xing.service;

import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.pojo.QiniuKey;
import com.xing.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    /***
     * add setmeal and associate setmeal with checkgroups
     * @param setmeal
     * @param checkgroupIds
     */
     void add(Setmeal setmeal, Integer[] checkgroupIds);
     PageResult findPage(QueryPageBean queryPageBean);
     List<Setmeal> findAll();
     Setmeal findById(Integer id);
     Setmeal findByIdWithoutDetail(Integer id);
     List<Map<String, Object>> getSetmealReport();

    /***
     * get key of image resource server
     * @return
     */
     QiniuKey getQiniuKey();

    void deleteById(int id);

    /***
     * find checkgroup ids related to the setmeal id
     * @param id
     * @return
     */
    List<Integer> findCheckgroupIdsBySetmealId(int id);

    void updateSetMeal(Setmeal setmeal , Integer[] checkgroupIds);
}
