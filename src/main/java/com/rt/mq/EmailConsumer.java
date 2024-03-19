package com.rt.mq;

import com.rt.bean.MailService;
import com.rt.common.Constants;
import com.rt.component.RabbitConfig;
import com.rt.entity.user.Auth;
import com.rt.utils.EmailUtil;
import com.rt.utils.RedisUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

// 消费者
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_EMAIL)
public class EmailConsumer {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    MailService mailService;

    @RabbitHandler
    public void processEmailMsg(String str) {
        String[] split = str.split("\\|");
        String email = split[0];
        String code = split[1];
        String type = split[2];
        if(code != null && !code.isEmpty()){
            // 处理发送邮件和设置 Redis 的逻辑
            EmailUtil emailUtil = new EmailUtil(mailService);
            Boolean b = emailUtil.SendRegister(email, code, type);
            if (b) {
                String key = Constants.redisSendEmailPrefix + type + email;
//                System.out.println("key: " + key + " code: " + code);
                redisUtil.set(key, code, 1, TimeUnit.MINUTES);
            }
        }else{
            System.out.println("code为空！！");
        }
    }

}
