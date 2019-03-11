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
package org.sonar.server.projectlink.ws;


import System2.INSTANCE;
import UserRole.USER;
import WebService.Action;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class CreateActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private WsActionTester ws = new WsActionTester(new CreateAction(dbClient, userSession, TestComponentFinder.from(db), UuidFactoryFast.getInstance()));

    @Test
    public void example_with_key() {
        ComponentDto project = db.components().insertPrivateProject();
        logInAsProjectAdministrator(project);
        String result = ws.newRequest().setMethod("POST").setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, project.getDbKey()).setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute().getInput();
        assertJson(result).ignoreFields("id").isSimilarTo(getClass().getResource("create-example.json"));
    }

    @Test
    public void example_with_id() {
        ComponentDto project = db.components().insertPrivateProject();
        logInAsProjectAdministrator(project);
        String result = ws.newRequest().setMethod("POST").setParam(ProjectLinksWsParameters.PARAM_PROJECT_ID, project.uuid()).setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute().getInput();
        assertJson(result).ignoreFields("id").isSimilarTo(getClass().getResource("create-example.json"));
    }

    @Test
    public void require_project_admin() {
        ComponentDto project = db.components().insertPrivateProject();
        logInAsProjectAdministrator(project);
        createAndTest(project);
    }

    @Test
    public void with_long_name() {
        ComponentDto project = db.components().insertPrivateProject();
        logInAsProjectAdministrator(project);
        String longName = StringUtils.leftPad("", 60, "a");
        String expectedType = StringUtils.leftPad("", 20, "a");
        createAndTest(project, longName, "http://example.org", expectedType);
    }

    @Test
    public void fail_if_no_name() {
        expectedException.expect(IllegalArgumentException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, "unknown").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute();
    }

    @Test
    public void fail_if_long_name() {
        expectedException.expect(IllegalArgumentException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, "unknown").setParam(ProjectLinksWsParameters.PARAM_NAME, StringUtils.leftPad("", 129, "*")).setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute();
    }

    @Test
    public void fail_if_no_url() {
        expectedException.expect(IllegalArgumentException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, "unknown").setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").execute();
    }

    @Test
    public void fail_if_long_url() {
        expectedException.expect(IllegalArgumentException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, "unknown").setParam(ProjectLinksWsParameters.PARAM_NAME, "random").setParam(ProjectLinksWsParameters.PARAM_URL, StringUtils.leftPad("", 2049, "*")).execute();
    }

    @Test
    public void fail_when_no_project() {
        expectedException.expect(NotFoundException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, "unknown").setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute();
    }

    @Test
    public void fail_if_anonymous() {
        userSession.anonymous();
        ComponentDto project = db.components().insertPublicProject();
        userSession.registerComponents(project);
        expectedException.expect(ForbiddenException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, project.getKey()).setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute();
    }

    @Test
    public void fail_if_not_project_admin() {
        userSession.logIn();
        ComponentDto project = db.components().insertPrivateProject();
        expectedException.expect(ForbiddenException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, project.getKey()).setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute();
    }

    @Test
    public void fail_if_module() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        failIfNotAProject(project, module);
    }

    @Test
    public void fail_if_directory() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(project, "A/B"));
        failIfNotAProject(project, directory);
    }

    @Test
    public void fail_if_file() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        failIfNotAProject(project, file);
    }

    @Test
    public void fail_if_view() {
        ComponentDto view = db.components().insertView();
        failIfNotAProject(view, view);
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, branch.getDbKey()).setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute();
    }

    @Test
    public void fail_when_using_branch_db_uuid() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component id '%s' not found", branch.uuid()));
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_ID, branch.uuid()).setParam(ProjectLinksWsParameters.PARAM_NAME, "Custom").setParam(ProjectLinksWsParameters.PARAM_URL, "http://example.org").execute();
    }

    @Test
    public void define_create_action() {
        WebService.Action action = ws.getDef();
        assertThat(action).isNotNull();
        assertThat(action.isPost()).isTrue();
        assertThat(action.handler()).isNotNull();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).hasSize(4);
    }
}

