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
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.WsParameters;


public class AddUserToTemplateActionTest extends BasePermissionWsTest<AddUserToTemplateAction> {
    private UserDto user;

    private PermissionTemplateDto permissionTemplate;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    @Test
    public void add_user_to_template() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(user.getLogin(), permissionTemplate.getUuid(), UserRole.CODEVIEWER);
        assertThat(getLoginsInTemplateAndPermission(permissionTemplate, UserRole.CODEVIEWER)).containsExactly(user.getLogin());
    }

    @Test
    public void add_user_to_template_by_name() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).setParam(PARAM_TEMPLATE_NAME, permissionTemplate.getName().toUpperCase()).execute();
        assertThat(getLoginsInTemplateAndPermission(permissionTemplate, UserRole.CODEVIEWER)).containsExactly(user.getLogin());
    }

    @Test
    public void add_user_to_template_by_name_and_organization() {
        OrganizationDto organizationDto = db.organizations().insert();
        PermissionTemplateDto permissionTemplate = db.permissionTemplates().insertTemplate(organizationDto);
        addUserAsMemberOfOrganization(organizationDto);
        loginAsAdmin(organizationDto);
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).setParam(PARAM_TEMPLATE_NAME, permissionTemplate.getName().toUpperCase()).setParam(PARAM_ORGANIZATION, organizationDto.getKey()).execute();
        assertThat(getLoginsInTemplateAndPermission(permissionTemplate, UserRole.CODEVIEWER)).containsExactly(user.getLogin());
    }

    @Test
    public void does_not_add_a_user_twice() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(user.getLogin(), permissionTemplate.getUuid(), UserRole.ISSUE_ADMIN);
        newRequest(user.getLogin(), permissionTemplate.getUuid(), UserRole.ISSUE_ADMIN);
        assertThat(getLoginsInTemplateAndPermission(permissionTemplate, UserRole.ISSUE_ADMIN)).containsExactly(user.getLogin());
    }

    @Test
    public void fail_if_not_a_project_permission() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(user.getLogin(), permissionTemplate.getUuid(), PROVISIONING);
    }

    @Test
    public void fail_if_not_admin_of_default_organization() throws Exception {
        userSession.logIn().addPermission(ADMINISTER_QUALITY_PROFILES, db.getDefaultOrganization());
        expectedException.expect(ForbiddenException.class);
        newRequest(user.getLogin(), permissionTemplate.getUuid(), UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_user_missing() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(null, permissionTemplate.getUuid(), UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_permission_missing() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest(user.getLogin(), permissionTemplate.getUuid(), null);
    }

    @Test
    public void fail_if_template_uuid_and_name_are_missing() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest(user.getLogin(), null, UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_user_does_not_exist() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("User with login 'unknown-login' is not found");
        newRequest("unknown-login", permissionTemplate.getUuid(), UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_template_key_does_not_exist() throws Exception {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Permission template with id 'unknown-key' is not found");
        newRequest(user.getLogin(), "unknown-key", UserRole.CODEVIEWER);
    }

    @Test
    public void fail_if_organization_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("No organization with key 'Unknown'");
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).setParam(PARAM_TEMPLATE_NAME, permissionTemplate.getName().toUpperCase()).setParam(PARAM_ORGANIZATION, "Unknown").execute();
    }

    @Test
    public void fail_to_add_permission_when_user_is_not_member_of_given_organization() {
        // User is not member of given organization
        OrganizationDto otherOrganization = db.organizations().insert();
        addUserAsMemberOfOrganization(otherOrganization);
        OrganizationDto organization = db.organizations().insert(( organizationDto) -> organizationDto.setKey("Organization key"));
        PermissionTemplateDto permissionTemplate = db.permissionTemplates().insertTemplate(organization);
        loginAsAdmin(organization);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User 'user-login' is not member of organization 'Organization key'");
        newRequest().setParam(PARAM_USER_LOGIN, user.getLogin()).setParam(PARAM_PERMISSION, UserRole.CODEVIEWER).setParam(PARAM_TEMPLATE_NAME, permissionTemplate.getName().toUpperCase()).setParam(PARAM_ORGANIZATION, organization.getKey()).execute();
    }
}

