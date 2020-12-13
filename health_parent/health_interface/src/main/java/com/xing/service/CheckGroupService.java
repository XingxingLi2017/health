package com.xing.service;

import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {
    /***
     * add check group and relate check group with check items
     * @param checkGroup
     * @param checkitemIds
     */
    void add(CheckGroup checkGroup, Integer[] checkitemIds);

    /***
     * query check group page
     * @param queryPageBean
     * @return
     */
    PageResult pageQuery(QueryPageBean queryPageBean);

    /***
     * find check group by id
     * @param id
     * @return
     */
    public CheckGroup findById(Integer id);

    /***
     * find check items' ids by check gruop id
     * @param id
     * @return
     */
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    /***
     * edit check group's basic info and related check items
     * @param checkGroup
     * @param checkitemIds
     */
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    /***
     * delete check group by id
     * @param id
     */
    void deleteById(int id);

    /***
     * find all check groups
     * @return
     */
    List<CheckGroup> findAll();
}
