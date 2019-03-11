package com.birbit.android.jobqueue.test.jobmanager;


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
public class SingleIdTest extends JobManagerTestBase {
    @Test
    public void testSingleIdPersistent() throws Throwable {
        testSingleId(true);
    }

    @Test
    public void testSingleIdNonPersistent() throws Throwable {
        testSingleId(false);
    }

    @Test
    public void testSingleIdRunningPersistent() throws Exception {
        testSingleIdRunning(true);
    }

    @Test
    public void testSingleIdRunningNonPersistent() throws Exception {
        testSingleIdRunning(false);
    }

    private static class SerializableDummyTwoLatchJob extends DummyJob {
        static CountDownLatch sLatchWait;

        static CountDownLatch sLatchRunning;

        public SerializableDummyTwoLatchJob(Params params, CountDownLatch latchWait, CountDownLatch latchRunning) {
            super(params);
            SingleIdTest.SerializableDummyTwoLatchJob.sLatchWait = latchWait;
            SingleIdTest.SerializableDummyTwoLatchJob.sLatchRunning = latchRunning;
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            SingleIdTest.SerializableDummyTwoLatchJob.sLatchRunning.countDown();
            SingleIdTest.SerializableDummyTwoLatchJob.sLatchWait.await();
            SingleIdTest.SerializableDummyTwoLatchJob.sLatchRunning = null;
            SingleIdTest.SerializableDummyTwoLatchJob.sLatchWait = null;
        }
    }

    private static class SerializableDummyLatchJob extends DummyJob {
        static CountDownLatch sLatchRunning;

        int onRunCnt = 0;

        public SerializableDummyLatchJob(Params params, CountDownLatch latchRunning) {
            super(params);
            SingleIdTest.SerializableDummyLatchJob.sLatchRunning = latchRunning;
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            SingleIdTest.SerializableDummyLatchJob.sLatchRunning.countDown();
            SingleIdTest.SerializableDummyLatchJob.sLatchRunning = null;
        }
    }
}

