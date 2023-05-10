package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO DTO
     * @return Result
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result add(@RequestBody DishDTO dishDTO) {
        dishService.add(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO DTO
     * @return Result<PageResult>
     */
    @GetMapping("/page")
    @ApiOperation("菜品条件分页查询")
    private Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     *
     * @param ids 多个要删除的菜品id的List集合
     * @return Result<String>
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    /* 前端提交的参数形式 "?ids=1,2,3" 本质是字符串，需要加注解@RequestParam才可被集合或数组接收 */
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("|> 删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     *
     * @param id 菜品id
     * @return Result<DishVO>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("|> 根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO DTO
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        log.info("|> 修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售停售
     *
     * @param status 启禁售状态
     * @param id     菜品id
     * @return Result<String>
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> setStatus(@PathVariable Integer status, Long id) {
        dishService.setStatus(status, id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * - 套餐管理添加菜品时中需要用此接口
     *
     * @param categoryId 菜品分类id
     * @return Result<List<Dish>>
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询该分类下所有菜品")
    public Result<List<Dish>> list(Long categoryId) {
        return Result.success(dishService.list(categoryId));
    }
}
