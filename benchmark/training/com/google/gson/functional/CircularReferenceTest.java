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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.common.TestTypes;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;


/**
 * Functional tests related to circular reference detection and error reporting.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class CircularReferenceTest extends TestCase {
    private Gson gson;

    public void testCircularSerialization() throws Exception {
        CircularReferenceTest.ContainsReferenceToSelfType a = new CircularReferenceTest.ContainsReferenceToSelfType();
        CircularReferenceTest.ContainsReferenceToSelfType b = new CircularReferenceTest.ContainsReferenceToSelfType();
        a.children.add(b);
        b.children.add(a);
        try {
            gson.toJson(a);
            TestCase.fail("Circular types should not get printed!");
        } catch (StackOverflowError expected) {
        }
    }

    public void testSelfReferenceIgnoredInSerialization() throws Exception {
        TestTypes.ClassOverridingEquals objA = new TestTypes.ClassOverridingEquals();
        objA.ref = objA;
        String json = gson.toJson(objA);
        TestCase.assertFalse(json.contains("ref"));// self-reference is ignored

    }

    public void testSelfReferenceArrayFieldSerialization() throws Exception {
        CircularReferenceTest.ClassWithSelfReferenceArray objA = new CircularReferenceTest.ClassWithSelfReferenceArray();
        objA.children = new CircularReferenceTest.ClassWithSelfReferenceArray[]{ objA };
        try {
            gson.toJson(objA);
            TestCase.fail("Circular reference to self can not be serialized!");
        } catch (StackOverflowError expected) {
        }
    }

    public void testSelfReferenceCustomHandlerSerialization() throws Exception {
        CircularReferenceTest.ClassWithSelfReference obj = new CircularReferenceTest.ClassWithSelfReference();
        obj.child = obj;
        Gson gson = new GsonBuilder().registerTypeAdapter(CircularReferenceTest.ClassWithSelfReference.class, new JsonSerializer<CircularReferenceTest.ClassWithSelfReference>() {
            public JsonElement serialize(CircularReferenceTest.ClassWithSelfReference src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject obj = new JsonObject();
                obj.addProperty("property", "value");
                obj.add("child", context.serialize(src.child));
                return obj;
            }
        }).create();
        try {
            gson.toJson(obj);
            TestCase.fail("Circular reference to self can not be serialized!");
        } catch (StackOverflowError expected) {
        }
    }

    public void testDirectedAcyclicGraphSerialization() throws Exception {
        CircularReferenceTest.ContainsReferenceToSelfType a = new CircularReferenceTest.ContainsReferenceToSelfType();
        CircularReferenceTest.ContainsReferenceToSelfType b = new CircularReferenceTest.ContainsReferenceToSelfType();
        CircularReferenceTest.ContainsReferenceToSelfType c = new CircularReferenceTest.ContainsReferenceToSelfType();
        a.children.add(b);
        a.children.add(c);
        b.children.add(c);
        TestCase.assertNotNull(gson.toJson(a));
    }

    public void testDirectedAcyclicGraphDeserialization() throws Exception {
        String json = "{\"children\":[{\"children\":[{\"children\":[]}]},{\"children\":[]}]}";
        CircularReferenceTest.ContainsReferenceToSelfType target = gson.fromJson(json, CircularReferenceTest.ContainsReferenceToSelfType.class);
        TestCase.assertNotNull(target);
        TestCase.assertEquals(2, target.children.size());
    }

    private static class ContainsReferenceToSelfType {
        Collection<CircularReferenceTest.ContainsReferenceToSelfType> children = new ArrayList<CircularReferenceTest.ContainsReferenceToSelfType>();
    }

    private static class ClassWithSelfReference {
        CircularReferenceTest.ClassWithSelfReference child;
    }

    private static class ClassWithSelfReferenceArray {
        @SuppressWarnings("unused")
        CircularReferenceTest.ClassWithSelfReferenceArray[] children;
    }
}

