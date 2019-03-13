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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.NanoPlot;
import com.twosigma.beakerx.chart.xychart.plotitem.ConstantBand;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ConstantBandSerializerTest {
    static ObjectMapper mapper;

    static ConstantBandSerializer constantBandSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    @Test
    public void serializeConstantBand_resultJsonHasType() throws IOException {
        // when
        ConstantBand constantBand = new ConstantBand() {};
        ConstantBandSerializerTest.constantBandSerializer.serialize(constantBand, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantBandSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("ConstantBand");
    }

    @Test
    public void serializeXConstantBand_resultJsonHasX() throws IOException {
        // when
        ConstantBand constantBand = new ConstantBand();
        constantBand.setX(Arrays.asList(1, 2, 3));
        ConstantBandSerializerTest.constantBandSerializer.serialize(constantBand, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantBandSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x")).isTrue();
        Assertions.assertThat(actualObj.get("x")).isNotEmpty();
    }

    @Test
    public void serializeBigIntXWithNanoPlotType_resultJsonHasStringXs() throws IOException {
        // when
        ConstantBand constantBand = new ConstantBand();
        constantBand.setX(Arrays.asList(new BigInteger("12345678901234567891000"), new BigInteger("12345678901234567892000")));
        constantBand.setPlotType(NanoPlot.class);
        ConstantBandSerializerTest.constantBandSerializer.serialize(constantBand, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantBandSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("x")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("x")));
        Assertions.assertThat(arrayNode.get(0).isTextual()).isTrue();
    }

    @Test
    public void serializeYConstantBand_resultJsonHasY() throws IOException {
        // when
        ConstantBand constantBand = new ConstantBand();
        constantBand.setY(Arrays.asList(1, 2, 3));
        ConstantBandSerializerTest.constantBandSerializer.serialize(constantBand, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantBandSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("y")).isTrue();
        Assertions.assertThat(actualObj.get("y")).isNotEmpty();
    }

    @Test
    public void serializeVisibleConstantBand_resultJsonHasVisible() throws IOException {
        // when
        ConstantBand constantBand = new ConstantBand();
        constantBand.setVisible(true);
        ConstantBandSerializerTest.constantBandSerializer.serialize(constantBand, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantBandSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("visible")).isTrue();
        Assertions.assertThat(actualObj.get("visible").asBoolean()).isTrue();
    }

    @Test
    public void serializeYAxisConstantBand_resultJsonHasYAxis() throws IOException {
        // when
        ConstantBand constantBand = new ConstantBand();
        constantBand.setyAxis("Y Axis name");
        ConstantBandSerializerTest.constantBandSerializer.serialize(constantBand, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantBandSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("yAxis")).isTrue();
        Assertions.assertThat(actualObj.get("yAxis").asText()).isEqualTo("Y Axis name");
    }

    @Test
    public void serializeColorConstantBand_resultJsonHasColor() throws IOException {
        // when
        ConstantBand constantBand = new ConstantBand();
        constantBand.setColor(GREEN);
        ConstantBandSerializerTest.constantBandSerializer.serialize(constantBand, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = ConstantBandSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }
}

