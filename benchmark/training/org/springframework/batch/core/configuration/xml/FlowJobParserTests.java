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
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FlowJobParserTests {
    @Autowired
    @Qualifier("job1")
    private Job job1;

    @Autowired
    @Qualifier("job2")
    private Job job2;

    @Autowired
    @Qualifier("job3")
    private Job job3;

    @Autowired
    @Qualifier("job4")
    private Job job4;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean;

    @Test
    public void testFlowJob() throws Exception {
        Assert.assertNotNull(job1);
        JobExecution jobExecution = jobRepository.createJobExecution(job1.getName(), new JobParameters());
        job1.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        List<String> stepNames = getStepNames(jobExecution);
        Assert.assertEquals(4, stepNames.size());
        Assert.assertEquals("[s1, s2, s3, s4]", stepNames.toString());
    }

    @Test
    public void testFlowJobWithNestedTransitions() throws Exception {
        Assert.assertNotNull(job2);
        JobExecution jobExecution = jobRepository.createJobExecution(job2.getName(), new JobParameters());
        job2.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(3, jobExecution.getStepExecutions().size());
        List<String> stepNames = getStepNames(jobExecution);
        Assert.assertEquals(3, stepNames.size());
        Assert.assertEquals("[s2, s3, job2.s1]", stepNames.toString());
    }

    @Test
    public void testFlowJobWithNoSteps() throws Exception {
        Assert.assertNotNull(job3);
        JobExecution jobExecution = jobRepository.createJobExecution(job3.getName(), new JobParameters());
        job3.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        List<String> stepNames = getStepNames(jobExecution);
        Assert.assertEquals(2, stepNames.size());
        Assert.assertEquals("[s2, s3]", stepNames.toString());
    }

    @Test
    public void testFlowInSplit() throws Exception {
        Assert.assertNotNull(job4);
        JobExecution jobExecution = jobRepository.createJobExecution(job4.getName(), new JobParameters());
        job4.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        List<String> stepNames = getStepNames(jobExecution);
        Assert.assertEquals(4, stepNames.size());
        Assert.assertEquals("[s2, s3, s2, s3]", stepNames.toString());
    }
}

