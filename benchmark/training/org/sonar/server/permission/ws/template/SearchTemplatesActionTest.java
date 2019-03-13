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


import Permissions.SearchTemplatesWsResponse;
import Qualifiers.APP;
import Qualifiers.PROJECT;
import Qualifiers.VIEW;
import UserRole.ADMIN;
import UserRole.CODEVIEWER;
import UserRole.ISSUE_ADMIN;
import UserRole.USER;
import org.junit.Test;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.l18n.I18nRule;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Permissions;


public class SearchTemplatesActionTest extends BasePermissionWsTest<SearchTemplatesAction> {
    private I18nRule i18n = new I18nRule();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private ResourceTypesRule resourceTypesWithViews = new ResourceTypesRule().setRootQualifiers(PROJECT, VIEW, APP);

    private ResourceTypesRule resourceTypesWithoutViews = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionServiceWithViews = new org.sonar.server.permission.PermissionServiceImpl(resourceTypesWithViews);

    private PermissionService permissionServiceWithoutViews = new org.sonar.server.permission.PermissionServiceImpl(resourceTypesWithoutViews);

    private WsActionTester underTestWithoutViews;

    @Test
    public void search_project_permissions_without_views() {
        OrganizationDto organization = db.getDefaultOrganization();
        PermissionTemplateDto projectTemplate = insertProjectTemplate(organization);
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        UserDto user3 = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup(organization);
        GroupDto group2 = db.users().insertGroup(organization);
        GroupDto group3 = db.users().insertGroup(organization);
        addUserToTemplate(projectTemplate.getId(), user1.getId(), ISSUE_ADMIN);
        addUserToTemplate(projectTemplate.getId(), user2.getId(), ISSUE_ADMIN);
        addUserToTemplate(projectTemplate.getId(), user3.getId(), ISSUE_ADMIN);
        addUserToTemplate(projectTemplate.getId(), user1.getId(), CODEVIEWER);
        addGroupToTemplate(projectTemplate.getId(), group1.getId(), ADMIN);
        addPermissionTemplateWithProjectCreator(projectTemplate.getId(), ADMIN);
        db.organizations().setDefaultTemplates(projectTemplate, null, null);
        String result = newRequest(underTestWithoutViews).execute().getInput();
        assertJson(result).withStrictArrayOrder().isSimilarTo(getClass().getResource("search_templates-example-without-views.json"));
    }

    @Test
    public void search_project_permissions_with_views() {
        OrganizationDto organization = db.getDefaultOrganization();
        PermissionTemplateDto projectTemplate = insertProjectTemplate(organization);
        PermissionTemplateDto portfoliosTemplate = insertPortfoliosTemplate(organization);
        PermissionTemplateDto applicationsTemplate = insertApplicationsTemplate(organization);
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        UserDto user3 = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup(organization);
        GroupDto group2 = db.users().insertGroup(organization);
        GroupDto group3 = db.users().insertGroup(organization);
        addUserToTemplate(projectTemplate.getId(), user1.getId(), ISSUE_ADMIN);
        addUserToTemplate(projectTemplate.getId(), user2.getId(), ISSUE_ADMIN);
        addUserToTemplate(projectTemplate.getId(), user3.getId(), ISSUE_ADMIN);
        addUserToTemplate(projectTemplate.getId(), user1.getId(), CODEVIEWER);
        addGroupToTemplate(projectTemplate.getId(), group1.getId(), ADMIN);
        addPermissionTemplateWithProjectCreator(projectTemplate.getId(), ADMIN);
        addUserToTemplate(portfoliosTemplate.getId(), user1.getId(), USER);
        addUserToTemplate(portfoliosTemplate.getId(), user2.getId(), USER);
        addGroupToTemplate(portfoliosTemplate.getId(), group1.getId(), ISSUE_ADMIN);
        addGroupToTemplate(portfoliosTemplate.getId(), group2.getId(), ISSUE_ADMIN);
        addGroupToTemplate(portfoliosTemplate.getId(), group3.getId(), ISSUE_ADMIN);
        db.organizations().setDefaultTemplates(projectTemplate, applicationsTemplate, portfoliosTemplate);
        String result = newRequest().execute().getInput();
        assertJson(result).withStrictArrayOrder().isSimilarTo(getClass().getResource("search_templates-example-with-views.json"));
    }

