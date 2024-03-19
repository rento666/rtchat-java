package com.rt.entity.msg;

import com.anwen.mongo.annotation.ID;
import com.anwen.mongo.annotation.collection.CollectionField;
import com.anwen.mongo.annotation.collection.CollectionName;
import com.anwen.mongo.enums.IdTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rt.entity.user.UserMsgVo;
import com.rt.utils.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CollectionName("msg")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Msg implements Serializable {
    @ID(type = IdTypeEnum.ASSIGN_ID)
    private String _id;
    // 文本消息
    private String text;
    // 创建时间（时间戳）
    @CollectionField("created_at")
    private String createdAt;
    // 发送消息的人
    private UserMsgVo user;
    // 图片链接
    private String image;
    // 视频链接
    private String video;
    // 音频链接
    private String audio;
    // 是不是系统消息
    private Boolean system;
    // 是不是已发送
    private Boolean sent;
    // 是不是已经接收
    private Boolean received;
    // 是不是代办（创建闹钟）
    private Boolean pending;
    // 接收消息的人-id （friend表）
    @CollectionField("receive_id")
    private String receiveId;
    // 判断 receiveId 是 friend的uid还是group的gid —— 1 true, 0 false
    @CollectionField("is_group")
    private Boolean isGroup;
    // 当且仅当AI发送。。。的时候为true
    @CollectionField(exist = false)
    private Boolean isBotReply;

    public Msg newSystem(UserMsgVo user, String msg, String userId, Boolean isGroup) {
        return newMsg(msg,user, userId, isGroup, "","",
                "", true, true, true,false);
    }

    public Msg newAiHello(UserMsgVo aiUser, String userId, Boolean isGroup) {
        return newMsg("你好，我是AI助手",aiUser,userId,isGroup, "",
                "","", false, true, false, false);
    }

    public Msg newMsg(String msg,UserMsgVo user,String receiveId,
                      Boolean isGroup, String image,String video,
                      String audio, Boolean isSystem, Boolean sent,
                      Boolean received, Boolean pending) {
        Msg m = new Msg();
        m.setText(msg);
        m.setCreatedAt(TimeUtil.now());
        m.setUser(user);
        m.setReceiveId(receiveId);
        m.setIsGroup(isGroup);
        m.setImage(image);
        m.setVideo(video);
        m.setAudio(audio);
        m.setSystem(isSystem);
        m.setSent(sent);
        m.setReceived(received);
        m.setPending(pending);
        return m;
    }
}
