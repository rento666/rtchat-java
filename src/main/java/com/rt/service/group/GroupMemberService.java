package com.rt.service.group;

import com.anwen.mongo.service.IService;
import com.rt.entity.group.GroupMember;
import com.rt.entity.group.GroupVo;
import com.rt.entity.user.ContactItem;

import java.util.List;

public interface GroupMemberService extends IService<GroupMember> {
    List<ContactItem> getList(Integer uid);

    Boolean insertGroupMembers(GroupVo groupVo);

    Long getCount(Integer gid);

    Long getCountInManyGroup(Integer uid);

    Boolean readAllMsgOnlyOneGroup(Integer uid, Integer gid);
}
