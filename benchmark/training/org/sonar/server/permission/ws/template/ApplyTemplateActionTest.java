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


import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.es.TestProjectIndexers;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.permission.PermissionTemplateService;
import org.sonar.server.permission.ws.BasePermissionWsTest;


public class ApplyTemplateActionTest extends BasePermissionWsTest<ApplyTemplateAction> {
    @Rule
    public DefaultTemplatesResolverRule defaultTemplatesResolver = DefaultTemplatesResolverRule.withoutGovernance();

    private UserDto user1;

    private UserDto user2;

    private GroupDto group1;

    private GroupDto group2;

    private ComponentDto project;

    private PermissionTemplateDto template1;

    private PermissionTemplateDto template2;

    private PermissionTemplateService permissionTemplateService = new PermissionTemplateService(db.getDbClient(), new TestProjectIndexers(), userSession, defaultTemplatesResolver);

    @Test
    public void apply_template_with_project_uuid() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(template1.getUuid(), project.uuid(), null);
        assertTemplate1AppliedToProject();
    }

    @Test
    public void apply_template_with_project_uuid_by_template_name() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_TEMPLATE_NAME, template1.getName().toUpperCase()).setParam(PARAM_PROJECT_ID, project.uuid()).execute();
        assertTemplate1AppliedToProject();
    }

    @Test
    public void apply_template_with_project_key() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(template1.getUuid(), null, project.getDbKey());
        assertTemplate1AppliedToProject();
    }

    @Test
    public void fail_when_unknown_template() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Permission template with id 'unknown-template-uuid' is not found");
        newRequest("unknown-template-uuid", project.uuid(), null);
    }

    @Test
    public void fail_when_unknown_project_uuid() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Project id 'unknown-project-uuid' not found");
        newRequest(template1.getUuid(), "unknown-project-uuid", null);
    }

    @Test
    public void fail_when_unknown_project_key() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Project key 'unknown-project-key' not found");
        newRequest(template1.getUuid(), null, "unknown-project-key");
    }

    @Test
    public void fail_when_template_is_not_provided() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        newRequest(null, project.uuid(), null);
    }

    @Test
    public void fail_when_project_uuid_and_key_not_provided() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Project id or project key can be provided, not both.");
        newRequest(template1.getUuid(), null, null);
    }

    @Test
    public void fail_when_not_admin_of_organization() {
        userSession.logIn().addPermission(ADMINISTER, "otherOrg");
        expectedException.expect(ForbiddenException.class);
        newRequest(template1.getUuid(), project.uuid(), null);
    }
}

