/**
 * Copyright 2018 ThoughtWorks, Inc.
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


import AdminUser.NAME;
import Authorization.PresentationElement;
import Authorization.PrivilegeState.DISABLED;
import Authorization.PrivilegeState.OFF;
import Authorization.PrivilegeState.ON;
import Authorization.UserType.ROLE;
import Authorization.UserType.USER;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AuthorizationTest {
    @Test
    public void shouldReturnTrueIfViewPermissionDefined() {
        Authorization authorization = new Authorization(new ViewConfig(new AdminUser(new CaseInsensitiveString("baby"))));
        Assert.assertThat(authorization.hasViewPermissionDefined(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfViewPermissionNotDefined() {
        Authorization authorization = new Authorization(new ViewConfig());
        Assert.assertThat(authorization.hasViewPermissionDefined(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfOperationPermissionDefined() {
        Authorization authorization = new Authorization(new OperationConfig(new AdminUser(new CaseInsensitiveString("baby"))));
        Assert.assertThat(authorization.hasOperationPermissionDefined(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfOperationPermissionNotDefined() {
        Authorization authorization = new Authorization(new OperationConfig());
        Assert.assertThat(authorization.hasOperationPermissionDefined(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfAdminsAreDefined() {
        Authorization authorization = new Authorization(new AdminsConfig(new AdminUser(new CaseInsensitiveString("foo"))));
        Assert.assertThat(authorization.hasAdminsDefined(), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfAnUserIsAdmin() {
        Authorization authorization = new Authorization(new AdminsConfig(new AdminUser(new CaseInsensitiveString("foo"))));
        Assert.assertThat(authorization.isUserAnAdmin(new CaseInsensitiveString("foo"), new ArrayList()), Matchers.is(true));
        Assert.assertThat(authorization.isUserAnAdmin(new CaseInsensitiveString("bar"), new ArrayList()), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfAnUserBelongsToAnAdminRole() {
        Authorization authorization = new Authorization(new AdminsConfig(new AdminRole(new CaseInsensitiveString("bar1")), new AdminRole(new CaseInsensitiveString("bar2"))));
        Assert.assertThat(authorization.isUserAnAdmin(new CaseInsensitiveString("foo1"), Arrays.asList(new RoleConfig(new CaseInsensitiveString("bar1")), new RoleConfig(new CaseInsensitiveString("bar1")))), Matchers.is(true));
        Assert.assertThat(authorization.isUserAnAdmin(new CaseInsensitiveString("foo2"), Arrays.asList(new RoleConfig(new CaseInsensitiveString("bar2")))), Matchers.is(true));
        Assert.assertThat(authorization.isUserAnAdmin(new CaseInsensitiveString("foo3"), Arrays.asList(new RoleConfig(new CaseInsensitiveString("bar1")))), Matchers.is(true));
        Assert.assertThat(authorization.isUserAnAdmin(new CaseInsensitiveString("foo4"), new ArrayList()), Matchers.is(false));
    }

    @Test
    public void shouldSayThatAnAdmin_HasAdminOrViewPermissions() {
        CaseInsensitiveString adminUser = new CaseInsensitiveString("admin");
        Authorization authorization = new Authorization(new AdminsConfig(new AdminUser(adminUser)));
        Assert.assertThat(authorization.hasAdminOrViewPermissions(adminUser, null), Matchers.is(true));
    }

    @Test
    public void shouldSayThatAViewUser_HasAdminOrViewPermissions() {
        CaseInsensitiveString viewUser = new CaseInsensitiveString("view");
        Authorization authorization = new Authorization(new ViewConfig(new AdminUser(viewUser)));
        Assert.assertThat(authorization.hasAdminOrViewPermissions(viewUser, null), Matchers.is(true));
    }

    @Test
    public void shouldSayThatAnAdminWithinARole_HasAdminOrViewPermissions() {
        CaseInsensitiveString adminUser = new CaseInsensitiveString("admin");
        RoleConfig role = new RoleConfig(new CaseInsensitiveString("role1"), new RoleUser(adminUser));
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        Authorization authorization = new Authorization(new AdminsConfig(new AdminRole(role)));
        Assert.assertThat(authorization.hasAdminOrViewPermissions(adminUser, roles), Matchers.is(true));
    }

    @Test
    public void shouldSayThatAViewUserWithinARole_HasAdminOrViewPermissions() {
        CaseInsensitiveString viewUser = new CaseInsensitiveString("view");
        RoleConfig role = new RoleConfig(new CaseInsensitiveString("role1"), new RoleUser(viewUser));
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        Authorization authorization = new Authorization(new ViewConfig(new AdminRole(role)));
        Assert.assertThat(authorization.hasAdminOrViewPermissions(viewUser, roles), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseForUserNotInAdminOrViewConfig() {
        CaseInsensitiveString viewUser = new CaseInsensitiveString("view");
        Authorization authorization = new Authorization();
        Assert.assertThat(authorization.hasAdminOrViewPermissions(viewUser, null), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseForNonAdminNonViewUserWithinARole() {
        CaseInsensitiveString viewUser = new CaseInsensitiveString("view");
        RoleConfig role = new RoleConfig(new CaseInsensitiveString("role1"), new RoleUser(viewUser));
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        Authorization authorization = new Authorization(new ViewConfig(new AdminUser(new CaseInsensitiveString("other-user"))));
        Assert.assertThat(authorization.hasAdminOrViewPermissions(viewUser, roles), Matchers.is(false));
    }

    @Test
    public void shouldReturnAuthorizationMapForView() {
        Authorization authorization = new Authorization();
        authorization.getAdminsConfig().add(new AdminRole(new CaseInsensitiveString("group_of_losers")));
        authorization.getOperationConfig().addAll(DataStructureUtils.a(new AdminUser(new CaseInsensitiveString("loser")), new AdminRole(new CaseInsensitiveString("group_of_losers")), new AdminRole(new CaseInsensitiveString("gang_of_boozers"))));
        authorization.getViewConfig().addAll(DataStructureUtils.a(new AdminUser(new CaseInsensitiveString("boozer")), new AdminUser(new CaseInsensitiveString("loser"))));
        List<Authorization.PresentationElement> userAuthMap = authorization.getUserAuthorizations();
        Assert.assertThat(userAuthMap.size(), Matchers.is(2));
        assetEntry(userAuthMap.get(0), "boozer", OFF, ON, OFF, USER);
        assetEntry(userAuthMap.get(1), "loser", OFF, ON, ON, USER);
        List<Authorization.PresentationElement> roleAuthMap = authorization.getRoleAuthorizations();
        Assert.assertThat(roleAuthMap.size(), Matchers.is(2));
        assetEntry(roleAuthMap.get(0), "gang_of_boozers", OFF, OFF, ON, ROLE);
        assetEntry(roleAuthMap.get(1), "group_of_losers", ON, DISABLED, DISABLED, ROLE);
    }

    @Test
    public void shouldPopulateErrorsOnPresentationElementWhenAnInvalidUserIsAddedToAdminList() {
        Authorization authorization = new Authorization();
        AdminUser invalidUser = new AdminUser(new CaseInsensitiveString("boo_user"));
        invalidUser.addError(NAME, "some error");
        AdminUser validUser = new AdminUser(new CaseInsensitiveString("valid_user"));
        authorization.getAdminsConfig().add(invalidUser);
        authorization.getAdminsConfig().add(validUser);
        List<Authorization.PresentationElement> userAuthorizations = authorization.getUserAuthorizations();
        Assert.assertThat(userAuthorizations.get(0).errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(userAuthorizations.get(0).errors().on(Admin.NAME), Matchers.is("some error"));
        Assert.assertThat(userAuthorizations.get(1).errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldPopulateErrorsOnPresentationElementWhenAnInvalidRoleIsAddedToAdminList() {
        Authorization authorization = new Authorization();
        AdminRole invalidRole = new AdminRole(new CaseInsensitiveString("boo_user"));
        invalidRole.addError(NAME, "some error");
        AdminRole validRole = new AdminRole(new CaseInsensitiveString("valid_user"));
        authorization.getAdminsConfig().add(invalidRole);
        authorization.getAdminsConfig().add(validRole);
        List<Authorization.PresentationElement> roleAuthorizations = authorization.getRoleAuthorizations();
        Assert.assertThat(roleAuthorizations.get(0).errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(roleAuthorizations.get(0).errors().on(Admin.NAME), Matchers.is("some error"));
        Assert.assertThat(roleAuthorizations.get(1).errors().isEmpty(), Matchers.is(true));
    }
}

