package com.rt.entity.group;

import com.anwen.mongo.annotation.ID;
import com.anwen.mongo.annotation.collection.CollectionField;
import com.anwen.mongo.annotation.collection.CollectionName;
import com.anwen.mongo.enums.IdTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CollectionName("group_member")
public class GroupMember {

    @ID(type = IdTypeEnum.ASSIGN_ID)
    private String id;
    @CollectionField("group_id")
    private String groupId;
    @CollectionField("user_id")
    private String userId;
    // 成员状态（0申请加入群聊中、1在群中、2拉黑禁止入群）
    private String status;
    // 群内名
    @CollectionField("group_user_name")
    private String groupUserName;
    // 加入时间
    @CollectionField("join_at")
    private String joinAt;
    // 未读消息数
    @CollectionField("no_read_count")
    private String noReadCount;
    // 是否屏蔽群消息
    @CollectionField("is_block_msg")
    private String isBlockMsg;
    // 当前用户对群的备注
    private String remark;
}
