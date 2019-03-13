package org.springframework.batch.integration.chunk;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class RemoteChunkStepIntegrationTests {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    public void testSunnyDaySimpleStep() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("3"))));
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        Assert.assertEquals(9, stepExecution.getWriteCount());
    }

    @Test
    public void testFailedStep() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("fail"))));
        Assert.assertEquals(FAILED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        // In principle the write count could be more than 2 and less than 9...
        Assert.assertEquals(7, stepExecution.getWriteCount());
    }
}

