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


import Color.BLACK;
import Color.BLUE;
import Color.GREEN;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.plotitem.Bars;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class BarsSerializerTest {
    static ObjectMapper mapper;

    static BarsSerializer barsSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    Bars bars;

    @Test
    public void serializeWidthBars_resultJsonHasWidth() throws IOException {
        // when
        bars.setWidth(11);
        BarsSerializerTest.barsSerializer.serialize(bars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("width")).isTrue();
        Assertions.assertThat(actualObj.get("width").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeWidthsBars_resultJsonHasWidths() throws IOException {
        // when
        bars.setWidth(Arrays.asList(11, 22, 33));
        BarsSerializerTest.barsSerializer.serialize(bars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("widths")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("widths")));
        Assertions.assertThat(arrayNode.get(1).asInt()).isEqualTo(22);
    }

    @Test
    public void serializeColorBars_resultJsonHasColor() throws IOException {
        // when
        bars.setColor(GREEN);
        BarsSerializerTest.barsSerializer.serialize(bars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeColorsBars_resultJsonHasColors() throws IOException {
        // when
        bars.setColor(Arrays.asList(BLUE, GREEN, BLACK));
        BarsSerializerTest.barsSerializer.serialize(bars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("colors")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("colors")));
        Assertions.assertThat(arrayNode.get(1).get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeOutlineColorBars_resultJsonHasOutlineColor() throws IOException {
        // when
        bars.setOutlineColor(GREEN);
        BarsSerializerTest.barsSerializer.serialize(bars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outline_color")).isTrue();
        Assertions.assertThat(actualObj.get("outline_color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeOutlineColorsBars_resultJsonHasOutlineColors() throws IOException {
        // when
        bars.setOutlineColor(Arrays.asList(BLUE, GREEN, BLACK));
        BarsSerializerTest.barsSerializer.serialize(bars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outline_colors")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("outline_colors")));
        Assertions.assertThat(arrayNode.get(1).get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }
}

