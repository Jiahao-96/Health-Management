package com.itheima.health.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPageBeanDTO implements Serializable {
    private Integer currentPage;
    private Integer pageSize;
    private String queryString;
}
