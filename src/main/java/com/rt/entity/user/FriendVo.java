package com.rt.entity.user;

import lombok.Data;

@Data
public class FriendVo {
    // 此uid由token获取
    private Integer uid;
    // 想要添加的好友id
    private Integer fid;
    // 申请内容，例如：我是xxx
    private String content;
    // 给好友的备注
    private String remark;
    // 标签
    private String label;
}
