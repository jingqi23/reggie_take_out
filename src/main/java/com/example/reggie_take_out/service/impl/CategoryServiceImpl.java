package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.entity.mapper.CategoryMapper;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.DishService;
import com.example.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count=dishService.count(queryWrapper);
        if(count>0){
            throw new CustomException("当前分类下关联了菜品不能删除");
        }
        LambdaQueryWrapper<Setmeal> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId, id);
        int count2=setmealService.count(queryWrapper1);
        if(count2>0){
            throw new CustomException("当前分类下关联了套餐不能删除");
        }
        super.removeById(id);

    }
}
