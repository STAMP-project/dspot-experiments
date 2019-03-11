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


import com.github.nisrulz.sensey.OrientationDetector.OrientationListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


// TODO add tests for smoothness > 1
@RunWith(RobolectricTestRunner.class)
public class OrientationDetectorTest {
    private OrientationListener mockListener;

    private OrientationDetector testOrientationDetector;

    @Test
    public void detectBottomSideUp() {
        testOrientationDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ -9.81F, -9.81F, -9.81F }));
        testOrientationDetector.onSensorChanged(testMagneticEvent(new float[]{ 0, 0, 1 }));
        Mockito.verify(mockListener, Mockito.only()).onBottomSideUp();
    }

    @Test
    public void detectLeftSideUp() {
        testOrientationDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ -9.81F, 0, -9.81F }));
        testOrientationDetector.onSensorChanged(testMagneticEvent(new float[]{ 0, 0, 1 }));
        Mockito.verify(mockListener, Mockito.only()).onLeftSideUp();
    }

    @Test
    public void detectNothingForOnlyAccelerometerEvent() {
        testOrientationDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 1, 2, 3 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void detectNothingForOnlyMagneticEvent() {
        testOrientationDetector.onSensorChanged(testMagneticEvent(new float[]{ 1, 2, 3 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void detectRightSideUp() {
        testOrientationDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 9.81F, 0, 9.81F }));
        testOrientationDetector.onSensorChanged(testMagneticEvent(new float[]{ 0, 0, 1 }));
        Mockito.verify(mockListener, Mockito.only()).onRightSideUp();
    }

    @Test
    public void detectTopSideUp() {
        testOrientationDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 9.81F, 9.81F, 9.81F }));
        testOrientationDetector.onSensorChanged(testMagneticEvent(new float[]{ 0, 0, 1 }));
        Mockito.verify(mockListener, Mockito.only()).onTopSideUp();
    }
}

