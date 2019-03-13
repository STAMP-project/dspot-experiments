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
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.reflect.testbed.GenericConsumer;
import org.apache.commons.lang3.reflect.testbed.GenericParent;
import org.apache.commons.lang3.reflect.testbed.StringParameterizedChild;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.apache.commons.lang3.ClassUtils.Interfaces.INCLUDE;


/**
 * Unit tests {@link org.apache.commons.lang3.ClassUtils}.
 */
// JUnit4 does not support primitive equality testing apart from long
@SuppressWarnings("boxing")
public class ClassUtilsTest {
    // empty
    private static class CX implements ClassUtilsTest.IA , ClassUtilsTest.IB , ClassUtilsTest.IE {}

    // empty
    private static class CY extends ClassUtilsTest.CX implements ClassUtilsTest.IB , ClassUtilsTest.IC {}

    // empty
    private interface IA {}

    // empty
    private interface IB {}

    // empty
    private interface IC extends ClassUtilsTest.ID , ClassUtilsTest.IE {}

    // empty
    private interface ID {}

    // empty
    private interface IE extends ClassUtilsTest.IF {}

    // empty
    private interface IF {}

    private static class Inner {
        // empty
        private class DeeplyNested {}
    }

    @Test
    public void test_convertClassesToClassNames_List() {
        final List<Class<?>> list = new ArrayList<>();
        List<String> result = ClassUtils.convertClassesToClassNames(list);
        Assertions.assertEquals(0, result.size());
        list.add(String.class);
        list.add(null);
        list.add(Object.class);
        result = ClassUtils.convertClassesToClassNames(list);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("java.lang.String", result.get(0));
        Assertions.assertNull(result.get(1));
        Assertions.assertEquals("java.lang.Object", result.get(2));
        // test what happens when non-generic code adds wrong type of element
        @SuppressWarnings("unchecked")
        final List<Object> olist = ((List<Object>) ((List<?>) (list)));
        olist.add(new Object());
        Assertions.assertThrows(ClassCastException.class, () -> ClassUtils.convertClassesToClassNames(list), "Should not have been able to convert list");
        Assertions.assertNull(ClassUtils.convertClassesToClassNames(null));
    }

    // -------------------------------------------------------------------------
    @Test
    public void test_convertClassNamesToClasses_List() {
        final List<String> list = new ArrayList<>();
        List<Class<?>> result = ClassUtils.convertClassNamesToClasses(list);
        Assertions.assertEquals(0, result.size());
        list.add("java.lang.String");
        list.add("java.lang.xxx");
        list.add("java.lang.Object");
        result = ClassUtils.convertClassNamesToClasses(list);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(String.class, result.get(0));
        Assertions.assertNull(result.get(1));
        Assertions.assertEquals(Object.class, result.get(2));
        // test what happens when non-generic code adds wrong type of element
        @SuppressWarnings("unchecked")
        final List<Object> olist = ((List<Object>) ((List<?>) (list)));
        olist.add(new Object());
        Assertions.assertThrows(ClassCastException.class, () -> ClassUtils.convertClassNamesToClasses(list), "Should not have been able to convert list");
        Assertions.assertNull(ClassUtils.convertClassNamesToClasses(null));
    }

    // -------------------------------------------------------------------------
    @Test
    public void test_getAbbreviatedName_Class() {
        Assertions.assertEquals("", ClassUtils.getAbbreviatedName(((Class<?>) (null)), 1));
        Assertions.assertEquals("j.l.String", ClassUtils.getAbbreviatedName(String.class, 1));
        Assertions.assertEquals("j.l.String", ClassUtils.getAbbreviatedName(String.class, 5));
        Assertions.assertEquals("j.lang.String", ClassUtils.getAbbreviatedName(String.class, 13));
        Assertions.assertEquals("j.lang.String", ClassUtils.getAbbreviatedName(String.class, 15));
        Assertions.assertEquals("java.lang.String", ClassUtils.getAbbreviatedName(String.class, 20));
    }

