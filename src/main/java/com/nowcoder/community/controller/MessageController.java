package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.HostHolder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import javafx.geometry.HorizontalDirection;
import jdk.management.resource.internal.inst.UnixAsynchronousServerSocketChannelImplRMHooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;


/**
 * @author Eugen
 * @creat 2022-05-08 10:30
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(),page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList();
        if(conversations != null){
            for(Message msg : conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",msg);
                map.put("letterCount", messageService.findLetterCount(msg.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(),msg.getConversationId()));
                int targetId = user.getId()==msg.getFromId() ? msg.getToId() : msg.getFromId();
                map.put("target", userService.findUserBuId(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读信息总数量（私信、通知）
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page){
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message message : letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserBuId(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target",getLetterTarget(conversationId));

        // 未读设为已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "site/letter-detail";
    }

    // 获取未读私信的id
    public List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList != null){
            for(Message message : letterList){
                User user = hostHolder.getUser();
                // 要用equals(),不能==：包装类对象不可使用“==”符做比较运算
                if(user.getId().equals(message.getToId()) && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    // 根据会话conversationId获取私信目标用户
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId() == id0){
            return userService.findUserBuId(id1);
        }else{
            return userService.findUserBuId(id0);
        }
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content){

        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJsonString(1,"目标用户不存在");
        }
        // 封装message信息
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        // 默认状态 0，如果不设置则为null,因为sql语句有#{status}
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJsonString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        //===> 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), Constant.TOPIC_COMMENT);
        Map<String,Object> messageVo = new HashMap<>();
        // 不能放进if里，便于notice.html中判断commentNotice.message!=null
        messageVo.put("message", message);

        if(message != null){

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user",userService.findUserBuId((Integer) data.get("userId")));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("postId", data.get("postId"));
            // 评论的通知数量
            int count = messageService.findNoticeCount(user.getId(), Constant.TOPIC_COMMENT);
            messageVo.put("count", count);
            // 评论的未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(), Constant.TOPIC_COMMENT);
            messageVo.put("unread", unread);
        }
        model.addAttribute("commentNotice", messageVo);

        //===> 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), Constant.TOPIC_LIKE);
        messageVo = new HashMap<>();
        // 不能放进if里，便于notice.html中判断commentNotice.message!=null
        messageVo.put("message", message);

        if(message != null){

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserBuId((Integer) data.get("userId")));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("postId", data.get("postId"));
            // 点赞的通知数量
            int count = messageService.findNoticeCount(user.getId(), Constant.TOPIC_LIKE);
            messageVo.put("count", count);
            // 点赞的未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(), Constant.TOPIC_LIKE);
            messageVo.put("unread", unread);
        }
        model.addAttribute("likeNotice", messageVo);

        //===> 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), Constant.TOPIC_FOLLOW);
        messageVo = new HashMap<>();
        // 不能放进if里，便于notice.html中判断commentNotice.message!=null
        messageVo.put("message", message);

        if(message != null){

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserBuId((Integer) data.get("userId")));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("entityType", data.get("entityType"));
//            messageVo.put("postId", data.get("postId"));
            // 点赞的通知数量
            int count = messageService.findNoticeCount(user.getId(), Constant.TOPIC_FOLLOW);
            messageVo.put("count", count);
            // 点赞的未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(), Constant.TOPIC_FOLLOW);
            messageVo.put("unread", unread);
        }
        model.addAttribute("followNotice", messageVo);

        //===> 查询未读消息总数(私信、通知）
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        if(noticeList != null){
            for(Message notice : noticeList){
                Map<String,Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserBuId((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                // 通知作者
                map.put("fromUser", userService.findUserBuId(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "site/notice-detail";
    }







}
