package com.rt.im.param;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParamType {
    /**
     * 建立连接
     */
    CONNECTION(10001),
    /**
     * 心跳检测
     */
    PING(10086),
    /**
     * 聊天消息
     */
    CHAT(10002),
    /**
     * 好友消息
     */
    FRIEND(20006),
    /**
     * 个人信息消息
     */
    PROFILE(30008),

    ERROR(-1);

    private final Integer code;

    public static ParamType match(Integer code) {
        for(ParamType value : ParamType.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return ERROR;
    }
}
