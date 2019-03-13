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
import StrokeType.SOLID;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.NanoPlot;
import com.twosigma.beakerx.chart.xychart.plotitem.ConstantLine;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ConstantLineSerializerTest {
    static ObjectMapper mapper;

    static ConstantLineSerializer constantLineSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    @Test
    public void serializeConstantLine_resultJsonHasType() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine() {};
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("ConstantLine");
    }

    @Test
    public void serializeXConstantLine_resultJsonHasX() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setX(1);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x")).isTrue();
        Assertions.assertThat(actualObj.get("x").asInt()).isEqualTo(1);
    }

    @Test
    public void serializeBigIntXWithNanoPlotType_resultJsonHasStringX() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setX(new BigInteger("12345678901234567891000"));
        constantLine.setPlotType(NanoPlot.class);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x")).isTrue();
        Assertions.assertThat(actualObj.get("x").isTextual()).isTrue();
    }

    @Test
    public void serializeYConstantLine_resultJsonHasY() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setY(1);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("y")).isTrue();
        Assertions.assertThat(actualObj.get("y").asInt()).isEqualTo(1);
    }

    @Test
    public void serializeVisibleConstantLine_resultJsonHasVisible() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setVisible(true);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("visible")).isTrue();
        Assertions.assertThat(actualObj.get("visible").asBoolean()).isTrue();
    }

    @Test
    public void serializeYAxisConstantLine_resultJsonHasYAxis() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setyAxis("Y Axis name");
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("yAxis")).isTrue();
        Assertions.assertThat(actualObj.get("yAxis").asText()).isEqualTo("Y Axis name");
    }

    @Test
    public void serializeShowLabelConstantLine_resultJsonHasShowLabel() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setShowLabel(true);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("showLabel")).isTrue();
        Assertions.assertThat(actualObj.get("showLabel").asBoolean()).isTrue();
    }

    @Test
    public void serializeWidthConstantLine_resultJsonHasWidth() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setWidth(2.0F);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("width")).isTrue();
        Assertions.assertThat(actualObj.get("width").asDouble()).isEqualTo(2.0);
    }

    @Test
    public void serializeStyleConstantLine_resultJsonHasStyle() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setStyle(SOLID);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("style")).isTrue();
        Assertions.assertThat(actualObj.get("style").asText()).isEqualTo("SOLID");
    }

    @Test
    public void serializeColorConstantLine_resultJsonHasColor() throws IOException {
        // when
        ConstantLine constantLine = new ConstantLine();
        constantLine.setColor(GREEN);
        ConstantLineSerializerTest.constantLineSerializer.serialize(constantLine, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantLineSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }
}

