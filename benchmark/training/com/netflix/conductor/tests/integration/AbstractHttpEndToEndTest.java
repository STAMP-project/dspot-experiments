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
package com.netflix.conductor.tests.integration;


import Status.COMPLETED;
import Task.Status.SCHEDULED;
import TaskType.SIMPLE;
import WorkflowStatus.RUNNING;
import WorkflowStatus.TERMINATED;
import com.netflix.conductor.client.exceptions.ConductorClientException;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.run.WorkflowSummary;
import com.netflix.conductor.common.validation.ValidationError;
import com.netflix.conductor.elasticsearch.EmbeddedElasticSearch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public abstract class AbstractHttpEndToEndTest extends AbstractEndToEndTest {
    protected static String apiRoot;

    protected static TaskClient taskClient;

    protected static WorkflowClient workflowClient;

    protected static EmbeddedElasticSearch search;

    protected static MetadataClient metadataClient;

    @Test
    public void testAll() throws Exception {
        createAndRegisterTaskDefinitions("t", 5);
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        WorkflowTask t0 = new WorkflowTask();
        t0.setName("t0");
        t0.setWorkflowTaskType(SIMPLE);
        t0.setTaskReferenceName("t0");
        WorkflowTask t1 = new WorkflowTask();
        t1.setName("t1");
        t1.setWorkflowTaskType(SIMPLE);
        t1.setTaskReferenceName("t1");
        def.getTasks().add(t0);
        def.getTasks().add(t1);
        AbstractHttpEndToEndTest.metadataClient.registerWorkflowDef(def);
        WorkflowDef workflowDefinitionFromSystem = AbstractHttpEndToEndTest.metadataClient.getWorkflowDef(def.getName(), null);
        Assert.assertNotNull(workflowDefinitionFromSystem);
        Assert.assertEquals(def, workflowDefinitionFromSystem);
        String correlationId = "test_corr_id";
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest().withName(def.getName()).withCorrelationId(correlationId).withInput(new HashMap());
        String workflowId = AbstractHttpEndToEndTest.workflowClient.startWorkflow(startWorkflowRequest);
        Assert.assertNotNull(workflowId);
        Workflow workflow = AbstractHttpEndToEndTest.workflowClient.getWorkflow(workflowId, false);
        Assert.assertEquals(0, workflow.getTasks().size());
        Assert.assertEquals(workflowId, workflow.getWorkflowId());
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Workflow> workflowList = AbstractHttpEndToEndTest.workflowClient.getWorkflows(def.getName(), correlationId, false, false);
            assertEquals(1, workflowList.size());
            assertEquals(workflowId, workflowList.get(0).getWorkflowId());
        });
        workflow = AbstractHttpEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());
        Assert.assertEquals(t0.getTaskReferenceName(), workflow.getTasks().get(0).getReferenceTaskName());
        Assert.assertEquals(workflowId, workflow.getWorkflowId());
        int queueSize = AbstractHttpEndToEndTest.taskClient.getQueueSizeForTask(workflow.getTasks().get(0).getTaskType());
        Assert.assertEquals(1, queueSize);
        java.util.List<String> runningIds = AbstractHttpEndToEndTest.workflowClient.getRunningWorkflow(def.getName(), def.getVersion());
        Assert.assertNotNull(runningIds);
        Assert.assertEquals(1, runningIds.size());
        Assert.assertEquals(workflowId, runningIds.get(0));
        java.util.List<Task> polled = AbstractHttpEndToEndTest.taskClient.batchPollTasksByTaskType("non existing task", "test", 1, 100);
        Assert.assertNotNull(polled);
        Assert.assertEquals(0, polled.size());
        polled = AbstractHttpEndToEndTest.taskClient.batchPollTasksByTaskType(t0.getName(), "test", 1, 100);
        Assert.assertNotNull(polled);
        Assert.assertEquals(1, polled.size());
        Assert.assertEquals(t0.getName(), polled.get(0).getTaskDefName());
        Task task = polled.get(0);
        Boolean acked = AbstractHttpEndToEndTest.taskClient.ack(task.getTaskId(), "test");
        Assert.assertNotNull(acked);
        Assert.assertTrue(acked);
        task.getOutputData().put("key1", "value1");
        task.setStatus(COMPLETED);
        AbstractHttpEndToEndTest.taskClient.updateTask(new TaskResult(task), task.getTaskType());
        polled = AbstractHttpEndToEndTest.taskClient.batchPollTasksByTaskType(t0.getName(), "test", 1, 100);
        Assert.assertNotNull(polled);
        Assert.assertTrue(polled.toString(), polled.isEmpty());
        workflow = AbstractHttpEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(2, workflow.getTasks().size());
        Assert.assertEquals(t0.getTaskReferenceName(), workflow.getTasks().get(0).getReferenceTaskName());
        Assert.assertEquals(t1.getTaskReferenceName(), workflow.getTasks().get(1).getReferenceTaskName());
        Assert.assertEquals(Task.Status.COMPLETED, workflow.getTasks().get(0).getStatus());
        Assert.assertEquals(SCHEDULED, workflow.getTasks().get(1).getStatus());
        Task taskById = AbstractHttpEndToEndTest.taskClient.getTaskDetails(task.getTaskId());
        Assert.assertNotNull(taskById);
        Assert.assertEquals(task.getTaskId(), taskById.getTaskId());
        queueSize = AbstractHttpEndToEndTest.taskClient.getQueueSizeForTask(workflow.getTasks().get(1).getTaskType());
        Assert.assertEquals(1, queueSize);
        java.util.List<Task> getTasks = AbstractHttpEndToEndTest.taskClient.getPendingTasksByType(t0.getName(), null, 1);
        Assert.assertNotNull(getTasks);
        Assert.assertEquals(0, getTasks.size());// getTasks only gives pending tasks

        getTasks = AbstractHttpEndToEndTest.taskClient.getPendingTasksByType(t1.getName(), null, 1);
        Assert.assertNotNull(getTasks);
        Assert.assertEquals(1, getTasks.size());
        Task pending = AbstractHttpEndToEndTest.taskClient.getPendingTaskForWorkflow(workflowId, t1.getTaskReferenceName());
        Assert.assertNotNull(pending);
        Assert.assertEquals(t1.getTaskReferenceName(), pending.getReferenceTaskName());
        Assert.assertEquals(workflowId, pending.getWorkflowInstanceId());
        Thread.sleep(1000);
        SearchResult<WorkflowSummary> searchResult = AbstractHttpEndToEndTest.workflowClient.search((("workflowType='" + (def.getName())) + "'"));
        Assert.assertNotNull(searchResult);
        Assert.assertEquals(1, searchResult.getTotalHits());
        AbstractHttpEndToEndTest.workflowClient.terminateWorkflow(workflowId, "terminate reason");
        workflow = AbstractHttpEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(TERMINATED, workflow.getStatus());
        AbstractHttpEndToEndTest.workflowClient.restart(workflowId);
        workflow = AbstractHttpEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(workflow);
        Assert.assertEquals(RUNNING, workflow.getStatus());
        Assert.assertEquals(1, workflow.getTasks().size());
    }

    @Test(expected = ConductorClientException.class)
    public void testMetadataWorkflowDefinition() {
        String workflowDefName = "testWorkflowDefMetadata";
        WorkflowDef def = new WorkflowDef();
        def.setName(workflowDefName);
        def.setVersion(1);
        WorkflowTask t0 = new WorkflowTask();
        t0.setName("t0");
        t0.setWorkflowTaskType(SIMPLE);
        t0.setTaskReferenceName("t0");
        WorkflowTask t1 = new WorkflowTask();
        t1.setName("t1");
        t1.setWorkflowTaskType(SIMPLE);
        t1.setTaskReferenceName("t1");
        def.getTasks().add(t0);
        def.getTasks().add(t1);
        AbstractHttpEndToEndTest.metadataClient.registerWorkflowDef(def);
        AbstractHttpEndToEndTest.metadataClient.unregisterWorkflowDef(workflowDefName, 1);
        try {
            AbstractHttpEndToEndTest.metadataClient.getWorkflowDef(workflowDefName, 1);
        } catch (ConductorClientException e) {
            int statusCode = e.getStatus();
            String errorMessage = e.getMessage();
            boolean retryable = e.isRetryable();
            Assert.assertEquals(404, statusCode);
            Assert.assertEquals("No such workflow found by name: testWorkflowDefMetadata, version: 1", errorMessage);
            Assert.assertFalse(retryable);
            throw e;
        }
    }

    @Test(expected = ConductorClientException.class)
    public void testInvalidResource() {
        MetadataClient metadataClient = new MetadataClient();
        metadataClient.setRootURI(String.format("%sinvalid", AbstractHttpEndToEndTest.apiRoot));
        WorkflowDef def = new WorkflowDef();
        def.setName("testWorkflowDel");
        def.setVersion(1);
        try {
            metadataClient.registerWorkflowDef(def);
        } catch (ConductorClientException e) {
            int statusCode = e.getStatus();
            boolean retryable = e.isRetryable();
            Assert.assertEquals(404, statusCode);
            Assert.assertFalse(retryable);
            throw e;
        }
    }

    @Test(expected = ConductorClientException.class)
    public void testUpdateWorkflow() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("taskUpdate");
        ArrayList<TaskDef> tasks = new ArrayList<>();
        tasks.add(taskDef);
        AbstractHttpEndToEndTest.metadataClient.registerTaskDefs(tasks);
        WorkflowDef def = new WorkflowDef();
        def.setName("testWorkflowDel");
        def.setVersion(1);
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("taskUpdate");
        workflowTask.setTaskReferenceName("taskUpdate");
        java.util.List<WorkflowTask> workflowTaskList = new ArrayList<>();
        workflowTaskList.add(workflowTask);
        def.setTasks(workflowTaskList);
        java.util.List<WorkflowDef> workflowList = new ArrayList<>();
        workflowList.add(def);
        AbstractHttpEndToEndTest.metadataClient.registerWorkflowDef(def);
        def.setVersion(2);
        AbstractHttpEndToEndTest.metadataClient.updateWorkflowDefs(workflowList);
        WorkflowDef def1 = AbstractHttpEndToEndTest.metadataClient.getWorkflowDef(def.getName(), 2);
        Assert.assertNotNull(def1);
        try {
            AbstractHttpEndToEndTest.metadataClient.getTaskDef("test");
        } catch (ConductorClientException e) {
            int statuCode = e.getStatus();
            Assert.assertEquals(404, statuCode);
            Assert.assertEquals("No such taskType found by name: test", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartWorkflow() {
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        try {
            AbstractHttpEndToEndTest.workflowClient.startWorkflow(startWorkflowRequest);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Workflow name cannot be null or empty", e.getMessage());
            throw e;
        }
    }

    @Test(expected = ConductorClientException.class)
    public void testUpdateTask() {
        TaskResult taskResult = new TaskResult();
        try {
            AbstractHttpEndToEndTest.taskClient.updateTask(taskResult, "taskTest");
        } catch (ConductorClientException e) {
            Assert.assertEquals(400, e.getStatus());
            Assert.assertEquals("Validation failed, check below errors for detail.", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            java.util.List<ValidationError> errors = e.getValidationErrors();
            java.util.List<String> errorMessages = errors.stream().map(( v) -> v.getMessage()).collect(Collectors.toList());
            Assert.assertEquals(2, errors.size());
            Assert.assertTrue(errorMessages.contains("Workflow Id cannot be null or empty"));
            throw e;
        }
    }

    @Test(expected = ConductorClientException.class)
    public void testGetWorfklowNotFound() {
        try {
            AbstractHttpEndToEndTest.workflowClient.getWorkflow("w123", true);
        } catch (ConductorClientException e) {
            Assert.assertEquals(404, e.getStatus());
            Assert.assertEquals("No such workflow found by id: w123", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            throw e;
        }
    }

    @Test(expected = ConductorClientException.class)
    public void testEmptyCreateWorkflowDef() {
        try {
            WorkflowDef workflowDef = new WorkflowDef();
            AbstractHttpEndToEndTest.metadataClient.registerWorkflowDef(workflowDef);
        } catch (ConductorClientException e) {
            Assert.assertEquals(400, e.getStatus());
            Assert.assertEquals("Validation failed, check below errors for detail.", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            java.util.List<ValidationError> errors = e.getValidationErrors();
            java.util.List<String> errorMessages = errors.stream().map(( v) -> v.getMessage()).collect(Collectors.toList());
            Assert.assertTrue(errorMessages.contains("WorkflowDef name cannot be null or empty"));
            Assert.assertTrue(errorMessages.contains("WorkflowTask list cannot be empty"));
            throw e;
        }
    }

    @Test(expected = ConductorClientException.class)
    public void testUpdateWorkflowDef() {
        try {
            WorkflowDef workflowDef = new WorkflowDef();
            java.util.List<WorkflowDef> workflowDefList = new ArrayList<>();
            workflowDefList.add(workflowDef);
            AbstractHttpEndToEndTest.metadataClient.updateWorkflowDefs(workflowDefList);
        } catch (ConductorClientException e) {
            Assert.assertEquals(400, e.getStatus());
            Assert.assertEquals("Validation failed, check below errors for detail.", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            java.util.List<ValidationError> errors = e.getValidationErrors();
            java.util.List<String> errorMessages = errors.stream().map(( v) -> v.getMessage()).collect(Collectors.toList());
            Assert.assertEquals(2, errors.size());
            Assert.assertTrue(errorMessages.contains("WorkflowTask list cannot be empty"));
            Assert.assertTrue(errorMessages.contains("WorkflowDef name cannot be null or empty"));
            throw e;
        }
    }

    @Test
    public void testGetTaskInProgress() {
        AbstractHttpEndToEndTest.taskClient.getPendingTaskForWorkflow("test", "t1");
    }

    @Test(expected = ConductorClientException.class)
    public void testRemoveTaskFromTaskQueue() {
        try {
            AbstractHttpEndToEndTest.taskClient.removeTaskFromQueue("test", "fakeQueue");
        } catch (ConductorClientException e) {
            Assert.assertEquals(404, e.getStatus());
            throw e;
        }
    }

    @Test
    public void testTaskByTaskId() {
        try {
            AbstractHttpEndToEndTest.taskClient.getTaskDetails("test999");
        } catch (ConductorClientException e) {
            Assert.assertEquals(404, e.getStatus());
            Assert.assertEquals("No such task found by taskId: test999", e.getMessage());
        }
    }

    @Test
    public void testListworkflowsByCorrelationId() {
        AbstractHttpEndToEndTest.workflowClient.getWorkflows("test", "test12", false, false);
    }

    @Test(expected = ConductorClientException.class)
    public void testCreateInvalidWorkflowDef() {
        try {
            WorkflowDef workflowDef = new WorkflowDef();
            java.util.List<WorkflowDef> workflowDefList = new ArrayList<>();
            workflowDefList.add(workflowDef);
            AbstractHttpEndToEndTest.metadataClient.registerWorkflowDef(workflowDef);
        } catch (ConductorClientException e) {
            Assert.assertEquals(2, e.getValidationErrors().size());
            Assert.assertEquals(400, e.getStatus());
            Assert.assertEquals("Validation failed, check below errors for detail.", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            java.util.List<ValidationError> errors = e.getValidationErrors();
            java.util.List<String> errorMessages = errors.stream().map(( v) -> v.getMessage()).collect(Collectors.toList());
            Assert.assertTrue(errorMessages.contains("WorkflowDef name cannot be null or empty"));
            Assert.assertTrue(errorMessages.contains("WorkflowTask list cannot be empty"));
            throw e;
        }
    }

    @Test(expected = ConductorClientException.class)
    public void testUpdateTaskDefNameNull() {
        TaskDef taskDef = new TaskDef();
        try {
            AbstractHttpEndToEndTest.metadataClient.updateTaskDef(taskDef);
        } catch (ConductorClientException e) {
            Assert.assertEquals(1, e.getValidationErrors().size());
            Assert.assertEquals(400, e.getStatus());
            Assert.assertEquals("Validation failed, check below errors for detail.", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            java.util.List<ValidationError> errors = e.getValidationErrors();
            java.util.List<String> errorMessages = errors.stream().map(( v) -> v.getMessage()).collect(Collectors.toList());
            Assert.assertTrue(errorMessages.contains("TaskDef name cannot be null or empty"));
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTaskDefNotExisting() {
        AbstractHttpEndToEndTest.metadataClient.getTaskDef("");
    }

    @Test(expected = ConductorClientException.class)
    public void testUpdateWorkflowDefNameNull() {
        WorkflowDef workflowDef = new WorkflowDef();
        java.util.List<WorkflowDef> list = new ArrayList<>();
        list.add(workflowDef);
        try {
            AbstractHttpEndToEndTest.metadataClient.updateWorkflowDefs(list);
        } catch (ConductorClientException e) {
            Assert.assertEquals(2, e.getValidationErrors().size());
            Assert.assertEquals(400, e.getStatus());
            Assert.assertEquals("Validation failed, check below errors for detail.", e.getMessage());
            Assert.assertFalse(e.isRetryable());
            java.util.List<ValidationError> errors = e.getValidationErrors();
            java.util.List<String> errorMessages = errors.stream().map(( v) -> v.getMessage()).collect(Collectors.toList());
            Assert.assertTrue(errorMessages.contains("WorkflowDef name cannot be null or empty"));
            Assert.assertTrue(errorMessages.contains("WorkflowTask list cannot be empty"));
            throw e;
        }
    }
}

