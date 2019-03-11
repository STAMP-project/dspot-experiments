/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.lang.reflect;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class AccessibleObjectTest extends TestCase {
    public class TestClass {
        public Object aField;

        @AccessibleObjectTest.InheritedRuntime
        public void annotatedMethod() {
        }
    }

    public class SubTestClass extends AccessibleObjectTest.TestClass {
        @AccessibleObjectTest.AnnotationRuntime0
        @AccessibleObjectTest.AnnotationRuntime1
        @AccessibleObjectTest.AnnotationClass0
        @AccessibleObjectTest.AnnotationSource0
        public void annotatedMethod() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    static @interface AnnotationRuntime0 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    static @interface AnnotationRuntime1 {}

    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.METHOD })
    static @interface AnnotationClass0 {}

    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD })
    static @interface AnnotationSource0 {}

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    static @interface InheritedRuntime {}

    // used for constructor test
    private static class MyAccessibleObject extends AccessibleObject {
        public MyAccessibleObject() {
            super();
        }
    }

    /**
     * java.lang.reflect.AccessibleObject#AccessibleObject()
     */
    public void test_Constructor() {
        TestCase.assertNotNull(new AccessibleObjectTest.MyAccessibleObject());
    }

    /**
     * java.lang.reflect.AccessibleObject#isAccessible()
     */
    public void test_isAccessible() {
        // Test for method boolean
        // java.lang.reflect.AccessibleObject.isAccessible()
        try {
            AccessibleObject ao = AccessibleObjectTest.TestClass.class.getField("aField");
            ao.setAccessible(true);
            TestCase.assertTrue("Returned false to isAccessible", ao.isAccessible());
            ao.setAccessible(false);
            TestCase.assertTrue("Returned true to isAccessible", (!(ao.isAccessible())));
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
    }

    /**
     * java.lang.reflect.AccessibleObject#setAccessible(java.lang.reflect.AccessibleObject[],
     *        boolean)
     */
    public void test_setAccessible$Ljava_lang_reflect_AccessibleObjectZ() {
        try {
            AccessibleObject ao = AccessibleObjectTest.TestClass.class.getField("aField");
            AccessibleObject[] aoa = new AccessibleObject[]{ ao };
            AccessibleObject.setAccessible(aoa, true);
            TestCase.assertTrue("Returned false to isAccessible", ao.isAccessible());
            AccessibleObject.setAccessible(aoa, false);
            TestCase.assertTrue("Returned true to isAccessible", (!(ao.isAccessible())));
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
    }

    /**
     * java.lang.reflect.AccessibleObject#setAccessible(boolean)
     */
    public void test_setAccessible() throws Exception {
        AccessibleObject ao = AccessibleObjectTest.TestClass.class.getField("aField");
        ao.setAccessible(true);
        TestCase.assertTrue("Returned false to isAccessible", ao.isAccessible());
        ao.setAccessible(false);
        TestCase.assertFalse("Returned true to isAccessible", ao.isAccessible());
    }

    public void test_getAnnotation() throws Exception {
        AccessibleObject ao = AccessibleObjectTest.SubTestClass.class.getMethod("annotatedMethod");
        // test error case
        boolean npeThrown = false;
        try {
            ao.getAnnotation(null);
            TestCase.fail("NPE expected");
        } catch (NullPointerException e) {
            npeThrown = true;
        }
        TestCase.assertTrue("NPE expected", npeThrown);
        // test inherited on method has no effect
        AccessibleObjectTest.InheritedRuntime ir = ao.getAnnotation(AccessibleObjectTest.InheritedRuntime.class);
        TestCase.assertNull("Inherited Annotations should have no effect", ir);
        // test ordinary runtime annotation
        AccessibleObjectTest.AnnotationRuntime0 rt0 = ao.getAnnotation(AccessibleObjectTest.AnnotationRuntime0.class);
        TestCase.assertNotNull("AnnotationRuntime0 instance expected", rt0);
    }

    public void test_getAnnotations() throws Exception {
        AccessibleObject ao = AccessibleObjectTest.SubTestClass.class.getMethod("annotatedMethod");
        Annotation[] annotations = ao.getAnnotations();
        TestCase.assertEquals(2, annotations.length);
        Set<Class<?>> ignoreOrder = new HashSet<Class<?>>();
        ignoreOrder.add(annotations[0].annotationType());
        ignoreOrder.add(annotations[1].annotationType());
        TestCase.assertTrue("Missing @AnnotationRuntime0", ignoreOrder.contains(AccessibleObjectTest.AnnotationRuntime0.class));
        TestCase.assertTrue("Missing @AnnotationRuntime1", ignoreOrder.contains(AccessibleObjectTest.AnnotationRuntime1.class));
    }

    public void test_getDeclaredAnnotations() throws Exception {
        AccessibleObject ao = AccessibleObjectTest.SubTestClass.class.getMethod("annotatedMethod");
        Annotation[] annotations = ao.getDeclaredAnnotations();
        TestCase.assertEquals(2, annotations.length);
        Set<Class<?>> ignoreOrder = new HashSet<Class<?>>();
        ignoreOrder.add(annotations[0].annotationType());
        ignoreOrder.add(annotations[1].annotationType());
        TestCase.assertTrue("Missing @AnnotationRuntime0", ignoreOrder.contains(AccessibleObjectTest.AnnotationRuntime0.class));
        TestCase.assertTrue("Missing @AnnotationRuntime1", ignoreOrder.contains(AccessibleObjectTest.AnnotationRuntime1.class));
    }

    public void test_isAnnotationPresent() throws Exception {
        AccessibleObject ao = AccessibleObjectTest.SubTestClass.class.getMethod("annotatedMethod");
        TestCase.assertTrue("Missing @AnnotationRuntime0", ao.isAnnotationPresent(AccessibleObjectTest.AnnotationRuntime0.class));
        TestCase.assertFalse("AnnotationSource0 should not be visible at runtime", ao.isAnnotationPresent(AccessibleObjectTest.AnnotationSource0.class));
        boolean npeThrown = false;
        try {
            ao.isAnnotationPresent(null);
            TestCase.fail("NPE expected");
        } catch (NullPointerException e) {
            npeThrown = true;
        }
        TestCase.assertTrue("NPE expected", npeThrown);
    }
}

