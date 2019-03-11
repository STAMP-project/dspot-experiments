package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReRunWithLimitTest extends JobManagerTestBase {
    @Test
    public void testReRunWithLimit() throws Exception {
        JobManager jobManager = createJobManager();
        testReRun(jobManager, false);
    }

    @Test
    public void testReRunWithLimitPersist() throws Exception {
        JobManager jobManager = createJobManager();
        testReRun(jobManager, true);
    }
}

