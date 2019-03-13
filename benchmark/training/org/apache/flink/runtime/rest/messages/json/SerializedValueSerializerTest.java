/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rest.messages.json;


import java.io.Serializable;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link SerializedValueSerializer} and {@link SerializedValueDeserializer}.
 */
public class SerializedValueSerializerTest extends TestLogger {
    private ObjectMapper objectMapper;

    @Test
    public void testSerializationDeserialization() throws Exception {
        final String json = objectMapper.writeValueAsString(new SerializedValue(new SerializedValueSerializerTest.TestClass()));
        final SerializedValue<SerializedValueSerializerTest.TestClass> serializedValue = objectMapper.readValue(json, new org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.type.TypeReference<SerializedValue<SerializedValueSerializerTest.TestClass>>() {});
        final SerializedValueSerializerTest.TestClass deserializedValue = serializedValue.deserializeValue(ClassLoader.getSystemClassLoader());
        Assert.assertEquals("baz", deserializedValue.foo);
        Assert.assertEquals(1, deserializedValue.bar);
    }

    private static class TestClass implements Serializable {
        private static final long serialVersionUID = 1L;

        private String foo = "baz";

        private int bar = 1;
    }
}

