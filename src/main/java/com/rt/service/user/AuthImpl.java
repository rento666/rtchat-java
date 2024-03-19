package com.rt.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rt.common.Constants;
import com.rt.common.Result;
import com.rt.component.JwtConfig;
import com.rt.entity.msg.Msg;
import com.rt.entity.user.Auth;
import com.rt.entity.user.Friend;
import com.rt.entity.user.User;
import com.rt.entity.user.UserMsgVo;
import com.rt.mapper.user.AuthMapper;
import com.rt.mapper.user.FriendMapper;
import com.rt.mapper.user.UserMapper;
import com.rt.mq.EmailProducer;
import com.rt.mq.MsgProducer;
import com.rt.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthImpl extends ServiceImpl<AuthMapper, Auth> implements AuthService{
    @Resource
    RedisUtil redisUtil;
    @Resource
    AuthMapper authMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    JwtConfig jwtConfig;
    @Resource
    FriendMapper friendMapper;
    @Resource
    EmailProducer emailProducer;
    @Resource
    MsgProducer msgProducer;
    @Resource
    UserService userService;

    @Override
    public Result sendCode(String email,String type,String t) {
        if(email == null || email.isEmpty() || type == null || type.isEmpty()){
            return new Result().errorInfoLack();
        }
        // 这里只有email发送验证码
        if(type.equals(Constants.identityTypeEmail)){
            // redis中还存在验证码没有到期，不会再发一次的！
            String key = Constants.redisSendEmailPrefix + t + email;
            if(redisUtil.hasKey(key)){
                return new Result().error("验证码未过期");
            }
            String code = Random.genRandomNumber(4);
            // 发送新的验证码、发送成功后再去设置redis(通过rabbitMQ)
            emailProducer.sendEmailMsg(email,code, t);
        }
        return new Result().suc("发送成功");
    }

    @Override
    public Result login(Auth auth, HttpServletRequest request) {
        if(auth.getIdentifier() == null || auth.getIdentifier().isEmpty()
                || auth.getCredential() == null || auth.getCredential().isEmpty()){
            return new Result().errorInfoLack();
        }
        auth.setCredential(MD5Util.encrypt(auth.getCredential()));
        auth = setType(auth);
        Integer uid = authMapper.selectByAccountPassword(auth);
        if(uid == null || uid == 0){
            return new Result().error("账号或密码错误");
        }

        User user = new User();
        user.setId(uid);
        String ip = IpUtil.getClientIP(request);
        user.setIp(ip);
        String addr = IpUtil.getIpAddrCity(ip);
        user.setAddr(addr);
        userMapper.updateById(user);

        user = userMapper.selectById(uid);

        String token = jwtConfig.getToken(uid);

        Map<String,Object> res = new HashMap<>();
        res.put("user",user);
        res.put("token",token);
        return new Result().success("登录成功",res);
    }

    @Override
    public Result register(Auth auth, HttpServletRequest request) {

        if(auth.getIdentifier() == null || auth.getIdentifier().isEmpty()
                || auth.getCredential() == null || auth.getCredential().isEmpty()
                || auth.getCode() == null || auth.getCode().isEmpty()){
            return new Result().errorInfoLack();
        }
        // 密码md5加密
        auth.setCredential(MD5Util.encrypt(auth.getCredential()));
        auth = setType(auth);
        String t = auth.getType();
        if(!t.equals(Constants.identityTypeEmail) && !t.equals(Constants.identityTypePhone)){
            // 注册时，只能是邮箱或者手机号，不能是其他的
            return new Result().error("账号格式不对");
        }
        Integer uid = authMapper.selectByAccount(auth);
        if(uid != null){
            return new Result().error("账号已存在");
        }
        // 如果用户输入的验证码和redis内的不一致，则返回错误
        Object o = redisUtil.get(Constants.redisSendEmailPrefix + "注册" + auth.getIdentifier());
        if(o == null || !o.equals(auth.getCode())){
            return new Result().error("验证码错误");
        }

        // 验证码正确，从redis删除
        redisUtil.delete(Constants.redisSendEmailPrefix + "注册" +  auth.getIdentifier());

        // 这里用到了事务！！！

        // user表新增
        String ip = IpUtil.getClientIP(request);
        User user = userInsert(auth,ip);
        Integer userid = user.getId();
        // auth表新增两种登录方式： 邮箱/手机号、id号
        auth.setUid(userid);
        authMapper.insert(auth);
        auth.setId(null);
        auth.setType(Constants.identityTypeNumber);
        auth.setIdentifier(user.getNumber());
        authMapper.insert(auth);
        // 朋友表新增AI朋友
        Friend friend = new Friend().addAiFriend(userid, 1);
        friendMapper.insert(friend);
        Friend friend2 = new Friend().addAiFriend(userid, 12);
        friendMapper.insert(friend2);

        // 其他表。。。
        // 加了机器人好友，机器人要发送消息的！
        UserMsgVo ai1 = userService.getUserMsgVo(1);
        UserMsgVo ai2 = userService.getUserMsgVo(12);
        String sysText = "你们已经是好友了，快来聊天吧！";
        Msg msg1 = new Msg().newSystem(ai1,sysText,String.valueOf(userid),false);
        msgProducer.saveMsgToMongo(msg1);
        Msg msg2 = new Msg().newSystem(ai2,sysText,String.valueOf(userid),false);
        msgProducer.saveMsgToMongo(msg2);
        Msg msg3 = new Msg().newAiHello(ai1, String.valueOf(userid), false);
        msgProducer.saveMsgToMongo(msg3);
        Msg msg4 = new Msg().newAiHello(ai2, String.valueOf(userid), false);
        msgProducer.saveMsgToMongo(msg4);


        user = userMapper.selectById(userid);
        String addr = IpUtil.getIpAddrCity(ip);
        user.setAddr(addr);
        String token = jwtConfig.getToken(userid);

        Map<String,Object> res = new HashMap<>();
        res.put("user",user);
        res.put("token",token);
        return new Result().success("注册成功",res);
    }

    @Override
    public Boolean changePwd1(Auth auth) {

        auth = setType(auth);

        Integer uid = authMapper.selectByAccount(auth);
        if(uid == null){
            // 账号不存在
            System.out.println("账号不存在");
            return false;
        }

        Object o = redisUtil.get(Constants.redisSendEmailPrefix + "修改密码" + auth.getIdentifier());
        if(o == null || !o.equals(auth.getCode())){
            // 验证码不对
            System.out.println("验证码不对");
            return false;
        }
        // 验证码正确，从redis删除
        redisUtil.delete(Constants.redisSendEmailPrefix + "修改密码" + auth.getIdentifier());
        String pwd = MD5Util.encrypt(auth.getCredential());
        authMapper.updateManyByUid(uid, pwd);

        return true;
    }

    @Override
    public Boolean changePwd2(Auth auth) {
        return null;
    }

    public User userInsert(Auth auth, String ip){
        User user;
        if(auth.getType().equals(Constants.identityTypeEmail)){
            user = new User().initUser(null, auth.getIdentifier(),ip);
        }else {
            user = new User().initUser(auth.getIdentifier(), null,ip);
        }
        userMapper.insert(user);
        return user;
    }

    public Auth setType(Auth auth) {

        if(Regular.isValidEmail(auth.getIdentifier())){
            auth.setType(Constants.identityTypeEmail);
        }else if (Regular.isValidPhone(auth.getIdentifier())){
            auth.setType(Constants.identityTypePhone);
        }else {
            auth.setType(Constants.identityTypeNumber);
        }
        return auth;
    }

}
