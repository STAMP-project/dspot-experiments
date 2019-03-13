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


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.reflect.testbed.Annotated;
import org.apache.commons.lang3.reflect.testbed.GenericConsumer;
import org.apache.commons.lang3.reflect.testbed.GenericParent;
import org.apache.commons.lang3.reflect.testbed.PublicChild;
import org.apache.commons.lang3.reflect.testbed.StringParameterizedChild;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.apache.commons.lang3.ClassUtils.Interfaces.EXCLUDE;
import static org.apache.commons.lang3.ClassUtils.Interfaces.INCLUDE;


/**
 * Unit tests MethodUtils
 */
public class MethodUtilsTest {
    private interface PrivateInterface {}

    static class TestBeanWithInterfaces implements MethodUtilsTest.PrivateInterface {
        public String foo() {
            return "foo()";
        }
    }

    public static class TestBean {
        public static String bar() {
            return "bar()";
        }

        public static String bar(final int i) {
            return "bar(int)";
        }

        public static String bar(final Integer i) {
            return "bar(Integer)";
        }

        public static String bar(final double d) {
            return "bar(double)";
        }

        public static String bar(final String s) {
            return "bar(String)";
        }

        public static String bar(final Object o) {
            return "bar(Object)";
        }

        public static String bar(final String... s) {
            return "bar(String...)";
        }

        public static String bar(final long... s) {
            return "bar(long...)";
        }

        public static String bar(final Integer i, final String... s) {
            return "bar(int, String...)";
        }

        public static void oneParameterStatic(final String s) {
            // empty
        }

        @SuppressWarnings("unused")
        private void privateStuff() {
        }

        @SuppressWarnings("unused")
        private String privateStringStuff() {
            return "privateStringStuff()";
        }

        @SuppressWarnings("unused")
        private String privateStringStuff(final int i) {
            return "privateStringStuff(int)";
        }

        @SuppressWarnings("unused")
        private String privateStringStuff(final Integer i) {
            return "privateStringStuff(Integer)";
        }

        @SuppressWarnings("unused")
        private String privateStringStuff(final double d) {
            return "privateStringStuff(double)";
        }

        @SuppressWarnings("unused")
        private String privateStringStuff(final String s) {
            return "privateStringStuff(String)";
        }

        @SuppressWarnings("unused")
        private String privateStringStuff(final Object s) {
            return "privateStringStuff(Object)";
        }

        public String foo() {
            return "foo()";
        }

        public String foo(final int i) {
            return "foo(int)";
        }

        public String foo(final Integer i) {
            return "foo(Integer)";
        }

        public String foo(final double d) {
            return "foo(double)";
        }

        public String foo(final long l) {
            return "foo(long)";
        }

        public String foo(final String s) {
            return "foo(String)";
        }

        public String foo(final Object o) {
            return "foo(Object)";
        }

        public String foo(final String... s) {
            return "foo(String...)";
        }

        public String foo(final long... l) {
            return "foo(long...)";
        }

        public String foo(final Integer i, final String... s) {
            return "foo(int, String...)";
        }

        public void oneParameter(final String s) {
            // empty
        }

        public String foo(final Object... s) {
            return "foo(Object...)";
        }

        public int[] unboxing(final int... values) {
            return values;
        }

        // This method is overloaded for the wrapper class for every primitive type, plus the common supertypes
        // Number and Object. This is an acid test since it easily leads to ambiguous methods.
        public static String varOverload(final Byte... args) {
            return "Byte...";
        }

        public static String varOverload(final Character... args) {
            return "Character...";
        }

        public static String varOverload(final Short... args) {
            return "Short...";
        }

        public static String varOverload(final Boolean... args) {
            return "Boolean...";
        }

        public static String varOverload(final Float... args) {
            return "Float...";
        }

        public static String varOverload(final Double... args) {
            return "Double...";
        }

        public static String varOverload(final Integer... args) {
            return "Integer...";
        }

        public static String varOverload(final Long... args) {
            return "Long...";
        }

        public static String varOverload(final Number... args) {
            return "Number...";
        }

        public static String varOverload(final Object... args) {
            return "Object...";
        }

        public static String varOverload(final String... args) {
            return "String...";
        }

        // This method is overloaded for the wrapper class for every numeric primitive type, plus the common
        // supertype Number
        public static String numOverload(final Byte... args) {
            return "Byte...";
        }

        public static String numOverload(final Short... args) {
            return "Short...";
        }

