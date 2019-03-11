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


import Ce.Task;
import Ce.TaskResponse;
import Ce.TaskStatus.FAILED;
import CeQueueDto.Status.PENDING;
import CeTaskTypes.REPORT;
import GlobalPermissions.SCAN_EXECUTION;
import System2.INSTANCE;
import UserRole.USER;
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
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Ce;


public class TaskActionTest {
    private static final String SOME_TASK_UUID = "TASK_1";

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private OrganizationDto organization;

    private ComponentDto privateProject;

    private ComponentDto publicProject;

    private TaskFormatter formatter = new TaskFormatter(db.getDbClient(), System2.INSTANCE);

    private TaskAction underTest = new TaskAction(db.getDbClient(), formatter, userSession);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void task_is_in_queue() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setRoot();
        CeQueueDto queueDto = new CeQueueDto();
        queueDto.setTaskType(REPORT);
        queueDto.setUuid(TaskActionTest.SOME_TASK_UUID);
        queueDto.setComponentUuid(privateProject.uuid());
        queueDto.setStatus(PENDING);
        queueDto.setSubmitterUuid(user.getUuid());
        persist(queueDto);
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).executeProtobuf(TaskResponse.class);
        assertThat(taskResponse.getTask().getOrganization()).isEqualTo(organization.getKey());
        assertThat(taskResponse.getTask().getId()).isEqualTo(TaskActionTest.SOME_TASK_UUID);
        assertThat(taskResponse.getTask().getStatus()).isEqualTo(Ce.TaskStatus.PENDING);
        assertThat(taskResponse.getTask().getSubmitterLogin()).isEqualTo(user.getLogin());
        assertThat(taskResponse.getTask().getComponentId()).isEqualTo(privateProject.uuid());
        assertThat(taskResponse.getTask().getComponentKey()).isEqualTo(privateProject.getDbKey());
        assertThat(taskResponse.getTask().getComponentName()).isEqualTo(privateProject.name());
        assertThat(taskResponse.getTask().hasExecutionTimeMs()).isFalse();
        assertThat(taskResponse.getTask().getLogs()).isFalse();
        assertThat(taskResponse.getTask().getWarningCount()).isZero();
        assertThat(taskResponse.getTask().getWarningsList()).isEmpty();
    }

    @Test
    public void no_warning_detail_on_task_in_queue() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setRoot();
        CeQueueDto queueDto = createAndPersistQueueTask(null, user);
        IntStream.range(0, (1 + (new Random().nextInt(5)))).forEach(( i) -> db.getDbClient().ceTaskMessageDao().insert(db.getSession(), new CeTaskMessageDto().setUuid(("u_" + i)).setTaskUuid(queueDto.getUuid()).setMessage(("m_" + i)).setCreatedAt(((queueDto.getUuid().hashCode()) + i))));
        db.commit();
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).executeProtobuf(TaskResponse.class);
        Ce.Task task = taskResponse.getTask();
        assertThat(task.getWarningCount()).isZero();
        assertThat(task.getWarningsList()).isEmpty();
    }

    @Test
    public void task_is_archived() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setRoot();
        CeActivityDto activityDto = createActivityDto(TaskActionTest.SOME_TASK_UUID);
        persist(activityDto);
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).executeProtobuf(TaskResponse.class);
        Ce.Task task = taskResponse.getTask();
        assertThat(task.getOrganization()).isEqualTo(organization.getKey());
        assertThat(task.getId()).isEqualTo(TaskActionTest.SOME_TASK_UUID);
        assertThat(task.getStatus()).isEqualTo(FAILED);
        assertThat(task.getComponentId()).isEqualTo(privateProject.uuid());
        assertThat(task.getComponentKey()).isEqualTo(privateProject.getDbKey());
        assertThat(task.getComponentName()).isEqualTo(privateProject.name());
        assertThat(task.getAnalysisId()).isEqualTo(activityDto.getAnalysisUuid());
        assertThat(task.getExecutionTimeMs()).isEqualTo(500L);
        assertThat(task.getLogs()).isFalse();
        assertThat(task.getWarningCount()).isZero();
        assertThat(task.getWarningsList()).isEmpty();
    }

    @Test
    public void long_living_branch_in_past_activity() {
        logInAsRoot();
        ComponentDto project = db.components().insertMainBranch();
        userSession.addProjectPermission(USER, project);
        ComponentDto longLivingBranch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(LONG));
        db.components().insertSnapshot(longLivingBranch);
        CeActivityDto activity = createAndPersistArchivedTask(project);
        insertCharacteristic(activity, BRANCH_KEY, longLivingBranch.getBranch());
        insertCharacteristic(activity, BRANCH_TYPE_KEY, LONG.name());
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).executeProtobuf(TaskResponse.class);
        assertThat(taskResponse.getTask()).extracting(Ce.Task::getId, Ce.Task::getBranch, Ce.Task::getBranchType, Ce.Task::getComponentKey).containsExactlyInAnyOrder(TaskActionTest.SOME_TASK_UUID, longLivingBranch.getBranch(), Common.BranchType.LONG, longLivingBranch.getKey());
    }

    @Test
    public void long_living_branch_in_queue_analysis() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setRoot();
        String branch = "my_branch";
        CeQueueDto queueDto = createAndPersistQueueTask(null, user);
        insertCharacteristic(queueDto, BRANCH_KEY, branch);
        insertCharacteristic(queueDto, BRANCH_TYPE_KEY, LONG.name());
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).executeProtobuf(TaskResponse.class);
        assertThat(taskResponse.getTask()).extracting(Ce.Task::getId, Ce.Task::getBranch, Ce.Task::getBranchType, Ce.Task::hasComponentKey).containsExactlyInAnyOrder(TaskActionTest.SOME_TASK_UUID, branch, Common.BranchType.LONG, false);
    }

    @Test
    public void return_stacktrace_of_failed_activity_with_stacktrace_when_additionalField_is_set() {
        logInAsRoot();
        CeActivityDto activityDto = createActivityDto(TaskActionTest.SOME_TASK_UUID).setErrorMessage("error msg").setErrorStacktrace("error stack");
        persist(activityDto);
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).setParam("additionalFields", "stacktrace").executeProtobuf(TaskResponse.class);
        Ce.Task task = taskResponse.getTask();
        assertThat(task.getId()).isEqualTo(TaskActionTest.SOME_TASK_UUID);
        assertThat(task.getErrorMessage()).isEqualTo(activityDto.getErrorMessage());
        assertThat(task.hasErrorStacktrace()).isTrue();
        assertThat(task.getErrorStacktrace()).isEqualTo(activityDto.getErrorStacktrace());
    }

    @Test
    public void do_not_return_stacktrace_of_failed_activity_with_stacktrace_when_additionalField_is_not_set() {
        logInAsRoot();
        CeActivityDto activityDto = createActivityDto(TaskActionTest.SOME_TASK_UUID).setErrorMessage("error msg").setErrorStacktrace("error stack");
        persist(activityDto);
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).executeProtobuf(TaskResponse.class);
        Ce.Task task = taskResponse.getTask();
        assertThat(task.getId()).isEqualTo(TaskActionTest.SOME_TASK_UUID);
        assertThat(task.getErrorMessage()).isEqualTo(activityDto.getErrorMessage());
        assertThat(task.hasErrorStacktrace()).isFalse();
    }

    @Test
    public void return_scannerContext_of_activity_with_scannerContext_when_additionalField_is_set() {
        logInAsRoot();
        String scannerContext = "this is some scanner context, yeah!";
        persist(createActivityDto(TaskActionTest.SOME_TASK_UUID));
        persistScannerContext(TaskActionTest.SOME_TASK_UUID, scannerContext);
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).setParam("additionalFields", "scannerContext").executeProtobuf(TaskResponse.class);
        Ce.Task task = taskResponse.getTask();
        assertThat(task.getId()).isEqualTo(TaskActionTest.SOME_TASK_UUID);
        assertThat(task.getScannerContext()).isEqualTo(scannerContext);
    }

    @Test
    public void do_not_return_scannerContext_of_activity_with_scannerContext_when_additionalField_is_not_set() {
        logInAsRoot();
        String scannerContext = "this is some scanner context, yeah!";
        persist(createActivityDto(TaskActionTest.SOME_TASK_UUID));
        persistScannerContext(TaskActionTest.SOME_TASK_UUID, scannerContext);
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).setParam("additionalFields", "stacktrace").executeProtobuf(TaskResponse.class);
        Ce.Task task = taskResponse.getTask();
        assertThat(task.getId()).isEqualTo(TaskActionTest.SOME_TASK_UUID);
        assertThat(task.hasScannerContext()).isFalse();
    }

    @Test
    public void do_not_return_stacktrace_of_failed_activity_without_stacktrace() {
        logInAsRoot();
        CeActivityDto activityDto = createActivityDto(TaskActionTest.SOME_TASK_UUID).setErrorMessage("error msg");
        persist(activityDto);
        Ce.TaskResponse taskResponse = ws.newRequest().setParam("id", TaskActionTest.SOME_TASK_UUID).executeProtobuf(TaskResponse.class);
        Ce.Task task = taskResponse.getTask();
        assertThat(task.getId()).isEqualTo(TaskActionTest.SOME_TASK_UUID);
        assertThat(task.getErrorMessage()).isEqualTo(activityDto.getErrorMessage());
        assertThat(task.hasErrorStacktrace()).isFalse();
    }

    @Test
    public void throw_NotFoundException_if_id_does_not_exist() {
        logInAsRoot();
        expectedException.expect(NotFoundException.class);
        ws.newRequest().setParam("id", "DOES_NOT_EXIST").execute();
    }

    @Test
    public void get_project_queue_task_with_scan_permission_on_project() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, privateProject);
        CeQueueDto task = createAndPersistQueueTask(privateProject, user);
        call(task.getUuid());
    }

    @Test
    public void getting_project_queue_task_of_public_project_fails_with_ForbiddenException() {
        UserDto user = db.users().insertUser();
        userSession.logIn().registerComponents(publicProject);
        CeQueueDto task = createAndPersistQueueTask(publicProject, user);
        expectedException.expect(ForbiddenException.class);
        call(task.getUuid());
    }

    @Test
    public void get_project_queue_task_of_private_project_with_user_permission_fails_with_ForbiddenException() {
        UserDto user = db.users().insertUser();
        userSession.logIn().addProjectPermission(USER, privateProject);
        CeQueueDto task = createAndPersistQueueTask(privateProject, user);
        expectedException.expect(ForbiddenException.class);
        call(task.getUuid());
    }

    @Test
    public void get_project_queue_task_on_public_project() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addProjectPermission(SCAN_EXECUTION, privateProject);
        CeQueueDto task = createAndPersistQueueTask(privateProject, user);
        call(task.getUuid());
    }

    @Test
    public void get_project_queue_task_with_scan_permission_on_organization_but_not_on_project() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addPermission(SCAN, privateProject.getOrganizationUuid());
        CeQueueDto task = createAndPersistQueueTask(privateProject, user);
        call(task.getUuid());
    }

    @Test
    public void getting_project_queue_task_throws_ForbiddenException_if_no_admin_nor_scan_permissions() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        CeQueueDto task = createAndPersistQueueTask(privateProject, user);
        expectedException.expect(ForbiddenException.class);
        call(task.getUuid());
    }

    @Test
    public void getting_global_queue_task_requires_to_be_system_administrator() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setSystemAdministrator();
        CeQueueDto task = createAndPersistQueueTask(null, user);
        call(task.getUuid());
    }

    @Test
    public void getting_global_queue_throws_ForbiddenException_if_not_system_administrator() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setNonSystemAdministrator();
        CeQueueDto task = createAndPersistQueueTask(null, user);
        expectedException.expect(ForbiddenException.class);
        call(task.getUuid());
    }

    @Test
    public void get_project_archived_task_with_scan_permission_on_project() {
        userSession.logIn().addProjectPermission(SCAN_EXECUTION, privateProject);
        CeActivityDto task = createAndPersistArchivedTask(privateProject);
        call(task.getUuid());
    }

    @Test
    public void getting_archived_task_of_public_project_fails_with_ForbiddenException() {
        userSession.logIn().registerComponents(publicProject);
        CeActivityDto task = createAndPersistArchivedTask(publicProject);
        expectedException.expect(ForbiddenException.class);
        call(task.getUuid());
    }

    @Test
    public void get_project_archived_task_with_scan_permission_on_organization_but_not_on_project() {
        userSession.logIn().addPermission(SCAN, privateProject.getOrganizationUuid());
        CeActivityDto task = createAndPersistArchivedTask(privateProject);
        call(task.getUuid());
    }

    @Test
    public void getting_project_archived_task_throws_ForbiddenException_if_no_admin_nor_scan_permissions() {
        userSession.logIn();
        CeActivityDto task = createAndPersistArchivedTask(privateProject);
        expectedException.expect(ForbiddenException.class);
        call(task.getUuid());
    }

    @Test
    public void getting_global_archived_task_requires_to_be_system_administrator() {
        logInAsSystemAdministrator();
        CeActivityDto task = createAndPersistArchivedTask(null);
        call(task.getUuid());
    }

    @Test
    public void getting_global_archived_throws_ForbiddenException_if_not_system_administrator() {
        userSession.logIn().setNonSystemAdministrator();
        CeActivityDto task = createAndPersistArchivedTask(null);
        expectedException.expect(ForbiddenException.class);
        call(task.getUuid());
    }

    @Test
    public void get_warnings_on_global_archived_task_requires_to_be_system_administrator() {
        logInAsSystemAdministrator();
        getWarningsImpl(createAndPersistArchivedTask(null));
    }

    @Test
    public void get_warnings_on_public_project_archived_task_if_not_admin_fails_with_ForbiddenException() {
        userSession.logIn().registerComponents(publicProject);
        expectedException.expect(ForbiddenException.class);
        getWarningsImpl(createAndPersistArchivedTask(publicProject));
    }

    @Test
    public void get_warnings_on_private_project_archived_task_if_user_fails_with_ForbiddenException() {
        userSession.logIn().addProjectPermission(USER, privateProject);
        expectedException.expect(ForbiddenException.class);
        getWarningsImpl(createAndPersistArchivedTask(privateProject));
    }

    @Test
    public void get_warnings_on_private_project_archived_task_if_scan() {
        userSession.logIn().addProjectPermission(SCAN_EXECUTION, privateProject);
        getWarningsImpl(createAndPersistArchivedTask(privateProject));
    }

    @Test
    public void get_warnings_on_private_project_archived_task_if_scan_on_organization() {
        userSession.logIn().addPermission(OrganizationPermission.SCAN, organization);
        getWarningsImpl(createAndPersistArchivedTask(privateProject));
    }
}

