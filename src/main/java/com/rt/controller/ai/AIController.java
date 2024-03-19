package com.rt.controller.ai;

import com.rt.bean.AIBotService;
import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.entity.ai.QykVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource
    AIBotService aiBotService;

    @GetMapping("/qyk")
    public Result qyk(@RequestParam(defaultValue = "") String question) {

        if("".equals(question)){
            return new Result().error("请求参数错误");
        }
        try {
            QykVo qykVo = aiBotService.askQingYunKeAI(question);
            String content = qykVo.getContent();
            return new Result().success(content);
        }catch (UnsupportedEncodingException e){
            return new Result().error("AI出错啦", "不好啦，我被偷家啦！暂时不能回答你的问题了呀！");
        }
    }

    @GetMapping("/sz")
    public Result sz(@RequestParam(defaultValue = "") String question) {
        if("".equals(question)){
            return new Result().error("请求参数错误");
        }
        Integer userId = UserContext.getUserId();
        String content = aiBotService.askSiZhiAI(question, String.valueOf(userId));
        return new Result().success(content);
    }

}
