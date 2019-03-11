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


import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.SerializedThrowable;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link SerializedThrowableSerializer} and {@link SerializedThrowableDeserializer}.
 */
public class SerializedThrowableSerializerTest extends TestLogger {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSerializationDeserialization() throws Exception {
        final String lastExceptionMessage = "message";
        final String causeMessage = "cause";
        final SerializedThrowable serializedThrowable = new SerializedThrowable(new RuntimeException(lastExceptionMessage, new RuntimeException(causeMessage)));
        final String json = objectMapper.writeValueAsString(serializedThrowable);
        final SerializedThrowable deserializedSerializedThrowable = objectMapper.readValue(json, SerializedThrowable.class);
        Assert.assertThat(deserializedSerializedThrowable.getMessage(), Matchers.equalTo(lastExceptionMessage));
        Assert.assertThat(deserializedSerializedThrowable.getFullStringifiedStackTrace(), Matchers.equalTo(serializedThrowable.getFullStringifiedStackTrace()));
        Assert.assertThat(deserializedSerializedThrowable.getCause().getMessage(), Matchers.equalTo(causeMessage));
        Assert.assertThat(deserializedSerializedThrowable.getCause(), Matchers.instanceOf(SerializedThrowable.class));
    }
}

