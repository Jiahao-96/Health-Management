package com.itheima.health.pojo.entity;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotSetmealInfo {
    private BigDecimal proportion;
    private String name;
    private Integer setmeal_count;
}
