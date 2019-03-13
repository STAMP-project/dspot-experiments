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
package com.google.gson.stream;


import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.TestCase;


@SuppressWarnings("resource")
public final class JsonReaderTest extends TestCase {
    public void testReadArray() throws IOException {
        JsonReader reader = new JsonReader(reader("[true, true]"));
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals(true, reader.nextBoolean());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testReadEmptyArray() throws IOException {
        JsonReader reader = new JsonReader(reader("[]"));
        reader.beginArray();
        TestCase.assertFalse(reader.hasNext());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testReadObject() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\": \"android\", \"b\": \"banana\"}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals("android", reader.nextString());
        TestCase.assertEquals("b", reader.nextName());
        TestCase.assertEquals("banana", reader.nextString());
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testReadEmptyObject() throws IOException {
        JsonReader reader = new JsonReader(reader("{}"));
        reader.beginObject();
        TestCase.assertFalse(reader.hasNext());
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipArray() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        reader.skipValue();
        TestCase.assertEquals("b", reader.nextName());
        TestCase.assertEquals(123, reader.nextInt());
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipArrayAfterPeek() throws Exception {
        JsonReader reader = new JsonReader(reader("{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals(JsonToken.BEGIN_ARRAY, reader.peek());
        reader.skipValue();
        TestCase.assertEquals("b", reader.nextName());
        TestCase.assertEquals(123, reader.nextInt());
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipTopLevelObject() throws Exception {
        JsonReader reader = new JsonReader(reader("{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
        reader.skipValue();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipObject() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\": { \"c\": [], \"d\": [true, true, {}] }, \"b\": \"banana\"}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        reader.skipValue();
        TestCase.assertEquals("b", reader.nextName());
        reader.skipValue();
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipObjectAfterPeek() throws Exception {
        String json = "{" + ((("  \"one\": { \"num\": 1 }" + ", \"two\": { \"num\": 2 }") + ", \"three\": { \"num\": 3 }") + "}");
        JsonReader reader = new JsonReader(reader(json));
        reader.beginObject();
        TestCase.assertEquals("one", reader.nextName());
        TestCase.assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
        reader.skipValue();
        TestCase.assertEquals("two", reader.nextName());
        TestCase.assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
        reader.skipValue();
        TestCase.assertEquals("three", reader.nextName());
        reader.skipValue();
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipInteger() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":123456789,\"b\":-123456789}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        reader.skipValue();
        TestCase.assertEquals("b", reader.nextName());
        reader.skipValue();
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipDouble() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":-123.456e-789,\"b\":123456789.0}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        reader.skipValue();
        TestCase.assertEquals("b", reader.nextName());
        reader.skipValue();
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testHelloWorld() throws IOException {
        String json = "{\n" + (("   \"hello\": true,\n" + "   \"foo\": [\"world\"]\n") + "}");
        JsonReader reader = new JsonReader(reader(json));
        reader.beginObject();
        TestCase.assertEquals("hello", reader.nextName());
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals("foo", reader.nextName());
        reader.beginArray();
        TestCase.assertEquals("world", reader.nextString());
        reader.endArray();
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testInvalidJsonInput() throws IOException {
        String json = "{\n" + (("   \"h\\ello\": true,\n" + "   \"foo\": [\"world\"]\n") + "}");
        JsonReader reader = new JsonReader(reader(json));
        reader.beginObject();
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testNulls() {
        try {
            new JsonReader(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testEmptyString() {
        try {
            new JsonReader(reader("")).beginArray();
            TestCase.fail();
        } catch (IOException expected) {
        }
        try {
            new JsonReader(reader("")).beginObject();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testCharacterUnescaping() throws IOException {
        String json = "[\"a\"," + (((((((((((((((((("\"a\\\"\"," + "\"\\\"\",") + "\":\",") + "\",\",") + "\"\\b\",") + "\"\\f\",") + "\"\\n\",") + "\"\\r\",") + "\"\\t\",") + "\" \",") + "\"\\\\\",") + "\"{\",") + "\"}\",") + "\"[\",") + "\"]\",") + "\"\\u0000\",") + "\"\\u0019\",") + "\"\\u20AC\"") + "]");
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        TestCase.assertEquals("a", reader.nextString());
        TestCase.assertEquals("a\"", reader.nextString());
        TestCase.assertEquals("\"", reader.nextString());
        TestCase.assertEquals(":", reader.nextString());
        TestCase.assertEquals(",", reader.nextString());
        TestCase.assertEquals("\b", reader.nextString());
        TestCase.assertEquals("\f", reader.nextString());
        TestCase.assertEquals("\n", reader.nextString());
        TestCase.assertEquals("\r", reader.nextString());
        TestCase.assertEquals("\t", reader.nextString());
        TestCase.assertEquals(" ", reader.nextString());
        TestCase.assertEquals("\\", reader.nextString());
        TestCase.assertEquals("{", reader.nextString());
        TestCase.assertEquals("}", reader.nextString());
        TestCase.assertEquals("[", reader.nextString());
        TestCase.assertEquals("]", reader.nextString());
        TestCase.assertEquals("\u0000", reader.nextString());
        TestCase.assertEquals("\u0019", reader.nextString());
        TestCase.assertEquals("\u20ac", reader.nextString());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testUnescapingInvalidCharacters() throws IOException {
        String json = "[\"\\u000g\"]";
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        try {
            reader.nextString();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
    }

    public void testUnescapingTruncatedCharacters() throws IOException {
        String json = "[\"\\u000";
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testUnescapingTruncatedSequence() throws IOException {
        String json = "[\"\\";
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testIntegersWithFractionalPartSpecified() throws IOException {
        JsonReader reader = new JsonReader(reader("[1.0,1.0,1.0]"));
        reader.beginArray();
        TestCase.assertEquals(1.0, reader.nextDouble());
        TestCase.assertEquals(1, reader.nextInt());
        TestCase.assertEquals(1L, reader.nextLong());
    }

    public void testDoubles() throws IOException {
        String json = "[-0.0," + ((((((("1.0," + "1.7976931348623157E308,") + "4.9E-324,") + "0.0,") + "-0.5,") + "2.2250738585072014E-308,") + "3.141592653589793,") + "2.718281828459045]");
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        TestCase.assertEquals((-0.0), reader.nextDouble());
        TestCase.assertEquals(1.0, reader.nextDouble());
        TestCase.assertEquals(1.7976931348623157E308, reader.nextDouble());
        TestCase.assertEquals(4.9E-324, reader.nextDouble());
        TestCase.assertEquals(0.0, reader.nextDouble());
        TestCase.assertEquals((-0.5), reader.nextDouble());
        TestCase.assertEquals(2.2250738585072014E-308, reader.nextDouble());
        TestCase.assertEquals(3.141592653589793, reader.nextDouble());
        TestCase.assertEquals(2.718281828459045, reader.nextDouble());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testStrictNonFiniteDoubles() throws IOException {
        String json = "[NaN]";
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        try {
            reader.nextDouble();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testStrictQuotedNonFiniteDoubles() throws IOException {
        String json = "[\"NaN\"]";
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        try {
            reader.nextDouble();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testLenientNonFiniteDoubles() throws IOException {
        String json = "[NaN, -Infinity, Infinity]";
        JsonReader reader = new JsonReader(reader(json));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertTrue(Double.isNaN(reader.nextDouble()));
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, reader.nextDouble());
        TestCase.assertEquals(Double.POSITIVE_INFINITY, reader.nextDouble());
        reader.endArray();
    }

    public void testLenientQuotedNonFiniteDoubles() throws IOException {
        String json = "[\"NaN\", \"-Infinity\", \"Infinity\"]";
        JsonReader reader = new JsonReader(reader(json));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertTrue(Double.isNaN(reader.nextDouble()));
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, reader.nextDouble());
        TestCase.assertEquals(Double.POSITIVE_INFINITY, reader.nextDouble());
        reader.endArray();
    }

    public void testStrictNonFiniteDoublesWithSkipValue() throws IOException {
        String json = "[NaN]";
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testLongs() throws IOException {
        String json = "[0,0,0," + ((("1,1,1," + "-1,-1,-1,") + "-9223372036854775808,") + "9223372036854775807]");
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        TestCase.assertEquals(0L, reader.nextLong());
        TestCase.assertEquals(0, reader.nextInt());
        TestCase.assertEquals(0.0, reader.nextDouble());
        TestCase.assertEquals(1L, reader.nextLong());
        TestCase.assertEquals(1, reader.nextInt());
        TestCase.assertEquals(1.0, reader.nextDouble());
        TestCase.assertEquals((-1L), reader.nextLong());
        TestCase.assertEquals((-1), reader.nextInt());
        TestCase.assertEquals((-1.0), reader.nextDouble());
        try {
            reader.nextInt();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        TestCase.assertEquals(Long.MIN_VALUE, reader.nextLong());
        try {
            reader.nextInt();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        TestCase.assertEquals(Long.MAX_VALUE, reader.nextLong());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testBooleans() throws IOException {
        JsonReader reader = new JsonReader(reader("[true,false]"));
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals(false, reader.nextBoolean());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testPeekingUnquotedStringsPrefixedWithBooleans() throws IOException {
        JsonReader reader = new JsonReader(reader("[truey]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals("truey", reader.nextString());
        reader.endArray();
    }

    public void testMalformedNumbers() throws IOException {
        assertNotANumber("-");
        assertNotANumber(".");
        // exponent lacks digit
        assertNotANumber("e");
        assertNotANumber("0e");
        assertNotANumber(".e");
        assertNotANumber("0.e");
        assertNotANumber("-.0e");
        // no integer
        assertNotANumber("e1");
        assertNotANumber(".e1");
        assertNotANumber("-e1");
        // trailing characters
        assertNotANumber("1x");
        assertNotANumber("1.1x");
        assertNotANumber("1e1x");
        assertNotANumber("1ex");
        assertNotANumber("1.1ex");
        assertNotANumber("1.1e1x");
        // fraction has no digit
        assertNotANumber("0.");
        assertNotANumber("-0.");
        assertNotANumber("0.e1");
        assertNotANumber("-0.e1");
        // no leading digit
        assertNotANumber(".0");
        assertNotANumber("-.0");
        assertNotANumber(".0e1");
        assertNotANumber("-.0e1");
    }

    public void testPeekingUnquotedStringsPrefixedWithIntegers() throws IOException {
        JsonReader reader = new JsonReader(reader("[12.34e5x]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
        try {
            reader.nextInt();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        TestCase.assertEquals("12.34e5x", reader.nextString());
    }

    public void testPeekLongMinValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[-9223372036854775808]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals(-9223372036854775808L, reader.nextLong());
    }

    public void testPeekLongMaxValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[9223372036854775807]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals(9223372036854775807L, reader.nextLong());
    }

    public void testLongLargerThanMaxLongThatWrapsAround() throws IOException {
        JsonReader reader = new JsonReader(reader("[22233720368547758070]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        try {
            reader.nextLong();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
    }

    public void testLongLargerThanMinLongThatWrapsAround() throws IOException {
        JsonReader reader = new JsonReader(reader("[-22233720368547758070]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        try {
            reader.nextLong();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
    }

    /**
     * Issue 1053, negative zero.
     *
     * @throws Exception
     * 		
     */
    public void testNegativeZero() throws Exception {
        JsonReader reader = new JsonReader(reader("[-0]"));
        reader.setLenient(false);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals("-0", reader.nextString());
    }

    public void testPeekMuchLargerThanLongMinValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[-92233720368547758080]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        try {
            reader.nextLong();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        TestCase.assertEquals((-9.223372036854776E19), reader.nextDouble());
    }

    public void testQuotedNumberWithEscape() throws IOException {
        JsonReader reader = new JsonReader(reader("[\"1234\"]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
        TestCase.assertEquals(1234, reader.nextInt());
    }

    public void testMixedCaseLiterals() throws IOException {
        JsonReader reader = new JsonReader(reader("[True,TruE,False,FALSE,NULL,nulL]"));
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals(false, reader.nextBoolean());
        TestCase.assertEquals(false, reader.nextBoolean());
        reader.nextNull();
        reader.nextNull();
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testMissingValue() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testPrematureEndOfInput() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":true,"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals(true, reader.nextBoolean());
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testPrematurelyClosed() throws IOException {
        try {
            JsonReader reader = new JsonReader(reader("{\"a\":[]}"));
            reader.beginObject();
            reader.close();
            reader.nextName();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            JsonReader reader = new JsonReader(reader("{\"a\":[]}"));
            reader.close();
            reader.beginObject();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            JsonReader reader = new JsonReader(reader("{\"a\":true}"));
            reader.beginObject();
            reader.nextName();
            reader.peek();
            reader.close();
            reader.nextBoolean();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testNextFailuresDoNotAdvance() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":true}"));
        reader.beginObject();
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.beginArray();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.endArray();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.beginObject();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.endObject();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals(true, reader.nextBoolean());
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.beginArray();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.endArray();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
        reader.close();
    }

    public void testIntegerMismatchFailuresDoNotAdvance() throws IOException {
        JsonReader reader = new JsonReader(reader("[1.5]"));
        reader.beginArray();
        try {
            reader.nextInt();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        TestCase.assertEquals(1.5, reader.nextDouble());
        reader.endArray();
    }

    public void testStringNullIsNotNull() throws IOException {
        JsonReader reader = new JsonReader(reader("[\"null\"]"));
        reader.beginArray();
        try {
            reader.nextNull();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testNullLiteralIsNotAString() throws IOException {
        JsonReader reader = new JsonReader(reader("[null]"));
        reader.beginArray();
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testStrictNameValueSeparator() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\"=true}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("{\"a\"=>true}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientNameValueSeparator() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\"=true}"));
        reader.setLenient(true);
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals(true, reader.nextBoolean());
        reader = new JsonReader(reader("{\"a\"=>true}"));
        reader.setLenient(true);
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals(true, reader.nextBoolean());
    }

    public void testStrictNameValueSeparatorWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\"=true}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("{\"a\"=>true}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testCommentsInStringValue() throws Exception {
        JsonReader reader = new JsonReader(reader("[\"// comment\"]"));
        reader.beginArray();
        TestCase.assertEquals("// comment", reader.nextString());
        reader.endArray();
        reader = new JsonReader(reader("{\"a\":\"#someComment\"}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals("#someComment", reader.nextString());
        reader.endObject();
        reader = new JsonReader(reader("{\"#//a\":\"#some //Comment\"}"));
        reader.beginObject();
        TestCase.assertEquals("#//a", reader.nextName());
        TestCase.assertEquals("#some //Comment", reader.nextString());
        reader.endObject();
    }

    public void testStrictComments() throws IOException {
        JsonReader reader = new JsonReader(reader("[// comment \n true]"));
        reader.beginArray();
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[# comment \n true]"));
        reader.beginArray();
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[/* comment */ true]"));
        reader.beginArray();
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientComments() throws IOException {
        JsonReader reader = new JsonReader(reader("[// comment \n true]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        reader = new JsonReader(reader("[# comment \n true]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        reader = new JsonReader(reader("[/* comment */ true]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
    }

    public void testStrictCommentsWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[// comment \n true]"));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[# comment \n true]"));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[/* comment */ true]"));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictUnquotedNames() throws IOException {
        JsonReader reader = new JsonReader(reader("{a:true}"));
        reader.beginObject();
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientUnquotedNames() throws IOException {
        JsonReader reader = new JsonReader(reader("{a:true}"));
        reader.setLenient(true);
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
    }

    public void testStrictUnquotedNamesWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("{a:true}"));
        reader.beginObject();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictSingleQuotedNames() throws IOException {
        JsonReader reader = new JsonReader(reader("{'a':true}"));
        reader.beginObject();
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientSingleQuotedNames() throws IOException {
        JsonReader reader = new JsonReader(reader("{'a':true}"));
        reader.setLenient(true);
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
    }

    public void testStrictSingleQuotedNamesWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("{'a':true}"));
        reader.beginObject();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictUnquotedStrings() throws IOException {
        JsonReader reader = new JsonReader(reader("[a]"));
        reader.beginArray();
        try {
            reader.nextString();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testStrictUnquotedStringsWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[a]"));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testLenientUnquotedStrings() throws IOException {
        JsonReader reader = new JsonReader(reader("[a]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals("a", reader.nextString());
    }

    public void testStrictSingleQuotedStrings() throws IOException {
        JsonReader reader = new JsonReader(reader("['a']"));
        reader.beginArray();
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientSingleQuotedStrings() throws IOException {
        JsonReader reader = new JsonReader(reader("['a']"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals("a", reader.nextString());
    }

    public void testStrictSingleQuotedStringsWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("['a']"));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictSemicolonDelimitedArray() throws IOException {
        JsonReader reader = new JsonReader(reader("[true;true]"));
        reader.beginArray();
        try {
            reader.nextBoolean();
            reader.nextBoolean();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientSemicolonDelimitedArray() throws IOException {
        JsonReader reader = new JsonReader(reader("[true;true]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals(true, reader.nextBoolean());
    }

    public void testStrictSemicolonDelimitedArrayWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[true;true]"));
        reader.beginArray();
        try {
            reader.skipValue();
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictSemicolonDelimitedNameValuePair() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":true;\"b\":true}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.nextBoolean();
            reader.nextName();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientSemicolonDelimitedNameValuePair() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":true;\"b\":true}"));
        reader.setLenient(true);
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals("b", reader.nextName());
    }

    public void testStrictSemicolonDelimitedNameValuePairWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":true;\"b\":true}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        try {
            reader.skipValue();
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictUnnecessaryArraySeparators() throws IOException {
        JsonReader reader = new JsonReader(reader("[true,,true]"));
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        try {
            reader.nextNull();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[,true]"));
        reader.beginArray();
        try {
            reader.nextNull();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[true,]"));
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        try {
            reader.nextNull();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[,]"));
        reader.beginArray();
        try {
            reader.nextNull();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientUnnecessaryArraySeparators() throws IOException {
        JsonReader reader = new JsonReader(reader("[true,,true]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        reader.nextNull();
        TestCase.assertEquals(true, reader.nextBoolean());
        reader.endArray();
        reader = new JsonReader(reader("[,true]"));
        reader.setLenient(true);
        reader.beginArray();
        reader.nextNull();
        TestCase.assertEquals(true, reader.nextBoolean());
        reader.endArray();
        reader = new JsonReader(reader("[true,]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        reader.nextNull();
        reader.endArray();
        reader = new JsonReader(reader("[,]"));
        reader.setLenient(true);
        reader.beginArray();
        reader.nextNull();
        reader.nextNull();
        reader.endArray();
    }

    public void testStrictUnnecessaryArraySeparatorsWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[true,,true]"));
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[,true]"));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[true,]"));
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
        reader = new JsonReader(reader("[,]"));
        reader.beginArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictMultipleTopLevelValues() throws IOException {
        JsonReader reader = new JsonReader(reader("[] []"));
        reader.beginArray();
        reader.endArray();
        try {
            reader.peek();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientMultipleTopLevelValues() throws IOException {
        JsonReader reader = new JsonReader(reader("[] true {}"));
        reader.setLenient(true);
        reader.beginArray();
        reader.endArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        reader.beginObject();
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testStrictMultipleTopLevelValuesWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("[] []"));
        reader.beginArray();
        reader.endArray();
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testTopLevelValueTypes() throws IOException {
        JsonReader reader1 = new JsonReader(reader("true"));
        TestCase.assertTrue(reader1.nextBoolean());
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader1.peek());
        JsonReader reader2 = new JsonReader(reader("false"));
        TestCase.assertFalse(reader2.nextBoolean());
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader2.peek());
        JsonReader reader3 = new JsonReader(reader("null"));
        TestCase.assertEquals(JsonToken.NULL, reader3.peek());
        reader3.nextNull();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader3.peek());
        JsonReader reader4 = new JsonReader(reader("123"));
        TestCase.assertEquals(123, reader4.nextInt());
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader4.peek());
        JsonReader reader5 = new JsonReader(reader("123.4"));
        TestCase.assertEquals(123.4, reader5.nextDouble());
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader5.peek());
        JsonReader reader6 = new JsonReader(reader("\"a\""));
        TestCase.assertEquals("a", reader6.nextString());
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader6.peek());
    }

    public void testTopLevelValueTypeWithSkipValue() throws IOException {
        JsonReader reader = new JsonReader(reader("true"));
        reader.skipValue();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testStrictNonExecutePrefix() {
        JsonReader reader = new JsonReader(reader(")]}\'\n []"));
        try {
            reader.beginArray();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testStrictNonExecutePrefixWithSkipValue() {
        JsonReader reader = new JsonReader(reader(")]}\'\n []"));
        try {
            reader.skipValue();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientNonExecutePrefix() throws IOException {
        JsonReader reader = new JsonReader(reader(")]}\'\n []"));
        reader.setLenient(true);
        reader.beginArray();
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testLenientNonExecutePrefixWithLeadingWhitespace() throws IOException {
        JsonReader reader = new JsonReader(reader("\r\n \t)]}\'\n []"));
        reader.setLenient(true);
        reader.beginArray();
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testLenientPartialNonExecutePrefix() {
        JsonReader reader = new JsonReader(reader(")]}' []"));
        reader.setLenient(true);
        try {
            TestCase.assertEquals(")", reader.nextString());
            reader.nextString();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testBomIgnoredAsFirstCharacterOfDocument() throws IOException {
        JsonReader reader = new JsonReader(reader("\ufeff[]"));
        reader.beginArray();
        reader.endArray();
    }

    public void testBomForbiddenAsOtherCharacterInDocument() throws IOException {
        JsonReader reader = new JsonReader(reader("[\ufeff]"));
        reader.beginArray();
        try {
            reader.endArray();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testFailWithPosition() throws IOException {
        testFailWithPosition("Expected value at line 6 column 5 path $[1]", "[\n\n\n\n\n\"a\",}]");
    }

    public void testFailWithPositionGreaterThanBufferSize() throws IOException {
        String spaces = repeat(' ', 8192);
        testFailWithPosition("Expected value at line 6 column 5 path $[1]", (("[\n\n" + spaces) + "\n\n\n\"a\",}]"));
    }

    public void testFailWithPositionOverSlashSlashEndOfLineComment() throws IOException {
        testFailWithPosition("Expected value at line 5 column 6 path $[1]", "\n// foo\n\n//bar\r\n[\"a\",}");
    }

    public void testFailWithPositionOverHashEndOfLineComment() throws IOException {
        testFailWithPosition("Expected value at line 5 column 6 path $[1]", "\n# foo\n\n#bar\r\n[\"a\",}");
    }

    public void testFailWithPositionOverCStyleComment() throws IOException {
        testFailWithPosition("Expected value at line 6 column 12 path $[1]", "\n\n/* foo\n*\n*\r\nbar */[\"a\",}");
    }

    public void testFailWithPositionOverQuotedString() throws IOException {
        testFailWithPosition("Expected value at line 5 column 3 path $[1]", "[\"foo\nbar\r\nbaz\n\",\n  }");
    }

    public void testFailWithPositionOverUnquotedString() throws IOException {
        testFailWithPosition("Expected value at line 5 column 2 path $[1]", "[\n\nabcd\n\n,}");
    }

    public void testFailWithEscapedNewlineCharacter() throws IOException {
        testFailWithPosition("Expected value at line 5 column 3 path $[1]", "[\n\n\"\\\n\n\",}");
    }

    public void testFailWithPositionIsOffsetByBom() throws IOException {
        testFailWithPosition("Expected value at line 1 column 6 path $[1]", "\ufeff[\"a\",}]");
    }

    public void testFailWithPositionDeepPath() throws IOException {
        JsonReader reader = new JsonReader(reader("[1,{\"a\":[2,3,}"));
        reader.beginArray();
        reader.nextInt();
        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        reader.nextInt();
        reader.nextInt();
        try {
            reader.peek();
            TestCase.fail();
        } catch (IOException expected) {
            TestCase.assertEquals("Expected value at line 1 column 14 path $[1].a[2]", expected.getMessage());
        }
    }

    public void testStrictVeryLongNumber() throws IOException {
        JsonReader reader = new JsonReader(reader((("[0." + (repeat('9', 8192))) + "]")));
        reader.beginArray();
        try {
            TestCase.assertEquals(1.0, reader.nextDouble());
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testLenientVeryLongNumber() throws IOException {
        JsonReader reader = new JsonReader(reader((("[0." + (repeat('9', 8192))) + "]")));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
        TestCase.assertEquals(1.0, reader.nextDouble());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testVeryLongUnquotedLiteral() throws IOException {
        String literal = ("a" + (repeat('b', 8192))) + "c";
        JsonReader reader = new JsonReader(reader((("[" + literal) + "]")));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(literal, reader.nextString());
        reader.endArray();
    }

    public void testDeeplyNestedArrays() throws IOException {
        // this is nested 40 levels deep; Gson is tuned for nesting is 30 levels deep or fewer
        JsonReader reader = new JsonReader(reader("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]"));
        for (int i = 0; i < 40; i++) {
            reader.beginArray();
        }
        TestCase.assertEquals(("$[0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0]" + "[0][0][0][0][0][0][0][0][0][0][0][0][0][0]"), reader.getPath());
        for (int i = 0; i < 40; i++) {
            reader.endArray();
        }
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testDeeplyNestedObjects() throws IOException {
        // Build a JSON document structured like {"a":{"a":{"a":{"a":true}}}}, but 40 levels deep
        String array = "{\"a\":%s}";
        String json = "true";
        for (int i = 0; i < 40; i++) {
            json = String.format(array, json);
        }
        JsonReader reader = new JsonReader(reader(json));
        for (int i = 0; i < 40; i++) {
            reader.beginObject();
            TestCase.assertEquals("a", reader.nextName());
        }
        TestCase.assertEquals(("$.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a" + ".a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a"), reader.getPath());
        TestCase.assertEquals(true, reader.nextBoolean());
        for (int i = 0; i < 40; i++) {
            reader.endObject();
        }
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    // http://code.google.com/p/google-gson/issues/detail?id=409
    public void testStringEndingInSlash() throws IOException {
        JsonReader reader = new JsonReader(reader("/"));
        reader.setLenient(true);
        try {
            reader.peek();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testDocumentWithCommentEndingInSlash() throws IOException {
        JsonReader reader = new JsonReader(reader("/* foo *//"));
        reader.setLenient(true);
        try {
            reader.peek();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testStringWithLeadingSlash() throws IOException {
        JsonReader reader = new JsonReader(reader("/x"));
        reader.setLenient(true);
        try {
            reader.peek();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testUnterminatedObject() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":\"android\"x"));
        reader.setLenient(true);
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals("android", reader.nextString());
        try {
            reader.peek();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }

    public void testVeryLongQuotedString() throws IOException {
        char[] stringChars = new char[1024 * 16];
        Arrays.fill(stringChars, 'x');
        String string = new String(stringChars);
        String json = ("[\"" + string) + "\"]";
        JsonReader reader = new JsonReader(reader(json));
        reader.beginArray();
        TestCase.assertEquals(string, reader.nextString());
        reader.endArray();
    }

    public void testVeryLongUnquotedString() throws IOException {
        char[] stringChars = new char[1024 * 16];
        Arrays.fill(stringChars, 'x');
        String string = new String(stringChars);
        String json = ("[" + string) + "]";
        JsonReader reader = new JsonReader(reader(json));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(string, reader.nextString());
        reader.endArray();
    }

    public void testVeryLongUnterminatedString() throws IOException {
        char[] stringChars = new char[1024 * 16];
        Arrays.fill(stringChars, 'x');
        String string = new String(stringChars);
        String json = "[" + string;
        JsonReader reader = new JsonReader(reader(json));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(string, reader.nextString());
        try {
            reader.peek();
            TestCase.fail();
        } catch (EOFException expected) {
        }
    }

    public void testSkipVeryLongUnquotedString() throws IOException {
        JsonReader reader = new JsonReader(reader((("[" + (repeat('x', 8192))) + "]")));
        reader.setLenient(true);
        reader.beginArray();
        reader.skipValue();
        reader.endArray();
    }

    public void testSkipTopLevelUnquotedString() throws IOException {
        JsonReader reader = new JsonReader(reader(repeat('x', 8192)));
        reader.setLenient(true);
        reader.skipValue();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testSkipVeryLongQuotedString() throws IOException {
        JsonReader reader = new JsonReader(reader((("[\"" + (repeat('x', 8192))) + "\"]")));
        reader.beginArray();
        reader.skipValue();
        reader.endArray();
    }

    public void testSkipTopLevelQuotedString() throws IOException {
        JsonReader reader = new JsonReader(reader((("\"" + (repeat('x', 8192))) + "\"")));
        reader.setLenient(true);
        reader.skipValue();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testStringAsNumberWithTruncatedExponent() throws IOException {
        JsonReader reader = new JsonReader(reader("[123e]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
    }

    public void testStringAsNumberWithDigitAndNonDigitExponent() throws IOException {
        JsonReader reader = new JsonReader(reader("[123e4b]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
    }

    public void testStringAsNumberWithNonDigitExponent() throws IOException {
        JsonReader reader = new JsonReader(reader("[123eb]"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
    }

    public void testEmptyStringName() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"\":true}"));
        reader.setLenient(true);
        TestCase.assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
        reader.beginObject();
        TestCase.assertEquals(JsonToken.NAME, reader.peek());
        TestCase.assertEquals("", reader.nextName());
        TestCase.assertEquals(JsonToken.BOOLEAN, reader.peek());
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals(JsonToken.END_OBJECT, reader.peek());
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testStrictExtraCommasInMaps() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":\"b\",}"));
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals("b", reader.nextString());
        try {
            reader.peek();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testLenientExtraCommasInMaps() throws IOException {
        JsonReader reader = new JsonReader(reader("{\"a\":\"b\",}"));
        reader.setLenient(true);
        reader.beginObject();
        TestCase.assertEquals("a", reader.nextName());
        TestCase.assertEquals("b", reader.nextString());
        try {
            reader.peek();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testMalformedDocuments() throws IOException {
        assertDocument("{]", JsonToken.BEGIN_OBJECT, IOException.class);
        assertDocument("{,", JsonToken.BEGIN_OBJECT, IOException.class);
        assertDocument("{{", JsonToken.BEGIN_OBJECT, IOException.class);
        assertDocument("{[", JsonToken.BEGIN_OBJECT, IOException.class);
        assertDocument("{:", JsonToken.BEGIN_OBJECT, IOException.class);
        assertDocument("{\"name\",", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\",", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\":}", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\"::", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\":,", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\"=}", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\"=>}", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\"=>\"string\":", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\"=>\"string\"=", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\"=>\"string\"=>", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\"=>\"string\",", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\"=>\"string\",\"name\"", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, JsonToken.NAME);
        assertDocument("[}", JsonToken.BEGIN_ARRAY, IOException.class);
        assertDocument("[,]", JsonToken.BEGIN_ARRAY, JsonToken.NULL, JsonToken.NULL, JsonToken.END_ARRAY);
        assertDocument("{", JsonToken.BEGIN_OBJECT, IOException.class);
        assertDocument("{\"name\"", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{\"name\",", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{'name'", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{'name',", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("{name", JsonToken.BEGIN_OBJECT, JsonToken.NAME, IOException.class);
        assertDocument("[", JsonToken.BEGIN_ARRAY, IOException.class);
        assertDocument("[string", JsonToken.BEGIN_ARRAY, JsonToken.STRING, IOException.class);
        assertDocument("[\"string\"", JsonToken.BEGIN_ARRAY, JsonToken.STRING, IOException.class);
        assertDocument("['string'", JsonToken.BEGIN_ARRAY, JsonToken.STRING, IOException.class);
        assertDocument("[123", JsonToken.BEGIN_ARRAY, JsonToken.NUMBER, IOException.class);
        assertDocument("[123,", JsonToken.BEGIN_ARRAY, JsonToken.NUMBER, IOException.class);
        assertDocument("{\"name\":123", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.NUMBER, IOException.class);
        assertDocument("{\"name\":123,", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.NUMBER, IOException.class);
        assertDocument("{\"name\":\"string\"", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\":\"string\",", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\":\'string\'", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\":\'string\',", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.STRING, IOException.class);
        assertDocument("{\"name\":false", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.BOOLEAN, IOException.class);
        assertDocument("{\"name\":false,,", JsonToken.BEGIN_OBJECT, JsonToken.NAME, JsonToken.BOOLEAN, IOException.class);
    }

    /**
     * This test behave slightly differently in Gson 2.2 and earlier. It fails
     * during peek rather than during nextString().
     */
    public void testUnterminatedStringFailure() throws IOException {
        JsonReader reader = new JsonReader(reader("[\"string"));
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertEquals(JsonToken.STRING, reader.peek());
        try {
            reader.nextString();
            TestCase.fail();
        } catch (MalformedJsonException expected) {
        }
    }
}

