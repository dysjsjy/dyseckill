package com.dyseckill.service.impl;

import com.dyseckill.model.entity.Goods;
import com.dyseckill.mapper.GoodsMapper;
import com.dyseckill.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

}
