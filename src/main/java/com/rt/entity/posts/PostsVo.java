package com.rt.entity.posts;

import lombok.Data;


@Data
public class PostsVo {
    // post
    private Integer id;
    private Integer uid;
    private String postTime;
    private String post;
    private String postImg;
    private Integer likes;
    private Integer comments;
    private Boolean isTop;
    // user
    private String userName;
    private String userImg;
    // groupNumber
    private String userLabel;
    // postLikeUser
    private Boolean liked;
}
