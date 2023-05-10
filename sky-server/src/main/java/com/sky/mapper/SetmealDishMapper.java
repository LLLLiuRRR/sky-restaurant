package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 批量插入多条setmealDish中间表条目(关联关系)(动态SQL)
     *
     * @param setmealDishList List<SetmealDish>
     */
    void insertBatch(List<SetmealDish> setmealDishList);
}
