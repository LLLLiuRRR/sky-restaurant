package com.sky.mapper;

import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealMapper {

    /**
     * 插入一条套餐条目
     *
     * @param setmeal 套餐Entity
     */
    void insert(Setmeal setmeal);
}
