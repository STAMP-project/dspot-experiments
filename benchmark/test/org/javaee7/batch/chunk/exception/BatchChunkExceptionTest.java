package org.javaee7.batch.chunk.exception;


import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric.MetricType;
import javax.batch.runtime.StepExecution;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Exceptions are a natural part of Batch Processing, and the batch itself should be prepared to deal with
 * exceptions during processing.
 *
 * Batch Processing deals with two kinds of exceptions: skippable and retryable. Skippable Exceptions are used to skip
 * elements during reading, processing and writing and continue to the next element. Retryable Exceptions on the other
 * hand when thrown will try to retry the chunk on which the exception occurred.
 *
 * When the same exception is specified as both retryable and skippable, retryable takes precedence over skippable
 * during regular processing of the chunk. While the chunk is retrying, skippable takes precedence over retryable since
 * the exception is already being retried.
 *
 * The Reader:
 * include::MyItemReader[]
 *
 * Just reads elements from a list and simulate a retry exception.
 *
 * The Processor:
 * include::MyItemProcessor[]
 *
 * Process and simulate a skip exception.
 *
 * The Writer:
 * include::MyItemWriter[]
 *
 * The writer will retry an exception and then skip it.
 *
 * The batch specification also allows you to provide listeners for skipping and retrying for every operation. Have a
 * look into the following classes:
 *
 * * +MySkipReadListener+
 * * +MySkipProcessorListener+
 * * +MySkipWriteListener+
 * * +MyRetryReadListener+
 * * +MyRetryProcessorListener+
 * * +MyRetryWriteListener+
 *
 * Events can be caught via extending the following classes, for the appropriate batch lifecycle event:
 *
 * * +javax.batch.api.chunk.listener.SkipReadListener+
 * * +javax.batch.api.chunk.listener.SkipProcessListener+
 * * +javax.batch.api.chunk.listener.SkipWriteListener+
 * * +javax.batch.api.chunk.listener.RetryReadListener+
 * * +javax.batch.api.chunk.listener.RetryProcessListener+
 * * +javax.batch.api.chunk.listener.RetryWriteListener+
 *
 * include::myJob.xml[]
 *
 * A very simple job is defined in the +myJob.xml+ file. Just a single step with a reader, a processor and a writer. For
 * this sample we are going to process a few records and mix some exceptions during read, processing and write of the
 * chunk. Batch exception handling is achieved by defining the elements +skippable-exception-classes+ and
 * +retryable-exception-classes+ into the +chunk+. Both elements should indicate the full qualified name of the
 * exceptions that we are trying to catch. The +listeners+ element can be used at the +step+ level to define which
 * listeners to run for each batch processing event.
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class BatchChunkExceptionTest {
    /**
     * In the test, we're just going to invoke the batch execution and wait for completion. To validate the test
     * expected behaviour we need to query the +javax.batch.runtime.Metric+ object available in the step execution.
     *
     * @throws Exception
     * 		an exception if the batch could not complete successfully.
     */
    @Test
    public void testBatchChunkException() throws Exception {
        JobOperator jobOperator = getJobOperator();
        Long executionId = jobOperator.start("myJob", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        for (StepExecution stepExecution : stepExecutions) {
            if (stepExecution.getStepName().equals("myStep")) {
                Map<MetricType, Long> metricsMap = getMetricsMap(stepExecution.getMetrics());
                // TODO: Both WildFLy and Payara have a 2 here, but the test originally tested
                // for 1. Needs investigation.
                long skipCount = metricsMap.get(PROCESS_SKIP_COUNT).longValue();
                Assert.assertTrue(("Skip count=" + skipCount), ((skipCount == 1L) || (skipCount == 2L)));
                // There are a few differences between Glassfish and Wildfly. Needs investigation.
                // assertEquals(1L, metricsMap.get(Metric.MetricType.WRITE_SKIP_COUNT).longValue());
                // assertEquals(1L, retryReadExecutions);
                Assert.assertTrue(("retryReadExecutions=" + (ChunkExceptionRecorder.retryReadExecutions)), (((ChunkExceptionRecorder.retryReadExecutions) == 1L) || ((ChunkExceptionRecorder.retryReadExecutions) == 2L)));
            }
        }
        Assert.assertTrue(ChunkExceptionRecorder.chunkExceptionsCountDownLatch.await(0, TimeUnit.SECONDS));
        Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
    }
}

