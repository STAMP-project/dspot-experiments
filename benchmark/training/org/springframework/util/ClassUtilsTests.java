/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.util;


import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.objects.DerivedTestObject;
import org.springframework.tests.sample.objects.ITestInterface;
import org.springframework.tests.sample.objects.ITestObject;
import org.springframework.tests.sample.objects.TestObject;


/**
 *
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rick Evans
 */
public class ClassUtilsTests {
    private ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void testIsPresent() {
        Assert.assertTrue(ClassUtils.isPresent("java.lang.String", classLoader));
        Assert.assertFalse(ClassUtils.isPresent("java.lang.MySpecialString", classLoader));
    }

    @Test
    public void testForName() throws ClassNotFoundException {
        Assert.assertEquals(String.class, ClassUtils.forName("java.lang.String", classLoader));
        Assert.assertEquals(String[].class, ClassUtils.forName("java.lang.String[]", classLoader));
        Assert.assertEquals(String[].class, ClassUtils.forName(String[].class.getName(), classLoader));
        Assert.assertEquals(String[][].class, ClassUtils.forName(String[][].class.getName(), classLoader));
        Assert.assertEquals(String[][][].class, ClassUtils.forName(String[][][].class.getName(), classLoader));
        Assert.assertEquals(TestObject.class, ClassUtils.forName("org.springframework.tests.sample.objects.TestObject", classLoader));
        Assert.assertEquals(TestObject[].class, ClassUtils.forName("org.springframework.tests.sample.objects.TestObject[]", classLoader));
        Assert.assertEquals(TestObject[].class, ClassUtils.forName(TestObject[].class.getName(), classLoader));
        Assert.assertEquals(TestObject[][].class, ClassUtils.forName("org.springframework.tests.sample.objects.TestObject[][]", classLoader));
        Assert.assertEquals(TestObject[][].class, ClassUtils.forName(TestObject[][].class.getName(), classLoader));
        Assert.assertEquals(short[][][].class, ClassUtils.forName("[[[S", classLoader));
    }

    @Test
    public void testForNameWithPrimitiveClasses() throws ClassNotFoundException {
        Assert.assertEquals(boolean.class, ClassUtils.forName("boolean", classLoader));
        Assert.assertEquals(byte.class, ClassUtils.forName("byte", classLoader));
        Assert.assertEquals(char.class, ClassUtils.forName("char", classLoader));
        Assert.assertEquals(short.class, ClassUtils.forName("short", classLoader));
        Assert.assertEquals(int.class, ClassUtils.forName("int", classLoader));
        Assert.assertEquals(long.class, ClassUtils.forName("long", classLoader));
        Assert.assertEquals(float.class, ClassUtils.forName("float", classLoader));
        Assert.assertEquals(double.class, ClassUtils.forName("double", classLoader));
        Assert.assertEquals(void.class, ClassUtils.forName("void", classLoader));
    }

    @Test
    public void testForNameWithPrimitiveArrays() throws ClassNotFoundException {
        Assert.assertEquals(boolean[].class, ClassUtils.forName("boolean[]", classLoader));
        Assert.assertEquals(byte[].class, ClassUtils.forName("byte[]", classLoader));
        Assert.assertEquals(char[].class, ClassUtils.forName("char[]", classLoader));
        Assert.assertEquals(short[].class, ClassUtils.forName("short[]", classLoader));
        Assert.assertEquals(int[].class, ClassUtils.forName("int[]", classLoader));
        Assert.assertEquals(long[].class, ClassUtils.forName("long[]", classLoader));
        Assert.assertEquals(float[].class, ClassUtils.forName("float[]", classLoader));
        Assert.assertEquals(double[].class, ClassUtils.forName("double[]", classLoader));
    }

