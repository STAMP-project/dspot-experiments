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


import com.github.nisrulz.sensey.ProximityDetector.ProximityListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProximityDetectorTest {
    @Mock
    private ProximityListener mockListener;

    @Test
    public void detectOnFarWithExtraValues() {
        testDetector().onSensorChanged(testProximityEvent(new float[]{ 10, 0, 43, 3, -423 }));
        Mockito.verify(mockListener, Mockito.only()).onFar();
    }

    @Test
    public void detectOnFarWithLuxMoreThanDefaultThreshold() {
        testDetector().onSensorChanged(testProximityEvent(new float[]{ 10 }));
        Mockito.verify(mockListener, Mockito.only()).onFar();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void exceptionWithEmptyValues() {
        testDetector().onSensorChanged(testProximityEvent(new float[]{  }));
    }
}

