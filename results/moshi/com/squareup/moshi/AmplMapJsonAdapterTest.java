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


public final class AmplMapJsonAdapterTest {
    private final com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().build();

    @org.junit.Test
    public void map() throws java.lang.Exception {
        java.util.Map<java.lang.String, java.lang.Boolean> map = new java.util.LinkedHashMap<>();
        map.put("a", true);
        map.put("b", false);
        map.put("c", null);
        java.lang.String toJson = toJson(java.lang.String.class, java.lang.Boolean.class, map);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"a\":true,\"b\":false,\"c\":null}");
        java.util.Map<java.lang.String, java.lang.Boolean> fromJson = fromJson(java.lang.String.class, java.lang.Boolean.class, "{\"a\":true,\"b\":false,\"c\":null}");
        org.assertj.core.api.Assertions.assertThat(fromJson).containsExactly(org.assertj.core.data.MapEntry.entry("a", true), org.assertj.core.data.MapEntry.entry("b", false), org.assertj.core.data.MapEntry.entry("c", null));
    }

    @org.junit.Test
    public void mapWithNullKeyFailsToEmit() throws java.lang.Exception {
        java.util.Map<java.lang.String, java.lang.Boolean> map = new java.util.LinkedHashMap<>();
        map.put(null, true);
        try {
            toJson(java.lang.String.class, java.lang.Boolean.class, map);
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Map key is null at $.");
        }
    }

    @org.junit.Test
    public void emptyMap() throws java.lang.Exception {
        java.util.Map<java.lang.String, java.lang.Boolean> map = new java.util.LinkedHashMap<>();
        java.lang.String toJson = toJson(java.lang.String.class, java.lang.Boolean.class, map);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{}");
        java.util.Map<java.lang.String, java.lang.Boolean> fromJson = fromJson(java.lang.String.class, java.lang.Boolean.class, "{}");
        org.assertj.core.api.Assertions.assertThat(fromJson).isEmpty();
    }

    @org.junit.Test
    public void nullMap() throws java.lang.Exception {
        com.squareup.moshi.JsonAdapter<?> jsonAdapter = mapAdapter(java.lang.String.class, java.lang.Boolean.class);
        okio.Buffer buffer = new okio.Buffer();
        com.squareup.moshi.JsonWriter jsonWriter = com.squareup.moshi.JsonWriter.of(buffer);
        jsonWriter.setLenient(true);
        jsonAdapter.toJson(jsonWriter, null);
        org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo("null");
        com.squareup.moshi.JsonReader jsonReader = com.squareup.moshi.TestUtil.newReader("null");
        jsonReader.setLenient(true);
        org.assertj.core.api.Assertions.assertThat(jsonAdapter.fromJson(jsonReader)).isEqualTo(null);
    }

    @org.junit.Test
    public void orderIsRetained() throws java.lang.Exception {
        java.util.Map<java.lang.String, java.lang.Integer> map = new java.util.LinkedHashMap<>();
        map.put("c", 1);
        map.put("a", 2);
        map.put("d", 3);
        map.put("b", 4);
        java.lang.String toJson = toJson(java.lang.String.class, java.lang.Integer.class, map);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"c\":1,\"a\":2,\"d\":3,\"b\":4}");
        java.util.Map<java.lang.String, java.lang.Integer> fromJson = fromJson(java.lang.String.class, java.lang.Integer.class, "{\"c\":1,\"a\":2,\"d\":3,\"b\":4}");
        org.assertj.core.api.Assertions.assertThat(new java.util.ArrayList<java.lang.Object>(fromJson.keySet())).isEqualTo(java.util.Arrays.asList("c", "a", "d", "b"));
    }

    @org.junit.Test
    public void duplicatesAreForbidden() throws java.lang.Exception {
        try {
            fromJson(java.lang.String.class, java.lang.Integer.class, "{\"c\":1,\"c\":2}");
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Map key 'c' has multiple values at path $.c: 1 and 2");
        }
    }

    /**
     * * This leans on {@code promoteNameToValue} to do the heavy lifting.
     */
    @org.junit.Test
    public void mapWithNonStringKeys() throws java.lang.Exception {
        java.util.Map<java.lang.Integer, java.lang.Boolean> map = new java.util.LinkedHashMap<>();
        map.put(5, true);
        map.put(6, false);
        map.put(7, null);
        java.lang.String toJson = toJson(java.lang.Integer.class, java.lang.Boolean.class, map);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"5\":true,\"6\":false,\"7\":null}");
        java.util.Map<java.lang.String, java.lang.Boolean> fromJson = fromJson(java.lang.Integer.class, java.lang.Boolean.class, "{\"5\":true,\"6\":false,\"7\":null}");
        org.assertj.core.api.Assertions.assertThat(fromJson).containsExactly(org.assertj.core.data.MapEntry.entry(5, true), org.assertj.core.data.MapEntry.entry(6, false), org.assertj.core.data.MapEntry.entry(7, null));
    }

    @org.junit.Test
    public void mapWithNonStringKeysToJsonObject() throws java.lang.Exception {
        java.util.Map<java.lang.Integer, java.lang.Boolean> map = new java.util.LinkedHashMap<>();
        map.put(5, true);
        map.put(6, false);
        map.put(7, null);
        java.util.Map<java.lang.String, java.lang.Boolean> jsonObject = new java.util.LinkedHashMap<>();
        jsonObject.put("5", true);
        jsonObject.put("6", false);
        jsonObject.put("7", null);
        com.squareup.moshi.JsonAdapter<java.util.Map<java.lang.Integer, java.lang.Boolean>> jsonAdapter = mapAdapter(java.lang.Integer.class, java.lang.Boolean.class);
        org.assertj.core.api.Assertions.assertThat(jsonAdapter.serializeNulls().toJsonValue(map)).isEqualTo(jsonObject);
        org.assertj.core.api.Assertions.assertThat(jsonAdapter.fromJsonValue(jsonObject)).isEqualTo(map);
    }

    private <K, V> java.lang.String toJson(java.lang.reflect.Type keyType, java.lang.reflect.Type valueType, java.util.Map<K, V> value) throws java.io.IOException {
        com.squareup.moshi.JsonAdapter<java.util.Map<K, V>> jsonAdapter = mapAdapter(keyType, valueType);
        okio.Buffer buffer = new okio.Buffer();
        com.squareup.moshi.JsonWriter jsonWriter = com.squareup.moshi.JsonWriter.of(buffer);
        jsonWriter.setSerializeNulls(true);
        jsonAdapter.toJson(jsonWriter, value);
        return buffer.readUtf8();
    }

    // It's the caller's responsibility to make sure K and V match.
    @java.lang.SuppressWarnings(value = "unchecked")
    private <K, V> com.squareup.moshi.JsonAdapter<java.util.Map<K, V>> mapAdapter(java.lang.reflect.Type keyType, java.lang.reflect.Type valueType) {
        return ((com.squareup.moshi.JsonAdapter<java.util.Map<K, V>>) (com.squareup.moshi.MapJsonAdapter.FACTORY.create(com.squareup.moshi.Types.newParameterizedType(java.util.Map.class, keyType, valueType), com.squareup.moshi.Util.NO_ANNOTATIONS, moshi)));
    }

    private <K, V> java.util.Map<K, V> fromJson(java.lang.reflect.Type keyType, java.lang.reflect.Type valueType, java.lang.String json) throws java.io.IOException {
        com.squareup.moshi.JsonAdapter<java.util.Map<K, V>> mapJsonAdapter = mapAdapter(keyType, valueType);
        return mapJsonAdapter.fromJson(json);
    }
}

