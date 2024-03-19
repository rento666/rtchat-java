package com.rt.component.value;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class SysValue {

    @Value("${rtchat.name}")
    public String appName;

    @Value("${rtchat.version}")
    public String version;

    @Value("${rtchat.copyrightYear}")
    public String copyRightYear;

    @Value("${rtchat.im.port}")
    public Integer imPort;
    // 使用方法
    // 这一步 获取对象 SysValue sysV = SpringContextUtils.getBean(SysValue.class);
    // 这一步 使用值  String appName = sysV.getAppName();

}
