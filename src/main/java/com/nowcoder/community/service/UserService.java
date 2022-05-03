package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Eugen
 * @creat 2022-05-02 17:07
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserBuId(int id){
        return userMapper.selectById(id);
    }
}
