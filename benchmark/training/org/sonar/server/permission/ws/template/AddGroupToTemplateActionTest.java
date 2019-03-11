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
import UserRole.ADMIN;
import org.junit.Test;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.web.UserRole;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.GroupDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.WsParameters;


public class AddGroupToTemplateActionTest extends BasePermissionWsTest<AddGroupToTemplateAction> {
    private PermissionTemplateDto template;

    private GroupDto group;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    @Test
    public void add_group_to_template() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(group.getName(), template.getUuid(), UserRole.CODEVIEWER);
        assertThat(getGroupNamesInTemplateAndPermission(template, UserRole.CODEVIEWER)).containsExactly(group.getName());
    }

    @Test
    public void add_group_to_template_by_name() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_GROUP_NAME, group.getName()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).setParam(PARAM_TEMPLATE_NAME, template.getName().toUpperCase()).execute();
        assertThat(getGroupNamesInTemplateAndPermission(template, UserRole.CODEVIEWER)).containsExactly(group.getName());
    }

    @Test
    public void add_with_group_id() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_TEMPLATE_ID, template.getUuid()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).setParam(PARAM_GROUP_ID, String.valueOf(group.getId())).execute();
        assertThat(getGroupNamesInTemplateAndPermission(template, UserRole.CODEVIEWER)).containsExactly(group.getName());
    }

    @Test
    public void does_not_add_a_group_twice() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(group.getName(), template.getUuid(), UserRole.ISSUE_ADMIN);
        newRequest(group.getName(), template.getUuid(), UserRole.ISSUE_ADMIN);
        assertThat(getGroupNamesInTemplateAndPermission(template, UserRole.ISSUE_ADMIN)).containsExactly(group.getName());
    }

    @Test
    public void add_anyone_group_to_template() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(ANYONE, template.getUuid(), UserRole.CODEVIEWER);
        assertThat(getGroupNamesInTemplateAndPermission(template, UserRole.CODEVIEWER)).containsExactly(ANYONE);
    }

    @Test
    public void fail_if_add_anyone_group_to_admin_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(String.format("It is not possible to add the '%s' permission to the group 'Anyone'", ADMIN));
        newRequest(ANYONE, template.getUuid(), UserRole.ADMIN);
    }

    @Test
    public void fail_if_not_a_project_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(group.getName(), template.getUuid(), PROVISIONING);
    }

    @Test
    public void fail_if_not_admin_of_default_organization() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        newRequest(group.getName(), template.getUuid(), UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_group_params_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest(null, template.getUuid(), UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_permission_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(group.getName(), template.getUuid(), null);
    }

    @Test
    public void fail_if_template_uuid_and_name_missing() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest(group.getName(), null, UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_group_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("No group with name 'unknown-group-name'");
        newRequest("unknown-group-name", template.getUuid(), UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_template_key_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Permission template with id 'unknown-key' is not found");
        newRequest(group.getName(), "unknown-key", UserRole.CODEVIEWER);
    }
}

