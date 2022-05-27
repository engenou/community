package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Eugen
 * @creat 2022-04-30 17:17
 */
@Controller
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/alpha")
    @ResponseBody
    public String select(){
        return alphaService.select();
    }

    @RequestMapping(value = "/student/{id}")
    @ResponseBody
    public String getStu(
            @RequestParam(name="current",required = false, defaultValue = "1")int current,
            @RequestParam(name = "limit",required = false, defaultValue = "10")int limit,
            @PathVariable("id")int id){
        System.out.println(current);
        System.out.println(limit);
        System.out.println(id);
        return "hello,student";
    }

    // ajax示例
    @RequestMapping(value = "/ajax")
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJsonString(0,"成功");
    }

}
