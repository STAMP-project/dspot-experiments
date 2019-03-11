/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.serialization.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;
import org.axonframework.messaging.MetaData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MetaDataDeserializerTest {
    private final ObjectMapper.DefaultTyping defaultTyping;

    private String serializedString;

    private String emptySerializedString;

    private String serializedContainerString;

    private String serializedDataInDataString;

    private ObjectMapper objectMapper;

    public MetaDataDeserializerTest(ObjectMapper.DefaultTyping defaultTyping) {
        this.defaultTyping = defaultTyping;
    }

    @Test
    public void testMetaDataSerializationWithDefaultTyping() throws IOException {
        MetaData deserialized = this.objectMapper.readValue(this.serializedString, MetaData.class);
        TestCase.assertEquals(deserialized.get("one"), "two");
        TestCase.assertEquals(this.serializedString, objectMapper.writeValueAsString(deserialized));
    }

    @Test
    public void testEmptyMetaDataSerializationWithDefaultTyping() throws IOException {
        MetaData deserialized = this.objectMapper.readValue(this.emptySerializedString, MetaData.class);
        TestCase.assertTrue(deserialized.entrySet().isEmpty());
        TestCase.assertEquals(this.emptySerializedString, objectMapper.writeValueAsString(deserialized));
    }

    @Test
    public void testMetaDataContainerWithDefaultTyping() throws IOException {
        MetaDataDeserializerTest.Container deserialized = this.objectMapper.readValue(this.serializedContainerString, MetaDataDeserializerTest.Container.class);
        TestCase.assertEquals(deserialized.b.get("one"), "two");
        TestCase.assertEquals(this.serializedContainerString, objectMapper.writeValueAsString(deserialized));
    }

    @Test
    public void testMetaDataContainerWithDataInDataWithDefaultTyping() throws IOException {
        MetaDataDeserializerTest.Container deserialized = this.objectMapper.readValue(this.serializedDataInDataString, MetaDataDeserializerTest.Container.class);
        if ((defaultTyping) != null) {
            TestCase.assertEquals(get("one"), "two");
        } else {
            // as there is no typing information, Jackson can't know it's a MetaData entry
            TestCase.assertEquals(((Map) (deserialized.b.get("one"))).get("one"), "two");
        }
        TestCase.assertEquals(this.serializedDataInDataString, objectMapper.writeValueAsString(deserialized));
    }

    public static class Container {
        private String a;

        private MetaData b;

        private Integer c;

        @JsonCreator
        public Container(@JsonProperty("a")
        String a, @JsonProperty("b")
        MetaData b, @JsonProperty("c")
        Integer c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public MetaData getB() {
            return b;
        }

        public void setB(MetaData b) {
            this.b = b;
        }

        public Integer getC() {
            return c;
        }

        public void setC(Integer c) {
            this.c = c;
        }
    }
}

