package com.baeldung.batch.understanding;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import java.util.List;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


class SimpleErrorChunkUnitTest {
    @Test
    public void givenChunkError_thenBatch_CompletesWithFailed() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("simpleErrorChunk", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestFailed(jobExecution);
        Assert.assertEquals(jobExecution.getBatchStatus(), FAILED);
    }

    @Test
    public void givenChunkError_thenErrorSkipped_CompletesWithSuccess() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("simpleErrorSkipChunk", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        for (StepExecution stepExecution : stepExecutions) {
            if (stepExecution.getStepName().equals("errorStep")) {
                jobOperator.getStepExecutions(executionId).stream().map(BatchTestHelper::getProcessSkipCount).forEach(( skipCount) -> assertEquals(1L, skipCount.longValue()));
            }
        }
        Assert.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }
}

