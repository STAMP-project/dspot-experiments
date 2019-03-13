/**
 * Copyright 2016 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 *
 */
package com.netflix.conductor.core.execution;


import Status.COMPLETED;
import Status.FAILED;
import Status.FAILED_WITH_TERMINAL_ERROR;
import Status.IN_PROGRESS;
import Status.SCHEDULED;
import Status.TIMED_OUT;
import TaskType.DECISION;
import TaskType.SIMPLE;
import TimeoutPolicy.ALERT_ONLY;
import TimeoutPolicy.RETRY;
import TimeoutPolicy.TIME_OUT_WF;
import WorkflowStatus.RUNNING;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.utils.JsonMapperProvider;
import com.netflix.conductor.core.execution.DeciderService.DeciderOutcome;
import com.netflix.conductor.core.utils.ExternalPayloadStorageUtils;
import com.netflix.conductor.dao.MetadataDAO;
import com.netflix.spectator.api.Counter;
import com.netflix.spectator.api.Registry;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Viren
 */
@SuppressWarnings("Duplicates")
public class TestDeciderService {
    private DeciderService deciderService;

    private ParametersUtils parametersUtils;

    private MetadataDAO metadataDAO;

    private ExternalPayloadStorageUtils externalPayloadStorageUtils;

    private static Registry registry;

    private static ObjectMapper objectMapper = new JsonMapperProvider().get();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetTaskInputV2() {
        Workflow workflow = createDefaultWorkflow();
        workflow.getWorkflowDefinition().setSchemaVersion(2);
        Map<String, Object> ip = new HashMap<>();
        ip.put("workflowInputParam", "${workflow.input.requestId}");
        ip.put("taskOutputParam", "${task2.output.location}");
        ip.put("taskOutputParam2", "${task2.output.locationBad}");
        ip.put("taskOutputParam3", "${task3.output.location}");
        ip.put("constParam", "Some String value");
        ip.put("nullValue", null);
        ip.put("task2Status", "${task2.status}");
        ip.put("channelMap", "${workflow.input.channelMapping}");
        Map<String, Object> taskInput = parametersUtils.getTaskInput(ip, workflow, null, null);
        Assert.assertNotNull(taskInput);
        Assert.assertTrue(taskInput.containsKey("workflowInputParam"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam2"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam3"));
        Assert.assertNull(taskInput.get("taskOutputParam2"));
        Assert.assertNotNull(taskInput.get("channelMap"));
        Assert.assertEquals(5, taskInput.get("channelMap"));
        Assert.assertEquals("request id 001", taskInput.get("workflowInputParam"));
        Assert.assertEquals("http://location", taskInput.get("taskOutputParam"));
        Assert.assertNull(taskInput.get("taskOutputParam3"));
        Assert.assertNull(taskInput.get("nullValue"));
        Assert.assertEquals(workflow.getTasks().get(0).getStatus().name(), taskInput.get("task2Status"));// task2 and task3 are the tasks respectively

    }

