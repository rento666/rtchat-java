package com.rt.service.search;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rt.component.UserContext;
import com.rt.entity.group.Group;
import com.rt.entity.group.GroupMember;
import com.rt.entity.search.SearchVo;
import com.rt.entity.user.User;
import com.rt.service.group.GroupMemberService;
import com.rt.service.group.GroupService;
import com.rt.service.user.FriendService;
import com.rt.service.user.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SearchService {

    @Resource
    UserService userService;
    @Resource
    FriendService friendService;
    @Resource
    GroupService groupService;
    @Resource
    GroupMemberService gmService;

    public SearchVo getByKeyWord(Integer uid, String keyword) {


        QueryWrapper<User> wp1 = new QueryWrapper<>();
        wp1.like("phone", keyword)
                .or().like("email", keyword)
                .or().like("username", keyword);
        List<User> ul = userService.list(wp1);

        ul.forEach(user -> {
            Integer fid = user.getId();
            Boolean isFriend = friendService.isFriend(uid, fid);
            user.setIsFriend(isFriend);
        });

        QueryWrapper<Group> wp2 = new QueryWrapper<>();
        wp2.like("name",keyword);
        List<Group> gl = groupService.list(wp2);
        gl.forEach(group -> {
            Integer gid = group.getId();
            GroupMember gm = gmService.lambdaQuery()
                    .eq(GroupMember::getGroupId, String.valueOf(gid))
                    .eq(GroupMember::getUserId, String.valueOf(uid))
                    .one();
            group.setIsInGroup(gm != null);
        });
        SearchVo sv = new SearchVo();
        sv.setUsers(ul);
        sv.setGroups(gl);
        return sv;
    }

}
