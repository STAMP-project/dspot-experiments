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
import UserRole.ADMIN;
import UserRole.USER;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.ProjectLinkDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.ProjectLinks.SearchWsResponse;


public class SearchActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private WsActionTester ws = new WsActionTester(new SearchAction(dbClient, userSession, TestComponentFinder.from(db)));

    @Test
    public void example() {
        ComponentDto project = db.components().insertPrivateProject();
        db.componentLinks().insertProvidedLink(project, ( l) -> l.setUuid("1").setType("homepage").setName("Homepage").setHref("http://example.org"));
        db.componentLinks().insertCustomLink(project, ( l) -> l.setUuid("2").setType("custom").setName("Custom").setHref("http://example.org/custom"));
        logInAsProjectAdministrator(project);
        String result = ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, project.getKey()).execute().getInput();
        assertJson(result).isSimilarTo(getClass().getResource("search-example.json"));
    }

    @Test
    public void request_by_project_id() {
        ComponentDto project = db.components().insertPrivateProject();
        ProjectLinkDto link = db.componentLinks().insertCustomLink(project);
        logInAsProjectAdministrator(project);
        SearchWsResponse response = callByUuid(project.uuid());
        assertThat(response.getLinksList()).extracting(Link::getId, Link::getName).containsExactlyInAnyOrder(tuple(link.getUuid(), link.getName()));
    }

    @Test
    public void request_by_project_key() {
        ComponentDto project = db.components().insertPrivateProject();
        ProjectLinkDto link = db.componentLinks().insertCustomLink(project);
        logInAsProjectAdministrator(project);
        SearchWsResponse response = callByKey(project.getKey());
        assertThat(response.getLinksList()).extracting(Link::getId, Link::getName).containsExactlyInAnyOrder(tuple(link.getUuid(), link.getName()));
    }

    @Test
    public void response_fields() {
        ComponentDto project = db.components().insertPrivateProject();
        ProjectLinkDto homepageLink = db.componentLinks().insertProvidedLink(project);
        ProjectLinkDto customLink = db.componentLinks().insertCustomLink(project);
        logInAsProjectAdministrator(project);
        SearchWsResponse response = callByKey(project.getKey());
        assertThat(response.getLinksList()).extracting(Link::getId, Link::getName, Link::getType, Link::getUrl).containsExactlyInAnyOrder(tuple(homepageLink.getUuid(), "", homepageLink.getType(), homepageLink.getHref()), tuple(customLink.getUuid(), customLink.getName(), customLink.getType(), customLink.getHref()));
    }

    @Test
    public void several_projects() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        ProjectLinkDto link1 = db.componentLinks().insertCustomLink(project1);
        ProjectLinkDto link2 = db.componentLinks().insertCustomLink(project2);
        userSession.logIn().setRoot();
        SearchWsResponse response = callByKey(project1.getKey());
        assertThat(response.getLinksList()).extracting(Link::getId, Link::getName).containsExactlyInAnyOrder(tuple(link1.getUuid(), link1.getName()));
    }

    @Test
    public void request_does_not_fail_when_link_has_no_name() {
        ComponentDto project = db.components().insertPrivateProject();
        ProjectLinkDto link = db.componentLinks().insertProvidedLink(project);
        logInAsProjectAdministrator(project);
        SearchWsResponse response = callByKey(project.getKey());
        assertThat(response.getLinksList()).extracting(Link::getId, Link::hasName).containsExactlyInAnyOrder(tuple(link.getUuid(), false));
    }

    @Test
    public void project_administrator_can_search_for_links() {
        ComponentDto project = db.components().insertPrivateProject();
        ProjectLinkDto link = db.componentLinks().insertCustomLink(project);
        userSession.logIn().addProjectPermission(ADMIN, project);
        SearchWsResponse response = callByKey(project.getKey());
        assertThat(response.getLinksList()).extracting(Link::getId, Link::getName).containsExactlyInAnyOrder(tuple(link.getUuid(), link.getName()));
    }

    @Test
    public void project_user_can_search_for_links() {
        ComponentDto project = db.components().insertPrivateProject();
        ProjectLinkDto link = db.componentLinks().insertCustomLink(project);
        userSession.logIn().addProjectPermission(USER, project);
        SearchWsResponse response = callByKey(project.getKey());
        assertThat(response.getLinksList()).extracting(Link::getId, Link::getName).containsExactlyInAnyOrder(tuple(link.getUuid(), link.getName()));
    }

    @Test
    public void fail_when_no_project() {
        expectedException.expect(NotFoundException.class);
        callByKey("unknown");
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
    public void fail_if_insufficient_privileges() {
        userSession.anonymous();
        ComponentDto project = db.components().insertPrivateProject();
        expectedException.expect(ForbiddenException.class);
        callByKey(project.getKey());
    }

    @Test
    public void fail_when_both_id_and_key_are_provided() {
        ComponentDto project = db.components().insertPrivateProject();
        logInAsProjectAdministrator(project);
        expectedException.expect(IllegalArgumentException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, project.getKey()).setParam(ProjectLinksWsParameters.PARAM_PROJECT_ID, project.uuid()).execute();
    }

    @Test
    public void fail_when_no_id_nor_key_are_provided() {
        ComponentDto project = db.components().insertPrivateProject();
        expectedException.expect(IllegalArgumentException.class);
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, project.getKey()).setParam(ProjectLinksWsParameters.PARAM_PROJECT_ID, project.uuid()).execute();
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_KEY, branch.getDbKey()).execute();
    }

    @Test
    public void fail_when_using_branch_db_uuid() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component id '%s' not found", branch.uuid()));
        ws.newRequest().setParam(ProjectLinksWsParameters.PARAM_PROJECT_ID, branch.uuid()).execute();
    }

    @Test
    public void define_search_action() {
        WebService.Action action = ws.getDef();
        assertThat(action).isNotNull();
        assertThat(action.isPost()).isFalse();
        assertThat(action.handler()).isNotNull();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).hasSize(2);
    }
}

