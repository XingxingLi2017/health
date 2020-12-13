package com.xing.service;

import com.xing.pojo.User;

public interface UserService {
    public User findByUsername(String username);
}
