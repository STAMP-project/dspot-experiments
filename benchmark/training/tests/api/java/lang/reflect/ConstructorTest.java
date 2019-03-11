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
package tests.api.java.lang.reflect;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class ConstructorTest extends TestCase {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.CONSTRUCTOR, ElementType.PARAMETER })
    static @interface ConstructorTestAnnotationRuntime0 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.CONSTRUCTOR, ElementType.PARAMETER })
    static @interface ConstructorTestAnnotationRuntime1 {}

    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.CONSTRUCTOR, ElementType.PARAMETER })
    static @interface ConstructorTestAnnotationClass0 {}

    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.CONSTRUCTOR, ElementType.PARAMETER })
    static @interface ConstructorTestAnnotationSource0 {}

    static class ConstructorTestHelper extends Object {
        int cval;

        @ConstructorTest.ConstructorTestAnnotationRuntime0
        @ConstructorTest.ConstructorTestAnnotationRuntime1
        @ConstructorTest.ConstructorTestAnnotationClass0
        @ConstructorTest.ConstructorTestAnnotationSource0
        public ConstructorTestHelper() throws IndexOutOfBoundsException {
            cval = 99;
        }

        public ConstructorTestHelper(@ConstructorTest.ConstructorTestAnnotationRuntime0
        @ConstructorTest.ConstructorTestAnnotationRuntime1
        @ConstructorTest.ConstructorTestAnnotationClass0
        @ConstructorTest.ConstructorTestAnnotationSource0
        Object x) {
        }

        public ConstructorTestHelper(String... x) {
        }

        private ConstructorTestHelper(int a) {
        }

        protected ConstructorTestHelper(long a) {
        }

        public int check() {
            return cval;
        }
    }

    static class GenericConstructorTestHelper<T, S extends T, E extends Exception> {
        public GenericConstructorTestHelper(T t, S s) {
        }

        public GenericConstructorTestHelper() throws E {
        }
    }

    // This class has no public constructor.
    static class NoPublicConstructorTestHelper {}

    // Used to test synthetic constructor.
    // 
    // static class Outer {
    // private Outer(){}
    // class Inner {
    // {new Outer();}
    // }
    // }
    public void test_getParameterAnnotations() throws Exception {
        Constructor<ConstructorTest.ConstructorTestHelper> ctor1 = ConstructorTest.ConstructorTestHelper.class.getConstructor(Object.class);
        Annotation[][] paramAnnotations = ctor1.getParameterAnnotations();
        TestCase.assertEquals("Annotations for wrong number of parameters returned", 1, paramAnnotations.length);
        TestCase.assertEquals("Wrong number of annotations returned", 2, paramAnnotations[0].length);
        Set<Class<?>> ignoreOrder = new HashSet<Class<?>>();
        ignoreOrder.add(paramAnnotations[0][0].annotationType());
        ignoreOrder.add(paramAnnotations[0][1].annotationType());
        TestCase.assertTrue("Missing ConstructorTestAnnotationRuntime0", ignoreOrder.contains(ConstructorTest.ConstructorTestAnnotationRuntime0.class));
        TestCase.assertTrue("Missing ConstructorTestAnnotationRuntime1", ignoreOrder.contains(ConstructorTest.ConstructorTestAnnotationRuntime1.class));
    }

    public void test_getDeclaredAnnotations() throws Exception {
        Constructor<ConstructorTest.ConstructorTestHelper> ctor1 = null;
        ctor1 = ConstructorTest.ConstructorTestHelper.class.getConstructor(new Class[0]);
        Annotation[] annotations = ctor1.getDeclaredAnnotations();
        TestCase.assertEquals("Wrong number of annotations returned", 2, annotations.length);
        Set<Class<?>> ignoreOrder = new HashSet<Class<?>>();
        ignoreOrder.add(annotations[0].annotationType());
        ignoreOrder.add(annotations[1].annotationType());
        TestCase.assertTrue("Missing ConstructorTestAnnotationRuntime0", ignoreOrder.contains(ConstructorTest.ConstructorTestAnnotationRuntime0.class));
        TestCase.assertTrue("Missing ConstructorTestAnnotationRuntime1", ignoreOrder.contains(ConstructorTest.ConstructorTestAnnotationRuntime1.class));
    }

    public void test_isVarArgs() throws Exception {
        Constructor<ConstructorTest.ConstructorTestHelper> varArgCtor = ConstructorTest.ConstructorTestHelper.class.getConstructor(String[].class);
        TestCase.assertTrue("Vararg constructor not recognized", varArgCtor.isVarArgs());
        Constructor<ConstructorTest.ConstructorTestHelper> nonVarArgCtor = ConstructorTest.ConstructorTestHelper.class.getConstructor(Object.class);
        TestCase.assertFalse("Non vararg constructor recognized as vararg constructor", nonVarArgCtor.isVarArgs());
    }

    public void test_hashCode() throws Exception {
        Constructor<ConstructorTest.ConstructorTestHelper> constructor = ConstructorTest.ConstructorTestHelper.class.getConstructor();
        TestCase.assertEquals("The constructor's hashCode is not equal to the hashCode of the name of the declaring class", ConstructorTest.ConstructorTestHelper.class.getName().hashCode(), constructor.hashCode());
    }

    public void test_equalsLjava_lang_Object() {
        Constructor<ConstructorTest.ConstructorTestHelper> ctor1 = null;
        Constructor<ConstructorTest.ConstructorTestHelper> ctor2 = null;
        try {
            ctor1 = ConstructorTest.ConstructorTestHelper.class.getConstructor(new Class[0]);
            ctor2 = ConstructorTest.ConstructorTestHelper.class.getConstructor(Object.class);
        } catch (Exception e) {
            TestCase.fail(("Exception during equals test : " + (e.getMessage())));
        }
        TestCase.assertTrue("Different Contructors returned equal", (!(ctor1.equals(ctor2))));
    }

    public void test_getDeclaringClass() {
        boolean val = false;
        try {
            Class<? extends ConstructorTest.ConstructorTestHelper> pclass = new ConstructorTest.ConstructorTestHelper().getClass();
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = pclass.getConstructor(new Class[0]);
            val = ctor.getDeclaringClass().equals(pclass);
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        TestCase.assertTrue("Returned incorrect declaring class", val);
    }

    public void test_getExceptionTypes() {
        // Test for method java.lang.Class []
        // java.lang.reflect.Constructor.getExceptionTypes()
        Class[] exceptions = null;
        Class<? extends IndexOutOfBoundsException> ex = null;
        try {
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getConstructor(new Class[0]);
            exceptions = ctor.getExceptionTypes();
            ex = new IndexOutOfBoundsException().getClass();
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        TestCase.assertEquals("Returned exception list of incorrect length", 1, exceptions.length);
        TestCase.assertTrue("Returned incorrect exception", exceptions[0].equals(ex));
    }

    public void test_getModifiers() {
        // Test for method int java.lang.reflect.Constructor.getModifiers()
        int mod = 0;
        try {
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getConstructor(new Class[0]);
            mod = ctor.getModifiers();
            TestCase.assertTrue("Returned incorrect modifers for public ctor", (((mod & (Modifier.PUBLIC)) == (Modifier.PUBLIC)) && ((mod & (Modifier.PRIVATE)) == 0)));
        } catch (NoSuchMethodException e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        try {
            Class[] cl = new Class[]{ int.class };
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getDeclaredConstructor(cl);
            mod = ctor.getModifiers();
            TestCase.assertTrue("Returned incorrect modifers for private ctor", (((mod & (Modifier.PRIVATE)) == (Modifier.PRIVATE)) && ((mod & (Modifier.PUBLIC)) == 0)));
        } catch (NoSuchMethodException e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        try {
            Class[] cl = new Class[]{ long.class };
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getDeclaredConstructor(cl);
            mod = ctor.getModifiers();
            TestCase.assertTrue("Returned incorrect modifers for private ctor", (((mod & (Modifier.PROTECTED)) == (Modifier.PROTECTED)) && ((mod & (Modifier.PUBLIC)) == 0)));
        } catch (NoSuchMethodException e) {
            TestCase.fail(("NoSuchMethodException during test : " + (e.getMessage())));
        }
    }

    public void test_getName() {
        // Test for method java.lang.String
        // java.lang.reflect.Constructor.getName()
        try {
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getConstructor(new Class[0]);
            TestCase.assertTrue(("Returned incorrect name: " + (ctor.getName())), ctor.getName().equals("tests.api.java.lang.reflect.ConstructorTest$ConstructorTestHelper"));
        } catch (Exception e) {
            TestCase.fail(("Exception obtaining contructor : " + (e.getMessage())));
        }
    }

    public void test_getParameterTypes() {
        // Test for method java.lang.Class []
        // java.lang.reflect.Constructor.getParameterTypes()
        Class[] types = null;
        try {
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getConstructor(new Class[0]);
            types = ctor.getParameterTypes();
        } catch (Exception e) {
            TestCase.fail(("Exception during getParameterTypes test:" + (e.toString())));
        }
        TestCase.assertEquals("Incorrect parameter returned", 0, types.length);
        Class[] parms = null;
        try {
            parms = new Class[1];
            parms[0] = new Object().getClass();
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getConstructor(parms);
            types = ctor.getParameterTypes();
        } catch (Exception e) {
            TestCase.fail(("Exception during getParameterTypes test:" + (e.toString())));
        }
        TestCase.assertTrue("Incorrect parameter returned", types[0].equals(parms[0]));
    }

    public void test_newInstance$Ljava_lang_Object() {
        // Test for method java.lang.Object
        // java.lang.reflect.Constructor.newInstance(java.lang.Object [])
        ConstructorTest.ConstructorTestHelper test = null;
        try {
            Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = new ConstructorTest.ConstructorTestHelper().getClass().getConstructor(new Class[0]);
            test = ctor.newInstance(((Object[]) (null)));
        } catch (Exception e) {
            TestCase.fail(("Failed to create instance : " + (e.getMessage())));
        }
        TestCase.assertEquals("improper instance created", 99, test.check());
    }

    public void test_toString() {
        // Test for method java.lang.String
        // java.lang.reflect.Constructor.toString()
        Class[] parms = null;
        Constructor<? extends ConstructorTest.ConstructorTestHelper> ctor = null;
        try {
            parms = new Class[1];
            parms[0] = new Object().getClass();
            ctor = new ConstructorTest.ConstructorTestHelper().getClass().getConstructor(parms);
        } catch (Exception e) {
            TestCase.fail(("Exception during getParameterTypes test:" + (e.toString())));
        }
        TestCase.assertTrue(("Returned incorrect string representation: " + (ctor.toString())), ctor.toString().equals("public tests.api.java.lang.reflect.ConstructorTest$ConstructorTestHelper(java.lang.Object)"));
    }

    public void test_getConstructor() throws Exception {
        // Passing new Class[0] should be equivalent to (Class[]) null.
        Class<ConstructorTest.ConstructorTestHelper> c2 = ConstructorTest.ConstructorTestHelper.class;
        TestCase.assertEquals(c2.getConstructor(new Class[0]), c2.getConstructor(((Class[]) (null))));
        TestCase.assertEquals(c2.getDeclaredConstructor(new Class[0]), c2.getDeclaredConstructor(((Class[]) (null))));
        // We can get a non-public constructor via getDeclaredConstructor...
        Class<ConstructorTest.NoPublicConstructorTestHelper> c1 = ConstructorTest.NoPublicConstructorTestHelper.class;
        c1.getDeclaredConstructor(((Class[]) (null)));
        // ...but not with getConstructor (which only returns public constructors).
        try {
            c1.getConstructor(((Class[]) (null)));
            TestCase.fail("Should throw NoSuchMethodException");
        } catch (NoSuchMethodException ex) {
            // Expected.
        }
    }
}

