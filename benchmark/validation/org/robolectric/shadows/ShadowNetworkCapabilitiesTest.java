package org.robolectric.shadows;


import android.net.NetworkCapabilities;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowNetworkCapabilitiesTest {
    @Test
    public void hasTransport_shouldReturnAsPerAssignedTransportTypes() throws Exception {
        NetworkCapabilities networkCapabilities = ShadowNetworkCapabilities.newInstance();
        // Assert default false state.
        assertThat(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).isFalse();
        Shadows.shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        Shadows.shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        assertThat(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).isTrue();
        assertThat(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).isTrue();
        Shadows.shadowOf(networkCapabilities).removeTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        assertThat(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).isFalse();
        assertThat(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).isTrue();
    }
}

