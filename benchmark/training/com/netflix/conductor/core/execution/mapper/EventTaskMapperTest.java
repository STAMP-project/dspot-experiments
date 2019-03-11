package com.netflix.conductor.core.execution.mapper;


import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.utils.IDGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EventTaskMapperTest {
    @Test
    public void getMappedTasks() throws Exception {
        ParametersUtils parametersUtils = Mockito.mock(ParametersUtils.class);
        EventTaskMapper eventTaskMapper = new EventTaskMapper(parametersUtils);
        WorkflowTask taskToBeScheduled = new WorkflowTask();
        taskToBeScheduled.setSink("SQSSINK");
        String taskId = IDGenerator.generate();
        Map<String, Object> eventTaskInput = new HashMap<>();
        eventTaskInput.put("sink", "SQSSINK");
        Mockito.when(parametersUtils.getTaskInput(ArgumentMatchers.anyMap(), ArgumentMatchers.any(Workflow.class), ArgumentMatchers.any(TaskDef.class), ArgumentMatchers.anyString())).thenReturn(eventTaskInput);
        WorkflowDef wd = new WorkflowDef();
        Workflow w = new Workflow();
        w.setWorkflowDefinition(wd);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(wd).withWorkflowInstance(w).withTaskDefinition(new TaskDef()).withTaskToSchedule(taskToBeScheduled).withRetryCount(0).withTaskId(taskId).build();
        List<Task> mappedTasks = eventTaskMapper.getMappedTasks(taskMapperContext);
        Assert.assertEquals(1, mappedTasks.size());
        Task eventTask = mappedTasks.get(0);
        Assert.assertEquals(taskId, eventTask.getTaskId());
    }
}

