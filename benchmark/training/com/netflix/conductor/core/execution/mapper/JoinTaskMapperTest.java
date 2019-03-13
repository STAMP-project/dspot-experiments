package com.netflix.conductor.core.execution.mapper;


import TaskType.JOIN;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.utils.IDGenerator;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JoinTaskMapperTest {
    @Test
    public void getMappedTasks() throws Exception {
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setType(JOIN.name());
        taskToSchedule.setJoinOn(Arrays.asList("task1, task2"));
        String taskId = IDGenerator.generate();
        WorkflowDef wd = new WorkflowDef();
        Workflow w = new Workflow();
        w.setWorkflowDefinition(wd);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(wd).withWorkflowInstance(w).withTaskDefinition(new TaskDef()).withTaskToSchedule(taskToSchedule).withRetryCount(0).withTaskId(taskId).build();
        List<Task> mappedTasks = new JoinTaskMapper().getMappedTasks(taskMapperContext);
        Assert.assertNotNull(mappedTasks);
        Assert.assertEquals(SystemTaskType.JOIN.name(), mappedTasks.get(0).getTaskType());
    }
}

