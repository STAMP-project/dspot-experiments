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
package com.twosigma.beakerx.jvm.object;


import TestContainer.Serializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.twosigma.beakerx.jvm.serialization.SerializationTestHelper;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class TestContainerTest {
    private static Serializer serializer;

    private static SerializationTestHelper<TestContainer.Serializer, TestContainer> helper;

    @Test
    public void serializeTestContainer_resultJsonHasType() throws IOException {
        // given
        TestContainer container = new TestContainer();
        // when
        JsonNode actualObj = TestContainerTest.helper.serializeObject(container);
        // then
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("TestContainer");
    }
}

