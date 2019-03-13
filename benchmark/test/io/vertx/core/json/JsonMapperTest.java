/**
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.json;


import Json.mapper;
import Json.prettyMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static Json.mapper;
import static Json.prettyMapper;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class JsonMapperTest extends VertxTestBase {
    @Test
    public void testGetSetMapper() {
        ObjectMapper mapper = mapper;
        assertNotNull(mapper);
        ObjectMapper newMapper = new ObjectMapper();
        mapper = newMapper;
        assertSame(newMapper, mapper);
        mapper = mapper;
    }

    @Test
    public void testGetSetPrettyMapper() {
        ObjectMapper mapper = prettyMapper;
        assertNotNull(mapper);
        ObjectMapper newMapper = new ObjectMapper();
        prettyMapper = newMapper;
        assertSame(newMapper, prettyMapper);
        prettyMapper = mapper;
    }

    @Test
    public void encodeCustomTypeInstant() {
        Instant now = Instant.now();
        String json = Json.encode(now);
        assertNotNull(json);
        // the RFC is one way only
        Instant decoded = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(json.substring(1, ((json.length()) - 1))));
        assertEquals(now, decoded);
    }

    @Test
    public void encodeCustomTypeInstantNull() {
        Instant now = null;
        String json = Json.encode(now);
        assertNotNull(json);
        assertEquals("null", json);
    }

    @Test
    public void encodeCustomTypeBinary() {
        byte[] data = new byte[]{ 'h', 'e', 'l', 'l', 'o' };
        String json = Json.encode(data);
        assertNotNull(json);
        // base64 encoded hello
        assertEquals("\"aGVsbG8=\"", json);
    }

    @Test
    public void encodeCustomTypeBinaryNull() {
        byte[] data = null;
        String json = Json.encode(data);
        assertNotNull(json);
        assertEquals("null", json);
    }

    @Test
    public void encodeToBuffer() {
        Buffer json = Json.encodeToBuffer("Hello World!");
        assertNotNull(json);
        // json strings are always UTF8
        assertEquals("\"Hello World!\"", json.toString("UTF-8"));
    }

    @Test
    public void testGenericDecoding() {
        JsonMapperTest.Pojo original = new JsonMapperTest.Pojo();
        original.value = "test";
        String json = Json.encode(Collections.singletonList(original));
        List<JsonMapperTest.Pojo> correct;
        correct = Json.decodeValue(json, new TypeReference<List<JsonMapperTest.Pojo>>() {});
        assertTrue(((((List) (correct)).get(0)) instanceof JsonMapperTest.Pojo));
        assertEquals(original.value, correct.get(0).value);
        // same must apply if instead of string we use a buffer
        correct = Json.decodeValue(Buffer.buffer(json, "UTF8"), new TypeReference<List<JsonMapperTest.Pojo>>() {});
        assertTrue(((((List) (correct)).get(0)) instanceof JsonMapperTest.Pojo));
        assertEquals(original.value, correct.get(0).value);
        List incorrect = Json.decodeValue(json, List.class);
        assertFalse(((incorrect.get(0)) instanceof JsonMapperTest.Pojo));
        assertTrue(((incorrect.get(0)) instanceof Map));
        assertEquals(original.value, ((Map) (incorrect.get(0))).get("value"));
    }

    @Test
    public void testInstantDecoding() {
        JsonMapperTest.Pojo original = new JsonMapperTest.Pojo();
        original.instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2018-06-20T07:25:38.397Z"));
        JsonMapperTest.Pojo decoded = Json.decodeValue("{\"instant\":\"2018-06-20T07:25:38.397Z\"}", JsonMapperTest.Pojo.class);
        assertEquals(original.instant, decoded.instant);
    }

    @Test
    public void testNullInstantDecoding() {
        JsonMapperTest.Pojo original = new JsonMapperTest.Pojo();
        JsonMapperTest.Pojo decoded = Json.decodeValue("{\"instant\":null}", JsonMapperTest.Pojo.class);
        assertEquals(original.instant, decoded.instant);
    }

    @Test
    public void testBytesDecoding() {
        JsonMapperTest.Pojo original = new JsonMapperTest.Pojo();
        original.bytes = TestUtils.randomByteArray(12);
        JsonMapperTest.Pojo decoded = Json.decodeValue((("{\"bytes\":\"" + (Base64.getEncoder().encodeToString(original.bytes))) + "\"}"), JsonMapperTest.Pojo.class);
        assertArrayEquals(original.bytes, decoded.bytes);
    }

    @Test
    public void testNullBytesDecoding() {
        JsonMapperTest.Pojo original = new JsonMapperTest.Pojo();
        JsonMapperTest.Pojo decoded = Json.decodeValue("{\"bytes\":null}", JsonMapperTest.Pojo.class);
        assertEquals(original.bytes, decoded.bytes);
    }

    private static class Pojo {
        @JsonProperty
        String value;

        @JsonProperty
        Instant instant;

        @JsonProperty
        byte[] bytes;
    }

    private static final TypeReference<Integer> INTEGER_TYPE_REF = new TypeReference<Integer>() {};

    private static final TypeReference<Long> LONG_TYPE_REF = new TypeReference<Long>() {};

    private static final TypeReference<String> STRING_TYPE_REF = new TypeReference<String>() {};

    private static final TypeReference<Float> FLOAT_TYPE_REF = new TypeReference<Float>() {};

    private static final TypeReference<Double> DOUBLE_TYPE_REF = new TypeReference<Double>() {};

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {};

    private static final TypeReference<List<Object>> LIST_TYPE_REF = new TypeReference<List<Object>>() {};

    private static final TypeReference<Boolean> BOOLEAN_TYPE_REF = new TypeReference<Boolean>() {};

    @Test
    public void testDecodeValue() {
        assertDecodeValue(Buffer.buffer("42"), 42, JsonMapperTest.INTEGER_TYPE_REF);
        assertDecodeValue(Buffer.buffer("42"), 42L, JsonMapperTest.LONG_TYPE_REF);
        assertDecodeValue(Buffer.buffer("\"foobar\""), "foobar", JsonMapperTest.STRING_TYPE_REF);
        assertDecodeValue(Buffer.buffer("3.4"), 3.4F, JsonMapperTest.FLOAT_TYPE_REF);
        assertDecodeValue(Buffer.buffer("3.4"), 3.4, JsonMapperTest.DOUBLE_TYPE_REF);
        assertDecodeValue(Buffer.buffer("{\"foo\":4}"), Collections.singletonMap("foo", 4), JsonMapperTest.MAP_TYPE_REF);
        assertDecodeValue(Buffer.buffer("[0,1,2]"), Arrays.asList(0, 1, 2), JsonMapperTest.LIST_TYPE_REF);
        assertDecodeValue(Buffer.buffer("true"), true, JsonMapperTest.BOOLEAN_TYPE_REF);
        assertDecodeValue(Buffer.buffer("false"), false, JsonMapperTest.BOOLEAN_TYPE_REF);
    }

    @Test
    public void testDecodeBufferUnknowContent() {
        testDecodeUnknowContent(true);
    }

    @Test
    public void testDecodeStringUnknowContent() {
        testDecodeUnknowContent(false);
    }
}

