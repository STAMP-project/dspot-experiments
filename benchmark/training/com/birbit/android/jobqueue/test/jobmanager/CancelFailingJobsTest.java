package com.birbit.android.jobqueue.test.jobmanager;


import RuntimeEnvironment.application;
import TagConstraint.ALL;
import TagConstraint.ANY;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.network.NetworkUtil;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CancelFailingJobsTest extends JobManagerTestBase {
    static JobManagerTestBase.DummyNetworkUtilWithConnectivityEventSupport networkUtil = new JobManagerTestBase.DummyNetworkUtilWithConnectivityEventSupport();

    @Test
    public void testCancelAnyAsyncWithoutNetwork() throws InterruptedException {
        testCancelWithoutNetwork(true, ANY);
    }

    @Test
    public void testCancelAnySyncWithoutNetwork() throws InterruptedException {
        testCancelWithoutNetwork(false, ANY);
    }

    @Test
    public void testCancelAllAsyncWithoutNetwork() throws InterruptedException {
        testCancelWithoutNetwork(true, ALL);
    }

    @Test
    public void testCancelAllSyncWithoutNetwork() throws InterruptedException {
        testCancelWithoutNetwork(false, ALL);
    }

    @Test
    public void testCancelAnyAsyncWithoutNetworkAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(true, ANY);
    }

    @Test
    public void testCancelAnySyncWithoutNetworkAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(false, ANY);
    }

    @Test
    public void testCancelAllAsyncWithoutNetworkAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(true, ALL);
    }

    @Test
    public void testCancelAllSyncWithoutNetworkAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(false, ALL);
    }

    static CountDownLatch[] persistentLatches = new CountDownLatch[]{ new CountDownLatch(1), new CountDownLatch(1), new CountDownLatch(1), new CountDownLatch(1) };

    static int latchCounter = 0;

    public static class FailingJob extends DummyJob {
        public FailingJob(Params params) {
            super(params);
        }

        @Override
        public int getShouldReRunOnThrowableCnt() {
            return 20;
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            if ((CancelFailingJobsTest.networkUtil.getNetworkStatus(application)) == (NetworkUtil.DISCONNECTED)) {
                // noinspection SLEEP_IN_CODE
                Thread.sleep(((getCurrentRunCount()) * 200));
                throw new RuntimeException("I'm bad, i crash!");
            }
        }
    }

    public static class PersistentDummyJob extends DummyJob {
        final int latch;

        public PersistentDummyJob(Params params, int latch) {
            super(params.persist());
            this.latch = latch;
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            if ((CancelFailingJobsTest.networkUtil.getNetworkStatus(application)) == (NetworkUtil.DISCONNECTED)) {
                // noinspection SLEEP_IN_CODE
                Thread.sleep(((getCurrentRunCount()) * 200));
                throw new RuntimeException("I'm bad, i crash!");
            }
            CancelFailingJobsTest.persistentLatches[latch].countDown();
        }
    }
}

