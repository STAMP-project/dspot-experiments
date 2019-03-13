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
package org.sonar.server.qualityprofile.ws;


import Qualifiers.PROJECT;
import UserRole.ADMIN;
import WebService.Action;
import WebService.Param;
import java.net.HttpURLConnection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class RemoveProjectActionTest {
    private static final String LANGUAGE_1 = "xoo";

    private static final String LANGUAGE_2 = "foo";

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private DbClient dbClient = db.getDbClient();

    private Languages languages = LanguageTesting.newLanguages(RemoveProjectActionTest.LANGUAGE_1, RemoveProjectActionTest.LANGUAGE_2);

    private QProfileWsSupport wsSupport = new QProfileWsSupport(dbClient, userSession, TestDefaultOrganizationProvider.from(db));

    private RemoveProjectAction underTest = new RemoveProjectAction(dbClient, userSession, languages, new org.sonar.server.component.ComponentFinder(dbClient, new ResourceTypesRule().setRootQualifiers(PROJECT)), wsSupport);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.since()).isEqualTo("5.2");
        assertThat(definition.isPost()).isTrue();
        assertThat(definition.key()).isEqualTo("remove_project");
        assertThat(definition.params()).extracting(WebService.Param::key).containsOnly("key", "qualityProfile", "project", "language", "projectUuid", "organization");
        WebService.Param languageParam = definition.param("language");
        assertThat(languageParam.possibleValues()).containsOnly(RemoveProjectActionTest.LANGUAGE_1, RemoveProjectActionTest.LANGUAGE_2);
        assertThat(languageParam.exampleValue()).isNull();
        assertThat(languageParam.deprecatedSince()).isNullOrEmpty();
        WebService.Param organizationParam = definition.param("organization");
        assertThat(organizationParam.since()).isEqualTo("6.4");
        assertThat(organizationParam.isInternal()).isTrue();
        WebService.Param profile = definition.param("key");
        assertThat(profile.deprecatedKey()).isEqualTo("profileKey");
        WebService.Param profileName = definition.param("qualityProfile");
        assertThat(profileName.deprecatedSince()).isNullOrEmpty();
        WebService.Param project = definition.param("project");
        assertThat(project.deprecatedKey()).isEqualTo("projectKey");
        WebService.Param projectUuid = definition.param("projectUuid");
        assertThat(projectUuid.deprecatedSince()).isEqualTo("6.5");
    }

    @Test
    public void remove_profile_from_project_in_default_organization() {
        logInAsProfileAdmin();
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        QProfileDto profileLang1 = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(LANGUAGE_1));
        QProfileDto profileLang2 = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(LANGUAGE_2));
        db.qualityProfiles().associateWithProject(project, profileLang1);
        db.qualityProfiles().associateWithProject(project, profileLang2);
        TestResponse response = call(project, profileLang1);
        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
        assertProjectIsNotAssociatedToProfile(project, profileLang1);
        assertProjectIsAssociatedToProfile(project, profileLang2);
    }

    @Test
    public void removal_does_not_fail_if_profile_is_not_associated_to_project() {
        logInAsProfileAdmin();
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        TestResponse response = call(project, profile);
        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NO_CONTENT);
        assertProjectIsNotAssociatedToProfile(project, profile);
    }

    @Test
    public void project_administrator_can_remove_profile() {
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        db.qualityProfiles().associateWithProject(project, profile);
        userSession.logIn(db.users().insertUser()).addProjectPermission(ADMIN, project);
        call(project, profile);
        assertProjectIsNotAssociatedToProfile(project, profile);
    }

    @Test
    public void as_qprofile_editor() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage(LANGUAGE_1));
        db.qualityProfiles().associateWithProject(project, profile);
        UserDto user = db.users().insertUser();
        db.qualityProfiles().addUserPermission(profile, user);
        userSession.logIn(user);
        call(project, profile);
        assertProjectIsNotAssociatedToProfile(project, profile);
    }

    @Test
    public void fail_if_not_enough_permissions() {
        userSession.logIn(db.users().insertUser());
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        call(project, profile);
    }

    @Test
    public void fail_if_not_logged_in() {
        userSession.anonymous();
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        call(project, profile);
    }

    @Test
    public void fail_if_project_does_not_exist() {
        logInAsProfileAdmin();
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Component id 'unknown' not found");
        ws.newRequest().setParam("projectUuid", "unknown").setParam("profileKey", profile.getKee()).execute();
    }

    @Test
    public void fail_if_profile_does_not_exist() {
        logInAsProfileAdmin();
        ComponentDto project = db.components().insertPrivateProject(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Quality Profile with key 'unknown' does not exist");
        ws.newRequest().setParam("projectUuid", project.uuid()).setParam("profileKey", "unknown").execute();
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        QProfileDto profile = db.qualityProfiles().insert(organization);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        ws.newRequest().setParam("project", branch.getDbKey()).setParam("profileKey", profile.getKee()).execute();
    }

    @Test
    public void fail_when_using_branch_uuid() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(ADMIN, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        QProfileDto profile = db.qualityProfiles().insert(organization);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component id '%s' not found", branch.uuid()));
        ws.newRequest().setParam("projectUuid", branch.uuid()).setParam("profileKey", profile.getKee()).execute();
    }
}

