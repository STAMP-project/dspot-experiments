/**
 * contributor license agreements.  See the NOTICE file distributed with
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
/**
 * import tests.support.Support_ClassLoader;
 */
package libcore.java.lang;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import junit.framework.TestCase;


@SuppressWarnings("deprecation")
public class OldClassTest extends TestCase {
    public static final String FILENAME = (OldClassTest.class.getPackage().getName().replace('.', '/')) + "/test#.properties";

    final String packageName = getClass().getPackage().getName();

    final String classNameInitError1 = (packageName) + ".TestClass1";

    final String classNameInitError2 = (packageName) + ".TestClass1B";

    final String classNameLinkageError = (packageName) + ".TestClass";

    final String sourceJARfile = "illegalClasses.jar";

    final String illegalClassName = "illegalClass";

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
        String value();
    }

    public static class TestClass1C {
        static OldClassTest.TestClass2 tc = new OldClassTest.TestClass2(0);

        TestClass1C() {
        }
    }

    public static class TestClass2 {
        public TestClass2(int i) throws IllegalArgumentException {
            throw new IllegalArgumentException();
        }
    }

    public static class TestClass3 {
        private TestClass3() {
        }
    }

    interface TestInterface {
        public static int TEST_INTERFACE_FIELD = 0;

        int getCount();

        void setCount(int value);
    }

    static class StaticMember$Class {
        class Member2$A {}
    }

    class Member$Class {
        class Member3$B {}
    }

    @Deprecated
    @OldClassTest.TestAnnotation("libcore.java.lang.OldClassTest$ExtendTestClass")
    public static class ExtendTestClass extends OldClassTest.PublicTestClass {
        private static final long serialVersionUID = 1L;

        public enum enumExm {

            ONE,
            TWO,
            THREE;}

        @Override
        public void setCount(int value) {
        }
    }

    public class ExtendTestClass1 extends OldClassTest.ExtendTestClass {}

    @OldClassTest.TestAnnotation("libcore.java.lang.OldClassTest$PublicTestClass")
    public static class PublicTestClass implements Serializable , Cloneable , OldClassTest.TestInterface {
        private static final long serialVersionUID = 1L;

        public static String TEST_FIELD = "test field";

        Object clazz;

        public PublicTestClass() {
            class LocalClass {}
            clazz = new LocalClass();
        }

        public Object getLocalClass() {
            class LocalClass {}
            Object returnedObject = new LocalClass();
            return returnedObject;
        }

        int count = 0;

        public int getCount() {
            return count;
        }

        public void setCount(int value) {
            count = value;
        }

        private class PrivateClass1 {
            public String toString() {
                return "PrivateClass0";
            }
        }

        public class PrivateClass2 {
            public String toString() {
                return "PrivateClass1";
            }
        }
    }

    public static class TestClass {
        @SuppressWarnings("unused")
        private int privField = 1;

        public int pubField = 2;

        private Object cValue = null;

        public Object ack = new Object();

        @SuppressWarnings("unused")
        private int privMethod() {
            return 1;
        }

        public int pubMethod() {
            return 2;
        }

        public Object cValue() {
            return cValue;
        }

        public TestClass() {
        }

        @SuppressWarnings("unused")
        private TestClass(Object o) {
        }
    }

    public static class SubTestClass extends OldClassTest.TestClass {}

    interface Intf1 {
        public int field1 = 1;

        public int field2 = 1;

        void test();
    }

    interface Intf2 {
        public int field1 = 1;

        void test();
    }

    interface Intf3 extends OldClassTest.Intf1 {
        public int field1 = 1;
    }

    interface Intf4 extends OldClassTest.Intf1 , OldClassTest.Intf2 {
        public int field1 = 1;

        void test2(int a, Object b);
    }

    interface Intf5 extends OldClassTest.Intf1 {}

    class Cls1 implements OldClassTest.Intf2 {
        public int field1 = 2;

        public int field2 = 2;

        public void test() {
        }
    }

    class Cls2 extends OldClassTest.Cls1 implements OldClassTest.Intf1 {
        public int field1 = 2;

        @Override
        public void test() {
        }
    }

    class Cls3 implements OldClassTest.Intf3 , OldClassTest.Intf4 {
        public void test() {
        }

        public void test2(int a, Object b) {
        }
    }

    static class Cls4 {}

    public void test_getAnnotations() {
        Annotation[] annotations = OldClassTest.PublicTestClass.class.getAnnotations();
        TestCase.assertEquals(1, annotations.length);
        TestCase.assertEquals(OldClassTest.TestAnnotation.class, annotations[0].annotationType());
        annotations = OldClassTest.ExtendTestClass.class.getAnnotations();
        TestCase.assertEquals(2, annotations.length);
        for (int i = 0; i < (annotations.length); i++) {
            Class<? extends Annotation> type = annotations[i].annotationType();
            TestCase.assertTrue(((("Annotation's type " + i) + ": ") + type), ((type.equals(Deprecated.class)) || (type.equals(OldClassTest.TestAnnotation.class))));
        }
    }

    public void test_forNameLjava_lang_StringLbooleanLClassLoader() throws Exception {
        ClassLoader pcl = getClass().getClassLoader();
        Class<?>[] classes = new Class<?>[]{ OldClassTest.PublicTestClass.class, OldClassTest.ExtendTestClass.class, OldClassTest.ExtendTestClass1.class, OldClassTest.TestInterface.class, String.class };
        for (int i = 0; i < (classes.length); i++) {
            Class<?> clazz = Class.forName(classes[i].getName(), true, pcl);
            TestCase.assertEquals(classes[i], clazz);
            clazz = Class.forName(classes[i].getName(), false, pcl);
            TestCase.assertEquals(classes[i], clazz);
        }
        Class<?>[] systemClasses = new Class<?>[]{ String.class, Integer.class, Object.class, Object[].class };
        for (int i = 0; i < (systemClasses.length); i++) {
            Class<?> clazz = Class.forName(systemClasses[i].getName(), true, ClassLoader.getSystemClassLoader());
            TestCase.assertEquals(systemClasses[i], clazz);
            clazz = Class.forName(systemClasses[i].getName(), false, ClassLoader.getSystemClassLoader());
            TestCase.assertEquals(systemClasses[i], clazz);
        }
        try {
            Class.forName(null, true, pcl);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            Class.forName("NotExistClass", true, pcl);
            TestCase.fail("ClassNotFoundException is not thrown for non existent class.");
        } catch (ClassNotFoundException cnfe) {
            // expected
        }
        try {
            Class.forName("String", false, pcl);
            TestCase.fail("ClassNotFoundException is not thrown for non existent class.");
        } catch (ClassNotFoundException cnfe) {
            // expected
        }
        try {
            Class.forName("libcore.java.lang.NonexistentClass", false, pcl);
            TestCase.fail("ClassNotFoundException is not thrown for non existent class.");
        } catch (ClassNotFoundException cnfe) {
            // expected
        }
    }

    // AndroidOnly: Class.forName method throws ClassNotFoundException on Android.
    public void test_forNameLjava_lang_StringLbooleanLClassLoader_AndroidOnly() throws Exception {
        // Android doesn't support loading class files from a jar.
        try {
            URL url = getClass().getClassLoader().getResource((((packageName.replace(".", "/")) + "/") + (sourceJARfile)));
            ClassLoader loader = new URLClassLoader(new URL[]{ url }, getClass().getClassLoader());
            try {
                Class.forName(classNameLinkageError, true, loader);
                TestCase.fail("LinkageError or ClassNotFoundException expected.");
            } catch (LinkageError le) {
                // Expected for the RI.
            } catch (ClassNotFoundException ce) {
                // Expected for Android.
            }
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception was thrown: " + (e.toString())));
        }
        try {
            Class.forName(classNameInitError2, true, getClass().getClassLoader());
            TestCase.fail(("ExceptionInInitializerError or ClassNotFoundException " + "should be thrown."));
        } catch (ExceptionInInitializerError ie) {
            // Expected for the RI.
            // Remove this comment to let the test pass on Android.
        } catch (ClassNotFoundException ce) {
            // Expected for Android.
        }
    }

    public void test_getAnnotation() {
        OldClassTest.TestAnnotation target = OldClassTest.PublicTestClass.class.getAnnotation(OldClassTest.TestAnnotation.class);
        TestCase.assertEquals(target.value(), OldClassTest.PublicTestClass.class.getName());
        TestCase.assertNull(OldClassTest.PublicTestClass.class.getAnnotation(Deprecated.class));
        Deprecated target2 = OldClassTest.ExtendTestClass.class.getAnnotation(Deprecated.class);
        TestCase.assertNotNull(target2);
    }

    public void test_getDeclaredAnnotations() {
        Annotation[] annotations = OldClassTest.PublicTestClass.class.getDeclaredAnnotations();
        TestCase.assertEquals(1, annotations.length);
        annotations = OldClassTest.ExtendTestClass.class.getDeclaredAnnotations();
        TestCase.assertEquals(2, annotations.length);
        annotations = OldClassTest.TestInterface.class.getDeclaredAnnotations();
        TestCase.assertEquals(0, annotations.length);
        annotations = String.class.getDeclaredAnnotations();
        TestCase.assertEquals(0, annotations.length);
    }

    public void test_getEnclosingClass() {
        Class clazz = OldClassTest.class.getEnclosingClass();
        TestCase.assertNull(clazz);
        TestCase.assertEquals(getClass(), OldClassTest.Cls1.class.getEnclosingClass());
        TestCase.assertEquals(getClass(), OldClassTest.Intf1.class.getEnclosingClass());
        TestCase.assertEquals(getClass(), OldClassTest.Cls4.class.getEnclosingClass());
    }

    public void test_getEnclosingMethod() {
        Method clazz = OldClassTest.ExtendTestClass.class.getEnclosingMethod();
        TestCase.assertNull(clazz);
        OldClassTest.PublicTestClass ptc = new OldClassTest.PublicTestClass();
        try {
            TestCase.assertEquals("getEnclosingMethod returns incorrect method.", OldClassTest.PublicTestClass.class.getMethod("getLocalClass", ((Class[]) (null))), ptc.getLocalClass().getClass().getEnclosingMethod());
        } catch (NoSuchMethodException nsme) {
            TestCase.fail("NoSuchMethodException was thrown.");
        }
    }

    public void test_getEnclosingConstructor() {
        OldClassTest.PublicTestClass ptc = new OldClassTest.PublicTestClass();
        TestCase.assertEquals("getEnclosingConstructor method returns incorrect class.", OldClassTest.PublicTestClass.class.getConstructors()[0], ptc.clazz.getClass().getEnclosingConstructor());
        TestCase.assertNull(("getEnclosingConstructor should return null for local " + "class declared in method."), ptc.getLocalClass().getClass().getEnclosingConstructor());
        TestCase.assertNull(("getEnclosingConstructor should return null for local " + "class declared in method."), OldClassTest.ExtendTestClass.class.getEnclosingConstructor());
    }

    public void test_getEnumConstants() {
        Object[] clazz = OldClassTest.ExtendTestClass.class.getEnumConstants();
        TestCase.assertNull(clazz);
        Object[] constants = OldClassTest.TestEnum.class.getEnumConstants();
        TestCase.assertEquals(OldClassTest.TestEnum.values().length, constants.length);
        for (int i = 0; i < (constants.length); i++) {
            TestCase.assertEquals(OldClassTest.TestEnum.values()[i], constants[i]);
        }
        TestCase.assertEquals(0, OldClassTest.TestEmptyEnum.class.getEnumConstants().length);
    }

    public enum TestEnum {

        ONE,
        TWO,
        THREE;}

    public enum TestEmptyEnum {
        ;
    }

    public void test_getGenericInterfaces() {
        Type[] types = OldClassTest.ExtendTestClass1.class.getGenericInterfaces();
        TestCase.assertEquals(0, types.length);
        Class[] interfaces = new Class[]{ OldClassTest.TestInterface.class, Serializable.class, Cloneable.class };
        types = OldClassTest.PublicTestClass.class.getGenericInterfaces();
        TestCase.assertEquals(interfaces.length, types.length);
        for (int i = 0; i < (types.length); i++) {
            TestCase.assertEquals(interfaces[i], types[i]);
        }
        types = OldClassTest.TestInterface.class.getGenericInterfaces();
        TestCase.assertEquals(0, types.length);
        types = List.class.getGenericInterfaces();
        TestCase.assertEquals(1, types.length);
        TestCase.assertEquals(Collection.class, ((ParameterizedType) (types[0])).getRawType());
        TestCase.assertEquals(0, int.class.getGenericInterfaces().length);
        TestCase.assertEquals(0, void.class.getGenericInterfaces().length);
    }

    public void test_getGenericSuperclass() {
        TestCase.assertEquals(OldClassTest.PublicTestClass.class, OldClassTest.ExtendTestClass.class.getGenericSuperclass());
        TestCase.assertEquals(OldClassTest.ExtendTestClass.class, OldClassTest.ExtendTestClass1.class.getGenericSuperclass());
        TestCase.assertEquals(Object.class, OldClassTest.PublicTestClass.class.getGenericSuperclass());
        TestCase.assertEquals(Object.class, String.class.getGenericSuperclass());
        TestCase.assertEquals(null, OldClassTest.TestInterface.class.getGenericSuperclass());
        ParameterizedType type = ((ParameterizedType) (Vector.class.getGenericSuperclass()));
        TestCase.assertEquals(AbstractList.class, type.getRawType());
    }

    // RoboVM note: Uses custom classloader which isn't supported on RoboVM
    // // AndroidOnly: Uses dalvik.system.PathClassLoader.
    // // Different behavior between cts host and run-core-test")
    // public void test_getPackage() {
    // Package thisPackage = getClass().getPackage();
    // assertEquals("libcore.java.lang",
    // thisPackage.getName());
    // Package stringPackage = String.class.getPackage();
    // assertNotNull("java.lang", stringPackage.getName());
    // String hyts_package_name = "hyts_package_dex.jar";
    // File resources = Support_Resources.createTempFolder();
    // Support_Resources.copyFile(resources, "Package", hyts_package_name);
    // String resPath = resources.toString();
    // if (resPath.charAt(0) == '/' || resPath.charAt(0) == '\\')
    // resPath = resPath.substring(1);
    // try {
    // URL resourceURL = new URL("file:/" + resPath + "/Package/"
    // + hyts_package_name);
    // ClassLoader cl = Support_ClassLoader.getInstance(resourceURL,
    // getClass().getClassLoader());
    // Class clazz = cl.loadClass("C");
    // assertNull("getPackage for C.class should return null",
    // clazz.getPackage());
    // clazz = cl.loadClass("a.b.C");
    // Package cPackage = clazz.getPackage();
    // assertNotNull("getPackage for a.b.C.class should not return null",
    // cPackage);
    // /*
    // * URLClassLoader doesn't work on Android for jar files
    // *
    // * URL url = getClass().getClassLoader().getResource(
    // *         packageName.replace(".", "/") + "/" + sourceJARfile);
    // *
    // * ClassLoader loader = new URLClassLoader(new URL[] { url }, null);
    // *
    // * try {
    // *     Class<?> clazz = loader.loadClass(illegalClassName);
    // *     Package pack = clazz.getPackage();
    // *     assertNull(pack);
    // * } catch(ClassNotFoundException cne) {
    // *     fail("ClassNotFoundException was thrown for " + illegalClassName);
    // * }
    // */
    // } catch(Exception e) {
    // fail("Unexpected exception was thrown: " + e.toString());
    // }
    // }
    public void test_getSigners() {
        TestCase.assertNull(void.class.getSigners());
        TestCase.assertNull(OldClassTest.PublicTestClass.class.getSigners());
    }

    public void test_getSimpleName() {
        TestCase.assertEquals("PublicTestClass", OldClassTest.PublicTestClass.class.getSimpleName());
        TestCase.assertEquals("void", void.class.getSimpleName());
        TestCase.assertEquals("int[]", int[].class.getSimpleName());
    }

    public void test_getTypeParameters() {
        TestCase.assertEquals(0, OldClassTest.PublicTestClass.class.getTypeParameters().length);
        TypeVariable[] tv = OldClassTest.TempTestClass1.class.getTypeParameters();
        TestCase.assertEquals(1, tv.length);
        TestCase.assertEquals(Object.class, tv[0].getBounds()[0]);
        OldClassTest.TempTestClass2<String> tc = new OldClassTest.TempTestClass2<String>();
        tv = tc.getClass().getTypeParameters();
        TestCase.assertEquals(1, tv.length);
        TestCase.assertEquals(String.class, tv[0].getBounds()[0]);
    }

    class TempTestClass1<T> {}

    class TempTestClass2<S extends String> extends OldClassTest.TempTestClass1<S> {}

    public void test_isAnnotation() {
        TestCase.assertTrue(Deprecated.class.isAnnotation());
        TestCase.assertTrue(OldClassTest.TestAnnotation.class.isAnnotation());
        TestCase.assertFalse(OldClassTest.PublicTestClass.class.isAnnotation());
        TestCase.assertFalse(String.class.isAnnotation());
    }

    public void test_isAnnotationPresent() {
        TestCase.assertTrue(OldClassTest.PublicTestClass.class.isAnnotationPresent(OldClassTest.TestAnnotation.class));
        TestCase.assertFalse(OldClassTest.ExtendTestClass1.class.isAnnotationPresent(OldClassTest.TestAnnotation.class));
        TestCase.assertFalse(String.class.isAnnotationPresent(Deprecated.class));
        TestCase.assertTrue(OldClassTest.ExtendTestClass.class.isAnnotationPresent(OldClassTest.TestAnnotation.class));
        TestCase.assertTrue(OldClassTest.ExtendTestClass.class.isAnnotationPresent(Deprecated.class));
    }

    public void test_isAnonymousClass() {
        TestCase.assertFalse(OldClassTest.PublicTestClass.class.isAnonymousClass());
        TestCase.assertTrue(new Thread() {}.getClass().isAnonymousClass());
    }

    public void test_isEnum() {
        TestCase.assertFalse(OldClassTest.PublicTestClass.class.isEnum());
        TestCase.assertFalse(OldClassTest.ExtendTestClass.class.isEnum());
        TestCase.assertTrue(OldClassTest.TestEnum.ONE.getClass().isEnum());
        TestCase.assertTrue(OldClassTest.TestEnum.class.isEnum());
    }

    public void test_isLocalClass() {
        TestCase.assertFalse(OldClassTest.ExtendTestClass.class.isLocalClass());
        TestCase.assertFalse(OldClassTest.TestInterface.class.isLocalClass());
        TestCase.assertFalse(OldClassTest.TestEnum.class.isLocalClass());
        class InternalClass {}
        TestCase.assertTrue(InternalClass.class.isLocalClass());
    }

    public void test_isMemberClass() {
        TestCase.assertFalse(OldClassTest.class.isMemberClass());
        TestCase.assertFalse(String.class.isMemberClass());
        TestCase.assertTrue(OldClassTest.TestEnum.class.isMemberClass());
        TestCase.assertTrue(OldClassTest.StaticMember$Class.class.isMemberClass());
    }

    public void test_isSynthetic() {
        TestCase.assertFalse("Returned true for non synthetic class.", OldClassTest.ExtendTestClass.class.isSynthetic());
        TestCase.assertFalse("Returned true for non synthetic class.", OldClassTest.TestInterface.class.isSynthetic());
        TestCase.assertFalse("Returned true for non synthetic class.", String.class.isSynthetic());
    }

    public void test_getCanonicalName() {
        Class[] classArray = new Class[]{ int.class, int[].class, String.class, OldClassTest.PublicTestClass.class, OldClassTest.TestInterface.class, OldClassTest.ExtendTestClass.class };
        String[] classNames = new String[]{ "int", "int[]", "java.lang.String", "libcore.java.lang.OldClassTest.PublicTestClass", "libcore.java.lang.OldClassTest.TestInterface", "libcore.java.lang.OldClassTest.ExtendTestClass" };
        for (int i = 0; i < (classArray.length); i++) {
            TestCase.assertEquals(classNames[i], classArray[i].getCanonicalName());
        }
    }

    public void test_getClassLoader() {
        TestCase.assertEquals(OldClassTest.ExtendTestClass.class.getClassLoader(), OldClassTest.PublicTestClass.class.getClassLoader());
        TestCase.assertNull(int.class.getClassLoader());
        TestCase.assertNull(void.class.getClassLoader());
    }

    public void test_getClasses() {
        TestCase.assertEquals("Incorrect class array returned", 11, OldClassTest.class.getClasses().length);
    }

    public void test_getDeclaredClasses() {
        Class[] declClasses = Object.class.getDeclaredClasses();
        TestCase.assertEquals(("Incorrect length of declared classes array is returned " + "for Object."), 0, declClasses.length);
        declClasses = OldClassTest.PublicTestClass.class.getDeclaredClasses();
        TestCase.assertEquals(2, declClasses.length);
        TestCase.assertEquals(0, int.class.getDeclaredClasses().length);
        TestCase.assertEquals(0, void.class.getDeclaredClasses().length);
        for (int i = 0; i < (declClasses.length); i++) {
            Constructor<?> constr = declClasses[i].getDeclaredConstructors()[0];
            constr.setAccessible(true);
            OldClassTest.PublicTestClass publicClazz = new OldClassTest.PublicTestClass();
            try {
                Object o = constr.newInstance(publicClazz);
                TestCase.assertTrue(("Returned incorrect class: " + (o.toString())), o.toString().startsWith("PrivateClass"));
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception was thrown: " + (e.toString())));
            }
        }
        declClasses = OldClassTest.TestInterface.class.getDeclaredClasses();
        TestCase.assertEquals(0, declClasses.length);
    }

    public void test_getDeclaredConstructor$Ljava_lang_Class() throws Exception {
        try {
            OldClassTest.TestClass.class.getDeclaredConstructor(String.class);
            TestCase.fail("NoSuchMethodException should be thrown.");
        } catch (NoSuchMethodException nsme) {
            // expected
        }
    }

    public void test_getDeclaredFieldLjava_lang_String() throws Exception {
        try {
            OldClassTest.TestClass.class.getDeclaredField(null);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            OldClassTest.TestClass.class.getDeclaredField("NonExistentField");
            TestCase.fail("NoSuchFieldException is not thrown.");
        } catch (NoSuchFieldException nsfe) {
            // expected
        }
    }

    public void test_getDeclaredMethodLjava_lang_String$Ljava_lang_Class() throws Exception {
        try {
            OldClassTest.TestClass.class.getDeclaredMethod(null, new Class[0]);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            OldClassTest.TestClass.class.getDeclaredMethod("NonExistentMethod", new Class[0]);
            TestCase.fail("NoSuchMethodException is not thrown.");
        } catch (NoSuchMethodException nsme) {
            // expected
        }
    }

    public void test_getMethodLjava_lang_String$Ljava_lang_Class() throws Exception {
        Method m = OldClassTest.ExtendTestClass1.class.getMethod("getCount", new Class[0]);
        TestCase.assertEquals("Returned incorrect method", 0, ((Integer) (m.invoke(new OldClassTest.ExtendTestClass1()))).intValue());
        try {
            m = OldClassTest.TestClass.class.getMethod("init", new Class[0]);
            TestCase.fail("Failed to throw exception accessing to init method");
        } catch (NoSuchMethodException e) {
            // Correct
            return;
        }
        try {
            OldClassTest.TestClass.class.getMethod("pubMethod", new Class[0]);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    public void test_getDeclaringClass() {
        TestCase.assertNull(OldClassTest.class.getDeclaringClass());
        TestCase.assertNotNull(OldClassTest.PublicTestClass.class.getDeclaringClass());
    }

    public void test_getFieldLjava_lang_String() throws Exception {
        Field f = OldClassTest.TestClass.class.getField("pubField");
        TestCase.assertEquals("Returned incorrect field", 2, f.getInt(new OldClassTest.TestClass()));
        f = OldClassTest.PublicTestClass.class.getField("TEST_FIELD");
        TestCase.assertEquals("Returned incorrect field", "test field", f.get(new OldClassTest.PublicTestClass()));
        f = OldClassTest.PublicTestClass.class.getField("TEST_INTERFACE_FIELD");
        TestCase.assertEquals("Returned incorrect field", 0, f.getInt(new OldClassTest.PublicTestClass()));
        try {
            OldClassTest.TestClass.class.getField(null);
            TestCase.fail("NullPointerException is thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    public void test_getFields2() throws Exception {
        Field[] f;
        Field expected = null;
        f = OldClassTest.PublicTestClass.class.getFields();
        TestCase.assertEquals("Test 1: Incorrect number of fields;", 2, f.length);
        f = OldClassTest.Cls2.class.getFields();
        TestCase.assertEquals("Test 2: Incorrect number of fields;", 6, f.length);
        f = OldClassTest.Cls3.class.getFields();
        TestCase.assertEquals("Test 2: Incorrect number of fields;", 5, f.length);
        for (Field field : f) {
            if (field.toString().equals(("public static final int " + "libcore.java.lang.OldClassTest$Intf3.field1"))) {
                expected = field;
                break;
            }
        }
        if (expected == null) {
            TestCase.fail("Test 3: getFields() did not return all fields.");
        }
        TestCase.assertEquals("Test 4: Incorrect field;", expected, OldClassTest.Cls3.class.getField("field1"));
        expected = null;
        for (Field field : f) {
            if (field.toString().equals(("public static final int " + "libcore.java.lang.OldClassTest$Intf1.field2"))) {
                expected = field;
                break;
            }
        }
        if (expected == null) {
            TestCase.fail("Test 5: getFields() did not return all fields.");
        }
        TestCase.assertEquals("Test 6: Incorrect field;", expected, OldClassTest.Cls3.class.getField("field2"));
    }

    public void test_getFields() throws Exception {
        Field expected = null;
        Field[] fields = OldClassTest.Cls2.class.getFields();
        for (Field field : fields) {
            if (field.toString().equals("public int libcore.java.lang.OldClassTest$Cls2.field1")) {
                expected = field;
                break;
            }
        }
        if (expected == null) {
            TestCase.fail("getFields() did not return all fields");
        }
        TestCase.assertEquals(expected, OldClassTest.Cls2.class.getField("field1"));
    }

    public void test_getInterfaces() {
        Class[] interfaces1 = OldClassTest.Cls1.class.getInterfaces();
        TestCase.assertEquals(1, interfaces1.length);
        TestCase.assertEquals(OldClassTest.Intf2.class, interfaces1[0]);
        Class[] interfaces2 = OldClassTest.Cls2.class.getInterfaces();
        TestCase.assertEquals(1, interfaces2.length);
        TestCase.assertEquals(OldClassTest.Intf1.class, interfaces2[0]);
        Class[] interfaces3 = OldClassTest.Cls3.class.getInterfaces();
        TestCase.assertEquals(2, interfaces3.length);
        TestCase.assertEquals(OldClassTest.Intf3.class, interfaces3[0]);
        TestCase.assertEquals(OldClassTest.Intf4.class, interfaces3[1]);
        Class[] interfaces4 = OldClassTest.Cls4.class.getInterfaces();
        TestCase.assertEquals(0, interfaces4.length);
    }

    public void test_getMethods() throws Exception {
        TestCase.assertEquals("Incorrect number of methods", 10, OldClassTest.Cls2.class.getMethods().length);
        TestCase.assertEquals("Incorrect number of methods", 11, OldClassTest.Cls3.class.getMethods().length);
        Method expected = null;
        Method[] methods = OldClassTest.Cls2.class.getMethods();
        for (Method method : methods) {
            if (method.toString().equals("public void libcore.java.lang.OldClassTest$Cls2.test()")) {
                expected = method;
                break;
            }
        }
        if (expected == null) {
            TestCase.fail("getMethods() did not return all methods");
        }
        TestCase.assertEquals(expected, OldClassTest.Cls2.class.getMethod("test"));
        expected = null;
        methods = OldClassTest.Cls3.class.getMethods();
        for (Method method : methods) {
            if (method.toString().equals("public void libcore.java.lang.OldClassTest$Cls3.test()")) {
                expected = method;
                break;
            }
        }
        if (expected == null) {
            TestCase.fail("getMethods() did not return all methods");
        }
        TestCase.assertEquals(expected, OldClassTest.Cls3.class.getMethod("test"));
        expected = null;
        methods = OldClassTest.Cls3.class.getMethods();
        for (Method method : methods) {
            if (method.toString().equals(("public void libcore.java.lang.OldClassTest$Cls3.test2(int," + "java.lang.Object)"))) {
                expected = method;
                break;
            }
        }
        if (expected == null) {
            TestCase.fail("getMethods() did not return all methods");
        }
        TestCase.assertEquals(expected, OldClassTest.Cls3.class.getMethod("test2", int.class, Object.class));
        TestCase.assertEquals("Incorrect number of methods", 1, OldClassTest.Intf5.class.getMethods().length);
    }

    public void test_getResourceLjava_lang_String() {
        TestCase.assertNull(getClass().getResource("libcore/java/lang/NonExistentResource"));
        TestCase.assertNull(getClass().getResource(((getClass().getName()) + "NonExistentResource")));
    }

    public void test_getResourceAsStreamLjava_lang_String() throws Exception {
        String name = "/HelloWorld.txt";
        TestCase.assertNotNull(((("the file " + name) + " can not be found in this ") + "directory"), getClass().getResourceAsStream(name));
        final String nameBadURI = "org/apache/harmony/luni/tests/test_resource.txt";
        TestCase.assertNull((("the file " + nameBadURI) + " should not be found in this directory"), getClass().getResourceAsStream(nameBadURI));
        ClassLoader pcl = getClass().getClassLoader();
        Class<?> clazz = pcl.loadClass("libcore.java.lang.OldClassTest");
        TestCase.assertNotNull(clazz.getResourceAsStream("HelloWorld1.txt"));
        try {
            getClass().getResourceAsStream(null);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    public void test_isAssignableFromLjava_lang_Class() {
        TestCase.assertFalse("returned true not assignable classes", Integer.class.isAssignableFrom(String.class));
        try {
            Runnable.class.isAssignableFrom(null);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    public void test_newInstance() throws Exception {
        try {
            OldClassTest.TestClass3.class.newInstance();
            TestCase.fail("IllegalAccessException is not thrown.");
        } catch (IllegalAccessException iae) {
            // expected
        }
        try {
            OldClassTest.TestClass1C.class.newInstance();
            TestCase.fail("ExceptionInInitializerError should be thrown.");
        } catch (ExceptionInInitializerError ie) {
            // expected
        }
    }

    public void test_asSubclass1() {
        TestCase.assertEquals(OldClassTest.ExtendTestClass.class, OldClassTest.ExtendTestClass.class.asSubclass(OldClassTest.PublicTestClass.class));
        TestCase.assertEquals(OldClassTest.PublicTestClass.class, OldClassTest.PublicTestClass.class.asSubclass(OldClassTest.TestInterface.class));
        TestCase.assertEquals(OldClassTest.ExtendTestClass1.class, OldClassTest.ExtendTestClass1.class.asSubclass(OldClassTest.PublicTestClass.class));
        TestCase.assertEquals(OldClassTest.PublicTestClass.class, OldClassTest.PublicTestClass.class.asSubclass(OldClassTest.PublicTestClass.class));
    }

    public void test_asSubclass2() {
        try {
            OldClassTest.PublicTestClass.class.asSubclass(OldClassTest.ExtendTestClass.class);
            TestCase.fail("Test 1: ClassCastException expected.");
        } catch (ClassCastException cce) {
            // Expected.
        }
        try {
            OldClassTest.PublicTestClass.class.asSubclass(String.class);
            TestCase.fail("Test 2: ClassCastException expected.");
        } catch (ClassCastException cce) {
            // Expected.
        }
    }

    public void test_cast() {
        Object o = OldClassTest.PublicTestClass.class.cast(new OldClassTest.ExtendTestClass());
        TestCase.assertTrue((o instanceof OldClassTest.ExtendTestClass));
        try {
            OldClassTest.ExtendTestClass.class.cast(new OldClassTest.PublicTestClass());
            TestCase.fail("Test 1: ClassCastException expected.");
        } catch (ClassCastException cce) {
            // expected
        }
        try {
            OldClassTest.ExtendTestClass.class.cast(new String());
            TestCase.fail("ClassCastException is not thrown.");
        } catch (ClassCastException cce) {
            // expected
        }
    }

    public void test_desiredAssertionStatus() {
        Class[] classArray = new Class[]{ Object.class, Integer.class, String.class, OldClassTest.PublicTestClass.class, OldClassTest.ExtendTestClass.class, OldClassTest.ExtendTestClass1.class };
        for (int i = 0; i < (classArray.length); i++) {
            TestCase.assertFalse(("assertion status for " + (classArray[i])), classArray[i].desiredAssertionStatus());
        }
    }

    public void testGetResourceAsStream1() throws IOException {
        Class clazz = getClass();
        InputStream stream = clazz.getResourceAsStream("HelloWorld.txt");
        TestCase.assertNotNull(stream);
        byte[] buffer = new byte[20];
        int length = stream.read(buffer);
        String s = new String(buffer, 0, length);
        TestCase.assertEquals("Hello, World.\n", s);
        stream.close();
    }

    public void testGetResourceAsStream2() throws IOException {
        Class clazz = getClass();
        InputStream stream = clazz.getResourceAsStream("/libcore/java/lang/HelloWorld.txt");
        TestCase.assertNotNull(stream);
        byte[] buffer = new byte[20];
        int length = stream.read(buffer);
        String s = new String(buffer, 0, length);
        TestCase.assertEquals("Hello, World.\n", s);
        stream.close();
        try {
            clazz.getResourceAsStream(null);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
        TestCase.assertNull(clazz.getResourceAsStream("/NonExistentResource"));
        TestCase.assertNull(clazz.getResourceAsStream("libcore/java/lang/HelloWorld.txt"));
    }
}

