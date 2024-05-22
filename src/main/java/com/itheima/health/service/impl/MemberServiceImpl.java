package com.itheima.health.service.impl;

import com.itheima.health.dao.MemberDao;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 会员SERVICE实现类
 */
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;

    @Transactional
    @Override
    public Member createOrUpdate(Member member) {
        log.info("[会员-添加]data:{}", member);
        Member pojo = memberDao.selectByPhoneNumber(member.getPhoneNumber());
        if (null != pojo) {
            //会员已存在，则更新
            member.setId(pojo.getId());
            memberDao.updateByIdSelective(member);
        } else {
            //调用DAO插入数据
            member.setRegTime(new Date());
            memberDao.insert(member);
        }
        return memberDao.selectById(member.getId());
    }

    @Override
    public Member findById(Integer id) {
        return memberDao.selectById(id);
    }

    @Override
    public List<Integer> countByMonth(List<String> months) {
        List<Integer> resultLIst = new ArrayList<>();
        List<String> oneMonth = Arrays.asList("01", "03", "05", "07", "08", "10", "12");

        for (String month : months) {
            String endDate = month + "30";
            if (month.contains("02")) {
                endDate = month + ".28";
            } else if (oneMonth.contains(month)) {
                endDate = month + "31";
            }
            Long count = memberDao.countByRegTimeBefore(endDate);
            resultLIst.add(count.intValue());
        }
        return resultLIst;
    }
}
