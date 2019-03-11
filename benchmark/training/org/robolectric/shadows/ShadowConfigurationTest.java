package org.robolectric.shadows;


import Build.VERSION_CODES;
import android.content.res.Configuration;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowConfigurationTest {
    private Configuration configuration;

    @Test
    public void setToDefaultsShouldSetRealDefaults() {
        configuration.setToDefaults();
        assertThat(configuration.fontScale).isEqualTo(1.0F);
        assertThat(configuration.screenLayout).isEqualTo(SCREENLAYOUT_UNDEFINED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testSetLocale() {
        configuration.setLocale(Locale.US);
        assertThat(configuration.locale).isEqualTo(Locale.US);
        configuration.setLocale(Locale.FRANCE);
        assertThat(configuration.locale).isEqualTo(Locale.FRANCE);
    }

    @Test
    public void testConstructCopy() {
        configuration.setToDefaults();
        Configuration clone = new Configuration(configuration);
        assertThat(configuration).isEqualTo(clone);
    }

    @Test
    public void testToString_shouldntExplode() throws Exception {
        assertThat(new Configuration().toString()).contains("mcc");
    }
}

