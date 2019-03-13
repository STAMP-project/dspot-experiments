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


import DefaultGroups.ANYONE;
import Qualifiers.PROJECT;
import org.junit.Test;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.web.UserRole;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.GroupDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.permission.PermissionService;


public class GroupsActionTest extends BasePermissionWsTest<GroupsAction> {
    private GroupDto group1;

    private GroupDto group2;

    private GroupDto group3;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    @Test
    public void search_for_groups_with_one_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        String json = newRequest().setParam(PARAM_PERMISSION, SCAN.getKey()).execute().getInput();
        assertJson(json).isSimilarTo((((((((((((((((((("{\n" + (((((((((((((("  \"paging\": {\n" + "    \"pageIndex\": 1,\n") + "    \"pageSize\": 20,\n") + "    \"total\": 3\n") + "  },\n") + "  \"groups\": [\n") + "    {\n") + "      \"name\": \"Anyone\",\n") + "      \"permissions\": [\n") + "        \"scan\"\n") + "      ]\n") + "    },\n") + "    {\n") + "      \"name\": \"group-1-name\",\n") + "      \"description\": \"")) + (group1.getDescription())) + "\",\n") + "      \"permissions\": [\n") + "        \"scan\"\n") + "      ]\n") + "    },\n") + "    {\n") + "      \"name\": \"group-2-name\",\n") + "      \"description\": \"") + (group2.getDescription())) + "\",\n") + "      \"permissions\": [\n") + "        \"scan\"\n") + "      ]\n") + "    }\n") + "  ]\n") + "}\n"));
    }

    @Test
    public void search_with_selection() {
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PERMISSION, SCAN.getKey()).execute().getInput();
        assertThat(result).containsSubsequence(ANYONE, "group-1", "group-2");
    }

    @Test
    public void search_groups_with_pagination() {
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PERMISSION, SCAN.getKey()).setParam(PAGE_SIZE, "1").setParam(PAGE, "3").execute().getInput();
        assertThat(result).contains("group-2").doesNotContain("group-1").doesNotContain("group-3");
    }

    @Test
    public void search_groups_with_query() {
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PERMISSION, SCAN.getKey()).setParam(TEXT_QUERY, "group-").execute().getInput();
        assertThat(result).contains("group-1", "group-2").doesNotContain(ANYONE);
    }

    @Test
    public void search_groups_with_project_permissions() {
        OrganizationDto organizationDto = db.getDefaultOrganization();
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organizationDto, "project-uuid"));
        GroupDto group = db.users().insertGroup(organizationDto, "project-group-name");
        db.users().insertProjectPermissionOnGroup(group, UserRole.ISSUE_ADMIN, project);
        ComponentDto anotherProject = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        GroupDto anotherGroup = db.users().insertGroup(organizationDto, "another-project-group-name");
        db.users().insertProjectPermissionOnGroup(anotherGroup, UserRole.ISSUE_ADMIN, anotherProject);
        GroupDto groupWithoutPermission = db.users().insertGroup(organizationDto, "group-without-permission");
        userSession.logIn().addProjectPermission(UserRole.ADMIN, project);
        String result = newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_ID, "project-uuid").execute().getInput();
        assertThat(result).contains(group.getName()).doesNotContain(anotherGroup.getName()).doesNotContain(groupWithoutPermission.getName());
    }

    @Test
    public void return_also_groups_without_permission_when_search_query() {
        OrganizationDto organizationDto = db.getDefaultOrganization();
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organizationDto, "project-uuid"));
        GroupDto group = db.users().insertGroup(organizationDto, "group-with-permission");
        db.users().insertProjectPermissionOnGroup(group, UserRole.ISSUE_ADMIN, project);
        GroupDto groupWithoutPermission = db.users().insertGroup(organizationDto, "group-without-permission");
        GroupDto anotherGroup = db.users().insertGroup(organizationDto, "another-group");
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_ID, "project-uuid").setParam(TEXT_QUERY, "group-with").execute().getInput();
        assertThat(result).contains(group.getName()).doesNotContain(groupWithoutPermission.getName()).doesNotContain(anotherGroup.getName());
    }

    @Test
    public void return_only_groups_with_permission_when_no_search_query() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(db.getDefaultOrganization(), "project-uuid"));
        GroupDto group = db.users().insertGroup(db.getDefaultOrganization(), "project-group-name");
        db.users().insertProjectPermissionOnGroup(group, UserRole.ISSUE_ADMIN, project);
        GroupDto groupWithoutPermission = db.users().insertGroup(db.getDefaultOrganization(), "group-without-permission");
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_ID, project.uuid()).execute().getInput();
        assertThat(result).contains(group.getName()).doesNotContain(groupWithoutPermission.getName());
    }

    @Test
    public void return_anyone_group_when_search_query_and_no_param_permission() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organizationDto, "project-uuid"));
        GroupDto group = db.users().insertGroup(organizationDto, "group-with-permission");
        db.users().insertProjectPermissionOnGroup(group, UserRole.ISSUE_ADMIN, project);
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PROJECT_ID, project.uuid()).setParam(TEXT_QUERY, "nyo").execute().getInput();
        assertThat(result).contains("Anyone");
    }

    @Test
    public void search_groups_on_views() {
        ComponentDto view = db.components().insertComponent(newView(db.getDefaultOrganization(), "view-uuid").setDbKey("view-key"));
        GroupDto group = db.users().insertGroup(db.getDefaultOrganization(), "project-group-name");
        db.users().insertProjectPermissionOnGroup(group, UserRole.ISSUE_ADMIN, view);
        loginAsAdmin(db.getDefaultOrganization());
        String result = newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_ID, "view-uuid").execute().getInput();
        assertThat(result).contains("project-group-name").doesNotContain("group-1").doesNotContain("group-2").doesNotContain("group-3");
    }

    @Test
    public void fail_if_not_logged_in() {
        expectedException.expect(UnauthorizedException.class);
        userSession.anonymous();
        newRequest().setParam(PARAM_PERMISSION, SCAN.getKey()).execute();
    }

    @Test
    public void fail_if_insufficient_privileges() {
        expectedException.expect(ForbiddenException.class);
        userSession.logIn("login");
        newRequest().setParam(PARAM_PERMISSION, SCAN.getKey()).execute();
    }

    @Test
    public void fail_if_project_uuid_and_project_key_are_provided() {
        db.components().insertComponent(newPrivateProjectDto(db.organizations().insert(), "project-uuid").setDbKey("project-key"));
        expectedException.expect(BadRequestException.class);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_PERMISSION, SCAN_EXECUTION).setParam(PARAM_PROJECT_ID, "project-uuid").setParam(PARAM_PROJECT_KEY, "project-key").execute();
    }

    @Test
    public void fail_when_using_branch_uuid() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        GroupDto group = db.users().insertGroup(db.getDefaultOrganization());
        db.users().insertProjectPermissionOnGroup(group, UserRole.ISSUE_ADMIN, project);
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Project id '%s' not found", branch.uuid()));
        newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_ID, branch.uuid()).execute();
    }

    @Test
    public void fail_when_using_branch_db_key() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        GroupDto group = db.users().insertGroup(db.getDefaultOrganization());
        db.users().insertProjectPermissionOnGroup(group, UserRole.ISSUE_ADMIN, project);
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Project key '%s' not found", branch.getDbKey()));
        newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_PROJECT_KEY, branch.getDbKey()).execute();
    }
}

