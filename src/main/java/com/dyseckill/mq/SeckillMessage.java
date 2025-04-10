package com.dyseckill.mq;


import com.dyseckill.model.entity.User;
import lombok.Data;

@Data
public class SeckillMessage {
    private User user;
    private long goodsId;
}
