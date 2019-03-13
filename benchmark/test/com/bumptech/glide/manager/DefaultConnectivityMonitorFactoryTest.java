package com.bumptech.glide.manager;


import ConnectivityMonitor.ConnectivityListener;
import RuntimeEnvironment.application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DefaultConnectivityMonitorFactoryTest {
    private ConnectivityMonitorFactory factory;

    @Test
    public void testReturnsDefaultConnectivityMonitorWhenHasPermission() {
        ShadowApplication.getInstance().grantPermissions("android.permission.ACCESS_NETWORK_STATE");
        ConnectivityMonitor connectivityMonitor = factory.build(application, Mockito.mock(ConnectivityListener.class));
        assertThat(connectivityMonitor).isInstanceOf(DefaultConnectivityMonitor.class);
    }

    @Test
    public void testReturnsNullConnectivityMonitorWhenDoesNotHavePermission() {
        ConnectivityMonitor connectivityMonitor = factory.build(application, Mockito.mock(ConnectivityListener.class));
        assertThat(connectivityMonitor).isInstanceOf(NullConnectivityMonitor.class);
    }
}

