package com.dyseckill.service.impl;

import com.dyseckill.model.entity.SeckillGoods;
import com.dyseckill.mapper.SeckillGoodsMapper;
import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.service.ISeckillGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.dyseckill.mapper.GoodsMapper;


@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsDTO> getSeckillGoodsList() {
        return goodsMapper.queryAllGoodsVo();
    }

    @Override
    public GoodsDTO getSeckillGoodsDTOByGoodsId(long goodsId) {
        return goodsMapper.queryGoodsDTOByGoodsId(goodsId);
    }

    @Override
    public int reduceStock(long goodsId) {
        return goodsMapper.updateStock(goodsId);
    }
}
