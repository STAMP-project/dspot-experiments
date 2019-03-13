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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Thomas Risberg
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class StepWithSimpleTaskJobParserTests {
    @Autowired
    private Job job;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    @Qualifier("listener")
    private TestListener listener;

    @Test
    public void testJob() throws Exception {
        Assert.assertNotNull(job);
        Assert.assertTrue(((job) instanceof FlowJob));
        JobExecution jobExecution = jobRepository.createJobExecution(job.getName(), new JobParameters());
        TestTasklet t1 = assertTasklet(job, "step1", "t1");
        TestTasklet t2 = assertTasklet(job, "step2", "t2");
        TestTasklet t3 = assertTasklet(job, "step3", "t3");
        TestTasklet t4 = assertTasklet(job, "step4", "t4");
        job.execute(jobExecution);
        Assert.assertTrue(t1.isExecuted());
        Assert.assertTrue(t2.isExecuted());
        Assert.assertTrue(t3.isExecuted());
        Assert.assertTrue(t4.isExecuted());
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(4, jobExecution.getStepExecutions().size());
        Assert.assertTrue(listener.isExecuted());
    }
}

