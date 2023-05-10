package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT令牌配置属性类，用于从yml配置文件中读取配置存入本类属性，并作为bean。
 * 将来在JwtTokenAdminInterceptor拦截器类中注入本bean，调用get方法获取JWT令牌配置信息。
 */
@Component //bean
@ConfigurationProperties(prefix = "sky.jwt") //定位yml文件的sky.jwt配置项，读取各配置，赋给本类的各个属性
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
