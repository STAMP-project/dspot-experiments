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
import ShapeType.CIRCLE;
import ShapeType.CROSS;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.xychart.plotitem.Points;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class PointsSerializerTest {
    static ObjectMapper mapper;

    static PointsSerializer pointsSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    Points points;

    @Test
    public void serializeSizePoints_resultJsonHasSize() throws IOException {
        // when
        points.setSize(new Integer(11));
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("size")).isTrue();
        Assertions.assertThat(actualObj.get("size").asInt()).isEqualTo(11);
    }

    @Test
    public void serializeSizesPoints_resultJsonHasSizes() throws IOException {
        // when
        points.setSize(Arrays.asList(11, 22, 33));
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("sizes")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("sizes")));
        Assertions.assertThat(arrayNode.get(1).asInt()).isEqualTo(22);
    }

    @Test
    public void serializeShapePoints_resultJsonHasShape() throws IOException {
        // when
        points.setShape(CIRCLE);
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("shape")).isTrue();
        Assertions.assertThat(actualObj.get("shape").asText()).isEqualTo("CIRCLE");
    }

    @Test
    public void serializeShapesPoints_resultJsonHasShapes() throws IOException {
        // when
        points.setShape(Arrays.asList(CIRCLE, CROSS));
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("shaps")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("shaps")));
        Assertions.assertThat(arrayNode.get(1).asText()).isEqualTo("CROSS");
    }

    @Test
    public void serializeFillPoints_resultJsonHasFill() throws IOException {
        // when
        points.setFill(true);
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("fill")).isTrue();
        Assertions.assertThat(actualObj.get("fill").asBoolean()).isTrue();
    }

    @Test
    public void serializeFillsPoints_resultJsonHasFills() throws IOException {
        // when
        points.setFill(Arrays.asList(false, true, false));
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("fills")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("fills")));
        Assertions.assertThat(arrayNode.get(1).asBoolean()).isTrue();
    }

    @Test
    public void serializeColorPoints_resultJsonHasColor() throws IOException {
        // when
        points.setColor(GREEN);
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("color")).isTrue();
        Assertions.assertThat(actualObj.get("color")).isNotEmpty();
    }

    @Test
    public void serializeColorsPoints_resultJsonHasColors() throws IOException {
        // when
        points.setColor(Arrays.asList(GREEN, BLUE));
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("colors")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("colors")));
        Assertions.assertThat(arrayNode.get(1)).isNotEmpty();
    }

    @Test
    public void serializeOutlineColorPoints_resultJsonHasOutlineColor() throws IOException {
        // when
        points.setOutlineColor(GREEN);
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outline_color")).isTrue();
        Assertions.assertThat(actualObj.get("outline_color").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }

    @Test
    public void serializeOutlineColorsPoints_resultJsonHasOutlineColors() throws IOException {
        // when
        points.setOutlineColor(Arrays.asList(GREEN, BLUE));
        PointsSerializerTest.pointsSerializer.serialize(points, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        JsonNode actualObj = PointsSerializerTest.mapper.readTree(sw.toString());
        Assertions.assertThat(actualObj.has("outline_colors")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("outline_colors")));
        Assertions.assertThat(arrayNode.get(1).get("rgb").asInt()).isEqualTo(BLUE.getRGB());
    }
}

