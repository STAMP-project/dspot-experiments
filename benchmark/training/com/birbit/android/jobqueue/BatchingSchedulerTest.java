package com.birbit.android.jobqueue;


import BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS;
import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import NetworkUtil.UNMETERED;
import com.birbit.android.jobqueue.scheduling.Scheduler;
import com.birbit.android.jobqueue.scheduling.SchedulerConstraint;
import com.birbit.android.jobqueue.test.timer.MockTimer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class BatchingSchedulerTest {
    BatchingScheduler bs;

    Scheduler scheduler;

    MockTimer timer;

    @Test
    public void testCustomDuration() {
        scheduler = Mockito.mock(Scheduler.class);
        timer = new MockTimer();
        bs = new BatchingScheduler(scheduler, timer, 123);
        MatcherAssert.assertThat(bs.batchingDurationInMs, CoreMatchers.is(123L));
        MatcherAssert.assertThat(bs.batchingDurationInNs, CoreMatchers.is(123000000L));
    }

    @Test
    public void testAddOne() {
        SchedulerConstraint constraint = new SchedulerConstraint("abc");
        constraint.setDelayInMs(0);
        constraint.setNetworkStatus(DISCONNECTED);
        bs.request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testAddTwoOfTheSame() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(0)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testAddTwoOfTheSameWithTimeDiff() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        timer.incrementMs(((BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS) - 10));
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(0)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testAddTwoOfTheSameWithDelay() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 100);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(0)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testAddTwoOfTheSameWithDelayWithTimeDiff() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        timer.incrementMs(((BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS) - 101));
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 100);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(0)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testAddTwoOfTheSameWithEnoughDelay() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, DEFAULT_BATCHING_PERIOD_IN_MS);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getDelayInMs(), CoreMatchers.is(((BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS) * 2)));
    }

    @Test
    public void testAddTwoOfTheSameWithEnoughTimeDifference() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        timer.incrementMs(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testSecondWithDeadline() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 0, 10L);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getOverrideDeadlineInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testFirstWithDeadline() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0, 10L);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint.getOverrideDeadlineInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getOverrideDeadlineInMs(), CoreMatchers.nullValue());
    }

    @Test
    public void testTwoWithDeadlinesAndBatch() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0, 10L);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 0, 20L);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(0)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint.getOverrideDeadlineInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testAddTwoOfTheSameWithEnoughDeadline() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0, 0L);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 0, DEFAULT_BATCHING_PERIOD_IN_MS);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint.getOverrideDeadlineInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getOverrideDeadlineInMs(), CoreMatchers.is(((BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS) * 2)));
    }

    @Test
    public void testAddTwoWithDifferentNetwork() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(UNMETERED, 0);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint2);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        MatcherAssert.assertThat(constraint2.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }

    @Test
    public void testAddRemoveThenAddAgainOfTheSame() {
        SchedulerConstraint constraint = BatchingSchedulerTest.createConstraint(METERED, 0);
        bs.request(constraint);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint);
        MatcherAssert.assertThat(constraint.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
        bs.onFinished(constraint, false);
        SchedulerConstraint constraint2 = BatchingSchedulerTest.createConstraint(METERED, 2);
        bs.request(constraint2);
        Mockito.verify(scheduler, Mockito.times(1)).request(constraint2);
        MatcherAssert.assertThat(constraint2.getDelayInMs(), CoreMatchers.is(BatchingScheduler.DEFAULT_BATCHING_PERIOD_IN_MS));
    }
}

