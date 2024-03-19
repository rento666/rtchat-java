package com.rt.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AuthVo {
    @TableField(exist = false)
    private String code;
//    @TableField(exist = false)
//    private String ip;
//    @TableField(exist = false)
//    private String addr;
}
