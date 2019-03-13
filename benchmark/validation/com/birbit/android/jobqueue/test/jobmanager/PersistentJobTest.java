package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PersistentJobTest extends JobManagerTestBase {
    // TEST parallel running
    public static CountDownLatch persistentRunLatch = new CountDownLatch(1);

    @Test
    public void testPersistentJob() throws Exception {
        JobManager jobManager = createJobManager();
        jobManager.addJob(new PersistentJobTest.DummyPersistentLatchJob());
        PersistentJobTest.persistentRunLatch.await(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(((int) (PersistentJobTest.persistentRunLatch.getCount())), CoreMatchers.equalTo(0));
    }

    protected static class DummyPersistentLatchJob extends DummyJob {
        public DummyPersistentLatchJob() {
            super(new Params(0).persist());
        }

        @Override
        public void onRun() throws Throwable {
            PersistentJobTest.persistentRunLatch.countDown();
        }
    }
}

