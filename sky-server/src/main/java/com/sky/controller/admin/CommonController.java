package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result upload(MultipartFile file/*注意此形参名须与前端一致，见接口文档*/) {
        log.info("|> 文件上传: {}", file);
        //构造随机文件名，利用UUID
        // 获取文件全名
        String originalFilename = file.getOriginalFilename();
        // 截取扩展名
        String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成UUID文件名、拼接扩展名
        String newFileName = UUID.randomUUID().toString() + extName;
        //调用工具类，上传
        try {
            String fileUrl = aliOssUtil.upload(file.getBytes(), newFileName);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("|> 文件上传失败!");
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
