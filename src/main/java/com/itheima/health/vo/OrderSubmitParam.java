package com.itheima.health.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderSubmitParam {
    private String name;
    private String sex;
    private String telephone;
    private String validateCode;
    private String idCard;
    private Date orderDate;
    private Integer setMealId;
}
