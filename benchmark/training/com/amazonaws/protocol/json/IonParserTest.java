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
import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonToken;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonWriter;
import software.amazon.ion.system.IonBinaryWriterBuilder;
import software.amazon.ion.system.IonSystemBuilder;
import software.amazon.ion.system.IonTextWriterBuilder;


/**
 * Tests the {@link IonParser} for conformity with the {@link JsonParser} API.
 * Also tests that the IonParser correctly converts Ion-only value types to
 * the correct {@link JsonToken}s. For testing of additional value types and
 * roundtrip testing with the {@link SdkIonGenerator}, see {@link IonRoundtripTest}.
 */
@RunWith(Parameterized.class)
public class IonParserTest {
    private enum WriteFormat {

        TEXT() {
            @Override
            public byte[] write(String data) throws IOException {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                IonParserTest.WriteFormat.write(data, IonTextWriterBuilder.standard().build(out));
                return out.toByteArray();
            }
        },
        BINARY() {
            @Override
            public byte[] write(String data) throws IOException {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                IonParserTest.WriteFormat.write(data, IonBinaryWriterBuilder.standard().build(out));
                return out.toByteArray();
            }
        };
        public static void write(String data, IonWriter writer) throws IOException {
            IonReader reader = IonParserTest.SYSTEM.newReader(data);
            writer.writeValues(reader);
            writer.close();
        }

        public abstract byte[] write(String data) throws IOException;
    }

    private static IonSystem SYSTEM = IonSystemBuilder.standard().build();

    private IonParserTest.WriteFormat format;

    public IonParserTest(IonParserTest.WriteFormat format) {
        this.format = format;
    }

