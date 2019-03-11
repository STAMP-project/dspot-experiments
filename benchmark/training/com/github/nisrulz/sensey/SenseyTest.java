/**
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.nisrulz.sensey;


import android.hardware.Sensor;
import com.github.nisrulz.sensey.FlipDetector.FlipListener;
import com.github.nisrulz.sensey.LightDetector.LightListener;
import com.github.nisrulz.sensey.OrientationDetector.OrientationListener;
import com.github.nisrulz.sensey.ProximityDetector.ProximityListener;
import com.github.nisrulz.sensey.ShakeDetector.ShakeListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSensorManager;


@RunWith(RobolectricTestRunner.class)
public class SenseyTest {
    private Sensey sensey;

    private ShadowSensorManager shadowSensorManager;

    @Test
    public void detectListenerWithStartFlipDetection() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        FlipListener fakeListener = Mockito.mock(FlipListener.class);
        sensey.startFlipDetection(fakeListener);
        FlipDetector detector = getDetector(fakeListener, FlipDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for flip", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be flip detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectListenerWithStartLightDetection() {
        addSensor(Sensor.TYPE_LIGHT);
        LightListener fakeListener = Mockito.mock(LightListener.class);
        sensey.startLightDetection(fakeListener);
        LightDetector detector = getDetector(fakeListener, LightDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for light", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be light detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectListenerWithStartLightDetectionWithCustomThreshold() {
        addSensor(Sensor.TYPE_LIGHT);
        LightListener fakeListener = Mockito.mock(LightListener.class);
        sensey.startLightDetection(4, fakeListener);
        LightDetector detector = getDetector(fakeListener, LightDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for light", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be light detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectListenerWithStartOrientationDetection() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        addSensor(Sensor.TYPE_MAGNETIC_FIELD);
        OrientationListener fakeListener = Mockito.mock(OrientationListener.class);
        sensey.startOrientationDetection(fakeListener);
        OrientationDetector detector = getDetector(fakeListener, OrientationDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for orientation", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be orientation detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectListenerWithStartOrientationDetectionWithCustomSmoothness() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        addSensor(Sensor.TYPE_MAGNETIC_FIELD);
        OrientationListener fakeListener = Mockito.mock(OrientationListener.class);
        sensey.startOrientationDetection(3, fakeListener);
        OrientationDetector detector = getDetector(fakeListener, OrientationDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for orientation", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be orientation detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectListenerWithStartProximityDetection() {
        addSensor(Sensor.TYPE_PROXIMITY);
        ProximityListener fakeListener = Mockito.mock(ProximityListener.class);
        sensey.startProximityDetection(fakeListener);
        ProximityDetector detector = getDetector(fakeListener, ProximityDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for proximity", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be proximity detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectListenerWithStartShakeDetection() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeListener fakeListener = Mockito.mock(ShakeListener.class);
        sensey.startShakeDetection(fakeListener);
        ShakeDetector detector = getDetector(fakeListener, ShakeDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for shake", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be shake detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectListenerWithStartShakeDetectionWithCustomThreshold() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeListener fakeListener = Mockito.mock(ShakeListener.class);
        sensey.startShakeDetection(4.0F, 1000, fakeListener);
        ShakeDetector detector = getDetector(fakeListener, ShakeDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for shake", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be shake detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectNoListenerWithStopFlipDetection() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        FlipListener fakeListener = Mockito.mock(FlipListener.class);
        sensey.startFlipDetection(fakeListener);
        FlipDetector detector = getDetector(fakeListener, FlipDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for flip", shadowSensorManager.hasListener(detector));
            sensey.stopFlipDetection(fakeListener);
            Assert.assertFalse("There should be no more sensor event listener in sensor manager", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be flip detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectNoListenerWithStopLightDetection() {
        addSensor(Sensor.TYPE_LIGHT);
        LightListener fakeListener = Mockito.mock(LightListener.class);
        sensey.startLightDetection(fakeListener);
        LightDetector detector = getDetector(fakeListener, LightDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for light", shadowSensorManager.hasListener(detector));
            sensey.stopLightDetection(fakeListener);
            Assert.assertFalse("There should be no more sensor event listener in sensor manager", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be light detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectNoListenerWithStopOrientationDetection() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        addSensor(Sensor.TYPE_MAGNETIC_FIELD);
        OrientationListener fakeListener = Mockito.mock(OrientationListener.class);
        sensey.startOrientationDetection(fakeListener);
        OrientationDetector detector = getDetector(fakeListener, OrientationDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for orientation", shadowSensorManager.hasListener(detector));
            sensey.stopOrientationDetection(fakeListener);
            Assert.assertFalse("There should be no more sensor event listener in sensor manager", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be orientation detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectNoListenerWithStopProximityDetection() {
        addSensor(Sensor.TYPE_PROXIMITY);
        ProximityListener fakeListener = Mockito.mock(ProximityListener.class);
        sensey.startProximityDetection(fakeListener);
        ProximityDetector detector = getDetector(fakeListener, ProximityDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for proximity", shadowSensorManager.hasListener(detector));
            sensey.stopProximityDetection(fakeListener);
            Assert.assertFalse("There should be no more sensor event listener in sensor manager", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be proximity detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectNoListenerWithStopShakeDetection() {
        addSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeListener fakeListener = Mockito.mock(ShakeListener.class);
        sensey.startShakeDetection(fakeListener);
        ShakeDetector detector = getDetector(fakeListener, ShakeDetector.class);
        if (detector != null) {
            Assert.assertTrue("Sensor Manager must contain sensor event listener for shake", shadowSensorManager.hasListener(detector));
            sensey.stopShakeDetection(fakeListener);
            Assert.assertFalse("There should be no more sensor event listener in sensor manager", shadowSensorManager.hasListener(detector));
        } else {
            Assert.fail("There should be shake detector in sensey. If not, please, check last version of class and update reflection accessing to it field");
        }
    }

    @Test
    public void detectNoListenerWithStoppingTwoSameDetections() {
        addSensor(Sensor.TYPE_PROXIMITY);
        ProximityListener fakeListener1 = Mockito.mock(ProximityListener.class);
        ProximityListener fakeListener2 = Mockito.mock(ProximityListener.class);
        ProximityDetector detector1 = startProximityDetection(fakeListener1);
        ProximityDetector detector2 = startProximityDetection(fakeListener2);
        sensey.stopProximityDetection(fakeListener1);
        sensey.stopProximityDetection(fakeListener2);
        Assert.assertFalse("Sensor manager need to contain no detectors", shadowSensorManager.hasListener(detector2));
        Assert.assertFalse("Sensor manager need to contain no detectors", shadowSensorManager.hasListener(detector1));
    }
}

