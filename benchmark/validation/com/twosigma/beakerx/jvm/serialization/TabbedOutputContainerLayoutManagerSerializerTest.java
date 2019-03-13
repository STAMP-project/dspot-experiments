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
import com.twosigma.beakerx.jvm.object.TabbedOutputContainerLayoutManager;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class TabbedOutputContainerLayoutManagerSerializerTest {
    private TabbedOutputContainerLayoutManager layoutManager;

    private static TabbedOutputContainerLayoutManagerSerializer serializer;

    private static SerializationTestHelper<TabbedOutputContainerLayoutManagerSerializer, TabbedOutputContainerLayoutManager> helper;

    @Test
    public void serializeTabbedOutputContainerLayoutManager_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = TabbedOutputContainerLayoutManagerSerializerTest.helper.serializeObject(layoutManager);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("TabbedOutputContainerLayoutManager");
    }

    @Test
    public void serializeBorderDisplayed_resultJsonHasBorderDisplayed() throws IOException {
        // given
        layoutManager.setBorderDisplayed(true);
        // when
        JsonNode actualObj = TabbedOutputContainerLayoutManagerSerializerTest.helper.serializeObject(layoutManager);
        // then
        Assertions.assertThat(actualObj.has("borderDisplayed")).isTrue();
        Assertions.assertThat(actualObj.get("borderDisplayed").asBoolean()).isTrue();
    }
}

