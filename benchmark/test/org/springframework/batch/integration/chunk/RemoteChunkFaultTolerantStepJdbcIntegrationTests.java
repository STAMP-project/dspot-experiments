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
import org.springframework.messaging.PollableChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class RemoteChunkFaultTolerantStepJdbcIntegrationTests {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private PollableChannel replies;

    @Test
    @DirtiesContext
    public void testFailedStep() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("unsupported"))));
        Assert.assertEquals(FAILED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        // In principle the write count could be more than 2 and less than 9...
        Assert.assertEquals(7, stepExecution.getWriteCount());
    }

    @Test
    @DirtiesContext
    public void testFailedStepOnError() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("error"))));
        Assert.assertEquals(FAILED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        // In principle the write count could be more than 2 and less than 9...
        Assert.assertEquals(7, stepExecution.getWriteCount());
    }

    @Test
    @DirtiesContext
    public void testSunnyDayFaultTolerant() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new org.springframework.batch.core.JobParameters(Collections.singletonMap("item.three", new JobParameter("3"))));
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        Assert.assertEquals(9, stepExecution.getWriteCount());
    }

    @Test
    @DirtiesContext
    public void testSkipsInWriter() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder().addString("item.three", "fail").addLong("run.id", 1L).toJobParameters());
        // System.err.println(new SimpleJdbcTemplate(dataSource).queryForList("SELECT * FROM INT_MESSAGE_GROUP"));
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(9, stepExecution.getReadCount());
        Assert.assertEquals(7, stepExecution.getWriteCount());
        // The whole chunk gets skipped...
        Assert.assertEquals(2, stepExecution.getWriteSkipCount());
    }
}

