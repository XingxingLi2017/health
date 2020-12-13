package com.xing.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xing.constant.MessageConstant;
import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.entity.Result;
import com.xing.pojo.CheckGroup;
import com.xing.service.CheckGroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    @PreAuthorize("hasAuthority('CHECKGROUP_ADD')")
    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Object> map){
        CheckGroup checkGroup = JSON.parseObject(JSON.toJSONString(map), CheckGroup.class);
        Integer[] checkitemIds =JSON.parseObject(JSON.toJSONString(map.get("checkitemIds")), Integer[].class);
        try {
            checkGroupService.add(checkGroup, checkitemIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = null;
        try {
            pageResult = checkGroupService.pageQuery(queryPageBean);
        } catch (Exception e) {
            return new PageResult(0L, new ArrayList<CheckGroup>());
        }
        return pageResult;
    }

    @GetMapping("/findById")
    public Result findById(int id) {
        CheckGroup checkGroup = null;
        try {
            checkGroup = checkGroupService.findById(id);
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroup);
    }
    @GetMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckgroupId(int id){
        List<Integer> ids = null;
        try {
             ids = checkGroupService.findCheckItemIdsByCheckGroupId(id);
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
        return new Result(false, MessageConstant.QUERY_CHECKITEM_SUCCESS , ids);
    }
    @GetMapping("/findAll")
    public Result findAll(){
        List<CheckGroup> checkGroups = null;
        try {
            checkGroups = checkGroupService.findAll();
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroups);
    }

    @PreAuthorize("hasAuthority('CHECKGROUP_EDIT')")
    @PutMapping("/edit")
    public Result edit(@RequestBody Map<String, Object> map){
        CheckGroup checkGroup = JSON.parseObject(JSON.toJSONString(map.get("formData")), CheckGroup.class);
        Integer[] checkitemIds =JSON.parseObject(JSON.toJSONString(map.get("checkitemIds")), Integer[].class);
        try {
            checkGroupService.edit(checkGroup, checkitemIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }

    @PreAuthorize("hasAuthority('CHECKGROUP_DELETE')")
    @DeleteMapping("/delete")
    public Result deleteById(int id){
        try {
            checkGroupService.deleteById(id);
        } catch (Exception e) {
            return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }
}
