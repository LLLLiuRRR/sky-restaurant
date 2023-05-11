package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 向表中插入一条目菜品
     *
     * @param dish 菜品
     */
    @AutoFill(value = OperationType.INSERT) //AOP自动填充4个公共字段
    void insert(Dish dish);

    /**
     * 条件分页查询
     *
     * @param dishPageQueryDTO DTO
     * @return List<Dish>
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查询菜品
     *
     * @param id 菜品id
     * @return Dish
     */
    @Select("SELECT * FROM dish WHERE id = #{id}")
    Dish getById(Long id);

    /**
     * 根据主键删除菜品
     *
     * @param id 菜品id
     */
    @Delete("DELETE FROM dish WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id修改菜品
     *
     * @param dish 菜品Entity
     */
    @AutoFill(value = OperationType.UPDATE) //AOP自动填充2个公共字段
    void update(Dish dish);

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId 菜品所属的分类id
     * @return 该分类下的菜品数，没有则为0
     */
    @Select("SELECT COUNT(id) FROM dish WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据给定条件列出符合的菜品条目
     *
     * @param dish 菜品Entity
     * @return List<Dish>
     */
    List<Dish> select(Dish dish);

    /**
     * 根据套餐id查询菜品
     * - 使用场景：SetmealServiceImpl中setStatus方法，启用套餐时须判断旗下关联菜品是否有被禁用的，
     * -    需要根据套餐id查询菜品，获得菜品的status
     * - 注意SQL用的LEFT JOIN保证所有菜品都cover
     *
     * @param setmealId 套餐id
     * @return List of Dish, size could be zero, 因为菜品可能没有套餐与之关联
     */
    @Select("SELECT d.* FROM dish d LEFT JOIN setmeal_dish sd ON d.id = sd.dish_id WHERE sd.setmeal_id = #{setmeadId}")
    List<Dish> getBySetmealId(Long setmealId);
}