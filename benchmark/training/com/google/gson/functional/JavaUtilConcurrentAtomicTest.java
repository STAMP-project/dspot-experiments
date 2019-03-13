/**
 * Copyright (C) 2015 Google Inc.
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
import com.google.gson.LongSerializationPolicy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import junit.framework.TestCase;


/**
 * Functional test for Json serialization and deserialization for classes in java.util.concurrent.atomic
 */
public class JavaUtilConcurrentAtomicTest extends TestCase {
    private Gson gson;

    public void testAtomicBoolean() throws Exception {
        AtomicBoolean target = gson.fromJson("true", AtomicBoolean.class);
        TestCase.assertTrue(target.get());
        String json = gson.toJson(target);
        TestCase.assertEquals("true", json);
    }

    public void testAtomicInteger() throws Exception {
        AtomicInteger target = gson.fromJson("10", AtomicInteger.class);
        TestCase.assertEquals(10, target.get());
        String json = gson.toJson(target);
        TestCase.assertEquals("10", json);
    }

    public void testAtomicLong() throws Exception {
        AtomicLong target = gson.fromJson("10", AtomicLong.class);
        TestCase.assertEquals(10, target.get());
        String json = gson.toJson(target);
        TestCase.assertEquals("10", json);
    }

    public void testAtomicLongWithStringSerializationPolicy() throws Exception {
        Gson gson = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
        JavaUtilConcurrentAtomicTest.AtomicLongHolder target = gson.fromJson("{'value':'10'}", JavaUtilConcurrentAtomicTest.AtomicLongHolder.class);
        TestCase.assertEquals(10, target.value.get());
        String json = gson.toJson(target);
        TestCase.assertEquals("{\"value\":\"10\"}", json);
    }

    public void testAtomicIntegerArray() throws Exception {
        AtomicIntegerArray target = gson.fromJson("[10, 13, 14]", AtomicIntegerArray.class);
        TestCase.assertEquals(3, target.length());
        TestCase.assertEquals(10, target.get(0));
        TestCase.assertEquals(13, target.get(1));
        TestCase.assertEquals(14, target.get(2));
        String json = gson.toJson(target);
        TestCase.assertEquals("[10,13,14]", json);
    }

    public void testAtomicLongArray() throws Exception {
        AtomicLongArray target = gson.fromJson("[10, 13, 14]", AtomicLongArray.class);
        TestCase.assertEquals(3, target.length());
        TestCase.assertEquals(10, target.get(0));
        TestCase.assertEquals(13, target.get(1));
        TestCase.assertEquals(14, target.get(2));
        String json = gson.toJson(target);
        TestCase.assertEquals("[10,13,14]", json);
    }

    public void testAtomicLongArrayWithStringSerializationPolicy() throws Exception {
        Gson gson = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
        AtomicLongArray target = gson.fromJson("['10', '13', '14']", AtomicLongArray.class);
        TestCase.assertEquals(3, target.length());
        TestCase.assertEquals(10, target.get(0));
        TestCase.assertEquals(13, target.get(1));
        TestCase.assertEquals(14, target.get(2));
        String json = gson.toJson(target);
        TestCase.assertEquals("[\"10\",\"13\",\"14\"]", json);
    }

    private static class AtomicLongHolder {
        AtomicLong value;
    }
}

