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


import com.twosigma.beakerx.chart.xychart.plotitem.Area;
import com.twosigma.beakerx.chart.xychart.plotitem.ConstantBand;
import com.twosigma.beakerx.chart.xychart.plotitem.ConstantLine;
import com.twosigma.beakerx.chart.xychart.plotitem.Line;
import com.twosigma.beakerx.chart.xychart.plotitem.Rasters;
import com.twosigma.beakerx.chart.xychart.plotitem.Text;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;


public class PlotTest extends XYChartTest<Plot> {
    Plot plot;

    Line line;

    Area area;

    @Test
    public void createPlotByEmptyConstructor_plotHasGraphicsListIsEmpty() {
        // when
        Plot plot = new Plot();
        // then
        assertThat(plot.getGraphics().size()).isEqualTo(0);
    }

    @Test
    public void addLineToPlot_plotHasGraphicsListSizeIsOne() {
        // when
        plot.add(line);
        // then
        assertThat(plot.getGraphics().size()).isEqualTo(1);
    }

    @Test
    public void addAreaToPlot_plotHasGraphicsListSizeIsOne() {
        // when
        plot.add(area);
        // then
        assertThat(plot.getGraphics().size()).isEqualTo(1);
    }

    @Test
    public void addLineAndAreaToPlot_plotHasGraphicsListSizeIsTwo() {
        // when
        plot.add(line);
        plot.add(area);
        // then
        assertThat(plot.getGraphics().size()).isEqualTo(2);
    }

    @Test
    public void leftShiftForRasters_plotHasRastersListSizeIsOne() {
        // given
        Rasters raster = new Rasters();
        List<Number> value = Collections.singletonList(1);
        raster.setY(value);
        raster.setWidth(value);
        raster.setHeight(value);
        // when
        plot.add(raster);
        // then
        assertThat(plot.getRasters().size()).isEqualTo(1);
    }

    @Test
    public void addListOfPlotObjects_hasAllPlotObjects() {
        // given
        Rasters rasters = new Rasters();
        List<Number> value = Collections.singletonList(1);
        rasters.setY(value);
        rasters.setWidth(value);
        rasters.setHeight(value);
        // when
        plot.add(Arrays.asList(line, new ConstantLine(), new ConstantBand(), rasters, new Text()));
        // then
        assertThat(plot.getGraphics().size()).isEqualTo(1);
        assertThat(plot.getConstantLines().size()).isEqualTo(1);
        assertThat(plot.getConstantBands().size()).isEqualTo(1);
        assertThat(plot.getRasters().size()).isEqualTo(1);
        assertThat(plot.getTexts().size()).isEqualTo(1);
    }

    @Test
    public void setxAutoRangeByTrue_XAutoRangeIsTrue() {
        // when
        plot.setxAutoRange(true);
        // then
        assertThat(plot.getXAutoRange()).isTrue();
    }

    @Test
    public void setxBoundByList_hasXLowerAndUpperBounds() throws Exception {
        // when
        plot.setxBound(Arrays.asList(1.0, 10.0));
        // then
        assertThat(plot.getXLowerBound()).isEqualTo(1.0);
        assertThat(plot.getXUpperBound()).isEqualTo(10.0);
    }
}

