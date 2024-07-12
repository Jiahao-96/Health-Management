package com.itheima.health.service.impl;
import com.itheima.health.common.MessageConst;
import com.itheima.health.common.RedisConst;
import com.itheima.health.dao.*;
import com.itheima.health.exception.BusinessRuntimeException;
import com.itheima.health.pojo.dto.SubmitDTO;
import com.itheima.health.pojo.dto.ValidateCodeDTO;
import com.itheima.health.pojo.entity.*;
import com.itheima.health.properties.AliOssProperties;
import com.itheima.health.service.MobileService;
import com.itheima.health.utils.AliyunSmsTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MobileServiceImpl implements MobileService {


    @Autowired
    private AliyunSmsTemplate aliyunSmsTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SetMealDao setMealDao;
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Autowired
    private AliOssProperties aliOssProperties;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrdersettingDao ordersettingDao;


    /**
     * 向阿里云短信服务获取验证码并存入缓存
     * @param type
     * @param telephone
     */
    @Override
    public void send(String type, String telephone) {
        if(type.equals(RedisConst.MOBILE_SIGNIN)){
            //登录验证码
            String code = aliyunSmsTemplate.sendSms(telephone);
            if(code == null){
                throw new BusinessRuntimeException(MessageConst.SEND_VALIDATECODE_FAIL);
            }
            redisTemplate.opsForValue().set(RedisConst.MOBILE_SIGNIN+telephone,code);
        }else if(type.equals(RedisConst.ORDER)){
            //预约验证码
            String code = aliyunSmsTemplate.sendSms(telephone);
            if(code == null){
                throw new BusinessRuntimeException(MessageConst.SEND_VALIDATECODE_FAIL);
            }
            redisTemplate.opsForValue().set(RedisConst.ORDER+telephone,code);
        }
    }

    /**
     * 登录功能验证码校验--从缓存中取出手机号与类型对应的验证码比对是否一致
     * @param validateCodeDTO
     */
    @Override
    public void smsLogin(ValidateCodeDTO validateCodeDTO) {
        String redisValidateCode = (String) redisTemplate.opsForValue().get(RedisConst.MOBILE_SIGNIN + validateCodeDTO.getTelephone());
        if (redisValidateCode == null || !redisValidateCode.equals(validateCodeDTO.getValidateCode())){
            throw new BusinessRuntimeException(MessageConst.VALIDATECODE_ERROR);
        }
    }

    /**
     * 套餐展示
     * @return
     */
    @Override
    public List<Setmeal> getSetmeal() {
        List<Setmeal> setmeals = setMealDao.selectAll();
        setmeals.forEach(setmeal -> {
            // 构造图片完整路径
            setmeal.setImg(aliOssProperties.getUrlPrefix()+setmeal.getImg());
        });
        return setmeals;
    }

    /**
     * 套餐详情查看
     * @param id
     * @return
     */
    @Override
    public Setmeal findById(String id) {
        Setmeal setmeal = setMealDao.findById(id);
        setmeal.setImg(aliOssProperties.getUrlPrefix() + setmeal.getImg());
        List<CheckGroup> checkGroups = setMealDao.selectCheckGroupBySetmealId(id);
        setmeal.setCheckGroups(checkGroups);
        for (CheckGroup checkGroup : checkGroups) {
            List<CheckItem> checkItems = checkGroupDao.selectCheckItemsByCheckGroupId(checkGroup.getId());
            checkGroup.setCheckItems(checkItems);
        }
        return setmeal;
    }

    /**
     * 提交预约
     * @param submitDTO
     * @return
     */
    @Transactional
    @Override
    public Order submit(SubmitDTO submitDTO) {
        //校验验证码
        String redisValidateCode = (String) redisTemplate.opsForValue().get(RedisConst.ORDER + submitDTO.getTelephone());
        if (redisValidateCode == null || !redisValidateCode.equals(submitDTO.getValidateCode())){
            throw new BusinessRuntimeException(MessageConst.VALIDATECODE_ERROR);
        }
        //获取会员信息
        Member member = memberDao.searchMemberByPhoneNumber(submitDTO.getTelephone());
        if (member == null){
            member = new Member();
            BeanUtils.copyProperties(submitDTO,member);
            member.setPhoneNumber(submitDTO.getTelephone());
            member.setRegTime(new Date());
            memberDao.insertMember(member);
        }
        //查询会员是否在该天已经预约了
        Integer order = orderDao.searchOrderByMemberIdAndOrderDate(member.getId(), submitDTO.getOrderDate());
        if (order > 0){
            throw new BusinessRuntimeException(MessageConst.HAS_ORDERED);
        }
        //查询是否预约已满
        Integer number = orderDao.searchOrderByMemberIdAndOrderDate(null,submitDTO.getOrderDate());
        Integer maxNumber = ordersettingDao.searchOrdersettingNumber(submitDTO.getOrderDate());
        if(number >= maxNumber){
            throw new BusinessRuntimeException(MessageConst.ORDER_FULL);
        }

        //封装预约信息
        Order newOrder = Order.builder().
                memberId(member.getId()).
                orderDate(submitDTO.getOrderDate()).
                orderType("微信预约").
                orderStatus("未到诊").
                setmealId(submitDTO.getSetmealId()).build();
        //插入到数据库中的预约表
        orderDao.insertOrder(newOrder);

        //响应给前端
        return newOrder;

    }

    /**
     * 根据id查询成功信息
     * @param id
     * @return
     */
    @Override
    public OrderInfo searchOrderSucessBySetmealId(Integer id) {
        //order表、成员表，套餐表
        OrderInfo orderInfo = orderDao.searchOrderSucessBySetmealId(id);
        return orderInfo;
    }
}
