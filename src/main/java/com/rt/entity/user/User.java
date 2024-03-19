package com.rt.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rt.entity.user.UserVo;
import com.rt.utils.IpUtil;
import com.rt.utils.Random;
import com.rt.utils.TimeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author TwoZiBro
 * @since 2023-12-03
 */
@Getter
@Setter
@TableName("user")
@ToString
@ApiModel(value = "User对象", description = "用户")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends UserVo implements Serializable  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("蝶语号")
    private String number;

    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("昵称")
    private String username;

    @ApiModelProperty("简介")
    private String about;

    @ApiModelProperty("是否删除")
    @JsonIgnore
    private Boolean isDelete;

    private String ip;
    private String addr;
    // 创建日期 年月日
    private String createdAt;

    @TableField(exist = false)
    private Boolean isFriend;

    public User initUser(String phone,String email,String ip){
        User user = new User();
        user.setNumber("dyid_" + Random.genRandomStr(10));
        user.setPhone(phone);
        user.setEmail(email);
        user.setAvatar("default.png");
        user.setUsername(Random.genRandomUsername());
        user.setCreatedAt(TimeUtil.getYYYYMMDD());
        user.setAbout("我的简介，独一无二");
        user.setIsDelete(false);
        user.setIp(ip);
        String addr = IpUtil.getIpAddrCity(ip);
        user.setAddr(addr);
        return user;
    }

}
