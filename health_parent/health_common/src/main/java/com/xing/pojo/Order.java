package com.xing.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * appointment info
 */
public class Order implements Serializable{
    public static final String ORDERTYPE_TELEPHONE = "telephone order";
    public static final String ORDERTYPE_WEIXIN = "we_chat order";
    public static final String MEMBERSTATUS_YES = "attended";
    public static final String MEMBERSTATUS_NO = "absent";
    public static final String ORDERSTATUS_YES = "confirmed";
    public static final String ORDERSTATUS_NO = "cancelled";
    public static final String ORDERSTATUS_SUBMITTED = "submitted";
    private Integer id;
    private Integer memberId;// member id
    private Date orderDate;
    private String orderType;   // wechat or telephone
    private String orderStatus;// cancelled or confirmed
    private Integer setmealId;// check setmeal id
    private String memberStatus; // if member attended

    public String getMemberStatus() {
        return memberStatus;
    }
    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }


    public Order() {
    }

    public Order(Integer id) {
        this.id = id;
    }

    public Order(Integer memberId, Date orderDate, String orderType, String orderStatus, Integer setmealId) {
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.setmealId = setmealId;
    }

    public Order(Integer memberId, Date orderDate,  Integer setmealId) {
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.setmealId = setmealId;
    }

    public Order(Integer id, Integer memberId, Date orderDate, String orderType, String orderStatus, String memberStatus, Integer setmealId) {
        this.id = id;
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.setmealId = setmealId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getSetmealId() {
        return setmealId;
    }

    public void setSetmealId(Integer setmealId) {
        this.setmealId = setmealId;
    }
}
