package com.rt.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rt.common.Result;
import com.rt.entity.msg.MsgListVo;
import com.rt.entity.user.Friend;
import com.rt.entity.user.FriendVo;

import java.util.List;

public interface FriendService extends IService<Friend> {

    // 此方法用于MsgListVo，获取朋友表那部分数据
    List<MsgListVo> getFriendsToMsgList(Integer uid);
    // 判断两人是不是好友
    Boolean isFriend(Integer uid, Integer fid);
    // 申请添加好友 （uid去申请fid，这里需要判断，uid是否被fid拉黑了、被拒绝了、两人是否已经是好友、已经申请过）
    Result applyFriend(FriendVo friendVo);
    // TODO 同意添加好友（uid同意fid的申请，所以实际上在数据库中，fid对应uid，uid对应fid）
    Result agreeFriend(FriendVo friendVo);
    // TODO 拒绝添加好友（uid拒绝fid的申请，所以实际上在数据库中，fid对应uid，uid对应fid）
    Result refuseFriend(FriendVo friendVo);
    // TODO 拉黑该用户（只需要新增\更新一条uid、fid、拉黑即可！）
    Result blockFriend(FriendVo friendVo);
    // TODO 取消拉黑
    Result cancelBlock(FriendVo friendVo);
    // TODO 删除好友（软删除，有is_delete字段）
    Result delFriend(FriendVo friendVo);
    // 获取当前用户所有好友
    Result getList(Integer uid);

    Result getApplyList(Integer uid);

    String getTime(Integer uid, Integer fid);

    Long getNewFriendCount(Integer uid);
}
