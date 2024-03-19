package com.rt.service.msg;

import com.anwen.mongo.conditions.query.LambdaQueryChainWrapper;
import com.anwen.mongo.conditions.update.LambdaUpdateChainWrapper;
import com.anwen.mongo.conditions.update.UpdateChainWrapper;
import com.anwen.mongo.service.impl.ServiceImpl;
import com.rt.entity.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsgImpl extends ServiceImpl<Msg> implements MsgService {

    @Override
    public Boolean readAllMsgOnlyOneFriend(Integer uid, Integer fid) {
        LambdaUpdateChainWrapper<Msg> ucw = this.lambdaUpdate()
                .set("received", true)
                .eq("user._id", String.valueOf(fid))
                .eq(Msg::getReceiveId, String.valueOf(uid));

        return this.update(ucw);
    }


    @Override
    public Long getNoReadFriend(Integer uid) {
        // 获取当前用户所有未读的私聊消息 receive_id = uid isGroup =false received = false
        LambdaQueryChainWrapper<Msg> lqcw = this.lambdaQuery()
                .eq(Msg::getReceived, false)
                .eq(Msg::getIsGroup, false)
                .eq(Msg::getReceiveId, String.valueOf(uid));

        return this.count(lqcw);
    }

}
