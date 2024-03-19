package com.rt.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserVo  {
    @TableField(exist = false)
    private String accompanyDay;
    @TableField(exist = false)
    private String friendsCount;
    @TableField(exist = false)
    private String postsCount;

}
