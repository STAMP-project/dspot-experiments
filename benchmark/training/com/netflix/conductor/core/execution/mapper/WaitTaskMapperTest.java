package com.netflix.conductor.core.execution.mapper;


import TaskType.WAIT;
import Wait.NAME;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.utils.IDGenerator;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class WaitTaskMapperTest {
    @Test
    public void getMappedTasks() {
        // Given
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setName("Wait_task");
        taskToSchedule.setType(WAIT.name());
        String taskId = IDGenerator.generate();
        ParametersUtils parametersUtils = new ParametersUtils();
        Workflow workflow = new Workflow();
        WorkflowDef workflowDef = new WorkflowDef();
        workflow.setWorkflowDefinition(workflowDef);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(workflowDef).withWorkflowInstance(workflow).withTaskDefinition(new TaskDef()).withTaskToSchedule(taskToSchedule).withTaskInput(new HashMap()).withRetryCount(0).withTaskId(taskId).build();
        WaitTaskMapper waitTaskMapper = new WaitTaskMapper(parametersUtils);
        // When
        List<Task> mappedTasks = waitTaskMapper.getMappedTasks(taskMapperContext);
        // Then
        Assert.assertEquals(1, mappedTasks.size());
        Assert.assertEquals(NAME, mappedTasks.get(0).getTaskType());
    }
}

