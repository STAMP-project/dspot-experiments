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
package com.netflix.conductor.core.events;


import Status.COMPLETED;
import Type.complete_task;
import Type.start_workflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.events.EventHandler.Action;
import com.netflix.conductor.common.metadata.events.EventHandler.StartWorkflow;
import com.netflix.conductor.common.metadata.events.EventHandler.TaskDetails;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestActionProcessor {
    private WorkflowExecutor workflowExecutor;

    private ActionProcessor actionProcessor;

    @SuppressWarnings("unchecked")
    @Test
    public void testStartWorkflow() throws Exception {
        StartWorkflow startWorkflow = new StartWorkflow();
        startWorkflow.setName("testWorkflow");
        startWorkflow.getInput().put("testInput", "${testId}");
        Map<String, String> taskToDomain = new HashMap<>();
        taskToDomain.put("*", "dev");
        startWorkflow.setTaskToDomain(taskToDomain);
        Action action = new Action();
        action.setAction(start_workflow);
        action.setStart_workflow(startWorkflow);
        Object payload = new ObjectMapper().readValue("{\"testId\":\"test_1\"}", Object.class);
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("testWorkflow");
        workflowDef.setVersion(1);
        Mockito.when(workflowExecutor.startWorkflow(ArgumentMatchers.eq("testWorkflow"), ArgumentMatchers.eq(null), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq("testEvent"), ArgumentMatchers.anyMap())).thenReturn("workflow_1");
        Map<String, Object> output = actionProcessor.execute(action, payload, "testEvent", "testMessage");
        Assert.assertNotNull(output);
        Assert.assertEquals("workflow_1", output.get("workflowId"));
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(workflowExecutor).startWorkflow(ArgumentMatchers.eq("testWorkflow"), ArgumentMatchers.eq(null), ArgumentMatchers.any(), argumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq("testEvent"), captor.capture());
        Assert.assertEquals("test_1", argumentCaptor.getValue().get("testInput"));
        Assert.assertEquals("testMessage", argumentCaptor.getValue().get("conductor.event.messageId"));
        Assert.assertEquals("testEvent", argumentCaptor.getValue().get("conductor.event.name"));
        Assert.assertEquals(taskToDomain, captor.getValue());
    }

    @Test
    public void testCompleteTask() throws Exception {
        TaskDetails taskDetails = new TaskDetails();
        taskDetails.setWorkflowId("${workflowId}");
        taskDetails.setTaskRefName("testTask");
        Action action = new Action();
        action.setAction(complete_task);
        action.setComplete_task(taskDetails);
        Object payload = new ObjectMapper().readValue("{\"workflowId\":\"workflow_1\"}", Object.class);
        Task task = new Task();
        task.setReferenceTaskName("testTask");
        Workflow workflow = new Workflow();
        workflow.getTasks().add(task);
        Mockito.when(workflowExecutor.getWorkflow(ArgumentMatchers.eq("workflow_1"), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        actionProcessor.execute(action, payload, "testEvent", "testMessage");
        ArgumentCaptor<TaskResult> argumentCaptor = ArgumentCaptor.forClass(TaskResult.class);
        Mockito.verify(workflowExecutor).updateTask(argumentCaptor.capture());
        Assert.assertEquals(COMPLETED, argumentCaptor.getValue().getStatus());
        Assert.assertEquals("testMessage", argumentCaptor.getValue().getOutputData().get("conductor.event.messageId"));
        Assert.assertEquals("testEvent", argumentCaptor.getValue().getOutputData().get("conductor.event.name"));
        Assert.assertEquals("workflow_1", argumentCaptor.getValue().getOutputData().get("workflowId"));
        Assert.assertEquals("testTask", argumentCaptor.getValue().getOutputData().get("taskRefName"));
    }

    @Test
    public void testCompleteTaskByTaskId() throws Exception {
        TaskDetails taskDetails = new TaskDetails();
        taskDetails.setWorkflowId("${workflowId}");
        taskDetails.setTaskId("${taskId}");
        Action action = new Action();
        action.setAction(complete_task);
        action.setComplete_task(taskDetails);
        Object payload = new ObjectMapper().readValue("{\"workflowId\":\"workflow_1\", \"taskId\":\"task_1\"}", Object.class);
        Task task = new Task();
        task.setTaskId("task_1");
        task.setReferenceTaskName("testTask");
        Mockito.when(workflowExecutor.getTask(ArgumentMatchers.eq("task_1"))).thenReturn(task);
        actionProcessor.execute(action, payload, "testEvent", "testMessage");
        ArgumentCaptor<TaskResult> argumentCaptor = ArgumentCaptor.forClass(TaskResult.class);
        Mockito.verify(workflowExecutor).updateTask(argumentCaptor.capture());
        Assert.assertEquals(COMPLETED, argumentCaptor.getValue().getStatus());
        Assert.assertEquals("testMessage", argumentCaptor.getValue().getOutputData().get("conductor.event.messageId"));
        Assert.assertEquals("testEvent", argumentCaptor.getValue().getOutputData().get("conductor.event.name"));
        Assert.assertEquals("workflow_1", argumentCaptor.getValue().getOutputData().get("workflowId"));
        Assert.assertEquals("task_1", argumentCaptor.getValue().getOutputData().get("taskId"));
    }
}

