package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Eugen
 * @creat 2022-05-10 9:31
 */
@Controller
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    // 关注
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(Constant.TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        // 发送消息
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(0,"已关注！");
    }

    // 取消关注
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0,"已取消关注！");
    }

    // 查询用户的关注列表
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserBuId(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, Constant.ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if(userList != null){
            for(Map<String, Object> map : userList){
                User followeeUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(followeeUser.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "site/followee";
    }

    // 查询用户的粉丝列表
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserBuId(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(Constant.ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if(userList != null){
            for(Map<String, Object> map : userList){
                User followeeUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(followeeUser.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "site/follower";
    }

    // 当前用户是否关注：
    private boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null) return false;

        return followService.hasFollowed(hostHolder.getUser().getId(), Constant.ENTITY_TYPE_USER, userId);
    }
}
