package com.birbit.android.jobqueue.test.jobmanager;


import Build.VERSION_CODES;
import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import NetworkUtil.UNMETERED;
import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.callback.JobManagerCallback;
import com.birbit.android.jobqueue.callback.JobManagerCallbackAdapter;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
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
public class NetworkJobTest extends JobManagerTestBase {
    final boolean unmetered;

    static CountDownLatch persistentDummyJobRunLatch;

    public NetworkJobTest(boolean unmetered) {
        this.unmetered = unmetered;
    }

    @Test
    public void testNetworkJobWithTimeout() throws InterruptedException {
        JobManagerTestBase.DummyNetworkUtil dummyNetworkUtil = new JobManagerTestBase.DummyNetworkUtil();
        dummyNetworkUtil.setNetworkStatus(DISCONNECTED);
        final JobManager jobManager = createJobManager(networkUtil(dummyNetworkUtil).timer(mockTimer));
        final CountDownLatch runLatch = new CountDownLatch(1);
        DummyJob networkDummyJob = new DummyJob(addRequirement(new Params(1), 4)) {
            @Override
            public void onRun() throws Throwable {
                runLatch.countDown();
                super.onRun();
            }
        };
        jobManager.addJob(networkDummyJob);
        MatcherAssert.assertThat("job should not run", runLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(false));
        mockTimer.incrementMs(4);
        MatcherAssert.assertThat("job should run because network wait timed out", runLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    @Test
    public void testPersistentNetworkJobWithTimeout() throws InterruptedException {
        JobManagerTestBase.DummyNetworkUtil dummyNetworkUtil = new JobManagerTestBase.DummyNetworkUtil();
        dummyNetworkUtil.setNetworkStatus(DISCONNECTED);
        final JobManager jobManager = createJobManager(networkUtil(dummyNetworkUtil).timer(mockTimer));
        NetworkJobTest.PersistentDummyJob networkDummyJob = new NetworkJobTest.PersistentDummyJob(addRequirement(new Params(1), 4));
        jobManager.addJob(networkDummyJob);
        MatcherAssert.assertThat("job should not run", NetworkJobTest.persistentDummyJobRunLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(false));
        mockTimer.incrementMs(4);
        MatcherAssert.assertThat("job should run because network wait timed out", NetworkJobTest.persistentDummyJobRunLatch.await(3, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    @TargetApi(VERSION_CODES.GINGERBREAD)
    @Test
    public void testNetworkJob() throws Exception {
        enableDebug();
        JobManagerTestBase.DummyNetworkUtil dummyNetworkUtil = new JobManagerTestBase.DummyNetworkUtil();
        final JobManager jobManager = createJobManager(networkUtil(dummyNetworkUtil).timer(mockTimer));
        jobManager.stop();
        DummyJob networkDummyJob = new DummyJob(addRequirement(new Params(5)));
        jobManager.addJob(networkDummyJob);
        DummyJob noNetworkDummyJob = new DummyJob(new Params(2));
        jobManager.addJob(noNetworkDummyJob);
        DummyJob networkPersistentJob = new DummyJob(addRequirement(new Params(6).persist()));
        jobManager.addJob(networkPersistentJob);
        DummyJob noNetworkPersistentJob = new DummyJob(new Params(1).persist());
        jobManager.addJob(noNetworkPersistentJob);
        MatcherAssert.assertThat("count should be correct if there are network and non-network jobs w/o network", jobManager.count(), CoreMatchers.equalTo(4));
        dummyNetworkUtil.setNetworkStatus(METERED);
        MatcherAssert.assertThat("count should be correct if there is network and non-network jobs w/o network", jobManager.count(), CoreMatchers.equalTo(4));
        dummyNetworkUtil.setNetworkStatus(DISCONNECTED);
        final CountDownLatch noNetworkLatch = new CountDownLatch(2);
        jobManager.addCallback(new JobManagerCallbackAdapter() {
            @Override
            public void onAfterJobRun(@NonNull
            Job job, int resultCode) {
                if (resultCode == (JobManagerCallback.RESULT_SUCCEED)) {
                    MatcherAssert.assertThat("should be a no network job", job.requiresNetwork(), CoreMatchers.is(false));
                    noNetworkLatch.countDown();
                    if ((noNetworkLatch.getCount()) == 0) {
                        jobManager.removeCallback(this);
                    }
                }
            }
        });
        jobManager.start();
        MatcherAssert.assertThat(noNetworkLatch.await(1, TimeUnit.MINUTES), CoreMatchers.is(true));
        MatcherAssert.assertThat("no network jobs should be executed even if there is no network", jobManager.count(), CoreMatchers.equalTo(2));
        final CountDownLatch networkLatch = new CountDownLatch(2);
        jobManager.addCallback(new JobManagerCallbackAdapter() {
            @Override
            public void onAfterJobRun(@NonNull
            Job job, int resultCode) {
                if (resultCode == (JobManagerCallback.RESULT_SUCCEED)) {
                    MatcherAssert.assertThat("should be a network job", job.requiresNetwork(), CoreMatchers.is(true));
                    networkLatch.countDown();
                    if ((networkLatch.getCount()) == 0) {
                        jobManager.removeCallback(this);
                    }
                }
            }
        });
        dummyNetworkUtil.setNetworkStatus(METERED);
        mockTimer.incrementMs(10000);// network check delay, make public?

        if (unmetered) {
            MatcherAssert.assertThat("if jobs require unmetered, they should not be run", networkLatch.await(10, TimeUnit.SECONDS), CoreMatchers.is(false));
            MatcherAssert.assertThat(networkLatch.getCount(), CoreMatchers.is(2L));
            dummyNetworkUtil.setNetworkStatus(UNMETERED);
            mockTimer.incrementMs(10000);// network check delay

        }
        MatcherAssert.assertThat(networkLatch.await(1, TimeUnit.MINUTES), CoreMatchers.is(true));
        MatcherAssert.assertThat("when network is recovered, all network jobs should be automatically consumed", jobManager.count(), CoreMatchers.equalTo(0));
    }

    public static class PersistentDummyJob extends Job {
        public PersistentDummyJob(Params params) {
            super(params.persist());
        }

        @Override
        public void onAdded() {
        }

        @Override
        public void onRun() throws Throwable {
            NetworkJobTest.persistentDummyJobRunLatch.countDown();
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            throw new RuntimeException("not expected arrive here");
        }
    }
}

