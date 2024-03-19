package com.rt.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rt.entity.msg.MsgListVo;
import com.rt.entity.user.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {

    List<MsgListVo> selectMsgListInFriend(@Param("uid") Integer uid);

    Friend searchByUidAndFid(@Param("uid") Integer uid, @Param("fid") Integer fid);

    Long getFriendCount(@Param("uid") Integer uid);

}
