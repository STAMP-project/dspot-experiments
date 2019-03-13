package com.birbit.android.jobqueue.test.jobmanager;


import com.birbit.android.jobqueue.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class KeepAliveTest extends JobManagerTestBase {
    @Test
    public void testKeepAlive() throws Exception {
        testKeepAlive(new JobManagerTestBase.DummyNetworkUtilWithConnectivityEventSupport());
    }

    @Test
    public void testKeepAliveWithoutNetworkEvents() throws Exception {
        testKeepAlive(new JobManagerTestBase.DummyNetworkUtil());
    }
}

