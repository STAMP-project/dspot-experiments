package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.telephony.euicc.EuiccManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Junit test for {@link ShadowEuiccManager}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.P)
public class ShadowEuiccManagerTest {
    private EuiccManager euiccManager;

    @Test
    public void isEnabled() {
        Shadows.shadowOf(euiccManager).setIsEnabled(true);
        assertThat(euiccManager.isEnabled()).isTrue();
    }

    @Test
    public void isEnabled_whenSetToFalse() {
        Shadows.shadowOf(euiccManager).setIsEnabled(false);
        assertThat(euiccManager.isEnabled()).isFalse();
    }

    @Test
    public void getEid() {
        String eid = "testEid";
        Shadows.shadowOf(euiccManager).setEid(eid);
        assertThat(euiccManager.getEid()).isEqualTo(eid);
    }
}

