package com.dyseckill.controller;


import com.dyseckill.common.BaseResponse;
import com.dyseckill.common.ErrorCode;
import com.dyseckill.common.ResultUtils;
import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.model.entity.OrderInfo;
import com.dyseckill.model.entity.User;
import com.dyseckill.model.vo.OrderDetailVo;
import com.dyseckill.redis.RedisService;
import com.dyseckill.redis.UserKey;
import com.dyseckill.service.ISeckillGoodsService;
import com.dyseckill.service.ISeckillOrderService;
import com.dyseckill.uitls.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/seckill-order")
@Slf4j
public class SeckillOrderController {

    @Autowired
    RedisService redisService;

    @Autowired
    ISeckillOrderService seckillOrderService;

    @Autowired
    ISeckillGoodsService seckillGoodsService;

    // 在mq处理完成后获取订单详细信息
    @RequestMapping("/detail")
    @ResponseBody
    public BaseResponse<OrderDetailVo> info(@RequestParam("orderId") long orderId, HttpServletRequest request){
        String loginToken = CookieUtils.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if(user == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        OrderInfo order = seckillOrderService.getOrderInfoById(orderId);
        if(order == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        Long goodsId = order.getGoodsId();
        GoodsDTO goods = seckillGoodsService.getSeckillGoodsDTOByGoodsId(goodsId);

        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goods);
        vo.setOrder(order);
        return ResultUtils.success(vo);
    }
}
