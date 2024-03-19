package com.rt.component;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class AuthWebMvcConfigurer implements WebMvcConfigurer {

    @Resource
    AuthHandlerInterceptor authHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "",
                        "/ip",
                        "/auth/**",
                        "/file/**",
                        "/file/preview/**",
                        "/auth/code/**"
                );
    }
}
