package com.rt.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Integer code;
    private String msg;
    private Object data;

    public Result suc(String msg) {
        return new Result().success(msg,null);
    }
    public Result success(Object data) {
        return new Result().success("获取成功",data);
    }
    public Result success(String msg, Object data) {
        Result res = new Result();
        res.setCode(Code.Success.getCode());
        res.setMsg(msg);
        res.setData(data);
        return res;
    }
    public Result errorInfoLack() {
        return new Result().error("信息不全",null);
    }
    public Result error(String msg) {
        return new Result().error(msg,null);
    }
    public Result error(String msg, Object data) {
        Result res = new Result();
        res.setCode(Code.Error.getCode());
        res.setMsg(msg);
        res.setData(data);
        return res;
    }

    public Result system(String msg) {
        return new Result().system(msg, null);
    }

    public Result system(String msg, Object data) {
        Result res = new Result();
        res.setCode(Code.System.getCode());
        res.setMsg(msg);
        res.setData(data);
        return res;
    }


    public Result user(Object data) {
        Result res = new Result();
        res.setCode(Code.User.getCode());
        res.setMsg("消息发送成功");
        res.setData(data);
        return res;
    }

}
