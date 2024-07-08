package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.pojo.dto.QueryPageBeanDTO;
import com.itheima.health.pojo.entity.Setmeal;
import com.itheima.health.pojo.result.PageResult;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.service.SetMealService;
import com.itheima.health.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result upload(MultipartFile imgFile) {
        // 获取原始文件名
        String originalFilename = imgFile.getOriginalFilename();
        log.info("文件上传,原始文件名：{}", originalFilename);

        try {
            // 1.调用AliOssUtil工具类的upload文件上传方法
            // String objectName = UUID.randomUUID() + suffix;
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;

            aliOssUtil.upload(imgFile.getBytes(), fileName);//其实这个时候已经返回了正确的url
            // 2.返回图片路径结果
            String url = aliOssUtil.getUrlPrefix() + fileName;//不知道为啥要再平均获取，可能后面有用吧
            log.info("url: {}", url);
            return new Result(true, MessageConst.PIC_UPLOAD_SUCCESS, url);
        } catch (IOException e) {
            log.info("文件上传失败！！！{}", e.getMessage());
            return new Result(false, MessageConst.PIC_UPLOAD_FAIL);
        }
    }

    /**
     * 添加套餐
     *
     * @param setmeal       套餐基本信息
     * @param checkgroupIds 检查组ID列表
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds) {
        log.info("[套餐-添加]data:{},checkgroupIds:{}", setmeal, checkgroupIds);
        if (!StringUtils.isEmpty(setmeal.getImg())) {
            String img = setmeal.getImg().replace(aliOssUtil.getUrlPrefix(), "");
            setmeal.setImg(img);
        }
        setMealService.add(setmeal, checkgroupIds);

        return new Result(true, MessageConst.ADD_SETMEAL_SUCCESS);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @GetMapping("/findPage")
    public Result findPage(QueryPageBeanDTO queryPageBean) {
        //调用DAO层查询并返回
        PageResult pageResult = setMealService.findPage(queryPageBean);
        //构造返回结果并返回
        return new Result(true, MessageConst.QUERY_SETMEAL_SUCCESS, pageResult);
    }
}
