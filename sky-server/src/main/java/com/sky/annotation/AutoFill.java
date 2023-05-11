package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识公共字段自动填充的注解
 */
@Target(ElementType.METHOD) //注解应用目标：方法
@Retention(RetentionPolicy.RUNTIME) //保留期至：运行时
public @interface AutoFill {

    /**
     * 指定操作类型(UPDATE、INSERT)
     * - INSERT需要填充4条公共字段 create_user create_time update_user update_time
     * - UPDATE需要填充2条公共字段 update_user update_time
     *
     * @return OperationType枚举类实例：UPDATE或INSERT
     */
    OperationType value();
}
