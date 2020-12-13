package com.xing.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xing.constant.MessageConstant;
import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.entity.Result;
import com.xing.pojo.Order;
import com.xing.service.AccessKeysService;
import com.xing.service.OrderService;
import com.xing.utils.DateUtils;
import com.xing.utils.MailUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @Reference
    private AccessKeysService accessKeysService;

    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean pageBean){
        try {
            return orderService.findPage(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new PageResult(0L, new ArrayList<Map<String, Object>>());
        }
    }

    @PreAuthorize("hasAuthority('ORDERSETTING')")
    @PostMapping("/add")
    public Result add(@RequestBody Map params) throws Exception{
        Map formData = JSON.parseObject(JSON.toJSONString(params.get("formData")), Map.class);
        List<Integer> setmealIds = JSON.parseObject(JSON.toJSONString(params.get("setmealIds")), List.class);
        formData.put("orderType", Order.ORDERTYPE_TELEPHONE);

        Result res = new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);

        try {
            res = orderService.orderByBatch(formData, setmealIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        // send confirmation email
        if(res.isFlag()) {
            Map<String, Object> keys = accessKeysService.getMailKey();
            MailUtils utils = new MailUtils((String)keys.get("emailAddress") , (String)keys.get("password"));
            Date orderDate = null;
            orderDate = DateUtils.parseString2Date(formData.get("orderDate").toString());
            String content = null;
            content = String.format("Hi, this is XingHealth. Your appointment of the physical examination on %s is confirmed.", DateUtils.parseDate2String(orderDate));
            utils.sendMail(formData.get("email").toString(), content, "XingHealth Reservation Confirmation");

        }
        return res;
    }

    @PreAuthorize("hasAuthority('ORDERSETTING')")
    @PutMapping("/confirm")
    public Result confirm(Integer id) {
        try {
            orderService.confirmOrderById(id);
            return new Result(true, "Confirming reservation successfully.");
        } catch (Exception e) {
            return new Result(false, "Confirming reservation failed.");
        }
    }

    @PreAuthorize("hasAuthority('ORDERSETTING')")
    @PutMapping("/cancel")
    public Result cancel(Integer id) {
        try {
            orderService.cancelOrderById(id);
            return new Result(true, "Canceling reservation successfully.");
        } catch (Exception e) {
            return new Result(false, "Canceling reservation failed.");
        }
    }
}
