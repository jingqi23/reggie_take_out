package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
    DishDto getByIdWithFlavor(Long id);
    void updateWithFlavor(DishDto dishDto);
    void deleteByIds(Long[] ids);

    void updateStatesByIds(int k, Long[] ids);
}
