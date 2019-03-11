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


import com.github.nisrulz.sensey.LightDetector.LightListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LightDetectorTest {
    @Mock
    private LightListener mockListener;

    @Test
    public void detectOnDarkWithLuxLessThanCustomThreshold() {
        testDetector(9).onSensorChanged(testLightEvent(new float[]{ 3 }));
        Mockito.verify(mockListener, Mockito.only()).onDark();
    }

    @Test
    public void detectOnDarkWithLuxLessThanDefaultThreshold() {
        testDetector().onSensorChanged(testLightEvent(new float[]{ 1 }));
        Mockito.verify(mockListener, Mockito.only()).onDark();
    }

    @Test
    public void detectOnLightWithExtraValues() {
        testDetector().onSensorChanged(testLightEvent(new float[]{ 10, 0, 43, 3, -423 }));
        Mockito.verify(mockListener, Mockito.only()).onLight();
    }

    @Test
    public void detectOnLightWithLuxEqualsToCustomThreshold() {
        testDetector(9).onSensorChanged(testLightEvent(new float[]{ 9 }));
        Mockito.verify(mockListener, Mockito.only()).onLight();
    }

    @Test
    public void detectOnLightWithLuxEqualsToDefaultThreshold() {
        testDetector().onSensorChanged(testLightEvent(new float[]{ 3 }));
        Mockito.verify(mockListener, Mockito.only()).onLight();
    }

    @Test
    public void detectOnLightWithLuxMoreThanCustomThreshold() {
        testDetector(9).onSensorChanged(testLightEvent(new float[]{ 12 }));
        Mockito.verify(mockListener, Mockito.only()).onLight();
    }

    @Test
    public void detectOnLightWithLuxMoreThanDefaultThreshold() {
        testDetector().onSensorChanged(testLightEvent(new float[]{ 10 }));
        Mockito.verify(mockListener, Mockito.only()).onLight();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void exceptionWithEmptyValues() {
        testDetector().onSensorChanged(testLightEvent(new float[]{  }));
    }
}

