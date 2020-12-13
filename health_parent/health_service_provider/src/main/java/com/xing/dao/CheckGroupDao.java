package com.xing.dao;

import com.github.pagehelper.Page;
import com.xing.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    /***
     * add check group to t_checkgroup table and return int id
     * @param checkGroup
     */
    void add(CheckGroup checkGroup);

    /***
     * set relationship in t_checkgroup_checkitem
     * @param map
     */
    public void setCheckGroupAndCheckItem(Map map);

    /***
     * find check group page by condition
     * @param queryString
     * @return
     */
    public Page<CheckGroup> findByCondition(String queryString);

    /***
     * find check group by id
     * @param id
     * @return
     */
    public CheckGroup findById(Integer id);

    /***
     * find check item ids by check group id
     * @param id
     * @return
     */
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    /***
     * edit check group info
     * @param checkGroup
     */
    public void edit(CheckGroup checkGroup);

    /***
     * delete relationship in t_checkgroup_checkitem through check group id
     * @param id
     */
    public void deleteAssocication(Integer id);

    /***
     * delete check group by id
     * @param id
     */
    void deleteById(int id);

    List<CheckGroup> findAll();
}
