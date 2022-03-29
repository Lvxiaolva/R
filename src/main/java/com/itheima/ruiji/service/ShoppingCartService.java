package com.itheima.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruiji.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart add (ShoppingCart shoppingCart);
}
