/**
 * Copyright 2006-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.scope.context;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.jsr.configuration.support.BatchPropertyContext;
import org.springframework.batch.item.ExecutionContext;


/**
 *
 *
 * @author Dave Syer
 * @author Nicolas Widart
 * @author Mahmoud Ben Hassine
 */
public class StepContextTests {
    private List<String> list = new ArrayList<>();

    private StepExecution stepExecution = new StepExecution("step", new JobExecution(new JobInstance(2L, "job"), 0L, null, null), 1L);

    private StepContext context = new StepContext(stepExecution);

    private BatchPropertyContext propertyContext = new BatchPropertyContext();

    @Test
    public void testGetStepExecution() {
        context = new StepContext(stepExecution);
        Assert.assertNotNull(context.getStepExecution());
    }

    @Test
    public void testNullStepExecution() {
        try {
            context = new StepContext(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGetPartitionPlan() {
        Properties partitionPropertyValues = new Properties();
        partitionPropertyValues.put("key1", "value1");
        propertyContext.setStepProperties(stepExecution.getStepName(), partitionPropertyValues);
        context = new StepContext(stepExecution, propertyContext);
        Map<String, Object> plan = context.getPartitionPlan();
        Assert.assertEquals("value1", plan.get("key1"));
    }

    @Test
    public void testEqualsSelf() {
        Assert.assertEquals(context, context);
    }

    @Test
    public void testNotEqualsNull() {
        Assert.assertFalse(context.equals(null));
    }

    @Test
    public void testEqualsContextWithSameStepExecution() {
        Assert.assertEquals(new StepContext(stepExecution), context);
    }

    @Test
    public void testDestructionCallbackSunnyDay() throws Exception {
        context.setAttribute("foo", "FOO");
        context.registerDestructionCallback("foo", new Runnable() {
            @Override
            public void run() {
                list.add("bar");
            }
        });
        context.close();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("bar", list.get(0));
    }

    @Test
    public void testDestructionCallbackMissingAttribute() throws Exception {
        context.registerDestructionCallback("foo", new Runnable() {
            @Override
            public void run() {
                list.add("bar");
            }
        });
        context.close();
        // Yes the callback should be called even if the attribute is missing -
        // for inner beans
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testDestructionCallbackWithException() throws Exception {
        context.setAttribute("foo", "FOO");
        context.setAttribute("bar", "BAR");
        context.registerDestructionCallback("bar", new Runnable() {
            @Override
            public void run() {
                list.add("spam");
                throw new RuntimeException("fail!");
            }
        });
        context.registerDestructionCallback("foo", new Runnable() {
            @Override
            public void run() {
                list.add("bar");
                throw new RuntimeException("fail!");
            }
        });
        try {
            context.close();
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            // We don't care which one was thrown...
            Assert.assertEquals("fail!", e.getMessage());
        }
        // ...but we do care that both were executed:
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("bar"));
        Assert.assertTrue(list.contains("spam"));
    }

    @Test
    public void testStepName() throws Exception {
        Assert.assertEquals("step", context.getStepName());
    }

    @Test
    public void testJobName() throws Exception {
        Assert.assertEquals("job", context.getJobName());
    }

    @Test
    public void testJobInstanceId() throws Exception {
        Assert.assertEquals(2L, ((long) (context.getJobInstanceId())));
    }

    @Test
    public void testStepExecutionContext() throws Exception {
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        executionContext.put("foo", "bar");
        Assert.assertEquals("bar", context.getStepExecutionContext().get("foo"));
    }

    @Test
    public void testSystemProperties() throws Exception {
        System.setProperty("foo", "bar");
        Assert.assertEquals("bar", context.getSystemProperties().getProperty("foo"));
    }

    @Test
    public void testJobExecutionContext() throws Exception {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        executionContext.put("foo", "bar");
        Assert.assertEquals("bar", context.getJobExecutionContext().get("foo"));
    }

    @Test
    public void testJobParameters() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", "bar").toJobParameters();
        JobInstance instance = stepExecution.getJobExecution().getJobInstance();
        stepExecution = new StepExecution("step", new JobExecution(instance, jobParameters));
        context = new StepContext(stepExecution);
        Assert.assertEquals("bar", context.getJobParameters().get("foo"));
    }

    @Test
    public void testContextId() throws Exception {
        Assert.assertEquals("execution#1", context.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalContextId() throws Exception {
        context = new StepContext(new StepExecution("foo", new JobExecution(0L)));
        context.getId();
    }
}

