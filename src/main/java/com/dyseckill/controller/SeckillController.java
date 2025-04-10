package com.dyseckill.controller;


import com.dyseckill.aop.AccessLimit;
import com.dyseckill.common.BaseResponse;
import com.dyseckill.common.Const;
import com.dyseckill.common.ErrorCode;
import com.dyseckill.common.ResultUtils;
import com.dyseckill.model.entity.SeckillOrder;
import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.model.entity.User;
import com.dyseckill.mq.MQSender;
import com.dyseckill.mq.SeckillMessage;
import com.dyseckill.redis.GoodsKey;
import com.dyseckill.redis.UserKey;
import com.dyseckill.redis.RedisService;
import com.dyseckill.service.ISeckillGoodsService;
import com.dyseckill.service.ISeckillOrderService;
import com.dyseckill.uitls.CookieUtils;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill-goods")
@Slf4j
public class SeckillController implements InitializingBean {

    @Autowired
    RedisService redisService;

    @Autowired
    ISeckillGoodsService seckillGoodsService;
    @Autowired
    ISeckillOrderService seckillOrderService;

    
    private RateLimiter rateLimiter = RateLimiter.create(100);

    @Autowired
    MQSender mqSender;

    // 同时存储在本地内存中，存储每个商品有没有用完，
    private ConcurrentHashMap<Long, Boolean> localOverMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsDTO> goodsList = seckillGoodsService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (GoodsDTO goods : goodsList) {
            
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(goods.getId(), false);
        }
    }


    // 处理秒杀请求，通过验证用户、检查库存、防止重复购买，并将订单异步发送到消息队列来完成秒杀操作。
    @PostMapping("/{path}/seckill")
    @ResponseBody
    public BaseResponse<Integer> list(@PathVariable("path") String path, @RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtils.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        boolean check = seckillOrderService.checkUrl(user, goodsId, path);
        if (!check) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        Long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
        if (stock == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        SeckillOrder order = seckillOrderService.getSeckillOrderByUIdAndGId(user.getId(), goodsId);
        if (order == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(mm);
        
        return ResultUtils.success(0);
    }

    // 查看秒杀结果
    @GetMapping("/result")
    @ResponseBody
    public BaseResponse<Long> seckillResult(@RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtils.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        long result = seckillOrderService.getSeckillResultByUIdAndGId(user.getId(), goodsId);
        return ResultUtils.success(result);
    }

    @AccessLimit(seconds = 5, maxCount = 5)
    @GetMapping("/path")
    @ResponseBody
    public BaseResponse<String> getSeckillPath(HttpServletRequest request, @RequestParam("goodsId") long goodsId) {
        String loginToken = CookieUtils.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        if (!rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String url = seckillOrderService.createUrl(user, goodsId);
        return ResultUtils.success(url);
    }
}
