package com.itheima.health.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//操作日志实体类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperateLog {
    private Integer id; //主键ID
    private Integer operateUserId; //操作人ID
    private String operateUserName;//操作人用户名
    private LocalDateTime operateTime; //操作时间
    private String className; //操作类名
    private String methodName; //操作方法名
    private String methodParams; //操作方法参数
    private String returnValue; //操作方法返回值
    private Long costTime; //操作耗时
}