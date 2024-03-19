package com.rt.im;

import com.rt.component.SpringContextUtils;
import com.rt.component.value.SysValue;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * 被NettyListener使用
 */
@Component
@DependsOn("springContextUtils")
public class WebSocketServer {
    private EventLoopGroup bossGroup;       // 主线程池
    private EventLoopGroup workerGroup;     // 工作线程池
    private ServerBootstrap server;         // 服务器
    private ChannelFuture future;           // 回调

    public void start() {
        try {
            SysValue value = SpringContextUtils.getBean(SysValue.class);
            // 绑定端口
            future = server.bind(value.getImPort()).sync();
            System.out.println("websocket - 启动成功");
            // 等待服务端口关闭
            future.channel().closeFuture().sync();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public WebSocketServer() throws Exception {
        // 构造两个线程组
        bossGroup = new NioEventLoopGroup();
        // 用于处理boss接收到的请求
        workerGroup = new NioEventLoopGroup();
        // 服务端启动辅助类
        server = new ServerBootstrap();
        // 装配线程池、指定服务端监听通道、设置业务指责链
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyServerInitializer());
    }

}
