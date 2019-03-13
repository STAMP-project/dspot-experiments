/**
 * Copyright 2013-2014 the original author or authors.
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


import ExitStatus.COMPLETED;
import ExitStatus.FAILED;
import ExitStatus.STOPPED;
import java.util.List;
import java.util.Properties;
import javax.batch.api.AbstractBatchlet;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.jsr.AbstractJsrTestCase;


/**
 * <p>
 * Unit tests around {@link FlowParser}.
 * </p>
 *
 * @author Chris Schaefer
 * @since 3.0
 */
public class FlowParserTests extends AbstractJsrTestCase {
    @Test
    public void testDuplicateTransitionPatternsAllowed() throws Exception {
        JobExecution stoppedExecution = AbstractJsrTestCase.runJob("FlowParserTests-context", new Properties(), 10000L);
        Assert.assertEquals(STOPPED.getExitCode(), stoppedExecution.getExitStatus());
        JobExecution endedExecution = AbstractJsrTestCase.restartJob(stoppedExecution.getExecutionId(), new Properties(), 10000L);
        Assert.assertEquals(COMPLETED.getExitCode(), endedExecution.getExitStatus());
    }

    @Test
    public void testWildcardAddedLastWhenUsedWithNextAttrAndNoTransitionElements() throws Exception {
        JobExecution jobExecution = AbstractJsrTestCase.runJob("FlowParserTestsWildcardAndNextAttrJob", new Properties(), 1000L);
        Assert.assertEquals(FAILED.getExitCode(), jobExecution.getExitStatus());
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(jobExecution.getExecutionId());
        Assert.assertEquals(1, stepExecutions.size());
        StepExecution failedStep = stepExecutions.get(0);
        Assert.assertTrue("step1".equals(failedStep.getStepName()));
    }

    @Test
    public void testStepGetsFailedTransitionWhenNextAttributePresent() throws Exception {
        JobExecution jobExecution = AbstractJsrTestCase.runJob("FlowParserTestsStepGetsFailedTransitionWhenNextAttributePresent", new Properties(), 10000L);
        Assert.assertEquals(FAILED.getExitCode(), jobExecution.getExitStatus());
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(jobExecution.getExecutionId());
        Assert.assertEquals(1, stepExecutions.size());
        StepExecution failedStep = stepExecutions.get(0);
        Assert.assertTrue("failedExitStatusStep".equals(failedStep.getStepName()));
        Assert.assertTrue("FAILED".equals(failedStep.getExitStatus()));
    }

    @Test
    public void testStepNoOverrideWhenNextAndFailedTransitionElementExists() throws Exception {
        JobExecution jobExecution = AbstractJsrTestCase.runJob("FlowParserTestsStepNoOverrideWhenNextAndFailedTransitionElementExists", new Properties(), 10000L);
        Assert.assertEquals(FAILED.getExitCode(), jobExecution.getExitStatus());
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(jobExecution.getExecutionId());
        Assert.assertEquals(1, stepExecutions.size());
        StepExecution failedStep = stepExecutions.get(0);
        Assert.assertTrue("failedExitStatusStepDontOverride".equals(failedStep.getStepName()));
        Assert.assertTrue("CUSTOM_FAIL".equals(failedStep.getExitStatus()));
    }

    public static class TestBatchlet extends AbstractBatchlet {
        private static int CNT;

        @Inject
        private StepContext stepContext;

        @Override
        public String process() throws Exception {
            String exitCode = "DISTINCT";
            if ("step3".equals(stepContext.getStepName())) {
                exitCode = (((FlowParserTests.TestBatchlet.CNT) % 2) == 0) ? "DISTINCT" : "RESTART";
                (FlowParserTests.TestBatchlet.CNT)++;
            }
            if ("failedExitStatusStep".equals(stepContext.getStepName())) {
                exitCode = "FAILED";
            }
            if ("failedExitStatusStepDontOverride".equals(stepContext.getStepName())) {
                exitCode = "CUSTOM_FAIL";
            }
            return exitCode;
        }
    }

    public static class FailingTestBatchlet extends AbstractBatchlet {
        @Override
        public String process() throws Exception {
            throw new RuntimeException("blah");
        }
    }
}

