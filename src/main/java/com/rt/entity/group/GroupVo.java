package com.rt.entity.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupVo {
    // 群id（创建群后设置，前端不用传递）
    private Integer gid;
    // 群主id（通过token获取，前端不用传递）
    private Integer uid;
    // 群名
    private String name;
    // 群头像
    private String img;
    // 群成员uid
    private List<Integer> members;
}
