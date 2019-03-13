/**
 * Copyright 2017 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.core.execution;


import Status.CANCELED;
import Status.COMPLETED;
import Status.FAILED;
import Status.IN_PROGRESS;
import Status.SCHEDULED;
import Status.TIMED_OUT;
import TaskType.SIMPLE;
import TaskType.USER_DEFINED;
import TaskType.WAIT;
import Wait.NAME;
import Workflow.WorkflowStatus.RUNNING;
import Workflow.WorkflowStatus.TERMINATED;
import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.conductor.common.metadata.tasks.PollData;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.mapper.DeciderService;
import com.netflix.conductor.core.execution.tasks.Wait;
import com.netflix.conductor.core.execution.tasks.WorkflowSystemTask;
import com.netflix.conductor.core.orchestration.ExecutionDAOFacade;
import com.netflix.conductor.core.utils.IDGenerator;
import com.netflix.conductor.dao.MetadataDAO;
import com.netflix.conductor.dao.QueueDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Viren
 */
public class TestWorkflowExecutor {
    private WorkflowExecutor workflowExecutor;

    private ExecutionDAOFacade executionDAOFacade;

    private MetadataDAO metadataDAO;

    private QueueDAO queueDAO;

    private WorkflowStatusListener workflowStatusListener;

    private DeciderService deciderService;

