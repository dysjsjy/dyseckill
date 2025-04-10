package com.dyseckill.mapper;

import com.dyseckill.model.entity.SeckillOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

    int delete(Long id);

    int insert(SeckillOrder seckillOrder);

    int insertSelective(SeckillOrder seckillOrder);

    int update(SeckillOrder seckillOrder);

    int updateSelective(SeckillOrder seckillOrder);

    SeckillOrder querySeckillOrderById(@Param("id") Long id);

    SeckillOrder querySeckillOrderByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
