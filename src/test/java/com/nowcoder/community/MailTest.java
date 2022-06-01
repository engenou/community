package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Eugen
 * @creat 2022-05-03 14:10
 */
@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;//thymeleaf模版引擎

    @Test
    public void testSendMail(){
        mailClient.sendMail("1533183133@qq.com","test","welcome");
    }

    @Test
    public void testSendMailHTML(){
        Context context = new Context();
        context.setVariable("username", "eugen");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1533183133@qq.com","test",content);
    }
}
