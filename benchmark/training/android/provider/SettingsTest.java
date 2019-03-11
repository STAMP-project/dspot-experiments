package android.provider;


import android.content.ContentResolver;
import android.provider.Settings.Secure;
import androidx.test.filters.SdkSuppress;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Compatibility test for {@link Settings}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class SettingsTest {
    private ContentResolver contentResolver;

    private boolean gpsProviderStartState;

    private boolean networkProviderStartState;

    private int locationModeStartState;

    @SdkSuppress(maxSdkVersion = JELLY_BEAN_MR2)
    @Config(maxSdk = JELLY_BEAN_MR2)
    @Test
    public void setLocationProviderEnabled() {
        // Verify default values
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isTrue();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isFalse();
        Secure.setLocationProviderEnabled(contentResolver, NETWORK_PROVIDER, true);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isTrue();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isTrue();
        Secure.setLocationProviderEnabled(contentResolver, GPS_PROVIDER, false);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isFalse();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isTrue();
        Secure.setLocationProviderEnabled(contentResolver, NETWORK_PROVIDER, false);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isFalse();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isFalse();
    }

    // TODO(christianw) fix location mode
    @SdkSuppress(minSdkVersion = LOLLIPOP)
    @Config(minSdk = LOLLIPOP, maxSdk = P)
    @Test
    public void contentProviders_affectsLocationMode() {
        // Verify default values
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isTrue();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isFalse();
        Secure.setLocationProviderEnabled(contentResolver, NETWORK_PROVIDER, true);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isTrue();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isTrue();
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_HIGH_ACCURACY);
        Secure.setLocationProviderEnabled(contentResolver, GPS_PROVIDER, false);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isFalse();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isTrue();
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_BATTERY_SAVING);
        Secure.setLocationProviderEnabled(contentResolver, NETWORK_PROVIDER, false);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isFalse();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isFalse();
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_OFF);
    }

    // TODO(christianw) fix location mode
    @SdkSuppress(minSdkVersion = LOLLIPOP)
    @Config(minSdk = LOLLIPOP, maxSdk = P)
    @Test
    public void locationMode_affectsContentProviders() {
        // Verify the default value
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_SENSORS_ONLY);
        // LOCATION_MODE_OFF should set value and disable both content providers
        assertThat(Secure.putInt(contentResolver, Secure.LOCATION_MODE, Secure.LOCATION_MODE_OFF)).isTrue();
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_OFF);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isFalse();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isFalse();
        // LOCATION_MODE_SENSORS_ONLY should set value and enable GPS_PROVIDER
        assertThat(Secure.putInt(contentResolver, Secure.LOCATION_MODE, Secure.LOCATION_MODE_SENSORS_ONLY)).isTrue();
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_SENSORS_ONLY);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isTrue();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isFalse();
        // LOCATION_MODE_BATTERY_SAVING should set value and enable NETWORK_PROVIDER
        assertThat(Secure.putInt(contentResolver, Secure.LOCATION_MODE, Secure.LOCATION_MODE_BATTERY_SAVING)).isTrue();
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_BATTERY_SAVING);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isFalse();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isTrue();
        // LOCATION_MODE_HIGH_ACCURACY should set value and enable both providers
        assertThat(Secure.putInt(contentResolver, Secure.LOCATION_MODE, Secure.LOCATION_MODE_HIGH_ACCURACY)).isTrue();
        assertThat(Secure.getInt(contentResolver, Secure.LOCATION_MODE, (-1))).isEqualTo(Secure.LOCATION_MODE_HIGH_ACCURACY);
        assertThat(Secure.isLocationProviderEnabled(contentResolver, GPS_PROVIDER)).isTrue();
        assertThat(Secure.isLocationProviderEnabled(contentResolver, NETWORK_PROVIDER)).isTrue();
    }
}

