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
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CollectionDeserializerTest {
    private String json = "[\"1\",\"2\",\"3\"]";

    @Test
    public void deserialize_resultObjectHasValues() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(json);
        CollectionDeserializer deserializer = new CollectionDeserializer(new BasicObjectSerializer());
        // when
        List list = ((List) (deserializer.deserialize(actualObj, mapper)));
        // then
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void canBeUsed_returnTrue() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(json);
        CollectionDeserializer deserializer = new CollectionDeserializer(new BasicObjectSerializer());
        // when
        boolean result = deserializer.canBeUsed(actualObj);
        // then
        Assertions.assertThat(result).isTrue();
    }
}

