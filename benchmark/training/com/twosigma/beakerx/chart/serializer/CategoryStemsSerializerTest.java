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


import StrokeType.DASHDOT;
import StrokeType.SOLID;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryStems;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryStemsSerializerTest {
    static ObjectMapper mapper;

    static CategoryStemsSerializer categoryStemsSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    @Test
    public void serializeBasesCategoryStems_resultJsonHasBases() throws IOException {
        // when
        CategoryStems categoryStems = new CategoryStems();
        categoryStems.setBase(Arrays.asList(11, 22, 33));
        CategoryStemsSerializerTest.categoryStemsSerializer.serialize(categoryStems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryStemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("bases")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("bases")));
        Assertions.assertThat(arrayNode.get(1).asInt()).isEqualTo(22);
    }

    @Test
    public void serializeBaseCategoryStems_resultJsonHasBase() throws IOException {
        // when
        CategoryStems categoryStems = new CategoryStems();
        categoryStems.setBase(11);
        CategoryStemsSerializerTest.categoryStemsSerializer.serialize(categoryStems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryStemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("base")).isTrue();
        Assertions.assertThat(actualObj.get("base").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeWidthCategoryStems_resultJsonHasWidth() throws IOException {
        // when
        CategoryStems categoryStems = new CategoryStems();
        categoryStems.setWidth(11.0F);
        CategoryStemsSerializerTest.categoryStemsSerializer.serialize(categoryStems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryStemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("width")).isTrue();
        Assertions.assertThat(actualObj.get("width").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeStrokeTypeCategoryStems_resultJsonHasStyle() throws IOException {
        // when
        CategoryStems categoryStems = new CategoryStems();
        categoryStems.setStyle(SOLID);
        CategoryStemsSerializerTest.categoryStemsSerializer.serialize(categoryStems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryStemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("style")).isTrue();
        Assertions.assertThat(actualObj.get("style").asText()).isEqualTo("SOLID");
    }

    @Test
    public void serializeStrokeTypeListCategoryStems_resultJsonHasStyles() throws IOException {
        // when
        CategoryStems categoryStems = new CategoryStems();
        categoryStems.setStyle(Arrays.asList(SOLID, DASHDOT));
        CategoryStemsSerializerTest.categoryStemsSerializer.serialize(categoryStems, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryStemsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("styles")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("styles")));
        Assertions.assertThat(arrayNode.get(1).asText()).isEqualTo("DASHDOT");
    }
}

