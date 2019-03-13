package com.jsoniter;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.jsoniter.extra.GsonCompatibilityMode;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.TimeZone;
import junit.framework.TestCase;


public class TestGson extends TestCase {
    public static class TestObject1 {
        @SerializedName("field-1")
        public String field1;
    }

    public void test_SerializedName() {
        Gson gson = new Gson();
        TestGson.TestObject1 obj = gson.fromJson("{\"field-1\":\"hello\"}", TestGson.TestObject1.class);
        TestCase.assertEquals("hello", obj.field1);
        obj = JsonIterator.deserialize(new GsonCompatibilityMode.Builder().build(), "{\"field-1\":\"hello\"}", TestGson.TestObject1.class);
        TestCase.assertEquals("hello", obj.field1);
    }

    public static class TestObject2 {
        @Expose(deserialize = false)
        public String field1;
    }

    public void test_Expose() {
        // test if the iterator reuse will keep right config cache
        JsonIterator.deserialize(new GsonCompatibilityMode.Builder().build(), "{\"field-1\":\"hello\"}", TestGson.TestObject2.class);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        TestGson.TestObject2 obj = gson.fromJson("{\"field1\":\"hello\"}", TestGson.TestObject2.class);
        TestCase.assertNull(obj.field1);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().excludeFieldsWithoutExposeAnnotation().build();
        obj = JsonIterator.deserialize(config, "{\"field1\":\"hello\"}", TestGson.TestObject2.class);
        TestCase.assertNull(obj.field1);
    }

