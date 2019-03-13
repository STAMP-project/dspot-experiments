/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.bodyparser;


import IsInteger.KEY;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import ninja.Context;
import ninja.params.ControllerMethodInvokerTest;
import ninja.validation.Validation;
import org.hamcrest.CoreMatchers;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class BodyParserEnginePostTest {
    @Mock
    Context context;

    Validation validation;

    BodyParserEnginePost bodyParserEnginePost;

    @Test
    public void testBodyParser() {
        // some setup for this method:
        String dateString = "2014-10-10";
        String dateTimeString = "2014-10-10T20:09:10";
        Map<String, String[]> map = new HashMap<>();
        map.put("integerPrimitive", new String[]{ "1000" });
        map.put("integerObject", new String[]{ "2000" });
        map.put("longPrimitive", new String[]{ "3000" });
        map.put("longObject", new String[]{ "4000" });
        map.put("floatPrimitive", new String[]{ "1.234" });
        map.put("floatObject", new String[]{ "2.345" });
        map.put("doublePrimitive", new String[]{ "3.456" });
        map.put("doubleObject", new String[]{ "4.567" });
        map.put("string", new String[]{ "aString" });
        map.put("characterPrimitive", new String[]{ "a" });
        map.put("characterObject", new String[]{ "b" });
        map.put("date", new String[]{ dateString });
        map.put("timestamp", new String[]{ dateTimeString });
        map.put("somethingElseWhatShouldBeSkipped", new String[]{ "somethingElseWhatShouldBeSkipped" });
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObject testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObject.class);
        // and test:
        Assert.assertThat(testObject.integerPrimitive, CoreMatchers.equalTo(1000));
        Assert.assertThat(testObject.integerObject, CoreMatchers.equalTo(2000));
        Assert.assertThat(testObject.longPrimitive, CoreMatchers.equalTo(3000L));
        Assert.assertThat(testObject.longObject, CoreMatchers.equalTo(4000L));
        Assert.assertThat(testObject.floatPrimitive, CoreMatchers.equalTo(1.234F));
        Assert.assertThat(testObject.floatObject, CoreMatchers.equalTo(2.345F));
        Assert.assertThat(testObject.doublePrimitive, CoreMatchers.equalTo(3.456));
        Assert.assertThat(testObject.doubleObject, CoreMatchers.equalTo(4.567));
        Assert.assertThat(testObject.string, CoreMatchers.equalTo("aString"));
        Assert.assertThat(testObject.characterPrimitive, CoreMatchers.equalTo('a'));
        Assert.assertThat(testObject.characterObject, CoreMatchers.equalTo('b'));
        Assert.assertThat(testObject.date, CoreMatchers.equalTo(new LocalDateTime(dateString).toDate()));
        Assert.assertThat(testObject.timestamp, CoreMatchers.equalTo(new LocalDateTime(dateTimeString).toDate()));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void testBodyParserWithValidationErrors() {
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("integerPrimitive", new String[]{ "a" });
        map.put("integerObject", new String[]{ "b" });
        map.put("longPrimitive", new String[]{ "c" });
        map.put("longObject", new String[]{ "d" });
        map.put("floatPrimitive", new String[]{ "e" });
        map.put("floatObject", new String[]{ "f" });
        map.put("doublePrimitive", new String[]{ "g" });
        map.put("doubleObject", new String[]{ "h" });
        map.put("characterPrimitive", new String[]{ null });
        map.put("characterObject", new String[]{ null });
        map.put("date", new String[]{ "cc" });
        map.put("timestamp", new String[]{ "dd" });
        map.put("uuid", new String[]{ "ee" });
        map.put("somethingElseWhatShouldBeSkipped", new String[]{ "somethingElseWhatShouldBeSkipped" });
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObject testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObject.class);
        // and test:
        Assert.assertTrue(validation.hasViolations());
        assertViolation("integerPrimitive", KEY);
        Assert.assertThat(testObject.integerPrimitive, CoreMatchers.equalTo(0));
        assertViolation("integerObject", KEY);
        Assert.assertNull(testObject.integerObject);
        assertViolation("longPrimitive", KEY);
        Assert.assertThat(testObject.longPrimitive, CoreMatchers.equalTo(0L));
        assertViolation("longObject", KEY);
        Assert.assertNull(testObject.longObject);
        assertViolation("floatPrimitive", IsFloat.KEY);
        Assert.assertThat(testObject.floatPrimitive, CoreMatchers.equalTo(0.0F));
        assertViolation("floatObject", IsFloat.KEY);
        Assert.assertNull(testObject.floatObject);
        assertViolation("doublePrimitive", IsFloat.KEY);
        Assert.assertThat(testObject.doublePrimitive, CoreMatchers.equalTo(0.0));
        assertViolation("doubleObject", IsFloat.KEY);
        Assert.assertNull(testObject.doubleObject);
        assertViolation("date", IsDate.KEY);
        Assert.assertNull(testObject.date);
        assertViolation("timestamp", IsDate.KEY);
        Assert.assertNull(testObject.timestamp);
        assertViolation("uuid", IsUUID.KEY);
        Assert.assertNull(testObject.uuid);
        Assert.assertNull(testObject.string);
        Assert.assertThat(testObject.characterPrimitive, CoreMatchers.equalTo('\u0000'));
        Assert.assertNull(testObject.characterObject);
        Assert.assertNull(testObject.string);
    }

    @Test
    public void testBodyParserWhenThereIsAnUnsupportedFieldType() {
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("string", new String[]{ "aString" });
        map.put("iAmNotSupportedField", new String[]{ "iAmNotSupportedField" });
        map.put("longs", new String[]{ "1", "2" });
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObjectWithUnsupportedFields testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObjectWithUnsupportedFields.class);
        // and test:
        Assert.assertThat(testObject.string, CoreMatchers.equalTo("aString"));
        Assert.assertThat(testObject.iAmNotSupportedField, CoreMatchers.equalTo(null));
        Assert.assertThat(testObject.longs, CoreMatchers.equalTo(null));
    }

    @Test
    public void testBodyParserWithCollectionAndArray() {
        Map<String, String[]> map = new HashMap<>();
        map.put("integers", new String[]{ "1", "2" });
        map.put("strings", new String[]{ "hello", "world" });
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObjectWithArraysAndCollections testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObjectWithArraysAndCollections.class);
        // and test:
        Assert.assertThat(testObject.integers.length, CoreMatchers.equalTo(2));
        Assert.assertThat(testObject.integers[0], CoreMatchers.equalTo(1));
        Assert.assertThat(testObject.integers[1], CoreMatchers.equalTo(2));
        Assert.assertThat(testObject.strings.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(testObject.strings.get(0), CoreMatchers.equalTo("hello"));
        Assert.assertThat(testObject.strings.get(1), CoreMatchers.equalTo("world"));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void testBodyParserWithEnumerations() {
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("enum1", new String[]{ BodyParserEnginePostTest.MyEnum.VALUE_A.name() });
        map.put("enum2", new String[]{ new String("value_b") });
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObjectWithEnum testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObjectWithEnum.class);
        // and test:
        Assert.assertThat(testObject.enum1, CoreMatchers.equalTo(BodyParserEnginePostTest.MyEnum.VALUE_A));
        Assert.assertThat(testObject.enum2, CoreMatchers.equalTo(BodyParserEnginePostTest.MyEnum.VALUE_B));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void testBodyParserWithCustomNeedingInjectionParamParser() {
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("dep", new String[]{ "dep1" });
        map.put("depArray", new String[]{ "depArray1", "depArray2" });
        map.put("depList", new String[]{ "depList1", "depList2" });
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObjectWithCustomType testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObjectWithCustomType.class);
        // and test:
        Assert.assertThat(testObject.dep, CoreMatchers.equalTo(new ControllerMethodInvokerTest.Dep("hello_dep1")));
        Assert.assertNotNull(testObject.depArray);
        Assert.assertThat(testObject.depArray.length, CoreMatchers.equalTo(2));
        Assert.assertThat(testObject.depArray[0], CoreMatchers.equalTo(new ControllerMethodInvokerTest.Dep("hello_depArray1")));
        Assert.assertThat(testObject.depArray[1], CoreMatchers.equalTo(new ControllerMethodInvokerTest.Dep("hello_depArray2")));
        Assert.assertNotNull(testObject.depList);
        Assert.assertThat(testObject.depList.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(testObject.depList.get(0), CoreMatchers.equalTo(new ControllerMethodInvokerTest.Dep("hello_depList1")));
        Assert.assertThat(testObject.depList.get(1), CoreMatchers.equalTo(new ControllerMethodInvokerTest.Dep("hello_depList2")));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void testBodyParserWithInnerObjects() {
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("object1.integerPrimitive", new String[]{ "1000" });
        map.put("object1.integerObject", new String[]{ "2000" });
        map.put("object2.integerPrimitive", new String[]{ "3000" });
        map.put("object2.integerObject", new String[]{ "4000" });
        Mockito.when(context.getParameter("object1.integerPrimitive")).thenReturn("1000");
        Mockito.when(context.getParameter("object1.integerObject")).thenReturn("2000");
        Mockito.when(context.getParameter("object2.integerPrimitive")).thenReturn("3000");
        Mockito.when(context.getParameter("object2.integerObject")).thenReturn("4000");
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObjectWithInnerObjects testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObjectWithInnerObjects.class);
        // and test:
        Assert.assertNotNull(testObject.object1);
        Assert.assertThat(testObject.object1.integerPrimitive, CoreMatchers.equalTo(1000));
        Assert.assertThat(testObject.object1.integerObject, CoreMatchers.equalTo(2000));
        Assert.assertNotNull(testObject.object2);
        Assert.assertThat(testObject.object2.integerPrimitive, CoreMatchers.equalTo(3000));
        Assert.assertThat(testObject.object2.integerObject, CoreMatchers.equalTo(4000));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void testBodyParserWithEmptyInnerObjects() {
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("object1.integerPrimitive", new String[]{ "" });
        map.put("object1.integerObject", new String[]{ "" });
        map.put("object2.integerPrimitive", new String[]{ null });
        map.put("object2.integerObject", new String[]{ null });
        Mockito.when(context.getParameter("object1.integerPrimitive")).thenReturn("");
        Mockito.when(context.getParameter("object1.integerObject")).thenReturn("");
        Mockito.when(context.getParameter("object2.integerPrimitive")).thenReturn(null);
        Mockito.when(context.getParameter("object2.integerObject")).thenReturn(null);
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);
        // do
        BodyParserEnginePostTest.TestObjectWithInnerObjects testObject = bodyParserEnginePost.invoke(context, BodyParserEnginePostTest.TestObjectWithInnerObjects.class);
        // and test:
        Assert.assertNull(testObject.object1);
        Assert.assertNull(testObject.object2);
        // objects should not have been initialized for empty content
        Assert.assertFalse(validation.hasViolations());
    }

    public static class TestObject {
        public int integerPrimitive;

        public Integer integerObject;

        public long longPrimitive;

        public Long longObject;

        public float floatPrimitive;

        public Float floatObject;

        public double doublePrimitive;

        public Double doubleObject;

        public String string;

        public char characterPrimitive;

        public Character characterObject;

        public Date date;

        public Date timestamp;

        public UUID uuid;

        @NotNull
        public Object requiredObject;
    }

    public static class TestObjectWithUnsupportedFields {
        public StringBuffer iAmNotSupportedField;

        public String string;

        public long[] longs;
    }

    public static class TestObjectWithArraysAndCollections {
        public Integer[] integers;

        public List<String> strings;
    }

    public static enum MyEnum {

        VALUE_A,
        VALUE_B,
        VALUE_C;}

    public static class TestObjectWithEnum {
        public BodyParserEnginePostTest.MyEnum enum1;

        public BodyParserEnginePostTest.MyEnum enum2;
    }

    public static class TestObjectWithCustomType {
        public ControllerMethodInvokerTest.Dep dep;

        public ControllerMethodInvokerTest.Dep[] depArray;

        public List<ControllerMethodInvokerTest.Dep> depList;
    }

    public static class TestObjectWithInnerObjects {
        public BodyParserEnginePostTest.TestObject object1;

        public BodyParserEnginePostTest.TestObject object2;
    }
}

