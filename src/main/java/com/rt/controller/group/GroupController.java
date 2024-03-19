package com.rt.controller.group;

import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.entity.group.Group;
import com.rt.entity.group.GroupVo;
import com.rt.service.group.GroupMemberService;
import com.rt.service.group.GroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Resource
    private GroupService groupService;

    @Resource
    private GroupMemberService gmService;

    // 创建一个群聊
    @PostMapping("/new")
    public Result save(@RequestBody GroupVo groupVo){
        Integer uid = UserContext.getUserId();
        if(uid == null){
            return new Result().error("未知用户在创建群聊");
        }
        groupVo.setUid(uid);

        return groupService.createGroup(groupVo);
    }
    @GetMapping("/list")
    public Result list() {
        Integer userId = UserContext.getUserId();
        // 去查群成员表，如果是群成员，那就说明在群里
        return new Result().success(gmService.getList(userId));
    }

}
