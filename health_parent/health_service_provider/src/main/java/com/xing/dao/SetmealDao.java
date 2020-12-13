package com.xing.dao;

import com.github.pagehelper.Page;
import com.xing.pojo.QiniuKey;
import com.xing.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    QiniuKey getQiniuKeyById(int id);
    void add(Setmeal setmeal);
    void setSetmealAndCheckGroup(Map<String, Integer> map);
    Page<Setmeal> findByCondition(String queryString);
    List<Setmeal> findAll();
    Setmeal findById4Detail(Integer id);
    List<Map<String, Object>> getSetmealReport();

    /***
     * delete relationship between setmeal and check groups
     * @param id
     */
    void deleteAssocication(Integer id);
    /***
     * delete setmeal by id
     * @param id
     */
    void deleteById(int id);

    /***
     * find check group ids related to the setmeal id
     * @param id
     * @return
     */
    List<Integer> findCheckgroupIdsBySetmealId(int id);

    Setmeal findById(Integer id);
    Setmeal findByIdWithoutDetail(Integer id);
    void updateSetMeal(Setmeal setmeal);
}
