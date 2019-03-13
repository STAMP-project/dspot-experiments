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


import Ce.ComponentResponse;
import Ce.Task;
import CeActivityDto.Status.CANCELED;
import CeActivityDto.Status.FAILED;
import CeActivityDto.Status.SUCCESS;
import MediaTypes.PROTOBUF;
import System2.INSTANCE;
import UserRole.USER;
import java.util.Collections;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.ce.CeActivityDto;
import org.sonar.db.ce.CeQueueDto;
import org.sonar.db.ce.CeTaskMessageDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.TestComponentFinder;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Ce;


public class ComponentActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private TaskFormatter formatter = new TaskFormatter(db.getDbClient(), System2.INSTANCE);

    private ComponentAction underTest = new ComponentAction(userSession, db.getDbClient(), formatter, TestComponentFinder.from(db));

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void empty_queue_and_empty_activity() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.addProjectPermission(USER, project);
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, project.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.getQueueCount()).isEqualTo(0);
        assertThat(response.hasCurrent()).isFalse();
    }

    @Test
    public void project_tasks() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        SnapshotDto analysisProject1 = db.components().insertSnapshot(project1);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        userSession.addProjectPermission(USER, project1);
        insertActivity("T1", project1, SUCCESS, analysisProject1);
        insertActivity("T2", project2, FAILED, null);
        insertActivity("T3", project1, FAILED, null);
        insertQueue("T4", project1, IN_PROGRESS);
        insertQueue("T5", project1, PENDING);
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, project1.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.getQueueCount()).isEqualTo(2);
        assertThat(response.getQueue(0).getId()).isEqualTo("T4");
        assertThat(response.getQueue(1).getId()).isEqualTo("T5");
        // T3 is the latest task executed on PROJECT_1
        assertThat(response.hasCurrent()).isTrue();
        Ce.Task current = response.getCurrent();
        assertThat(current.getId()).isEqualTo("T3");
        assertThat(current.hasAnalysisId()).isFalse();
        assertThat(current.getWarningCount()).isZero();
        assertThat(current.getWarningsList()).isEmpty();
        assertThat(response.getQueueList()).extracting(Ce.Task::getOrganization).containsOnly(organization.getKey());
        assertThat(current.getOrganization()).isEqualTo(organization.getKey());
    }

    @Test
    public void search_tasks_by_component_key() {
        ComponentDto project = db.components().insertPrivateProject();
        logInWithBrowsePermission(project);
        SnapshotDto analysis = db.components().insertSnapshot(project);
        insertActivity("T1", project, SUCCESS, analysis);
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, project.getDbKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.hasCurrent()).isTrue();
        Ce.Task current = response.getCurrent();
        assertThat(current.getId()).isEqualTo("T1");
        assertThat(current.getAnalysisId()).isEqualTo(analysis.getUuid());
        assertThat(current.getWarningCount()).isZero();
        assertThat(current.getWarningsList()).isEmpty();
    }

    @Test
    public void search_tasks_by_component_id() {
        ComponentDto project = db.components().insertPrivateProject();
        logInWithBrowsePermission(project);
        SnapshotDto analysis = db.components().insertSnapshot(project);
        insertActivity("T1", project, SUCCESS, analysis);
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT_ID, project.uuid()).executeProtobuf(ComponentResponse.class);
        assertThat(response.hasCurrent()).isTrue();
        Ce.Task current = response.getCurrent();
        assertThat(current.getId()).isEqualTo("T1");
        assertThat(current.getAnalysisId()).isEqualTo(analysis.getUuid());
        assertThat(current.getWarningCount()).isZero();
        assertThat(current.getWarningsList()).isEmpty();
    }

    @Test
    public void canceled_tasks_must_not_be_picked_as_current_analysis() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.addProjectPermission(USER, project);
        insertActivity("T1", project, SUCCESS);
        insertActivity("T2", project, FAILED);
        insertActivity("T3", project, SUCCESS);
        insertActivity("T4", project, CANCELED);
        insertActivity("T5", project, CANCELED);
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, project.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.getQueueCount()).isEqualTo(0);
        // T3 is the latest task executed on PROJECT_1 ignoring Canceled ones
        assertThat(response.hasCurrent()).isTrue();
        Ce.Task current = response.getCurrent();
        assertThat(current.getId()).isEqualTo("T3");
        assertThat(current.getWarningCount()).isZero();
        assertThat(current.getWarningsList()).isEmpty();
    }

    @Test
    public void long_living_branch_in_activity() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(LONG));
        SnapshotDto analysis = db.components().insertSnapshot(longLivingBranch);
        CeActivityDto activity = insertActivity("T1", project, SUCCESS, analysis);
        insertCharacteristic(activity, BRANCH_KEY, longLivingBranch.getBranch());
        insertCharacteristic(activity, BRANCH_TYPE_KEY, LONG.name());
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, project.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.getCurrent()).extracting(Ce.Task::getId, Ce.Task::getBranch, Ce.Task::getBranchType, Ce.Task::getStatus, Ce.Task::getComponentKey, Ce.Task::getWarningCount, Ce.Task::getWarningsList).containsOnly("T1", longLivingBranch.getBranch(), Common.BranchType.LONG, Ce.TaskStatus.SUCCESS, project.getKey(), 0, Collections.emptyList());
    }

    @Test
    public void long_living_branch_in_queue_analysis() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(LONG));
        CeQueueDto queue1 = insertQueue("T1", project, IN_PROGRESS);
        insertCharacteristic(queue1, BRANCH_KEY, longLivingBranch.getBranch());
        insertCharacteristic(queue1, BRANCH_TYPE_KEY, LONG.name());
        CeQueueDto queue2 = insertQueue("T2", project, PENDING);
        insertCharacteristic(queue2, BRANCH_KEY, longLivingBranch.getBranch());
        insertCharacteristic(queue2, BRANCH_TYPE_KEY, LONG.name());
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, longLivingBranch.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.getQueueList()).extracting(Ce.Task::getId, Ce.Task::getBranch, Ce.Task::getBranchType, Ce.Task::getStatus, Ce.Task::getComponentKey, Ce.Task::getWarningCount, Ce.Task::getWarningsList).containsOnly(tuple("T1", longLivingBranch.getBranch(), Common.BranchType.LONG, Ce.TaskStatus.IN_PROGRESS, project.getKey(), 0, Collections.emptyList()), tuple("T2", longLivingBranch.getBranch(), Common.BranchType.LONG, Ce.TaskStatus.PENDING, project.getKey(), 0, Collections.emptyList()));
    }

    @Test
    public void return_many_tasks_from_same_project() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        insertQueue("Main", project, IN_PROGRESS);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(LONG).setKey("long-branch"));
        CeQueueDto longLivingBranchQueue = insertQueue("Long", project, IN_PROGRESS);
        insertCharacteristic(longLivingBranchQueue, BRANCH_KEY, longLivingBranch.getBranch());
        insertCharacteristic(longLivingBranchQueue, BRANCH_TYPE_KEY, LONG.name());
        ComponentDto shortLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(SHORT).setKey("short-branch"));
        CeQueueDto shortLivingBranchQueue = insertQueue("Short", project, PENDING);
        insertCharacteristic(shortLivingBranchQueue, BRANCH_KEY, shortLivingBranch.getBranch());
        insertCharacteristic(shortLivingBranchQueue, BRANCH_TYPE_KEY, SHORT.name());
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, longLivingBranch.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.getQueueList()).extracting(Ce.Task::getId, Ce.Task::getComponentKey, Ce.Task::getBranch, Ce.Task::getBranchType, Ce.Task::getWarningCount, Ce.Task::getWarningsList).containsOnly(tuple("Main", project.getKey(), "", Common.BranchType.UNKNOWN_BRANCH_TYPE, 0, Collections.emptyList()), tuple("Long", longLivingBranch.getKey(), longLivingBranch.getBranch(), Common.BranchType.LONG, 0, Collections.emptyList()), tuple("Short", shortLivingBranch.getKey(), shortLivingBranch.getBranch(), Common.BranchType.SHORT, 0, Collections.emptyList()));
    }

    @Test
    public void populates_warning_count_of_activities_but_not_warnings() {
        ComponentDto privateProject = db.components().insertPrivateProject();
        userSession.addProjectPermission(USER, privateProject);
        SnapshotDto analysis = db.components().insertSnapshot(privateProject);
        CeActivityDto activity = insertActivity("Short", privateProject, SUCCESS, analysis);
        int messageCount = 1 + (new Random().nextInt(10));
        IntStream.range(0, messageCount).forEach(( i) -> db.getDbClient().ceTaskMessageDao().insert(db.getSession(), new CeTaskMessageDto().setUuid(("uuid_" + i)).setTaskUuid(activity.getUuid()).setMessage(("m_" + i)).setCreatedAt(i)));
        db.commit();
        Ce.ComponentResponse response = ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, privateProject.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.hasCurrent()).isTrue();
        assertThat(response.getCurrent()).extracting(Ce.Task::getWarningCount, Ce.Task::getWarningsList).containsOnly(messageCount, Collections.emptyList());
    }

    @Test
    public void deprecated_component_key() {
        ComponentDto project = db.components().insertPrivateProject();
        logInWithBrowsePermission(project);
        SnapshotDto analysis = db.components().insertSnapshot(project);
        insertActivity("T1", project, SUCCESS, analysis);
        Ce.ComponentResponse response = ws.newRequest().setParam("componentKey", project.getKey()).executeProtobuf(ComponentResponse.class);
        assertThat(response.hasCurrent()).isTrue();
        assertThat(response.getCurrent().getId()).isEqualTo("T1");
        assertThat(response.getCurrent().getAnalysisId()).isEqualTo(analysis.getUuid());
    }

    @Test
    public void fail_with_404_when_component_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, "UNKNOWN").setMediaType(PROTOBUF).execute();
    }

    @Test
    public void throw_ForbiddenException_if_user_cant_access_project() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ws.newRequest().setParam(CeWsParameters.PARAM_COMPONENT, project.getKey()).execute();
    }

    @Test
    public void fail_when_no_component_parameter() {
        expectedException.expect(IllegalArgumentException.class);
        logInWithBrowsePermission(db.components().insertPrivateProject());
        ws.newRequest().execute();
    }
}

