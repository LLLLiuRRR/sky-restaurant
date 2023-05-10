package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 菜品管理业务接口
 */
public interface DishService {


    /**
     * 新增菜品
     *
     * @param dishDTO DTO
     */
    void add(DishDTO dishDTO);

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO DTO
     * @return PageResult
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     *
     * @param ids 多个菜品id
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和口味
     *
     * @param id 菜品id
     * @return DishVO
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据id修改菜品
     *
     * @param dishDTO DTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 菜品起售停售
     *
     * @param status 待设启停售状态
     * @param id     菜品id
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据分类id列出菜品
     *
     * @param categoryId 分类id
     * @return List<Dish>
     */
    List<Dish> list(Long categoryId);
}
