package com.itheima.health.pojo.entity;

import lombok.Data;

import java.util.List;

@Data
public class SetmealReport {
    private List<String> setmealNames;
    private List<SetmealNameValue> setmealCount;
}
