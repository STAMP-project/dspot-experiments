package com.baeldung.concurrent.prioritytaskexecution;


import org.junit.Test;

import static JobPriority.HIGH;
import static JobPriority.LOW;
import static JobPriority.MEDIUM;


public class PriorityJobSchedulerUnitTest {
    private static int POOL_SIZE = 1;

    private static int QUEUE_SIZE = 10;

    @Test
    public void whenMultiplePriorityJobsQueued_thenHighestPriorityJobIsPicked() {
        Job job1 = new Job("Job1", LOW);
        Job job2 = new Job("Job2", MEDIUM);
        Job job3 = new Job("Job3", HIGH);
        Job job4 = new Job("Job4", MEDIUM);
        Job job5 = new Job("Job5", LOW);
        Job job6 = new Job("Job6", HIGH);
        PriorityJobScheduler pjs = new PriorityJobScheduler(PriorityJobSchedulerUnitTest.POOL_SIZE, PriorityJobSchedulerUnitTest.QUEUE_SIZE);
        pjs.scheduleJob(job1);
        pjs.scheduleJob(job2);
        pjs.scheduleJob(job3);
        pjs.scheduleJob(job4);
        pjs.scheduleJob(job5);
        pjs.scheduleJob(job6);
        // ensure no tasks is pending before closing the scheduler
        while ((pjs.getQueuedTaskCount()) != 0);
        // delay to avoid job sleep (added for demo) being interrupted
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        pjs.closeScheduler();
    }
}

