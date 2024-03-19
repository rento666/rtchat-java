package com.rt.im;

import com.rt.im.handler.HeartBeatHandler;
import com.rt.im.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.security.KeyStore;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;

    public NettyServerInitializer() throws Exception {
        // 加载 SSL 证书
        InputStream keyStoreStream = getClass().getClassLoader().getResourceAsStream("ssl/rtcode.asia.jks");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(keyStoreStream, "1fz85fcr68ikb9".toCharArray()); // 替换为你的密码
        keyStoreStream.close();

        // 创建 KeyManagerFactory 并初始化
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "1fz85fcr68ikb9".toCharArray()); // 替换为你的密码

        // 构建 SSL 上下文对象
        sslContext = SslContextBuilder.forServer(keyManagerFactory).build();
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {


        // channel 获取相应的管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        // 添加 SSL 处理器
        SSLEngine engine = sslContext.newEngine(socketChannel.alloc());
        pipeline.addLast(new SslHandler(engine));

        // 添加http编码解码器
        pipeline.addLast(new HttpServerCodec())
                // 对大数据流进行支持
                .addLast(new ChunkedWriteHandler())
                // 对http消息做聚合操作：FullHttpRequest、FullHttpResponse
                .addLast(new HttpObjectAggregator(1024 * 64))
                // websocket，http的后缀路径
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                // 添加Netty空闲超时检查的支持
                // 1. 读空闲超时（超过一定的时间会发送对应的事件消息）
                // 2. 写空闲超时
                // 3. 读写空闲超时
                .addLast(new IdleStateHandler(4,8,12))
                // 心跳检测
                .addLast(new HeartBeatHandler())
                // 读取消息（自定义的）
                .addLast(new WebSocketHandler());
    }
}
