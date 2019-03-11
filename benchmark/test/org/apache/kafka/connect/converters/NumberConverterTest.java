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


import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.DataException;
import org.junit.Assert;
import org.junit.Test;


public abstract class NumberConverterTest<T extends Number> {
    private static final String TOPIC = "topic";

    private static final String HEADER_NAME = "header";

    private T[] samples;

    private Schema schema;

    private NumberConverter<T> converter;

    private Serializer<T> serializer;

    @Test
    public void testConvertingSamplesToAndFromBytes() throws UnsupportedOperationException {
        for (T sample : samples) {
            byte[] expected = serializer.serialize(NumberConverterTest.TOPIC, sample);
            // Data conversion
            Assert.assertArrayEquals(expected, converter.fromConnectData(NumberConverterTest.TOPIC, schema, sample));
            SchemaAndValue data = converter.toConnectData(NumberConverterTest.TOPIC, expected);
            Assert.assertEquals(schema, data.schema());
            Assert.assertEquals(sample, data.value());
            // Header conversion
            Assert.assertArrayEquals(expected, converter.fromConnectHeader(NumberConverterTest.TOPIC, NumberConverterTest.HEADER_NAME, schema, sample));
            data = converter.toConnectHeader(NumberConverterTest.TOPIC, NumberConverterTest.HEADER_NAME, expected);
            Assert.assertEquals(schema, data.schema());
            Assert.assertEquals(sample, data.value());
        }
    }

    @Test(expected = DataException.class)
    public void testDeserializingDataWithTooManyBytes() {
        converter.toConnectData(NumberConverterTest.TOPIC, new byte[10]);
    }

    @Test(expected = DataException.class)
    public void testDeserializingHeaderWithTooManyBytes() {
        converter.toConnectHeader(NumberConverterTest.TOPIC, NumberConverterTest.HEADER_NAME, new byte[10]);
    }

    @Test(expected = DataException.class)
    public void testSerializingIncorrectType() {
        converter.fromConnectData(NumberConverterTest.TOPIC, schema, "not a valid number");
    }

    @Test(expected = DataException.class)
    public void testSerializingIncorrectHeader() {
        converter.fromConnectHeader(NumberConverterTest.TOPIC, NumberConverterTest.HEADER_NAME, schema, "not a valid number");
    }

    @Test
    public void testNullToBytes() {
        Assert.assertEquals(null, converter.fromConnectData(NumberConverterTest.TOPIC, schema, null));
    }

    @Test
    public void testBytesNullToNumber() {
        SchemaAndValue data = converter.toConnectData(NumberConverterTest.TOPIC, null);
        Assert.assertEquals(schema(), data.schema());
        Assert.assertNull(data.value());
    }
}

