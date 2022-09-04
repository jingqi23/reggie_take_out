package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.dto.SetmealDto;
import com.example.reggie_take_out.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void removeWithDish(List<Long> ids);

    void changeStatusByIds(int k, Long[] ids);

    void saveWithSetmealDish(SetmealDto setmealDto);

    SetmealDto getByIdWithSetmealDish(Long id);

    void updateWithSetmealDish(SetmealDto setmealDto);
}
