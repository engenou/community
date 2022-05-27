package com.nowcoder.community.config;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.interceptor.LoginRequiredInteceptor;
import com.nowcoder.community.interceptor.LoginTicketInterceptor;
import com.nowcoder.community.interceptor.MessageInteceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Eugen
 * @creat 2022-05-04 21:46
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInteceptor loginRequiredInteceptor;

    @Autowired
    private MessageInteceptor messageInteceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                // 除了静态资源外都拦截
                .excludePathPatterns("/static/**");

        registry.addInterceptor(loginRequiredInteceptor)
                // 除了静态资源外都拦截
                .excludePathPatterns("/static/**");

        registry.addInterceptor(messageInteceptor)
                // 除了静态资源外都拦截
                .excludePathPatterns("/static/**");
    }
}
