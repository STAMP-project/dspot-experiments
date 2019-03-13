package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Junit test for {@link ShadowCarrierConfigManager}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.M)
public class ShadowCarrierConfigManagerTest {
    private CarrierConfigManager carrierConfigManager;

    private static final int TEST_ID = 123;

    @Test
    public void getConfigForSubId_shouldReturnNonNullValue() throws Exception {
        PersistableBundle persistableBundle = carrierConfigManager.getConfigForSubId((-1));
        assertThat(persistableBundle).isNotNull();
    }

    @Test
    public void testGetConfigForSubId() throws Exception {
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString("key1", "test");
        persistableBundle.putInt("key2", 100);
        persistableBundle.putBoolean("key3", true);
        Shadows.shadowOf(carrierConfigManager).setConfigForSubId(ShadowCarrierConfigManagerTest.TEST_ID, persistableBundle);
        PersistableBundle verifyBundle = carrierConfigManager.getConfigForSubId(ShadowCarrierConfigManagerTest.TEST_ID);
        assertThat(verifyBundle).isNotNull();
        assertThat(verifyBundle.get("key1")).isEqualTo("test");
        assertThat(verifyBundle.getInt("key2")).isEqualTo(100);
        assertThat(verifyBundle.getBoolean("key3")).isTrue();
    }
}

