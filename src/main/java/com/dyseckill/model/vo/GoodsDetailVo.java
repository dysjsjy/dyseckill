package com.dyseckill.model.vo;

import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.model.entity.User;
import lombok.Data;

@Data
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsDTO goods;
    private User user;
}
