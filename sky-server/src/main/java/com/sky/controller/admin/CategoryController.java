package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO DTO
     * @return Result
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result add(@RequestBody CategoryDTO categoryDTO) {
        log.info("|> 新增分类: {}", categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO DTO
     * @return Result<PageResult>
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("|> 分页查询: {}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类
     *
     * @param id 分类id
     * @return Result
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result delete(Long id) { //使用url传参，类似GET
        log.info("|> 删除分类: {}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO DTO
     * @return Result{data=List<Category>}
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        log.info("|> 修改分类: {}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }


    /**
     * 根据条件(type、status)查询分类
     * - 1.用于新增菜品时选择菜品分类下拉框的内容展示。此时下拉框只需展示菜品分类(type=1)，启用禁用状态无所谓(status不传参)
     * - (由于前端不要求分页，Result的data项要求为Category数组，而非PageResult，故须单独做此接口，而不能利用已有的分页查询接口)
     * - 2.用于用户端显示各个分类，要求显示菜品和套餐分类(type不传参)，但不显示已禁用的分类(status=1)
     * - 已有的categoryDTO缺少status项，故这里直接使用Category实体类做DTO
     *
     * @param category DTO
     * @return Result<List < Category>>
     */
    @GetMapping("list")
    @ApiOperation("根据条件列出所有分类，不分页")
    public Result<List<Category>> list(Category category) {
        log.info("|> 根据类型查询分类: {}", category);
        List<Category> categoryList = categoryService.list(category);
        return Result.success(categoryList);
    }

    /**
     * 根据分类id启用/禁用分类
     *
     * @param status 状态：启用1，禁用0
     * @param id 分类id
     * @return Result
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用分类")
    public Result toggleStatus(@PathVariable Integer status, Long id) {
        log.info("|> 调整分类:" + id + " 的启/禁用状态:" + status);
        categoryService.toggleStatus(status, id);
        return Result.success();
    }
}
