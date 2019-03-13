/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.permission.ws.template;


import GlobalPermissions.PROVISIONING;
import Qualifiers.PROJECT;
import org.junit.Test;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.web.UserRole;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.RequestValidator;
import org.sonar.server.permission.ws.WsParameters;


public class RemoveUserFromTemplateActionTest extends BasePermissionWsTest<RemoveUserFromTemplateAction> {
    private static final String DEFAULT_PERMISSION = UserRole.CODEVIEWER;

    private UserDto user;

    private PermissionTemplateDto template;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    private RequestValidator requestValidator = new RequestValidator(permissionService);

    @Test
    public void remove_user_from_template() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(user.getLogin(), template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
        assertThat(getLoginsInTemplateAndPermission(template, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION)).isEmpty();
    }

    @Test
    public void remove_user_from_template_by_name_case_insensitive() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION).setParam(PARAM_TEMPLATE_NAME, template.getName().toUpperCase()).execute();
        assertThat(getLoginsInTemplateAndPermission(template, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION)).isEmpty();
    }

    @Test
    public void remove_user_from_template_twice_without_failing() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(user.getLogin(), template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
        newRequest(user.getLogin(), template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
        assertThat(getLoginsInTemplateAndPermission(template, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION)).isEmpty();
    }

    @Test
    public void keep_user_permission_not_removed() {
        addUserToTemplate(user, template, UserRole.ISSUE_ADMIN);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(user.getLogin(), template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
        assertThat(getLoginsInTemplateAndPermission(template, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION)).isEmpty();
        assertThat(getLoginsInTemplateAndPermission(template, UserRole.ISSUE_ADMIN)).containsExactly(user.getLogin());
    }

    @Test
    public void keep_other_users_when_one_user_removed() {
        UserDto newUser = db.users().insertUser("new-login");
        db.organizations().addMember(db.getDefaultOrganization(), newUser);
        addUserToTemplate(newUser, template, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(user.getLogin(), template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
        assertThat(getLoginsInTemplateAndPermission(template, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION)).containsExactly("new-login");
    }

    @Test
    public void fail_if_not_a_project_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(user.getLogin(), template.getUuid(), PROVISIONING);
    }

    @Test
    public void fail_if_insufficient_privileges() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        newRequest(user.getLogin(), template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
    }

    @Test
    public void fail_if_not_logged_in() {
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest(user.getLogin(), template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
    }

    @Test
    public void fail_if_user_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(null, template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
    }

    @Test
    public void fail_if_permission_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(user.getLogin(), template.getUuid(), null);
    }

    @Test
    public void fail_if_template_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest(user.getLogin(), null, RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
    }

    @Test
    public void fail_if_user_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("User with login 'unknown-login' is not found");
        newRequest("unknown-login", template.getUuid(), RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
    }

    @Test
    public void fail_if_template_key_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Permission template with id 'unknown-key' is not found");
        newRequest(user.getLogin(), "unknown-key", RemoveUserFromTemplateActionTest.DEFAULT_PERMISSION);
    }
}

