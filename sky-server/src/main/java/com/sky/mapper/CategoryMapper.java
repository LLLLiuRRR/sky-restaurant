package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 插入一条新分类
     * - id字段表中为主键、自增，故这里SQL插入语句无需设置
     *
     * @param category Entity
     */
    @Insert("INSERT INTO category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);

    /**
     * 根据条件查询
     *
     * @param categoryPageQueryDTO DTO
     * @return Page<Category>
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除
     *
     * @param id 分类id
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    void delete(Long id);

    /**
     * 修改分类
     * - 实体category那些属性非null，就更新哪些。主键id必须提供
     *
     * @param category
     */
    void update(Category category);

    /**
     * 根据指定条件查出所有分类List
     *
     * @param category DTO
     * @return List<Category>
     */
    List<Category> list(Category category);
}
