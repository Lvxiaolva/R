package com.itheima.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruiji.common.BaseContext;
import com.itheima.ruiji.common.R;
import com.itheima.ruiji.dto.OrdersDto;
import com.itheima.ruiji.entity.*;
import com.itheima.ruiji.service.OrderDetailService;
import com.itheima.ruiji.service.OrderService;
import com.itheima.ruiji.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        if (beginTime != null && endTime != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timeBegin = LocalDateTime.parse(beginTime, formatter);
            LocalDateTime timeEnd = LocalDateTime.parse(endTime, formatter);
            ordersLambdaQueryWrapper.between(Orders::getOrderTime,timeBegin,timeEnd);
        }

        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage,ordersLambdaQueryWrapper);
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> ordersDtos = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            Long userId = item.getUserId();
            log.info("userId:"+userId);
            Orders orders = orderService.getById(userId);
            log.info("orders:"+orders);
            if (orders != null) {
                String userName = orders.getUserName();
                log.info("username:"+userName);
                ordersDto.setUserName(userName);
            }
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtos);

        return R.success(ordersDtoPage);
    }

    @GetMapping("/userPage")
    public R<Page> getOrders(int page, int pageSize){
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @PutMapping
    public R<String> updateStatus(HttpServletRequest request, @RequestBody Orders orders){

        orderService.updateById(orders);

        return R.success("操作成功");
    }

    @PostMapping("/again")
    public R<String> reOrder(HttpServletRequest request,@RequestBody Orders order){
        log.info(order.toString());
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getId,order.getId());
        Orders one = orderService.getOne(ordersLambdaQueryWrapper);
        log.info(one.toString());
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,one.getId());
        OrderDetail orderDetail = orderDetailService.getOne(orderDetailLambdaQueryWrapper);
        log.info("dishId:"+orderDetail.getDishId());
        log.info("setmealId:"+orderDetail.getSetmealId());
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setDishId(orderDetail.getDishId());
        shoppingCart.setSetmealId(orderDetail.getSetmealId());
        shoppingCart.setAmount(orderDetail.getAmount());
        shoppingCartService.save(shoppingCart);

        return R.success("操作成功");
    }
}
