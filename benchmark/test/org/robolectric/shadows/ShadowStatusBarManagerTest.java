package org.robolectric.shadows;


import ShadowStatusBarManager.DEFAULT_DISABLE2_MASK;
import ShadowStatusBarManager.DEFAULT_DISABLE_MASK;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link ShadowStatusBarManager}.
 */
@RunWith(AndroidJUnit4.class)
public final class ShadowStatusBarManagerTest {
    @Test
    public void getDisable() throws ClassNotFoundException {
        ShadowStatusBarManagerTest.callDisableMethodofStatusBarManager(DEFAULT_DISABLE_MASK);
        assertThat(getDisableFlags()).isEqualTo(DEFAULT_DISABLE_MASK);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getDisable2() throws ClassNotFoundException {
        ShadowStatusBarManagerTest.callDisable2MethodofStatusBarManager(DEFAULT_DISABLE2_MASK);
        assertThat(getDisable2Flags()).isEqualTo(DEFAULT_DISABLE2_MASK);
    }
}

