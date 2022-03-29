package com.itheima.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.ruiji.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
