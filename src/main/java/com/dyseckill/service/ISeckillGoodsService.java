package com.dyseckill.service;

import com.dyseckill.model.entity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dyseckill.model.dto.GoodsDTO;

import java.util.List;


public interface ISeckillGoodsService extends IService<SeckillGoods> {
    List<GoodsDTO> getSeckillGoodsList();

    GoodsDTO getSeckillGoodsDTOByGoodsId(long goodsId);

    int reduceStock(long goodsId);
}
