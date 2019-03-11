/**
 * Copyright 2006-2007 the original author or authors.
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


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ExecutionContext;


/**
 *
 *
 * @author Dave Syer
 * @author Jimmy Praet
 */
public class JobContextTests {
    private List<String> list;

    private JobExecution jobExecution;

    private JobContext context;

    @Test
    public void testGetJobExecution() {
        context = new JobContext(jobExecution);
        Assert.assertNotNull(context.getJobExecution());
    }

    @Test
    public void testNullJobExecution() {
        try {
            context = new JobContext(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
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
    public void testEqualsContextWithSameJobExecution() {
        Assert.assertEquals(new JobContext(jobExecution), context);
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
    public void testJobName() throws Exception {
        Assert.assertEquals("job", context.getJobName());
    }

    @Test
    public void testJobExecutionContext() throws Exception {
        ExecutionContext executionContext = jobExecution.getExecutionContext();
        executionContext.put("foo", "bar");
        Assert.assertEquals("bar", context.getJobExecutionContext().get("foo"));
    }

    @Test
    public void testSystemProperties() throws Exception {
        System.setProperty("foo", "bar");
        Assert.assertEquals("bar", context.getSystemProperties().getProperty("foo"));
    }

    @Test
    public void testJobParameters() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", "bar").toJobParameters();
        JobInstance jobInstance = new JobInstance(0L, "foo");
        jobExecution = new JobExecution(5L, jobParameters);
        jobExecution.setJobInstance(jobInstance);
        context = new JobContext(jobExecution);
        Assert.assertEquals("bar", context.getJobParameters().get("foo"));
    }

    @Test
    public void testContextId() throws Exception {
        Assert.assertEquals("jobExecution#1", context.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalContextId() throws Exception {
        context = new JobContext(new JobExecution(((Long) (null))));
        context.getId();
    }
}

