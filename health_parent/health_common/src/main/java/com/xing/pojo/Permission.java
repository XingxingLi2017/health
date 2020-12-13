package com.xing.pojo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class Permission implements Serializable{
    private Integer id;
    private String name; // permission name
    private String keyword; // used for managing permission
    private String description;
    private Set<Role> roles = new HashSet<Role>(0); // specify the permission for roles

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
