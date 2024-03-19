package com.rt.controller.group;

import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.entity.group.GroupVo;
import com.rt.service.group.GroupMemberService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/gm")
public class GMController {

    @Resource
    private GroupMemberService groupMemberService;

    @PostMapping("/new")
    public Result newGm(@RequestBody GroupVo groupVo) {
        // 批量创建群成员
        Integer userId = UserContext.getUserId();
        // 所需参数： userid、members、gid
        groupVo.setUid(userId);
        Boolean b = groupMemberService.insertGroupMembers(groupVo);
        if(!b){
            return new Result().error("邀请好友失败");
        }
        return new Result().success("邀请好友成功",true);
    }

    @GetMapping("/count")
    public Result getCount(@RequestParam("gid") Integer gid) {
        // 获取当前群聊的成员数量
        if(gid == null){
            return new Result().error("获取消息数失败");
        }
        Long count = groupMemberService.getCount(gid);
        return new Result().success(count);
    }
}
