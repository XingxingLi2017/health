package com.xing.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.constant.MessageConstant;
import com.xing.entity.Result;
import com.xing.pojo.Setmeal;
import com.xing.service.SetmealService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference(interfaceClass = com.xing.service.SetmealService.class)
    private SetmealService setmealService;

    @GetMapping("/getAllSetmeal")
    public Result getAllSetmeal(){
        List<Setmeal> list = null;
        try {
            list = setmealService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS , list);
    }

    @GetMapping("/findById")
    public Result findById(int id) {
        Setmeal setmeal = null;
        try {
            setmeal = setmealService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS , setmeal);
    }

    @GetMapping("/findByIdWithoutDetail")
    public Result findByIdWithoutDetail(int id) {
        Setmeal setmeal = null;
        try {
            setmeal = setmealService.findByIdWithoutDetail(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS , setmeal);
    }
}
