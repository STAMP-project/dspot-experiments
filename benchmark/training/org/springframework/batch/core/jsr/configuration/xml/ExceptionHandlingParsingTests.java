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
package org.springframework.batch.core.jsr.configuration.xml;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import Metric.MetricType.PROCESS_SKIP_COUNT;
import Metric.MetricType.ROLLBACK_COUNT;
import java.util.List;
import java.util.Properties;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.jsr.AbstractJsrTestCase;


public class ExceptionHandlingParsingTests extends AbstractJsrTestCase {
    @Test
    public void testSkippable() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties jobParameters = new Properties();
        jobParameters.setProperty("run", "1");
        JobExecution execution1 = AbstractJsrTestCase.runJob("ExceptionHandlingParsingTests-context", jobParameters, 10000L);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(execution1.getExecutionId());
        Assert.assertEquals(FAILED, execution1.getBatchStatus());
        Assert.assertEquals(1, stepExecutions.size());
        Assert.assertEquals(1, AbstractJsrTestCase.getMetric(stepExecutions.get(0), PROCESS_SKIP_COUNT).getValue());
        jobParameters = new Properties();
        jobParameters.setProperty("run", "2");
        JobExecution execution2 = AbstractJsrTestCase.restartJob(execution1.getExecutionId(), jobParameters, 10000L);
        stepExecutions = jobOperator.getStepExecutions(execution2.getExecutionId());
        Assert.assertEquals(FAILED, execution2.getBatchStatus());
        Assert.assertEquals(2, stepExecutions.size());
        jobParameters = new Properties();
        jobParameters.setProperty("run", "3");
        JobExecution execution3 = AbstractJsrTestCase.restartJob(execution2.getExecutionId(), jobParameters, 10000L);
        stepExecutions = jobOperator.getStepExecutions(execution3.getExecutionId());
        Assert.assertEquals(COMPLETED, execution3.getBatchStatus());
        Assert.assertEquals(2, stepExecutions.size());
        Assert.assertEquals(0, AbstractJsrTestCase.getMetric(stepExecutions.get(1), ROLLBACK_COUNT).getValue());
        jobParameters = new Properties();
        jobParameters.setProperty("run", "4");
        JobExecution execution4 = AbstractJsrTestCase.runJob("ExceptionHandlingParsingTests-context", jobParameters, 10000L);
        stepExecutions = jobOperator.getStepExecutions(execution4.getExecutionId());
        Assert.assertEquals(COMPLETED, execution4.getBatchStatus());
        Assert.assertEquals(3, stepExecutions.size());
    }

    public static class ProblemProcessor implements ItemProcessor {
        @Inject
        @BatchProperty
        private String runId = "0";

        private boolean hasRetried = false;

        private void throwException(Object item) throws Exception {
            int runId = Integer.parseInt(this.runId);
            if (runId == 1) {
                if (item.equals("One")) {
                    throw new Exception("skip me");
                } else
                    if (item.equals("Two")) {
                        throw new RuntimeException("But don't skip me");
                    }

            } else
                if (runId == 2) {
                    if ((item.equals("Three")) && (!(hasRetried))) {
                        hasRetried = true;
                        throw new Exception("retry me");
                    } else
                        if (item.equals("Four")) {
                            throw new RuntimeException("But don't retry me");
                        }

                } else
                    if (runId == 3) {
                        if (item.equals("Five")) {
                            throw new Exception("Don't rollback on my account");
                        }
                    }


        }

        @Override
        public Object processItem(Object item) throws Exception {
            throwException(item);
            return item;
        }
    }
}

