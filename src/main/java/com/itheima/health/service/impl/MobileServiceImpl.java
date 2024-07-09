package com.itheima.health.service.impl;
import com.itheima.health.common.MessageConst;
import com.itheima.health.common.RedisConst;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.dao.SetMealDao;
import com.itheima.health.exception.BusinessRuntimeException;
import com.itheima.health.pojo.dto.SubmitDTO;
import com.itheima.health.pojo.dto.ValidateCodeDTO;
import com.itheima.health.pojo.entity.CheckGroup;
import com.itheima.health.pojo.entity.CheckItem;
import com.itheima.health.pojo.entity.Order;
import com.itheima.health.pojo.entity.Setmeal;
import com.itheima.health.properties.AliOssProperties;
import com.itheima.health.service.MobileService;
import com.itheima.health.utils.AliyunSmsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
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
    @Override
    public Order submit(SubmitDTO submitDTO) {
        String redisValidateCode = (String) redisTemplate.opsForValue().get(RedisConst.ORDER + submitDTO.getTelephone());
        if (redisValidateCode == null || !redisValidateCode.equals(submitDTO.getValidateCode())){
            throw new BusinessRuntimeException(MessageConst.VALIDATECODE_ERROR);
        }
        return null;
        //TODO
    }
}
