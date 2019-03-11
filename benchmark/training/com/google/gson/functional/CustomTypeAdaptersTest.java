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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.common.TestTypes;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Functional tests for the support of custom serializer and deserializers.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class CustomTypeAdaptersTest extends TestCase {
    private GsonBuilder builder;

    public void testCustomSerializers() {
        Gson gson = builder.registerTypeAdapter(TestTypes.ClassWithCustomTypeConverter.class, new JsonSerializer<TestTypes.ClassWithCustomTypeConverter>() {
            @Override
            public JsonElement serialize(TestTypes.ClassWithCustomTypeConverter src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = new JsonObject();
                json.addProperty("bag", 5);
                json.addProperty("value", 25);
                return json;
            }
        }).create();
        TestTypes.ClassWithCustomTypeConverter target = new TestTypes.ClassWithCustomTypeConverter();
        TestCase.assertEquals("{\"bag\":5,\"value\":25}", gson.toJson(target));
    }

    public void testCustomDeserializers() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.ClassWithCustomTypeConverter.class, new JsonDeserializer<TestTypes.ClassWithCustomTypeConverter>() {
            @Override
            public TestTypes.ClassWithCustomTypeConverter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                JsonObject jsonObject = json.getAsJsonObject();
                int value = jsonObject.get("bag").getAsInt();
                return new TestTypes.ClassWithCustomTypeConverter(new TestTypes.BagOfPrimitives(value, value, false, ""), value);
            }
        }).create();
        String json = "{\"bag\":5,\"value\":25}";
        TestTypes.ClassWithCustomTypeConverter target = gson.fromJson(json, TestTypes.ClassWithCustomTypeConverter.class);
        TestCase.assertEquals(5, target.getBag().getIntValue());
    }

    public void testCustomNestedSerializers() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.BagOfPrimitives.class, new JsonSerializer<TestTypes.BagOfPrimitives>() {
            @Override
            public JsonElement serialize(TestTypes.BagOfPrimitives src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(6);
            }
        }).create();
        TestTypes.ClassWithCustomTypeConverter target = new TestTypes.ClassWithCustomTypeConverter();
        TestCase.assertEquals("{\"bag\":6,\"value\":10}", gson.toJson(target));
    }

    public void testCustomNestedDeserializers() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.BagOfPrimitives.class, new JsonDeserializer<TestTypes.BagOfPrimitives>() {
            @Override
            public TestTypes.BagOfPrimitives deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                int value = json.getAsInt();
                return new TestTypes.BagOfPrimitives(value, value, false, "");
            }
        }).create();
        String json = "{\"bag\":7,\"value\":25}";
        TestTypes.ClassWithCustomTypeConverter target = gson.fromJson(json, TestTypes.ClassWithCustomTypeConverter.class);
        TestCase.assertEquals(7, target.getBag().getIntValue());
    }

    public void testCustomTypeAdapterDoesNotAppliesToSubClasses() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.Base.class, new JsonSerializer<CustomTypeAdaptersTest.Base>() {
            @Override
            public JsonElement serialize(CustomTypeAdaptersTest.Base src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = new JsonObject();
                json.addProperty("value", src.baseValue);
                return json;
            }
        }).create();
        CustomTypeAdaptersTest.Base b = new CustomTypeAdaptersTest.Base();
        String json = gson.toJson(b);
        TestCase.assertTrue(json.contains("value"));
        b = new CustomTypeAdaptersTest.Derived();
        json = gson.toJson(b);
        TestCase.assertTrue(json.contains("derivedValue"));
    }

    public void testCustomTypeAdapterAppliesToSubClassesSerializedAsBaseClass() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.Base.class, new JsonSerializer<CustomTypeAdaptersTest.Base>() {
            @Override
            public JsonElement serialize(CustomTypeAdaptersTest.Base src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = new JsonObject();
                json.addProperty("value", src.baseValue);
                return json;
            }
        }).create();
        CustomTypeAdaptersTest.Base b = new CustomTypeAdaptersTest.Base();
        String json = gson.toJson(b);
        TestCase.assertTrue(json.contains("value"));
        b = new CustomTypeAdaptersTest.Derived();
        json = gson.toJson(b, CustomTypeAdaptersTest.Base.class);
        TestCase.assertTrue(json.contains("value"));
        TestCase.assertFalse(json.contains("derivedValue"));
    }

    private static class Base {
        int baseValue = 2;
    }

    private static class Derived extends CustomTypeAdaptersTest.Base {
        @SuppressWarnings("unused")
        int derivedValue = 3;
    }

    public static class Foo {
        private final int key;

        private final long value;

        public Foo() {
            this(0, 0L);
        }

        public Foo(int key, long value) {
            this.key = key;
            this.value = value;
        }
    }

    public static final class FooTypeAdapter implements JsonDeserializer<CustomTypeAdaptersTest.Foo> , JsonSerializer<CustomTypeAdaptersTest.Foo> {
        @Override
        public CustomTypeAdaptersTest.Foo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.deserialize(json, typeOfT);
        }

        @Override
        public JsonElement serialize(CustomTypeAdaptersTest.Foo src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, typeOfSrc);
        }
    }

    public void testCustomSerializerInvokedForPrimitives() {
        Gson gson = new GsonBuilder().registerTypeAdapter(boolean.class, new JsonSerializer<Boolean>() {
            @Override
            public JsonElement serialize(Boolean s, Type t, JsonSerializationContext c) {
                return new JsonPrimitive((s ? 1 : 0));
            }
        }).create();
        TestCase.assertEquals("1", gson.toJson(true, boolean.class));
        TestCase.assertEquals("true", gson.toJson(true, Boolean.class));
    }

    public void testCustomByteArraySerializer() {
        Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
            @Override
            public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
                StringBuilder sb = new StringBuilder(src.length);
                for (byte b : src) {
                    sb.append(b);
                }
                return new JsonPrimitive(sb.toString());
            }
        }).create();
        byte[] data = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        String json = gson.toJson(data);
        TestCase.assertEquals("\"0123456789\"", json);
    }

    public void testCustomByteArrayDeserializerAndInstanceCreator() {
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
            @Override
            public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                String str = json.getAsString();
                byte[] data = new byte[str.length()];
                for (int i = 0; i < (data.length); ++i) {
                    data[i] = Byte.parseByte(("" + (str.charAt(i))));
                }
                return data;
            }
        });
        Gson gson = gsonBuilder.create();
        String json = "'0123456789'";
        byte[] actual = gson.fromJson(json, byte[].class);
        byte[] expected = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        for (int i = 0; i < (actual.length); ++i) {
            TestCase.assertEquals(expected[i], actual[i]);
        }
    }

    private static final class StringHolder {
        String part1;

        String part2;

        public StringHolder(String string) {
            String[] parts = string.split(":");
            part1 = parts[0];
            part2 = parts[1];
        }

        public StringHolder(String part1, String part2) {
            this.part1 = part1;
            this.part2 = part2;
        }
    }

    private static class StringHolderTypeAdapter implements InstanceCreator<CustomTypeAdaptersTest.StringHolder> , JsonDeserializer<CustomTypeAdaptersTest.StringHolder> , JsonSerializer<CustomTypeAdaptersTest.StringHolder> {
        @Override
        public CustomTypeAdaptersTest.StringHolder createInstance(Type type) {
            // Fill up with objects that will be thrown away
            return new CustomTypeAdaptersTest.StringHolder("unknown:thing");
        }

        @Override
        public CustomTypeAdaptersTest.StringHolder deserialize(JsonElement src, Type type, JsonDeserializationContext context) {
            return new CustomTypeAdaptersTest.StringHolder(src.getAsString());
        }

        @Override
        public JsonElement serialize(CustomTypeAdaptersTest.StringHolder src, Type typeOfSrc, JsonSerializationContext context) {
            String contents = ((src.part1) + ':') + (src.part2);
            return new JsonPrimitive(contents);
        }
    }

    // Test created from Issue 70
    public void testCustomAdapterInvokedForCollectionElementSerializationWithType() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.StringHolder.class, new CustomTypeAdaptersTest.StringHolderTypeAdapter()).create();
        Type setType = new TypeToken<Set<CustomTypeAdaptersTest.StringHolder>>() {}.getType();
        CustomTypeAdaptersTest.StringHolder holder = new CustomTypeAdaptersTest.StringHolder("Jacob", "Tomaw");
        Set<CustomTypeAdaptersTest.StringHolder> setOfHolders = new HashSet<CustomTypeAdaptersTest.StringHolder>();
        setOfHolders.add(holder);
        String json = gson.toJson(setOfHolders, setType);
        TestCase.assertTrue(json.contains("Jacob:Tomaw"));
    }

    // Test created from Issue 70
    public void testCustomAdapterInvokedForCollectionElementSerialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.StringHolder.class, new CustomTypeAdaptersTest.StringHolderTypeAdapter()).create();
        CustomTypeAdaptersTest.StringHolder holder = new CustomTypeAdaptersTest.StringHolder("Jacob", "Tomaw");
        Set<CustomTypeAdaptersTest.StringHolder> setOfHolders = new HashSet<CustomTypeAdaptersTest.StringHolder>();
        setOfHolders.add(holder);
        String json = gson.toJson(setOfHolders);
        TestCase.assertTrue(json.contains("Jacob:Tomaw"));
    }

    // Test created from Issue 70
    public void testCustomAdapterInvokedForCollectionElementDeserialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.StringHolder.class, new CustomTypeAdaptersTest.StringHolderTypeAdapter()).create();
        Type setType = new TypeToken<Set<CustomTypeAdaptersTest.StringHolder>>() {}.getType();
        Set<CustomTypeAdaptersTest.StringHolder> setOfHolders = gson.fromJson("['Jacob:Tomaw']", setType);
        TestCase.assertEquals(1, setOfHolders.size());
        CustomTypeAdaptersTest.StringHolder foo = setOfHolders.iterator().next();
        TestCase.assertEquals("Jacob", foo.part1);
        TestCase.assertEquals("Tomaw", foo.part2);
    }

    // Test created from Issue 70
    public void testCustomAdapterInvokedForMapElementSerializationWithType() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.StringHolder.class, new CustomTypeAdaptersTest.StringHolderTypeAdapter()).create();
        Type mapType = new TypeToken<Map<String, CustomTypeAdaptersTest.StringHolder>>() {}.getType();
        CustomTypeAdaptersTest.StringHolder holder = new CustomTypeAdaptersTest.StringHolder("Jacob", "Tomaw");
        Map<String, CustomTypeAdaptersTest.StringHolder> mapOfHolders = new HashMap<String, CustomTypeAdaptersTest.StringHolder>();
        mapOfHolders.put("foo", holder);
        String json = gson.toJson(mapOfHolders, mapType);
        TestCase.assertTrue(json.contains("\"foo\":\"Jacob:Tomaw\""));
    }

    // Test created from Issue 70
    public void testCustomAdapterInvokedForMapElementSerialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.StringHolder.class, new CustomTypeAdaptersTest.StringHolderTypeAdapter()).create();
        CustomTypeAdaptersTest.StringHolder holder = new CustomTypeAdaptersTest.StringHolder("Jacob", "Tomaw");
        Map<String, CustomTypeAdaptersTest.StringHolder> mapOfHolders = new HashMap<String, CustomTypeAdaptersTest.StringHolder>();
        mapOfHolders.put("foo", holder);
        String json = gson.toJson(mapOfHolders);
        TestCase.assertTrue(json.contains("\"foo\":\"Jacob:Tomaw\""));
    }

    // Test created from Issue 70
    public void testCustomAdapterInvokedForMapElementDeserialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.StringHolder.class, new CustomTypeAdaptersTest.StringHolderTypeAdapter()).create();
        Type mapType = new TypeToken<Map<String, CustomTypeAdaptersTest.StringHolder>>() {}.getType();
        Map<String, CustomTypeAdaptersTest.StringHolder> mapOfFoo = gson.fromJson("{'foo':'Jacob:Tomaw'}", mapType);
        TestCase.assertEquals(1, mapOfFoo.size());
        CustomTypeAdaptersTest.StringHolder foo = mapOfFoo.get("foo");
        TestCase.assertEquals("Jacob", foo.part1);
        TestCase.assertEquals("Tomaw", foo.part2);
    }

    public void testEnsureCustomSerializerNotInvokedForNullValues() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.DataHolder.class, new CustomTypeAdaptersTest.DataHolderSerializer()).create();
        CustomTypeAdaptersTest.DataHolderWrapper target = new CustomTypeAdaptersTest.DataHolderWrapper(new CustomTypeAdaptersTest.DataHolder("abc"));
        String json = gson.toJson(target);
        TestCase.assertEquals("{\"wrappedData\":{\"myData\":\"abc\"}}", json);
    }

    public void testEnsureCustomDeserializerNotInvokedForNullValues() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomTypeAdaptersTest.DataHolder.class, new CustomTypeAdaptersTest.DataHolderDeserializer()).create();
        String json = "{wrappedData:null}";
        CustomTypeAdaptersTest.DataHolderWrapper actual = gson.fromJson(json, CustomTypeAdaptersTest.DataHolderWrapper.class);
        TestCase.assertNull(actual.wrappedData);
    }

    // Test created from Issue 352
    public void testRegisterHierarchyAdapterForDate() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new CustomTypeAdaptersTest.DateTypeAdapter()).create();
        TestCase.assertEquals("0", gson.toJson(new Date(0)));
        TestCase.assertEquals("0", gson.toJson(new java.sql.Date(0)));
        TestCase.assertEquals(new Date(0), gson.fromJson("0", Date.class));
        TestCase.assertEquals(new java.sql.Date(0), gson.fromJson("0", java.sql.Date.class));
    }

    private static class DataHolder {
        final String data;

        public DataHolder(String data) {
            this.data = data;
        }
    }

    private static class DataHolderWrapper {
        final CustomTypeAdaptersTest.DataHolder wrappedData;

        public DataHolderWrapper(CustomTypeAdaptersTest.DataHolder data) {
            this.wrappedData = data;
        }
    }

    private static class DataHolderSerializer implements JsonSerializer<CustomTypeAdaptersTest.DataHolder> {
        @Override
        public JsonElement serialize(CustomTypeAdaptersTest.DataHolder src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("myData", src.data);
            return obj;
        }
    }

    private static class DataHolderDeserializer implements JsonDeserializer<CustomTypeAdaptersTest.DataHolder> {
        @Override
        public CustomTypeAdaptersTest.DataHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            JsonElement jsonElement = jsonObj.get("data");
            if ((jsonElement == null) || (jsonElement.isJsonNull())) {
                return new CustomTypeAdaptersTest.DataHolder(null);
            }
            return new CustomTypeAdaptersTest.DataHolder(jsonElement.getAsString());
        }
    }

    private static class DateTypeAdapter implements JsonDeserializer<Date> , JsonSerializer<Date> {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return typeOfT == (Date.class) ? new Date(json.getAsLong()) : new java.sql.Date(json.getAsLong());
        }

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime());
        }
    }
}

