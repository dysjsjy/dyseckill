package com.dyseckill.controller;


import com.dyseckill.common.BaseResponse;
import com.dyseckill.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
    @GetMapping("/sayHello")
    public BaseResponse<String> sayHello(@RequestParam String name) {
        return ResultUtils.success(name + "hello");
    }
}
