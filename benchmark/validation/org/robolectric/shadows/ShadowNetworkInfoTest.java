package org.robolectric.shadows;


import NetworkInfo.DetailedState.SCANNING;
import android.net.NetworkInfo;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.shadow.api.Shadow;


@RunWith(AndroidJUnit4.class)
public class ShadowNetworkInfoTest {
    @Test
    public void getDetailedState_shouldReturnTheAssignedState() throws Exception {
        NetworkInfo networkInfo = Shadow.newInstanceOf(NetworkInfo.class);
        Shadows.shadowOf(networkInfo).setDetailedState(SCANNING);
        assertThat(networkInfo.getDetailedState()).isEqualTo(SCANNING);
    }
}

