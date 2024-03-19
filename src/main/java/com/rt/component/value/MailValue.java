package com.rt.component.value;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MailValue {

    @Value("${rtchat.email.host}")
    public String host;
    @Value("${rtchat.email.port}")
    public String port;
    @Value("${rtchat.email.username}")
    public String username;
    @Value("${rtchat.email.password}")
    public String password;
}
