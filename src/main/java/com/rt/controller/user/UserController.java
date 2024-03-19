package com.rt.controller.user;

import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.entity.user.User;
import com.rt.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @GetMapping("/info")
    public Result info(){
        Integer uid = UserContext.getUserId();
        return userService.getUserInfo(uid);
    }

    @PostMapping("/update")
    public Result updateUser(@RequestBody User user) {
        Integer uid = UserContext.getUserId();
        user.setId(uid);
        if(!userService.updateUser(user)){
            return new Result().error("更新失败");
        }
        return new Result().success("更新成功", true);
    }
}
