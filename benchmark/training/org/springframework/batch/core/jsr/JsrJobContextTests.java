/**
 * Copyright 2013 the original author or authors.
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
package org.springframework.batch.core.jsr;


import BatchStatus.COMPLETED;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.javax.batch.runtime.BatchStatus;


public class JsrJobContextTests {
    private JsrJobContext context;

    @Mock
    private JobExecution execution;

    @Mock
    private JobInstance instance;

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        context = new JsrJobContext();
        context.setJobExecution(null);
    }

    @Test
    public void testGetJobName() {
        Mockito.when(instance.getJobName()).thenReturn("jobName");
        Assert.assertEquals("jobName", context.getJobName());
    }

    @Test
    public void testTransientUserData() {
        context.setTransientUserData("This is my data");
        Assert.assertEquals("This is my data", context.getTransientUserData());
    }

    @Test
    public void testGetInstanceId() {
        Mockito.when(instance.getId()).thenReturn(5L);
        Assert.assertEquals(5L, context.getInstanceId());
    }

    @Test
    public void testGetExecutionId() {
        Mockito.when(execution.getId()).thenReturn(5L);
        Assert.assertEquals(5L, context.getExecutionId());
    }

    @Test
    public void testJobParameters() {
        JobParameters params = new JobParametersBuilder().addString("key1", "value1").toJobParameters();
        Mockito.when(execution.getJobParameters()).thenReturn(params);
        Assert.assertEquals("value1", execution.getJobParameters().getString("key1"));
    }

    @Test
    public void testJobProperties() {
        Assert.assertEquals("jobLevelValue1", context.getProperties().get("jobLevelProperty1"));
    }

    @Test
    public void testGetBatchStatus() {
        Mockito.when(execution.getStatus()).thenReturn(COMPLETED);
        Assert.assertEquals(javax.batch.runtime.BatchStatus, context.getBatchStatus());
    }

    @Test
    public void testExitStatus() {
        context.setExitStatus("my exit status");
        Mockito.verify(execution).setExitStatus(new ExitStatus("my exit status"));
        Mockito.when(execution.getExitStatus()).thenReturn(new ExitStatus("exit"));
        Assert.assertEquals("exit", context.getExitStatus());
    }

    @Test
    public void testInitialNullExitStatus() {
        Mockito.when(execution.getExitStatus()).thenReturn(new ExitStatus("exit"));
        Assert.assertEquals(null, context.getExitStatus());
    }
}

