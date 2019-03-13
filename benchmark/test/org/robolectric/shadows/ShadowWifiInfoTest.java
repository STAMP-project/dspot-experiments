package org.robolectric.shadows;


import SupplicantState.COMPLETED;
import SupplicantState.DISCONNECTED;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.net.InetAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowWifiInfoTest {
    private WifiManager wifiManager;

    @Test
    public void newInstance_shouldNotCrash() throws Exception {
        assertThat(ShadowWifiInfo.newInstance()).isNotNull();
    }

    @Test
    public void shouldReturnIpAddress() throws Exception {
        String ipAddress = "192.168.0.1";
        int expectedIpAddress = 16820416;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Shadows.shadowOf(wifiInfo).setInetAddress(InetAddress.getByName(ipAddress));
        assertThat(wifiInfo.getIpAddress()).isEqualTo(expectedIpAddress);
    }

    @Test
    public void shouldReturnMacAddress() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Shadows.shadowOf(wifiInfo).setMacAddress("mac address");
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getMacAddress()).isEqualTo("mac address");
    }

    @Test
    public void shouldReturnSSID() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Shadows.shadowOf(wifiInfo).setSSID("SSID");
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getSSID()).contains("SSID");
    }

    @Test
    public void shouldReturnBSSID() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getBSSID()).isEqualTo(null);
        Shadows.shadowOf(wifiInfo).setBSSID("BSSID");
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getBSSID()).isEqualTo("BSSID");
    }

    @Test
    public void shouldReturnRssi() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Shadows.shadowOf(wifiInfo).setRssi(10);
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getRssi()).isEqualTo(10);
    }

    @Test
    public void shouldReturnLinkSpeed() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getLinkSpeed()).isEqualTo((-1));
        Shadows.shadowOf(wifiInfo).setLinkSpeed(10);
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getLinkSpeed()).isEqualTo(10);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void shouldReturnFrequency() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getFrequency()).isEqualTo((-1));
        Shadows.shadowOf(wifiInfo).setFrequency(10);
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getFrequency()).isEqualTo(10);
    }

    @Test
    public void shouldReturnNetworkId() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getNetworkId()).isEqualTo((-1));
        Shadows.shadowOf(wifiInfo).setNetworkId(10);
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getNetworkId()).isEqualTo(10);
    }

    @Test
    public void shouldReturnSupplicantState() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Shadows.shadowOf(wifiInfo).setSupplicantState(COMPLETED);
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getSupplicantState()).isEqualTo(COMPLETED);
        Shadows.shadowOf(wifiInfo).setSupplicantState(DISCONNECTED);
        wifiInfo = wifiManager.getConnectionInfo();
        assertThat(wifiInfo.getSupplicantState()).isEqualTo(DISCONNECTED);
    }
}

