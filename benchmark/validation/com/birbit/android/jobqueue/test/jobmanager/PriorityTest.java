package com.birbit.android.jobqueue.test.jobmanager;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PriorityTest extends JobManagerTestBase {
    private static CountDownLatch priorityRunLatch;

    @Test
    public void testPriority() throws Exception {
        JobManager jobManager = createJobManager(maxConsumerCount(1).timer(mockTimer));
        testPriority(jobManager, false);
    }

    public static class DummyJobWithRunOrderAssert extends Job {
        public static transient AtomicInteger globalRunCount;

        private int expectedRunOrder;

        public DummyJobWithRunOrderAssert(int expectedRunOrder, Params params) {
            super(params.requireNetwork());
            this.expectedRunOrder = expectedRunOrder;
        }

        @Override
        public void onAdded() {
        }

        @Override
        public void onRun() throws Throwable {
            final int cnt = PriorityTest.DummyJobWithRunOrderAssert.globalRunCount.incrementAndGet();
            MatcherAssert.assertThat(expectedRunOrder, CoreMatchers.equalTo(cnt));
            PriorityTest.priorityRunLatch.countDown();
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            return RetryConstraint.CANCEL;
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
        }
    }
}

