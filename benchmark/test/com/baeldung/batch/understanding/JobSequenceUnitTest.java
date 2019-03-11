package com.baeldung.batch.understanding;


import BatchStatus.COMPLETED;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class JobSequenceUnitTest {
    @Test
    public void givenTwoSteps_thenBatch_CompleteWithSuccess() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("simpleJobSequence", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        Assertions.assertEquals(2, jobOperator.getStepExecutions(executionId).size());
        Assertions.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }

    @Test
    public void givenFlow_thenBatch_CompleteWithSuccess() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("flowJobSequence", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        Assertions.assertEquals(3, jobOperator.getStepExecutions(executionId).size());
        Assertions.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }

    @Test
    public void givenDecider_thenBatch_CompleteWithSuccess() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("decideJobSequence", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        List<String> executedSteps = new ArrayList<>();
        for (StepExecution stepExecution : stepExecutions) {
            executedSteps.add(stepExecution.getStepName());
        }
        Assertions.assertEquals(2, jobOperator.getStepExecutions(executionId).size());
        Assertions.assertArrayEquals(new String[]{ "firstBatchStepStep1", "firstBatchStepStep3" }, executedSteps.toArray());
        Assertions.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }

    @Test
    public void givenSplit_thenBatch_CompletesWithSuccess() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("splitJobSequence", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        List<String> executedSteps = new ArrayList<>();
        for (StepExecution stepExecution : stepExecutions) {
            executedSteps.add(stepExecution.getStepName());
        }
        Assertions.assertEquals(3, stepExecutions.size());
        Assertions.assertTrue(executedSteps.contains("splitJobSequenceStep1"));
        Assertions.assertTrue(executedSteps.contains("splitJobSequenceStep2"));
        Assertions.assertTrue(executedSteps.contains("splitJobSequenceStep3"));
        Assertions.assertTrue(((executedSteps.get(0).equals("splitJobSequenceStep1")) || (executedSteps.get(0).equals("splitJobSequenceStep2"))));
        Assertions.assertTrue(((executedSteps.get(1).equals("splitJobSequenceStep1")) || (executedSteps.get(1).equals("splitJobSequenceStep2"))));
        Assertions.assertTrue(executedSteps.get(2).equals("splitJobSequenceStep3"));
        Assertions.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }
}

