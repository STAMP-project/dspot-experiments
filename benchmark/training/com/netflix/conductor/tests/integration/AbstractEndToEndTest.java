package com.netflix.conductor.tests.integration;


import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractEndToEndTest {
    private static final String TASK_DEFINITION_PREFIX = "task_";

    private static final String DEFAULT_DESCRIPTION = "description";

    // Represents null value deserialized from the redis in memory db
    private static final String DEFAULT_NULL_VALUE = "null";

    @Test
    public void testEphemeralWorkflowsWithStoredTasks() {
        String workflowExecutionName = "testEphemeralWorkflow";
        createAndRegisterTaskDefinitions("storedTaskDef", 5);
        WorkflowDef workflowDefinition = createWorkflowDefinition(workflowExecutionName);
        WorkflowTask workflowTask1 = createWorkflowTask("storedTaskDef1");
        WorkflowTask workflowTask2 = createWorkflowTask("storedTaskDef2");
        workflowDefinition.getTasks().addAll(Arrays.asList(workflowTask1, workflowTask2));
        String workflowId = startWorkflow(workflowExecutionName, workflowDefinition);
        Assert.assertNotNull(workflowId);
        Workflow workflow = getWorkflow(workflowId, true);
        WorkflowDef ephemeralWorkflow = workflow.getWorkflowDefinition();
        Assert.assertNotNull(ephemeralWorkflow);
        Assert.assertEquals(workflowDefinition, ephemeralWorkflow);
    }

    @Test
    public void testEphemeralWorkflowsWithEphemeralTasks() {
        String workflowExecutionName = "ephemeralWorkflowWithEphemeralTasks";
        WorkflowDef workflowDefinition = createWorkflowDefinition(workflowExecutionName);
        WorkflowTask workflowTask1 = createWorkflowTask("ephemeralTask1");
        TaskDef taskDefinition1 = createTaskDefinition("ephemeralTaskDef1");
        workflowTask1.setTaskDefinition(taskDefinition1);
        WorkflowTask workflowTask2 = createWorkflowTask("ephemeralTask2");
        TaskDef taskDefinition2 = createTaskDefinition("ephemeralTaskDef2");
        workflowTask2.setTaskDefinition(taskDefinition2);
        workflowDefinition.getTasks().addAll(Arrays.asList(workflowTask1, workflowTask2));
        String workflowId = startWorkflow(workflowExecutionName, workflowDefinition);
        Assert.assertNotNull(workflowId);
        Workflow workflow = getWorkflow(workflowId, true);
        WorkflowDef ephemeralWorkflow = workflow.getWorkflowDefinition();
        Assert.assertNotNull(ephemeralWorkflow);
        Assert.assertEquals(workflowDefinition, ephemeralWorkflow);
        List<WorkflowTask> ephemeralTasks = ephemeralWorkflow.getTasks();
        Assert.assertEquals(2, ephemeralTasks.size());
        for (WorkflowTask ephemeralTask : ephemeralTasks) {
            Assert.assertNotNull(ephemeralTask.getTaskDefinition());
        }
    }

    @Test
    public void testEphemeralWorkflowsWithEphemeralAndStoredTasks() {
        createAndRegisterTaskDefinitions("storedTask", 1);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testEphemeralWorkflowsWithEphemeralAndStoredTasks");
        WorkflowTask workflowTask1 = createWorkflowTask("ephemeralTask1");
        TaskDef taskDefinition1 = createTaskDefinition("ephemeralTaskDef1");
        workflowTask1.setTaskDefinition(taskDefinition1);
        WorkflowTask workflowTask2 = createWorkflowTask("storedTask0");
        workflowDefinition.getTasks().add(workflowTask1);
        workflowDefinition.getTasks().add(workflowTask2);
        String workflowExecutionName = "ephemeralWorkflowWithEphemeralAndStoredTasks";
        String workflowId = startWorkflow(workflowExecutionName, workflowDefinition);
        Assert.assertNotNull(workflowId);
        Workflow workflow = getWorkflow(workflowId, true);
        WorkflowDef ephemeralWorkflow = workflow.getWorkflowDefinition();
        Assert.assertNotNull(ephemeralWorkflow);
        Assert.assertEquals(workflowDefinition, ephemeralWorkflow);
        TaskDef storedTaskDefinition = getTaskDefinition("storedTask0");
        List<WorkflowTask> tasks = ephemeralWorkflow.getTasks();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals(workflowTask1, tasks.get(0));
        TaskDef currentStoredTaskDefinition = tasks.get(1).getTaskDefinition();
        Assert.assertNotNull(currentStoredTaskDefinition);
        Assert.assertEquals(storedTaskDefinition, currentStoredTaskDefinition);
    }
}

