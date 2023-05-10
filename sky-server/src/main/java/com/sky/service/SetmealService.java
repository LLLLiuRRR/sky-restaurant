package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

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

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO DTO
     * @return PageResult
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
