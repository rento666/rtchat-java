package com.rt.service.group;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rt.common.Result;
import com.rt.entity.group.Group;
import com.rt.entity.group.GroupVo;
import com.rt.entity.msg.MsgListVo;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.im.param.ParamType;
import com.rt.mapper.group.GroupMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class GroupImpl extends ServiceImpl<GroupMapper,Group> implements GroupService{
    @Resource
    private GroupMapper groupMapper;

    @Override
    public Result createGroup(GroupVo groupVo) {
        if(groupVo == null || groupVo.getUid() == null
                || groupVo.getName() == null || groupVo.getImg() == null){
            // 参数缺失
            return new Result().errorInfoLack();
        }
        Group one = new Group().createOne(groupVo.getUid(), groupVo.getName(), groupVo.getImg());
        int i = groupMapper.insert(one);
        if(i != 1){
            return new Result().error("创建群聊失败");
        }

        return new Result().success("创建成功",one);
    }

    @Override
    public MsgListVo getGroupsToMsgList(Integer gid) {
        Group group = groupMapper.selectById(gid);
        MsgListVo mlv = new MsgListVo();
        // id为群id
        mlv.setId(gid);
        mlv.setIsGroup(true);
        mlv.setUsername(group.getName());
        mlv.setImg(group.getImg());
        return mlv;
    }

}
