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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.NanoPlot;
import com.twosigma.beakerx.chart.xychart.plotitem.Text;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class TextSerializerTest {
    static ObjectMapper mapper;

    static TextSerializer textSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    Text text;

    @Test
    public void serializeText_resultJsonHasType() throws IOException {
        // when
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("Text");
    }

    @Test
    public void serializeXText_resultJsonHasX() throws IOException {
        // when
        text.setX(new Integer(11));
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x")).isTrue();
        Assertions.assertThat(actualObj.get("x").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeBigIntXWithNanoPlotType_resultJsonHasStringX() throws IOException {
        // when
        text.setX(new BigInteger("12345678901234567891000"));
        text.setPlotType(NanoPlot.class);
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x")).isTrue();
        Assertions.assertThat(actualObj.get("x").isTextual()).isTrue();
    }

    @Test
    public void serializeYText_resultJsonHasY() throws IOException {
        // when
        text.setY(new Integer(22));
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("y")).isTrue();
        Assertions.assertThat(actualObj.get("y").asInt()).isEqualTo(22);
    }

    @Test
    public void serializeShowPointerText_resultJsonHasShowPointer() throws IOException {
        // when
        text.setShowPointer(true);
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("show_pointer")).isTrue();
        Assertions.assertThat(actualObj.get("show_pointer").asBoolean()).isTrue();
    }

    @Test
    public void serializeTextOfText_resultJsonHasText() throws IOException {
        // when
        text.setText("some text");
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("text")).isTrue();
        Assertions.assertThat(actualObj.get("text").asText()).isEqualTo("some text");
    }

    @Test
    public void serializePointerAngleOfText_resultJsonHasPointerAngle() throws IOException {
        // when
        text.setPointerAngle(0.5);
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("pointer_angle")).isTrue();
        Assertions.assertThat(actualObj.get("pointer_angle").asDouble()).isEqualTo(0.5);
    }

    @Test
    public void serializeColorOfText_resultJsonHasColor() throws IOException {
        // when
        text.setColor(GREEN);
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeSizeOfText_resultJsonHasSize() throws IOException {
        // when
        text.setSize(11);
        TextSerializerTest.textSerializer.serialize(text, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TextSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("size")).isTrue();
        Assertions.assertThat(actualObj.get("size").asInt()).isEqualTo(11);
    }
}

