package com.itheima.health.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPageBean implements Serializable {
    private Integer currentPage;
    private Integer pageSize;
    private String queryString;
}
