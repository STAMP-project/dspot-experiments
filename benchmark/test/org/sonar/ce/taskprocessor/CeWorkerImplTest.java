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
package org.sonar.ce.taskprocessor;


import CeActivityDto.Status.FAILED;
import CeActivityDto.Status.SUCCESS;
import CeTask.User;
import CeTaskTypes.REPORT;
import CeWorker.ExecutionListener;
import LoggerLevel.DEBUG;
import LoggerLevel.ERROR;
import LoggerLevel.INFO;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.api.utils.log.LogAndArguments;
import org.sonar.api.utils.log.LogTester;
import org.sonar.ce.queue.InternalCeQueue;
import org.sonar.ce.task.CeTask;
import org.sonar.ce.task.CeTaskResult;
import org.sonar.ce.task.projectanalysis.taskprocessor.ReportTaskProcessor;
import org.sonar.ce.task.taskprocessor.CeTaskProcessor;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;
import org.sonar.server.organization.BillingValidations;


public class CeWorkerImplTest {
    private System2 system2 = new TestSystem2().setNow(1450000000000L);

    @Rule
    public CeTaskProcessorRepositoryRule taskProcessorRepository = new CeTaskProcessorRepositoryRule();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(system2);

    private DbSession session = db.getSession();

    private InternalCeQueue queue = Mockito.mock(InternalCeQueue.class);

    private ReportTaskProcessor taskProcessor = Mockito.mock(ReportTaskProcessor.class);

    private ExecutionListener executionListener1 = Mockito.mock(ExecutionListener.class);

    private ExecutionListener executionListener2 = Mockito.mock(ExecutionListener.class);

    private CeWorkerController ceWorkerController = Mockito.mock(CeWorkerController.class);

    private ArgumentCaptor<String> workerUuidCaptor = ArgumentCaptor.forClass(String.class);

    private int randomOrdinal = new Random().nextInt(50);

    private String workerUuid = UUID.randomUUID().toString();

    private CeWorker underTest = new CeWorkerImpl(randomOrdinal, workerUuid, queue, taskProcessorRepository, ceWorkerController, executionListener1, executionListener2);

    private CeWorker underTestNoListener = new CeWorkerImpl(randomOrdinal, workerUuid, queue, taskProcessorRepository, ceWorkerController);

    private InOrder inOrder = Mockito.inOrder(taskProcessor, queue, executionListener1, executionListener2);

    private final User submitter = new CeTask.User("UUID_USER_1", "LOGIN_1");

