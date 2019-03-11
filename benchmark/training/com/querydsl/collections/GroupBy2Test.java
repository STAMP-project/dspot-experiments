package com.querydsl.collections;


import com.google.common.collect.Lists;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.group.GroupBy;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static QGroupBy2Test_Role.role;
import static QGroupBy2Test_SecurityGroup.securityGroup;
import static QGroupBy2Test_User.user;


public class GroupBy2Test {
    // select u1.id,u1.name,r1.id,r1.name,s1.name from users u1
    // join roles r1 on u1.role = r1.id
    // join security_groups s1 on r1.secgroup = s1.id
    @QueryEntity
    public static class User {
        public Long id;

        public String name;

        public List<GroupBy2Test.Role> roles;
    }

    @QueryEntity
    public static class Role {
        public Long id;

        public String name;

        public List<GroupBy2Test.SecurityGroup> groups;
    }

    @QueryEntity
    public static class SecurityGroup {
        public Long id;

        public String name;

        public SecurityGroup(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class UserDto {
        public Long id;

        public String name;

        public List<Long> roleIds;

        public List<String> roleNames;

        public List<Long> secIds;

        @QueryProjection
        public UserDto(Long id, String name, List<Long> roleIds, List<String> roleNames, List<Long> secIds) {
            this.id = id;
            this.name = name;
            this.roleIds = roleIds;
            this.roleNames = roleNames;
            this.secIds = secIds;
        }

        @QueryProjection
        public UserDto(Long id, String name, Map<Long, String> roles, Map<Long, String> groups) {
            this.id = id;
            this.name = name;
            this.roleIds = Lists.newArrayList(roles.keySet());
            this.roleNames = Lists.newArrayList(roles.values());
            this.secIds = Lists.newArrayList(groups.keySet());
        }
    }

    private List<GroupBy2Test.User> users;

    @Test
    public void test() {
        QGroupBy2Test_User user = user;
        QGroupBy2Test_Role role = role;
        QGroupBy2Test_SecurityGroup group = securityGroup;
        Map<Long, GroupBy2Test.UserDto> userDtos = CollQueryFactory.from(user, users).innerJoin(user.roles, role).innerJoin(role.groups, group).transform(GroupBy.groupBy(user.id).as(new QGroupBy2Test_UserDto(user.id, user.name, list(role.id), list(role.name), list(group.id))));
        GroupBy2Test.UserDto dto1 = userDtos.get(3L);
        Assert.assertEquals(1, dto1.roleIds.size());
        Assert.assertEquals(1, dto1.roleNames.size());
        Assert.assertEquals(1, dto1.secIds.size());
        GroupBy2Test.UserDto dto2 = userDtos.get(32L);
        Assert.assertEquals(3, dto2.roleIds.size());
        Assert.assertEquals(1, dto2.roleNames.size());
        Assert.assertEquals(3, dto2.secIds.size());
    }

    @Test
    public void test2() {
        QGroupBy2Test_User user = user;
        QGroupBy2Test_Role role = role;
        QGroupBy2Test_SecurityGroup group = securityGroup;
        Map<Long, GroupBy2Test.UserDto> userDtos = CollQueryFactory.from(user, users).innerJoin(user.roles, role).innerJoin(role.groups, group).transform(GroupBy.groupBy(user.id).as(new QGroupBy2Test_UserDto(user.id, user.name, map(role.id, role.name), map(group.id, group.name))));
        GroupBy2Test.UserDto dto1 = userDtos.get(3L);
        Assert.assertEquals(1, dto1.roleIds.size());
        Assert.assertEquals(1, dto1.roleNames.size());
        Assert.assertEquals(1, dto1.secIds.size());
        GroupBy2Test.UserDto dto2 = userDtos.get(32L);
        Assert.assertEquals(2, dto2.roleIds.size());
        Assert.assertEquals(2, dto2.roleNames.size());
        Assert.assertEquals(3, dto2.secIds.size());
    }
}

