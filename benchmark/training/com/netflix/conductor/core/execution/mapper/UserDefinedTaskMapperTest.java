package com.netflix.conductor.core.execution.mapper;


import TaskType.USER_DEFINED;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.execution.TerminateWorkflowException;
import com.netflix.conductor.core.utils.IDGenerator;
import com.netflix.conductor.dao.MetadataDAO;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UserDefinedTaskMapperTest {
    private ParametersUtils parametersUtils;

    private MetadataDAO metadataDAO;

    private UserDefinedTaskMapper userDefinedTaskMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getMappedTasks() {
        // Given
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setName("user_task");
        taskToSchedule.setType(USER_DEFINED.name());
        taskToSchedule.setTaskDefinition(new TaskDef("user_task"));
        String taskId = IDGenerator.generate();
        String retriedTaskId = IDGenerator.generate();
        Workflow workflow = new Workflow();
        WorkflowDef workflowDef = new WorkflowDef();
        workflow.setWorkflowDefinition(workflowDef);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(workflowDef).withWorkflowInstance(workflow).withTaskDefinition(new TaskDef()).withTaskToSchedule(taskToSchedule).withTaskInput(new HashMap()).withRetryCount(0).withRetryTaskId(retriedTaskId).withTaskId(taskId).build();
        // when
        List<Task> mappedTasks = userDefinedTaskMapper.getMappedTasks(taskMapperContext);
        // Then
        Assert.assertEquals(1, mappedTasks.size());
        Assert.assertEquals(USER_DEFINED.name(), mappedTasks.get(0).getTaskType());
    }

    @Test
    public void getMappedTasksException() {
        // Given
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setName("user_task");
        taskToSchedule.setType(USER_DEFINED.name());
        String taskId = IDGenerator.generate();
        String retriedTaskId = IDGenerator.generate();
        Workflow workflow = new Workflow();
        WorkflowDef workflowDef = new WorkflowDef();
        workflow.setWorkflowDefinition(workflowDef);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(workflowDef).withWorkflowInstance(workflow).withTaskToSchedule(taskToSchedule).withTaskInput(new HashMap()).withRetryCount(0).withRetryTaskId(retriedTaskId).withTaskId(taskId).build();
        // then
        expectedException.expect(TerminateWorkflowException.class);
        expectedException.expectMessage(String.format("Invalid task specified. Cannot find task by name %s in the task definitions", taskToSchedule.getName()));
        // when
        userDefinedTaskMapper.getMappedTasks(taskMapperContext);
    }
}

