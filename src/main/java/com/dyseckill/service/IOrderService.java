package com.dyseckill.service;

import com.dyseckill.model.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IOrderService extends IService<OrderInfo> {
    long addOrder(OrderInfo orderInfo);

    OrderInfo getOrderInfoById(long orderId);
}
