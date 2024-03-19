package com.rt.service.group;

import com.alibaba.fastjson.JSON;
import com.anwen.mongo.conditions.query.LambdaQueryChainWrapper;
import com.anwen.mongo.conditions.query.QueryWrapper;
import com.anwen.mongo.conditions.update.LambdaUpdateChainWrapper;
import com.anwen.mongo.service.impl.ServiceImpl;
import com.rt.common.Constants;
import com.rt.entity.group.Group;
import com.rt.entity.group.GroupMember;
import com.rt.entity.group.GroupVo;
import com.rt.entity.user.ContactItem;
import com.rt.entity.user.User;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.im.param.ParamType;
import com.rt.service.user.UserService;
import com.rt.utils.PinYinUtil;
import com.rt.utils.TimeUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GroupMemberImpl extends ServiceImpl<GroupMember> implements GroupMemberService{


    @Resource
    GroupService groupService;
    @Resource
    UserService userService;

    @Override
    public List<ContactItem> getList(Integer uid) {
        // 获取当前用户的所有群聊
        LambdaQueryChainWrapper<GroupMember> lmq = this.lambdaQuery();
        List<GroupMember> list = lmq.eq(GroupMember::getUserId, String.valueOf(uid))
                .eq(GroupMember::getStatus, Constants.inHere)
                .list();
        List<ContactItem> res = new ArrayList<>();
        if(list.isEmpty()){
            return res;
        }
        list.forEach(groupMember -> {
            String groupId = groupMember.getGroupId();
            Group group = groupService.getById(Integer.valueOf(groupId));
            ContactItem item = new ContactItem();
            item.setId(group.getId());
            String name = isNotNull(groupMember.getRemark()) ? groupMember.getRemark() : group.getName();
            item.setName(PinYinUtil.getPinYin(name));
            item.setUserName(name);
            item.setImg(group.getImg());
            res.add(item);
        });
        return res;
    }
    private Boolean isNotNull(String str) {
        return str != null && !str.isEmpty();
    }

    // 此方法为，新建群聊时的邀请成员，所以状态为直接进群，没有申请、拒绝、拉黑一说！
    @Override
    public Boolean insertGroupMembers(GroupVo groupVo){

        List<User> users = userService.selectBatchByIds(groupVo.getMembers());
        List<GroupMember> gms = new ArrayList<>();
        // 记得把自己加进去！！
        GroupMember gmer = new GroupMember();
        String gid = String.valueOf(groupVo.getGid());
        gmer.setGroupId(gid);
        gmer.setUserId(String.valueOf(groupVo.getUid()));
        gmer.setStatus(Constants.inHere);
        gmer.setJoinAt(TimeUtil.now());
        gmer.setNoReadCount(Constants.noReadCount);
        gmer.setIsBlockMsg(Constants.isNotBlockMsg);
        gms.add(gmer);
        users.forEach(user -> {
            GroupMember gm = new GroupMember();
            gm.setGroupId(gid);
            gm.setUserId(String.valueOf(user.getId()));
            gm.setStatus(Constants.inHere);
            gm.setGroupUserName(user.getUsername());
            gm.setJoinAt(TimeUtil.now());
            gm.setNoReadCount(Constants.noReadCount);
            gm.setIsBlockMsg(Constants.isNotBlockMsg);
            gms.add(gm);
        });

        Channel uc = UserChannelMap.get(String.valueOf(groupVo.getUid()));
        if(uc != null){
            Param param = new Param();
            param.setCode(ParamType.FRIEND.getCode());
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(param));
            uc.writeAndFlush(message);
        }

        return this.saveBatch(gms);
    }

    @Override
    public Long getCount(Integer gid) {
        // 获取这个群有多少用户
        LambdaQueryChainWrapper<GroupMember> wrapper = this.lambdaQuery()
                .eq(GroupMember::getGroupId, String.valueOf(gid))
                .eq(GroupMember::getStatus, Constants.inHere);
        return this.count(wrapper);
    }

    @Override
    public Long getCountInManyGroup(Integer uid) {
        // 获取当前用户有多少群
        LambdaQueryChainWrapper<GroupMember> wrapper = this.lambdaQuery()
                .eq(GroupMember::getUserId, String.valueOf(uid))
                .eq(GroupMember::getStatus, Constants.inHere);

        List<GroupMember> list = this.list(wrapper);
        AtomicLong res = new AtomicLong(0L);
        list.forEach(gm -> {
            if(gm.getIsBlockMsg().equals(Constants.isNotBlockMsg)){
                res.addAndGet(Integer.parseInt(gm.getNoReadCount()));
            }
        });
        return res.get();
    }

    @Override
    public Boolean readAllMsgOnlyOneGroup(Integer uid, Integer gid) {
        LambdaUpdateChainWrapper<GroupMember> w = this.lambdaUpdate()
                .set("no_read_count", Constants.noReadCount)
                .eq(GroupMember::getUserId, String.valueOf(uid))
                .eq(GroupMember::getGroupId, String.valueOf(gid));

        return this.update(w);
    }
}
