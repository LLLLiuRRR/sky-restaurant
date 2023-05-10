package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealMapper {

    /**
     * 插入一条套餐条目
     *
     * @param setmeal 套餐Entity
     */
    void insert(Setmeal setmeal);

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO DTO
     * @return Page<SetmealVO>
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
