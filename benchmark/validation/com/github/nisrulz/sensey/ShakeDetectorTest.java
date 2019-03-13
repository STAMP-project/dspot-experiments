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


import com.github.nisrulz.sensey.ShakeDetector.ShakeListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ShakeDetectorTest {
    @Mock
    private ShakeListener mockListener;

    @Test
    public void detectNothingWithZeroGravity() {
        testDetector().onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 0 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void detectNothingWithZeroGravityForCustomThreshold() {
        testDetector(10, 1000).onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 0 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void detectShakeWithDoubleGravity() {
        testDetector().onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 2 * 9.81F }));
        Mockito.verify(mockListener, Mockito.only()).onShakeDetected();
    }

    @Test
    public void detectShakeWithDoubleGravityForCustomThreshold() {
        testDetector(9, 1000).onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 2 * 9.81F }));
        Mockito.verify(mockListener, Mockito.only()).onShakeDetected();
    }

    @Test
    public void detectShakeWithSeveralGravitySensors() {
        ShakeDetector testDetector = testDetector();
        testDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 2 * 9.81F }));
        testDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 2 * (-9.81F) }));
        Mockito.verify(mockListener, Mockito.times(2)).onShakeDetected();
    }

    @Test
    public void detectShakeWithSeveralGravitySensorsForCustomThreshold() {
        ShakeDetector testDetector = testDetector(9, 1000);
        testDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 2 * 9.81F }));
        testDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 2 * (-9.81F) }));
        Mockito.verify(mockListener, Mockito.times(1)).onShakeDetected();
    }

    @Test
    public void detectShakeWithSeveralStrongGravitySensorsForCustomThreshold() {
        ShakeDetector testDetector = testDetector(9, 1000);
        testDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 2 * 9.81F }));
        testDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 3 * (-9.81F) }));
        Mockito.verify(mockListener, Mockito.times(2)).onShakeDetected();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void exceptionWithLessThanThreeElements() {
        testDetector().onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 2, 3 }));
    }
}

