/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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


import Mode.DICE;
import ValueAccessor.WEIGHT;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.treemap.TreeMap;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class TreeMapSerializerTest {
    static ObjectMapper mapper;

    static TreeMapSerializer treeMapSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    TreeMap treeMap;

    @Test
    public void serializeGraphicsListOfTreeMap_resultJsonHasGraphicsList() throws IOException {
        // when
        TreeMapSerializerTest.treeMapSerializer.serialize(treeMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TreeMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("graphics_list")).isTrue();
    }

    @Test
    public void serializeModeOfTreeMap_resultJsonHasMode() throws IOException {
        // when
        treeMap.setMode(DICE);
        TreeMapSerializerTest.treeMapSerializer.serialize(treeMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TreeMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("mode")).isTrue();
        Assertions.assertThat(actualObj.get("mode").asText()).isNotEmpty();
    }

    @Test
    public void serializeStickyOfTreeMap_resultJsonHasSticky() throws IOException {
        // when
        treeMap.setSticky(true);
        TreeMapSerializerTest.treeMapSerializer.serialize(treeMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TreeMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("sticky")).isTrue();
        Assertions.assertThat(actualObj.get("sticky").asBoolean()).isTrue();
    }

    @Test
    public void serializeRatioOfTreeMap_resultJsonHasRatio() throws IOException {
        // when
        treeMap.setRatio(2.0);
        TreeMapSerializerTest.treeMapSerializer.serialize(treeMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TreeMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("ratio")).isTrue();
        Assertions.assertThat(actualObj.get("ratio").asDouble()).isEqualTo(2.0);
    }

    @Test
    public void serializeRoundOfTreeMap_resultJsonHasRound() throws IOException {
        // when
        treeMap.setRound(true);
        TreeMapSerializerTest.treeMapSerializer.serialize(treeMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TreeMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("round")).isTrue();
        Assertions.assertThat(actualObj.get("round").asBoolean()).isTrue();
    }

    @Test
    public void serializeValueAccessorOfTreeMap_resultJsonHasValueAccessor() throws IOException {
        // when
        treeMap.setValueAccessor(WEIGHT);
        TreeMapSerializerTest.treeMapSerializer.serialize(treeMap, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = TreeMapSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("valueAccessor")).isTrue();
        Assertions.assertThat(actualObj.get("valueAccessor").asText()).isNotEmpty();
    }
}

