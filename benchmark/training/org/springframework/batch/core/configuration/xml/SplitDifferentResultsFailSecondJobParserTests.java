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
 * @author Dan Garrette
 * @since 2.0
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class SplitDifferentResultsFailSecondJobParserTests extends AbstractJobParserTests {
    @Test
    public void testSplitDifferentResultsFailSecond() throws Exception {
        JobExecution jobExecution = createJobExecution();
        job.execute(jobExecution);
        Assert.assertEquals(("Wrong step names: " + (stepNamesList)), 3, stepNamesList.size());
        Assert.assertTrue(("Wrong step names: " + (stepNamesList)), stepNamesList.contains("s1"));
        Assert.assertTrue(("Wrong step names: " + (stepNamesList)), stepNamesList.contains("fail"));
        Assert.assertTrue(stepNamesList.contains("s3"));
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        // You can't suppress a FAILED exit status
        Assert.assertEquals(FAILED, jobExecution.getExitStatus());
        StepExecution stepExecution1 = getStepExecution(jobExecution, "s1");
        Assert.assertEquals(COMPLETED, stepExecution1.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, stepExecution1.getExitStatus());
        StepExecution stepExecution2 = getStepExecution(jobExecution, "fail");
        Assert.assertEquals(BatchStatus.FAILED, stepExecution2.getStatus());
        Assert.assertEquals(FAILED.getExitCode(), stepExecution2.getExitStatus().getExitCode());
        StepExecution stepExecution3 = getStepExecution(jobExecution, "s3");
        Assert.assertEquals(COMPLETED, stepExecution3.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, stepExecution3.getExitStatus());
    }
}

