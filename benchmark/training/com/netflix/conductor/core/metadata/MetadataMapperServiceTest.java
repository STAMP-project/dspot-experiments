package com.netflix.conductor.core.metadata;


import TaskType.SUB_WORKFLOW;
import com.google.common.collect.ImmutableList;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.SubWorkflowParams;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.core.execution.ApplicationException;
import com.netflix.conductor.core.execution.TerminateWorkflowException;
import com.netflix.conductor.dao.MetadataDAO;
import com.netflix.conductor.utility.TestUtils;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class MetadataMapperServiceTest {
    @Mock
    private MetadataDAO metadataDAO;

    private MetadataMapperService metadataMapperService;

    @Test
    public void testMetadataPopulationOnSimpleTask() {
        String nameTaskDefinition = "task1";
        TaskDef taskDefinition = createTaskDefinition(nameTaskDefinition);
        WorkflowTask workflowTask = createWorkflowTask(nameTaskDefinition);
        Mockito.when(metadataDAO.getTaskDef(nameTaskDefinition)).thenReturn(taskDefinition);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testMetadataPopulation");
        workflowDefinition.setTasks(ImmutableList.of(workflowTask));
        metadataMapperService.populateTaskDefinitions(workflowDefinition);
        Assert.assertEquals(1, workflowDefinition.getTasks().size());
        WorkflowTask populatedWorkflowTask = workflowDefinition.getTasks().get(0);
        assertNotNull(populatedWorkflowTask.getTaskDefinition());
        Mockito.verify(metadataDAO).getTaskDef(nameTaskDefinition);
    }

    @Test
    public void testNoMetadataPopulationOnEmbeddedTaskDefinition() {
        String nameTaskDefinition = "task2";
        TaskDef taskDefinition = createTaskDefinition(nameTaskDefinition);
        WorkflowTask workflowTask = createWorkflowTask(nameTaskDefinition);
        workflowTask.setTaskDefinition(taskDefinition);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testMetadataPopulation");
        workflowDefinition.setTasks(ImmutableList.of(workflowTask));
        metadataMapperService.populateTaskDefinitions(workflowDefinition);
        Assert.assertEquals(1, workflowDefinition.getTasks().size());
        WorkflowTask populatedWorkflowTask = workflowDefinition.getTasks().get(0);
        assertNotNull(populatedWorkflowTask.getTaskDefinition());
        Mockito.verifyZeroInteractions(metadataDAO);
    }

    @Test
    public void testMetadataPopulationOnlyOnNecessaryWorkflowTasks() {
        String nameTaskDefinition1 = "task4";
        TaskDef taskDefinition = createTaskDefinition(nameTaskDefinition1);
        WorkflowTask workflowTask1 = createWorkflowTask(nameTaskDefinition1);
        workflowTask1.setTaskDefinition(taskDefinition);
        String nameTaskDefinition2 = "task5";
        WorkflowTask workflowTask2 = createWorkflowTask(nameTaskDefinition2);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testMetadataPopulation");
        workflowDefinition.setTasks(ImmutableList.of(workflowTask1, workflowTask2));
        Mockito.when(metadataDAO.getTaskDef(nameTaskDefinition2)).thenReturn(taskDefinition);
        metadataMapperService.populateTaskDefinitions(workflowDefinition);
        Assert.assertEquals(2, workflowDefinition.getTasks().size());
        List<WorkflowTask> workflowTasks = workflowDefinition.getTasks();
        assertNotNull(workflowTasks.get(0).getTaskDefinition());
        assertNotNull(workflowTasks.get(1).getTaskDefinition());
        Mockito.verify(metadataDAO).getTaskDef(nameTaskDefinition2);
        Mockito.verifyNoMoreInteractions(metadataDAO);
    }

    @Test(expected = ApplicationException.class)
    public void testMetadataPopulationMissingDefinitions() {
        String nameTaskDefinition1 = "task4";
        WorkflowTask workflowTask1 = createWorkflowTask(nameTaskDefinition1);
        String nameTaskDefinition2 = "task5";
        WorkflowTask workflowTask2 = createWorkflowTask(nameTaskDefinition2);
        TaskDef taskDefinition = createTaskDefinition(nameTaskDefinition1);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testMetadataPopulation");
        workflowDefinition.setTasks(ImmutableList.of(workflowTask1, workflowTask2));
        Mockito.when(metadataDAO.getTaskDef(nameTaskDefinition1)).thenReturn(taskDefinition);
        Mockito.when(metadataDAO.getTaskDef(nameTaskDefinition2)).thenReturn(null);
        metadataMapperService.populateTaskDefinitions(workflowDefinition);
    }

    @Test
    public void testVersionPopulationForSubworkflowTaskIfVersionIsNotAvailable() {
        String nameTaskDefinition = "taskSubworkflow6";
        String workflowDefinitionName = "subworkflow";
        Integer version = 3;
        WorkflowDef subWorkflowDefinition = createWorkflowDefinition("workflowDefinitionName");
        subWorkflowDefinition.setVersion(version);
        WorkflowTask workflowTask = createWorkflowTask(nameTaskDefinition);
        workflowTask.setWorkflowTaskType(SUB_WORKFLOW);
        SubWorkflowParams subWorkflowParams = new SubWorkflowParams();
        subWorkflowParams.setName(workflowDefinitionName);
        workflowTask.setSubWorkflowParam(subWorkflowParams);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testMetadataPopulation");
        workflowDefinition.setTasks(ImmutableList.of(workflowTask));
        Mockito.when(metadataDAO.getLatest(workflowDefinitionName)).thenReturn(Optional.of(subWorkflowDefinition));
        metadataMapperService.populateTaskDefinitions(workflowDefinition);
        Assert.assertEquals(1, workflowDefinition.getTasks().size());
        List<WorkflowTask> workflowTasks = workflowDefinition.getTasks();
        SubWorkflowParams params = workflowTasks.get(0).getSubWorkflowParam();
        Assert.assertEquals(workflowDefinitionName, params.getName());
        Assert.assertEquals(version, params.getVersion());
        Mockito.verify(metadataDAO).getLatest(workflowDefinitionName);
        Mockito.verifyNoMoreInteractions(metadataDAO);
    }

    @Test
    public void testNoVersionPopulationForSubworkflowTaskIfAvailable() {
        String nameTaskDefinition = "taskSubworkflow7";
        String workflowDefinitionName = "subworkflow";
        Integer version = 2;
        WorkflowTask workflowTask = createWorkflowTask(nameTaskDefinition);
        workflowTask.setWorkflowTaskType(SUB_WORKFLOW);
        SubWorkflowParams subWorkflowParams = new SubWorkflowParams();
        subWorkflowParams.setName(workflowDefinitionName);
        subWorkflowParams.setVersion(version);
        workflowTask.setSubWorkflowParam(subWorkflowParams);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testMetadataPopulation");
        workflowDefinition.setTasks(ImmutableList.of(workflowTask));
        metadataMapperService.populateTaskDefinitions(workflowDefinition);
        Assert.assertEquals(1, workflowDefinition.getTasks().size());
        List<WorkflowTask> workflowTasks = workflowDefinition.getTasks();
        SubWorkflowParams params = workflowTasks.get(0).getSubWorkflowParam();
        Assert.assertEquals(workflowDefinitionName, params.getName());
        Assert.assertEquals(version, params.getVersion());
        Mockito.verifyZeroInteractions(metadataDAO);
    }

    @Test(expected = TerminateWorkflowException.class)
    public void testExceptionWhenWorkflowDefinitionNotAvailable() {
        String nameTaskDefinition = "taskSubworkflow8";
        String workflowDefinitionName = "subworkflow";
        WorkflowTask workflowTask = createWorkflowTask(nameTaskDefinition);
        workflowTask.setWorkflowTaskType(SUB_WORKFLOW);
        SubWorkflowParams subWorkflowParams = new SubWorkflowParams();
        subWorkflowParams.setName(workflowDefinitionName);
        workflowTask.setSubWorkflowParam(subWorkflowParams);
        WorkflowDef workflowDefinition = createWorkflowDefinition("testMetadataPopulation");
        workflowDefinition.setTasks(ImmutableList.of(workflowTask));
        Mockito.when(metadataDAO.getLatest(workflowDefinitionName)).thenReturn(Optional.empty());
        metadataMapperService.populateTaskDefinitions(workflowDefinition);
        Mockito.verify(metadataDAO).getLatest(workflowDefinitionName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupWorkflowDefinition() {
        try {
            String workflowName = "test";
            Mockito.when(metadataDAO.get(workflowName, 0)).thenReturn(Optional.of(new WorkflowDef()));
            Optional<WorkflowDef> optionalWorkflowDef = metadataMapperService.lookupWorkflowDefinition(workflowName, 0);
            assertTrue(optionalWorkflowDef.isPresent());
            metadataMapperService.lookupWorkflowDefinition(null, 0);
        } catch (ConstraintViolationException ex) {
            assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            assertTrue(messages.contains("WorkflowIds list cannot be null."));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupLatestWorkflowDefinition() {
        String workflowName = "test";
        Mockito.when(metadataDAO.getLatest(workflowName)).thenReturn(Optional.of(new WorkflowDef()));
        Optional<WorkflowDef> optionalWorkflowDef = metadataMapperService.lookupLatestWorkflowDefinition(workflowName);
        assertTrue(optionalWorkflowDef.isPresent());
        metadataMapperService.lookupLatestWorkflowDefinition(null);
    }
}

