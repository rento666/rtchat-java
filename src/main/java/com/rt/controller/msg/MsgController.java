package com.rt.controller.msg;

import com.alibaba.fastjson.JSON;
import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.entity.msg.Msg;
import com.rt.entity.msg.MsgListVo;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.im.param.ParamType;
import com.rt.service.group.GroupMemberService;
import com.rt.service.msg.MsgImpl;
import com.rt.service.msg.MsgListService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/msg")
public class MsgController {

    @Resource
    private MsgListService mlService;
    @Resource
    private MsgImpl msgImpl;
    @Resource
    private GroupMemberService groupMemberService;

    @GetMapping("/list")
    public Result list() {
        // 获取消息列表(私聊+群聊) 按照时间逆序

        // 获取当前用户有多少好友、多少群
        // 再去查这些好友有哪些说过话了
        // 前端展示页面的删除，是删除的缓存，不用修改数据库的数据，可以在缓存上加个标志，某某id的直到下次新消息来之前不显示

        Integer uid = UserContext.getUserId();
        // 好友的消息列表
        List<MsgListVo> fls = mlService.getFriendList(uid);
        // 群聊的消息列表
        List<MsgListVo> gls = mlService.getGroupList(uid);

        if(fls.isEmpty() && gls.isEmpty()) {
            // 不可能查不到，因为在注册时，必定添加了AI
            return new Result().error("错误账户");
        }
        else if(!fls.isEmpty()  && !gls.isEmpty()) {
            // 合并两个列表
            List<MsgListVo> combinedList = Stream.concat(fls.stream(), gls.stream())
                    .collect(Collectors.toList());
            // 根据 messageTime 属性逆序排列
            List<MsgListVo> sortedList = combinedList.stream()
                    .sorted(Comparator.comparing(MsgListVo::getMessageTime, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            return new Result().success(sortedList);
        }else {
            List<MsgListVo> res;
            if (!fls.isEmpty()) {
                res = fls.stream()
                        .sorted(Comparator.comparing(MsgListVo::getMessageTime, Comparator.nullsFirst(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            }else{
                res = gls.stream()
                        .sorted(Comparator.comparing(MsgListVo::getMessageTime, Comparator.nullsFirst(Comparator.reverseOrder())))
                        .collect(Collectors.toList());
            }
            return new Result().success(res);
        }
    }

    @GetMapping("/friend/{fid}")
    public Result getMsgByFid(@PathVariable("fid") Integer fid){
        // 通过好友id去查询聊天记录
        Integer uid = UserContext.getUserId();
        // 如果查询的是本人的消息，那么就查一次即可！
        if(uid.equals(fid)){
            List<Msg> msg = mlService.findMsgByUidAndFid(fid, uid);
            List<Msg> list = msg.stream()
                    .filter(mlv -> mlv.getCreatedAt() != null)
                    .sorted(Comparator.comparing(Msg::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            return new Result().success(list);
        }
        // 查询朋友发的
        List<Msg> msg1 = mlService.findMsgByUidAndFid(uid, fid);
        // 查询用户发的
        List<Msg> msg2 = mlService.findMsgByUidAndFid(fid, uid);
        List<Msg> collect = Stream.concat(msg1.stream(), msg2.stream())
                .collect(Collectors.toList());
        List<Msg> list = collect.stream()
                .filter(mlv -> mlv.getCreatedAt() != null)
                .sorted(Comparator.comparing(Msg::getCreatedAt).reversed())
                .collect(Collectors.toList());
        return new Result().success(list);
    }

    @PostMapping("/read/friend/{fid}")
    public Result readAllMsgOnlyOneFriend(@PathVariable("fid") Integer fid) {
        // 已读此朋友的所有未读消息
        Integer uid = UserContext.getUserId();

        if(!uid.equals(fid)){
            Boolean b = msgImpl.readAllMsgOnlyOneFriend(uid, fid);
            if(!b){
                return new Result().error("read fail");
            }
        }
        Channel uc = UserChannelMap.get(String.valueOf(uid));
        if(uc != null){
            Param msg = new Param();
            msg.setCode(ParamType.CHAT.getCode());
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(msg));
            uc.writeAndFlush(message);
        }
        return new Result().suc("read");
    }

    @GetMapping("/group/{gid}")
    public Result getMsgByGid(@PathVariable("gid") Integer gid){
        // 通过群聊id去查询聊天记录
        Integer uid = UserContext.getUserId();
        // receive_id 是 gid、isGroup 为 true
        List<Msg> list = mlService.findMsgByUidAndGid(gid);
        return new Result().success(list);
    }

    @PostMapping("/read/group/{gid}")
    public Result readAllMsgOnlyOneGroup(@PathVariable("gid") Integer gid) {
        // 已读此群的所有未读消息
        Integer uid = UserContext.getUserId();

        if(!uid.equals(gid)){
            Boolean b = groupMemberService.readAllMsgOnlyOneGroup(uid, gid);
            if(!b){
                return new Result().error("read group fail");
            }
        }
        Channel uc = UserChannelMap.get(String.valueOf(uid));
        if(uc != null){
            Param msg = new Param();
            msg.setCode(ParamType.CHAT.getCode());
            TextWebSocketFrame message = new TextWebSocketFrame(JSON.toJSONString(msg));
            uc.writeAndFlush(message);
        }
        return new Result().suc("read group");
    }

    @GetMapping("/noRead/count")
    public Result getNoReadCount(){
        // 获取当前用户所有未读消息数(私聊+群聊)
        Integer uid = UserContext.getUserId();

        // 私聊未读
        Long count1 = msgImpl.getNoReadFriend(uid);
        // 群聊未读
        Long count2 = groupMemberService.getCountInManyGroup(uid);

        return new Result().success(count1 + count2);
    }
}
