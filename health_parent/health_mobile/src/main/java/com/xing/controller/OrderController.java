package com.xing.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.constant.MessageConstant;
import com.xing.constant.RedisConstant;
import com.xing.entity.Result;
import com.xing.pojo.Order;
import com.xing.service.AccessKeysService;
import com.xing.service.OrderService;
import com.xing.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/***
 * process physical checkup order
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JedisPool jedisPool;

    @Reference(interfaceClass = com.xing.service.OrderService.class)
    private OrderService orderService;

    @Reference(interfaceClass = com.xing.service.AccessKeysService.class)
    private AccessKeysService accessKeysService;

    /***
     * process submitted order info, place order and send confirmation email
     * @param paramMap
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String, Object> paramMap){
        Result res = new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        try(Jedis jedis = jedisPool.getResource()) {
            // check verification code
            String email = (String) paramMap.get("email");
            String inputCode = (String) paramMap.get("validateCode");
            String code = jedis.get(email + "_" + RedisConstant.Message_SENDTYPE_ORDER);
            if(code != null && inputCode != null && code.equals(inputCode)) {
                // put order into DB
                paramMap.put("orderType" , Order.ORDERTYPE_WEIXIN);
                res = orderService.order(paramMap);
            } else {
                return new Result(false, MessageConstant.VALIDATECODE_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }

        // send confirmation email
        if(res.isFlag()) {
            Map<String, Object> keys = accessKeysService.getMailKey();
            MailUtils utils = new MailUtils((String)keys.get("emailAddress") , (String)keys.get("password"));
            String content = String.format("Hi, this is XingHealth. Your appointment of the physical examination on %s is confirmed.", (String)paramMap.get("orderDate"));
            utils.sendMail((String) paramMap.get("email"), content, "XingHealth Reservation Confirmation");
        }
        return res;
    }

    /***
     * find order date, member name, examination package detail by order id
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id) {
        try {
            Map map = orderService.findById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS , map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
