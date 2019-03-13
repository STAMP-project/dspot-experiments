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
package com.twosigma.beakerx.chart.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.chart.xychart.CombinedPlot;
import com.twosigma.beakerx.chart.xychart.Plot;
import com.twosigma.beakerx.chart.xychart.SimpleTimePlot;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CombinedPlotSerializerTest {
    static ObjectMapper mapper;

    static CombinedPlotSerializer combinedPlotSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    KernelTest kernel;

    @Test
    public void serializeCombinedPlot_resultJsonHasType() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("CombinedPlot");
    }

    @Test
    public void serializeInitWidthOfCombinedPlot_resultJsonHasInitWidth() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.setInitWidth(600);
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("init_width")).isTrue();
        Assertions.assertThat(actualObj.get("init_width").asInt()).isEqualTo(600);
    }

    @Test
    public void serializeInitHeightOfCombinedPlot_resultJsonHasInitHeight() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.setInitHeight(300);
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("init_height")).isTrue();
        Assertions.assertThat(actualObj.get("init_height").asInt()).isEqualTo(300);
    }

    @Test
    public void serializeTitleOfCombinedPlot_resultJsonHasTitle() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.setTitle("Some title");
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("title")).isTrue();
        Assertions.assertThat(actualObj.get("title").asText()).isEqualTo("Some title");
    }

    @Test
    public void serializeXLabelNameOfCombinedPlot_resultJsonHasXLabelName() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.setXLabel("X label name");
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x_label")).isTrue();
        Assertions.assertThat(actualObj.get("x_label").asText()).isEqualTo("X label name");
    }

    @Test
    public void serializePlotTypeOfCombinedPlot_resultJsonHasPlotType() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.add(new Plot());
        combinedPlot.add(new Plot());
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("plot_type")).isTrue();
        Assertions.assertThat(actualObj.get("plot_type").asText()).isEqualTo("Plot");
    }

    @Test
    public void serializeTimePlotTypeOfCombinedPlot_resultJsonHasTimePlotType() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.add(new SimpleTimePlot(createDataForSimpleTimePlot(), Arrays.asList("m3", "time")));
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("plot_type")).isTrue();
        Assertions.assertThat(actualObj.get("plot_type").asText()).isEqualTo("TimePlot");
    }

    @Test
    public void serializePlotsOfCombinedPlot_resultJsonHasPlots() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.add(new Plot());
        combinedPlot.add(new Plot());
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("plots")).isTrue();
        Assertions.assertThat(actualObj.get("plots")).isNotEmpty();
    }

    @Test
    public void serializeWeightsOfCombinedPlot_resultJsonHasWeights() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.add(new Plot(), 3);
        combinedPlot.add(new Plot(), 3);
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("weights")).isTrue();
        Assertions.assertThat(actualObj.get("weights")).isNotEmpty();
    }

    @Test
    public void serializeCombinedPlot_resultJsonHasVersion() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("version")).isTrue();
        Assertions.assertThat(actualObj.get("version").asText()).isEqualTo("groovy");
    }

    @Test
    public void serializeXTickLabelsVisibleOfCombinedPlot_resultJsonHasXTickLabelsVisible() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.setxTickLabelsVisible(true);
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x_tickLabels_visible")).isTrue();
        Assertions.assertThat(actualObj.get("x_tickLabels_visible").asBoolean()).isTrue();
    }

    @Test
    public void serializeYTickLabelsVisibleOfCombinedPlot_resultJsonHasYTickLabelsVisible() throws IOException {
        // when
        CombinedPlot combinedPlot = new CombinedPlot();
        combinedPlot.setyTickLabelsVisible(true);
        CombinedPlotSerializerTest.combinedPlotSerializer.serialize(combinedPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CombinedPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("y_tickLabels_visible")).isTrue();
        Assertions.assertThat(actualObj.get("y_tickLabels_visible").asBoolean()).isTrue();
    }
}

