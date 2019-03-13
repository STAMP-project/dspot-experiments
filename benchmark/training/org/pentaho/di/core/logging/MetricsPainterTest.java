/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.logging;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.gui.GCInterface;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.metrics.MetricsDuration;


public class MetricsPainterTest {
    MetricsPainter metricsPainter;

    List<MetricsDuration> durations = null;

    final int heightStub = 0;

    final double pixelsPerMsStub = 0;

    final long periodInMsStub = 0;

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentExceptionNullArgPaint() {
        callPaint(durations);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentExceptionEmptyArgPaint() {
        durations = new ArrayList<MetricsDuration>();
        callPaint(durations);
    }

    @Test(timeout = 1000)
    public void testDrawTimeScaleLineInfinityLoop() {
        GCInterface gCInterfaceMock = Mockito.mock(GCInterface.class);
        Mockito.when(metricsPainter.getGc()).thenReturn(gCInterfaceMock);
        Mockito.doCallRealMethod().when(metricsPainter).drawTimeScaleLine(heightStub, pixelsPerMsStub, periodInMsStub);
        Mockito.when(gCInterfaceMock.textExtent(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(Point.class));
        metricsPainter.drawTimeScaleLine(heightStub, pixelsPerMsStub, periodInMsStub);
    }
}

