package com.xing.dao;

import com.xing.pojo.User;

public interface UserDao {
    public User findByUsername(String username);
}
