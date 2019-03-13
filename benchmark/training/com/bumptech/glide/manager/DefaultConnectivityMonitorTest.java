package com.bumptech.glide.manager;


import ConnectivityMonitor.ConnectivityListener;
import Context.CONNECTIVITY_SERVICE;
import NetworkInfo.DetailedState.CONNECTED;
import RuntimeEnvironment.application;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = DefaultConnectivityMonitorTest.PermissionConnectivityManager.class)
public class DefaultConnectivityMonitorTest {
    @Mock
    private ConnectivityListener listener;

    private DefaultConnectivityMonitor monitor;

    private DefaultConnectivityMonitorTest.ConnectivityHarness harness;

    @Test
    public void testRegistersReceiverOnStart() {
        monitor.onStart();
        assertThat(getConnectivityReceivers()).hasSize(1);
    }

    @Test
    public void testDoesNotRegisterTwiceOnStart() {
        monitor.onStart();
        monitor.onStart();
        assertThat(getConnectivityReceivers()).hasSize(1);
    }

    @Test
    public void testUnregistersReceiverOnStop() {
        monitor.onStart();
        monitor.onStop();
        assertThat(getConnectivityReceivers()).isEmpty();
    }

    @Test
    public void testHandlesUnregisteringTwiceInARow() {
        monitor.onStop();
        monitor.onStop();
        assertThat(getConnectivityReceivers()).isEmpty();
    }

    @Test
    public void testDoesNotNotifyListenerIfConnectedAndBecomesConnected() {
        harness.connect();
        monitor.onStart();
        harness.broadcast();
        Mockito.verify(listener, Mockito.never()).onConnectivityChanged(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testNotifiesListenerIfConnectedAndBecomesDisconnected() {
        harness.connect();
        monitor.onStart();
        harness.disconnect();
        harness.broadcast();
        Mockito.verify(listener).onConnectivityChanged(ArgumentMatchers.eq(false));
    }

    @Test
    public void testNotifiesListenerIfDisconnectedAndBecomesConnected() {
        harness.disconnect();
        monitor.onStart();
        harness.connect();
        harness.broadcast();
        Mockito.verify(listener).onConnectivityChanged(ArgumentMatchers.eq(true));
    }

    @Test
    public void testDoesNotNotifyListenerWhenNotRegistered() {
        harness.disconnect();
        monitor.onStart();
        monitor.onStop();
        harness.connect();
        harness.broadcast();
        Mockito.verify(listener, Mockito.never()).onConnectivityChanged(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void register_withMissingPermission_doesNotThrow() {
        harness.shadowConnectivityManager.isNetworkPermissionGranted = false;
        monitor.onStart();
    }

    @Test
    public void onReceive_withMissingPermission_doesNotThrow() {
        monitor.onStart();
        harness.shadowConnectivityManager.isNetworkPermissionGranted = false;
        harness.broadcast();
    }

    @Test
    public void onReceive_withMissingPermission_previouslyDisconnected_notifiesListenersConnected() {
        harness.disconnect();
        monitor.onStart();
        harness.shadowConnectivityManager.isNetworkPermissionGranted = false;
        harness.broadcast();
        Mockito.verify(listener).onConnectivityChanged(true);
    }

    @Test
    public void onReceive_withMissingPermission_previouslyConnected_doesNotNotifyListeners() {
        harness.connect();
        monitor.onStart();
        harness.shadowConnectivityManager.isNetworkPermissionGranted = false;
        harness.broadcast();
        Mockito.verify(listener, Mockito.never()).onConnectivityChanged(ArgumentMatchers.anyBoolean());
    }

    private static class ConnectivityHarness {
        private final DefaultConnectivityMonitorTest.PermissionConnectivityManager shadowConnectivityManager;

        public ConnectivityHarness() {
            ConnectivityManager connectivityManager = ((ConnectivityManager) (application.getSystemService(CONNECTIVITY_SERVICE)));
            shadowConnectivityManager = Shadow.extract(connectivityManager);
        }

        void disconnect() {
            setActiveNetworkInfo(null);
        }

        void connect() {
            NetworkInfo networkInfo = ShadowNetworkInfo.newInstance(CONNECTED, 0, 0, true, true);
            shadowConnectivityManager.setActiveNetworkInfo(networkInfo);
        }

        void broadcast() {
            Intent connected = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
            application.sendBroadcast(connected);
        }
    }

    @Implements(ConnectivityManager.class)
    public static final class PermissionConnectivityManager extends ShadowConnectivityManager {
        private boolean isNetworkPermissionGranted = true;

        @Implementation
        @Override
        public NetworkInfo getActiveNetworkInfo() {
            if (!(isNetworkPermissionGranted)) {
                throw new SecurityException();
            }
            return super.getActiveNetworkInfo();
        }
    }
}

