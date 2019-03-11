package com.evernote.android.job.util;


import ConnectivityManager.TYPE_MOBILE;
import ConnectivityManager.TYPE_VPN;
import ConnectivityManager.TYPE_WIFI;
import Context.CONNECTIVITY_SERVICE;
import JobRequest.NetworkType.ANY;
import JobRequest.NetworkType.CONNECTED;
import JobRequest.NetworkType.NOT_ROAMING;
import JobRequest.NetworkType.UNMETERED;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.mock.MockContext;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;


/**
 *
 *
 * @author rwondratschek
 */
@FixMethodOrder(MethodSorters.JVM)
@SuppressWarnings("deprecation")
public class DeviceTest {
    @Test
    public void testNetworkStateNotConnectedWithNullNetworkInfo() {
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(ANY);
    }

    @Test
    public void testNetworkStateNotConnected() {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(false);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(ANY);
    }

    @Test
    public void testNetworkStateUnmeteredWifi() {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(true);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        Mockito.when(networkInfo.getType()).thenReturn(TYPE_WIFI);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(UNMETERED);
    }

    @Test
    public void testNetworkStateMeteredNotRoaming() {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(true);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        Mockito.when(networkInfo.getType()).thenReturn(TYPE_MOBILE);
        Mockito.when(networkInfo.isRoaming()).thenReturn(false);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(NOT_ROAMING);
    }

    @Test
    public void testNetworkStateRoaming() {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(true);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        Mockito.when(networkInfo.getType()).thenReturn(TYPE_MOBILE);
        Mockito.when(networkInfo.isRoaming()).thenReturn(true);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(CONNECTED);
    }

    @Test
    public void testNetworkStateWifiAndMobile() {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(true);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        Mockito.when(networkInfo.getType()).thenReturn(TYPE_WIFI);
        Mockito.when(networkInfo.isRoaming()).thenReturn(false);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(UNMETERED);
    }

    @Test
    public void testNetworkStateWifiAndRoaming() {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(true);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        Mockito.when(networkInfo.getType()).thenReturn(TYPE_WIFI);
        Mockito.when(networkInfo.isRoaming()).thenReturn(true);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(UNMETERED);
    }

    @Test
    public void testNetworkStateVpn() {
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(true);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        Mockito.when(networkInfo.getType()).thenReturn(TYPE_VPN);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(NOT_ROAMING);
    }

    @Test
    public void testPlatformBug() {
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenThrow(new NullPointerException());
        Context context = Mockito.mock(MockContext.class);
        Mockito.when(context.getSystemService(CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        assertThat(Device.getNetworkType(context)).isEqualTo(ANY);
    }
}

