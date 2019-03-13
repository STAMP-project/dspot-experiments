/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.reflect;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests ConstructorUtils
 */
public class ConstructorUtilsTest {
    public static class TestBean {
        private final String toString;

        final String[] varArgs;

        public TestBean() {
            toString = "()";
            varArgs = null;
        }

        public TestBean(final int i) {
            toString = "(int)";
            varArgs = null;
        }

        public TestBean(final Integer i) {
            toString = "(Integer)";
            varArgs = null;
        }

        public TestBean(final double d) {
            toString = "(double)";
            varArgs = null;
        }

        public TestBean(final String s) {
            toString = "(String)";
            varArgs = null;
        }

        public TestBean(final Object o) {
            toString = "(Object)";
            varArgs = null;
        }

        public TestBean(final String... s) {
            toString = "(String...)";
            varArgs = s;
        }

        public TestBean(final ConstructorUtilsTest.BaseClass bc, final String... s) {
            toString = "(BaseClass, String...)";
            varArgs = s;
        }

        public TestBean(final Integer i, final String... s) {
            toString = "(Integer, String...)";
            varArgs = s;
        }

        public TestBean(final Integer first, final int... args) {
            toString = "(Integer, String...)";
            varArgs = new String[args.length];
            for (int i = 0; i < (args.length); ++i) {
                varArgs[i] = Integer.toString(args[i]);
            }
        }

        @Override
        public String toString() {
            return toString;
        }

        void verify(final String str, final String[] args) {
            Assertions.assertEquals(str, toString);
            Assertions.assertArrayEquals(args, varArgs);
        }
    }

    private static class BaseClass {}

    private static class SubClass extends ConstructorUtilsTest.BaseClass {}

    static class PrivateClass {
        @SuppressWarnings("unused")
        public PrivateClass() {
        }

        @SuppressWarnings("unused")
        public static class PublicInnerClass {
            public PublicInnerClass() {
            }
        }
    }

    private final Map<Class<?>, Class<?>[]> classCache;

    public ConstructorUtilsTest() {
        classCache = new HashMap<>();
    }

    @Test
    public void testConstructor() throws Exception {
        Assertions.assertNotNull(MethodUtils.class.newInstance());
    }

    @Test
    public void testInvokeConstructor() throws Exception {
        Assertions.assertEquals("()", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))).toString());
        Assertions.assertEquals("()", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, ((Object[]) (null))).toString());
        Assertions.assertEquals("()", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class).toString());
        Assertions.assertEquals("(String)", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, "").toString());
        Assertions.assertEquals("(Object)", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, new Object()).toString());
        Assertions.assertEquals("(Object)", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, Boolean.TRUE).toString());
        Assertions.assertEquals("(Integer)", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.INTEGER_ONE).toString());
        Assertions.assertEquals("(int)", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.BYTE_ONE).toString());
        Assertions.assertEquals("(double)", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.LONG_ONE).toString());
        Assertions.assertEquals("(double)", ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.DOUBLE_ONE).toString());
        ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.INTEGER_ONE).verify("(Integer)", null);
        ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, "a", "b").verify("(String...)", new String[]{ "a", "b" });
        ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.INTEGER_ONE, "a", "b").verify("(Integer, String...)", new String[]{ "a", "b" });
        ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, new ConstructorUtilsTest.SubClass(), new String[]{ "a", "b" }).verify("(BaseClass, String...)", new String[]{ "a", "b" });
    }

    @Test
    public void testInvokeExactConstructor() throws Exception {
        Assertions.assertEquals("()", ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))).toString());
        Assertions.assertEquals("()", ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, ((Object[]) (null))).toString());
        Assertions.assertEquals("(String)", ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, "").toString());
        Assertions.assertEquals("(Object)", ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, new Object()).toString());
        Assertions.assertEquals("(Integer)", ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.INTEGER_ONE).toString());
        Assertions.assertEquals("(double)", ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, new Object[]{ NumberUtils.DOUBLE_ONE }, new Class[]{ Double.TYPE }).toString());
        Assertions.assertThrows(NoSuchMethodException.class, () -> ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.BYTE_ONE));
        Assertions.assertThrows(NoSuchMethodException.class, () -> ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, NumberUtils.LONG_ONE));
        Assertions.assertThrows(NoSuchMethodException.class, () -> ConstructorUtils.invokeExactConstructor(ConstructorUtilsTest.TestBean.class, Boolean.TRUE));
    }

    @Test
    public void testGetAccessibleConstructor() throws Exception {
        Assertions.assertNotNull(ConstructorUtils.getAccessibleConstructor(Object.class.getConstructor(ArrayUtils.EMPTY_CLASS_ARRAY)));
        Assertions.assertNull(ConstructorUtils.getAccessibleConstructor(ConstructorUtilsTest.PrivateClass.class.getConstructor(ArrayUtils.EMPTY_CLASS_ARRAY)));
        Assertions.assertNull(ConstructorUtils.getAccessibleConstructor(ConstructorUtilsTest.PrivateClass.PublicInnerClass.class));
    }

    @Test
    public void testGetAccessibleConstructorFromDescription() {
        Assertions.assertNotNull(ConstructorUtils.getAccessibleConstructor(Object.class, ArrayUtils.EMPTY_CLASS_ARRAY));
        Assertions.assertNull(ConstructorUtils.getAccessibleConstructor(ConstructorUtilsTest.PrivateClass.class, ArrayUtils.EMPTY_CLASS_ARRAY));
    }

    @Test
    public void testGetMatchingAccessibleMethod() {
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY);
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, null, ArrayUtils.EMPTY_CLASS_ARRAY);
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(String.class), singletonArray(String.class));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Object.class), singletonArray(Object.class));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Boolean.class), singletonArray(Object.class));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Byte.class), singletonArray(Integer.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Byte.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Short.class), singletonArray(Integer.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Short.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Character.class), singletonArray(Integer.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Character.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Integer.class), singletonArray(Integer.class));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Integer.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Long.class), singletonArray(Double.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Long.TYPE), singletonArray(Double.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Float.class), singletonArray(Double.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Float.TYPE), singletonArray(Double.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Double.class), singletonArray(Double.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, singletonArray(Double.TYPE), singletonArray(Double.TYPE));
        expectMatchingAccessibleConstructorParameterTypes(ConstructorUtilsTest.TestBean.class, new Class<?>[]{ ConstructorUtilsTest.SubClass.class, String[].class }, new Class<?>[]{ ConstructorUtilsTest.BaseClass.class, String[].class });
    }

    @Test
    public void testNullArgument() {
        expectMatchingAccessibleConstructorParameterTypes(MutableObject.class, singletonArray(null), singletonArray(Object.class));
    }

    @Test
    public void testVarArgsUnboxing() throws Exception {
        final ConstructorUtilsTest.TestBean testBean = ConstructorUtils.invokeConstructor(ConstructorUtilsTest.TestBean.class, Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
        Assertions.assertArrayEquals(new String[]{ "2", "3" }, testBean.varArgs);
    }
}

