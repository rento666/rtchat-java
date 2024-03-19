package com.rt.mapper.user;

import com.rt.entity.user.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户 Mapper 接口
 * </p>
 *
 * @author TwoZiBro
 * @since 2023-12-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
