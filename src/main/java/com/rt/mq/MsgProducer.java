package com.rt.mq;

import com.rt.component.RabbitConfig;
import com.rt.entity.msg.Msg;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MsgProducer {
    @Resource
    private AmqpTemplate rabbitTemplate;
    public void saveMsgToMongo(Msg msg) {
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_MSG, msg);
    }
}
