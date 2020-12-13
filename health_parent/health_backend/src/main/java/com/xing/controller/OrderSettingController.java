package com.xing.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.constant.MessageConstant;
import com.xing.entity.Result;
import com.xing.pojo.OrderSetting;
import com.xing.service.OrderSettingService;
import com.xing.utils.POIUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference(interfaceClass = com.xing.service.OrderSettingService.class)
    private OrderSettingService orderSettingService;

    @PreAuthorize("hasAuthority('ORDERSETTING')")
    @PostMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile) {
        try {
            // read excel file through POI lib
            List<String[]> list = POIUtils.readExcel(excelFile);
            List<OrderSetting> data = new ArrayList<>();
            SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
            for(String[] row: list) {
                String orderDate = row[0].trim();
                String number = row[1].trim();
                if(orderDate.length() > 0 && number.length() > 0) {
                    OrderSetting e = new OrderSetting(formater.parse(orderDate) , Integer.parseInt(number));
                    data.add(e);
                }
            }
            orderSettingService.add(data);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false , MessageConstant.IMPORT_ORDERSETTING_FAIL);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Result(false , MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
        return new Result(true , MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
    }

    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(@RequestParam("date") String dateStr){
        // dateStr yyyy-MM
        List<OrderSetting> list = null;
        try {
            list = orderSettingService.getOrderSettingByMonth(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false , MessageConstant.GET_ORDERSETTING_FAIL);
        }
        return new Result(true , MessageConstant.GET_ORDERSETTING_SUCCESS, list);
    }

    @PreAuthorize("hasAuthority('ORDERSETTING')")
    @PutMapping("/editNumberByOrderDate")
    public Result editNumberByOrderDate(@RequestBody OrderSetting orderSetting) {
        try {
            orderSettingService.editNumberByOrderDate(orderSetting);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
        return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
    }
}
