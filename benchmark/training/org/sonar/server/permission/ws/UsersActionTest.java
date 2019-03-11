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
package org.sonar.server.permission.ws;


import Param.SELECTED;
import Qualifiers.PROJECT;
import SelectionMode.ALL;
import UserRole.ADMIN;
import UserRole.ISSUE_ADMIN;
import org.junit.Test;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.web.UserRole;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.permission.PermissionService;


public class UsersActionTest extends BasePermissionWsTest<UsersAction> {
    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    private RequestValidator requestValidator = new RequestValidator(permissionService);

    @Test
    public void search_for_users_with_response_example() {
        UserDto user1 = db.users().insertUser(UserTesting.newUserDto().setLogin("admin").setName("Administrator").setEmail("admin@admin.com"));
        db.organizations().addMember(db.getDefaultOrganization(), user1);
        UserDto user2 = db.users().insertUser(UserTesting.newUserDto().setLogin("george.orwell").setName("George Orwell").setEmail("george.orwell@1984.net"));
        db.organizations().addMember(db.getDefaultOrganization(), user2);
        db.users().insertPermissionOnUser(user1, ADMINISTER_QUALITY_PROFILES);
        db.users().insertPermissionOnUser(user1, ADMINISTER);
        db.users().insertPermissionOnUser(user1, ADMINISTER_QUALITY_GATES);
        db.users().insertPermissionOnUser(user2, SCAN);
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().execute().getInput();
        assertJson(result).withStrictArrayOrder().isSimilarTo(getClass().getResource("users-example.json"));
    }

    @Test
    public void search_for_users_with_one_permission() {
        insertUsersHavingGlobalPermissions();
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam("permission", "scan").execute().getInput();
        assertJson(result).withStrictArrayOrder().isSimilarTo(getClass().getResource("UsersActionTest/users.json"));
    }

