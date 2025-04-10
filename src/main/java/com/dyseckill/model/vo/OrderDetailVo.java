package com.dyseckill.model.vo;

import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.model.entity.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
    private GoodsDTO goods;
    private OrderInfo order;
}
