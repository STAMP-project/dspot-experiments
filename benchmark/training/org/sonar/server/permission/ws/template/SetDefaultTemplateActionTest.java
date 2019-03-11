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


import Qualifiers.FILE;
import Qualifiers.PROJECT;
import org.junit.Test;
import org.sonar.db.DbClient;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.l18n.I18nRule;
import org.sonar.server.permission.ws.BasePermissionWsTest;


public class SetDefaultTemplateActionTest extends BasePermissionWsTest<SetDefaultTemplateAction> {
    private DbClient dbClient = db.getDbClient();

    private I18nRule i18n = new I18nRule();

    @Test
    public void update_project_default_template() {
        PermissionTemplateDto portfolioDefaultTemplate = db.permissionTemplates().insertTemplate(db.getDefaultOrganization());
        PermissionTemplateDto applicationDefaultTemplate = db.permissionTemplates().insertTemplate(db.getDefaultOrganization());
        db.organizations().setDefaultTemplates(db.permissionTemplates().insertTemplate(db.getDefaultOrganization()), applicationDefaultTemplate, portfolioDefaultTemplate);
        PermissionTemplateDto template = insertTemplate(db.getDefaultOrganization());
        loginAsAdmin(db.getDefaultOrganization());
        newRequest(template.getUuid(), PROJECT);
        assertDefaultTemplates(db.getDefaultOrganization(), template.getUuid(), applicationDefaultTemplate.getUuid(), portfolioDefaultTemplate.getUuid());
    }

    @Test
    public void update_project_default_template_without_qualifier_param() {
        OrganizationDto organization = db.organizations().insert();
        db.organizations().setDefaultTemplates(organization, "any-project-template-uuid", "any-view-template-uuid", null);
        PermissionTemplateDto template = insertTemplate(organization);
        loginAsAdmin(organization);
        // default value is project qualifier's value
        newRequest(template.getUuid(), null);
        assertDefaultTemplates(organization, template.getUuid(), "any-view-template-uuid", null);
    }

    @Test
    public void update_project_default_template_by_template_name() {
        OrganizationDto organization = db.organizations().insert();
        PermissionTemplateDto portfolioDefaultTemplate = db.permissionTemplates().insertTemplate(organization);
        PermissionTemplateDto applicationDefaultTemplate = db.permissionTemplates().insertTemplate(organization);
        db.organizations().setDefaultTemplates(db.permissionTemplates().insertTemplate(organization), applicationDefaultTemplate, portfolioDefaultTemplate);
        PermissionTemplateDto template = insertTemplate(organization);
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_ORGANIZATION, organization.getKey()).setParam(PARAM_TEMPLATE_NAME, template.getName().toUpperCase()).execute();
        db.getSession().commit();
        assertDefaultTemplates(organization, template.getUuid(), applicationDefaultTemplate.getUuid(), portfolioDefaultTemplate.getUuid());
    }

    @Test
    public void update_view_default_template() {
        OrganizationDto organization = db.organizations().insert();
        PermissionTemplateDto projectDefaultTemplate = db.permissionTemplates().insertTemplate(organization);
        db.organizations().setDefaultTemplates(projectDefaultTemplate, null, null);
        PermissionTemplateDto template = insertTemplate(organization);
        loginAsAdmin(organization);
        newRequest(template.getUuid(), VIEW);
        assertDefaultTemplates(organization, projectDefaultTemplate.getUuid(), null, template.getUuid());
    }

    @Test
    public void update_app_default_template() {
        OrganizationDto organization = db.organizations().insert();
        PermissionTemplateDto projectDefaultTemplate = db.permissionTemplates().insertTemplate(organization);
        db.organizations().setDefaultTemplates(projectDefaultTemplate, null, null);
        PermissionTemplateDto template = insertTemplate(organization);
        loginAsAdmin(organization);
        newRequest(template.getUuid(), APP);
        assertDefaultTemplates(organization, projectDefaultTemplate.getUuid(), template.getUuid(), null);
    }

    @Test
    public void fail_if_anonymous() {
        OrganizationDto organization = db.organizations().insert();
        PermissionTemplateDto template = insertTemplate(organization);
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest(template.getUuid(), PROJECT);
    }

    @Test
    public void fail_if_not_admin() {
        OrganizationDto organization = db.organizations().insert();
        PermissionTemplateDto template = insertTemplate(organization);
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        newRequest(template.getUuid(), null);
    }

    @Test
    public void fail_if_template_not_provided() {
        expectedException.expect(BadRequestException.class);
        newRequest(null, PROJECT);
    }

    @Test
    public void fail_if_template_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        newRequest("unknown-template-uuid", PROJECT);
    }

    @Test
    public void fail_if_qualifier_is_not_root() {
        OrganizationDto organization = db.organizations().insert();
        PermissionTemplateDto template = insertTemplate(organization);
        loginAsAdmin(organization);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value of parameter 'qualifier' (FIL) must be one of: [APP, TRK, VW]");
        newRequest(template.getUuid(), FILE);
    }

    @Test
    public void fail_if_organization_has_no_default_templates() {
        OrganizationDto organization = db.organizations().insert();
        PermissionTemplateDto template = insertTemplate(organization);
        loginAsAdmin(organization);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage((("No Default templates for organization with uuid '" + (organization.getUuid())) + "'"));
        newRequest(template.getUuid(), null);
    }
}

