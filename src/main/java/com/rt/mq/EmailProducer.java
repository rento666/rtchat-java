package com.rt.mq;

import com.rt.component.RabbitConfig;
import com.rt.entity.user.Auth;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// 生产者
@Component
public class EmailProducer {

    @Resource
    private AmqpTemplate rabbitTemplate;

    public void sendEmailMsg(String email, String code, String type) {
        String str = email + "|" + code + "|" + type;
        // 发送给消费者
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_EMAIL, str);
    }

}
