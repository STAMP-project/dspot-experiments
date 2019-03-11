/**
 * Copyright (C) 2011 Google Inc.
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
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Tests for Gson serialization of a sub-class object while encountering a base-class type
 *
 * @author Inderjeet Singh
 */
@SuppressWarnings("unused")
public class MoreSpecificTypeSerializationTest extends TestCase {
    private Gson gson;

    public void testSubclassFields() {
        MoreSpecificTypeSerializationTest.ClassWithBaseFields target = new MoreSpecificTypeSerializationTest.ClassWithBaseFields(new MoreSpecificTypeSerializationTest.Sub(1, 2));
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("\"b\":1"));
        TestCase.assertTrue(json.contains("\"s\":2"));
    }

    public void testListOfSubclassFields() {
        Collection<MoreSpecificTypeSerializationTest.Base> list = new ArrayList<MoreSpecificTypeSerializationTest.Base>();
        list.add(new MoreSpecificTypeSerializationTest.Base(1));
        list.add(new MoreSpecificTypeSerializationTest.Sub(2, 3));
        MoreSpecificTypeSerializationTest.ClassWithContainersOfBaseFields target = new MoreSpecificTypeSerializationTest.ClassWithContainersOfBaseFields(list, null);
        String json = gson.toJson(target);
        TestCase.assertTrue(json, json.contains("{\"b\":1}"));
        TestCase.assertTrue(json, json.contains("{\"s\":3,\"b\":2}"));
    }

    public void testMapOfSubclassFields() {
        Map<String, MoreSpecificTypeSerializationTest.Base> map = new HashMap<String, MoreSpecificTypeSerializationTest.Base>();
        map.put("base", new MoreSpecificTypeSerializationTest.Base(1));
        map.put("sub", new MoreSpecificTypeSerializationTest.Sub(2, 3));
        MoreSpecificTypeSerializationTest.ClassWithContainersOfBaseFields target = new MoreSpecificTypeSerializationTest.ClassWithContainersOfBaseFields(null, map);
        JsonObject json = gson.toJsonTree(target).getAsJsonObject().get("map").getAsJsonObject();
        TestCase.assertEquals(1, json.get("base").getAsJsonObject().get("b").getAsInt());
        JsonObject sub = json.get("sub").getAsJsonObject();
        TestCase.assertEquals(2, sub.get("b").getAsInt());
        TestCase.assertEquals(3, sub.get("s").getAsInt());
    }

    /**
     * For parameterized type, Gson ignores the more-specific type and sticks to the declared type
     */
    public void testParameterizedSubclassFields() {
        MoreSpecificTypeSerializationTest.ClassWithParameterizedBaseFields target = new MoreSpecificTypeSerializationTest.ClassWithParameterizedBaseFields(new MoreSpecificTypeSerializationTest.ParameterizedSub<String>("one", "two"));
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("\"t\":\"one\""));
        TestCase.assertFalse(json.contains("\"s\""));
    }

    /**
     * For parameterized type in a List, Gson ignores the more-specific type and sticks to
     * the declared type
     */
    public void testListOfParameterizedSubclassFields() {
        Collection<MoreSpecificTypeSerializationTest.ParameterizedBase<String>> list = new ArrayList<MoreSpecificTypeSerializationTest.ParameterizedBase<String>>();
        list.add(new MoreSpecificTypeSerializationTest.ParameterizedBase<String>("one"));
        list.add(new MoreSpecificTypeSerializationTest.ParameterizedSub<String>("two", "three"));
        MoreSpecificTypeSerializationTest.ClassWithContainersOfParameterizedBaseFields target = new MoreSpecificTypeSerializationTest.ClassWithContainersOfParameterizedBaseFields(list, null);
        String json = gson.toJson(target);
        TestCase.assertTrue(json, json.contains("{\"t\":\"one\"}"));
        TestCase.assertFalse(json, json.contains("\"s\":"));
    }

    /**
     * For parameterized type in a map, Gson ignores the more-specific type and sticks to the
     * declared type
     */
    public void testMapOfParameterizedSubclassFields() {
        Map<String, MoreSpecificTypeSerializationTest.ParameterizedBase<String>> map = new HashMap<String, MoreSpecificTypeSerializationTest.ParameterizedBase<String>>();
        map.put("base", new MoreSpecificTypeSerializationTest.ParameterizedBase<String>("one"));
        map.put("sub", new MoreSpecificTypeSerializationTest.ParameterizedSub<String>("two", "three"));
        MoreSpecificTypeSerializationTest.ClassWithContainersOfParameterizedBaseFields target = new MoreSpecificTypeSerializationTest.ClassWithContainersOfParameterizedBaseFields(null, map);
        JsonObject json = gson.toJsonTree(target).getAsJsonObject().get("map").getAsJsonObject();
        TestCase.assertEquals("one", json.get("base").getAsJsonObject().get("t").getAsString());
        JsonObject sub = json.get("sub").getAsJsonObject();
        TestCase.assertEquals("two", sub.get("t").getAsString());
        TestCase.assertNull(sub.get("s"));
    }

    private static class Base {
        int b;

        Base(int b) {
            this.b = b;
        }
    }

    private static class Sub extends MoreSpecificTypeSerializationTest.Base {
        int s;

        Sub(int b, int s) {
            super(b);
            this.s = s;
        }
    }

    private static class ClassWithBaseFields {
        MoreSpecificTypeSerializationTest.Base b;

        ClassWithBaseFields(MoreSpecificTypeSerializationTest.Base b) {
            this.b = b;
        }
    }

    private static class ClassWithContainersOfBaseFields {
        Collection<MoreSpecificTypeSerializationTest.Base> collection;

        Map<String, MoreSpecificTypeSerializationTest.Base> map;

        ClassWithContainersOfBaseFields(Collection<MoreSpecificTypeSerializationTest.Base> collection, Map<String, MoreSpecificTypeSerializationTest.Base> map) {
            this.collection = collection;
            this.map = map;
        }
    }

    private static class ParameterizedBase<T> {
        T t;

        ParameterizedBase(T t) {
            this.t = t;
        }
    }

    private static class ParameterizedSub<T> extends MoreSpecificTypeSerializationTest.ParameterizedBase<T> {
        T s;

        ParameterizedSub(T t, T s) {
            super(t);
            this.s = s;
        }
    }

    private static class ClassWithParameterizedBaseFields {
        MoreSpecificTypeSerializationTest.ParameterizedBase<String> b;

        ClassWithParameterizedBaseFields(MoreSpecificTypeSerializationTest.ParameterizedBase<String> b) {
            this.b = b;
        }
    }

    private static class ClassWithContainersOfParameterizedBaseFields {
        Collection<MoreSpecificTypeSerializationTest.ParameterizedBase<String>> collection;

        Map<String, MoreSpecificTypeSerializationTest.ParameterizedBase<String>> map;

        ClassWithContainersOfParameterizedBaseFields(Collection<MoreSpecificTypeSerializationTest.ParameterizedBase<String>> collection, Map<String, MoreSpecificTypeSerializationTest.ParameterizedBase<String>> map) {
            this.collection = collection;
            this.map = map;
        }
    }
}

