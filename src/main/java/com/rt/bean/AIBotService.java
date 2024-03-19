package com.rt.bean;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rt.entity.ai.QykVo;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIBotService {

    public QykVo askQingYunKeAI(String question) throws UnsupportedEncodingException {

        String url = "http://api.qingyunke.com/api.php";
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", "free");
        map.put("appid", "0");
        map.put("msg", URLEncoder.encode(question,"UTF-8"));
        String s = HttpUtil.get(url, map);
        JSONObject jo = JSON.parseObject(s);
        return JSON.toJavaObject(jo, QykVo.class);
    }

    public String askSiZhiAI(String question, String userid){
        String url = "https://api.ownthink.com/bot";
        HashMap<String, Object> map = new HashMap<>();
        map.put("appid", "681a2ebf8ed2b9a5e9d3f28120b29d7c");
        map.put("userid", userid);
        map.put("spoken",question);
        String s = HttpUtil.get(url, map);
        JSONObject jo = JSON.parseObject(s);
        JSONObject data = jo.getJSONObject("data");
        JSONObject info = data.getJSONObject("info");

        String message = jo.getString("message");
        Integer type = data.getIntValue("type");
        if(!message.equals("success") || !type.equals(5000)){
            return "不好啦，我被偷家啦！暂时不能回答你的问题了呀！";
        }
        return info.getString("text");
    }

}
