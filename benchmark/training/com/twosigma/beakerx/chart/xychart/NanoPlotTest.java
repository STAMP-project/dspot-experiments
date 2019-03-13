/**
 * Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.chart.xychart;


import com.twosigma.beakerx.chart.xychart.plotitem.Line;
import com.twosigma.beakerx.chart.xychart.plotitem.Points;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class NanoPlotTest extends XYChartTest<NanoPlot> {
    Line line;

    Points points;

    @Test
    public void createNanoPlotByEmptyConstructor_nanoPlotHasGraphicsListIsEmpty() {
        // when
        NanoPlot nanoPlot = new NanoPlot();
        // then
        Assertions.assertThat(nanoPlot.getGraphics().size()).isEqualTo(0);
    }

    @Test
    public void addPointsToNanoPlot_nanoPlotHasGraphicsListSizeIsOne() {
        NanoPlot nanoPlot = new NanoPlot();
        // when
        nanoPlot.add(points);
        // then
        Assertions.assertThat(nanoPlot.getGraphics().size()).isEqualTo(1);
    }

    @Test
    public void addLineToNanoPlot_nanoPlotHasGraphicsListSizeIsOne() {
        NanoPlot nanoPlot = new NanoPlot();
        // when
        nanoPlot.add(line);
        // then
        Assertions.assertThat(nanoPlot.getGraphics().size()).isEqualTo(1);
    }

    @Test
    public void addPointsAndLineToNanoPlot_nanoPlotHasGraphicsListSizeIsTwo() {
        NanoPlot nanoPlot = new NanoPlot();
        // when
        nanoPlot.add(points);
        nanoPlot.add(line);
        // then
        Assertions.assertThat(nanoPlot.getGraphics().size()).isEqualTo(2);
    }

    @Test
    public void nanoPlotSubclassIsNanoPlot() {
        class FemtoPlot extends NanoPlot {}
        NanoPlot plot = new FemtoPlot();
        Assertions.assertThat(NanoPlot.isNanoPlotClass(plot.getClass())).isTrue();
    }
}

