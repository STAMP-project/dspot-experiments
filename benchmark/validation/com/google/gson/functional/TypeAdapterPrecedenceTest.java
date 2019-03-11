/**
 * Copyright (C) 2011 Google Inc.
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
import junit.framework.TestCase;


public final class TypeAdapterPrecedenceTest extends TestCase {
    public void testNonstreamingFollowedByNonstreaming() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("serializer 1")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("serializer 2")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("deserializer 1")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("deserializer 2")).create();
        TestCase.assertEquals("\"foo via serializer 2\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via deserializer 2", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    public void testStreamingFollowedByStreaming() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newTypeAdapter("type adapter 1")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newTypeAdapter("type adapter 2")).create();
        TestCase.assertEquals("\"foo via type adapter 2\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via type adapter 2", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    public void testSerializeNonstreamingTypeAdapterFollowedByStreamingTypeAdapter() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("serializer")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("deserializer")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newTypeAdapter("type adapter")).create();
        TestCase.assertEquals("\"foo via type adapter\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via type adapter", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    public void testStreamingFollowedByNonstreaming() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newTypeAdapter("type adapter")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("serializer")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("deserializer")).create();
        TestCase.assertEquals("\"foo via serializer\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via deserializer", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    public void testStreamingHierarchicalFollowedByNonstreaming() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newTypeAdapter("type adapter")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("serializer")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("deserializer")).create();
        TestCase.assertEquals("\"foo via serializer\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via deserializer", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    public void testStreamingFollowedByNonstreamingHierarchical() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newTypeAdapter("type adapter")).registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("serializer")).registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("deserializer")).create();
        TestCase.assertEquals("\"foo via type adapter\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via type adapter", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    public void testStreamingHierarchicalFollowedByNonstreamingHierarchical() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("serializer")).registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("deserializer")).registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newTypeAdapter("type adapter")).create();
        TestCase.assertEquals("\"foo via type adapter\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via type adapter", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    public void testNonstreamingHierarchicalFollowedByNonstreaming() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("hierarchical")).registerTypeHierarchyAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("hierarchical")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newSerializer("non hierarchical")).registerTypeAdapter(TypeAdapterPrecedenceTest.Foo.class, newDeserializer("non hierarchical")).create();
        TestCase.assertEquals("\"foo via non hierarchical\"", gson.toJson(new TypeAdapterPrecedenceTest.Foo("foo")));
        TestCase.assertEquals("foo via non hierarchical", gson.fromJson("foo", TypeAdapterPrecedenceTest.Foo.class).name);
    }

    private static class Foo {
        final String name;

        private Foo(String name) {
            this.name = name;
        }
    }
}

