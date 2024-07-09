package com.itheima.health.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SubmitDTO extends ValidateCodeDTO{
    private Integer setmealId;
    private String sex;
    private Date orderDate;
    private String name;
    private String idCard;

}