    @Test
    public void test_getAbbreviatedName_Class_NegativeLen() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ClassUtils.getAbbreviatedName(String.class, (-10)));
    }

    @Test
    public void test_getAbbreviatedName_Class_ZeroLen() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ClassUtils.getAbbreviatedName(String.class, 0));
    }

    @Test
    public void test_getAbbreviatedName_String() {
        Assertions.assertEquals("", ClassUtils.getAbbreviatedName(((String) (null)), 1));
        Assertions.assertEquals("WithoutPackage", ClassUtils.getAbbreviatedName("WithoutPackage", 1));
        Assertions.assertEquals("j.l.String", ClassUtils.getAbbreviatedName("java.lang.String", 1));
    }

    @Test
    public void test_getAllInterfaces_Class() {
        final List<?> list = ClassUtils.getAllInterfaces(ClassUtilsTest.CY.class);
        Assertions.assertEquals(6, list.size());
        Assertions.assertEquals(ClassUtilsTest.IB.class, list.get(0));
        Assertions.assertEquals(ClassUtilsTest.IC.class, list.get(1));
        Assertions.assertEquals(ClassUtilsTest.ID.class, list.get(2));
        Assertions.assertEquals(ClassUtilsTest.IE.class, list.get(3));
        Assertions.assertEquals(ClassUtilsTest.IF.class, list.get(4));
        Assertions.assertEquals(ClassUtilsTest.IA.class, list.get(5));
        Assertions.assertNull(ClassUtils.getAllInterfaces(null));
    }

    // -------------------------------------------------------------------------
    @Test
    public void test_getAllSuperclasses_Class() {
        final List<?> list = ClassUtils.getAllSuperclasses(ClassUtilsTest.CY.class);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(ClassUtilsTest.CX.class, list.get(0));
        Assertions.assertEquals(Object.class, list.get(1));
        Assertions.assertNull(ClassUtils.getAllSuperclasses(null));
    }

    @Test
    public void test_getCanonicalName_Class() {
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtils", ClassUtils.getCanonicalName(ClassUtils.class));
        Assertions.assertEquals("java.util.Map.Entry", ClassUtils.getCanonicalName(Map.Entry.class));
        Assertions.assertEquals("", ClassUtils.getCanonicalName(((Class<?>) (null))));
        Assertions.assertEquals("java.lang.String[]", ClassUtils.getCanonicalName(String[].class));
        Assertions.assertEquals("java.util.Map.Entry[]", ClassUtils.getCanonicalName(Map.Entry[].class));
        // Primitives
        Assertions.assertEquals("boolean", ClassUtils.getCanonicalName(boolean.class));
        Assertions.assertEquals("byte", ClassUtils.getCanonicalName(byte.class));
        Assertions.assertEquals("char", ClassUtils.getCanonicalName(char.class));
        Assertions.assertEquals("short", ClassUtils.getCanonicalName(short.class));
        Assertions.assertEquals("int", ClassUtils.getCanonicalName(int.class));
        Assertions.assertEquals("long", ClassUtils.getCanonicalName(long.class));
        Assertions.assertEquals("float", ClassUtils.getCanonicalName(float.class));
        Assertions.assertEquals("double", ClassUtils.getCanonicalName(double.class));
        // Primitive Arrays
        Assertions.assertEquals("boolean[]", ClassUtils.getCanonicalName(boolean[].class));
        Assertions.assertEquals("byte[]", ClassUtils.getCanonicalName(byte[].class));
        Assertions.assertEquals("char[]", ClassUtils.getCanonicalName(char[].class));
        Assertions.assertEquals("short[]", ClassUtils.getCanonicalName(short[].class));
        Assertions.assertEquals("int[]", ClassUtils.getCanonicalName(int[].class));
        Assertions.assertEquals("long[]", ClassUtils.getCanonicalName(long[].class));
        Assertions.assertEquals("float[]", ClassUtils.getCanonicalName(float[].class));
        Assertions.assertEquals("double[]", ClassUtils.getCanonicalName(double[].class));
        // Arrays of arrays of ...
        Assertions.assertEquals("java.lang.String[][]", ClassUtils.getCanonicalName(String[][].class));
        Assertions.assertEquals("java.lang.String[][][]", ClassUtils.getCanonicalName(String[][][].class));
        Assertions.assertEquals("java.lang.String[][][][]", ClassUtils.getCanonicalName(String[][][][].class));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals(StringUtils.EMPTY, ClassUtils.getCanonicalName(new Object() {}.getClass()));
        Assertions.assertEquals(StringUtils.EMPTY, ClassUtils.getCanonicalName(Named.class));
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest.Inner", ClassUtils.getCanonicalName(ClassUtilsTest.Inner.class));
    }

    @Test
    public void test_getCanonicalName_Class_String() {
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtils", ClassUtils.getCanonicalName(ClassUtils.class, "X"));
        Assertions.assertEquals("java.util.Map.Entry", ClassUtils.getCanonicalName(Map.Entry.class, "X"));
        Assertions.assertEquals("X", ClassUtils.getCanonicalName(((Class<?>) (null)), "X"));
        Assertions.assertEquals("java.lang.String[]", ClassUtils.getCanonicalName(String[].class, "X"));
        Assertions.assertEquals("java.util.Map.Entry[]", ClassUtils.getCanonicalName(Map.Entry[].class, "X"));
        // Primitives
        Assertions.assertEquals("boolean", ClassUtils.getCanonicalName(boolean.class, "X"));
        Assertions.assertEquals("byte", ClassUtils.getCanonicalName(byte.class, "X"));
        Assertions.assertEquals("char", ClassUtils.getCanonicalName(char.class, "X"));
        Assertions.assertEquals("short", ClassUtils.getCanonicalName(short.class, "X"));
        Assertions.assertEquals("int", ClassUtils.getCanonicalName(int.class, "X"));
        Assertions.assertEquals("long", ClassUtils.getCanonicalName(long.class, "X"));
        Assertions.assertEquals("float", ClassUtils.getCanonicalName(float.class, "X"));
        Assertions.assertEquals("double", ClassUtils.getCanonicalName(double.class, "X"));
        // Primitive Arrays
        Assertions.assertEquals("boolean[]", ClassUtils.getCanonicalName(boolean[].class, "X"));
        Assertions.assertEquals("byte[]", ClassUtils.getCanonicalName(byte[].class, "X"));
        Assertions.assertEquals("char[]", ClassUtils.getCanonicalName(char[].class, "X"));
        Assertions.assertEquals("short[]", ClassUtils.getCanonicalName(short[].class, "X"));
        Assertions.assertEquals("int[]", ClassUtils.getCanonicalName(int[].class, "X"));
        Assertions.assertEquals("long[]", ClassUtils.getCanonicalName(long[].class, "X"));
        Assertions.assertEquals("float[]", ClassUtils.getCanonicalName(float[].class, "X"));
        Assertions.assertEquals("double[]", ClassUtils.getCanonicalName(double[].class, "X"));
        // Arrays of arrays of ...
        Assertions.assertEquals("java.lang.String[][]", ClassUtils.getCanonicalName(String[][].class, "X"));
        Assertions.assertEquals("java.lang.String[][][]", ClassUtils.getCanonicalName(String[][][].class, "X"));
        Assertions.assertEquals("java.lang.String[][][][]", ClassUtils.getCanonicalName(String[][][][].class, "X"));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("X", ClassUtils.getCanonicalName(new Object() {}.getClass(), "X"));
        Assertions.assertEquals("X", ClassUtils.getCanonicalName(Named.class, "X"));
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest.Inner", ClassUtils.getCanonicalName(ClassUtilsTest.Inner.class, "X"));
    }

    @Test
    public void test_getName_Class() {
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtils", ClassUtils.getName(ClassUtils.class));
        Assertions.assertEquals("java.util.Map$Entry", ClassUtils.getName(Map.Entry.class));
        Assertions.assertEquals("", ClassUtils.getName(((Class<?>) (null))));
        Assertions.assertEquals("[Ljava.lang.String;", ClassUtils.getName(String[].class));
        Assertions.assertEquals("[Ljava.util.Map$Entry;", ClassUtils.getName(Map.Entry[].class));
        // Primitives
        Assertions.assertEquals("boolean", ClassUtils.getName(boolean.class));
        Assertions.assertEquals("byte", ClassUtils.getName(byte.class));
        Assertions.assertEquals("char", ClassUtils.getName(char.class));
        Assertions.assertEquals("short", ClassUtils.getName(short.class));
        Assertions.assertEquals("int", ClassUtils.getName(int.class));
        Assertions.assertEquals("long", ClassUtils.getName(long.class));
        Assertions.assertEquals("float", ClassUtils.getName(float.class));
        Assertions.assertEquals("double", ClassUtils.getName(double.class));
        // Primitive Arrays
        Assertions.assertEquals("[Z", ClassUtils.getName(boolean[].class));
        Assertions.assertEquals("[B", ClassUtils.getName(byte[].class));
        Assertions.assertEquals("[C", ClassUtils.getName(char[].class));
        Assertions.assertEquals("[S", ClassUtils.getName(short[].class));
        Assertions.assertEquals("[I", ClassUtils.getName(int[].class));
        Assertions.assertEquals("[J", ClassUtils.getName(long[].class));
        Assertions.assertEquals("[F", ClassUtils.getName(float[].class));
        Assertions.assertEquals("[D", ClassUtils.getName(double[].class));
        // Arrays of arrays of ...
        Assertions.assertEquals("[[Ljava.lang.String;", ClassUtils.getName(String[][].class));
        Assertions.assertEquals("[[[Ljava.lang.String;", ClassUtils.getName(String[][][].class));
        Assertions.assertEquals("[[[[Ljava.lang.String;", ClassUtils.getName(String[][][][].class));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest$3", ClassUtils.getName(new Object() {}.getClass()));
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest$3Named", ClassUtils.getName(Named.class));
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest$Inner", ClassUtils.getName(ClassUtilsTest.Inner.class));
    }

    @Test
    public void test_getName_Object() {
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtils", ClassUtils.getName(new ClassUtils(), "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest$Inner", ClassUtils.getName(new ClassUtilsTest.Inner(), "<null>"));
        Assertions.assertEquals("java.lang.String", ClassUtils.getName("hello", "<null>"));
        Assertions.assertEquals("<null>", ClassUtils.getName(null, "<null>"));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest$4", ClassUtils.getName(new Object() {}, "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest$4Named", ClassUtils.getName(new Named(), "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3.ClassUtilsTest$Inner", ClassUtils.getName(new ClassUtilsTest.Inner(), "<null>"));
    }

    @Test
    public void test_getPackageCanonicalName_Class() {
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(ClassUtils.class));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(ClassUtils[].class));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(ClassUtils[][].class));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName(int[].class));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName(int[][].class));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(new Object() {}.getClass()));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(Named.class));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(ClassUtilsTest.Inner.class));
    }

    @Test
    public void test_getPackageCanonicalName_Object() {
        Assertions.assertEquals("<null>", ClassUtils.getPackageCanonicalName(null, "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(new ClassUtils(), "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(new ClassUtils[0], "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(new ClassUtils[0][0], "<null>"));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName(new int[0], "<null>"));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName(new int[0][0], "<null>"));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(new Object() {}, "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(new Named(), "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName(new ClassUtilsTest.Inner(), "<null>"));
    }

    @Test
    public void test_getPackageCanonicalName_String() {
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("org.apache.commons.lang3.ClassUtils"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("[Lorg.apache.commons.lang3.ClassUtils;"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("[[Lorg.apache.commons.lang3.ClassUtils;"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("org.apache.commons.lang3.ClassUtils[]"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("org.apache.commons.lang3.ClassUtils[][]"));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName("[I"));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName("[[I"));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName("int[]"));
        Assertions.assertEquals("", ClassUtils.getPackageCanonicalName("int[][]"));
        // Inner types
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("org.apache.commons.lang3.ClassUtilsTest$6"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("org.apache.commons.lang3.ClassUtilsTest$5Named"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageCanonicalName("org.apache.commons.lang3.ClassUtilsTest$Inner"));
    }

    @Test
    public void test_getPackageName_Class() {
        Assertions.assertEquals("java.lang", ClassUtils.getPackageName(String.class));
        Assertions.assertEquals("java.util", ClassUtils.getPackageName(Map.Entry.class));
        Assertions.assertEquals("", ClassUtils.getPackageName(((Class<?>) (null))));
        // LANG-535
        Assertions.assertEquals("java.lang", ClassUtils.getPackageName(String[].class));
        // Primitive Arrays
        Assertions.assertEquals("", ClassUtils.getPackageName(boolean[].class));
        Assertions.assertEquals("", ClassUtils.getPackageName(byte[].class));
        Assertions.assertEquals("", ClassUtils.getPackageName(char[].class));
        Assertions.assertEquals("", ClassUtils.getPackageName(short[].class));
        Assertions.assertEquals("", ClassUtils.getPackageName(int[].class));
        Assertions.assertEquals("", ClassUtils.getPackageName(long[].class));
        Assertions.assertEquals("", ClassUtils.getPackageName(float[].class));
        Assertions.assertEquals("", ClassUtils.getPackageName(double[].class));
        // Arrays of arrays of ...
        Assertions.assertEquals("java.lang", ClassUtils.getPackageName(String[][].class));
        Assertions.assertEquals("java.lang", ClassUtils.getPackageName(String[][][].class));
        Assertions.assertEquals("java.lang", ClassUtils.getPackageName(String[][][][].class));
        // On-the-fly types
        // empty
        class Named {}
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageName(new Object() {}.getClass()));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageName(Named.class));
    }

    // -------------------------------------------------------------------------
    @Test
    public void test_getPackageName_Object() {
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageName(new ClassUtils(), "<null>"));
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageName(new ClassUtilsTest.Inner(), "<null>"));
        Assertions.assertEquals("<null>", ClassUtils.getPackageName(null, "<null>"));
    }

    @Test
    public void test_getPackageName_String() {
        Assertions.assertEquals("org.apache.commons.lang3", ClassUtils.getPackageName(ClassUtils.class.getName()));
        Assertions.assertEquals("java.util", ClassUtils.getPackageName(Map.Entry.class.getName()));
        Assertions.assertEquals("", ClassUtils.getPackageName(((String) (null))));
        Assertions.assertEquals("", ClassUtils.getPackageName(""));
    }

    @Test
    public void test_getShortCanonicalName_Class() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getShortCanonicalName(ClassUtils.class));
        Assertions.assertEquals("ClassUtils[]", ClassUtils.getShortCanonicalName(ClassUtils[].class));
        Assertions.assertEquals("ClassUtils[][]", ClassUtils.getShortCanonicalName(ClassUtils[][].class));
        Assertions.assertEquals("int[]", ClassUtils.getShortCanonicalName(int[].class));
        Assertions.assertEquals("int[][]", ClassUtils.getShortCanonicalName(int[][].class));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("ClassUtilsTest.8", ClassUtils.getShortCanonicalName(new Object() {}.getClass()));
        Assertions.assertEquals("ClassUtilsTest.8Named", ClassUtils.getShortCanonicalName(Named.class));
        Assertions.assertEquals("ClassUtilsTest.Inner", ClassUtils.getShortCanonicalName(ClassUtilsTest.Inner.class));
    }

    @Test
    public void test_getShortCanonicalName_Object() {
        Assertions.assertEquals("<null>", ClassUtils.getShortCanonicalName(null, "<null>"));
        Assertions.assertEquals("ClassUtils", ClassUtils.getShortCanonicalName(new ClassUtils(), "<null>"));
        Assertions.assertEquals("ClassUtils[]", ClassUtils.getShortCanonicalName(new ClassUtils[0], "<null>"));
        Assertions.assertEquals("ClassUtils[][]", ClassUtils.getShortCanonicalName(new ClassUtils[0][0], "<null>"));
        Assertions.assertEquals("int[]", ClassUtils.getShortCanonicalName(new int[0], "<null>"));
        Assertions.assertEquals("int[][]", ClassUtils.getShortCanonicalName(new int[0][0], "<null>"));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("ClassUtilsTest.9", ClassUtils.getShortCanonicalName(new Object() {}, "<null>"));
        Assertions.assertEquals("ClassUtilsTest.9Named", ClassUtils.getShortCanonicalName(new Named(), "<null>"));
        Assertions.assertEquals("ClassUtilsTest.Inner", ClassUtils.getShortCanonicalName(new ClassUtilsTest.Inner(), "<null>"));
    }

    @Test
    public void test_getShortCanonicalName_String() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getShortCanonicalName("org.apache.commons.lang3.ClassUtils"));
        Assertions.assertEquals("ClassUtils[]", ClassUtils.getShortCanonicalName("[Lorg.apache.commons.lang3.ClassUtils;"));
        Assertions.assertEquals("ClassUtils[][]", ClassUtils.getShortCanonicalName("[[Lorg.apache.commons.lang3.ClassUtils;"));
        Assertions.assertEquals("ClassUtils[]", ClassUtils.getShortCanonicalName("org.apache.commons.lang3.ClassUtils[]"));
        Assertions.assertEquals("ClassUtils[][]", ClassUtils.getShortCanonicalName("org.apache.commons.lang3.ClassUtils[][]"));
        Assertions.assertEquals("int[]", ClassUtils.getShortCanonicalName("[I"));
        Assertions.assertEquals("int[][]", ClassUtils.getShortCanonicalName("[[I"));
        Assertions.assertEquals("int[]", ClassUtils.getShortCanonicalName("int[]"));
        Assertions.assertEquals("int[][]", ClassUtils.getShortCanonicalName("int[][]"));
        // Inner types
        Assertions.assertEquals("ClassUtilsTest.6", ClassUtils.getShortCanonicalName("org.apache.commons.lang3.ClassUtilsTest$6"));
        Assertions.assertEquals("ClassUtilsTest.5Named", ClassUtils.getShortCanonicalName("org.apache.commons.lang3.ClassUtilsTest$5Named"));
        Assertions.assertEquals("ClassUtilsTest.Inner", ClassUtils.getShortCanonicalName("org.apache.commons.lang3.ClassUtilsTest$Inner"));
    }

    @Test
    public void test_getShortClassName_Class() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getShortClassName(ClassUtils.class));
        Assertions.assertEquals("Map.Entry", ClassUtils.getShortClassName(Map.Entry.class));
        Assertions.assertEquals("", ClassUtils.getShortClassName(((Class<?>) (null))));
        // LANG-535
        Assertions.assertEquals("String[]", ClassUtils.getShortClassName(String[].class));
        Assertions.assertEquals("Map.Entry[]", ClassUtils.getShortClassName(Map.Entry[].class));
        // Primitives
        Assertions.assertEquals("boolean", ClassUtils.getShortClassName(boolean.class));
        Assertions.assertEquals("byte", ClassUtils.getShortClassName(byte.class));
        Assertions.assertEquals("char", ClassUtils.getShortClassName(char.class));
        Assertions.assertEquals("short", ClassUtils.getShortClassName(short.class));
        Assertions.assertEquals("int", ClassUtils.getShortClassName(int.class));
        Assertions.assertEquals("long", ClassUtils.getShortClassName(long.class));
        Assertions.assertEquals("float", ClassUtils.getShortClassName(float.class));
        Assertions.assertEquals("double", ClassUtils.getShortClassName(double.class));
        // Primitive Arrays
        Assertions.assertEquals("boolean[]", ClassUtils.getShortClassName(boolean[].class));
        Assertions.assertEquals("byte[]", ClassUtils.getShortClassName(byte[].class));
        Assertions.assertEquals("char[]", ClassUtils.getShortClassName(char[].class));
        Assertions.assertEquals("short[]", ClassUtils.getShortClassName(short[].class));
        Assertions.assertEquals("int[]", ClassUtils.getShortClassName(int[].class));
        Assertions.assertEquals("long[]", ClassUtils.getShortClassName(long[].class));
        Assertions.assertEquals("float[]", ClassUtils.getShortClassName(float[].class));
        Assertions.assertEquals("double[]", ClassUtils.getShortClassName(double[].class));
        // Arrays of arrays of ...
        Assertions.assertEquals("String[][]", ClassUtils.getShortClassName(String[][].class));
        Assertions.assertEquals("String[][][]", ClassUtils.getShortClassName(String[][][].class));
        Assertions.assertEquals("String[][][][]", ClassUtils.getShortClassName(String[][][][].class));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("ClassUtilsTest.10", ClassUtils.getShortClassName(new Object() {}.getClass()));
        Assertions.assertEquals("ClassUtilsTest.10Named", ClassUtils.getShortClassName(Named.class));
        Assertions.assertEquals("ClassUtilsTest.Inner", ClassUtils.getShortClassName(ClassUtilsTest.Inner.class));
    }

    // -------------------------------------------------------------------------
    @Test
    public void test_getShortClassName_Object() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getShortClassName(new ClassUtils(), "<null>"));
        Assertions.assertEquals("ClassUtilsTest.Inner", ClassUtils.getShortClassName(new ClassUtilsTest.Inner(), "<null>"));
        Assertions.assertEquals("String", ClassUtils.getShortClassName("hello", "<null>"));
        Assertions.assertEquals("<null>", ClassUtils.getShortClassName(null, "<null>"));
        // Inner types
        // empty
        class Named {}
        Assertions.assertEquals("ClassUtilsTest.11", ClassUtils.getShortClassName(new Object() {}, "<null>"));
        Assertions.assertEquals("ClassUtilsTest.11Named", ClassUtils.getShortClassName(new Named(), "<null>"));
        Assertions.assertEquals("ClassUtilsTest.Inner", ClassUtils.getShortClassName(new ClassUtilsTest.Inner(), "<null>"));
    }

    @Test
    public void test_getShortClassName_String() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getShortClassName(ClassUtils.class.getName()));
        Assertions.assertEquals("Map.Entry", ClassUtils.getShortClassName(Map.Entry.class.getName()));
        Assertions.assertEquals("", ClassUtils.getShortClassName(((String) (null))));
        Assertions.assertEquals("", ClassUtils.getShortClassName(""));
    }

    @Test
    public void test_getSimpleName_Class() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getSimpleName(ClassUtils.class));
        Assertions.assertEquals("Entry", ClassUtils.getSimpleName(Map.Entry.class));
        Assertions.assertEquals("", ClassUtils.getSimpleName(null));
        // LANG-535
        Assertions.assertEquals("String[]", ClassUtils.getSimpleName(String[].class));
        Assertions.assertEquals("Entry[]", ClassUtils.getSimpleName(Map.Entry[].class));
        // Primitives
        Assertions.assertEquals("boolean", ClassUtils.getSimpleName(boolean.class));
        Assertions.assertEquals("byte", ClassUtils.getSimpleName(byte.class));
        Assertions.assertEquals("char", ClassUtils.getSimpleName(char.class));
        Assertions.assertEquals("short", ClassUtils.getSimpleName(short.class));
        Assertions.assertEquals("int", ClassUtils.getSimpleName(int.class));
        Assertions.assertEquals("long", ClassUtils.getSimpleName(long.class));
        Assertions.assertEquals("float", ClassUtils.getSimpleName(float.class));
        Assertions.assertEquals("double", ClassUtils.getSimpleName(double.class));
        // Primitive Arrays
        Assertions.assertEquals("boolean[]", ClassUtils.getSimpleName(boolean[].class));
        Assertions.assertEquals("byte[]", ClassUtils.getSimpleName(byte[].class));
        Assertions.assertEquals("char[]", ClassUtils.getSimpleName(char[].class));
        Assertions.assertEquals("short[]", ClassUtils.getSimpleName(short[].class));
        Assertions.assertEquals("int[]", ClassUtils.getSimpleName(int[].class));
        Assertions.assertEquals("long[]", ClassUtils.getSimpleName(long[].class));
        Assertions.assertEquals("float[]", ClassUtils.getSimpleName(float[].class));
        Assertions.assertEquals("double[]", ClassUtils.getSimpleName(double[].class));
        // Arrays of arrays of ...
        Assertions.assertEquals("String[][]", ClassUtils.getSimpleName(String[][].class));
        Assertions.assertEquals("String[][][]", ClassUtils.getSimpleName(String[][][].class));
        Assertions.assertEquals("String[][][][]", ClassUtils.getSimpleName(String[][][][].class));
        // On-the-fly types
        // empty
        class Named {}
        Assertions.assertEquals("", ClassUtils.getSimpleName(new Object() {}.getClass()));
        Assertions.assertEquals("Named", ClassUtils.getSimpleName(Named.class));
    }

    @Test
    public void test_getSimpleName_Object() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getSimpleName(new ClassUtils()));
        Assertions.assertEquals("Inner", ClassUtils.getSimpleName(new ClassUtilsTest.Inner()));
        Assertions.assertEquals("String", ClassUtils.getSimpleName("hello"));
        Assertions.assertEquals(StringUtils.EMPTY, ClassUtils.getSimpleName(null));
        Assertions.assertEquals(StringUtils.EMPTY, ClassUtils.getSimpleName(null));
    }

    @Test
    public void test_getSimpleName_Object_String() {
        Assertions.assertEquals("ClassUtils", ClassUtils.getSimpleName(new ClassUtils(), "<null>"));
        Assertions.assertEquals("Inner", ClassUtils.getSimpleName(new ClassUtilsTest.Inner(), "<null>"));
        Assertions.assertEquals("String", ClassUtils.getSimpleName("hello", "<null>"));
        Assertions.assertEquals("<null>", ClassUtils.getSimpleName(null, "<null>"));
        Assertions.assertNull(ClassUtils.getSimpleName(null, null));
    }

    @Test
    public void test_isAssignable() {
        Assertions.assertFalse(ClassUtils.isAssignable(((Class<?>) (null)), null));
        Assertions.assertFalse(ClassUtils.isAssignable(String.class, null));
        Assertions.assertTrue(ClassUtils.isAssignable(null, Object.class));
        Assertions.assertTrue(ClassUtils.isAssignable(null, Integer.class));
        Assertions.assertFalse(ClassUtils.isAssignable(null, Integer.TYPE));
        Assertions.assertTrue(ClassUtils.isAssignable(String.class, Object.class));
        Assertions.assertTrue(ClassUtils.isAssignable(String.class, String.class));
        Assertions.assertFalse(ClassUtils.isAssignable(Object.class, String.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Integer.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Object.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.TYPE));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Object.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Integer.TYPE));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.TYPE, Boolean.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.TYPE, Object.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Boolean.TYPE));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Object.class));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.TYPE, Boolean.TYPE));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Boolean.class));
    }

    @Test
    public void test_isAssignable_Autoboxing() {
        Assertions.assertFalse(ClassUtils.isAssignable(((Class<?>) (null)), null, true));
        Assertions.assertFalse(ClassUtils.isAssignable(String.class, null, true));
        Assertions.assertTrue(ClassUtils.isAssignable(null, Object.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(null, Integer.class, true));
        Assertions.assertFalse(ClassUtils.isAssignable(null, Integer.TYPE, true));
        Assertions.assertTrue(ClassUtils.isAssignable(String.class, Object.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(String.class, String.class, true));
        Assertions.assertFalse(ClassUtils.isAssignable(Object.class, String.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Integer.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Object.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.TYPE, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Object.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Integer.TYPE, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.TYPE, Boolean.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Boolean.TYPE, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Object.class, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.TYPE, Boolean.TYPE, true));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Boolean.class, true));
    }

    // -------------------------------------------------------------------------
    @Test
    public void test_isAssignable_ClassArray_ClassArray() {
        final Class<?>[] array2 = new Class[]{ Object.class, Object.class };
        final Class<?>[] array1 = new Class[]{ Object.class };
        final Class<?>[] array1s = new Class[]{ String.class };
        final Class<?>[] array0 = new Class[]{  };
        final Class<?>[] arrayPrimitives = new Class<?>[]{ Integer.TYPE, Boolean.TYPE };
        final Class<?>[] arrayWrappers = new Class<?>[]{ Integer.class, Boolean.class };
        Assertions.assertFalse(ClassUtils.isAssignable(array1, array2));
        Assertions.assertFalse(ClassUtils.isAssignable(null, array2));
        Assertions.assertTrue(ClassUtils.isAssignable(null, array0));
        Assertions.assertTrue(ClassUtils.isAssignable(array0, array0));
        Assertions.assertTrue(ClassUtils.isAssignable(array0, ((Class<?>[]) (null))));// explicit cast to avoid warning

        Assertions.assertTrue(ClassUtils.isAssignable(null, ((Class<?>[]) (null))));// explicit cast to avoid warning

        Assertions.assertFalse(ClassUtils.isAssignable(array1, array1s));
        Assertions.assertTrue(ClassUtils.isAssignable(array1s, array1s));
        Assertions.assertTrue(ClassUtils.isAssignable(array1s, array1));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayPrimitives, arrayWrappers));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayWrappers, arrayPrimitives));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayPrimitives, array1));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayWrappers, array1));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayPrimitives, array2));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayWrappers, array2));
    }

    @Test
    public void test_isAssignable_ClassArray_ClassArray_Autoboxing() {
        final Class<?>[] array2 = new Class[]{ Object.class, Object.class };
        final Class<?>[] array1 = new Class[]{ Object.class };
        final Class<?>[] array1s = new Class[]{ String.class };
        final Class<?>[] array0 = new Class[]{  };
        final Class<?>[] arrayPrimitives = new Class<?>[]{ Integer.TYPE, Boolean.TYPE };
        final Class<?>[] arrayWrappers = new Class<?>[]{ Integer.class, Boolean.class };
        Assertions.assertFalse(ClassUtils.isAssignable(array1, array2, true));
        Assertions.assertFalse(ClassUtils.isAssignable(null, array2, true));
        Assertions.assertTrue(ClassUtils.isAssignable(null, array0, true));
        Assertions.assertTrue(ClassUtils.isAssignable(array0, array0, true));
        Assertions.assertTrue(ClassUtils.isAssignable(array0, null, true));
        Assertions.assertTrue(ClassUtils.isAssignable(((Class[]) (null)), null, true));
        Assertions.assertFalse(ClassUtils.isAssignable(array1, array1s, true));
        Assertions.assertTrue(ClassUtils.isAssignable(array1s, array1s, true));
        Assertions.assertTrue(ClassUtils.isAssignable(array1s, array1, true));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayPrimitives, arrayWrappers, true));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayWrappers, arrayPrimitives, true));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayPrimitives, array1, true));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayWrappers, array1, true));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayPrimitives, array2, true));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayWrappers, array2, true));
    }

    @Test
    public void test_isAssignable_ClassArray_ClassArray_NoAutoboxing() {
        final Class<?>[] array2 = new Class[]{ Object.class, Object.class };
        final Class<?>[] array1 = new Class[]{ Object.class };
        final Class<?>[] array1s = new Class[]{ String.class };
        final Class<?>[] array0 = new Class[]{  };
        final Class<?>[] arrayPrimitives = new Class<?>[]{ Integer.TYPE, Boolean.TYPE };
        final Class<?>[] arrayWrappers = new Class<?>[]{ Integer.class, Boolean.class };
        Assertions.assertFalse(ClassUtils.isAssignable(array1, array2, false));
        Assertions.assertFalse(ClassUtils.isAssignable(null, array2, false));
        Assertions.assertTrue(ClassUtils.isAssignable(null, array0, false));
        Assertions.assertTrue(ClassUtils.isAssignable(array0, array0, false));
        Assertions.assertTrue(ClassUtils.isAssignable(array0, null, false));
        Assertions.assertTrue(ClassUtils.isAssignable(((Class[]) (null)), null, false));
        Assertions.assertFalse(ClassUtils.isAssignable(array1, array1s, false));
        Assertions.assertTrue(ClassUtils.isAssignable(array1s, array1s, false));
        Assertions.assertTrue(ClassUtils.isAssignable(array1s, array1, false));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayPrimitives, arrayWrappers, false));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayWrappers, arrayPrimitives, false));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayPrimitives, array1, false));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayWrappers, array1, false));
        Assertions.assertTrue(ClassUtils.isAssignable(arrayWrappers, array2, false));
        Assertions.assertFalse(ClassUtils.isAssignable(arrayPrimitives, array2, false));
    }

    @Test
    public void test_isAssignable_DefaultUnboxing_Widening() {
        // test byte conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Byte.class, Character.TYPE), "byte -> char");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Byte.TYPE), "byte -> byte");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Short.TYPE), "byte -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Integer.TYPE), "byte -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Long.TYPE), "byte -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Float.TYPE), "byte -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Double.TYPE), "byte -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Byte.class, Boolean.TYPE), "byte -> boolean");
        // test short conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Short.class, Character.TYPE), "short -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Short.class, Byte.TYPE), "short -> byte");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Short.TYPE), "short -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Integer.TYPE), "short -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Long.TYPE), "short -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Float.TYPE), "short -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Double.TYPE), "short -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Short.class, Boolean.TYPE), "short -> boolean");
        // test char conversions
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Character.TYPE), "char -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.class, Byte.TYPE), "char -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.class, Short.TYPE), "char -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Integer.TYPE), "char -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Long.TYPE), "char -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Float.TYPE), "char -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Double.TYPE), "char -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.class, Boolean.TYPE), "char -> boolean");
        // test int conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Character.TYPE), "int -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Byte.TYPE), "int -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Short.TYPE), "int -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.TYPE), "int -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Long.TYPE), "int -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Float.TYPE), "int -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Double.TYPE), "int -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Boolean.TYPE), "int -> boolean");
        // test long conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Character.TYPE), "long -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Byte.TYPE), "long -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Short.TYPE), "long -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Integer.TYPE), "long -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.class, Long.TYPE), "long -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.class, Float.TYPE), "long -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.class, Double.TYPE), "long -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Boolean.TYPE), "long -> boolean");
        // test float conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Character.TYPE), "float -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Byte.TYPE), "float -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Short.TYPE), "float -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Integer.TYPE), "float -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Long.TYPE), "float -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Float.class, Float.TYPE), "float -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Float.class, Double.TYPE), "float -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Boolean.TYPE), "float -> boolean");
        // test double conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Character.TYPE), "double -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Byte.TYPE), "double -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Short.TYPE), "double -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Integer.TYPE), "double -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Long.TYPE), "double -> long");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Float.TYPE), "double -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Double.class, Double.TYPE), "double -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Boolean.TYPE), "double -> boolean");
        // test boolean conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Character.TYPE), "boolean -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Byte.TYPE), "boolean -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Short.TYPE), "boolean -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Integer.TYPE), "boolean -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Long.TYPE), "boolean -> long");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Float.TYPE), "boolean -> float");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Double.TYPE), "boolean -> double");
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Boolean.TYPE), "boolean -> boolean");
    }

    @Test
    public void test_isAssignable_NoAutoboxing() {
        Assertions.assertFalse(ClassUtils.isAssignable(((Class<?>) (null)), null, false));
        Assertions.assertFalse(ClassUtils.isAssignable(String.class, null, false));
        Assertions.assertTrue(ClassUtils.isAssignable(null, Object.class, false));
        Assertions.assertTrue(ClassUtils.isAssignable(null, Integer.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(null, Integer.TYPE, false));
        Assertions.assertTrue(ClassUtils.isAssignable(String.class, Object.class, false));
        Assertions.assertTrue(ClassUtils.isAssignable(String.class, String.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(Object.class, String.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.TYPE, Integer.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.TYPE, Object.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Integer.TYPE, false));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Integer.TYPE, false));
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Boolean.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Object.class, false));
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Boolean.TYPE, false));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Object.class, false));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.TYPE, Boolean.TYPE, false));
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Boolean.class, false));
    }

    @Test
    public void test_isAssignable_Unboxing_Widening() {
        // test byte conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Byte.class, Character.TYPE, true), "byte -> char");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Byte.TYPE, true), "byte -> byte");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Short.TYPE, true), "byte -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Integer.TYPE, true), "byte -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Long.TYPE, true), "byte -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Float.TYPE, true), "byte -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.class, Double.TYPE, true), "byte -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Byte.class, Boolean.TYPE, true), "byte -> boolean");
        // test short conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Short.class, Character.TYPE, true), "short -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Short.class, Byte.TYPE, true), "short -> byte");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Short.TYPE, true), "short -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Integer.TYPE, true), "short -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Long.TYPE, true), "short -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Float.TYPE, true), "short -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.class, Double.TYPE, true), "short -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Short.class, Boolean.TYPE, true), "short -> boolean");
        // test char conversions
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Character.TYPE, true), "char -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.class, Byte.TYPE, true), "char -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.class, Short.TYPE, true), "char -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Integer.TYPE, true), "char -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Long.TYPE, true), "char -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Float.TYPE, true), "char -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.class, Double.TYPE, true), "char -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.class, Boolean.TYPE, true), "char -> boolean");
        // test int conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Character.TYPE, true), "int -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Byte.TYPE, true), "int -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Short.TYPE, true), "int -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.TYPE, true), "int -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Long.TYPE, true), "int -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Float.TYPE, true), "int -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.class, Double.TYPE, true), "int -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.class, Boolean.TYPE, true), "int -> boolean");
        // test long conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Character.TYPE, true), "long -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Byte.TYPE, true), "long -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Short.TYPE, true), "long -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Integer.TYPE, true), "long -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.class, Long.TYPE, true), "long -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.class, Float.TYPE, true), "long -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.class, Double.TYPE, true), "long -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.class, Boolean.TYPE, true), "long -> boolean");
        // test float conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Character.TYPE, true), "float -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Byte.TYPE, true), "float -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Short.TYPE, true), "float -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Integer.TYPE, true), "float -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Long.TYPE, true), "float -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Float.class, Float.TYPE, true), "float -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Float.class, Double.TYPE, true), "float -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.class, Boolean.TYPE, true), "float -> boolean");
        // test double conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Character.TYPE, true), "double -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Byte.TYPE, true), "double -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Short.TYPE, true), "double -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Integer.TYPE, true), "double -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Long.TYPE, true), "double -> long");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Float.TYPE, true), "double -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Double.class, Double.TYPE, true), "double -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.class, Boolean.TYPE, true), "double -> boolean");
        // test boolean conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Character.TYPE, true), "boolean -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Byte.TYPE, true), "boolean -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Short.TYPE, true), "boolean -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Integer.TYPE, true), "boolean -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Long.TYPE, true), "boolean -> long");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Float.TYPE, true), "boolean -> float");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.class, Double.TYPE, true), "boolean -> double");
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.class, Boolean.TYPE, true), "boolean -> boolean");
    }

    @Test
    public void test_isAssignable_Widening() {
        // test byte conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Byte.TYPE, Character.TYPE), "byte -> char");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.TYPE, Byte.TYPE), "byte -> byte");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.TYPE, Short.TYPE), "byte -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.TYPE, Integer.TYPE), "byte -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.TYPE, Long.TYPE), "byte -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.TYPE, Float.TYPE), "byte -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Byte.TYPE, Double.TYPE), "byte -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Byte.TYPE, Boolean.TYPE), "byte -> boolean");
        // test short conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Short.TYPE, Character.TYPE), "short -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Short.TYPE, Byte.TYPE), "short -> byte");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.TYPE, Short.TYPE), "short -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.TYPE, Integer.TYPE), "short -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.TYPE, Long.TYPE), "short -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.TYPE, Float.TYPE), "short -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Short.TYPE, Double.TYPE), "short -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Short.TYPE, Boolean.TYPE), "short -> boolean");
        // test char conversions
        Assertions.assertTrue(ClassUtils.isAssignable(Character.TYPE, Character.TYPE), "char -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.TYPE, Byte.TYPE), "char -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.TYPE, Short.TYPE), "char -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.TYPE, Integer.TYPE), "char -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.TYPE, Long.TYPE), "char -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.TYPE, Float.TYPE), "char -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Character.TYPE, Double.TYPE), "char -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Character.TYPE, Boolean.TYPE), "char -> boolean");
        // test int conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.TYPE, Character.TYPE), "int -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.TYPE, Byte.TYPE), "int -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.TYPE, Short.TYPE), "int -> short");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Integer.TYPE), "int -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Long.TYPE), "int -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Float.TYPE), "int -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Integer.TYPE, Double.TYPE), "int -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Integer.TYPE, Boolean.TYPE), "int -> boolean");
        // test long conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Long.TYPE, Character.TYPE), "long -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.TYPE, Byte.TYPE), "long -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.TYPE, Short.TYPE), "long -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.TYPE, Integer.TYPE), "long -> int");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.TYPE, Long.TYPE), "long -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.TYPE, Float.TYPE), "long -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Long.TYPE, Double.TYPE), "long -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Long.TYPE, Boolean.TYPE), "long -> boolean");
        // test float conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Float.TYPE, Character.TYPE), "float -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.TYPE, Byte.TYPE), "float -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.TYPE, Short.TYPE), "float -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.TYPE, Integer.TYPE), "float -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.TYPE, Long.TYPE), "float -> long");
        Assertions.assertTrue(ClassUtils.isAssignable(Float.TYPE, Float.TYPE), "float -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Float.TYPE, Double.TYPE), "float -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Float.TYPE, Boolean.TYPE), "float -> boolean");
        // test double conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Double.TYPE, Character.TYPE), "double -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.TYPE, Byte.TYPE), "double -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.TYPE, Short.TYPE), "double -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.TYPE, Integer.TYPE), "double -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.TYPE, Long.TYPE), "double -> long");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.TYPE, Float.TYPE), "double -> float");
        Assertions.assertTrue(ClassUtils.isAssignable(Double.TYPE, Double.TYPE), "double -> double");
        Assertions.assertFalse(ClassUtils.isAssignable(Double.TYPE, Boolean.TYPE), "double -> boolean");
        // test boolean conversions
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Character.TYPE), "boolean -> char");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Byte.TYPE), "boolean -> byte");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Short.TYPE), "boolean -> short");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Integer.TYPE), "boolean -> int");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Long.TYPE), "boolean -> long");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Float.TYPE), "boolean -> float");
        Assertions.assertFalse(ClassUtils.isAssignable(Boolean.TYPE, Double.TYPE), "boolean -> double");
        Assertions.assertTrue(ClassUtils.isAssignable(Boolean.TYPE, Boolean.TYPE), "boolean -> boolean");
    }

    // -------------------------------------------------------------------------
    @Test
    public void test_isInnerClass_Class() {
        Assertions.assertTrue(ClassUtils.isInnerClass(ClassUtilsTest.Inner.class));
        Assertions.assertTrue(ClassUtils.isInnerClass(Map.Entry.class));
        Assertions.assertTrue(ClassUtils.isInnerClass(new Cloneable() {}.getClass()));
        Assertions.assertFalse(ClassUtils.isInnerClass(this.getClass()));
        Assertions.assertFalse(ClassUtils.isInnerClass(String.class));
        Assertions.assertFalse(ClassUtils.isInnerClass(null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new ClassUtils());
        final Constructor<?>[] cons = ClassUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(ClassUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(ClassUtils.class.getModifiers()));
    }

    @Test
    public void testGetClassByNormalNameArrays() throws ClassNotFoundException {
        Assertions.assertEquals(int[].class, ClassUtils.getClass("int[]"));
        Assertions.assertEquals(long[].class, ClassUtils.getClass("long[]"));
        Assertions.assertEquals(short[].class, ClassUtils.getClass("short[]"));
        Assertions.assertEquals(byte[].class, ClassUtils.getClass("byte[]"));
        Assertions.assertEquals(char[].class, ClassUtils.getClass("char[]"));
        Assertions.assertEquals(float[].class, ClassUtils.getClass("float[]"));
        Assertions.assertEquals(double[].class, ClassUtils.getClass("double[]"));
        Assertions.assertEquals(boolean[].class, ClassUtils.getClass("boolean[]"));
        Assertions.assertEquals(String[].class, ClassUtils.getClass("java.lang.String[]"));
        Assertions.assertEquals(Map.Entry[].class, ClassUtils.getClass("java.util.Map.Entry[]"));
        Assertions.assertEquals(Map.Entry[].class, ClassUtils.getClass("java.util.Map$Entry[]"));
        Assertions.assertEquals(Map.Entry[].class, ClassUtils.getClass("[Ljava.util.Map.Entry;"));
        Assertions.assertEquals(Map.Entry[].class, ClassUtils.getClass("[Ljava.util.Map$Entry;"));
    }

    @Test
    public void testGetClassByNormalNameArrays2D() throws ClassNotFoundException {
        Assertions.assertEquals(int[][].class, ClassUtils.getClass("int[][]"));
        Assertions.assertEquals(long[][].class, ClassUtils.getClass("long[][]"));
        Assertions.assertEquals(short[][].class, ClassUtils.getClass("short[][]"));
        Assertions.assertEquals(byte[][].class, ClassUtils.getClass("byte[][]"));
        Assertions.assertEquals(char[][].class, ClassUtils.getClass("char[][]"));
        Assertions.assertEquals(float[][].class, ClassUtils.getClass("float[][]"));
        Assertions.assertEquals(double[][].class, ClassUtils.getClass("double[][]"));
        Assertions.assertEquals(boolean[][].class, ClassUtils.getClass("boolean[][]"));
        Assertions.assertEquals(String[][].class, ClassUtils.getClass("java.lang.String[][]"));
    }

    @Test
    public void testGetClassClassNotFound() throws Exception {
        assertGetClassThrowsClassNotFound("bool");
        assertGetClassThrowsClassNotFound("bool[]");
        assertGetClassThrowsClassNotFound("integer[]");
    }

    @Test
    public void testGetClassInvalidArguments() throws Exception {
        assertGetClassThrowsNullPointerException(null);
        assertGetClassThrowsClassNotFound("[][][]");
        assertGetClassThrowsClassNotFound("[[]");
        assertGetClassThrowsClassNotFound("[");
        assertGetClassThrowsClassNotFound("java.lang.String][");
        assertGetClassThrowsClassNotFound(".hello.world");
        assertGetClassThrowsClassNotFound("hello..world");
    }

    @Test
    public void testGetClassRawPrimitives() throws ClassNotFoundException {
        Assertions.assertEquals(int.class, ClassUtils.getClass("int"));
        Assertions.assertEquals(long.class, ClassUtils.getClass("long"));
        Assertions.assertEquals(short.class, ClassUtils.getClass("short"));
        Assertions.assertEquals(byte.class, ClassUtils.getClass("byte"));
        Assertions.assertEquals(char.class, ClassUtils.getClass("char"));
        Assertions.assertEquals(float.class, ClassUtils.getClass("float"));
        Assertions.assertEquals(double.class, ClassUtils.getClass("double"));
        Assertions.assertEquals(boolean.class, ClassUtils.getClass("boolean"));
        Assertions.assertEquals(void.class, ClassUtils.getClass("void"));
    }

    @Test
    public void testGetClassWithArrayClasses() throws Exception {
        assertGetClassReturnsClass(String[].class);
        assertGetClassReturnsClass(int[].class);
        assertGetClassReturnsClass(long[].class);
        assertGetClassReturnsClass(short[].class);
        assertGetClassReturnsClass(byte[].class);
        assertGetClassReturnsClass(char[].class);
        assertGetClassReturnsClass(float[].class);
        assertGetClassReturnsClass(double[].class);
        assertGetClassReturnsClass(boolean[].class);
    }

    @Test
    public void testGetClassWithArrayClasses2D() throws Exception {
        assertGetClassReturnsClass(String[][].class);
        assertGetClassReturnsClass(int[][].class);
        assertGetClassReturnsClass(long[][].class);
        assertGetClassReturnsClass(short[][].class);
        assertGetClassReturnsClass(byte[][].class);
        assertGetClassReturnsClass(char[][].class);
        assertGetClassReturnsClass(float[][].class);
        assertGetClassReturnsClass(double[][].class);
        assertGetClassReturnsClass(boolean[][].class);
    }

    @Test
    public void testGetInnerClass() throws ClassNotFoundException {
        Assertions.assertEquals(ClassUtilsTest.Inner.DeeplyNested.class, ClassUtils.getClass("org.apache.commons.lang3.ClassUtilsTest.Inner.DeeplyNested"));
        Assertions.assertEquals(ClassUtilsTest.Inner.DeeplyNested.class, ClassUtils.getClass("org.apache.commons.lang3.ClassUtilsTest.Inner$DeeplyNested"));
        Assertions.assertEquals(ClassUtilsTest.Inner.DeeplyNested.class, ClassUtils.getClass("org.apache.commons.lang3.ClassUtilsTest$Inner$DeeplyNested"));
        Assertions.assertEquals(ClassUtilsTest.Inner.DeeplyNested.class, ClassUtils.getClass("org.apache.commons.lang3.ClassUtilsTest$Inner.DeeplyNested"));
    }

    @Test
    public void testGetPublicMethod() throws Exception {
        // Tests with Collections$UnmodifiableSet
        final Set<?> set = Collections.unmodifiableSet(new HashSet<>());
        final Method isEmptyMethod = ClassUtils.getPublicMethod(set.getClass(), "isEmpty");
        Assertions.assertTrue(Modifier.isPublic(isEmptyMethod.getDeclaringClass().getModifiers()));
        Assertions.assertTrue(((Boolean) (isEmptyMethod.invoke(set))));
        // Tests with a public Class
        final Method toStringMethod = ClassUtils.getPublicMethod(Object.class, "toString");
        Assertions.assertEquals(Object.class.getMethod("toString", new Class[0]), toStringMethod);
    }

    @Test
    public void testHierarchyExcludingInterfaces() {
        final Iterator<Class<?>> iter = ClassUtils.hierarchy(StringParameterizedChild.class).iterator();
        Assertions.assertEquals(StringParameterizedChild.class, iter.next());
        Assertions.assertEquals(GenericParent.class, iter.next());
        Assertions.assertEquals(Object.class, iter.next());
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    public void testHierarchyIncludingInterfaces() {
        final Iterator<Class<?>> iter = ClassUtils.hierarchy(StringParameterizedChild.class, INCLUDE).iterator();
        Assertions.assertEquals(StringParameterizedChild.class, iter.next());
        Assertions.assertEquals(GenericParent.class, iter.next());
        Assertions.assertEquals(GenericConsumer.class, iter.next());
        Assertions.assertEquals(Object.class, iter.next());
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    public void testIsPrimitiveOrWrapper() {
        // test primitive wrapper classes
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Boolean.class), "Boolean.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Byte.class), "Byte.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Character.class), "Character.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Short.class), "Short.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Integer.class), "Integer.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Long.class), "Long.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Double.class), "Double.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Float.class), "Float.class");
        // test primitive classes
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Boolean.TYPE), "boolean");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Byte.TYPE), "byte");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Character.TYPE), "char");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Short.TYPE), "short");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Integer.TYPE), "int");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Long.TYPE), "long");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Double.TYPE), "double");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Float.TYPE), "float");
        Assertions.assertTrue(ClassUtils.isPrimitiveOrWrapper(Void.TYPE), "Void.TYPE");
        // others
        Assertions.assertFalse(ClassUtils.isPrimitiveOrWrapper(null), "null");
        Assertions.assertFalse(ClassUtils.isPrimitiveOrWrapper(Void.class), "Void.class");
        Assertions.assertFalse(ClassUtils.isPrimitiveOrWrapper(String.class), "String.class");
        Assertions.assertFalse(ClassUtils.isPrimitiveOrWrapper(this.getClass()), "this.getClass()");
    }

    @Test
    public void testIsPrimitiveWrapper() {
        // test primitive wrapper classes
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Boolean.class), "Boolean.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Byte.class), "Byte.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Character.class), "Character.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Short.class), "Short.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Integer.class), "Integer.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Long.class), "Long.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Double.class), "Double.class");
        Assertions.assertTrue(ClassUtils.isPrimitiveWrapper(Float.class), "Float.class");
        // test primitive classes
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Boolean.TYPE), "boolean");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Byte.TYPE), "byte");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Character.TYPE), "char");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Short.TYPE), "short");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Integer.TYPE), "int");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Long.TYPE), "long");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Double.TYPE), "double");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Float.TYPE), "float");
        // others
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(null), "null");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Void.class), "Void.class");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(Void.TYPE), "Void.TYPE");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(String.class), "String.class");
        Assertions.assertFalse(ClassUtils.isPrimitiveWrapper(this.getClass()), "this.getClass()");
    }

    @Test
    public void testPrimitivesToWrappers() {
        // test null
        // assertNull("null -> null", ClassUtils.primitivesToWrappers(null)); // generates warning
        Assertions.assertNull(ClassUtils.primitivesToWrappers(((Class<?>[]) (null))), "null -> null");// equivalent cast to avoid warning

        // Other possible casts for null
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CLASS_ARRAY, ClassUtils.primitivesToWrappers(), "empty -> empty");
        final Class<?>[] castNull = ClassUtils.primitivesToWrappers(((Class<?>) (null)));// == new Class<?>[]{null}

        Assertions.assertArrayEquals(new Class<?>[]{ null }, castNull, "(Class<?>) null -> [null]");
        // test empty array is returned unchanged
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CLASS_ARRAY, ClassUtils.primitivesToWrappers(ArrayUtils.EMPTY_CLASS_ARRAY), "empty -> empty");
        // test an array of various classes
        final Class<?>[] primitives = new Class[]{ Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Double.TYPE, Float.TYPE, String.class, ClassUtils.class };
        final Class<?>[] wrappers = ClassUtils.primitivesToWrappers(primitives);
        for (int i = 0; i < (primitives.length); i++) {
            // test each returned wrapper
            final Class<?> primitive = primitives[i];
            final Class<?> expectedWrapper = ClassUtils.primitiveToWrapper(primitive);
            Assertions.assertEquals(expectedWrapper, wrappers[i], ((primitive + " -> ") + expectedWrapper));
        }
        // test an array of no primitive classes
        final Class<?>[] noPrimitives = new Class[]{ String.class, ClassUtils.class, Void.TYPE };
        // This used to return the exact same array, but no longer does.
        Assertions.assertNotSame(noPrimitives, ClassUtils.primitivesToWrappers(noPrimitives), "unmodified");
    }

    @Test
    public void testPrimitiveToWrapper() {
        // test primitive classes
        Assertions.assertEquals(Boolean.class, ClassUtils.primitiveToWrapper(Boolean.TYPE), "boolean -> Boolean.class");
        Assertions.assertEquals(Byte.class, ClassUtils.primitiveToWrapper(Byte.TYPE), "byte -> Byte.class");
        Assertions.assertEquals(Character.class, ClassUtils.primitiveToWrapper(Character.TYPE), "char -> Character.class");
        Assertions.assertEquals(Short.class, ClassUtils.primitiveToWrapper(Short.TYPE), "short -> Short.class");
        Assertions.assertEquals(Integer.class, ClassUtils.primitiveToWrapper(Integer.TYPE), "int -> Integer.class");
        Assertions.assertEquals(Long.class, ClassUtils.primitiveToWrapper(Long.TYPE), "long -> Long.class");
        Assertions.assertEquals(Double.class, ClassUtils.primitiveToWrapper(Double.TYPE), "double -> Double.class");
        Assertions.assertEquals(Float.class, ClassUtils.primitiveToWrapper(Float.TYPE), "float -> Float.class");
        // test a few other classes
        Assertions.assertEquals(String.class, ClassUtils.primitiveToWrapper(String.class), "String.class -> String.class");
        Assertions.assertEquals(ClassUtils.class, ClassUtils.primitiveToWrapper(ClassUtils.class), "ClassUtils.class -> ClassUtils.class");
        Assertions.assertEquals(Void.TYPE, ClassUtils.primitiveToWrapper(Void.TYPE), "Void.TYPE -> Void.TYPE");
        // test null
        Assertions.assertNull(ClassUtils.primitiveToWrapper(null), "null -> null");
    }

    // Show the Java bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957
    // We may have to delete this if a JDK fixes the bug.
    @Test
    public void testShowJavaBug() throws Exception {
        // Tests with Collections$UnmodifiableSet
        final Set<?> set = Collections.unmodifiableSet(new HashSet<>());
        final Method isEmptyMethod = set.getClass().getMethod("isEmpty");
        Assertions.assertThrows(IllegalAccessException.class, () -> isEmptyMethod.invoke(set));
    }

    @Test
    public void testToClass_object() {
        // assertNull(ClassUtils.toClass(null)); // generates warning
        Assertions.assertNull(ClassUtils.toClass(((Object[]) (null))));// equivalent explicit cast

        // Additional varargs tests
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CLASS_ARRAY, ClassUtils.toClass(), "empty -> empty");
        final Class<?>[] castNull = ClassUtils.toClass(((Object) (null)));// == new Object[]{null}

        Assertions.assertArrayEquals(new Object[]{ null }, castNull, "(Object) null -> [null]");
        Assertions.assertSame(ArrayUtils.EMPTY_CLASS_ARRAY, ClassUtils.toClass(ArrayUtils.EMPTY_OBJECT_ARRAY));
        Assertions.assertArrayEquals(new Class[]{ String.class, Integer.class, Double.class }, ClassUtils.toClass("Test", Integer.valueOf(1), Double.valueOf(99.0)));
        Assertions.assertArrayEquals(new Class[]{ String.class, null, Double.class }, ClassUtils.toClass("Test", null, Double.valueOf(99.0)));
    }

    @Test
    public void testWithInterleavingWhitespace() throws ClassNotFoundException {
        Assertions.assertEquals(int[].class, ClassUtils.getClass(" int [ ] "));
        Assertions.assertEquals(long[].class, ClassUtils.getClass("\rlong\t[\n]\r"));
        Assertions.assertEquals(short[].class, ClassUtils.getClass("\tshort                \t\t[]"));
        Assertions.assertEquals(byte[].class, ClassUtils.getClass("byte[\t\t\n\r]   "));
    }

    @Test
    public void testWrappersToPrimitives() {
        // an array with classes to test
        final Class<?>[] classes = new Class<?>[]{ Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class, String.class, ClassUtils.class, null };
        final Class<?>[] primitives = ClassUtils.wrappersToPrimitives(classes);
        // now test the result
        Assertions.assertEquals(classes.length, primitives.length, "Wrong length of result array");
        for (int i = 0; i < (classes.length); i++) {
            final Class<?> expectedPrimitive = ClassUtils.wrapperToPrimitive(classes[i]);
            Assertions.assertEquals(expectedPrimitive, primitives[i], (((classes[i]) + " -> ") + expectedPrimitive));
        }
    }

    @Test
    public void testWrappersToPrimitivesEmpty() {
        final Class<?>[] empty = new Class[0];
        Assertions.assertArrayEquals(empty, ClassUtils.wrappersToPrimitives(empty), "Wrong result for empty input");
    }

    @Test
    public void testWrappersToPrimitivesNull() {
        // assertNull("Wrong result for null input", ClassUtils.wrappersToPrimitives(null)); // generates warning
        Assertions.assertNull(ClassUtils.wrappersToPrimitives(((Class<?>[]) (null))), "Wrong result for null input");// equivalent cast

        // Other possible casts for null
        Assertions.assertArrayEquals(ArrayUtils.EMPTY_CLASS_ARRAY, ClassUtils.wrappersToPrimitives(), "empty -> empty");
        final Class<?>[] castNull = ClassUtils.wrappersToPrimitives(((Class<?>) (null)));// == new Class<?>[]{null}

        Assertions.assertArrayEquals(new Class<?>[]{ null }, castNull, "(Class<?>) null -> [null]");
    }

    @Test
    public void testWrapperToPrimitive() {
        // an array with classes to convert
        final Class<?>[] primitives = new Class<?>[]{ Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE };
        for (final Class<?> primitive : primitives) {
            final Class<?> wrapperCls = ClassUtils.primitiveToWrapper(primitive);
            Assertions.assertFalse(wrapperCls.isPrimitive(), "Still primitive");
            Assertions.assertEquals(primitive, ClassUtils.wrapperToPrimitive(wrapperCls), ((wrapperCls + " -> ") + primitive));
        }
    }

    @Test
    public void testWrapperToPrimitiveNoWrapper() {
        Assertions.assertNull(ClassUtils.wrapperToPrimitive(String.class), "Wrong result for non wrapper class");
    }

    @Test
    public void testWrapperToPrimitiveNull() {
        Assertions.assertNull(ClassUtils.wrapperToPrimitive(null), "Wrong result for null class");
    }
}

