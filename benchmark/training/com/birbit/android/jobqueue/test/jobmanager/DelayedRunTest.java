package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.Job;
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
public class DelayedRunTest extends JobManagerTestBase {
    @Test
    public void testDelayedRun() throws Exception {
        delayedRunTest(false, false);
    }

    @Test
    public void testDelayedRunPersist() throws Exception {
        delayedRunTest(true, false);
    }

    @Test
    public void testDelayedRunTryToStop() throws Exception {
        delayedRunTest(false, true);
    }

    @Test
    public void testDelayedRunPersistAndTryToStop() throws Exception {
        delayedRunTest(true, true);
    }

    @Test
    public void testDelayWith0Consumers() throws InterruptedException {
        JobManager jobManager = createJobManager(minConsumerCount(0).maxConsumerCount(3).timer(mockTimer));
        final CountDownLatch latch = new CountDownLatch(1);
        final DummyJob dummyJob = new DummyJob(new Params(0).delayInMs(2000)) {
            @Override
            public void onRun() throws Throwable {
                super.onRun();
                latch.countDown();
            }
        };
        jobManager.addJob(dummyJob);
        mockTimer.incrementMs(1999);
        MatcherAssert.assertThat("there should not be any ready jobs", jobManager.countReadyJobs(), CoreMatchers.is(0));
        waitUntilAJobIsDone(jobManager, new JobManagerTestBase.WaitUntilCallback() {
            @Override
            public void run() {
                mockTimer.incrementMs(1002);
            }

            @Override
            public void assertJob(Job job) {
                MatcherAssert.assertThat("should be the dummy job", job, CoreMatchers.is(((Job) (dummyJob))));
            }
        });
        MatcherAssert.assertThat("job should run in 3 seconds", latch.await(3, TimeUnit.NANOSECONDS), CoreMatchers.is(true));
    }
}

