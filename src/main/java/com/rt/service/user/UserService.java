package com.rt.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rt.common.Result;
import com.rt.entity.user.User;
import com.rt.entity.user.UserMsgVo;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> selectBatchByIds(List<Integer> ids);

    Result getUserInfo(Integer uid);

    UserMsgVo getUserMsgVo(Integer uid);

    Boolean updateUser(User user);
}
