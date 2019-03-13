package org.javaee7.batch.samples.scheduling;


import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.batch.runtime.JobExecution;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * The Batch specification does not offer anything to schedule jobs. However, the Java EE plataform offer a few ways
 * that allow you to schedule Batch jobs.
 *
 * Annotating a method bean with +javax.ejb.Schedule+, it's possible to schedule an execution of a batch job by the
 * specified cron expression in the +javax.ejb.Schedule+ annotation.
 *
 * include::AbstractTimerBatch[]
 *
 * include::MyTimerScheduleBean[]
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class TimerScheduleBatchTest {
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
        MyStepListener.countDownLatch.await(90, TimeUnit.SECONDS);
        Assert.assertEquals(0, MyStepListener.countDownLatch.getCount());
        Assert.assertEquals(3, executedBatchs.size());
        final JobExecution lastExecution = getJobOperator().getJobExecution(executedBatchs.get(2));
        await().atMost(ONE_MINUTE).with().pollInterval(FIVE_HUNDRED_MILLISECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return (lastExecution.getBatchStatus()) != (STARTED);
            }
        });
        for (Long executedBatch : executedBatchs) {
            System.out.println(((("TimerScheduleBatchTest checking batch " + executedBatch) + " batch statuc = ") + (getJobOperator().getJobExecution(executedBatch).getBatchStatus())));
            Assert.assertEquals(COMPLETED, getJobOperator().getJobExecution(executedBatch).getBatchStatus());
        }
    }
}

