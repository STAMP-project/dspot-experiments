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


import com.github.nisrulz.sensey.FlipDetector.FlipListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FlipDetectorTest {
    @Mock
    private FlipListener mockListener;

    private FlipDetector testFlipDetector;

    @Test
    public void detectFlipWithMiddleFaceDownValue() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, -9.5F }));
        Mockito.verify(mockListener, Mockito.only()).onFaceDown();
    }

    @Test
    public void detectFlipWithMiddleFaceUpValue() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 9.5F }));
        Mockito.verify(mockListener, Mockito.only()).onFaceUp();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void exceptionWithArrayLessThenThreeElements() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void notDetectFlipWithMaxFaceDownValue() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, -9 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void notDetectFlipWithMaxFaceUpValue() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 10 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void notDetectFlipWithMinFaceDownValue() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, -10 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void notDetectFlipWithMinFaceUpValue() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 9 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void notDetectFlipWithOtherValue() {
        testFlipDetector.onSensorChanged(SensorUtils.testAccelerometerEvent(new float[]{ 0, 0, 0 }));
        Mockito.verifyNoMoreInteractions(mockListener);
    }
}

