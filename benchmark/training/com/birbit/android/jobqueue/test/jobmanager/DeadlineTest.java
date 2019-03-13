package com.birbit.android.jobqueue.test.jobmanager;


import CancelReason.REACHED_DEADLINE;
import NetworkUtil.DISCONNECTED;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DeadlineTest extends JobManagerTestBase {
    private final boolean persistent;

    private final boolean reqNetwork;

    private final boolean reqUnmeteredNetwork;

    private final long delay;

    private final boolean cancelOnDeadline;

    public DeadlineTest(boolean persistent, boolean reqNetwork, boolean reqUnmeteredNetwork, long delay, boolean cancelOnDeadline) {
        this.persistent = persistent;
        this.reqNetwork = reqNetwork;
        this.reqUnmeteredNetwork = reqUnmeteredNetwork;
        this.delay = delay;
        this.cancelOnDeadline = cancelOnDeadline;
    }

    @Test
    public void deadlineTest() throws Exception {
        JobManagerTestBase.DummyNetworkUtil networkUtil = new JobManagerTestBase.DummyNetworkUtilWithConnectivityEventSupport();
        JobManager jobManager = createJobManager(networkUtil(networkUtil).timer(mockTimer));
        networkUtil.setNetworkStatus(DISCONNECTED);
        Params params = new Params(0).setPersistent(persistent).setRequiresNetwork(reqNetwork).setRequiresUnmeteredNetwork(reqUnmeteredNetwork).delayInMs(delay);
        if (cancelOnDeadline) {
            params.overrideDeadlineToCancelInMs(200);
        } else {
            params.overrideDeadlineToRunInMs(200);
        }
        DeadlineTest.DeadlineJob job = new DeadlineTest.DeadlineJob(params);
        jobManager.addJob(job);
        if ((!(reqNetwork)) && (!(reqUnmeteredNetwork))) {
            if ((delay) > 0) {
                MatcherAssert.assertThat(DeadlineTest.DeadlineJob.runLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(false));
                MatcherAssert.assertThat(DeadlineTest.DeadlineJob.cancelLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(false));
                mockTimer.incrementMs(delay);
            }
            // no network is required, it should run
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.runLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(true));
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.cancelLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(false));
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.wasDeadlineReached, CoreMatchers.is(false));
            return;
        }
        MatcherAssert.assertThat(DeadlineTest.DeadlineJob.runLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(false));
        MatcherAssert.assertThat(DeadlineTest.DeadlineJob.cancelLatch.await(1, TimeUnit.MILLISECONDS), CoreMatchers.is(false));
        mockTimer.incrementMs(200);
        if (cancelOnDeadline) {
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.cancelLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(true));
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.runLatch.await(1, TimeUnit.SECONDS), CoreMatchers.is(false));
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.cancelReason, CoreMatchers.is(REACHED_DEADLINE));
        } else {
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.runLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(true));
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.cancelLatch.await(1, TimeUnit.SECONDS), CoreMatchers.is(false));
            MatcherAssert.assertThat(DeadlineTest.DeadlineJob.cancelReason, CoreMatchers.is((-1)));
        }
        MatcherAssert.assertThat(DeadlineTest.DeadlineJob.wasDeadlineReached, CoreMatchers.is(true));
    }

    public static class DeadlineJob extends Job {
        static CountDownLatch runLatch;

        static CountDownLatch cancelLatch;

        static boolean wasDeadlineReached;

        static int cancelReason;

        static {
            DeadlineTest.DeadlineJob.clear();
        }

        static void clear() {
            DeadlineTest.DeadlineJob.runLatch = new CountDownLatch(1);
            DeadlineTest.DeadlineJob.cancelLatch = new CountDownLatch(1);
            DeadlineTest.DeadlineJob.cancelReason = -1;// just a dummy one

            DeadlineTest.DeadlineJob.wasDeadlineReached = false;
        }

        protected DeadlineJob(Params params) {
            super(params);
        }

        @Override
        public void onAdded() {
        }

        @Override
        public void onRun() throws Throwable {
            DeadlineTest.DeadlineJob.wasDeadlineReached = isDeadlineReached();
            DeadlineTest.DeadlineJob.runLatch.countDown();
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
            DeadlineTest.DeadlineJob.cancelReason = cancelReason;
            DeadlineTest.DeadlineJob.wasDeadlineReached = isDeadlineReached();
            DeadlineTest.DeadlineJob.cancelLatch.countDown();
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            return RetryConstraint.CANCEL;
        }
    }
}