        public static String numOverload(final Float... args) {
            return "Float...";
        }

        public static String numOverload(final Double... args) {
            return "Double...";
        }

        public static String numOverload(final Integer... args) {
            return "Integer...";
        }

        public static String numOverload(final Long... args) {
            return "Long...";
        }

        public static String numOverload(final Number... args) {
            return "Number...";
        }

        // These varOverloadEcho and varOverloadEchoStatic methods are designed to verify that
        // not only is the correct overloaded variant invoked, but that the varags arguments
        // are also delivered correctly to the method.
        public ImmutablePair<String, Object[]> varOverloadEcho(final String... args) {
            return new ImmutablePair<>("String...", args);
        }

        public ImmutablePair<String, Object[]> varOverloadEcho(final Number... args) {
            return new ImmutablePair<>("Number...", args);
        }

        public static ImmutablePair<String, Object[]> varOverloadEchoStatic(final String... args) {
            return new ImmutablePair<>("String...", args);
        }

        public static ImmutablePair<String, Object[]> varOverloadEchoStatic(final Number... args) {
            return new ImmutablePair<>("Number...", args);
        }

        static void verify(final ImmutablePair<String, Object[]> a, final ImmutablePair<String, Object[]> b) {
            Assertions.assertEquals(a.getLeft(), b.getLeft());
            Assertions.assertArrayEquals(a.getRight(), b.getRight());
        }

        static void verify(final ImmutablePair<String, Object[]> a, final Object _b) {
            @SuppressWarnings("unchecked")
            final ImmutablePair<String, Object[]> b = ((ImmutablePair<String, Object[]>) (_b));
            MethodUtilsTest.TestBean.verify(a, b);
        }
    }

    private static class TestMutable implements Mutable<Object> {
        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(final Object value) {
        }
    }

    private MethodUtilsTest.TestBean testBean;

    private final Map<Class<?>, Class<?>[]> classCache = new HashMap<>();

    @Test
    public void testConstructor() throws Exception {
        Assertions.assertNotNull(MethodUtils.class.newInstance());
    }

    @Test
    public void verifyJavaVarargsOverloadingResolution() {
        // This code is not a test of MethodUtils.
        // Rather it makes explicit the behavior of the Java specification for
        // various cases of overload resolution.
        Assertions.assertEquals("Byte...", MethodUtilsTest.TestBean.varOverload(((byte) (1)), ((byte) (2))));
        Assertions.assertEquals("Short...", MethodUtilsTest.TestBean.varOverload(((short) (1)), ((short) (2))));
        Assertions.assertEquals("Integer...", MethodUtilsTest.TestBean.varOverload(1, 2));
        Assertions.assertEquals("Long...", MethodUtilsTest.TestBean.varOverload(1L, 2L));
        Assertions.assertEquals("Float...", MethodUtilsTest.TestBean.varOverload(1.0F, 2.0F));
        Assertions.assertEquals("Double...", MethodUtilsTest.TestBean.varOverload(1.0, 2.0));
        Assertions.assertEquals("Character...", MethodUtilsTest.TestBean.varOverload('a', 'b'));
        Assertions.assertEquals("String...", MethodUtilsTest.TestBean.varOverload("a", "b"));
        Assertions.assertEquals("Boolean...", MethodUtilsTest.TestBean.varOverload(true, false));
        Assertions.assertEquals("Object...", MethodUtilsTest.TestBean.varOverload(1, "s"));
        Assertions.assertEquals("Object...", MethodUtilsTest.TestBean.varOverload(1, true));
        Assertions.assertEquals("Object...", MethodUtilsTest.TestBean.varOverload(1.1, true));
        Assertions.assertEquals("Object...", MethodUtilsTest.TestBean.varOverload('c', true));
        Assertions.assertEquals("Number...", MethodUtilsTest.TestBean.varOverload(1, 1.1));
        Assertions.assertEquals("Number...", MethodUtilsTest.TestBean.varOverload(1, 1L));
        Assertions.assertEquals("Number...", MethodUtilsTest.TestBean.varOverload(1.0, 1.0F));
        Assertions.assertEquals("Number...", MethodUtilsTest.TestBean.varOverload(((short) (1)), ((byte) (1))));
        Assertions.assertEquals("Object...", MethodUtilsTest.TestBean.varOverload(1, 'c'));
        Assertions.assertEquals("Object...", MethodUtilsTest.TestBean.varOverload('c', "s"));
    }

