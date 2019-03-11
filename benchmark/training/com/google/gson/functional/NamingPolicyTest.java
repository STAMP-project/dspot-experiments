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


import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.common.TestTypes;
import java.lang.reflect.Field;
import junit.framework.TestCase;


/**
 * Functional tests for naming policies.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class NamingPolicyTest extends TestCase {
    private GsonBuilder builder;

    public void testGsonWithNonDefaultFieldNamingPolicySerialization() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
        TestCase.assertEquals((("{\"SomeConstantStringInstanceField\":\"" + (target.someConstantStringInstanceField)) + "\"}"), gson.toJson(target));
    }

    public void testGsonWithNonDefaultFieldNamingPolicyDeserialiation() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String target = "{\"SomeConstantStringInstanceField\":\"someValue\"}";
        TestTypes.StringWrapper deserializedObject = gson.fromJson(target, TestTypes.StringWrapper.class);
        TestCase.assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
    }

    public void testGsonWithLowerCaseDashPolicySerialization() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
        TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
        TestCase.assertEquals((("{\"some-constant-string-instance-field\":\"" + (target.someConstantStringInstanceField)) + "\"}"), gson.toJson(target));
    }

    public void testGsonWithLowerCaseDotPolicySerialization() {
        Gson gson = builder.setFieldNamingPolicy(LOWER_CASE_WITH_DOTS).create();
        TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
        TestCase.assertEquals((("{\"some.constant.string.instance.field\":\"" + (target.someConstantStringInstanceField)) + "\"}"), gson.toJson(target));
    }

    public void testGsonWithLowerCaseDotPolicyDeserialiation() {
        Gson gson = builder.setFieldNamingPolicy(LOWER_CASE_WITH_DOTS).create();
        String target = "{\"some.constant.string.instance.field\":\"someValue\"}";
        TestTypes.StringWrapper deserializedObject = gson.fromJson(target, TestTypes.StringWrapper.class);
        TestCase.assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
    }

    public void testGsonWithLowerCaseDashPolicyDeserialiation() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
        String target = "{\"some-constant-string-instance-field\":\"someValue\"}";
        TestTypes.StringWrapper deserializedObject = gson.fromJson(target, TestTypes.StringWrapper.class);
        TestCase.assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
    }

    public void testGsonWithLowerCaseUnderscorePolicySerialization() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
        TestCase.assertEquals((("{\"some_constant_string_instance_field\":\"" + (target.someConstantStringInstanceField)) + "\"}"), gson.toJson(target));
    }

    public void testGsonWithLowerCaseUnderscorePolicyDeserialiation() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        String target = "{\"some_constant_string_instance_field\":\"someValue\"}";
        TestTypes.StringWrapper deserializedObject = gson.fromJson(target, TestTypes.StringWrapper.class);
        TestCase.assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
    }

    public void testGsonWithSerializedNameFieldNamingPolicySerialization() {
        Gson gson = builder.create();
        TestTypes.ClassWithSerializedNameFields expected = new TestTypes.ClassWithSerializedNameFields(5, 6);
        String actual = gson.toJson(expected);
        TestCase.assertEquals(expected.getExpectedJson(), actual);
    }

    public void testGsonWithSerializedNameFieldNamingPolicyDeserialization() {
        Gson gson = builder.create();
        TestTypes.ClassWithSerializedNameFields expected = new TestTypes.ClassWithSerializedNameFields(5, 7);
        TestTypes.ClassWithSerializedNameFields actual = gson.fromJson(expected.getExpectedJson(), TestTypes.ClassWithSerializedNameFields.class);
        TestCase.assertEquals(expected.f, actual.f);
    }

    public void testGsonDuplicateNameUsingSerializedNameFieldNamingPolicySerialization() {
        Gson gson = builder.create();
        try {
            NamingPolicyTest.ClassWithDuplicateFields target = new NamingPolicyTest.ClassWithDuplicateFields(10);
            gson.toJson(target);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testGsonWithUpperCamelCaseSpacesPolicySerialiation() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES).create();
        TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
        TestCase.assertEquals((("{\"Some Constant String Instance Field\":\"" + (target.someConstantStringInstanceField)) + "\"}"), gson.toJson(target));
    }

    public void testGsonWithUpperCamelCaseSpacesPolicyDeserialiation() {
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES).create();
        String target = "{\"Some Constant String Instance Field\":\"someValue\"}";
        TestTypes.StringWrapper deserializedObject = gson.fromJson(target, TestTypes.StringWrapper.class);
        TestCase.assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
    }

    public void testDeprecatedNamingStrategy() throws Exception {
        Gson gson = builder.setFieldNamingStrategy(new NamingPolicyTest.UpperCaseNamingStrategy()).create();
        NamingPolicyTest.ClassWithDuplicateFields target = new NamingPolicyTest.ClassWithDuplicateFields(10);
        String actual = gson.toJson(target);
        TestCase.assertEquals("{\"A\":10}", actual);
    }

    public void testComplexFieldNameStrategy() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(new NamingPolicyTest.ClassWithComplexFieldName(10));
        String escapedFieldName = "@value\\\"_s$\\\\";
        TestCase.assertEquals((("{\"" + escapedFieldName) + "\":10}"), json);
        NamingPolicyTest.ClassWithComplexFieldName obj = gson.fromJson(json, NamingPolicyTest.ClassWithComplexFieldName.class);
        TestCase.assertEquals(10, obj.value);
    }

    /**
     * http://code.google.com/p/google-gson/issues/detail?id=349
     */
    public void testAtSignInSerializedName() {
        TestCase.assertEquals("{\"@foo\":\"bar\"}", new Gson().toJson(new NamingPolicyTest.AtName()));
    }

    static final class AtName {
        @SerializedName("@foo")
        String f = "bar";
    }

    private static final class UpperCaseNamingStrategy implements FieldNamingStrategy {
        @Override
        public String translateName(Field f) {
            return f.getName().toUpperCase();
        }
    }

    @SuppressWarnings("unused")
    private static class ClassWithDuplicateFields {
        public Integer a;

        @SerializedName("a")
        public Double b;

        public ClassWithDuplicateFields(Integer a) {
            this(a, null);
        }

        public ClassWithDuplicateFields(Double b) {
            this(null, b);
        }

        public ClassWithDuplicateFields(Integer a, Double b) {
            this.a = a;
            this.b = b;
        }
    }

    private static class ClassWithComplexFieldName {
        @SerializedName("@value\"_s$\\")
        public final long value;

        ClassWithComplexFieldName(long value) {
            this.value = value;
        }
    }
}

