package com.querydsl.apt.domain;


import QGeneric12Test_ChannelRole.channelRole.permissions;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class Generic12Test {
    // some common stuff
    @Entity
    @Inheritance
    @DiscriminatorColumn(name = "CONTEXT")
    public abstract static class Permission {}

    // CP specific stuff
    @Entity
    @DiscriminatorValue("CHANNEL")
    public static class ChannelPermission extends Generic12Test.Permission {}

    // SP specific stuff
    @Entity
    @DiscriminatorValue("SUBJECT")
    public static class SubjectPermission extends Generic12Test.Permission {}

    // A bunch of role classes
    @Entity
    @Inheritance
    @DiscriminatorColumn(name = "CONTEXT")
    public abstract static class Role<T extends Generic12Test.Permission> {
        @ManyToMany(targetEntity = Generic12Test.Permission.class)
        private final List<T> permissions = Lists.newArrayList();
    }

    // some constructors
    @Entity
    @DiscriminatorValue("CHANNEL")
    public static class ChannelRole extends Generic12Test.Role<Generic12Test.ChannelPermission> {}

    // missing type param, should be Role<SubjectPermission>
    // some constructors
    @Entity
    @DiscriminatorValue("SUBJECT")
    public static class SubjectRole extends Generic12Test.Role {}

    @Test
    public void test() {
        Assert.assertEquals(QGeneric12Test_Permission.class, permissions.get(0).getClass());
        Assert.assertEquals(QGeneric12Test_Permission.class, QGeneric12Test_SubjectRole.subjectRole.permissions.get(0).getClass());
    }
}

