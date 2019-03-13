package org.javaee7.batch.flow;


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
 * sample, by using the +flow+ element, we define a sequence of elements that execute together as a unit. When the
 * flow is finished the flow transitions to the next execution element. The execution elements of a flow cannot
 * transition to elements outside the flow.
 *
 * include::myJob.xml[]
 *
 * The flow element is useful to build a self contained workflow that you can reference and build as a part of a bigger
 * workflow.
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class BatchFlowTest {
    /**
     * In the test, we're just going to invoke the batch execution and wait for completion. To validate the test
     * expected behaviour we need to query +javax.batch.operations.JobOperator#getStepExecutions+ and the
     * +javax.batch.runtime.Metric+ object available in the step execution.
     *
     * @throws Exception
     * 		an exception if the batch could not complete successfully.
     */
    @Test
    public void testBatchFlow() throws Exception {
        JobOperator jobOperator = getJobOperator();
        Long executionId = jobOperator.start("myJob", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        List<String> executedSteps = new ArrayList<>();
        for (StepExecution stepExecution : stepExecutions) {
            executedSteps.add(stepExecution.getStepName());
            if (stepExecution.getStepName().equals("step2")) {
                Map<Metric.MetricType, Long> metricsMap = BatchTestHelper.getMetricsMap(stepExecution.getMetrics());
                System.out.println(metricsMap);
                Assert.assertEquals(5L, metricsMap.get(READ_COUNT).longValue());
                Assert.assertEquals(5L, metricsMap.get(WRITE_COUNT).longValue());
                Assert.assertEquals(((5L / 3) + ((5 % 3) > 0 ? 1 : 0)), metricsMap.get(COMMIT_COUNT).longValue());
            }
        }
        // <1> Make sure all the steps were executed.
        Assert.assertEquals(3, stepExecutions.size());
        // <2> Make sure all the steps were executed in order of declaration.
        Assert.assertArrayEquals(new String[]{ "step1", "step2", "step3" }, executedSteps.toArray());
        // <3> Job should be completed.
        Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
    }
}

