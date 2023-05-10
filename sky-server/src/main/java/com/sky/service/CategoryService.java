package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * 分类管理业务接口
 */
public interface CategoryService {

    /**
     * 新增分类
     *
     * @param categoryDTO DTO
     */
    void add(CategoryDTO categoryDTO);

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO DTO
     * @return PageResult
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     *
     * @param id 分类id
     */
    void deleteById(Long id);

    /**
     * 修改分类(id, status, name)
     *
     * @param categoryDTO DTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 根据条件查询分类
     *
     * @param category DTO
     * @return List<Category>
     */
    List<Category> list(Category category);

    /**
     * 根据分类id启用/禁用分类
     *
     * @param status 状态：启用1，禁用0
     * @param id 分类id
     */
    void toggleStatus(Integer status, Long id);
}
