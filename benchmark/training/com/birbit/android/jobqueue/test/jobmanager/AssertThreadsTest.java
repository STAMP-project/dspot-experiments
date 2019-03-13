package com.birbit.android.jobqueue.test.jobmanager;


import TagConstraint.ANY;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AssertThreadsTest extends JobManagerTestBase {
    JobManager jobManager;

    @Test
    public void testGetActiveConsumerCount() throws InterruptedException {
        assertFailure(new Runnable() {
            @Override
            public void run() {
                jobManager.getActiveConsumerCount();
            }
        });
    }

    @Test
    public void testAddJob() throws InterruptedException {
        assertFailure(new Runnable() {
            @Override
            public void run() {
                jobManager.addJob(new DummyJob(new Params(0)));
            }
        });
    }

    @Test
    public void testCancelJobs() throws InterruptedException {
        assertFailure(new Runnable() {
            @Override
            public void run() {
                jobManager.cancelJobs(ANY, "blah");
            }
        });
    }

    @Test
    public void testCount() throws InterruptedException {
        assertFailure(new Runnable() {
            @Override
            public void run() {
                jobManager.count();
            }
        });
    }

    @Test
    public void testCountReady() throws InterruptedException {
        assertFailure(new Runnable() {
            @Override
            public void run() {
                jobManager.countReadyJobs();
            }
        });
    }

    @Test
    public void testJobStatus() throws InterruptedException {
        assertFailure(new Runnable() {
            @Override
            public void run() {
                jobManager.getJobStatus("abc");
            }
        });
    }

    @Test
    public void testClear() throws InterruptedException {
        assertFailure(new Runnable() {
            @Override
            public void run() {
                jobManager.clear();
            }
        });
    }
}

