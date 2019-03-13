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
package com.netflix.conductor.dao.dynomite;


import Status.IN_PROGRESS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.utils.JsonMapperProvider;
import com.netflix.conductor.dao.ExecutionDAOTest;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Viren
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisExecutionDAOTest extends ExecutionDAOTest {
    private RedisExecutionDAO executionDAO;

    private static ObjectMapper objectMapper = new JsonMapperProvider().get();

    @Test
    @SuppressWarnings("unchecked")
    public void testCorrelateTaskToWorkflowInDS() {
        String workflowId = "workflowId";
        String taskId = "taskId1";
        String taskDefName = "task1";
        TaskDef def = new TaskDef();
        def.setName("task1");
        def.setConcurrentExecLimit(1);
        Task task = new Task();
        task.setTaskId(taskId);
        task.setWorkflowInstanceId(workflowId);
        task.setReferenceTaskName("ref_name");
        task.setTaskDefName(taskDefName);
        task.setTaskType(taskDefName);
        task.setStatus(IN_PROGRESS);
        List<Task> tasks = executionDAO.createTasks(Collections.singletonList(task));
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        executionDAO.correlateTaskToWorkflowInDS(taskId, workflowId);
        tasks = executionDAO.getTasksForWorkflow(workflowId);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(workflowId, tasks.get(0).getWorkflowInstanceId());
        Assert.assertEquals(taskId, tasks.get(0).getTaskId());
    }

    @Test
    public void testExceedsRateLimitWhenNoRateLimitSet() {
        Task task = new Task();
        Assert.assertFalse(executionDAO.exceedsRateLimitPerFrequency(task));
    }

    @Test
    public void testExceedsRateLimitWithinLimit() {
        Task task = new Task();
        task.setRateLimitFrequencyInSeconds(60);
        task.setRateLimitPerFrequency(20);
        Assert.assertFalse(executionDAO.exceedsRateLimitPerFrequency(task));
    }

    @Test
    public void testExceedsRateLimitOutOfLimit() {
        Task task = new Task();
        task.setRateLimitFrequencyInSeconds(60);
        task.setRateLimitPerFrequency(1);
        Assert.assertFalse(executionDAO.exceedsRateLimitPerFrequency(task));
        Assert.assertTrue(executionDAO.exceedsRateLimitPerFrequency(task));
    }
}

