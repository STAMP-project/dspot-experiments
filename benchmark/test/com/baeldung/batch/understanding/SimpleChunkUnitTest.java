package com.baeldung.batch.understanding;


import BatchStatus.COMPLETED;
import Metric.MetricType;
import Metric.MetricType.COMMIT_COUNT;
import Metric.MetricType.FILTER_COUNT;
import Metric.MetricType.PROCESS_SKIP_COUNT;
import Metric.MetricType.READ_COUNT;
import Metric.MetricType.READ_SKIP_COUNT;
import Metric.MetricType.ROLLBACK_COUNT;
import Metric.MetricType.WRITE_COUNT;
import Metric.MetricType.WRITE_SKIP_COUNT;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class SimpleChunkUnitTest {
    @Test
    public void givenChunk_thenBatch_CompletesWithSucess() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("simpleChunk", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        for (StepExecution stepExecution : stepExecutions) {
            if (stepExecution.getStepName().equals("firstChunkStep")) {
                Map<Metric.MetricType, Long> metricsMap = BatchTestHelper.getMetricsMap(stepExecution.getMetrics());
                Assertions.assertEquals(10L, metricsMap.get(READ_COUNT).longValue());
                Assertions.assertEquals((10L / 2L), metricsMap.get(WRITE_COUNT).longValue());
                Assertions.assertEquals(((10L / 3) + ((10L % 3) > 0 ? 1 : 0)), metricsMap.get(COMMIT_COUNT).longValue());
            }
        }
        Assertions.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }

    @Test
    public void givenChunk__thenBatch_fetchInformation() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Long executionId = jobOperator.start("simpleChunk", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        // job name contains simpleBatchLet which is the name of the file
        Assertions.assertTrue(jobOperator.getJobNames().contains("simpleChunk"));
        // job parameters are empty
        Assertions.assertTrue(jobOperator.getParameters(executionId).isEmpty());
        // step execution information
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        Assertions.assertEquals("firstChunkStep", stepExecutions.get(0).getStepName());
        // finding out batch status
        Assertions.assertEquals(COMPLETED, stepExecutions.get(0).getBatchStatus());
        Map<Metric.MetricType, Long> metricTest = BatchTestHelper.getMetricsMap(stepExecutions.get(0).getMetrics());
        Assertions.assertEquals(10L, metricTest.get(READ_COUNT).longValue());
        Assertions.assertEquals(5L, metricTest.get(FILTER_COUNT).longValue());
        Assertions.assertEquals(4L, metricTest.get(COMMIT_COUNT).longValue());
        Assertions.assertEquals(5L, metricTest.get(WRITE_COUNT).longValue());
        Assertions.assertEquals(0L, metricTest.get(READ_SKIP_COUNT).longValue());
        Assertions.assertEquals(0L, metricTest.get(WRITE_SKIP_COUNT).longValue());
        Assertions.assertEquals(0L, metricTest.get(PROCESS_SKIP_COUNT).longValue());
        Assertions.assertEquals(0L, metricTest.get(ROLLBACK_COUNT).longValue());
        Assertions.assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }
}

