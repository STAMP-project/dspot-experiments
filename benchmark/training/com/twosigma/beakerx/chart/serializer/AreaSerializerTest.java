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
import com.twosigma.beakerx.chart.xychart.plotitem.Area;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class AreaSerializerTest {
    static ObjectMapper mapper;

    static AreaSerializer areaSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    Area area;

    @Test
    public void serializeInterpolationArea_resultJsonHasInterpolation() throws IOException {
        // when
        area.setInterpolation(1);
        AreaSerializerTest.areaSerializer.serialize(area, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = AreaSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("interpolation")).isTrue();
        Assertions.assertThat(actualObj.get("interpolation").asInt()).isEqualTo(1);
    }

    @Test
    public void serializeColorArea_resultJsonHasColor() throws IOException {
        // when
        area.setColor(GREEN);
        AreaSerializerTest.areaSerializer.serialize(area, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = AreaSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }
}

