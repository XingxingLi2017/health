package com.xing.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.constant.MessageConstant;
import com.xing.constant.RedisConstant;
import com.xing.entity.Result;
import com.xing.service.AccessKeysService;
import com.xing.utils.MailUtils;
import com.xing.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/***
 * send and verify validate code for user
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    @Reference(interfaceClass = com.xing.service.AccessKeysService.class)
    private AccessKeysService accessKeysService;

    // send validate code for reservation
    @PostMapping("/send4Order")
    public Result send4Order(@RequestParam("email") String toEmail){
        int code = ValidateCodeUtils.generateValidateCode(6);
        try {
            Map<String, Object> mailKey = accessKeysService.getMailKey();
            String email = mailKey.get("emailAddress").toString();
            String password = mailKey.get("password").toString();
            MailUtils mailUtils = new MailUtils(email, password);
            String content = String.format("Hello, this is XingHealth physical examination. Your verification code is %d. It will expire in 5 min." , code);
            mailUtils.sendMail(toEmail , content, "XingHealth Verification Code");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        try(Jedis jedis = jedisPool.getResource()){
            jedis.setex(toEmail + "_" + RedisConstant.Message_SENDTYPE_ORDER , 300, code+"");
        }
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    /***
     * login by email verification
     * @param toEmail
     * @return
     */
    @GetMapping("/send4Login")
    public Result send4Login(@RequestParam("email") String toEmail) {
        int code = ValidateCodeUtils.generateValidateCode(6);
        try {
            Map<String, Object> mailKey = accessKeysService.getMailKey();
            String email = mailKey.get("emailAddress").toString();
            String password = mailKey.get("password").toString();
            MailUtils mailUtils = new MailUtils(email, password);
            String content = String.format("Hello, this is XingHealth physical examination. Your verification code for login is %d. It will expire in 5 min." , code);
            mailUtils.sendMail(toEmail , content, "XingHealth Verification Code");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }

        try(Jedis jedis = jedisPool.getResource()) {
            // expiration time 5 min , login type
            jedis.setex(toEmail + "_" + RedisConstant.Message_SENDTYPE_LOGIN , 300, code+"");
        }
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
