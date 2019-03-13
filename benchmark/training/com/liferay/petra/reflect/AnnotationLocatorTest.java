/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.reflect;


import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * <p>
 * <table>
 * <tr>
 * <th colspan = 3>
 * Test Classes Inherited Hierarchy
 * </th>
 * </tr>
 * <tr valign="top">
 * <td>
 * <pre>
 * &#64;Type(value = 5)
 * OriginClass {    -------->
 *   &#64;Method(value = 5)
 *   &#64;Mix(value = 5)
 *   originMethod1()
 *   originMethod2()
 * }
 * </pre></td>
 * <td>
 * <pre>
 * &#64;Mix(value = 8)
 * OriginInterface2 {  -------->
 *   &#64;Method(value = 8)
 *   originMethod2()
 * }
 * </pre></td>
 * <td>
 * <pre>
 * &#64;Type(value = 9)
 * OriginInterface1 {
 *   &#64;Method(value = 9)
 *   &#64;Mix(value = 9)
 *   originMethod1()
 * }
 * </pre></td>
 * </tr>
 * <tr valign="top">
 * <td>
 * <pre>
 *   ^
 *   |
 * </pre></td>
 * <td>
 * <pre>
 * </pre></td>
 * <td>
 * <pre>
 *   ^
 *   |
 * </pre></td>
 * </tr>
 * <tr valign="top">
 * <td>
 * <pre>
 * &#64;Mix(value = 2)
 * SuperClass {    -------->
 *   &#64;Method(value = 2)
 *   originMethod2()
 *   &#64;Method(value = 2)
 *   superMethod1()
 *   superMethod2()
 * }
 * </pre></td>
 * <td>
 * <pre>
 * &#64;Type(value = 6)
 * SuperInterface2 {  -------->
 *   &#64;Method(value = 6)
 *   &#64;Mix(value = 6)
 *   originMethod1()
 *   &#64;Method(value = 6)
 *   &#64;Mix(value = 6)
 *   superMethod2()
 * }
 * </pre></td>
 * <td>
 * <pre>
 * &#64;Mix(value = 7)
 * SuperInterface1 {
 * &#64;Method(value = 7)
 * superMethod1()
 * }
 * </pre></td>
 * </tr>
 * <tr valign="top">
 * <td>
 * <pre>
 *   ^
 *   |
 * </pre></td>
 * <td>
 * <pre>
 *   ^
 *   |
 * </pre></td>
 * <td>
 * <pre>
 * </pre></td>
 * </tr>
 * <tr valign="top">
 * <td>
 * <pre>
 * &#64;Type(value = 1)
 * TestClass {    -------->
 * &#64;Method(value = 1)
 *   &#64;Method(value = 1)
 *   &#64;Mix(value = 1)
 *   originMethod1()
 *   &#64;Method(value = 1)
 *   &#64;Mix(value = 1)
 *   superMethod2()
 *   &#64;Method(value = 1)
 *   &#64;Mix(value = 1)
 *   testMethod1()
 *   testMethod2()
 * }
 * </pre></td>
 * <td>
 * <pre>
 * &#64;Mix(value = 3)
 * TestInterface2 {  -------->
 *   &#64;Method(value = 3)
 *   superMethod1()
 *   &#64;Method(value = 3)
 *   testMethod2()
 * }
 * </pre></td>
 * <td>
 * <pre>
 * &#64;Type(value = 4)
 * TestInterface1 {
 *   &#64;Method(value = 4)
 *   &#64;Mix(value = 4)
 *   testMethod1()
 * }
 * </pre></td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Shuyang Zhou
 */
