package com.netflix.conductor.core.execution.mapper;


import TaskType.LAMBDA;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.utils.IDGenerator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LambdaTaskMapperTest {
    private ParametersUtils parametersUtils;

    @Test
    public void getMappedTasks() throws Exception {
        WorkflowTask taskToSchedule = new WorkflowTask();
        taskToSchedule.setType(LAMBDA.name());
        taskToSchedule.setScriptExpression("if ($.input.a==1){return {testValue: true}} else{return {testValue: false} }");
        String taskId = IDGenerator.generate();
        WorkflowDef wd = new WorkflowDef();
        Workflow w = new Workflow();
        w.setWorkflowDefinition(wd);
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(wd).withWorkflowInstance(w).withTaskDefinition(new TaskDef()).withTaskToSchedule(taskToSchedule).withRetryCount(0).withTaskId(taskId).build();
        List<Task> mappedTasks = new LambdaTaskMapper(parametersUtils).getMappedTasks(taskMapperContext);
        Assert.assertEquals(1, mappedTasks.size());
        Assert.assertNotNull(mappedTasks);
        Assert.assertEquals(LAMBDA.name(), mappedTasks.get(0).getTaskType());
    }
}

