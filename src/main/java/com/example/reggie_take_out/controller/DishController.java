package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.DishFlavor;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.DishFlavorService;
import com.example.reggie_take_out.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequestMapping("/dish")
@RestController
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize,String name){
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> pageDtoInfo =new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo, pageDtoInfo,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        pageDtoInfo.setRecords(list);
        return R.success(pageDtoInfo);
    }
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/
   @GetMapping("/list")
   public R<List<DishDto>> list(Dish dish){
       List<DishDto> dishDtos=null;
       String key="dish"+dish.getCategoryId()+"_"+dish.getStatus();
       dishDtos= (List<DishDto>) redisTemplate.opsForValue().get(key);
       if(dishDtos!=null) return R.success(dishDtos);

       LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
       queryWrapper.eq(Dish::getStatus, 1);
       queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
       queryWrapper.orderByDesc(Dish::getUpdateTime);
       List<Dish> list = dishService.list(queryWrapper);
       dishDtos=list.stream().map((item)->{
           DishDto dishDto=new DishDto();
           BeanUtils.copyProperties(item, dishDto);
           Long categoryId = item.getCategoryId();
           if(categoryId!=null){
               dishDto.setCategoryName(categoryService.getById(categoryId).getName());
           }
           LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
           queryWrapper1.eq(DishFlavor::getDishId,item.getId());
            dishDto.setFlavors(dishFlavorService.list(queryWrapper1));
            return dishDto;
       }).collect(Collectors.toList());

       redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);
       return R.success(dishDtos);
   }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        String key="dish"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("菜品更新成功");
    }
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        String key="dish"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("菜品添加成功");
    }
    @PostMapping("/status/{k}")
    public R<String> updateStatesByIds(@PathVariable int k, Long[] ids){
        dishService.updateStatesByIds(k,ids);
        return R.success("状态更新成功");
    }
    @DeleteMapping
    public R<String> deleteByIds(Long[] ids){
        dishService.deleteByIds(ids);
        return R.success("菜品删除成功");
    }

}
