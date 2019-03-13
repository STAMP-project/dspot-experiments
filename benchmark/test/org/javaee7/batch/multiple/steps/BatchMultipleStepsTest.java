package org.javaee7.batch.multiple.steps;


import Metric.MetricType;
import Metric.MetricType.COMMIT_COUNT;
import Metric.MetricType.READ_COUNT;
import Metric.MetricType.WRITE_COUNT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import org.javaee7.util.BatchTestHelper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * The Batch specification allows you to implement process workflow using a Job Specification Language (JSL). In this
 * sample, by using the +step+ element, it's possible to configure a job that runs multiple steps.
 *
 * One Chunk oriented Step and a Batchlet are configured in the file +myJob.xml+. They both execute in order of
 * declaration. First the Chunk oriented Step and finally the Batchlet Step.
 *
 * include::myJob.xml[]
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class BatchMultipleStepsTest {
    /**
     * In the test, we're just going to invoke the batch execution and wait for completion. To validate the test
     * expected behaviour we need to query +javax.batch.operations.JobOperator#getStepExecutions+ and the
     * +javax.batch.runtime.Metric+ object available in the step execution.
     *
     * @throws Exception
     * 		an exception if the batch could not complete successfully.
     */
    @Test
    public void testBatchMultipleSteps() throws Exception {
        JobOperator jobOperator = getJobOperator();
        Long executionId = jobOperator.start("myJob", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        List<String> executedSteps = new ArrayList<>();
        for (StepExecution stepExecution : stepExecutions) {
            executedSteps.add(stepExecution.getStepName());
            if (stepExecution.getStepName().equals("step1")) {
                Map<Metric.MetricType, Long> metricsMap = BatchTestHelper.getMetricsMap(stepExecution.getMetrics());
                Assert.assertEquals(10L, metricsMap.get(READ_COUNT).longValue());
                Assert.assertEquals((10L / 2), metricsMap.get(WRITE_COUNT).longValue());
                Assert.assertEquals(((10L / 3) + ((10L % 3) > 0 ? 1 : 0)), metricsMap.get(COMMIT_COUNT).longValue());
            }
        }
        // <1> Make sure all the steps were executed.
        Assert.assertEquals(2, stepExecutions.size());
        // <2> Make sure all the steps were executed in order of declaration.
        Assert.assertArrayEquals(new String[]{ "step1", "step2" }, executedSteps.toArray());
        // <3> Job should be completed.
        Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
    }
}

