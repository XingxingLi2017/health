package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xing.dao.CheckItemDao;
import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.pojo.CheckItem;
import com.xing.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/***
 * manage check item
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    CheckItemDao checkItemDao;

    /***
     * add check item
     * @param checkItem
     */
    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    /***
     * get current page of check items
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        /**
         * use page helper plugin of mybatis
         * */
        // put limit ?,? into local thread , dynamically append to sql statements
        PageHelper.startPage(currentPage, pageSize);
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        Long total = page.getTotal();
        List<CheckItem> rows = page.getResult();
        return new PageResult(total, rows);
    }

    /***
     * delete check item by item id
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        Long count = checkItemDao.findCountByCheckItemId(id);
        if(count > 0) {
            // check item is in some check group
            throw new RuntimeException("check item is already in some check groups.");
        }
        checkItemDao.deleteById(id);
    }

    /***
     * edit check item
     * @param checkItem
     */
    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    /***
     *
     * @param id
     * @return
     */
    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    /***
     * query all the check items
     * @return
     */
    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}
