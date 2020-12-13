package com.xing.service;

import com.xing.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    public void add(List<OrderSetting> list);
    public List<OrderSetting> getOrderSettingByMonth(String dateStr);
    public void editNumberByOrderDate(OrderSetting orderSetting);
}
