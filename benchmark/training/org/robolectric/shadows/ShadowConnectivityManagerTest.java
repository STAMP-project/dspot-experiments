package org.robolectric.shadows;


import ConnectivityManager.DEFAULT_NETWORK_PREFERENCE;
import ConnectivityManager.NetworkCallback;
import ConnectivityManager.OnNetworkActiveListener;
import ConnectivityManager.TYPE_MOBILE;
import ConnectivityManager.TYPE_MOBILE_HIPRI;
import ConnectivityManager.TYPE_VPN;
import ConnectivityManager.TYPE_WIFI;
import NetworkCapabilities.NET_CAPABILITY_MMS;
import NetworkInfo.State.CONNECTED;
import NetworkInfo.State.CONNECTING;
import NetworkInfo.State.DISCONNECTED;
import NetworkRequest.Builder;
import Settings.Global;
import Settings.Global.AIRPLANE_MODE_ON;
import ShadowConnectivityManager.NET_ID_WIFI;
import TelephonyManager.NETWORK_TYPE_EDGE;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;


@RunWith(AndroidJUnit4.class)
public class ShadowConnectivityManagerTest {
    private ConnectivityManager connectivityManager;

    private ShadowNetworkInfo shadowOfActiveNetworkInfo;

    @Test
    public void getActiveNetworkInfo_shouldInitializeItself() {
        assertThat(connectivityManager.getActiveNetworkInfo()).isNotNull();
    }

    @Test
    public void getActiveNetworkInfo_shouldReturnTrueCorrectly() {
        shadowOfActiveNetworkInfo.setConnectionStatus(CONNECTED);
        assertThat(connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()).isTrue();
        assertThat(connectivityManager.getActiveNetworkInfo().isConnected()).isTrue();
        shadowOfActiveNetworkInfo.setConnectionStatus(CONNECTING);
        assertThat(connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()).isTrue();
        assertThat(connectivityManager.getActiveNetworkInfo().isConnected()).isFalse();
        shadowOfActiveNetworkInfo.setConnectionStatus(DISCONNECTED);
        assertThat(connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()).isFalse();
        assertThat(connectivityManager.getActiveNetworkInfo().isConnected()).isFalse();
    }

