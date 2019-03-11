/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.stepfunctions.builder.internal;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.stepfunctions.builder.SimplePojo;
import com.amazonaws.services.stepfunctions.builder.StatesAsserts;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JacksonUtilsTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void jsonToString_NullNode_ReturnsNull() {
        Assert.assertNull(JacksonUtils.jsonToString(null));
    }

    @Test
    public void jsonToString_NonNullNode_ReturnsJson() throws IOException {
        String json = "{\"foo\": \"bar\"}";
        StatesAsserts.assertJsonEquals(json, JacksonUtils.jsonToString(JacksonUtilsTest.MAPPER.readTree(json)));
    }

    @Test(expected = SdkClientException.class)
    public void invalidJsonNode_ThrowsException() throws IOException {
        JsonNode mock = Mockito.mock(JsonNode.class);
        Mockito.doThrow(new JsonMappingException("BOOM")).when(mock).serialize(ArgumentMatchers.any(JsonGenerator.class), ArgumentMatchers.any(SerializerProvider.class));
        JacksonUtils.jsonToString(mock);
    }

    @Test
    public void stringToJsonNode_NullString_ReturnsNull() {
        Assert.assertNull(JacksonUtils.stringToJsonNode("Param", null));
    }

    @Test
    public void stringToJsonNode_ValidJson_ReturnsJsonNode() throws JsonProcessingException {
        String json = "[1, 2, 3]";
        JsonNode actual = JacksonUtils.stringToJsonNode("Param", json);
        StatesAsserts.assertJsonEquals(json, JacksonUtilsTest.MAPPER.writeValueAsString(actual));
    }

    @Test(expected = SdkClientException.class)
    public void stringToJsonNode_InvalidJson_ThrowsException() throws JsonProcessingException {
        JacksonUtils.stringToJsonNode("Param", "{");
    }

    @Test
    public void objectToJsonNode_ReturnsJsonNode() throws JsonProcessingException {
        JsonNode actual = JacksonUtils.objectToJsonNode(new SimplePojo("value"));
        StatesAsserts.assertJsonEquals("{\"foo\": \"value\"}", JacksonUtilsTest.MAPPER.writeValueAsString(actual));
    }
}

