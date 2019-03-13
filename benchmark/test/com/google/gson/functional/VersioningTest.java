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
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.common.TestTypes;
import junit.framework.TestCase;


/**
 * Functional tests for versioning support in Gson.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class VersioningTest extends TestCase {
    private static final int A = 0;

    private static final int B = 1;

    private static final int C = 2;

    private static final int D = 3;

    private GsonBuilder builder;

    public void testVersionedUntilSerialization() {
        VersioningTest.Version1 target = new VersioningTest.Version1();
        Gson gson = builder.setVersion(1.29).create();
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains(("\"a\":" + (VersioningTest.A))));
        gson = builder.setVersion(1.3).create();
        json = gson.toJson(target);
        TestCase.assertFalse(json.contains(("\"a\":" + (VersioningTest.A))));
    }

    public void testVersionedUntilDeserialization() {
        Gson gson = builder.setVersion(1.3).create();
        String json = "{\"a\":3,\"b\":4,\"c\":5}";
        VersioningTest.Version1 version1 = gson.fromJson(json, VersioningTest.Version1.class);
        TestCase.assertEquals(VersioningTest.A, version1.a);
    }

    public void testVersionedClassesSerialization() {
        Gson gson = builder.setVersion(1.0).create();
        String json1 = gson.toJson(new VersioningTest.Version1());
        String json2 = gson.toJson(new VersioningTest.Version1_1());
        TestCase.assertEquals(json1, json2);
    }

    public void testVersionedClassesDeserialization() {
        Gson gson = builder.setVersion(1.0).create();
        String json = "{\"a\":3,\"b\":4,\"c\":5}";
        VersioningTest.Version1 version1 = gson.fromJson(json, VersioningTest.Version1.class);
        TestCase.assertEquals(3, version1.a);
        TestCase.assertEquals(4, version1.b);
        VersioningTest.Version1_1 version1_1 = gson.fromJson(json, VersioningTest.Version1_1.class);
        TestCase.assertEquals(3, version1_1.a);
        TestCase.assertEquals(4, version1_1.b);
        TestCase.assertEquals(VersioningTest.C, version1_1.c);
    }

    public void testIgnoreLaterVersionClassSerialization() {
        Gson gson = builder.setVersion(1.0).create();
        TestCase.assertEquals("null", gson.toJson(new VersioningTest.Version1_2()));
    }

    public void testIgnoreLaterVersionClassDeserialization() {
        Gson gson = builder.setVersion(1.0).create();
        String json = "{\"a\":3,\"b\":4,\"c\":5,\"d\":6}";
        VersioningTest.Version1_2 version1_2 = gson.fromJson(json, VersioningTest.Version1_2.class);
        // Since the class is versioned to be after 1.0, we expect null
        // This is the new behavior in Gson 2.0
        TestCase.assertNull(version1_2);
    }

    public void testVersionedGsonWithUnversionedClassesSerialization() {
        Gson gson = builder.setVersion(1.0).create();
        TestTypes.BagOfPrimitives target = new TestTypes.BagOfPrimitives(10, 20, false, "stringValue");
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testVersionedGsonWithUnversionedClassesDeserialization() {
        Gson gson = builder.setVersion(1.0).create();
        String json = "{\"longValue\":10,\"intValue\":20,\"booleanValue\":false}";
        TestTypes.BagOfPrimitives expected = new TestTypes.BagOfPrimitives();
        expected.longValue = 10;
        expected.intValue = 20;
        expected.booleanValue = false;
        TestTypes.BagOfPrimitives actual = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals(expected, actual);
    }

    public void testVersionedGsonMixingSinceAndUntilSerialization() {
        Gson gson = builder.setVersion(1.0).create();
        VersioningTest.SinceUntilMixing target = new VersioningTest.SinceUntilMixing();
        String json = gson.toJson(target);
        TestCase.assertFalse(json.contains(("\"b\":" + (VersioningTest.B))));
        gson = builder.setVersion(1.2).create();
        json = gson.toJson(target);
        TestCase.assertTrue(json.contains(("\"b\":" + (VersioningTest.B))));
        gson = builder.setVersion(1.3).create();
        json = gson.toJson(target);
        TestCase.assertFalse(json.contains(("\"b\":" + (VersioningTest.B))));
    }

    public void testVersionedGsonMixingSinceAndUntilDeserialization() {
        String json = "{\"a\":5,\"b\":6}";
        Gson gson = builder.setVersion(1.0).create();
        VersioningTest.SinceUntilMixing result = gson.fromJson(json, VersioningTest.SinceUntilMixing.class);
        TestCase.assertEquals(5, result.a);
        TestCase.assertEquals(VersioningTest.B, result.b);
        gson = builder.setVersion(1.2).create();
        result = gson.fromJson(json, VersioningTest.SinceUntilMixing.class);
        TestCase.assertEquals(5, result.a);
        TestCase.assertEquals(6, result.b);
        gson = builder.setVersion(1.3).create();
        result = gson.fromJson(json, VersioningTest.SinceUntilMixing.class);
        TestCase.assertEquals(5, result.a);
        TestCase.assertEquals(VersioningTest.B, result.b);
    }

    private static class Version1 {
        @Until(1.3)
        int a = VersioningTest.A;

        @Since(1.0)
        int b = VersioningTest.B;
    }

    private static class Version1_1 extends VersioningTest.Version1 {
        @Since(1.1)
        int c = VersioningTest.C;
    }

    @Since(1.2)
    private static class Version1_2 extends VersioningTest.Version1_1 {
        @SuppressWarnings("unused")
        int d = VersioningTest.D;
    }

    private static class SinceUntilMixing {
        int a = VersioningTest.A;

        @Since(1.1)
        @Until(1.3)
        int b = VersioningTest.B;
    }
}

