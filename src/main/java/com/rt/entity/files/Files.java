package com.rt.entity.files;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 文件
 * </p>
 *
 * @author TwoZiBro
 * @since 2023-12-03
 */
@Getter
@Setter
public class Files implements Serializable {

    private static final long serialVersionUID = 1L;
  @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      private String name;

      private String fType;

      private Long size;

      private String url;

      private Boolean isDelete;

      private Boolean enable;

      private String md5;


}
