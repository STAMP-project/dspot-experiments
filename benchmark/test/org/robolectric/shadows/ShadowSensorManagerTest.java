package org.robolectric.shadows;


import Build.VERSION_CODES;
import Sensor.TYPE_ACCELEROMETER;
import SensorManager.SENSOR_ACCELEROMETER;
import SensorManager.SENSOR_DELAY_NORMAL;
import android.hardware.Sensor;
import android.hardware.SensorDirectChannel;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.MemoryFile;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowSensorManagerTest {
    private SensorManager sensorManager;

    private ShadowSensorManager shadow;

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void createDirectChannel() throws Exception {
        SensorDirectChannel channel = ((SensorDirectChannel) (sensorManager.createDirectChannel(new MemoryFile("name", 10))));
        assertThat(channel.isValid()).isTrue();
        channel.close();
        assertThat(channel.isValid()).isFalse();
    }

    @Test
    public void shouldReturnHasListenerAfterRegisteringListener() {
        SensorEventListener listener = registerListener();
        assertThat(shadow.hasListener(listener)).isTrue();
    }

    @Test
    public void shouldReturnHasNoListenerAfterUnregisterListener() {
        SensorEventListener listener = registerListener();
        sensorManager.unregisterListener(listener, sensorManager.getDefaultSensor(SENSOR_ACCELEROMETER));
        assertThat(shadow.hasListener(listener)).isFalse();
    }

    @Test
    public void shouldReturnHasNoListenerAfterUnregisterListenerWithoutSpecificSensor() {
        SensorEventListener listener = registerListener();
        sensorManager.unregisterListener(listener);
        assertThat(shadow.hasListener(listener)).isFalse();
    }

    @Test
    public void shouldReturnHasNoListenerByDefault() {
        SensorEventListener listener = new ShadowSensorManagerTest.TestSensorEventListener();
        assertThat(shadow.hasListener(listener)).isFalse();
    }

    @Test
    public void shouldSendSensorEventToSingleRegisteredListener() {
        ShadowSensorManagerTest.TestSensorEventListener listener = new ShadowSensorManagerTest.TestSensorEventListener();
        Sensor sensor = sensorManager.getDefaultSensor(SENSOR_ACCELEROMETER);
        sensorManager.registerListener(listener, sensor, SENSOR_DELAY_NORMAL);
        SensorEvent event = shadow.createSensorEvent();
        // Confirm that the listener has received no events yet.
        assertThat(listener.getLatestSensorEvent()).isAbsent();
        shadow.sendSensorEventToListeners(event);
        assertThat(listener.getLatestSensorEvent().get()).isEqualTo(event);
    }

    @Test
    public void shouldSendSensorEventToMultipleRegisteredListeners() {
        ShadowSensorManagerTest.TestSensorEventListener listener1 = new ShadowSensorManagerTest.TestSensorEventListener();
        ShadowSensorManagerTest.TestSensorEventListener listener2 = new ShadowSensorManagerTest.TestSensorEventListener();
        Sensor sensor = sensorManager.getDefaultSensor(SENSOR_ACCELEROMETER);
        sensorManager.registerListener(listener1, sensor, SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener2, sensor, SENSOR_DELAY_NORMAL);
        SensorEvent event = shadow.createSensorEvent();
        shadow.sendSensorEventToListeners(event);
        assertThat(listener1.getLatestSensorEvent().get()).isEqualTo(event);
        assertThat(listener2.getLatestSensorEvent().get()).isEqualTo(event);
    }

    @Test
    public void shouldNotSendSensorEventIfNoRegisteredListeners() {
        // Create a listener but don't register it.
        ShadowSensorManagerTest.TestSensorEventListener listener = new ShadowSensorManagerTest.TestSensorEventListener();
        Sensor sensor = sensorManager.getDefaultSensor(SENSOR_ACCELEROMETER);
        SensorEvent event = shadow.createSensorEvent();
        shadow.sendSensorEventToListeners(event);
        assertThat(listener.getLatestSensorEvent()).isAbsent();
    }

    @Test
    public void shouldCreateSensorEvent() {
        assertThat(((shadow.createSensorEvent()) instanceof SensorEvent)).isTrue();
    }

    @Test
    public void shouldCreateSensorEventWithValueArray() {
        SensorEvent event = shadow.createSensorEvent(3);
        assertThat(event.values.length).isEqualTo(3);
    }

    @Test
    public void createSensorEvent_shouldThrowExceptionWhenValueLessThan1() {
        try {
            /* valueArraySize= */
            shadow.createSensorEvent(0);
            Assert.fail("Expected IllegalArgumentException not thrown");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void getSensor_shouldBeConfigurable() {
        Sensor sensor = ShadowSensor.newInstance(TYPE_ACCELEROMETER);
        Shadows.shadowOf(sensorManager).addSensor(sensor);
        assertThat(sensor).isSameAs(sensorManager.getDefaultSensor(TYPE_ACCELEROMETER));
    }

    @Test
    public void shouldReturnASensorList() throws Exception {
        assertThat(sensorManager.getSensorList(0)).isNotNull();
    }

    private static class TestSensorEventListener implements SensorEventListener {
        private Optional<SensorEvent> latestSensorEvent = Optional.absent();

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            latestSensorEvent = Optional.of(event);
        }

        public Optional<SensorEvent> getLatestSensorEvent() {
            return latestSensorEvent;
        }
    }
}

