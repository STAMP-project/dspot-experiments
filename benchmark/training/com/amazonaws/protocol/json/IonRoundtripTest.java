/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.protocol.json;


import StringUtils.UTF8;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import software.amazon.ion.IonException;
import software.amazon.ion.IonSystem;
import software.amazon.ion.system.IonBinaryWriterBuilder;
import software.amazon.ion.system.IonSystemBuilder;


/**
 * Tests that data written by the {@link SdkIonGenerator} is correctly read
 * by the {@link IonParser}. For additional stand-alone testing of the
 * {@link IonParser}, see {@link IonParserTest}.
 */
@RunWith(Parameterized.class)
public class IonRoundtripTest {
    private enum Data {

        NULL() {
            @Override
            public void generate(SdkIonGenerator generator) {
                // Is this the only way to write a null value?
                generator.writeValue(((String) (null)));
                generator.writeValue(((BigInteger) (null)));
                generator.writeValue(((BigDecimal) (null)));
                generator.writeValue(((Date) (null)), null);
                generator.writeValue(((ByteBuffer) (null)));
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                Assert.assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                Assert.assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                Assert.assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                Assert.assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            }
        },
        BOOL() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(true);
                generator.writeValue(false);
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
                Assert.assertEquals(true, parser.getBooleanValue());
                Assert.assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
                Assert.assertEquals(false, parser.getBooleanValue());
            }
        },
        SHORT() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(0);
                // There's no writeValue(byte) method, but there is writeValue(short)...
                generator.writeValue(Byte.MAX_VALUE);
                generator.writeValue(Byte.MIN_VALUE);
                generator.writeValue(Short.MAX_VALUE);
                generator.writeValue(Short.MIN_VALUE);
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(0, parser.getIntValue());
                Assert.assertEquals(0, parser.getLongValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Byte.MAX_VALUE, parser.getByteValue());
                Assert.assertEquals(((short) (Byte.MAX_VALUE)), parser.getShortValue());
                Assert.assertEquals(((int) (Byte.MAX_VALUE)), parser.getIntValue());
                Assert.assertEquals(((long) (Byte.MAX_VALUE)), parser.getLongValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Byte.MIN_VALUE, parser.getByteValue());
                Assert.assertEquals(((short) (Byte.MIN_VALUE)), parser.getShortValue());
                Assert.assertEquals(((int) (Byte.MIN_VALUE)), parser.getIntValue());
                Assert.assertEquals(((long) (Byte.MIN_VALUE)), parser.getLongValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Short.MAX_VALUE, parser.getShortValue());
                Assert.assertEquals(((int) (Short.MAX_VALUE)), parser.getIntValue());
                Assert.assertEquals(((long) (Short.MAX_VALUE)), parser.getLongValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Short.MIN_VALUE, parser.getShortValue());
                Assert.assertEquals(((int) (Short.MIN_VALUE)), parser.getIntValue());
                Assert.assertEquals(((long) (Short.MIN_VALUE)), parser.getLongValue());
            }
        },
        INT() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(Integer.MAX_VALUE);
                generator.writeValue(Integer.MIN_VALUE);
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Integer.MAX_VALUE, parser.getIntValue());
                Assert.assertEquals(((long) (Integer.MAX_VALUE)), parser.getLongValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Integer.MIN_VALUE, parser.getIntValue());
                Assert.assertEquals(((long) (Integer.MIN_VALUE)), parser.getLongValue());
            }
        },
        LONG() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(Long.MAX_VALUE);
                generator.writeValue(Long.MIN_VALUE);
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Long.MAX_VALUE, parser.getLongValue());
                Assert.assertEquals(BigInteger.valueOf(Long.MAX_VALUE), parser.getBigIntegerValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(Long.MIN_VALUE, parser.getLongValue());
                Assert.assertEquals(BigInteger.valueOf(Long.MIN_VALUE), parser.getBigIntegerValue());
            }
        },
        BIG_INTEGER() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
                generator.writeValue(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE));
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE), parser.getBigIntegerValue());
                try {
                    parser.getLongValue();
                } catch (IonException e1) {
                    Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    Assert.assertEquals(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE), parser.getBigIntegerValue());
                    try {
                        parser.getLongValue();
                    } catch (IonException e2) {
                        return;
                    }
                }
                throw new AssertionError("number shouldn't fit in a long");
            }
        },
        FLOAT() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(Float.MAX_VALUE);
                generator.writeValue(Float.MIN_VALUE);
                generator.writeValue((-(Float.MAX_VALUE)));
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals(Float.MAX_VALUE, parser.getFloatValue(), 1.0E-9);
                Assert.assertEquals(((double) (Float.MAX_VALUE)), parser.getDoubleValue(), 1.0E-9);
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals(Float.MIN_VALUE, parser.getFloatValue(), 1.0E-9);
                Assert.assertEquals(((double) (Float.MIN_VALUE)), parser.getDoubleValue(), 1.0E-9);
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals((-(Float.MAX_VALUE)), parser.getFloatValue(), 1.0E-9);
                Assert.assertEquals(((double) (-(Float.MAX_VALUE))), parser.getDoubleValue(), 1.0E-9);
            }
        },
        DOUBLE() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(Double.MAX_VALUE);
                generator.writeValue(Double.MIN_VALUE);
                generator.writeValue((-(Double.MAX_VALUE)));
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals(Double.MAX_VALUE, parser.getDoubleValue(), 1.0E-9);
                Assert.assertEquals(BigDecimal.valueOf(Double.MAX_VALUE), parser.getDecimalValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals(Float.MIN_VALUE, parser.getDoubleValue(), 1.0E-9);
                Assert.assertEquals(BigDecimal.valueOf(Double.MIN_VALUE), parser.getDecimalValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals((-(Double.MAX_VALUE)), parser.getDoubleValue(), 1.0E-9);
                Assert.assertEquals(BigDecimal.valueOf((-(Double.MAX_VALUE))), parser.getDecimalValue());
            }
        },
        BIG_DECIMAL() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.ONE));
                generator.writeValue(BigDecimal.valueOf((-(Double.MAX_VALUE))).subtract(BigDecimal.ONE));
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals(BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.ONE), parser.getDecimalValue());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                Assert.assertEquals(BigDecimal.valueOf((-(Double.MAX_VALUE))).subtract(BigDecimal.ONE), parser.getDecimalValue());
            }
        },
        TIMESTAMP() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(new Date(0), null);
                // Note: dates too far in the future are rejected by Ion
                generator.writeValue(new Date(Integer.MAX_VALUE), null);
                generator.writeValue(new Date(Integer.MIN_VALUE), null);
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
                Assert.assertEquals(new Date(0), parser.getEmbeddedObject());
                Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
                Assert.assertEquals(new Date(Integer.MAX_VALUE), parser.getEmbeddedObject());
                Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
                Assert.assertEquals(new Date(Integer.MIN_VALUE), parser.getEmbeddedObject());
            }
        },
        BYTES() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(ByteBuffer.wrap("foobar".getBytes(UTF8)));
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
                Assert.assertEquals(ByteBuffer.wrap("foobar".getBytes(UTF8)), parser.getEmbeddedObject());
            }
        },
        EMPTY_STRUCT() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeStartObject();
                generator.writeEndObject();
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                Assert.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            }
        },
        EMPTY_LIST() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeStartArray();
                generator.writeEndArray();
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            }
        },
        STRUCT() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeStartObject();
                generator.writeFieldName("int");
                generator.writeValue(1);
                generator.writeFieldName("string");
                generator.writeValue("foo");
                generator.writeFieldName("bool");
                generator.writeValue(false);
                generator.writeEndObject();
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                Assert.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
                Assert.assertEquals("int", parser.getText());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(1, parser.getIntValue());
                Assert.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
                Assert.assertEquals("string", parser.getText());
                Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                Assert.assertEquals("foo", parser.getText());
                Assert.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
                Assert.assertEquals("bool", parser.getText());
                Assert.assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
                Assert.assertEquals(false, parser.getBooleanValue());
                Assert.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            }
        },
        LIST() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeStartArray();
                generator.writeValue(1);
                generator.writeValue("foo");
                generator.writeValue(true);
                generator.writeEndArray();
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(1, parser.getIntValue());
                Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                Assert.assertEquals("foo", parser.getText());
                Assert.assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
                Assert.assertEquals(true, parser.getBooleanValue());
                Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            }
        },
        STRUCT_IN_LIST() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeStartArray();
                IonRoundtripTest.Data.STRUCT.generate(generator);
                generator.writeEndArray();
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                IonRoundtripTest.Data.STRUCT.parse(parser);
                Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            }
        },
        LIST_IN_STRUCT() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeStartObject();
                generator.writeFieldName("list");
                IonRoundtripTest.Data.LIST.generate(generator);
                generator.writeEndObject();
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                Assert.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
                Assert.assertEquals("list", parser.getText());
                IonRoundtripTest.Data.LIST.parse(parser);
                Assert.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            }
        },
        STRUCT_SKIP() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(42);
                IonRoundtripTest.Data.STRUCT.generate(generator);
                generator.writeValue("foo");
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(42, parser.getIntValue());
                Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                parser.skipChildren();
                Assert.assertEquals(JsonToken.END_OBJECT, parser.getCurrentToken());
                Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                Assert.assertEquals("foo", parser.getText());
            }
        },
        LIST_SKIP() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(42);
                IonRoundtripTest.Data.LIST.generate(generator);
                generator.writeValue("foo");
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(42, parser.getIntValue());
                Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parser.skipChildren();
                Assert.assertEquals(JsonToken.END_ARRAY, parser.getCurrentToken());
                Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                Assert.assertEquals("foo", parser.getText());
            }
        },
        NESTED_SKIP() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeValue(42);
                IonRoundtripTest.Data.LIST_IN_STRUCT.generate(generator);
                generator.writeValue("foo");
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(42, parser.getIntValue());
                Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                parser.skipChildren();
                Assert.assertEquals(JsonToken.END_OBJECT, parser.getCurrentToken());
                Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                Assert.assertEquals("foo", parser.getText());
            }
        },
        NESTED_INNER_SKIP() {
            @Override
            public void generate(SdkIonGenerator generator) {
                generator.writeStartArray();
                generator.writeValue(42);
                IonRoundtripTest.Data.STRUCT.generate(generator);
                generator.writeValue("foo");
                generator.writeEndArray();
            }

            @Override
            public void parse(IonParser parser) throws IOException {
                Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                Assert.assertEquals(42, parser.getIntValue());
                Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                parser.skipChildren();
                Assert.assertEquals(JsonToken.END_OBJECT, parser.getCurrentToken());
                Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                Assert.assertEquals("foo", parser.getText());
                Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            }
        };
        public abstract void generate(SdkIonGenerator generator);

        public abstract void parse(IonParser parser) throws IOException;
    }

    private static final IonSystem SYSTEM = IonSystemBuilder.standard().build();

    private final IonRoundtripTest.Data data;

    public IonRoundtripTest(IonRoundtripTest.Data data) {
        this.data = data;
    }

    @Test
    public void testRoundtrip() throws IOException {
        SdkIonGenerator generator = SdkIonGenerator.create(IonBinaryWriterBuilder.standard(), "foo");
        data.generate(generator);
        IonParser parser = new IonParser(IonRoundtripTest.SYSTEM.newReader(generator.getBytes()), false);
        data.parse(parser);
        Assert.assertNull(parser.nextToken());// Asserts data was read fully.

        Assert.assertFalse(parser.hasCurrentToken());
        Assert.assertFalse(parser.isClosed());
        parser.close();
        Assert.assertTrue(parser.isClosed());
    }
}