    @Test
    public void testInvokeJavaVarargsOverloadingResolution() throws Exception {
        Assertions.assertEquals("Byte...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", ((byte) (1)), ((byte) (2))));
        Assertions.assertEquals("Short...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", ((short) (1)), ((short) (2))));
        Assertions.assertEquals("Integer...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1, 2));
        Assertions.assertEquals("Long...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1L, 2L));
        Assertions.assertEquals("Float...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1.0F, 2.0F));
        Assertions.assertEquals("Double...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1.0, 2.0));
        Assertions.assertEquals("Character...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 'a', 'b'));
        Assertions.assertEquals("String...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", "a", "b"));
        Assertions.assertEquals("Boolean...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", true, false));
        Assertions.assertEquals("Object...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1, "s"));
        Assertions.assertEquals("Object...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1, true));
        Assertions.assertEquals("Object...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1.1, true));
        Assertions.assertEquals("Object...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 'c', true));
        Assertions.assertEquals("Number...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1, 1.1));
        Assertions.assertEquals("Number...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1, 1L));
        Assertions.assertEquals("Number...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1.0, 1.0F));
        Assertions.assertEquals("Number...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", ((short) (1)), ((byte) (1))));
        Assertions.assertEquals("Object...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 1, 'c'));
        Assertions.assertEquals("Object...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", 'c', "s"));
        Assertions.assertEquals("Object...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverload", ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))));
        Assertions.assertEquals("Number...", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "numOverload", ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))));
    }

    @Test
    public void testInvokeMethod() throws Exception {
        Assertions.assertEquals("foo()", MethodUtils.invokeMethod(testBean, "foo", ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))));
        Assertions.assertEquals("foo()", MethodUtils.invokeMethod(testBean, "foo"));
        Assertions.assertEquals("foo()", MethodUtils.invokeMethod(testBean, "foo", ((Object[]) (null))));
        Assertions.assertEquals("foo()", MethodUtils.invokeMethod(testBean, "foo", null, null));
        Assertions.assertEquals("foo(String)", MethodUtils.invokeMethod(testBean, "foo", ""));
        Assertions.assertEquals("foo(Object)", MethodUtils.invokeMethod(testBean, "foo", new Object()));
        Assertions.assertEquals("foo(Object)", MethodUtils.invokeMethod(testBean, "foo", Boolean.TRUE));
        Assertions.assertEquals("foo(Integer)", MethodUtils.invokeMethod(testBean, "foo", NumberUtils.INTEGER_ONE));
        Assertions.assertEquals("foo(int)", MethodUtils.invokeMethod(testBean, "foo", NumberUtils.BYTE_ONE));
        Assertions.assertEquals("foo(long)", MethodUtils.invokeMethod(testBean, "foo", NumberUtils.LONG_ONE));
        Assertions.assertEquals("foo(double)", MethodUtils.invokeMethod(testBean, "foo", NumberUtils.DOUBLE_ONE));
        Assertions.assertEquals("foo(String...)", MethodUtils.invokeMethod(testBean, "foo", "a", "b", "c"));
        Assertions.assertEquals("foo(String...)", MethodUtils.invokeMethod(testBean, "foo", "a", "b", "c"));
        Assertions.assertEquals("foo(int, String...)", MethodUtils.invokeMethod(testBean, "foo", 5, "a", "b", "c"));
        Assertions.assertEquals("foo(long...)", MethodUtils.invokeMethod(testBean, "foo", 1L, 2L));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeMethod(testBean, "foo", 1, 2));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("String...", new String[]{ "x", "y" }), MethodUtils.invokeMethod(testBean, "varOverloadEcho", "x", "y"));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("Number...", new Number[]{ 17, 23, 42 }), MethodUtils.invokeMethod(testBean, "varOverloadEcho", 17, 23, 42));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("String...", new String[]{ "x", "y" }), MethodUtils.invokeMethod(testBean, "varOverloadEcho", "x", "y"));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("Number...", new Number[]{ 17, 23, 42 }), MethodUtils.invokeMethod(testBean, "varOverloadEcho", 17, 23, 42));
    }

    @Test
    public void testInvokeExactMethod() throws Exception {
        Assertions.assertEquals("foo()", MethodUtils.invokeExactMethod(testBean, "foo", ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))));
        Assertions.assertEquals("foo()", MethodUtils.invokeExactMethod(testBean, "foo"));
        Assertions.assertEquals("foo()", MethodUtils.invokeExactMethod(testBean, "foo", ((Object[]) (null))));
        Assertions.assertEquals("foo()", MethodUtils.invokeExactMethod(testBean, "foo", null, null));
        Assertions.assertEquals("foo(String)", MethodUtils.invokeExactMethod(testBean, "foo", ""));
        Assertions.assertEquals("foo(Object)", MethodUtils.invokeExactMethod(testBean, "foo", new Object()));
        Assertions.assertEquals("foo(Integer)", MethodUtils.invokeExactMethod(testBean, "foo", NumberUtils.INTEGER_ONE));
        Assertions.assertEquals("foo(double)", MethodUtils.invokeExactMethod(testBean, "foo", new Object[]{ NumberUtils.DOUBLE_ONE }, new Class[]{ Double.TYPE }));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeExactMethod(testBean, "foo", NumberUtils.BYTE_ONE));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeExactMethod(testBean, "foo", NumberUtils.LONG_ONE));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeExactMethod(testBean, "foo", Boolean.TRUE));
    }

    @Test
    public void testInvokeStaticMethod() throws Exception {
        Assertions.assertEquals("bar()", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))));
        Assertions.assertEquals("bar()", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", ((Object[]) (null))));
        Assertions.assertEquals("bar()", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", null, null));
        Assertions.assertEquals("bar(String)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", ""));
        Assertions.assertEquals("bar(Object)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", new Object()));
        Assertions.assertEquals("bar(Object)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", Boolean.TRUE));
        Assertions.assertEquals("bar(Integer)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", NumberUtils.INTEGER_ONE));
        Assertions.assertEquals("bar(int)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", NumberUtils.BYTE_ONE));
        Assertions.assertEquals("bar(double)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", NumberUtils.DOUBLE_ONE));
        Assertions.assertEquals("bar(String...)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", "a", "b"));
        Assertions.assertEquals("bar(long...)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", 1L, 2L));
        Assertions.assertEquals("bar(int, String...)", MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "bar", NumberUtils.INTEGER_ONE, "a", "b"));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("String...", new String[]{ "x", "y" }), MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverloadEchoStatic", "x", "y"));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("Number...", new Number[]{ 17, 23, 42 }), MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverloadEchoStatic", 17, 23, 42));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("String...", new String[]{ "x", "y" }), MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverloadEchoStatic", "x", "y"));
        MethodUtilsTest.TestBean.verify(new ImmutablePair<>("Number...", new Number[]{ 17, 23, 42 }), MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "varOverloadEchoStatic", 17, 23, 42));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeStaticMethod(MethodUtilsTest.TestBean.class, "does_not_exist"));
    }

    @Test
    public void testInvokeExactStaticMethod() throws Exception {
        Assertions.assertEquals("bar()", MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", ((Object[]) (ArrayUtils.EMPTY_CLASS_ARRAY))));
        Assertions.assertEquals("bar()", MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", ((Object[]) (null))));
        Assertions.assertEquals("bar()", MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", null, null));
        Assertions.assertEquals("bar(String)", MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", ""));
        Assertions.assertEquals("bar(Object)", MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", new Object()));
        Assertions.assertEquals("bar(Integer)", MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", NumberUtils.INTEGER_ONE));
        Assertions.assertEquals("bar(double)", MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", new Object[]{ NumberUtils.DOUBLE_ONE }, new Class[]{ Double.TYPE }));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", NumberUtils.BYTE_ONE));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", NumberUtils.LONG_ONE));
        Assertions.assertThrows(NoSuchMethodException.class, () -> MethodUtils.invokeExactStaticMethod(MethodUtilsTest.TestBean.class, "bar", Boolean.TRUE));
    }

    @Test
    public void testGetAccessibleInterfaceMethod() throws Exception {
        final Class<?>[][] p = new Class<?>[][]{ ArrayUtils.EMPTY_CLASS_ARRAY, null };
        for (final Class<?>[] element : p) {
            final Method method = MethodUtilsTest.TestMutable.class.getMethod("getValue", element);
            final Method accessibleMethod = MethodUtils.getAccessibleMethod(method);
            Assertions.assertNotSame(accessibleMethod, method);
            Assertions.assertSame(Mutable.class, accessibleMethod.getDeclaringClass());
        }
    }

    @Test
    public void testGetAccessibleMethodPrivateInterface() throws Exception {
        final Method expected = MethodUtilsTest.TestBeanWithInterfaces.class.getMethod("foo");
        Assertions.assertNotNull(expected);
        final Method actual = MethodUtils.getAccessibleMethod(MethodUtilsTest.TestBeanWithInterfaces.class, "foo");
        Assertions.assertNull(actual);
    }

    @Test
    public void testGetAccessibleInterfaceMethodFromDescription() {
        final Class<?>[][] p = new Class<?>[][]{ ArrayUtils.EMPTY_CLASS_ARRAY, null };
        for (final Class<?>[] element : p) {
            final Method accessibleMethod = MethodUtils.getAccessibleMethod(MethodUtilsTest.TestMutable.class, "getValue", element);
            Assertions.assertSame(Mutable.class, accessibleMethod.getDeclaringClass());
        }
    }

    @Test
    public void testGetAccessiblePublicMethod() throws Exception {
        Assertions.assertSame(MutableObject.class, MethodUtils.getAccessibleMethod(MutableObject.class.getMethod("getValue", ArrayUtils.EMPTY_CLASS_ARRAY)).getDeclaringClass());
    }

    @Test
    public void testGetAccessiblePublicMethodFromDescription() {
        Assertions.assertSame(MutableObject.class, MethodUtils.getAccessibleMethod(MutableObject.class, "getValue", ArrayUtils.EMPTY_CLASS_ARRAY).getDeclaringClass());
    }

    @Test
    public void testGetAccessibleMethodInaccessible() throws Exception {
        final Method expected = MethodUtilsTest.TestBean.class.getDeclaredMethod("privateStuff");
        final Method actual = MethodUtils.getAccessibleMethod(expected);
        Assertions.assertNull(actual);
    }

    @Test
    public void testGetMatchingAccessibleMethod() {
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY);
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", null, ArrayUtils.EMPTY_CLASS_ARRAY);
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(String.class), singletonArray(String.class));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Object.class), singletonArray(Object.class));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Boolean.class), singletonArray(Object.class));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Byte.class), singletonArray(Integer.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Byte.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Short.class), singletonArray(Integer.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Short.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Character.class), singletonArray(Integer.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Character.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Integer.class), singletonArray(Integer.class));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Integer.TYPE), singletonArray(Integer.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Long.class), singletonArray(Long.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Long.TYPE), singletonArray(Long.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Float.class), singletonArray(Double.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Float.TYPE), singletonArray(Double.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Double.class), singletonArray(Double.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Double.TYPE), singletonArray(Double.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", singletonArray(Double.TYPE), singletonArray(Double.TYPE));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", new Class[]{ String.class, String.class }, new Class[]{ String[].class });
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "foo", new Class[]{ Integer.TYPE, String.class, String.class }, new Class[]{ Integer.class, String[].class });
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.InheritanceBean.class, "testOne", singletonArray(MethodUtilsTest.ParentObject.class), singletonArray(MethodUtilsTest.ParentObject.class));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.InheritanceBean.class, "testOne", singletonArray(MethodUtilsTest.ChildObject.class), singletonArray(MethodUtilsTest.ParentObject.class));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.InheritanceBean.class, "testTwo", singletonArray(MethodUtilsTest.ParentObject.class), singletonArray(MethodUtilsTest.GrandParentObject.class));
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.InheritanceBean.class, "testTwo", singletonArray(MethodUtilsTest.ChildObject.class), singletonArray(MethodUtilsTest.ChildInterface.class));
    }

    @Test
    public void testNullArgument() {
        expectMatchingAccessibleMethodParameterTypes(MethodUtilsTest.TestBean.class, "oneParameter", singletonArray(null), singletonArray(String.class));
    }

    @Test
    public void testGetOverrideHierarchyIncludingInterfaces() {
        final Method method = MethodUtils.getAccessibleMethod(StringParameterizedChild.class, "consume", String.class);
        final Iterator<MethodUtilsTest.MethodDescriptor> expected = Arrays.asList(new MethodUtilsTest.MethodDescriptor(StringParameterizedChild.class, "consume", String.class), new MethodUtilsTest.MethodDescriptor(GenericParent.class, "consume", GenericParent.class.getTypeParameters()[0]), new MethodUtilsTest.MethodDescriptor(GenericConsumer.class, "consume", GenericConsumer.class.getTypeParameters()[0])).iterator();
        for (final Method m : MethodUtils.getOverrideHierarchy(method, INCLUDE)) {
            Assertions.assertTrue(expected.hasNext());
            final MethodUtilsTest.MethodDescriptor md = expected.next();
            Assertions.assertEquals(md.declaringClass, m.getDeclaringClass());
            Assertions.assertEquals(md.name, m.getName());
            Assertions.assertEquals(md.parameterTypes.length, m.getParameterTypes().length);
            for (int i = 0; i < (md.parameterTypes.length); i++) {
                Assertions.assertTrue(TypeUtils.equals(md.parameterTypes[i], m.getGenericParameterTypes()[i]));
            }
        }
        Assertions.assertFalse(expected.hasNext());
    }

    @Test
    public void testGetOverrideHierarchyExcludingInterfaces() {
        final Method method = MethodUtils.getAccessibleMethod(StringParameterizedChild.class, "consume", String.class);
        final Iterator<MethodUtilsTest.MethodDescriptor> expected = Arrays.asList(new MethodUtilsTest.MethodDescriptor(StringParameterizedChild.class, "consume", String.class), new MethodUtilsTest.MethodDescriptor(GenericParent.class, "consume", GenericParent.class.getTypeParameters()[0])).iterator();
        for (final Method m : MethodUtils.getOverrideHierarchy(method, EXCLUDE)) {
            Assertions.assertTrue(expected.hasNext());
            final MethodUtilsTest.MethodDescriptor md = expected.next();
            Assertions.assertEquals(md.declaringClass, m.getDeclaringClass());
            Assertions.assertEquals(md.name, m.getName());
            Assertions.assertEquals(md.parameterTypes.length, m.getParameterTypes().length);
            for (int i = 0; i < (md.parameterTypes.length); i++) {
                Assertions.assertTrue(TypeUtils.equals(md.parameterTypes[i], m.getGenericParameterTypes()[i]));
            }
        }
        Assertions.assertFalse(expected.hasNext());
    }

    @Test
    @Annotated
    public void testGetMethodsWithAnnotation() throws NoSuchMethodException {
        Assertions.assertArrayEquals(new Method[0], MethodUtils.getMethodsWithAnnotation(Object.class, Annotated.class));
        final Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(MethodUtilsTest.class, Annotated.class);
        Assertions.assertEquals(2, methodsWithAnnotation.length);
        MatcherAssert.assertThat(methodsWithAnnotation, Matchers.hasItemInArray(MethodUtilsTest.class.getMethod("testGetMethodsWithAnnotation")));
        MatcherAssert.assertThat(methodsWithAnnotation, Matchers.hasItemInArray(MethodUtilsTest.class.getMethod("testGetMethodsListWithAnnotation")));
    }

    @Test
    public void testGetMethodsWithAnnotationSearchSupersAndIgnoreAccess() {
        Assertions.assertArrayEquals(new Method[0], MethodUtils.getMethodsWithAnnotation(Object.class, Annotated.class, true, true));
        final Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(PublicChild.class, Annotated.class, true, true);
        Assertions.assertEquals(4, methodsWithAnnotation.length);
        Assertions.assertEquals("PublicChild", methodsWithAnnotation[0].getDeclaringClass().getSimpleName());
        Assertions.assertEquals("PublicChild", methodsWithAnnotation[1].getDeclaringClass().getSimpleName());
        Assertions.assertTrue(methodsWithAnnotation[0].getName().endsWith("AnnotatedMethod"));
        Assertions.assertTrue(methodsWithAnnotation[1].getName().endsWith("AnnotatedMethod"));
        Assertions.assertEquals("Foo.doIt", (((methodsWithAnnotation[2].getDeclaringClass().getSimpleName()) + '.') + (methodsWithAnnotation[2].getName())));
        Assertions.assertEquals("Parent.parentProtectedAnnotatedMethod", (((methodsWithAnnotation[3].getDeclaringClass().getSimpleName()) + '.') + (methodsWithAnnotation[3].getName())));
    }

    @Test
    public void testGetMethodsWithAnnotationNotSearchSupersButIgnoreAccess() {
        Assertions.assertArrayEquals(new Method[0], MethodUtils.getMethodsWithAnnotation(Object.class, Annotated.class, false, true));
        final Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(PublicChild.class, Annotated.class, false, true);
        Assertions.assertEquals(2, methodsWithAnnotation.length);
        Assertions.assertEquals("PublicChild", methodsWithAnnotation[0].getDeclaringClass().getSimpleName());
        Assertions.assertEquals("PublicChild", methodsWithAnnotation[1].getDeclaringClass().getSimpleName());
        Assertions.assertTrue(methodsWithAnnotation[0].getName().endsWith("AnnotatedMethod"));
        Assertions.assertTrue(methodsWithAnnotation[1].getName().endsWith("AnnotatedMethod"));
    }

    @Test
    public void testGetMethodsWithAnnotationSearchSupersButNotIgnoreAccess() {
        Assertions.assertArrayEquals(new Method[0], MethodUtils.getMethodsWithAnnotation(Object.class, Annotated.class, true, false));
        final Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(PublicChild.class, Annotated.class, true, false);
        Assertions.assertEquals(2, methodsWithAnnotation.length);
        Assertions.assertEquals("PublicChild.publicAnnotatedMethod", (((methodsWithAnnotation[0].getDeclaringClass().getSimpleName()) + '.') + (methodsWithAnnotation[0].getName())));
        Assertions.assertEquals("Foo.doIt", (((methodsWithAnnotation[1].getDeclaringClass().getSimpleName()) + '.') + (methodsWithAnnotation[1].getName())));
    }

    @Test
    public void testGetMethodsWithAnnotationNotSearchSupersAndNotIgnoreAccess() {
        Assertions.assertArrayEquals(new Method[0], MethodUtils.getMethodsWithAnnotation(Object.class, Annotated.class, false, false));
        final Method[] methodsWithAnnotation = MethodUtils.getMethodsWithAnnotation(PublicChild.class, Annotated.class, false, false);
        Assertions.assertEquals(1, methodsWithAnnotation.length);
        Assertions.assertEquals("PublicChild.publicAnnotatedMethod", (((methodsWithAnnotation[0].getDeclaringClass().getSimpleName()) + '.') + (methodsWithAnnotation[0].getName())));
    }

    @Test
    public void testGetAnnotationSearchSupersAndIgnoreAccess() throws NoSuchMethodException {
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentNotAnnotatedMethod"), Annotated.class, true, true));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("doIt"), Annotated.class, true, true));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentProtectedAnnotatedMethod"), Annotated.class, true, true));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getDeclaredMethod("privateAnnotatedMethod"), Annotated.class, true, true));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("publicAnnotatedMethod"), Annotated.class, true, true));
    }

    @Test
    public void testGetAnnotationNotSearchSupersButIgnoreAccess() throws NoSuchMethodException {
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentNotAnnotatedMethod"), Annotated.class, false, true));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("doIt"), Annotated.class, false, true));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentProtectedAnnotatedMethod"), Annotated.class, false, true));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getDeclaredMethod("privateAnnotatedMethod"), Annotated.class, false, true));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("publicAnnotatedMethod"), Annotated.class, false, true));
    }

    @Test
    public void testGetAnnotationSearchSupersButNotIgnoreAccess() throws NoSuchMethodException {
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentNotAnnotatedMethod"), Annotated.class, true, false));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("doIt"), Annotated.class, true, false));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentProtectedAnnotatedMethod"), Annotated.class, true, false));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getDeclaredMethod("privateAnnotatedMethod"), Annotated.class, true, false));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("publicAnnotatedMethod"), Annotated.class, true, false));
    }

    @Test
    public void testGetAnnotationNotSearchSupersAndNotIgnoreAccess() throws NoSuchMethodException {
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentNotAnnotatedMethod"), Annotated.class, false, false));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("doIt"), Annotated.class, false, false));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("parentProtectedAnnotatedMethod"), Annotated.class, false, false));
        Assertions.assertNull(MethodUtils.getAnnotation(PublicChild.class.getDeclaredMethod("privateAnnotatedMethod"), Annotated.class, false, false));
        Assertions.assertNotNull(MethodUtils.getAnnotation(PublicChild.class.getMethod("publicAnnotatedMethod"), Annotated.class, false, false));
    }

    @Test
    public void testGetMethodsWithAnnotationIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getMethodsWithAnnotation(FieldUtilsTest.class, null));
    }

    @Test
    public void testGetMethodsWithAnnotationIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getMethodsWithAnnotation(null, Annotated.class));
    }

    @Test
    public void testGetMethodsWithAnnotationIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getMethodsWithAnnotation(null, null));
    }

    @Test
    @Annotated
    public void testGetMethodsListWithAnnotation() throws NoSuchMethodException {
        Assertions.assertEquals(0, MethodUtils.getMethodsListWithAnnotation(Object.class, Annotated.class).size());
        final List<Method> methodWithAnnotation = MethodUtils.getMethodsListWithAnnotation(MethodUtilsTest.class, Annotated.class);
        Assertions.assertEquals(2, methodWithAnnotation.size());
        MatcherAssert.assertThat(methodWithAnnotation, Matchers.hasItems(MethodUtilsTest.class.getMethod("testGetMethodsWithAnnotation"), MethodUtilsTest.class.getMethod("testGetMethodsListWithAnnotation")));
    }

    @Test
    public void testGetMethodsListWithAnnotationIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getMethodsListWithAnnotation(FieldUtilsTest.class, null));
    }

    @Test
    public void testGetMethodsListWithAnnotationIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getMethodsListWithAnnotation(null, Annotated.class));
    }

    @Test
    public void testGetMethodsListWithAnnotationIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getMethodsListWithAnnotation(null, null));
    }

    @Test
    public void testGetAnnotationIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getAnnotation(FieldUtilsTest.class.getDeclaredMethods()[0], null, true, true));
    }

    @Test
    public void testGetAnnotationIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getAnnotation(null, Annotated.class, true, true));
    }

    @Test
    public void testGetAnnotationIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> MethodUtils.getAnnotation(null, null, true, true));
    }

    public static class InheritanceBean {
        public void testOne(final Object obj) {
        }

        public void testOne(final MethodUtilsTest.GrandParentObject obj) {
        }

        public void testOne(final MethodUtilsTest.ParentObject obj) {
        }

        public void testTwo(final Object obj) {
        }

        public void testTwo(final MethodUtilsTest.GrandParentObject obj) {
        }

        public void testTwo(final MethodUtilsTest.ChildInterface obj) {
        }
    }

    interface ChildInterface {}

    public static class GrandParentObject {}

    public static class ParentObject extends MethodUtilsTest.GrandParentObject {}

    public static class ChildObject extends MethodUtilsTest.ParentObject implements MethodUtilsTest.ChildInterface {}

    private static class MethodDescriptor {
        final Class<?> declaringClass;

        final String name;

        final Type[] parameterTypes;

        MethodDescriptor(final Class<?> declaringClass, final String name, final Type... parameterTypes) {
            this.declaringClass = declaringClass;
            this.name = name;
            this.parameterTypes = parameterTypes;
        }
    }

    @Test
    public void testVarArgsUnboxing() throws Exception {
        final MethodUtilsTest.TestBean testBean = new MethodUtilsTest.TestBean();
        final int[] actual = ((int[]) (MethodUtils.invokeMethod(testBean, "unboxing", Integer.valueOf(1), Integer.valueOf(2))));
        Assertions.assertArrayEquals(new int[]{ 1, 2 }, actual);
    }

    @Test
    public void testInvokeMethodForceAccessNoArgs() throws Exception {
        Assertions.assertEquals("privateStringStuff()", MethodUtils.invokeMethod(testBean, true, "privateStringStuff"));
    }

    @Test
    public void testInvokeMethodForceAccessWithArgs() throws Exception {
        Assertions.assertEquals("privateStringStuff(Integer)", MethodUtils.invokeMethod(testBean, true, "privateStringStuff", 5));
        Assertions.assertEquals("privateStringStuff(double)", MethodUtils.invokeMethod(testBean, true, "privateStringStuff", 5.0));
        Assertions.assertEquals("privateStringStuff(String)", MethodUtils.invokeMethod(testBean, true, "privateStringStuff", "Hi There"));
        Assertions.assertEquals("privateStringStuff(Object)", MethodUtils.invokeMethod(testBean, true, "privateStringStuff", new Date()));
    }

    @Test
    public void testDistance() throws Exception {
        final Method distanceMethod = MethodUtils.getMatchingMethod(MethodUtils.class, "distance", Class[].class, Class[].class);
        distanceMethod.setAccessible(true);
        Assertions.assertEquals((-1), distanceMethod.invoke(null, new Class[]{ String.class }, new Class[]{ Date.class }));
        Assertions.assertEquals(0, distanceMethod.invoke(null, new Class[]{ Date.class }, new Class[]{ Date.class }));
        Assertions.assertEquals(1, distanceMethod.invoke(null, new Class[]{ Integer.class }, new Class[]{ ClassUtils.wrapperToPrimitive(Integer.class) }));
        Assertions.assertEquals(2, distanceMethod.invoke(null, new Class[]{ Integer.class }, new Class[]{ Object.class }));
        distanceMethod.setAccessible(false);
    }
}

