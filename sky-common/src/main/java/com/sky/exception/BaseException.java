package com.sky.exception;

/**
 * 业务异常
 * - 继承自RuntimeException，作为各类业务异常的基类
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
