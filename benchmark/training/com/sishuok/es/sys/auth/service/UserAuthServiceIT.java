/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.sys.auth.service;


import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.service.UserService;
import com.sishuok.es.test.BaseIT;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-2 ??3:54
 * <p>Version: 1.0
 */
public class UserAuthServiceIT extends BaseIT {
    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserService userService;

    // /////////////////////////????
    @Test
    public void testUserAuth() {
        executeSqlScript("sql/intergration-test-user-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertTrue(permissions.contains("example:upload:all"));
        Assert.assertTrue(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    @Test
    public void testDefaultUserGroupAuth() {
        executeSqlScript("sql/intergration-test-defualt-user-group-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertFalse(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertFalse(permissions.contains("example:upload:all"));
        Assert.assertFalse(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    @Test
    public void testUserGroupWithUniqueUserAuth() {
        executeSqlScript("sql/intergration-test-user-group-unique-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertTrue(permissions.contains("example:upload:all"));
        Assert.assertTrue(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    @Test
    public void testUserGroupWithRangeUserAuth() {
        executeSqlScript("sql/intergration-test-user-group-range-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertTrue(permissions.contains("example:upload:all"));
        Assert.assertTrue(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    // ///////////////////////////??????
    @Test
    public void testOrganizationAuth() {
        executeSqlScript("sql/intergration-test-organization-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertFalse(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertFalse(permissions.contains("example:upload:all"));
        Assert.assertFalse(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    @Test
    public void testOrganizationWithInheritAuth() {
        executeSqlScript("sql/intergration-test-organization-with-inherit-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertTrue(permissions.contains("example:upload:all"));
        Assert.assertTrue(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    @Test
    public void testOrganizationGroupAuth() {
        executeSqlScript("sql/intergration-test-organization-group-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertTrue(permissions.contains("example:upload:all"));
        Assert.assertTrue(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    // ///////////////////////////??????
    @Test
    public void testJobAuth() {
        executeSqlScript("sql/intergration-test-job-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertFalse(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertFalse(permissions.contains("example:upload:all"));
        Assert.assertFalse(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    @Test
    public void testJobWithInheritAuth() {
        executeSqlScript("sql/intergration-test-job-with-inherit-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertTrue(permissions.contains("example:upload:all"));
        Assert.assertTrue(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    // ///////////////////////////???????????
    @Test
    public void testOrganizationAndJobAuth() {
        executeSqlScript("sql/intergration-test-organization-and-job-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertFalse(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertFalse(permissions.contains("example:upload:all"));
        Assert.assertFalse(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }

    // /////////////////////////all
    @Test
    public void testAllAuth() {
        executeSqlScript("sql/intergration-test-all-data.sql", false);
        User user = userService.findOne(1L);
        Set<String> roles = userAuthService.findStringRoles(user);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("test"));
        Assert.assertFalse(roles.contains("none"));
        Set<String> permissions = userAuthService.findStringPermissions(user);
        Assert.assertTrue(permissions.contains("example:example:all"));
        Assert.assertTrue(permissions.contains("example:example:save"));
        Assert.assertTrue(permissions.contains("example:upload:all"));
        Assert.assertTrue(permissions.contains("example:upload:update"));
        Assert.assertFalse(permissions.contains("example:deleted:all"));
        Assert.assertFalse(permissions.contains("example:example:none"));
    }
}

