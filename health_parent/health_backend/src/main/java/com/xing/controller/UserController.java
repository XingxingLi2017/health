package com.xing.controller;

import com.xing.constant.MessageConstant;
import com.xing.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.User;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/getUsername")
    public Result getUsername(){
        // get User object from spring security context
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user != null) {
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, user.getUsername());
        } else {
            return new Result(false, MessageConstant.GET_USERNAME_FAIL);
        }
    }
}
