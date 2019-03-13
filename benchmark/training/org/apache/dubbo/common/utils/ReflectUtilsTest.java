/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ReflectUtilsTest {
    @Test
    public void testIsPrimitives() throws Exception {
        Assertions.assertTrue(ReflectUtils.isPrimitives(boolean[].class));
        Assertions.assertTrue(ReflectUtils.isPrimitives(byte.class));
        Assertions.assertFalse(ReflectUtils.isPrimitive(Map[].class));
    }

    @Test
    public void testIsPrimitive() throws Exception {
        Assertions.assertTrue(ReflectUtils.isPrimitive(boolean.class));
        Assertions.assertTrue(ReflectUtils.isPrimitive(String.class));
        Assertions.assertTrue(ReflectUtils.isPrimitive(Boolean.class));
        Assertions.assertTrue(ReflectUtils.isPrimitive(Character.class));
        Assertions.assertTrue(ReflectUtils.isPrimitive(Number.class));
        Assertions.assertTrue(ReflectUtils.isPrimitive(Date.class));
        Assertions.assertFalse(ReflectUtils.isPrimitive(Map.class));
    }

    @Test
    public void testGetBoxedClass() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(int.class), Matchers.sameInstance(Integer.class));
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(boolean.class), Matchers.sameInstance(Boolean.class));
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(long.class), Matchers.sameInstance(Long.class));
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(float.class), Matchers.sameInstance(Float.class));
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(double.class), Matchers.sameInstance(Double.class));
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(char.class), Matchers.sameInstance(Character.class));
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(byte.class), Matchers.sameInstance(Byte.class));
        MatcherAssert.assertThat(ReflectUtils.getBoxedClass(short.class), Matchers.sameInstance(Short.class));
    }

    @Test
    public void testIsCompatible() throws Exception {
        Assertions.assertTrue(ReflectUtils.isCompatible(short.class, ((short) (1))));
        Assertions.assertTrue(ReflectUtils.isCompatible(int.class, 1));
        Assertions.assertTrue(ReflectUtils.isCompatible(double.class, 1.2));
        Assertions.assertTrue(ReflectUtils.isCompatible(Object.class, 1.2));
        Assertions.assertTrue(ReflectUtils.isCompatible(List.class, new ArrayList<String>()));
    }

    @Test
    public void testIsCompatibleWithArray() throws Exception {
        Assertions.assertFalse(ReflectUtils.isCompatible(new Class[]{ short.class, int.class }, new Object[]{ ((short) (1)) }));
        Assertions.assertFalse(ReflectUtils.isCompatible(new Class[]{ double.class }, new Object[]{ "hello" }));
        Assertions.assertTrue(ReflectUtils.isCompatible(new Class[]{ double.class }, new Object[]{ 1.2 }));
    }

    @Test
    public void testGetCodeBase() throws Exception {
        Assertions.assertNull(ReflectUtils.getCodeBase(null));
        Assertions.assertNull(ReflectUtils.getCodeBase(String.class));
        Assertions.assertNotNull(ReflectUtils.getCodeBase(ReflectUtils.class));
    }

    @Test
    public void testGetName() throws Exception {
        // getName
        Assertions.assertEquals("boolean", ReflectUtils.getName(boolean.class));
        Assertions.assertEquals("int[][][]", ReflectUtils.getName(int[][][].class));
        Assertions.assertEquals("java.lang.Object[][]", ReflectUtils.getName(Object[][].class));
        // getDesc
        Assertions.assertEquals("Z", ReflectUtils.getDesc(boolean.class));
        Assertions.assertEquals("[[[I", ReflectUtils.getDesc(int[][][].class));
        Assertions.assertEquals("[[Ljava/lang/Object;", ReflectUtils.getDesc(Object[][].class));
        // name2desc
        Assertions.assertEquals("Z", ReflectUtils.name2desc(ReflectUtils.getName(boolean.class)));
        Assertions.assertEquals("[[[I", ReflectUtils.name2desc(ReflectUtils.getName(int[][][].class)));
        Assertions.assertEquals("[[Ljava/lang/Object;", ReflectUtils.name2desc(ReflectUtils.getName(Object[][].class)));
        // desc2name
        Assertions.assertEquals("short[]", ReflectUtils.desc2name(ReflectUtils.getDesc(short[].class)));
        Assertions.assertEquals("boolean[]", ReflectUtils.desc2name(ReflectUtils.getDesc(boolean[].class)));
        Assertions.assertEquals("byte[]", ReflectUtils.desc2name(ReflectUtils.getDesc(byte[].class)));
        Assertions.assertEquals("char[]", ReflectUtils.desc2name(ReflectUtils.getDesc(char[].class)));
        Assertions.assertEquals("double[]", ReflectUtils.desc2name(ReflectUtils.getDesc(double[].class)));
        Assertions.assertEquals("float[]", ReflectUtils.desc2name(ReflectUtils.getDesc(float[].class)));
        Assertions.assertEquals("int[]", ReflectUtils.desc2name(ReflectUtils.getDesc(int[].class)));
        Assertions.assertEquals("long[]", ReflectUtils.desc2name(ReflectUtils.getDesc(long[].class)));
        Assertions.assertEquals("int", ReflectUtils.desc2name(ReflectUtils.getDesc(int.class)));
        Assertions.assertEquals("void", ReflectUtils.desc2name(ReflectUtils.getDesc(void.class)));
        Assertions.assertEquals("java.lang.Object[][]", ReflectUtils.desc2name(ReflectUtils.getDesc(Object[][].class)));
    }

    @Test
    public void testGetGenericClass() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.getGenericClass(ReflectUtilsTest.Foo1.class), Matchers.sameInstance(String.class));
    }

    @Test
    public void testGetGenericClassWithIndex() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.getGenericClass(ReflectUtilsTest.Foo1.class, 0), Matchers.sameInstance(String.class));
        MatcherAssert.assertThat(ReflectUtils.getGenericClass(ReflectUtilsTest.Foo1.class, 1), Matchers.sameInstance(Integer.class));
        MatcherAssert.assertThat(ReflectUtils.getGenericClass(ReflectUtilsTest.Foo2.class, 0), Matchers.sameInstance(List.class));
        MatcherAssert.assertThat(ReflectUtils.getGenericClass(ReflectUtilsTest.Foo2.class, 1), Matchers.sameInstance(int.class));
        MatcherAssert.assertThat(ReflectUtils.getGenericClass(ReflectUtilsTest.Foo3.class, 0), Matchers.sameInstance(ReflectUtilsTest.Foo1.class));
        MatcherAssert.assertThat(ReflectUtils.getGenericClass(ReflectUtilsTest.Foo3.class, 1), Matchers.sameInstance(ReflectUtilsTest.Foo2.class));
    }

    @Test
    public void testGetMethodName() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.getName(ReflectUtilsTest.Foo2.class.getDeclaredMethod("hello", int[].class)), Matchers.equalTo("java.util.List hello(int[])"));
    }

    @Test
    public void testGetSignature() throws Exception {
        Method m = ReflectUtilsTest.Foo2.class.getDeclaredMethod("hello", int[].class);
        MatcherAssert.assertThat(ReflectUtils.getSignature("greeting", m.getParameterTypes()), Matchers.equalTo("greeting([I)"));
    }

    @Test
    public void testGetConstructorName() throws Exception {
        Constructor c = ReflectUtilsTest.Foo2.class.getConstructors()[0];
        MatcherAssert.assertThat(ReflectUtils.getName(c), Matchers.equalTo("(java.util.List,int[])"));
    }

    @Test
    public void testName2Class() throws Exception {
        Assertions.assertEquals(boolean.class, ReflectUtils.name2class("boolean"));
        Assertions.assertEquals(boolean[].class, ReflectUtils.name2class("boolean[]"));
        Assertions.assertEquals(int[][].class, ReflectUtils.name2class(ReflectUtils.getName(int[][].class)));
        Assertions.assertEquals(ReflectUtilsTest[].class, ReflectUtils.name2class(ReflectUtils.getName(ReflectUtilsTest[].class)));
    }

    @Test
    public void testGetDescMethod() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.getDesc(ReflectUtilsTest.Foo2.class.getDeclaredMethod("hello", int[].class)), Matchers.equalTo("hello([I)Ljava/util/List;"));
    }

    @Test
    public void testGetDescConstructor() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.getDesc(ReflectUtilsTest.Foo2.class.getConstructors()[0]), Matchers.equalTo("(Ljava/util/List;[I)V"));
    }

    @Test
    public void testGetDescWithoutMethodName() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.getDescWithoutMethodName(ReflectUtilsTest.Foo2.class.getDeclaredMethod("hello", int[].class)), Matchers.equalTo("([I)Ljava/util/List;"));
    }

    @Test
    public void testFindMethodByMethodName1() throws Exception {
        Assertions.assertNotNull(ReflectUtils.findMethodByMethodName(ReflectUtilsTest.Foo.class, "hello"));
    }

    @Test
    public void testFindMethodByMethodName2() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ReflectUtils.findMethodByMethodName(ReflectUtilsTest.Foo2.class, "hello");
        });
    }

    @Test
    public void testFindConstructor() throws Exception {
        Constructor constructor = ReflectUtils.findConstructor(ReflectUtilsTest.Foo3.class, ReflectUtilsTest.Foo2.class);
        Assertions.assertNotNull(constructor);
    }

    @Test
    public void testIsInstance() throws Exception {
        Assertions.assertTrue(ReflectUtils.isInstance(new ReflectUtilsTest.Foo1(), ReflectUtilsTest.Foo.class.getName()));
    }

    @Test
    public void testIsBeanPropertyReadMethod() throws Exception {
        Method method = ReflectUtilsTest.EmptyClass.class.getMethod("getProperty");
        Assertions.assertTrue(ReflectUtils.isBeanPropertyReadMethod(method));
        method = ReflectUtilsTest.EmptyClass.class.getMethod("getProperties");
        Assertions.assertFalse(ReflectUtils.isBeanPropertyReadMethod(method));
        method = ReflectUtilsTest.EmptyClass.class.getMethod("isProperty");
        Assertions.assertFalse(ReflectUtils.isBeanPropertyReadMethod(method));
        method = ReflectUtilsTest.EmptyClass.class.getMethod("getPropertyIndex", int.class);
        Assertions.assertFalse(ReflectUtils.isBeanPropertyReadMethod(method));
    }

    @Test
    public void testGetPropertyNameFromBeanReadMethod() throws Exception {
        Method method = ReflectUtilsTest.EmptyClass.class.getMethod("getProperty");
        Assertions.assertEquals(ReflectUtils.getPropertyNameFromBeanReadMethod(method), "property");
        method = ReflectUtilsTest.EmptyClass.class.getMethod("isSet");
        Assertions.assertEquals(ReflectUtils.getPropertyNameFromBeanReadMethod(method), "set");
    }

    @Test
    public void testIsBeanPropertyWriteMethod() throws Exception {
        Method method = ReflectUtilsTest.EmptyClass.class.getMethod("setProperty", ReflectUtilsTest.EmptyProperty.class);
        Assertions.assertTrue(ReflectUtils.isBeanPropertyWriteMethod(method));
        method = ReflectUtilsTest.EmptyClass.class.getMethod("setSet", boolean.class);
        Assertions.assertTrue(ReflectUtils.isBeanPropertyWriteMethod(method));
    }

    @Test
    public void testGetPropertyNameFromBeanWriteMethod() throws Exception {
        Method method = ReflectUtilsTest.EmptyClass.class.getMethod("setProperty", ReflectUtilsTest.EmptyProperty.class);
        Assertions.assertEquals(ReflectUtils.getPropertyNameFromBeanWriteMethod(method), "property");
    }

    @Test
    public void testIsPublicInstanceField() throws Exception {
        Field field = ReflectUtilsTest.EmptyClass.class.getDeclaredField("set");
        Assertions.assertTrue(ReflectUtils.isPublicInstanceField(field));
        field = ReflectUtilsTest.EmptyClass.class.getDeclaredField("property");
        Assertions.assertFalse(ReflectUtils.isPublicInstanceField(field));
    }

    @Test
    public void testGetBeanPropertyFields() throws Exception {
        Map<String, Field> map = ReflectUtils.getBeanPropertyFields(ReflectUtilsTest.EmptyClass.class);
        MatcherAssert.assertThat(map.size(), Matchers.is(2));
        MatcherAssert.assertThat(map, Matchers.hasKey("set"));
        MatcherAssert.assertThat(map, Matchers.hasKey("property"));
        for (Field f : map.values()) {
            if (!(f.isAccessible())) {
                Assertions.fail();
            }
        }
    }

    @Test
    public void testGetBeanPropertyReadMethods() throws Exception {
        Map<String, Method> map = ReflectUtils.getBeanPropertyReadMethods(ReflectUtilsTest.EmptyClass.class);
        MatcherAssert.assertThat(map.size(), Matchers.is(2));
        MatcherAssert.assertThat(map, Matchers.hasKey("set"));
        MatcherAssert.assertThat(map, Matchers.hasKey("property"));
        for (Method m : map.values()) {
            if (!(m.isAccessible())) {
                Assertions.fail();
            }
        }
    }

    @Test
    public void testDesc2Class() throws Exception {
        Assertions.assertEquals(void.class, ReflectUtils.desc2class("V"));
        Assertions.assertEquals(boolean.class, ReflectUtils.desc2class("Z"));
        Assertions.assertEquals(boolean[].class, ReflectUtils.desc2class("[Z"));
        Assertions.assertEquals(byte.class, ReflectUtils.desc2class("B"));
        Assertions.assertEquals(char.class, ReflectUtils.desc2class("C"));
        Assertions.assertEquals(double.class, ReflectUtils.desc2class("D"));
        Assertions.assertEquals(float.class, ReflectUtils.desc2class("F"));
        Assertions.assertEquals(int.class, ReflectUtils.desc2class("I"));
        Assertions.assertEquals(long.class, ReflectUtils.desc2class("J"));
        Assertions.assertEquals(short.class, ReflectUtils.desc2class("S"));
        Assertions.assertEquals(String.class, ReflectUtils.desc2class("Ljava.lang.String;"));
        Assertions.assertEquals(int[][].class, ReflectUtils.desc2class(ReflectUtils.getDesc(int[][].class)));
        Assertions.assertEquals(ReflectUtilsTest[].class, ReflectUtils.desc2class(ReflectUtils.getDesc(ReflectUtilsTest[].class)));
        String desc;
        Class<?>[] cs;
        cs = new Class<?>[]{ int.class, getClass(), String.class, int[][].class, boolean[].class };
        desc = ReflectUtils.getDesc(cs);
        assertSame(cs, ReflectUtils.desc2classArray(desc));
        cs = new Class<?>[]{  };
        desc = ReflectUtils.getDesc(cs);
        assertSame(cs, ReflectUtils.desc2classArray(desc));
        cs = new Class<?>[]{ void.class, String[].class, int[][].class, ReflectUtilsTest[][].class };
        desc = ReflectUtils.getDesc(cs);
        assertSame(cs, ReflectUtils.desc2classArray(desc));
    }

    @Test
    public void testFindMethodByMethodSignature() throws Exception {
        Method m = ReflectUtils.findMethodByMethodSignature(ReflectUtilsTest.TestedClass.class, "method1", null);
        Assertions.assertEquals("method1", m.getName());
        Class<?>[] parameterTypes = m.getParameterTypes();
        Assertions.assertEquals(1, parameterTypes.length);
        Assertions.assertEquals(int.class, parameterTypes[0]);
    }

    @Test
    public void testFindMethodByMethodSignature_override() throws Exception {
        {
            Method m = ReflectUtils.findMethodByMethodSignature(ReflectUtilsTest.TestedClass.class, "overrideMethod", new String[]{ "int" });
            Assertions.assertEquals("overrideMethod", m.getName());
            Class<?>[] parameterTypes = m.getParameterTypes();
            Assertions.assertEquals(1, parameterTypes.length);
            Assertions.assertEquals(int.class, parameterTypes[0]);
        }
        {
            Method m = ReflectUtils.findMethodByMethodSignature(ReflectUtilsTest.TestedClass.class, "overrideMethod", new String[]{ "java.lang.Integer" });
            Assertions.assertEquals("overrideMethod", m.getName());
            Class<?>[] parameterTypes = m.getParameterTypes();
            Assertions.assertEquals(1, parameterTypes.length);
            Assertions.assertEquals(Integer.class, parameterTypes[0]);
        }
    }

    @Test
    public void testFindMethodByMethodSignatureOverrideMoreThan1() throws Exception {
        try {
            ReflectUtils.findMethodByMethodSignature(ReflectUtilsTest.TestedClass.class, "overrideMethod", null);
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Not unique method for method name("));
        }
    }

    @Test
    public void testFindMethodByMethodSignatureNotFound() throws Exception {
        try {
            ReflectUtils.findMethodByMethodSignature(ReflectUtilsTest.TestedClass.class, "notExsited", null);
            Assertions.fail();
        } catch (NoSuchMethodException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("No such method "));
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("in class"));
        }
    }

    @Test
    public void testGetEmptyObject() throws Exception {
        Assertions.assertTrue(((ReflectUtils.getEmptyObject(Collection.class)) instanceof Collection));
        Assertions.assertTrue(((ReflectUtils.getEmptyObject(List.class)) instanceof List));
        Assertions.assertTrue(((ReflectUtils.getEmptyObject(Set.class)) instanceof Set));
        Assertions.assertTrue(((ReflectUtils.getEmptyObject(Map.class)) instanceof Map));
        Assertions.assertTrue(((ReflectUtils.getEmptyObject(Object[].class)) instanceof Object[]));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(String.class), "");
        Assertions.assertEquals(ReflectUtils.getEmptyObject(short.class), Short.valueOf(((short) (0))));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(byte.class), Byte.valueOf(((byte) (0))));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(int.class), Integer.valueOf(0));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(long.class), Long.valueOf(0));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(float.class), Float.valueOf(0));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(double.class), Double.valueOf(0));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(char.class), Character.valueOf('\u0000'));
        Assertions.assertEquals(ReflectUtils.getEmptyObject(boolean.class), Boolean.FALSE);
        ReflectUtilsTest.EmptyClass object = ((ReflectUtilsTest.EmptyClass) (ReflectUtils.getEmptyObject(ReflectUtilsTest.EmptyClass.class)));
        Assertions.assertNotNull(object);
        Assertions.assertNotNull(object.getProperty());
    }

    @Test
    public void testForName1() throws Exception {
        MatcherAssert.assertThat(ReflectUtils.forName(ReflectUtils.class.getName()), Matchers.sameInstance(ReflectUtils.class));
    }

    @Test
    public void testForName2() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ReflectUtils.forName("a.c.d.e.F");
        });
    }

    public static class EmptyClass {
        private ReflectUtilsTest.EmptyProperty property;

        public boolean set;

        public static String s;

        private transient int i;

        public ReflectUtilsTest.EmptyProperty getProperty() {
            return property;
        }

        public ReflectUtilsTest.EmptyProperty getPropertyIndex(int i) {
            return property;
        }

        public static ReflectUtilsTest.EmptyProperty getProperties() {
            return null;
        }

        public void isProperty() {
        }

        public boolean isSet() {
            return set;
        }

        public void setProperty(ReflectUtilsTest.EmptyProperty property) {
            this.property = property;
        }

        public void setSet(boolean set) {
            this.set = set;
        }
    }

    public static class EmptyProperty {}

    static class TestedClass {
        public void method1(int x) {
        }

        public void overrideMethod(int x) {
        }

        public void overrideMethod(Integer x) {
        }

        public void overrideMethod(String s) {
        }

        public void overrideMethod(String s1, String s2) {
        }
    }

    interface Foo<A, B> {
        A hello(B b);
    }

    static class Foo1 implements ReflectUtilsTest.Foo<String, Integer> {
        @Override
        public String hello(Integer integer) {
            return null;
        }
    }

    static class Foo2 implements ReflectUtilsTest.Foo<List<String>, int[]> {
        public Foo2(List<String> list, int[] ints) {
        }

        @Override
        public List<String> hello(int[] ints) {
            return null;
        }
    }

    static class Foo3 implements ReflectUtilsTest.Foo<ReflectUtilsTest.Foo1, ReflectUtilsTest.Foo2> {
        public Foo3(ReflectUtilsTest.Foo foo) {
        }

        @Override
        public ReflectUtilsTest.Foo1 hello(ReflectUtilsTest.Foo2 foo2) {
            return null;
        }
    }
}

