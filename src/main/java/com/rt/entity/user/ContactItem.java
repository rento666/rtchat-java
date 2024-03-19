package com.rt.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactItem {

    // fid 或者 gid
    private Integer id;
    private String img;
    // 拼音
    private String name;
    private String userName;
    // 朋友才有这个
    private String subText;
    // 朋友状态
    private Integer status;
    // 时间（要排序）
    private String time;
    // 是主动还是被动？（主动添加or被动添加？）
    private Boolean isActive;
}
