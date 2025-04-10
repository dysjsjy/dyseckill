package com.dyseckill.service.impl;

import com.dyseckill.model.entity.OrderInfo;
import com.dyseckill.mapper.OrderInfoMapper;
import com.dyseckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Override
    public long addOrder(OrderInfo orderInfo) {
        return orderInfoMapper.insertSelective(orderInfo);
    }

    @Override
    public OrderInfo getOrderInfoById(long orderId) {
        return orderInfoMapper.queryOrderInfoById(orderId);
    }
}
