package com.rt.mapper.user;

import com.rt.entity.user.Auth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author TwoZiBro
 * @since 2023-12-24
 */
@Mapper
public interface AuthMapper extends BaseMapper<Auth> {
    Integer selectByAccountPassword(Auth auth);

    Integer selectByAccount(Auth auth);

    void updateManyByUid(@Param("uid") Integer uid, @Param("newCredential") String newCredential);
}
