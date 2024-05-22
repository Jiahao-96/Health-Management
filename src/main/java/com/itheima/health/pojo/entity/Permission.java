package com.itheima.health.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Serializable{
    private Integer id;
    private String name;        // 权限名称
    private String keyword;     // 权限关键字，用于权限控制
    private String description; // 描述
    private Set<Role> roles = new HashSet<Role>(0);
    public Permission(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
    }
}
