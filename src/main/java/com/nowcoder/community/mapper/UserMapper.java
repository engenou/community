package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author Eugen
 * @creat 2022-05-02 14:53
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户 id 查询用户
     */
    User selectById(int id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String name);

    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(String email);

    /**
     * 添加用户
     */
    int insertUser(User user);


    /**
     * 修改用户状态
     */
    int updateStatus(int id, int status);

    /**
     * 更新用户头像
     */
    int updateHeaderUrl(int id, String headerUrl);

    /**
     * 修改密码
     */
    int updatePassword(int id, String password);

    /**
     * 注册用户
     */
//    Map<String,Object> register(User user);

}
