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
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;


public class MapAsArrayTypeAdapterTest extends TestCase {
    public void testSerializeComplexMapWithTypeAdapter() {
        Type type = new TypeToken<Map<MapAsArrayTypeAdapterTest.Point, String>>() {}.getType();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<MapAsArrayTypeAdapterTest.Point, String> original = new LinkedHashMap<MapAsArrayTypeAdapterTest.Point, String>();
        original.put(new MapAsArrayTypeAdapterTest.Point(5, 5), "a");
        original.put(new MapAsArrayTypeAdapterTest.Point(8, 8), "b");
        String json = gson.toJson(original, type);
        TestCase.assertEquals("[[{\"x\":5,\"y\":5},\"a\"],[{\"x\":8,\"y\":8},\"b\"]]", json);
        TestCase.assertEquals(original, gson.<Map<MapAsArrayTypeAdapterTest.Point, String>>fromJson(json, type));
        // test that registering a type adapter for one map doesn't interfere with others
        Map<String, Boolean> otherMap = new LinkedHashMap<String, Boolean>();
        otherMap.put("t", true);
        otherMap.put("f", false);
        TestCase.assertEquals("{\"t\":true,\"f\":false}", gson.toJson(otherMap, Map.class));
        TestCase.assertEquals("{\"t\":true,\"f\":false}", gson.toJson(otherMap, new TypeToken<Map<String, Boolean>>() {}.getType()));
        TestCase.assertEquals(otherMap, gson.<Object>fromJson("{\"t\":true,\"f\":false}", new TypeToken<Map<String, Boolean>>() {}.getType()));
    }

    public void testTwoTypesCollapseToOneDeserialize() {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String s = "[[\"1.00\",\"a\"],[\"1.0\",\"b\"]]";
        try {
            gson.fromJson(s, new TypeToken<Map<Double, String>>() {}.getType());
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
        }
    }

    public void testMultipleEnableComplexKeyRegistrationHasNoEffect() throws Exception {
        Type type = new TypeToken<Map<MapAsArrayTypeAdapterTest.Point, String>>() {}.getType();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().enableComplexMapKeySerialization().create();
        Map<MapAsArrayTypeAdapterTest.Point, String> original = new LinkedHashMap<MapAsArrayTypeAdapterTest.Point, String>();
        original.put(new MapAsArrayTypeAdapterTest.Point(6, 5), "abc");
        original.put(new MapAsArrayTypeAdapterTest.Point(1, 8), "def");
        String json = gson.toJson(original, type);
        TestCase.assertEquals("[[{\"x\":6,\"y\":5},\"abc\"],[{\"x\":1,\"y\":8},\"def\"]]", json);
        TestCase.assertEquals(original, gson.<Map<MapAsArrayTypeAdapterTest.Point, String>>fromJson(json, type));
    }

    public void testMapWithTypeVariableSerialization() {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        MapAsArrayTypeAdapterTest.PointWithProperty<MapAsArrayTypeAdapterTest.Point> map = new MapAsArrayTypeAdapterTest.PointWithProperty<MapAsArrayTypeAdapterTest.Point>();
        map.map.put(new MapAsArrayTypeAdapterTest.Point(2, 3), new MapAsArrayTypeAdapterTest.Point(4, 5));
        Type type = new TypeToken<MapAsArrayTypeAdapterTest.PointWithProperty<MapAsArrayTypeAdapterTest.Point>>() {}.getType();
        String json = gson.toJson(map, type);
        TestCase.assertEquals("{\"map\":[[{\"x\":2,\"y\":3},{\"x\":4,\"y\":5}]]}", json);
    }

    public void testMapWithTypeVariableDeserialization() {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String json = "{map:[[{x:2,y:3},{x:4,y:5}]]}";
        Type type = new TypeToken<MapAsArrayTypeAdapterTest.PointWithProperty<MapAsArrayTypeAdapterTest.Point>>() {}.getType();
        MapAsArrayTypeAdapterTest.PointWithProperty<MapAsArrayTypeAdapterTest.Point> map = gson.fromJson(json, type);
        MapAsArrayTypeAdapterTest.Point key = map.map.keySet().iterator().next();
        MapAsArrayTypeAdapterTest.Point value = map.map.values().iterator().next();
        TestCase.assertEquals(new MapAsArrayTypeAdapterTest.Point(2, 3), key);
        TestCase.assertEquals(new MapAsArrayTypeAdapterTest.Point(4, 5), value);
    }

    static class Point {
        int x;

        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Point() {
        }

        @Override
        public boolean equals(Object o) {
            return ((o instanceof MapAsArrayTypeAdapterTest.Point) && ((((MapAsArrayTypeAdapterTest.Point) (o)).x) == (x))) && ((((MapAsArrayTypeAdapterTest.Point) (o)).y) == (y));
        }

        @Override
        public int hashCode() {
            return ((x) * 37) + (y);
        }

        @Override
        public String toString() {
            return ((("(" + (x)) + ",") + (y)) + ")";
        }
    }

    static class PointWithProperty<T> {
        Map<MapAsArrayTypeAdapterTest.Point, T> map = new HashMap<MapAsArrayTypeAdapterTest.Point, T>();
    }
}

