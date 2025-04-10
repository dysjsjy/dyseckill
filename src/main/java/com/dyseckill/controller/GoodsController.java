package com.dyseckill.controller;

import com.dyseckill.common.BaseResponse;
import com.dyseckill.common.Const;
import com.dyseckill.common.ErrorCode;
import com.dyseckill.common.ResultUtils;
import com.dyseckill.model.dto.GoodsDTO;
import com.dyseckill.model.entity.User;
import com.dyseckill.model.vo.GoodsDetailVo;
import com.dyseckill.redis.GoodsKey;
import com.dyseckill.redis.RedisService;
import com.dyseckill.redis.UserKey;
import com.dyseckill.service.ISeckillGoodsService;
import com.dyseckill.service.IUserService;
import com.dyseckill.uitls.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    IUserService userService;

    @Autowired
    ISeckillGoodsService seckillGoodsService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/list")
    @ResponseBody
    public BaseResponse<List<GoodsDTO>> list() {
        // 从 Redis 获取商品列表
        List<GoodsDTO> goodsList = redisService.get(GoodsKey.getGoodsList, "", List.class);
        if (goodsList != null && !goodsList.isEmpty()) {
            return ResultUtils.success(goodsList);
        }

        // 如果 Redis 中没有商品列表，则从数据库获取
        goodsList = seckillGoodsService.getSeckillGoodsList();
        if (goodsList != null && !goodsList.isEmpty()) {
            // 将商品列表存入 Redis
            redisService.set(GoodsKey.getGoodsList, "", goodsList, Const.RedisCacheExtime.GOODS_LIST);
        }

        return ResultUtils.success(goodsList);
    }

    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public BaseResponse<GoodsDetailVo> detail(@PathVariable("goodsId") long goodsId, HttpServletRequest request) {
        // redis读取用户，并返回给前端
        String loginToken = CookieUtils.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);

        //通过Cookie保存用户名实现，在redis不可用的情况下使用cookie读取
        if (user == null) {
            Cookie[] cookies = request.getCookies();
            String cookie_userphone = null;
            for (Cookie cookie : cookies) {
                if ("cookie_userphone".equals(cookie.getName())) {
                    cookie_userphone = cookie.getValue();
                }
            }
            user = userService.getUserByPhone(cookie_userphone);
        }


        GoodsDTO goods = seckillGoodsService.getSeckillGoodsDTOByGoodsId(goodsId);
        if (goods == null) {
            //没有查询到货物信息
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        } else {
            long startTime = goods.getStartDate().getTime();
            long endTime = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();

            int miaoshaStatus = 0;
            int remainSeconds = 0;
            if (now < startTime) {
                miaoshaStatus = 0;
                remainSeconds = (int) ((startTime - now) / 1000);
            } else if (now > endTime) {
                miaoshaStatus = 2;
                remainSeconds = -1;
            } else {
                miaoshaStatus = 1;
                remainSeconds = 0;
            }

            GoodsDetailVo vo = new GoodsDetailVo();
            vo.setGoods(goods);
            vo.setUser(user);
            vo.setMiaoshaStatus(miaoshaStatus);
            vo.setRemainSeconds(remainSeconds);
            return ResultUtils.success(vo);
        }
    }
}