    @Test
    public void testScheduleTask() {
        AtomicBoolean httpTaskExecuted = new AtomicBoolean(false);
        AtomicBoolean http2TaskExecuted = new AtomicBoolean(false);
        new Wait();
        new WorkflowSystemTask("HTTP") {
            @Override
            public boolean isAsync() {
                return true;
            }

            @Override
            public void start(Workflow workflow, Task task, WorkflowExecutor executor) {
                httpTaskExecuted.set(true);
                task.setStatus(COMPLETED);
                super.start(workflow, task, executor);
            }
        };
        new WorkflowSystemTask("HTTP2") {
            @Override
            public void start(Workflow workflow, Task task, WorkflowExecutor executor) {
                http2TaskExecuted.set(true);
                task.setStatus(COMPLETED);
                super.start(workflow, task, executor);
            }
        };
        Workflow workflow = new Workflow();
        workflow.setWorkflowId("1");
        List<Task> tasks = new LinkedList<>();
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setWorkflowTaskType(USER_DEFINED);
        taskToSchedule.setType("HTTP");
        WorkflowTask taskToSchedule2 = new WorkflowTask();
        taskToSchedule2.setWorkflowTaskType(USER_DEFINED);
        taskToSchedule2.setType("HTTP2");
        WorkflowTask wait = new WorkflowTask();
        wait.setWorkflowTaskType(WAIT);
        wait.setType("WAIT");
        wait.setTaskReferenceName("wait");
        Task task1 = new Task();
        task1.setTaskType(taskToSchedule.getType());
        task1.setTaskDefName(taskToSchedule.getName());
        task1.setReferenceTaskName(taskToSchedule.getTaskReferenceName());
        task1.setWorkflowInstanceId(workflow.getWorkflowId());
        task1.setCorrelationId(workflow.getCorrelationId());
        task1.setScheduledTime(System.currentTimeMillis());
        task1.setTaskId(IDGenerator.generate());
        task1.setInputData(new HashMap());
        task1.setStatus(SCHEDULED);
        task1.setRetryCount(0);
        task1.setCallbackAfterSeconds(taskToSchedule.getStartDelay());
        task1.setWorkflowTask(taskToSchedule);
        Task task2 = new Task();
        task2.setTaskType(NAME);
        task2.setTaskDefName(taskToSchedule.getName());
        task2.setReferenceTaskName(taskToSchedule.getTaskReferenceName());
        task2.setWorkflowInstanceId(workflow.getWorkflowId());
        task2.setCorrelationId(workflow.getCorrelationId());
        task2.setScheduledTime(System.currentTimeMillis());
        task2.setInputData(new HashMap());
        task2.setTaskId(IDGenerator.generate());
        task2.setStatus(IN_PROGRESS);
        task2.setWorkflowTask(taskToSchedule);
        Task task3 = new Task();
        task3.setTaskType(taskToSchedule2.getType());
        task3.setTaskDefName(taskToSchedule.getName());
        task3.setReferenceTaskName(taskToSchedule.getTaskReferenceName());
        task3.setWorkflowInstanceId(workflow.getWorkflowId());
        task3.setCorrelationId(workflow.getCorrelationId());
        task3.setScheduledTime(System.currentTimeMillis());
        task3.setTaskId(IDGenerator.generate());
        task3.setInputData(new HashMap());
        task3.setStatus(SCHEDULED);
        task3.setRetryCount(0);
        task3.setCallbackAfterSeconds(taskToSchedule.getStartDelay());
        task3.setWorkflowTask(taskToSchedule);
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        Mockito.when(executionDAOFacade.createTasks(tasks)).thenReturn(tasks);
        AtomicInteger startedTaskCount = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            startedTaskCount.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateTask(ArgumentMatchers.any());
        AtomicInteger queuedTaskCount = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            String queueName = getArgumentAt(0, String.class);
            System.out.println(queueName);
            queuedTaskCount.incrementAndGet();
            return null;
        }).when(queueDAO).push(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        boolean stateChanged = workflowExecutor.scheduleTask(workflow, tasks);
        Assert.assertEquals(2, startedTaskCount.get());
        Assert.assertEquals(1, queuedTaskCount.get());
        Assert.assertTrue(stateChanged);
        Assert.assertFalse(httpTaskExecuted.get());
        Assert.assertTrue(http2TaskExecuted.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompleteWorkflow() {
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.setWorkflowId("1");
        workflow.setStatus(RUNNING);
        workflow.setOwnerApp("junit_test");
        workflow.setStartTime(10L);
        workflow.setEndTime(100L);
        workflow.setOutput(Collections.EMPTY_MAP);
        Mockito.when(executionDAOFacade.getWorkflowById(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        AtomicInteger updateWorkflowCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            updateWorkflowCalledCounter.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateWorkflow(ArgumentMatchers.any());
        AtomicInteger updateTasksCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            updateTasksCalledCounter.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateTasks(ArgumentMatchers.any());
        AtomicInteger removeQueueEntryCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            removeQueueEntryCalledCounter.incrementAndGet();
            return null;
        }).when(queueDAO).remove(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        workflowExecutor.completeWorkflow(workflow);
        Assert.assertEquals(Workflow.WorkflowStatus.COMPLETED, workflow.getStatus());
        Assert.assertEquals(1, updateWorkflowCalledCounter.get());
        Assert.assertEquals(1, updateTasksCalledCounter.get());
        Assert.assertEquals(1, removeQueueEntryCalledCounter.get());
        Mockito.verify(workflowStatusListener, Mockito.times(0)).onWorkflowCompleted(ArgumentMatchers.any(Workflow.class));
        def.setWorkflowStatusListenerEnabled(true);
        workflow.setStatus(RUNNING);
        workflowExecutor.completeWorkflow(workflow);
        Mockito.verify(workflowStatusListener, Mockito.times(1)).onWorkflowCompleted(ArgumentMatchers.any(Workflow.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTerminatedWorkflow() {
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.setWorkflowId("1");
        workflow.setStatus(RUNNING);
        workflow.setOwnerApp("junit_test");
        workflow.setStartTime(10L);
        workflow.setEndTime(100L);
        workflow.setOutput(Collections.EMPTY_MAP);
        Mockito.when(executionDAOFacade.getWorkflowById(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        AtomicInteger updateWorkflowCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            updateWorkflowCalledCounter.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateWorkflow(ArgumentMatchers.any());
        AtomicInteger updateTasksCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            updateTasksCalledCounter.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateTasks(ArgumentMatchers.any());
        AtomicInteger removeQueueEntryCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            removeQueueEntryCalledCounter.incrementAndGet();
            return null;
        }).when(queueDAO).remove(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        workflowExecutor.terminateWorkflow("workflowId", "reason");
        Assert.assertEquals(TERMINATED, workflow.getStatus());
        Assert.assertEquals(1, updateWorkflowCalledCounter.get());
        Assert.assertEquals(1, removeQueueEntryCalledCounter.get());
        Mockito.verify(workflowStatusListener, Mockito.times(0)).onWorkflowTerminated(ArgumentMatchers.any(Workflow.class));
        def.setWorkflowStatusListenerEnabled(true);
        workflow.setStatus(RUNNING);
        workflowExecutor.completeWorkflow(workflow);
        Mockito.verify(workflowStatusListener, Mockito.times(1)).onWorkflowCompleted(ArgumentMatchers.any(Workflow.class));
    }

    @Test
    public void testRestartWorkflow() {
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("test_task");
        workflowTask.setTaskReferenceName("task_ref");
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("testDef");
        workflowDef.setVersion(1);
        workflowDef.setRestartable(true);
        workflowDef.getTasks().addAll(Collections.singletonList(workflowTask));
        Task task_1 = new Task();
        task_1.setTaskId(UUID.randomUUID().toString());
        task_1.setSeq(1);
        task_1.setStatus(FAILED);
        task_1.setTaskDefName(workflowTask.getName());
        task_1.setReferenceTaskName(workflowTask.getTaskReferenceName());
        Task task_2 = new Task();
        task_2.setTaskId(UUID.randomUUID().toString());
        task_2.setSeq(2);
        task_2.setStatus(FAILED);
        task_2.setTaskDefName(workflowTask.getName());
        task_2.setReferenceTaskName(workflowTask.getTaskReferenceName());
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(workflowDef);
        workflow.setWorkflowId("test-workflow-id");
        workflow.getTasks().addAll(Arrays.asList(task_1, task_2));
        workflow.setStatus(Workflow.WorkflowStatus.FAILED);
        Mockito.when(executionDAOFacade.getWorkflowById(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        Mockito.doNothing().when(executionDAOFacade).removeTask(ArgumentMatchers.any());
        Mockito.when(metadataDAO.get(workflow.getWorkflowName(), workflow.getWorkflowVersion())).thenReturn(Optional.of(workflowDef));
        Mockito.when(metadataDAO.getTaskDef(workflowTask.getName())).thenReturn(new TaskDef());
        Mockito.when(executionDAOFacade.updateWorkflow(ArgumentMatchers.any())).thenReturn("");
        workflowExecutor.rewind(workflow.getWorkflowId(), false);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Mockito.verify(metadataDAO, Mockito.never()).getLatest(ArgumentMatchers.any());
        ArgumentCaptor<Workflow> argumentCaptor = ArgumentCaptor.forClass(Workflow.class);
        Mockito.verify(executionDAOFacade, Mockito.times(2)).updateWorkflow(argumentCaptor.capture());
        Assert.assertEquals(workflow.getWorkflowId(), argumentCaptor.getAllValues().get(1).getWorkflowId());
        Assert.assertEquals(workflow.getWorkflowDefinition(), argumentCaptor.getAllValues().get(1).getWorkflowDefinition());
        // add a new version of the workflow definition and restart with latest
        workflow.setStatus(Workflow.WorkflowStatus.COMPLETED);
        workflowDef = new WorkflowDef();
        workflowDef.setName("testDef");
        workflowDef.setVersion(2);
        workflowDef.setRestartable(true);
        workflowDef.getTasks().addAll(Collections.singletonList(workflowTask));
        Mockito.when(metadataDAO.getLatest(workflow.getWorkflowName())).thenReturn(Optional.of(workflowDef));
        workflowExecutor.rewind(workflow.getWorkflowId(), true);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Mockito.verify(metadataDAO, Mockito.times(1)).getLatest(ArgumentMatchers.anyString());
        argumentCaptor = ArgumentCaptor.forClass(Workflow.class);
        Mockito.verify(executionDAOFacade, Mockito.times(4)).updateWorkflow(argumentCaptor.capture());
        Assert.assertEquals(workflow.getWorkflowId(), argumentCaptor.getAllValues().get(3).getWorkflowId());
        Assert.assertEquals(workflowDef, argumentCaptor.getAllValues().get(3).getWorkflowDefinition());
    }

    @Test
    public void testGetFailedTasksToRetry() {
        // setup
        Task task_1_1 = new Task();
        task_1_1.setTaskId(UUID.randomUUID().toString());
        task_1_1.setSeq(1);
        task_1_1.setStatus(FAILED);
        task_1_1.setTaskDefName("task_1_def");
        task_1_1.setReferenceTaskName("task_1_ref_1");
        Task task_1_2 = new Task();
        task_1_2.setTaskId(UUID.randomUUID().toString());
        task_1_2.setSeq(10);
        task_1_2.setStatus(FAILED);
        task_1_2.setTaskDefName("task_1_def");
        task_1_2.setReferenceTaskName("task_1_ref_2");
        Task task_1_3_1 = new Task();
        task_1_3_1.setTaskId(UUID.randomUUID().toString());
        task_1_3_1.setSeq(100);
        task_1_3_1.setStatus(FAILED);
        task_1_3_1.setTaskDefName("task_1_def");
        task_1_3_1.setReferenceTaskName("task_1_ref_3");
        Task task_1_3_2 = new Task();
        task_1_3_2.setTaskId(UUID.randomUUID().toString());
        task_1_3_2.setSeq(101);
        task_1_3_2.setStatus(FAILED);
        task_1_3_2.setTaskDefName("task_1_def");
        task_1_3_2.setReferenceTaskName("task_1_ref_3");
        Task task_2_1 = new Task();
        task_2_1.setTaskId(UUID.randomUUID().toString());
        task_2_1.setSeq(2);
        task_2_1.setStatus(COMPLETED);
        task_2_1.setTaskDefName("task_2_def");
        task_2_1.setReferenceTaskName("task_2_ref_1");
        Task task_2_2 = new Task();
        task_2_2.setTaskId(UUID.randomUUID().toString());
        task_2_2.setSeq(20);
        task_2_2.setStatus(FAILED);
        task_2_2.setTaskDefName("task_2_def");
        task_2_2.setReferenceTaskName("task_2_ref_2");
        Task task_3_1 = new Task();
        task_3_1.setTaskId(UUID.randomUUID().toString());
        task_3_1.setSeq(20);
        task_3_1.setStatus(TIMED_OUT);
        task_3_1.setTaskDefName("task_3_def");
        task_3_1.setReferenceTaskName("task_3_ref_1");
        Workflow workflow = new Workflow();
        // 2 different task definitions
        workflow.setTasks(Arrays.asList(task_1_1, task_2_1));
        List<Task> tasks = workflowExecutor.getFailedTasksToRetry(workflow);
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals(task_1_1.getTaskId(), tasks.get(0).getTaskId());
        // 2 tasks with the same  definition but different reference numbers
        workflow.setTasks(Arrays.asList(task_1_3_1, task_1_3_2));
        tasks = workflowExecutor.getFailedTasksToRetry(workflow);
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals(task_1_3_2.getTaskId(), tasks.get(0).getTaskId());
        // 3 tasks with definitions and reference numbers
        workflow.setTasks(Arrays.asList(task_1_1, task_1_2, task_1_3_1, task_1_3_2, task_2_1, task_2_2, task_3_1));
        tasks = workflowExecutor.getFailedTasksToRetry(workflow);
        Assert.assertEquals(4, tasks.size());
        Assert.assertTrue(tasks.contains(task_1_1));
        Assert.assertTrue(tasks.contains(task_1_2));
        Assert.assertTrue(tasks.contains(task_2_2));
        Assert.assertTrue(tasks.contains(task_1_3_2));
    }

    @Test(expected = ApplicationException.class)
    public void testRetryNonTerminalWorkflow() {
        Workflow workflow = new Workflow();
        workflow.setWorkflowId("testRetryNonTerminalWorkflow");
        workflow.setStatus(Workflow.WorkflowStatus.COMPLETED);
        Mockito.when(executionDAOFacade.getWorkflowById(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        workflowExecutor.retry(workflow.getWorkflowId());
    }

    @Test(expected = ApplicationException.class)
    public void testRetryWorkflowNoTasks() {
        Workflow workflow = new Workflow();
        workflow.setWorkflowId("ApplicationException");
        workflow.setStatus(Workflow.WorkflowStatus.FAILED);
        // noinspection unchecked
        workflow.setTasks(new ArrayList());
        Mockito.when(executionDAOFacade.getWorkflowById(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        workflowExecutor.retry(workflow.getWorkflowId());
    }

    @Test
    public void testRetryWorkflow() {
        // setup
        Workflow workflow = new Workflow();
        workflow.setWorkflowId("testRetryWorkflowId");
        workflow.setWorkflowType("testRetryWorkflowId");
        workflow.setVersion(1);
        workflow.setOwnerApp("junit_testRetryWorkflowId");
        workflow.setStartTime(10L);
        workflow.setEndTime(100L);
        // noinspection unchecked
        workflow.setOutput(Collections.EMPTY_MAP);
        workflow.setStatus(Workflow.WorkflowStatus.FAILED);
        AtomicInteger updateWorkflowCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            updateWorkflowCalledCounter.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateWorkflow(ArgumentMatchers.any());
        AtomicInteger updateTasksCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            updateTasksCalledCounter.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateTasks(ArgumentMatchers.any());
        AtomicInteger updateTaskCalledCounter = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            updateTaskCalledCounter.incrementAndGet();
            return null;
        }).when(executionDAOFacade).updateTask(ArgumentMatchers.any());
        // add 2 failed task in 2 forks and 1 cancelled in the 3rd fork
        Task task_1_1 = new Task();
        task_1_1.setTaskId(UUID.randomUUID().toString());
        task_1_1.setSeq(20);
        task_1_1.setRetryCount(0);
        task_1_1.setTaskType(SIMPLE.toString());
        task_1_1.setStatus(CANCELED);
        task_1_1.setTaskDefName("task1");
        task_1_1.setReferenceTaskName("task1_ref1");
        Task task_1_2 = new Task();
        task_1_2.setTaskId(UUID.randomUUID().toString());
        task_1_2.setSeq(21);
        task_1_2.setRetryCount(1);
        task_1_2.setTaskType(SIMPLE.toString());
        task_1_2.setStatus(FAILED);
        task_1_2.setTaskDefName("task1");
        task_1_2.setReferenceTaskName("task1_ref1");
        Task task_2_1 = new Task();
        task_2_1.setTaskId(UUID.randomUUID().toString());
        task_2_1.setSeq(22);
        task_2_1.setRetryCount(0);
        task_2_1.setStatus(FAILED);
        task_2_1.setTaskType(SIMPLE.toString());
        task_2_1.setTaskDefName("task2");
        task_2_1.setReferenceTaskName("task2_ref1");
        Task task_3_1 = new Task();
        task_3_1.setTaskId(UUID.randomUUID().toString());
        task_3_1.setSeq(23);
        task_3_1.setRetryCount(0);
        task_3_1.setStatus(CANCELED);
        task_3_1.setTaskType(SIMPLE.toString());
        task_3_1.setTaskDefName("task3");
        task_3_1.setReferenceTaskName("task3_ref1");
        Task task_4_1 = new Task();
        task_4_1.setTaskId(UUID.randomUUID().toString());
        task_4_1.setSeq(122);
        task_4_1.setRetryCount(0);
        task_4_1.setStatus(FAILED);
        task_4_1.setTaskType(SIMPLE.toString());
        task_4_1.setTaskDefName("task1");
        task_4_1.setReferenceTaskName("task4_refABC");
        workflow.getTasks().addAll(Arrays.asList(task_1_1, task_1_2, task_2_1, task_3_1, task_4_1));
        // end of setup
        // when:
        Mockito.when(executionDAOFacade.getWorkflowById(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        WorkflowDef workflowDef = new WorkflowDef();
        Mockito.when(metadataDAO.get(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(Optional.of(workflowDef));
        workflowExecutor.retry(workflow.getWorkflowId());
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(1, updateWorkflowCalledCounter.get());
        Assert.assertEquals(1, updateTasksCalledCounter.get());
        Assert.assertEquals(0, updateTaskCalledCounter.get());
    }

    @Test
    public void testGetActiveDomain() {
        String taskType = "test-task";
        String[] domains = new String[]{ "domain1", "domain2" };
        PollData pollData1 = new PollData("queue1", domains[0], "worker1", ((System.currentTimeMillis()) - (99 * 1000)));
        Mockito.when(executionDAOFacade.getTaskPollDataByDomain(taskType, domains[0])).thenReturn(pollData1);
        String activeDomain = workflowExecutor.getActiveDomain(taskType, domains);
        Assert.assertEquals(domains[0], activeDomain);
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        PollData pollData2 = new PollData("queue2", domains[1], "worker2", ((System.currentTimeMillis()) - (99 * 1000)));
        Mockito.when(executionDAOFacade.getTaskPollDataByDomain(taskType, domains[1])).thenReturn(pollData2);
        activeDomain = workflowExecutor.getActiveDomain(taskType, domains);
        Assert.assertEquals(domains[1], activeDomain);
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        activeDomain = workflowExecutor.getActiveDomain(taskType, domains);
        Assert.assertEquals(domains[1], activeDomain);
        domains = new String[]{ "" };
        Mockito.when(executionDAOFacade.getTaskPollDataByDomain(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new PollData());
        activeDomain = workflowExecutor.getActiveDomain(taskType, domains);
        Assert.assertNotNull(activeDomain);
        Assert.assertEquals("", activeDomain);
        domains = new String[]{  };
        activeDomain = workflowExecutor.getActiveDomain(taskType, domains);
        Assert.assertNull(activeDomain);
        activeDomain = workflowExecutor.getActiveDomain(taskType, null);
        Assert.assertNull(activeDomain);
    }

    @Test
    public void testDedupAndAddTasks() {
        Workflow workflow = new Workflow();
        Task task1 = new Task();
        task1.setReferenceTaskName("task1");
        task1.setRetryCount(1);
        Task task2 = new Task();
        task2.setReferenceTaskName("task2");
        task2.setRetryCount(2);
        List<Task> tasks = new ArrayList(Arrays.asList(task1, task2));
        List<Task> taskList = workflowExecutor.dedupAndAddTasks(workflow, tasks);
        Assert.assertEquals(2, taskList.size());
        Assert.assertEquals(tasks, taskList);
        Assert.assertEquals(workflow.getTasks(), taskList);
        // Adding the same tasks again
        taskList = workflowExecutor.dedupAndAddTasks(workflow, tasks);
        Assert.assertEquals(0, taskList.size());
        Assert.assertEquals(workflow.getTasks(), tasks);
        // Adding 2 new tasks
        Task newTask = new Task();
        newTask.setReferenceTaskName("newTask");
        newTask.setRetryCount(0);
        taskList = workflowExecutor.dedupAndAddTasks(workflow, Collections.singletonList(newTask));
        Assert.assertEquals(1, taskList.size());
        Assert.assertEquals(newTask, taskList.get(0));
        Assert.assertEquals(3, workflow.getTasks().size());
    }
}

