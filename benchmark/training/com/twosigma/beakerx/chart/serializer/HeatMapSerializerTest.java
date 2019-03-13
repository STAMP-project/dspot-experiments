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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.heatmap.HeatMap;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class HeatMapSerializerTest {
    static ObjectMapper mapper;

    static HeatMapSerializer heatMapSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    @Test
    public void serializeDataOfHeatMap_resultJsonHasGraphicsList() throws IOException {
        // when
        HeatMap heatMap = new HeatMap();
        heatMap.setData(new Integer[][]{ new Integer[]{ new Integer(1), new Integer(2) }, new Integer[]{ new Integer(3), new Integer(4) } });
        HeatMapSerializerTest.heatMapSerializer.serialize(heatMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HeatMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("graphics_list")).isTrue();
        Assertions.assertThat(actualObj.get("graphics_list")).isNotEmpty();
    }

    @Test
    public void serializeColorOfHeatMap_resultJsonHasColor() throws IOException {
        // when
        HeatMap heatMap = new HeatMap();
        heatMap.setColor(new com.twosigma.beakerx.chart.GradientColor(Arrays.asList(GREEN, BLUE)));
        HeatMapSerializerTest.heatMapSerializer.serialize(heatMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = HeatMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color").get("colors")).isNotEmpty();
    }
}

