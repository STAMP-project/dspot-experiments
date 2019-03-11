/**
 * Copyright 2016 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package com.netflix.conductor.common.tasks;


import Status.FAILED;
import TaskResult.Status;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public class TaskTest {
    @Test
    public void test() {
        Task task = new Task();
        task.setStatus(FAILED);
        Assert.assertEquals(FAILED, task.getStatus());
        Set<String> resultStatues = Arrays.asList(Status.values()).stream().map(( status) -> status.name()).collect(Collectors.toSet());
        for (com.netflix.conductor.common.metadata.tasks.Task.Status status : com.netflix.conductor.common.metadata.tasks.Task.Status.values()) {
            if (resultStatues.contains(status.name())) {
                TaskResult.Status trStatus = Status.valueOf(status.name());
                Assert.assertEquals(status.name(), trStatus.name());
                task = new Task();
                task.setStatus(status);
                Assert.assertEquals(status, task.getStatus());
            }
        }
    }

    @Test
    public void testTaskDefinitionIfAvailable() {
        Task task = new Task();
        task.setStatus(FAILED);
        Assert.assertEquals(FAILED, task.getStatus());
        Assert.assertNull(task.getWorkflowTask());
        Assert.assertFalse(task.getTaskDefinition().isPresent());
        WorkflowTask workflowTask = new WorkflowTask();
        TaskDef taskDefinition = new TaskDef();
        workflowTask.setTaskDefinition(taskDefinition);
        task.setWorkflowTask(workflowTask);
        Assert.assertTrue(task.getTaskDefinition().isPresent());
        Assert.assertEquals(taskDefinition, task.getTaskDefinition().get());
    }
}

