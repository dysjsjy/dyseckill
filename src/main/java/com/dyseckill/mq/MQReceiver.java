package com.dyseckill.mq;

import com.dyseckill.model.entity.SeckillOrder;
import com.dyseckill.model.entity.User;
import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.redis.RedisService;
import com.dyseckill.service.IOrderService;
import com.dyseckill.service.ISeckillGoodsService;
import com.dyseckill.service.ISeckillOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    ISeckillGoodsService seckillGoodsService;

    @Autowired
    IOrderService orderService;

    @Autowired
    ISeckillOrderService seckillOrderService;


    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message){
        logger.info("receive message:" + message);
        SeckillMessage mm = RedisService.stringToBean(message, SeckillMessage.class);
        User user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsDTO goods = seckillGoodsService.getSeckillGoodsDTOByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if(stock <= 0){
            return;
        }
        SeckillOrder order = seckillOrderService.getSeckillOrderByUIdAndGId(user.getId(), goodsId);
        if(order != null){
            return;
        }
        seckillOrderService.insert(user,goods);
    }
}
