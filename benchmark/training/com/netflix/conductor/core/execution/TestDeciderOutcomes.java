/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 *
 */
package com.netflix.conductor.core.execution;


import Status.FAILED;
import SystemTaskType.FORK;
import SystemTaskType.JOIN;
import Task.Status.COMPLETED;
import Task.Status.COMPLETED_WITH_ERRORS;
import Task.Status.IN_PROGRESS;
import Task.Status.SCHEDULED;
import TaskType.DECISION;
import TaskType.FORK_JOIN_DYNAMIC;
import TaskType.SIMPLE;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.DeciderService.DeciderOutcome;
import com.netflix.conductor.core.execution.tasks.Join;
import com.netflix.conductor.dao.MetadataDAO;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


/**
 *
 *
 * @author Viren
 */
public class TestDeciderOutcomes {
    private MetadataDAO metadataDAO;

    private DeciderService deciderService;

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        TestDeciderOutcomes.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TestDeciderOutcomes.objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        TestDeciderOutcomes.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        TestDeciderOutcomes.objectMapper.setSerializationInclusion(NON_NULL);
        TestDeciderOutcomes.objectMapper.setSerializationInclusion(NON_EMPTY);
    }

    @Test
    public void testWorkflowWithNoTasks() throws Exception {
        InputStream stream = TestDeciderOutcomes.class.getResourceAsStream("/conditional_flow.json");
        WorkflowDef def = TestDeciderOutcomes.objectMapper.readValue(stream, WorkflowDef.class);
        Assert.assertNotNull(def);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.setStartTime(0);
        workflow.getInput().put("param1", "nested");
        workflow.getInput().put("param2", "one");
        DeciderOutcome outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        Assert.assertFalse(outcome.isComplete);
        Assert.assertTrue(outcome.tasksToBeUpdated.isEmpty());
        Assert.assertEquals(3, outcome.tasksToBeScheduled.size());
        System.out.println(outcome.tasksToBeScheduled);
        outcome.tasksToBeScheduled.forEach(( t) -> t.setStatus(Status.COMPLETED));
        workflow.getTasks().addAll(outcome.tasksToBeScheduled);
        outcome = deciderService.decide(workflow);
        Assert.assertFalse(outcome.isComplete);
        Assert.assertEquals(outcome.tasksToBeUpdated.toString(), 3, outcome.tasksToBeUpdated.size());
        Assert.assertEquals(1, outcome.tasksToBeScheduled.size());
        Assert.assertEquals("junit_task_3", outcome.tasksToBeScheduled.get(0).getTaskDefName());
        System.out.println(outcome.tasksToBeScheduled);
    }

    @Test
    public void testRetries() {
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("test_task");
        workflowTask.setType("USER_TASK");
        workflowTask.setTaskReferenceName("t0");
        workflowTask.getInputParameters().put("taskId", "${CPEWF_TASK_ID}");
        workflowTask.getInputParameters().put("requestId", "${workflow.input.requestId}");
        workflowTask.setTaskDefinition(new TaskDef("test_task"));
        def.getTasks().add(workflowTask);
        def.setSchemaVersion(2);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.getInput().put("requestId", 123);
        workflow.setStartTime(System.currentTimeMillis());
        DeciderOutcome outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        Assert.assertEquals(1, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(workflowTask.getTaskReferenceName(), outcome.tasksToBeScheduled.get(0).getReferenceTaskName());
        String task1Id = outcome.tasksToBeScheduled.get(0).getTaskId();
        Assert.assertEquals(task1Id, outcome.tasksToBeScheduled.get(0).getInputData().get("taskId"));
        Assert.assertEquals(123, outcome.tasksToBeScheduled.get(0).getInputData().get("requestId"));
        outcome.tasksToBeScheduled.get(0).setStatus(FAILED);
        workflow.getTasks().addAll(outcome.tasksToBeScheduled);
        outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        Assert.assertEquals(1, outcome.tasksToBeUpdated.size());
        Assert.assertEquals(1, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(task1Id, outcome.tasksToBeUpdated.get(0).getTaskId());
        Assert.assertNotSame(task1Id, outcome.tasksToBeScheduled.get(0).getTaskId());
        Assert.assertEquals(outcome.tasksToBeScheduled.get(0).getTaskId(), outcome.tasksToBeScheduled.get(0).getInputData().get("taskId"));
        Assert.assertEquals(task1Id, outcome.tasksToBeScheduled.get(0).getRetriedTaskId());
        Assert.assertEquals(123, outcome.tasksToBeScheduled.get(0).getInputData().get("requestId"));
        WorkflowTask fork = new WorkflowTask();
        fork.setName("fork0");
        fork.setWorkflowTaskType(FORK_JOIN_DYNAMIC);
        fork.setTaskReferenceName("fork0");
        fork.setDynamicForkTasksInputParamName("forkedInputs");
        fork.setDynamicForkTasksParam("forks");
        fork.getInputParameters().put("forks", "${workflow.input.forks}");
        fork.getInputParameters().put("forkedInputs", "${workflow.input.forkedInputs}");
        WorkflowTask join = new WorkflowTask();
        join.setName("join0");
        join.setType("JOIN");
        join.setTaskReferenceName("join0");
        def.getTasks().clear();
        def.getTasks().add(fork);
        def.getTasks().add(join);
        List<WorkflowTask> forks = new LinkedList<>();
        Map<String, Map<String, Object>> forkedInputs = new HashMap<>();
        for (int i = 0; i < 1; i++) {
            WorkflowTask wft = new WorkflowTask();
            wft.setName(("f" + i));
            wft.setTaskReferenceName(("f" + i));
            wft.setWorkflowTaskType(SIMPLE);
            wft.getInputParameters().put("requestId", "${workflow.input.requestId}");
            wft.getInputParameters().put("taskId", "${CPEWF_TASK_ID}");
            wft.setTaskDefinition(new TaskDef(("f" + i)));
            forks.add(wft);
            Map<String, Object> input = new HashMap<>();
            input.put("k", "v");
            input.put("k1", 1);
            forkedInputs.put(wft.getTaskReferenceName(), input);
        }
        workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.getInput().put("requestId", 123);
        workflow.setStartTime(System.currentTimeMillis());
        workflow.getInput().put("forks", forks);
        workflow.getInput().put("forkedInputs", forkedInputs);
        outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        Assert.assertEquals(3, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(0, outcome.tasksToBeUpdated.size());
        Assert.assertEquals("v", outcome.tasksToBeScheduled.get(1).getInputData().get("k"));
        Assert.assertEquals(1, outcome.tasksToBeScheduled.get(1).getInputData().get("k1"));
        Assert.assertEquals(outcome.tasksToBeScheduled.get(1).getTaskId(), outcome.tasksToBeScheduled.get(1).getInputData().get("taskId"));
        System.out.println(outcome.tasksToBeScheduled.get(1).getInputData());
        task1Id = outcome.tasksToBeScheduled.get(1).getTaskId();
        outcome.tasksToBeScheduled.get(1).setStatus(FAILED);
        for (Task taskToBeScheduled : outcome.tasksToBeScheduled) {
            taskToBeScheduled.setUpdateTime(System.currentTimeMillis());
        }
        workflow.getTasks().addAll(outcome.tasksToBeScheduled);
        outcome = deciderService.decide(workflow);
        Assert.assertTrue(outcome.tasksToBeScheduled.stream().anyMatch(( task1) -> task1.getReferenceTaskName().equals("f0")));
        // noinspection ConstantConditions
        Task task1 = outcome.tasksToBeScheduled.stream().filter(( t) -> t.getReferenceTaskName().equals("f0")).findFirst().get();
        Assert.assertEquals("v", task1.getInputData().get("k"));
        Assert.assertEquals(1, task1.getInputData().get("k1"));
        Assert.assertEquals(task1.getTaskId(), task1.getInputData().get("taskId"));
        Assert.assertNotSame(task1Id, task1.getTaskId());
        Assert.assertEquals(task1Id, task1.getRetriedTaskId());
        System.out.println(task1.getInputData());
    }

    @Test
    public void testOptional() {
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        WorkflowTask task1 = new WorkflowTask();
        task1.setName("task0");
        task1.setType("SIMPLE");
        task1.setTaskReferenceName("t0");
        task1.getInputParameters().put("taskId", "${CPEWF_TASK_ID}");
        task1.setOptional(true);
        task1.setTaskDefinition(new TaskDef("task0"));
        WorkflowTask task2 = new WorkflowTask();
        task2.setName("task1");
        task2.setType("SIMPLE");
        task2.setTaskReferenceName("t1");
        task2.setTaskDefinition(new TaskDef("task1"));
        def.getTasks().add(task1);
        def.getTasks().add(task2);
        def.setSchemaVersion(2);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.setStartTime(System.currentTimeMillis());
        DeciderOutcome outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        System.out.println(("Schedule after starting: " + (outcome.tasksToBeScheduled)));
        Assert.assertEquals(1, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(task1.getTaskReferenceName(), outcome.tasksToBeScheduled.get(0).getReferenceTaskName());
        System.out.println(("TaskId of the scheduled task in input: " + (outcome.tasksToBeScheduled.get(0).getInputData())));
        String task1Id = outcome.tasksToBeScheduled.get(0).getTaskId();
        Assert.assertEquals(task1Id, outcome.tasksToBeScheduled.get(0).getInputData().get("taskId"));
        workflow.getTasks().addAll(outcome.tasksToBeScheduled);
        workflow.getTasks().get(0).setStatus(FAILED);
        outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        System.out.println(("Schedule: " + (outcome.tasksToBeScheduled)));
        System.out.println(("Update: " + (outcome.tasksToBeUpdated)));
        Assert.assertEquals(1, outcome.tasksToBeUpdated.size());
        Assert.assertEquals(1, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(COMPLETED_WITH_ERRORS, workflow.getTasks().get(0).getStatus());
        Assert.assertEquals(task1Id, outcome.tasksToBeUpdated.get(0).getTaskId());
        Assert.assertEquals(task2.getTaskReferenceName(), outcome.tasksToBeScheduled.get(0).getReferenceTaskName());
    }

    @Test
    public void testOptionalWithDynamicFork() {
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        WorkflowTask task1 = new WorkflowTask();
        task1.setName("fork0");
        task1.setWorkflowTaskType(FORK_JOIN_DYNAMIC);
        task1.setTaskReferenceName("fork0");
        task1.setDynamicForkTasksInputParamName("forkedInputs");
        task1.setDynamicForkTasksParam("forks");
        task1.getInputParameters().put("forks", "${workflow.input.forks}");
        task1.getInputParameters().put("forkedInputs", "${workflow.input.forkedInputs}");
        WorkflowTask task2 = new WorkflowTask();
        task2.setName("join0");
        task2.setType("JOIN");
        task2.setTaskReferenceName("join0");
        def.getTasks().add(task1);
        def.getTasks().add(task2);
        def.setSchemaVersion(2);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        List<WorkflowTask> forks = new LinkedList<>();
        Map<String, Map<String, Object>> forkedInputs = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            WorkflowTask workflowTask = new WorkflowTask();
            workflowTask.setName(("f" + i));
            workflowTask.setTaskReferenceName(("f" + i));
            workflowTask.setWorkflowTaskType(SIMPLE);
            workflowTask.setOptional(true);
            workflowTask.setTaskDefinition(new TaskDef(("f" + i)));
            forks.add(workflowTask);
            forkedInputs.put(workflowTask.getTaskReferenceName(), new HashMap());
        }
        workflow.getInput().put("forks", forks);
        workflow.getInput().put("forkedInputs", forkedInputs);
        workflow.setStartTime(System.currentTimeMillis());
        DeciderOutcome outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        Assert.assertEquals(5, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(0, outcome.tasksToBeUpdated.size());
        Assert.assertEquals(FORK.name(), outcome.tasksToBeScheduled.get(0).getTaskType());
        Assert.assertEquals(COMPLETED, outcome.tasksToBeScheduled.get(0).getStatus());
        for (int i = 1; i < 4; i++) {
            Assert.assertEquals(SCHEDULED, outcome.tasksToBeScheduled.get(i).getStatus());
            Assert.assertEquals(("f" + (i - 1)), outcome.tasksToBeScheduled.get(i).getTaskDefName());
            outcome.tasksToBeScheduled.get(i).setStatus(FAILED);// let's mark them as failure

        }
        Assert.assertEquals(IN_PROGRESS, outcome.tasksToBeScheduled.get(4).getStatus());
        workflow.getTasks().clear();
        workflow.getTasks().addAll(outcome.tasksToBeScheduled);
        for (Task taskToBeScheduled : outcome.tasksToBeScheduled) {
            taskToBeScheduled.setUpdateTime(System.currentTimeMillis());
        }
        outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        Assert.assertEquals(JOIN.name(), outcome.tasksToBeScheduled.get(0).getTaskType());
        for (int i = 1; i < 4; i++) {
            Assert.assertEquals(COMPLETED_WITH_ERRORS, outcome.tasksToBeUpdated.get(i).getStatus());
            Assert.assertEquals(("f" + (i - 1)), outcome.tasksToBeUpdated.get(i).getTaskDefName());
        }
        Assert.assertEquals(IN_PROGRESS, outcome.tasksToBeScheduled.get(0).getStatus());
        new Join().execute(workflow, outcome.tasksToBeScheduled.get(0), null);
        Assert.assertEquals(COMPLETED, outcome.tasksToBeScheduled.get(0).getStatus());
        outcome.tasksToBeScheduled.stream().map(( task) -> (((task.getStatus()) + ":") + (task.getTaskType())) + ":").forEach(System.out::println);
        outcome.tasksToBeUpdated.stream().map(( task) -> (((task.getStatus()) + ":") + (task.getTaskType())) + ":").forEach(System.out::println);
    }

    @Test
    public void testDecisionCases() {
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        WorkflowTask even = new WorkflowTask();
        even.setName("even");
        even.setType("SIMPLE");
        even.setTaskReferenceName("even");
        even.setTaskDefinition(new TaskDef("even"));
        WorkflowTask odd = new WorkflowTask();
        odd.setName("odd");
        odd.setType("SIMPLE");
        odd.setTaskReferenceName("odd");
        odd.setTaskDefinition(new TaskDef("odd"));
        WorkflowTask defaultt = new WorkflowTask();
        defaultt.setName("defaultt");
        defaultt.setType("SIMPLE");
        defaultt.setTaskReferenceName("defaultt");
        defaultt.setTaskDefinition(new TaskDef("defaultt"));
        WorkflowTask decide = new WorkflowTask();
        decide.setName("decide");
        decide.setWorkflowTaskType(DECISION);
        decide.setTaskReferenceName("d0");
        decide.getInputParameters().put("Id", "${workflow.input.Id}");
        decide.getInputParameters().put("location", "${workflow.input.location}");
        decide.setCaseExpression("if ($.Id == null) 'bad input'; else if ( ($.Id != null && $.Id % 2 == 0) || $.location == 'usa') 'even'; else 'odd'; ");
        decide.getDecisionCases().put("even", Arrays.asList(even));
        decide.getDecisionCases().put("odd", Arrays.asList(odd));
        decide.setDefaultCase(Arrays.asList(defaultt));
        def.getTasks().add(decide);
        def.setSchemaVersion(2);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.setStartTime(System.currentTimeMillis());
        DeciderOutcome outcome = deciderService.decide(workflow);
        Assert.assertNotNull(outcome);
        System.out.println(("Schedule after starting: " + (outcome.tasksToBeScheduled)));
        Assert.assertEquals(2, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(decide.getTaskReferenceName(), outcome.tasksToBeScheduled.get(0).getReferenceTaskName());
        Assert.assertEquals(defaultt.getTaskReferenceName(), outcome.tasksToBeScheduled.get(1).getReferenceTaskName());// default

        System.out.println(outcome.tasksToBeScheduled.get(0).getOutputData().get("caseOutput"));
        Assert.assertEquals(Arrays.asList("bad input"), outcome.tasksToBeScheduled.get(0).getOutputData().get("caseOutput"));
        workflow.getInput().put("Id", 9);
        workflow.getInput().put("location", "usa");
        outcome = deciderService.decide(workflow);
        Assert.assertEquals(2, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(decide.getTaskReferenceName(), outcome.tasksToBeScheduled.get(0).getReferenceTaskName());
        Assert.assertEquals(even.getTaskReferenceName(), outcome.tasksToBeScheduled.get(1).getReferenceTaskName());// even because of location == usa

        Assert.assertEquals(Arrays.asList("even"), outcome.tasksToBeScheduled.get(0).getOutputData().get("caseOutput"));
        workflow.getInput().put("Id", 9);
        workflow.getInput().put("location", "canada");
        outcome = deciderService.decide(workflow);
        Assert.assertEquals(2, outcome.tasksToBeScheduled.size());
        Assert.assertEquals(decide.getTaskReferenceName(), outcome.tasksToBeScheduled.get(0).getReferenceTaskName());
        Assert.assertEquals(odd.getTaskReferenceName(), outcome.tasksToBeScheduled.get(1).getReferenceTaskName());
        // odd
        Assert.assertEquals(Arrays.asList("odd"), outcome.tasksToBeScheduled.get(0).getOutputData().get("caseOutput"));
    }
}

