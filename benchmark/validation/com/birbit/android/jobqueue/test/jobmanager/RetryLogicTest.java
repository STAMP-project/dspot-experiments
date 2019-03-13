package com.birbit.android.jobqueue.test.jobmanager;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.concurrent.CountDownLatch;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RetryLogicTest extends JobManagerTestBase {
    static RetryLogicTest.RetryProvider retryProvider;

    static boolean canRun;

    static int runCount;

    static CountDownLatch onRunLatch;

    static RetryLogicTest.Callback onRunCallback;

    static RetryLogicTest.CancelCallback onCancelCallback;

    static CountDownLatch cancelLatch;

    static CountDownLatch dummyJobRunLatch;

    @Test
    public void testExponential() {
        MatcherAssert.assertThat("exp 1", RetryConstraint.createExponentialBackoff(1, 10).getNewDelayInMs(), CoreMatchers.is(10L));
        MatcherAssert.assertThat("exp 2", RetryConstraint.createExponentialBackoff(2, 10).getNewDelayInMs(), CoreMatchers.is(20L));
        MatcherAssert.assertThat("exp 3", RetryConstraint.createExponentialBackoff(3, 10).getNewDelayInMs(), CoreMatchers.is(40L));
        MatcherAssert.assertThat("exp 1", RetryConstraint.createExponentialBackoff(1, 5).getNewDelayInMs(), CoreMatchers.is(5L));
        MatcherAssert.assertThat("exp 2", RetryConstraint.createExponentialBackoff(2, 5).getNewDelayInMs(), CoreMatchers.is(10L));
        MatcherAssert.assertThat("exp 3", RetryConstraint.createExponentialBackoff(3, 5).getNewDelayInMs(), CoreMatchers.is(20L));
    }

    @Test
    public void testRunCountPersistent() throws InterruptedException {
        testFirstRunCount(true);
    }

    @Test
    public void testRunCountNonPersistent() throws InterruptedException {
        testFirstRunCount(false);
    }

    @Test
    public void testChangeDelayOfTheGroup() throws InterruptedException {
        testChangeDelayOfTheGroup(null);
    }

    @Test
    public void testChangeDelayOfTheGroupPersistent() throws InterruptedException {
        testChangeDelayOfTheGroup(true);
    }

    @Test
    public void testChangeDelayOfTheGroupNonPersistent() throws InterruptedException {
        testChangeDelayOfTheGroup(false);
    }

    @Test
    public void testChangeDelayPersistent() throws InterruptedException {
        testChangeDelay(true);
    }

    @Test
    public void testChangeDelayNonPersistent() throws InterruptedException {
        testChangeDelay(false);
    }

    @Test
    public void testChangePriorityAndObserveExecutionOrderPersistent() throws InterruptedException {
        testChangePriorityAndObserveExecutionOrder(true);
    }

    @Test
    public void testChangePriorityAndObserveExecutionOrderNonPersistent() throws InterruptedException {
        testChangePriorityAndObserveExecutionOrder(false);
    }

    @Test
    public void testChangePriorityPersistent() throws InterruptedException {
        testChangePriority(true);
    }

    @Test
    public void testChangePriorityNonPersistent() throws InterruptedException {
        testChangePriority(false);
    }

    @Test
    public void testCancelPersistent() throws InterruptedException {
        testCancel(true);
    }

    @Test
    public void testCancelNonPersistent() throws InterruptedException {
        testCancel(false);
    }

    @Test
    public void retryPersistent() throws InterruptedException {
        testRetry(true, true);
    }

    @Test
    public void retryNonPersistent() throws InterruptedException {
        testRetry(false, true);
    }

    @Test
    public void retryPersistentWithNull() throws InterruptedException {
        testRetry(true, false);
    }

    @Test
    public void retryNonPersistentWithNull() throws InterruptedException {
        testRetry(false, false);
    }

    public static class RetryJob extends Job {
        int retryLimit = 5;

        String identifier;

        protected RetryJob(Params params) {
            super(params);
        }

        @Override
        public void onAdded() {
        }

        @Override
        public void onRun() throws Throwable {
            MatcherAssert.assertThat("should be allowed to run", RetryLogicTest.canRun, CoreMatchers.is(true));
            if ((RetryLogicTest.onRunCallback) != null) {
                RetryLogicTest.onRunCallback.on(this);
            }
            (RetryLogicTest.runCount)++;
            if ((RetryLogicTest.onRunLatch) != null) {
                RetryLogicTest.onRunLatch.countDown();
            }
            throw new RuntimeException(("i like to fail please " + (identifier)));
        }

        @Override
        public String toString() {
            return ("RETRY_JOB[" + (identifier)) + "]";
        }

        @Override
        protected int getRetryLimit() {
            return retryLimit;
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
            if ((RetryLogicTest.onCancelCallback) != null) {
                RetryLogicTest.onCancelCallback.on(this, cancelReason, throwable);
            }
            RetryLogicTest.cancelLatch.countDown();
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            if ((RetryLogicTest.retryProvider) != null) {
                return RetryLogicTest.retryProvider.build(this, throwable, runCount, maxRunCount);
            }
            return RetryConstraint.createExponentialBackoff(runCount, 1000);
        }
    }

    private static class PersistableDummyJob extends DummyJob {
        public PersistableDummyJob(Params params) {
            super(params);
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            RetryLogicTest.dummyJobRunLatch.countDown();
        }
    }

    interface RetryProvider {
        RetryConstraint build(Job job, Throwable throwable, int runCount, int maxRunCount);
    }

    interface Callback {
        public void on(Job job);
    }

    interface CancelCallback {
        public void on(Job job, @CancelReason
        int cancelReason, @Nullable
        Throwable throwable);
    }
}

