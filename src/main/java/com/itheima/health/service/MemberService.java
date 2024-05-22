package com.itheima.health.service;

import com.itheima.health.pojo.Member;

import java.util.List;

/**
 * 会员Service
 */
public interface MemberService {
    /**
     * 添加会员
     * 不存在则创建并返回，存在则更新非空字段并返回
     *
     * @param member
     * @return
     */
    Member createOrUpdate(Member member);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Member findById(Integer id);

    /**
     * 根据月份统计用户数量
     *
     * @param months
     * @return
     */
    List<Integer> countByMonth(List<String> months);
}
