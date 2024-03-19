package com.rt.im.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rt.entity.msg.Msg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Param {

    private Integer code;

    private String token;

    private Msg msg;

}
