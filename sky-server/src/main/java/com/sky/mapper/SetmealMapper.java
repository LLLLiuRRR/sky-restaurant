package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 插入一条套餐条目
     *
     * @param setmeal 套餐Entity
     */
    @AutoFill(value = OperationType.INSERT) //AOP自动填充4个公共字段
    void insert(Setmeal setmeal);

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO DTO
     * @return Page(List) of SetmealVO
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * - ↓根据id删除setmeal时，需判断其status是不是启售态，调用本方法。
     *
     * @param id 套餐id
     * @return Setmeal
     */
    @Select("SELECT * FROM setmeal WHERE id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据id删除套餐
     * - 删除前需判断该套餐是否处于启售态↑
     *
     * @param setmealId 套餐条目id
     */
    @Delete("DELETE FROM setmeal WHERE id = #{id}")
    void deleteById(Long setmealId);

    /**
     * 根据id修改套餐
     *
     * @param setmeal 套餐实体
     */
    @AutoFill(value = OperationType.UPDATE) //AOP自动填充2个公共字段
    void update(Setmeal setmeal);

//    /**
//     * 根据分类id查询套餐的数量
//     * @param id
//     * @return
//     */
//    @Select("select count(id) from setmeal where category_id = #{categoryId}")
//    Integer countByCategoryId(Long id);
}