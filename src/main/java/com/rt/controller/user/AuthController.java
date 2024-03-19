package com.rt.controller.user;

import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.entity.user.Auth;
import com.rt.service.user.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    AuthService authService;

    @GetMapping("/code")
    public Result code(@RequestParam(defaultValue = "") String email,
                       @RequestParam(defaultValue = "") String type,
                       @RequestParam(defaultValue = "") String t) {
        return authService.sendCode(email,type, t);
    }

    @PostMapping("/login")
    public Result login(@RequestBody Auth auth, HttpServletRequest request) {

        return authService.login(auth,request);
    }

    @PostMapping("/register")
    public Result register(@RequestBody Auth auth, HttpServletRequest request) {

        return authService.register(auth,request);
    }

    @PostMapping("/password")
    public Result changePwd1(@RequestBody Auth auth){
        // 忘记密码，修改密码
        // 需要邮箱验证码(未登录)
        // identifier credential code
        Boolean b = authService.changePwd1(auth);
        if(!b){
            return new Result().error("修改失败");
        }

        return new Result().success("修改成功", true);
    }

    @PostMapping("/password/know")
    public Result changePwd2(@RequestBody Auth auth){
        // 已知密码，修改密码
        // 需要原密码（登录状态）
        Integer userId = UserContext.getUserId();
        if(userId == null){
            // 需要判断，因为此接口可能没有token
            return new Result().error("错误的用户");
        }

        return new Result().success("修改成功", true);
    }

}
