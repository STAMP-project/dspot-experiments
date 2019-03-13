package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobHolder;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SessionIdTest extends JobManagerTestBase {
    @Test
    public void testSessionId() throws Throwable {
        JobManager jobManager = createJobManager();
        Long sessionId = mockTimer.nanoTime();// we know job manager uses this value :/

        jobManager.stop();
        Job[] jobs = new Job[]{ new DummyJob(new Params(0)), new DummyJob(new Params(0).persist()) };
        for (Job job : jobs) {
            jobManager.addJob(job);
        }
        for (int i = 0; i < (jobs.length); i++) {
            JobHolder jobHolder = nextJob(jobManager);
            MatcherAssert.assertThat(("session id should be correct for job " + i), jobHolder.getRunningSessionId(), CoreMatchers.equalTo(sessionId));
        }
    }
}

