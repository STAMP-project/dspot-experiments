package com.netflix.conductor.core.execution.mapper;


import TaskType.DECISION;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.DeciderService;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.utils.IDGenerator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class DecisionTaskMapperTest {
    private ParametersUtils parametersUtils;

    private DeciderService deciderService;

    // Subject
    private DecisionTaskMapper decisionTaskMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Map<String, Object> ip1;

    WorkflowTask task1;

    WorkflowTask task2;

    WorkflowTask task3;

    @Test
    public void getMappedTasks() {
        // Given
        // Task Definition
        TaskDef taskDef = new TaskDef();
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("Id", "${workflow.input.Id}");
        List<Map<String, Object>> taskDefinitionInput = new LinkedList<>();
        taskDefinitionInput.add(inputMap);
        // Decision task instance
        WorkflowTask decisionTask = new WorkflowTask();
        decisionTask.setType(DECISION.name());
        decisionTask.setName("Decision");
        decisionTask.setTaskReferenceName("decisionTask");
        decisionTask.setDefaultCase(Arrays.asList(task1));
        decisionTask.setCaseValueParam("case");
        decisionTask.getInputParameters().put("Id", "${workflow.input.Id}");
        decisionTask.setCaseExpression("if ($.Id == null) 'bad input'; else if ( ($.Id != null && $.Id % 2 == 0)) 'even'; else 'odd'; ");
        Map<String, List<WorkflowTask>> decisionCases = new HashMap<>();
        decisionCases.put("even", Arrays.asList(task2));
        decisionCases.put("odd", Arrays.asList(task3));
        decisionTask.setDecisionCases(decisionCases);
        // Workflow instance
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setSchemaVersion(2);
        Workflow workflowInstance = new Workflow();
        workflowInstance.setWorkflowDefinition(workflowDef);
        Map<String, Object> workflowInput = new HashMap<>();
        workflowInput.put("Id", "22");
        workflowInstance.setInput(workflowInput);
        Map<String, Object> body = new HashMap<>();
        body.put("input", taskDefinitionInput);
        taskDef.getInputTemplate().putAll(body);
        Map<String, Object> input = parametersUtils.getTaskInput(decisionTask.getInputParameters(), workflowInstance, null, null);
        Task theTask = new Task();
        theTask.setReferenceTaskName("Foo");
        theTask.setTaskId(IDGenerator.generate());
        Mockito.when(deciderService.getTasksToBeScheduled(workflowInstance, task2, 0, null)).thenReturn(Arrays.asList(theTask));
        TaskMapperContext taskMapperContext = TaskMapperContext.newBuilder().withWorkflowDefinition(workflowDef).withWorkflowInstance(workflowInstance).withTaskToSchedule(decisionTask).withTaskInput(input).withRetryCount(0).withTaskId(IDGenerator.generate()).withDeciderService(deciderService).build();
        // When
        List<Task> mappedTasks = decisionTaskMapper.getMappedTasks(taskMapperContext);
        // Then
        Assert.assertEquals(2, mappedTasks.size());
        Assert.assertEquals("decisionTask", mappedTasks.get(0).getReferenceTaskName());
        Assert.assertEquals("Foo", mappedTasks.get(1).getReferenceTaskName());
    }

    @Test
    public void getEvaluatedCaseValue() {
        WorkflowTask decisionTask = new WorkflowTask();
        decisionTask.setType(DECISION.name());
        decisionTask.setName("Decision");
        decisionTask.setTaskReferenceName("decisionTask");
        decisionTask.setInputParameters(ip1);
        decisionTask.setDefaultCase(Arrays.asList(task1));
        decisionTask.setCaseValueParam("case");
        Map<String, List<WorkflowTask>> decisionCases = new HashMap<>();
        decisionCases.put("0", Arrays.asList(task2));
        decisionCases.put("1", Arrays.asList(task3));
        decisionTask.setDecisionCases(decisionCases);
        Workflow workflowInstance = new Workflow();
        workflowInstance.setWorkflowDefinition(new WorkflowDef());
        Map<String, Object> workflowInput = new HashMap<>();
        workflowInput.put("param1", "test1");
        workflowInput.put("param2", "test2");
        workflowInput.put("case", "0");
        workflowInstance.setInput(workflowInput);
        Map<String, Object> input = parametersUtils.getTaskInput(decisionTask.getInputParameters(), workflowInstance, null, null);
        Assert.assertEquals("0", decisionTaskMapper.getEvaluatedCaseValue(decisionTask, input));
    }

    @Test
    public void getEvaluatedCaseValueUsingExpression() {
        // Given
        // Task Definition
        TaskDef taskDef = new TaskDef();
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("Id", "${workflow.input.Id}");
        List<Map<String, Object>> taskDefinitionInput = new LinkedList<>();
        taskDefinitionInput.add(inputMap);
        // Decision task instance
        WorkflowTask decisionTask = new WorkflowTask();
        decisionTask.setType(DECISION.name());
        decisionTask.setName("Decision");
        decisionTask.setTaskReferenceName("decisionTask");
        decisionTask.setDefaultCase(Arrays.asList(task1));
        decisionTask.setCaseValueParam("case");
        decisionTask.getInputParameters().put("Id", "${workflow.input.Id}");
        decisionTask.setCaseExpression("if ($.Id == null) 'bad input'; else if ( ($.Id != null && $.Id % 2 == 0)) 'even'; else 'odd'; ");
        Map<String, List<WorkflowTask>> decisionCases = new HashMap<>();
        decisionCases.put("even", Arrays.asList(task2));
        decisionCases.put("odd", Arrays.asList(task3));
        decisionTask.setDecisionCases(decisionCases);
        // Workflow instance
        WorkflowDef def = new WorkflowDef();
        def.setSchemaVersion(2);
        Workflow workflowInstance = new Workflow();
        workflowInstance.setWorkflowDefinition(def);
        Map<String, Object> workflowInput = new HashMap<>();
        workflowInput.put("Id", "22");
        workflowInstance.setInput(workflowInput);
        Map<String, Object> body = new HashMap<>();
        body.put("input", taskDefinitionInput);
        taskDef.getInputTemplate().putAll(body);
        Map<String, Object> evaluatorInput = parametersUtils.getTaskInput(decisionTask.getInputParameters(), workflowInstance, taskDef, null);
        Assert.assertEquals("even", decisionTaskMapper.getEvaluatedCaseValue(decisionTask, evaluatorInput));
    }

    @Test
    public void getEvaluatedCaseValueException() {
        // Given
        // Task Definition
        TaskDef taskDef = new TaskDef();
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("Id", "${workflow.input.Id}");
        List<Map<String, Object>> taskDefinitionInput = new LinkedList<>();
        taskDefinitionInput.add(inputMap);
        // Decision task instance
        WorkflowTask decisionTask = new WorkflowTask();
        decisionTask.setType(DECISION.name());
        decisionTask.setName("Decision");
        decisionTask.setTaskReferenceName("decisionTask");
        decisionTask.setDefaultCase(Arrays.asList(task1));
        decisionTask.setCaseValueParam("case");
        decisionTask.getInputParameters().put("Id", "${workflow.input.Id}");
        decisionTask.setCaseExpression("if ($Id == null) 'bad input'; else if ( ($Id != null && $Id % 2 == 0)) 'even'; else 'odd'; ");
        Map<String, List<WorkflowTask>> decisionCases = new HashMap<>();
        decisionCases.put("even", Arrays.asList(task2));
        decisionCases.put("odd", Arrays.asList(task3));
        decisionTask.setDecisionCases(decisionCases);
        // Workflow instance
        WorkflowDef def = new WorkflowDef();
        def.setSchemaVersion(2);
        Workflow workflowInstance = new Workflow();
        workflowInstance.setWorkflowDefinition(def);
        Map<String, Object> workflowInput = new HashMap<>();
        workflowInput.put(".Id", "22");
        workflowInstance.setInput(workflowInput);
        Map<String, Object> body = new HashMap<>();
        body.put("input", taskDefinitionInput);
        taskDef.getInputTemplate().putAll(body);
        Map<String, Object> evaluatorInput = parametersUtils.getTaskInput(decisionTask.getInputParameters(), workflowInstance, taskDef, null);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(("Error while evaluating the script " + (decisionTask.getCaseExpression())));
        decisionTaskMapper.getEvaluatedCaseValue(decisionTask, evaluatorInput);
    }
}