    @Test
    public void empty_result() {
        db.organizations().setDefaultTemplates(db.getDefaultOrganization(), "AU-Tpxb--iU5OvuD2FLy", "AU-Tpxb--iU5OvuD2FLz", "AU-TpxcA-iU5OvuD2FLx");
        String result = newRequest(wsTester).execute().getInput();
        assertJson(result).withStrictArrayOrder().ignoreFields("permissions").isSimilarTo(("{" + ((((((((((((((("  \"permissionTemplates\": []," + "  \"defaultTemplates\": [") + "    {") + "      \"templateId\": \"AU-Tpxb--iU5OvuD2FLy\",") + "      \"qualifier\": \"TRK\"") + "    },") + "    {") + "      \"templateId\": \"AU-Tpxb--iU5OvuD2FLz\",") + "      \"qualifier\": \"APP\"") + "    },") + "    {") + "      \"templateId\": \"AU-TpxcA-iU5OvuD2FLx\",") + "      \"qualifier\": \"VW\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void empty_result_without_views() {
        db.organizations().setDefaultTemplates(db.getDefaultOrganization(), "AU-Tpxb--iU5OvuD2FLy", "AU-TpxcA-iU5OvuD2FLz", "AU-TpxcA-iU5OvuD2FLx");
        String result = newRequest(underTestWithoutViews).execute().getInput();
        assertJson(result).withStrictArrayOrder().ignoreFields("permissions").isSimilarTo(("{" + ((((((("  \"permissionTemplates\": []," + "  \"defaultTemplates\": [") + "    {") + "      \"templateId\": \"AU-Tpxb--iU5OvuD2FLy\",") + "      \"qualifier\": \"TRK\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void search_by_name_in_default_organization() {
        db.organizations().setDefaultTemplates(db.permissionTemplates().insertTemplate(db.getDefaultOrganization()), null, null);
        insertProjectTemplate(db.getDefaultOrganization());
        insertPortfoliosTemplate(db.getDefaultOrganization());
        String result = newRequest(wsTester).setParam(TEXT_QUERY, "portfolio").execute().getInput();
        assertThat(result).contains("Default template for Portfolios").doesNotContain("projects").doesNotContain("developers");
    }

    @Test
    public void search_in_organization() {
        OrganizationDto org = db.organizations().insert();
        PermissionTemplateDto projectDefaultTemplate = db.permissionTemplates().insertTemplate(org);
        db.organizations().setDefaultTemplates(projectDefaultTemplate, null, null);
        PermissionTemplateDto templateInOrg = insertProjectTemplate(org);
        insertProjectTemplate(db.getDefaultOrganization());
        db.commit();
        userSession.addPermission(ADMINISTER, org);
        Permissions.SearchTemplatesWsResponse result = newRequest(underTestWithoutViews).setParam("organization", org.getKey()).executeProtobuf(SearchTemplatesWsResponse.class);
        assertThat(result.getPermissionTemplatesCount()).isEqualTo(2);
        assertThat(result.getPermissionTemplatesList()).extracting(Permissions.PermissionTemplate::getId).containsOnly(projectDefaultTemplate.getUuid(), templateInOrg.getUuid());
    }

    @Test
    public void fail_if_not_logged_in() {
        expectedException.expect(UnauthorizedException.class);
        userSession.anonymous();
        newRequest().execute();
    }

    @Test
    public void display_all_project_permissions() {
        db.organizations().setDefaultTemplates(db.permissionTemplates().insertTemplate(db.getDefaultOrganization()), null, null);
        String result = newRequest(underTestWithoutViews).execute().getInput();
        assertJson(result).withStrictArrayOrder().ignoreFields("defaultTemplates", "permissionTemplates").isSimilarTo(("{" + (((((((((((((((((((((((((((((((("  \"permissions\": [" + "    {") + "      \"key\": \"admin\",") + "      \"name\": \"Administer\",") + "      \"description\": \"Ability to access project settings and perform administration tasks. (Users will also need \\\"Browse\\\" permission)\"") + "    },") + "    {") + "      \"key\": \"codeviewer\",") + "      \"name\": \"See Source Code\",") + "      \"description\": \"Ability to view the project\\u0027s source code. (Users will also need \\\"Browse\\\" permission)\"") + "    },") + "    {") + "      \"key\": \"issueadmin\",") + "      \"name\": \"Administer Issues\",") + "      \"description\": \"Grants the permission to perform advanced editing on issues: marking an issue False Positive / Won\\u0027t Fix or changing an Issue\\u0027s severity. (Users will also need \\\"Browse\\\" permission)\"") + "    },") + "    {") + "      \"key\": \"securityhotspotadmin\",") + "      \"name\": \"Administer Security Hotspots\",") + "      \"description\": \"Detect a Vulnerability from a \\\"Security Hotspot\\\". Reject, clear, accept, reopen a \\\"Security Hotspot\\\" (users also need \\\"Browse\\\" permissions).\"") + "    },") + "    {") + "      \"key\": \"scan\",") + "      \"name\": \"Execute Analysis\",") + "      \"description\": \"Ability to execute analyses, and to get all settings required to perform the analysis, even the secured ones like the scm account password, the jira account password, and so on.\"") + "    },") + "    {") + "      \"key\": \"user\",") + "      \"name\": \"Browse\",") + "      \"description\": \"Ability to access a project, browse its measures, and create/edit issues for it.\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void display_all_project_permissions_with_views() {
        db.organizations().setDefaultTemplates(db.permissionTemplates().insertTemplate(db.getDefaultOrganization()), null, null);
        String result = newRequest().execute().getInput();
        assertJson(result).withStrictArrayOrder().ignoreFields("defaultTemplates", "permissionTemplates").isSimilarTo(("{" + (((((((((((((((((((((((((((((((("  \"permissions\": [" + "    {") + "      \"key\": \"admin\",") + "      \"name\": \"Administer\",") + "      \"description\": \"Ability to access project settings and perform administration tasks. (Users will also need \\\"Browse\\\" permission)\"") + "    },") + "    {") + "      \"key\": \"codeviewer\",") + "      \"name\": \"See Source Code\",") + "      \"description\": \"Ability to view the project\\u0027s source code. (Users will also need \\\"Browse\\\" permission)\"") + "    },") + "    {") + "      \"key\": \"issueadmin\",") + "      \"name\": \"Administer Issues\",") + "      \"description\": \"Grants the permission to perform advanced editing on issues: marking an issue False Positive / Won\\u0027t Fix or changing an Issue\\u0027s severity. (Users will also need \\\"Browse\\\" permission)\"") + "    },") + "    {") + "      \"key\": \"securityhotspotadmin\",") + "      \"name\": \"Administer Security Hotspots\",") + "      \"description\": \"Detect a Vulnerability from a \\\"Security Hotspot\\\". Reject, clear, accept, reopen a \\\"Security Hotspot\\\" (users also need \\\"Browse\\\" permissions).\"") + "    },") + "    {") + "      \"key\": \"scan\",") + "      \"name\": \"Execute Analysis\",") + "      \"description\": \"Ability to execute analyses, and to get all settings required to perform the analysis, even the secured ones like the scm account password, the jira account password, and so on.\"") + "    },") + "    {") + "      \"key\": \"user\",") + "      \"name\": \"Browse\",") + "      \"description\": \"Ability to access a project, browse its measures, and create/edit issues for it.\"") + "    }") + "  ]") + "}")));
    }
}

