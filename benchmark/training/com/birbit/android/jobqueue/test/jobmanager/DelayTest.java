package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DelayTest extends JobManagerTestBase {
    @Test
    public void testDelay() throws Throwable {
        testDelay(false);
    }

    @Test
    public void testDelayPersistent() throws Throwable {
        testDelay(true);
    }
}

