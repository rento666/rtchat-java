package com.rt.component;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_EMAIL = "emailQueue";
    public static final String QUEUE_MSG = "msgQueue";

    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE_EMAIL);
    }

    @Bean
    public Queue msgQueue() {
        return new Queue(QUEUE_MSG);
    }

}
