/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.storage;


import Schema.BOOLEAN_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.junit.Assert;
import org.junit.Test;


public class StringConverterTest {
    private static final String TOPIC = "topic";

    private static final String SAMPLE_STRING = "a string";

    private StringConverter converter = new StringConverter();

    @Test
    public void testStringToBytes() throws UnsupportedEncodingException {
        Assert.assertArrayEquals(StringConverterTest.SAMPLE_STRING.getBytes("UTF8"), converter.fromConnectData(StringConverterTest.TOPIC, STRING_SCHEMA, StringConverterTest.SAMPLE_STRING));
    }

    @Test
    public void testNonStringToBytes() throws UnsupportedEncodingException {
        Assert.assertArrayEquals("true".getBytes("UTF8"), converter.fromConnectData(StringConverterTest.TOPIC, BOOLEAN_SCHEMA, true));
    }

    @Test
    public void testNullToBytes() {
        Assert.assertEquals(null, converter.fromConnectData(StringConverterTest.TOPIC, OPTIONAL_STRING_SCHEMA, null));
    }

    @Test
    public void testToBytesIgnoresSchema() throws UnsupportedEncodingException {
        Assert.assertArrayEquals("true".getBytes("UTF8"), converter.fromConnectData(StringConverterTest.TOPIC, null, true));
    }

    @Test
    public void testToBytesNonUtf8Encoding() throws UnsupportedEncodingException {
        converter.configure(Collections.singletonMap("converter.encoding", "UTF-16"), true);
        Assert.assertArrayEquals(StringConverterTest.SAMPLE_STRING.getBytes("UTF-16"), converter.fromConnectData(StringConverterTest.TOPIC, STRING_SCHEMA, StringConverterTest.SAMPLE_STRING));
    }

    @Test
    public void testBytesToString() {
        SchemaAndValue data = converter.toConnectData(StringConverterTest.TOPIC, StringConverterTest.SAMPLE_STRING.getBytes());
        Assert.assertEquals(OPTIONAL_STRING_SCHEMA, data.schema());
        Assert.assertEquals(StringConverterTest.SAMPLE_STRING, data.value());
    }

    @Test
    public void testBytesNullToString() {
        SchemaAndValue data = converter.toConnectData(StringConverterTest.TOPIC, null);
        Assert.assertEquals(OPTIONAL_STRING_SCHEMA, data.schema());
        Assert.assertEquals(null, data.value());
    }

    @Test
    public void testBytesToStringNonUtf8Encoding() throws UnsupportedEncodingException {
        converter.configure(Collections.singletonMap("converter.encoding", "UTF-16"), true);
        SchemaAndValue data = converter.toConnectData(StringConverterTest.TOPIC, StringConverterTest.SAMPLE_STRING.getBytes("UTF-16"));
        Assert.assertEquals(OPTIONAL_STRING_SCHEMA, data.schema());
        Assert.assertEquals(StringConverterTest.SAMPLE_STRING, data.value());
    }

    // Note: the header conversion methods delegates to the data conversion methods, which are tested above.
    // The following simply verify that the delegation works.
    @Test
    public void testStringHeaderValueToBytes() throws UnsupportedEncodingException {
        Assert.assertArrayEquals(StringConverterTest.SAMPLE_STRING.getBytes("UTF8"), converter.fromConnectHeader(StringConverterTest.TOPIC, "hdr", STRING_SCHEMA, StringConverterTest.SAMPLE_STRING));
    }

    @Test
    public void testNonStringHeaderValueToBytes() throws UnsupportedEncodingException {
        Assert.assertArrayEquals("true".getBytes("UTF8"), converter.fromConnectHeader(StringConverterTest.TOPIC, "hdr", BOOLEAN_SCHEMA, true));
    }

    @Test
    public void testNullHeaderValueToBytes() {
        Assert.assertEquals(null, converter.fromConnectHeader(StringConverterTest.TOPIC, "hdr", OPTIONAL_STRING_SCHEMA, null));
    }
}