    @Test
    public void testGetTaskInputV2Partial() {
        Workflow workflow = createDefaultWorkflow();
        System.setProperty("EC2_INSTANCE", "i-123abcdef990");
        Map<String, Object> wfi = new HashMap<>();
        Map<String, Object> wfmap = new HashMap<>();
        wfmap.put("input", workflow.getInput());
        wfmap.put("output", workflow.getOutput());
        wfi.put("workflow", wfmap);
        workflow.getTasks().stream().map(Task::getReferenceTaskName).forEach(( ref) -> {
            Map<String, Object> taskInput = workflow.getTaskByRefName(ref).getInputData();
            Map<String, Object> taskOutput = workflow.getTaskByRefName(ref).getOutputData();
            Map<String, Object> io = new HashMap<>();
            io.put("input", taskInput);
            io.put("output", taskOutput);
            wfi.put(ref, io);
        });
        workflow.getWorkflowDefinition().setSchemaVersion(2);
        Map<String, Object> ip = new HashMap<>();
        ip.put("workflowInputParam", "${workflow.input.requestId}");
        ip.put("workfowOutputParam", "${workflow.output.name}");
        ip.put("taskOutputParam", "${task2.output.location}");
        ip.put("taskOutputParam2", "${task2.output.locationBad}");
        ip.put("taskOutputParam3", "${task3.output.location}");
        ip.put("constParam", "Some String value   &");
        ip.put("partial", "${task2.output.location}/something?host=${EC2_INSTANCE}");
        ip.put("jsonPathExtracted", "${workflow.output.names[*].year}");
        ip.put("secondName", "${workflow.output.names[1].name}");
        ip.put("concatenatedName", "The Band is: ${workflow.output.names[1].name}-\t${EC2_INSTANCE}");
        TaskDef taskDef = new TaskDef();
        taskDef.getInputTemplate().put("opname", "${workflow.output.name}");
        List<Object> listParams = new LinkedList<>();
        List<Object> listParams2 = new LinkedList<>();
        listParams2.add("${workflow.input.requestId}-10-${EC2_INSTANCE}");
        listParams.add(listParams2);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "${workflow.output.names[0].name}");
        map.put("hasAwards", "${workflow.input.hasAwards}");
        listParams.add(map);
        taskDef.getInputTemplate().put("listValues", listParams);
        Map<String, Object> taskInput = parametersUtils.getTaskInput(ip, workflow, taskDef, null);
        Assert.assertNotNull(taskInput);
        Assert.assertTrue(taskInput.containsKey("workflowInputParam"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam2"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam3"));
        Assert.assertNull(taskInput.get("taskOutputParam2"));
        Assert.assertNotNull(taskInput.get("jsonPathExtracted"));
        Assert.assertTrue(((taskInput.get("jsonPathExtracted")) instanceof List));
        Assert.assertNotNull(taskInput.get("secondName"));
        Assert.assertTrue(((taskInput.get("secondName")) instanceof String));
        Assert.assertEquals("The Doors", taskInput.get("secondName"));
        Assert.assertEquals("The Band is: The Doors-\ti-123abcdef990", taskInput.get("concatenatedName"));
        Assert.assertEquals("request id 001", taskInput.get("workflowInputParam"));
        Assert.assertEquals("http://location", taskInput.get("taskOutputParam"));
        Assert.assertNull(taskInput.get("taskOutputParam3"));
        Assert.assertNotNull(taskInput.get("partial"));
        Assert.assertEquals("http://location/something?host=i-123abcdef990", taskInput.get("partial"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetTaskInput() {
        Map<String, Object> ip = new HashMap<>();
        ip.put("workflowInputParam", "${workflow.input.requestId}");
        ip.put("taskOutputParam", "${task2.output.location}");
        List<Map<String, Object>> json = new LinkedList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put("name", "person name");
        m1.put("city", "New York");
        m1.put("phone", 2120001234);
        m1.put("status", "${task2.output.isPersonActive}");
        Map<String, Object> m2 = new HashMap<>();
        m2.put("employer", "City Of New York");
        m2.put("color", "purple");
        m2.put("requestId", "${workflow.input.requestId}");
        json.add(m1);
        json.add(m2);
        ip.put("complexJson", json);
        WorkflowDef def = new WorkflowDef();
        def.setName("testGetTaskInput");
        def.setSchemaVersion(2);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.getInput().put("requestId", "request id 001");
        Task task = new Task();
        task.setReferenceTaskName("task2");
        task.getOutputData().put("location", "http://location");
        task.getOutputData().put("isPersonActive", true);
        workflow.getTasks().add(task);
        Map<String, Object> taskInput = parametersUtils.getTaskInput(ip, workflow, null, null);
        Assert.assertNotNull(taskInput);
        Assert.assertTrue(taskInput.containsKey("workflowInputParam"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam"));
        Assert.assertEquals("request id 001", taskInput.get("workflowInputParam"));
        Assert.assertEquals("http://location", taskInput.get("taskOutputParam"));
        Assert.assertNotNull(taskInput.get("complexJson"));
        Assert.assertTrue(((taskInput.get("complexJson")) instanceof List));
        List<Map<String, Object>> resolvedInput = ((List<Map<String, Object>>) (taskInput.get("complexJson")));
        Assert.assertEquals(2, resolvedInput.size());
    }

    @Test
    public void testGetTaskInputV1() {
        Map<String, Object> ip = new HashMap<>();
        ip.put("workflowInputParam", "workflow.input.requestId");
        ip.put("taskOutputParam", "task2.output.location");
        WorkflowDef def = new WorkflowDef();
        def.setSchemaVersion(1);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.getInput().put("requestId", "request id 001");
        Task task = new Task();
        task.setReferenceTaskName("task2");
        task.getOutputData().put("location", "http://location");
        task.getOutputData().put("isPersonActive", true);
        workflow.getTasks().add(task);
        Map<String, Object> taskInput = parametersUtils.getTaskInput(ip, workflow, null, null);
        Assert.assertNotNull(taskInput);
        Assert.assertTrue(taskInput.containsKey("workflowInputParam"));
        Assert.assertTrue(taskInput.containsKey("taskOutputParam"));
        Assert.assertEquals("request id 001", taskInput.get("workflowInputParam"));
        Assert.assertEquals("http://location", taskInput.get("taskOutputParam"));
    }

    @Test
    public void testGetNextTask() {
        WorkflowDef def = createNestedWorkflow();
        WorkflowTask firstTask = def.getTasks().get(0);
        Assert.assertNotNull(firstTask);
        Assert.assertEquals("fork1", firstTask.getTaskReferenceName());
        WorkflowTask nextAfterFirst = def.getNextTask(firstTask.getTaskReferenceName());
        Assert.assertNotNull(nextAfterFirst);
        Assert.assertEquals("join1", nextAfterFirst.getTaskReferenceName());
        WorkflowTask fork2 = def.getTaskByRefName("fork2");
        Assert.assertNotNull(fork2);
        Assert.assertEquals("fork2", fork2.getTaskReferenceName());
        WorkflowTask taskAfterFork2 = def.getNextTask("fork2");
        Assert.assertNotNull(taskAfterFork2);
        Assert.assertEquals("join2", taskAfterFork2.getTaskReferenceName());
        WorkflowTask t2 = def.getTaskByRefName("t2");
        Assert.assertNotNull(t2);
        Assert.assertEquals("t2", t2.getTaskReferenceName());
        WorkflowTask taskAfterT2 = def.getNextTask("t2");
        Assert.assertNotNull(taskAfterT2);
        Assert.assertEquals("t4", taskAfterT2.getTaskReferenceName());
        WorkflowTask taskAfterT3 = def.getNextTask("t3");
        Assert.assertNotNull(taskAfterT3);
        Assert.assertEquals(DECISION.name(), taskAfterT3.getType());
        Assert.assertEquals("d1", taskAfterT3.getTaskReferenceName());
        WorkflowTask taskAfterT4 = def.getNextTask("t4");
        Assert.assertNotNull(taskAfterT4);
        Assert.assertEquals("join2", taskAfterT4.getTaskReferenceName());
        WorkflowTask taskAfterT6 = def.getNextTask("t6");
        Assert.assertNotNull(taskAfterT6);
        Assert.assertEquals("t9", taskAfterT6.getTaskReferenceName());
        WorkflowTask taskAfterJoin2 = def.getNextTask("join2");
        Assert.assertNotNull(taskAfterJoin2);
        Assert.assertEquals("join1", taskAfterJoin2.getTaskReferenceName());
        WorkflowTask taskAfterJoin1 = def.getNextTask("join1");
        Assert.assertNotNull(taskAfterJoin1);
        Assert.assertEquals("t5", taskAfterJoin1.getTaskReferenceName());
        WorkflowTask taskAfterSubWF = def.getNextTask("sw1");
        Assert.assertNotNull(taskAfterSubWF);
        Assert.assertEquals("join1", taskAfterSubWF.getTaskReferenceName());
        WorkflowTask taskAfterT9 = def.getNextTask("t9");
        Assert.assertNotNull(taskAfterT9);
        Assert.assertEquals("join1", taskAfterT9.getTaskReferenceName());
    }

    @Test
    public void testCaseStatement() {
        WorkflowDef def = createConditionalWF();
        Workflow wf = new Workflow();
        wf.setWorkflowDefinition(def);
        wf.setCreateTime(0L);
        wf.setWorkflowId("a");
        wf.setCorrelationId("b");
        wf.setStatus(RUNNING);
        DeciderOutcome outcome = deciderService.decide(wf);
        List<Task> scheduledTasks = outcome.tasksToBeScheduled;
        Assert.assertNotNull(scheduledTasks);
        Assert.assertEquals(2, scheduledTasks.size());
        Assert.assertEquals(IN_PROGRESS, scheduledTasks.get(0).getStatus());
        Assert.assertEquals(SCHEDULED, scheduledTasks.get(1).getStatus());
    }

    @Test
    public void testGetTaskByRef() {
        Workflow workflow = new Workflow();
        Task t1 = new Task();
        t1.setReferenceTaskName("ref");
        t1.setSeq(0);
        t1.setStatus(TIMED_OUT);
        Task t2 = new Task();
        t2.setReferenceTaskName("ref");
        t2.setSeq(1);
        t2.setStatus(FAILED);
        Task t3 = new Task();
        t3.setReferenceTaskName("ref");
        t3.setSeq(2);
        t3.setStatus(COMPLETED);
        workflow.getTasks().add(t1);
        workflow.getTasks().add(t2);
        workflow.getTasks().add(t3);
        Task task = workflow.getTaskByRefName("ref");
        Assert.assertNotNull(task);
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertEquals(t3.getSeq(), task.getSeq());
    }

    @Test
    public void testTaskTimeout() {
        Counter counter = TestDeciderService.registry.counter("task_timeout", "class", "WorkflowMonitor", "taskType", "test");
        Assert.assertEquals(0, counter.count());
        TaskDef taskType = new TaskDef();
        taskType.setName("test");
        taskType.setTimeoutPolicy(RETRY);
        taskType.setTimeoutSeconds(1);
        Task task = new Task();
        task.setTaskType(taskType.getName());
        task.setStartTime(((System.currentTimeMillis()) - 2000));// 2 seconds ago!

        task.setStatus(IN_PROGRESS);
        deciderService.checkForTimeout(taskType, task);
        // Task should be marked as timed out
        Assert.assertEquals(TIMED_OUT, task.getStatus());
        Assert.assertNotNull(task.getReasonForIncompletion());
        Assert.assertEquals(1, counter.count());
        taskType.setTimeoutPolicy(ALERT_ONLY);
        task.setStatus(IN_PROGRESS);
        task.setReasonForIncompletion(null);
        deciderService.checkForTimeout(taskType, task);
        // Nothing will happen
        Assert.assertEquals(IN_PROGRESS, task.getStatus());
        Assert.assertNull(task.getReasonForIncompletion());
        Assert.assertEquals(2, counter.count());
        boolean exception = false;
        taskType.setTimeoutPolicy(TIME_OUT_WF);
        task.setStatus(IN_PROGRESS);
        task.setReasonForIncompletion(null);
        try {
            deciderService.checkForTimeout(taskType, task);
        } catch (TerminateWorkflowException tw) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Assert.assertEquals(TIMED_OUT, task.getStatus());
        Assert.assertNotNull(task.getReasonForIncompletion());
        Assert.assertEquals(3, counter.count());
        taskType.setTimeoutPolicy(TIME_OUT_WF);
        task.setStatus(IN_PROGRESS);
        task.setReasonForIncompletion(null);
        deciderService.checkForTimeout(null, task);// this will be a no-op

        Assert.assertEquals(IN_PROGRESS, task.getStatus());
        Assert.assertNull(task.getReasonForIncompletion());
        Assert.assertEquals(3, counter.count());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConcurrentTaskInputCalc() throws InterruptedException {
        TaskDef def = new TaskDef();
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("path", "${workflow.input.inputLocation}");
        inputMap.put("type", "${workflow.input.sourceType}");
        inputMap.put("channelMapping", "${workflow.input.channelMapping}");
        List<Map<String, Object>> input = new LinkedList<>();
        input.add(inputMap);
        Map<String, Object> body = new HashMap<>();
        body.put("input", input);
        def.getInputTemplate().putAll(body);
        ExecutorService es = Executors.newFixedThreadPool(10);
        final int[] result = new int[10];
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            final int x = i;
            es.submit(() -> {
                try {
                    Map<String, Object> workflowInput = new HashMap<>();
                    workflowInput.put("outputLocation", ("baggins://outputlocation/" + x));
                    workflowInput.put("inputLocation", ("baggins://inputlocation/" + x));
                    workflowInput.put("sourceType", "MuxedSource");
                    workflowInput.put("channelMapping", x);
                    WorkflowDef workflowDef = new WorkflowDef();
                    workflowDef.setName("testConcurrentTaskInputCalc");
                    workflowDef.setVersion(1);
                    Workflow workflow = new Workflow();
                    workflow.setWorkflowDefinition(workflowDef);
                    workflow.setInput(workflowInput);
                    Map<String, Object> taskInput = parametersUtils.getTaskInputV2(new HashMap(), workflow, null, def);
                    Object reqInputObj = taskInput.get("input");
                    Assert.assertNotNull(reqInputObj);
                    Assert.assertTrue((reqInputObj instanceof List));
                    List<Map<String, Object>> reqInput = ((List<Map<String, Object>>) (reqInputObj));
                    Object cmObj = reqInput.get(0).get("channelMapping");
                    Assert.assertNotNull(cmObj);
                    if (!(cmObj instanceof Number)) {
                        result[x] = -1;
                    } else {
                        Number channelMapping = ((Number) (cmObj));
                        result[x] = channelMapping.intValue();
                    }
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        latch.await(1, TimeUnit.MINUTES);
        if ((latch.getCount()) > 0) {
            Assert.fail("Executions did not complete in a minute.  Something wrong with the build server?");
        }
        es.shutdownNow();
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(i, result[i]);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTaskRetry() {
        Workflow workflow = createDefaultWorkflow();
        workflow.getWorkflowDefinition().setSchemaVersion(2);
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("workflowInputParam", "${workflow.input.requestId}");
        inputParams.put("taskOutputParam", "${task2.output.location}");
        inputParams.put("constParam", "Some String value");
        inputParams.put("nullValue", null);
        inputParams.put("task2Status", "${task2.status}");
        inputParams.put("null", null);
        inputParams.put("task_id", "${CPEWF_TASK_ID}");
        Map<String, Object> env = new HashMap<>();
        env.put("env_task_id", "${CPEWF_TASK_ID}");
        inputParams.put("env", env);
        Map<String, Object> taskInput = parametersUtils.getTaskInput(inputParams, workflow, null, "t1");
        Task task = new Task();
        task.getInputData().putAll(taskInput);
        task.setStatus(FAILED);
        task.setTaskId("t1");
        TaskDef taskDef = new TaskDef();
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.getInputParameters().put("task_id", "${CPEWF_TASK_ID}");
        workflowTask.getInputParameters().put("env", env);
        Task task2 = deciderService.retry(taskDef, workflowTask, task, workflow);
        System.out.println((((task.getTaskId()) + ":\n") + (task.getInputData())));
        System.out.println((((task2.getTaskId()) + ":\n") + (task2.getInputData())));
        Assert.assertEquals("t1", task.getInputData().get("task_id"));
        Assert.assertEquals("t1", ((Map<String, Object>) (task.getInputData().get("env"))).get("env_task_id"));
        Assert.assertNotSame(task.getTaskId(), task2.getTaskId());
        Assert.assertEquals(task2.getTaskId(), task2.getInputData().get("task_id"));
        Assert.assertEquals(task2.getTaskId(), ((Map<String, Object>) (task2.getInputData().get("env"))).get("env_task_id"));
        Task task3 = new Task();
        task3.getInputData().putAll(taskInput);
        task3.setStatus(FAILED_WITH_TERMINAL_ERROR);
        task3.setTaskId("t1");
        Mockito.when(metadataDAO.get(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(Optional.of(new WorkflowDef()));
        exception.expect(TerminateWorkflowException.class);
        deciderService.retry(taskDef, workflowTask, task3, workflow);
    }

    @Test
    public void testFork() throws IOException {
        InputStream stream = TestDeciderService.class.getResourceAsStream("/test.json");
        Workflow workflow = TestDeciderService.objectMapper.readValue(stream, Workflow.class);
        DeciderOutcome outcome = deciderService.decide(workflow);
        Assert.assertFalse(outcome.isComplete);
        Assert.assertEquals(5, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(1, outcome.tasksToBeUpdated.size());
    }

    @Test
    public void testDecideSuccessfulWorkflow() {
        WorkflowDef workflowDef = createLinearWorkflow();
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(workflowDef);
        workflow.setStatus(RUNNING);
        Task task1 = new Task();
        task1.setTaskType("junit_task_l1");
        task1.setReferenceTaskName("s1");
        task1.setSeq(1);
        task1.setRetried(false);
        task1.setExecuted(false);
        task1.setStatus(COMPLETED);
        workflow.getTasks().add(task1);
        DeciderOutcome deciderOutcome = deciderService.decide(workflow);
        Assert.assertNotNull(deciderOutcome);
        Assert.assertFalse(workflow.getTaskByRefName("s1").isRetried());
        Assert.assertEquals(1, deciderOutcome.tasksToBeUpdated.size());
        Assert.assertEquals("s1", deciderOutcome.tasksToBeUpdated.get(0).getReferenceTaskName());
        Assert.assertEquals(1, deciderOutcome.tasksToBeScheduled.size());
        Assert.assertEquals("s2", deciderOutcome.tasksToBeScheduled.get(0).getReferenceTaskName());
        Assert.assertEquals(0, deciderOutcome.tasksToBeRequeued.size());
        Assert.assertFalse(deciderOutcome.isComplete);
        Task task2 = new Task();
        task2.setTaskType("junit_task_l2");
        task2.setReferenceTaskName("s2");
        task2.setSeq(2);
        task2.setRetried(false);
        task2.setExecuted(false);
        task2.setStatus(COMPLETED);
        workflow.getTasks().add(task2);
        deciderOutcome = deciderService.decide(workflow);
        Assert.assertNotNull(deciderOutcome);
        Assert.assertTrue(workflow.getTaskByRefName("s2").isExecuted());
        Assert.assertFalse(workflow.getTaskByRefName("s2").isRetried());
        Assert.assertEquals(1, deciderOutcome.tasksToBeUpdated.size());
        Assert.assertEquals("s2", deciderOutcome.tasksToBeUpdated.get(0).getReferenceTaskName());
        Assert.assertEquals(0, deciderOutcome.tasksToBeScheduled.size());
        Assert.assertEquals(0, deciderOutcome.tasksToBeRequeued.size());
        Assert.assertTrue(deciderOutcome.isComplete);
    }

    @Test
    public void testDecideFailedTask() {
        WorkflowDef workflowDef = createLinearWorkflow();
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(workflowDef);
        workflow.setStatus(RUNNING);
        Task task = new Task();
        task.setTaskType("junit_task_l1");
        task.setReferenceTaskName("s1");
        task.setSeq(1);
        task.setRetried(false);
        task.setExecuted(false);
        task.setStatus(FAILED);
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setTaskReferenceName("s1");
        workflowTask.setName("junit_task_l1");
        workflowTask.setTaskDefinition(new TaskDef("junit_task_l1"));
        task.setWorkflowTask(workflowTask);
        workflow.getTasks().add(task);
        DeciderOutcome deciderOutcome = deciderService.decide(workflow);
        Assert.assertNotNull(deciderOutcome);
        Assert.assertFalse(workflow.getTaskByRefName("s1").isExecuted());
        Assert.assertTrue(workflow.getTaskByRefName("s1").isRetried());
        Assert.assertEquals(1, deciderOutcome.tasksToBeUpdated.size());
        Assert.assertEquals("s1", deciderOutcome.tasksToBeUpdated.get(0).getReferenceTaskName());
        Assert.assertEquals(1, deciderOutcome.tasksToBeScheduled.size());
        Assert.assertEquals("s1", deciderOutcome.tasksToBeScheduled.get(0).getReferenceTaskName());
        Assert.assertEquals(0, deciderOutcome.tasksToBeRequeued.size());
        Assert.assertFalse(deciderOutcome.isComplete);
    }

    @Test
    public void testGetTasksToBeScheduled() {
        WorkflowDef workflowDef = createLinearWorkflow();
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(workflowDef);
        workflow.setStatus(RUNNING);
        WorkflowTask workflowTask1 = new WorkflowTask();
        workflowTask1.setName("s1");
        workflowTask1.setTaskReferenceName("s1");
        workflowTask1.setType(SIMPLE.name());
        workflowTask1.setTaskDefinition(new TaskDef("s1"));
        List<Task> tasksToBeScheduled = deciderService.getTasksToBeScheduled(workflow, workflowTask1, 0, null);
        Assert.assertNotNull(tasksToBeScheduled);
        Assert.assertEquals(1, tasksToBeScheduled.size());
        Assert.assertEquals("s1", tasksToBeScheduled.get(0).getReferenceTaskName());
        WorkflowTask workflowTask2 = new WorkflowTask();
        workflowTask2.setName("s2");
        workflowTask2.setTaskReferenceName("s2");
        workflowTask2.setType(SIMPLE.name());
        workflowTask2.setTaskDefinition(new TaskDef("s2"));
        tasksToBeScheduled = deciderService.getTasksToBeScheduled(workflow, workflowTask2, 0, null);
        Assert.assertNotNull(tasksToBeScheduled);
        Assert.assertEquals(1, tasksToBeScheduled.size());
        Assert.assertEquals("s2", tasksToBeScheduled.get(0).getReferenceTaskName());
    }

    @Test
    public void testIsResponsedTimeOut() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("test_rt");
        taskDef.setResponseTimeoutSeconds(10);
        Task task = new Task();
        task.setTaskDefName("test_rt");
        task.setStatus(IN_PROGRESS);
        task.setTaskId("aa");
        task.setUpdateTime(((System.currentTimeMillis()) - (TimeUnit.SECONDS.toMillis(11))));
        boolean flag = deciderService.isResponseTimedOut(taskDef, task);
        Assert.assertNotNull(task);
        Assert.assertTrue(flag);
    }

    @Test
    public void testPopulateWorkflowAndTaskData() {
        String workflowInputPath = "workflow/input/test.json";
        String taskInputPath = "task/input/test.json";
        String taskOutputPath = "task/output/test.json";
        Map<String, Object> workflowParams = new HashMap<>();
        workflowParams.put("key1", "value1");
        workflowParams.put("key2", 100);
        Mockito.when(externalPayloadStorageUtils.downloadPayload(workflowInputPath)).thenReturn(workflowParams);
        Map<String, Object> taskInputParams = new HashMap<>();
        taskInputParams.put("key", "taskInput");
        Mockito.when(externalPayloadStorageUtils.downloadPayload(taskInputPath)).thenReturn(taskInputParams);
        Map<String, Object> taskOutputParams = new HashMap<>();
        taskOutputParams.put("key", "taskOutput");
        Mockito.when(externalPayloadStorageUtils.downloadPayload(taskOutputPath)).thenReturn(taskOutputParams);
        Task task = new Task();
        task.setExternalInputPayloadStoragePath(taskInputPath);
        task.setExternalOutputPayloadStoragePath(taskOutputPath);
        Workflow workflow = new Workflow();
        workflow.setExternalInputPayloadStoragePath(workflowInputPath);
        workflow.getTasks().add(task);
        Workflow workflowInstance = deciderService.populateWorkflowAndTaskData(workflow);
        Assert.assertNotNull(workflowInstance);
        Assert.assertTrue(workflow.getInput().isEmpty());
        Assert.assertNotNull(workflowInstance.getInput());
        Assert.assertEquals(workflowParams, workflowInstance.getInput());
        Assert.assertTrue(workflow.getTasks().get(0).getInputData().isEmpty());
        Assert.assertNotNull(workflowInstance.getTasks().get(0).getInputData());
        Assert.assertEquals(taskInputParams, workflowInstance.getTasks().get(0).getInputData());
        Assert.assertTrue(workflow.getTasks().get(0).getOutputData().isEmpty());
        Assert.assertNotNull(workflowInstance.getTasks().get(0).getOutputData());
        Assert.assertEquals(taskOutputParams, workflowInstance.getTasks().get(0).getOutputData());
        Assert.assertNull(workflowInstance.getExternalInputPayloadStoragePath());
        Assert.assertNull(workflowInstance.getTasks().get(0).getExternalInputPayloadStoragePath());
        Assert.assertNull(workflowInstance.getTasks().get(0).getExternalOutputPayloadStoragePath());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateWorkflowOutput() {
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(new WorkflowDef());
        deciderService.updateWorkflowOutput(workflow, null);
        Assert.assertNotNull(workflow.getOutput());
        Assert.assertTrue(workflow.getOutput().isEmpty());
        Task task = new Task();
        Map<String, Object> taskOutput = new HashMap<>();
        taskOutput.put("taskKey", "taskValue");
        task.setOutputData(taskOutput);
        workflow.getTasks().add(task);
        WorkflowDef workflowDef = new WorkflowDef();
        Mockito.when(metadataDAO.get(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(Optional.of(workflowDef));
        deciderService.updateWorkflowOutput(workflow, null);
        Assert.assertNotNull(workflow.getOutput());
        Assert.assertEquals("taskValue", workflow.getOutput().get("taskKey"));
    }
}

