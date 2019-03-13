package org.robolectric.shadows;


import Build.VERSION_CODES;
import Sensor.STRING_TYPE_ACCELEROMETER;
import Sensor.TYPE_ACCELEROMETER;
import android.hardware.Sensor;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Test for {@link ShadowSensor}
 */
@RunWith(AndroidJUnit4.class)
public class ShadowSensorTest {
    @Test
    public void getType() {
        Sensor sensor = ShadowSensor.newInstance(TYPE_ACCELEROMETER);
        assertThat(sensor.getType()).isEqualTo(TYPE_ACCELEROMETER);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getStringType() {
        Sensor sensor = ShadowSensor.newInstance(TYPE_ACCELEROMETER);
        assertThat(sensor.getStringType()).isEqualTo(STRING_TYPE_ACCELEROMETER);
    }
}

