package com.rt.entity.msg;

import lombok.Data;

// 消息列表对象
@Data
public class MsgListVo {
    // 好友id或者群聊id
    private Integer id;
    // 是群吗？
    private Boolean isGroup;
    // 好友昵称或者群聊昵称
    private String username;
    // 好友备注或者群聊备注
    private String remark;
    // 好友头像或者群聊头像
    private String img;
    // 好友消息最后一条时间或者群聊消息最后一条时间
    private String messageTime;
    // 好友消息或者群聊消息
    private String messageText;
    // 消息未读数(当用户为接收消息者时才有效) uid==receId
    private String noReadCount;
}
