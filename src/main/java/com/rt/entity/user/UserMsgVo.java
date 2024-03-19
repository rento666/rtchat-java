package com.rt.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMsgVo implements Serializable {

    private String _id;
    private String name;
    private String avatar;
    private String time;
}
