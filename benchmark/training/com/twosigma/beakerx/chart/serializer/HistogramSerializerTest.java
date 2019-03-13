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


import Color.BLUE;
import Color.GREEN;
import Histogram.DisplayMode.STACK;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.histogram.Histogram;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class HistogramSerializerTest {
    static ObjectMapper mapper;

    static HistogramSerializer histogramSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    Histogram histogram;

    @Test
    public void serializeColorOfHistogram_resultJsonHasColor() throws IOException {
        // when
        histogram.setColor(GREEN);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeColorsOfHistogram_resultJsonHasColors() throws IOException {
        // when
        histogram.setColor(Arrays.asList(GREEN, BLUE));
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("colors")).isTrue();
        Assertions.assertThat(actualObj.get("colors")).isNotEmpty();
    }

    @Test
    public void serializeDataListOfHistogram_resultJsonHasGraphicsList() throws IOException {
        // when
        histogram.setData(Arrays.asList(1, 2));
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("graphics_list")).isTrue();
        Assertions.assertThat(actualObj.get("graphics_list")).isNotEmpty();
    }

    @Test
    public void serializeDataListListOfHistogram_resultJsonHasGraphicsList() throws IOException {
        // when
        histogram.setData(Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4)));
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("graphics_list")).isTrue();
        Assertions.assertThat(actualObj.get("graphics_list")).isNotEmpty();
    }

    @Test
    public void serializeRightCloseOfHistogram_resultJsonHasRightClose() throws IOException {
        // when
        histogram.setRightClose(true);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("right_close")).isTrue();
        Assertions.assertThat(actualObj.get("right_close").asBoolean()).isTrue();
    }

    @Test
    public void serializeRangeMinOfHistogram_resultJsonHasRangeMin() throws IOException {
        // when
        histogram.setRangeMin(11);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("range_min")).isTrue();
        Assertions.assertThat(actualObj.get("range_min").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeRangeMaxOfHistogram_resultJsonHasRangeMax() throws IOException {
        // when
        histogram.setRangeMax(11);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("range_max")).isTrue();
        Assertions.assertThat(actualObj.get("range_max").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeBinCountOfHistogram_resultJsonHasBinCount() throws IOException {
        // when
        histogram.setBinCount(11);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("bin_count")).isTrue();
        Assertions.assertThat(actualObj.get("bin_count").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeCumulativeOfHistogram_resultJsonHasCumulative() throws IOException {
        // when
        histogram.setCumulative(true);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("cumulative")).isTrue();
        Assertions.assertThat(actualObj.get("cumulative").asBoolean()).isTrue();
    }

    @Test
    public void serializeNormedOfHistogram_resultJsonHasNormed() throws IOException {
        // when
        histogram.setNormed(true);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("normed")).isTrue();
        Assertions.assertThat(actualObj.get("normed").asBoolean()).isTrue();
    }

    @Test
    public void serializeLogOfHistogram_resultJsonHasLog() throws IOException {
        // when
        histogram.setLog(true);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("log")).isTrue();
        Assertions.assertThat(actualObj.get("log").asBoolean()).isTrue();
    }

    @Test
    public void serializeDisplayModeOfHistogram_resultJsonHasDisplayMode() throws IOException {
        // when
        histogram.setDisplayMode(STACK);
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("displayMode")).isTrue();
        Assertions.assertThat(actualObj.get("displayMode").asText()).isEqualTo("STACK");
    }

    @Test
    public void serializeNamesOfHistogram_resultJsonHasNames() throws IOException {
        // when
        histogram.setNames(Arrays.asList("name1", "name2"));
        HistogramSerializerTest.histogramSerializer.serialize(histogram, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HistogramSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("names")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("names")));
        Assertions.assertThat(arrayNode.get(1).asText()).isEqualTo("name2");
    }
}

