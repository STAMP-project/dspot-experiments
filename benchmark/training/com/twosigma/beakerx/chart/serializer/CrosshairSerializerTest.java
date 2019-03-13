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


import Color.GREEN;
import StrokeType.DASH;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.plotitem.Crosshair;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CrosshairSerializerTest {
    static ObjectMapper mapper;

    static CrosshairSerializer crosshairSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    @Test
    public void serializeCrosshair_resultJsonHasType() throws IOException {
        // when
        Crosshair crosshair = new Crosshair();
        CrosshairSerializerTest.crosshairSerializer.serialize(crosshair, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CrosshairSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("Crosshair");
    }

    @Test
    public void serializeColorCrosshair_resultJsonHasColor() throws IOException {
        // when
        Crosshair crosshair = new Crosshair();
        crosshair.setColor(GREEN);
        CrosshairSerializerTest.crosshairSerializer.serialize(crosshair, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CrosshairSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeStyleCrosshair_resultJsonHasStyle() throws IOException {
        // when
        Crosshair crosshair = new Crosshair();
        crosshair.setStyle(DASH);
        CrosshairSerializerTest.crosshairSerializer.serialize(crosshair, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CrosshairSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("style")).isTrue();
        Assertions.assertThat(actualObj.get("style").asText()).isEqualTo("DASH");
    }

    @Test
    public void serializeWidthCrosshair_resultJsonHasWidth() throws IOException {
        // when
        Crosshair crosshair = new Crosshair();
        crosshair.setWidth(2.0F);
        CrosshairSerializerTest.crosshairSerializer.serialize(crosshair, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CrosshairSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("width")).isTrue();
        Assertions.assertThat(actualObj.get("width").asDouble()).isEqualTo(2.0);
    }
}