    @Test
    public void testForNameWithPrimitiveArraysInternalName() throws ClassNotFoundException {
        Assert.assertEquals(boolean[].class, ClassUtils.forName(boolean[].class.getName(), classLoader));
        Assert.assertEquals(byte[].class, ClassUtils.forName(byte[].class.getName(), classLoader));
        Assert.assertEquals(char[].class, ClassUtils.forName(char[].class.getName(), classLoader));
        Assert.assertEquals(short[].class, ClassUtils.forName(short[].class.getName(), classLoader));
        Assert.assertEquals(int[].class, ClassUtils.forName(int[].class.getName(), classLoader));
        Assert.assertEquals(long[].class, ClassUtils.forName(long[].class.getName(), classLoader));
        Assert.assertEquals(float[].class, ClassUtils.forName(float[].class.getName(), classLoader));
        Assert.assertEquals(double[].class, ClassUtils.forName(double[].class.getName(), classLoader));
    }

    @Test
    public void testIsCacheSafe() {
        ClassLoader childLoader1 = new ClassLoader(classLoader) {};
        ClassLoader childLoader2 = new ClassLoader(classLoader) {};
        ClassLoader childLoader3 = new ClassLoader(classLoader) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return childLoader1.loadClass(name);
            }
        };
        Class<?> composite = ClassUtils.createCompositeInterface(new Class<?>[]{ Serializable.class, Externalizable.class }, childLoader1);
        Assert.assertTrue(ClassUtils.isCacheSafe(String.class, null));
        Assert.assertTrue(ClassUtils.isCacheSafe(String.class, classLoader));
        Assert.assertTrue(ClassUtils.isCacheSafe(String.class, childLoader1));
        Assert.assertTrue(ClassUtils.isCacheSafe(String.class, childLoader2));
        Assert.assertTrue(ClassUtils.isCacheSafe(String.class, childLoader3));
        Assert.assertFalse(ClassUtils.isCacheSafe(ClassUtilsTests.InnerClass.class, null));
        Assert.assertTrue(ClassUtils.isCacheSafe(ClassUtilsTests.InnerClass.class, classLoader));
        Assert.assertTrue(ClassUtils.isCacheSafe(ClassUtilsTests.InnerClass.class, childLoader1));
        Assert.assertTrue(ClassUtils.isCacheSafe(ClassUtilsTests.InnerClass.class, childLoader2));
        Assert.assertTrue(ClassUtils.isCacheSafe(ClassUtilsTests.InnerClass.class, childLoader3));
        Assert.assertFalse(ClassUtils.isCacheSafe(composite, null));
        Assert.assertFalse(ClassUtils.isCacheSafe(composite, classLoader));
        Assert.assertTrue(ClassUtils.isCacheSafe(composite, childLoader1));
        Assert.assertFalse(ClassUtils.isCacheSafe(composite, childLoader2));
        Assert.assertTrue(ClassUtils.isCacheSafe(composite, childLoader3));
    }

    @Test
    public void testGetShortName() {
        String className = ClassUtils.getShortName(getClass());
        Assert.assertEquals("Class name did not match", "ClassUtilsTests", className);
    }

    @Test
    public void testGetShortNameForObjectArrayClass() {
        String className = ClassUtils.getShortName(Object[].class);
        Assert.assertEquals("Class name did not match", "Object[]", className);
    }

    @Test
    public void testGetShortNameForMultiDimensionalObjectArrayClass() {
        String className = ClassUtils.getShortName(Object[][].class);
        Assert.assertEquals("Class name did not match", "Object[][]", className);
    }

    @Test
    public void testGetShortNameForPrimitiveArrayClass() {
        String className = ClassUtils.getShortName(byte[].class);
        Assert.assertEquals("Class name did not match", "byte[]", className);
    }

    @Test
    public void testGetShortNameForMultiDimensionalPrimitiveArrayClass() {
        String className = ClassUtils.getShortName(byte[][][].class);
        Assert.assertEquals("Class name did not match", "byte[][][]", className);
    }

    @Test
    public void testGetShortNameForInnerClass() {
        String className = ClassUtils.getShortName(ClassUtilsTests.InnerClass.class);
        Assert.assertEquals("Class name did not match", "ClassUtilsTests.InnerClass", className);
    }

    @Test
    public void testGetShortNameAsProperty() {
        String shortName = ClassUtils.getShortNameAsProperty(this.getClass());
        Assert.assertEquals("Class name did not match", "classUtilsTests", shortName);
    }

    @Test
    public void testGetClassFileName() {
        Assert.assertEquals("String.class", ClassUtils.getClassFileName(String.class));
        Assert.assertEquals("ClassUtilsTests.class", ClassUtils.getClassFileName(getClass()));
    }

    @Test
    public void testGetPackageName() {
        Assert.assertEquals("java.lang", ClassUtils.getPackageName(String.class));
        Assert.assertEquals(getClass().getPackage().getName(), ClassUtils.getPackageName(getClass()));
    }

    @Test
    public void testGetQualifiedName() {
        String className = ClassUtils.getQualifiedName(getClass());
        Assert.assertEquals("Class name did not match", "org.springframework.util.ClassUtilsTests", className);
    }

    @Test
    public void testGetQualifiedNameForObjectArrayClass() {
        String className = ClassUtils.getQualifiedName(Object[].class);
        Assert.assertEquals("Class name did not match", "java.lang.Object[]", className);
    }

    @Test
    public void testGetQualifiedNameForMultiDimensionalObjectArrayClass() {
        String className = ClassUtils.getQualifiedName(Object[][].class);
        Assert.assertEquals("Class name did not match", "java.lang.Object[][]", className);
    }

    @Test
    public void testGetQualifiedNameForPrimitiveArrayClass() {
        String className = ClassUtils.getQualifiedName(byte[].class);
        Assert.assertEquals("Class name did not match", "byte[]", className);
    }

    @Test
    public void testGetQualifiedNameForMultiDimensionalPrimitiveArrayClass() {
        String className = ClassUtils.getQualifiedName(byte[][].class);
        Assert.assertEquals("Class name did not match", "byte[][]", className);
    }

    @Test
    public void testHasMethod() {
        Assert.assertTrue(ClassUtils.hasMethod(Collection.class, "size"));
        Assert.assertTrue(ClassUtils.hasMethod(Collection.class, "remove", Object.class));
        Assert.assertFalse(ClassUtils.hasMethod(Collection.class, "remove"));
        Assert.assertFalse(ClassUtils.hasMethod(Collection.class, "someOtherMethod"));
    }

    @Test
    public void testGetMethodIfAvailable() {
        Method method = ClassUtils.getMethodIfAvailable(Collection.class, "size");
        Assert.assertNotNull(method);
        Assert.assertEquals("size", method.getName());
        method = ClassUtils.getMethodIfAvailable(Collection.class, "remove", Object.class);
        Assert.assertNotNull(method);
        Assert.assertEquals("remove", method.getName());
        Assert.assertNull(ClassUtils.getMethodIfAvailable(Collection.class, "remove"));
        Assert.assertNull(ClassUtils.getMethodIfAvailable(Collection.class, "someOtherMethod"));
    }

    @Test
    public void testGetMethodCountForName() {
        Assert.assertEquals("Verifying number of overloaded 'print' methods for OverloadedMethodsClass.", 2, ClassUtils.getMethodCountForName(ClassUtilsTests.OverloadedMethodsClass.class, "print"));
        Assert.assertEquals("Verifying number of overloaded 'print' methods for SubOverloadedMethodsClass.", 4, ClassUtils.getMethodCountForName(ClassUtilsTests.SubOverloadedMethodsClass.class, "print"));
    }

    @Test
    public void testCountOverloadedMethods() {
        Assert.assertFalse(ClassUtils.hasAtLeastOneMethodWithName(TestObject.class, "foobar"));
        // no args
        Assert.assertTrue(ClassUtils.hasAtLeastOneMethodWithName(TestObject.class, "hashCode"));
        // matches although it takes an arg
        Assert.assertTrue(ClassUtils.hasAtLeastOneMethodWithName(TestObject.class, "setAge"));
    }

    @Test
    public void testNoArgsStaticMethod() throws IllegalAccessException, InvocationTargetException {
        Method method = ClassUtils.getStaticMethod(ClassUtilsTests.InnerClass.class, "staticMethod");
        method.invoke(null, ((Object[]) (null)));
        Assert.assertTrue("no argument method was not invoked.", ClassUtilsTests.InnerClass.noArgCalled);
    }

    @Test
    public void testArgsStaticMethod() throws IllegalAccessException, InvocationTargetException {
        Method method = ClassUtils.getStaticMethod(ClassUtilsTests.InnerClass.class, "argStaticMethod", String.class);
        method.invoke(null, "test");
        Assert.assertTrue("argument method was not invoked.", ClassUtilsTests.InnerClass.argCalled);
    }

    @Test
    public void testOverloadedStaticMethod() throws IllegalAccessException, InvocationTargetException {
        Method method = ClassUtils.getStaticMethod(ClassUtilsTests.InnerClass.class, "staticMethod", String.class);
        method.invoke(null, "test");
        Assert.assertTrue("argument method was not invoked.", ClassUtilsTests.InnerClass.overloadedCalled);
    }

    @Test
    public void testIsAssignable() {
        Assert.assertTrue(ClassUtils.isAssignable(Object.class, Object.class));
        Assert.assertTrue(ClassUtils.isAssignable(String.class, String.class));
        Assert.assertTrue(ClassUtils.isAssignable(Object.class, String.class));
        Assert.assertTrue(ClassUtils.isAssignable(Object.class, Integer.class));
        Assert.assertTrue(ClassUtils.isAssignable(Number.class, Integer.class));
        Assert.assertTrue(ClassUtils.isAssignable(Number.class, int.class));
        Assert.assertTrue(ClassUtils.isAssignable(Integer.class, int.class));
        Assert.assertTrue(ClassUtils.isAssignable(int.class, Integer.class));
        Assert.assertFalse(ClassUtils.isAssignable(String.class, Object.class));
        Assert.assertFalse(ClassUtils.isAssignable(Integer.class, Number.class));
        Assert.assertFalse(ClassUtils.isAssignable(Integer.class, double.class));
        Assert.assertFalse(ClassUtils.isAssignable(double.class, Integer.class));
    }

    @Test
    public void testClassPackageAsResourcePath() {
        String result = ClassUtils.classPackageAsResourcePath(Proxy.class);
        Assert.assertEquals("java/lang/reflect", result);
    }

    @Test
    public void testAddResourcePathToPackagePath() {
        String result = "java/lang/reflect/xyzabc.xml";
        Assert.assertEquals(result, ClassUtils.addResourcePathToPackagePath(Proxy.class, "xyzabc.xml"));
        Assert.assertEquals(result, ClassUtils.addResourcePathToPackagePath(Proxy.class, "/xyzabc.xml"));
        Assert.assertEquals("java/lang/reflect/a/b/c/d.xml", ClassUtils.addResourcePathToPackagePath(Proxy.class, "a/b/c/d.xml"));
    }

    @Test
    public void testGetAllInterfaces() {
        DerivedTestObject testBean = new DerivedTestObject();
        List<Class<?>> ifcs = Arrays.asList(ClassUtils.getAllInterfaces(testBean));
        Assert.assertEquals("Correct number of interfaces", 4, ifcs.size());
        Assert.assertTrue("Contains Serializable", ifcs.contains(Serializable.class));
        Assert.assertTrue("Contains ITestBean", ifcs.contains(ITestObject.class));
        Assert.assertTrue("Contains IOther", ifcs.contains(ITestInterface.class));
    }

    @Test
    public void testClassNamesToString() {
        List<Class<?>> ifcs = new LinkedList<>();
        ifcs.add(Serializable.class);
        ifcs.add(Runnable.class);
        Assert.assertEquals("[interface java.io.Serializable, interface java.lang.Runnable]", ifcs.toString());
        Assert.assertEquals("[java.io.Serializable, java.lang.Runnable]", ClassUtils.classNamesToString(ifcs));
        List<Class<?>> classes = new LinkedList<>();
        classes.add(LinkedList.class);
        classes.add(Integer.class);
        Assert.assertEquals("[class java.util.LinkedList, class java.lang.Integer]", classes.toString());
        Assert.assertEquals("[java.util.LinkedList, java.lang.Integer]", ClassUtils.classNamesToString(classes));
        Assert.assertEquals("[interface java.util.List]", Collections.singletonList(List.class).toString());
        Assert.assertEquals("[java.util.List]", ClassUtils.classNamesToString(List.class));
        Assert.assertEquals("[]", Collections.EMPTY_LIST.toString());
        Assert.assertEquals("[]", ClassUtils.classNamesToString(Collections.emptyList()));
    }

    @Test
    public void testDetermineCommonAncestor() {
        Assert.assertEquals(Number.class, ClassUtils.determineCommonAncestor(Integer.class, Number.class));
        Assert.assertEquals(Number.class, ClassUtils.determineCommonAncestor(Number.class, Integer.class));
        Assert.assertEquals(Number.class, ClassUtils.determineCommonAncestor(Number.class, null));
        Assert.assertEquals(Integer.class, ClassUtils.determineCommonAncestor(null, Integer.class));
        Assert.assertEquals(Integer.class, ClassUtils.determineCommonAncestor(Integer.class, Integer.class));
        Assert.assertEquals(Number.class, ClassUtils.determineCommonAncestor(Integer.class, Float.class));
        Assert.assertEquals(Number.class, ClassUtils.determineCommonAncestor(Float.class, Integer.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(Integer.class, String.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(String.class, Integer.class));
        Assert.assertEquals(Collection.class, ClassUtils.determineCommonAncestor(List.class, Collection.class));
        Assert.assertEquals(Collection.class, ClassUtils.determineCommonAncestor(Collection.class, List.class));
        Assert.assertEquals(Collection.class, ClassUtils.determineCommonAncestor(Collection.class, null));
        Assert.assertEquals(List.class, ClassUtils.determineCommonAncestor(null, List.class));
        Assert.assertEquals(List.class, ClassUtils.determineCommonAncestor(List.class, List.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(List.class, Set.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(Set.class, List.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(List.class, Runnable.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(Runnable.class, List.class));
        Assert.assertEquals(List.class, ClassUtils.determineCommonAncestor(List.class, ArrayList.class));
        Assert.assertEquals(List.class, ClassUtils.determineCommonAncestor(ArrayList.class, List.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(List.class, String.class));
        Assert.assertNull(ClassUtils.determineCommonAncestor(String.class, List.class));
    }

    public static class InnerClass {
        static boolean noArgCalled;

        static boolean argCalled;

        static boolean overloadedCalled;

        public static void staticMethod() {
            ClassUtilsTests.InnerClass.noArgCalled = true;
        }

        public static void staticMethod(String anArg) {
            ClassUtilsTests.InnerClass.overloadedCalled = true;
        }

        public static void argStaticMethod(String anArg) {
            ClassUtilsTests.InnerClass.argCalled = true;
        }
    }

    @SuppressWarnings("unused")
    private static class OverloadedMethodsClass {
        public void print(String messages) {
            /* no-op */
        }

        public void print(String[] messages) {
            /* no-op */
        }
    }

    @SuppressWarnings("unused")
    private static class SubOverloadedMethodsClass extends ClassUtilsTests.OverloadedMethodsClass {
        public void print(String header, String[] messages) {
            /* no-op */
        }

        void print(String header, String[] messages, String footer) {
            /* no-op */
        }
    }
}

