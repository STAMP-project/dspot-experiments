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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.common.TestTypes;
import com.google.gson.internal.JavaVersion;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import junit.framework.TestCase;

import static com.google.gson.common.TestTypes.BagOfPrimitives.DEFAULT_VALUE;


/**
 * Functional tests for Json serialization and deserialization of regular classes.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ObjectTest extends TestCase {
    private Gson gson;

    private TimeZone oldTimeZone = TimeZone.getDefault();

    public void testJsonInSingleQuotesDeserialization() {
        String json = "{'stringValue':'no message','intValue':10,'longValue':20}";
        TestTypes.BagOfPrimitives target = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("no message", target.stringValue);
        TestCase.assertEquals(10, target.intValue);
        TestCase.assertEquals(20, target.longValue);
    }

    public void testJsonInMixedQuotesDeserialization() {
        String json = "{\"stringValue\":\'no message\',\'intValue\':10,\'longValue\':20}";
        TestTypes.BagOfPrimitives target = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("no message", target.stringValue);
        TestCase.assertEquals(10, target.intValue);
        TestCase.assertEquals(20, target.longValue);
    }

    public void testBagOfPrimitivesSerialization() throws Exception {
        TestTypes.BagOfPrimitives target = new TestTypes.BagOfPrimitives(10, 20, false, "stringValue");
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testBagOfPrimitivesDeserialization() throws Exception {
        TestTypes.BagOfPrimitives src = new TestTypes.BagOfPrimitives(10, 20, false, "stringValue");
        String json = src.getExpectedJson();
        TestTypes.BagOfPrimitives target = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals(json, target.getExpectedJson());
    }

    public void testBagOfPrimitiveWrappersSerialization() throws Exception {
        TestTypes.BagOfPrimitiveWrappers target = new TestTypes.BagOfPrimitiveWrappers(10L, 20, false);
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testBagOfPrimitiveWrappersDeserialization() throws Exception {
        TestTypes.BagOfPrimitiveWrappers target = new TestTypes.BagOfPrimitiveWrappers(10L, 20, false);
        String jsonString = target.getExpectedJson();
        target = gson.fromJson(jsonString, TestTypes.BagOfPrimitiveWrappers.class);
        TestCase.assertEquals(jsonString, target.getExpectedJson());
    }

    public void testClassWithTransientFieldsSerialization() throws Exception {
        TestTypes.ClassWithTransientFields<Long> target = new TestTypes.ClassWithTransientFields<Long>(1L);
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testClassWithNoFieldsSerialization() throws Exception {
        TestCase.assertEquals("{}", gson.toJson(new TestTypes.ClassWithNoFields()));
    }

    public void testClassWithNoFieldsDeserialization() throws Exception {
        String json = "{}";
        TestTypes.ClassWithNoFields target = gson.fromJson(json, TestTypes.ClassWithNoFields.class);
        TestTypes.ClassWithNoFields expected = new TestTypes.ClassWithNoFields();
        TestCase.assertEquals(expected, target);
    }

    public void testNestedSerialization() throws Exception {
        TestTypes.Nested target = new TestTypes.Nested(new TestTypes.BagOfPrimitives(10, 20, false, "stringValue"), new TestTypes.BagOfPrimitives(30, 40, true, "stringValue"));
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testNestedDeserialization() throws Exception {
        String json = "{\"primitive1\":{\"longValue\":10,\"intValue\":20,\"booleanValue\":false," + ("\"stringValue\":\"stringValue\"},\"primitive2\":{\"longValue\":30,\"intValue\":40," + "\"booleanValue\":true,\"stringValue\":\"stringValue\"}}");
        TestTypes.Nested target = gson.fromJson(json, TestTypes.Nested.class);
        TestCase.assertEquals(json, target.getExpectedJson());
    }

    public void testNullSerialization() throws Exception {
        TestCase.assertEquals("null", gson.toJson(null));
    }

    public void testEmptyStringDeserialization() throws Exception {
        Object object = gson.fromJson("", Object.class);
        TestCase.assertNull(object);
    }

    public void testTruncatedDeserialization() {
        try {
            gson.fromJson("[\"a\", \"b\",", new TypeToken<List<String>>() {}.getType());
            TestCase.fail();
        } catch (JsonParseException expected) {
        }
    }

    public void testNullDeserialization() throws Exception {
        String myNullObject = null;
        Object object = gson.fromJson(myNullObject, Object.class);
        TestCase.assertNull(object);
    }

    public void testNullFieldsSerialization() throws Exception {
        TestTypes.Nested target = new TestTypes.Nested(new TestTypes.BagOfPrimitives(10, 20, false, "stringValue"), null);
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testNullFieldsDeserialization() throws Exception {
        String json = "{\"primitive1\":{\"longValue\":10,\"intValue\":20,\"booleanValue\":false" + ",\"stringValue\":\"stringValue\"}}";
        TestTypes.Nested target = gson.fromJson(json, TestTypes.Nested.class);
        TestCase.assertEquals(json, target.getExpectedJson());
    }

    public void testArrayOfObjectsSerialization() throws Exception {
        TestTypes.ArrayOfObjects target = new TestTypes.ArrayOfObjects();
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testArrayOfObjectsDeserialization() throws Exception {
        String json = new TestTypes.ArrayOfObjects().getExpectedJson();
        TestTypes.ArrayOfObjects target = gson.fromJson(json, TestTypes.ArrayOfObjects.class);
        TestCase.assertEquals(json, target.getExpectedJson());
    }

    public void testArrayOfArraysSerialization() throws Exception {
        ObjectTest.ArrayOfArrays target = new ObjectTest.ArrayOfArrays();
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testArrayOfArraysDeserialization() throws Exception {
        String json = new ObjectTest.ArrayOfArrays().getExpectedJson();
        ObjectTest.ArrayOfArrays target = gson.fromJson(json, ObjectTest.ArrayOfArrays.class);
        TestCase.assertEquals(json, target.getExpectedJson());
    }

    public void testArrayOfObjectsAsFields() throws Exception {
        TestTypes.ClassWithObjects classWithObjects = new TestTypes.ClassWithObjects();
        TestTypes.BagOfPrimitives bagOfPrimitives = new TestTypes.BagOfPrimitives();
        String stringValue = "someStringValueInArray";
        String classWithObjectsJson = gson.toJson(classWithObjects);
        String bagOfPrimitivesJson = gson.toJson(bagOfPrimitives);
        TestTypes.ClassWithArray classWithArray = new TestTypes.ClassWithArray(new Object[]{ stringValue, classWithObjects, bagOfPrimitives });
        String json = gson.toJson(classWithArray);
        TestCase.assertTrue(json.contains(classWithObjectsJson));
        TestCase.assertTrue(json.contains(bagOfPrimitivesJson));
        TestCase.assertTrue(json.contains((("\"" + stringValue) + "\"")));
    }

    /**
     * Created in response to Issue 14: http://code.google.com/p/google-gson/issues/detail?id=14
     */
    public void testNullArraysDeserialization() throws Exception {
        String json = "{\"array\": null}";
        TestTypes.ClassWithArray target = gson.fromJson(json, TestTypes.ClassWithArray.class);
        TestCase.assertNull(target.array);
    }

    /**
     * Created in response to Issue 14: http://code.google.com/p/google-gson/issues/detail?id=14
     */
    public void testNullObjectFieldsDeserialization() throws Exception {
        String json = "{\"bag\": null}";
        TestTypes.ClassWithObjects target = gson.fromJson(json, TestTypes.ClassWithObjects.class);
        TestCase.assertNull(target.bag);
    }

    public void testEmptyCollectionInAnObjectDeserialization() throws Exception {
        String json = "{\"children\":[]}";
        ObjectTest.ClassWithCollectionField target = gson.fromJson(json, ObjectTest.ClassWithCollectionField.class);
        TestCase.assertNotNull(target);
        TestCase.assertTrue(target.children.isEmpty());
    }

    private static class ClassWithCollectionField {
        Collection<String> children = new ArrayList<String>();
    }

    public void testPrimitiveArrayInAnObjectDeserialization() throws Exception {
        String json = "{\"longArray\":[0,1,2,3,4,5,6,7,8,9]}";
        TestTypes.PrimitiveArray target = gson.fromJson(json, TestTypes.PrimitiveArray.class);
        TestCase.assertEquals(json, target.getExpectedJson());
    }

    /**
     * Created in response to Issue 14: http://code.google.com/p/google-gson/issues/detail?id=14
     */
    public void testNullPrimitiveFieldsDeserialization() throws Exception {
        String json = "{\"longValue\":null}";
        TestTypes.BagOfPrimitives target = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals(DEFAULT_VALUE, target.longValue);
    }

    public void testEmptyCollectionInAnObjectSerialization() throws Exception {
        ObjectTest.ClassWithCollectionField target = new ObjectTest.ClassWithCollectionField();
        TestCase.assertEquals("{\"children\":[]}", gson.toJson(target));
    }

    public void testPrivateNoArgConstructorDeserialization() throws Exception {
        ObjectTest.ClassWithPrivateNoArgsConstructor target = gson.fromJson("{\"a\":20}", ObjectTest.ClassWithPrivateNoArgsConstructor.class);
        TestCase.assertEquals(20, target.a);
    }

    public void testAnonymousLocalClassesSerialization() throws Exception {
        TestCase.assertEquals("null", gson.toJson(new TestTypes.ClassWithNoFields() {}));
    }

    public void testAnonymousLocalClassesCustomSerialization() throws Exception {
        gson = new GsonBuilder().registerTypeHierarchyAdapter(TestTypes.ClassWithNoFields.class, new JsonSerializer<TestTypes.ClassWithNoFields>() {
            public JsonElement serialize(TestTypes.ClassWithNoFields src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonObject();
            }
        }).create();
        TestCase.assertEquals("null", gson.toJson(new TestTypes.ClassWithNoFields() {}));
    }

    public void testPrimitiveArrayFieldSerialization() {
        TestTypes.PrimitiveArray target = new TestTypes.PrimitiveArray(new long[]{ 1L, 2L, 3L });
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    /**
     * Tests that a class field with type Object can be serialized properly.
     * See issue 54
     */
    public void testClassWithObjectFieldSerialization() {
        ObjectTest.ClassWithObjectField obj = new ObjectTest.ClassWithObjectField();
        obj.member = "abc";
        String json = gson.toJson(obj);
        TestCase.assertTrue(json.contains("abc"));
    }

    private static class ClassWithObjectField {
        @SuppressWarnings("unused")
        Object member;
    }

    public void testInnerClassSerialization() {
        ObjectTest.Parent p = new ObjectTest.Parent();
        ObjectTest.Parent.Child c = p.new Child();
        String json = gson.toJson(c);
        TestCase.assertTrue(json.contains("value2"));
        TestCase.assertFalse(json.contains("value1"));
    }

    public void testInnerClassDeserialization() {
        final ObjectTest.Parent p = new ObjectTest.Parent();
        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectTest.Parent.Child.class, new InstanceCreator<ObjectTest.Parent.Child>() {
            public ObjectTest.Parent.Child createInstance(Type type) {
                return p.new Child();
            }
        }).create();
        String json = "{'value2':3}";
        ObjectTest.Parent.Child c = gson.fromJson(json, ObjectTest.Parent.Child.class);
        TestCase.assertEquals(3, c.value2);
    }

    private static class Parent {
        @SuppressWarnings("unused")
        int value1 = 1;

        private class Child {
            int value2 = 2;
        }
    }

    private static class ArrayOfArrays {
        private final TestTypes.BagOfPrimitives[][] elements;

        public ArrayOfArrays() {
            elements = new TestTypes.BagOfPrimitives[3][2];
            for (int i = 0; i < (elements.length); ++i) {
                TestTypes.BagOfPrimitives[] row = elements[i];
                for (int j = 0; j < (row.length); ++j) {
                    row[j] = new TestTypes.BagOfPrimitives((i + j), (i * j), false, ((i + "_") + j));
                }
            }
        }

        public String getExpectedJson() {
            StringBuilder sb = new StringBuilder("{\"elements\":[");
            boolean first = true;
            for (TestTypes.BagOfPrimitives[] row : elements) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                boolean firstOfRow = true;
                sb.append("[");
                for (TestTypes.BagOfPrimitives element : row) {
                    if (firstOfRow) {
                        firstOfRow = false;
                    } else {
                        sb.append(",");
                    }
                    sb.append(element.getExpectedJson());
                }
                sb.append("]");
            }
            sb.append("]}");
            return sb.toString();
        }
    }

    private static class ClassWithPrivateNoArgsConstructor {
        public int a;

        private ClassWithPrivateNoArgsConstructor() {
            a = 10;
        }
    }

    /**
     * In response to Issue 41 http://code.google.com/p/google-gson/issues/detail?id=41
     */
    public void testObjectFieldNamesWithoutQuotesDeserialization() {
        String json = "{longValue:1,\'booleanValue\':true,\"stringValue\":\'bar\'}";
        TestTypes.BagOfPrimitives bag = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals(1, bag.longValue);
        TestCase.assertTrue(bag.booleanValue);
        TestCase.assertEquals("bar", bag.stringValue);
    }

    public void testStringFieldWithNumberValueDeserialization() {
        String json = "{\"stringValue\":1}";
        TestTypes.BagOfPrimitives bag = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("1", bag.stringValue);
        json = "{\"stringValue\":1.5E+6}";
        bag = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("1.5E+6", bag.stringValue);
        json = "{\"stringValue\":true}";
        bag = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("true", bag.stringValue);
    }

    /**
     * Created to reproduce issue 140
     */
    public void testStringFieldWithEmptyValueSerialization() {
        ObjectTest.ClassWithEmptyStringFields target = new ObjectTest.ClassWithEmptyStringFields();
        target.a = "5794749";
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("\"a\":\"5794749\""));
        TestCase.assertTrue(json.contains("\"b\":\"\""));
        TestCase.assertTrue(json.contains("\"c\":\"\""));
    }

    /**
     * Created to reproduce issue 140
     */
    public void testStringFieldWithEmptyValueDeserialization() {
        String json = "{a:\"5794749\",b:\"\",c:\"\"}";
        ObjectTest.ClassWithEmptyStringFields target = gson.fromJson(json, ObjectTest.ClassWithEmptyStringFields.class);
        TestCase.assertEquals("5794749", target.a);
        TestCase.assertEquals("", target.b);
        TestCase.assertEquals("", target.c);
    }

    private static class ClassWithEmptyStringFields {
        String a = "";

        String b = "";

        String c = "";
    }

    public void testJsonObjectSerialization() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject obj = new JsonObject();
        String json = gson.toJson(obj);
        TestCase.assertEquals("{}", json);
    }

    /**
     * Test for issue 215.
     */
    public void testSingletonLists() {
        Gson gson = new Gson();
        ObjectTest.Product product = new ObjectTest.Product();
        TestCase.assertEquals("{\"attributes\":[],\"departments\":[]}", gson.toJson(product));
        gson.fromJson(gson.toJson(product), ObjectTest.Product.class);
        product.departments.add(new ObjectTest.Department());
        TestCase.assertEquals("{\"attributes\":[],\"departments\":[{\"name\":\"abc\",\"code\":\"123\"}]}", gson.toJson(product));
        gson.fromJson(gson.toJson(product), ObjectTest.Product.class);
        product.attributes.add("456");
        TestCase.assertEquals("{\"attributes\":[\"456\"],\"departments\":[{\"name\":\"abc\",\"code\":\"123\"}]}", gson.toJson(product));
        gson.fromJson(gson.toJson(product), ObjectTest.Product.class);
    }

    // http://code.google.com/p/google-gson/issues/detail?id=270
    public void testDateAsMapObjectField() {
        ObjectTest.HasObjectMap a = new ObjectTest.HasObjectMap();
        a.map.put("date", new Date(0));
        if (JavaVersion.isJava9OrLater()) {
            TestCase.assertEquals("{\"map\":{\"date\":\"Dec 31, 1969, 4:00:00 PM\"}}", gson.toJson(a));
        } else {
            TestCase.assertEquals("{\"map\":{\"date\":\"Dec 31, 1969 4:00:00 PM\"}}", gson.toJson(a));
        }
    }

    public class HasObjectMap {
        Map<String, Object> map = new HashMap<String, Object>();
    }

    static final class Department {
        public String name = "abc";

        public String code = "123";
    }

    static final class Product {
        private List<String> attributes = new ArrayList<String>();

        private List<ObjectTest.Department> departments = new ArrayList<ObjectTest.Department>();
    }
}

