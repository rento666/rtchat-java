package com.rt.im.handler;

import com.alibaba.fastjson.JSON;
import com.rt.common.Result;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.im.param.ParamType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 处理消息的handler
 * TextWebSocketFrame: 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 保存所有的客户端连接
    private static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        // 接收到消息后都会自动执行这里
        try {
            Param param = JSON.parseObject(frame.text(), Param.class);
            switch (ParamType.match(param.getCode())){
                case CONNECTION:
                    ConnectionHandler.execute(ctx, param);
                    break;
                case PING:
                    // 接收到心跳信息，啥也不做就行
//                    System.out.println("心跳");
                    break;
                case CHAT:
                    ChatHandler.execute(ctx, frame, clients);
                    break;
                default:
                    Result res = new Result().system("不支持的连接");
                    TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(res));
                    ctx.channel().writeAndFlush(message);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            ctx.channel().writeAndFlush(new Result().system("系统错误"));
        }
    }

    // 当有新的客户端连接服务器之后，会自动调用这个方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 将新的通道加入到clients
        clients.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        ctx.channel().close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭通道");
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        UserChannelMap.print();
    }

}
