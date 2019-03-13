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


import BatchStatus.COMPLETED;
import BatchStatus.STOPPED;
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
public class StopCustomStatusJobParserTests extends AbstractJobParserTests {
    @Test
    public void testStopCustomStatus() throws Exception {
        // 
        // First Launch
        // 
        JobExecution jobExecution = createJobExecution();
        job.execute(jobExecution);
        Assert.assertEquals(1, stepNamesList.size());
        Assert.assertEquals(("Wrong steps executed: " + (stepNamesList)), "[stop]", stepNamesList.toString());
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.STOPPED.getExitCode(), jobExecution.getExitStatus().getExitCode());
        StepExecution stepExecution1 = getStepExecution(jobExecution, "stop");
        Assert.assertEquals(COMPLETED, stepExecution1.getStatus());
        Assert.assertEquals("FOO", stepExecution1.getExitStatus().getExitCode());
        // 
        // Second Launch
        // 
        stepNamesList.clear();
        jobExecution = createJobExecution();
        job.execute(jobExecution);
        Assert.assertEquals(1, stepNamesList.size());// step1 is not executed

        Assert.assertEquals(("Wrong steps executed: " + (stepNamesList)), "[s2]", stepNamesList.toString());
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        StepExecution stepExecution2 = getStepExecution(jobExecution, "s2");
        Assert.assertEquals(COMPLETED, stepExecution2.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, stepExecution2.getExitStatus());
    }
}

