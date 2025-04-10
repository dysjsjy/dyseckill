package com.dyseckill.mapper;

import com.dyseckill.model.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dyseckill.model.dto.GoodsDTO;

import java.util.List;


public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsDTO> queryAllGoodsVo();

    GoodsDTO queryGoodsDTOByGoodsId(long goodsId);

    int updateStock(long goodsId);
}
