/**
 * Copyright 2016 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 *
 */
package com.netflix.conductor.tests.integration;


import Status.COMPLETED;
import Status.SCHEDULED;
import TimeoutPolicy.RETRY;
import WorkflowStatus.RUNNING;
import WorkflowStatus.TERMINATED;
import com.netflix.conductor.client.grpc.MetadataClient;
import com.netflix.conductor.client.grpc.TaskClient;
import com.netflix.conductor.client.grpc.WorkflowClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.run.WorkflowSummary;
import com.netflix.conductor.elasticsearch.EmbeddedElasticSearch;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public abstract class AbstractGrpcEndToEndTest extends AbstractEndToEndTest {
    protected static TaskClient taskClient;

    protected static WorkflowClient workflowClient;

    protected static MetadataClient metadataClient;

    protected static EmbeddedElasticSearch search;

    @Test
    public void testAll() throws Exception {
        Assert.assertNotNull(AbstractGrpcEndToEndTest.taskClient);
        List<TaskDef> defs = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            TaskDef def = new TaskDef(("t" + i), ("task " + i));
            def.setTimeoutPolicy(RETRY);
            defs.add(def);
        }
        AbstractGrpcEndToEndTest.metadataClient.registerTaskDefs(defs);
        for (int i = 0; i < 5; i++) {
            final String taskName = "t" + i;
            TaskDef def = AbstractGrpcEndToEndTest.metadataClient.getTaskDef(taskName);
            Assert.assertNotNull(def);
            Assert.assertEquals(taskName, def.getName());
        }
        WorkflowDef def = createWorkflowDefinition("test");
        WorkflowTask t0 = createWorkflowTask("t0");
        WorkflowTask t1 = createWorkflowTask("t1");
        def.getTasks().add(t0);
        def.getTasks().add(t1);
        AbstractGrpcEndToEndTest.metadataClient.registerWorkflowDef(def);
        WorkflowDef found = AbstractGrpcEndToEndTest.metadataClient.getWorkflowDef(def.getName(), null);
        Assert.assertNotNull(found);
        Assert.assertEquals(def, found);
        String correlationId = "test_corr_id";
        StartWorkflowRequest startWf = new StartWorkflowRequest();
        startWf.setName(def.getName());
        startWf.setCorrelationId(correlationId);
        String workflowId = AbstractGrpcEndToEndTest.workflowClient.startWorkflow(startWf);
        Assert.assertNotNull(workflowId);
        System.out.println(("Started workflow id=" + workflowId));
        Workflow wf = AbstractGrpcEndToEndTest.workflowClient.getWorkflow(workflowId, false);
        Assert.assertEquals(0, wf.getTasks().size());
        Assert.assertEquals(workflowId, wf.getWorkflowId());
        wf = AbstractGrpcEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertEquals(1, wf.getTasks().size());
        Assert.assertEquals(t0.getTaskReferenceName(), wf.getTasks().get(0).getReferenceTaskName());
        Assert.assertEquals(workflowId, wf.getWorkflowId());
        List<String> runningIds = AbstractGrpcEndToEndTest.workflowClient.getRunningWorkflow(def.getName(), def.getVersion());
        Assert.assertNotNull(runningIds);
        Assert.assertEquals(1, runningIds.size());
        Assert.assertEquals(workflowId, runningIds.get(0));
        List<Task> polled = AbstractGrpcEndToEndTest.taskClient.batchPollTasksByTaskType("non existing task", "test", 1, 100);
        Assert.assertNotNull(polled);
        Assert.assertEquals(0, polled.size());
        polled = AbstractGrpcEndToEndTest.taskClient.batchPollTasksByTaskType(t0.getName(), "test", 1, 100);
        Assert.assertNotNull(polled);
        Assert.assertEquals(1, polled.size());
        Assert.assertEquals(t0.getName(), polled.get(0).getTaskDefName());
        Task task = polled.get(0);
        Boolean acked = AbstractGrpcEndToEndTest.taskClient.ack(task.getTaskId(), "test");
        Assert.assertNotNull(acked);
        Assert.assertTrue(acked);
        task.getOutputData().put("key1", "value1");
        task.setStatus(COMPLETED);
        AbstractGrpcEndToEndTest.taskClient.updateTask(new com.netflix.conductor.common.metadata.tasks.TaskResult(task));
        polled = AbstractGrpcEndToEndTest.taskClient.batchPollTasksByTaskType(t0.getName(), "test", 1, 100);
        Assert.assertNotNull(polled);
        Assert.assertTrue(polled.toString(), polled.isEmpty());
        wf = AbstractGrpcEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertEquals(2, wf.getTasks().size());
        Assert.assertEquals(t0.getTaskReferenceName(), wf.getTasks().get(0).getReferenceTaskName());
        Assert.assertEquals(t1.getTaskReferenceName(), wf.getTasks().get(1).getReferenceTaskName());
        Assert.assertEquals(COMPLETED, wf.getTasks().get(0).getStatus());
        Assert.assertEquals(SCHEDULED, wf.getTasks().get(1).getStatus());
        Task taskById = AbstractGrpcEndToEndTest.taskClient.getTaskDetails(task.getTaskId());
        Assert.assertNotNull(taskById);
        Assert.assertEquals(task.getTaskId(), taskById.getTaskId());
        List<Task> getTasks = AbstractGrpcEndToEndTest.taskClient.getPendingTasksByType(t0.getName(), null, 1);
        Assert.assertNotNull(getTasks);
        Assert.assertEquals(0, getTasks.size());// getTasks only gives pending tasks

        getTasks = AbstractGrpcEndToEndTest.taskClient.getPendingTasksByType(t1.getName(), null, 1);
        Assert.assertNotNull(getTasks);
        Assert.assertEquals(1, getTasks.size());
        Task pending = AbstractGrpcEndToEndTest.taskClient.getPendingTaskForWorkflow(workflowId, t1.getTaskReferenceName());
        Assert.assertNotNull(pending);
        Assert.assertEquals(t1.getTaskReferenceName(), pending.getReferenceTaskName());
        Assert.assertEquals(workflowId, pending.getWorkflowInstanceId());
        Thread.sleep(1000);
        SearchResult<WorkflowSummary> searchResult = AbstractGrpcEndToEndTest.workflowClient.search((("workflowType='" + (def.getName())) + "'"));
        Assert.assertNotNull(searchResult);
        Assert.assertEquals(1, searchResult.getTotalHits());
        AbstractGrpcEndToEndTest.workflowClient.terminateWorkflow(workflowId, "terminate reason");
        wf = AbstractGrpcEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(TERMINATED, wf.getStatus());
        AbstractGrpcEndToEndTest.workflowClient.restart(workflowId);
        wf = AbstractGrpcEndToEndTest.workflowClient.getWorkflow(workflowId, true);
        Assert.assertNotNull(wf);
        Assert.assertEquals(RUNNING, wf.getStatus());
        Assert.assertEquals(1, wf.getTasks().size());
    }
}

