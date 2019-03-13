/**
 * *****************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ****************************************************************************
 */
package com.hazelcast.internal.json;


import Json.FALSE;
import Json.NULL;
import Json.TRUE;
import com.hazelcast.internal.json.Json.DefaultHandler;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(QuickTest.class)
public class JsonParser_Test {
    private JsonParser_Test.TestHandler handler;

    private JsonParser parser;

    @Test(expected = NullPointerException.class)
    public void constructor_rejectsNullHandler() {
        new JsonParser(null);
    }

    @Test(expected = NullPointerException.class)
    public void parse_string_rejectsNull() {
        parser.parse(((String) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void parse_reader_rejectsNull() throws IOException {
        parser.parse(((Reader) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_reader_rejectsNegativeBufferSize() throws IOException {
        parser.parse(new StringReader("[]"), (-1));
    }

    @Test
    public void parse_string_rejectsEmpty() {
        assertParseException(0, "Unexpected end of input", "");
    }

    @Test
    public void parse_reader_rejectsEmpty() {
        ParseException exception = TestUtil.assertException(ParseException.class, new TestUtil.RunnableEx() {
            public void run() throws IOException {
                parser.parse(new StringReader(""));
            }
        });
        Assert.assertEquals(0, exception.getLocation().offset);
        Assert.assertThat(exception.getMessage(), StringStartsWith.startsWith("Unexpected end of input at"));
    }

    @Test
    public void parse_null() {
        parser.parse("null");
        Assert.assertEquals(JsonParser_Test.join("startNull 0", "endNull 4"), handler.getLog());
    }

    @Test
    public void parse_true() {
        parser.parse("true");
        Assert.assertEquals(JsonParser_Test.join("startBoolean 0", "endBoolean true 4"), handler.getLog());
    }

    @Test
    public void parse_false() {
        parser.parse("false");
        Assert.assertEquals(JsonParser_Test.join("startBoolean 0", "endBoolean false 5"), handler.getLog());
    }

    @Test
    public void parse_string() {
        parser.parse("\"foo\"");
        Assert.assertEquals(JsonParser_Test.join("startString 0", "endString foo 5"), handler.getLog());
    }

    @Test
    public void parse_string_empty() {
        parser.parse("\"\"");
        Assert.assertEquals(JsonParser_Test.join("startString 0", "endString  2"), handler.getLog());
    }

    @Test
    public void parse_number() {
        parser.parse("23");
        Assert.assertEquals(JsonParser_Test.join("startNumber 0", "endNumber 23 2"), handler.getLog());
    }

    @Test
    public void parse_number_negative() {
        parser.parse("-23");
        Assert.assertEquals(JsonParser_Test.join("startNumber 0", "endNumber -23 3"), handler.getLog());
    }

    @Test
    public void parse_number_negative_exponent() {
        parser.parse("-2.3e-12");
        Assert.assertEquals(JsonParser_Test.join("startNumber 0", "endNumber -2.3e-12 8"), handler.getLog());
    }

    @Test
    public void parse_array() {
        parser.parse("[23]");
        Assert.assertEquals(JsonParser_Test.join("startArray 0", "startArrayValue a1 1", "startNumber 1", "endNumber 23 3", "endArrayValue a1 3", "endArray a1 4"), handler.getLog());
    }

    @Test
    public void parse_array_empty() {
        parser.parse("[]");
        Assert.assertEquals(JsonParser_Test.join("startArray 0", "endArray a1 2"), handler.getLog());
    }

    @Test
    public void parse_object() {
        parser.parse("{\"foo\": 23}");
        Assert.assertEquals(JsonParser_Test.join("startObject 0", "startObjectName o1 1", "endObjectName o1 foo 6", "startObjectValue o1 foo 8", "startNumber 8", "endNumber 23 10", "endObjectValue o1 foo 10", "endObject o1 11"), handler.getLog());
    }

    @Test
    public void parse_object_empty() {
        parser.parse("{}");
        Assert.assertEquals(JsonParser_Test.join("startObject 0", "endObject o1 2"), handler.getLog());
    }

    @Test
    public void parse_stripsPadding() {
        Assert.assertEquals(new JsonArray(), Json.parse(" [ ] "));
    }

    @Test
    public void parse_ignoresAllWhiteSpace() {
        Assert.assertEquals(new JsonArray(), Json.parse("\t\r\n [\t\r\n ]\t\r\n "));
    }

    @Test
    public void parse_failsWithUnterminatedString() {
        assertParseException(5, "Unexpected end of input", "[\"foo");
    }

    @Test
    public void parse_lineAndColumn_onFirstLine() {
        parser.parse("[]");
        Assert.assertEquals("1:3", handler.lastLocation.toString());
    }

    @Test
    public void parse_lineAndColumn_afterLF() {
        parser.parse("[\n]");
        Assert.assertEquals("2:2", handler.lastLocation.toString());
    }

    @Test
    public void parse_lineAndColumn_afterCRLF() {
        parser.parse("[\r\n]");
        Assert.assertEquals("2:2", handler.lastLocation.toString());
    }

    @Test
    public void parse_lineAndColumn_afterCR() {
        parser.parse("[\r]");
        Assert.assertEquals("1:4", handler.lastLocation.toString());
    }

    @Test
    public void parse_handlesInputsThatExceedBufferSize() throws IOException {
        DefaultHandler defHandler = new DefaultHandler();
        parser = new JsonParser(defHandler);
        String input = "[ 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47 ]";
        parser.parse(new StringReader(input), 3);
        Assert.assertEquals("[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47]", defHandler.getValue().toString());
    }

    @Test
    public void parse_handlesStringsThatExceedBufferSize() throws IOException {
        DefaultHandler defHandler = new DefaultHandler();
        parser = new JsonParser(defHandler);
        String input = "[ \"lorem ipsum dolor sit amet\" ]";
        parser.parse(new StringReader(input), 3);
        Assert.assertEquals("[\"lorem ipsum dolor sit amet\"]", defHandler.getValue().toString());
    }

    @Test
    public void parse_handlesNumbersThatExceedBufferSize() throws IOException {
        DefaultHandler defHandler = new DefaultHandler();
        parser = new JsonParser(defHandler);
        String input = "[ 3.141592653589 ]";
        parser.parse(new StringReader(input), 3);
        Assert.assertEquals("[3.141592653589]", defHandler.getValue().toString());
    }

    @Test
    public void parse_handlesPositionsCorrectlyWhenInputExceedsBufferSize() {
        final String input = "{\n  \"a\": 23,\n  \"b\": 42,\n}";
        ParseException exception = TestUtil.assertException(ParseException.class, new TestUtil.RunnableEx() {
            public void run() throws IOException {
                parser.parse(new StringReader(input), 3);
            }
        });
        Assert.assertEquals(new Location(24, 4, 1), exception.getLocation());
    }

    @Test
    public void parse_failsOnTooDeeplyNestedArray() {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 1001; i++) {
            array = new JsonArray().add(array);
        }
        final String input = array.toString();
        ParseException exception = TestUtil.assertException(ParseException.class, new TestUtil.RunnableEx() {
            public void run() throws IOException {
                parser.parse(input);
            }
        });
        Assert.assertEquals("Nesting too deep at 1:1002", exception.getMessage());
    }

    @Test
    public void parse_failsOnTooDeeplyNestedObject() {
        JsonObject object = new JsonObject();
        for (int i = 0; i < 1001; i++) {
            object = new JsonObject().add("foo", object);
        }
        final String input = object.toString();
        ParseException exception = TestUtil.assertException(ParseException.class, new TestUtil.RunnableEx() {
            public void run() throws IOException {
                parser.parse(input);
            }
        });
        Assert.assertEquals("Nesting too deep at 1:7002", exception.getMessage());
    }

    @Test
    public void parse_failsOnTooDeeplyNestedMixedObject() {
        JsonValue value = new JsonObject();
        for (int i = 0; i < 1001; i++) {
            value = ((i % 2) == 0) ? new JsonArray().add(value) : new JsonObject().add("foo", value);
        }
        final String input = value.toString();
        ParseException exception = TestUtil.assertException(ParseException.class, new TestUtil.RunnableEx() {
            public void run() throws IOException {
                parser.parse(input);
            }
        });
        Assert.assertEquals("Nesting too deep at 1:4002", exception.getMessage());
    }

    @Test
    public void parse_doesNotFailWithManyArrays() {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 1001; i++) {
            array.add(new JsonArray().add(7));
        }
        final String input = array.toString();
        JsonValue result = Json.parse(input);
        Assert.assertTrue(result.isArray());
    }

    @Test
    public void parse_doesNotFailWithManyEmptyArrays() {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 1001; i++) {
            array.add(new JsonArray());
        }
        final String input = array.toString();
        JsonValue result = Json.parse(input);
        Assert.assertTrue(result.isArray());
    }

    @Test
    public void parse_doesNotFailWithManyObjects() {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 1001; i++) {
            array.add(new JsonObject().add("a", 7));
        }
        final String input = array.toString();
        JsonValue result = Json.parse(input);
        Assert.assertTrue(result.isArray());
    }

    @Test
    public void parse_doesNotFailWithManyEmptyObjects() {
        JsonArray array = new JsonArray();
        for (int i = 0; i < 1001; i++) {
            array.add(new JsonObject());
        }
        final String input = array.toString();
        JsonValue result = Json.parse(input);
        Assert.assertTrue(result.isArray());
    }

    @Test
    public void parse_canBeCalledTwice() {
        parser.parse("[23]");
        parser.parse("[42]");
        Assert.assertEquals(// first run
        // second run
        JsonParser_Test.join("startArray 0", "startArrayValue a1 1", "startNumber 1", "endNumber 23 3", "endArrayValue a1 3", "endArray a1 4", "startArray 0", "startArrayValue a2 1", "startNumber 1", "endNumber 42 3", "endArrayValue a2 3", "endArray a2 4"), handler.getLog());
    }

    @Test
    public void arrays_empty() {
        Assert.assertEquals("[]", Json.parse("[]").toString());
    }

    @Test
    public void arrays_singleValue() {
        Assert.assertEquals("[23]", Json.parse("[23]").toString());
    }

    @Test
    public void arrays_multipleValues() {
        Assert.assertEquals("[23,42]", Json.parse("[23,42]").toString());
    }

    @Test
    public void arrays_withWhitespaces() {
        Assert.assertEquals("[23,42]", Json.parse("[ 23 , 42 ]").toString());
    }

    @Test
    public void arrays_nested() {
        Assert.assertEquals("[[23]]", Json.parse("[[23]]").toString());
        Assert.assertEquals("[[[]]]", Json.parse("[[[]]]").toString());
        Assert.assertEquals("[[23],42]", Json.parse("[[23],42]").toString());
        Assert.assertEquals("[[23],[42]]", Json.parse("[[23],[42]]").toString());
        Assert.assertEquals("[[23],[42]]", Json.parse("[[23],[42]]").toString());
        Assert.assertEquals("[{\"foo\":[23]},{\"bar\":[42]}]", Json.parse("[{\"foo\":[23]},{\"bar\":[42]}]").toString());
    }

    @Test
    public void arrays_illegalSyntax() {
        assertParseException(1, "Expected value", "[,]");
        assertParseException(4, "Expected ',' or ']'", "[23 42]");
        assertParseException(4, "Expected value", "[23,]");
    }

    @Test
    public void arrays_incomplete() {
        assertParseException(1, "Unexpected end of input", "[");
        assertParseException(2, "Unexpected end of input", "[ ");
        assertParseException(3, "Unexpected end of input", "[23");
        assertParseException(4, "Unexpected end of input", "[23 ");
        assertParseException(4, "Unexpected end of input", "[23,");
        assertParseException(5, "Unexpected end of input", "[23, ");
    }

    @Test
    public void objects_empty() {
        Assert.assertEquals("{}", Json.parse("{}").toString());
    }

    @Test
    public void objects_singleValue() {
        Assert.assertEquals("{\"foo\":23}", Json.parse("{\"foo\":23}").toString());
    }

    @Test
    public void objects_multipleValues() {
        Assert.assertEquals("{\"foo\":23,\"bar\":42}", Json.parse("{\"foo\":23,\"bar\":42}").toString());
    }

    @Test
    public void objects_whitespace() {
        Assert.assertEquals("{\"foo\":23,\"bar\":42}", Json.parse("{ \"foo\" : 23, \"bar\" : 42 }").toString());
    }

    @Test
    public void objects_nested() {
        Assert.assertEquals("{\"foo\":{}}", Json.parse("{\"foo\":{}}").toString());
        Assert.assertEquals("{\"foo\":{\"bar\":42}}", Json.parse("{\"foo\":{\"bar\": 42}}").toString());
        Assert.assertEquals("{\"foo\":{\"bar\":{\"baz\":42}}}", Json.parse("{\"foo\":{\"bar\": {\"baz\": 42}}}").toString());
        Assert.assertEquals("{\"foo\":[{\"bar\":{\"baz\":[[42]]}}]}", Json.parse("{\"foo\":[{\"bar\": {\"baz\": [[42]]}}]}").toString());
    }

    @Test
    public void objects_illegalSyntax() {
        assertParseException(1, "Expected name", "{,}");
        assertParseException(1, "Expected name", "{:}");
        assertParseException(1, "Expected name", "{23}");
        assertParseException(4, "Expected ':'", "{\"a\"}");
        assertParseException(5, "Expected ':'", "{\"a\" \"b\"}");
        assertParseException(5, "Expected value", "{\"a\":}");
        assertParseException(8, "Expected name", "{\"a\":23,}");
        assertParseException(8, "Expected name", "{\"a\":23,42");
    }

    @Test
    public void objects_incomplete() {
        assertParseException(1, "Unexpected end of input", "{");
        assertParseException(2, "Unexpected end of input", "{ ");
        assertParseException(2, "Unexpected end of input", "{\"");
        assertParseException(4, "Unexpected end of input", "{\"a\"");
        assertParseException(5, "Unexpected end of input", "{\"a\" ");
        assertParseException(5, "Unexpected end of input", "{\"a\":");
        assertParseException(6, "Unexpected end of input", "{\"a\": ");
        assertParseException(7, "Unexpected end of input", "{\"a\":23");
        assertParseException(8, "Unexpected end of input", "{\"a\":23 ");
        assertParseException(8, "Unexpected end of input", "{\"a\":23,");
        assertParseException(9, "Unexpected end of input", "{\"a\":23, ");
    }

    @Test
    public void strings_emptyString_isAccepted() {
        Assert.assertEquals("", Json.parse("\"\"").asString());
    }

    @Test
    public void strings_asciiCharacters_areAccepted() {
        Assert.assertEquals(" ", Json.parse("\" \"").asString());
        Assert.assertEquals("a", Json.parse("\"a\"").asString());
        Assert.assertEquals("foo", Json.parse("\"foo\"").asString());
        Assert.assertEquals("A2-D2", Json.parse("\"A2-D2\"").asString());
        Assert.assertEquals("\u007f", Json.parse("\"\u007f\"").asString());
    }

    @Test
    public void strings_nonAsciiCharacters_areAccepted() {
        Assert.assertEquals("???????", Json.parse("\"\u0420\u0443\u0441\u0441\u043a\u0438\u0439\"").asString());
        Assert.assertEquals("???????", Json.parse("\"\u0627\u0644\u0639\u0631\u0628\u064a\u0629\"").asString());
        Assert.assertEquals("???", Json.parse("\"\u65e5\u672c\u8a9e\"").asString());
    }

    @Test
    public void strings_controlCharacters_areRejected() {
        // JSON string must not contain characters < 0x20
        assertParseException(3, "Expected valid string character", "\"--\n--\"");
        assertParseException(3, "Expected valid string character", "\"--\r\n--\"");
        assertParseException(3, "Expected valid string character", "\"--\t--\"");
        assertParseException(3, "Expected valid string character", "\"--\u0000--\"");
        assertParseException(3, "Expected valid string character", "\"--\u001f--\"");
    }

    @Test
    public void strings_validEscapes_areAccepted() {
        // valid escapes are \" \\ \/ \b \f \n \r \t and unicode escapes
        Assert.assertEquals(" \" ", Json.parse("\" \\\" \"").asString());
        Assert.assertEquals(" \\ ", Json.parse("\" \\\\ \"").asString());
        Assert.assertEquals(" / ", Json.parse("\" \\/ \"").asString());
        Assert.assertEquals(" \b ", Json.parse("\" \\b \"").asString());
        Assert.assertEquals(" \f ", Json.parse("\" \\f \"").asString());
        Assert.assertEquals(" \r ", Json.parse("\" \\r \"").asString());
        Assert.assertEquals(" \n ", Json.parse("\" \\n \"").asString());
        Assert.assertEquals(" \t ", Json.parse("\" \\t \"").asString());
    }

    @Test
    public void strings_escape_atStart() {
        Assert.assertEquals("\\x", Json.parse("\"\\\\x\"").asString());
    }

    @Test
    public void strings_escape_atEnd() {
        Assert.assertEquals("x\\", Json.parse("\"x\\\\\"").asString());
    }

    @Test
    public void strings_illegalEscapes_areRejected() {
        assertParseException(2, "Expected valid escape sequence", "\"\\a\"");
        assertParseException(2, "Expected valid escape sequence", "\"\\x\"");
        assertParseException(2, "Expected valid escape sequence", "\"\\000\"");
    }

    @Test
    public void strings_validUnicodeEscapes_areAccepted() {
        Assert.assertEquals("!", Json.parse("\"\\u0021\"").asString());
        Assert.assertEquals("\u4711", Json.parse("\"\\u4711\"").asString());
        Assert.assertEquals("\uffff", Json.parse("\"\\uffff\"").asString());
        Assert.assertEquals("\uabcdx", Json.parse("\"\\uabcdx\"").asString());
    }

    @Test
    public void strings_illegalUnicodeEscapes_areRejected() {
        assertParseException(3, "Expected hexadecimal digit", "\"\\u \"");
        assertParseException(3, "Expected hexadecimal digit", "\"\\ux\"");
        assertParseException(5, "Expected hexadecimal digit", "\"\\u20 \"");
        assertParseException(6, "Expected hexadecimal digit", "\"\\u000x\"");
    }

    @Test
    public void strings_incompleteStrings_areRejected() {
        assertParseException(1, "Unexpected end of input", "\"");
        assertParseException(4, "Unexpected end of input", "\"foo");
        assertParseException(5, "Unexpected end of input", "\"foo\\");
        assertParseException(6, "Unexpected end of input", "\"foo\\n");
        assertParseException(6, "Unexpected end of input", "\"foo\\u");
        assertParseException(7, "Unexpected end of input", "\"foo\\u0");
        assertParseException(9, "Unexpected end of input", "\"foo\\u000");
        assertParseException(10, "Unexpected end of input", "\"foo\\u0000");
    }

    @Test
    public void numbers_integer() {
        Assert.assertEquals(new JsonNumber("0"), Json.parse("0"));
        Assert.assertEquals(new JsonNumber("-0"), Json.parse("-0"));
        Assert.assertEquals(new JsonNumber("1"), Json.parse("1"));
        Assert.assertEquals(new JsonNumber("-1"), Json.parse("-1"));
        Assert.assertEquals(new JsonNumber("23"), Json.parse("23"));
        Assert.assertEquals(new JsonNumber("-23"), Json.parse("-23"));
        Assert.assertEquals(new JsonNumber("1234567890"), Json.parse("1234567890"));
        Assert.assertEquals(new JsonNumber("123456789012345678901234567890"), Json.parse("123456789012345678901234567890"));
    }

    @Test
    public void numbers_minusZero() {
        // allowed by JSON, allowed by Java
        JsonValue value = Json.parse("-0");
        Assert.assertEquals(0, value.asInt());
        Assert.assertEquals(0L, value.asLong());
        Assert.assertEquals(0.0F, value.asFloat(), 0);
        Assert.assertEquals(0.0, value.asDouble(), 0);
    }

    @Test
    public void numbers_decimal() {
        Assert.assertEquals(new JsonNumber("0.23"), Json.parse("0.23"));
        Assert.assertEquals(new JsonNumber("-0.23"), Json.parse("-0.23"));
        Assert.assertEquals(new JsonNumber("1234567890.12345678901234567890"), Json.parse("1234567890.12345678901234567890"));
    }

    @Test
    public void numbers_withExponent() {
        Assert.assertEquals(new JsonNumber("0.1e9"), Json.parse("0.1e9"));
        Assert.assertEquals(new JsonNumber("0.1e9"), Json.parse("0.1e9"));
        Assert.assertEquals(new JsonNumber("0.1E9"), Json.parse("0.1E9"));
        Assert.assertEquals(new JsonNumber("-0.23e9"), Json.parse("-0.23e9"));
        Assert.assertEquals(new JsonNumber("0.23e9"), Json.parse("0.23e9"));
        Assert.assertEquals(new JsonNumber("0.23e+9"), Json.parse("0.23e+9"));
        Assert.assertEquals(new JsonNumber("0.23e-9"), Json.parse("0.23e-9"));
    }

    @Test
    public void numbers_withInvalidFormat() {
        assertParseException(0, "Expected value", "+1");
        assertParseException(0, "Expected value", ".1");
        assertParseException(1, "Unexpected character", "02");
        assertParseException(2, "Unexpected character", "-02");
        assertParseException(1, "Expected digit", "-x");
        assertParseException(2, "Expected digit", "1.x");
        assertParseException(2, "Expected digit", "1ex");
        assertParseException(3, "Unexpected character", "1e1x");
    }

    @Test
    public void numbers_incomplete() {
        assertParseException(1, "Unexpected end of input", "-");
        assertParseException(2, "Unexpected end of input", "1.");
        assertParseException(4, "Unexpected end of input", "1.0e");
        assertParseException(5, "Unexpected end of input", "1.0e-");
    }

    @Test
    public void null_complete() {
        Assert.assertEquals(NULL, Json.parse("null"));
    }

    @Test
    public void null_incomplete() {
        assertParseException(1, "Unexpected end of input", "n");
        assertParseException(2, "Unexpected end of input", "nu");
        assertParseException(3, "Unexpected end of input", "nul");
    }

    @Test
    public void null_withIllegalCharacter() {
        assertParseException(1, "Expected 'u'", "nx");
        assertParseException(2, "Expected 'l'", "nux");
        assertParseException(3, "Expected 'l'", "nulx");
        assertParseException(4, "Unexpected character", "nullx");
    }

    @Test
    public void true_complete() {
        Assert.assertSame(TRUE, Json.parse("true"));
    }

    @Test
    public void true_incomplete() {
        assertParseException(1, "Unexpected end of input", "t");
        assertParseException(2, "Unexpected end of input", "tr");
        assertParseException(3, "Unexpected end of input", "tru");
    }

    @Test
    public void true_withIllegalCharacter() {
        assertParseException(1, "Expected 'r'", "tx");
        assertParseException(2, "Expected 'u'", "trx");
        assertParseException(3, "Expected 'e'", "trux");
        assertParseException(4, "Unexpected character", "truex");
    }

    @Test
    public void false_complete() {
        Assert.assertSame(FALSE, Json.parse("false"));
    }

    @Test
    public void false_incomplete() {
        assertParseException(1, "Unexpected end of input", "f");
        assertParseException(2, "Unexpected end of input", "fa");
        assertParseException(3, "Unexpected end of input", "fal");
        assertParseException(4, "Unexpected end of input", "fals");
    }

    @Test
    public void false_withIllegalCharacter() {
        assertParseException(1, "Expected 'a'", "fx");
        assertParseException(2, "Expected 'l'", "fax");
        assertParseException(3, "Expected 's'", "falx");
        assertParseException(4, "Expected 'e'", "falsx");
        assertParseException(5, "Unexpected character", "falsex");
    }

    static class TestHandler extends JsonHandler<Object, Object> {
        Location lastLocation;

        StringBuilder log = new StringBuilder();

        int sequence = 0;

        @Override
        public void startNull() {
            record("startNull");
        }

        @Override
        public void endNull() {
            record("endNull");
        }

        @Override
        public void startBoolean() {
            record("startBoolean");
        }

        @Override
        public void endBoolean(boolean value) {
            record("endBoolean", Boolean.valueOf(value));
        }

        @Override
        public void startString() {
            record("startString");
        }

        @Override
        public void endString(String string) {
            record("endString", string);
        }

        @Override
        public void startNumber() {
            record("startNumber");
        }

        @Override
        public void endNumber(String string) {
            record("endNumber", string);
        }

        @Override
        public Object startArray() {
            record("startArray");
            return "a" + (++(sequence));
        }

        @Override
        public void endArray(Object array) {
            record("endArray", array);
        }

        @Override
        public void startArrayValue(Object array) {
            record("startArrayValue", array);
        }

        @Override
        public void endArrayValue(Object array) {
            record("endArrayValue", array);
        }

        @Override
        public Object startObject() {
            record("startObject");
            return "o" + (++(sequence));
        }

        @Override
        public void endObject(Object object) {
            record("endObject", object);
        }

        @Override
        public void startObjectName(Object object) {
            record("startObjectName", object);
        }

        @Override
        public void endObjectName(Object object, String name) {
            record("endObjectName", object, name);
        }

        @Override
        public void startObjectValue(Object object, String name) {
            record("startObjectValue", object, name);
        }

        @Override
        public void endObjectValue(Object object, String name) {
            record("endObjectValue", object, name);
        }

        private void record(String event, Object... args) {
            lastLocation = getLocation();
            log.append(event);
            for (Object arg : args) {
                log.append(' ').append(arg);
            }
            log.append(' ').append(lastLocation.offset).append('\n');
        }

        String getLog() {
            return log.toString();
        }
    }
}

