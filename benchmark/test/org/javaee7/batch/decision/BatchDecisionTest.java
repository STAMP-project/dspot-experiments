package org.javaee7.batch.decision;


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
 * sample, by using the +decision+ element, it's possible to configure a job that follows different paths of execution
 * based on your own criteria by implementing a +javax.batch.api.Decider+
 *
 * The +javax.batch.api.Decider+ just needs to return a meaningful value, to use in the +myJob.xml+ file to be able to
 * reference the next step that must be executed.
 *
 * include::myJob.xml[]
 *
 * Three Steps and one Decider are configured in the file +myJob.xml+. We start by executing one +step1+ and
 * hand over the control to the Decider, which will execute +step3+, since the Decider is always returning the value
 * +foobar+ which forwards the execution to +step3+.
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class BatchDecisionTest {
    /**
     * In the test, we're just going to invoke the batch execution and wait for completion. To validate the test
     * expected behaviour we need to query +javax.batch.operations.JobOperator#getStepExecutions+ and the
     * +javax.batch.runtime.Metric+ object available in the step execution.
     *
     * @throws Exception
     * 		an exception if the batch could not complete successfully.
     */
    @Test
    public void testBatchDecision() throws Exception {
        JobOperator jobOperator = getJobOperator();
        Long executionId = jobOperator.start("myJob", new Properties());
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(executionId);
        List<String> executedSteps = new ArrayList<>();
        for (StepExecution stepExecution : stepExecutions) {
            executedSteps.add(stepExecution.getStepName());
        }
        // <1> Make sure that only two steps were executed.
        Assert.assertEquals(2, stepExecutions.size());
        // <2> Make sure that only the expected steps were executed an in order.
        Assert.assertArrayEquals(new String[]{ "step1", "step3" }, executedSteps.toArray());
        // <3> Make sure that this step was never executed.
        Assert.assertFalse(executedSteps.contains("step2"));
        // <4> Job should be completed.
        Assert.assertEquals(COMPLETED, jobExecution.getBatchStatus());
    }
}

