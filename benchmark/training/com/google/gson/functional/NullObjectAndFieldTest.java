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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.common.TestTypes;
import java.lang.reflect.Type;
import java.util.Collection;
import junit.framework.TestCase;


/**
 * Functional tests for the different cases for serializing (or ignoring) null fields and object.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class NullObjectAndFieldTest extends TestCase {
    private GsonBuilder gsonBuilder;

    public void testTopLevelNullObjectSerialization() {
        Gson gson = gsonBuilder.create();
        String actual = gson.toJson(null);
        TestCase.assertEquals("null", actual);
        actual = gson.toJson(null, String.class);
        TestCase.assertEquals("null", actual);
    }

    public void testTopLevelNullObjectDeserialization() throws Exception {
        Gson gson = gsonBuilder.create();
        String actual = gson.fromJson("null", String.class);
        TestCase.assertNull(actual);
    }

    public void testExplicitSerializationOfNulls() {
        Gson gson = gsonBuilder.create();
        TestTypes.ClassWithObjects target = new TestTypes.ClassWithObjects(null);
        String actual = gson.toJson(target);
        String expected = "{\"bag\":null}";
        TestCase.assertEquals(expected, actual);
    }

    public void testExplicitDeserializationOfNulls() throws Exception {
        Gson gson = gsonBuilder.create();
        TestTypes.ClassWithObjects target = gson.fromJson("{\"bag\":null}", TestTypes.ClassWithObjects.class);
        TestCase.assertNull(target.bag);
    }

    public void testExplicitSerializationOfNullArrayMembers() {
        Gson gson = gsonBuilder.create();
        NullObjectAndFieldTest.ClassWithMembers target = new NullObjectAndFieldTest.ClassWithMembers();
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("\"array\":null"));
    }

    /**
     * Added to verify http://code.google.com/p/google-gson/issues/detail?id=68
     */
    public void testNullWrappedPrimitiveMemberSerialization() {
        Gson gson = gsonBuilder.serializeNulls().create();
        NullObjectAndFieldTest.ClassWithNullWrappedPrimitive target = new NullObjectAndFieldTest.ClassWithNullWrappedPrimitive();
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("\"value\":null"));
    }

    /**
     * Added to verify http://code.google.com/p/google-gson/issues/detail?id=68
     */
    public void testNullWrappedPrimitiveMemberDeserialization() {
        Gson gson = gsonBuilder.create();
        String json = "{'value':null}";
        NullObjectAndFieldTest.ClassWithNullWrappedPrimitive target = gson.fromJson(json, NullObjectAndFieldTest.ClassWithNullWrappedPrimitive.class);
        TestCase.assertNull(target.value);
    }

    public void testExplicitSerializationOfNullCollectionMembers() {
        Gson gson = gsonBuilder.create();
        NullObjectAndFieldTest.ClassWithMembers target = new NullObjectAndFieldTest.ClassWithMembers();
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("\"col\":null"));
    }

    public void testExplicitSerializationOfNullStringMembers() {
        Gson gson = gsonBuilder.create();
        NullObjectAndFieldTest.ClassWithMembers target = new NullObjectAndFieldTest.ClassWithMembers();
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains("\"str\":null"));
    }

    public void testCustomSerializationOfNulls() {
        gsonBuilder.registerTypeAdapter(TestTypes.ClassWithObjects.class, new NullObjectAndFieldTest.ClassWithObjectsSerializer());
        Gson gson = gsonBuilder.create();
        TestTypes.ClassWithObjects target = new TestTypes.ClassWithObjects(new TestTypes.BagOfPrimitives());
        String actual = gson.toJson(target);
        String expected = "{\"bag\":null}";
        TestCase.assertEquals(expected, actual);
    }

    public void testPrintPrintingObjectWithNulls() throws Exception {
        gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String result = gson.toJson(new NullObjectAndFieldTest.ClassWithMembers());
        TestCase.assertEquals("{}", result);
        gson = gsonBuilder.serializeNulls().create();
        result = gson.toJson(new NullObjectAndFieldTest.ClassWithMembers());
        TestCase.assertTrue(result.contains("\"str\":null"));
    }

    public void testPrintPrintingArraysWithNulls() throws Exception {
        gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String result = gson.toJson(new String[]{ "1", null, "3" });
        TestCase.assertEquals("[\"1\",null,\"3\"]", result);
        gson = gsonBuilder.serializeNulls().create();
        result = gson.toJson(new String[]{ "1", null, "3" });
        TestCase.assertEquals("[\"1\",null,\"3\"]", result);
    }

    // test for issue 389
    public void testAbsentJsonElementsAreSetToNull() {
        Gson gson = new Gson();
        NullObjectAndFieldTest.ClassWithInitializedMembers target = gson.fromJson("{array:[1,2,3]}", NullObjectAndFieldTest.ClassWithInitializedMembers.class);
        TestCase.assertTrue((((target.array.length) == 3) && ((target.array[1]) == 2)));
        TestCase.assertEquals(NullObjectAndFieldTest.ClassWithInitializedMembers.MY_STRING_DEFAULT, target.str1);
        TestCase.assertNull(target.str2);
        TestCase.assertEquals(NullObjectAndFieldTest.ClassWithInitializedMembers.MY_INT_DEFAULT, target.int1);
        TestCase.assertEquals(0, target.int2);// test the default value of a primitive int field per JVM spec

        TestCase.assertEquals(NullObjectAndFieldTest.ClassWithInitializedMembers.MY_BOOLEAN_DEFAULT, target.bool1);
        TestCase.assertFalse(target.bool2);// test the default value of a primitive boolean field per JVM spec

    }

    public static class ClassWithInitializedMembers {
        // Using a mix of no-args constructor and field initializers
        // Also, some fields are intialized and some are not (so initialized per JVM spec)
        public static final String MY_STRING_DEFAULT = "string";

        private static final int MY_INT_DEFAULT = 2;

        private static final boolean MY_BOOLEAN_DEFAULT = true;

        int[] array;

        String str1;

        String str2;

        int int1 = NullObjectAndFieldTest.ClassWithInitializedMembers.MY_INT_DEFAULT;

        int int2;

        boolean bool1 = NullObjectAndFieldTest.ClassWithInitializedMembers.MY_BOOLEAN_DEFAULT;

        boolean bool2;

        public ClassWithInitializedMembers() {
            str1 = NullObjectAndFieldTest.ClassWithInitializedMembers.MY_STRING_DEFAULT;
        }
    }

    private static class ClassWithNullWrappedPrimitive {
        private Long value;
    }

    @SuppressWarnings("unused")
    private static class ClassWithMembers {
        String str;

        int[] array;

        Collection<String> col;
    }

    private static class ClassWithObjectsSerializer implements JsonSerializer<TestTypes.ClassWithObjects> {
        @Override
        public JsonElement serialize(TestTypes.ClassWithObjects src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("bag", JsonNull.INSTANCE);
            return obj;
        }
    }

    public void testExplicitNullSetsFieldToNullDuringDeserialization() {
        Gson gson = new Gson();
        String json = "{value:null}";
        NullObjectAndFieldTest.ObjectWithField obj = gson.fromJson(json, NullObjectAndFieldTest.ObjectWithField.class);
        TestCase.assertNull(obj.value);
    }

    public void testCustomTypeAdapterPassesNullSerialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(NullObjectAndFieldTest.ObjectWithField.class, new JsonSerializer<NullObjectAndFieldTest.ObjectWithField>() {
            @Override
            public JsonElement serialize(NullObjectAndFieldTest.ObjectWithField src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(null);
            }
        }).create();
        NullObjectAndFieldTest.ObjectWithField target = new NullObjectAndFieldTest.ObjectWithField();
        target.value = "value1";
        String json = gson.toJson(target);
        TestCase.assertFalse(json.contains("value1"));
    }

    public void testCustomTypeAdapterPassesNullDesrialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(NullObjectAndFieldTest.ObjectWithField.class, new JsonDeserializer<NullObjectAndFieldTest.ObjectWithField>() {
            @Override
            public NullObjectAndFieldTest.ObjectWithField deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
                return context.deserialize(null, type);
            }
        }).create();
        String json = "{value:'value1'}";
        NullObjectAndFieldTest.ObjectWithField target = gson.fromJson(json, NullObjectAndFieldTest.ObjectWithField.class);
        TestCase.assertNull(target);
    }

    private static class ObjectWithField {
        String value = "";
    }
}

