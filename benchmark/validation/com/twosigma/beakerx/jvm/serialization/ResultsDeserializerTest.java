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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ResultsDeserializerTest {
    private String json;

    private boolean payload;

    @Test
    public void deserialize_resultObjectHasPayload() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(json);
        ResultsDeserializer deserializer = new ResultsDeserializer(new BasicObjectSerializer());
        // when
        Boolean result = ((Boolean) (deserializer.deserialize(actualObj, mapper)));
        // then
        Assertions.assertThat(result).isEqualTo(payload);
    }

    @Test
    public void canBeUsed_returnTrue() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(json);
        ResultsDeserializer deserializer = new ResultsDeserializer(new BasicObjectSerializer());
        // when
        boolean result = deserializer.canBeUsed(actualObj);
        // then
        Assertions.assertThat(result).isTrue();
    }
}

