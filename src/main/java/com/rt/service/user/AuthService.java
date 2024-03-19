package com.rt.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rt.common.Result;
import com.rt.entity.user.Auth;

import javax.servlet.http.HttpServletRequest;

public interface AuthService extends IService<Auth> {

    Result sendCode(String email, String type, String t);

    Result login(Auth auth, HttpServletRequest request);

    Result register(Auth auth, HttpServletRequest request);

    Boolean changePwd1(Auth auth);

    Boolean changePwd2(Auth auth);

}
