/**
 * Copyright (C) 2010 Google Inc.
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
import com.google.gson.reflect.TypeToken;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;


/**
 * Unit tests to validate serialization of parameterized types without explicit types
 *
 * @author Inderjeet Singh
 */
public class RawSerializationTest extends TestCase {
    private Gson gson;

    public void testCollectionOfPrimitives() {
        Collection<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
        String json = gson.toJson(ints);
        TestCase.assertEquals("[1,2,3,4,5]", json);
    }

    public void testCollectionOfObjects() {
        Collection<RawSerializationTest.Foo> foos = Arrays.asList(new RawSerializationTest.Foo(1), new RawSerializationTest.Foo(2));
        String json = gson.toJson(foos);
        TestCase.assertEquals("[{\"b\":1},{\"b\":2}]", json);
    }

    public void testParameterizedObject() {
        RawSerializationTest.Bar<RawSerializationTest.Foo> bar = new RawSerializationTest.Bar<RawSerializationTest.Foo>(new RawSerializationTest.Foo(1));
        String expectedJson = "{\"t\":{\"b\":1}}";
        // Ensure that serialization works without specifying the type explicitly
        String json = gson.toJson(bar);
        TestCase.assertEquals(expectedJson, json);
        // Ensure that serialization also works when the type is specified explicitly
        json = gson.toJson(bar, new TypeToken<RawSerializationTest.Bar<RawSerializationTest.Foo>>() {}.getType());
        TestCase.assertEquals(expectedJson, json);
    }

    public void testTwoLevelParameterizedObject() {
        RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Foo>> bar = new RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Foo>>(new RawSerializationTest.Bar<RawSerializationTest.Foo>(new RawSerializationTest.Foo(1)));
        String expectedJson = "{\"t\":{\"t\":{\"b\":1}}}";
        // Ensure that serialization works without specifying the type explicitly
        String json = gson.toJson(bar);
        TestCase.assertEquals(expectedJson, json);
        // Ensure that serialization also works when the type is specified explicitly
        json = gson.toJson(bar, new TypeToken<RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Foo>>>() {}.getType());
        TestCase.assertEquals(expectedJson, json);
    }

    public void testThreeLevelParameterizedObject() {
        RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Foo>>> bar = new RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Foo>>>(new RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Foo>>(new RawSerializationTest.Bar<RawSerializationTest.Foo>(new RawSerializationTest.Foo(1))));
        String expectedJson = "{\"t\":{\"t\":{\"t\":{\"b\":1}}}}";
        // Ensure that serialization works without specifying the type explicitly
        String json = gson.toJson(bar);
        TestCase.assertEquals(expectedJson, json);
        // Ensure that serialization also works when the type is specified explicitly
        json = gson.toJson(bar, new TypeToken<RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Bar<RawSerializationTest.Foo>>>>() {}.getType());
        TestCase.assertEquals(expectedJson, json);
    }

    private static class Foo {
        @SuppressWarnings("unused")
        int b;

        Foo(int b) {
            this.b = b;
        }
    }

    private static class Bar<T> {
        @SuppressWarnings("unused")
        T t;

        Bar(T t) {
            this.t = t;
        }
    }
}

