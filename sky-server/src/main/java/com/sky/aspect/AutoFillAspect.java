package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自动填充的切面类
 * -
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * “切入点”：定义对哪些类的哪些方法进行拦截
     * - (* com.sky.mapper.*.*(..)) 拦截mapper包下任意类名 任意方法名 任意形参 的方法
     * - @annotation(com.sky.annotation.AutoFill) 拦截加了AutoFill注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * “通知”：拦截到方法后进行的操作
     * - 根据insert或update
     */
    @Before("autoFillPointCut()") //在Mapper方法执行前在此补充字段
    public void autoFillAdvice(JoinPoint joinPoint /*“连接点”，即拦截到的方法“对象”*/) {
        log.info("|> 公共字段Autofill...");
        //获取拦截到的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //判断是update型还是insert型操作
        // 获取方法上的@AutoFill注解
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        // 获取注解中的value，以得知操作类型(update/insert)
        OperationType value = annotation.value();

        //获取形参处的实体类
        // 获取拦截到的方法传入的参数
        Object[] args = joinPoint.getArgs();
        /* 编写方法时已约定实体参数放在形参的第1位 */
        if (args == null || args.length == 0) {
            return;
        }
        // 获取形参传来的实体Entity对象
        Object entity = args[0];

        //准备公共字段数据
        LocalDateTime time = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        //判断是INSERT型方法还是UPDATE型方法
        if (value == OperationType.INSERT) {
            // INSERT型方法，补充create_user、create_time、update_user、update_time字段
            try {
                //通过反射为当前实体的公共属性赋值
                // 获得实体对象中的setter方法对象
                Method setCreateTimeMethod = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTimeMethod = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUserMethod = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, java.lang.Long.class);
                Method setUpdateUserMethod = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, java.lang.Long.class);
                // 利用反射调用这个setter方法，传参时间time
                setCreateTimeMethod.invoke(entity, time);
                setUpdateTimeMethod.invoke(entity, time);
                setCreateUserMethod.invoke(entity, id);
                setUpdateUserMethod.invoke(entity, id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //UPDATE型方法，补充update_user和update_time字段
            try {
                //通过反射为当前实体的公共属性赋值
                // 获得实体对象中的setter方法对象
                Method setUpdateTimeMethod = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, java.lang.Long.class);
                // 利用反射调用这个setter方法，传参时间time
                setUpdateTimeMethod.invoke(entity, time);
                setUpdateUserMethod.invoke(entity, id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}