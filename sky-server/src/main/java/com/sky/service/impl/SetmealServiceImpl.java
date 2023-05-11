package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐管理业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO DTO
     */
    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        //1-向套餐表dish插入一条数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //补充公共字段//
        setmealMapper.insert(setmeal);
        // 主键返回备用
        Long setmealId = setmeal.getId();

        //2-向中间表setmeal_dish插入多条数据
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && setmealDishList.size() > 0) {
            setmealDishList.forEach(setmealDish ->
                    setmealDish.setSetmealId(setmealId)
            );
            setmealDishMapper.insertBatch(setmealDishList);
        }
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO DTO
     * @return PageResult
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //设置PageHelper
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //查套餐表
        // 与菜品分页查询同理，setmeal表里只有分类id没有分类名，故在SQL里内连接category表
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     *
     * @param ids 多个待删套餐id的List
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //遍历ids，判断这些id对应的套餐是否处于启售，是则抛异常拒绝删除
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (StatusConstant.ENABLE.equals(setmeal.getStatus())) {
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        //遍历ids，删除这些id对应的套餐：先删setmeal表条目，再删中间表setmeal_dish条目
        ids.forEach(setmealId -> {
            //删除套餐表中的数据
            setmealMapper.deleteById(setmealId);
            //删除套餐菜品关系表setmeal_dish中的数据(该id可能对应setmeal_dish表中的0条或多条)
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }


    /**
     * 根据id查询套餐和套餐菜品关系
     *
     * @param id 套餐id
     * @return VO
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        //1-查setmeal表
        Setmeal setmeal = setmealMapper.getById(id);
        //2-查setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        //3-构造VO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO DTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //构造Entity：拷贝DTO属性，添加公共字段
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //setmeal.setUpdateTime(LocalDateTime.now());
        //setmeal.setUpdateUser(BaseContext.getCurrentId());

        //1、修改套餐表，执行update
        setmealMapper.update(setmeal);

        //套餐id
        Long setmealId = setmealDTO.getId();

        //为修改setmeal_dish表，由于修改还包含了dish的删、改，故先删除原来关联的所有dish，重新添加
        //2、删除套餐和菜品的原有关联关系，操作setmeal_dish表，执行delete
        setmealDishMapper.deleteBySetmealId(setmealId);

        //3、重新添加setmeal-dish关联条目，往setmeal_dish表插入0-n条数据
        //先补上setmeal_id
        // FYI：这里前端已经控制了不允许套餐关联的菜品为空，故setmealDishes的size()不会是0
        //  此外，DTO里的setmealDishes属性new了ArrayList，故setmealDishes不可能是null
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //然后重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * ※套餐起售、停售※
     *
     * @param status 待设定启/停售状态
     * @param id     套餐id
     */
    @Override
    public void setStatus(Integer status, Long id) {
        //1-起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if (status.equals(StatusConstant.ENABLE)) {
            /* 中间表条目只有菜品id而不含status；
                此处在DishMapper中做一个根据套餐id查询Dish的方法，用dish表LEFT JOIN以保证每个dish都能被查到;
                连接中间表setmeal_dish以获取setmeal_id
               SQL:
                SELECT d.*
                FROM dish d LEFT JOIN setmeal_dish sd
                ON d.id = sd.dish_id
                WHERE sd.category_id = #{categoryId}
             */
            //根据套餐id查dish表
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            //遍历，若有菜品status是禁用，则抛异常
            if (dishList != null && dishList.size() > 0) {
                dishList.forEach(dish -> {
                    if (dish.getStatus().equals(StatusConstant.DISABLE)) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        //2-修改套餐status
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                //.updateTime(LocalDateTime.now())
                //.updateUser(BaseContext.getCurrentId())
                .build();
        setmealMapper.update(setmeal);
    }
}
