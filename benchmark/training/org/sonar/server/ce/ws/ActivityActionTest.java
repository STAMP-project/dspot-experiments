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
package org.sonar.server.ce.ws;


import CeTaskTypes.REPORT;
import MediaTypes.JSON;
import MediaTypes.PROTOBUF;
import Param.PAGE_SIZE;
import Param.TEXT_QUERY;
import System2.INSTANCE;
import UserRole.ADMIN;
import UserRole.USER;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.taskprocessor.CeTaskProcessor;
import org.sonar.db.DbTester;
import org.sonar.db.ce.CeActivityDto;
import org.sonar.db.ce.CeQueueDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;
import org.sonar.test.JsonAssert;
import org.sonarqube.ws.Ce;
import org.sonarqube.ws.Ce.ActivityResponse;
import org.sonarqube.ws.Ce.Task;


public class ActivityActionTest {
    private static final long EXECUTED_AT = INSTANCE.now();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private TaskFormatter formatter = new TaskFormatter(db.getDbClient(), System2.INSTANCE);

    private ActivityAction underTest = new ActivityAction(userSession, db.getDbClient(), formatter, new CeTaskProcessor[]{ Mockito.mock(CeTaskProcessor.class) });

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void get_all_past_activity() {
        logInAsSystemAdministrator();
        OrganizationDto org1 = db.organizations().insert();
        ComponentDto project1 = db.components().insertPrivateProject(org1);
        OrganizationDto org2 = db.organizations().insert();
        ComponentDto project2 = db.components().insertPrivateProject(org2);
        SnapshotDto analysisProject1 = db.components().insertSnapshot(project1);
        insertActivity("T1", project1, SUCCESS, analysisProject1);
        insertActivity("T2", project2, FAILED, null);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam(CeWsParameters.PARAM_MAX_EXECUTED_AT, formatDateTime(((ActivityActionTest.EXECUTED_AT) + 2000))));
        assertThat(activityResponse.getTasksCount()).isEqualTo(2);
        // chronological order, from newest to oldest
        Task task = activityResponse.getTasks(0);
        assertThat(task.getOrganization()).isEqualTo(org2.getKey());
        assertThat(task.getId()).isEqualTo("T2");
        assertThat(task.getStatus()).isEqualTo(Ce.TaskStatus.FAILED);
        assertThat(task.getComponentId()).isEqualTo(project2.uuid());
        assertThat(task.hasAnalysisId()).isFalse();
        assertThat(task.getExecutionTimeMs()).isEqualTo(500L);
        assertThat(task.getLogs()).isFalse();
        assertThat(task.getWarningCount()).isZero();
        task = activityResponse.getTasks(1);
        assertThat(task.getId()).isEqualTo("T1");
        assertThat(task.getStatus()).isEqualTo(Ce.TaskStatus.SUCCESS);
        assertThat(task.getComponentId()).isEqualTo(project1.uuid());
        assertThat(task.getLogs()).isFalse();
        assertThat(task.getOrganization()).isEqualTo(org1.getKey());
        assertThat(task.getWarningCount()).isZero();
    }

    @Test
    public void filter_by_status() {
        logInAsSystemAdministrator();
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        insertActivity("T1", project1, SUCCESS);
        insertActivity("T2", project2, FAILED);
        insertQueue("T3", project1, IN_PROGRESS);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam("status", "FAILED,IN_PROGRESS"));
        assertThat(activityResponse.getTasksCount()).isEqualTo(2);
        assertThat(activityResponse.getTasks(0).getId()).isEqualTo("T3");
        assertThat(activityResponse.getTasks(1).getId()).isEqualTo("T2");
    }

    @Test
    public void filter_by_max_executed_at_exclude() {
        logInAsSystemAdministrator();
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        insertActivity("T1", project1, SUCCESS);
        insertActivity("T2", project2, FAILED);
        insertQueue("T3", project1, IN_PROGRESS);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam("status", "FAILED,IN_PROGRESS,SUCCESS").setParam(CeWsParameters.PARAM_MAX_EXECUTED_AT, "2016-02-15"));
        assertThat(activityResponse.getTasksCount()).isEqualTo(0);
    }

    @Test
    public void filter_by_min_submitted_and_max_executed_at_include_day() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertPrivateProject();
        insertActivity("T1", project, SUCCESS);
        String today = formatDate(new Date(ActivityActionTest.EXECUTED_AT));
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam(CeWsParameters.PARAM_MIN_SUBMITTED_AT, today).setParam(CeWsParameters.PARAM_MAX_EXECUTED_AT, today));
        assertThat(activityResponse.getTasksCount()).isEqualTo(1);
    }

    @Test
    public void filter_on_current_activities() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertPrivateProject();
        // T2 is the current activity (the most recent one)
        insertActivity("T1", project, SUCCESS);
        insertActivity("T2", project, FAILED);
        insertQueue("T3", project, PENDING);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam("onlyCurrents", "true"));
        assertThat(activityResponse.getTasksCount()).isEqualTo(1);
        assertThat(activityResponse.getTasks(0).getId()).isEqualTo("T2");
    }

    @Test
    public void task_without_project() {
        logInAsSystemAdministrator();
        insertQueue("T3", null, PENDING);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam("status", "PENDING"));
        assertThat(activityResponse.getTasksList()).hasSize(1);
    }

    @Test
    public void limit_results() {
        logInAsSystemAdministrator();
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        insertActivity("T1", project1, SUCCESS);
        insertActivity("T2", project2, FAILED);
        insertQueue("T3", project1, IN_PROGRESS);
        assertPage(1, Arrays.asList("T3"));
        assertPage(2, Arrays.asList("T3", "T2"));
        assertPage(10, Arrays.asList("T3", "T2", "T1"));
    }

    @Test
    public void return_warnings_count_on_queue_and_activity_but_no_warnings_list() {
        logInAsSystemAdministrator();
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        insertActivity("T1", project1, SUCCESS);
        insertActivity("T2", project2, FAILED);
        insertQueue("T3", project1, IN_PROGRESS);
        insertMessages("T1", 2);
        insertMessages("T2", 0);
        insertMessages("T3", 5);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam(PAGE_SIZE, Integer.toString(10)).setParam(CeWsParameters.PARAM_STATUS, "SUCCESS,FAILED,CANCELED,IN_PROGRESS,PENDING"));
        assertThat(activityResponse.getTasksList()).extracting(Task::getId, Task::getWarningCount, Task::getWarningsList).containsOnly(tuple("T1", 2, Collections.emptyList()), tuple("T2", 0, Collections.emptyList()), tuple("T3", 0, Collections.emptyList()));
    }

    @Test
    public void project_administrator_can_access_his_project_activity() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        // no need to be a system admin
        userSession.logIn().addProjectPermission(ADMIN, project1);
        insertActivity("T1", project1, SUCCESS);
        insertActivity("T2", project2, FAILED);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam("componentId", project1.uuid()));
        assertThat(activityResponse.getTasksCount()).isEqualTo(1);
        assertThat(activityResponse.getTasks(0).getId()).isEqualTo("T1");
        assertThat(activityResponse.getTasks(0).getStatus()).isEqualTo(Ce.TaskStatus.SUCCESS);
        assertThat(activityResponse.getTasks(0).getComponentId()).isEqualTo(project1.uuid());
    }

    @Test
    public void return_401_if_user_is_not_logged_in() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        ActivityActionTest.call(ws.newRequest().setParam("componentId", project.uuid()));
    }

    @Test
    public void search_activity_by_component_name() {
        ComponentDto struts = db.components().insertPrivateProject(( c) -> c.setName("old apache struts"));
        ComponentDto zookeeper = db.components().insertPrivateProject(( c) -> c.setName("new apache zookeeper"));
        ComponentDto eclipse = db.components().insertPrivateProject(( c) -> c.setName("eclipse"));
        db.components().insertSnapshot(struts);
        db.components().insertSnapshot(zookeeper);
        db.components().insertSnapshot(eclipse);
        logInAsSystemAdministrator();
        insertActivity("T1", struts, SUCCESS);
        insertActivity("T2", zookeeper, SUCCESS);
        insertActivity("T3", eclipse, SUCCESS);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT_QUERY, "apac"));
        assertThat(activityResponse.getTasksList()).extracting("id").containsOnly("T1", "T2");
    }

    @Test
    public void search_activity_returns_views() {
        ComponentDto apacheView = db.components().insertView(( v) -> v.setName("Apache View"));
        db.components().insertSnapshot(apacheView);
        logInAsSystemAdministrator();
        insertActivity("T2", apacheView, SUCCESS);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT_QUERY, "apac"));
        assertThat(activityResponse.getTasksList()).extracting("id").containsOnly("T2");
    }

    @Test
    public void search_activity_returns_application() {
        ComponentDto apacheApp = db.components().insertApplication(db.getDefaultOrganization(), ( a) -> a.setName("Apache App"));
        db.components().insertSnapshot(apacheApp);
        logInAsSystemAdministrator();
        insertActivity("T2", apacheApp, SUCCESS);
        ActivityResponse activityResponse = ActivityActionTest.call(ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT_QUERY, "apac"));
        assertThat(activityResponse.getTasksList()).extracting(Task::getId).containsOnly("T2");
    }

    @Test
    public void search_task_id_in_queue_ignoring_other_parameters() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertPrivateProject();
        insertQueue("T1", project, IN_PROGRESS);
        ActivityResponse result = ActivityActionTest.call(ws.newRequest().setParam(TEXT_QUERY, "T1").setParam(CeWsParameters.PARAM_STATUS, PENDING.name()));
        assertThat(result.getTasksCount()).isEqualTo(1);
        assertThat(result.getTasks(0).getId()).isEqualTo("T1");
    }

    @Test
    public void search_task_id_in_activity() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertPrivateProject();
        insertActivity("T1", project, SUCCESS);
        ActivityResponse result = ActivityActionTest.call(ws.newRequest().setParam(TEXT_QUERY, "T1"));
        assertThat(result.getTasksCount()).isEqualTo(1);
        assertThat(result.getTasks(0).getId()).isEqualTo("T1");
    }

    @Test
    public void search_by_task_id_returns_403_if_project_admin_but_not_root() {
        // WS api/ce/task must be used in order to search by task id.
        // Here it's a convenient feature of search by text query, which
        // is reserved to roots
        ComponentDto view = db.components().insertView();
        insertActivity("T1", view, SUCCESS);
        userSession.logIn().addProjectPermission(ADMIN, view);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ActivityActionTest.call(ws.newRequest().setParam(TEXT_QUERY, "T1"));
    }

    @Test
    public void search_task_by_component_id() {
        ComponentDto project = db.components().insertPrivateProject();
        insertQueue("T1", project, IN_PROGRESS);
        insertActivity("T1", project, SUCCESS);
        userSession.logIn().addProjectPermission(ADMIN, project);
        ActivityResponse result = ActivityActionTest.call(ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT_ID, project.uuid()).setParam(CeWsParameters.PARAM_TYPE, REPORT).setParam(CeWsParameters.PARAM_STATUS, "SUCCESS,FAILED,CANCELED,IN_PROGRESS,PENDING"));
        assertThat(result.getTasksCount()).isEqualTo(2);
    }

    @Test
    public void long_living_branch_in_past_activity() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(LONG));
        SnapshotDto analysis = db.components().insertSnapshot(longLivingBranch);
        CeActivityDto activity = insertActivity("T1", project, SUCCESS, analysis);
        insertCharacteristic(activity, BRANCH_KEY, longLivingBranch.getBranch());
        insertCharacteristic(activity, BRANCH_TYPE_KEY, LONG.name());
        ActivityResponse response = ws.newRequest().executeProtobuf(ActivityResponse.class);
        assertThat(response.getTasksList()).extracting(Task::getId, Ce.Task::getBranch, Ce.Task::getBranchType, Ce.Task::getStatus, Ce.Task::getComponentKey).containsExactlyInAnyOrder(tuple("T1", longLivingBranch.getBranch(), Common.BranchType.LONG, Ce.TaskStatus.SUCCESS, longLivingBranch.getKey()));
    }

    @Test
    public void long_living_branch_in_queue_analysis() {
        logInAsSystemAdministrator();
        String branch = "ny_branch";
        CeQueueDto queue1 = insertQueue("T1", null, IN_PROGRESS);
        insertCharacteristic(queue1, BRANCH_KEY, branch);
        insertCharacteristic(queue1, BRANCH_TYPE_KEY, LONG.name());
        CeQueueDto queue2 = insertQueue("T2", null, PENDING);
        insertCharacteristic(queue2, BRANCH_KEY, branch);
        insertCharacteristic(queue2, BRANCH_TYPE_KEY, LONG.name());
        ActivityResponse response = ws.newRequest().setParam("status", "FAILED,IN_PROGRESS,PENDING").executeProtobuf(ActivityResponse.class);
        assertThat(response.getTasksList()).extracting(Task::getId, Ce.Task::getBranch, Ce.Task::getBranchType, Ce.Task::getStatus).containsExactlyInAnyOrder(tuple("T1", branch, Common.BranchType.LONG, Ce.TaskStatus.IN_PROGRESS), tuple("T2", branch, Common.BranchType.LONG, Ce.TaskStatus.PENDING));
    }

    @Test
    public void pull_request_in_past_activity() {
        logInAsSystemAdministrator();
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        ComponentDto pullRequest = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(BranchType.PULL_REQUEST));
        SnapshotDto analysis = db.components().insertSnapshot(pullRequest);
        CeActivityDto activity = insertActivity("T1", project, SUCCESS, analysis);
        insertCharacteristic(activity, PULL_REQUEST, pullRequest.getPullRequest());
        ActivityResponse response = ws.newRequest().executeProtobuf(ActivityResponse.class);
        // TODO the pull request title must be loaded from db
        assertThat(response.getTasksList()).extracting(Task::getId, Ce.Task::getPullRequest, Ce.Task::hasPullRequestTitle, Ce.Task::getStatus, Ce.Task::getComponentKey).containsExactlyInAnyOrder(tuple("T1", pullRequest.getPullRequest(), false, Ce.TaskStatus.SUCCESS, pullRequest.getKey()));
    }

    @Test
    public void pull_request_in_queue_analysis() {
        logInAsSystemAdministrator();
        String branch = "pr-123";
        CeQueueDto queue1 = insertQueue("T1", null, IN_PROGRESS);
        insertCharacteristic(queue1, PULL_REQUEST, branch);
        CeQueueDto queue2 = insertQueue("T2", null, PENDING);
        insertCharacteristic(queue2, PULL_REQUEST, branch);
        ActivityResponse response = ws.newRequest().setParam("status", "FAILED,IN_PROGRESS,PENDING").executeProtobuf(ActivityResponse.class);
        assertThat(response.getTasksList()).extracting(Task::getId, Ce.Task::getPullRequest, Ce.Task::hasPullRequestTitle, Ce.Task::getStatus).containsExactlyInAnyOrder(tuple("T1", branch, false, Ce.TaskStatus.IN_PROGRESS), tuple("T2", branch, false, Ce.TaskStatus.PENDING));
    }

    @Test
    public void fail_if_both_filters_on_component_id_and_name() {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("componentId and componentQuery must not be set at the same time");
        ws.newRequest().setParam("componentId", "ID1").setParam("componentQuery", "apache").setMediaType(PROTOBUF).execute();
    }

    @Test
    public void fail_if_page_size_greater_than_1000() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'ps' value (1001) must be less than 1000");
        ws.newRequest().setParam(PAGE_SIZE, "1001").execute();
    }

    @Test
    public void fail_if_date_is_not_well_formatted() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Date 'ill-formatted-date' cannot be parsed as either a date or date+time");
        ws.newRequest().setParam(CeWsParameters.PARAM_MAX_EXECUTED_AT, "ill-formatted-date").execute();
    }

    @Test
    public void throws_IAE_if_pageSize_is_0() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("page size must be >= 1");
        ActivityActionTest.call(ws.newRequest().setParam(PAGE_SIZE, Integer.toString(0)).setParam(CeWsParameters.PARAM_STATUS, "SUCCESS,FAILED,CANCELED,IN_PROGRESS,PENDING"));
    }

    @Test
    public void fail_when_project_does_not_exist() {
        logInAsSystemAdministrator();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Component 'unknown' does not exist");
        ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT_ID, "unknown").execute();
    }

    @Test
    public void support_json_response() {
        logInAsSystemAdministrator();
        TestResponse wsResponse = ws.newRequest().setMediaType(JSON).execute();
        JsonAssert.assertJson(wsResponse.getInput()).isSimilarTo("{\"tasks\":[]}");
    }
}

