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
package org.sonar.ce.queue;


import CeActivityDto.Status.CANCELED;
import CeActivityDto.Status.FAILED;
import CeQueue.WorkersPauseStatus.PAUSED;
import CeQueue.WorkersPauseStatus.PAUSING;
import CeQueue.WorkersPauseStatus.RESUMED;
import CeTaskTypes.REPORT;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.ce.queue.CeTaskSubmit.Component;
import org.sonar.ce.task.CeTask;
import org.sonar.core.util.UuidFactory;
import org.sonar.core.util.UuidFactoryImpl;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.ce.CeActivityDto;
import org.sonar.db.ce.CeQueueDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;


public class CeQueueImplTest {
    private static final String WORKER_UUID = "workerUuid";

    private static final long NOW = 1450000000000L;

    private System2 system2 = new TestSystem2().setNow(CeQueueImplTest.NOW);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(system2);

    private DbSession session = db.getSession();

    private UuidFactory uuidFactory = UuidFactoryImpl.INSTANCE;

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private CeQueue underTest = new CeQueueImpl(system2, db.getDbClient(), uuidFactory, defaultOrganizationProvider);

    @Test
    public void submit_returns_task_populated_from_CeTaskSubmit_and_creates_CeQueue_row() {
        String componentUuid = randomAlphabetic(3);
        String mainComponentUuid = randomAlphabetic(4);
        CeTaskSubmit taskSubmit = createTaskSubmit(REPORT, new Component(componentUuid, mainComponentUuid), "submitter uuid");
        UserDto userDto = db.getDbClient().userDao().selectByUuid(db.getSession(), taskSubmit.getSubmitterUuid());
        CeTask task = underTest.submit(taskSubmit);
        verifyCeTask(taskSubmit, task, null, userDto);
        verifyCeQueueDtoForTaskSubmit(taskSubmit);
    }

    @Test
    public void submit_populates_component_name_and_key_of_CeTask_if_component_exists() {
        ComponentDto componentDto = insertComponent(ComponentTesting.newPrivateProjectDto(db.organizations().insert(), "PROJECT_1"));
        CeTaskSubmit taskSubmit = createTaskSubmit(REPORT, Component.fromDto(componentDto), null);
        CeTask task = underTest.submit(taskSubmit);
        verifyCeTask(taskSubmit, task, componentDto, null);
    }

    @Test
    public void submit_returns_task_without_component_info_when_submit_has_none() {
        CeTaskSubmit taskSubmit = createTaskSubmit("not cpt related");
        CeTask task = underTest.submit(taskSubmit);
        verifyCeTask(taskSubmit, task, null, null);
    }

    @Test
    public void submit_populates_submitter_login_of_CeTask_if_submitter_exists() {
        UserDto userDto = insertUser(UserTesting.newUserDto());
        CeTaskSubmit taskSubmit = createTaskSubmit(REPORT, null, userDto.getUuid());
        CeTask task = underTest.submit(taskSubmit);
        verifyCeTask(taskSubmit, task, null, userDto);
    }

    @Test
    public void submit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_without_component_when_there_is_a_pending_task_without_component() {
        CeTaskSubmit taskSubmit = createTaskSubmit("no_component");
        CeQueueDto dto = insertPendingInQueue(null);
        Optional<CeTask> task = underTest.submit(taskSubmit, SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(task).isNotEmpty();
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid(), task.get().getUuid());
    }

