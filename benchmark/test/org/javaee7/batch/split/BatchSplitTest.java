package org.javaee7.batch.split;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import org.javaee7.util.BatchTestHelper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * The Batch specification allows you to implement process workflow using a Job Specification Language (JSL). In this
 * sample, by using the +split+ element, it's possible to configure a job that runs parallel flows. A +split+ can only
 * contain +flow+ elements. These +flow+ elements can be used to implement separate executions to be processed by the
 * job.
 *
 * Three simple Batchlet's are configured in the file +myJob.xml+. +MyBatchlet1+ and +MyBatchlet2+ are setted up to
 * execute in parallel by using the +split+ and +flow+ elements. +MyBatchlet3+ is only going to execute after
 * +MyBatchlet1+ and +MyBatchlet2+ are both done with their job.
 *
 * include::myJob.xml[]
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class BatchSplitTest {
    /**
     * In the test, we're just going to invoke the batch execution and wait for completion. To validate the test
     * expected behaviour we need to query +javax.batch.operations.JobOperator#getStepExecutions+.
     *
     * @throws Exception
     * 		an exception if the batch could not complete successfully.
     */
    @Test
    public void testBatchSplit() throws Exception {
        JobOperator jobOperator = getJobOperator();
        Long executionId = jobOperator.start("myJob", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        List<String> executedSteps = new ArrayList<>();
        for (StepExecution stepExecution : stepExecutions) {
            executedSteps.add(stepExecution.getStepName());
        }
        // <1> Make sure all the steps were executed.
        Assert.assertEquals(3, stepExecutions.size());
        Assert.assertTrue(executedSteps.contains("step1"));
        Assert.assertTrue(executedSteps.contains("step2"));
        Assert.assertTrue(executedSteps.contains("step3"));
        // <2> Steps 'step1' and 'step2' can appear in any order, since they were executed in parallel.
        Assert.assertTrue(((executedSteps.get(0).equals("step1")) || (executedSteps.get(0).equals("step2"))));
        Assert.assertTrue(((executedSteps.get(1).equals("step1")) || (executedSteps.get(1).equals("step2"))));
        // <3> Step 'step3' is always the last to be executed.
        Assert.assertTrue(executedSteps.get(2).equals("step3"));
        // <4> Job should be completed.
        Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
    }
}

