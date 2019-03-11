package com.netflix.conductor.validations;


import TaskType.TASK_TYPE_SIMPLE;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.dao.MetadataDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WorkflowDefConstraintTest {
    private Validator validator;

    private MetadataDAO mockMetadataDao;

    private HibernateValidatorConfiguration config;

    @Test
    public void testWorkflowTaskName() {
        TaskDef taskDef = new TaskDef();// name is null

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> result = validator.validate(taskDef);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testWorkflowTaskSimple() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("sampleWorkflow");
        workflowDef.setDescription("Sample workflow def");
        workflowDef.setVersion(2);
        WorkflowTask workflowTask_1 = new WorkflowTask();
        workflowTask_1.setName("task_1");
        workflowTask_1.setTaskReferenceName("task_1");
        workflowTask_1.setType(TASK_TYPE_SIMPLE);
        Map<String, Object> inputParam = new HashMap<>();
        inputParam.put("fileLocation", "${workflow.input.fileLocation}");
        workflowTask_1.setInputParameters(inputParam);
        List<WorkflowTask> tasks = new ArrayList<>();
        tasks.add(workflowTask_1);
        workflowDef.setTasks(tasks);
        Set<ConstraintViolation<WorkflowDef>> result = validator.validate(workflowDef);
        Assert.assertEquals(0, result.size());
    }

    /* Testcase to check inputParam is not valid */
    @Test
    public void testWorkflowTaskInvalidInputParam() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("sampleWorkflow");
        workflowDef.setDescription("Sample workflow def");
        workflowDef.setVersion(2);
        WorkflowTask workflowTask_1 = new WorkflowTask();
        workflowTask_1.setName("task_1");
        workflowTask_1.setTaskReferenceName("task_1");
        workflowTask_1.setType(TASK_TYPE_SIMPLE);
        Map<String, Object> inputParam = new HashMap<>();
        inputParam.put("fileLocation", "${work.input.fileLocation}");
        workflowTask_1.setInputParameters(inputParam);
        List<WorkflowTask> tasks = new ArrayList<>();
        tasks.add(workflowTask_1);
        workflowDef.setTasks(tasks);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Mockito.when(mockMetadataDao.getTaskDef("work1")).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowDef>> result = validator.validate(workflowDef);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.iterator().next().getMessage(), "taskReferenceName: work for given task: task_1 input value: fileLocation of input parameter: ${work.input.fileLocation} is not defined in workflow definition.");
    }

    @Test
    public void testWorkflowTaskReferenceNameNotUnique() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("sampleWorkflow");
        workflowDef.setDescription("Sample workflow def");
        workflowDef.setVersion(2);
        WorkflowTask workflowTask_1 = new WorkflowTask();
        workflowTask_1.setName("task_1");
        workflowTask_1.setTaskReferenceName("task_1");
        workflowTask_1.setType(TASK_TYPE_SIMPLE);
        Map<String, Object> inputParam = new HashMap<>();
        inputParam.put("fileLocation", "${task_2.input.fileLocation}");
        workflowTask_1.setInputParameters(inputParam);
        WorkflowTask workflowTask_2 = new WorkflowTask();
        workflowTask_2.setName("task_2");
        workflowTask_2.setTaskReferenceName("task_1");
        workflowTask_2.setType(TASK_TYPE_SIMPLE);
        workflowTask_2.setInputParameters(inputParam);
        List<WorkflowTask> tasks = new ArrayList<>();
        tasks.add(workflowTask_1);
        tasks.add(workflowTask_2);
        workflowDef.setTasks(tasks);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowDef>> result = validator.validate(workflowDef);
        Assert.assertEquals(3, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("taskReferenceName: task_2 for given task: task_2 input value: fileLocation of input parameter: ${task_2.input.fileLocation} is not defined in workflow definition."));
        Assert.assertTrue(validationErrors.contains("taskReferenceName: task_2 for given task: task_1 input value: fileLocation of input parameter: ${task_2.input.fileLocation} is not defined in workflow definition."));
        Assert.assertTrue(validationErrors.contains("taskReferenceName: task_1 should be unique across tasks for a given workflowDefinition: sampleWorkflow"));
    }
}

