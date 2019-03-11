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
package org.apache.kafka.connect.data;


import Schema.BOOLEAN_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import Time.SCHEMA;
import Type.STRING;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.errors.DataException;
import org.junit.Assert;
import org.junit.Test;


public class ValuesTest {
    private static final long MILLIS_PER_DAY = ((24 * 60) * 60) * 1000;

    private static final Map<String, String> STRING_MAP = new LinkedHashMap<>();

    private static final Schema STRING_MAP_SCHEMA = SchemaBuilder.map(STRING_SCHEMA, STRING_SCHEMA).schema();

    private static final Map<String, Short> STRING_SHORT_MAP = new LinkedHashMap<>();

    private static final Schema STRING_SHORT_MAP_SCHEMA = SchemaBuilder.map(STRING_SCHEMA, INT16_SCHEMA).schema();

    private static final Map<String, Integer> STRING_INT_MAP = new LinkedHashMap<>();

    private static final Schema STRING_INT_MAP_SCHEMA = SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA).schema();

    private static final List<Integer> INT_LIST = new ArrayList<>();

    private static final Schema INT_LIST_SCHEMA = SchemaBuilder.array(INT32_SCHEMA).schema();

    private static final List<String> STRING_LIST = new ArrayList<>();

    private static final Schema STRING_LIST_SCHEMA = SchemaBuilder.array(STRING_SCHEMA).schema();

    static {
        ValuesTest.STRING_MAP.put("foo", "123");
        ValuesTest.STRING_MAP.put("bar", "baz");
        ValuesTest.STRING_SHORT_MAP.put("foo", ((short) (12345)));
        ValuesTest.STRING_SHORT_MAP.put("bar", ((short) (0)));
        ValuesTest.STRING_SHORT_MAP.put("baz", ((short) (-4321)));
        ValuesTest.STRING_INT_MAP.put("foo", 1234567890);
        ValuesTest.STRING_INT_MAP.put("bar", 0);
        ValuesTest.STRING_INT_MAP.put("baz", (-987654321));
        ValuesTest.STRING_LIST.add("foo");
        ValuesTest.STRING_LIST.add("bar");
        ValuesTest.INT_LIST.add(1234567890);
        ValuesTest.INT_LIST.add((-987654321));
    }

    @Test
    public void shouldEscapeStringsWithEmbeddedQuotesAndBackslashes() {
        String original = "three\"blind\\\"mice";
        String expected = "three\\\"blind\\\\\\\"mice";
        Assert.assertEquals(expected, Values.escape(original));
    }

    @Test
    public void shouldConvertNullValue() {
        assertRoundTrip(STRING_SCHEMA, STRING_SCHEMA, null);
        assertRoundTrip(OPTIONAL_STRING_SCHEMA, STRING_SCHEMA, null);
    }

    @Test
    public void shouldConvertBooleanValues() {
        assertRoundTrip(BOOLEAN_SCHEMA, BOOLEAN_SCHEMA, Boolean.FALSE);
        SchemaAndValue resultFalse = roundTrip(BOOLEAN_SCHEMA, "false");
        Assert.assertEquals(BOOLEAN_SCHEMA, resultFalse.schema());
        Assert.assertEquals(Boolean.FALSE, resultFalse.value());
        assertRoundTrip(BOOLEAN_SCHEMA, BOOLEAN_SCHEMA, Boolean.TRUE);
        SchemaAndValue resultTrue = roundTrip(BOOLEAN_SCHEMA, "true");
        Assert.assertEquals(BOOLEAN_SCHEMA, resultTrue.schema());
        Assert.assertEquals(Boolean.TRUE, resultTrue.value());
    }

    @Test(expected = DataException.class)
    public void shouldFailToParseInvalidBooleanValueString() {
        Values.convertToBoolean(STRING_SCHEMA, "\"green\"");
    }

    @Test
    public void shouldConvertSimpleString() {
        assertRoundTrip(STRING_SCHEMA, "simple");
    }

    @Test
    public void shouldConvertEmptyString() {
        assertRoundTrip(STRING_SCHEMA, "");
    }

    @Test
    public void shouldConvertStringWithQuotesAndOtherDelimiterCharacters() {
        assertRoundTrip(STRING_SCHEMA, STRING_SCHEMA, "three\"blind\\\"mice");
        assertRoundTrip(STRING_SCHEMA, STRING_SCHEMA, "string with delimiters: <>?,./\\=+-!@#$%^&*(){}[]|;\':");
    }

    @Test
    public void shouldConvertMapWithStringKeys() {
        assertRoundTrip(ValuesTest.STRING_MAP_SCHEMA, ValuesTest.STRING_MAP_SCHEMA, ValuesTest.STRING_MAP);
    }

    @Test
    public void shouldParseStringOfMapWithStringValuesWithoutWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(ValuesTest.STRING_MAP_SCHEMA, "{\"foo\":\"123\",\"bar\":\"baz\"}");
        Assert.assertEquals(ValuesTest.STRING_MAP_SCHEMA, result.schema());
        Assert.assertEquals(ValuesTest.STRING_MAP, result.value());
    }

    @Test
    public void shouldParseStringOfMapWithStringValuesWithWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(ValuesTest.STRING_MAP_SCHEMA, "{ \"foo\" : \"123\", \n\"bar\" : \"baz\" } ");
        Assert.assertEquals(ValuesTest.STRING_MAP_SCHEMA, result.schema());
        Assert.assertEquals(ValuesTest.STRING_MAP, result.value());
    }

    @Test
    public void shouldConvertMapWithStringKeysAndShortValues() {
        assertRoundTrip(ValuesTest.STRING_SHORT_MAP_SCHEMA, ValuesTest.STRING_SHORT_MAP_SCHEMA, ValuesTest.STRING_SHORT_MAP);
    }

    @Test
    public void shouldParseStringOfMapWithShortValuesWithoutWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(ValuesTest.STRING_SHORT_MAP_SCHEMA, "{\"foo\":12345,\"bar\":0,\"baz\":-4321}");
        Assert.assertEquals(ValuesTest.STRING_SHORT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(ValuesTest.STRING_SHORT_MAP, result.value());
    }

    @Test
    public void shouldParseStringOfMapWithShortValuesWithWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(ValuesTest.STRING_SHORT_MAP_SCHEMA, " { \"foo\" :  12345 , \"bar\" : 0,  \"baz\" : -4321 }  ");
        Assert.assertEquals(ValuesTest.STRING_SHORT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(ValuesTest.STRING_SHORT_MAP, result.value());
    }

    @Test
    public void shouldConvertMapWithStringKeysAndIntegerValues() {
        assertRoundTrip(ValuesTest.STRING_INT_MAP_SCHEMA, ValuesTest.STRING_INT_MAP_SCHEMA, ValuesTest.STRING_INT_MAP);
    }

    @Test
    public void shouldParseStringOfMapWithIntValuesWithoutWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(ValuesTest.STRING_INT_MAP_SCHEMA, "{\"foo\":1234567890,\"bar\":0,\"baz\":-987654321}");
        Assert.assertEquals(ValuesTest.STRING_INT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(ValuesTest.STRING_INT_MAP, result.value());
    }

    @Test
    public void shouldParseStringOfMapWithIntValuesWithWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(ValuesTest.STRING_INT_MAP_SCHEMA, " { \"foo\" :  1234567890 , \"bar\" : 0,  \"baz\" : -987654321 }  ");
        Assert.assertEquals(ValuesTest.STRING_INT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(ValuesTest.STRING_INT_MAP, result.value());
    }

    @Test
    public void shouldConvertListWithStringValues() {
        assertRoundTrip(ValuesTest.STRING_LIST_SCHEMA, ValuesTest.STRING_LIST_SCHEMA, ValuesTest.STRING_LIST);
    }

    @Test
    public void shouldConvertListWithIntegerValues() {
        assertRoundTrip(ValuesTest.INT_LIST_SCHEMA, ValuesTest.INT_LIST_SCHEMA, ValuesTest.INT_LIST);
    }

    /**
     * The parsed array has byte values and one int value, so we should return list with single unified type of integers.
     */
    @Test
    public void shouldConvertStringOfListWithOnlyNumericElementTypesIntoListOfLargestNumericType() {
        int thirdValue = (Short.MAX_VALUE) + 1;
        List<?> list = Values.convertToList(STRING_SCHEMA, (("[1, 2, " + thirdValue) + "]"));
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(1, ((Number) (list.get(0))).intValue());
        Assert.assertEquals(2, ((Number) (list.get(1))).intValue());
        Assert.assertEquals(thirdValue, ((Number) (list.get(2))).intValue());
    }

    /**
     * The parsed array has byte values and one int value, so we should return list with single unified type of integers.
     */
    @Test
    public void shouldConvertStringOfListWithMixedElementTypesIntoListWithDifferentElementTypes() {
        String str = "[1, 2, \"three\"]";
        List<?> list = Values.convertToList(STRING_SCHEMA, str);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(1, ((Number) (list.get(0))).intValue());
        Assert.assertEquals(2, ((Number) (list.get(1))).intValue());
        Assert.assertEquals("three", list.get(2));
    }

    /**
     * We parse into different element types, but cannot infer a common element schema.
     */
    @Test
    public void shouldParseStringListWithMultipleElementTypesAndReturnListWithNoSchema() {
        String str = "[1, 2, 3, \"four\"]";
        SchemaAndValue result = Values.parseString(str);
        Assert.assertNull(result.schema());
        List<?> list = ((List<?>) (result.value()));
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(1, ((Number) (list.get(0))).intValue());
        Assert.assertEquals(2, ((Number) (list.get(1))).intValue());
        Assert.assertEquals(3, ((Number) (list.get(2))).intValue());
        Assert.assertEquals("four", list.get(3));
    }

    /**
     * We can't infer or successfully parse into a different type, so this returns the same string.
     */
    @Test
    public void shouldParseStringListWithExtraDelimitersAndReturnString() {
        String str = "[1, 2, 3,,,]";
        SchemaAndValue result = Values.parseString(str);
        Assert.assertEquals(STRING, result.schema().type());
        Assert.assertEquals(str, result.value());
    }

    /**
     * This is technically invalid JSON, and we don't want to simply ignore the blank elements.
     */
    @Test(expected = DataException.class)
    public void shouldFailToConvertToListFromStringWithExtraDelimiters() {
        Values.convertToList(STRING_SCHEMA, "[1, 2, 3,,,]");
    }

    /**
     * Schema of type ARRAY requires a schema for the values, but Connect has no union or "any" schema type.
     * Therefore, we can't represent this.
     */
    @Test(expected = DataException.class)
    public void shouldFailToConvertToListFromStringWithNonCommonElementTypeAndBlankElement() {
        Values.convertToList(STRING_SCHEMA, "[1, 2, 3, \"four\",,,]");
    }

    /**
     * This is technically invalid JSON, and we don't want to simply ignore the blank entry.
     */
    @Test(expected = DataException.class)
    public void shouldFailToParseStringOfMapWithIntValuesWithBlankEntry() {
        Values.convertToList(STRING_SCHEMA, " { \"foo\" :  1234567890 ,, \"bar\" : 0,  \"baz\" : -987654321 }  ");
    }

    /**
     * This is technically invalid JSON, and we don't want to simply ignore the malformed entry.
     */
    @Test(expected = DataException.class)
    public void shouldFailToParseStringOfMalformedMap() {
        Values.convertToList(STRING_SCHEMA, " { \"foo\" :  1234567890 , \"a\", \"bar\" : 0,  \"baz\" : -987654321 }  ");
    }

    /**
     * This is technically invalid JSON, and we don't want to simply ignore the blank entries.
     */
    @Test(expected = DataException.class)
    public void shouldFailToParseStringOfMapWithIntValuesWithOnlyBlankEntries() {
        Values.convertToList(STRING_SCHEMA, " { ,,  , , }  ");
    }

    /**
     * This is technically invalid JSON, and we don't want to simply ignore the blank entry.
     */
    @Test(expected = DataException.class)
    public void shouldFailToParseStringOfMapWithIntValuesWithBlankEntries() {
        Values.convertToList(STRING_SCHEMA, " { \"foo\" :  \"1234567890\" ,, \"bar\" : \"0\",  \"baz\" : \"boz\" }  ");
    }

    /**
     * Schema for Map requires a schema for key and value, but we have no key or value and Connect has no "any" type
     */
    @Test(expected = DataException.class)
    public void shouldFailToParseStringOfEmptyMap() {
        Values.convertToList(STRING_SCHEMA, " { }  ");
    }

    @Test
    public void shouldParseStringsWithoutDelimiters() {
        // assertParsed("");
        assertParsed("  ");
        assertParsed("simple");
        assertParsed("simple string");
        assertParsed("simple \n\t\bstring");
        assertParsed("'simple' string");
        assertParsed("si\\mple");
        assertParsed("si\\\\mple");
    }

    @Test
    public void shouldParseStringsWithEscapedDelimiters() {
        assertParsed("si\\\"mple");
        assertParsed("si\\{mple");
        assertParsed("si\\}mple");
        assertParsed("si\\]mple");
        assertParsed("si\\[mple");
        assertParsed("si\\:mple");
        assertParsed("si\\,mple");
    }

    @Test
    public void shouldParseStringsWithSingleDelimiter() {
        assertParsed("a{b", "a", "{", "b");
        assertParsed("a}b", "a", "}", "b");
        assertParsed("a[b", "a", "[", "b");
        assertParsed("a]b", "a", "]", "b");
        assertParsed("a:b", "a", ":", "b");
        assertParsed("a,b", "a", ",", "b");
        assertParsed("a\"b", "a", "\"", "b");
        assertParsed("{b", "{", "b");
        assertParsed("}b", "}", "b");
        assertParsed("[b", "[", "b");
        assertParsed("]b", "]", "b");
        assertParsed(":b", ":", "b");
        assertParsed(",b", ",", "b");
        assertParsed("\"b", "\"", "b");
        assertParsed("{", "{");
        assertParsed("}", "}");
        assertParsed("[", "[");
        assertParsed("]", "]");
        assertParsed(":", ":");
        assertParsed(",", ",");
        assertParsed("\"", "\"");
    }

    @Test
    public void shouldParseStringsWithMultipleDelimiters() {
        assertParsed("\"simple\" string", "\"", "simple", "\"", " string");
        assertParsed("a{bc}d", "a", "{", "bc", "}", "d");
        assertParsed("a { b c } d", "a ", "{", " b c ", "}", " d");
        assertParsed("a { b c } d", "a ", "{", " b c ", "}", " d");
    }

    @Test
    public void shouldConvertTimeValues() {
        Date current = new Date();
        long currentMillis = (current.getTime()) % (ValuesTest.MILLIS_PER_DAY);
        // java.util.Date - just copy
        Date t1 = Values.convertToTime(SCHEMA, current);
        Assert.assertEquals(current, t1);
        // java.util.Date as a Timestamp - discard the date and keep just day's milliseconds
        t1 = Values.convertToTime(Timestamp.SCHEMA, current);
        Assert.assertEquals(new Date(currentMillis), t1);
        // ISO8601 strings - currently broken because tokenization breaks at colon
        // Millis as string
        Date t3 = Values.convertToTime(SCHEMA, Long.toString(currentMillis));
        Assert.assertEquals(currentMillis, t3.getTime());
        // Millis as long
        Date t4 = Values.convertToTime(SCHEMA, currentMillis);
        Assert.assertEquals(currentMillis, t4.getTime());
    }

    @Test
    public void shouldConvertDateValues() {
        Date current = new Date();
        long currentMillis = (current.getTime()) % (ValuesTest.MILLIS_PER_DAY);
        long days = (current.getTime()) / (ValuesTest.MILLIS_PER_DAY);
        // java.util.Date - just copy
        Date d1 = Values.convertToDate(Date.SCHEMA, current);
        Assert.assertEquals(current, d1);
        // java.util.Date as a Timestamp - discard the day's milliseconds and keep the date
        Date currentDate = new Date(((current.getTime()) - currentMillis));
        d1 = Values.convertToDate(Timestamp.SCHEMA, currentDate);
        Assert.assertEquals(currentDate, d1);
        // ISO8601 strings - currently broken because tokenization breaks at colon
        // Days as string
        Date d3 = Values.convertToDate(Date.SCHEMA, Long.toString(days));
        Assert.assertEquals(currentDate, d3);
        // Days as long
        Date d4 = Values.convertToDate(Date.SCHEMA, days);
        Assert.assertEquals(currentDate, d4);
    }

    @Test
    public void shouldConvertTimestampValues() {
        Date current = new Date();
        long currentMillis = (current.getTime()) % (ValuesTest.MILLIS_PER_DAY);
        // java.util.Date - just copy
        Date ts1 = Values.convertToTimestamp(Timestamp.SCHEMA, current);
        Assert.assertEquals(current, ts1);
        // java.util.Date as a Timestamp - discard the day's milliseconds and keep the date
        Date currentDate = new Date(((current.getTime()) - currentMillis));
        ts1 = Values.convertToTimestamp(Date.SCHEMA, currentDate);
        Assert.assertEquals(currentDate, ts1);
        // java.util.Date as a Time - discard the date and keep the day's milliseconds
        ts1 = Values.convertToTimestamp(SCHEMA, currentMillis);
        Assert.assertEquals(new Date(currentMillis), ts1);
        // ISO8601 strings - currently broken because tokenization breaks at colon
        // Millis as string
        Date ts3 = Values.convertToTimestamp(Timestamp.SCHEMA, Long.toString(current.getTime()));
        Assert.assertEquals(current, ts3);
        // Millis as long
        Date ts4 = Values.convertToTimestamp(Timestamp.SCHEMA, current.getTime());
        Assert.assertEquals(current, ts4);
    }
}

