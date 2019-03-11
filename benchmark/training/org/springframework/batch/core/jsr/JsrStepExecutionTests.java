/**
 * Copyright 2014 the original author or authors.
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


import java.util.Date;
import javax.batch.runtime.BatchStatus.STARTED;
import javax.batch.runtime.Metric;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.util.ClassUtils;


public class JsrStepExecutionTests {
    private StepExecution stepExecution;

    private StepExecution jsrStepExecution;

    // The API that sets the persisted user data is on the JsrStepContext so the key within the ExecutionContext is JsrStepContext
    private ExecutionContextUserSupport executionContextUserSupport = new ExecutionContextUserSupport(ClassUtils.getShortName(JsrStepContext.class));

    @Test(expected = IllegalArgumentException.class)
    public void testWithNullStepExecution() {
        new JsrStepExecution(null);
    }

    @Test
    public void testNullExitStatus() {
        stepExecution.setExitStatus(null);
        Assert.assertNull(jsrStepExecution.getExitStatus());
    }

    @Test
    public void testBaseValues() {
        Assert.assertEquals(5L, jsrStepExecution.getStepExecutionId());
        Assert.assertEquals("testStep", jsrStepExecution.getStepName());
        Assert.assertEquals(STARTED, jsrStepExecution.getBatchStatus());
        Assert.assertEquals(new Date(0), jsrStepExecution.getStartTime());
        Assert.assertEquals(new Date(10000000), jsrStepExecution.getEndTime());
        Assert.assertEquals("customExitStatus", jsrStepExecution.getExitStatus());
        Assert.assertEquals("persisted data", jsrStepExecution.getPersistentUserData());
        Metric[] metrics = jsrStepExecution.getMetrics();
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
}

