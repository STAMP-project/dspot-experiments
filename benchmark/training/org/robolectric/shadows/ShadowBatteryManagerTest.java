package org.robolectric.shadows;


import android.os.BatteryManager;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowBatteryManagerTest {
    private BatteryManager batteryManager;

    private ShadowBatteryManager shadowBatteryManager;

    private static final int TEST_ID = 123;

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testIsCharging() {
        assertThat(batteryManager.isCharging()).isFalse();
        shadowBatteryManager.setIsCharging(true);
        assertThat(batteryManager.isCharging()).isTrue();
        shadowBatteryManager.setIsCharging(false);
        assertThat(batteryManager.isCharging()).isFalse();
    }

    @Test
    public void testGetIntProperty() {
        assertThat(batteryManager.getIntProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(Integer.MIN_VALUE);
        shadowBatteryManager.setIntProperty(ShadowBatteryManagerTest.TEST_ID, 5);
        assertThat(batteryManager.getIntProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(5);
        shadowBatteryManager.setIntProperty(ShadowBatteryManagerTest.TEST_ID, 0);
        assertThat(batteryManager.getIntProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(0);
        shadowBatteryManager.setIntProperty(ShadowBatteryManagerTest.TEST_ID, Integer.MAX_VALUE);
        assertThat(batteryManager.getIntProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testGetLongProperty() {
        assertThat(batteryManager.getLongProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(Long.MIN_VALUE);
        shadowBatteryManager.setLongProperty(ShadowBatteryManagerTest.TEST_ID, 5L);
        assertThat(batteryManager.getLongProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(5L);
        shadowBatteryManager.setLongProperty(ShadowBatteryManagerTest.TEST_ID, 0);
        assertThat(batteryManager.getLongProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(0);
        shadowBatteryManager.setLongProperty(ShadowBatteryManagerTest.TEST_ID, Long.MAX_VALUE);
        assertThat(batteryManager.getLongProperty(ShadowBatteryManagerTest.TEST_ID)).isEqualTo(Long.MAX_VALUE);
    }
}

