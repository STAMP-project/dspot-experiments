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
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.Expose;
import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Unit tests for the regarding functional "@Expose" type tests.
 *
 * @author Joel Leitch
 */
public class ExposeFieldsTest extends TestCase {
    private Gson gson;

    public void testNullExposeFieldSerialization() throws Exception {
        ExposeFieldsTest.ClassWithExposedFields object = new ExposeFieldsTest.ClassWithExposedFields(null, 1);
        String json = gson.toJson(object);
        TestCase.assertEquals(object.getExpectedJson(), json);
    }

    public void testArrayWithOneNullExposeFieldObjectSerialization() throws Exception {
        ExposeFieldsTest.ClassWithExposedFields object1 = new ExposeFieldsTest.ClassWithExposedFields(1, 1);
        ExposeFieldsTest.ClassWithExposedFields object2 = new ExposeFieldsTest.ClassWithExposedFields(null, 1);
        ExposeFieldsTest.ClassWithExposedFields object3 = new ExposeFieldsTest.ClassWithExposedFields(2, 2);
        ExposeFieldsTest.ClassWithExposedFields[] objects = new ExposeFieldsTest.ClassWithExposedFields[]{ object1, object2, object3 };
        String json = gson.toJson(objects);
        String expected = new StringBuilder().append('[').append(object1.getExpectedJson()).append(',').append(object2.getExpectedJson()).append(',').append(object3.getExpectedJson()).append(']').toString();
        TestCase.assertEquals(expected, json);
    }

    public void testExposeAnnotationSerialization() throws Exception {
        ExposeFieldsTest.ClassWithExposedFields target = new ExposeFieldsTest.ClassWithExposedFields(1, 2);
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testExposeAnnotationDeserialization() throws Exception {
        String json = "{a:3,b:4,d:20.0}";
        ExposeFieldsTest.ClassWithExposedFields target = gson.fromJson(json, ExposeFieldsTest.ClassWithExposedFields.class);
        TestCase.assertEquals(3, ((int) (target.a)));
        TestCase.assertNull(target.b);
        TestCase.assertFalse(((target.d) == 20));
    }

    public void testNoExposedFieldSerialization() throws Exception {
        ExposeFieldsTest.ClassWithNoExposedFields obj = new ExposeFieldsTest.ClassWithNoExposedFields();
        String json = gson.toJson(obj);
        TestCase.assertEquals("{}", json);
    }

    public void testNoExposedFieldDeserialization() throws Exception {
        String json = "{a:4,b:5}";
        ExposeFieldsTest.ClassWithNoExposedFields obj = gson.fromJson(json, ExposeFieldsTest.ClassWithNoExposedFields.class);
        TestCase.assertEquals(0, obj.a);
        TestCase.assertEquals(1, obj.b);
    }

    public void testExposedInterfaceFieldSerialization() throws Exception {
        String expected = "{\"interfaceField\":{}}";
        ExposeFieldsTest.ClassWithInterfaceField target = new ExposeFieldsTest.ClassWithInterfaceField(new ExposeFieldsTest.SomeObject());
        String actual = gson.toJson(target);
        TestCase.assertEquals(expected, actual);
    }

    public void testExposedInterfaceFieldDeserialization() throws Exception {
        String json = "{\"interfaceField\":{}}";
        ExposeFieldsTest.ClassWithInterfaceField obj = gson.fromJson(json, ExposeFieldsTest.ClassWithInterfaceField.class);
        TestCase.assertNotNull(obj.interfaceField);
    }

    private static class ClassWithExposedFields {
        @Expose
        private final Integer a;

        private final Integer b;

        @Expose(serialize = false)
        final long c;

        @Expose(deserialize = false)
        final double d;

        @Expose(serialize = false, deserialize = false)
        final char e;

        public ClassWithExposedFields(Integer a, Integer b) {
            this(a, b, 1L, 2.0, 'a');
        }

        public ClassWithExposedFields(Integer a, Integer b, long c, double d, char e) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
        }

        public String getExpectedJson() {
            StringBuilder sb = new StringBuilder("{");
            if ((a) != null) {
                sb.append("\"a\":").append(a).append(",");
            }
            sb.append("\"d\":").append(d);
            sb.append("}");
            return sb.toString();
        }
    }

    private static class ClassWithNoExposedFields {
        private final int a = 0;

        private final int b = 1;
    }

    // Empty interface
    private static interface SomeInterface {}

    // Do nothing
    private static class SomeObject implements ExposeFieldsTest.SomeInterface {}

    private static class SomeInterfaceInstanceCreator implements InstanceCreator<ExposeFieldsTest.SomeInterface> {
        @Override
        public ExposeFieldsTest.SomeInterface createInstance(Type type) {
            return new ExposeFieldsTest.SomeObject();
        }
    }

    private static class ClassWithInterfaceField {
        @Expose
        private final ExposeFieldsTest.SomeInterface interfaceField;

        public ClassWithInterfaceField(ExposeFieldsTest.SomeInterface interfaceField) {
            this.interfaceField = interfaceField;
        }
    }
}

