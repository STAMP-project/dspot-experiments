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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.twosigma.beakerx.chart.GradientColor;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class GradientColorSerializerTest {
    static ObjectMapper mapper;

    static GradientColorSerializer gradientColorSerializer;

    JsonGenerator jgen;

    StringWriter sw;

    @Test
    public void serializeGradientColor_resultJsonHasGradientColor() throws IOException {
        // when
        GradientColor gradientColor = new GradientColor(Arrays.asList(GREEN, BLUE));
        GradientColorSerializerTest.gradientColorSerializer.serialize(gradientColor, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        // then
        ArrayNode arrayNode = ((ArrayNode) (GradientColorSerializerTest.mapper.readTree(sw.toString())));
        Assertions.assertThat(arrayNode).isNotEmpty();
        Assertions.assertThat(arrayNode.get(0).get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }
}

