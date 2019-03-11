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
import Permissions.UsersWsResponse;
import Qualifiers.PROJECT;
import WebService.Param.PAGE;
import WebService.Param.PAGE_SIZE;
import WebService.Param.SELECTED;
import WebService.Param.TEXT_QUERY;
import org.junit.Test;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.web.UserRole;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.RequestValidator;
import org.sonar.server.permission.ws.WsParameters;
import org.sonarqube.ws.Permissions;


public class TemplateUsersActionTest extends BasePermissionWsTest<TemplateUsersAction> {
    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    private RequestValidator requestValidator = new RequestValidator(permissionService);

    @Test
    public void search_for_users_with_response_example() {
        UserDto user1 = insertUser(UserTesting.newUserDto().setLogin("admin").setName("Administrator").setEmail("admin@admin.com"));
        UserDto user2 = insertUser(UserTesting.newUserDto().setLogin("george.orwell").setName("George Orwell").setEmail("george.orwell@1984.net"));
        PermissionTemplateDto template1 = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.CODEVIEWER, template1, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.CODEVIEWER, template1, user2));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ADMIN, template1, user2));
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest(null, template1.getUuid()).execute().getInput();
        assertJson(result).isSimilarTo(getClass().getResource("template_users-example.json"));
    }

    @Test
    public void search_for_users_by_template_name() {
        loginAsAdmin(db.getDefaultOrganization());
        UserDto user1 = insertUser(UserTesting.newUserDto().setLogin("login-1").setName("name-1").setEmail("email-1"));
        UserDto user2 = insertUser(UserTesting.newUserDto().setLogin("login-2").setName("name-2").setEmail("email-2"));
        UserDto user3 = insertUser(UserTesting.newUserDto().setLogin("login-3").setName("name-3").setEmail("email-3"));
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user2));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user3));
        PermissionTemplateDto anotherTemplate = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, anotherTemplate, user1));
        Permissions.UsersWsResponse response = newRequest(null, null).setParam(PARAM_TEMPLATE_NAME, template.getName()).executeProtobuf(UsersWsResponse.class);
        assertThat(response.getUsersList()).extracting("login").containsExactly("login-1", "login-2", "login-3");
        assertThat(response.getUsers(0).getPermissionsList()).containsOnly("issueadmin", "user");
        assertThat(response.getUsers(1).getPermissionsList()).containsOnly("user");
        assertThat(response.getUsers(2).getPermissionsList()).containsOnly("issueadmin");
    }

    @Test
    public void search_using_text_query() {
        loginAsAdmin(db.getDefaultOrganization());
        UserDto user1 = insertUser(UserTesting.newUserDto().setLogin("login-1").setName("name-1").setEmail("email-1"));
        UserDto user2 = insertUser(UserTesting.newUserDto().setLogin("login-2").setName("name-2").setEmail("email-2"));
        UserDto user3 = insertUser(UserTesting.newUserDto().setLogin("login-3").setName("name-3").setEmail("email-3"));
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user2));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user3));
        PermissionTemplateDto anotherTemplate = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, anotherTemplate, user1));
        Permissions.UsersWsResponse response = newRequest(null, null).setParam(PARAM_TEMPLATE_NAME, template.getName()).setParam(TEXT_QUERY, "ame-1").executeProtobuf(UsersWsResponse.class);
        assertThat(response.getUsersList()).extracting("login").containsOnly("login-1");
    }

    @Test
    public void search_using_permission() {
        UserDto user1 = insertUser(UserTesting.newUserDto().setLogin("login-1").setName("name-1").setEmail("email-1"));
        UserDto user2 = insertUser(UserTesting.newUserDto().setLogin("login-2").setName("name-2").setEmail("email-2"));
        UserDto user3 = insertUser(UserTesting.newUserDto().setLogin("login-3").setName("name-3").setEmail("email-3"));
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user2));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user3));
        PermissionTemplateDto anotherTemplate = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, anotherTemplate, user1));
        loginAsAdmin(db.getDefaultOrganization());
        Permissions.UsersWsResponse response = newRequest(UserRole.USER, template.getUuid()).executeProtobuf(UsersWsResponse.class);
        assertThat(response.getUsersList()).extracting("login").containsExactly("login-1", "login-2");
        assertThat(response.getUsers(0).getPermissionsList()).containsOnly("issueadmin", "user");
        assertThat(response.getUsers(1).getPermissionsList()).containsOnly("user");
    }

    @Test
    public void search_with_pagination() {
        UserDto user1 = insertUser(UserTesting.newUserDto().setLogin("login-1").setName("name-1").setEmail("email-1"));
        UserDto user2 = insertUser(UserTesting.newUserDto().setLogin("login-2").setName("name-2").setEmail("email-2"));
        UserDto user3 = insertUser(UserTesting.newUserDto().setLogin("login-3").setName("name-3").setEmail("email-3"));
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user2));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user3));
        PermissionTemplateDto anotherTemplate = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, anotherTemplate, user1));
        loginAsAdmin(db.getDefaultOrganization());
        Permissions.UsersWsResponse response = newRequest(UserRole.USER, null).setParam(PARAM_TEMPLATE_NAME, template.getName()).setParam(SELECTED, "all").setParam(PAGE, "2").setParam(PAGE_SIZE, "1").executeProtobuf(UsersWsResponse.class);
        assertThat(response.getUsersList()).extracting("login").containsOnly("login-2");
    }

    @Test
    public void users_are_sorted_by_name() {
        UserDto user1 = insertUser(UserTesting.newUserDto().setLogin("login-2").setName("name-2"));
        UserDto user2 = insertUser(UserTesting.newUserDto().setLogin("login-3").setName("name-3"));
        UserDto user3 = insertUser(UserTesting.newUserDto().setLogin("login-1").setName("name-1"));
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user1));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.USER, template, user2));
        addUserToTemplate(TemplateUsersActionTest.newPermissionTemplateUser(UserRole.ISSUE_ADMIN, template, user3));
        loginAsAdmin(db.getDefaultOrganization());
        Permissions.UsersWsResponse response = newRequest(null, null).setParam(PARAM_TEMPLATE_NAME, template.getName()).executeProtobuf(UsersWsResponse.class);
        assertThat(response.getUsersList()).extracting("login").containsExactly("login-1", "login-2", "login-3");
    }

    @Test
    public void fail_if_not_a_project_permission() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(PROVISIONING, template.getUuid()).execute();
    }

    @Test
    public void fail_if_no_template_param() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest(null, null).execute();
    }

    @Test
    public void fail_if_template_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        newRequest(null, "unknown-template-uuid").execute();
    }

    @Test
    public void fail_if_template_uuid_and_name_provided() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest(null, template.getUuid()).setParam(PARAM_TEMPLATE_NAME, template.getName()).execute();
    }

    @Test
    public void fail_if_not_logged_in() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest(null, template.getUuid()).execute();
    }

    @Test
    public void fail_if_insufficient_privileges() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        userSession.logIn().addPermission(SCAN, db.getDefaultOrganization());
        expectedException.expect(ForbiddenException.class);
        newRequest(null, template.getUuid()).execute();
    }
}

