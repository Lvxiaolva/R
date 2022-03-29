package com.itheima.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruiji.dto.SetmealDto;
import com.itheima.ruiji.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    SetmealDto getDto(Long id);

    void updateWithDto(SetmealDto setmealDto);
}
