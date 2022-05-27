package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @author Eugen
 * @creat 2022-05-05 12:53
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.upload}")
    private String uploadPath;

    /**
     *  用户设置页面
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "site/setting";
    }

    /**
     *  上传头像
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("templates/error","您还没有选择图片");
            return "site/setting";
        }
        // 文件后缀
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("templates/error","文件格式不正确！");
            return "site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 文件路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }

        // 更新用户头像的路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     *  获取头像
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 文件存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);

        try(
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (Exception e) {
            log.error("读取头像失败:" + e.getMessage());
        }
    }

    /**
     *  修改密码
     */
    @LoginRequired
    @PostMapping("/password")
    public String updatePassword(Model model, String oldPassword, String newPassword,
                                              String confirmPassword, @CookieValue String ticket){
        // 当前登录用户
        User curUser = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(curUser.getId(), oldPassword, newPassword, confirmPassword);
        // map 为空则修改成功,退出重新登录
        if (map.isEmpty()) {
            userService.logout(ticket);
            return "redirect:/login";
        }
        // 修改密码失败
        model.addAttribute("OldPasswordMessage", map.get("OldPasswordMessage"));
        model.addAttribute("NewPasswordMessage", map.get("NewPasswordMessage"));
        model.addAttribute("ConfirmPasswordMessage", map.get("ConfirmPasswordMessage"));
        return "site/setting";
    }

    /**
     *  个人主页
     */
    @RequestMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserBuId(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        // 用户
        model.addAttribute("user",user);
        // 获赞数量
        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",userLikeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(user.getId(), Constant.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(Constant.ENTITY_TYPE_USER, user.getId());
        model.addAttribute("followerCount",followerCount);
        // 当前用户是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), Constant.ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "site/profile";
    }


}
