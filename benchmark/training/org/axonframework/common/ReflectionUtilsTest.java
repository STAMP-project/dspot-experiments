/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 * @since 0.7
 */
public class ReflectionUtilsTest {
    @Test
    public void testFindFieldsInClass() {
        Iterable<Field> actualFields = ReflectionUtils.ReflectionUtils.fieldsOf(ReflectionUtilsTest.SomeSubType.class);
        int t = 0;
        for (Field actual : actualFields) {
            if (actual.isSynthetic()) {
                // this test is probably running with ByteCode manipulation. We ignore synthetic fields.
                continue;
            }
            switch (t++) {
                case 0 :
                    Assert.assertEquals("field3", actual.getName());
                    break;
                case 1 :
                case 2 :
                    Assert.assertTrue(((("Expected either field1 or field2, but got " + (actual.getName())) + " declared in ") + (actual.getDeclaringClass().getName())), (("field1".equals(actual.getName())) || ("field2".equals(actual.getName()))));
                    break;
            }
        }
        Assert.assertTrue((t >= 2));
    }

    @Test
    public void testFindMethodsInClass() {
        Iterable<Method> actualMethods = ReflectionUtils.ReflectionUtils.methodsOf(ReflectionUtilsTest.SomeSubType.class);
        int t = 0;
        for (Method actual : actualMethods) {
            if (actual.isSynthetic()) {
                // this test is probably running with bytecode manipulation. We ignore synthetic methods.
                continue;
            }
            switch (t++) {
                case 0 :
                    Assert.assertEquals("getField3", actual.getName());
                    break;
                case 1 :
                    Assert.assertEquals("getField3", actual.getName());
                    Assert.assertEquals("SomeSubInterface", actual.getDeclaringClass().getSimpleName());
                    break;
                case 2 :
                case 3 :
                    Assert.assertTrue(((("Expected either getField1 or getField2, but got " + (actual.getName())) + " declared in ") + (actual.getDeclaringClass().getName())), (("getField1".equals(actual.getName())) || ("getField2".equals(actual.getName()))));
                    break;
                case 4 :
                    Assert.assertEquals("SomeInterface", actual.getDeclaringClass().getSimpleName());
                    break;
            }
        }
        Assert.assertTrue((t >= 4));
    }

    @Test
    public void testGetFieldValue() throws NoSuchFieldException {
        Object value = ReflectionUtils.ReflectionUtils.getFieldValue(ReflectionUtilsTest.SomeType.class.getDeclaredField("field1"), new ReflectionUtilsTest.SomeSubType());
        Assert.assertEquals("field1", value);
    }

    @Test
    public void testSetFieldValue() throws Exception {
        int expectedFieldValue = 4;
        ReflectionUtilsTest.SomeSubType testObject = new ReflectionUtilsTest.SomeSubType();
        ReflectionUtils.ReflectionUtils.setFieldValue(ReflectionUtilsTest.SomeSubType.class.getDeclaredField("field3"), testObject, expectedFieldValue);
        Assert.assertEquals(expectedFieldValue, testObject.getField3());
    }

    @Test
    public void testIsAccessible() throws NoSuchFieldException {
        Field field1 = ReflectionUtilsTest.SomeType.class.getDeclaredField("field1");
        Field field2 = ReflectionUtilsTest.SomeType.class.getDeclaredField("field2");
        Field field3 = ReflectionUtilsTest.SomeSubType.class.getDeclaredField("field3");
        Assert.assertFalse(ReflectionUtils.ReflectionUtils.isAccessible(field1));
        Assert.assertFalse(ReflectionUtils.ReflectionUtils.isAccessible(field2));
        Assert.assertTrue(ReflectionUtils.ReflectionUtils.isAccessible(field3));
    }

    @Test
    public void testExplicitlyUnequal_NullValues() {
        Assert.assertFalse(explicitlyUnequal(null, null));
        Assert.assertTrue(explicitlyUnequal(null, ""));
        Assert.assertTrue(explicitlyUnequal("", null));
    }

    @Test
    public void testHasEqualsMethod() {
        Assert.assertTrue(hasEqualsMethod(String.class));
        Assert.assertTrue(hasEqualsMethod(ArrayList.class));
        Assert.assertFalse(hasEqualsMethod(ReflectionUtilsTest.SomeType.class));
    }

    @SuppressWarnings("RedundantStringConstructorCall")
    @Test
    public void testExplicitlyUnequal_ComparableValues() {
        Assert.assertFalse(explicitlyUnequal("value", new String("value")));
        Assert.assertTrue(explicitlyUnequal("value1", "value2"));
    }

    @Test
    public void testExplicitlyUnequal_OverridesEqualsMethod() {
        Assert.assertFalse(explicitlyUnequal(Collections.singletonList("value"), Collections.singletonList("value")));
        Assert.assertTrue(explicitlyUnequal(Collections.singletonList("value1"), Collections.singletonList("value")));
    }

    @Test
    public void testExplicitlyUnequal_NoEqualsOrComparable() {
        Assert.assertFalse(explicitlyUnequal(new ReflectionUtilsTest.SomeType(), new ReflectionUtilsTest.SomeType()));
    }

    @Test
    public void testResolvePrimitiveWrapperTypeForLong() {
        Assert.assertEquals(Long.class, resolvePrimitiveWrapperType(long.class));
    }

    private static class SomeType implements ReflectionUtilsTest.SomeInterface {
        private String field1 = "field1";

        private String field2 = "field2";

        @Override
        public String getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }
    }

    public interface SomeInterface {
        String getField1();
    }

    public interface SomeSubInterface {
        int getField3();
    }

    public static class SomeSubType extends ReflectionUtilsTest.SomeType implements ReflectionUtilsTest.SomeSubInterface {
        public int field3 = 3;

        @Override
        public int getField3() {
            return field3;
        }
    }

    public static class ContainsCollectionsType extends ReflectionUtilsTest.SomeType {
        private List<String> listOfStrings;

        private Map<String, String> mapOfStringToString;

        private Set<String> setOfStrings;

        public ContainsCollectionsType(List<String> listOfStrings, Map<String, String> mapOfStringToString, Set<String> setOfStrings) {
            this.listOfStrings = listOfStrings;
            this.mapOfStringToString = mapOfStringToString;
            this.setOfStrings = setOfStrings;
        }

        public List<String> getListOfStrings() {
            return listOfStrings;
        }

        public Map<String, String> getMapOfStringToString() {
            return mapOfStringToString;
        }

        public Set<String> getSetOfStrings() {
            return setOfStrings;
        }
    }
}

