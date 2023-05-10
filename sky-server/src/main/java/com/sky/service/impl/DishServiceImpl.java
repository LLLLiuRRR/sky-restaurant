package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品管理业务实现
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * - 涉及dish表和dish_flavor表
     *
     * @param dishDTO DTO
     */
    @Transactional
    @Override
    public void add(DishDTO dishDTO) {
        //1-向菜品表插入1条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //补充公共字段
        // 菜品默认启售
        dish.setStatus(StatusConstant.ENABLE);

        // TODO AOP

        //插入数据
        dishMapper.insert(dish);
        //!后续dish_flavor插入数据时需要这里返回生成的主键赋给dish，故xml标签加上useGeneratedKeys="true" keyProperty="id"
        //获取生成的主键id，作为dishFlavor的dishId
        Long dishId = dish.getId();

        //2-向口味表插入0或多条数据
        // 获取口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            //给每个dishFlavor对象设定好dishId属性
            for (DishFlavor df : flavors) {
                df.setDishId(dishId);
            }
            //批量把dishFlavor条目插入表中
            // 可在这里遍历后一条条插入，也可在xml里利用foreach标签批量插入
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品条件分页查询
     *
     * @param dishPageQueryDTO DTO
     * @return PageResult
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //PageHelper设置
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        /* 可以先查dish表，再根据category_id查category表，再构造VO。麻烦。 */
        /* 本例可直接在SQL中JOIN成新表 */
        //查菜品表LEFT JOIN分类表，构造成VO返回List，赋给Page
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids 多个待删菜品id
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //查询一下菜品表，看看售卖状态——dish
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                //当前菜品在售状态，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // TODO 查询一下套餐菜品关系表，查看菜品是否被关联了，如果菜品被套餐关联，则不能删除

        for (Long id : ids) {
            //从菜品表删除一条数据——dish
            dishMapper.deleteById(id);

            //从口味表删除当前菜品关联的口味数据——dish_flavor
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 根据id查询菜品+口味
     *
     * @param id 菜品id
     * @return DishVO
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 根据id查询菜品表，获取菜品的基本信息
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味表，获取口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将查询到的两部分数据封装到VO中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据id修改菜品
     *
     * @param dishDTO DTO
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //1、删除原来的菜品关联的所有口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //2、再重新添加一遍口味(如果有的话)
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //TODO to be checked
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }

        //3、更新菜品表，根据id修改数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(BaseContext.getCurrentId());
        dishMapper.update(dish);
    }

    /**
     * 菜品起售停售
     *
     * @param status 启停售状态
     * @param id     菜品id
     */
    @Override
    public void setStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        dishMapper.update(dish);
    }

    /**
     * 根据分类id列出菜品
     *
     * @param categoryId 分类id
     * @return List<Dish>
     */
    @Override
    public List<Dish> list(Long categoryId) {
        return dishMapper.select(
                Dish.builder()
                        .categoryId(categoryId)
                        .build()
        );
    }

}