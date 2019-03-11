package com.birbit.android.jobqueue.test.jobmanager;


import TagConstraint.ALL;
import TagConstraint.ANY;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CancelWithNetworkToggleTest extends JobManagerTestBase {
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
    public void testCancelAnyAsyncWithoutNetworAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(true, ANY);
    }

    @Test
    public void testCancelAnySyncWithoutNetworAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(false, ANY);
    }

    @Test
    public void testCancelAllAsyncWithoutNetworAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(true, ALL);
    }

    @Test
    public void testCancelAllSyncWithoutNetworAndPersistent() throws InterruptedException {
        testCancelWithoutNetwork(false, ALL);
    }

    static CountDownLatch[] persistentLatches = new CountDownLatch[]{ new CountDownLatch(1), new CountDownLatch(1), new CountDownLatch(1), new CountDownLatch(1) };

    static int latchCounter = 0;

    public static class PersistentDummyJob extends DummyJob {
        final int latch;

        public PersistentDummyJob(Params params, int latch) {
            super(params.persist());
            this.latch = latch;
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            CancelWithNetworkToggleTest.persistentLatches[latch].countDown();
        }
    }
}

