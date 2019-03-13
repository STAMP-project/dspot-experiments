/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization;


import ConfigurationOptions.ES_MAPPING_EXCLUDE;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.util.FastByteArrayOutputStream;
import org.elasticsearch.hadoop.util.TestSettings;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class PigNamedTypeToJsonTest {
    private static FastByteArrayOutputStream out;

    @Test
    public void testNamedNull() {
        String expected = "{\"name\":null}";
        Assert.assertThat(pigTypeToJson(createTuple(null, createSchema("name:bytearray"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonymousNull() {
        String expected = "{\"val_0\":null}";
        Assert.assertThat(pigTypeToJson(createTuple(null, createSchema("bytearray"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedString() {
        String expected = "{\"name\":\"some string\"}";
        Assert.assertThat(pigTypeToJson(createTuple("some string", createSchema("name:chararray"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonString() {
        String expected = "{\"val_0\":\"some string\"}";
        Assert.assertThat(pigTypeToJson(createTuple("some string", createSchema("chararray"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedLong() {
        String expected = ("{\"name\":" + (Long.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Long.MAX_VALUE, createSchema("name:long"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonLong() {
        String expected = ("{\"val_0\":" + (Long.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Long.MAX_VALUE, createSchema("long"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonInteger() {
        String expected = ("{\"val_0\":" + (Integer.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Integer.MAX_VALUE, createSchema("int"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedInteger() {
        String expected = ("{\"name\":" + (Integer.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Integer.MAX_VALUE, createSchema("name:int"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedDouble() {
        String expected = ("{\"name\":" + (Double.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Double.MAX_VALUE, createSchema("name:double"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonDouble() {
        String expected = ("{\"val_0\":" + (Double.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Double.MAX_VALUE, createSchema("double"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedFloat() {
        String expected = ("{\"name\":" + (Float.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Float.MAX_VALUE, createSchema("name:float"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonFloat() {
        String expected = ("{\"val_0\":" + (Float.MAX_VALUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Float.MAX_VALUE, createSchema("float"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonBoolean() {
        String expected = ("{\"val_0\":" + (Boolean.TRUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Boolean.TRUE, createSchema("boolean"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedBoolean() {
        String expected = ("{\"name\":" + (Boolean.TRUE)) + "}";
        Assert.assertThat(pigTypeToJson(createTuple(Boolean.TRUE, createSchema("name:boolean"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedByte() {
        String expected = ("{\"name\":" + (Byte.MAX_VALUE)) + "}";
        // byte is not recognized by the schema
        Assert.assertThat(pigTypeToJson(createTuple(Byte.MAX_VALUE, createSchema("name:int"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonByte() {
        String expected = ("{\"val_0\":" + (Byte.MAX_VALUE)) + "}";
        // byte is not recognized by the schema
        Assert.assertThat(pigTypeToJson(createTuple(Byte.MAX_VALUE, createSchema("int"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedByteArray() {
        String expected = "{\"name\":\"Ynl0ZSBhcnJheQ==\"}";
        Assert.assertThat(pigTypeToJson(createTuple(new DataByteArray("byte array".getBytes()), createSchema("name:bytearray"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonByteArray() {
        String expected = "{\"val_0\":\"Ynl0ZSBhcnJheQ==\"}";
        Assert.assertThat(pigTypeToJson(createTuple(new DataByteArray("byte array".getBytes()), createSchema("bytearray"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedTuple() {
        String expected = "{\"namedtuple\":[{\"first\":\"one\",\"second\":\"two\"}]}";
        Assert.assertThat(pigTypeToJson(createTuple(TupleFactory.getInstance().newTuple(Arrays.asList(new String[]{ "one", "two" })), createSchema("namedtuple: (first:chararray, second:chararray)"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedTupleWithMixedValues() {
        String expected = "{\"namedtuplewithmixedvalues\":[{\"first\":1,\"second\":\"two\"}]}";
        Assert.assertThat(pigTypeToJson(createTuple(TupleFactory.getInstance().newTuple(Arrays.asList(new Object[]{ 1, "two" })), createSchema("namedtuplewithmixedvalues: (first:int, second:chararray)"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonTuple() {
        String expected = "{\"anontuple\":[{\"val_0\":\"xxx\",\"val_1\":\"yyy\",\"val_2\":\"zzz\"}]}";
        Assert.assertThat(pigTypeToJson(createTuple(TupleFactory.getInstance().newTuple(Arrays.asList(new String[]{ "xxx", "yyy", "zzz" })), createSchema("anontuple: (chararray, chararray, chararray)"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedMap() {
        String expected = "{\"map\":{\"one\":1,\"two\":2,\"three\":3}}";
        Map<String, Number> map = new LinkedHashMap<String, Number>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        Assert.assertThat(pigTypeToJson(createTuple(map, createSchema("map: [int]"))), CoreMatchers.is(expected));
    }

    @Test
    public void testAnonMap() {
        String expected = "{\"map_0\":{\"one\":1,\"two\":2,\"three\":3}}";
        Map<String, Number> map = new LinkedHashMap<String, Number>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        Assert.assertThat(pigTypeToJson(createTuple(map, createSchema("[int]"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedBag() {
        String expected = "{\"bag\":[[{\"first\":\"one\",\"second\":\"two\",\"third\":\"three\"}]," + ("[{\"first\":\"one\",\"second\":\"two\",\"third\":\"three\"}]," + "[{\"first\":\"one\",\"second\":\"two\",\"third\":\"three\"}]]}");
        Tuple tuple = TupleFactory.getInstance().newTuple(Arrays.asList(new String[]{ "one", "two", "three" }));
        Assert.assertThat(pigTypeToJson(createTuple(new org.apache.pig.data.DefaultDataBag(Arrays.asList(new Tuple[]{ tuple, tuple, tuple })), createSchema("bag: {t:(first:chararray, second:chararray, third: chararray)}"))), CoreMatchers.is(expected));
    }

    @Test
    public void testBagWithAnonTuple() {
        String expected = "{\"bag_0\":[[{\"val_0\":\"xxx\",\"val_1\":\"yyy\"}]," + ("[{\"val_0\":\"xxx\",\"val_1\":\"yyy\"}]," + "[{\"val_0\":\"xxx\",\"val_1\":\"yyy\"}]]}");
        Tuple tuple = TupleFactory.getInstance().newTuple(Arrays.asList(new String[]{ "xxx", "yyy" }));
        Assert.assertThat(pigTypeToJson(createTuple(new org.apache.pig.data.DefaultDataBag(Arrays.asList(new Tuple[]{ tuple, tuple, tuple })), createSchema("{t:(chararray, chararray)}"))), CoreMatchers.is(expected));
    }

    @Test
    public void testNamedTupleWithExclusion() {
        Settings settings = new TestSettings();
        settings.setProperty(ES_MAPPING_EXCLUDE, "namedtuple.second");
        String expected = "{\"namedtuple\":[{\"first\":\"one\"}]}";
        Assert.assertThat(pigTypeToJson(createTuple(TupleFactory.getInstance().newTuple(Arrays.asList(new String[]{ "one", "two" })), createSchema("namedtuple: (first:chararray, second:chararray)")), settings), CoreMatchers.is(expected));
    }
}

