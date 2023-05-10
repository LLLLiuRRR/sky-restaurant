package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类管理业务实现
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增分类
     *
     * @param categoryDTO DTO
     */
    @Override
    public void add(CategoryDTO categoryDTO) {
        //准备Entity，补充字段
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        //TODO AOP

        // 新菜品status默认为禁用(0)
        category.setStatus(StatusConstant.DISABLE);
        //持久层插入数据
        categoryMapper.insert(category);
    }

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO DTO
     * @return PageResult
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //查数据库
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        //构造PageResult并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id删除分类
     *
     * @param id 分类id
     */
    @Override
    public void deleteById(Long id) {
        //判断当前分类是否关联了菜品
        // 查菜品表此分类id下有几个条目
        Integer dishCount = dishMapper.countByCategoryId(id);
        // 如有，则该分给还有菜品与之关联，不能删，抛异常
        if (dishCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //TODO 判断当前分类是否关联了套餐

        //传参id删除该分类
        categoryMapper.delete(id);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO DTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        //构建Entity
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        //补全字段
        //TODO AOP
        categoryMapper.update(category);
    }

    /**
     * 根据条件列出所有分类，不分页
     *
     * @param category DTO
     * @return List<Category>
     */
    @Override
    public List<Category> list(Category category) {
        return categoryMapper.list(category);
    }

    /**
     * 根据分类id启用/禁用分类
     *
     * @param status 状态：启用1，禁用0
     * @param id 分类id
     */
    @Override
    public void toggleStatus(Integer status, Long id) {
        //利用现有update方法。构造Category实体
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateUser(BaseContext.getCurrentId())
                .updateTime(LocalDateTime.now())
                .build();
        categoryMapper.update(category);
    }
}
