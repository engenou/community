package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
@SpringBootTest
class CommunityApplicationTests implements ApplicationContextAware {

    // ioc容器
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);

        System.out.println(applicationContext.getBean(AlphaService.class));
    }
}
