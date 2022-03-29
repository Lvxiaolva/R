package com.itheima.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruiji.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
