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
package com.thoughtworks.go.config;


import java.util.List;
import java.util.regex.Pattern;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SecurityConfigTest {
    public static final Role ROLE1 = new RoleConfig(new CaseInsensitiveString("role1"), new RoleUser(new CaseInsensitiveString("chris")), new RoleUser(new CaseInsensitiveString("jez")));

    public static final Role ROLE2 = new RoleConfig(new CaseInsensitiveString("role2"), new RoleUser(new CaseInsensitiveString("chris")));

    public static final Role[] DEFAULT_ROLES = new Role[]{ SecurityConfigTest.ROLE1, SecurityConfigTest.ROLE2 };

    @Test
    public void shouldNotSaySecurityEnabledIfSecurityHasNoAuthenticatorsDefined() {
        ServerConfig serverConfig = new ServerConfig();
        Assert.assertFalse("Security should not be enabled by default", serverConfig.isSecurityEnabled());
    }

    @Test
    public void twoEmptySecurityConfigsShouldBeTheSame() throws Exception {
        SecurityConfig one = new SecurityConfig();
        SecurityConfig two = new SecurityConfig();
        Assert.assertThat(one, Matchers.is(two));
    }

    @Test
    public void shouldSaySecurityEnabledIfPasswordFileSecurityEnabled() {
        ServerConfig serverConfig = server(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins());
        Assert.assertTrue("Security should be enabled when password file config present", serverConfig.isSecurityEnabled());
    }

    @Test
    public void shouldKnowIfUserIsAdmin() throws Exception {
        SecurityConfig security = SecurityConfigTest.security(null, SecurityConfigTest.admins(SecurityConfigTest.user("chris")));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("chris"))), Matchers.is(true));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("evilHacker"))), Matchers.is(true));
        security = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins(SecurityConfigTest.user("chris")));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("chris"))), Matchers.is(true));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("evilHacker"))), Matchers.is(false));
    }

    @Test
    public void shouldKnowIfRoleIsAdmin() throws Exception {
        SecurityConfig security = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins(SecurityConfigTest.role("role1")));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("chris"))), Matchers.is(true));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("jez"))), Matchers.is(true));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("evilHacker"))), Matchers.is(false));
    }

    @Test
    public void shouldNotCareIfValidUserInRoleOrUser() throws Exception {
        SecurityConfig security = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins(SecurityConfigTest.role("role2")));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("chris"))), Matchers.is(true));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("jez"))), Matchers.is(false));
        security = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins(SecurityConfigTest.role("role2"), SecurityConfigTest.user("jez")));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("chris"))), Matchers.is(true));
        Assert.assertThat(security.isAdmin(new AdminUser(new CaseInsensitiveString("jez"))), Matchers.is(true));
    }

    @Test
    public void shouldValidateRoleAsAdmin() throws Exception {
        SecurityConfig security = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins(SecurityConfigTest.role("role2")));
        Assert.assertThat(security.isAdmin(new AdminRole(new CaseInsensitiveString("role2"))), Matchers.is(true));
    }

    @Test
    public void shouldReturnTheMemberRoles() throws Exception {
        SecurityConfig securityConfig = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins());
        assertUserRoles(securityConfig, "chris", SecurityConfigTest.DEFAULT_ROLES);
        assertUserRoles(securityConfig, "jez", SecurityConfigTest.DEFAULT_ROLES[0]);
        assertUserRoles(securityConfig, "loser");
    }

    @Test
    public void shouldReturnTrueIfDeletingARoleGoesThroughSuccessfully() throws Exception {
        SecurityConfig securityConfig = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins());
        securityConfig.deleteRole(SecurityConfigTest.ROLE1);
        assertUserRoles(securityConfig, "chris", SecurityConfigTest.ROLE2);
        assertUserRoles(securityConfig, "jez");
    }

    @Test
    public void shouldBombIfDeletingARoleWhichDoesNotExist() throws Exception {
        try {
            SecurityConfig securityConfig = SecurityConfigTest.security(SecurityConfigTest.passwordFileAuthConfig(), SecurityConfigTest.admins());
            securityConfig.deleteRole(new RoleConfig(new CaseInsensitiveString("role99")));
            Assert.fail("Should have blown up with an exception on the previous line as deleting role99 should blow up");
        } catch (RuntimeException e) {
            Assert.assertTrue(Pattern.compile("does not exist").matcher(e.getMessage()).find());
        }
    }

    @Test
    public void testEqualsAndHashCode() {
        SecurityConfig one = new SecurityConfig(null, true);
        SecurityConfig two = new SecurityConfig(null, false);
        SecurityConfig three = new SecurityConfig(null, true);
        Assert.assertThat(one, Matchers.is(three));
        Assert.assertThat(one, Matchers.not(Matchers.is(two)));
        Assert.assertThat(one.hashCode(), Matchers.is(three.hashCode()));
        Assert.assertThat(one.hashCode(), Matchers.not(Matchers.is(two.hashCode())));
    }

    @Test
    public void shouldGetPluginRolesWhichBelogsToSpecifiedPlugin() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.addRole(new PluginRoleConfig("foo", "ldap"));
        securityConfig.addRole(new PluginRoleConfig("bar", "github"));
        securityConfig.addRole(new RoleConfig(new CaseInsensitiveString("xyz")));
        securityConfig.securityAuthConfigs().add(new SecurityAuthConfig("ldap", "cd.go.ldap"));
        securityConfig.securityAuthConfigs().add(new SecurityAuthConfig("github", "cd.go.github"));
        List<PluginRoleConfig> pluginRolesConfig = securityConfig.getPluginRoles("cd.go.ldap");
        Assert.assertThat(pluginRolesConfig, Matchers.hasSize(1));
        Assert.assertThat(pluginRolesConfig, Matchers.contains(new PluginRoleConfig("foo", "ldap")));
    }

    @Test
    public void getPluginRolesConfig_shouldReturnNothingWhenBadPluginIdSpecified() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.addRole(new PluginRoleConfig("foo", "ldap"));
        securityConfig.addRole(new PluginRoleConfig("bar", "github"));
        securityConfig.addRole(new RoleConfig(new CaseInsensitiveString("xyz")));
        securityConfig.securityAuthConfigs().add(new SecurityAuthConfig("ldap", "cd.go.ldap"));
        securityConfig.securityAuthConfigs().add(new SecurityAuthConfig("github", "cd.go.github"));
        List<PluginRoleConfig> pluginRolesConfig = securityConfig.getPluginRoles("non-existant-plugin");
        Assert.assertThat(pluginRolesConfig, Matchers.hasSize(0));
    }

    @Test
    public void getPluginRole_shouldReturnPluginRoleMatchingTheGivenName() throws Exception {
        PluginRoleConfig role = new PluginRoleConfig("foo", "ldap");
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.addRole(role);
        Assert.assertThat(securityConfig.getPluginRole(new CaseInsensitiveString("FOO")), Matchers.is(role));
    }

    @Test
    public void getPluginRole_shouldReturnNullInAbsenceOfPluginRoleForTheGivenName() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        Assert.assertNull(securityConfig.getPluginRole(new CaseInsensitiveString("foo")));
    }
}

