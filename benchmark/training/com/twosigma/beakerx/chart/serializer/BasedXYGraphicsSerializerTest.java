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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.plotitem.Area;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class BasedXYGraphicsSerializerTest {
    static ObjectMapper mapper;

    static AreaSerializer basedXYGraphicsSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    Area area;

    @Test
    public void serializeBasesOfBasedXYGraphics_resultJsonHasBases() throws IOException {
        // when
        area.setBase(Arrays.asList(11, 22, 33));
        BasedXYGraphicsSerializerTest.basedXYGraphicsSerializer.serialize(area, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BasedXYGraphicsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("bases")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("bases")));
        Assertions.assertThat(arrayNode.get(1).asInt()).isEqualTo(22);
    }

    @Test
    public void serializeBaseOfBasedXYGraphics_resultJsonHasBase() throws IOException {
        // when
        area.setBase(11);
        BasedXYGraphicsSerializerTest.basedXYGraphicsSerializer.serialize(area, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = BasedXYGraphicsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("base")).isTrue();
        Assertions.assertThat(actualObj.get("base").asInt()).isEqualTo(11);
    }
}

