package com.netflix.conductor.validations;


import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.SubWorkflowParams;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.dao.MetadataDAO;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WorkflowTaskTypeConstraintTest {
    private static Validator validator;

    private MetadataDAO mockMetadataDao;

    private HibernateValidatorConfiguration config;

    @Test
    public void testWorkflowTaskMissingReferenceName() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setTaskReferenceName(null);
        Set<ConstraintViolation<Object>> result = WorkflowTaskTypeConstraintTest.validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.iterator().next().getMessage(), "WorkflowTask taskReferenceName name cannot be empty or null");
    }

    @Test
    public void testWorkflowTaskTestSetType() throws NoSuchMethodException {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        Method method = WorkflowTask.class.getMethod("setType", String.class);
        Object[] parameterValues = new Object[]{ "" };
        ExecutableValidator executableValidator = WorkflowTaskTypeConstraintTest.validator.forExecutables();
        Set<ConstraintViolation<Object>> result = executableValidator.validateParameters(workflowTask, method, parameterValues);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.iterator().next().getMessage(), "WorkTask type cannot be null or empty");
    }

    @Test
    public void testWorkflowTaskTypeEvent() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("EVENT");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.iterator().next().getMessage(), "sink field is required for taskType: EVENT taskName: encode");
    }

    @Test
    public void testWorkflowTaskTypeDynamic() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("DYNAMIC");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.iterator().next().getMessage(), "dynamicTaskNameParam field is required for taskType: DYNAMIC taskName: encode");
    }

    @Test
    public void testWorkflowTaskTypeDecision() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("DECISION");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(2, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("decisionCases should have atleast one task for taskType: DECISION taskName: encode"));
        Assert.assertTrue(validationErrors.contains("caseValueParam or caseExpression field is required for taskType: DECISION taskName: encode"));
    }

    @Test
    public void testWorkflowTaskTypeDecisionWithCaseParam() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("DECISION");
        workflowTask.setCaseExpression("$.valueCheck == null ? 'true': 'false'");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("decisionCases should have atleast one task for taskType: DECISION taskName: encode"));
    }

    @Test
    public void testWorkflowTaskTypeForJoinDynamic() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("FORK_JOIN_DYNAMIC");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(2, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("dynamicForkTasksInputParamName field is required for taskType: FORK_JOIN_DYNAMIC taskName: encode"));
        Assert.assertTrue(validationErrors.contains("dynamicForkTasksParam field is required for taskType: FORK_JOIN_DYNAMIC taskName: encode"));
    }

    @Test
    public void testWorkflowTaskTypeForJoinDynamicLegacy() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("FORK_JOIN_DYNAMIC");
        workflowTask.setDynamicForkJoinTasksParam("taskList");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testWorkflowTaskTypeForJoinDynamicWithForJoinTaskParam() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("FORK_JOIN_DYNAMIC");
        workflowTask.setDynamicForkJoinTasksParam("taskList");
        workflowTask.setDynamicForkTasksInputParamName("ForkTaskInputParam");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("dynamicForkJoinTasksParam or combination of dynamicForkTasksInputParamName and dynamicForkTasksParam cam be used for taskType: FORK_JOIN_DYNAMIC taskName: encode"));
    }

    @Test
    public void testWorkflowTaskTypeForJoinDynamicValid() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("FORK_JOIN_DYNAMIC");
        workflowTask.setDynamicForkTasksParam("ForkTasksParam");
        workflowTask.setDynamicForkTasksInputParamName("ForkTaskInputParam");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testWorkflowTaskTypeForJoinDynamicWithForJoinTaskParamAndInputTaskParam() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("FORK_JOIN_DYNAMIC");
        workflowTask.setDynamicForkJoinTasksParam("taskList");
        workflowTask.setDynamicForkTasksInputParamName("ForkTaskInputParam");
        workflowTask.setDynamicForkTasksParam("ForkTasksParam");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("dynamicForkJoinTasksParam or combination of dynamicForkTasksInputParamName and dynamicForkTasksParam cam be used for taskType: FORK_JOIN_DYNAMIC taskName: encode"));
    }

    @Test
    public void testWorkflowTaskTypeHTTP() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("HTTP");
        workflowTask.getInputParameters().put("http_request", "http://www.netflix.com");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testWorkflowTaskTypeHTTPWithHttpParamMissing() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("HTTP");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("inputParameters.http_request field is required for taskType: HTTP taskName: encode"));
    }

    @Test
    public void testWorkflowTaskTypeHTTPWithHttpParamInTaskDef() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("HTTP");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        TaskDef taskDef = new TaskDef();
        taskDef.setName("encode");
        taskDef.getInputTemplate().put("http_request", "http://www.netflix.com");
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(taskDef);
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testWorkflowTaskTypeHTTPWithHttpParamInTaskDefAndWorkflowTask() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("HTTP");
        workflowTask.getInputParameters().put("http_request", "http://www.netflix.com");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        TaskDef taskDef = new TaskDef();
        taskDef.setName("encode");
        taskDef.getInputTemplate().put("http_request", "http://www.netflix.com");
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(taskDef);
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testWorkflowTaskTypeFork() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("FORK_JOIN");
        ConstraintMapping mapping = config.createConstraintMapping();
        mapping.type(WorkflowTask.class).constraint(new WorkflowTaskTypeConstraintDef());
        Validator validator = config.addMapping(mapping).buildValidatorFactory().getValidator();
        Mockito.when(mockMetadataDao.getTaskDef(ArgumentMatchers.anyString())).thenReturn(new TaskDef());
        Set<ConstraintViolation<WorkflowTask>> result = validator.validate(workflowTask);
        Assert.assertEquals(1, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("forkTasks should have atleast one task for taskType: FORK_JOIN taskName: encode"));
    }

    @Test
    public void testWorkflowTaskTypeSubworkflow() {
        WorkflowTask workflowTask = createSampleWorkflowTask();
        workflowTask.setType("SUB_WORKFLOW");
        SubWorkflowParams subWorkflowTask = new SubWorkflowParams();
        workflowTask.setSubWorkflowParam(subWorkflowTask);
        Set<ConstraintViolation<WorkflowTask>> result = WorkflowTaskTypeConstraintTest.validator.validate(workflowTask);
        Assert.assertEquals(2, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("SubWorkflowParams name cannot be null"));
        Assert.assertTrue(validationErrors.contains("SubWorkflowParams name cannot be empty"));
    }
}

