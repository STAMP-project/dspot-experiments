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


import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;


public class SimpleHeaderConverterTest {
    private static final String TOPIC = "topic";

    private static final String HEADER = "header";

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
        SimpleHeaderConverterTest.STRING_MAP.put("foo", "123");
        SimpleHeaderConverterTest.STRING_MAP.put("bar", "baz");
        SimpleHeaderConverterTest.STRING_SHORT_MAP.put("foo", ((short) (12345)));
        SimpleHeaderConverterTest.STRING_SHORT_MAP.put("bar", ((short) (0)));
        SimpleHeaderConverterTest.STRING_SHORT_MAP.put("baz", ((short) (-4321)));
        SimpleHeaderConverterTest.STRING_INT_MAP.put("foo", 1234567890);
        SimpleHeaderConverterTest.STRING_INT_MAP.put("bar", 0);
        SimpleHeaderConverterTest.STRING_INT_MAP.put("baz", (-987654321));
        SimpleHeaderConverterTest.STRING_LIST.add("foo");
        SimpleHeaderConverterTest.STRING_LIST.add("bar");
        SimpleHeaderConverterTest.INT_LIST.add(1234567890);
        SimpleHeaderConverterTest.INT_LIST.add((-987654321));
    }

    private SimpleHeaderConverter converter;

    @Test
    public void shouldConvertNullValue() {
        assertRoundTrip(STRING_SCHEMA, null);
        assertRoundTrip(OPTIONAL_STRING_SCHEMA, null);
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
        assertRoundTrip(STRING_SCHEMA, "three\"blind\\\"mice");
        assertRoundTrip(STRING_SCHEMA, "string with delimiters: <>?,./\\=+-!@#$%^&*(){}[]|;\':");
    }

    @Test
    public void shouldConvertMapWithStringKeys() {
        assertRoundTrip(SimpleHeaderConverterTest.STRING_MAP_SCHEMA, SimpleHeaderConverterTest.STRING_MAP);
    }

    @Test
    public void shouldParseStringOfMapWithStringValuesWithoutWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(STRING_SCHEMA, "{\"foo\":\"123\",\"bar\":\"baz\"}");
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_MAP_SCHEMA, result.schema());
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_MAP, result.value());
    }

    @Test
    public void shouldParseStringOfMapWithStringValuesWithWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(STRING_SCHEMA, "{ \"foo\" : \"123\", \n\"bar\" : \"baz\" } ");
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_MAP_SCHEMA, result.schema());
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_MAP, result.value());
    }

    @Test
    public void shouldConvertMapWithStringKeysAndShortValues() {
        assertRoundTrip(SimpleHeaderConverterTest.STRING_SHORT_MAP_SCHEMA, SimpleHeaderConverterTest.STRING_SHORT_MAP);
    }

    @Test
    public void shouldParseStringOfMapWithShortValuesWithoutWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(STRING_SCHEMA, "{\"foo\":12345,\"bar\":0,\"baz\":-4321}");
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_SHORT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_SHORT_MAP, result.value());
    }

    @Test
    public void shouldParseStringOfMapWithShortValuesWithWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(STRING_SCHEMA, " { \"foo\" :  12345 , \"bar\" : 0,  \"baz\" : -4321 }  ");
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_SHORT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_SHORT_MAP, result.value());
    }

    @Test
    public void shouldConvertMapWithStringKeysAndIntegerValues() {
        assertRoundTrip(SimpleHeaderConverterTest.STRING_INT_MAP_SCHEMA, SimpleHeaderConverterTest.STRING_INT_MAP);
    }

    @Test
    public void shouldParseStringOfMapWithIntValuesWithoutWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(STRING_SCHEMA, "{\"foo\":1234567890,\"bar\":0,\"baz\":-987654321}");
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_INT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_INT_MAP, result.value());
    }

    @Test
    public void shouldParseStringOfMapWithIntValuesWithWhitespaceAsMap() {
        SchemaAndValue result = roundTrip(STRING_SCHEMA, " { \"foo\" :  1234567890 , \"bar\" : 0,  \"baz\" : -987654321 }  ");
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_INT_MAP_SCHEMA, result.schema());
        Assert.assertEquals(SimpleHeaderConverterTest.STRING_INT_MAP, result.value());
    }

    @Test
    public void shouldConvertListWithStringValues() {
        assertRoundTrip(SimpleHeaderConverterTest.STRING_LIST_SCHEMA, SimpleHeaderConverterTest.STRING_LIST);
    }

    @Test
    public void shouldConvertListWithIntegerValues() {
        assertRoundTrip(SimpleHeaderConverterTest.INT_LIST_SCHEMA, SimpleHeaderConverterTest.INT_LIST);
    }

    @Test
    public void shouldConvertMapWithStringKeysAndMixedValuesToMapWithoutSchema() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("foo", "bar");
        map.put("baz", ((short) (3456)));
        assertRoundTrip(null, map);
    }

    @Test
    public void shouldConvertListWithMixedValuesToListWithoutSchema() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(((short) (13344)));
        assertRoundTrip(null, list);
    }

    @Test
    public void shouldConvertEmptyMapToMapWithoutSchema() {
        assertRoundTrip(null, new LinkedHashMap<>());
    }

    @Test
    public void shouldConvertEmptyListToListWithoutSchema() {
        assertRoundTrip(null, new ArrayList<>());
    }
}

