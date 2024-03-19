package com.rt.im;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听SpringBoot启动，在SpringBoot启动的同时，来运行一些方法
 */
@Component
public class NettyListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private WebSocketServer websocketServer;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null) {
            websocketServer.start();
        }
    }
}
