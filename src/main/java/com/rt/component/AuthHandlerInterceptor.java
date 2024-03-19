package com.rt.component;

import com.alibaba.fastjson.JSON;
import com.rt.common.Code;
import com.rt.common.Result;
import com.rt.entity.user.User;
import com.rt.service.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Component
public class AuthHandlerInterceptor implements HandlerInterceptor {
    @Resource
    JwtConfig tokenUtil;


    @Resource
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是映射到方法直接通过,可以访问资源.
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //为空就返回错误
        String token = request.getHeader("token");
        if (null == token || token.trim().isEmpty()) {
            printJson(response,"空令牌！");
            return false;
        }
        Map<String,String> map;
        try{
            map = tokenUtil.parseToken(token);
        }catch (Exception e){
            printJson(response,"假令牌！");
            return false;
        }
        String uid = map.get("uid");
        User user = userService.getById(Integer.valueOf(uid));
        if(user == null) {
            printJson(response,"无效的令牌！");
            return false;
        }
        UserContext.setUserId(user.getId());
        // 7天刷新时间
        long refreshTime = 1000 * 60 * 60 * 24 * 7;
        // 15天过期
        long expiresTime = 1000 * 60 * 60 * 24 * 15;
        // token开始日期
        long timeStamp = Long.parseLong(map.get("timeStamp"));
        // token已使用天数，假设 这里是8天
        long timeOfUse = System.currentTimeMillis() - timeStamp;
        // 8<7 ? -> false 需要刷新了，再不刷新就过期了
        if (timeOfUse < refreshTime) {
            return true;
        }
        //  8<15 ? -> true 还没过期，等超过15天了就不能刷新新token了
        else if (timeOfUse < expiresTime) {
            response.setHeader("newToken",tokenUtil.getToken(Integer.valueOf(uid)));
            return true;
        }//token过期就返回 token 无效.
        else {
            printJson(response,"长时间未登录！");
            return false;
        }
    }

    private static void printJson(HttpServletResponse response,String msg) {
        Result responseResult = new Result(Code.NotAllow.getCode(),"请重新登录,原因："+msg,null);
        String content = JSON.toJSONString(responseResult);
        printContent(response, content);
    }
    private static void printContent(HttpServletResponse response, String content) {
        try {
            response.reset();
            response.setContentType("application/json");
//            response.setHeader("Cache-Control", "no-store");
            response.setCharacterEncoding("UTF-8");
            PrintWriter pw = response.getWriter();
            pw.write(content);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
