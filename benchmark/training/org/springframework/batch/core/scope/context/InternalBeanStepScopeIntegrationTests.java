/**
 * Copyright 2014-2018 the original author or authors.
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
package org.springframework.batch.core.scope.context;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author mminella
 */
public class InternalBeanStepScopeIntegrationTests {
    @Test
    public void testCommitIntervalJobParameter() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("/org/springframework/batch/core/scope/context/CommitIntervalJobParameter-context.xml");
        Job job = context.getBean(Job.class);
        JobLauncher launcher = context.getBean(JobLauncher.class);
        JobExecution execution = launcher.run(job, new JobParametersBuilder().addLong("commit.interval", 1L).toJobParameters());
        Assert.assertEquals(COMPLETED, execution.getStatus());
        Assert.assertEquals(2, execution.getStepExecutions().iterator().next().getReadCount());
        Assert.assertEquals(2, execution.getStepExecutions().iterator().next().getWriteCount());
    }

    @Test
    public void testInvalidCommitIntervalJobParameter() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("/org/springframework/batch/core/scope/context/CommitIntervalJobParameter-context.xml");
        Job job = context.getBean(Job.class);
        JobLauncher launcher = context.getBean(JobLauncher.class);
        JobExecution execution = launcher.run(job, new JobParametersBuilder().addLong("commit.intervall", 1L).toJobParameters());
        Assert.assertEquals(FAILED, execution.getStatus());
    }
}

