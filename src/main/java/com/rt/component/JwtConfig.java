package com.rt.component;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtConfig {

    private String secretKey = "RtChatIsDieYu";

    public String getToken(Integer userId) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                // 7天刷新时间
                long refreshTime = 1000 * 60 * 60 * 24 * 7;
                put("uid", userId);
                put("timeStamp", System.currentTimeMillis());
                // 7天过期
                put("expire_time", System.currentTimeMillis() + refreshTime);
            }
        };

        return JWTUtil.createToken(map, secretKey.getBytes());
    }

    public Map<String,String> parseToken(String token) {

        final JWT jwt = JWTUtil.parseToken(token);

        jwt.setKey(secretKey.getBytes());
        jwt.getHeader(JWTHeader.TYPE);
        Integer uid = (Integer) jwt.getPayload("uid");
        Long timeStamp = (Long) jwt.getPayload("timeStamp");
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", String.valueOf(uid));
        map.put("timeStamp", String.valueOf(timeStamp));
        return map;
    }

    public boolean Verify(String token){
        return JWTUtil.verify(token, secretKey.getBytes());
    }
}
