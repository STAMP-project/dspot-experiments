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
import org.sonar.db.user.GroupDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.WsParameters;


public class RemoveGroupFromTemplateActionTest extends BasePermissionWsTest<RemoveGroupFromTemplateAction> {
    private static final String PERMISSION = UserRole.CODEVIEWER;

    private GroupDto group;

    private PermissionTemplateDto template;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    @Test
    public void remove_group_from_template() {
        newRequest(group.getName(), template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
        assertThat(getGroupNamesInTemplateAndPermission(template, RemoveGroupFromTemplateActionTest.PERMISSION)).isEmpty();
    }

    @Test
    public void remove_group_from_template_by_name_case_insensitive() {
        newRequest().setParam(PARAM_GROUP_NAME, group.getName()).setParam(PARAM_PERMISSION, RemoveGroupFromTemplateActionTest.PERMISSION).setParam(PARAM_TEMPLATE_NAME, template.getName().toUpperCase()).execute();
        assertThat(getGroupNamesInTemplateAndPermission(template, RemoveGroupFromTemplateActionTest.PERMISSION)).isEmpty();
    }

    @Test
    public void remove_group_with_group_id() {
        newRequest().setParam(PARAM_TEMPLATE_ID, template.getUuid()).setParam(PARAM_PERMISSION, RemoveGroupFromTemplateActionTest.PERMISSION).setParam(PARAM_GROUP_ID, String.valueOf(group.getId())).execute();
        assertThat(getGroupNamesInTemplateAndPermission(template, RemoveGroupFromTemplateActionTest.PERMISSION)).isEmpty();
    }

    @Test
    public void remove_group_twice_without_error() {
        newRequest(group.getName(), template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
        newRequest(group.getName(), template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
        assertThat(getGroupNamesInTemplateAndPermission(template, RemoveGroupFromTemplateActionTest.PERMISSION)).isEmpty();
    }

    @Test
    public void remove_anyone_group_from_template() {
        addGroupToTemplate(template, null, RemoveGroupFromTemplateActionTest.PERMISSION);
        newRequest(ANYONE, template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
        assertThat(getGroupNamesInTemplateAndPermission(template, RemoveGroupFromTemplateActionTest.PERMISSION)).containsExactly(group.getName());
    }

    @Test
    public void fail_if_not_a_project_permission() {
        expectedException.expect(IllegalArgumentException.class);
        newRequest(group.getName(), template.getUuid(), PROVISIONING);
    }

    @Test
    public void fail_if_insufficient_privileges() {
        userSession.logIn().addPermission(SCAN, db.getDefaultOrganization());
        expectedException.expect(ForbiddenException.class);
        newRequest(group.getName(), template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
    }

    @Test
    public void fail_if_not_logged_in() {
        expectedException.expect(UnauthorizedException.class);
        userSession.anonymous();
        newRequest(group.getName(), template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
    }

    @Test
    public void fail_if_group_params_missing() {
        expectedException.expect(BadRequestException.class);
        newRequest(null, template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
    }

    @Test
    public void fail_if_permission_missing() {
        expectedException.expect(IllegalArgumentException.class);
        newRequest(group.getName(), template.getUuid(), null);
    }

    @Test
    public void fail_if_template_missing() {
        expectedException.expect(BadRequestException.class);
        newRequest(group.getName(), null, RemoveGroupFromTemplateActionTest.PERMISSION);
    }

    @Test
    public void fail_if_group_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("No group with name 'unknown-group-name'");
        newRequest("unknown-group-name", template.getUuid(), RemoveGroupFromTemplateActionTest.PERMISSION);
    }

    @Test
    public void fail_if_template_key_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Permission template with id 'unknown-key' is not found");
        newRequest(group.getName(), "unknown-key", RemoveGroupFromTemplateActionTest.PERMISSION);
    }
}

