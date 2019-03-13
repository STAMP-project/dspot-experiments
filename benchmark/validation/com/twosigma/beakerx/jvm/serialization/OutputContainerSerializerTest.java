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
import com.twosigma.beakerx.jvm.object.OutputContainer;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class OutputContainerSerializerTest {
    private OutputContainer outputContainer;

    private static OutputContainerSerializer serializer;

    private static SerializationTestHelper<OutputContainerSerializer, OutputContainer> helper;

    @Test
    public void serializeOutputContainer_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = OutputContainerSerializerTest.helper.serializeObject(outputContainer);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("OutputContainer");
    }
}