    // public void test_setDateFormat_no_op() {
    // TimeZone orig = TimeZone.getDefault();
    // try {
    // TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    // Gson gson = new GsonBuilder().create();
    // Date obj = gson.fromJson("\"Jan 1, 1970 12:00:00 AM\"", Date.class);
    // assertEquals(0, obj.getTime());
    // GsonCompatibilityMode config = new GsonCompatibilityMode.Builder()
    // .build();
    // obj = JsonIterator.deserialize(config, "\"Jan 1, 1970 12:00:00 AM\"", Date.class);
    // assertEquals(0, obj.getTime());
    // } finally {
    // TimeZone.setDefault(orig);
    // }
    // }
    public void test_setDateFormat_format() {
        TimeZone orig = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Gson gson = new GsonBuilder().setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z").create();
            Date obj = gson.fromJson("\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", Date.class);
            TestCase.assertEquals(0, obj.getTime());
            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z").build();
            obj = JsonIterator.deserialize(config, "\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", Date.class);
            TestCase.assertEquals(0, obj.getTime());
        } finally {
            TimeZone.setDefault(orig);
        }
    }

    public static class TestObject3 {
        public String field1;
    }

    public void test_setFieldNamingStrategy() {
        FieldNamingStrategy fieldNamingStrategy = new FieldNamingStrategy() {
            @Override
            public String translateName(Field f) {
                return "_" + (f.getName());
            }
        };
        Gson gson = new GsonBuilder().setFieldNamingStrategy(fieldNamingStrategy).create();
        TestGson.TestObject3 obj = gson.fromJson("{\"_field1\":\"hello\"}", TestGson.TestObject3.class);
        TestCase.assertEquals("hello", obj.field1);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setFieldNamingStrategy(fieldNamingStrategy).build();
        obj = JsonIterator.deserialize(config, "{\"_field1\":\"hello\"}", TestGson.TestObject3.class);
        TestCase.assertEquals("hello", obj.field1);
    }

    public void test_setFieldNamingPolicy() {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        TestGson.TestObject3 obj = gson.fromJson("{\"Field1\":\"hello\"}", TestGson.TestObject3.class);
        TestCase.assertEquals("hello", obj.field1);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).build();
        obj = JsonIterator.deserialize(config, "{\"Field1\":\"hello\"}", TestGson.TestObject3.class);
        TestCase.assertEquals("hello", obj.field1);
    }

    public static class TestObject5 {
        @Since(3.0)
        public String field1 = "";

        @Until(1.0)
        public String field2 = "";

        @Since(2.0)
        public String field3 = "";

        @Until(2.0)
        public String field4 = "";
    }

    public void test_setVersion() {
        Gson gson = new GsonBuilder().setVersion(2.0).create();
        TestGson.TestObject5 obj = gson.fromJson("{\"field1\":\"field1\",\"field2\":\"field2\",\"field3\":\"field3\",\"field4\":\"field4\"}", TestGson.TestObject5.class);
        TestCase.assertEquals("", obj.field1);
        TestCase.assertEquals("", obj.field2);
        TestCase.assertEquals("field3", obj.field3);
        TestCase.assertEquals("", obj.field4);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setVersion(2.0).build();
        obj = JsonIterator.deserialize(config, "{\"field1\":\"field1\",\"field2\":\"field2\",\"field3\":\"field3\",\"field4\":\"field4\"}", TestGson.TestObject5.class);
        TestCase.assertEquals("", obj.field1);
        TestCase.assertEquals("", obj.field2);
        TestCase.assertEquals("field3", obj.field3);
        TestCase.assertEquals("", obj.field4);
    }

    public void test_addDeserializationExclusionStrategy() {
        ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return !(f.getName().equals("field3"));
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(exclusionStrategy).create();
        TestGson.TestObject5 obj = gson.fromJson("{\"field1\":\"field1\",\"field2\":\"field2\",\"field3\":\"field3\",\"field4\":\"field4\"}", TestGson.TestObject5.class);
        TestCase.assertEquals("", obj.field1);
        TestCase.assertEquals("", obj.field2);
        TestCase.assertEquals("field3", obj.field3);
        TestCase.assertEquals("", obj.field4);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().addDeserializationExclusionStrategy(exclusionStrategy).build();
        obj = JsonIterator.deserialize(config, "{\"field1\":\"field1\",\"field2\":\"field2\",\"field3\":\"field3\",\"field4\":\"field4\"}", TestGson.TestObject5.class);
        TestCase.assertEquals("", obj.field1);
        TestCase.assertEquals("", obj.field2);
        TestCase.assertEquals("field3", obj.field3);
        TestCase.assertEquals("", obj.field4);
    }

    public void test_int_as_string() {
        Gson gson = new Gson();
        String str = gson.fromJson("1.1", String.class);
        TestCase.assertEquals("1.1", str);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        str = JsonIterator.deserialize(config, "1", String.class);
        TestCase.assertEquals("1", str);
    }

    public void test_bool_as_string() {
        Gson gson = new Gson();
        String str = gson.fromJson("true", String.class);
        TestCase.assertEquals("true", str);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        str = JsonIterator.deserialize(config, "true", String.class);
        TestCase.assertEquals("true", str);
    }

    public static class TestObject6 {
        public boolean field;
    }

    public void test_null_as_boolean() {
        Gson gson = new Gson();
        TestGson.TestObject6 obj = gson.fromJson("{\"field\":null}", TestGson.TestObject6.class);
        TestCase.assertFalse(obj.field);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        obj = JsonIterator.deserialize(config, "{\"field\":null}", TestGson.TestObject6.class);
        TestCase.assertFalse(obj.field);
    }

    public static class TestObject7 {
        public long field;
    }

    public void test_null_as_long() {
        Gson gson = new Gson();
        TestGson.TestObject7 obj = gson.fromJson("{\"field\":null}", TestGson.TestObject7.class);
        TestCase.assertEquals(0, obj.field);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        obj = JsonIterator.deserialize(config, "{\"field\":null}", TestGson.TestObject7.class);
        TestCase.assertEquals(0, obj.field);
    }

    public static class TestObject8 {
        public int field;
    }

    public void test_null_as_int() {
        Gson gson = new Gson();
        TestGson.TestObject8 obj = gson.fromJson("{\"field\":null}", TestGson.TestObject8.class);
        TestCase.assertEquals(0, obj.field);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        obj = JsonIterator.deserialize(config, "{\"field\":null}", TestGson.TestObject8.class);
        TestCase.assertEquals(0, obj.field);
    }

    public static class TestObject9 {
        public float field;
    }

    public void test_null_as_float() {
        Gson gson = new Gson();
        TestGson.TestObject9 obj = gson.fromJson("{\"field\":null}", TestGson.TestObject9.class);
        TestCase.assertEquals(0.0F, obj.field);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        obj = JsonIterator.deserialize(config, "{\"field\":null}", TestGson.TestObject9.class);
        TestCase.assertEquals(0.0F, obj.field);
    }

    public static class TestObject10 {
        public double field;
    }

    public void test_null_as_double() {
        Gson gson = new Gson();
        TestGson.TestObject10 obj = gson.fromJson("{\"field\":null}", TestGson.TestObject10.class);
        TestCase.assertEquals(0.0, obj.field);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        obj = JsonIterator.deserialize(config, "{\"field\":null}", TestGson.TestObject10.class);
        TestCase.assertEquals(0.0, obj.field);
    }
}

