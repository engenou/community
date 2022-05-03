package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eugen
 * @creat 2022-05-02 17:15
 */
@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;


    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        // page 自动封装进 model
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit());
        // 把查询出来的list，通过userid增加user放进新的List<> discussPosts
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserBuId(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "index";
    }

//    @GetMapping("/error")
//    public String toErrorPage() {
//        return "error/500";
//    }
}
