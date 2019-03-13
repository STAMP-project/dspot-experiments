package com.birbit.android.jobqueue.test.jobmanager;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
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
public class SlowOnAddedTest extends JobManagerTestBase {
    @Test
    public void testNonPersistent() throws InterruptedException {
        JobManager jobManager = createJobManager();
        CountDownLatch runLatch = new CountDownLatch(1);
        SlowOnAddedTest.MyDummyJob job = new SlowOnAddedTest.MyDummyJob(new Params(2), runLatch);
        for (int i = 0; i < 50; i++) {
            jobManager.addJob(new DummyJob(new Params(1)));
        }
        jobManager.addJob(job);
        runLatch.await();
        MatcherAssert.assertThat("on added should be called before on run", job.onAddedCntWhenRun, CoreMatchers.equalTo(1));
    }

    @Test
    public void testPersistent() throws InterruptedException {
        JobManager jobManager = createJobManager();
        SlowOnAddedTest.MyDummyPersistentJob.persistentJobLatch = new CountDownLatch(1);
        for (int i = 0; i < 50; i++) {
            jobManager.addJob(new DummyJob(new Params(1).persist()));
        }
        jobManager.addJob(new SlowOnAddedTest.MyDummyPersistentJob(2));
        SlowOnAddedTest.MyDummyPersistentJob.persistentJobLatch.await();
        MatcherAssert.assertThat("even if job is persistent, onAdded should be called b4 onRun", SlowOnAddedTest.MyDummyPersistentJob.onAddedCountWhenOnRun, CoreMatchers.equalTo(1));
    }

    public static class MyDummyPersistentJob extends Job {
        private static CountDownLatch persistentJobLatch;

        private static int persistentOnAdded = 0;

        private static int onAddedCountWhenOnRun = -1;

        protected MyDummyPersistentJob(int priority) {
            super(new Params(priority).persist());
        }

        @Override
        public void onAdded() {
            try {
                // noinspection SLEEP_IN_CODE
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // 
            }
            (SlowOnAddedTest.MyDummyPersistentJob.persistentOnAdded)++;
        }

        @Override
        public void onRun() throws Throwable {
            SlowOnAddedTest.MyDummyPersistentJob.onAddedCountWhenOnRun = SlowOnAddedTest.MyDummyPersistentJob.persistentOnAdded;
            SlowOnAddedTest.MyDummyPersistentJob.persistentJobLatch.countDown();
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            return RetryConstraint.RETRY;
        }
    }

    private static class MyDummyJob extends JobManagerTestBase.DummyLatchJob {
        int onAddedCntWhenRun = -1;

        protected MyDummyJob(Params params, CountDownLatch latch) {
            super(params, latch);
        }

        @Override
        public void onAdded() {
            try {
                // noinspection SLEEP_IN_CODE
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.onAdded();
        }

        @Override
        public void onRun() throws Throwable {
            onAddedCntWhenRun = super.getOnAddedCnt();
            super.onRun();
        }
    }
}