    @Test
    public void getNetworkInfo_shouldReturnDefaultNetworks() throws Exception {
        NetworkInfo wifi = connectivityManager.getNetworkInfo(TYPE_WIFI);
        assertThat(wifi.getDetailedState()).isEqualTo(NetworkInfo.DetailedState.DISCONNECTED);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(TYPE_MOBILE);
        assertThat(mobile.getDetailedState()).isEqualTo(NetworkInfo.DetailedState.CONNECTED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getNetworkInfo_shouldReturnSomeForAllNetworks() throws Exception {
        Network[] allNetworks = connectivityManager.getAllNetworks();
        for (Network network : allNetworks) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            assertThat(networkInfo).isNotNull();
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getNetworkInfo_shouldReturnAddedNetwork() throws Exception {
        Network vpnNetwork = ShadowNetwork.newInstance(123);
        NetworkInfo vpnNetworkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED, TYPE_VPN, 0, true, CONNECTED);
        Shadows.shadowOf(connectivityManager).addNetwork(vpnNetwork, vpnNetworkInfo);
        NetworkInfo returnedNetworkInfo = connectivityManager.getNetworkInfo(vpnNetwork);
        assertThat(returnedNetworkInfo).isSameAs(vpnNetworkInfo);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getNetworkInfo_shouldNotReturnRemovedNetwork() throws Exception {
        Network wifiNetwork = ShadowNetwork.newInstance(NET_ID_WIFI);
        Shadows.shadowOf(connectivityManager).removeNetwork(wifiNetwork);
        NetworkInfo returnedNetworkInfo = connectivityManager.getNetworkInfo(wifiNetwork);
        assertThat(returnedNetworkInfo).isNull();
    }

    @Test
    public void setConnectionType_shouldReturnTypeCorrectly() {
        shadowOfActiveNetworkInfo.setConnectionType(TYPE_MOBILE);
        assertThat(shadowOfActiveNetworkInfo.getType()).isEqualTo(TYPE_MOBILE);
        shadowOfActiveNetworkInfo.setConnectionType(TYPE_WIFI);
        assertThat(shadowOfActiveNetworkInfo.getType()).isEqualTo(TYPE_WIFI);
    }

    @Test
    public void shouldGetAndSetBackgroundDataSetting() throws Exception {
        assertThat(connectivityManager.getBackgroundDataSetting()).isFalse();
        Shadows.shadowOf(connectivityManager).setBackgroundDataSetting(true);
        assertThat(connectivityManager.getBackgroundDataSetting()).isTrue();
    }

    @Test
    public void setActiveNetworkInfo_shouldSetActiveNetworkInfo() throws Exception {
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(null);
        assertThat(connectivityManager.getActiveNetworkInfo()).isNull();
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, TYPE_MOBILE_HIPRI, NETWORK_TYPE_EDGE, true, DISCONNECTED));
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        assertThat(info.getType()).isEqualTo(TYPE_MOBILE_HIPRI);
        assertThat(info.getSubtype()).isEqualTo(NETWORK_TYPE_EDGE);
        assertThat(info.isAvailable()).isTrue();
        assertThat(info.isConnected()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getActiveNetwork_shouldInitializeItself() {
        assertThat(connectivityManager.getActiveNetwork()).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getActiveNetwork_nullIfNetworkNotActive() {
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(false);
        assertThat(connectivityManager.getActiveNetwork()).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void setActiveNetworkInfo_shouldSetActiveNetwork() throws Exception {
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(null);
        assertThat(connectivityManager.getActiveNetworkInfo()).isNull();
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, TYPE_MOBILE_HIPRI, NETWORK_TYPE_EDGE, true, DISCONNECTED));
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        assertThat(info.getType()).isEqualTo(TYPE_MOBILE_HIPRI);
        assertThat(info.getSubtype()).isEqualTo(NETWORK_TYPE_EDGE);
        assertThat(info.isAvailable()).isTrue();
        assertThat(info.isConnected()).isFalse();
        assertThat(Shadows.shadowOf(connectivityManager.getActiveNetwork()).getNetId()).isEqualTo(info.getType());
    }

    @Test
    public void getAllNetworkInfo_shouldReturnAllNetworkInterfaces() throws Exception {
        NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
        assertThat(infos).asList().hasSize(2);
        assertThat(infos).asList().contains(connectivityManager.getActiveNetworkInfo());
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(null);
        assertThat(connectivityManager.getAllNetworkInfo()).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getAllNetworkInfo_shouldEqualGetAllNetworks() throws Exception {
        // Update the active network so that we're no longer in the default state.
        NetworkInfo networkInfo = /* subType */
        /* isAvailable */
        /* isConnected */
        ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED, TYPE_WIFI, 0, true, true);
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(networkInfo);
        // Verify that getAllNetworks and getAllNetworkInfo match.
        Network[] networks = connectivityManager.getAllNetworks();
        NetworkInfo[] networkInfos = new NetworkInfo[networks.length];
        for (int i = 0; i < (networks.length); i++) {
            networkInfos[i] = connectivityManager.getNetworkInfo(networks[i]);
            assertThat(connectivityManager.getAllNetworkInfo()).asList().contains(networkInfos[i]);
        }
        assertThat(networkInfos).hasLength(connectivityManager.getAllNetworkInfo().length);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getAllNetworkInfo_nullIfNetworkNotActive() {
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(false);
        assertThat(connectivityManager.getAllNetworkInfo()).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getAllNetworks_shouldReturnAllNetworks() throws Exception {
        Network[] networks = connectivityManager.getAllNetworks();
        assertThat(networks).asList().hasSize(2);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getAllNetworks_shouldReturnNoNetworksWhenCleared() throws Exception {
        Shadows.shadowOf(connectivityManager).clearAllNetworks();
        Network[] networks = connectivityManager.getAllNetworks();
        assertThat(networks).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getAllNetworks_shouldReturnAddedNetworks() throws Exception {
        // Let's start clear.
        Shadows.shadowOf(connectivityManager).clearAllNetworks();
        // Add a "VPN network".
        Network vpnNetwork = ShadowNetwork.newInstance(123);
        NetworkInfo vpnNetworkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED, TYPE_VPN, 0, true, CONNECTED);
        Shadows.shadowOf(connectivityManager).addNetwork(vpnNetwork, vpnNetworkInfo);
        Network[] networks = connectivityManager.getAllNetworks();
        assertThat(networks).asList().hasSize(1);
        Network returnedNetwork = networks[0];
        assertThat(returnedNetwork).isSameAs(vpnNetwork);
        NetworkInfo returnedNetworkInfo = connectivityManager.getNetworkInfo(returnedNetwork);
        assertThat(returnedNetworkInfo).isSameAs(vpnNetworkInfo);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getAllNetworks_shouldNotReturnRemovedNetworks() throws Exception {
        Network wifiNetwork = ShadowNetwork.newInstance(NET_ID_WIFI);
        Shadows.shadowOf(connectivityManager).removeNetwork(wifiNetwork);
        Network[] networks = connectivityManager.getAllNetworks();
        assertThat(networks).asList().hasSize(1);
        Network returnedNetwork = networks[0];
        ShadowNetwork shadowReturnedNetwork = Shadows.shadowOf(returnedNetwork);
        assertThat(shadowReturnedNetwork.getNetId()).isNotEqualTo(NET_ID_WIFI);
    }

    @Test
    public void getNetworkPreference_shouldGetDefaultValue() throws Exception {
        assertThat(connectivityManager.getNetworkPreference()).isEqualTo(DEFAULT_NETWORK_PREFERENCE);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getReportedNetworkConnectivity() throws Exception {
        Network wifiNetwork = ShadowNetwork.newInstance(NET_ID_WIFI);
        connectivityManager.reportNetworkConnectivity(wifiNetwork, true);
        Map<Network, Boolean> reportedNetworks = Shadows.shadowOf(connectivityManager).getReportedNetworkConnectivity();
        assertThat(reportedNetworks.size()).isEqualTo(1);
        assertThat(reportedNetworks.get(wifiNetwork)).isTrue();
        // Update the status.
        connectivityManager.reportNetworkConnectivity(wifiNetwork, false);
        reportedNetworks = Shadows.shadowOf(connectivityManager).getReportedNetworkConnectivity();
        assertThat(reportedNetworks.size()).isEqualTo(1);
        assertThat(reportedNetworks.get(wifiNetwork)).isFalse();
    }

    @Test
    public void setNetworkPreference_shouldSetDefaultValue() throws Exception {
        connectivityManager.setNetworkPreference(TYPE_MOBILE);
        assertThat(connectivityManager.getNetworkPreference()).isEqualTo(connectivityManager.getNetworkPreference());
        connectivityManager.setNetworkPreference(TYPE_WIFI);
        assertThat(connectivityManager.getNetworkPreference()).isEqualTo(TYPE_WIFI);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getNetworkCallbacks_shouldHaveEmptyDefault() throws Exception {
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCallbacks()).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void requestNetwork_shouldAddCallback() throws Exception {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        ConnectivityManager.NetworkCallback callback = ShadowConnectivityManagerTest.createSimpleCallback();
        connectivityManager.requestNetwork(builder.build(), callback);
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCallbacks()).hasSize(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void registerCallback_shouldAddCallback() throws Exception {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        ConnectivityManager.NetworkCallback callback = ShadowConnectivityManagerTest.createSimpleCallback();
        connectivityManager.registerNetworkCallback(builder.build(), callback);
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCallbacks()).hasSize(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void registerDefaultCallback_shouldAddCallback() throws Exception {
        ConnectivityManager.NetworkCallback callback = ShadowConnectivityManagerTest.createSimpleCallback();
        connectivityManager.registerDefaultNetworkCallback(callback);
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCallbacks()).hasSize(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void unregisterCallback_shouldRemoveCallbacks() throws Exception {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        // Add two different callbacks.
        ConnectivityManager.NetworkCallback callback1 = ShadowConnectivityManagerTest.createSimpleCallback();
        ConnectivityManager.NetworkCallback callback2 = ShadowConnectivityManagerTest.createSimpleCallback();
        connectivityManager.registerNetworkCallback(builder.build(), callback1);
        connectivityManager.registerNetworkCallback(builder.build(), callback2);
        // Remove one at the time.
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCallbacks()).hasSize(2);
        connectivityManager.unregisterNetworkCallback(callback2);
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCallbacks()).hasSize(1);
        connectivityManager.unregisterNetworkCallback(callback1);
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCallbacks()).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void unregisterCallback_shouldNotAllowNullCallback() throws Exception {
        // Verify that exception is thrown.
        connectivityManager.unregisterNetworkCallback(((ConnectivityManager.NetworkCallback) (null)));
    }

    @Test
    public void isActiveNetworkMetered_defaultsToTrue() {
        assertThat(connectivityManager.isActiveNetworkMetered()).isTrue();
    }

    @Test
    public void isActiveNetworkMetered_mobileIsMetered() {
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(connectivityManager.getNetworkInfo(TYPE_MOBILE));
        assertThat(connectivityManager.isActiveNetworkMetered()).isTrue();
    }

    @Test
    public void isActiveNetworkMetered_nonMobileIsUnmetered() {
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(connectivityManager.getNetworkInfo(TYPE_WIFI));
        assertThat(connectivityManager.isActiveNetworkMetered()).isFalse();
    }

    @Test
    public void isActiveNetworkMetered_noActiveNetwork() {
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(null);
        assertThat(connectivityManager.isActiveNetworkMetered()).isFalse();
    }

    @Test
    public void isActiveNetworkMetered_noDefaultNetworkActive() {
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(false);
        assertThat(connectivityManager.isActiveNetworkMetered()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void bindProcessToNetwork_shouldGetBoundNetworkForProcess() {
        Network network = ShadowNetwork.newInstance(789);
        connectivityManager.bindProcessToNetwork(network);
        assertThat(connectivityManager.getBoundNetworkForProcess()).isSameAs(network);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isDefaultNetworkActive_defaultActive() {
        assertThat(Shadows.shadowOf(connectivityManager).isDefaultNetworkActive()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isDefaultNetworkActive_notActive() {
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(false);
        assertThat(Shadows.shadowOf(connectivityManager).isDefaultNetworkActive()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void addDefaultNetworkActiveListener_shouldAddListener() throws Exception {
        ConnectivityManager.OnNetworkActiveListener listener1 = Mockito.spy(ShadowConnectivityManagerTest.createSimpleOnNetworkActiveListener());
        ConnectivityManager.OnNetworkActiveListener listener2 = Mockito.spy(ShadowConnectivityManagerTest.createSimpleOnNetworkActiveListener());
        connectivityManager.addDefaultNetworkActiveListener(listener1);
        connectivityManager.addDefaultNetworkActiveListener(listener2);
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(true);
        Mockito.verify(listener1).onNetworkActive();
        Mockito.verify(listener2).onNetworkActive();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void removeDefaultNetworkActiveListener_shouldRemoveListeners() throws Exception {
        // Add two different callbacks.
        ConnectivityManager.OnNetworkActiveListener listener1 = Mockito.spy(ShadowConnectivityManagerTest.createSimpleOnNetworkActiveListener());
        ConnectivityManager.OnNetworkActiveListener listener2 = Mockito.spy(ShadowConnectivityManagerTest.createSimpleOnNetworkActiveListener());
        connectivityManager.addDefaultNetworkActiveListener(listener1);
        connectivityManager.addDefaultNetworkActiveListener(listener2);
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(true);
        Mockito.verify(listener1).onNetworkActive();
        Mockito.verify(listener2).onNetworkActive();
        // Remove one at the time.
        connectivityManager.removeDefaultNetworkActiveListener(listener2);
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(true);
        Mockito.verify(listener1, Mockito.times(2)).onNetworkActive();
        Mockito.verify(listener2).onNetworkActive();
        connectivityManager.removeDefaultNetworkActiveListener(listener1);
        Shadows.shadowOf(connectivityManager).setDefaultNetworkActive(true);
        Mockito.verify(listener1, Mockito.times(2)).onNetworkActive();
        Mockito.verify(listener2).onNetworkActive();
    }

    @Test(expected = IllegalArgumentException.class)
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void removeDefaultNetworkActiveListener_shouldNotAllowNullListener() throws Exception {
        // Verify that exception is thrown.
        connectivityManager.removeDefaultNetworkActiveListener(null);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getNetworkCapabilities() throws Exception {
        NetworkCapabilities nc = new NetworkCapabilities(null);
        ReflectionHelpers.callInstanceMethod(nc, "addCapability", ClassParameter.from(int.class, NET_CAPABILITY_MMS));
        Shadows.shadowOf(connectivityManager).setNetworkCapabilities(Shadows.shadowOf(connectivityManager).getActiveNetwork(), nc);
        assertThat(Shadows.shadowOf(connectivityManager).getNetworkCapabilities(Shadows.shadowOf(connectivityManager).getActiveNetwork()).hasCapability(NET_CAPABILITY_MMS)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void getCaptivePortalServerUrl_shouldReturnAddedUrl() {
        assertThat(connectivityManager.getCaptivePortalServerUrl()).isEqualTo("http://10.0.0.2");
        Shadows.shadowOf(connectivityManager).setCaptivePortalServerUrl("http://10.0.0.1");
        assertThat(connectivityManager.getCaptivePortalServerUrl()).isEqualTo("http://10.0.0.1");
        Shadows.shadowOf(connectivityManager).setCaptivePortalServerUrl("http://10.0.0.2");
        assertThat(connectivityManager.getCaptivePortalServerUrl()).isEqualTo("http://10.0.0.2");
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void setAirplaneMode() {
        connectivityManager.setAirplaneMode(false);
        assertThat(Global.getInt(ApplicationProvider.getApplicationContext().getContentResolver(), AIRPLANE_MODE_ON, (-1))).isEqualTo(0);
        connectivityManager.setAirplaneMode(true);
        assertThat(Global.getInt(ApplicationProvider.getApplicationContext().getContentResolver(), AIRPLANE_MODE_ON, (-1))).isEqualTo(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getLinkProperties() {
        Network network = Shadows.shadowOf(connectivityManager).getActiveNetwork();
        LinkProperties lp = ReflectionHelpers.callConstructor(LinkProperties.class);
        Shadows.shadowOf(connectivityManager).setLinkProperties(network, lp);
        assertThat(connectivityManager.getLinkProperties(network)).isEqualTo(lp);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getLinkProperties_shouldReturnNull() {
        Network network = Shadows.shadowOf(connectivityManager).getActiveNetwork();
        Shadows.shadowOf(connectivityManager).setLinkProperties(network, null);
        assertThat(connectivityManager.getLinkProperties(network)).isNull();
    }
}

