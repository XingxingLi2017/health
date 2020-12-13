package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xing.dao.CheckGroupDao;
import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.pojo.CheckGroup;
import com.xing.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;

    /***
     * add check group and connect check group with check items
     * @param checkGroup
     * @param checkitemIds
     */
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        // create check group
        checkGroupDao.add(checkGroup);
        // insert relationship into t_checkgroup_checkitem relationship table
        Integer checkGroupId = checkGroup.getId();
        setCheckGroupAndCheckItem(checkGroupId, checkitemIds);
    }

    /***
     * query check group page
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        int currenPage = queryPageBean.getCurrentPage();
        int pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        PageHelper.startPage(currenPage, pageSize);
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /***
     * find check group by id
     * @param id
     * @return
     */
    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    /***
     * find check items' ids through check group's id
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    /***
     * edit check group and association check group with check items
     * @param checkGroup
     * @param checkitemIds
     */
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.deleteAssocication(checkGroup.getId());
        checkGroupDao.edit(checkGroup);
        setCheckGroupAndCheckItem(checkGroup.getId(), checkitemIds);
    }

    /***
     * delete
     * @param id
     */
    @Override
    public void deleteById(int id) {
        checkGroupDao.deleteAssocication(id);
        checkGroupDao.deleteById(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    /***
     * insert checkitemId-checkgroupId pair into check group and check item relationship table
     * @param checkGroupId
     * @param checkitemIds
     */
    private void setCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds) {
        if(checkitemIds != null && checkitemIds.length > 0) {
            Map<String, Integer> map = null;
            for(Integer checkitemId : checkitemIds) {
                map = new HashMap<>();
                map.put("checkgroupId" , checkGroupId);
                map.put("checkitemId" , checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}
