package com.xing.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xing.constant.MessageConstant;
import com.xing.constant.RedisConstant;
import com.xing.entity.Result;
import com.xing.pojo.Member;
import com.xing.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    /***
     * login by email and verification code
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map params, HttpServletResponse response){
        try(Jedis jedis = jedisPool.getResource()) {
            String email = (String)params.get("email");
            String validateCode = jedis.get(email + "_"+ RedisConstant.Message_SENDTYPE_LOGIN);
            String inputCode = (String)params.get("validateCode");
            if(validateCode != null && validateCode.equals(inputCode)) {
                // check if the current user is our member
                Member member = memberService.findByEmail(email);
                if(member == null) {
                    // register the user as new member
                    member = new Member();
                    member.setEmail(email);
                    memberService.add(member);
                }

                // write email into cookie, so user don't need login anymore
                Cookie cookie = new Cookie("login_member_email", email);
                cookie.setPath("/");
                // valid for 30 days
                cookie.setMaxAge(60*60*24*30);
                response.addCookie(cookie);

                // store memeber info into redis for tracking user
                String json = JSON.toJSONString(member);
                jedis.setex(email , 1800 , json);
                return new Result(true, MessageConstant.LOGIN_SUCCESS);
            } else {
                return new Result(false, MessageConstant.VALIDATECODE_ERROR);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Connection Error in Server.");
        }
    }
}
