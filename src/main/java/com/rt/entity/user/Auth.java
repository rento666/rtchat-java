package com.rt.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 *
 * </p>
 *
 * @author TwoZiBro
 * @since 2023-12-24
 */
@Getter
@Setter
@ToString
  @TableName("auth")
@ApiModel(value = "Auth对象", description = "")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Auth extends AuthVo implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("授权id")
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("用户id")
      private Integer uid;

      @ApiModelProperty("账号类型")
      private String type;

      @ApiModelProperty("账号")
      private String identifier;

      @ApiModelProperty("密码")
      private String credential;


}
