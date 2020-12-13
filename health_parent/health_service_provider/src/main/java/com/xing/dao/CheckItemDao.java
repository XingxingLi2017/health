package com.xing.dao;

import com.github.pagehelper.Page;
import com.xing.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {

    void add(CheckItem checkItem);
    /***
     * result is automatically pack into Page object by page helper
     * @param queryString
     * @return
     */
    Page<CheckItem> selectByCondition(String queryString);

    /***
     * delete check item by id
     * @param id
     */
    void deleteById(Integer id);

    /***
     * get whether check item is in check group
     * @param id
     * @return
     */
    Long findCountByCheckItemId(Integer id);

    /***
     * edit check item
     * @param checkItem
     */
    void edit(CheckItem checkItem);

    /***
     *
     * @param id
     * @return
     */
    CheckItem findById(Integer id);

    /***
     * query all the items
     * @return
     */
    List<CheckItem> findAll();
}
