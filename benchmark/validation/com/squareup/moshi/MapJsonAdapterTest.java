/**
 * Copyright (C) 2015 Square, Inc.
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


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Test;


public final class MapJsonAdapterTest {
    private final Moshi moshi = new Moshi.Builder().build();

    @Test
    public void map() throws Exception {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("a", true);
        map.put("b", false);
        map.put("c", null);
        String toJson = toJson(String.class, Boolean.class, map);
        assertThat(toJson).isEqualTo("{\"a\":true,\"b\":false,\"c\":null}");
        Map<String, Boolean> fromJson = fromJson(String.class, Boolean.class, "{\"a\":true,\"b\":false,\"c\":null}");
        assertThat(fromJson).containsExactly(new AbstractMap.SimpleEntry<String, Boolean>("a", true), new AbstractMap.SimpleEntry<String, Boolean>("b", false), new AbstractMap.SimpleEntry<String, Boolean>("c", null));
    }

    @Test
    public void mapWithNullKeyFailsToEmit() throws Exception {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put(null, true);
        try {
            toJson(String.class, Boolean.class, map);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Map key is null at $.");
        }
    }

    @Test
    public void emptyMap() throws Exception {
        Map<String, Boolean> map = new LinkedHashMap<>();
        String toJson = toJson(String.class, Boolean.class, map);
        assertThat(toJson).isEqualTo("{}");
        Map<String, Boolean> fromJson = fromJson(String.class, Boolean.class, "{}");
        assertThat(fromJson).isEmpty();
    }

    @Test
    public void nullMap() throws Exception {
        JsonAdapter<?> jsonAdapter = mapAdapter(String.class, Boolean.class);
        Buffer buffer = new Buffer();
        JsonWriter jsonWriter = JsonWriter.of(buffer);
        jsonWriter.setLenient(true);
        jsonAdapter.toJson(jsonWriter, null);
        assertThat(buffer.readUtf8()).isEqualTo("null");
        JsonReader jsonReader = TestUtil.newReader("null");
        jsonReader.setLenient(true);
        assertThat(jsonAdapter.fromJson(jsonReader)).isEqualTo(null);
    }

    @Test
    public void orderIsRetained() throws Exception {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("c", 1);
        map.put("a", 2);
        map.put("d", 3);
        map.put("b", 4);
        String toJson = toJson(String.class, Integer.class, map);
        assertThat(toJson).isEqualTo("{\"c\":1,\"a\":2,\"d\":3,\"b\":4}");
        Map<String, Integer> fromJson = fromJson(String.class, Integer.class, "{\"c\":1,\"a\":2,\"d\":3,\"b\":4}");
        assertThat(new ArrayList<Object>(fromJson.keySet())).isEqualTo(Arrays.asList("c", "a", "d", "b"));
    }

    @Test
    public void duplicatesAreForbidden() throws Exception {
        try {
            fromJson(String.class, Integer.class, "{\"c\":1,\"c\":2}");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Map key 'c' has multiple values at path $.c: 1 and 2");
        }
    }

    /**
     * This leans on {@code promoteNameToValue} to do the heavy lifting.
     */
    @Test
    public void mapWithNonStringKeys() throws Exception {
        Map<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(5, true);
        map.put(6, false);
        map.put(7, null);
        String toJson = toJson(Integer.class, Boolean.class, map);
        assertThat(toJson).isEqualTo("{\"5\":true,\"6\":false,\"7\":null}");
        Map<Integer, Boolean> fromJson = fromJson(Integer.class, Boolean.class, "{\"5\":true,\"6\":false,\"7\":null}");
        assertThat(fromJson).containsExactly(new AbstractMap.SimpleEntry<Integer, Boolean>(5, true), new AbstractMap.SimpleEntry<Integer, Boolean>(6, false), new AbstractMap.SimpleEntry<Integer, Boolean>(7, null));
    }

    @Test
    public void mapWithNonStringKeysToJsonObject() {
        Map<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(5, true);
        map.put(6, false);
        map.put(7, null);
        Map<String, Boolean> jsonObject = new LinkedHashMap<>();
        jsonObject.put("5", true);
        jsonObject.put("6", false);
        jsonObject.put("7", null);
        JsonAdapter<Map<Integer, Boolean>> jsonAdapter = mapAdapter(Integer.class, Boolean.class);
        assertThat(jsonAdapter.serializeNulls().toJsonValue(map)).isEqualTo(jsonObject);
        assertThat(jsonAdapter.fromJsonValue(jsonObject)).isEqualTo(map);
    }

    @Test
    public void booleanKeyTypeHasCoherentErrorMessage() {
        Map<Boolean, String> map = new LinkedHashMap<>();
        map.put(true, "");
        JsonAdapter<Map<Boolean, String>> adapter = mapAdapter(Boolean.class, String.class);
        try {
            adapter.toJson(map);
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Boolean cannot be used as a map key in JSON at path $.");
        }
        try {
            adapter.toJsonValue(map);
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Boolean cannot be used as a map key in JSON at path $.");
        }
    }

    static final class Key {}

    @Test
    public void objectKeyTypeHasCoherentErrorMessage() {
        Map<MapJsonAdapterTest.Key, String> map = new LinkedHashMap<>();
        map.put(new MapJsonAdapterTest.Key(), "");
        JsonAdapter<Map<MapJsonAdapterTest.Key, String>> adapter = mapAdapter(MapJsonAdapterTest.Key.class, String.class);
        try {
            adapter.toJson(map);
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Object cannot be used as a map key in JSON at path $.");
        }
        try {
            adapter.toJsonValue(map);
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage(("Object cannot be " + "used as a map key in JSON at path $."));
        }
    }

    @Test
    public void arrayKeyTypeHasCoherentErrorMessage() {
        Map<String[], String> map = new LinkedHashMap<>();
        map.put(new String[0], "");
        JsonAdapter<Map<String[], String>> adapter = mapAdapter(Types.arrayOf(String.class), String.class);
        try {
            adapter.toJson(map);
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Array cannot be used as a map key in JSON at path $.");
        }
        try {
            adapter.toJsonValue(map);
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Array cannot be used as a map key in JSON at path $.");
        }
    }
}

