package com.rt.utils;

import cn.hutool.core.util.StrUtil;
import com.rt.bean.MailService;
import com.rt.common.Constants;
import com.rt.component.SpringContextUtils;
import com.rt.component.value.SysValue;
import com.rt.utils.muban.ThymeleafTemplateUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class EmailUtil {

    private final MailService mailService;

    // 构造方法注入
    public EmailUtil(MailService mailService) {
        this.mailService = mailService;
    }


    public Boolean SendRegister(String to,String code, String type) {

        Context context = new Context();
        context.setVariable("username", StrUtil.subBefore(to, "@", false));
        context.setVariable("code1",code);
        context.setVariable("type1", type);
        SysValue sys = SpringContextUtils.getBean(SysValue.class);
        String htmlContent = ThymeleafTemplateUtil.parseTemplate("mailcode",context);
        this.mailService.sendHtmlEmail(to,sys.getAppName() + type + "验证",htmlContent);
        return true;
    }

    public void Send(String to, String subject, String htmlContent) {
        this.mailService.sendHtmlEmail(to, subject, htmlContent);
    }
}
