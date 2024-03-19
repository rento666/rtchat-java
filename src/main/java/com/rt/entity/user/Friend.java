package com.rt.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rt.common.Constants;
import com.rt.utils.TimeUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Friend {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer fid;
    private String createAt;
    private Integer status;
    private String remark;
    private String label;
    @TableField("apply_text")
    private String applyText;
    @TableField("is_delete")
    private Boolean isDelete;
    @TableField(exist = false)
    private String username;    //user表的昵称
    @TableField(exist = false)
    private String avatar;      //user表的头像

    // 初始化好友关系，也就是uid去申请添加fid
    public Friend initFriend(Integer uid, Integer fid) {
        Friend friend = new Friend();
        friend.setUid(uid);
        friend.setFid(fid);
        friend.setCreateAt(TimeUtil.now());
        friend.setStatus(Integer.valueOf(Constants.apply));
        friend.setIsDelete(false);
        // 备注、标签均为空
        return friend;
    }

    // 添加AI为好友，因为每个用户的第一个好友都是AI！
    public Friend addAiFriend(Integer uid, Integer aiId) {
        Friend friend = new Friend();
        friend.setUid(uid);
        friend.setFid(aiId);
        friend.setCreateAt(TimeUtil.now());
        friend.setStatus(Integer.valueOf(Constants.inHere));
        friend.setLabel(Constants.labelAI);
        friend.setIsDelete(false);
        friend.setApplyText("系统默认添加");
        return friend;
    }

}
