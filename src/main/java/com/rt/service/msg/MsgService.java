package com.rt.service.msg;

import com.anwen.mongo.service.IService;
import com.rt.entity.msg.Msg;

import java.util.List;

public interface MsgService extends IService<Msg>{
    Boolean readAllMsgOnlyOneFriend(Integer uid, Integer fid);

    Long getNoReadFriend(Integer uid);

}
