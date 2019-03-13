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
import BatchStatus.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 * @since 2.1.9
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class NextAttributeUnknownJobParserTests extends AbstractJobParserTests {
    @Test
    public void testDefaultUnknown() throws Exception {
        JobExecution jobExecution = createJobExecution();
        job.execute(jobExecution);
        Assert.assertEquals(3, stepNamesList.size());
        Assert.assertEquals("[s1, unknown, s2]", stepNamesList.toString());
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        StepExecution stepExecution1 = getStepExecution(jobExecution, "s1");
        Assert.assertEquals(COMPLETED, stepExecution1.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, stepExecution1.getExitStatus());
        StepExecution stepExecution2 = getStepExecution(jobExecution, "unknown");
        Assert.assertEquals(UNKNOWN, stepExecution2.getStatus());
        Assert.assertEquals(ExitStatus.UNKNOWN, stepExecution2.getExitStatus());
    }

    public static class UnknownListener extends StepExecutionListenerSupport {
        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            stepExecution.setStatus(UNKNOWN);
            return ExitStatus.UNKNOWN;
        }
    }
}

