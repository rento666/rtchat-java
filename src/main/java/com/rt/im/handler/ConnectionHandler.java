package com.rt.im.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.rt.common.Result;
import com.rt.component.JwtConfig;
import com.rt.entity.user.User;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.service.user.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;

public class ConnectionHandler {
    public static void execute(ChannelHandlerContext ctx, Param param) {

        // 将当前用户添加到在线用户
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        // 可以根据uid来获取user信息
        UserService us = SpringUtil.getBean(UserService.class);
        Map<String,String> map;
        try{
            map = jwtConfig.parseToken(param.getToken());
        }catch (Exception e){
            Result res = new Result().system("无效令牌-连接");
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(res));
            ctx.channel().writeAndFlush(message);
            return;
        }
        String uid = map.get("uid");
        User user = us.getById(Integer.valueOf(uid));
        if(user == null) {
            Result res = new Result().system("错误用户-连接");
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(res));
            ctx.channel().writeAndFlush(message);
        }
        UserChannelMap.put(uid, ctx.channel());
        System.out.println("建立用户:" + uid + "与通道" + ctx.channel().id() + "的关联");
        // 发送消息
        Result res = new Result().system("与服务端连接建立成功");
        TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(res));
        ctx.channel().writeAndFlush(message);

    }
}
