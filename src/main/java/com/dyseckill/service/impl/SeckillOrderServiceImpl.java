package com.dyseckill.service.impl;

import com.dyseckill.common.Const;
import com.dyseckill.model.entity.OrderInfo;
import com.dyseckill.model.entity.SeckillOrder;
import com.dyseckill.mapper.SeckillOrderMapper;
import com.dyseckill.model.entity.User;
import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.redis.RedisService;
import com.dyseckill.redis.SeckillKey;
import com.dyseckill.service.IOrderService;
import com.dyseckill.service.ISeckillGoodsService;
import com.dyseckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dyseckill.uitls.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;


@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String salt = "afasdf_ )@$";

    @Autowired
    SeckillOrderMapper seckillOrderMapper;

    @Autowired
    ISeckillGoodsService seckillGoodsService;

    @Autowired
    IOrderService orderService;

    @Autowired
    RedisService redisService;

    @Override
    public SeckillOrder getSeckillOrderByUIdAndGId(long userId, long goodsId) {
        return seckillOrderMapper.querySeckillOrderByUserIdAndGoodsId(userId, goodsId);
    }

    @Transactional
    @Override
    public OrderInfo insert(User user, GoodsDTO goodsDTO) {
        int success = seckillGoodsService.reduceStock(goodsDTO.getId());

        if(success == 1){
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setCreateDate(LocalDateTime.now());
            orderInfo.setAddrId(0L);
            orderInfo.setGoodsCount(1);
            orderInfo.setGoodsId(goodsDTO.getId());
            orderInfo.setGoodsName(goodsDTO.getGoodsName());
            orderInfo.setGoodsPrice(goodsDTO.getSeckillPrice());
            orderInfo.setOrderChannel(1);
            orderInfo.setStatus(0);
            orderInfo.setUserId(user.getId());
            long orderId = orderService.addOrder(orderInfo);
            logger.info("orderId -->" +orderId);
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setGoodsId(goodsDTO.getId());
            seckillOrder.setOrderId(orderInfo.getId());
            seckillOrder.setUserId(user.getId());
            seckillOrderMapper.insertSelective(seckillOrder);
            return orderInfo;
        }else {
            setGoodsOver(goodsDTO.getId());
            return null;
        }
    }

    @Override
    public OrderInfo getOrderInfoById(long orderId) {
        SeckillOrder seckillOrder = seckillOrderMapper.querySeckillOrderById(orderId);
        if (seckillOrder == null) {
            return null;
        }

        return orderService.getOrderInfoById(seckillOrder.getOrderId());
    }

    // 查看秒杀结果
    @Override
    public long getSeckillResultByUIdAndGId(Long userId, long goodsId) {
        SeckillOrder seckillOrder = getSeckillOrderByUIdAndGId(userId, goodsId);
        if (seckillOrder != null) {
            return seckillOrder.getId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean checkUrl(User user, long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillUrl, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    @Override
    public String createUrl(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }

        String str = MD5Util.md5(UUID.randomUUID() + salt);
        redisService.set(SeckillKey.getSeckillUrl, "" + user.getId() + "_" + goodsId, str, Const.RedisCacheExtime.GOODS_ID);
        return str;
    }

    // 设置秒杀已经结束
    private void setGoodsOver(Long goodsId){
        redisService.set(SeckillKey.isGoodsOver,""+goodsId,true,Const.RedisCacheExtime.GOODS_ID);
    }

    // 查看秒杀是否结束
    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
    }
}
