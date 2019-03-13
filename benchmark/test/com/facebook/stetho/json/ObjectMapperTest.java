/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.stetho.json;


import Build.VERSION_CODES;
import com.facebook.stetho.json.annotation.JsonProperty;
import com.facebook.stetho.json.annotation.JsonValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link ObjectMapper}
 */
@Config(emulateSdk = VERSION_CODES.JELLY_BEAN)
@RunWith(RobolectricTestRunner.class)
public class ObjectMapperTest {
    private ObjectMapper mObjectMapper;

    @Test
    public void testJsonProperty() throws IOException, JSONException {
        ObjectMapperTest.JsonPropertyString c = new ObjectMapperTest.JsonPropertyString();
        c.testString = "test";
        String expected = "{\"testString\":\"test\"}";
        JSONObject jsonObject = mObjectMapper.convertValue(c, JSONObject.class);
        String str = jsonObject.toString();
        Assert.assertEquals(expected, str);
        ObjectMapperTest.JsonPropertyString jsonPropertyString = mObjectMapper.convertValue(new JSONObject(expected), ObjectMapperTest.JsonPropertyString.class);
        Assert.assertEquals(c, jsonPropertyString);
    }

    @Test
    public void testNestedProperty() throws JSONException {
        ObjectMapperTest.NestedJsonProperty njp = new ObjectMapperTest.NestedJsonProperty();
        njp.child1 = new ObjectMapperTest.JsonPropertyString();
        njp.child2 = new ObjectMapperTest.JsonPropertyInt();
        njp.child1.testString = "testString";
        njp.child2.i = 4;
        // The ordering of serialization changes depending on Java 7 vs Java 8.
        String expected7 = "{\"child1\":{\"testString\":\"testString\"},\"child2\":{\"i\":4}}";
        String expected8 = "{\"child2\":{\"i\":4},\"child1\":{\"testString\":\"testString\"}}";
        ObjectMapperTest.NestedJsonProperty parsed7 = mObjectMapper.convertValue(new JSONObject(expected7), ObjectMapperTest.NestedJsonProperty.class);
        Assert.assertEquals(njp, parsed7);
        ObjectMapperTest.NestedJsonProperty parsed8 = mObjectMapper.convertValue(new JSONObject(expected8), ObjectMapperTest.NestedJsonProperty.class);
        Assert.assertEquals(njp, parsed8);
        JSONObject jsonObject = mObjectMapper.convertValue(njp, JSONObject.class);
        Assert.assertTrue(((expected7.equals(jsonObject.toString())) || (expected8.equals(jsonObject.toString()))));
    }

    @Test
    public void testEnumProperty() throws JSONException {
        ObjectMapperTest.JsonPropertyEnum jpe = new ObjectMapperTest.JsonPropertyEnum();
        jpe.enumValue = ObjectMapperTest.TestEnum.VALUE_TWO;
        String expected = "{\"enumValue\":\"two\"}";
        ObjectMapperTest.JsonPropertyEnum parsed = mObjectMapper.convertValue(new JSONObject(expected), ObjectMapperTest.JsonPropertyEnum.class);
        Assert.assertEquals(jpe, parsed);
        JSONObject jsonObject = mObjectMapper.convertValue(jpe, JSONObject.class);
        Assert.assertEquals(expected, jsonObject.toString());
    }

    @Test
    public void testListString() throws JSONException {
        ObjectMapperTest.JsonPropertyStringList jpsl = new ObjectMapperTest.JsonPropertyStringList();
        List<String> values = new ArrayList<String>();
        jpsl.stringList = values;
        values.add("one");
        values.add("two");
        values.add("three");
        String expected = "{\"stringList\":[\"one\",\"two\",\"three\"]}";
        ObjectMapperTest.JsonPropertyStringList jsonPropertyStringList = mObjectMapper.convertValue(new JSONObject(expected), ObjectMapperTest.JsonPropertyStringList.class);
        Assert.assertEquals(jpsl, jsonPropertyStringList);
        JSONObject jsonObject = mObjectMapper.convertValue(jpsl, JSONObject.class);
        String str = jsonObject.toString();
        Assert.assertEquals(expected, str);
    }

    @Test
    public void testSerializeMultitypedList() throws JSONException {
        List<Object> list = new ArrayList<Object>();
        list.add("foo");
        list.add(Collections.singletonList("bar"));
        ObjectMapperTest.JsonPropertyMultitypedList javaObj = new ObjectMapperTest.JsonPropertyMultitypedList();
        javaObj.multitypedList = list;
        String expected = "{\"multitypedList\":[\"foo\",[\"bar\"]]}";
        JSONObject jsonObj = mObjectMapper.convertValue(javaObj, JSONObject.class);
        String str = jsonObj.toString();
        Assert.assertEquals(expected, str);
    }