    @Test
    public void testEmptySexp() throws IOException {
        IonParser parser = parse("()");
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testSexp() throws IOException {
        IonParser parser = parse("(a+b)");
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("a", parser.getText());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("+", parser.getText());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("b", parser.getText());
        Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testNestedSexp() throws IOException {
        IonParser parser = parse("((a)+(b))");
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("a", parser.getText());
        Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("+", parser.getText());
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("b", parser.getText());
        Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testSexpSkip() throws IOException {
        IonParser parser = parse("(a+b)");
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        parser.skipChildren();
        Assert.assertEquals(JsonToken.END_ARRAY, parser.getCurrentToken());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testNestedSexpSkip() throws IOException {
        IonParser parser = parse("((a)+(b))");
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        parser.skipChildren();
        Assert.assertEquals(JsonToken.END_ARRAY, parser.getCurrentToken());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("+", parser.getText());
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        parser.skipChildren();
        Assert.assertEquals(JsonToken.END_ARRAY, parser.getCurrentToken());
        Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testEmptyClob() throws IOException {
        IonParser parser = parse("{{}}");
        Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
        Assert.assertEquals(ByteBuffer.wrap(new byte[0]), parser.getEmbeddedObject());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testClob() throws IOException {
        IonParser parser = parse("{{\"abc123\"}}");
        Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
        Assert.assertEquals(ByteBuffer.wrap("abc123".getBytes(UTF8)), parser.getEmbeddedObject());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testSymbolValue() throws IOException {
        IonParser parser = parse("a1 _1 $foo '123' 'sp ace'");
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("a1", parser.getText());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("_1", parser.getText());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("$foo", parser.getText());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("123", parser.getText());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertEquals("sp ace", parser.getText());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testSkipChildrenNotAtContainerStartDoesNothing() throws IOException {
        IonParser parser = parse("123 (a+b)");
        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        parser.skipChildren();// should do nothing

        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.getCurrentToken());
        Assert.assertEquals(123, parser.getIntValue());
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        parser.skipChildren();// should do nothing

        Assert.assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
        Assert.assertEquals("a", parser.getText());
    }

    @Test
    public void testGetEmbeddedObjectOnBasicValueReturnsNull() throws IOException {
        IonParser parser = parse("123 (a+b) abc");
        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        Assert.assertNull(parser.getEmbeddedObject());
        Assert.assertEquals(123, parser.getIntValue());
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertNull(parser.getEmbeddedObject());
        parser.skipChildren();
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertNull(parser.getEmbeddedObject());
        Assert.assertEquals("abc", parser.getText());
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testNulls() throws IOException {
        IonParser parser = parse(("null " + (((((((((((("null.null " + "null.bool ") + "null.int ") + "null.float ") + "null.decimal ") + "null.timestamp ") + "null.string ") + "null.symbol ") + "null.blob ") + "null.clob ") + "null.struct ") + "null.list ") + "null.sexp")));
        JsonToken token = null;
        int count = 0;
        while ((token = parser.nextToken()) != null) {
            Assert.assertEquals(JsonToken.VALUE_NULL, token);
            count++;
        } 
        Assert.assertEquals(14, count);
    }

    @Test
    public void testNextValue() throws IOException {
        IonParser parser = parse("{foo:{bar:\"abc\"}, baz:123} 42.0");
        Assert.assertEquals(JsonToken.START_OBJECT, parser.nextValue());
        Assert.assertEquals(JsonToken.START_OBJECT, parser.nextValue());
        Assert.assertEquals("foo", parser.getCurrentName());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextValue());
        Assert.assertEquals("abc", parser.getText());
        Assert.assertEquals("bar", parser.getCurrentName());
        Assert.assertEquals(JsonToken.END_OBJECT, parser.nextValue());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextValue());
        Assert.assertEquals(123, parser.getIntValue());
        Assert.assertEquals("baz", parser.getCurrentName());
        Assert.assertEquals(JsonToken.END_OBJECT, parser.nextValue());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextValue());
        Assert.assertEquals(42.0, parser.getFloatValue(), 1.0E-9);
        Assert.assertNull(parser.nextValue());
    }

    @Test
    public void testGetCurrentNameNotAtFieldReturnsNull() throws IOException {
        IonParser parser = parse("{foo:\"abc\"} [a, b] {{}} \"bar\"");
        Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextValue());
        Assert.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
        Assert.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
        Assert.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
        Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
        Assert.assertNull(parser.nextToken());
        Assert.assertNull(parser.getCurrentName());
    }

    @Test
    public void testClearCurrentToken() throws IOException {
        IonParser parser = parse("{}");
        Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        parser.clearCurrentToken();
        Assert.assertNull(parser.getCurrentToken());
        Assert.assertFalse(parser.hasCurrentToken());
        Assert.assertEquals(JsonToken.START_OBJECT, parser.getLastClearedToken());
    }

    @Test
    public void testGetText() throws IOException {
        String defaultText = "default";
        String integer = String.valueOf(123);
        String flt = String.valueOf(42.0);
        IonParser parser = parse((((("{foo:" + integer) + ", bar:") + flt) + "} {{\"abc\"}} null true false"));
        Assert.assertNull(parser.getText());
        Assert.assertEquals(defaultText, parser.getValueAsString(defaultText));
        Assert.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        Assert.assertEquals(JsonToken.START_OBJECT.asString(), parser.getText());// "{"

        Assert.assertEquals(defaultText, parser.getValueAsString(defaultText));
        Assert.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        Assert.assertEquals("foo", parser.getText());
        Assert.assertEquals(defaultText, parser.getValueAsString(defaultText));
        Assert.assertEquals("foo", parser.getCurrentName());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        Assert.assertEquals(integer, parser.getText());
        Assert.assertEquals(integer, parser.getValueAsString(defaultText));
        Assert.assertEquals(123, parser.getIntValue());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextValue());
        Assert.assertEquals(flt, parser.getText());
        Assert.assertEquals(flt, parser.getValueAsString(defaultText));
        Assert.assertEquals(42.0, parser.getFloatValue(), 1.0E-9);
        Assert.assertEquals("bar", parser.getCurrentName());
        Assert.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        Assert.assertEquals(JsonToken.END_OBJECT.asString(), parser.getText());// "}"

        Assert.assertEquals(defaultText, parser.getValueAsString(defaultText));
        Assert.assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
        Assert.assertNull(parser.getText());// embedded objects have undefined text

        Assert.assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_NULL.asString(), parser.getText());// "null"

        Assert.assertEquals(defaultText, parser.getValueAsString(defaultText));
        Assert.assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_TRUE.asString(), parser.getText());// "true"

        Assert.assertEquals(JsonToken.VALUE_TRUE.asString(), parser.getValueAsString(defaultText));
        Assert.assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
        Assert.assertEquals(JsonToken.VALUE_FALSE.asString(), parser.getText());// "false"

        Assert.assertEquals(JsonToken.VALUE_FALSE.asString(), parser.getValueAsString(defaultText));
        Assert.assertNull(parser.nextToken());
        Assert.assertNull(parser.getText());
        Assert.assertEquals(defaultText, parser.getValueAsString(defaultText));
    }

    @Test
    public void testGetNumberValue() throws IOException {
        String integer = String.valueOf(Integer.MAX_VALUE);
        String lng = String.valueOf(Long.MAX_VALUE);
        String bigInteger = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE).toString();
        String flt = String.valueOf(Float.MAX_VALUE);
        String dbl = String.valueOf(Double.MAX_VALUE);
        String inf = "1.7976931348623157E309";// Double.MAX_VALUE * 10;

        String bigDecimal = new BigDecimal(inf).toString();
        IonParser parser = parse(((((((((((((integer + " ") + lng) + " ") + bigInteger) + " ") + flt) + " ") + dbl) + " ") + inf) + " ") + (bigDecimal.toLowerCase().replace("e", "D"))));
        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        Assert.assertEquals(integer, parser.getNumberValue().toString());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        Assert.assertEquals(lng, parser.getNumberValue().toString());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        Assert.assertEquals(bigInteger, parser.getNumberValue().toString());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertEquals(flt, parser.getNumberValue().toString());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertEquals(dbl, parser.getNumberValue().toString());
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertTrue(Double.isInfinite(parser.getDoubleValue()));
        Assert.assertTrue(Double.isInfinite(parser.getFloatValue()));
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertEquals(bigDecimal, parser.getNumberValue().toString());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetNumberValueNotOnNumberFails() throws IOException {
        IonParser parser = parse("foo {{}} {abc:123}");
        Assert.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        Assert.assertNull(parser.getNumberType());
        thrown.expect(AmazonClientException.class);
        parser.getNumberValue();
    }

    @Test
    public void testSpecialFloatValues() throws IOException {
        IonParser parser = parse(("1.7976931348623157E309 "// Double.MAX_VALUE * 10
         + ((("-1.7976931348623157E309 " + "+inf ") + "-inf ") + "nan")));
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertTrue(Double.isInfinite(parser.getDoubleValue()));
        Assert.assertTrue(Double.isInfinite(parser.getFloatValue()));
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertTrue(Double.isInfinite(parser.getDoubleValue()));
        Assert.assertTrue(Double.isInfinite(parser.getFloatValue()));
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertTrue(Double.isInfinite(parser.getDoubleValue()));
        Assert.assertTrue(Double.isInfinite(parser.getFloatValue()));
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertTrue(Double.isInfinite(parser.getDoubleValue()));
        Assert.assertTrue(Double.isInfinite(parser.getFloatValue()));
        Assert.assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        Assert.assertTrue(Double.isNaN(parser.getDoubleValue()));
        Assert.assertTrue(Double.isNaN(parser.getFloatValue()));
    }
}