    @Test
    public void constructor_throws_IAE_if_ordinal_is_less_than_zero() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Ordinal must be >= 0");
        new CeWorkerImpl(((-1) - (new Random().nextInt(20))), workerUuid, queue, taskProcessorRepository, ceWorkerController);
    }

    @Test
    public void getUUID_must_return_the_uuid_of_constructor() {
        String uuid = UUID.randomUUID().toString();
        CeWorker underTest = new CeWorkerImpl(randomOrdinal, uuid, queue, taskProcessorRepository, ceWorkerController);
        assertThat(underTest.getUUID()).isEqualTo(uuid);
    }

    @Test
    public void worker_disabled() throws Exception {
        Mockito.reset(ceWorkerController);
        Mockito.when(ceWorkerController.isEnabled(underTest)).thenReturn(false);
        assertThat(underTest.call()).isEqualTo(Result.DISABLED);
        Mockito.verifyZeroInteractions(taskProcessor, executionListener1, executionListener2);
    }

    @Test
    public void worker_disabled_no_listener() throws Exception {
        Mockito.reset(ceWorkerController);
        Mockito.when(ceWorkerController.isEnabled(underTest)).thenReturn(false);
        assertThat(underTestNoListener.call()).isEqualTo(Result.DISABLED);
        Mockito.verifyZeroInteractions(taskProcessor, executionListener1, executionListener2);
    }

    @Test
    public void no_pending_tasks_in_queue() throws Exception {
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        assertThat(underTest.call()).isEqualTo(Result.NO_TASK);
        Mockito.verifyZeroInteractions(taskProcessor, executionListener1, executionListener2);
    }

    @Test
    public void no_pending_tasks_in_queue_without_listener() throws Exception {
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        assertThat(underTestNoListener.call()).isEqualTo(Result.NO_TASK);
        Mockito.verifyZeroInteractions(taskProcessor, executionListener1, executionListener2);
    }

    @Test
    public void fail_when_no_CeTaskProcessor_is_found_in_repository() throws Exception {
        CeTask task = CeWorkerImplTest.createCeTask(null);
        taskProcessorRepository.setNoProcessorForTask(REPORT);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(task));
        assertThat(underTest.call()).isEqualTo(Result.TASK_PROCESSED);
        verifyWorkerUuid();
        inOrder.verify(executionListener1).onStart(task);
        inOrder.verify(executionListener2).onStart(task);
        inOrder.verify(queue).remove(task, FAILED, null, null);
        inOrder.verify(executionListener1).onEnd(task, FAILED, null, null);
        inOrder.verify(executionListener2).onEnd(task, FAILED, null, null);
    }

    @Test
    public void fail_when_no_CeTaskProcessor_is_found_in_repository_without_listener() throws Exception {
        CeTask task = CeWorkerImplTest.createCeTask(null);
        taskProcessorRepository.setNoProcessorForTask(REPORT);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(task));
        assertThat(underTestNoListener.call()).isEqualTo(Result.TASK_PROCESSED);
        verifyWorkerUuid();
        inOrder.verify(queue).remove(task, FAILED, null, null);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void peek_and_process_task() throws Exception {
        CeTask task = CeWorkerImplTest.createCeTask(null);
        taskProcessorRepository.setProcessorForTask(task.getType(), taskProcessor);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(task));
        assertThat(underTest.call()).isEqualTo(Result.TASK_PROCESSED);
        verifyWorkerUuid();
        inOrder.verify(executionListener1).onStart(task);
        inOrder.verify(executionListener2).onStart(task);
        inOrder.verify(taskProcessor).process(task);
        inOrder.verify(queue).remove(task, SUCCESS, null, null);
        inOrder.verify(executionListener1).onEnd(task, SUCCESS, null, null);
        inOrder.verify(executionListener2).onEnd(task, SUCCESS, null, null);
    }

    @Test
    public void peek_and_process_task_without_listeners() throws Exception {
        CeTask task = CeWorkerImplTest.createCeTask(null);
        taskProcessorRepository.setProcessorForTask(task.getType(), taskProcessor);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(task));
        assertThat(underTestNoListener.call()).isEqualTo(Result.TASK_PROCESSED);
        verifyWorkerUuid();
        inOrder.verify(taskProcessor).process(task);
        inOrder.verify(queue).remove(task, SUCCESS, null, null);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void fail_to_process_task() throws Exception {
        CeTask task = CeWorkerImplTest.createCeTask(null);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(task));
        taskProcessorRepository.setProcessorForTask(task.getType(), taskProcessor);
        Throwable error = makeTaskProcessorFail(task);
        assertThat(underTest.call()).isEqualTo(Result.TASK_PROCESSED);
        verifyWorkerUuid();
        inOrder.verify(executionListener1).onStart(task);
        inOrder.verify(executionListener2).onStart(task);
        inOrder.verify(taskProcessor).process(task);
        inOrder.verify(queue).remove(task, FAILED, null, error);
        inOrder.verify(executionListener1).onEnd(task, FAILED, null, error);
        inOrder.verify(executionListener2).onEnd(task, FAILED, null, error);
    }

    @Test
    public void fail_to_process_task_without_listeners() throws Exception {
        CeTask task = CeWorkerImplTest.createCeTask(null);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(task));
        taskProcessorRepository.setProcessorForTask(task.getType(), taskProcessor);
        Throwable error = makeTaskProcessorFail(task);
        assertThat(underTestNoListener.call()).isEqualTo(Result.TASK_PROCESSED);
        verifyWorkerUuid();
        inOrder.verify(taskProcessor).process(task);
        inOrder.verify(queue).remove(task, FAILED, null, error);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void log_task_characteristics() throws Exception {
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(CeWorkerImplTest.createCeTask(null, "pullRequest", "123", "branch", "foo")));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        underTest.call();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        for (int i = 0; i < 2; i++) {
            assertThat(logs.get(i)).contains("pullRequest=123");
            assertThat(logs.get(i)).contains("branch=foo");
        }
    }

    @Test
    public void do_not_log_submitter_param_if_anonymous_and_success() throws Exception {
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(CeWorkerImplTest.createCeTask(null)));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        underTest.call();
        verifyWorkerUuid();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        for (int i = 0; i < 2; i++) {
            assertThat(logs.get(i)).doesNotContain("submitter=");
        }
    }

    @Test
    public void do_not_log_submitter_param_if_anonymous_and_error() throws Exception {
        CeTask ceTask = CeWorkerImplTest.createCeTask(null);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(ceTask.getType(), taskProcessor);
        makeTaskProcessorFail(ceTask);
        underTest.call();
        verifyWorkerUuid();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).doesNotContain("submitter=");
        assertThat(logs.get(1)).doesNotContain("submitter=");
        logs = logTester.logs(ERROR);
        assertThat(logs).hasSize(1);
        assertThat(logs.iterator().next()).doesNotContain("submitter=");
        assertThat(logTester.logs(DEBUG)).isEmpty();
    }

    @Test
    public void log_submitter_login_if_authenticated_and_success() throws Exception {
        UserDto userDto = insertRandomUser();
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(CeWorkerImplTest.createCeTask(toTaskSubmitter(userDto))));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        underTest.call();
        verifyWorkerUuid();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains(String.format("submitter=%s", userDto.getLogin()));
        assertThat(logs.get(1)).contains(String.format("submitter=%s | status=SUCCESS | time=", userDto.getLogin()));
        assertThat(logTester.logs(ERROR)).isEmpty();
        assertThat(logTester.logs(DEBUG)).isEmpty();
    }

    @Test
    public void log_submitterUuid_if_user_matching_submitterUuid_can_not_be_found() throws Exception {
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(CeWorkerImplTest.createCeTask(new CeTask.User("UUID_USER", null))));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        underTest.call();
        verifyWorkerUuid();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains("submitter=UUID_USER");
        assertThat(logs.get(1)).contains("submitter=UUID_USER | status=SUCCESS | time=");
        assertThat(logTester.logs(ERROR)).isEmpty();
        assertThat(logTester.logs(DEBUG)).isEmpty();
    }

    @Test
    public void display_submitterLogin_in_logs_when_set_in_case_of_error() throws Exception {
        UserDto userDto = insertRandomUser();
        CeTask ceTask = CeWorkerImplTest.createCeTask(toTaskSubmitter(userDto));
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(ceTask.getType(), taskProcessor);
        makeTaskProcessorFail(ceTask);
        underTest.call();
        verifyWorkerUuid();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains(String.format("submitter=%s", userDto.getLogin()));
        assertThat(logs.get(1)).contains(String.format("submitter=%s | status=FAILED | time=", userDto.getLogin()));
        logs = logTester.logs(ERROR);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0)).isEqualTo(("Failed to execute task " + (ceTask.getUuid())));
    }

    @Test
    public void display_start_stop_at_debug_level_for_console_if_DEBUG_is_enabled_and_task_successful() throws Exception {
        logTester.setLevel(DEBUG);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(CeWorkerImplTest.createCeTask(submitter)));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        underTest.call();
        verifyWorkerUuid();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains((" | submitter=" + (submitter.getLogin())));
        assertThat(logs.get(1)).contains(String.format(" | submitter=%s | status=SUCCESS | time=", submitter.getLogin()));
        assertThat(logTester.logs(ERROR)).isEmpty();
        assertThat(logTester.logs(DEBUG)).isEmpty();
    }

    @Test
    public void display_start_at_debug_level_stop_at_error_level_for_console_if_DEBUG_is_enabled_and_task_failed() throws Exception {
        logTester.setLevel(DEBUG);
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        makeTaskProcessorFail(ceTask);
        underTest.call();
        verifyWorkerUuid();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains((" | submitter=" + (submitter.getLogin())));
        assertThat(logs.get(1)).contains(String.format(" | submitter=%s | status=FAILED | time=", submitter.getLogin()));
        logs = logTester.logs(ERROR);
        assertThat(logs).hasSize(1);
        assertThat(logs.iterator().next()).isEqualTo(("Failed to execute task " + (ceTask.getUuid())));
        assertThat(logTester.logs(DEBUG)).isEmpty();
    }

    @Test
    public void call_sets_and_restores_thread_name_with_information_of_worker_when_there_is_no_task_to_process() throws Exception {
        String threadName = randomAlphabetic(3);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenAnswer(( invocation) -> {
            assertThat(Thread.currentThread().getName()).isEqualTo(((((("Worker " + (randomOrdinal)) + " (UUID=") + (workerUuid)) + ") on ") + threadName));
            return Optional.empty();
        });
        Thread newThread = createThreadNameVerifyingThread(threadName);
        newThread.start();
        newThread.join();
    }

    @Test
    public void call_sets_and_restores_thread_name_with_information_of_worker_when_a_task_is_processed() throws Exception {
        String threadName = randomAlphabetic(3);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenAnswer(( invocation) -> {
            assertThat(Thread.currentThread().getName()).isEqualTo(((((("Worker " + (randomOrdinal)) + " (UUID=") + (workerUuid)) + ") on ") + threadName));
            return Optional.of(createCeTask(submitter));
        });
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        Thread newThread = createThreadNameVerifyingThread(threadName);
        newThread.start();
        newThread.join();
    }

    @Test
    public void call_sets_and_restores_thread_name_with_information_of_worker_when_an_error_occurs() throws Exception {
        String threadName = randomAlphabetic(3);
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenAnswer(( invocation) -> {
            assertThat(Thread.currentThread().getName()).isEqualTo(((((("Worker " + (randomOrdinal)) + " (UUID=") + (workerUuid)) + ") on ") + threadName));
            return Optional.of(ceTask);
        });
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        makeTaskProcessorFail(ceTask);
        Thread newThread = createThreadNameVerifyingThread(threadName);
        newThread.start();
        newThread.join();
    }

    @Test
    public void call_sets_and_restores_thread_name_with_information_of_worker_when_worker_is_disabled() throws Exception {
        Mockito.reset(ceWorkerController);
        Mockito.when(ceWorkerController.isEnabled(underTest)).thenReturn(false);
        String threadName = randomAlphabetic(3);
        Thread newThread = createThreadNameVerifyingThread(threadName);
        newThread.start();
        newThread.join();
    }

    @Test
    public void log_error_when_task_fails_with_not_MessageException() throws Exception {
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        makeTaskProcessorFail(ceTask);
        underTest.call();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains((" | submitter=" + (submitter.getLogin())));
        assertThat(logs.get(1)).contains(String.format(" | submitter=%s | status=FAILED | time=", submitter.getLogin()));
        logs = logTester.logs(ERROR);
        assertThat(logs).hasSize(1);
        assertThat(logs.iterator().next()).isEqualTo(("Failed to execute task " + (ceTask.getUuid())));
    }

    @Test
    public void do_no_log_error_when_task_fails_with_MessageException() throws Exception {
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        makeTaskProcessorFail(ceTask, MessageException.of("simulate MessageException thrown by TaskProcessor#process"));
        underTest.call();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(1)).contains((" | submitter=" + (submitter.getLogin())));
        assertThat(logs.get(1)).contains(String.format(" | submitter=%s | status=FAILED | time=", submitter.getLogin()));
        assertThat(logTester.logs(ERROR)).isEmpty();
    }

    @Test
    public void do_no_log_error_when_task_fails_with_BillingValidationsException() throws Exception {
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        makeTaskProcessorFail(ceTask, new BillingValidations.BillingValidationsException("simulate MessageException thrown by TaskProcessor#process"));
        underTest.call();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(1)).contains((" | submitter=" + (submitter.getLogin())));
        assertThat(logs.get(1)).contains(String.format(" | submitter=%s | status=FAILED | time=", submitter.getLogin()));
        assertThat(logTester.logs(ERROR)).isEmpty();
    }

    @Test
    public void log_error_when_task_was_successful_but_ending_state_can_not_be_persisted_to_db() throws Exception {
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        Mockito.doThrow(new RuntimeException("Simulate queue#remove failing")).when(queue).remove(ceTask, SUCCESS, null, null);
        underTest.call();
        assertThat(logTester.logs(ERROR)).containsOnly((("Failed to finalize task with uuid '" + (ceTask.getUuid())) + "' and persist its state to db"));
    }

    @Test
    public void log_error_when_task_failed_and_ending_state_can_not_be_persisted_to_db() throws Exception {
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        IllegalStateException ex = makeTaskProcessorFail(ceTask);
        RuntimeException runtimeException = new RuntimeException("Simulate queue#remove failing");
        Mockito.doThrow(runtimeException).when(queue).remove(ceTask, FAILED, null, ex);
        underTest.call();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains((" | submitter=" + (submitter.getLogin())));
        assertThat(logs.get(1)).contains(String.format(" | submitter=%s | status=FAILED | time=", submitter.getLogin()));
        List<LogAndArguments> logAndArguments = logTester.getLogs(ERROR);
        assertThat(logAndArguments).hasSize(2);
        LogAndArguments executionErrorLog = logAndArguments.get(0);
        assertThat(executionErrorLog.getFormattedMsg()).isEqualTo(("Failed to execute task " + (ceTask.getUuid())));
        assertThat(executionErrorLog.getArgs().get()).containsOnly(ceTask.getUuid(), ex);
        LogAndArguments finalizingErrorLog = logAndArguments.get(1);
        assertThat(finalizingErrorLog.getFormattedMsg()).isEqualTo((("Failed to finalize task with uuid '" + (ceTask.getUuid())) + "' and persist its state to db"));
        Object arg1 = finalizingErrorLog.getArgs().get()[0];
        assertThat(arg1).isSameAs(runtimeException);
        assertThat(((Exception) (arg1)).getSuppressed()).containsOnly(ex);
    }

    @Test
    public void log_error_as_suppressed_when_task_failed_with_MessageException_and_ending_state_can_not_be_persisted_to_db() throws Exception {
        CeTask ceTask = CeWorkerImplTest.createCeTask(submitter);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(REPORT, taskProcessor);
        MessageException ex = makeTaskProcessorFail(ceTask, MessageException.of("simulate MessageException thrown by TaskProcessor#process"));
        RuntimeException runtimeException = new RuntimeException("Simulate queue#remove failing");
        Mockito.doThrow(runtimeException).when(queue).remove(ceTask, FAILED, null, ex);
        underTest.call();
        List<String> logs = logTester.logs(INFO);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0)).contains((" | submitter=" + (submitter.getLogin())));
        assertThat(logs.get(1)).contains(String.format(" | submitter=%s | status=FAILED | time=", submitter.getLogin()));
        List<LogAndArguments> logAndArguments = logTester.getLogs(ERROR);
        assertThat(logAndArguments).hasSize(1);
        assertThat(logAndArguments.get(0).getFormattedMsg()).isEqualTo((("Failed to finalize task with uuid '" + (ceTask.getUuid())) + "' and persist its state to db"));
        Object arg1 = logAndArguments.get(0).getArgs().get()[0];
        assertThat(arg1).isSameAs(runtimeException);
        assertThat(((Exception) (arg1)).getSuppressed()).containsOnly(ex);
    }

    @Test
    public void isExecutedBy_returns_false_when_no_interaction_with_instance() {
        assertThat(underTest.isExecutedBy(Thread.currentThread())).isFalse();
        assertThat(underTest.isExecutedBy(new Thread())).isFalse();
    }

    @Test
    public void isExecutedBy_returns_false_unless_a_thread_is_currently_calling_call() throws InterruptedException {
        CountDownLatch inCallLatch = new CountDownLatch(1);
        CountDownLatch assertionsDoneLatch = new CountDownLatch(1);
        // mock long running peek(String) call => Thread is executing call() but not running a task
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenAnswer(((Answer<Optional<CeTask>>) (( invocation) -> {
            inCallLatch.countDown();
            try {
                assertionsDoneLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.empty();
        })));
        Thread t = callCallInNewThread(underTest);
        try {
            t.start();
            inCallLatch.await(10, TimeUnit.SECONDS);
            assertThat(underTest.isExecutedBy(Thread.currentThread())).isFalse();
            assertThat(underTest.isExecutedBy(new Thread())).isFalse();
            assertThat(underTest.isExecutedBy(t)).isTrue();
        } finally {
            assertionsDoneLatch.countDown();
            t.join();
        }
        assertThat(underTest.isExecutedBy(Thread.currentThread())).isFalse();
        assertThat(underTest.isExecutedBy(new Thread())).isFalse();
        assertThat(underTest.isExecutedBy(t)).isFalse();
    }

    @Test
    public void isExecutedBy_returns_false_unless_a_thread_is_currently_executing_a_task() throws InterruptedException {
        CountDownLatch inCallLatch = new CountDownLatch(1);
        CountDownLatch assertionsDoneLatch = new CountDownLatch(1);
        String taskType = randomAlphabetic(12);
        CeTask ceTask = Mockito.mock(CeTask.class);
        Mockito.when(ceTask.getType()).thenReturn(taskType);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(taskType, new CeWorkerImplTest.SimpleCeTaskProcessor() {
            @CheckForNull
            @Override
            public CeTaskResult process(CeTask task) {
                inCallLatch.countDown();
                try {
                    assertionsDoneLatch.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });
        Thread t = callCallInNewThread(underTest);
        try {
            t.start();
            inCallLatch.await(10, TimeUnit.SECONDS);
            assertThat(underTest.isExecutedBy(Thread.currentThread())).isFalse();
            assertThat(underTest.isExecutedBy(new Thread())).isFalse();
            assertThat(underTest.isExecutedBy(t)).isTrue();
        } finally {
            assertionsDoneLatch.countDown();
            t.join();
        }
        assertThat(underTest.isExecutedBy(Thread.currentThread())).isFalse();
        assertThat(underTest.isExecutedBy(new Thread())).isFalse();
        assertThat(underTest.isExecutedBy(t)).isFalse();
    }

    @Test
    public void getCurrentTask_returns_empty_when_no_interaction_with_instance() {
        assertThat(underTest.getCurrentTask()).isEmpty();
    }

    @Test
    public void getCurrentTask_returns_empty_when_a_thread_is_currently_calling_call_but_not_executing_a_task() throws InterruptedException {
        CountDownLatch inCallLatch = new CountDownLatch(1);
        CountDownLatch assertionsDoneLatch = new CountDownLatch(1);
        // mock long running peek(String) call => Thread is executing call() but not running a task
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenAnswer(((Answer<Optional<CeTask>>) (( invocation) -> {
            inCallLatch.countDown();
            try {
                assertionsDoneLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.empty();
        })));
        Thread t = callCallInNewThread(underTest);
        try {
            t.start();
            inCallLatch.await(10, TimeUnit.SECONDS);
            assertThat(underTest.getCurrentTask()).isEmpty();
        } finally {
            assertionsDoneLatch.countDown();
            t.join();
        }
        assertThat(underTest.getCurrentTask()).isEmpty();
    }

    @Test
    public void getCurrentTask_returns_empty_unless_a_thread_is_currently_executing_a_task() throws InterruptedException {
        CountDownLatch inCallLatch = new CountDownLatch(1);
        CountDownLatch assertionsDoneLatch = new CountDownLatch(1);
        String taskType = randomAlphabetic(12);
        CeTask ceTask = Mockito.mock(CeTask.class);
        Mockito.when(ceTask.getType()).thenReturn(taskType);
        Mockito.when(queue.peek(ArgumentMatchers.anyString())).thenReturn(Optional.of(ceTask));
        taskProcessorRepository.setProcessorForTask(taskType, new CeWorkerImplTest.SimpleCeTaskProcessor() {
            @CheckForNull
            @Override
            public CeTaskResult process(CeTask task) {
                inCallLatch.countDown();
                try {
                    assertionsDoneLatch.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });
        Thread t = callCallInNewThread(underTest);
        try {
            t.start();
            inCallLatch.await(10, TimeUnit.SECONDS);
            assertThat(underTest.getCurrentTask()).contains(ceTask);
        } finally {
            assertionsDoneLatch.countDown();
            t.join();
        }
        assertThat(underTest.getCurrentTask()).isEmpty();
    }

    private abstract static class SimpleCeTaskProcessor implements CeTaskProcessor {
        @Override
        public Set<String> getHandledCeTaskTypes() {
            throw new UnsupportedOperationException("getHandledCeTaskTypes should not be called");
        }
    }
}

