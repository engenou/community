package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.LoginTicketMapper;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.naming.CompositeName;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Eugen
 * @creat 2022-05-02 17:07
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    public User findUserBuId(int id){
        return userMapper.selectById(id);
    }

    /**
     * 注册用户
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        // 空值判断
        if(user == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("UsernameMessage","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("PasswordMessage","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("EmailMessage","邮箱不能为空!");
            return map;
        }

        // 验证账号,邮箱
        User u = userMapper.selectByUsername(user.getUsername());
        if(u != null){
            map.put("UsernameMessage","账号已存在!");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("EmailMessage","该邮箱已被注册!");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮箱
        Context context = new Context();//用于保存变量，在 html 使用
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/community/activation/153/ajdaejfsiufhsfbsef
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    /**
     *  激活用户
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return Constant.ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return Constant.ACTIVATION_SUCCESS;
        }else{
            return Constant.ACTIVATION_FAILURE;
        }
    }

    /**
     *  用户登录
     */
    public Map<String,Object> login(String username, String password, int expired){
        Map<String,Object> map = new HashMap<>();

        // 空值判断
        if(StringUtils.isBlank(username)){
            map.put("UsernameMessage","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("PasswordMessage","密码不能为空!");
            return map;
        }

        // 验证帐号
        User user = userMapper.selectByUsername(username);
        if(user == null){
            map.put("UsernameMessage","账号不存在!");
            return map;
        }

        // 验证状态
        if(user.getStatus() == 0){ // 0:未激活，1：激活
            map.put("UsernameMessage","账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("PasswordMessage","密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);//表示有效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expired * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 用户退出
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     *  查询登录凭证 LoginTicket
     */
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     *  更新头像
     */
    public int updateHeaderUrl(int userId, String headerUrl){
        return userMapper.updateHeaderUrl(userId, headerUrl);
    }

    /**
     *  修改密码
     */
    public Map<String, Object> updatePassword(int id, String oldPassword, String newPassword, String confirmPassword){
        Map<String, Object> map = new HashMap<>();
        // 获取当前登录用户
        User curUser = hostHolder.getUser();
        // 密码判断
        if (StringUtils.isBlank(oldPassword)) {
            map.put("OldPasswordMessage", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("NewPasswordMessage", "新密码不能为空!");
            return map;
        }
        if (!newPassword.equals(confirmPassword)) {
            map.put("ConfirmPasswordMessage", "两次密码输入不一致!");
            return map;
        }
        // 判断用户输入的原密码（明文）是否正确
        String inputPassword = CommunityUtil.md5(oldPassword + curUser.getSalt());
        if (!inputPassword.equals(curUser.getPassword())) {
            map.put("OldPasswordMessage", "原密码错误!");
            return map;
        }
        // 修改当前登录用户密码
        newPassword = CommunityUtil.md5(newPassword + curUser.getSalt());
        userMapper.updatePassword(curUser.getId(), newPassword);
        return map;
    }





}
