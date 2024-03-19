package com.rt.service.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rt.common.Constants;
import com.rt.common.Result;
import com.rt.entity.msg.Msg;
import com.rt.entity.msg.MsgListVo;
import com.rt.entity.user.*;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.im.param.ParamType;
import com.rt.mapper.user.FriendMapper;
import com.rt.service.msg.MsgService;
import com.rt.utils.PinYinUtil;
import com.rt.utils.TimeUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FriendImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService{
    @Resource
    FriendMapper friendMapper;
    @Resource
    UserService userService;
    @Resource
    MsgService msgService;

    @Override
    public Result getList(Integer uid) {
        if(uid == null){
            return null;
        }
        QueryWrapper<Friend> wp = new QueryWrapper<>();
        wp.eq("uid", uid)
                .eq("status", Constants.inHere);
        List<Friend> list = friendMapper.selectList(wp);
        List<ContactItem> res = new ArrayList<>();
        if(list.isEmpty()){
            return new Result().success(res);
        }
        list.forEach(friend -> {
            User f = userService.getById(friend.getFid());
            ContactItem item = new ContactItem();
            item.setId(f.getId());
            item.setImg(f.getAvatar());
            String name = isNotNull(friend.getRemark()) ? friend.getRemark() : f.getUsername();
            item.setName(PinYinUtil.getPinYin(name));
            item.setUserName(name);
            item.setSubText(f.getEmail());
            res.add(item);
        });
        return new Result().success(res);
    }

    private Boolean isNotNull(String str) {
        return str != null && !str.isEmpty();
    }

    @Override
    public List<MsgListVo> getFriendsToMsgList(Integer uid) {
        if(uid == null){
            return null;
        }
        // 已经是好友、并且没有主动删除他的（对方删没删 我不知道）
        List<MsgListVo> mlvs = friendMapper.selectMsgListInFriend(uid);
        mlvs.forEach(msgListVo ->{
            msgListVo.setIsGroup(false);
            if(msgListVo.getRemark() == null){
                msgListVo.setRemark("");
            }
        });
        return mlvs;
    }

    @Override
    public Boolean isFriend(Integer uid, Integer fid) {
        if(uid.equals(fid)){
            return true;
        }
        Friend friend = friendMapper.searchByUidAndFid(uid, fid);
        if(friend != null){
            // 已经是好友，无需再次申请
            return friend.getStatus().equals(Integer.valueOf(Constants.inHere));
        }
        return false;
    }

    @Override
    public Long getNewFriendCount(Integer uid) {
        QueryWrapper<Friend> qw = new QueryWrapper<>();
        qw.eq("fid", uid).eq("status", Constants.apply);
        return friendMapper.selectCount(qw);
    }

    @Override
    public String getTime(Integer uid, Integer fid) {
        QueryWrapper<Friend> wp = new QueryWrapper<>();
        wp.eq("uid", uid).eq("fid", fid);
        Friend friend = friendMapper.selectOne(wp);
        return friend.getCreateAt();
    }

    @Override
    public Result getApplyList(Integer uid) {
        // 获取当前用户 有没有新朋友待确认 包括申请中、已经拒绝的
        // 先查询对方申请我们的，再查询我们申请对方的、然后查询拒绝的
        QueryWrapper<Friend> qw = new QueryWrapper<>();
        qw.and(q -> q.eq("fid", uid).eq("status", Constants.apply))
                .or(wp -> wp.eq("fid", uid).eq("status", Constants.refusing))
                .orderByDesc("create_at");

        List<Friend> list = friendMapper.selectList(qw);
        List<ContactItem> res = new ArrayList<>();
        if(list.isEmpty()){
            return new Result().success(res);
        }
        list.forEach(friend -> {
            boolean b = friend.getUid().equals(uid);
            Integer fid = b ? friend.getFid() : friend.getUid();
            User f = userService.getById(fid);
            ContactItem item = new ContactItem();
            item.setId(f.getId());
            item.setIsActive(b);
            item.setImg(f.getAvatar());
            String name = isNotNull(friend.getRemark()) ? friend.getRemark() : f.getUsername();
            item.setUserName(name);
            item.setStatus(friend.getStatus());
            item.setTime(friend.getCreateAt());
            item.setSubText(friend.getApplyText());
            res.add(item);
        });
        return new Result().success(res);
    }

    @Override
    public Result applyFriend(FriendVo friendVo) {
        if(!existFv(friendVo)){
            return new Result().errorInfoLack();
        }
        Integer uid = friendVo.getUid();
        Integer fid = friendVo.getFid();
        // 这里需要查询是否已经申请过
        Friend friend = friendMapper.searchByUidAndFid(uid, fid);
        // 能查到记录，说明两人有过联系
        if(friend != null) {
            // 已经是好友，无需再次申请
            if(friend.getStatus().equals(Integer.valueOf(Constants.inHere))){
                return new Result().error("你们已经是好友啦");
            }
            // 已经在申请中，无需再次提交申请
            if(friend.getStatus().equals(Integer.valueOf(Constants.apply))){
                return new Result().error("正在申请中...");
            }
            // 拉黑后，需要进行取消拉黑,所以此时不能添加好友
            if (friend.getStatus().equals(Integer.valueOf(Constants.blocking))){
                return new Result().error("你已拉黑TA，暂时无法申请");
            }
            // 拒绝之后可以申请添加,所以拒绝可以向下执行。
            if(friend.getStatus().equals(Integer.valueOf(Constants.blocking))){
                friend.setStatus(Integer.valueOf(Constants.apply));
                friend.setApplyText(friendVo.getContent());
                friend.setLabel(friendVo.getLabel());
                friend.setRemark(friendVo.getRemark());
                int count = friendMapper.updateById(friend);
                if(count != 1){
                    return new Result().error("重新申请失败,请重试...");
                }
            }
        } else {
            // 没查到记录，说明是初次申请
            // 如何添加申请？ 新增一条记录 uid为申请人 fid为被添加人 status为apply
            Friend f = new Friend().initFriend(uid, fid);
            f.setApplyText(friendVo.getContent());
            f.setLabel(friendVo.getLabel());
            f.setRemark(friendVo.getRemark());
            int i = friendMapper.insert(f);
            if(i != 1){
                return new Result().error("申请失败,请重试...");
            }
        }

        // 申请之后，好友那边会收到通知请求 (websocket!)
        Channel uc = UserChannelMap.get(String.valueOf(fid));
        if(uc != null){
            // 好友在线，可直接发送
            Param param = new Param();
            param.setCode(ParamType.FRIEND.getCode());
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(param));
            uc.writeAndFlush(message);
        }

        return new Result().suc("申请成功");
    }

    @Override
    public Result agreeFriend(FriendVo friendVo) {
        // 同意好友申请
        if(!existFv(friendVo)){
            return new Result().errorInfoLack();
        }
        Integer uid = friendVo.getUid();
        Integer fid = friendVo.getFid();
        // 更新好友那边的申请为通过
        UpdateWrapper<Friend> fuw = new UpdateWrapper<>();
        fuw.eq("uid",fid).eq("fid",uid).eq("status",Integer.valueOf(Constants.apply));
        Friend f1 = new Friend();
        f1.setStatus(Integer.valueOf(Constants.inHere));
        f1.setCreateAt(TimeUtil.now());  // 创建时间为初次添加好友时间
        int i1 = friendMapper.update(f1, fuw);
        if(i1 != 1) {
            return new Result().error("暂时无法同意,可能TA没有添加你");
        }
        // 新增一条自己的好友数据
        Friend f2 = new Friend().initFriend(uid, fid);
        f2.setStatus(Integer.valueOf(Constants.inHere));
        int i2 = friendMapper.insert(f2);
        if(i2 != 1){
            return new Result().error("对方已添加你，但你添加对方失败");
        }

        // 由系统发送一条消息，“我通过了你的朋友验证请求，现在我们可以开始聊天了”
        // 涉及到聊天部分，此时先向数据库插入一条数据？
        String str = "我通过了你的朋友验证请求，现在我们可以开始聊天了";
        UserMsgVo vo = userService.getUserMsgVo(uid);
        Msg msg = new Msg().newMsg(str,vo,String.valueOf(fid),false,"","","",
                false,true,false,false);
        msgService.save(msg);

        Channel uc = UserChannelMap.get(String.valueOf(fid));
        if(uc != null){
            // 好友在线，可直接发送
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(msg));
            uc.writeAndFlush(message);

            Param param = new Param();
            param.setCode(ParamType.FRIEND.getCode());
            TextWebSocketFrame ms = new TextWebSocketFrame(JSON.toJSONString(param));
            uc.writeAndFlush(ms);
        }

        Channel uc2 = UserChannelMap.get(String.valueOf(uid));
        if(uc2 != null){
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(msg));
            uc2.writeAndFlush(message);

            Param param = new Param();
            param.setCode(ParamType.FRIEND.getCode());
            TextWebSocketFrame ms = new TextWebSocketFrame(JSON.toJSONString(param));
            uc2.writeAndFlush(ms);
        }

        return new Result().suc("同意成功");
    }

    @Override
    public Result refuseFriend(FriendVo friendVo) {
        if(!existFv(friendVo)){
            return new Result().errorInfoLack();
        }
        Integer uid = friendVo.getUid();
        Integer fid = friendVo.getFid();
        // 更新好友那边的申请为拒绝
        UpdateWrapper<Friend> fuw = new UpdateWrapper<>();
        // 更新条件为，fid正在申请添加uid
        fuw.eq("uid",fid).eq("fid",uid).eq("status",Integer.valueOf(Constants.apply));
        Friend f1 = new Friend();
        f1.setStatus(Integer.valueOf(Constants.refusing));
        int i1 = friendMapper.update(f1, fuw);
        if(i1 != 1) {
            return new Result().error("拒绝失败,可能TA没有添加你");
        }

        // TODO 拒绝好友之后，好友那边会收到通知

        return new Result().suc("拒绝成功");
    }

    @Override
    public Result blockFriend(FriendVo friendVo) {
        if(!existFv(friendVo)){
            return new Result().errorInfoLack();
        }
        Integer uid = friendVo.getUid();
        Integer fid = friendVo.getFid();
        // 首先查询好友状态存在，且是否为：“申请、是好友、拒绝、拉黑”
        Friend friend = friendMapper.searchByUidAndFid(uid, fid);
        if(friend == null
                || !friend.getStatus().equals(Integer.valueOf(Constants.apply))
                || !friend.getStatus().equals(Integer.valueOf(Constants.inHere))
                || !friend.getStatus().equals(Integer.valueOf(Constants.refusing))
                || !friend.getStatus().equals(Integer.valueOf(Constants.blocking))){
            // 如果查不到好友申请，或者好友状态不为“申请、是好友、拒绝”，那么就返回错误即可
            return new Result().error("暂不支持拉黑");
        }
        if(friend.getStatus().equals(Integer.valueOf(Constants.blocking))){
            return new Result().error("你已经拉黑了TA");
        }
        // 拉黑操作，在 “申请、是好友、拒绝”状态之后产生的！
        UpdateWrapper<Friend> fuw = new UpdateWrapper<>();
        fuw.eq("uid",uid).eq("fid",fid);
        Friend f1 = new Friend();
        f1.setStatus(Integer.valueOf(Constants.blocking));
        int i1 = friendMapper.update(f1, fuw);
        if(i1 != 1) {
            return new Result().error("暂时无法拉黑,请稍后重试...");
        }

        return new Result().suc("拉黑成功");
    }

    @Override
    public Result cancelBlock(FriendVo friendVo) {
        if(!existFv(friendVo)){
            return new Result().errorInfoLack();
        }
        // 首先判断你是否已经拉黑了对面，没有拉黑则直接返回
        Integer uid = friendVo.getUid();
        Integer fid = friendVo.getFid();
        UpdateWrapper<Friend> fuw = new UpdateWrapper<>();
        // 更新条件：uid拉黑了fid
        fuw.eq("uid",uid).eq("fid",fid).eq("status",Integer.valueOf(Constants.blocking));
        Friend f1 = new Friend();
        f1.setCreateAt(TimeUtil.now());
        // 设置好友状态：根据对方有没有你好友来判断
        Friend friend = friendMapper.searchByUidAndFid(fid, uid);
        // 当对方有你好友时，直接设置成好友；其余均设置成申请
        if(friend != null && friend.getStatus().equals(Integer.valueOf(Constants.inHere))){
            f1.setStatus(Integer.valueOf(Constants.inHere));
        }else {
            f1.setStatus(Integer.valueOf(Constants.apply));
        }
        int count = friendMapper.update(f1, fuw);
        if(count != 1){
            return new Result().error("取消拉黑失败，请确保你已拉黑TA");
        }

        return new Result().suc("取消拉黑成功，已自动申请添加TA");
    }

    @Override
    public Result delFriend(FriendVo friendVo) {
        if(!existFv(friendVo)){
            return new Result().errorInfoLack();
        }
        Friend friend = new Friend();
        friend.setIsDelete(true);
        // 已为好友的 且没有删除的 才能删除
        UpdateWrapper<Friend> fuw = new UpdateWrapper<>();
        fuw.eq("uid",friendVo.getUid())
                .eq("fid",friendVo.getFid())
                .eq("status",Integer.valueOf(Constants.inHere))
                .eq("is_delete",false);
        int i = friendMapper.update(friend, fuw);
        if(i != 1){
            return new Result().error("请确保TA是你的好友，并且你没有删除TA");
        }

        return new Result().suc("删除成功");
    }

    public Boolean existFv(FriendVo friendVo){
        return friendVo != null && friendVo.getUid() != null && friendVo.getFid() != null;
    }

}
