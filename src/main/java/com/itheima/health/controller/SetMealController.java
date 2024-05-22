package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.common.RedisConst;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetMealService;
import com.itheima.health.util.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author zhangmeng
 * @description 套餐控制器
 * @date 2019/9/26
 **/
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    /**
     * 上传图片
     *
     * @param multipartFile
     * @return
     */
    // @PostMapping("/upload")
    // public Result upload(@RequestParam("imgFile") MultipartFile multipartFile) {
    //     log.info("文件上传，name:{},size:{}", multipartFile.getOriginalFilename(), multipartFile.getSize());
    //     //原始文件名
    //     String originalFileName = multipartFile.getOriginalFilename();
    //     //使用UUID构造不重复的文件名
    //     String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalFileName;
    //
    //     //获取输入流并上传
    //     try (InputStream is = multipartFile.getInputStream()) {
    //         qiniuUtils.upload2Qiniu(is, fileName);
    //     } catch (IOException e) {
    //         log.error("", e);
    //         return new Result(false, MessageConst.PIC_UPLOAD_FAIL);
    //     }
    //
    //     //构造返回值
    //     String pic = qiniuUtils.getUrlPrefix() + fileName;
    //     return new Result(true, MessageConst.PIC_UPLOAD_SUCCESS, pic);
    // }

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/upload")
    public Result upload(MultipartFile imgFile) {
        // 获取原始文件名
        String originalFilename = imgFile.getOriginalFilename();
        log.info("文件上传,原始文件名：{}", originalFilename);

        try {
            // 1.调用AliOssUtil工具类的upload文件上传方法
            // String objectName = UUID.randomUUID() + suffix;
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;

            // 逻辑优化：将图片名加入redis中--为后续清理垃圾图片做准备 https://itheima-health-147.oss-cn-shenzhen.aliyuncs.com/1716018959280_6f6dd2db5f5c4df9b15a627668d7c9ed_bg5.jpg
            redisTemplate.boundSetOps(RedisConst.SETMEAL_PIC_RESOURCES).add(fileName);

            aliOssUtil.upload(imgFile.getBytes(), fileName);
            // 2.返回图片路径结果
            String url = aliOssUtil.getUrlPrefix() + fileName;
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

        // 逻辑优化：将图片名加入redis中--为后续清理垃圾图片做准备
        redisTemplate.boundSetOps(RedisConst.SETMEAL_PIC_RESOURCES).add(setmeal.getImg());

        return new Result(true, MessageConst.ADD_SETMEAL_SUCCESS);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @GetMapping("/findPage")
    public Result findPage(QueryPageBean queryPageBean) {
        //调用DAO层查询并返回
        PageResult pageResult = setMealService.findPage(queryPageBean);
        //构造返回结果并返回
        return new Result(true, MessageConst.QUERY_SETMEAL_SUCCESS, pageResult);
    }
}
