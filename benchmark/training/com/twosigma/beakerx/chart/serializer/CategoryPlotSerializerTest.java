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


import PlotOrientationType.VERTICAL;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.chart.categoryplot.CategoryPlot;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryBars;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryPoints;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryPlotSerializerTest {
    static ObjectMapper mapper;

    static CategoryPlotSerializer categoryPlotSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    KernelTest kernel;

    @Test
    public void serializeCategoryNamesOfCategoryPlot_resultJsonHasCategoryNames() throws IOException {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.setCategoryNames(Arrays.asList("name1", "name2"));
        CategoryPlotSerializerTest.categoryPlotSerializer.serialize(categoryPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("categoryNames")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("categoryNames")));
        Assertions.assertThat(arrayNode.get(1).asText()).isEqualTo("name2");
    }

    @Test
    public void serializeGraphicsListCategoryPlot_resultJsonHasGraphicsList() throws IOException {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.add(Arrays.asList(new CategoryBars(), new CategoryPoints()));
        CategoryPlotSerializerTest.categoryPlotSerializer.serialize(categoryPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("graphics_list")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("graphics_list")));
        Assertions.assertThat(arrayNode.size()).isEqualTo(2);
    }

    @Test
    public void serializeOrientationCategoryPlot_resultJsonHasOrientation() throws IOException {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.setOrientation(VERTICAL);
        CategoryPlotSerializerTest.categoryPlotSerializer.serialize(categoryPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("orientation")).isTrue();
        Assertions.assertThat(actualObj.get("orientation").asText()).isEqualTo("VERTICAL");
    }

    @Test
    public void serializeCategoryMarginOfCategoryPlot_resultJsonHasCategoryMargin() throws IOException {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.setCategoryMargin(0.5);
        CategoryPlotSerializerTest.categoryPlotSerializer.serialize(categoryPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("category_margin")).isTrue();
        Assertions.assertThat(actualObj.get("category_margin").asDouble()).isEqualTo(0.5);
    }

    @Test
    public void serializeCategoryNamesLabelAngleOfCategoryPlot_resultJsonHasCategoryNamesLabelAngle() throws IOException {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.setCategoryNamesLabelAngle(0.5);
        CategoryPlotSerializerTest.categoryPlotSerializer.serialize(categoryPlot, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = CategoryPlotSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("categoryNamesLabelAngle")).isTrue();
        Assertions.assertThat(actualObj.get("categoryNamesLabelAngle").asDouble()).isEqualTo(0.5);
    }
}

