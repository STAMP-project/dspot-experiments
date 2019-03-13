/**
 * Copyright (C) 2010 The Android Open Source Project
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
package org.json;


import JSONObject.NULL;
import junit.framework.TestCase;


public class ParsingTest extends TestCase {
    public void testParsingNoObjects() {
        try {
            new JSONTokener("").nextValue();
            TestCase.fail();
        } catch (JSONException e) {
        }
    }

    public void testParsingLiterals() throws JSONException {
        assertParsed(Boolean.TRUE, "true");
        assertParsed(Boolean.FALSE, "false");
        assertParsed(NULL, "null");
        assertParsed(NULL, "NULL");
        assertParsed(Boolean.FALSE, "False");
        assertParsed(Boolean.TRUE, "truE");
    }

    public void testParsingQuotedStrings() throws JSONException {
        assertParsed("abc", "\"abc\"");
        assertParsed("123", "\"123\"");
        assertParsed("foo\nbar", "\"foo\\nbar\"");
        assertParsed("foo bar", "\"foo\\u0020bar\"");
        assertParsed("\"{}[]/\\:,=;#", "\"\\\"{}[]/\\\\:,=;#\"");
    }

    public void testParsingSingleQuotedStrings() throws JSONException {
        assertParsed("abc", "'abc'");
        assertParsed("123", "'123'");
        assertParsed("foo\nbar", "\'foo\\nbar\'");
        assertParsed("foo bar", "\'foo\\u0020bar\'");
        assertParsed("\"{}[]/\\:,=;#", "\'\\\"{}[]/\\\\:,=;#\'");
    }

    public void testParsingUnquotedStrings() throws JSONException {
        assertParsed("abc", "abc");
        assertParsed("123abc", "123abc");
        assertParsed("123e0x", "123e0x");
        assertParsed("123e", "123e");
        assertParsed("123ee21", "123ee21");
        assertParsed("0xFFFFFFFFFFFFFFFFF", "0xFFFFFFFFFFFFFFFFF");
    }

    /**
     * Unfortunately the original implementation attempts to figure out what
     * Java number type best suits an input value.
     */
    public void testParsingNumbersThatAreBestRepresentedAsLongs() throws JSONException {
        assertParsed(9223372036854775807L, "9223372036854775807");
        assertParsed(9223372036854775806L, "9223372036854775806");
        assertParsed(-9223372036854775808L, "-9223372036854775808");
        assertParsed((-9223372036854775807L), "-9223372036854775807");
    }

    public void testParsingNumbersThatAreBestRepresentedAsIntegers() throws JSONException {
        assertParsed(0, "0");
        assertParsed(5, "5");
        assertParsed(-2147483648, "-2147483648");
        assertParsed(2147483647, "2147483647");
    }

    public void testParsingNegativeZero() throws JSONException {
        assertParsed(0, "-0");
    }

    public void testParsingIntegersWithAdditionalPrecisionYieldDoubles() throws JSONException {
        assertParsed(1.0, "1.00");
        assertParsed(1.0, "1.0");
        assertParsed(0.0, "0.0");
        assertParsed((-0.0), "-0.0");
    }

    public void testParsingNumbersThatAreBestRepresentedAsDoubles() throws JSONException {
        assertParsed(9.223372036854776E18, "9223372036854775808");
        assertParsed((-9.223372036854776E18), "-9223372036854775809");
        assertParsed(1.7976931348623157E308, "1.7976931348623157e308");
        assertParsed(2.2250738585072014E-308, "2.2250738585072014E-308");
        assertParsed(4.9E-324, "4.9E-324");
        assertParsed(4.9E-324, "4.9e-324");
    }

    public void testParsingOctalNumbers() throws JSONException {
        assertParsed(5, "05");
        assertParsed(8, "010");
        assertParsed(1046, "02026");
    }

    public void testParsingHexNumbers() throws JSONException {
        assertParsed(5, "0x5");
        assertParsed(16, "0x10");
        assertParsed(8230, "0x2026");
        assertParsed(180150010, "0xABCDEFA");
        assertParsed(2077093803, "0x7BCDEFAB");
    }

    public void testParsingLargeHexValues() throws JSONException {
        assertParsed(Integer.MAX_VALUE, "0x7FFFFFFF");
        String message = "Hex values are parsed as Strings if their signed " + "value is greater than Integer.MAX_VALUE.";
        assertParsed(message, 2147483648L, "0x80000000");
    }

    public void test64BitHexValues() throws JSONException {
        assertParsed("Large hex longs shouldn't be yield ints or strings", (-1L), "0xFFFFFFFFFFFFFFFF");
    }

    public void testParsingWithCommentsAndWhitespace() throws JSONException {
        assertParsed("baz", "  // foo bar \n baz");
        assertParsed("baz", "  // foo bar \r baz");
        assertParsed("baz", "  // foo bar \r\n baz");
        assertParsed("baz", "  # foo bar \n baz");
        assertParsed("baz", "  # foo bar \r baz");
        assertParsed("baz", "  # foo bar \r\n baz");
        assertParsed(5, "  /* foo bar \n baz */ 5");
        assertParsed(5, "  /* foo bar \n baz */ 5 // quux");
        assertParsed(5, "  5   ");
        assertParsed(5, "  5  \r\n\t ");
        assertParsed(5, "\r\n\t   5 ");
    }

    public void testParsingArrays() throws JSONException {
        assertParsed(array(), "[]");
        assertParsed(array(5, 6, true), "[5,6,true]");
        assertParsed(array(5, 6, array()), "[5,6,[]]");
        assertParsed(array(5, 6, 7), "[5;6;7]");
        assertParsed(array(5, 6, 7), "[5  , 6 \t; \r\n 7\n]");
        assertParsed(array(5, 6, 7, null), "[5,6,7,]");
        assertParsed(array(null, null), "[,]");
        assertParsed(array(5, null, null, null, 5), "[5,,,,5]");
        assertParsed(array(null, 5), "[,5]");
        assertParsed(array(null, null, null), "[,,]");
        assertParsed(array(null, null, null, 5), "[,,,5]");
    }

    public void testParsingObjects() throws JSONException {
        assertParsed(object("foo", 5), "{\"foo\": 5}");
        assertParsed(object("foo", 5), "{foo: 5}");
        assertParsed(object("foo", 5, "bar", "baz"), "{\"foo\": 5, \"bar\": \"baz\"}");
        assertParsed(object("foo", 5, "bar", "baz"), "{\"foo\": 5; \"bar\": \"baz\"}");
        assertParsed(object("foo", 5, "bar", "baz"), "{\"foo\"= 5; \"bar\"= \"baz\"}");
        assertParsed(object("foo", 5, "bar", "baz"), "{\"foo\"=> 5; \"bar\"=> \"baz\"}");
        assertParsed(object("foo", object(), "bar", array()), "{\"foo\"=> {}; \"bar\"=> []}");
        assertParsed(object("foo", object("foo", array(5, 6))), "{\"foo\": {\"foo\": [5, 6]}}");
        assertParsed(object("foo", object("foo", array(5, 6))), "{\"foo\":\n\t{\t \"foo\":[5,\r6]}}");
    }

    public void testSyntaxProblemUnterminatedObject() {
        assertParseFail("{");
        assertParseFail("{\"foo\"");
        assertParseFail("{\"foo\":");
        assertParseFail("{\"foo\":bar");
        assertParseFail("{\"foo\":bar,");
        assertParseFail("{\"foo\":bar,\"baz\"");
        assertParseFail("{\"foo\":bar,\"baz\":");
        assertParseFail("{\"foo\":bar,\"baz\":true");
        assertParseFail("{\"foo\":bar,\"baz\":true,");
    }

    public void testSyntaxProblemEmptyString() {
        assertParseFail("");
    }

    public void testSyntaxProblemUnterminatedArray() {
        assertParseFail("[");
        assertParseFail("[,");
        assertParseFail("[,,");
        assertParseFail("[true");
        assertParseFail("[true,");
        assertParseFail("[true,,");
    }

    public void testSyntaxProblemMalformedObject() {
        assertParseFail("{:}");
        assertParseFail("{\"key\":}");
        assertParseFail("{:true}");
        assertParseFail("{\"key\":true:}");
        assertParseFail("{null:true}");
        assertParseFail("{true:true}");
        assertParseFail("{0xFF:true}");
    }
}

