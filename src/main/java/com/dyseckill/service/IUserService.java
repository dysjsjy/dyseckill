package com.dyseckill.service;

import com.dyseckill.common.BaseResponse;
import com.dyseckill.model.dto.LoginDTO;
import com.dyseckill.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IUserService extends IService<User> {
    BaseResponse<User> login(LoginDTO loginParam);

    User getUserByPhone(String phone);

    int insert(User user);
}