    @Test
    public void submit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_when_there_is_a_pending_task_for_another_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        String otherMainComponentUuid = randomAlphabetic(6);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        CeQueueDto dto = insertPendingInQueue(CeQueueImplTest.newComponent(otherMainComponentUuid));
        Optional<CeTask> task = underTest.submit(taskSubmit, SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(task).isNotEmpty();
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid(), task.get().getUuid());
    }

    @Test
    public void submit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_does_not_create_task_when_there_is_one_pending_task_for_same_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        CeQueueDto dto = insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid));
        Optional<CeTask> task = underTest.submit(taskSubmit, SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(task).isEmpty();
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid());
    }

    @Test
    public void submit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_does_not_create_task_when_there_is_many_pending_task_for_same_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        String[] uuids = IntStream.range(0, (2 + (new Random().nextInt(5)))).mapToObj(( i) -> insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid))).map(CeQueueDto::getUuid).toArray(String[]::new);
        Optional<CeTask> task = underTest.submit(taskSubmit, SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(task).isEmpty();
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(uuids);
    }

    @Test
    public void submit_without_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_when_there_is_one_pending_task_for_same_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        CeQueueDto dto = insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid));
        CeTask task = underTest.submit(taskSubmit);
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid(), task.getUuid());
    }

    @Test
    public void submit_without_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_when_there_is_many_pending_task_for_same_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        String[] uuids = IntStream.range(0, (2 + (new Random().nextInt(5)))).mapToObj(( i) -> insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid))).map(CeQueueDto::getUuid).toArray(String[]::new);
        CeTask task = underTest.submit(taskSubmit);
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).hasSize(((uuids.length) + 1)).contains(uuids).contains(task.getUuid());
    }

    @Test
    public void massSubmit_returns_tasks_for_each_CeTaskSubmit_populated_from_CeTaskSubmit_and_creates_CeQueue_row_for_each() {
        String mainComponentUuid = randomAlphabetic(10);
        CeTaskSubmit taskSubmit1 = createTaskSubmit(REPORT, CeQueueImplTest.newComponent(mainComponentUuid), "submitter uuid");
        CeTaskSubmit taskSubmit2 = createTaskSubmit("some type");
        UserDto userDto1 = db.getDbClient().userDao().selectByUuid(db.getSession(), taskSubmit1.getSubmitterUuid());
        List<CeTask> tasks = underTest.massSubmit(Arrays.asList(taskSubmit1, taskSubmit2));
        assertThat(tasks).hasSize(2);
        verifyCeTask(taskSubmit1, tasks.get(0), null, userDto1);
        verifyCeTask(taskSubmit2, tasks.get(1), null, null);
        verifyCeQueueDtoForTaskSubmit(taskSubmit1);
        verifyCeQueueDtoForTaskSubmit(taskSubmit2);
    }

    @Test
    public void massSubmit_populates_component_name_and_key_of_CeTask_if_project_exists() {
        ComponentDto componentDto1 = insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), "PROJECT_1"));
        CeTaskSubmit taskSubmit1 = createTaskSubmit(REPORT, Component.fromDto(componentDto1), null);
        CeTaskSubmit taskSubmit2 = createTaskSubmit("something", CeQueueImplTest.newComponent(randomAlphabetic(12)), null);
        List<CeTask> tasks = underTest.massSubmit(Arrays.asList(taskSubmit1, taskSubmit2));
        assertThat(tasks).hasSize(2);
        verifyCeTask(taskSubmit1, tasks.get(0), componentDto1, null);
        verifyCeTask(taskSubmit2, tasks.get(1), null, null);
    }

    @Test
    public void massSubmit_populates_component_name_and_key_of_CeTask_if_project_and_branch_exists() {
        ComponentDto project = insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), "PROJECT_1"));
        ComponentDto branch1 = db.components().insertProjectBranch(project);
        ComponentDto branch2 = db.components().insertProjectBranch(project);
        CeTaskSubmit taskSubmit1 = createTaskSubmit(REPORT, Component.fromDto(branch1), null);
        CeTaskSubmit taskSubmit2 = createTaskSubmit("something", Component.fromDto(branch2), null);
        List<CeTask> tasks = underTest.massSubmit(Arrays.asList(taskSubmit1, taskSubmit2));
        assertThat(tasks).hasSize(2);
        verifyCeTask(taskSubmit1, tasks.get(0), branch1, project, null);
        verifyCeTask(taskSubmit2, tasks.get(1), branch2, project, null);
    }

    @Test
    public void massSubmit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_without_component_when_there_is_a_pending_task_without_component() {
        CeTaskSubmit taskSubmit = createTaskSubmit("no_component");
        CeQueueDto dto = insertPendingInQueue(null);
        List<CeTask> tasks = underTest.massSubmit(ImmutableList.of(taskSubmit), SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(tasks).hasSize(1);
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid(), tasks.iterator().next().getUuid());
    }

    @Test
    public void massSubmit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_when_there_is_a_pending_task_for_another_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        String otherMainComponentUuid = randomAlphabetic(6);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        CeQueueDto dto = insertPendingInQueue(CeQueueImplTest.newComponent(otherMainComponentUuid));
        List<CeTask> tasks = underTest.massSubmit(ImmutableList.of(taskSubmit), SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(tasks).hasSize(1);
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid(), tasks.iterator().next().getUuid());
    }

    @Test
    public void massSubmit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_does_not_create_task_when_there_is_one_pending_task_for_same_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        CeQueueDto dto = insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid));
        List<CeTask> tasks = underTest.massSubmit(ImmutableList.of(taskSubmit), SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(tasks).isEmpty();
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid());
    }

    @Test
    public void massSubmit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_does_not_create_task_when_there_is_many_pending_task_for_same_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        String[] uuids = IntStream.range(0, (2 + (new Random().nextInt(5)))).mapToObj(( i) -> insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid))).map(CeQueueDto::getUuid).toArray(String[]::new);
        List<CeTask> tasks = underTest.massSubmit(ImmutableList.of(taskSubmit), SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(tasks).isEmpty();
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(uuids);
    }

    @Test
    public void massSubmit_without_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_when_there_is_one_pending_task_for_other_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        CeQueueDto dto = insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid));
        List<CeTask> tasks = underTest.massSubmit(ImmutableList.of(taskSubmit));
        assertThat(tasks).hasSize(1);
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).containsOnly(dto.getUuid(), tasks.iterator().next().getUuid());
    }

    @Test
    public void massSubmit_without_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_task_when_there_is_many_pending_task_for_other_main_component() {
        String mainComponentUuid = randomAlphabetic(5);
        CeTaskSubmit taskSubmit = createTaskSubmit("with_component", CeQueueImplTest.newComponent(mainComponentUuid), null);
        String[] uuids = IntStream.range(0, (2 + (new Random().nextInt(5)))).mapToObj(( i) -> insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid))).map(CeQueueDto::getUuid).toArray(String[]::new);
        List<CeTask> tasks = underTest.massSubmit(ImmutableList.of(taskSubmit));
        assertThat(tasks).hasSize(1);
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).hasSize(((uuids.length) + 1)).contains(uuids).contains(tasks.iterator().next().getUuid());
    }

    @Test
    public void massSubmit_with_UNIQUE_QUEUE_PER_MAIN_COMPONENT_creates_tasks_depending_on_whether_there_is_pending_task_for_same_main_component() {
        String mainComponentUuid1 = randomAlphabetic(5);
        String mainComponentUuid2 = randomAlphabetic(6);
        String mainComponentUuid3 = randomAlphabetic(7);
        String mainComponentUuid4 = randomAlphabetic(8);
        String mainComponentUuid5 = randomAlphabetic(9);
        CeTaskSubmit taskSubmit1 = createTaskSubmit("with_one_pending", CeQueueImplTest.newComponent(mainComponentUuid1), null);
        CeQueueDto dto1 = insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid1));
        Component componentForMainComponentUuid2 = CeQueueImplTest.newComponent(mainComponentUuid2);
        CeTaskSubmit taskSubmit2 = createTaskSubmit("no_pending", componentForMainComponentUuid2, null);
        CeTaskSubmit taskSubmit3 = createTaskSubmit("with_many_pending", CeQueueImplTest.newComponent(mainComponentUuid3), null);
        String[] uuids3 = IntStream.range(0, (2 + (new Random().nextInt(5)))).mapToObj(( i) -> insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid3))).map(CeQueueDto::getUuid).toArray(String[]::new);
        Component componentForMainComponentUuid4 = CeQueueImplTest.newComponent(mainComponentUuid4);
        CeTaskSubmit taskSubmit4 = createTaskSubmit("no_pending_2", componentForMainComponentUuid4, null);
        CeTaskSubmit taskSubmit5 = createTaskSubmit("with_pending_2", CeQueueImplTest.newComponent(mainComponentUuid5), null);
        CeQueueDto dto5 = insertPendingInQueue(CeQueueImplTest.newComponent(mainComponentUuid5));
        List<CeTask> tasks = underTest.massSubmit(ImmutableList.of(taskSubmit1, taskSubmit2, taskSubmit3, taskSubmit4, taskSubmit5), SubmitOption.UNIQUE_QUEUE_PER_MAIN_COMPONENT);
        assertThat(tasks).hasSize(2).extracting(( task) -> task.getComponent().get().getUuid(), ( task) -> task.getMainComponent().get().getUuid()).containsOnly(tuple(componentForMainComponentUuid2.getUuid(), componentForMainComponentUuid2.getMainComponentUuid()), tuple(componentForMainComponentUuid4.getUuid(), componentForMainComponentUuid4.getMainComponentUuid()));
        assertThat(db.getDbClient().ceQueueDao().selectAllInAscOrder(db.getSession())).extracting(CeQueueDto::getUuid).hasSize((((1 + (uuids3.length)) + 1) + (tasks.size()))).contains(dto1.getUuid()).contains(uuids3).contains(dto5.getUuid()).containsAll(tasks.stream().map(CeTask::getUuid).collect(Collectors.toList()));
    }

    @Test
    public void cancel_pending() {
        CeTask task = submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(12)));
        CeQueueDto queueDto = db.getDbClient().ceQueueDao().selectByUuid(db.getSession(), task.getUuid()).get();
        underTest.cancel(db.getSession(), queueDto);
        Optional<CeActivityDto> activity = db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), task.getUuid());
        assertThat(activity.isPresent()).isTrue();
        assertThat(activity.get().getStatus()).isEqualTo(CANCELED);
    }

    @Test
    public void fail_to_cancel_if_in_progress() {
        submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(11)));
        CeQueueDto ceQueueDto = db.getDbClient().ceQueueDao().peek(session, CeQueueImplTest.WORKER_UUID).get();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(Matchers.startsWith("Task is in progress and can't be canceled"));
        underTest.cancel(db.getSession(), ceQueueDto);
    }

    @Test
    public void cancelAll_pendings_but_not_in_progress() {
        CeTask inProgressTask = submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(12)));
        CeTask pendingTask1 = submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(13)));
        CeTask pendingTask2 = submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(14)));
        db.getDbClient().ceQueueDao().peek(session, CeQueueImplTest.WORKER_UUID);
        int canceledCount = underTest.cancelAll();
        assertThat(canceledCount).isEqualTo(2);
        Optional<CeActivityDto> history = db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), pendingTask1.getUuid());
        assertThat(history.get().getStatus()).isEqualTo(CANCELED);
        history = db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), pendingTask2.getUuid());
        assertThat(history.get().getStatus()).isEqualTo(CANCELED);
        history = db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), inProgressTask.getUuid());
        assertThat(history.isPresent()).isFalse();
    }

    @Test
    public void pauseWorkers_marks_workers_as_paused_if_zero_tasks_in_progress() {
        submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(12)));
        // task is pending
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(RESUMED);
        underTest.pauseWorkers();
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(PAUSED);
    }

    @Test
    public void pauseWorkers_marks_workers_as_pausing_if_some_tasks_in_progress() {
        submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(12)));
        db.getDbClient().ceQueueDao().peek(session, CeQueueImplTest.WORKER_UUID);
        // task is in-progress
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(RESUMED);
        underTest.pauseWorkers();
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(PAUSING);
    }

    @Test
    public void resumeWorkers_does_nothing_if_not_paused() {
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(RESUMED);
        underTest.resumeWorkers();
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(RESUMED);
    }

    @Test
    public void resumeWorkers_resumes_pausing_workers() {
        submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(12)));
        db.getDbClient().ceQueueDao().peek(session, CeQueueImplTest.WORKER_UUID);
        // task is in-progress
        underTest.pauseWorkers();
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(PAUSING);
        underTest.resumeWorkers();
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(RESUMED);
    }

    @Test
    public void resumeWorkers_resumes_paused_workers() {
        underTest.pauseWorkers();
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(PAUSED);
        underTest.resumeWorkers();
        assertThat(underTest.getWorkersPauseStatus()).isEqualTo(RESUMED);
    }

    @Test
    public void fail_in_progress_task() {
        CeTask task = submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(12)));
        CeQueueDto queueDto = db.getDbClient().ceQueueDao().peek(db.getSession(), CeQueueImplTest.WORKER_UUID).get();
        underTest.fail(db.getSession(), queueDto, "TIMEOUT", "Failed on timeout");
        Optional<CeActivityDto> activity = db.getDbClient().ceActivityDao().selectByUuid(db.getSession(), task.getUuid());
        assertThat(activity.isPresent()).isTrue();
        assertThat(activity.get().getStatus()).isEqualTo(FAILED);
        assertThat(activity.get().getErrorType()).isEqualTo("TIMEOUT");
        assertThat(activity.get().getErrorMessage()).isEqualTo("Failed on timeout");
        assertThat(activity.get().getExecutedAt()).isEqualTo(CeQueueImplTest.NOW);
        assertThat(activity.get().getWorkerUuid()).isEqualTo(CeQueueImplTest.WORKER_UUID);
    }

    @Test
    public void fail_throws_exception_if_task_is_pending() {
        CeTask task = submit(REPORT, CeQueueImplTest.newComponent(randomAlphabetic(12)));
        CeQueueDto queueDto = db.getDbClient().ceQueueDao().selectByUuid(db.getSession(), task.getUuid()).get();
        Throwable thrown = catchThrowable(() -> underTest.fail(db.getSession(), queueDto, "TIMEOUT", "Failed on timeout"));
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage((("Task is not in-progress and can't be marked as failed [uuid=" + (task.getUuid())) + "]"));
    }

    private static int newComponentIdGenerator = new Random().nextInt(8999333);
}