    @Test
    public void testSerializeListOfLists() throws JSONException {
        List<List<String>> listOfLists = new ArrayList<List<String>>();
        listOfLists.add(Collections.singletonList("foo"));
        ArrayList<String> sublist2 = new ArrayList<String>();
        sublist2.add("1");
        sublist2.add("2");
        listOfLists.add(sublist2);
        ObjectMapperTest.JsonPropertyListOfLists javaObj = new ObjectMapperTest.JsonPropertyListOfLists();
        javaObj.listOfLists = listOfLists;
        String expected = "{\"listOfLists\":[[\"foo\"],[\"1\",\"2\"]]}";
        JSONObject jsonObj = mObjectMapper.convertValue(javaObj, JSONObject.class);
        String str = jsonObj.toString();
        Assert.assertEquals(expected, str);
    }

    @Test
    public void testObjectToPrimitive() throws JSONException {
        ObjectMapperTest.ArrayOfPrimitivesContainer container = new ObjectMapperTest.ArrayOfPrimitivesContainer();
        ArrayList<Object> primitives = container.primitives;
        primitives.add(Long.MIN_VALUE);
        primitives.add(Long.MAX_VALUE);
        primitives.add(Integer.MIN_VALUE);
        primitives.add(Integer.MAX_VALUE);
        primitives.add(Float.MIN_VALUE);
        primitives.add(Float.MAX_VALUE);
        primitives.add(Double.MIN_VALUE);
        primitives.add(Double.MAX_VALUE);
        String json = mObjectMapper.convertValue(container, JSONObject.class).toString();
        JSONObject obj = new JSONObject(json);
        JSONArray array = obj.getJSONArray("primitives");
        ArrayList<Object> actual = new ArrayList<>();
        for (int i = 0, N = array.length(); i < N; i++) {
            actual.add(array.get(i));
        }
        Assert.assertEquals(primitives.toString(), actual.toString());
    }

    public static class ArrayOfPrimitivesContainer {
        @JsonProperty
        public final ArrayList<Object> primitives = new ArrayList<>();
    }

    public static class NestedJsonProperty {
        @JsonProperty(required = true)
        public ObjectMapperTest.JsonPropertyString child1;

        @JsonProperty
        public ObjectMapperTest.JsonPropertyInt child2;

        @Override
        public boolean equals(Object o) {
            if ((o == null) || (!(o instanceof ObjectMapperTest.NestedJsonProperty))) {
                return false;
            }
            return (Objects.equals(child1, ((ObjectMapperTest.NestedJsonProperty) (o)).child1)) && (Objects.equals(child2, ((ObjectMapperTest.NestedJsonProperty) (o)).child2));
        }
    }

    public static class JsonPropertyString {
        @JsonProperty
        public String testString;

        @Override
        public boolean equals(Object o) {
            if ((o == null) || (!(o instanceof ObjectMapperTest.JsonPropertyString))) {
                return false;
            }
            return Objects.equals(testString, ((ObjectMapperTest.JsonPropertyString) (o)).testString);
        }
    }

    public static class JsonPropertyInt {
        @JsonProperty
        public int i;

        @Override
        public boolean equals(Object o) {
            if ((o == null) || (!(o instanceof ObjectMapperTest.JsonPropertyInt))) {
                return false;
            }
            return Objects.equals(i, ((ObjectMapperTest.JsonPropertyInt) (o)).i);
        }
    }

    public static class JsonPropertyEnum {
        @JsonProperty
        public ObjectMapperTest.TestEnum enumValue;

        @Override
        public boolean equals(Object o) {
            if ((o == null) || (!(o instanceof ObjectMapperTest.JsonPropertyEnum))) {
                return false;
            }
            return Objects.equals(enumValue, ((ObjectMapperTest.JsonPropertyEnum) (o)).enumValue);
        }
    }

    public static class JsonPropertyStringList {
        @JsonProperty
        public List<String> stringList;

        @Override
        public boolean equals(Object o) {
            if ((o == null) || (!(o instanceof ObjectMapperTest.JsonPropertyStringList))) {
                return false;
            }
            ObjectMapperTest.JsonPropertyStringList rhs = ((ObjectMapperTest.JsonPropertyStringList) (o));
            if (((stringList) == null) || ((rhs.stringList) == null)) {
                return (stringList) == (rhs.stringList);
            }
            if ((stringList.size()) != (rhs.stringList.size())) {
                return false;
            }
            ListIterator<String> myIter = stringList.listIterator();
            ListIterator<String> rhsIter = rhs.stringList.listIterator();
            while (myIter.hasNext()) {
                if (!(Objects.equals(myIter.next(), rhsIter.next()))) {
                    return false;
                }
            } 
            return true;
        }
    }

    public enum TestEnum {

        VALUE_ONE("one"),
        VALUE_TWO("two"),
        VALUE_THREE("three");
        private final String mValue;

        private TestEnum(String str) {
            mValue = str;
        }

        @JsonValue
        public String getValue() {
            return mValue;
        }
    }

    private static class JsonPropertyMultitypedList {
        @JsonProperty
        public List<Object> multitypedList;
    }

    private static class JsonPropertyListOfLists {
        @JsonProperty
        public List<List<String>> listOfLists;
    }
}

