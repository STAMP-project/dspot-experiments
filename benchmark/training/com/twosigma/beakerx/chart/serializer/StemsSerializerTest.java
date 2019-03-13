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
import StrokeType.DASHDOT;
import StrokeType.SOLID;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.plotitem.Stems;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class StemsSerializerTest {
    static ObjectMapper mapper;

    static StemsSerializer stemsSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    Stems stems;

    @Test
    public void serializeColorsStems_resultJsonHasColors() throws IOException {
        // when
        stems.setColor(Arrays.asList(BLUE, GREEN, BLACK));
        StemsSerializerTest.stemsSerializer.serialize(stems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = StemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("colors")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("colors")));
        Assertions.assertThat(arrayNode.get(1).get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeColorStems_resultJsonHasColor() throws IOException {
        // when
        stems.setColor(GREEN);
        StemsSerializerTest.stemsSerializer.serialize(stems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = StemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeWidthStems_resultJsonHasWidth() throws IOException {
        // when
        stems.setWidth(11.0F);
        StemsSerializerTest.stemsSerializer.serialize(stems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = StemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("width")).isTrue();
        Assertions.assertThat(actualObj.get("width").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeStrokeTypeStems_resultJsonHasStyle() throws IOException {
        // when
        stems.setStyle(SOLID);
        StemsSerializerTest.stemsSerializer.serialize(stems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = StemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("style")).isTrue();
        Assertions.assertThat(actualObj.get("style").asText()).isEqualTo("SOLID");
    }

    @Test
    public void serializeStrokeTypeListStems_resultJsonHasStyles() throws IOException {
        // when
        stems.setStyle(Arrays.asList(SOLID, DASHDOT));
        StemsSerializerTest.stemsSerializer.serialize(stems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = StemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("styles")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("styles")));
        Assertions.assertThat(arrayNode.get(1).asText()).isEqualTo("DASHDOT");
    }
}

