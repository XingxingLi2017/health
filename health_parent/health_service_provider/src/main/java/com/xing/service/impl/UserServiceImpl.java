package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xing.dao.PermissionDao;
import com.xing.dao.RoleDao;
import com.xing.dao.UserDao;
import com.xing.pojo.Role;
import com.xing.pojo.User;
import com.xing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service(interfaceClass = com.xing.service.UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    /***
     * find backend system users by user name
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        // User object includes roles, the role includes permissions
        User user = userDao.findByUsername(username);
        if(user == null) {
            return null;
        }
        user.setRoles(roleDao.findByUserId(user.getId()));
        if(user.getRoles() != null) {
            for(Role r : user.getRoles()) {
                r.setPermissions(permissionDao.findByRoleId(r.getId()));
            }
        }
        return user;
    }
}
