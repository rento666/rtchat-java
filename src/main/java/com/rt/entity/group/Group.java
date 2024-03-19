package com.rt.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rt.utils.TimeUtil;
import lombok.Data;

// 由于在MySQL中：group和groups是保留的关键字，所以这里吧表名设为了groupes
@Data
@TableName("groupes")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {
    // gid
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    // 群主id
    private Integer uid;
    // 群名
    private String name;
    // 群头像
    private String img;
    // 群公告
    private String notice;
    // 创建群时间
    private String createAt;
    @TableField(exist = false)
    private Boolean isInGroup;

    // 创建一个群
    public Group createOne(Integer uid, String name, String img){
        Group group = new Group();
        group.setUid(uid);
        group.setName(name);
        group.setImg(img);
        group.setNotice("");
        group.setCreateAt(TimeUtil.now());
        return group;
    }

}
