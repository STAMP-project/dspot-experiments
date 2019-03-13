/**
 * Copyright 2006-2013 the original author or authors.
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


import BatchStatus.STOPPED;
import BatchStatus.STOPPING;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 * @since 2.0
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class SplitInterruptedJobParserTests extends AbstractJobParserTests {
    @Test
    public void testSplitInterrupted() throws Exception {
        final JobExecution jobExecution = createJobExecution();
        new Thread(new Runnable() {
            @Override
            public void run() {
                job.execute(jobExecution);
            }
        }).start();
        Thread.sleep(100L);
        jobExecution.setStatus(STOPPING);
        Thread.sleep(200L);
        int count = 0;
        while (((jobExecution.getStatus()) == (BatchStatus.STOPPING)) && ((count++) < 10)) {
            Thread.sleep(200L);
        } 
        Assert.assertTrue(("Timed out waiting for job to stop: " + jobExecution), (count < 10));
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.STOPPED.getExitCode(), jobExecution.getExitStatus().getExitCode());
        Assert.assertTrue(("Wrong step names: " + (stepNamesList)), stepNamesList.contains("stop"));
        StepExecution stepExecution = getStepExecution(jobExecution, "stop");
        Assert.assertEquals(STOPPED, stepExecution.getStatus());
        Assert.assertEquals(ExitStatus.STOPPED.getExitCode(), stepExecution.getExitStatus().getExitCode());
        Assert.assertEquals(1, stepNamesList.size());
    }
}

