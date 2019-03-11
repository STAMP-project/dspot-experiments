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


import GlobalPermissions.QUALITY_GATE_ADMIN;
import Qualifiers.PROJECT;
import org.junit.Test;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.web.UserRole;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.GroupDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.RequestValidator;
import org.sonar.server.permission.ws.WsParameters;
import org.sonarqube.ws.Permissions.WsGroupsResponse;


public class TemplateGroupsActionTest extends BasePermissionWsTest<TemplateGroupsAction> {
    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    private RequestValidator requestValidator = new RequestValidator(permissionService);

    @Test
    public void template_groups_of_json_example() {
        GroupDto adminGroup = insertGroupOnDefaultOrganization("sonar-administrators", "System administrators");
        GroupDto userGroup = insertGroupOnDefaultOrganization("sonar-users", "Any new users created will automatically join this group");
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ISSUE_ADMIN, template.getId(), adminGroup.getId()));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ISSUE_ADMIN, template.getId(), userGroup.getId()));
        // Anyone group
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), null));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ISSUE_ADMIN, template.getId(), null));
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        String response = newRequest().setParam(PARAM_PERMISSION, UserRole.ISSUE_ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute().getInput();
        assertJson(response).ignoreFields("id").withStrictArrayOrder().isSimilarTo(getClass().getResource("template_groups-example.json"));
    }

    @Test
    public void do_not_fail_when_group_name_exists_in_multiple_organizations() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        String groupName = "group-name";
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), groupName);
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.CODEVIEWER, template.getId(), group1.getId()));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ADMIN, template.getId(), group1.getId()));
        OrganizationDto otherOrganization = db.organizations().insert();
        db.users().insertGroup(otherOrganization, groupName);
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setMediaType(PROTOBUF).setParam(PARAM_TEMPLATE_ID, template.getUuid()).setParam(TEXT_QUERY, "-nam").execute();
    }

    @Test
    public void return_all_permissions_of_matching_groups() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group-1-name");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.CODEVIEWER, template.getId(), group1.getId()));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ADMIN, template.getId(), group1.getId()));
        GroupDto group2 = db.users().insertGroup(db.getDefaultOrganization(), "group-2-name");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), group2.getId()));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ADMIN, template.getId(), group2.getId()));
        GroupDto group3 = db.users().insertGroup(db.getDefaultOrganization(), "group-3-name");
        // Anyone
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), null));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ISSUE_ADMIN, template.getId(), null));
        PermissionTemplateDto anotherTemplate = addTemplateToDefaultOrganization();
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ADMIN, anotherTemplate.getId(), group3.getId()));
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        WsGroupsResponse response = newRequest().setParam(PARAM_TEMPLATE_ID, template.getUuid()).executeProtobuf(WsGroupsResponse.class);
        assertThat(response.getGroupsList()).extracting("name").containsExactly("Anyone", "group-1-name", "group-2-name", "group-3-name");
        assertThat(response.getGroups(0).getPermissionsList()).containsOnly("user", "issueadmin");
        assertThat(response.getGroups(1).getPermissionsList()).containsOnly("codeviewer", "admin");
        assertThat(response.getGroups(2).getPermissionsList()).containsOnly("user", "admin");
    }

    @Test
    public void search_by_permission() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group-1-name");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), group1.getId()));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.CODEVIEWER, template.getId(), group1.getId()));
        GroupDto group2 = db.users().insertGroup(db.getDefaultOrganization(), "group-2-name");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ADMIN, template.getId(), group2.getId()));
        GroupDto group3 = db.users().insertGroup(db.getDefaultOrganization(), "group-3-name");
        // Anyone
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), null));
        PermissionTemplateDto anotherTemplate = addTemplateToDefaultOrganization();
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ADMIN, anotherTemplate.getId(), group3.getId()));
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        WsGroupsResponse response = newRequest().setParam(PARAM_PERMISSION, UserRole.USER).setParam(PARAM_TEMPLATE_ID, template.getUuid()).executeProtobuf(WsGroupsResponse.class);
        assertThat(response.getGroupsList()).extracting("name").containsExactly("Anyone", "group-1-name");
        assertThat(response.getGroups(0).getPermissionsList()).containsOnly("user");
        assertThat(response.getGroups(1).getPermissionsList()).containsOnly("user", "codeviewer");
    }

    @Test
    public void search_by_template_name() {
        OrganizationDto defaultOrg = db.getDefaultOrganization();
        GroupDto group1 = db.users().insertGroup(defaultOrg, "group-1-name");
        GroupDto group2 = db.users().insertGroup(defaultOrg, "group-2-name");
        GroupDto group3 = db.users().insertGroup(defaultOrg, "group-3-name");
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), group1.getId()));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.ADMIN, template.getId(), group2.getId()));
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), null));
        PermissionTemplateDto anotherTemplate = addTemplateToDefaultOrganization();
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, anotherTemplate.getId(), group1.getId()));
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        WsGroupsResponse response = newRequest().setParam(PARAM_TEMPLATE_NAME, template.getName()).executeProtobuf(WsGroupsResponse.class);
        assertThat(response.getGroupsList()).extracting("name").containsExactly("Anyone", "group-1-name", "group-2-name", "group-3-name");
    }

    @Test
    public void search_with_pagination() {
        OrganizationDto defaultOrg = db.getDefaultOrganization();
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        GroupDto group1 = db.users().insertGroup(defaultOrg, "group-1-name");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), group1.getId()));
        GroupDto group2 = db.users().insertGroup(defaultOrg, "group-2-name");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), group2.getId()));
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        WsGroupsResponse response = newRequest().setParam(PARAM_PERMISSION, UserRole.USER).setParam(PARAM_TEMPLATE_NAME, template.getName()).setParam(PAGE, "2").setParam(PAGE_SIZE, "1").executeProtobuf(WsGroupsResponse.class);
        assertThat(response.getGroupsList()).extracting("name").containsExactly("group-2-name");
    }

    @Test
    public void search_with_text_query() {
        OrganizationDto defaultOrg = db.getDefaultOrganization();
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        GroupDto group1 = db.users().insertGroup(defaultOrg, "group-1-name");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), group1.getId()));
        GroupDto group2 = db.users().insertGroup(defaultOrg, "group-2-name");
        GroupDto group3 = db.users().insertGroup(defaultOrg, "group-3");
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        WsGroupsResponse response = newRequest().setParam(PARAM_TEMPLATE_NAME, template.getName()).setParam(TEXT_QUERY, "-nam").executeProtobuf(WsGroupsResponse.class);
        assertThat(response.getGroupsList()).extracting("name").containsExactly("group-1-name", "group-2-name");
    }

    @Test
    public void search_with_text_query_return_all_groups_even_when_no_permission_set() {
        OrganizationDto defaultOrg = db.getDefaultOrganization();
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        db.users().insertGroup(defaultOrg, "group-1-name");
        db.users().insertGroup(defaultOrg, "group-2-name");
        db.users().insertGroup(defaultOrg, "group-3-name");
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        WsGroupsResponse response = newRequest().setParam(PARAM_TEMPLATE_ID, template.getUuid()).setParam(TEXT_QUERY, "-name").executeProtobuf(WsGroupsResponse.class);
        assertThat(response.getGroupsList()).extracting("name").containsExactly("group-1-name", "group-2-name", "group-3-name");
        assertThat(response.getGroups(0).getPermissionsList()).isEmpty();
        assertThat(response.getGroups(1).getPermissionsList()).isEmpty();
        assertThat(response.getGroups(2).getPermissionsList()).isEmpty();
    }

    @Test
    public void search_with_text_query_return_anyone_group_even_when_no_permission_set() {
        PermissionTemplateDto template = addTemplateToDefaultOrganization();
        GroupDto group = db.users().insertGroup(db.getDefaultOrganization(), "group");
        addGroupToTemplate(TemplateGroupsActionTest.newPermissionTemplateGroup(UserRole.USER, template.getId(), group.getId()));
        commit();
        loginAsAdmin(db.getDefaultOrganization());
        WsGroupsResponse response = newRequest().setParam(PARAM_TEMPLATE_ID, template.getUuid()).setParam(TEXT_QUERY, "nyo").executeProtobuf(WsGroupsResponse.class);
        assertThat(response.getGroupsList()).extracting("name").containsExactly("Anyone");
        assertThat(response.getGroups(0).getPermissionsList()).isEmpty();
    }

    @Test
    public void fail_if_not_logged_in() {
        PermissionTemplateDto template1 = addTemplateToDefaultOrganization();
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest().setParam(PARAM_PERMISSION, UserRole.USER).setParam(PARAM_TEMPLATE_ID, template1.getUuid()).execute();
    }

    @Test
    public void fail_if_insufficient_privileges() {
        PermissionTemplateDto template1 = addTemplateToDefaultOrganization();
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        newRequest().setParam(PARAM_PERMISSION, UserRole.USER).setParam(PARAM_TEMPLATE_ID, template1.getUuid()).execute();
    }

    @Test
    public void fail_if_template_uuid_and_name_provided() {
        PermissionTemplateDto template1 = addTemplateToDefaultOrganization();
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest().setParam(PARAM_PERMISSION, UserRole.USER).setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(PARAM_TEMPLATE_NAME, template1.getName()).execute();
    }

    @Test
    public void fail_if_template_uuid_nor_name_provided() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest().setParam(PARAM_PERMISSION, UserRole.USER).execute();
    }

    @Test
    public void fail_if_template_is_not_found() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        newRequest().setParam(PARAM_PERMISSION, UserRole.USER).setParam(PARAM_TEMPLATE_ID, "unknown-uuid").execute();
    }

    @Test
    public void fail_if_not_a_project_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        PermissionTemplateDto template1 = addTemplateToDefaultOrganization();
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(PARAM_PERMISSION, QUALITY_GATE_ADMIN).setParam(PARAM_TEMPLATE_ID, template1.getUuid()).execute();
    }
}

