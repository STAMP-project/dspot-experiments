/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.common.TestTypes;
import com.google.gson.internal..Gson.Types;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import junit.framework.TestCase;


/**
 * Functional test for Json serialization and deserialization for Maps
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class MapTest extends TestCase {
    private Gson gson;

    public void testMapSerialization() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 2);
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        String json = gson.toJson(map, typeOfMap);
        TestCase.assertTrue(json.contains("\"a\":1"));
        TestCase.assertTrue(json.contains("\"b\":2"));
    }

    public void testMapDeserialization() {
        String json = "{\"a\":1,\"b\":2}";
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> target = gson.fromJson(json, typeOfMap);
        TestCase.assertEquals(1, target.get("a").intValue());
        TestCase.assertEquals(2, target.get("b").intValue());
    }

    public void testMapSerializationEmpty() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        String json = gson.toJson(map, typeOfMap);
        TestCase.assertEquals("{}", json);
    }

    public void testMapDeserializationEmpty() {
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> map = gson.fromJson("{}", typeOfMap);
        TestCase.assertTrue(map.isEmpty());
    }

    public void testMapSerializationWithNullValue() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("abc", null);
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        String json = gson.toJson(map, typeOfMap);
        // Maps are represented as JSON objects, so ignoring null field
        TestCase.assertEquals("{}", json);
    }

    public void testMapDeserializationWithNullValue() {
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> map = gson.fromJson("{\"abc\":null}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertNull(map.get("abc"));
    }

    public void testMapSerializationWithNullValueButSerializeNulls() {
        gson = new GsonBuilder().serializeNulls().create();
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("abc", null);
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        String json = gson.toJson(map, typeOfMap);
        TestCase.assertEquals("{\"abc\":null}", json);
    }

    public void testMapSerializationWithNullKey() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put(null, 123);
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        String json = gson.toJson(map, typeOfMap);
        TestCase.assertEquals("{\"null\":123}", json);
    }

    public void testMapDeserializationWithNullKey() {
        Type typeOfMap = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> map = gson.fromJson("{\"null\":123}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertEquals(123, map.get("null").intValue());
        TestCase.assertNull(map.get(null));
        map = gson.fromJson("{null:123}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertEquals(123, map.get("null").intValue());
        TestCase.assertNull(map.get(null));
    }

    public void testMapSerializationWithIntegerKeys() {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        map.put(123, "456");
        Type typeOfMap = new TypeToken<Map<Integer, String>>() {}.getType();
        String json = gson.toJson(map, typeOfMap);
        TestCase.assertEquals("{\"123\":\"456\"}", json);
    }

    public void testMapDeserializationWithIntegerKeys() {
        Type typeOfMap = new TypeToken<Map<Integer, String>>() {}.getType();
        Map<Integer, String> map = gson.fromJson("{\"123\":\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
    }

    public void testMapDeserializationWithUnquotedIntegerKeys() {
        Type typeOfMap = new TypeToken<Map<Integer, String>>() {}.getType();
        Map<Integer, String> map = gson.fromJson("{123:\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
    }

    public void testMapDeserializationWithLongKeys() {
        long longValue = 9876543210L;
        String json = String.format("{\"%d\":\"456\"}", longValue);
        Type typeOfMap = new TypeToken<Map<Long, String>>() {}.getType();
        Map<Long, String> map = gson.fromJson(json, typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(longValue));
        TestCase.assertEquals("456", map.get(longValue));
    }

    public void testMapDeserializationWithUnquotedLongKeys() {
        long longKey = 9876543210L;
        String json = String.format("{%d:\"456\"}", longKey);
        Type typeOfMap = new TypeToken<Map<Long, String>>() {}.getType();
        Map<Long, String> map = gson.fromJson(json, typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(longKey));
        TestCase.assertEquals("456", map.get(longKey));
    }

    public void testHashMapDeserialization() throws Exception {
        Type typeOfMap = new TypeToken<HashMap<Integer, String>>() {}.getType();
        HashMap<Integer, String> map = gson.fromJson("{\"123\":\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
    }

    public void testSortedMap() throws Exception {
        Type typeOfMap = new TypeToken<SortedMap<Integer, String>>() {}.getType();
        SortedMap<Integer, String> map = gson.fromJson("{\"123\":\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
    }

    public void testConcurrentMap() throws Exception {
        Type typeOfMap = new TypeToken<ConcurrentMap<Integer, String>>() {}.getType();
        ConcurrentMap<Integer, String> map = gson.fromJson("{\"123\":\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
        String json = gson.toJson(map);
        TestCase.assertEquals("{\"123\":\"456\"}", json);
    }

    public void testConcurrentHashMap() throws Exception {
        Type typeOfMap = new TypeToken<ConcurrentHashMap<Integer, String>>() {}.getType();
        ConcurrentHashMap<Integer, String> map = gson.fromJson("{\"123\":\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
        String json = gson.toJson(map);
        TestCase.assertEquals("{\"123\":\"456\"}", json);
    }

    public void testConcurrentNavigableMap() throws Exception {
        Type typeOfMap = new TypeToken<ConcurrentNavigableMap<Integer, String>>() {}.getType();
        ConcurrentNavigableMap<Integer, String> map = gson.fromJson("{\"123\":\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
        String json = gson.toJson(map);
        TestCase.assertEquals("{\"123\":\"456\"}", json);
    }

    public void testConcurrentSkipListMap() throws Exception {
        Type typeOfMap = new TypeToken<ConcurrentSkipListMap<Integer, String>>() {}.getType();
        ConcurrentSkipListMap<Integer, String> map = gson.fromJson("{\"123\":\"456\"}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertTrue(map.containsKey(123));
        TestCase.assertEquals("456", map.get(123));
        String json = gson.toJson(map);
        TestCase.assertEquals("{\"123\":\"456\"}", json);
    }

    public void testParameterizedMapSubclassSerialization() {
        MapTest.MyParameterizedMap<String, String> map = new MapTest.MyParameterizedMap<String, String>(10);
        map.put("a", "b");
        Type type = new TypeToken<MapTest.MyParameterizedMap<String, String>>() {}.getType();
        String json = gson.toJson(map, type);
        TestCase.assertTrue(json.contains("\"a\":\"b\""));
    }

    @SuppressWarnings({ "unused", "serial" })
    private static class MyParameterizedMap<K, V> extends LinkedHashMap<K, V> {
        final int foo;

        MyParameterizedMap(int foo) {
            this.foo = foo;
        }
    }

    public void testMapSubclassSerialization() {
        MapTest.MyMap map = new MapTest.MyMap();
        map.put("a", "b");
        String json = gson.toJson(map, MapTest.MyMap.class);
        TestCase.assertTrue(json.contains("\"a\":\"b\""));
    }

    public void testMapStandardSubclassDeserialization() {
        String json = "{a:'1',b:'2'}";
        Type type = new TypeToken<LinkedHashMap<String, String>>() {}.getType();
        LinkedHashMap<String, Integer> map = gson.fromJson(json, type);
        TestCase.assertEquals("1", map.get("a"));
        TestCase.assertEquals("2", map.get("b"));
    }

    public void testMapSubclassDeserialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(MapTest.MyMap.class, new InstanceCreator<MapTest.MyMap>() {
            public MapTest.MyMap createInstance(Type type) {
                return new MapTest.MyMap();
            }
        }).create();
        String json = "{\"a\":1,\"b\":2}";
        MapTest.MyMap map = gson.fromJson(json, MapTest.MyMap.class);
        TestCase.assertEquals("1", map.get("a"));
        TestCase.assertEquals("2", map.get("b"));
    }

    public void testCustomSerializerForSpecificMapType() {
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, Map.class, String.class, Long.class);
        Gson gson = new GsonBuilder().registerTypeAdapter(type, new JsonSerializer<Map<String, Long>>() {
            public JsonElement serialize(Map<String, Long> src, Type typeOfSrc, JsonSerializationContext context) {
                JsonArray array = new JsonArray();
                for (long value : src.values()) {
                    array.add(new JsonPrimitive(value));
                }
                return array;
            }
        }).create();
        Map<String, Long> src = new LinkedHashMap<String, Long>();
        src.put("one", 1L);
        src.put("two", 2L);
        src.put("three", 3L);
        TestCase.assertEquals("[1,2,3]", gson.toJson(src, type));
    }

    /**
     * Created in response to http://code.google.com/p/google-gson/issues/detail?id=99
     */
    private static class ClassWithAMap {
        Map<String, String> map = new TreeMap<String, String>();
    }

    /**
     * Created in response to http://code.google.com/p/google-gson/issues/detail?id=99
     */
    public void testMapSerializationWithNullValues() {
        MapTest.ClassWithAMap target = new MapTest.ClassWithAMap();
        target.map.put("name1", null);
        target.map.put("name2", "value2");
        String json = gson.toJson(target);
        TestCase.assertFalse(json.contains("name1"));
        TestCase.assertTrue(json.contains("name2"));
    }

    /**
     * Created in response to http://code.google.com/p/google-gson/issues/detail?id=99
     */
    public void testMapSerializationWithNullValuesSerialized() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        MapTest.ClassWithAMap target = new MapTest.ClassWithAMap();
        target.map.put("name1", null);
        target.map.put("name2", "value2");
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("name1"));
        TestCase.assertTrue(json.contains("name2"));
    }

    public void testMapSerializationWithWildcardValues() {
        Map<String, ? extends Collection<? extends Integer>> map = new LinkedHashMap<String, Collection<Integer>>();
        map.put("test", null);
        Type typeOfMap = new TypeToken<Map<String, ? extends Collection<? extends Integer>>>() {}.getType();
        String json = gson.toJson(map, typeOfMap);
        TestCase.assertEquals("{}", json);
    }

    public void testMapDeserializationWithWildcardValues() {
        Type typeOfMap = new TypeToken<Map<String, ? extends Long>>() {}.getType();
        Map<String, ? extends Long> map = gson.fromJson("{\"test\":123}", typeOfMap);
        TestCase.assertEquals(1, map.size());
        TestCase.assertEquals(new Long(123L), map.get("test"));
    }

    private static class MyMap extends LinkedHashMap<String, String> {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unused")
        int foo = 10;
    }

    /**
     * From bug report http://code.google.com/p/google-gson/issues/detail?id=95
     */
    public void testMapOfMapSerialization() {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        Map<String, String> nestedMap = new HashMap<String, String>();
        nestedMap.put("1", "1");
        nestedMap.put("2", "2");
        map.put("nestedMap", nestedMap);
        String json = gson.toJson(map);
        TestCase.assertTrue(json.contains("nestedMap"));
        TestCase.assertTrue(json.contains("\"1\":\"1\""));
        TestCase.assertTrue(json.contains("\"2\":\"2\""));
    }

    /**
     * From bug report http://code.google.com/p/google-gson/issues/detail?id=95
     */
    public void testMapOfMapDeserialization() {
        String json = "{nestedMap:{'2':'2','1':'1'}}";
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        Map<String, Map<String, String>> map = gson.fromJson(json, type);
        Map<String, String> nested = map.get("nestedMap");
        TestCase.assertEquals("1", nested.get("1"));
        TestCase.assertEquals("2", nested.get("2"));
    }

    /**
     * From bug report http://code.google.com/p/google-gson/issues/detail?id=178
     */
    public void testMapWithQuotes() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("a\"b", "c\"d");
        String json = gson.toJson(map);
        TestCase.assertEquals("{\"a\\\"b\":\"c\\\"d\"}", json);
    }

    /**
     * From issue 227.
     */
    public void testWriteMapsWithEmptyStringKey() {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("", true);
        TestCase.assertEquals("{\"\":true}", gson.toJson(map));
    }

    public void testReadMapsWithEmptyStringKey() {
        Map<String, Boolean> map = gson.fromJson("{\"\":true}", new TypeToken<Map<String, Boolean>>() {}.getType());
        TestCase.assertEquals(Boolean.TRUE, map.get(""));
    }

    /**
     * From bug report http://code.google.com/p/google-gson/issues/detail?id=204
     */
    public void testSerializeMaps() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("a", 12);
        map.put("b", null);
        LinkedHashMap<String, Object> innerMap = new LinkedHashMap<String, Object>();
        innerMap.put("test", 1);
        innerMap.put("TestStringArray", new String[]{ "one", "two" });
        map.put("c", innerMap);
        TestCase.assertEquals("{\"a\":12,\"b\":null,\"c\":{\"test\":1,\"TestStringArray\":[\"one\",\"two\"]}}", new GsonBuilder().serializeNulls().create().toJson(map));
        TestCase.assertEquals(("{\n  \"a\": 12,\n  \"b\": null,\n  \"c\": " + ("{\n    \"test\": 1,\n    \"TestStringArray\": " + "[\n      \"one\",\n      \"two\"\n    ]\n  }\n}")), new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(map));
        TestCase.assertEquals("{\"a\":12,\"c\":{\"test\":1,\"TestStringArray\":[\"one\",\"two\"]}}", new GsonBuilder().create().toJson(map));
        TestCase.assertEquals(("{\n  \"a\": 12,\n  \"c\": " + ("{\n    \"test\": 1,\n    \"TestStringArray\": " + "[\n      \"one\",\n      \"two\"\n    ]\n  }\n}")), new GsonBuilder().setPrettyPrinting().create().toJson(map));
        innerMap.put("d", "e");
        TestCase.assertEquals("{\"a\":12,\"c\":{\"test\":1,\"TestStringArray\":[\"one\",\"two\"],\"d\":\"e\"}}", new Gson().toJson(map));
    }

    public final void testInterfaceTypeMap() {
        MapTest.MapClass element = new MapTest.MapClass();
        TestTypes.Sub subType = new TestTypes.Sub();
        element.addBase("Test", subType);
        element.addSub("Test", subType);
        String subTypeJson = new Gson().toJson(subType);
        String expected = (((("{\"bases\":{\"Test\":" + subTypeJson) + "},") + "\"subs\":{\"Test\":") + subTypeJson) + "}}";
        Gson gsonWithComplexKeys = new GsonBuilder().enableComplexMapKeySerialization().create();
        String json = gsonWithComplexKeys.toJson(element);
        TestCase.assertEquals(expected, json);
        Gson gson = new Gson();
        json = gson.toJson(element);
        TestCase.assertEquals(expected, json);
    }

    public final void testInterfaceTypeMapWithSerializer() {
        MapTest.MapClass element = new MapTest.MapClass();
        TestTypes.Sub subType = new TestTypes.Sub();
        element.addBase("Test", subType);
        element.addSub("Test", subType);
        Gson tempGson = new Gson();
        String subTypeJson = tempGson.toJson(subType);
        final JsonElement baseTypeJsonElement = tempGson.toJsonTree(subType, TestTypes.Base.class);
        String baseTypeJson = tempGson.toJson(baseTypeJsonElement);
        String expected = (((("{\"bases\":{\"Test\":" + baseTypeJson) + "},") + "\"subs\":{\"Test\":") + subTypeJson) + "}}";
        JsonSerializer<TestTypes.Base> baseTypeAdapter = new JsonSerializer<TestTypes.Base>() {
            public JsonElement serialize(TestTypes.Base src, Type typeOfSrc, JsonSerializationContext context) {
                return baseTypeJsonElement;
            }
        };
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().registerTypeAdapter(TestTypes.Base.class, baseTypeAdapter).create();
        String json = gson.toJson(element);
        TestCase.assertEquals(expected, json);
        gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, baseTypeAdapter).create();
        json = gson.toJson(element);
        TestCase.assertEquals(expected, json);
    }

    public void testGeneralMapField() throws Exception {
        MapTest.MapWithGeneralMapParameters map = new MapTest.MapWithGeneralMapParameters();
        map.map.put("string", "testString");
        map.map.put("stringArray", new String[]{ "one", "two" });
        map.map.put("objectArray", new Object[]{ 1, 2L, "three" });
        String expected = "{\"map\":{\"string\":\"testString\",\"stringArray\":" + "[\"one\",\"two\"],\"objectArray\":[1,2,\"three\"]}}";
        TestCase.assertEquals(expected, gson.toJson(map));
        gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        TestCase.assertEquals(expected, gson.toJson(map));
    }

    public void testComplexKeysSerialization() {
        Map<MapTest.Point, String> map = new LinkedHashMap<MapTest.Point, String>();
        map.put(new MapTest.Point(2, 3), "a");
        map.put(new MapTest.Point(5, 7), "b");
        String json = "{\"2,3\":\"a\",\"5,7\":\"b\"}";
        TestCase.assertEquals(json, gson.toJson(map, new TypeToken<Map<MapTest.Point, String>>() {}.getType()));
        TestCase.assertEquals(json, gson.toJson(map, Map.class));
    }

    public void testComplexKeysDeserialization() {
        String json = "{'2,3':'a','5,7':'b'}";
        try {
            gson.fromJson(json, new TypeToken<Map<MapTest.Point, String>>() {}.getType());
            TestCase.fail();
        } catch (JsonParseException expected) {
        }
    }

    public void testStringKeyDeserialization() {
        String json = "{'2,3':'a','5,7':'b'}";
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("2,3", "a");
        map.put("5,7", "b");
        TestCase.assertEquals(map, gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType()));
    }

    public void testNumberKeyDeserialization() {
        String json = "{'2.3':'a','5.7':'b'}";
        Map<Double, String> map = new LinkedHashMap<Double, String>();
        map.put(2.3, "a");
        map.put(5.7, "b");
        TestCase.assertEquals(map, gson.fromJson(json, new TypeToken<Map<Double, String>>() {}.getType()));
    }

    public void testBooleanKeyDeserialization() {
        String json = "{'true':'a','false':'b'}";
        Map<Boolean, String> map = new LinkedHashMap<Boolean, String>();
        map.put(true, "a");
        map.put(false, "b");
        TestCase.assertEquals(map, gson.fromJson(json, new TypeToken<Map<Boolean, String>>() {}.getType()));
    }

    public void testMapDeserializationWithDuplicateKeys() {
        try {
            gson.fromJson("{'a':1,'a':2}", new TypeToken<Map<String, Integer>>() {}.getType());
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
        }
    }

    public void testSerializeMapOfMaps() {
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        Map<String, Map<String, String>> map = newMap("a", newMap("ka1", "va1", "ka2", "va2"), "b", newMap("kb1", "vb1", "kb2", "vb2"));
        TestCase.assertEquals("{'a':{'ka1':'va1','ka2':'va2'},'b':{'kb1':'vb1','kb2':'vb2'}}", gson.toJson(map, type).replace('"', '\''));
    }

    public void testDeerializeMapOfMaps() {
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        Map<String, Map<String, String>> map = newMap("a", newMap("ka1", "va1", "ka2", "va2"), "b", newMap("kb1", "vb1", "kb2", "vb2"));
        String json = "{'a':{'ka1':'va1','ka2':'va2'},'b':{'kb1':'vb1','kb2':'vb2'}}";
        TestCase.assertEquals(map, gson.fromJson(json, type));
    }

    public void testMapNamePromotionWithJsonElementReader() {
        String json = "{'2.3':'a'}";
        Map<Double, String> map = new LinkedHashMap<Double, String>();
        map.put(2.3, "a");
        JsonElement tree = new JsonParser().parse(json);
        TestCase.assertEquals(map, gson.fromJson(tree, new TypeToken<Map<Double, String>>() {}.getType()));
    }

    static class Point {
        private final int x;

        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            return ((o instanceof MapTest.Point) && ((x) == (((MapTest.Point) (o)).x))) && ((y) == (((MapTest.Point) (o)).y));
        }

        @Override
        public int hashCode() {
            return ((x) * 37) + (y);
        }

        @Override
        public String toString() {
            return ((x) + ",") + (y);
        }
    }

    static final class MapClass {
        private final Map<String, TestTypes.Base> bases = new HashMap<String, TestTypes.Base>();

        private final Map<String, TestTypes.Sub> subs = new HashMap<String, TestTypes.Sub>();

        public final void addBase(String name, TestTypes.Base value) {
            bases.put(name, value);
        }

        public final void addSub(String name, TestTypes.Sub value) {
            subs.put(name, value);
        }
    }

    static final class MapWithGeneralMapParameters {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        final Map<String, Object> map = new LinkedHashMap();
    }
}

