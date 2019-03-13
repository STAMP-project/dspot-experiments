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
import com.twosigma.beakerx.jvm.object.CyclingOutputContainerLayoutManager;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CyclingOutputContainerLayoutManagerSerializerTest {
    private CyclingOutputContainerLayoutManager layoutManager;

    private static CyclingOutputContainerLayoutManagerSerializer serializer;

    private static SerializationTestHelper<CyclingOutputContainerLayoutManagerSerializer, CyclingOutputContainerLayoutManager> helper;

    @Test
    public void serializeCyclingOutputContainerLayoutManager_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = CyclingOutputContainerLayoutManagerSerializerTest.helper.serializeObject(layoutManager);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("CyclingOutputContainerLayoutManager");
    }

    @Test
    public void serializeBorderDisplayed_resultJsonHasBorderDisplayed() throws IOException {
        // given
        layoutManager.setBorderDisplayed(true);
        // when
        JsonNode actualObj = CyclingOutputContainerLayoutManagerSerializerTest.helper.serializeObject(layoutManager);
        // then
        Assertions.assertThat(actualObj.has("borderDisplayed")).isTrue();
        Assertions.assertThat(actualObj.get("borderDisplayed").asBoolean()).isTrue();
    }

    @Test
    public void serializePeriod_resultJsonHasPeriod() throws IOException {
        // given
        layoutManager.setPeriod(1000L);
        // when
        JsonNode actualObj = CyclingOutputContainerLayoutManagerSerializerTest.helper.serializeObject(layoutManager);
        // then
        Assertions.assertThat(actualObj.has("period")).isTrue();
        Assertions.assertThat(actualObj.get("period").asLong()).isEqualTo(1000L);
    }
}

