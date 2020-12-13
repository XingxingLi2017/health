package com.xing.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.pojo.Permission;
import com.xing.pojo.Role;
import com.xing.pojo.User;
import com.xing.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpringSecurityUserService implements UserDetailsService {

    @Reference(interfaceClass = com.xing.service.UserService.class)
    private UserService userService;

    /***
     * query user info from DB
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.findByUsername(s);
        if(user == null) {
            return null;
        } else {
            // authorize
            List<GrantedAuthority> list = new ArrayList<>();
            if(user.getRoles() != null) {
                for(Role role : user.getRoles()) {
                    list.add(new SimpleGrantedAuthority(role.getKeyword()));
                    if(role.getPermissions() != null) {
                        for(Permission p : role.getPermissions()) {
                            list.add(new SimpleGrantedAuthority(p.getKeyword()));
                        }
                    }
                }
            }
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), list);
        }
    }
}
