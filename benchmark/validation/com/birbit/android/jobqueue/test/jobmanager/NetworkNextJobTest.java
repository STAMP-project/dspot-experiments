package com.birbit.android.jobqueue.test.jobmanager;


import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobHolder;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NetworkNextJobTest extends JobManagerTestBase {
    @Test
    public void testNetworkNextJob() throws Throwable {
        JobManagerTestBase.DummyNetworkUtil dummyNetworkUtil = new JobManagerTestBase.DummyNetworkUtil();
        JobManager jobManager = createJobManager(networkUtil(dummyNetworkUtil).timer(mockTimer));
        jobManager.stop();
        DummyJob dummyJob = new DummyJob(new Params(0).requireNetwork());
        jobManager.addJob(dummyJob);
        dummyNetworkUtil.setNetworkStatus(DISCONNECTED);
        MatcherAssert.assertThat("when there isn't any network, next job should return null", nextJob(jobManager), CoreMatchers.nullValue());
        MatcherAssert.assertThat("even if there is network, job manager should return correct count", jobManager.count(), CoreMatchers.equalTo(1));
        dummyNetworkUtil.setNetworkStatus(METERED);
        JobHolder retrieved = nextJob(jobManager);
        MatcherAssert.assertThat("when network is recovered, next job should be retrieved", retrieved, CoreMatchers.notNullValue());
    }
}

