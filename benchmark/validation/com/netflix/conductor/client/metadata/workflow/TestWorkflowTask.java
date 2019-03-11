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
package com.netflix.conductor.client.metadata.workflow;


import TaskType.SUB_WORKFLOW;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import java.io.InputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public class TestWorkflowTask {
    private ObjectMapper objectMapper;

    @Test
    public void test() throws Exception {
        WorkflowTask task = new WorkflowTask();
        task.setType("Hello");
        task.setName("name");
        String json = objectMapper.writeValueAsString(task);
        WorkflowTask read = objectMapper.readValue(json, WorkflowTask.class);
        Assert.assertNotNull(read);
        Assert.assertEquals(task.getName(), read.getName());
        Assert.assertEquals(task.getType(), read.getType());
        task = new WorkflowTask();
        task.setWorkflowTaskType(SUB_WORKFLOW);
        task.setName("name");
        json = objectMapper.writeValueAsString(task);
        read = objectMapper.readValue(json, WorkflowTask.class);
        Assert.assertNotNull(read);
        Assert.assertEquals(task.getName(), read.getName());
        Assert.assertEquals(task.getType(), read.getType());
        Assert.assertEquals(SUB_WORKFLOW.name(), read.getType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObectMapper() throws Exception {
        try (InputStream stream = TestWorkflowTask.class.getResourceAsStream("/tasks.json")) {
            List<Task> tasks = objectMapper.readValue(stream, List.class);
            Assert.assertNotNull(tasks);
            Assert.assertEquals(1, tasks.size());
        }
    }
}

