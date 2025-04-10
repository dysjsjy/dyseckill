package com.dyseckill.mapper;

import com.dyseckill.model.entity.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    public int delete(@Param("orderId") Long id);

    public int insert(OrderInfo orderInfo);

    int insertSelective(OrderInfo orderInfo);

    public List<OrderInfo> queryAllOrderInfo();

    public OrderInfo queryOrderInfoById(@Param("orderId") Long id);

    public int update(OrderInfo orderInfo);

    public int updateSelective(OrderInfo orderInfo);
}
