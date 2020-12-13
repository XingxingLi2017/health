package com.xing.service;

import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.pojo.CheckItem;

import java.util.List;

/***
 * manage check item
 */
public interface CheckItemService {
    /***
     * add new check item
     * @param checkItem
     */
    void add(CheckItem checkItem);

    /***
     * query page through request query condition
     * @param queryPageBean
     * @return
     */
    PageResult pageQuery(QueryPageBean queryPageBean);

    /***
     * delete check item through item id
     * @param id
     */
    void deleteById(Integer id);

    /***
     * edite check item info
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
     * query all check items
     * @return
     */
    List<CheckItem> findAll();
}
