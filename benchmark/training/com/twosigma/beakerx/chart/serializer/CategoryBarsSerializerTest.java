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
import LabelPositionType.CENTER;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryBars;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryBarsSerializerTest {
    static ObjectMapper mapper;

    static CategoryBarsSerializer categoryBarsSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    @Test
    public void serializeBasesCategoryBars_resultJsonHasBases() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setBase(Arrays.asList(11, 22, 33));
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("bases")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("bases")));
        Assertions.assertThat(arrayNode.get(1).asInt()).isEqualTo(22);
    }

    @Test
    public void serializeBaseCategoryBars_resultJsonHasBase() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setBase(11);
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("base")).isTrue();
        Assertions.assertThat(actualObj.get("base").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeWidthCategoryBars_resultJsonHasWidth() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setWidth(11);
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("width")).isTrue();
        Assertions.assertThat(actualObj.get("width").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeWidthsCategoryBars_resultJsonHasWidths() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setWidth(Arrays.asList(11, 22, 33));
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("widths")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("widths")));
        Assertions.assertThat(arrayNode.get(1).asInt()).isEqualTo(22);
    }

    @Test
    public void serializeOutlineColorCategoryBars_resultJsonHasOutlineColor() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setOutlineColor(GREEN);
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outline_color")).isTrue();
        Assertions.assertThat(actualObj.get("outline_color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeOutlineColorsCategoryBars_resultJsonHasOutlineColors() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setOutlineColor(Arrays.asList(BLUE, GREEN, BLACK));
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outline_colors")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("outline_colors")));
        Assertions.assertThat(arrayNode.get(1).get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeFillCategoryBars_resultJsonHasFill() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setFill(true);
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("fill")).isTrue();
        Assertions.assertThat(actualObj.get("fill").asBoolean()).isTrue();
    }

    @Test
    public void serializeFillsCategoryBars_resultJsonHasFills() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setFill(Arrays.asList(false, true, false));
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("fills")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("fills")));
        Assertions.assertThat(arrayNode.get(1).asBoolean()).isTrue();
    }

    @Test
    public void serializeDrawOutlineCategoryBars_resultJsonHasOutline() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setDrawOutline(true);
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outline")).isTrue();
        Assertions.assertThat(actualObj.get("outline").asBoolean()).isTrue();
    }

    @Test
    public void serializeDrawOutlinesCategoryBars_resultJsonHasDrawOutlines() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setDrawOutline(Arrays.asList(false, true, false));
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outlines")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("outlines")));
        Assertions.assertThat(arrayNode.get(1).asBoolean()).isTrue();
    }

    @Test
    public void serializeLabelPositionCategoryBars_resultJsonHasLabelPosition() throws IOException {
        // when
        CategoryBars categoryBars = new CategoryBars();
        categoryBars.setLabelPosition(CENTER);
        CategoryBarsSerializerTest.categoryBarsSerializer.serialize(categoryBars, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryBarsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("labelPosition")).isTrue();
        Assertions.assertThat(actualObj.get("labelPosition").asText()).isEqualTo("CENTER");
    }
}

