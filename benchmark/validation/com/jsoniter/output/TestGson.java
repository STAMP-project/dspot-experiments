package com.jsoniter.output;


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
import com.jsoniter.spi.JsoniterSpi;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import junit.framework.TestCase;

import static EncodingMode.REFLECTION_MODE;


public class TestGson extends TestCase {
    public static class TestObject1 {
        @SerializedName("field-1")
        public String field1;
    }

    public void test_SerializedName_on_field() {
        Gson gson = new GsonBuilder().create();
        TestGson.TestObject1 obj = new TestGson.TestObject1();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        TestCase.assertEquals("{\"field-1\":\"hello\"}", output);
        output = JsonStream.serialize(new GsonCompatibilityMode.Builder().build(), obj);
        TestCase.assertEquals("{\"field-1\":\"hello\"}", output);
    }

    public static class TestObject2 {
        @Expose(serialize = false)
        public String field1;
    }

    public void test_Expose() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        TestGson.TestObject2 obj = new TestGson.TestObject2();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        TestCase.assertEquals("{}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().excludeFieldsWithoutExposeAnnotation().build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{}", output);
    }

    public static class TestObject3 {
        public String getField1() {
            return "hello";
        }
    }

    public void test_getter_should_be_ignored() {
        Gson gson = new GsonBuilder().create();
        TestGson.TestObject3 obj = new TestGson.TestObject3();
        String output = gson.toJson(obj);
        TestCase.assertEquals("{}", output);
        output = JsonStream.serialize(new GsonCompatibilityMode.Builder().build(), obj);
        TestCase.assertEquals("{}", output);
    }

    public static class TestObject4 {
        public String field1;
    }

    public void test_excludeFieldsWithoutExposeAnnotation() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        TestGson.TestObject4 obj = new TestGson.TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        TestCase.assertEquals("{}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().excludeFieldsWithoutExposeAnnotation().build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{}", output);
    }

    public static class TestObject5 {
        public String field1;

        public int field2;
    }

    public void test_serializeNulls() {
        TestGson.TestObject5 obj = new TestGson.TestObject5();
        Gson gson = new GsonBuilder().create();
        String output = gson.toJson(obj);
        TestCase.assertEquals("{\"field2\":0}", output);
        gson = new GsonBuilder().serializeNulls().create();
        output = gson.toJson(obj);
        TestCase.assertEquals("{\"field1\":null,\"field2\":0}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{\"field2\":0}", output);
        config = new GsonCompatibilityMode.Builder().serializeNulls().build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{\"field1\":null,\"field2\":0}", output);
    }

    public void test_setDateFormat_with_style() {
        TimeZone orig = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.LONG, DateFormat.LONG).create();
            String output = gson.toJson(new Date(0));
            TestCase.assertEquals("\"January 1, 1970 12:00:00 AM UTC\"", output);
            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setDateFormat(DateFormat.LONG, DateFormat.LONG).build();
            output = JsonStream.serialize(config, new Date(0));
            TestCase.assertEquals("\"January 1, 1970 12:00:00 AM UTC\"", output);
        } finally {
            TimeZone.setDefault(orig);
        }
    }

    public void test_setDateFormat_with_format() {
        TimeZone orig = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Gson gson = new GsonBuilder().setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z").create();
            String output = gson.toJson(new Date(0));
            TestCase.assertEquals("\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", output);
            GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setDateFormat("EEE, MMM d, yyyy hh:mm:ss a z").build();
            output = JsonStream.serialize(config, new Date(0));
            TestCase.assertEquals("\"Thu, Jan 1, 1970 12:00:00 AM UTC\"", output);
        } finally {
            TimeZone.setDefault(orig);
        }
    }

    public void test_setFieldNamingStrategy() {
        FieldNamingStrategy fieldNamingStrategy = new FieldNamingStrategy() {
            @Override
            public String translateName(Field f) {
                return "_" + (f.getName());
            }
        };
        Gson gson = new GsonBuilder().setFieldNamingStrategy(fieldNamingStrategy).create();
        TestGson.TestObject4 obj = new TestGson.TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        TestCase.assertEquals("{\"_field1\":\"hello\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setFieldNamingStrategy(fieldNamingStrategy).build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{\"_field1\":\"hello\"}", output);
    }

    public void test_setFieldNamingPolicy() {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        TestGson.TestObject4 obj = new TestGson.TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        TestCase.assertEquals("{\"Field1\":\"hello\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{\"Field1\":\"hello\"}", output);
    }

    public void test_setPrettyPrinting() {
        if ((JsoniterSpi.getCurrentConfig().encodingMode()) != (REFLECTION_MODE)) {
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TestGson.TestObject4 obj = new TestGson.TestObject4();
        obj.field1 = "hello";
        String output = gson.toJson(obj);
        TestCase.assertEquals(("{\n" + ("  \"field1\": \"hello\"\n" + "}")), output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setPrettyPrinting().build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals(("{\n" + ("  \"field1\": \"hello\"\n" + "}")), output);
    }

    public void test_disableHtmlEscaping_off() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String output = gson.toJson("<html>??</html>");
        TestCase.assertEquals("\"<html>\u4e2d\u6587</html>\"", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().disableHtmlEscaping().build();
        output = JsonStream.serialize(config, "<html>??</html>");
        TestCase.assertEquals("\"<html>\u4e2d\u6587</html>\"", output);
    }

    public void test_disableHtmlEscaping_on() {
        Gson gson = new GsonBuilder().create();
        String output = gson.toJson("<html>&nbsp;</html>");
        TestCase.assertEquals("\"\\u003chtml\\u003e\\u0026nbsp;\\u003c/html\\u003e\"", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().build();
        output = JsonStream.serialize(config, "<html>&nbsp;</html>");
        TestCase.assertEquals("\"\\u003chtml\\u003e\\u0026nbsp;\\u003c/html\\u003e\"", output);
    }

    public static class TestObject6 {
        @Since(3.0)
        public String field1 = "field1";

        @Until(1.0)
        public String field2 = "field2";

        @Since(2.0)
        public String field3 = "field3";

        @Until(2.0)
        public String field4 = "field4";
    }

    public void test_setVersion() {
        TestGson.TestObject6 obj = new TestGson.TestObject6();
        Gson gson = new GsonBuilder().setVersion(2.0).create();
        String output = gson.toJson(obj);
        TestCase.assertEquals("{\"field3\":\"field3\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().setVersion(2.0).build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{\"field3\":\"field3\"}", output);
    }

    public void test_addSerializationExclusionStrategy() {
        TestGson.TestObject6 obj = new TestGson.TestObject6();
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
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(exclusionStrategy).create();
        String output = gson.toJson(obj);
        TestCase.assertEquals("{\"field3\":\"field3\"}", output);
        GsonCompatibilityMode config = new GsonCompatibilityMode.Builder().addSerializationExclusionStrategy(exclusionStrategy).build();
        output = JsonStream.serialize(config, obj);
        TestCase.assertEquals("{\"field3\":\"field3\"}", output);
    }

    private static class TestObject {
        private String test;
    }

    public void test_surrogate() {
        GsonCompatibilityMode gsonConfig = new GsonCompatibilityMode.Builder().disableHtmlEscaping().build();
        String input = "{\"test\":\"lorem-\ud83d\udc44\ud83d\udc40\"}";
        TestGson.TestObject testObject = new Gson().fromJson(input, TestGson.TestObject.class);
        System.out.println(("Gson: " + (new Gson().toJson(testObject))));
        System.out.println(("jsoniter: " + (JsonStream.serialize(gsonConfig, testObject))));
    }
}

