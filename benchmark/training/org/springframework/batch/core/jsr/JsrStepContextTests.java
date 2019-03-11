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


import java.util.Properties;
import javax.batch.runtime.BatchStatus.STARTED;
import javax.batch.runtime.Metric;
import javax.batch.runtime.context.StepContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.util.ClassUtils;


public class JsrStepContextTests {
    private StepExecution stepExecution;

    private StepContext stepContext;

    private ExecutionContext executionContext;

    private ExecutionContextUserSupport executionContextUserSupport = new ExecutionContextUserSupport(ClassUtils.getShortName(JsrStepContext.class));

    @Test
    public void testBasicProperties() {
        Assert.assertEquals(STARTED, stepContext.getBatchStatus());
        Assert.assertEquals(null, stepContext.getExitStatus());
        stepContext.setExitStatus("customExitStatus");
        Assert.assertEquals("customExitStatus", stepContext.getExitStatus());
        Assert.assertEquals(5L, stepContext.getStepExecutionId());
        Assert.assertEquals("testStep", stepContext.getStepName());
        Assert.assertEquals("This is my transient data", stepContext.getTransientUserData());
        Properties params = stepContext.getProperties();
        Assert.assertEquals("value", params.get("key"));
        Metric[] metrics = stepContext.getMetrics();
        for (Metric metric : metrics) {
            switch (metric.getType()) {
                case COMMIT_COUNT :
                    Assert.assertEquals(1, metric.getValue());
                    break;
                case FILTER_COUNT :
                    Assert.assertEquals(2, metric.getValue());
                    break;
                case PROCESS_SKIP_COUNT :
                    Assert.assertEquals(3, metric.getValue());
                    break;
                case READ_COUNT :
                    Assert.assertEquals(4, metric.getValue());
                    break;
                case READ_SKIP_COUNT :
                    Assert.assertEquals(5, metric.getValue());
                    break;
                case ROLLBACK_COUNT :
                    Assert.assertEquals(6, metric.getValue());
                    break;
                case WRITE_COUNT :
                    Assert.assertEquals(7, metric.getValue());
                    break;
                case WRITE_SKIP_COUNT :
                    Assert.assertEquals(8, metric.getValue());
                    break;
                default :
                    Assert.fail("Invalid metric type");
            }
        }
    }

    @Test
    public void testSetExitStatus() {
        stepContext.setExitStatus("new Exit Status");
        Assert.assertEquals("new Exit Status", stepExecution.getExitStatus().getExitCode());
    }

    @Test
    public void testPersistentUserData() {
        String data = "saved data";
        stepContext.setPersistentUserData(data);
        Assert.assertEquals(data, stepContext.getPersistentUserData());
        Assert.assertEquals(data, executionContext.get(executionContextUserSupport.getKey("batch_jsr_persistentUserData")));
    }

    @Test
    public void testGetExceptionEmpty() {
        Assert.assertNull(stepContext.getException());
    }

    @Test
    public void testGetExceptionException() {
        stepExecution.addFailureException(new Exception("expected"));
        Assert.assertEquals("expected", stepContext.getException().getMessage());
    }

    @Test
    public void testGetExceptionThrowable() {
        stepExecution.addFailureException(new Throwable("expected"));
        Assert.assertTrue(stepContext.getException().getMessage().endsWith("expected"));
    }

    @Test
    public void testGetExceptionMultiple() {
        stepExecution.addFailureException(new Exception("not me"));
        stepExecution.addFailureException(new Exception("not me either"));
        stepExecution.addFailureException(new Exception("me"));
        Assert.assertEquals("me", stepContext.getException().getMessage());
    }
}

