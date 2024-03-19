package com.rt.controller.search;

import com.rt.common.Result;
import com.rt.component.UserContext;
import com.rt.service.search.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    SearchService service;

    @GetMapping
    public Result search(@RequestParam(defaultValue = "") String keyword) {
        Integer uid = UserContext.getUserId();
        if(uid == null){
            return new Result().error("无用户信息");
        }
        if("".equals(keyword)){
            return new Result().error("查询条件不能为空");
        }
        return new Result().success(service.getByKeyWord(uid,keyword));
    }

}
