package org.javaee7.batch.samples.scheduling;


import MyJob.executedBatchs;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.batch.runtime.JobExecution;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static MyJob.executedBatchs;


/**
 * The Batch specification does not offer anything to schedule jobs. However, the Java EE plataform offer a few ways
 * that allow you to schedule Batch jobs.
 *
 * Adding a +javax.enterprise.concurrent.Trigger+ to a +javax.enterprise.concurrent.ManagedScheduledExecutorService+
 * is possible to trigger an execution of the batch job by specifying the next execution date of the job.
 *
 * include::MyManagedScheduledBatchBean[]
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class ManagedScheduledBatchTest {
    @Inject
    private MyManagedScheduledBatch managedScheduledBatch;

    /**
     * The batch job is scheduled to execute each 15 seconds. We expect to run the batch instance exactly 3 times as
     * defined in the +CountDownLatch+ object. To validate the test expected behaviour we just need to check the
     * Batch Status in the +javax.batch.runtime.JobExecution+ object. We should get a
     * +javax.batch.runtime.BatchStatus.COMPLETED+ for every execution.
     *
     * @throws Exception
     * 		an exception if the batch could not complete successfully.
     */
    @Test
    public void testTimeScheduleBatch() throws Exception {
        managedScheduledBatch.runJob();
        MyStepListener.countDownLatch.await(90, TimeUnit.SECONDS);
        // If this assert fails it means we've timed out above
        Assert.assertEquals(0, MyStepListener.countDownLatch.getCount());
        Assert.assertEquals(3, executedBatchs.size());
        Thread.sleep(1000L);
        final JobExecution lastExecution = getJobOperator().getJobExecution(executedBatchs.get(2));
        await().atMost(ONE_MINUTE).with().pollInterval(FIVE_HUNDRED_MILLISECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return (lastExecution.getBatchStatus()) != (STARTED);
            }
        });
        for (Long executedBatch : executedBatchs) {
            System.out.println(("ManagedScheduledBatchTest checking completed for batch " + executedBatch));
            Assert.assertEquals(("Outcome equal for batch " + executedBatch), COMPLETED, getJobOperator().getJobExecution(executedBatch).getBatchStatus());
        }
    }
}

