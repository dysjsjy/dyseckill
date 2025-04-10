package com.dyseckill.service;

import com.dyseckill.model.entity.OrderInfo;
import com.dyseckill.model.entity.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dyseckill.model.entity.User;
import com.dyseckill.model.dto.GoodsDTO;


public interface ISeckillOrderService extends IService<SeckillOrder> {

    SeckillOrder getSeckillOrderByUIdAndGId(long userId, long goodsId);

    OrderInfo insert(User user, GoodsDTO goodsBo);

    OrderInfo getOrderInfoById(long orderId);

    long getSeckillResultByUIdAndGId(Long userId, long goodsId);

    boolean checkUrl(User user, long goodsId, String path);

    String createUrl(User user, long goodsId);

}
