package com.rt.service.group;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rt.common.Result;
import com.rt.entity.group.Group;
import com.rt.entity.group.GroupVo;
import com.rt.entity.msg.MsgListVo;

public interface GroupService extends IService<Group> {

    Result createGroup(GroupVo groupVo);

    MsgListVo getGroupsToMsgList(Integer gid);
}
