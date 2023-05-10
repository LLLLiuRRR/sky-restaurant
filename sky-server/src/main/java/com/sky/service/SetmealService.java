package com.sky.service;

import com.sky.dto.SetmealDTO;

/**
 * 套餐管理业务接口
 */
public interface SetmealService {


    /**
     * 新增套餐
     *
     * @param setmealDTO DTO
     */
    void add(SetmealDTO setmealDTO);
}
