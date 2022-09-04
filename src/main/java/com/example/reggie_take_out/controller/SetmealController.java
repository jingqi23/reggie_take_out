package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.SetmealDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo, setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }
    @GetMapping("/{id}")
    @Cacheable(value="setmealCache", key = "#id")
    public R<SetmealDto> getByIdWithSetmealDish(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithSetmealDish(id);
        return R.success(setmealDto);
    }
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("套餐添加成功");
    }
    @PostMapping("/status/{k}")
    public R<String> changeStatusByIds(@PathVariable int k,Long[] ids){
        setmealService.changeStatusByIds(k,ids);
        return R.success("状态更改成功");
    }
    @CacheEvict(value = "setmealCache",allEntries = true)//删除这个分类下的所有缓存数据
    @DeleteMapping
    public R<String> removeWithDish(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }
    @PutMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> updateWithSetmealDish(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithSetmealDish(setmealDto);
        return R.success("套餐更新成功");
    }
    @Cacheable(value="setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
