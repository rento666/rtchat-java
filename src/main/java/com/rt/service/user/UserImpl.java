package com.rt.service.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rt.common.Constants;
import com.rt.common.Result;
import com.rt.entity.user.Auth;
import com.rt.entity.user.User;
import com.rt.entity.user.UserMsgVo;
import com.rt.im.UserChannelMap;
import com.rt.im.param.Param;
import com.rt.im.param.ParamType;
import com.rt.mapper.user.AuthMapper;
import com.rt.mapper.user.FriendMapper;
import com.rt.mapper.user.UserMapper;
import com.rt.utils.TimeUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class UserImpl extends ServiceImpl<UserMapper,User> implements UserService{

    @Resource
    UserMapper userMapper;
    @Resource
    FriendMapper friendMapper;
    @Resource
    AuthMapper authMapper;

    @Override
    public List<User> selectBatchByIds(List<Integer> ids) {
        return userMapper.selectBatchIds(ids);
    }

    @Override
    public Result getUserInfo(Integer uid) {
        if(uid == null){
            return new Result().error("未授权用户");
        }
        User user = userMapper.selectById(uid);
        if(user == null){
            return new Result().error("错误的用户");
        }
        user.setIsFriend(true);
        user.setAccompanyDay(TimeUtil.getFromToDay(user.getCreatedAt()));
        user.setFriendsCount(friendMapper.getFriendCount(user.getId()) + "位");
        user.setPostsCount("0条");
        return new Result().success(user);
    }

    @Override
    public UserMsgVo getUserMsgVo(Integer uid) {
        if(uid == null){
            return null;
        }
        User user = userMapper.selectById(uid);
        if(user == null){
            return null;
        }
        UserMsgVo vo = new UserMsgVo();
        vo.set_id(String.valueOf(uid));
        vo.setName(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setTime(user.getCreatedAt());
        return vo;
    }

    @Override
    public Boolean updateUser(User user) {
        // 更新用户信息：
        // 要求：更新昵称、简介直接更新即可！
        // 若更新手机号、邮箱号，那么需要到auth表查询，如果存在，那也要同步更新
        if(user.getUsername() != null || user.getAbout() != null || user.getAvatar() != null){
            int count = userMapper.updateById(user);
            if(count == 1) {
                UserChannelMap.print();
                Channel uc = UserChannelMap.get(String.valueOf(user.getId()));
                if(uc != null){
                    Param param = new Param();
                    param.setCode(ParamType.PROFILE.getCode());
                    TextWebSocketFrame ms = new TextWebSocketFrame(JSON.toJSONString(param));
                    uc.writeAndFlush(ms);
                }
                return true;
            }
        }else if(user.getPhone() != null){
            QueryWrapper<Auth> wp = new QueryWrapper<>();
            wp.eq("uid", user.getId())
                    .eq("type", Constants.identityTypePhone);
            Auth auth = authMapper.selectOne(wp);
            if(auth == null){
                QueryWrapper<Auth> wp2 = new QueryWrapper<>();
                wp2.eq("uid", user.getId())
                        .eq("type", Constants.identityTypeEmail);
                Auth auth1 = authMapper.selectOne(wp2);
                auth = new Auth();
                auth.setUid(user.getId());
                auth.setType(Constants.identityTypePhone);
                auth.setIdentifier(user.getPhone());
                auth.setCredential(auth1.getCredential());
                int i = authMapper.insert(auth);
                if(i == 1) {
                    UserChannelMap.print();
                    Channel uc = UserChannelMap.get(String.valueOf(user.getId()));
                    if(uc != null){
                        Param param = new Param();
                        param.setCode(ParamType.PROFILE.getCode());
                        TextWebSocketFrame ms = new TextWebSocketFrame(JSON.toJSONString(param));
                        uc.writeAndFlush(ms);
                    }
                    return true;
                }
            }else {
                auth.setIdentifier(user.getPhone());
                int i = authMapper.updateById(auth);
                if(i == 1) {
                    UserChannelMap.print();
                    Channel uc = UserChannelMap.get(String.valueOf(user.getId()));
                    if(uc != null){
                        Param param = new Param();
                        param.setCode(ParamType.PROFILE.getCode());
                        TextWebSocketFrame ms = new TextWebSocketFrame(JSON.toJSONString(param));
                        uc.writeAndFlush(ms);
                    }
                    return true;
                }
            }
            return false;
        }else if(user.getEmail() != null){
            // 因为是邮箱注册的，所以必有邮箱！
            Auth auth = new Auth();
            auth.setIdentifier(user.getEmail());
            QueryWrapper<Auth> qw = new QueryWrapper<>();
            qw.eq("uid", user.getId())
                    .eq("type", Constants.identityTypeEmail);
            int i = authMapper.update(auth, qw);
            if(i == 1) {
                UserChannelMap.print();
                Channel uc = UserChannelMap.get(String.valueOf(user.getId()));
                if(uc != null){
                    Param param = new Param();
                    param.setCode(ParamType.PROFILE.getCode());
                    TextWebSocketFrame ms = new TextWebSocketFrame(JSON.toJSONString(param));
                    uc.writeAndFlush(ms);
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