public class AnnotationLocatorTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testClassListLocate() {
        _classListLocate(AnnotationLocatorTest.TestClass.class, Arrays.asList(_type(1), _mix(2)));
        _classListLocate(AnnotationLocatorTest.SuperClass.class, Arrays.asList(_mix(2), _type(5)));
        _classListLocate(AnnotationLocatorTest.TestInterface2.class, Arrays.asList(_mix(3), _type(4)));
        _classListLocate(AnnotationLocatorTest.TestInterface1.class, Arrays.asList(_type(4)));
        _classListLocate(AnnotationLocatorTest.OriginClass.class, Arrays.asList(_type(5), _mix(8)));
        _classListLocate(AnnotationLocatorTest.SuperInterface2.class, Arrays.asList(_type(6), _mix(7)));
        _classListLocate(AnnotationLocatorTest.SuperInterface1.class, Arrays.asList(_mix(7), _type(9)));
        _classListLocate(AnnotationLocatorTest.OriginInterface2.class, Arrays.asList(_mix(8), _type(9)));
        _classListLocate(AnnotationLocatorTest.OriginInterface1.class, Arrays.asList(_type(9)));
    }

    @Test
    public void testClassSingleLocate() {
        _classSingleLocate(AnnotationLocatorTest.TestClass.class, 2, 1);
        _classSingleLocate(AnnotationLocatorTest.SuperClass.class, 2, 5);
        _classSingleLocate(AnnotationLocatorTest.TestInterface2.class, 3, 4);
        _classSingleLocate(AnnotationLocatorTest.TestInterface1.class, (-1), 4);
        _classSingleLocate(AnnotationLocatorTest.OriginClass.class, 8, 5);
        _classSingleLocate(AnnotationLocatorTest.SuperInterface2.class, 7, 6);
        _classSingleLocate(AnnotationLocatorTest.SuperInterface1.class, 7, 9);
        _classSingleLocate(AnnotationLocatorTest.OriginInterface2.class, 8, 9);
        _classSingleLocate(AnnotationLocatorTest.OriginInterface1.class, (-1), 9);
    }

    @Test
    public void testConstructor() {
        new AnnotationLocator();
    }

    @Test
    public void testInheritedHierarchyWalking() throws Exception {
        List<Class<?>> expectedClassHierarchy = Arrays.asList(AnnotationLocatorTest.TestClass.class, AnnotationLocatorTest.SuperClass.class, AnnotationLocatorTest.TestInterface2.class, AnnotationLocatorTest.TestInterface1.class, AnnotationLocatorTest.OriginClass.class, AnnotationLocatorTest.SuperInterface2.class, AnnotationLocatorTest.SuperInterface1.class, AnnotationLocatorTest.OriginInterface2.class, AnnotationLocatorTest.OriginInterface1.class);
        List<Class<?>> actualClassHierarchy = new ArrayList<>();
        Queue<Class<?>> queue = new LinkedList<>();
        queue.offer(AnnotationLocatorTest.TestClass.class);
        Class<?> clazz = null;
        while ((clazz = queue.poll()) != null) {
            actualClassHierarchy.add(clazz);
            AnnotationLocatorTest._QUEUE_SUPER_TYPES_METHOD.invoke(null, queue, clazz);
        } 
        Assert.assertEquals(expectedClassHierarchy, actualClassHierarchy);
    }

    @Test
    public void testMethodListLocate() {
        _methodListLocate(AnnotationLocatorTest.TestClass.class, Arrays.asList(new Annotation[]{ _method(1), _mix(1), _type(1) }, new Annotation[]{ _type(1), _method(2), _mix(2) }, new Annotation[]{ _type(1), _method(2), _mix(2) }, new Annotation[]{ _method(1), _mix(1), _type(1) }, new Annotation[]{ _method(1), _mix(1), _type(1) }, new Annotation[]{ _type(1), _method(3), _mix(3) }));
        _methodListLocate(AnnotationLocatorTest.SuperClass.class, Arrays.asList(new Annotation[]{ _mix(2), _method(5), _type(5) }, new Annotation[]{ _method(2), _mix(2), _type(5) }, new Annotation[]{ _method(2), _mix(2), _type(6) }, new Annotation[]{ _mix(2), _method(6), _type(6) }, new Annotation[0], new Annotation[0]));
        _methodListLocate(AnnotationLocatorTest.TestInterface2.class, Arrays.asList(new Annotation[]{ _mix(3), _method(6), _type(6) }, new Annotation[0], new Annotation[]{ _method(3), _mix(3), _type(6) }, new Annotation[]{ _mix(3), _method(6), _type(6) }, new Annotation[]{ _mix(3), _method(4), _type(4) }, new Annotation[]{ _method(3), _mix(3) }));
        _methodListLocate(AnnotationLocatorTest.TestInterface1.class, Arrays.asList(new Annotation[0], new Annotation[0], new Annotation[0], new Annotation[0], new Annotation[]{ _method(4), _mix(4), _type(4) }, new Annotation[0]));
        _methodListLocate(AnnotationLocatorTest.OriginClass.class, Arrays.asList(new Annotation[]{ _method(5), _mix(5), _type(5) }, new Annotation[]{ _type(5), _method(8), _mix(8) }, new Annotation[0], new Annotation[0], new Annotation[0], new Annotation[0]));
        _methodListLocate(AnnotationLocatorTest.SuperInterface2.class, Arrays.asList(new Annotation[]{ _method(6), _mix(6), _type(6) }, new Annotation[0], new Annotation[]{ _type(6), _method(7), _mix(7) }, new Annotation[]{ _method(6), _mix(6), _type(6) }, new Annotation[0], new Annotation[0]));
        _methodListLocate(AnnotationLocatorTest.SuperInterface1.class, Arrays.asList(new Annotation[]{ _mix(7), _method(9), _type(9) }, new Annotation[0], new Annotation[]{ _method(7), _mix(7) }, new Annotation[0], new Annotation[0], new Annotation[0]));
        _methodListLocate(AnnotationLocatorTest.OriginInterface2.class, Arrays.asList(new Annotation[]{ _mix(8), _method(9), _type(9) }, new Annotation[]{ _method(8), _mix(8) }, new Annotation[0], new Annotation[0], new Annotation[0], new Annotation[0]));
        _methodListLocate(AnnotationLocatorTest.OriginInterface1.class, Arrays.asList(new Annotation[]{ _method(9), _mix(9), _type(9) }, new Annotation[0], new Annotation[0], new Annotation[0], new Annotation[0], new Annotation[0]));
    }

    @Test
    public void testMethodSingleLocate() {
        _methodSingleLocate(AnnotationLocatorTest.TestClass.class, new int[]{ 1, 2, 2, 1, 1, 3 }, new int[]{ 1, 2, 2, 1, 1, 3 }, new int[]{ 1, 1, 1, 1, 1, 1 });
        _methodSingleLocate(AnnotationLocatorTest.SuperClass.class, new int[]{ 5, 2, 2, 6, -1, -1 }, new int[]{ 2, 2, 2, 2, -1, -1 }, new int[]{ 5, 5, 6, 6, -1, -1 });
        _methodSingleLocate(AnnotationLocatorTest.TestInterface2.class, new int[]{ 6, -1, 3, 6, 4, 3 }, new int[]{ 3, -1, 3, 3, 3, 3 }, new int[]{ 6, -1, 6, 6, 4, -1 });
        _methodSingleLocate(AnnotationLocatorTest.TestInterface1.class, new int[]{ -1, -1, -1, -1, 4, -1 }, new int[]{ -1, -1, -1, -1, 4, -1 }, new int[]{ -1, -1, -1, -1, 4, -1 });
        _methodSingleLocate(AnnotationLocatorTest.OriginClass.class, new int[]{ 5, 8, -1, -1, -1, -1 }, new int[]{ 5, 8, -1, -1, -1, -1 }, new int[]{ 5, 5, -1, -1, -1, -1 });
        _methodSingleLocate(AnnotationLocatorTest.SuperInterface2.class, new int[]{ 6, -1, 7, 6, -1, -1 }, new int[]{ 6, -1, 7, 6, -1, -1 }, new int[]{ 6, -1, 6, 6, -1, -1 });
        _methodSingleLocate(AnnotationLocatorTest.SuperInterface1.class, new int[]{ 9, -1, 7, -1, -1, -1 }, new int[]{ 7, -1, 7, -1, -1, -1 }, new int[]{ 9, -1, -1, -1, -1, -1 });
        _methodSingleLocate(AnnotationLocatorTest.OriginInterface2.class, new int[]{ 9, 8, -1, -1, -1, -1 }, new int[]{ 8, 8, -1, -1, -1, -1 }, new int[]{ 9, -1, -1, -1, -1, -1 });
        _methodSingleLocate(AnnotationLocatorTest.OriginInterface1.class, new int[]{ 9, -1, -1, -1, -1, -1 }, new int[]{ 9, -1, -1, -1, -1, -1 }, new int[]{ 9, -1, -1, -1, -1, -1 });
    }

    private static final java.lang.reflect.Method _QUEUE_SUPER_TYPES_METHOD;

    private static final java.lang.reflect.Method[] _interfaceMethods;

    static {
        try {
            _interfaceMethods = new java.lang.reflect.Method[6];
            AnnotationLocatorTest._interfaceMethods[0] = AnnotationLocatorTest.OriginInterface1.class.getDeclaredMethod("originMethod1");
            AnnotationLocatorTest._interfaceMethods[1] = AnnotationLocatorTest.OriginInterface2.class.getDeclaredMethod("originMethod2");
            AnnotationLocatorTest._interfaceMethods[2] = AnnotationLocatorTest.SuperInterface1.class.getDeclaredMethod("superMethod1");
            AnnotationLocatorTest._interfaceMethods[3] = AnnotationLocatorTest.SuperInterface2.class.getDeclaredMethod("superMethod2");
            AnnotationLocatorTest._interfaceMethods[4] = AnnotationLocatorTest.TestInterface1.class.getDeclaredMethod("testMethod1");
            AnnotationLocatorTest._interfaceMethods[5] = AnnotationLocatorTest.TestInterface2.class.getDeclaredMethod("testMethod2");
            java.lang.reflect.Method queueSuperTypesMethod = AnnotationLocator.class.getDeclaredMethod("_queueSuperTypes", Queue.class, Class.class);
            queueSuperTypesMethod.setAccessible(true);
            _QUEUE_SUPER_TYPES_METHOD = queueSuperTypesMethod;
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @AnnotationLocatorTest.Type(5)
    private static class OriginClass implements AnnotationLocatorTest.OriginInterface1 , AnnotationLocatorTest.OriginInterface2 {
        @AnnotationLocatorTest.Method(5)
        @AnnotationLocatorTest.Mix(5)
        @Override
        public void originMethod1() {
        }

        @Override
        public void originMethod2() {
        }
    }

    @AnnotationLocatorTest.Mix(2)
    private static class SuperClass extends AnnotationLocatorTest.OriginClass implements AnnotationLocatorTest.SuperInterface1 , AnnotationLocatorTest.SuperInterface2 {
        @AnnotationLocatorTest.Method(2)
        @Override
        public void originMethod2() {
        }

        @AnnotationLocatorTest.Method(2)
        @Override
        public void superMethod1() {
        }

        @Override
        public void superMethod2() {
        }
    }

    @AnnotationLocatorTest.Type(1)
    private static class TestClass extends AnnotationLocatorTest.SuperClass implements AnnotationLocatorTest.TestInterface1 , AnnotationLocatorTest.TestInterface2 {
        @AnnotationLocatorTest.Method(1)
        @AnnotationLocatorTest.Mix(1)
        @Override
        public void originMethod1() {
        }

        @AnnotationLocatorTest.Method(1)
        @AnnotationLocatorTest.Mix(1)
        @Override
        public void superMethod2() {
        }

        @AnnotationLocatorTest.Method(1)
        @AnnotationLocatorTest.Mix(1)
        @Override
        public void testMethod1() {
        }

        @Override
        public void testMethod2() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface Method {
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    private @interface Mix {
        int value();
    }

    @AnnotationLocatorTest.Type(9)
    private interface OriginInterface1 {
        @AnnotationLocatorTest.Method(9)
        @AnnotationLocatorTest.Mix(9)
        public void originMethod1();
    }

    @AnnotationLocatorTest.Mix(8)
    private interface OriginInterface2 extends AnnotationLocatorTest.OriginInterface1 {
        @AnnotationLocatorTest.Method(8)
        public void originMethod2();
    }

    @AnnotationLocatorTest.Mix(7)
    private interface SuperInterface1 extends AnnotationLocatorTest.OriginInterface1 {
        @AnnotationLocatorTest.Method(7)
        void superMethod1();
    }

    @AnnotationLocatorTest.Type(6)
    private interface SuperInterface2 extends AnnotationLocatorTest.SuperInterface1 {
        @Override
        @AnnotationLocatorTest.Method(6)
        @AnnotationLocatorTest.Mix(6)
        void originMethod1();

        @AnnotationLocatorTest.Method(6)
        @AnnotationLocatorTest.Mix(6)
        void superMethod2();
    }

    @AnnotationLocatorTest.Type(4)
    private interface TestInterface1 {
        @AnnotationLocatorTest.Method(4)
        @AnnotationLocatorTest.Mix(4)
        public void testMethod1();
    }

    @AnnotationLocatorTest.Mix(3)
    private interface TestInterface2 extends AnnotationLocatorTest.SuperInterface2 , AnnotationLocatorTest.TestInterface1 {
        @AnnotationLocatorTest.Method(3)
        @Override
        public void superMethod1();

        @AnnotationLocatorTest.Method(3)
        public void testMethod2();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private @interface Type {
        int value();
    }
}

