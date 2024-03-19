package com.rt.entity.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QykVo {
    // 这个是AIController的一个接口，青云客？
    // 为0时，获取到消息了，为1时，没有获取到（目前来看，是这样的）
    private Integer result;
    private String content;
}
