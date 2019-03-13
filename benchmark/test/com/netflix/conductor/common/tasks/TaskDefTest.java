/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
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


import com.netflix.conductor.common.metadata.tasks.TaskDef;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public class TaskDefTest {
    private Validator validator;

    @Test
    public void test() {
        String name = "test1";
        String description = "desc";
        int retryCount = 10;
        int timeout = 100;
        TaskDef def = new TaskDef(name, description, retryCount, timeout);
        Assert.assertEquals(3600, def.getResponseTimeoutSeconds());
        Assert.assertEquals(name, def.getName());
        Assert.assertEquals(description, def.getDescription());
        Assert.assertEquals(retryCount, def.getRetryCount());
        Assert.assertEquals(timeout, def.getTimeoutSeconds());
    }

    @Test
    public void testTaskDef() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("task1");
        taskDef.setRetryCount((-1));
        taskDef.setTimeoutSeconds(1000);
        taskDef.setResponseTimeoutSeconds(1001);
        Set<ConstraintViolation<Object>> result = validator.validate(taskDef);
        Assert.assertEquals(2, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("TaskDef: task1 responseTimeoutSeconds: 1001 must be less than timeoutSeconds: 1000"));
        Assert.assertTrue(validationErrors.contains("TaskDef retryCount: 0 must be >= 0"));
    }

    @Test
    public void testTaskDefNameNotSet() {
        TaskDef taskDef = new TaskDef();
        taskDef.setRetryCount((-1));
        taskDef.setTimeoutSeconds(1000);
        taskDef.setResponseTimeoutSeconds(1);
        Set<ConstraintViolation<Object>> result = validator.validate(taskDef);
        Assert.assertEquals(2, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("TaskDef retryCount: 0 must be >= 0"));
        Assert.assertTrue(validationErrors.contains("TaskDef name cannot be null or empty"));
    }
}

