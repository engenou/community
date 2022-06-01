package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.DiscussPostMapper;
import com.nowcoder.community.mapper.LoginTicketMapper;
import com.nowcoder.community.mapper.MessageMapper;
import com.nowcoder.community.mapper.UserMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-02 15:33
 */
@SpringBootTest
public class MapperTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    MessageMapper messageMapper;

    @Test
    public void tsetInsert(){
        User user = new User();
        user.setUsername("caocao");
        user.setEmail("666@126.com");
        user.setSalt("1234");
        user.setActivationCode("111");
        user.setHeaderUrl("http://ww.cowcoder.com/101.png");
        user.setCreateTime(new Date());
        int i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println(user.getUsername());
    }

    @Test
    public void tsetSelect(){
//        User user1 = userMapper.selectById(166);
        User user1 = userMapper.selectByUsername("liubei");
        System.out.println(user1);
    }

    @Test
    public void tsetUpdate(){
        int i= userMapper.updateStatus(166,404);
        int i1 = userMapper.updateHeaderUrl(166, "http://ww.cowcoder.com/404.png");
        int i2 = userMapper.updatePassword(166, "123456");
        System.out.println(i);
        System.out.println(i1);
        System.out.println(i2);
    }

    @Test
    public void tsetSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPost(111, 0, 10, 0);
        for(DiscussPost post : list){
            System.out.println(post);
        }

        int i = discussPostMapper.selectDiscussPostRows(111);
        System.out.println(i);

    }

    @Test
    public void testInsertTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abd");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*60*10));

        loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(loginTicket.getId());
    }
    @Test
    public void testSelectTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abd");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abd", 1);
        loginTicket = loginTicketMapper.selectByTicket("abd");
        System.out.println(loginTicket);
    }


    // messageMapper
    @Test
    public void testSelectLetters(){
//        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
//        for(Message msg : messages){
//            System.out.println(msg);
//        }
//
//        int count = messageMapper.selectConversationCount(111);
//        System.out.println(count);

//        List<Message> list = messageMapper.selectLetters("111_112", 0, 10);
//        for(Message msg : list){
//            System.out.println(msg);
//        }

        int count0 = messageMapper.selectLettersCount("111_112");
        System.out.println(count0);
//
        int c = messageMapper.selectLettersUnreadCount(131, "111_131");
        System.out.println(c);

    }




}
