package com.rt.controller;

import com.rt.common.Constants;
import com.rt.common.Result;
import com.rt.component.SpringContextUtils;
import com.rt.component.value.SysValue;
import com.rt.entity.sys.IpVo;
import com.rt.utils.IpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("")
public class SysController {

    @RequestMapping()
    public Result defaultMain() {

        SysValue sysV = SpringContextUtils.getBean(SysValue.class);

        String content = "这里是" + sysV.getAppName() +
                "后端首页, 目前版本 V" + sysV.getVersion() + ", " +
                "版权年份: ©️" + sysV.getCopyRightYear();

        return new Result().success(content);
    }

    @RequestMapping("/ip")
    public Result getIpAddr(HttpServletRequest re){
        String ip = IpUtil.getClientIP(re);
        String ipAddr = IpUtil.getIpAddrProv(ip);
        IpVo ipVo = new IpVo();
        ipVo.setIp(ip);
        ipVo.setIpAddress(ipAddr);
        return new Result().success(ipVo);
    }

}
