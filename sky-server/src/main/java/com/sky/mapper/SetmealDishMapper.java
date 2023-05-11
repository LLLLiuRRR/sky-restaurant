package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 批量插入多条setmealDish中间表条目(关联关系)(动态SQL)
     *
     * @param setmealDishList List of SetmealDish
     */
    void insertBatch(List<SetmealDish> setmealDishList);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * - setmeal_dish表中可能根本没有此setmeal_id，也可能有多个
     *
     * @param setmealId 待删套餐id
     */
    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询套餐和菜品的关联关系
     * - 用户套餐管理的回显，构造VO
     *
     * @param setmealId 套餐id
     * @return List of SetmealDish
     */
    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

//    /**
//     * 根据菜品id查询关联的套餐id
//     * @param ids
//     * @return
//     */
//    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
