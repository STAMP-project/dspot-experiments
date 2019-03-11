/**
 * Copyright 2016 Netflix, Inc.
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
/**
 *
 */
package com.netflix.conductor.tests.integration;


import RetryLogic.FIXED;
import Status.SKIPPED;
import SubWorkflow.NAME;
import SubWorkflow.SUB_WORKFLOW_ID;
import SystemTaskType.DECISION;
import TaskResult.Status.FAILED_WITH_TERMINAL_ERROR;
import TaskType.EVENT;
import TaskType.LAMBDA;
import TaskType.SIMPLE;
import TaskType.WAIT;
import TimeoutPolicy.RETRY;
import WorkflowExecutor.DECIDER_QUEUE;
import WorkflowStatus.COMPLETED;
import WorkflowStatus.TERMINATED;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.conductor.common.metadata.tasks.PollData;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.workflow.DynamicForkJoinTaskList;
import com.netflix.conductor.common.metadata.workflow.RerunWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ApplicationException;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import com.netflix.conductor.core.execution.WorkflowSweeper;
import com.netflix.conductor.core.execution.tasks.SubWorkflow;
import com.netflix.conductor.core.metadata.MetadataMapperService;
import com.netflix.conductor.dao.QueueDAO;
import com.netflix.conductor.service.ExecutionService;
import com.netflix.conductor.service.MetadataService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractWorkflowServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractWorkflowServiceTest.class);

    private static final String COND_TASK_WF = "ConditionalTaskWF";

    private static final String FORK_JOIN_NESTED_WF = "FanInOutNestedTest";

    private static final String FORK_JOIN_WF = "FanInOutTest";

    private static final String DYNAMIC_FORK_JOIN_WF = "DynamicFanInOutTest";

    private static final String DYNAMIC_FORK_JOIN_WF_LEGACY = "DynamicFanInOutTestLegacy";

    private static final int RETRY_COUNT = 1;

    private static final String JUNIT_TEST_WF_NON_RESTARTABLE = "junit_test_wf_non_restartable";

    private static final String WF_WITH_SUB_WF = "WorkflowWithSubWorkflow";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Inject
    protected ExecutionService workflowExecutionService;

    @Inject
    protected SubWorkflow subworkflow;

    @Inject
    protected MetadataService metadataService;

    @Inject
    protected WorkflowSweeper workflowSweeper;

    @Inject
    protected QueueDAO queueDAO;

    @Inject
    protected WorkflowExecutor workflowExecutor;

    @Inject
    protected MetadataMapperService metadataMapperService;

    private static boolean registered;

    private static List<TaskDef> taskDefs;

    private static final String LINEAR_WORKFLOW_T1_T2 = "junit_test_wf";

    private static final String LINEAR_WORKFLOW_T1_T2_SW = "junit_test_wf_sw";

    private static final String LONG_RUNNING = "longRunningWf";

    private static final String TEST_WORKFLOW_NAME_3 = "junit_test_wf3";

    @Test
    public void testWorkflowWithNoTasks() {
        WorkflowDef empty = new WorkflowDef();
        empty.setName("empty_workflow");
        empty.setSchemaVersion(2);
        metadataService.registerWorkflowDef(empty);
        String id = startOrLoadWorkflowExecution(empty.getName(), 1, "testWorkflowWithNoTasks", new HashMap(), null, null);
        Assert.assertNotNull(id);
        Workflow workflow = workflowExecutionService.getExecutionStatus(id, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertEquals(0, workflow.getTasks().size());
    }

    @Test
    public void testTaskDefTemplate() throws Exception {
        System.setProperty("STACK2", "test_stack");
        TaskDef templatedTask = new TaskDef();
        templatedTask.setName("templated_task");
        Map<String, Object> httpRequest = new HashMap<>();
        httpRequest.put("method", "GET");
        httpRequest.put("vipStack", "${STACK2}");
        httpRequest.put("uri", "/get/something");
        Map<String, Object> body = new HashMap<>();
        body.put("inputPaths", Arrays.asList("${workflow.input.path1}", "${workflow.input.path2}"));
        body.put("requestDetails", "${workflow.input.requestDetails}");
        body.put("outputPath", "${workflow.input.outputPath}");
        httpRequest.put("body", body);
        templatedTask.getInputTemplate().put("http_request", httpRequest);
        metadataService.registerTaskDef(Arrays.asList(templatedTask));
        WorkflowDef templateWf = new WorkflowDef();
        templateWf.setName("template_workflow");
        WorkflowTask wft = new WorkflowTask();
        wft.setName(templatedTask.getName());
        wft.setWorkflowTaskType(SIMPLE);
        wft.setTaskReferenceName("t0");
        templateWf.getTasks().add(wft);
        templateWf.setSchemaVersion(2);
        metadataService.registerWorkflowDef(templateWf);
        Map<String, Object> requestDetails = new HashMap<>();
        requestDetails.put("key1", "value1");
        requestDetails.put("key2", 42);
        Map<String, Object> input = new HashMap<>();
        input.put("path1", "file://path1");
        input.put("path2", "file://path2");
        input.put("outputPath", "s3://bucket/outputPath");
        input.put("requestDetails", requestDetails);
        String id = startOrLoadWorkflowExecution(templateWf.getName(), 1, "testTaskDefTemplate", input, null, null);
        Assert.assertNotNull(id);
        Workflow workflow = workflowExecutionService.getExecutionStatus(id, true);
        Assert.assertNotNull(workflow);
        Assert.assertTrue(workflow.getReasonForIncompletion(), (!(workflow.getStatus().isTerminal())));
        Assert.assertEquals(1, workflow.getTasks().size());
        Task task = workflow.getTasks().get(0);
        Map<String, Object> taskInput = task.getInputData();
        Assert.assertNotNull(taskInput);
        Assert.assertTrue(taskInput.containsKey("http_request"));
        Assert.assertTrue(((taskInput.get("http_request")) instanceof Map));
        ObjectMapper om = new ObjectMapper();
        // Use the commented sysout to get the string value
        // System.out.println(om.writeValueAsString(om.writeValueAsString(taskInput)));
        String expected = "{\"http_request\":{\"method\":\"GET\",\"vipStack\":\"test_stack\",\"body\":{\"requestDetails\":{\"key1\":\"value1\",\"key2\":42},\"outputPath\":\"s3://bucket/outputPath\",\"inputPaths\":[\"file://path1\",\"file://path2\"]},\"uri\":\"/get/something\"}}";
        Assert.assertEquals(expected, om.writeValueAsString(taskInput));
    }

    @Test
    public void testWorkflowSchemaVersion() {
        WorkflowDef ver2 = new WorkflowDef();
        ver2.setSchemaVersion(2);
        ver2.setName("Test_schema_version2");
        ver2.setVersion(1);
        WorkflowDef ver1 = new WorkflowDef();
        ver1.setName("Test_schema_version1");
        ver1.setVersion(1);
        metadataService.updateWorkflowDef(ver1);
        metadataService.updateWorkflowDef(ver2);
        WorkflowDef found = metadataService.getWorkflowDef(ver2.getName(), 1);
        Assert.assertEquals(2, found.getSchemaVersion());
        WorkflowDef found1 = metadataService.getWorkflowDef(ver1.getName(), 1);
        Assert.assertEquals(2, found1.getSchemaVersion());
    }

    @Test
    public void testForkJoin() throws Exception {
        try {
            createForkJoinWorkflow();
        } catch (Exception e) {
        }
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        taskDef.setTimeoutSeconds(0);
        metadataService.updateTaskDef(taskDef);
        taskName = "junit_task_2";
        taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        taskDef.setTimeoutSeconds(0);
        metadataService.updateTaskDef(taskDef);
        taskName = "junit_task_3";
        taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        taskDef.setTimeoutSeconds(0);
        metadataService.updateTaskDef(taskDef);
        taskName = "junit_task_4";
        taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        taskDef.setTimeoutSeconds(0);
        metadataService.updateTaskDef(taskDef);
        Map<String, Object> input = new HashMap<>();
        String workflowId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.FORK_JOIN_WF, 1, "fanouttest", input, null, null);
        System.out.println(("testForkJoin.wfid=" + workflowId));
        printTaskStatuses(workflowId, "initiated");
        Task task1 = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNotNull(task1);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task1.getTaskId()));
        Task task2 = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertNotNull(task2);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task2.getTaskId()));
        Task task3 = workflowExecutionService.poll("junit_task_3", "test");
        Assert.assertNull(task3);
        task1.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task1);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("Found " + (workflow.getTasks())), RUNNING, workflow.getStatus());
        printTaskStatuses(workflow, "T1 completed");
        task3 = workflowExecutionService.poll("junit_task_3", "test");
        Assert.assertNotNull(task3);
        task2.setStatus(COMPLETED);
        task3.setStatus(COMPLETED);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> future1 = executorService.submit(() -> {
            try {
                workflowExecutionService.updateTask(task2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        future1.get();
        final Task _t3 = task3;
        Future<?> future2 = executorService.submit(() -> {
            try {
                workflowExecutionService.updateTask(_t3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        future2.get();
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        printTaskStatuses(workflow, "T2 T3 completed");
        Assert.assertEquals(("Found " + (workflow.getTasks())), RUNNING, workflow.getStatus());
        Assert.assertEquals(("Found " + (workflow.getTasks().stream().map(Task::getTaskType).collect(Collectors.toList()))), 6, workflow.getTasks().size());
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("Found " + (workflow.getTasks())), RUNNING, workflow.getStatus());
        Assert.assertTrue(("Found  " + (workflow.getTasks().stream().map(( t) -> ((t.getReferenceTaskName()) + ".") + (t.getStatus())).collect(Collectors.toList()))), workflow.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t4")));
        Task t4 = workflowExecutionService.poll("junit_task_4", "test");
        Assert.assertNotNull(t4);
        t4.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t4);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("Found " + (workflow.getTasks())), COMPLETED, workflow.getStatus());
        printTaskStatuses(workflow, "All completed");
    }

    @Test
    public void testForkJoinNestedSchemaVersion1() {
        createForkJoinNestedWorkflow(1);
        Map<String, Object> input = new HashMap<>();
        input.put("case", "a");// This should execute t16 and t19

        String wfid = startOrLoadWorkflowExecution("forkJoinNested", AbstractWorkflowServiceTest.FORK_JOIN_NESTED_WF, 1, "fork_join_nested_test", input, null, null);
        System.out.println(("testForkJoinNested.wfid=" + wfid));
        Workflow wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t11")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t12")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t13")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("fork1")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("fork2")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t16")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t1")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t2")));
        Task t1 = workflowExecutionService.poll("junit_task_11", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t1.getTaskId()));
        Task t2 = workflowExecutionService.poll("junit_task_12", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t2.getTaskId()));
        Task t3 = workflowExecutionService.poll("junit_task_13", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t3.getTaskId()));
        Assert.assertNotNull(t1);
        Assert.assertNotNull(t2);
        Assert.assertNotNull(t3);
        t1.setStatus(COMPLETED);
        t2.setStatus(COMPLETED);
        t3.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t1);
        workflowExecutionService.updateTask(t2);
        workflowExecutionService.updateTask(t3);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t16")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t14")));
        String[] tasks = new String[]{ "junit_task_14", "junit_task_16" };
        for (String tt : tasks) {
            Task polled = workflowExecutionService.poll(tt, "test");
            Assert.assertNotNull(("poll resulted empty for task: " + tt), polled);
            polled.setStatus(COMPLETED);
            workflowExecutionService.updateTask(polled);
        }
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t19")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t15")));// Not there yet

        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t20")));// Not there yet

        Task task19 = workflowExecutionService.poll("junit_task_19", "test");
        Assert.assertNotNull(task19);
        task19.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task19);
        Task task20 = workflowExecutionService.poll("junit_task_20", "test");
        Assert.assertNotNull(task20);
        task20.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task20);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Set<String> pendingTasks = wf.getTasks().stream().filter(( t) -> !(t.getStatus().isTerminal())).map(( t) -> t.getReferenceTaskName()).collect(Collectors.toSet());
        Assert.assertTrue(("Found only this: " + pendingTasks), wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("join1")));
        pendingTasks = wf.getTasks().stream().filter(( t) -> !(t.getStatus().isTerminal())).map(( t) -> t.getReferenceTaskName()).collect(Collectors.toSet());
        Assert.assertTrue(("Found only this: " + pendingTasks), wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t15")));
        Task task15 = workflowExecutionService.poll("junit_task_15", "test");
        Assert.assertNotNull(task15);
        task15.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task15);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(COMPLETED, wf.getStatus());
    }

    @Test
    public void testForkJoinNestedSchemaVersion2() {
        createForkJoinNestedWorkflow(2);
        Map<String, Object> input = new HashMap<>();
        input.put("case", "a");// This should execute t16 and t19

        String wfid = startOrLoadWorkflowExecution("forkJoinNested", AbstractWorkflowServiceTest.FORK_JOIN_NESTED_WF, 1, "fork_join_nested_test", input, null, null);
        System.out.println(("testForkJoinNested.wfid=" + wfid));
        Workflow wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t11")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t12")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t13")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("fork1")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("fork2")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t16")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t1")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t2")));
        Task t1 = workflowExecutionService.poll("junit_task_11", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t1.getTaskId()));
        Task t2 = workflowExecutionService.poll("junit_task_12", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t2.getTaskId()));
        Task t3 = workflowExecutionService.poll("junit_task_13", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t3.getTaskId()));
        Assert.assertNotNull(t1);
        Assert.assertNotNull(t2);
        Assert.assertNotNull(t3);
        t1.setStatus(COMPLETED);
        t2.setStatus(COMPLETED);
        t3.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t1);
        workflowExecutionService.updateTask(t2);
        workflowExecutionService.updateTask(t3);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t16")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t14")));
        String[] tasks = new String[]{ "junit_task_14", "junit_task_16" };
        for (String tt : tasks) {
            Task polled = workflowExecutionService.poll(tt, "test");
            Assert.assertNotNull(("poll resulted empty for task: " + tt), polled);
            polled.setStatus(COMPLETED);
            workflowExecutionService.updateTask(polled);
        }
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t19")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t15")));// Not there yet

        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t20")));// Not there yet

        Task task19 = workflowExecutionService.poll("junit_task_19", "test");
        Assert.assertNotNull(task19);
        task19.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task19);
        Task task20 = workflowExecutionService.poll("junit_task_20", "test");
        Assert.assertNotNull(task20);
        task20.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task20);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Set<String> pendingTasks = wf.getTasks().stream().filter(( t) -> !(t.getStatus().isTerminal())).map(( t) -> t.getReferenceTaskName()).collect(Collectors.toSet());
        Assert.assertTrue(("Found only this: " + pendingTasks), wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("join1")));
        pendingTasks = wf.getTasks().stream().filter(( t) -> !(t.getStatus().isTerminal())).map(( t) -> t.getReferenceTaskName()).collect(Collectors.toSet());
        Assert.assertTrue(("Found only this: " + pendingTasks), wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t15")));
        Task task15 = workflowExecutionService.poll("junit_task_15", "test");
        Assert.assertNotNull(task15);
        task15.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task15);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(COMPLETED, wf.getStatus());
    }

    @Test
    public void testForkJoinNestedWithSubWorkflow() {
        createForkJoinNestedWorkflowWithSubworkflow(1);
        Map<String, Object> input = new HashMap<>();
        input.put("case", "a");// This should execute t16 and t19

        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.FORK_JOIN_NESTED_WF, 1, "fork_join_nested_test", input, null, null);
        System.out.println(("testForkJoinNested.wfid=" + wfid));
        Workflow wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t11")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t12")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t13")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("sw1")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("fork1")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("fork2")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t16")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t1")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t2")));
        Task t1 = workflowExecutionService.poll("junit_task_11", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t1.getTaskId()));
        Task t2 = workflowExecutionService.poll("junit_task_12", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t2.getTaskId()));
        Task t3 = workflowExecutionService.poll("junit_task_13", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t3.getTaskId()));
        Assert.assertNotNull(t1);
        Assert.assertNotNull(t2);
        Assert.assertNotNull(t3);
        t1.setStatus(COMPLETED);
        t2.setStatus(COMPLETED);
        t3.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t1);
        workflowExecutionService.updateTask(t2);
        workflowExecutionService.updateTask(t3);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t16")));
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t14")));
        String[] tasks = new String[]{ "junit_task_1", "junit_task_2", "junit_task_14", "junit_task_16" };
        for (String tt : tasks) {
            Task polled = workflowExecutionService.poll(tt, "test");
            Assert.assertNotNull(("poll resulted empty for task: " + tt), polled);
            polled.setStatus(COMPLETED);
            workflowExecutionService.updateTask(polled);
        }
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertTrue(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t19")));
        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t15")));// Not there yet

        Assert.assertFalse(wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t20")));// Not there yet

        Task task19 = workflowExecutionService.poll("junit_task_19", "test");
        Assert.assertNotNull(task19);
        task19.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task19);
        Task task20 = workflowExecutionService.poll("junit_task_20", "test");
        Assert.assertNotNull(task20);
        task20.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task20);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Set<String> pendingTasks = wf.getTasks().stream().filter(( t) -> !(t.getStatus().isTerminal())).map(( t) -> t.getReferenceTaskName()).collect(Collectors.toSet());
        Assert.assertTrue(("Found only this: " + pendingTasks), wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("join1")));
        pendingTasks = wf.getTasks().stream().filter(( t) -> !(t.getStatus().isTerminal())).map(( t) -> t.getReferenceTaskName()).collect(Collectors.toSet());
        Assert.assertTrue(("Found only this: " + pendingTasks), wf.getTasks().stream().anyMatch(( t) -> t.getReferenceTaskName().equals("t15")));
        Task task15 = workflowExecutionService.poll("junit_task_15", "test");
        Assert.assertNotNull(task15);
        task15.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task15);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(COMPLETED, wf.getStatus());
    }

    @Test
    public void testForkJoinFailure() {
        try {
            createForkJoinWorkflow();
        } catch (Exception e) {
        }
        String taskName = "junit_task_2";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        int retryCount = taskDef.getRetryCount();
        taskDef.setRetryCount(0);
        metadataService.updateTaskDef(taskDef);
        Map<String, Object> input = new HashMap<String, Object>();
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.FORK_JOIN_WF, 1, "fanouttest", input, null, null);
        System.out.println(("testForkJoinFailure.wfid=" + wfid));
        Task t1 = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertNotNull(t1);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t1.getTaskId()));
        Task t2 = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t2.getTaskId()));
        Task t3 = workflowExecutionService.poll("junit_task_3", "test");
        Assert.assertNull(t3);
        Assert.assertNotNull(t1);
        Assert.assertNotNull(t2);
        t1.setStatus(FAILED);
        t2.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t2);
        Workflow wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(("Found " + (wf.getTasks())), RUNNING, wf.getStatus());
        t3 = workflowExecutionService.poll("junit_task_3", "test");
        Assert.assertNotNull(t3);
        workflowExecutionService.updateTask(t1);
        wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(("Found " + (wf.getTasks())), WorkflowStatus.FAILED, wf.getStatus());
        taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(retryCount);
        metadataService.updateTaskDef(taskDef);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDynamicForkJoinLegacy() {
        try {
            createDynamicForkJoinWorkflowDefsLegacy(1);
        } catch (Exception e) {
        }
        Map<String, Object> input = new HashMap<String, Object>();
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.DYNAMIC_FORK_JOIN_WF_LEGACY, 1, "dynfanouttest1", input, null, null);
        System.out.println(("testDynamicForkJoinLegacy.wfid=" + wfid));
        Task t1 = workflowExecutionService.poll("junit_task_1", "test");
        // assertTrue(ess.ackTaskRecieved(t1.getTaskId(), "test"));
        DynamicForkJoinTaskList dynamicForkJoinTasks = new DynamicForkJoinTaskList();
        input = new HashMap<String, Object>();
        input.put("k1", "v1");
        dynamicForkJoinTasks.add("junit_task_2", null, "xdt1", input);
        HashMap<String, Object> input2 = new HashMap<String, Object>();
        input2.put("k2", "v2");
        dynamicForkJoinTasks.add("junit_task_3", null, "xdt2", input2);
        t1.getOutputData().put("dynamicTasks", dynamicForkJoinTasks);
        t1.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t1);
        Task t2 = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t2.getTaskId()));
        Assert.assertEquals("xdt1", t2.getReferenceTaskName());
        Assert.assertTrue(t2.getInputData().containsKey("k1"));
        Assert.assertEquals("v1", t2.getInputData().get("k1"));
        Map<String, Object> output = new HashMap<String, Object>();
        output.put("ok1", "ov1");
        t2.setOutputData(output);
        t2.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t2);
        Task t3 = workflowExecutionService.poll("junit_task_3", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t3.getTaskId()));
        Assert.assertEquals("xdt2", t3.getReferenceTaskName());
        Assert.assertTrue(t3.getInputData().containsKey("k2"));
        Assert.assertEquals("v2", t3.getInputData().get("k2"));
        output = new HashMap<>();
        output.put("ok1", "ov1");
        t3.setOutputData(output);
        t3.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t3);
        Workflow wf = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(COMPLETED, wf.getStatus());
        // Check the output
        Task joinTask = wf.getTaskByRefName("dynamicfanouttask_join");
        Assert.assertEquals(("Found:" + (joinTask.getOutputData())), 2, joinTask.getOutputData().keySet().size());
        Set<String> joinTaskOutput = joinTask.getOutputData().keySet();
        System.out.println(("joinTaskOutput=" + joinTaskOutput));
        for (String key : joinTask.getOutputData().keySet()) {
            Assert.assertTrue(((key.equals("xdt1")) || (key.equals("xdt2"))));
            Assert.assertEquals("ov1", ((Map<String, Object>) (joinTask.getOutputData().get(key))).get("ok1"));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDynamicForkJoin() {
        createDynamicForkJoinWorkflowDefs();
        String taskName = "junit_task_2";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        int retryCount = taskDef.getRetryCount();
        taskDef.setRetryCount(2);
        taskDef.setRetryDelaySeconds(0);
        taskDef.setRetryLogic(FIXED);
        metadataService.updateTaskDef(taskDef);
        Map<String, Object> workflowInput = new HashMap<>();
        String workflowId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.DYNAMIC_FORK_JOIN_WF, 1, "dynfanouttest1", workflowInput, null, null);
        System.out.println(("testDynamicForkJoin.wfid=" + workflowId));
        Workflow workflow = workflowExecutor.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getReasonForIncompletion(), RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());
        Task task1 = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNotNull(task1);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task1.getTaskId()));
        Assert.assertEquals("dt1", task1.getReferenceTaskName());
        Map<String, Object> inputParams2 = new HashMap<>();
        inputParams2.put("k1", "v1");
        WorkflowTask workflowTask2 = new WorkflowTask();
        workflowTask2.setName("junit_task_2");
        workflowTask2.setTaskReferenceName("xdt1");
        Map<String, Object> inputParams3 = new HashMap<>();
        inputParams3.put("k2", "v2");
        WorkflowTask workflowTask3 = new WorkflowTask();
        workflowTask3.setName("junit_task_3");
        workflowTask3.setTaskReferenceName("xdt2");
        HashMap<String, Object> dynamicTasksInput = new HashMap<>();
        dynamicTasksInput.put("xdt1", inputParams2);
        dynamicTasksInput.put("xdt2", inputParams3);
        task1.getOutputData().put("dynamicTasks", Arrays.asList(workflowTask2, workflowTask3));
        task1.getOutputData().put("dynamicTasksInput", dynamicTasksInput);
        task1.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task1);
        workflow = workflowExecutor.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("Found " + (workflow.getTasks().stream().map(Task::getTaskType).collect(Collectors.toList()))), 5, workflow.getTasks().size());
        Task task2 = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task2.getTaskId()));
        Assert.assertEquals("xdt1", task2.getReferenceTaskName());
        Assert.assertTrue(task2.getInputData().containsKey("k1"));
        Assert.assertEquals("v1", task2.getInputData().get("k1"));
        Map<String, Object> output = new HashMap<>();
        output.put("ok1", "ov1");
        task2.setOutputData(output);
        task2.setStatus(FAILED);
        workflowExecutionService.updateTask(task2);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getReasonForIncompletion(), RUNNING, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().stream().filter(( t) -> t.getTaskType().equals("junit_task_2")).count());
        Assert.assertTrue(workflow.getTasks().stream().filter(( t) -> t.getTaskType().equals("junit_task_2")).allMatch(( t) -> (t.getWorkflowTask()) != null));
        Assert.assertEquals(("Found " + (workflow.getTasks().stream().map(Task::getTaskType).collect(Collectors.toList()))), 6, workflow.getTasks().size());
        task2 = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task2.getTaskId()));
        Assert.assertEquals("xdt1", task2.getReferenceTaskName());
        Assert.assertTrue(task2.getInputData().containsKey("k1"));
        Assert.assertEquals("v1", task2.getInputData().get("k1"));
        task2.setOutputData(output);
        task2.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task2);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("Found " + (workflow.getTasks().stream().map(Task::getTaskType).collect(Collectors.toList()))), 6, workflow.getTasks().size());
        Task task3 = workflowExecutionService.poll("junit_task_3", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task3.getTaskId()));
        Assert.assertEquals("xdt2", task3.getReferenceTaskName());
        Assert.assertTrue(task3.getInputData().containsKey("k2"));
        Assert.assertEquals("v2", task3.getInputData().get("k2"));
        output = new HashMap<>();
        output.put("ok1", "ov1");
        task3.setOutputData(output);
        task3.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task3);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getReasonForIncompletion(), RUNNING, workflow.getStatus());
        Assert.assertEquals(("Found " + (workflow.getTasks().stream().map(Task::getTaskType).collect(Collectors.toList()))), 7, workflow.getTasks().size());
        Task task4 = workflowExecutionService.poll("junit_task_4", "test");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task4.getTaskId()));
        Assert.assertEquals("task4", task4.getReferenceTaskName());
        task4.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task4);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getReasonForIncompletion(), COMPLETED, workflow.getStatus());
        Assert.assertEquals(("Found " + (workflow.getTasks().stream().map(Task::getTaskType).collect(Collectors.toList()))), 7, workflow.getTasks().size());
        // Check the output
        Task joinTask = workflow.getTaskByRefName("dynamicfanouttask_join");
        Assert.assertEquals(("Found:" + (joinTask.getOutputData())), 2, joinTask.getOutputData().keySet().size());
        Set<String> joinTaskOutput = joinTask.getOutputData().keySet();
        System.out.println(("joinTaskOutput=" + joinTaskOutput));
        for (String key : joinTask.getOutputData().keySet()) {
            Assert.assertTrue(((key.equals("xdt1")) || (key.equals("xdt2"))));
            Assert.assertEquals("ov1", ((Map<String, Object>) (joinTask.getOutputData().get(key))).get("ok1"));
        }
        // reset the task def
        taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(retryCount);
        taskDef.setRetryDelaySeconds(1);
        metadataService.updateTaskDef(taskDef);
    }

    @Test
    public void testDefDAO() {
        List<TaskDef> taskDefs = metadataService.getTaskDefs();
        Assert.assertNotNull(taskDefs);
        Assert.assertTrue((!(taskDefs.isEmpty())));
    }

    @Test
    public void testSimpleWorkflowFailureWithTerminalError() {
        clearWorkflows();
        TaskDef taskDef = notFoundSafeGetTaskDef("junit_task_1");
        taskDef.setRetryCount(1);
        metadataService.updateTaskDef(taskDef);
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(found);
        Map<String, Object> outputParameters = found.getOutputParameters();
        outputParameters.put("validationErrors", "${t1.output.ErrorMessage}");
        metadataService.updateWorkflowDef(found);
        String correlationId = "unit_test_1";
        Map<String, Object> input = new HashMap<>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String workflowInstanceId = startOrLoadWorkflowExecution("simpleWorkflowFailureWithTerminalError", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        AbstractWorkflowServiceTest.logger.info("testSimpleWorkflow.wfid= {}", workflowInstanceId);
        Assert.assertNotNull(workflowInstanceId);
        Workflow es = workflowExecutionService.getExecutionStatus(workflowInstanceId, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(es.getReasonForIncompletion(), RUNNING, es.getStatus());
        es = workflowExecutionService.getExecutionStatus(workflowInstanceId, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Assert.assertEquals(1, es.getTasks().size());// The very first task is the one that should be scheduled.

        boolean failed = false;
        try {
            workflowExecutor.rewind(workflowInstanceId, false);
        } catch (ApplicationException ae) {
            failed = true;
        }
        Assert.assertTrue(failed);
        // Polling for the first task should return the same task as before
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_1", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowInstanceId, task.getWorkflowInstanceId());
        TaskResult taskResult = new TaskResult(task);
        taskResult.setReasonForIncompletion("NON TRANSIENT ERROR OCCURRED: An integration point required to complete the task is down");
        taskResult.setStatus(FAILED_WITH_TERMINAL_ERROR);
        taskResult.addOutputData("TERMINAL_ERROR", "Integration endpoint down: FOOBAR");
        taskResult.addOutputData("ErrorMessage", "There was a terminal error");
        workflowExecutionService.updateTask(taskResult);
        workflowExecutor.decide(workflowInstanceId);
        es = workflowExecutionService.getExecutionStatus(workflowInstanceId, true);
        TaskDef junit_task_1 = notFoundSafeGetTaskDef("junit_task_1");
        Task t1 = es.getTaskByRefName("t1");
        Assert.assertNotNull(es);
        Assert.assertEquals(WorkflowStatus.FAILED, es.getStatus());
        Assert.assertEquals("NON TRANSIENT ERROR OCCURRED: An integration point required to complete the task is down", es.getReasonForIncompletion());
        Assert.assertEquals(1, junit_task_1.getRetryCount());// Configured retries at the task definition level

        Assert.assertEquals(0, t1.getRetryCount());// Actual retries done on the task

        Assert.assertEquals(true, es.getOutput().containsKey("o1"));
        Assert.assertEquals("p1 value", es.getOutput().get("o1"));
        Assert.assertEquals(es.getOutput().get("validationErrors").toString(), "There was a terminal error");
        outputParameters.remove("validationErrors");
        metadataService.updateWorkflowDef(found);
    }

    @Test
    public void testSimpleWorkflow() {
        clearWorkflows();
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_1";
        Map<String, Object> input = new HashMap<>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String workflowInstanceId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        AbstractWorkflowServiceTest.logger.info("testSimpleWorkflow.wfid= {}", workflowInstanceId);
        Assert.assertNotNull(workflowInstanceId);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowInstanceId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getReasonForIncompletion(), RUNNING, workflow.getStatus());
        workflow = workflowExecutionService.getExecutionStatus(workflowInstanceId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());// The very first task is the one that should be scheduled.

        boolean failed = false;
        try {
            workflowExecutor.rewind(workflowInstanceId, false);
        } catch (ApplicationException ae) {
            failed = true;
        }
        Assert.assertTrue(failed);
        // Polling for the first task
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_1", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowInstanceId, task.getWorkflowInstanceId());
        workflowExecutor.decide(workflowInstanceId);
        String task1Op = "task1.Done";
        List<Task> tasks = workflowExecutionService.getTasks(task.getTaskType(), null, 1);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        task = tasks.get(0);
        Assert.assertEquals(workflowInstanceId, task.getWorkflowInstanceId());
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowInstanceId, false);
        Assert.assertNotNull(workflow);
        Assert.assertNotNull(workflow.getOutput());
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(("Found=" + (task.getInputData())), task2Input);
        Assert.assertEquals(task1Op, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowInstanceId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        tasks = workflow.getTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(2, tasks.size());
        Assert.assertTrue(("Found " + (workflow.getOutput().toString())), workflow.getOutput().containsKey("o3"));
        Assert.assertEquals("task1.Done", workflow.getOutput().get("o3"));
    }

    @Test
    public void testSimpleWorkflowWithResponseTimeout() throws Exception {
        createWFWithResponseTimeout();
        String correlationId = "unit_test_1";
        Map<String, Object> workflowInput = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        workflowInput.put("param1", inputParam1);
        workflowInput.put("param2", "p2 value");
        String workflowId = startOrLoadWorkflowExecution("RTOWF", 1, correlationId, workflowInput, null, null);
        AbstractWorkflowServiceTest.logger.debug(("testSimpleWorkflowWithResponseTimeout.wfid=" + workflowId));
        Assert.assertNotNull(workflowId);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());// The very first task is the one that should be scheduled.

        Assert.assertEquals(1, queueDAO.getSize("task_rt"));
        // Polling for the first task should return the first task
        Task task = workflowExecutionService.poll("task_rt", "task1.junit.worker.testTimeout");
        Assert.assertNotNull(task);
        Assert.assertEquals("task_rt", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        // As the task_rt is out of the queue, the next poll should not get it
        Task nullTask = workflowExecutionService.poll("task_rt", "task1.junit.worker.testTimeout");
        Assert.assertNull(nullTask);
        Thread.sleep(10000);
        workflowExecutor.decide(workflowId);
        Assert.assertEquals(1, queueDAO.getSize("task_rt"));
        // The first task would be timed_out and a new task will be scheduled
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        Assert.assertTrue(workflow.getTasks().stream().allMatch(( t) -> t.getReferenceTaskName().equals("task_rt_t1")));
        Assert.assertEquals(TIMED_OUT, workflow.getTasks().get(0).getStatus());
        Assert.assertEquals(SCHEDULED, workflow.getTasks().get(1).getStatus());
        // Polling now should get the same task back because it should have been put back in the queue
        Task taskAgain = workflowExecutionService.poll("task_rt", "task1.junit.worker");
        Assert.assertNotNull(taskAgain);
        // update task with callback after seconds greater than the response timeout
        taskAgain.setStatus(IN_PROGRESS);
        taskAgain.setCallbackAfterSeconds(20);
        workflowExecutionService.updateTask(taskAgain);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(WorkflowStatus.RUNNING, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        Assert.assertEquals(IN_PROGRESS, workflow.getTasks().get(1).getStatus());
        // wait for callback after seconds which is longer than response timeout seconds and then call decide
        Thread.sleep(20000);
        workflowExecutor.decide(workflowId);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        // Poll for task again
        taskAgain = workflowExecutionService.poll("task_rt", "task1.junit.worker");
        Assert.assertNotNull(taskAgain);
        taskAgain.getOutputData().put("op", "task1.Done");
        taskAgain.setStatus(COMPLETED);
        workflowExecutionService.updateTask(taskAgain);
        // poll for next task
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker.testTimeout");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
    }

    @Test
    public void testWorkflowRerunWithSubWorkflows() {
        // Execute a workflow with sub-workflow
        String workflowId = this.runWorkflowWithSubworkflow();
        // Check it completed
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        // Now lets pickup the first task in the sub workflow and rerun it from there
        String subWorkflowId = null;
        for (Task task : workflow.getTasks()) {
            if (task.getTaskType().equalsIgnoreCase(NAME)) {
                subWorkflowId = task.getOutputData().get(SUB_WORKFLOW_ID).toString();
            }
        }
        Assert.assertNotNull(subWorkflowId);
        Workflow subWorkflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Task subWorkflowTask1 = null;
        for (Task task : subWorkflow.getTasks()) {
            if (task.getTaskDefName().equalsIgnoreCase("junit_task_1")) {
                subWorkflowTask1 = task;
            }
        }
        Assert.assertNotNull(subWorkflowTask1);
        RerunWorkflowRequest rerunWorkflowRequest = new RerunWorkflowRequest();
        rerunWorkflowRequest.setReRunFromTaskId(subWorkflowTask1.getTaskId());
        Map<String, Object> newInput = new HashMap<>();
        newInput.put("p1", "1");
        newInput.put("p2", "2");
        rerunWorkflowRequest.setTaskInput(newInput);
        String correlationId = "unit_test_sw_new";
        Map<String, Object> input = new HashMap<>();
        input.put("param1", "New p1 value");
        input.put("param2", "New p2 value");
        rerunWorkflowRequest.setCorrelationId(correlationId);
        rerunWorkflowRequest.setWorkflowInput(input);
        rerunWorkflowRequest.setReRunFromWorkflowId(workflowId);
        rerunWorkflowRequest.setReRunFromTaskId(subWorkflowTask1.getTaskId());
        // Rerun
        workflowExecutor.rerun(rerunWorkflowRequest);
        // The main WF and the sub WF should be in RUNNING state
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        Assert.assertEquals(correlationId, workflow.getCorrelationId());
        Assert.assertEquals("New p1 value", workflow.getInput().get("param1"));
        Assert.assertEquals("New p2 value", workflow.getInput().get("param2"));
        subWorkflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(subWorkflow);
        Assert.assertEquals(RUNNING, subWorkflow.getStatus());
        // Since we are re running from the sub workflow task, there
        // should be only 1 task that is SCHEDULED
        Assert.assertEquals(1, subWorkflow.getTasks().size());
        Assert.assertEquals(SCHEDULED, subWorkflow.getTasks().get(0).getStatus());
        // Now execute the task
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(task.getInputData().get("p1").toString(), "1");
        Assert.assertEquals(task.getInputData().get("p2").toString(), "2");
        task.getOutputData().put("op", "junit_task_1.done");
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        subWorkflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(subWorkflow);
        Assert.assertEquals(RUNNING, subWorkflow.getStatus());
        Assert.assertEquals(2, subWorkflow.getTasks().size());
        // Poll for second task of the sub workflow and execute it
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task.getOutputData().put("op", "junit_task_2.done");
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // Now the sub workflow and the main workflow must have finished
        subWorkflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(subWorkflow);
        Assert.assertEquals(COMPLETED, subWorkflow.getStatus());
        Assert.assertEquals(2, subWorkflow.getTasks().size());
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
    }

    @Test
    public void testSimpleWorkflowWithTaskSpecificDomain() {
        long startTimeTimestamp = System.currentTimeMillis();
        clearWorkflows();
        createWorkflowDefForDomain();
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2_SW, 1);
        String correlationId = "unit_test_sw";
        Map<String, Object> input = new HashMap<>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        Map<String, String> taskToDomain = new HashMap<>();
        taskToDomain.put("junit_task_3", "domain1");
        taskToDomain.put("junit_task_2", "domain1");
        // Poll before so that a polling for this task is "active"
        Task task = workflowExecutionService.poll("junit_task_3", "task1.junit.worker", "domain1");
        Assert.assertNull(task);
        task = workflowExecutionService.poll("junit_task_2", "task1.junit.worker", "domain1");
        Assert.assertNull(task);
        String workflowId = startOrLoadWorkflowExecution("simpleWorkflowWithTaskSpecificDomain", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2_SW, 1, correlationId, input, null, taskToDomain);
        Assert.assertNotNull(workflowId);
        Workflow workflow = workflowExecutor.getWorkflow(workflowId, false);
        Assert.assertNotNull(workflow);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getReasonForIncompletion(), RUNNING, workflow.getStatus());
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());// The very first task is the one that should be scheduled.

        // Check Size
        Map<String, Integer> sizes = workflowExecutionService.getTaskQueueSizes(Arrays.asList("domain1:junit_task_3", "junit_task_3"));
        Assert.assertEquals(sizes.get("domain1:junit_task_3").intValue(), 1);
        Assert.assertEquals(sizes.get("junit_task_3").intValue(), 0);
        // Polling for the first task
        task = workflowExecutionService.poll("junit_task_3", "task1.junit.worker");
        Assert.assertNull(task);
        task = workflowExecutionService.poll("junit_task_3", "task1.junit.worker", "domain1");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_3", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        List<Task> tasks = workflowExecutionService.getTasks(task.getTaskType(), null, 10);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        task = tasks.get(0);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        String task1Op = "task1.Done";
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, false);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_1", task.getTaskType());
        workflow = workflowExecutionService.getExecutionStatus(workflowId, false);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertNotNull(workflow.getTaskToDomain());
        Assert.assertEquals(workflow.getTaskToDomain().size(), 2);
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker", "domain1");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        tasks = workflow.getTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(2, tasks.size());
        Assert.assertTrue(("Found " + (workflow.getOutput().toString())), workflow.getOutput().containsKey("o3"));
        Assert.assertEquals("task1.Done", workflow.getOutput().get("o3"));
        Predicate<PollData> pollDataWithinTestTimes = ( pollData) -> ((pollData.getLastPollTime()) != 0) && ((pollData.getLastPollTime()) > startTimeTimestamp);
        List<PollData> pollData = workflowExecutionService.getPollData("junit_task_3").stream().filter(pollDataWithinTestTimes).collect(Collectors.toList());
        Assert.assertEquals(2, pollData.size());
        for (PollData pd : pollData) {
            Assert.assertEquals(pd.getQueueName(), "junit_task_3");
            Assert.assertEquals(pd.getWorkerId(), "task1.junit.worker");
            Assert.assertTrue(((pd.getLastPollTime()) != 0));
            if ((pd.getDomain()) != null) {
                Assert.assertEquals(pd.getDomain(), "domain1");
            }
        }
        List<PollData> pdList = workflowExecutionService.getAllPollData().stream().filter(pollDataWithinTestTimes).collect(Collectors.toList());
        int count = 0;
        for (PollData pd : pdList) {
            if (pd.getQueueName().equals("junit_task_3")) {
                count++;
            }
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void testSimpleWorkflowWithAllTaskInOneDomain() {
        clearWorkflows();
        createWorkflowDefForDomain();
        WorkflowDef def = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2_SW, 1);
        String correlationId = "unit_test_sw";
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        Map<String, String> taskToDomain = new HashMap<String, String>();
        taskToDomain.put("*", "domain11,, domain12");
        // Poll before so that a polling for this task is "active"
        Task task = workflowExecutionService.poll("junit_task_3", "task1.junit.worker", "domain11");
        Assert.assertNull(task);
        task = workflowExecutionService.poll("junit_task_2", "task1.junit.worker", "domain12");
        Assert.assertNull(task);
        String workflowId = startOrLoadWorkflowExecution("simpleWorkflowWithTasksInOneDomain", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2_SW, 1, correlationId, input, null, taskToDomain);
        Assert.assertNotNull(workflowId);
        Workflow workflow = workflowExecutor.getWorkflow(workflowId, false);
        Assert.assertNotNull(workflow);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getReasonForIncompletion(), RUNNING, workflow.getStatus());
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());// The very first task is the one that should be scheduled.

        // Check Size
        Map<String, Integer> sizes = workflowExecutionService.getTaskQueueSizes(Arrays.asList("domain11:junit_task_3", "junit_task_3"));
        Assert.assertEquals(sizes.get("domain11:junit_task_3").intValue(), 1);
        Assert.assertEquals(sizes.get("junit_task_3").intValue(), 0);
        // Polling for the first task
        task = workflowExecutionService.poll("junit_task_3", "task1.junit.worker");
        Assert.assertNull(task);
        task = workflowExecutionService.poll("junit_task_3", "task1.junit.worker", "domain11");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_3", task.getTaskType());
        Assert.assertEquals("domain11", task.getDomain());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        List<Task> tasks = workflowExecutionService.getTasks(task.getTaskType(), null, 1);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        task = tasks.get(0);
        String task1Op = "task1.Done";
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNull(task);
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker", "domain12");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_1", task.getTaskType());
        workflow = workflowExecutionService.getExecutionStatus(workflowId, false);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertNotNull(workflow.getTaskToDomain());
        Assert.assertEquals(workflow.getTaskToDomain().size(), 1);
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker", "domain11");
        Assert.assertNull(task);
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker", "domain12");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertEquals("domain12", task.getDomain());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        tasks = workflow.getTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(2, tasks.size());
        Assert.assertTrue(("Found " + (workflow.getOutput().toString())), workflow.getOutput().containsKey("o3"));
        Assert.assertEquals("task1.Done", workflow.getOutput().get("o3"));
    }

    @Test
    public void testLongRunning() {
        clearWorkflows();
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LONG_RUNNING, 1);
        String correlationId = "unit_test_1";
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String workflowId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LONG_RUNNING, 1, correlationId, input, null, null);
        System.out.println(("testLongRunning.wfid=" + workflowId));
        Assert.assertNotNull(workflowId);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        // Check the queue
        Assert.assertEquals(Integer.valueOf(1), workflowExecutionService.getTaskQueueSizes(Collections.singletonList("junit_task_1")).get("junit_task_1"));
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Output = "task1.In.Progress";
        task.getOutputData().put("op", task1Output);
        task.setStatus(IN_PROGRESS);
        task.setCallbackAfterSeconds(5);
        workflowExecutionService.updateTask(task);
        String taskId = task.getTaskId();
        // Check the queue
        Assert.assertEquals(Integer.valueOf(1), workflowExecutionService.getTaskQueueSizes(Arrays.asList("junit_task_1")).get("junit_task_1"));
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        // Polling for next task should not return anything
        Task task2 = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNull(task2);
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNull(task);
        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        // Polling for the first task should return the same task as before
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(task.getTaskId(), taskId);
        task1Output = "task1.Done";
        List<Task> tasks = workflowExecutionService.getTasks(task.getTaskType(), null, 1);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task = tasks.get(0);
        task.getOutputData().put("op", task1Output);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(task1Output, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        tasks = workflow.getTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(2, tasks.size());
    }

    @Test
    public void testResetWorkflowInProgressTasks() {
        clearWorkflows();
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LONG_RUNNING, 1);
        String correlationId = "unit_test_1";
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LONG_RUNNING, 1, correlationId, input, null, null);
        System.out.println(("testLongRunning.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // Check the queue
        Assert.assertEquals(Integer.valueOf(1), workflowExecutionService.getTaskQueueSizes(Arrays.asList("junit_task_1")).get("junit_task_1"));
        // /
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Op = "task1.In.Progress";
        task.getOutputData().put("op", task1Op);
        task.setStatus(IN_PROGRESS);
        task.setCallbackAfterSeconds(3600);
        workflowExecutionService.updateTask(task);
        String taskId = task.getTaskId();
        // Check the queue
        Assert.assertEquals(Integer.valueOf(1), workflowExecutionService.getTaskQueueSizes(Arrays.asList("junit_task_1")).get("junit_task_1"));
        // /
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // Polling for next task should not return anything
        Task task2 = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNull(task2);
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNull(task);
        // Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        // Reset
        workflowExecutor.resetCallbacksForInProgressTasks(wfid);
        // Now Polling for the first task should return the same task as before
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(task.getTaskId(), taskId);
        Assert.assertEquals(task.getCallbackAfterSeconds(), 0);
        task1Op = "task1.Done";
        List<Task> tasks = workflowExecutionService.getTasks(task.getTaskType(), null, 1);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals(wfid, task.getWorkflowInstanceId());
        task = tasks.get(0);
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(task1Op, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        tasks = es.getTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(2, tasks.size());
    }

    @Test
    public void testConcurrentWorkflowExecutions() {
        int count = 3;
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_concurrrent";
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String[] wfids = new String[count];
        for (int i = 0; i < count; i++) {
            String wfid = startOrLoadWorkflowExecution("concurrentWorkflowExecutions", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
            System.out.println(("testConcurrentWorkflowExecutions.wfid=" + wfid));
            Assert.assertNotNull(wfid);
            List<String> ids = workflowExecutionService.getRunningWorkflows(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
            Assert.assertNotNull(ids);
            Assert.assertTrue(("found no ids: " + ids), ((ids.size()) > 0));// if there are concurrent tests running, this would be more than 1

            boolean foundId = false;
            for (String id : ids) {
                if (id.equals(wfid)) {
                    foundId = true;
                }
            }
            Assert.assertTrue(foundId);
            wfids[i] = wfid;
        }
        String task1Op = "";
        for (int i = 0; i < count; i++) {
            Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
            Assert.assertNotNull(task);
            Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
            String param1 = ((String) (task.getInputData().get("p1")));
            String param2 = ((String) (task.getInputData().get("p2")));
            Assert.assertNotNull(param1);
            Assert.assertNotNull(param2);
            Assert.assertEquals("p1 value", param1);
            Assert.assertEquals("p2 value", param2);
            task1Op = (("task1.output->" + param1) + ".") + param2;
            task.getOutputData().put("op", task1Op);
            task.setStatus(COMPLETED);
            workflowExecutionService.updateTask(task);
        }
        for (int i = 0; i < count; i++) {
            Task task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
            Assert.assertNotNull(task);
            Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
            String task2Input = ((String) (task.getInputData().get("tp2")));
            Assert.assertNotNull(task2Input);
            Assert.assertEquals(task1Op, task2Input);
            task2Input = ((String) (task.getInputData().get("tp1")));
            Assert.assertNotNull(task2Input);
            Assert.assertEquals(inputParam1, task2Input);
            task.setStatus(COMPLETED);
            workflowExecutionService.updateTask(task);
        }
        List<Workflow> wfs = workflowExecutionService.getWorkflowInstances(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, correlationId, false, false);
        wfs.forEach(( wf) -> {
            assertEquals(WorkflowStatus.COMPLETED, wf.getStatus());
        });
    }

    @Test
    public void testCaseStatementsSchemaVersion1() {
        createConditionalWF(1);
        String correlationId = "testCaseStatements: " + (System.currentTimeMillis());
        Map<String, Object> input = new HashMap<String, Object>();
        String wfid;
        String[] sequence;
        // default case
        input.put("param1", "xxx");
        input.put("param2", "two");
        wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Task task = workflowExecutionService.poll("junit_task_2", "junit");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        Assert.assertEquals(3, es.getTasks().size());
        // nested - one
        input.put("param1", "nested");
        input.put("param2", "one");
        wfid = startOrLoadWorkflowExecution(((AbstractWorkflowServiceTest.COND_TASK_WF) + 2), AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        sequence = new String[]{ "junit_task_1", "junit_task_3" };
        validate(wfid, sequence, new String[]{ DECISION.name(), DECISION.name(), "junit_task_1", "junit_task_3", DECISION.name() }, 5);
        // nested - two
        input.put("param1", "nested");
        input.put("param2", "two");
        wfid = startOrLoadWorkflowExecution(((AbstractWorkflowServiceTest.COND_TASK_WF) + 3), AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        sequence = new String[]{ "junit_task_2" };
        validate(wfid, sequence, new String[]{ DECISION.name(), DECISION.name(), "junit_task_2", DECISION.name() }, 4);
        // three
        input.put("param1", "three");
        input.put("param2", "two");
        input.put("finalCase", "notify");
        wfid = startOrLoadWorkflowExecution(((AbstractWorkflowServiceTest.COND_TASK_WF) + 4), AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        sequence = new String[]{ "junit_task_3", "junit_task_4" };
        validate(wfid, sequence, new String[]{ DECISION.name(), "junit_task_3", DECISION.name(), "junit_task_4" }, 3);
    }

    @Test
    public void testCaseStatementsSchemaVersion2() {
        createConditionalWF(2);
        String correlationId = "testCaseStatements: " + (System.currentTimeMillis());
        Map<String, Object> input = new HashMap<String, Object>();
        String wfid;
        String[] sequence;
        // default case
        input.put("param1", "xxx");
        input.put("param2", "two");
        wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Task task = workflowExecutionService.poll("junit_task_2", "junit");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        Assert.assertEquals(3, es.getTasks().size());
        // nested - one
        input.put("param1", "nested");
        input.put("param2", "one");
        wfid = startOrLoadWorkflowExecution(((AbstractWorkflowServiceTest.COND_TASK_WF) + 2), AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        sequence = new String[]{ "junit_task_1", "junit_task_3" };
        validate(wfid, sequence, new String[]{ DECISION.name(), DECISION.name(), "junit_task_1", "junit_task_3", DECISION.name() }, 5);
        // nested - two
        input.put("param1", "nested");
        input.put("param2", "two");
        wfid = startOrLoadWorkflowExecution(((AbstractWorkflowServiceTest.COND_TASK_WF) + 3), AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        sequence = new String[]{ "junit_task_2" };
        validate(wfid, sequence, new String[]{ DECISION.name(), DECISION.name(), "junit_task_2", DECISION.name() }, 4);
        // three
        input.put("param1", "three");
        input.put("param2", "two");
        input.put("finalCase", "notify");
        wfid = startOrLoadWorkflowExecution(((AbstractWorkflowServiceTest.COND_TASK_WF) + 4), AbstractWorkflowServiceTest.COND_TASK_WF, 1, correlationId, input, null, null);
        System.out.println(("testCaseStatements.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        sequence = new String[]{ "junit_task_3", "junit_task_4" };
        validate(wfid, sequence, new String[]{ DECISION.name(), "junit_task_3", DECISION.name(), "junit_task_4" }, 3);
    }

    @Test
    public void testRetries() {
        String taskName = "junit_task_2";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(2);
        taskDef.setRetryDelaySeconds(1);
        metadataService.updateTaskDef(taskDef);
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_1";
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        System.out.println(("testRetries.wfid=" + wfid));
        Assert.assertNotNull(wfid);
        List<String> ids = workflowExecutionService.getRunningWorkflows(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        Assert.assertNotNull(ids);
        Assert.assertTrue(("found no ids: " + ids), ((ids.size()) > 0));// if there are concurrent tests running, this would be more than 1

        boolean foundId = false;
        for (String id : ids) {
            if (id.equals(wfid)) {
                foundId = true;
            }
        }
        Assert.assertTrue(foundId);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Op = (("task1.output->" + param1) + ".") + param2;
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // fail the task twice and then succeed
        verify(inputParam1, wfid, task1Op, true);
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        verify(inputParam1, wfid, task1Op, false);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        Assert.assertEquals(3, es.getTasks().size());// task 1, and 2 of the task 2

        Assert.assertEquals("junit_task_1", es.getTasks().get(0).getTaskType());
        Assert.assertEquals("junit_task_2", es.getTasks().get(1).getTaskType());
        Assert.assertEquals("junit_task_2", es.getTasks().get(2).getTaskType());
        Assert.assertEquals(COMPLETED, es.getTasks().get(0).getStatus());
        Assert.assertEquals(FAILED, es.getTasks().get(1).getStatus());
        Assert.assertEquals(COMPLETED, es.getTasks().get(2).getStatus());
        Assert.assertEquals(es.getTasks().get(1).getTaskId(), es.getTasks().get(2).getRetriedTaskId());
    }

    @Test
    public void testSuccess() {
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        List<String> ids = workflowExecutionService.getRunningWorkflows(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        Assert.assertNotNull(ids);
        Assert.assertTrue(("found no ids: " + ids), ((ids.size()) > 0));// if there are concurrent tests running, this would be more than 1

        boolean foundId = false;
        for (String id : ids) {
            if (id.equals(wfid)) {
                foundId = true;
            }
        }
        Assert.assertTrue(foundId);
        /* @correlationId
        List<Workflow> byCorrelationId = ess.getWorkflowInstances(LINEAR_WORKFLOW_T1_T2, correlationId, false, false);
        assertNotNull(byCorrelationId);
        assertTrue(!byCorrelationId.isEmpty());
        assertEquals(1, byCorrelationId.size());
         */
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // The first task would be marked as scheduled
        Assert.assertEquals(1, es.getTasks().size());
        Assert.assertEquals(SCHEDULED, es.getTasks().get(0).getStatus());
        // decideNow should be idempotent if re-run on the same state!
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Assert.assertEquals(1, es.getTasks().size());
        Task t = es.getTasks().get(0);
        Assert.assertEquals(SCHEDULED, t.getStatus());
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertNotNull(task);
        Assert.assertEquals(t.getTaskId(), task.getTaskId());
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        t = es.getTasks().get(0);
        Assert.assertEquals(IN_PROGRESS, t.getStatus());
        String taskId = t.getTaskId();
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Op = (("task1.output->" + param1) + ".") + param2;
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // If we get the full workflow here then, last task should be completed and the next task should be scheduled
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        es.getTasks().forEach(( wfTask) -> {
            if (wfTask.getTaskId().equals(taskId)) {
                assertEquals(COMPLETED, wfTask.getStatus());
            } else {
                assertEquals(SCHEDULED, wfTask.getStatus());
            }
        });
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertNotNull(task);
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(task1Op, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        // Check the tasks, at this time there should be 2 task
        Assert.assertEquals(es.getTasks().size(), 2);
        es.getTasks().forEach(( wfTask) -> {
            assertEquals(wfTask.getStatus(), COMPLETED);
        });
        System.out.println(("Total tasks=" + (es.getTasks().size())));
        Assert.assertTrue(((es.getTasks().size()) < 10));
    }

    @Test
    public void testDeciderUpdate() {
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        Workflow workflow = workflowExecutor.getWorkflow(wfid, false);
        long updated1 = workflow.getUpdateTime();
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        workflowExecutor.decide(wfid);
        workflow = workflowExecutor.getWorkflow(wfid, false);
        long updated2 = workflow.getUpdateTime();
        Assert.assertEquals(updated1, updated2);
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        workflowExecutor.terminateWorkflow(wfid, "done");
        workflow = workflowExecutor.getWorkflow(wfid, false);
        updated2 = workflow.getUpdateTime();
        Assert.assertTrue((((("updated1[" + updated1) + "] >? updated2[") + updated2) + "]"), (updated2 > updated1));
    }

    @Test
    public void testDeciderMix() throws Exception {
        ExecutorService executors = Executors.newFixedThreadPool(3);
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        List<String> ids = workflowExecutionService.getRunningWorkflows(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        Assert.assertNotNull(ids);
        Assert.assertTrue(("found no ids: " + ids), ((ids.size()) > 0));// if there are concurrent tests running, this would be more than 1

        boolean foundId = false;
        for (String id : ids) {
            if (id.equals(wfid)) {
                foundId = true;
            }
        }
        Assert.assertTrue(foundId);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // The first task would be marked as scheduled
        Assert.assertEquals(1, es.getTasks().size());
        Assert.assertEquals(SCHEDULED, es.getTasks().get(0).getStatus());
        List<Future<Void>> futures = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(executors.submit(() -> {
                workflowExecutor.decide(wfid);
                return null;
            }));
        }
        for (Future<Void> future : futures) {
            future.get();
        }
        futures.clear();
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // The first task would be marked as scheduled
        Assert.assertEquals(1, es.getTasks().size());
        Assert.assertEquals(SCHEDULED, es.getTasks().get(0).getStatus());
        // decideNow should be idempotent if re-run on the same state!
        workflowExecutor.decide(wfid);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Assert.assertEquals(1, es.getTasks().size());
        Task t = es.getTasks().get(0);
        Assert.assertEquals(SCHEDULED, t.getStatus());
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertNotNull(task);
        Assert.assertEquals(t.getTaskId(), task.getTaskId());
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        t = es.getTasks().get(0);
        Assert.assertEquals(IN_PROGRESS, t.getStatus());
        String taskId = t.getTaskId();
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Op = (("task1.output->" + param1) + ".") + param2;
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // If we get the full workflow here then, last task should be completed and the next task should be scheduled
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        es.getTasks().forEach(( wfTask) -> {
            if (wfTask.getTaskId().equals(taskId)) {
                assertEquals(COMPLETED, wfTask.getStatus());
            } else {
                assertEquals(SCHEDULED, wfTask.getStatus());
            }
        });
        // Run sweep 10 times!
        for (int i = 0; i < 10; i++) {
            futures.add(executors.submit(() -> {
                long s = System.currentTimeMillis();
                workflowExecutor.decide(wfid);
                System.out.println((("Took " + ((System.currentTimeMillis()) - s)) + " ms to run decider"));
                return null;
            }));
        }
        for (Future<Void> future : futures) {
            future.get();
        }
        futures.clear();
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertEquals(RUNNING, es.getStatus());
        Assert.assertEquals(2, es.getTasks().size());
        System.out.println(("Workflow tasks=" + (es.getTasks())));
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertNotNull(task);
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(task1Op, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        // Check the tasks, at this time there should be 2 task
        Assert.assertEquals(es.getTasks().size(), 2);
        es.getTasks().forEach(( wfTask) -> {
            assertEquals(wfTask.getStatus(), COMPLETED);
        });
        System.out.println(("Total tasks=" + (es.getTasks().size())));
        Assert.assertTrue(((es.getTasks().size()) < 10));
    }

    @Test
    public void testFailures() {
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.FORK_JOIN_WF, 1);
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        metadataService.updateTaskDef(taskDef);
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(found.getFailureWorkflow());
        Assert.assertFalse(StringUtils.isBlank(found.getFailureWorkflow()));
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        input.put("failureWfName", "FanInOutTest");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        Task task = getTask("junit_task_1");
        Assert.assertNotNull(task);
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        // If we get the full workflow here then, last task should be completed and the next task should be scheduled
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(WorkflowStatus.FAILED, es.getStatus());
        taskDef.setRetryCount(AbstractWorkflowServiceTest.RETRY_COUNT);
        metadataService.updateTaskDef(taskDef);
    }

    @Test
    public void testRetryWithForkJoin() throws Exception {
        String workflowId = this.runAFailedForkJoinWF();
        workflowExecutor.retry(workflowId);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(workflow.getStatus(), RUNNING);
        printTaskStatuses(workflow, "After retry called");
        Task t2 = workflowExecutionService.poll("junit_task_0_RT_2", "test");
        Assert.assertNotNull(t2);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t2.getTaskId()));
        Task t3 = workflowExecutionService.poll("junit_task_0_RT_3", "test");
        Assert.assertNotNull(t3);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(t3.getTaskId()));
        t2.setStatus(COMPLETED);
        t3.setStatus(COMPLETED);
        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<?> future1 = es.submit(() -> {
            try {
                workflowExecutionService.updateTask(t2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        final Task _t3 = t3;
        Future<?> future2 = es.submit(() -> {
            try {
                workflowExecutionService.updateTask(_t3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        future1.get();
        future2.get();
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        printTaskStatuses(workflow, "T2, T3 complete");
        workflowExecutor.decide(workflowId);
        Task t4 = workflowExecutionService.poll("junit_task_0_RT_4", "test");
        Assert.assertNotNull(t4);
        t4.setStatus(COMPLETED);
        workflowExecutionService.updateTask(t4);
        printTaskStatuses(workflowId, "After complete");
    }

    @Test
    public void testRetry() {
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        int retryCount = taskDef.getRetryCount();
        taskDef.setRetryCount(1);
        int retryDelay = taskDef.getRetryDelaySeconds();
        taskDef.setRetryDelaySeconds(0);
        metadataService.updateTaskDef(taskDef);
        WorkflowDef workflowDef = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(workflowDef.getFailureWorkflow());
        Assert.assertFalse(StringUtils.isBlank(workflowDef.getFailureWorkflow()));
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<>();
        input.put("param1", "p1 value");
        input.put("param2", "p2 value");
        String workflowId = startOrLoadWorkflowExecution("retry", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(workflowId);
        printTaskStatuses(workflowId, "initial");
        Task task = getTask("junit_task_1");
        Assert.assertNotNull(task);
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_1");
        Assert.assertNotNull(task);
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(WorkflowStatus.FAILED, workflow.getStatus());
        printTaskStatuses(workflowId, "before retry");
        workflowExecutor.retry(workflowId);
        printTaskStatuses(workflowId, "after retry");
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_1");
        Assert.assertNotNull(task);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_2");
        Assert.assertNotNull(task);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertEquals(3, workflow.getTasks().stream().filter(( t) -> t.getTaskType().equals("junit_task_1")).count());
        taskDef.setRetryCount(retryCount);
        taskDef.setRetryDelaySeconds(retryDelay);
        metadataService.updateTaskDef(taskDef);
        printTaskStatuses(workflowId, "final");
    }

    @Test
    public void testNonRestartartableWorkflows() {
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        metadataService.updateTaskDef(taskDef);
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        found.setName(AbstractWorkflowServiceTest.JUNIT_TEST_WF_NON_RESTARTABLE);
        found.setRestartable(false);
        metadataService.updateWorkflowDef(found);
        Assert.assertNotNull(found);
        Assert.assertNotNull(found.getFailureWorkflow());
        Assert.assertFalse(StringUtils.isBlank(found.getFailureWorkflow()));
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String workflowId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.JUNIT_TEST_WF_NON_RESTARTABLE, 1, correlationId, input, null, null);
        Assert.assertNotNull(workflowId);
        Task task = getTask("junit_task_1");
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(WorkflowStatus.FAILED, workflow.getStatus());
        workflowExecutor.rewind(workflow.getWorkflowId(), false);
        // Polling for the first task should return the same task as before
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_1", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        List<Task> tasks = workflowExecutionService.getTasks(task.getTaskType(), null, 1);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        task = tasks.get(0);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        String task1Op = "task1.Done";
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, false);
        Assert.assertNotNull(workflow);
        Assert.assertNotNull(workflow.getOutput());
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(("Found=" + (task.getInputData())), task2Input);
        Assert.assertEquals(task1Op, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        task.setReasonForIncompletion("unit test failure");
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        tasks = workflow.getTasks();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(2, tasks.size());
        Assert.assertTrue(("Found " + (workflow.getOutput().toString())), workflow.getOutput().containsKey("o3"));
        Assert.assertEquals("task1.Done", workflow.getOutput().get("o3"));
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(String.format("is an instance of WorkflowDef: %s and version: %d and is non restartable", AbstractWorkflowServiceTest.JUNIT_TEST_WF_NON_RESTARTABLE, 1));
        workflowExecutor.rewind(workflow.getWorkflowId(), false);
    }

    @Test
    public void testRestart() {
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        metadataService.updateTaskDef(taskDef);
        WorkflowDef workflowDef = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(workflowDef.getFailureWorkflow());
        Assert.assertFalse(StringUtils.isBlank(workflowDef.getFailureWorkflow()));
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String workflowId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(workflowId);
        Task task = getTask("junit_task_1");
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        // If we get the full workflow here then, last task should be completed and the next task should be scheduled
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(WorkflowStatus.FAILED, workflow.getStatus());
        workflowExecutor.rewind(workflow.getWorkflowId(), false);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_1");
        Assert.assertNotNull(task);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_2");
        Assert.assertNotNull(task);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        // Add a new version of the definition with an additional task
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("junit_task_20");
        workflowTask.setTaskReferenceName("task_added");
        workflowTask.setWorkflowTaskType(SIMPLE);
        workflowDef.getTasks().add(workflowTask);
        workflowDef.setVersion(2);
        metadataService.updateWorkflowDef(workflowDef);
        // restart with the latest definition
        workflowExecutor.rewind(workflow.getWorkflowId(), true);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_1");
        Assert.assertNotNull(task);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_2");
        Assert.assertNotNull(task);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = getTask("junit_task_20");
        Assert.assertNotNull(task);
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        Assert.assertEquals("task_added", task.getReferenceTaskName());
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertEquals(3, workflow.getTasks().size());
        // cleanup
        metadataService.unregisterWorkflowDef(workflowDef.getName(), 2);
    }

    @Test
    public void testTimeout() throws Exception {
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(1);
        taskDef.setTimeoutSeconds(1);
        taskDef.setRetryDelaySeconds(0);
        taskDef.setTimeoutPolicy(RETRY);
        metadataService.updateTaskDef(taskDef);
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(found.getFailureWorkflow());
        Assert.assertFalse(StringUtils.isBlank(found.getFailureWorkflow()));
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        input.put("failureWfName", "FanInOutTest");
        String wfid = startOrLoadWorkflowExecution("timeout", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        // Ensure that we have a workflow queued up for evaluation here...
        long size = queueDAO.getSize(DECIDER_QUEUE);
        Assert.assertEquals(1, size);
        // If we get the full workflow here then, last task should be completed and the next task should be scheduled
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Assert.assertEquals(("fond: " + (es.getTasks().stream().map(Task::toString).collect(Collectors.toList()))), 1, es.getTasks().size());
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals(wfid, task.getWorkflowInstanceId());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        // Ensure that we have a workflow queued up for evaluation here...
        size = queueDAO.getSize(DECIDER_QUEUE);
        Assert.assertEquals(1, size);
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        workflowSweeper.sweep(Arrays.asList(wfid), workflowExecutor);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(("fond: " + (es.getTasks().stream().map(Task::toString).collect(Collectors.toList()))), 2, es.getTasks().size());
        Task task1 = es.getTasks().get(0);
        Assert.assertEquals(TIMED_OUT, task1.getStatus());
        Task task2 = es.getTasks().get(1);
        Assert.assertEquals(SCHEDULED, task2.getStatus());
        task = workflowExecutionService.poll(task2.getTaskDefName(), "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals(wfid, task.getWorkflowInstanceId());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        workflowExecutor.decide(wfid);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(2, es.getTasks().size());
        Assert.assertEquals(TIMED_OUT, es.getTasks().get(0).getStatus());
        Assert.assertEquals(TIMED_OUT, es.getTasks().get(1).getStatus());
        Assert.assertEquals(WorkflowStatus.TIMED_OUT, es.getStatus());
        Assert.assertEquals(1, queueDAO.getSize(DECIDER_QUEUE));
        taskDef.setTimeoutSeconds(0);
        taskDef.setRetryCount(AbstractWorkflowServiceTest.RETRY_COUNT);
        metadataService.updateTaskDef(taskDef);
    }

    @Test
    public void testReruns() {
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // Check the tasks, at this time there should be 1 task
        Assert.assertEquals(es.getTasks().size(), 1);
        Task t = es.getTasks().get(0);
        Assert.assertEquals(SCHEDULED, t.getStatus());
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(t.getTaskId(), task.getTaskId());
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Op = (("task1.output->" + param1) + ".") + param2;
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // If we get the full workflow here then, last task should be completed and the next task should be scheduled
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        es.getTasks().forEach(( wfTask) -> {
            if (wfTask.getTaskId().equals(t.getTaskId())) {
                assertEquals(wfTask.getStatus(), COMPLETED);
            } else {
                assertEquals(wfTask.getStatus(), SCHEDULED);
            }
        });
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(task1Op, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        // Now rerun lets rerun the workflow from the second task
        RerunWorkflowRequest request = new RerunWorkflowRequest();
        request.setReRunFromWorkflowId(wfid);
        request.setReRunFromTaskId(es.getTasks().get(1).getTaskId());
        String reRunwfid = workflowExecutor.rerun(request);
        Workflow esRR = workflowExecutionService.getExecutionStatus(reRunwfid, true);
        Assert.assertNotNull(esRR);
        Assert.assertEquals(esRR.getReasonForIncompletion(), RUNNING, esRR.getStatus());
        // Check the tasks, at this time there should be 2 tasks
        // first one is skipped and the second one is scheduled
        Assert.assertEquals(esRR.getTasks().toString(), 2, esRR.getTasks().size());
        Assert.assertEquals(COMPLETED, esRR.getTasks().get(0).getStatus());
        Task tRR = esRR.getTasks().get(1);
        Assert.assertEquals(esRR.getTasks().toString(), SCHEDULED, tRR.getStatus());
        Assert.assertEquals(tRR.getTaskType(), "junit_task_2");
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(task1Op, task2Input);
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(reRunwfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        // ////////////////////
        // Now rerun the entire workflow
        RerunWorkflowRequest request1 = new RerunWorkflowRequest();
        request1.setReRunFromWorkflowId(wfid);
        String reRunwfid1 = workflowExecutor.rerun(request1);
        es = workflowExecutionService.getExecutionStatus(reRunwfid1, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // Check the tasks, at this time there should be 1 task
        Assert.assertEquals(es.getTasks().size(), 1);
        Assert.assertEquals(SCHEDULED, es.getTasks().get(0).getStatus());
        task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
    }

    @Test
    public void testTaskSkipping() {
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(0);
        taskDef.setTimeoutSeconds(0);
        metadataService.updateTaskDef(taskDef);
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.TEST_WORKFLOW_NAME_3, 1);
        String correlationId = "unit_test_1" + (UUID.randomUUID().toString());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.TEST_WORKFLOW_NAME_3, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        // Now Skip the second task
        workflowExecutor.skipTaskFromWorkflow(wfid, "t2", null);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        // Check the tasks, at this time there should be 3 task
        Assert.assertEquals(2, es.getTasks().size());
        Assert.assertEquals(SCHEDULED, es.getTasks().get(0).getStatus());
        Assert.assertEquals(SKIPPED, es.getTasks().get(1).getStatus());
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals("t1", task.getReferenceTaskName());
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Op = (("task1.output->" + param1) + ".") + param2;
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // If we get the full workflow here then, last task should be completed and the next task should be scheduled
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        es.getTasks().forEach(( wfTask) -> {
            if (wfTask.getReferenceTaskName().equals("t1")) {
                assertEquals(COMPLETED, wfTask.getStatus());
            } else
                if (wfTask.getReferenceTaskName().equals("t2")) {
                    assertEquals(Status.SKIPPED, wfTask.getStatus());
                } else {
                    assertEquals(SCHEDULED, wfTask.getStatus());
                }

        });
        task = workflowExecutionService.poll("junit_task_3", "task3.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals(IN_PROGRESS, task.getStatus());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
    }

    @Test
    public void testPauseResume() {
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        String correlationId = "unit_test_1" + (System.nanoTime());
        Map<String, Object> input = new HashMap<String, Object>();
        String inputParam1 = "p1 value";
        input.put("param1", inputParam1);
        input.put("param2", "p2 value");
        String wfid = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, input, null, null);
        Assert.assertNotNull(wfid);
        List<String> ids = workflowExecutionService.getRunningWorkflows(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        Assert.assertNotNull(ids);
        Assert.assertTrue(("found no ids: " + ids), ((ids.size()) > 0));// if there are concurrent tests running, this would be more than 1

        boolean foundId = false;
        for (String id : ids) {
            if (id.equals(wfid)) {
                foundId = true;
            }
        }
        Assert.assertTrue(foundId);
        Workflow es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(RUNNING, es.getStatus());
        Task t = es.getTasks().get(0);
        Assert.assertEquals(SCHEDULED, t.getStatus());
        // PAUSE
        workflowExecutor.pauseWorkflow(wfid);
        // The workflow is paused but the scheduled task should be pollable
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(t.getTaskId(), task.getTaskId());
        String param1 = ((String) (task.getInputData().get("p1")));
        String param2 = ((String) (task.getInputData().get("p2")));
        Assert.assertNotNull(param1);
        Assert.assertNotNull(param2);
        Assert.assertEquals("p1 value", param1);
        Assert.assertEquals("p2 value", param2);
        String task1Op = (("task1.output->" + param1) + ".") + param2;
        task.getOutputData().put("op", task1Op);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // This decide should not schedule the next task
        // ds.decideNow(wfid, task);
        // If we get the full workflow here then, last task should be completed and the rest (including PAUSE task) should be scheduled
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        es.getTasks().forEach(( wfTask) -> {
            if (wfTask.getTaskId().equals(t.getTaskId())) {
                assertEquals(wfTask.getStatus(), COMPLETED);
            }
        });
        // This should return null as workflow is paused
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNull(("Found: " + task), task);
        // Even if decide is run again the next task will not be scheduled as the workflow is still paused--
        workflowExecutor.decide(wfid);
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertTrue((task == null));
        // RESUME
        workflowExecutor.resumeWorkflow(wfid);
        // Now polling should get the second task
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String task2Input = ((String) (task.getInputData().get("tp2")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(task1Op, task2Input);
        Task byRefName = workflowExecutionService.getPendingTaskForWorkflow("t2", wfid);
        Assert.assertNotNull(byRefName);
        Assert.assertEquals(task.getTaskId(), byRefName.getTaskId());
        task2Input = ((String) (task.getInputData().get("tp1")));
        Assert.assertNotNull(task2Input);
        Assert.assertEquals(inputParam1, task2Input);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfid, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
    }

    @Test
    public void testSubWorkflow() {
        createSubWorkflow();
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1);
        Map<String, Object> input = new HashMap<>();
        input.put("param1", "param 1 value");
        input.put("param3", "param 2 value");
        input.put("wfName", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        String wfId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1, "test", input, null, null);
        Assert.assertNotNull(wfId);
        Workflow es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertNotNull(es);
        Task task = workflowExecutionService.poll("junit_task_5", "test");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertNotNull(es);
        Assert.assertNotNull(es.getTasks());
        task = es.getTasks().stream().filter(( t) -> t.getTaskType().equals(TaskType.SUB_WORKFLOW.name())).findAny().get();
        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getOutputData());
        Assert.assertNotNull(((("Output: " + (task.getOutputData().toString())) + ", status: ") + (task.getStatus())), task.getOutputData().get("subWorkflowId"));
        Assert.assertNotNull(task.getInputData());
        Assert.assertTrue(task.getInputData().containsKey("workflowInput"));
        Assert.assertEquals(42, ((Map<String, Object>) (task.getInputData().get("workflowInput"))).get("param2"));
        String subWorkflowId = task.getOutputData().get("subWorkflowId").toString();
        es = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(es);
        Assert.assertNotNull(es.getTasks());
        Assert.assertEquals(wfId, es.getParentWorkflowId());
        Assert.assertEquals(RUNNING, es.getStatus());
        task = workflowExecutionService.poll("junit_task_1", "test");
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        task = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertEquals(subWorkflowId, task.getWorkflowInstanceId());
        String uuid = UUID.randomUUID().toString();
        task.getOutputData().put("uuid", uuid);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
        Assert.assertNotNull(es.getOutput());
        Assert.assertTrue(es.getOutput().containsKey("o1"));
        Assert.assertTrue(es.getOutput().containsKey("o2"));
        Assert.assertEquals("sub workflow input param1", es.getOutput().get("o1"));
        Assert.assertEquals(uuid, es.getOutput().get("o2"));
        task = workflowExecutionService.poll("junit_task_6", "test");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(COMPLETED, es.getStatus());
    }

    @Test
    public void testSubWorkflowFailure() {
        TaskDef taskDef = notFoundSafeGetTaskDef("junit_task_1");
        Assert.assertNotNull(taskDef);
        taskDef.setRetryCount(0);
        taskDef.setTimeoutSeconds(2);
        metadataService.updateTaskDef(taskDef);
        createSubWorkflow();
        metadataService.getWorkflowDef(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1);
        Map<String, Object> input = new HashMap<>();
        input.put("param1", "param 1 value");
        input.put("param3", "param 2 value");
        input.put("wfName", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        String wfId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1, "test", input, null, null);
        Assert.assertNotNull(wfId);
        Workflow es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertNotNull(es);
        Task task = workflowExecutionService.poll("junit_task_5", "test");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertNotNull(es);
        Assert.assertNotNull(es.getTasks());
        task = es.getTasks().stream().filter(( t) -> t.getTaskType().equals(TaskType.SUB_WORKFLOW.name())).findAny().get();
        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getOutputData());
        Assert.assertNotNull(task.getOutputData().get("subWorkflowId"));
        String subWorkflowId = task.getOutputData().get("subWorkflowId").toString();
        es = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(es);
        Assert.assertNotNull(es.getTasks());
        Assert.assertEquals(wfId, es.getParentWorkflowId());
        Assert.assertEquals(RUNNING, es.getStatus());
        task = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNotNull(task);
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        es = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(es);
        Assert.assertEquals(WorkflowStatus.FAILED, es.getStatus());
        workflowExecutor.executeSystemTask(subworkflow, es.getParentWorkflowTaskId(), 1);
        es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertEquals(WorkflowStatus.FAILED, es.getStatus());
        taskDef.setTimeoutSeconds(0);
        taskDef.setRetryCount(AbstractWorkflowServiceTest.RETRY_COUNT);
        metadataService.updateTaskDef(taskDef);
    }

    @Test
    public void testSubWorkflowFailureInverse() {
        TaskDef taskDef = notFoundSafeGetTaskDef("junit_task_1");
        Assert.assertNotNull(taskDef);
        taskDef.setRetryCount(0);
        taskDef.setTimeoutSeconds(2);
        metadataService.updateTaskDef(taskDef);
        createSubWorkflow();
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1);
        Assert.assertNotNull(found);
        Map<String, Object> input = new HashMap<>();
        input.put("param1", "param 1 value");
        input.put("param3", "param 2 value");
        input.put("wfName", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        String wfId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1, "test", input, null, null);
        Assert.assertNotNull(wfId);
        Workflow es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertNotNull(es);
        Task task = workflowExecutionService.poll("junit_task_5", "test");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertNotNull(es);
        Assert.assertNotNull(es.getTasks());
        task = es.getTasks().stream().filter(( t) -> t.getTaskType().equals(TaskType.SUB_WORKFLOW.name())).findAny().get();
        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getOutputData());
        Assert.assertNotNull(task.getOutputData().get("subWorkflowId"));
        String subWorkflowId = task.getOutputData().get("subWorkflowId").toString();
        es = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(es);
        Assert.assertNotNull(es.getTasks());
        Assert.assertEquals(wfId, es.getParentWorkflowId());
        Assert.assertEquals(RUNNING, es.getStatus());
        workflowExecutor.terminateWorkflow(wfId, "fail");
        es = workflowExecutionService.getExecutionStatus(wfId, true);
        Assert.assertEquals(TERMINATED, es.getStatus());
        es = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertEquals(TERMINATED, es.getStatus());
    }

    @Test
    public void testSubWorkflowRetry() {
        String taskName = "junit_task_1";
        TaskDef taskDef = notFoundSafeGetTaskDef(taskName);
        int retryCount = notFoundSafeGetTaskDef(taskName).getRetryCount();
        taskDef.setRetryCount(0);
        metadataService.updateTaskDef(taskDef);
        // create a workflow with sub-workflow
        createSubWorkflow();
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1);
        // start the workflow
        Map<String, Object> workflowInputParams = new HashMap<>();
        workflowInputParams.put("param1", "param 1");
        workflowInputParams.put("param3", "param 2");
        workflowInputParams.put("wfName", AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2);
        String workflowId = startOrLoadWorkflowExecution(AbstractWorkflowServiceTest.WF_WITH_SUB_WF, 1, "test", workflowInputParams, null, null);
        Assert.assertNotNull(workflowId);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        // poll and complete first task
        Task task = workflowExecutionService.poll("junit_task_5", "test");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertNotNull(workflow.getTasks());
        Assert.assertEquals(2, workflow.getTasks().size());
        task = workflow.getTasks().stream().filter(( t) -> t.getTaskType().equals(TaskType.SUB_WORKFLOW.name())).findAny().orElse(null);
        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getOutputData());
        Assert.assertNotNull(((("Output: " + (task.getOutputData().toString())) + ", status: ") + (task.getStatus())), task.getOutputData().get("subWorkflowId"));
        String subWorkflowId = task.getOutputData().get("subWorkflowId").toString();
        workflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertNotNull(workflow.getTasks());
        Assert.assertEquals(workflowId, workflow.getParentWorkflowId());
        Assert.assertEquals(RUNNING, workflow.getStatus());
        // poll and fail the first task in sub-workflow
        task = workflowExecutionService.poll("junit_task_1", "test");
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(WorkflowStatus.FAILED, workflow.getStatus());
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(WorkflowStatus.FAILED, workflow.getStatus());
        // Retry the failed sub workflow
        workflowExecutor.retry(subWorkflowId);
        task = workflowExecutionService.poll("junit_task_1", "test");
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertEquals(subWorkflowId, task.getWorkflowInstanceId());
        String uuid = UUID.randomUUID().toString();
        task.getOutputData().put("uuid", uuid);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(subWorkflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertNotNull(workflow.getOutput());
        Assert.assertTrue(workflow.getOutput().containsKey("o1"));
        Assert.assertTrue(workflow.getOutput().containsKey("o2"));
        Assert.assertEquals("sub workflow input param1", workflow.getOutput().get("o1"));
        Assert.assertEquals(uuid, workflow.getOutput().get("o2"));
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        task = workflowExecutionService.poll("junit_task_6", "test");
        Assert.assertNotNull(task);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        // reset retry count
        taskDef = notFoundSafeGetTaskDef(taskName);
        taskDef.setRetryCount(retryCount);
        metadataService.updateTaskDef(taskDef);
    }

    @Test
    public void testWait() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("test_wait");
        workflowDef.setSchemaVersion(2);
        WorkflowTask waitWorkflowTask = new WorkflowTask();
        waitWorkflowTask.setWorkflowTaskType(WAIT);
        waitWorkflowTask.setName("wait");
        waitWorkflowTask.setTaskReferenceName("wait0");
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("junit_task_1");
        workflowTask.setTaskReferenceName("t1");
        workflowDef.getTasks().add(waitWorkflowTask);
        workflowDef.getTasks().add(workflowTask);
        metadataService.registerWorkflowDef(workflowDef);
        String workflowId = startOrLoadWorkflowExecution(workflowDef.getName(), workflowDef.getVersion(), "", new HashMap(), null, null);
        Workflow workflow = workflowExecutor.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(1, workflow.getTasks().size());
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Task waitTask = workflow.getTasks().get(0);
        Assert.assertEquals(WAIT.name(), waitTask.getTaskType());
        waitTask.setStatus(COMPLETED);
        workflowExecutor.updateTask(new TaskResult(waitTask));
        Task task = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNotNull(task);
        task.setStatus(Status.COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("tasks:" + (workflow.getTasks())), COMPLETED, workflow.getStatus());
    }

    @Test
    public void testLambda() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("test_lambda_wf");
        workflowDef.setSchemaVersion(2);
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("input", "${workflow.input}");
        inputParams.put("scriptExpression", "if ($.input.a==1){return {testvalue: true}} else{return {testvalue: false} }");
        WorkflowTask lambdaWorkflowTask = new WorkflowTask();
        lambdaWorkflowTask.setWorkflowTaskType(LAMBDA);
        lambdaWorkflowTask.setName("lambda");
        lambdaWorkflowTask.setInputParameters(inputParams);
        lambdaWorkflowTask.setTaskReferenceName("lambda0");
        workflowDef.getTasks().add(lambdaWorkflowTask);
        Assert.assertNotNull(workflowDef);
        metadataService.registerWorkflowDef(workflowDef);
        Map inputs = new HashMap<>();
        inputs.put("a", 1);
        String workflowId = startOrLoadWorkflowExecution(workflowDef.getName(), workflowDef.getVersion(), "", inputs, null, null);
        Workflow workflow = workflowExecutor.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(1, workflow.getTasks().size());
        workflowExecutor.decide(workflowId);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Task lambda_task = workflow.getTasks().get(0);
        Assert.assertEquals(lambda_task.getOutputData().toString(), "{result={testvalue=true}}");
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("tasks:" + (workflow.getTasks())), COMPLETED, workflow.getStatus());
    }

    @Test
    public void testEventWorkflow() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("eventX");
        taskDef.setTimeoutSeconds(1);
        metadataService.registerTaskDef(Collections.singletonList(taskDef));
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("test_event");
        workflowDef.setSchemaVersion(2);
        WorkflowTask eventWorkflowTask = new WorkflowTask();
        eventWorkflowTask.setWorkflowTaskType(EVENT);
        eventWorkflowTask.setName("eventX");
        eventWorkflowTask.setTaskReferenceName("wait0");
        eventWorkflowTask.setSink("conductor");
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("junit_task_1");
        workflowTask.setTaskReferenceName("t1");
        workflowDef.getTasks().add(eventWorkflowTask);
        workflowDef.getTasks().add(workflowTask);
        metadataService.registerWorkflowDef(workflowDef);
        String workflowId = startOrLoadWorkflowExecution(workflowDef.getName(), workflowDef.getVersion(), "", new HashMap(), null, null);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        Workflow workflow = workflowExecutor.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Task eventTask = workflow.getTasks().get(0);
        Assert.assertEquals(EVENT.name(), eventTask.getTaskType());
        Assert.assertEquals(COMPLETED, eventTask.getStatus());
        Assert.assertTrue((!(eventTask.getOutputData().isEmpty())));
        Assert.assertNotNull(eventTask.getOutputData().get("event_produced"));
        Task task = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNotNull(task);
        task.setStatus(Status.COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(("tasks:" + (workflow.getTasks())), COMPLETED, workflow.getStatus());
    }

    @Test
    public void testTaskWithCallbackAfterSecondsInWorkflow() {
        WorkflowDef workflowDef = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(workflowDef);
        String workflowId = startOrLoadWorkflowExecution(workflowDef.getName(), workflowDef.getVersion(), "", new HashMap(), null, null);
        Workflow workflow = workflowExecutor.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Task task = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        String taskId = task.getTaskId();
        task.setStatus(IN_PROGRESS);
        task.setCallbackAfterSeconds(5L);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(1, workflow.getTasks().size());
        // task should not be available
        task = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNull(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(1, workflow.getTasks().size());
        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        task = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNotNull(task);
        Assert.assertEquals(taskId, task.getTaskId());
        task.setStatus(Status.COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(2, workflow.getTasks().size());
        task = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertNotNull(task);
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        taskId = task.getTaskId();
        task.setStatus(IN_PROGRESS);
        task.setCallbackAfterSeconds(5L);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(2, workflow.getTasks().size());
        // task should not be available
        task = workflowExecutionService.poll("junit_task_1", "test");
        Assert.assertNull(task);
        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        task = workflowExecutionService.poll("junit_task_2", "test");
        Assert.assertNotNull(task);
        Assert.assertEquals(taskId, task.getTaskId());
        task.setStatus(Status.COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(2, workflow.getTasks().size());
        Assert.assertEquals(COMPLETED, workflow.getStatus());
    }

    @Test
    public void testWorkflowUsingExternalPayloadStorage() {
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(found);
        Map<String, Object> outputParameters = found.getOutputParameters();
        outputParameters.put("workflow_output", "${t1.output.op}");
        metadataService.updateWorkflowDef(found);
        String workflowInputPath = "workflow/input";
        String correlationId = "wf_external_storage";
        String workflowId = workflowExecutor.startWorkflow(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, null, workflowInputPath, null, null);
        Assert.assertNotNull(workflowId);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertTrue("The workflow input should not be persisted", workflow.getInput().isEmpty());
        Assert.assertEquals(workflowInputPath, workflow.getExternalInputPayloadStoragePath());
        Assert.assertEquals(workflow.getReasonForIncompletion(), WorkflowStatus.RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());
        // Polling for the first task
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_1", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        // update first task with COMPLETED
        String taskOutputPath = "task/output";
        task.setOutputData(null);
        task.setExternalOutputPayloadStoragePath(taskOutputPath);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertTrue("The workflow input should not be persisted", workflow.getInput().isEmpty());
        Assert.assertEquals(workflowInputPath, workflow.getExternalInputPayloadStoragePath());
        Assert.assertEquals(workflow.getReasonForIncompletion(), WorkflowStatus.RUNNING, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        Assert.assertTrue("The first task output should not be persisted", workflow.getTasks().get(0).getOutputData().isEmpty());
        Assert.assertTrue("The second task input should not be persisted", workflow.getTasks().get(1).getInputData().isEmpty());
        Assert.assertEquals(taskOutputPath, workflow.getTasks().get(0).getExternalOutputPayloadStoragePath());
        Assert.assertEquals("task/input", workflow.getTasks().get(1).getExternalInputPayloadStoragePath());
        // Polling for the second task
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertTrue(task.getInputData().isEmpty());
        Assert.assertNotNull(task.getExternalInputPayloadStoragePath());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        // update second task with COMPLETED
        task.getOutputData().put("op", "success_task2");
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertTrue("The workflow input should not be persisted", workflow.getInput().isEmpty());
        Assert.assertEquals(workflowInputPath, workflow.getExternalInputPayloadStoragePath());
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        Assert.assertTrue("The first task output should not be persisted", workflow.getTasks().get(0).getOutputData().isEmpty());
        Assert.assertTrue("The second task input should not be persisted", workflow.getTasks().get(1).getInputData().isEmpty());
        Assert.assertEquals(taskOutputPath, workflow.getTasks().get(0).getExternalOutputPayloadStoragePath());
        Assert.assertEquals("task/input", workflow.getTasks().get(1).getExternalInputPayloadStoragePath());
        Assert.assertTrue(workflow.getOutput().isEmpty());
        Assert.assertNotNull(workflow.getExternalOutputPayloadStoragePath());
        Assert.assertEquals("workflow/output", workflow.getExternalOutputPayloadStoragePath());
    }

    @Test
    public void testRetryWorkflowUsingExternalPayloadStorage() {
        WorkflowDef found = metadataService.getWorkflowDef(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1);
        Assert.assertNotNull(found);
        Map<String, Object> outputParameters = found.getOutputParameters();
        outputParameters.put("workflow_output", "${t1.output.op}");
        metadataService.updateWorkflowDef(found);
        String taskName = "junit_task_2";
        TaskDef taskDef = metadataService.getTaskDef(taskName);
        taskDef.setRetryCount(2);
        taskDef.setRetryDelaySeconds(0);
        metadataService.updateTaskDef(taskDef);
        String workflowInputPath = "workflow/input";
        String correlationId = "wf_external_storage";
        String workflowId = workflowExecutor.startWorkflow(AbstractWorkflowServiceTest.LINEAR_WORKFLOW_T1_T2, 1, correlationId, null, workflowInputPath, null, null);
        Assert.assertNotNull(workflowId);
        Workflow workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertTrue("The workflow input should not be persisted", workflow.getInput().isEmpty());
        Assert.assertEquals(workflowInputPath, workflow.getExternalInputPayloadStoragePath());
        Assert.assertEquals(workflow.getReasonForIncompletion(), WorkflowStatus.RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());
        // Polling for the first task
        Task task = workflowExecutionService.poll("junit_task_1", "task1.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_1", task.getTaskType());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        // update first task with COMPLETED
        String taskOutputPath = "task/output";
        task.setOutputData(null);
        task.setExternalOutputPayloadStoragePath(taskOutputPath);
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        // Polling for the second task
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertTrue(task.getInputData().isEmpty());
        Assert.assertNotNull(task.getExternalInputPayloadStoragePath());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        // update second task with FAILED
        task.getOutputData().put("op", "failed_task2");
        task.setStatus(FAILED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertTrue("The workflow input should not be persisted", workflow.getInput().isEmpty());
        Assert.assertEquals(workflowInputPath, workflow.getExternalInputPayloadStoragePath());
        Assert.assertEquals(WorkflowStatus.RUNNING, workflow.getStatus());
        // Polling again for the second task
        task = workflowExecutionService.poll("junit_task_2", "task2.junit.worker");
        Assert.assertNotNull(task);
        Assert.assertEquals("junit_task_2", task.getTaskType());
        Assert.assertTrue(task.getInputData().isEmpty());
        Assert.assertNotNull(task.getExternalInputPayloadStoragePath());
        Assert.assertTrue(workflowExecutionService.ackTaskReceived(task.getTaskId()));
        Assert.assertEquals(workflowId, task.getWorkflowInstanceId());
        // update second task with COMPLETED
        task.getOutputData().put("op", "success_task2");
        task.setStatus(COMPLETED);
        workflowExecutionService.updateTask(task);
        workflow = workflowExecutionService.getExecutionStatus(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertTrue("The workflow input should not be persisted", workflow.getInput().isEmpty());
        Assert.assertEquals(workflowInputPath, workflow.getExternalInputPayloadStoragePath());
        Assert.assertEquals(COMPLETED, workflow.getStatus());
        Assert.assertEquals(3, workflow.getTasks().size());
        Assert.assertTrue("The first task output should not be persisted", workflow.getTasks().get(0).getOutputData().isEmpty());
        Assert.assertTrue("The second task input should not be persisted", workflow.getTasks().get(1).getInputData().isEmpty());
        Assert.assertTrue("The second task input should not be persisted", workflow.getTasks().get(2).getInputData().isEmpty());
        Assert.assertEquals(taskOutputPath, workflow.getTasks().get(0).getExternalOutputPayloadStoragePath());
        Assert.assertEquals("task/input", workflow.getTasks().get(1).getExternalInputPayloadStoragePath());
        Assert.assertEquals("task/input", workflow.getTasks().get(2).getExternalInputPayloadStoragePath());
        Assert.assertTrue(workflow.getOutput().isEmpty());
        Assert.assertNotNull(workflow.getExternalOutputPayloadStoragePath());
        Assert.assertEquals("workflow/output", workflow.getExternalOutputPayloadStoragePath());
    }

    private boolean printWFTaskDetails = false;
}

