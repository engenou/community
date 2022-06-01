package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-02 12:17
 */
@Data //lombok注解
@TableName("user")
@JsonIgnoreProperties({"enabled","accountNonExpired", "accountNonLocked",
                "credentialsNonExpired", "authorities"})  //序列化User时，忽略这些属性
public class User /*implements UserDetails*/ {

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


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> list = new ArrayList<>();
//        list.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                switch (type){
//                    case 1:
//                        return "ADMIN";
//                    default:
//                        return "USER";
//                }
//            }
//        });
//        return list;
//    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    // 凭证未过期
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    // 账号可用
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
}
