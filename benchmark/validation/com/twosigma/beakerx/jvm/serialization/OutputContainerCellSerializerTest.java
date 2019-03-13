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
package com.twosigma.beakerx.jvm.serialization;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.twosigma.beakerx.jvm.object.OutputContainerCell;
import com.twosigma.beakerx.jvm.object.SimpleLayoutManager;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class OutputContainerCellSerializerTest {
    private OutputContainerCell outputContainerCell;

    private static OutputContainerCellSerializer serializer;

    private static SerializationTestHelper<OutputContainerCellSerializer, OutputContainerCell> helper;

    @Test
    public void serializeOutputContainerCell_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = OutputContainerCellSerializerTest.helper.serializeObject(outputContainerCell);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("OutputContainerCell");
    }

    @Test
    public void serializeLayoutManager_resultJsonHasLayoutManager() throws IOException {
        // given
        outputContainerCell.setLayoutManager(new SimpleLayoutManager());
        // when
        JsonNode actualObj = OutputContainerCellSerializerTest.helper.serializeObject(outputContainerCell);
        // then
        Assertions.assertThat(actualObj.has("layout")).isTrue();
    }

    @Test
    public void serializeLabels_resultJsonHasLabels() throws IOException {
        // given
        outputContainerCell.addItem("item object", "label");
        // when
        JsonNode actualObj = OutputContainerCellSerializerTest.helper.serializeObject(outputContainerCell);
        // then
        Assertions.assertThat(actualObj.has("labels")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("labels")));
        Assertions.assertThat(arrayNode.get(0).asText()).isEqualTo("label");
    }

    @Test
    public void serializeItems_resultJsonHasItems() throws IOException {
        // given
        outputContainerCell.addItem("item object", "label");
        // when
        JsonNode actualObj = OutputContainerCellSerializerTest.helper.serializeObject(outputContainerCell);
        // then
        Assertions.assertThat(actualObj.has("items")).isTrue();
        ArrayNode arrayNode = ((ArrayNode) (actualObj.get("items")));
        Assertions.assertThat(arrayNode.get(0).asText()).isEqualTo("item object");
    }
}

