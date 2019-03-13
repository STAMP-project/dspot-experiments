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
import javax.batch.api.Batchlet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleJobParsingTests {
    @Autowired
    public Job job;

    @Autowired
    @Qualifier("step1")
    public Step step1;

    @Autowired
    @Qualifier("step2")
    public Step step2;

    @Autowired
    @Qualifier("step3")
    public Step step3;

    @Autowired
    public JobLauncher jobLauncher;

    @Autowired
    public Batchlet batchlet;

    @Test
    public void test() throws Exception {
        Assert.assertNotNull(job);
        Assert.assertEquals("job1", job.getName());
        Assert.assertNotNull(step1);
        Assert.assertEquals("step1", step1.getName());
        Assert.assertNotNull(step2);
        Assert.assertEquals("step2", step2.getName());
        Assert.assertNotNull(step3);
        Assert.assertEquals("step3", step3.getName());
        Assert.assertNotNull(batchlet);
        JobExecution execution = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(COMPLETED, execution.getStatus());
        Assert.assertEquals(3, execution.getStepExecutions().size());
    }
}

