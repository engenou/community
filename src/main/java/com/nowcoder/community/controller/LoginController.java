package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Eugen
 * @creat 2022-05-03 14:43
 */
@Slf4j
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;//验证码

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *  跳转注册页面
     */
    @GetMapping("/register")
    public String getRegisterPage(){
        return "site/register";
    }

    /**
     *  跳转注册页面
     */
    @GetMapping("/login")
    public String getLoginPage(){
        return "site/login";
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        // map 为空则注册成功
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，请到邮箱激活该账号!");
            model.addAttribute("target", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("UsernameMessage", map.get("UsernameMessage"));
            model.addAttribute("PasswordMessage", map.get("PasswordMessage"));
            model.addAttribute("EmailMessage", map.get("EmailMessage"));
            return "site/register";
        }
    }


    /**
     *  邮箱激活账号
     *  http://localhost:8080/community/activation/153/ajdaejfsiufhsfbsef
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result == Constant.ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，你的账号可以正常使用!");
            model.addAttribute("target","/login");
        }else if(result == Constant.ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已激活过了!");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激激活失败，该账号激活码无效!");
            model.addAttribute("target","/index");
        }
        return "site/operate-result";
    }

    /**
     *  生成验证码
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){

        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

//        // 将验证码存入session
//        session.setAttribute("kaptcha",text);

        // 验证码归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        // 将验证码图片输出浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            log.error("响应验证码失败" + e.getMessage());
        }
    }


    /**
     *  用户登录
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberme,
                        Model model/*, HttpSession session*/,HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        // 判断验证码:
        // 未优化：从session取；优化后从redis取
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNoneBlank(kaptchaOwner)){
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("CodeMessage","验证码错误!");
            return "site/login";
        }

        // 检查账号和密码
        int expiredSeconds = rememberme? Constant.REMEMBER_EXPIRED_SECONDS : Constant.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //登录成功
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("UsernameMessage",map.get("UsernameMessage"));
            model.addAttribute("PasswordMessage",map.get("PasswordMessage"));
            return "site/login";
        }
    }

    /**
     *  用户退出
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);

        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

}
