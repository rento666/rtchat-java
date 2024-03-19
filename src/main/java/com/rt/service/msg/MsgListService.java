package com.rt.service.msg;

import com.anwen.mongo.conditions.query.LambdaQueryChainWrapper;
import com.rt.common.Constants;
import com.rt.common.Result;
import com.rt.entity.group.GroupMember;
import com.rt.entity.msg.Msg;
import com.rt.entity.msg.MsgListVo;
import com.rt.entity.user.UserMsgVo;
import com.rt.service.group.GroupMemberService;
import com.rt.service.group.GroupService;
import com.rt.service.user.FriendService;
import com.rt.service.user.UserService;
import com.rt.utils.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MsgListService {
    @Resource
    private MsgService msgService;
    @Resource
    private FriendService friendService;
    @Resource
    private GroupMemberService gmService;
    @Resource
    private GroupService groupService;
    @Resource
    private UserService userService;

    public List<MsgListVo> getFriendList(Integer uid) {
        List<MsgListVo> mlvs = friendService.getFriendsToMsgList(uid);
        if(mlvs == null) {
            return null;
        }
        mlvs.stream()
                .filter(mlv -> !mlv.getIsGroup())
                .forEach(mlv -> {
                    Integer fid = mlv.getId();
                    getLastMsgByUfId(mlv,uid,fid);
                });
        UserMsgVo umv = userService.getUserMsgVo(uid);

        // 自己也能给自己发消息！
        MsgListVo selfMLV = new MsgListVo();
        selfMLV.setId(uid);
        selfMLV.setIsGroup(false);
        selfMLV.setUsername(umv.getName() + "(自己)");
        selfMLV.setImg(umv.getAvatar());
        List<Msg> msg = findMsgByUidAndFid(uid, uid);
        if(!msg.isEmpty()){
            Msg m = msg.get(0);
            getText(selfMLV, m);
        }else{
            selfMLV.setMessageText("");
            selfMLV.setMessageTime(umv.getTime());
        }
        selfMLV.setNoReadCount(Constants.noReadCount);
        mlvs.add(selfMLV);
        return mlvs;
    }

    /**
     * 获取朋友给用户发的所有消息
     * @param uid 用户id
     * @param fid 朋友id
     * @return 消息列表
     */
    public List<Msg> findMsgByUidAndFid(Integer uid, Integer fid) {
        // fid为发送消息的人
        UserMsgVo umv = userService.getUserMsgVo(fid);
        if(umv == null) {
            return Collections.emptyList();
        }
        LambdaQueryChainWrapper<Msg> w2 = msgService.lambdaQuery()
                .eq("user._id", String.valueOf(fid))
                .eq(Msg::getReceiveId, String.valueOf(uid))
                .eq(Msg::getIsGroup, false);
        return w2
                .orderByDesc(Msg::getCreatedAt)
                .list();
    }

    public List<MsgListVo> getGroupList(Integer uid){
        // 查询当前用户id在哪些群
        List<GroupMember> gml = gmService.lambdaQuery()
                .eq(GroupMember::getUserId, String.valueOf(uid))
                .eq(GroupMember::getStatus, Constants.inHere)
                .list();
        // 如果用户不是群成员，则说明没加入群！
        if(gml == null) {
            return Collections.emptyList();
        }
        List<MsgListVo> mlvs= new ArrayList<>();
        gml.forEach(gm -> {
            Integer gid = Integer.valueOf(gm.getGroupId());
            // 获取所在的群信息
            MsgListVo mlv = groupService.getGroupsToMsgList(gid);
            mlv.setNoReadCount(gm.getNoReadCount());
            // 查找最后一条群消息
            List<Msg> mgs = findMsgByUidAndGid(gid);

            if(!mgs.isEmpty()){
                Msg mg = mgs.get(0);
                mlv.setMessageTime(mg.getCreatedAt());
                mlv.setMessageText(mg.getText());
            }else {
                mlv.setMessageTime(gm.getJoinAt());
                mlv.setMessageText("");
            }
            mlvs.add(mlv);

        });
        return mlvs;
    }

    /**
     * 获取当前用户所在目标群聊的所有消息
     */
    public List<Msg> findMsgByUidAndGid(Integer gid){
        // receive_id 是 gid、isGroup 为 true
        LambdaQueryChainWrapper<Msg> wrapper = msgService.lambdaQuery()
                .eq(Msg::getReceiveId, String.valueOf(gid))
                .eq(Msg::getIsGroup, true);
        return wrapper
                .orderByDesc(Msg::getCreatedAt)
                .list();
    }

    /**
     * 获取当前用户与朋友的最后一条信息
     */
    public void getLastMsgByUfId(MsgListVo mlv, Integer uid, Integer fid) {
        // 好友发的
        List<Msg> m1 = findMsgByUidAndFid(uid, fid);
        // 用户发的
        List<Msg> m2 = findMsgByUidAndFid(fid, uid);
        if(!m1.isEmpty()){
            Msg msg1 = m1.get(0);
            if(!m2.isEmpty()){
                Msg msg2 = m2.get(0);

                if(TimeUtil.isLeast(msg1.getCreatedAt(),msg2.getCreatedAt())){
                    //说明好友是最后发消息的
                    getText(mlv, msg1);
                    // 当前用户是消息接收方，可以查询未读消息数
                    mlv.setNoReadCount(getNoReadCount(uid, fid));
                }else{
                    // 用户是最后发消息的,则说明全都已读
                    getText(mlv, msg2);
                    mlv.setNoReadCount(Constants.noReadCount);
                }
            }else{
                // 好友发了，用户没发
                //说明好友是最后发消息的
                mlv.setMessageText(msg1.getText());
                mlv.setMessageTime(msg1.getCreatedAt());
                // 当前用户是消息接收方，可以查询未读消息数
                mlv.setNoReadCount(getNoReadCount(uid, fid));
            }
        }else if(!m2.isEmpty()){
            // 好友没有发送消息，看看用户发没发
            // 用户发了，则直接设置
            Msg msg2 = m2.get(0);
            mlv.setMessageText(msg2.getText());
            mlv.setMessageTime(msg2.getCreatedAt());
            mlv.setNoReadCount(Constants.noReadCount);
        }else{
            // 两人没发过消息，设为空
            String time = friendService.getTime(uid, fid);
            mlv.setMessageTime(time);
            mlv.setMessageText("");
            mlv.setNoReadCount(Constants.noReadCount);
        }
    }

    private void getText(MsgListVo mlv, Msg msg1) {
        String text = msg1.getText() != null ? msg1.getText() : "";
        if(msg1.getImage() != null && !msg1.getImage().isEmpty()){
            text += " [图片]";
        }
        if(msg1.getAudio() != null && !msg1.getAudio().isEmpty()){
            text = " [语音]";
        }
        if(msg1.getVideo() != null && !msg1.getVideo().isEmpty()){
            text += " [视频]";
        }
        mlv.setMessageText(text);
        mlv.setMessageTime(msg1.getCreatedAt());
    }

    private String getNoReadCount(Integer uid, Integer fid) {
        String s = String.valueOf(msgService.count(
                msgService.lambdaQuery()
                        .eq("user._id", String.valueOf(fid))
                        .eq(Msg::getReceiveId, String.valueOf(uid))
                        .eq(Msg::getReceived, false)
                        .eq(Msg::getIsGroup, false)));
        return s;
    }

}
