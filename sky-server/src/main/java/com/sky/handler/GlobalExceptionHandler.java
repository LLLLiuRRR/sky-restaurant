package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * - 已让各个业务异常均继承自BaseException。见common子模块的exception目录
     *
     * @param ex 业务异常
     * @return Result
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("|> 异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获违反SQL约束完整性异常
     * - 例如：尝试往数据库表中添加一条数据，其字段内容重复，而表约束该字段为UNIQUE
     * - 场景：尝试添加同username字段的员工，执行employeeMapper.insert(employee)时抛此异常
     * -      尝试添加同name字段的分类，执行categoryMapper.insert(category)时抛此异常
     *
     * @param ex 违反SQL约束完整性异常
     * @return Result
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        log.error("|> 异常信息：{}", message);
        //给前端提示异常信息
        // 需要从异常信息中提取出重复字段内容，供前端显示
        // 异常信息例如："Duplicate entry 'admin' for key 'employee.idx_username'")
        if (message.contains("Duplicate entry")) { //确认下真的是尝试给UNIQUE约束字段插入重复内容的这种异常
            //从异常信息中提取出重名字段内容('admin')，以空格分隔子串
            String[] strings = message.split(" ");
            //字段内容('admin')是数组中第3个元素
            String dupFieldEntry = strings[2];
            //返回错误信息： 'admin'已存在
            return Result.error(dupFieldEntry + MessageConstant.ALREADY_EXISTS);
        }
        //否则不是这种尝试插入重复字段的这类异常，提示“未知错误”
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}
