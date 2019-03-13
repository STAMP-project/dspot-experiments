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


import Color.BLACK;
import Color.BLUE;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryGraphics;
import com.twosigma.beakerx.jvm.serialization.SerializationTestHelper;
import java.io.IOException;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryGraphicsSerializerTest {
    private CategoryGraphics categoryGraphics;

    private static CategoryGraphicsSerializer serializer;

    private static SerializationTestHelper<CategoryGraphicsSerializer, CategoryGraphics> helper;

    @Test
    public void serializeSeriesNames_resultJsonHasSeriesNames() throws IOException {
        // given
        categoryGraphics.setSeriesNames(Arrays.asList("s1", "s2"));
        // when
        JsonNode actualObj = CategoryGraphicsSerializerTest.helper.serializeObject(categoryGraphics);
        // then
        Assertions.assertThat(actualObj.has("seriesNames")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("seriesNames")));
        Assertions.assertThat(arrayNode.size()).isEqualTo(2);
    }

    @Test
    public void serializeValue_resultJsonHasValue() throws IOException {
        // given
        categoryGraphics.setValue(new Integer[]{ new Integer(1), new Integer(2) });
        // when
        JsonNode actualObj = CategoryGraphicsSerializerTest.helper.serializeObject(categoryGraphics);
        // then
        Assertions.assertThat(actualObj.has("value")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("value")));
        Assertions.assertThat(arrayNode).isNotEmpty();
    }

    @Test
    public void serializeColors_resultJsonHasColors() throws IOException {
        // given
        categoryGraphics.setColor(Arrays.asList(BLACK, BLUE));
        // when
        JsonNode actualObj = CategoryGraphicsSerializerTest.helper.serializeObject(categoryGraphics);
        // then
        Assertions.assertThat(actualObj.has("colors")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("colors")));
        Assertions.assertThat(arrayNode).isNotEmpty();
    }
}

