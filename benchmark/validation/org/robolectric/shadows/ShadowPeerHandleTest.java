package org.robolectric.shadows;


import android.net.wifi.aware.PeerHandle;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link ShadowPeerHandle}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.O)
public class ShadowPeerHandleTest {
    @Test
    public void canCreatePeerHandleViaNewInstance() throws Exception {
        PeerHandle peerHandle = ShadowPeerHandle.newInstance();
        assertThat(peerHandle).isNotNull();
    }
}

