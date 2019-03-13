/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain.config;


import com.thoughtworks.go.helper.UserRoleMatcherMother;
import java.util.Arrays;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AdminsConfigTest {
    @Test
    public void shouldReturnTrueIfHasUser() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminUser(new CaseInsensitiveString("user1")));
        Assert.assertThat("shouldReturnTrueIfHasUser", adminsConfig.hasUser(new CaseInsensitiveString("user1"), UserRoleMatcherMother.ALWAYS_FALSE_MATCHER), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfUserMatchRole() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminUser(new CaseInsensitiveString("user1")), new AdminRole(new CaseInsensitiveString("role")));
        Assert.assertThat("shouldReturnTrueIfUserMatchRole", adminsConfig.hasUser(new CaseInsensitiveString("roleuser"), UserRoleMatcherMother.ALWAYS_TRUE_MATCHER), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotExist() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminUser(new CaseInsensitiveString("user1")));
        Assert.assertThat("shouldReturnFalseIfUserDoesNotExist", adminsConfig.hasUser(new CaseInsensitiveString("anyone"), UserRoleMatcherMother.ALWAYS_FALSE_MATCHER), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfAUserBelongsToAnAdminRole() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminRole(new CaseInsensitiveString("Role1")));
        Assert.assertThat(adminsConfig.isAdmin(new AdminUser(new CaseInsensitiveString("user1")), Arrays.asList(new RoleConfig(new CaseInsensitiveString("first")), new RoleConfig(new CaseInsensitiveString("role1")))), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfAUserIsAnAdmin() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminUser(new CaseInsensitiveString("USER1")));
        Assert.assertThat(adminsConfig.isAdmin(new AdminUser(new CaseInsensitiveString("user1")), Arrays.asList(new RoleConfig(new CaseInsensitiveString("first")), new RoleConfig(new CaseInsensitiveString("role1")))), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfAUserBelongsToAnAdminRoleNoRolesGiven() {
        CaseInsensitiveString username = new CaseInsensitiveString("USER1");
        AdminsConfig adminsConfig = new AdminsConfig(new AdminRole(username));
        // this is how isAdmin() is used in TemplatesConfig
        Assert.assertThat(adminsConfig.isAdmin(new AdminUser(username), null), Matchers.is(false));
    }

    @Test
    public void shouldUnderstandIfAUserIsAnAdminThroughRole() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminUser(new CaseInsensitiveString("loser")), new AdminRole(new CaseInsensitiveString("Role1")));
        Assert.assertThat(adminsConfig.isAdminRole(Arrays.asList(new RoleConfig(new CaseInsensitiveString("first")), new RoleConfig(new CaseInsensitiveString("role1")))), Matchers.is(true));
        Assert.assertThat(adminsConfig.isAdminRole(Arrays.asList(new RoleConfig(new CaseInsensitiveString("role2")))), Matchers.is(false));
        Assert.assertThat(adminsConfig.isAdminRole(Arrays.asList(new RoleConfig(new CaseInsensitiveString("loser")))), Matchers.is(false));
    }

    @Test
    public void shouldValidatePresenceOfUserName() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminUser(""));
        ValidationContext validationContext = Mockito.mock(com.thoughtworks.go.config.ValidationContext.class);
        Assert.assertFalse(adminsConfig.validateTree(validationContext));
        TestCase.assertTrue(adminsConfig.hasErrors());
        Assert.assertThat(adminsConfig.errors().on("users"), Matchers.is("User cannot be blank."));
    }

    @Test
    public void shouldValidateIfUserNameIsBlank() {
        AdminsConfig adminsConfig = new AdminsConfig(new AdminUser(new CaseInsensitiveString("")));
        ValidationContext validationContext = Mockito.mock(com.thoughtworks.go.config.ValidationContext.class);
        Assert.assertFalse(adminsConfig.validateTree(validationContext));
        TestCase.assertTrue(adminsConfig.hasErrors());
        Assert.assertThat(adminsConfig.errors().on("users"), Matchers.is("User cannot be blank."));
    }

    @Test
    public void shouldValidateIfRoleExists() {
        CaseInsensitiveString roleName = new CaseInsensitiveString("admin_role");
        AdminsConfig adminsConfig = new AdminsConfig(new AdminRole(roleName));
        ValidationContext validationContext = Mockito.mock(com.thoughtworks.go.config.ValidationContext.class);
        SecurityConfig securityConfig = Mockito.mock(com.thoughtworks.go.config.SecurityConfig.class);
        Mockito.when(validationContext.shouldNotCheckRole()).thenReturn(false);
        Mockito.when(validationContext.getServerSecurityConfig()).thenReturn(securityConfig);
        Mockito.when(securityConfig.isRoleExist(roleName)).thenReturn(false);
        Assert.assertFalse(adminsConfig.validateTree(validationContext));
        TestCase.assertTrue(adminsConfig.hasErrors());
        Assert.assertThat(adminsConfig.errors().on("roles"), Matchers.is("Role \"admin_role\" does not exist."));
    }
}

