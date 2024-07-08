package com.itheima.health.controller.mobile;

import com.itheima.health.common.MessageConst;
import com.itheima.health.pojo.dto.SubmitDTO;
import com.itheima.health.pojo.dto.ValidateCodeDTO;
import com.itheima.health.pojo.entity.Order;
import com.itheima.health.pojo.entity.Setmeal;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.service.MobileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mobile")
@Slf4j
public class MobileController {
    @Autowired
    private MobileService mobileService;

    /**
     * 发送验证码
     * @param type
     * @param telephone
     * @return
     */
    @PostMapping("/validateCode/send")
    public Result send(String type , String telephone){
        mobileService.send(type,telephone);
        return new Result(true, MessageConst.SEND_VALIDATECODE_SUCCESS);
    }

    /**
     * 校验验证码
     * @param validateCodeDTO
     * @return
     */
    @PostMapping("/login/smsLogin")
    public Result smsLogin(@RequestBody ValidateCodeDTO validateCodeDTO){
        mobileService.smsLogin(validateCodeDTO);
        return new Result(true, MessageConst.SEND_VALIDATECODE_SUCCESS);
    }

    /**
     * 套餐展示
     * @return
     */
    @GetMapping("/setmeal/getSetmeal")
    public Result getSetmeal(){
        List<Setmeal> setmeals = mobileService.getSetmeal();
        return new Result(true,MessageConst.QUERY_SETMEAL_SUCCESS,setmeals);
    }

    /**
     * 套餐详情查看
     * @return
     */
    @GetMapping("/setmeal/findById")
    public Result findById(String id){

        Setmeal setmeal = mobileService.findById(id);
        log.info(setmeal.toString());
        return new Result(true,MessageConst.QUERY_SETMEAL_SUCCESS,setmeal);
    }

    /**
     * 提交预约 TODO
     * @param submitDTO
     * @return
     */
    @PostMapping("/order/submit")
    public Result submit(SubmitDTO submitDTO){
        Order order = mobileService.submit(submitDTO);
        return new Result(true, MessageConst.ORDER_SUCCESS,order);
    }


}
