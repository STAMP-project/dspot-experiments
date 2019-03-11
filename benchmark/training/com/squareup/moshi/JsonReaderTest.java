/**
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.moshi;


import JsonReader.Options;
import JsonReader.Token.BOOLEAN;
import JsonReader.Token.END_DOCUMENT;
import JsonReader.Token.END_OBJECT;
import JsonReader.Token.NAME;
import JsonReader.Token.NUMBER;
import JsonReader.Token.STRING;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@SuppressWarnings("CheckReturnValue")
public final class JsonReaderTest {
    @Parameterized.Parameter
    public JsonCodecFactory factory;

    @Test
    public void readArray() throws IOException {
        JsonReader reader = newReader("[true, true]");
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.nextBoolean()).isTrue();
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void readEmptyArray() throws IOException {
        JsonReader reader = newReader("[]");
        reader.beginArray();
        assertThat(reader.hasNext()).isFalse();
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void readObject() throws IOException {
        JsonReader reader = newReader("{\"a\": \"android\", \"b\": \"banana\"}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("android");
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.nextString()).isEqualTo("banana");
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void readEmptyObject() throws IOException {
        JsonReader reader = newReader("{}");
        reader.beginObject();
        assertThat(reader.hasNext()).isFalse();
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipArray() throws IOException {
        JsonReader reader = newReader("{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        reader.skipValue();
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.nextInt()).isEqualTo(123);
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipArrayAfterPeek() throws Exception {
        JsonReader reader = newReader("{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.peek()).isEqualTo(Token.BEGIN_ARRAY);
        reader.skipValue();
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.nextInt()).isEqualTo(123);
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipTopLevelObject() throws Exception {
        JsonReader reader = newReader("{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}");
        reader.skipValue();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipObject() throws IOException {
        JsonReader reader = newReader("{\"a\": { \"c\": [], \"d\": [true, true, {}] }, \"b\": \"banana\"}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        reader.skipValue();
        assertThat(reader.nextName()).isEqualTo("b");
        reader.skipValue();
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipObjectAfterPeek() throws Exception {
        String json = "{" + ((("  \"one\": { \"num\": 1 }" + ", \"two\": { \"num\": 2 }") + ", \"three\": { \"num\": 3 }") + "}");
        JsonReader reader = newReader(json);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("one");
        assertThat(reader.peek()).isEqualTo(Token.BEGIN_OBJECT);
        reader.skipValue();
        assertThat(reader.nextName()).isEqualTo("two");
        assertThat(reader.peek()).isEqualTo(Token.BEGIN_OBJECT);
        reader.skipValue();
        assertThat(reader.nextName()).isEqualTo("three");
        reader.skipValue();
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipInteger() throws IOException {
        JsonReader reader = newReader("{\"a\":123456789,\"b\":-123456789}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        reader.skipValue();
        assertThat(reader.nextName()).isEqualTo("b");
        reader.skipValue();
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipDouble() throws IOException {
        JsonReader reader = newReader("{\"a\":-123.456e-789,\"b\":123456789.0}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        reader.skipValue();
        assertThat(reader.nextName()).isEqualTo("b");
        reader.skipValue();
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void failOnUnknownFailsOnUnknownObjectValue() throws IOException {
        JsonReader reader = newReader("{\"a\": 123}");
        reader.setFailOnUnknown(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Cannot skip unexpected NUMBER at $.a");
        }
        // Confirm that the reader is left in a consistent state after the exception.
        reader.setFailOnUnknown(false);
        assertThat(reader.nextInt()).isEqualTo(123);
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void failOnUnknownFailsOnUnknownArrayElement() throws IOException {
        JsonReader reader = newReader("[\"a\", 123]");
        reader.setFailOnUnknown(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("a");
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Cannot skip unexpected NUMBER at $[1]");
        }
        // Confirm that the reader is left in a consistent state after the exception.
        reader.setFailOnUnknown(false);
        assertThat(reader.nextInt()).isEqualTo(123);
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void helloWorld() throws IOException {
        String json = "{\n" + (("   \"hello\": true,\n" + "   \"foo\": [\"world\"]\n") + "}");
        JsonReader reader = newReader(json);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("hello");
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.nextName()).isEqualTo("foo");
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("world");
        reader.endArray();
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void emptyString() throws Exception {
        try {
            newReader("").beginArray();
            Assert.fail();
        } catch (EOFException expected) {
        }
        try {
            newReader("").beginObject();
            Assert.fail();
        } catch (EOFException expected) {
        }
    }

    @Test
    public void characterUnescaping() throws IOException {
        String json = "[\"a\"," + (((((((((((((((((("\"a\\\"\"," + "\"\\\"\",") + "\":\",") + "\",\",") + "\"\\b\",") + "\"\\f\",") + "\"\\n\",") + "\"\\r\",") + "\"\\t\",") + "\" \",") + "\"\\\\\",") + "\"{\",") + "\"}\",") + "\"[\",") + "\"]\",") + "\"\\u0000\",") + "\"\\u0019\",") + "\"\\u20AC\"") + "]");
        JsonReader reader = newReader(json);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("a\"");
        assertThat(reader.nextString()).isEqualTo("\"");
        assertThat(reader.nextString()).isEqualTo(":");
        assertThat(reader.nextString()).isEqualTo(",");
        assertThat(reader.nextString()).isEqualTo("\b");
        assertThat(reader.nextString()).isEqualTo("\f");
        assertThat(reader.nextString()).isEqualTo("\n");
        assertThat(reader.nextString()).isEqualTo("\r");
        assertThat(reader.nextString()).isEqualTo("\t");
        assertThat(reader.nextString()).isEqualTo(" ");
        assertThat(reader.nextString()).isEqualTo("\\");
        assertThat(reader.nextString()).isEqualTo("{");
        assertThat(reader.nextString()).isEqualTo("}");
        assertThat(reader.nextString()).isEqualTo("[");
        assertThat(reader.nextString()).isEqualTo("]");
        assertThat(reader.nextString()).isEqualTo("\u0000");
        assertThat(reader.nextString()).isEqualTo("\u0019");
        assertThat(reader.nextString()).isEqualTo("\u20ac");
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void integersWithFractionalPartSpecified() throws IOException {
        JsonReader reader = newReader("[1.0,1.0,1.0]");
        reader.beginArray();
        assertThat(reader.nextDouble()).isEqualTo(1.0);
        assertThat(reader.nextInt()).isEqualTo(1);
        assertThat(reader.nextLong()).isEqualTo(1L);
    }

    @Test
    public void doubles() throws IOException {
        String json = "[-0.0," + ((((((("1.0," + "1.7976931348623157E308,") + "4.9E-324,") + "0.0,") + "-0.5,") + "2.2250738585072014E-308,") + "3.141592653589793,") + "2.718281828459045]");
        JsonReader reader = newReader(json);
        reader.beginArray();
        assertThat(reader.nextDouble()).isEqualTo((-0.0));
        assertThat(reader.nextDouble()).isEqualTo(1.0);
        assertThat(reader.nextDouble()).isEqualTo(1.7976931348623157E308);
        assertThat(reader.nextDouble()).isEqualTo(4.9E-324);
        assertThat(reader.nextDouble()).isEqualTo(0.0);
        assertThat(reader.nextDouble()).isEqualTo((-0.5));
        assertThat(reader.nextDouble()).isEqualTo(2.2250738585072014E-308);
        assertThat(reader.nextDouble()).isEqualTo(3.141592653589793);
        assertThat(reader.nextDouble()).isEqualTo(2.718281828459045);
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void strictNonFiniteDoubles() throws IOException {
        String json = "[NaN]";
        JsonReader reader = newReader(json);
        reader.beginArray();
        try {
            reader.nextDouble();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictQuotedNonFiniteDoubles() throws IOException {
        String json = "[\"NaN\"]";
        JsonReader reader = newReader(json);
        reader.beginArray();
        try {
            reader.nextDouble();
            Assert.fail();
        } catch (JsonEncodingException expected) {
            assertThat(expected).hasMessageContaining("NaN");
        }
    }

    @Test
    public void lenientNonFiniteDoubles() throws IOException {
        String json = "[NaN, -Infinity, Infinity]";
        JsonReader reader = newReader(json);
        reader.setLenient(true);
        reader.beginArray();
        assertThat(Double.isNaN(reader.nextDouble())).isTrue();
        assertThat(reader.nextDouble()).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(reader.nextDouble()).isEqualTo(Double.POSITIVE_INFINITY);
        reader.endArray();
    }

    @Test
    public void lenientQuotedNonFiniteDoubles() throws IOException {
        String json = "[\"NaN\", \"-Infinity\", \"Infinity\"]";
        JsonReader reader = newReader(json);
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextDouble()).isNaN();
        assertThat(reader.nextDouble()).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(reader.nextDouble()).isEqualTo(Double.POSITIVE_INFINITY);
        reader.endArray();
    }

    @Test
    public void longs() throws IOException {
        Assume.assumeTrue(factory.implementsStrictPrecision());
        String json = "[0,0,0," + ((("1,1,1," + "-1,-1,-1,") + "-9223372036854775808,") + "9223372036854775807]");
        JsonReader reader = newReader(json);
        reader.beginArray();
        assertThat(reader.nextLong()).isEqualTo(0L);
        assertThat(reader.nextInt()).isEqualTo(0);
        assertThat(reader.nextDouble()).isEqualTo(0.0);
        assertThat(reader.nextLong()).isEqualTo(1L);
        assertThat(reader.nextInt()).isEqualTo(1);
        assertThat(reader.nextDouble()).isEqualTo(1.0);
        assertThat(reader.nextLong()).isEqualTo((-1L));
        assertThat(reader.nextInt()).isEqualTo((-1));
        assertThat(reader.nextDouble()).isEqualTo((-1.0));
        try {
            reader.nextInt();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextLong()).isEqualTo(Long.MIN_VALUE);
        try {
            reader.nextInt();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextLong()).isEqualTo(Long.MAX_VALUE);
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void booleans() throws IOException {
        JsonReader reader = newReader("[true,false]");
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.nextBoolean()).isFalse();
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void nextFailuresDoNotAdvance() throws IOException {
        JsonReader reader = newReader("{\"a\":true}");
        reader.beginObject();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextName();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        try {
            reader.beginArray();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        try {
            reader.endArray();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        try {
            reader.beginObject();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        try {
            reader.endObject();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        try {
            reader.nextName();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        try {
            reader.beginArray();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        try {
            reader.endArray();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
        reader.close();
    }

    @Test
    public void integerMismatchWithDoubleDoesNotAdvance() throws IOException {
        Assume.assumeTrue(factory.implementsStrictPrecision());
        JsonReader reader = newReader("[1.5]");
        reader.beginArray();
        try {
            reader.nextInt();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextDouble()).isEqualTo(1.5);
        reader.endArray();
    }

    @Test
    public void integerMismatchWithLongDoesNotAdvance() throws IOException {
        Assume.assumeTrue(factory.implementsStrictPrecision());
        JsonReader reader = newReader("[9223372036854775807]");
        reader.beginArray();
        try {
            reader.nextInt();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextLong()).isEqualTo(9223372036854775807L);
        reader.endArray();
    }

    @Test
    public void longMismatchWithDoubleDoesNotAdvance() throws IOException {
        Assume.assumeTrue(factory.implementsStrictPrecision());
        JsonReader reader = newReader("[1.5]");
        reader.beginArray();
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextDouble()).isEqualTo(1.5);
        reader.endArray();
    }

    @Test
    public void stringNullIsNotNull() throws IOException {
        JsonReader reader = newReader("[\"null\"]");
        reader.beginArray();
        try {
            reader.nextNull();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
    }

    @Test
    public void nullLiteralIsNotAString() throws IOException {
        JsonReader reader = newReader("[null]");
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
    }

    @Test
    public void topLevelValueTypes() throws IOException {
        JsonReader reader1 = newReader("true");
        assertThat(reader1.nextBoolean()).isTrue();
        assertThat(reader1.peek()).isEqualTo(END_DOCUMENT);
        JsonReader reader2 = newReader("false");
        assertThat(reader2.nextBoolean()).isFalse();
        assertThat(reader2.peek()).isEqualTo(END_DOCUMENT);
        JsonReader reader3 = newReader("null");
        assertThat(reader3.nextNull()).isNull();
        assertThat(reader3.peek()).isEqualTo(END_DOCUMENT);
        JsonReader reader4 = newReader("123");
        assertThat(reader4.nextInt()).isEqualTo(123);
        assertThat(reader4.peek()).isEqualTo(END_DOCUMENT);
        JsonReader reader5 = newReader("123.4");
        assertThat(reader5.nextDouble()).isEqualTo(123.4);
        assertThat(reader5.peek()).isEqualTo(END_DOCUMENT);
        JsonReader reader6 = newReader("\"a\"");
        assertThat(reader6.nextString()).isEqualTo("a");
        assertThat(reader6.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void topLevelValueTypeWithSkipValue() throws IOException {
        JsonReader reader = newReader("true");
        reader.skipValue();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void deeplyNestedArrays() throws IOException {
        JsonReader reader = newReader("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
        for (int i = 0; i < 31; i++) {
            reader.beginArray();
        }
        assertThat(reader.getPath()).isEqualTo(("$[0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0]" + "[0][0][0][0][0][0][0][0][0][0][0][0][0]"));
        for (int i = 0; i < 31; i++) {
            reader.endArray();
        }
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void deeplyNestedObjects() throws IOException {
        // Build a JSON document structured like {"a":{"a":{"a":{"a":true}}}}, but 31 levels deep.
        String array = "{\"a\":%s}";
        String json = "true";
        for (int i = 0; i < 31; i++) {
            json = String.format(array, json);
        }
        JsonReader reader = newReader(json);
        for (int i = 0; i < 31; i++) {
            reader.beginObject();
            assertThat(reader.nextName()).isEqualTo("a");
        }
        assertThat(reader.getPath()).isEqualTo("$.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a");
        assertThat(reader.nextBoolean()).isTrue();
        for (int i = 0; i < 31; i++) {
            reader.endObject();
        }
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipVeryLongUnquotedString() throws IOException {
        JsonReader reader = newReader((("[" + (TestUtil.repeat('x', 8192))) + "]"));
        reader.setLenient(true);
        reader.beginArray();
        reader.skipValue();
        reader.endArray();
    }

    @Test
    public void skipTopLevelUnquotedString() throws IOException {
        JsonReader reader = newReader(TestUtil.repeat('x', 8192));
        reader.setLenient(true);
        reader.skipValue();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipVeryLongQuotedString() throws IOException {
        JsonReader reader = newReader((("[\"" + (TestUtil.repeat('x', 8192))) + "\"]"));
        reader.beginArray();
        reader.skipValue();
        reader.endArray();
    }

    @Test
    public void skipTopLevelQuotedString() throws IOException {
        JsonReader reader = newReader((("\"" + (TestUtil.repeat('x', 8192))) + "\""));
        reader.setLenient(true);
        reader.skipValue();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void stringAsNumberWithTruncatedExponent() throws IOException {
        JsonReader reader = newReader("[123e]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.STRING);
    }

    @Test
    public void stringAsNumberWithDigitAndNonDigitExponent() throws IOException {
        JsonReader reader = newReader("[123e4b]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.STRING);
    }

    @Test
    public void stringAsNumberWithNonDigitExponent() throws IOException {
        JsonReader reader = newReader("[123eb]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.STRING);
    }

    @Test
    public void emptyStringName() throws IOException {
        JsonReader reader = newReader("{\"\":true}");
        reader.setLenient(true);
        assertThat(reader.peek()).isEqualTo(Token.BEGIN_OBJECT);
        reader.beginObject();
        assertThat(reader.peek()).isEqualTo(Token.NAME);
        assertThat(reader.nextName()).isEqualTo("");
        assertThat(reader.peek()).isEqualTo(BOOLEAN);
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.peek()).isEqualTo(END_OBJECT);
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void validEscapes() throws IOException {
        JsonReader reader = newReader("[\"\\\"\\\\\\/\\b\\f\\n\\r\\t\"]");
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("\"\\/\b\f\n\r\t");
    }

    @Test
    public void selectName() throws IOException {
        JsonReader.Options abc = Options.of("a", "b", "c");
        JsonReader reader = newReader("{\"a\": 5, \"b\": 5, \"c\": 5, \"d\": 5}");
        reader.beginObject();
        Assert.assertEquals("$.", reader.getPath());
        Assert.assertEquals(0, reader.selectName(abc));
        Assert.assertEquals("$.a", reader.getPath());
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.a", reader.getPath());
        Assert.assertEquals(1, reader.selectName(abc));
        Assert.assertEquals("$.b", reader.getPath());
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.b", reader.getPath());
        Assert.assertEquals(2, reader.selectName(abc));
        Assert.assertEquals("$.c", reader.getPath());
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.c", reader.getPath());
        // A missed selectName() doesn't advance anything, not even the path.
        Assert.assertEquals((-1), reader.selectName(abc));
        Assert.assertEquals("$.c", reader.getPath());
        Assert.assertEquals(NAME, reader.peek());
        Assert.assertEquals("d", reader.nextName());
        Assert.assertEquals("$.d", reader.getPath());
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.d", reader.getPath());
        reader.endObject();
    }

    /**
     * Select does match necessarily escaping. The decoded value is used in the path.
     */
    @Test
    public void selectNameNecessaryEscaping() throws IOException {
        JsonReader.Options options = Options.of("\n", "\u0000", "\"");
        JsonReader reader = newReader("{\"\\n\": 5,\"\\u0000\": 5, \"\\\"\": 5}");
        reader.beginObject();
        Assert.assertEquals(0, reader.selectName(options));
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.\n", reader.getPath());
        Assert.assertEquals(1, reader.selectName(options));
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.\u0000", reader.getPath());
        Assert.assertEquals(2, reader.selectName(options));
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.\"", reader.getPath());
        reader.endObject();
    }

    /**
     * Select removes unnecessary escaping from the source JSON.
     */
    @Test
    public void selectNameUnnecessaryEscaping() throws IOException {
        JsonReader.Options options = Options.of("coffee", "tea");
        JsonReader reader = newReader("{\"cof\\u0066ee\":5, \"\\u0074e\\u0061\":4, \"water\":3}");
        reader.beginObject();
        Assert.assertEquals(0, reader.selectName(options));
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.coffee", reader.getPath());
        Assert.assertEquals(1, reader.selectName(options));
        Assert.assertEquals(4, reader.nextInt());
        Assert.assertEquals("$.tea", reader.getPath());
        // Ensure select name doesn't advance the stack in case there are no matches.
        Assert.assertEquals((-1), reader.selectName(options));
        Assert.assertEquals(NAME, reader.peek());
        Assert.assertEquals("$.tea", reader.getPath());
        // Consume the last token.
        Assert.assertEquals("water", reader.nextName());
        Assert.assertEquals(3, reader.nextInt());
        reader.endObject();
    }

    @Test
    public void selectNameUnquoted() throws Exception {
        JsonReader.Options options = Options.of("a", "b");
        JsonReader reader = newReader("{a:2}");
        reader.setLenient(true);
        reader.beginObject();
        Assert.assertEquals(0, reader.selectName(options));
        Assert.assertEquals("$.a", reader.getPath());
        Assert.assertEquals(2, reader.nextInt());
        Assert.assertEquals("$.a", reader.getPath());
        reader.endObject();
    }

    @Test
    public void selectNameSingleQuoted() throws IOException {
        JsonReader.Options abc = Options.of("a", "b");
        JsonReader reader = newReader("{'a':5}");
        reader.setLenient(true);
        reader.beginObject();
        Assert.assertEquals(0, reader.selectName(abc));
        Assert.assertEquals("$.a", reader.getPath());
        Assert.assertEquals(5, reader.nextInt());
        Assert.assertEquals("$.a", reader.getPath());
        reader.endObject();
    }

    @Test
    public void selectString() throws IOException {
        JsonReader.Options abc = Options.of("a", "b", "c");
        JsonReader reader = newReader("[\"a\", \"b\", \"c\", \"d\"]");
        reader.beginArray();
        Assert.assertEquals("$[0]", reader.getPath());
        Assert.assertEquals(0, reader.selectString(abc));
        Assert.assertEquals("$[1]", reader.getPath());
        Assert.assertEquals(1, reader.selectString(abc));
        Assert.assertEquals("$[2]", reader.getPath());
        Assert.assertEquals(2, reader.selectString(abc));
        Assert.assertEquals("$[3]", reader.getPath());
        // A missed selectName() doesn't advance anything, not even the path.
        Assert.assertEquals((-1), reader.selectString(abc));
        Assert.assertEquals("$[3]", reader.getPath());
        Assert.assertEquals(STRING, reader.peek());
        Assert.assertEquals("d", reader.nextString());
        Assert.assertEquals("$[4]", reader.getPath());
        reader.endArray();
    }

    @Test
    public void selectStringNecessaryEscaping() throws Exception {
        JsonReader.Options options = Options.of("\n", "\u0000", "\"");
        JsonReader reader = newReader("[\"\\n\",\"\\u0000\", \"\\\"\"]");
        reader.beginArray();
        Assert.assertEquals(0, reader.selectString(options));
        Assert.assertEquals(1, reader.selectString(options));
        Assert.assertEquals(2, reader.selectString(options));
        reader.endArray();
    }

    /**
     * Select strips unnecessarily-escaped strings.
     */
    @Test
    public void selectStringUnnecessaryEscaping() throws IOException {
        JsonReader.Options abc = Options.of("a", "b", "c");
        JsonReader reader = newReader("[\"\\u0061\", \"b\", \"\\u0063\"]");
        reader.beginArray();
        Assert.assertEquals(0, reader.selectString(abc));
        Assert.assertEquals(1, reader.selectString(abc));
        Assert.assertEquals(2, reader.selectString(abc));
        reader.endArray();
    }

    @Test
    public void selectStringUnquoted() throws IOException {
        JsonReader.Options abc = Options.of("a", "b", "c");
        JsonReader reader = newReader("[a, \"b\", c]");
        reader.setLenient(true);
        reader.beginArray();
        Assert.assertEquals(0, reader.selectString(abc));
        Assert.assertEquals(1, reader.selectString(abc));
        Assert.assertEquals(2, reader.selectString(abc));
        reader.endArray();
    }

    @Test
    public void selectStringSingleQuoted() throws IOException {
        JsonReader.Options abc = Options.of("a", "b", "c");
        JsonReader reader = newReader("[\'a\', \"b\", c]");
        reader.setLenient(true);
        reader.beginArray();
        Assert.assertEquals(0, reader.selectString(abc));
        Assert.assertEquals(1, reader.selectString(abc));
        Assert.assertEquals(2, reader.selectString(abc));
        reader.endArray();
    }

    @Test
    public void selectStringMaintainsReaderState() throws IOException {
        JsonReader.Options abc = Options.of("a", "b", "c");
        JsonReader reader = newReader("[\"\\u0061\", \"42\"]");
        reader.beginArray();
        Assert.assertEquals(0, reader.selectString(abc));
        Assert.assertEquals((-1), reader.selectString(abc));
        Assert.assertEquals(STRING, reader.peek());
        // Next long can retrieve a value from a buffered string.
        Assert.assertEquals(42, reader.nextLong());
        reader.endArray();
    }

    @Test
    public void selectStringWithoutString() throws IOException {
        JsonReader.Options numbers = Options.of("1", "2.0", "true", "4");
        JsonReader reader = newReader("[0, 2.0, true, \"4\"]");
        reader.beginArray();
        assertThat(reader.selectString(numbers)).isEqualTo((-1));
        reader.skipValue();
        assertThat(reader.selectString(numbers)).isEqualTo((-1));
        reader.skipValue();
        assertThat(reader.selectString(numbers)).isEqualTo((-1));
        reader.skipValue();
        assertThat(reader.selectString(numbers)).isEqualTo(3);
        reader.endArray();
    }

    @Test
    public void stringToNumberCoersion() throws Exception {
        JsonReader reader = newReader("[\"0\", \"9223372036854775807\", \"1.5\"]");
        reader.beginArray();
        assertThat(reader.nextInt()).isEqualTo(0);
        assertThat(reader.nextLong()).isEqualTo(9223372036854775807L);
        assertThat(reader.nextDouble()).isEqualTo(1.5);
        reader.endArray();
    }

    @Test
    public void unnecessaryPrecisionNumberCoersion() throws Exception {
        JsonReader reader = newReader("[\"0.0\", \"9223372036854775807.0\"]");
        reader.beginArray();
        assertThat(reader.nextInt()).isEqualTo(0);
        assertThat(reader.nextLong()).isEqualTo(9223372036854775807L);
        reader.endArray();
    }

    @Test
    public void nanInfinityDoubleCoersion() throws Exception {
        JsonReader reader = newReader("[\"NaN\", \"Infinity\", \"-Infinity\"]");
        reader.beginArray();
        reader.setLenient(true);
        assertThat(reader.nextDouble()).isNaN();
        assertThat(reader.nextDouble()).isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(reader.nextDouble()).isEqualTo(Double.NEGATIVE_INFINITY);
        reader.endArray();
    }

    @Test
    public void intMismatchWithStringDoesNotAdvance() throws Exception {
        JsonReader reader = newReader("[\"a\"]");
        reader.beginArray();
        try {
            reader.nextInt();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextString()).isEqualTo("a");
        reader.endArray();
    }

    @Test
    public void longMismatchWithStringDoesNotAdvance() throws Exception {
        JsonReader reader = newReader("[\"a\"]");
        reader.beginArray();
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextString()).isEqualTo("a");
        reader.endArray();
    }

    @Test
    public void doubleMismatchWithStringDoesNotAdvance() throws Exception {
        JsonReader reader = newReader("[\"a\"]");
        reader.beginArray();
        try {
            reader.nextDouble();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextString()).isEqualTo("a");
        reader.endArray();
    }

    @Test
    public void readJsonValueInt() throws IOException {
        JsonReader reader = newReader("1");
        Object value = reader.readJsonValue();
        assertThat(value).isEqualTo(1.0);
    }

    @Test
    public void readJsonValueMap() throws IOException {
        JsonReader reader = newReader("{\"hello\": \"world\"}");
        Object value = reader.readJsonValue();
        assertThat(value).isEqualTo(Collections.singletonMap("hello", "world"));
    }

    @Test
    public void readJsonValueList() throws IOException {
        JsonReader reader = newReader("[\"a\", \"b\"]");
        Object value = reader.readJsonValue();
        assertThat(value).isEqualTo(Arrays.asList("a", "b"));
    }

    @Test
    public void readJsonValueListMultipleTypes() throws IOException {
        JsonReader reader = newReader("[\"a\", 5, false]");
        Object value = reader.readJsonValue();
        assertThat(value).isEqualTo(Arrays.asList("a", 5.0, false));
    }

    @Test
    public void readJsonValueNestedListInMap() throws IOException {
        JsonReader reader = newReader("{\"pizzas\": [\"cheese\", \"pepperoni\"]}");
        Object value = reader.readJsonValue();
        assertThat(value).isEqualTo(Collections.singletonMap("pizzas", Arrays.asList("cheese", "pepperoni")));
    }

    @Test
    public void skipName() throws IOException {
        JsonReader reader = newReader("{\"a\":1}");
        reader.beginObject();
        reader.skipName();
        assertThat(reader.peek()).isEqualTo(NUMBER);
        reader.skipValue();
        reader.endObject();
    }

    @Test
    public void skipNameOnValueFails() throws IOException {
        JsonReader reader = newReader("1");
        try {
            reader.skipName();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextInt()).isEqualTo(1);
    }

    @Test
    public void emptyDocumentHasNextReturnsFalse() throws IOException {
        JsonReader reader = newReader("1");
        reader.readJsonValue();
        assertThat(reader.hasNext()).isFalse();
    }

    @Test
    public void skipValueAtEndOfObjectFails() throws IOException {
        JsonReader reader = newReader("{}");
        reader.beginObject();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a value but was END_OBJECT at path $.");
        }
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipValueAtEndOfArrayFails() throws IOException {
        JsonReader reader = newReader("[]");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a value but was END_ARRAY at path $[0]");
        }
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipValueAtEndOfDocumentFails() throws IOException {
        JsonReader reader = newReader("1");
        reader.nextInt();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a value but was END_DOCUMENT at path $");
        }
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void basicPeekJson() throws IOException {
        JsonReader reader = newReader("{\"a\":12,\"b\":[34,56],\"c\":78}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextInt()).isEqualTo(12);
        assertThat(reader.nextName()).isEqualTo("b");
        reader.beginArray();
        assertThat(reader.nextInt()).isEqualTo(34);
        // Peek.
        JsonReader peekReader = reader.peekJson();
        assertThat(peekReader.nextInt()).isEqualTo(56);
        peekReader.endArray();
        assertThat(peekReader.nextName()).isEqualTo("c");
        assertThat(peekReader.nextInt()).isEqualTo(78);
        peekReader.endObject();
        assertThat(peekReader.peek()).isEqualTo(END_DOCUMENT);
        // Read again.
        assertThat(reader.nextInt()).isEqualTo(56);
        reader.endArray();
        assertThat(reader.nextName()).isEqualTo("c");
        assertThat(reader.nextInt()).isEqualTo(78);
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    /**
     * We have a document that requires 12 operations to read. We read it step-by-step with one real
     * reader. Before each of the real reader?s operations we create a peeking reader and let it read
     * the rest of the document.
     */
    @Test
    public void peekJsonReader() throws IOException {
        JsonReader reader = newReader("[12,34,{\"a\":56,\"b\":78},90]");
        for (int i = 0; i < 12; i++) {
            readPeek12Steps(reader.peekJson(), i, 12);
            readPeek12Steps(reader, i, (i + 1));
        }
    }

    /**
     * Confirm that we can peek in every state of the UTF-8 reader.
     */
    @Test
    public void peekAfterPeek() throws IOException {
        JsonReader reader = newReader("[{\"a\":\"aaa\",\'b\':\'bbb\',c:c,\"d\":\"d\"},true,false,null,1,2.0]");
        reader.setLenient(true);
        readValue(reader, true);
        reader.peekJson();
    }

    @Test
    public void peekAfterPromoteNameToValue() throws IOException {
        JsonReader reader = newReader("{\"a\":\"b\"}");
        reader.beginObject();
        reader.promoteNameToValue();
        Assert.assertEquals("a", reader.peekJson().nextString());
        Assert.assertEquals("a", reader.nextString());
        Assert.assertEquals("b", reader.peekJson().nextString());
        Assert.assertEquals("b", reader.nextString());
        reader.endObject();
    }
}

