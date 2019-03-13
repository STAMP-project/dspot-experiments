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
package org.springframework.batch.core.configuration.xml;


import BatchStatus.ABANDONED;
import BatchStatus.COMPLETED;
import ExitStatus.FAILED;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class StopAndRestartFailedJobParserTests extends AbstractJobParserTests {
    @Test
    public void testStopRestartOnCompletedStep() throws Exception {
        // 
        // First Launch
        // 
        JobExecution jobExecution = launchAndAssert("[s0, s1]");
        StepExecution stepExecution = getStepExecution(jobExecution, "s1");
        Assert.assertEquals(ABANDONED, stepExecution.getStatus());
        Assert.assertEquals(FAILED.getExitCode(), stepExecution.getExitStatus().getExitCode());
        // 
        // Second Launch
        // 
        stepNamesList.clear();
        jobExecution = launchAndAssert("[s0, s2]");
        stepExecution = getStepExecution(jobExecution, "s2");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED.getExitCode(), stepExecution.getExitStatus().getExitCode());
    }
}

