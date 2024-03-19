package com.rt.controller.user;

import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.entity.user.FriendVo;
import com.rt.service.user.FriendService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/friend")
public class FriendController {
    @Resource
    FriendService friendService;

    @PostMapping("/apply")
    public Result apply(@RequestBody FriendVo friendVo) {
        Integer userId = UserContext.getUserId();
        friendVo.setUid
                (userId);
        return friendService.applyFriend(friendVo);
    }

    @GetMapping("/list")
    public Result getList() {
        // 获取当前用户的所有好友
        Integer userId = UserContext.getUserId();
        return friendService.getList(userId);
    }

    @GetMapping("/list/apply")
    public Result getApplying() {
        // 获取当前用户 有没有新朋友待确认
        Integer userId = UserContext.getUserId();
        return friendService.getApplyList(userId);
    }

    @PostMapping("/agree")
    public Result agree(@RequestBody Integer fid) {
        Integer userId = UserContext.getUserId();
        FriendVo fv = new FriendVo();
        fv.setUid(userId);
        fv.setFid(fid);
        return friendService.agreeFriend(fv);
    }

    @PostMapping("/refuse")
    public Result refuse(@RequestBody Integer fid) {
        Integer userId = UserContext.getUserId();
        FriendVo fv = new FriendVo();
        fv.setUid(userId);
        fv.setFid(fid);
        return friendService.refuseFriend(fv);
    }

    @PostMapping("/block")
    public Result block(@RequestBody Integer fid) {
        Integer userId = UserContext.getUserId();
        FriendVo fv = new FriendVo();
        fv.setUid(userId);
        fv.setFid(fid);
        return friendService.blockFriend(fv);
    }
    @PostMapping("/cancel")
    public Result cancelBlock(@RequestBody Integer fid) {
        Integer userId = UserContext.getUserId();
        FriendVo fv = new FriendVo();
        fv.setUid(userId);
        fv.setFid(fid);
        return friendService.cancelBlock(fv);
    }

    @PostMapping("/del")
    public Result del(@RequestBody Integer fid) {
        Integer userId = UserContext.getUserId();
        FriendVo fv = new FriendVo();
        fv.setUid(userId);
        fv.setFid(fid);
        return friendService.delFriend(fv);
    }

    @GetMapping("/new/count")
    public Result newFriendCount() {
        // 新朋友 数量
        Integer userId = UserContext.getUserId();

        Long count = friendService.getNewFriendCount(userId);
        return new Result().success(count);
    }
}
