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
package org.apache.kafka.connect.converters;


import Schema.BYTES_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.OPTIONAL_BYTES_SCHEMA;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.DataException;
import org.junit.Assert;
import org.junit.Test;


public class ByteArrayConverterTest {
    private static final String TOPIC = "topic";

    private static final byte[] SAMPLE_BYTES = "sample string".getBytes(StandardCharsets.UTF_8);

    private ByteArrayConverter converter = new ByteArrayConverter();

    @Test
    public void testFromConnect() {
        Assert.assertArrayEquals(ByteArrayConverterTest.SAMPLE_BYTES, converter.fromConnectData(ByteArrayConverterTest.TOPIC, BYTES_SCHEMA, ByteArrayConverterTest.SAMPLE_BYTES));
    }

    @Test
    public void testFromConnectSchemaless() {
        Assert.assertArrayEquals(ByteArrayConverterTest.SAMPLE_BYTES, converter.fromConnectData(ByteArrayConverterTest.TOPIC, null, ByteArrayConverterTest.SAMPLE_BYTES));
    }

    @Test(expected = DataException.class)
    public void testFromConnectBadSchema() {
        converter.fromConnectData(ByteArrayConverterTest.TOPIC, INT32_SCHEMA, ByteArrayConverterTest.SAMPLE_BYTES);
    }

    @Test(expected = DataException.class)
    public void testFromConnectInvalidValue() {
        converter.fromConnectData(ByteArrayConverterTest.TOPIC, BYTES_SCHEMA, 12);
    }

    @Test
    public void testFromConnectNull() {
        Assert.assertNull(converter.fromConnectData(ByteArrayConverterTest.TOPIC, BYTES_SCHEMA, null));
    }

    @Test
    public void testToConnect() {
        SchemaAndValue data = converter.toConnectData(ByteArrayConverterTest.TOPIC, ByteArrayConverterTest.SAMPLE_BYTES);
        Assert.assertEquals(OPTIONAL_BYTES_SCHEMA, data.schema());
        Assert.assertTrue(Arrays.equals(ByteArrayConverterTest.SAMPLE_BYTES, ((byte[]) (data.value()))));
    }

    @Test
    public void testToConnectNull() {
        SchemaAndValue data = converter.toConnectData(ByteArrayConverterTest.TOPIC, null);
        Assert.assertEquals(OPTIONAL_BYTES_SCHEMA, data.schema());
        Assert.assertNull(data.value());
    }
}

