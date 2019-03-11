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
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class RemoteChunkFaultTolerantStepJmsIntegrationTests {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    public void testFailedStep() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("unsupported"))));
        Assert.assertEquals(FAILED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        // In principle the write count could be more than 2 and less than 9...
        Assert.assertEquals(7, stepExecution.getWriteCount());
    }

    @Test
    public void testFailedStepOnError() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("error"))));
        Assert.assertEquals(FAILED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        // In principle the write count could be more than 2 and less than 9...
        Assert.assertEquals(7, stepExecution.getWriteCount());
    }

    @Test
    public void testSunnyDayFaultTolerant() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("3"))));
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        Assert.assertEquals(9, stepExecution.getWriteCount());
    }

    @Test
    public void testSkipsInWriter() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder().addString("item.three", "fail").addLong("run.id", 1L).toJobParameters());
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        Assert.assertEquals(7, stepExecution.getWriteCount());
        Assert.assertEquals(2, stepExecution.getWriteSkipCount());
    }
}

