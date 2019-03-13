/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.tests.integration;


import TaskResult.Status.COMPLETED;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.elasticsearch.EmbeddedElasticSearch;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ExclusiveJoinEndToEndTest {
    private static TaskClient taskClient;

    private static WorkflowClient workflowClient;

    private static MetadataClient metadataClient;

    private static EmbeddedElasticSearch search;

    private static final int SERVER_PORT = 8093;

    private static String CONDUCTOR_WORKFLOW_DEF_NAME = "ExclusiveJoinTestWorkflow";

    private static Map<String, Object> workflowInput = new HashMap<>();

    private static Map<String, Object> taskOutput = new HashMap<>();

    @Test
    public void testDecision1Default() throws Exception {
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_1", "null");
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest().withName(ExclusiveJoinEndToEndTest.CONDUCTOR_WORKFLOW_DEF_NAME).withCorrelationId("").withInput(ExclusiveJoinEndToEndTest.workflowInput).withVersion(1);
        String wfInstanceId = ExclusiveJoinEndToEndTest.workflowClient.startWorkflow(startWorkflowRequest);
        String taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task1").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task1");
        TaskResult taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        Workflow workflow = ExclusiveJoinEndToEndTest.workflowClient.getWorkflow(wfInstanceId, true);
        String taskReferenceName = workflow.getTaskByRefName("exclusiveJoin").getOutputData().get("taskReferenceName").toString();
        Assert.assertEquals("task1", taskReferenceName);
        Assert.assertEquals(Workflow.WorkflowStatus.COMPLETED, workflow.getStatus());
    }

    @Test
    public void testDecision1TrueAndDecision2Default() throws Exception {
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_1", "true");
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_2", "null");
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest().withName(ExclusiveJoinEndToEndTest.CONDUCTOR_WORKFLOW_DEF_NAME).withCorrelationId("").withInput(ExclusiveJoinEndToEndTest.workflowInput).withVersion(1);
        String wfInstanceId = ExclusiveJoinEndToEndTest.workflowClient.startWorkflow(startWorkflowRequest);
        String taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task1").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task1");
        TaskResult taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task2").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task2");
        taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        Workflow workflow = ExclusiveJoinEndToEndTest.workflowClient.getWorkflow(wfInstanceId, true);
        String taskReferenceName = workflow.getTaskByRefName("exclusiveJoin").getOutputData().get("taskReferenceName").toString();
        Assert.assertEquals("task2", taskReferenceName);
        Assert.assertEquals(Workflow.WorkflowStatus.COMPLETED, workflow.getStatus());
    }

    @Test
    public void testDecision1TrueAndDecision2True() throws Exception {
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_1", "true");
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_2", "true");
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest().withName(ExclusiveJoinEndToEndTest.CONDUCTOR_WORKFLOW_DEF_NAME).withCorrelationId("").withInput(ExclusiveJoinEndToEndTest.workflowInput).withVersion(1);
        String wfInstanceId = ExclusiveJoinEndToEndTest.workflowClient.startWorkflow(startWorkflowRequest);
        String taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task1").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task1");
        TaskResult taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task2").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task2");
        taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task3").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task3");
        taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        Workflow workflow = ExclusiveJoinEndToEndTest.workflowClient.getWorkflow(wfInstanceId, true);
        String taskReferenceName = workflow.getTaskByRefName("exclusiveJoin").getOutputData().get("taskReferenceName").toString();
        Assert.assertEquals("task3", taskReferenceName);
        Assert.assertEquals(Workflow.WorkflowStatus.COMPLETED, workflow.getStatus());
    }

    @Test
    public void testDecision1FalseAndDecision3Default() throws Exception {
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_1", "false");
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_3", "null");
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest().withName(ExclusiveJoinEndToEndTest.CONDUCTOR_WORKFLOW_DEF_NAME).withCorrelationId("").withInput(ExclusiveJoinEndToEndTest.workflowInput).withVersion(1);
        String wfInstanceId = ExclusiveJoinEndToEndTest.workflowClient.startWorkflow(startWorkflowRequest);
        String taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task1").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task1");
        TaskResult taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task4").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task4");
        taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        Workflow workflow = ExclusiveJoinEndToEndTest.workflowClient.getWorkflow(wfInstanceId, true);
        String taskReferenceName = workflow.getTaskByRefName("exclusiveJoin").getOutputData().get("taskReferenceName").toString();
        Assert.assertEquals("task4", taskReferenceName);
        Assert.assertEquals(Workflow.WorkflowStatus.COMPLETED, workflow.getStatus());
    }

    @Test
    public void testDecision1FalseAndDecision3True() throws Exception {
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_1", "false");
        ExclusiveJoinEndToEndTest.workflowInput.put("decision_3", "true");
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest().withName(ExclusiveJoinEndToEndTest.CONDUCTOR_WORKFLOW_DEF_NAME).withCorrelationId("").withInput(ExclusiveJoinEndToEndTest.workflowInput).withVersion(1);
        String wfInstanceId = ExclusiveJoinEndToEndTest.workflowClient.startWorkflow(startWorkflowRequest);
        String taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task1").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task1");
        TaskResult taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task4").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task4");
        taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        taskId = ExclusiveJoinEndToEndTest.taskClient.getPendingTaskForWorkflow(wfInstanceId, "task5").getTaskId();
        ExclusiveJoinEndToEndTest.taskOutput.put("taskReferenceName", "task5");
        taskResult = setTaskResult(wfInstanceId, taskId, COMPLETED, ExclusiveJoinEndToEndTest.taskOutput);
        ExclusiveJoinEndToEndTest.taskClient.updateTask(taskResult, "");
        Workflow workflow = ExclusiveJoinEndToEndTest.workflowClient.getWorkflow(wfInstanceId, true);
        String taskReferenceName = workflow.getTaskByRefName("exclusiveJoin").getOutputData().get("taskReferenceName").toString();
        Assert.assertEquals("task5", taskReferenceName);
        Assert.assertEquals(Workflow.WorkflowStatus.COMPLETED, workflow.getStatus());
    }
}

