package com.dyseckill.service.impl;

import com.dyseckill.common.BaseResponse;
import com.dyseckill.common.ErrorCode;
import com.dyseckill.common.ResultUtils;
import com.dyseckill.model.dto.LoginDTO;
import com.dyseckill.model.entity.User;
import com.dyseckill.mapper.UserMapper;
import com.dyseckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dyseckill.uitls.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public BaseResponse<User> login(LoginDTO loginParam) {
        User user = userMapper.checkPhone(loginParam.getMobile());

        if (user == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String password = user.getPassword();

        String getPassword = MD5Util.formPassToDBPass(loginParam.getPassword(), user.getSalt());
        if (!StringUtils.equals(getPassword, password)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        user.setPassword(StringUtils.EMPTY);
        return ResultUtils.success(user);

    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.checkPhone(phone);
    }

    @Override
    public int insert(User user) {
        String password = user.getPassword();
        String salt = user.getSalt();
        String pass = MD5Util.inputPassToDbPass(password, salt);
        user.setPassword(pass);
        return userMapper.insert(user);
    }
}
