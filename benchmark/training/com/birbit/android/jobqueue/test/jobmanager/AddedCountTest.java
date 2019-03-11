package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AddedCountTest extends JobManagerTestBase {
    @Test
    public void testAddedCount() throws Exception {
        testAddedCount(new DummyJob(new Params(0)));
    }

    @Test
    public void testAddedCountPersistent() {
        testAddedCount(new DummyJob(new Params(0).persist()));
    }
}