    @Test
    public void search_for_users_with_permission_on_project() {
        // User has permission on project
        ComponentDto project = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()));
        UserDto user = db.users().insertUser(UserTesting.newUserDto());
        db.organizations().addMember(db.getDefaultOrganization(), user);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        // User has permission on another project
        ComponentDto anotherProject = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()));
        UserDto userHavePermissionOnAnotherProject = db.users().insertUser(UserTesting.newUserDto());
        db.organizations().addMember(db.getDefaultOrganization(), userHavePermissionOnAnotherProject);
        db.users().insertProjectPermissionOnUser(userHavePermissionOnAnotherProject, UserRole.ISSUE_ADMIN, anotherProject);
        // User has no permission
        UserDto withoutPermission = db.users().insertUser(UserTesting.newUserDto());
        db.organizations().addMember(db.getDefaultOrganization(), withoutPermission);
        userSession.logIn().addProjectPermission(SYSTEM_ADMIN, project);
        String result = newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_ID, project.uuid()).execute().getInput();
        assertThat(result).contains(user.getLogin()).doesNotContain(userHavePermissionOnAnotherProject.getLogin()).doesNotContain(withoutPermission.getLogin());
    }

    @Test
    public void search_also_for_users_without_permission_when_filtering_name() {
        // User with permission on project
        ComponentDto project = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(db.organizations().insert()));
        UserDto user = db.users().insertUser(UserTesting.newUserDto("with-permission-login", "with-permission-name", "with-permission-email"));
        db.organizations().addMember(db.getDefaultOrganization(), user);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        // User without permission
        UserDto withoutPermission = db.users().insertUser(UserTesting.newUserDto("without-permission-login", "without-permission-name", "without-permission-email"));
        db.organizations().addMember(db.getDefaultOrganization(), withoutPermission);
        UserDto anotherUser = db.users().insertUser(UserTesting.newUserDto("another-user", "another-user", "another-user"));
        db.organizations().addMember(db.getDefaultOrganization(), anotherUser);
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PROJECT_ID, project.uuid()).setParam(TEXT_QUERY, "with").execute().getInput();
        assertThat(result).contains(user.getLogin(), withoutPermission.getLogin()).doesNotContain(anotherUser.getLogin());
    }

    @Test
    public void search_also_for_users_without_permission_when_filtering_email() {
        // User with permission on project
        ComponentDto project = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(db.organizations().insert()));
        UserDto user = db.users().insertUser(UserTesting.newUserDto("with-permission-login", "with-permission-name", "with-permission-email"));
        db.organizations().addMember(db.getDefaultOrganization(), user);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        // User without permission
        UserDto withoutPermission = db.users().insertUser(UserTesting.newUserDto("without-permission-login", "without-permission-name", "without-permission-email"));
        db.organizations().addMember(db.getDefaultOrganization(), withoutPermission);
        UserDto anotherUser = db.users().insertUser(UserTesting.newUserDto("another-user", "another-user", "another-user"));
        db.organizations().addMember(db.getDefaultOrganization(), anotherUser);
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PROJECT_ID, project.uuid()).setParam(TEXT_QUERY, "email").execute().getInput();
        assertThat(result).contains(user.getLogin(), withoutPermission.getLogin()).doesNotContain(anotherUser.getLogin());
    }

    @Test
    public void search_also_for_users_without_permission_when_filtering_login() {
        // User with permission on project
        ComponentDto project = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(db.organizations().insert()));
        UserDto user = db.users().insertUser(UserTesting.newUserDto("with-permission-login", "with-permission-name", "with-permission-email"));
        db.organizations().addMember(db.getDefaultOrganization(), user);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        // User without permission
        UserDto withoutPermission = db.users().insertUser(UserTesting.newUserDto("without-permission-login", "without-permission-name", "without-permission-email"));
        db.organizations().addMember(db.getDefaultOrganization(), withoutPermission);
        UserDto anotherUser = db.users().insertUser(UserTesting.newUserDto("another-user", "another-user", "another-user"));
        db.organizations().addMember(db.getDefaultOrganization(), anotherUser);
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PROJECT_ID, project.uuid()).setParam(TEXT_QUERY, "login").execute().getInput();
        assertThat(result).contains(user.getLogin(), withoutPermission.getLogin()).doesNotContain(anotherUser.getLogin());
    }

    @Test
    public void search_for_users_with_query_as_a_parameter() {
        insertUsersHavingGlobalPermissions();
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam("permission", "scan").setParam(TEXT_QUERY, "ame-1").execute().getInput();
        assertThat(result).contains("login-1").doesNotContain("login-2").doesNotContain("login-3");
    }

    @Test
    public void search_for_users_with_select_as_a_parameter() {
        insertUsersHavingGlobalPermissions();
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().execute().getInput();
        assertThat(result).contains("login-1", "login-2", "login-3");
    }

    @Test
    public void search_for_users_is_paginated() {
        for (int i = 9; i >= 0; i--) {
            UserDto user = db.users().insertUser(UserTesting.newUserDto().setName(("user-" + i)));
            db.organizations().addMember(db.getDefaultOrganization(), user);
            db.users().insertPermissionOnUser(user, ADMINISTER);
            db.users().insertPermissionOnUser(user, ADMINISTER_QUALITY_GATES);
        }
        loginAsAdmin(db.getDefaultOrganization());
        assertJson(newRequest().setParam(PAGE, "1").setParam(PAGE_SIZE, "2").execute().getInput()).withStrictArrayOrder().isSimilarTo(("{\n" + ((((((((((((("  \"paging\": {\n" + "    \"pageIndex\": 1,\n") + "    \"pageSize\": 2,\n") + "    \"total\": 10\n") + "  },\n") + "  \"users\": [\n") + "    {\n") + "      \"name\": \"user-0\"\n") + "    },\n") + "    {\n") + "      \"name\": \"user-1\"\n") + "    }\n") + "  ]\n") + "}")));
        assertJson(newRequest().setParam(PAGE, "3").setParam(PAGE_SIZE, "4").execute().getInput()).withStrictArrayOrder().isSimilarTo(("{\n" + ((((((((((((("  \"paging\": {\n" + "    \"pageIndex\": 3,\n") + "    \"pageSize\": 4,\n") + "    \"total\": 10\n") + "  },\n") + "  \"users\": [\n") + "    {\n") + "      \"name\": \"user-8\"\n") + "    },\n") + "    {\n") + "      \"name\": \"user-9\"\n") + "    }\n") + "  ]\n") + "}")));
    }

    @Test
    public void return_more_than_20_permissions() {
        loginAsAdmin(db.getDefaultOrganization());
        for (int i = 0; i < 30; i++) {
            UserDto user = db.users().insertUser(UserTesting.newUserDto().setLogin(("user-" + i)));
            db.organizations().addMember(db.getDefaultOrganization(), user);
            db.users().insertPermissionOnUser(user, SCAN);
            db.users().insertPermissionOnUser(user, PROVISION_PROJECTS);
        }
        String result = newRequest().setParam(PAGE_SIZE, "100").execute().getInput();
        assertThat(countMatches(result, "scan")).isEqualTo(30);
    }

    @Test
    public void fail_if_project_permission_without_project() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest().setParam(PARAM_PERMISSION, ISSUE_ADMIN).setParam(SELECTED, ALL.value()).execute();
    }

    @Test
    public void fail_if_insufficient_privileges() {
        userSession.logIn("login");
        expectedException.expect(ForbiddenException.class);
        newRequest().setParam("permission", SYSTEM_ADMIN).execute();
    }

    @Test
    public void fail_if_not_logged_in() {
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest().setParam("permission", SYSTEM_ADMIN).execute();
    }

    @Test
    public void fail_if_project_uuid_and_project_key_are_provided() {
        db.components().insertComponent(newPrivateProjectDto(db.organizations().insert(), "project-uuid").setDbKey("project-key"));
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Project id or project key can be provided, not both.");
        newRequest().setParam(PARAM_PERMISSION, SYSTEM_ADMIN).setParam(PARAM_PROJECT_ID, "project-uuid").setParam(PARAM_PROJECT_KEY, "project-key").execute();
    }

    @Test
    public void fail_if_search_query_is_too_short() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'q' length (2) is shorter than the minimum authorized (3)");
        newRequest().setParam(TEXT_QUERY, "ab").execute();
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        UserDto user = db.users().insertUser(UserTesting.newUserDto());
        ComponentDto project = db.components().insertMainBranch(organization);
        ComponentDto branch = db.components().insertProjectBranch(project);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        userSession.logIn().addProjectPermission(ADMIN, project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Project key '%s' not found", branch.getDbKey()));
        newRequest().setParam(PARAM_ORGANIZATION, organization.getKey()).setParam(PARAM_PROJECT_KEY, branch.getDbKey()).setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, SYSTEM_ADMIN).execute();
    }

    @Test
    public void fail_when_using_branch_uuid() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user = db.users().insertUser(UserTesting.newUserDto());
        ComponentDto project = db.components().insertMainBranch(organization);
        ComponentDto branch = db.components().insertProjectBranch(project);
        db.users().insertProjectPermissionOnUser(user, UserRole.ISSUE_ADMIN, project);
        userSession.logIn().addProjectPermission(ADMIN, project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Project id '%s' not found", branch.uuid()));
        newRequest().setParam(PARAM_ORGANIZATION, organization.getKey()).setParam(PARAM_PROJECT_ID, branch.uuid()).setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, SYSTEM_ADMIN).execute();
    }
}

