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
package com.netflix.conductor.dao;


import Task.Status.COMPLETED;
import Task.Status.IN_PROGRESS;
import Task.Status.SCHEDULED;
import Workflow.WorkflowStatus.RUNNING;
import com.netflix.conductor.common.metadata.tasks.PollData;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.ApplicationException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public abstract class ExecutionDAOTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testTaskExceedsLimit() {
        TaskDef taskDefinition = new TaskDef();
        taskDefinition.setName("task1");
        taskDefinition.setConcurrentExecLimit(1);
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setName("task1");
        workflowTask.setTaskDefinition(taskDefinition);
        workflowTask.setTaskDefinition(taskDefinition);
        List<Task> tasks = new LinkedList<>();
        for (int i = 0; i < 15; i++) {
            Task task = new Task();
            task.setScheduledTime(1L);
            task.setSeq((i + 1));
            task.setTaskId(("t_" + i));
            task.setWorkflowInstanceId(("workflow_" + i));
            task.setReferenceTaskName("task1");
            task.setTaskDefName("task1");
            tasks.add(task);
            task.setStatus(SCHEDULED);
            task.setWorkflowTask(workflowTask);
        }
        getExecutionDAO().createTasks(tasks);
        Assert.assertFalse(getExecutionDAO().exceedsInProgressLimit(tasks.get(0)));
        tasks.get(0).setStatus(IN_PROGRESS);
        getExecutionDAO().updateTask(tasks.get(0));
        for (Task task : tasks) {
            Assert.assertTrue(getExecutionDAO().exceedsInProgressLimit(task));
        }
    }

    @Test
    public void testCreateTaskException() {
        Task task = new Task();
        task.setScheduledTime(1L);
        task.setSeq(1);
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskDefName("task1");
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage("Workflow instance id cannot be null");
        getExecutionDAO().createTasks(Collections.singletonList(task));
        task.setWorkflowInstanceId(UUID.randomUUID().toString());
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage("Task reference name cannot be null");
        getExecutionDAO().createTasks(Collections.singletonList(task));
    }

    @Test
    public void testCreateTaskException2() {
        Task task = new Task();
        task.setScheduledTime(1L);
        task.setSeq(1);
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskDefName("task1");
        task.setWorkflowInstanceId(UUID.randomUUID().toString());
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage("Task reference name cannot be null");
        getExecutionDAO().createTasks(Collections.singletonList(task));
    }

    @Test
    public void testPollData() {
        getExecutionDAO().updateLastPoll("taskDef", null, "workerId1");
        PollData pd = getExecutionDAO().getPollData("taskDef", null);
        Assert.assertNotNull(pd);
        Assert.assertTrue(((pd.getLastPollTime()) > 0));
        Assert.assertEquals(pd.getQueueName(), "taskDef");
        Assert.assertNull(pd.getDomain());
        Assert.assertEquals(pd.getWorkerId(), "workerId1");
        getExecutionDAO().updateLastPoll("taskDef", "domain1", "workerId1");
        pd = getExecutionDAO().getPollData("taskDef", "domain1");
        Assert.assertNotNull(pd);
        Assert.assertTrue(((pd.getLastPollTime()) > 0));
        Assert.assertEquals(pd.getQueueName(), "taskDef");
        Assert.assertEquals(pd.getDomain(), "domain1");
        Assert.assertEquals(pd.getWorkerId(), "workerId1");
        List<PollData> pData = getExecutionDAO().getPollData("taskDef");
        Assert.assertEquals(pData.size(), 2);
        pd = getExecutionDAO().getPollData("taskDef", "domain2");
        Assert.assertNull(pd);
    }

    @Test
    public void testTaskCreateDups() {
        List<Task> tasks = new LinkedList<>();
        String workflowId = UUID.randomUUID().toString();
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setScheduledTime(1L);
            task.setSeq((i + 1));
            task.setTaskId(((workflowId + "_t") + i));
            task.setReferenceTaskName(("t" + i));
            task.setRetryCount(0);
            task.setWorkflowInstanceId(workflowId);
            task.setTaskDefName(("task" + i));
            task.setStatus(IN_PROGRESS);
            tasks.add(task);
        }
        // Let's insert a retried task
        Task task = new Task();
        task.setScheduledTime(1L);
        task.setSeq(1);
        task.setTaskId(((workflowId + "_t") + 2));
        task.setReferenceTaskName(("t" + 2));
        task.setRetryCount(1);
        task.setWorkflowInstanceId(workflowId);
        task.setTaskDefName(("task" + 2));
        task.setStatus(IN_PROGRESS);
        tasks.add(task);
        // Duplicate task!
        task = new Task();
        task.setScheduledTime(1L);
        task.setSeq(1);
        task.setTaskId(((workflowId + "_t") + 1));
        task.setReferenceTaskName(("t" + 1));
        task.setRetryCount(0);
        task.setWorkflowInstanceId(workflowId);
        task.setTaskDefName(("task" + 1));
        task.setStatus(IN_PROGRESS);
        tasks.add(task);
        List<Task> created = getExecutionDAO().createTasks(tasks);
        Assert.assertEquals(((tasks.size()) - 1), created.size());// 1 less

        Set<String> srcIds = tasks.stream().map(( t) -> ((t.getReferenceTaskName()) + ".") + (t.getRetryCount())).collect(Collectors.toSet());
        Set<String> createdIds = created.stream().map(( t) -> ((t.getReferenceTaskName()) + ".") + (t.getRetryCount())).collect(Collectors.toSet());
        Assert.assertEquals(srcIds, createdIds);
        List<Task> pending = getExecutionDAO().getPendingTasksByWorkflow("task0", workflowId);
        Assert.assertNotNull(pending);
        Assert.assertEquals(1, pending.size());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(tasks.get(0), pending.get(0)));
        List<Task> found = getExecutionDAO().getTasks(tasks.get(0).getTaskDefName(), null, 1);
        Assert.assertNotNull(found);
        Assert.assertEquals(1, found.size());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(tasks.get(0), found.get(0)));
    }

    @Test
    public void testTaskOps() {
        List<Task> tasks = new LinkedList<>();
        String workflowId = UUID.randomUUID().toString();
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setScheduledTime(1L);
            task.setSeq(1);
            task.setTaskId(((workflowId + "_t") + i));
            task.setReferenceTaskName(("testTaskOps" + i));
            task.setRetryCount(0);
            task.setWorkflowInstanceId(workflowId);
            task.setTaskDefName(("testTaskOps" + i));
            task.setStatus(IN_PROGRESS);
            tasks.add(task);
        }
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setScheduledTime(1L);
            task.setSeq(1);
            task.setTaskId(((("x" + workflowId) + "_t") + i));
            task.setReferenceTaskName(("testTaskOps" + i));
            task.setRetryCount(0);
            task.setWorkflowInstanceId(("x" + workflowId));
            task.setTaskDefName(("testTaskOps" + i));
            task.setStatus(IN_PROGRESS);
            getExecutionDAO().createTasks(Collections.singletonList(task));
        }
        List<Task> created = getExecutionDAO().createTasks(tasks);
        Assert.assertEquals(tasks.size(), created.size());
        List<Task> pending = getExecutionDAO().getPendingTasksForTaskType(tasks.get(0).getTaskDefName());
        Assert.assertNotNull(pending);
        Assert.assertEquals(2, pending.size());
        // Pending list can come in any order.  finding the one we are looking for and then comparing
        Task matching = pending.stream().filter(( task) -> task.getTaskId().equals(tasks.get(0).getTaskId())).findAny().get();
        Assert.assertTrue(EqualsBuilder.reflectionEquals(matching, tasks.get(0)));
        List<Task> update = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            Task found = getExecutionDAO().getTask(((workflowId + "_t") + i));
            Assert.assertNotNull(found);
            found.getOutputData().put("updated", true);
            found.setStatus(COMPLETED);
            update.add(found);
        }
        getExecutionDAO().updateTasks(update);
        List<String> taskIds = tasks.stream().map(Task::getTaskId).collect(Collectors.toList());
        List<Task> found = getExecutionDAO().getTasks(taskIds);
        Assert.assertEquals(taskIds.size(), found.size());
        found.forEach(( task) -> {
            assertTrue(task.getOutputData().containsKey("updated"));
            assertEquals(true, task.getOutputData().get("updated"));
            boolean removed = getExecutionDAO().removeTask(task.getTaskId());
            assertTrue(removed);
        });
        found = getExecutionDAO().getTasks(taskIds);
        Assert.assertTrue(found.isEmpty());
    }

    @Test
    public void testPending() {
        WorkflowDef def = new WorkflowDef();
        def.setName("pending_count_test");
        Workflow workflow = createTestWorkflow();
        workflow.setWorkflowDefinition(def);
        List<String> workflowIds = generateWorkflows(workflow, 10);
        long count = getExecutionDAO().getPendingWorkflowCount(def.getName());
        Assert.assertEquals(10, count);
        for (int i = 0; i < 10; i++) {
            getExecutionDAO().removeFromPendingWorkflow(def.getName(), workflowIds.get(i));
        }
        count = getExecutionDAO().getPendingWorkflowCount(def.getName());
        Assert.assertEquals(0, count);
    }

    @Test
    public void complexExecutionTest() {
        Workflow workflow = createTestWorkflow();
        int numTasks = workflow.getTasks().size();
        String workflowId = getExecutionDAO().createWorkflow(workflow);
        Assert.assertEquals(workflow.getWorkflowId(), workflowId);
        List<Task> created = getExecutionDAO().createTasks(workflow.getTasks());
        Assert.assertEquals(workflow.getTasks().size(), created.size());
        Workflow workflowWithTasks = getExecutionDAO().getWorkflow(workflow.getWorkflowId(), true);
        Assert.assertEquals(workflowId, workflowWithTasks.getWorkflowId());
        Assert.assertEquals(numTasks, workflowWithTasks.getTasks().size());
        Workflow found = getExecutionDAO().getWorkflow(workflowId, false);
        Assert.assertTrue(found.getTasks().isEmpty());
        workflow.getTasks().clear();
        Assert.assertEquals(workflow, found);
        workflow.getInput().put("updated", true);
        getExecutionDAO().updateWorkflow(workflow);
        found = getExecutionDAO().getWorkflow(workflowId);
        Assert.assertNotNull(found);
        Assert.assertTrue(found.getInput().containsKey("updated"));
        Assert.assertEquals(true, found.getInput().get("updated"));
        List<String> running = getExecutionDAO().getRunningWorkflowIds(workflow.getWorkflowName());
        Assert.assertNotNull(running);
        Assert.assertTrue(running.isEmpty());
        workflow.setStatus(RUNNING);
        getExecutionDAO().updateWorkflow(workflow);
        running = getExecutionDAO().getRunningWorkflowIds(workflow.getWorkflowName());
        Assert.assertNotNull(running);
        Assert.assertEquals(1, running.size());
        Assert.assertEquals(workflow.getWorkflowId(), running.get(0));
        List<Workflow> pending = getExecutionDAO().getPendingWorkflowsByType(workflow.getWorkflowName());
        Assert.assertNotNull(pending);
        Assert.assertEquals(1, pending.size());
        Assert.assertEquals(3, pending.get(0).getTasks().size());
        pending.get(0).getTasks().clear();
        Assert.assertEquals(workflow, pending.get(0));
        workflow.setStatus(Workflow.WorkflowStatus.COMPLETED);
        getExecutionDAO().updateWorkflow(workflow);
        running = getExecutionDAO().getRunningWorkflowIds(workflow.getWorkflowName());
        Assert.assertNotNull(running);
        Assert.assertTrue(running.isEmpty());
        List<Workflow> bytime = getExecutionDAO().getWorkflowsByType(workflow.getWorkflowName(), System.currentTimeMillis(), ((System.currentTimeMillis()) + 100));
        Assert.assertNotNull(bytime);
        Assert.assertTrue(bytime.isEmpty());
        bytime = getExecutionDAO().getWorkflowsByType(workflow.getWorkflowName(), ((workflow.getCreateTime()) - 10), ((workflow.getCreateTime()) + 10));
        Assert.assertNotNull(bytime);
        Assert.assertEquals(1, bytime.size());
    }
}

