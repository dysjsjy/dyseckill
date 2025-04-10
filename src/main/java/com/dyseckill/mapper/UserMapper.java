package com.dyseckill.mapper;

import com.dyseckill.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


public interface UserMapper extends BaseMapper<User> {
    int delete(Long id);

    int update(User user);

    int updateSelective(User user);

    int insert(User user);

    User queryUserByPhoneAndPassword(@Param("phone") String phone, @Param("password") String password);

    User checkPhone(@Param("phone") String phone);
}
