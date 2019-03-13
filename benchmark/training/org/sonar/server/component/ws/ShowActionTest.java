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
package org.sonar.server.component.ws;


import System2.INSTANCE;
import UserRole.USER;
import WebService.Action;
import WebService.Param;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Components.ShowWsResponse;


public class ShowActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private WsActionTester ws = new WsActionTester(new ShowAction(userSession, db.getDbClient(), TestComponentFinder.from(db)));

    @Test
    public void verify_definition() {
        WebService.Action action = ws.getDef();
        assertThat(action.since()).isEqualTo("5.4");
        assertThat(action.description()).isNotNull();
        assertThat(action.responseExample()).isNotNull();
        assertThat(action.changelog()).extracting(Change::getVersion, Change::getDescription).containsExactlyInAnyOrder(tuple("6.4", "Analysis date has been added to the response"), tuple("6.4", "The field 'id' is deprecated in the response"), tuple("6.4", "The 'visibility' field is added to the response"), tuple("6.5", "Leak period date is added to the response"), tuple("6.6", "'branch' is added to the response"), tuple("6.6", "'version' is added to the response"), tuple("7.6", "The use of module keys in parameter 'component' is deprecated"));
        assertThat(action.params()).extracting(WebService.Param::key).containsExactlyInAnyOrder("component", "componentId", "branch", "pullRequest");
        WebService.Param componentId = action.param(PARAM_COMPONENT_ID);
        assertThat(componentId.isRequired()).isFalse();
        assertThat(componentId.description()).isNotNull();
        assertThat(componentId.exampleValue()).isNotNull();
        assertThat(componentId.deprecatedSince()).isEqualTo("6.4");
        assertThat(componentId.deprecatedKey()).isEqualTo("id");
        assertThat(componentId.deprecatedKeySince()).isEqualTo("6.4");
        WebService.Param component = action.param(PARAM_COMPONENT);
        assertThat(component.isRequired()).isFalse();
        assertThat(component.description()).isNotNull();
        assertThat(component.exampleValue()).isNotNull();
        assertThat(component.deprecatedKey()).isEqualTo("key");
        assertThat(component.deprecatedKeySince()).isEqualTo("6.4");
        WebService.Param branch = action.param(PARAM_BRANCH);
        assertThat(branch.isInternal()).isTrue();
        assertThat(branch.isRequired()).isFalse();
        assertThat(branch.since()).isEqualTo("6.6");
        WebService.Param pullRequest = action.param(PARAM_PULL_REQUEST);
        assertThat(pullRequest.isInternal()).isTrue();
        assertThat(pullRequest.isRequired()).isFalse();
        assertThat(pullRequest.since()).isEqualTo("7.1");
    }

    @Test
    public void json_example() {
        userSession.logIn().setRoot();
        insertJsonExampleComponentsAndSnapshots();
        String response = ws.newRequest().setParam("id", "AVIF-FffA3Ax6PH2efPD").execute().getInput();
        assertJson(response).isSimilarTo(getClass().getResource("show-example.json"));
    }

    @Test
    public void tags_displayed_only_for_project() {
        userSession.logIn().setRoot();
        insertJsonExampleComponentsAndSnapshots();
        String response = ws.newRequest().setParam("id", "AVIF-FffA3Ax6PH2efPD").execute().getInput();
        assertThat(response).containsOnlyOnce("\"tags\"");
    }

    @Test
    public void show_with_browse_permission() {
        ComponentDto project = newPrivateProjectDto(db.organizations().insert(), "project-uuid");
        db.components().insertProjectAndSnapshot(project);
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ShowWsResponse response = newRequest("project-uuid", null);
        assertThat(response.getComponent().getId()).isEqualTo("project-uuid");
    }

    @Test
    public void show_provided_project() {
        userSession.logIn().setRoot();
        db.components().insertComponent(newPrivateProjectDto(db.organizations().insert(), "project-uuid"));
        ShowWsResponse response = newRequest("project-uuid", null);
        assertThat(response.getComponent().getId()).isEqualTo("project-uuid");
        assertThat(response.getComponent().hasAnalysisDate()).isFalse();
        assertThat(response.getComponent().hasLeakPeriodDate()).isFalse();
    }

    @Test
    public void show_with_ancestors_when_not_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(newModuleDto(project));
        ComponentDto directory = db.components().insertComponent(newDirectory(module, "dir"));
        ComponentDto file = db.components().insertComponent(newFileDto(directory));
        userSession.addProjectPermission(UserRole.USER, project);
        ShowWsResponse response = newRequest(null, file.getDbKey());
        assertThat(response.getComponent().getKey()).isEqualTo(file.getDbKey());
        assertThat(response.getAncestorsList()).extracting(Component::getKey).containsOnly(directory.getDbKey(), module.getDbKey(), project.getDbKey());
    }

    @Test
    public void show_without_ancestors_when_project() {
        ComponentDto project = db.components().insertPrivateProject();
        db.components().insertComponent(newModuleDto(project));
        userSession.addProjectPermission(UserRole.USER, project);
        ShowWsResponse response = newRequest(null, project.getDbKey());
        assertThat(response.getComponent().getKey()).isEqualTo(project.getDbKey());
        assertThat(response.getAncestorsList()).isEmpty();
    }

    @Test
    public void show_with_last_analysis_date() {
        ComponentDto project = db.components().insertPrivateProject();
        db.components().insertSnapshots(newAnalysis(project).setCreatedAt(1000000000L).setLast(false), newAnalysis(project).setCreatedAt(2000000000L).setLast(false), newAnalysis(project).setCreatedAt(3000000000L).setLast(true));
        userSession.addProjectPermission(UserRole.USER, project);
        ShowWsResponse response = newRequest(null, project.getDbKey());
        assertThat(response.getComponent().getAnalysisDate()).isNotEmpty().isEqualTo(formatDateTime(new Date(3000000000L)));
    }

    @Test
    public void show_with_leak_period_date() {
        ComponentDto project = db.components().insertPrivateProject();
        db.components().insertSnapshots(newAnalysis(project).setPeriodDate(1000000000L).setLast(false), newAnalysis(project).setPeriodDate(2000000000L).setLast(false), newAnalysis(project).setPeriodDate(3000000000L).setLast(true));
        userSession.addProjectPermission(UserRole.USER, project);
        ShowWsResponse response = newRequest(null, project.getDbKey());
        assertThat(response.getComponent().getLeakPeriodDate()).isNotEmpty().isEqualTo(formatDateTime(new Date(3000000000L)));
    }

    @Test
    public void show_with_ancestors_and_analysis_date() {
        ComponentDto project = db.components().insertPrivateProject();
        db.components().insertSnapshot(newAnalysis(project).setCreatedAt(3000000000L).setLast(true));
        ComponentDto module = db.components().insertComponent(newModuleDto(project));
        ComponentDto directory = db.components().insertComponent(newDirectory(module, "dir"));
        ComponentDto file = db.components().insertComponent(newFileDto(directory));
        userSession.addProjectPermission(UserRole.USER, project);
        ShowWsResponse response = newRequest(null, file.getDbKey());
        String expectedDate = formatDateTime(new Date(3000000000L));
        assertThat(response.getAncestorsList()).extracting(Component::getAnalysisDate).containsOnly(expectedDate, expectedDate, expectedDate);
    }

    @Test
    public void should_return_visibility_for_private_project() {
        userSession.logIn().setRoot();
        ComponentDto privateProject = db.components().insertPrivateProject();
        ShowWsResponse result = newRequest(null, privateProject.getDbKey());
        assertThat(result.getComponent().hasVisibility()).isTrue();
        assertThat(result.getComponent().getVisibility()).isEqualTo("private");
    }

    @Test
    public void should_return_visibility_for_public_project() {
        userSession.logIn().setRoot();
        ComponentDto publicProject = db.components().insertPublicProject();
        ShowWsResponse result = newRequest(null, publicProject.getDbKey());
        assertThat(result.getComponent().hasVisibility()).isTrue();
        assertThat(result.getComponent().getVisibility()).isEqualTo("public");
    }

    @Test
    public void should_return_visibility_for_view() {
        userSession.logIn().setRoot();
        ComponentDto view = db.components().insertView();
        ShowWsResponse result = newRequest(null, view.getDbKey());
        assertThat(result.getComponent().hasVisibility()).isTrue();
    }

    @Test
    public void should_not_return_visibility_for_module() {
        userSession.logIn().setRoot();
        ComponentDto privateProject = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(newModuleDto(privateProject));
        ShowWsResponse result = newRequest(null, module.getDbKey());
        assertThat(result.getComponent().hasVisibility()).isFalse();
    }

    @Test
    public void display_version() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(newModuleDto(project));
        ComponentDto directory = db.components().insertComponent(newDirectory(module, "dir"));
        ComponentDto file = db.components().insertComponent(newFileDto(directory));
        db.components().insertSnapshot(project, ( s) -> s.setCodePeriodVersion("1.1"));
        userSession.addProjectPermission(UserRole.USER, project);
        ShowWsResponse response = newRequest(null, file.getDbKey());
        assertThat(response.getComponent().getVersion()).isEqualTo("1.1");
        assertThat(response.getAncestorsList()).extracting(Component::getVersion).containsOnly("1.1");
    }

    @Test
    public void branch() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        String branchKey = "my_branch";
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey(branchKey));
        ComponentDto module = db.components().insertComponent(newModuleDto(branch));
        ComponentDto directory = db.components().insertComponent(newDirectory(module, "dir"));
        ComponentDto file = db.components().insertComponent(newFileDto(directory));
        db.components().insertSnapshot(branch, ( s) -> s.setCodePeriodVersion("1.1"));
        ShowWsResponse response = ws.newRequest().setParam(PARAM_COMPONENT, file.getKey()).setParam(PARAM_BRANCH, branchKey).executeProtobuf(ShowWsResponse.class);
        assertThat(response.getComponent()).extracting(Component::getKey, Component::getBranch, Component::getVersion).containsExactlyInAnyOrder(file.getKey(), branchKey, "1.1");
        assertThat(response.getAncestorsList()).extracting(Component::getKey, Component::getBranch, Component::getVersion).containsExactlyInAnyOrder(tuple(directory.getKey(), branchKey, "1.1"), tuple(module.getKey(), branchKey, "1.1"), tuple(branch.getKey(), branchKey, "1.1"));
    }

    @Test
    public void pull_request() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        String pullRequest = "pr-1234";
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey(pullRequest).setBranchType(PULL_REQUEST));
        ComponentDto module = db.components().insertComponent(newModuleDto(branch));
        ComponentDto directory = db.components().insertComponent(newDirectory(module, "dir"));
        ComponentDto file = db.components().insertComponent(newFileDto(directory));
        db.components().insertSnapshot(branch, ( s) -> s.setCodePeriodVersion("1.1"));
        ShowWsResponse response = ws.newRequest().setParam(PARAM_COMPONENT, file.getKey()).setParam(PARAM_PULL_REQUEST, pullRequest).executeProtobuf(ShowWsResponse.class);
        assertThat(response.getComponent()).extracting(Component::getKey, Component::getPullRequest, Component::getVersion).containsExactlyInAnyOrder(file.getKey(), pullRequest, "1.1");
        assertThat(response.getAncestorsList()).extracting(Component::getKey, Component::getPullRequest, Component::getVersion).containsExactlyInAnyOrder(tuple(directory.getKey(), pullRequest, "1.1"), tuple(module.getKey(), pullRequest, "1.1"), tuple(branch.getKey(), pullRequest, "1.1"));
    }

    @Test
    public void throw_ForbiddenException_if_user_doesnt_have_browse_permission_on_project() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        db.components().insertProjectAndSnapshot(newPrivateProjectDto(db.organizations().insert(), "project-uuid"));
        newRequest("project-uuid", null);
    }

    @Test
    public void fail_if_component_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Component id 'unknown-uuid' not found");
        newRequest("unknown-uuid", null);
    }

    @Test
    public void fail_if_component_is_removed() {
        userSession.logIn().setRoot();
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(db.getDefaultOrganization()));
        db.components().insertComponent(newFileDto(project).setDbKey("file-key").setEnabled(false));
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Component key 'file-key' not found");
        newRequest(null, "file-key");
    }

    @Test
    public void fail_when_componentId_and_branch_params_are_used_together() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch"));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Parameter 'componentId' cannot be used at the same time as 'branch' or 'pullRequest'");
        ws.newRequest().setParam(PARAM_COMPONENT_ID, branch.uuid()).setParam(PARAM_BRANCH, "my_branch").execute();
    }

    @Test
    public void fail_if_branch_does_not_exist() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        userSession.addProjectPermission(USER, project);
        db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch"));
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component '%s' on branch '%s' not found", file.getKey(), "another_branch"));
        ws.newRequest().setParam(PARAM_COMPONENT, file.getKey()).setParam(PARAM_BRANCH, "another_branch").execute();
    }

    @Test
    public void fail_when_using_branch_db_key() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        ws.newRequest().setParam(PARAM_COMPONENT, branch.getDbKey()).executeProtobuf(ShowWsResponse.class);
    }

    @Test
    public void fail_when_using_branch_uuid() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component id '%s' not found", branch.uuid()));
        ws.newRequest().setParam(PARAM_COMPONENT_ID, branch.uuid()).executeProtobuf(ShowWsResponse.class);
    }
}

