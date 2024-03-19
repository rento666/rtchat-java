package com.rt.im.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.anwen.mongo.conditions.query.LambdaQueryChainWrapper;
import com.anwen.mongo.conditions.update.LambdaUpdateChainWrapper;
import com.rt.bean.AIBotService;
import com.rt.common.Result;
import com.rt.entity.ai.QykVo;
import com.rt.entity.group.GroupMember;
import com.rt.entity.msg.Msg;
import com.rt.entity.user.UserMsgVo;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.im.param.ParamType;
import com.rt.mq.MsgProducer;
import com.rt.service.group.GroupMemberService;
import com.rt.service.user.UserService;
import com.rt.utils.TimeUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatHandler {
    public static void execute(ChannelHandlerContext ctx, TextWebSocketFrame frame, ChannelGroup clients) {

        ArrayList<Integer> aiIdList = new ArrayList<>(Arrays.asList(1, 12));

        Param param = JSON.parseObject(frame.text(), Param.class);
        if (param == null) {
            Result res = new Result().system("消息格式错误");
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(res));
            ctx.channel().writeAndFlush(message);
            return;
        }
//        UserChannelMap.print();
        String uid = String.valueOf(param.getMsg().getUser().get_id());
        if(ObjectUtil.isNull(UserChannelMap.get(uid))){
            // 根据这个uid，查不到这个用户的ws连接为在线
            Result res = new Result().system("请先登录");
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(res));
            ctx.channel().writeAndFlush(message);
            return;
        }
        // 将聊天消息保存到数据库（交给消息队列去处理）
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        Msg msg = param.getMsg();

        Integer aiId = Integer.valueOf(msg.getReceiveId());

        msg.setCreatedAt(TimeUtil.now());
        msg.setSent(true);
        msg.setSystem(false);
        msg.setReceived(aiIdList.contains(aiId) && !msg.getIsGroup());
        msg.setPending(false);
        msgProducer.saveMsgToMongo(msg);

        // 私聊消息、群聊消息

        // 是私聊消息
        if(!msg.getIsGroup()){
            // 给当前用户一个反馈：发送成功
            sendUserMessage(msg,ctx.channel());

            // 发送给好友（如果好友在线）
            Channel uc = UserChannelMap.get(String.valueOf(msg.getReceiveId()));
            if(uc != null){
                // 好友在线，可直接发送
                sendUserMessage(msg,uc);
            }
            // 如果是给AI发的,并且文字不为空，那么要回复！
            String text = msg.getText();
            if(text != null && !text.isEmpty()){
                if(aiIdList.contains(aiId)){
                    handlerAiBot(ctx, msg, aiId, text, msgProducer, msg.getUser().get_id());
                }
            }
            if(uc != null){
                letUserUpdateHomeScreen(uc);
            }
        } else {
            // 是群聊消息
            // 给当前用户一个反馈：发送成功
            sendUserMessage(msg,ctx.channel());

            // 发送到群聊的各个好友那里
            sendGroupMessage(uid, msg);
        }
        // 让当前用户更新主页面
        letUserUpdateHomeScreen(ctx.channel());
    }

    /**
     * 让用户的主页面刷新
     */
    private static void letUserUpdateHomeScreen(Channel uc) {
        //  (此步骤用于提醒当前用户，主页面需要刷新啦！)
        Param msg = new Param();
        msg.setCode(ParamType.CHAT.getCode());
        TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(msg));
        uc.writeAndFlush(message);
    }

    private static void handlerAiBot(ChannelHandlerContext ctx, Msg msg, Integer aiId, String text,
                                     MsgProducer msgProducer, String userid) {
        // 先发送一个 正在发送中，看起来更像AI一点！
        ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();
        executorService2.schedule(() -> {
            sendAiMsg(ctx, msg, aiId, "思考中🤔...",true);
        }, 1, TimeUnit.SECONDS);
        executorService2.shutdown();

        // 让下面的代码延迟1秒再执行
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            AIBotService aiService = SpringUtil.getBean(AIBotService.class);
            try {
                String content = "";
                if(aiId.equals(1)){
                    QykVo qykVo = aiService.askQingYunKeAI(text);
                    content = qykVo.getContent();
                }
                if(aiId.equals(12)){
                    String user = userid;
                    if(user == null || user.isEmpty()){
                        user = "dyChat";
                    }
                    content = aiService.askSiZhiAI(text,user);
                }
                Msg m1 = sendAiMsg(ctx, msg, aiId, content,false);
                msgProducer.saveMsgToMongo(m1);
            } catch (UnsupportedEncodingException e) {
                String content = "不好啦，我被偷家啦！暂时不能回答你的问题了呀！";
                Msg m2 = sendAiMsg(ctx, msg, aiId, content,false);
                msgProducer.saveMsgToMongo(m2);
            }
        }, 1, TimeUnit.SECONDS);

        // 关闭 executorService
        executorService.shutdown();

        Channel uc = UserChannelMap.get(String.valueOf(msg.getUser().get_id()));
        if(uc != null){
            // 好友在线，可直接发送
            sendUserMessage(msg,uc);
        }
    }

    private static Msg sendAiMsg(ChannelHandlerContext ctx, Msg msg, Integer aiId, String content,Boolean isBotReply) {
        Msg m1 = new Msg();
        m1.setText(content);
        m1.setCreatedAt(TimeUtil.now());
        UserService userService = SpringUtil.getBean(UserService.class);
        UserMsgVo aiVo = userService.getUserMsgVo(aiId);
        m1.setReceiveId(msg.getUser().get_id());
        m1.setUser(aiVo);
        m1.setIsGroup(false);
        m1.setSent(true);
        m1.setSystem(false);
        m1.setReceived(true);
        m1.setPending(false);
        m1.setIsBotReply(isBotReply);
        sendUserMessage(m1,ctx.channel());
        return m1;
    }


    /**
     * 发送私聊消息-文本
     */
    private static void sendUserMessage(Msg msg, Channel targetChannel) {
        TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(msg));
        targetChannel.writeAndFlush(message);
    }

    /**
     * 发送群消息-文本
     */
    private static void sendGroupMessage(String uid,Msg msg){

        // 群成员表，

        GroupMemberService gms = SpringUtil.getBean(GroupMemberService.class);
        String groupId = msg.getReceiveId();

        // 通过群id来查询当前群的所有成员
        List<GroupMember> gml = gms.getByColumn(GroupMember::getGroupId, groupId);
        for(GroupMember gm: gml){
            // 除了本人以外，其他人都要更新
            if(!Objects.equals(gm.getUserId(), uid)){
                // 未读消息数+1
                GroupMemberService service = SpringUtil.getBean(GroupMemberService.class);
                LambdaQueryChainWrapper<GroupMember> wp1 = service.lambdaQuery()
                        .eq(GroupMember::getGroupId, msg.getReceiveId())
                        .eq(GroupMember::getUserId, gm.getUserId());
                GroupMember gm1 = service.limitOne(wp1);
                if(gm1 != null){
                    String noReadCount = gm1.getNoReadCount();
                    String num = String.valueOf(Integer.parseInt(noReadCount) + 1);
                    LambdaUpdateChainWrapper<GroupMember> wp2 = service.lambdaUpdate()
                            .set("no_read_count", num)
                            .eq(GroupMember::getGroupId, msg.getReceiveId())
                            .eq(GroupMember::getUserId, gm.getUserId());
                    service.update(wp2);
                }
            }
            Channel chan = UserChannelMap.get(String.valueOf(gm.getUserId()));
            if(chan != null){
                // 当前成员在线，直接推送消息过去！
                sendUserMessage(msg, chan);
                letUserUpdateHomeScreen(chan);
            }
        }
    }

}
