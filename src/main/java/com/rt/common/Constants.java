package com.rt.common;

public class Constants {
    public static String paintedEggshell = "rtChat"; // 彩蛋
    public static String redisSendEmailPrefix = "verificationCode:"; // redis 验证码前缀
    public static String identityTypeNumber = "number"; // 授权表-账号类型为蝶语号
    public static String identityTypeEmail = "email"; // 账号类型为email
    public static String identityTypePhone = "phone"; // 账号类型为手机号
    public static String msgUser = "user"; // msg表的m_type 所需值 消息类型为用户消息
    public static String msgSystem = "system"; // 系统消息
    public static String apply = "1";  // 好友、群聊状态：申请中
    public static String inHere = "0"; // 好友、群聊状态：是好友/在群内
    public static String refusing = "2";  // 好友、群聊状态：拒绝
    public static String blocking = "3"; // 好友、群聊状态：拉黑
    public static String msgStateNoRead = "1"; // 消息状态：未读
    public static String noReadCount = "0"; // 消息未读数量
    public static String isNotBlockMsg = "0"; // 是否屏蔽群消息，0为不屏蔽
    public static String labelAI = "AI"; // 好友标签label ai

}
