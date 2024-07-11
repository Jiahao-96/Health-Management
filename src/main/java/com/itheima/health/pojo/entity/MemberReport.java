package com.itheima.health.pojo.entity;


import lombok.Data;

import java.util.List;

@Data
public class MemberReport {
    private List<String> months;
    private List<Integer> memberCount;

}
