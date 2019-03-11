package org.robolectric.shadows;


import Configuration.UI_MODE_TYPE_CAR;
import Configuration.UI_MODE_TYPE_NORMAL;
import Configuration.UI_MODE_TYPE_UNDEFINED;
import android.app.UiModeManager;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 *
 */
@RunWith(AndroidJUnit4.class)
@Config(sdk = VERSION_CODES.LOLLIPOP, shadows = ShadowUIModeManager.class)
public class ShadowUIModeManagerTest {
    private UiModeManager uiModeManager;

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testModeSwitch() {
        assertThat(uiModeManager.getCurrentModeType()).isEqualTo(UI_MODE_TYPE_UNDEFINED);
        uiModeManager.enableCarMode(0);
        assertThat(uiModeManager.getCurrentModeType()).isEqualTo(UI_MODE_TYPE_CAR);
        uiModeManager.disableCarMode(0);
        assertThat(uiModeManager.getCurrentModeType()).isEqualTo(UI_MODE_TYPE_NORMAL);
    }
}

