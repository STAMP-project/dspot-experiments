package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RunManyNonPersistentTest extends JobManagerTestBase {
    @Test
    public void runManyNonPersistentJobs() throws Exception {
        JobManager jobManager = createJobManager();
        jobManager.stop();
        int limit = 2;
        final CountDownLatch latch = new CountDownLatch(limit);
        for (int i = 0; i < limit; i++) {
            jobManager.addJob(new JobManagerTestBase.DummyLatchJob(new Params(i), latch));
        }
        jobManager.start();
        latch.await(10, TimeUnit.SECONDS);
        MatcherAssert.assertThat(((int) (latch.getCount())), CoreMatchers.equalTo(0));
    }
}

