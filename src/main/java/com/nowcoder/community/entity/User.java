package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Eugen
 * @creat 2022-05-02 12:17
 */
@Data //lombok注解
@TableName("user")
public class User {

    // 用户 id
//    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 加密盐
    private String salt;
    // 邮箱
    private String email;
    // 用户类别   0-普通用户    1-超级管理员    2-版主
    private Integer type;
    // 状态      0-未激活      1-已激活
    private Integer status;
    // 激活码
    private String activationCode;
    // 头像
    private String headerUrl;
    // 创建时间
    private Date createTime;
}
