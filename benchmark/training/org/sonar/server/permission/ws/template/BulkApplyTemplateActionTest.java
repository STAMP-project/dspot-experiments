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


import Param.TEXT_QUERY;
import Qualifiers.APP;
import Qualifiers.PROJECT;
import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.es.ProjectIndexers;
import org.sonar.server.es.TestProjectIndexers;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.permission.ws.BasePermissionWsTest;


public class BulkApplyTemplateActionTest extends BasePermissionWsTest<BulkApplyTemplateAction> {
    @Rule
    public DefaultTemplatesResolverRule defaultTemplatesResolver = DefaultTemplatesResolverRule.withoutGovernance();

    private UserDto user1;

    private UserDto user2;

    private GroupDto group1;

    private GroupDto group2;

    private OrganizationDto organization;

    private PermissionTemplateDto template1;

    private PermissionTemplateDto template2;

    private ProjectIndexers projectIndexers = new TestProjectIndexers();

    @Test
    public void bulk_apply_template_by_template_uuid() {
        // this project should not be applied the template
        OrganizationDto otherOrganization = db.organizations().insert();
        db.components().insertPrivateProject(otherOrganization);
        ComponentDto privateProject = db.components().insertPrivateProject(organization);
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).execute();
        assertTemplate1AppliedToPrivateProject(privateProject);
        assertTemplate1AppliedToPublicProject(publicProject);
    }

    @Test
    public void request_throws_NotFoundException_if_template_with_specified_name_does_not_exist_in_specified_organization() {
        OrganizationDto otherOrganization = db.organizations().insert();
        loginAsAdmin(otherOrganization);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage((((("Permission template with name '" + (template1.getName())) + "' is not found (case insensitive) in organization with key '") + (otherOrganization.getKey())) + "'"));
        newRequest().setParam(PARAM_ORGANIZATION, otherOrganization.getKey()).setParam(PARAM_TEMPLATE_NAME, template1.getName()).execute();
    }

    @Test
    public void request_throws_IAE_if_more_than_1000_projects() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'projects' can contains only 1000 values, got 1001");
        newRequest().setParam(PARAM_ORGANIZATION, organization.getKey()).setParam(PARAM_TEMPLATE_NAME, template1.getName()).setParam(PARAM_PROJECTS, StringUtils.join(Collections.nCopies(1001, "foo"), ",")).execute();
    }

    @Test
    public void bulk_apply_template_by_template_name() {
        ComponentDto privateProject = db.components().insertPrivateProject(organization);
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_ORGANIZATION, organization.getKey()).setParam(PARAM_TEMPLATE_NAME, template1.getName()).execute();
        assertTemplate1AppliedToPrivateProject(privateProject);
        assertTemplate1AppliedToPublicProject(publicProject);
    }

    @Test
    public void apply_template_by_qualifiers() {
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        ComponentDto privateProject = db.components().insertPrivateProject(organization);
        ComponentDto view = db.components().insertComponent(newView(organization));
        ComponentDto application = db.components().insertComponent(newApplication(organization));
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(PARAM_QUALIFIERS, String.join(",", PROJECT, APP)).execute();
        assertTemplate1AppliedToPrivateProject(privateProject);
        assertTemplate1AppliedToPublicProject(publicProject);
        assertTemplate1AppliedToPublicProject(application);
        assertNoPermissionOnProject(view);
    }

    @Test
    public void apply_template_by_query_on_name_and_key_public_project() {
        ComponentDto publicProjectFoundByKey = ComponentTesting.newPublicProjectDto(organization).setDbKey("sonar");
        db.components().insertProjectAndSnapshot(publicProjectFoundByKey);
        ComponentDto publicProjectFoundByName = ComponentTesting.newPublicProjectDto(organization).setName("name-sonar-name");
        db.components().insertProjectAndSnapshot(publicProjectFoundByName);
        ComponentDto projectUntouched = ComponentTesting.newPublicProjectDto(organization).setDbKey("new-sona").setName("project-name");
        db.components().insertProjectAndSnapshot(projectUntouched);
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(TEXT_QUERY, "SONAR").execute();
        assertTemplate1AppliedToPublicProject(publicProjectFoundByKey);
        assertTemplate1AppliedToPublicProject(publicProjectFoundByName);
        assertNoPermissionOnProject(projectUntouched);
    }

    @Test
    public void apply_template_by_query_on_name_and_key() {
        // partial match on key
        ComponentDto privateProjectFoundByKey = ComponentTesting.newPrivateProjectDto(organization).setDbKey("sonarqube");
        db.components().insertProjectAndSnapshot(privateProjectFoundByKey);
        ComponentDto privateProjectFoundByName = ComponentTesting.newPrivateProjectDto(organization).setName("name-sonar-name");
        db.components().insertProjectAndSnapshot(privateProjectFoundByName);
        ComponentDto projectUntouched = ComponentTesting.newPublicProjectDto(organization).setDbKey("new-sona").setName("project-name");
        db.components().insertProjectAndSnapshot(projectUntouched);
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(TEXT_QUERY, "SONAR").execute();
        assertTemplate1AppliedToPrivateProject(privateProjectFoundByKey);
        assertTemplate1AppliedToPrivateProject(privateProjectFoundByName);
        assertNoPermissionOnProject(projectUntouched);
    }

    @Test
    public void apply_template_by_project_keys() {
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        ComponentDto untouchedProject = db.components().insertPrivateProject(organization);
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(PARAM_PROJECTS, String.join(",", project1.getKey(), project2.getKey())).execute();
        assertTemplate1AppliedToPrivateProject(project1);
        assertTemplate1AppliedToPrivateProject(project2);
        assertNoPermissionOnProject(untouchedProject);
    }

    @Test
    public void apply_template_by_provisioned_only() {
        ComponentDto provisionedProject1 = db.components().insertPrivateProject(organization);
        ComponentDto provisionedProject2 = db.components().insertPrivateProject(organization);
        ComponentDto analyzedProject = db.components().insertPrivateProject(organization);
        db.components().insertSnapshot(newAnalysis(analyzedProject));
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(PARAM_ON_PROVISIONED_ONLY, "true").execute();
        assertTemplate1AppliedToPrivateProject(provisionedProject1);
        assertTemplate1AppliedToPrivateProject(provisionedProject2);
        assertNoPermissionOnProject(analyzedProject);
    }

    @Test
    public void apply_template_by_analyzed_before() {
        ComponentDto oldProject1 = db.components().insertPrivateProject(organization);
        ComponentDto oldProject2 = db.components().insertPrivateProject(organization);
        ComponentDto recentProject = db.components().insertPrivateProject(organization);
        db.components().insertSnapshot(oldProject1, ( a) -> a.setCreatedAt(parseDate("2015-02-03").getTime()));
        db.components().insertSnapshot(oldProject2, ( a) -> a.setCreatedAt(parseDate("2016-12-11").getTime()));
        db.components().insertSnapshot(recentProject, ( a) -> a.setCreatedAt(System.currentTimeMillis()));
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(PARAM_ANALYZED_BEFORE, "2017-09-07").execute();
        assertTemplate1AppliedToPrivateProject(oldProject1);
        assertTemplate1AppliedToPrivateProject(oldProject2);
        assertNoPermissionOnProject(recentProject);
    }

    @Test
    public void apply_template_by_visibility() {
        ComponentDto privateProject1 = db.components().insertPrivateProject(organization);
        ComponentDto privateProject2 = db.components().insertPrivateProject(organization);
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        loginAsAdmin(organization);
        newRequest().setParam(PARAM_TEMPLATE_ID, template1.getUuid()).setParam(PARAM_VISIBILITY, "private").execute();
        assertTemplate1AppliedToPrivateProject(privateProject1);
        assertTemplate1AppliedToPrivateProject(privateProject2);
        assertNoPermissionOnProject(publicProject);
    }

    @Test
    public void fail_if_no_template_parameter() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Template name or template id must be provided, not both.");
        newRequest().execute();
    }

    @Test
    public void fail_if_template_name_is_incorrect() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Permission template with id 'unknown-template-uuid' is not found");
        newRequest().setParam(PARAM_TEMPLATE_ID, "unknown-template-uuid").execute();
    }
}

