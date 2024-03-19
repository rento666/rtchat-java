package com.rt.mq;

import com.rt.component.RabbitConfig;
import com.rt.entity.msg.Msg;
import com.rt.service.msg.MsgService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RabbitListener(queues = RabbitConfig.QUEUE_MSG)
public class MsgConsumer {

    @Resource
    MsgService msgService;

    @RabbitHandler
    public void receiveMsg(Msg msg){
        msgService.save(msg);
    }

}
