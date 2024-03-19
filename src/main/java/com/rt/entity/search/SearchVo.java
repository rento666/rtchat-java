package com.rt.entity.search;

import com.rt.entity.group.Group;
import com.rt.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchVo {
    private List<User> users;
    private List<Group> groups;
}
