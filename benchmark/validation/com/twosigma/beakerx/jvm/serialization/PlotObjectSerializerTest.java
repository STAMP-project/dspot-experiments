/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.jvm.serialization;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twosigma.beakerx.chart.categoryplot.CategoryPlot;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryLines;
import com.twosigma.beakerx.chart.heatmap.HeatMap;
import com.twosigma.beakerx.chart.histogram.Histogram;
import com.twosigma.beakerx.chart.treemap.TreeMap;
import com.twosigma.beakerx.chart.xychart.CombinedPlot;
import com.twosigma.beakerx.chart.xychart.Plot;
import com.twosigma.beakerx.chart.xychart.plotitem.Bars;
import com.twosigma.beakerx.jvm.object.ConsoleOutput;
import java.util.Arrays;
import net.sf.jtreemap.swing.TreeMapNode;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class PlotObjectSerializerTest {
    private PlotObjectSerializer plotObjectSerializer;

    private static JsonGenerator jgen;

    private static ObjectMapper mapper;

    @Test
    public void serializeXYChart_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new Plot(), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeXYGraphics_returnTrue() throws Exception {
        Bars obj = new Bars();
        obj.setX(Arrays.asList(10, 20));
        obj.setY(Arrays.asList(10, 20));
        obj.setDisplayName("test display name");
        // when
        boolean result = plotObjectSerializer.writeObject(obj, PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeArrays_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(Arrays.asList("v1", "v2"), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeCategoryPlot_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new CategoryPlot(), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeCategoryGraphics_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new CategoryLines(), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeHistogram_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new Histogram(), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeTreeMap_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new TreeMap(), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeTreeMapNode_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new TreeMapNode("010"), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeCombinedPlot_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new CombinedPlot(), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeHeatMap_returnTrue() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new HeatMap(), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void serializeConsoleOutput_returnFalse() throws Exception {
        // when
        boolean result = plotObjectSerializer.writeObject(new ConsoleOutput(true, "txt"), PlotObjectSerializerTest.jgen, true);
        // then
        Assertions.assertThat(result).isFalse();
    }
}

