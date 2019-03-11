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
package org.apache.harmony.tests.java.lang;


import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import junit.framework.TestCase;


public class ClassTest extends TestCase {
    static class StaticMember$Class {
        class Member2$A {}
    }

    class Member$Class {
        class Member3$B {}
    }

    public static class TestClass {
        private int privField = 1;

        public int pubField = 2;

        private Object cValue = null;

        public Object ack = new Object();

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

        private TestClass(Object o) {
        }
    }

    public static class SubTestClass extends ClassTest.TestClass {}

    /**
     *
     *
     * @unknown java.lang.Class#forName(java.lang.String)
     */
    public void test_forNameLjava_lang_String() throws Exception {
        TestCase.assertEquals("Class for name failed for java.lang.Object", Object.class, Class.forName("java.lang.Object"));
        TestCase.assertEquals("Class for name failed for [[Ljava.lang.Object;", Object[][].class, Class.forName("[[Ljava.lang.Object;"));
        TestCase.assertEquals("Class for name failed for [I", int[].class, Class.forName("[I"));
        try {
            Class.forName("int");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("byte");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("char");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("void");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("short");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("long");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("boolean");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("float");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("double");
            TestCase.fail();
        } catch (ClassNotFoundException e) {
        }
        // regression test for JIRA 2162
        try {
            Class.forName("%");
            TestCase.fail("should throw ClassNotFoundException.");
        } catch (ClassNotFoundException e) {
        }
    }

    /**
     *
     *
     * @unknown java.lang.Class#getClasses()
     */
    /* TODO(tball): enable if Class.getClasses is mapped.
    public void test_getClasses() {
    assertEquals("Incorrect class array returned",
    2, ClassTest.class.getClasses().length);
    }
     */
    /**
     *
     *
     * @unknown java.lang.Class#getClasses()
     */
    /* TODO(tball): enable if SecurityManager is implemented.
    public void test_getClasses_subtest0() {
    final Permission privCheckPermission = new BasicPermission("Privilege check") {
    private static final long serialVersionUID = 1L;
    };

    class MyCombiner implements DomainCombiner {
    boolean combine;

    public ProtectionDomain[] combine(ProtectionDomain[] executionDomains,
    ProtectionDomain[] parentDomains) {
    combine = true;
    return new ProtectionDomain[0];
    }

    private boolean recurring = false;

    public boolean isPriviledged() {
    if (recurring) {
    return true;
    }
    try {
    recurring = true;
    combine = false;
    try {
    AccessController.checkPermission(privCheckPermission);
    } catch (SecurityException e) {}
    return !combine;
    } finally {
    recurring = false;
    }
    }
    }

    final MyCombiner combiner = new MyCombiner();
    class SecurityManagerCheck extends SecurityManager {
    String reason;

    Class<?> checkClass;

    int checkType;

    int checkPermission;

    int checkMemberAccess;

    int checkPackageAccess;

    public void setExpected(String reason, Class<?> cls, int type) {
    this.reason = reason;
    checkClass = cls;
    checkType = type;
    checkPermission = 0;
    checkMemberAccess = 0;
    checkPackageAccess = 0;
    }

    @Override
    public void checkPermission(Permission perm) {
    if (combiner.isPriviledged())
    return;
    checkPermission++;
    }

    @Override
    public void checkMemberAccess(Class<?> cls, int type) {
    if (combiner.isPriviledged())
    return;
    checkMemberAccess++;
    assertEquals(reason + " unexpected class", checkClass, cls);
    assertEquals(reason + "unexpected type", checkType, type);
    }

    @Override
    public void checkPackageAccess(String packageName) {
    if (combiner.isPriviledged())
    return;
    checkPackageAccess++;
    String name = checkClass.getName();
    int index = name.lastIndexOf('.');
    String checkPackage = name.substring(0, index);
    assertEquals(reason + " unexpected package",
    checkPackage,  packageName);
    }

    public void assertProperCalls() {
    assertEquals(reason + " unexpected checkPermission count",
    0, checkPermission);
    assertEquals(reason + " unexpected checkMemberAccess count",
    1, checkMemberAccess);
    assertEquals(reason + " unexpected checkPackageAccess count",
    1, checkPackageAccess);
    }
    }

    AccessControlContext acc = new AccessControlContext(new ProtectionDomain[0]);
    AccessControlContext acc2 = new AccessControlContext(acc, combiner);

    PrivilegedAction<?> action = new PrivilegedAction<Object>() {
    public Object run() {
    File resources = Support_Resources.createTempFolder();
    try {
    Support_Resources.copyFile(resources, null, "hyts_security.jar");
    File file = new File(resources.toString() + "/hyts_security.jar");
    URL url = new URL("file:" + file.getPath());
    ClassLoader loader = new URLClassLoader(new URL[] { url }, null);
    Class<?> cls = Class.forName("packB.SecurityTestSub", false, loader);
    SecurityManagerCheck sm = new SecurityManagerCheck();
    System.setSecurityManager(sm);
    try {
    sm.setExpected("getClasses", cls, Member.PUBLIC);
    cls.getClasses();
    sm.assertProperCalls();

    sm.setExpected("getDeclaredClasses", cls, Member.DECLARED);
    cls.getDeclaredClasses();
    sm.assertProperCalls();

    sm.setExpected("getConstructor", cls, Member.PUBLIC);
    cls.getConstructor(new Class[0]);
    sm.assertProperCalls();

    sm.setExpected("getConstructors", cls, Member.PUBLIC);
    cls.getConstructors();
    sm.assertProperCalls();

    sm.setExpected("getDeclaredConstructor", cls, Member.DECLARED);
    cls.getDeclaredConstructor(new Class[0]);
    sm.assertProperCalls();

    sm.setExpected("getDeclaredConstructors", cls, Member.DECLARED);
    cls.getDeclaredConstructors();
    sm.assertProperCalls();

    sm.setExpected("getField", cls, Member.PUBLIC);
    cls.getField("publicField");
    sm.assertProperCalls();

    sm.setExpected("getFields", cls, Member.PUBLIC);
    cls.getFields();
    sm.assertProperCalls();

    sm.setExpected("getDeclaredField", cls, Member.DECLARED);
    cls.getDeclaredField("publicField");
    sm.assertProperCalls();

    sm.setExpected("getDeclaredFields", cls, Member.DECLARED);
    cls.getDeclaredFields();
    sm.assertProperCalls();

    sm.setExpected("getDeclaredMethod", cls, Member.DECLARED);
    cls.getDeclaredMethod("publicMethod", new Class[0]);
    sm.assertProperCalls();

    sm.setExpected("getDeclaredMethods", cls, Member.DECLARED);
    cls.getDeclaredMethods();
    sm.assertProperCalls();

    sm.setExpected("getMethod", cls, Member.PUBLIC);
    cls.getMethod("publicMethod", new Class[0]);
    sm.assertProperCalls();

    sm.setExpected("getMethods", cls, Member.PUBLIC);
    cls.getMethods();
    sm.assertProperCalls();

    sm.setExpected("newInstance", cls, Member.PUBLIC);
    cls.newInstance();
    sm.assertProperCalls();
    } finally {
    System.setSecurityManager(null);
    }
    } catch (Exception e) {
    if (e instanceof RuntimeException)
    throw (RuntimeException) e;
    fail("unexpected exception: " + e);
    }
    return null;
    }
    };
    AccessController.doPrivileged(action, acc2);
    }
     */
    /**
     *
     *
     * @unknown java.lang.Class#getComponentType()
     */
    public void test_getComponentType() {
        TestCase.assertSame("int array does not have int component type", int.class, int[].class.getComponentType());
        TestCase.assertSame("Object array does not have Object component type", Object.class, Object[].class.getComponentType());
        TestCase.assertNull("Object has non-null component type", Object.class.getComponentType());
    }

    /**
     *
     *
     * @unknown java.lang.Class#getConstructor(java.lang.Class[])
     */
    public void test_getConstructor$Ljava_lang_Class() throws NoSuchMethodException {
        ClassTest.TestClass.class.getConstructor(new Class[0]);
        try {
            ClassTest.TestClass.class.getConstructor(Object.class);
            TestCase.fail("Found private constructor");
        } catch (NoSuchMethodException e) {
            // Correct - constructor with obj is private
        }
    }

    /**
     *
     *
     * @unknown java.lang.Class#getConstructors()
     */
    public void test_getConstructors() throws Exception {
        Constructor<?>[] c = ClassTest.TestClass.class.getConstructors();
        TestCase.assertEquals("Incorrect number of constructors returned", 1, c.length);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaredClasses()
     */
    public void test_getDeclaredClasses() {
        TestCase.assertEquals("Incorrect class array returned", 2, ClassTest.class.getClasses().length);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaredConstructor(java.lang.Class[])
     */
    public void test_getDeclaredConstructor$Ljava_lang_Class() throws Exception {
        Constructor<ClassTest.TestClass> c = ClassTest.TestClass.class.getDeclaredConstructor(new Class[0]);
        TestCase.assertNull("Incorrect constructor returned", c.newInstance().cValue());
        c = ClassTest.TestClass.class.getDeclaredConstructor(Object.class);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaredConstructors()
     */
    public void test_getDeclaredConstructors() throws Exception {
        Constructor<?>[] c = ClassTest.TestClass.class.getDeclaredConstructors();
        TestCase.assertEquals("Incorrect number of constructors returned", 2, c.length);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaredField(java.lang.String)
     */
    public void test_getDeclaredFieldLjava_lang_String() throws Exception {
        Field f = ClassTest.TestClass.class.getDeclaredField("pubField");
        TestCase.assertEquals("Returned incorrect field", 2, f.getInt(new ClassTest.TestClass()));
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaredFields()
     */
    public void test_getDeclaredFields() throws Exception {
        Field[] f = ClassTest.TestClass.class.getDeclaredFields();
        TestCase.assertEquals("Returned incorrect number of fields", 4, f.length);
        f = ClassTest.SubTestClass.class.getDeclaredFields();
        TestCase.assertEquals("Returned incorrect number of fields", 0, f.length);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaredMethod(java.lang.String,
    java.lang.Class[])
     */
    public void test_getDeclaredMethodLjava_lang_String$Ljava_lang_Class() throws Exception {
        Method m = ClassTest.TestClass.class.getDeclaredMethod("pubMethod", new Class[0]);
        TestCase.assertEquals("Returned incorrect method", 2, ((Integer) (m.invoke(new ClassTest.TestClass()))).intValue());
        m = ClassTest.TestClass.class.getDeclaredMethod("privMethod", new Class[0]);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaredMethods()
     */
    public void test_getDeclaredMethods() throws Exception {
        Method[] m = ClassTest.TestClass.class.getDeclaredMethods();
        TestCase.assertEquals("Returned incorrect number of methods", 3, m.length);
        m = ClassTest.SubTestClass.class.getDeclaredMethods();
        TestCase.assertEquals("Returned incorrect number of methods", 0, m.length);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getDeclaringClass()
     */
    public void test_getDeclaringClass() {
        TestCase.assertEquals(ClassTest.class, ClassTest.TestClass.class.getDeclaringClass());
    }

    /**
     *
     *
     * @unknown java.lang.Class#getField(java.lang.String)
     */
    public void test_getFieldLjava_lang_String() throws Exception {
        Field f = ClassTest.TestClass.class.getField("pubField");
        TestCase.assertEquals("Returned incorrect field", 2, f.getInt(new ClassTest.TestClass()));
        try {
            f = ClassTest.TestClass.class.getField("privField");
            TestCase.fail("Private field access failed to throw exception");
        } catch (NoSuchFieldException e) {
            // Correct
        }
    }

    /**
     *
     *
     * @unknown java.lang.Class#getFields()
     */
    public void test_getFields() throws Exception {
        Field[] f = ClassTest.TestClass.class.getFields();
        TestCase.assertEquals("Incorrect number of fields", 2, f.length);
        f = ClassTest.SubTestClass.class.getFields();
        // Check inheritance of pub fields
        TestCase.assertEquals("Incorrect number of fields", 2, f.length);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getInterfaces()
     */
    public void test_getInterfaces() {
        List<?> interfaceList;
        interfaceList = Arrays.asList(Vector.class.getInterfaces());
        TestCase.assertTrue("Incorrect interface list for Vector", (((interfaceList.contains(Cloneable.class)) && (interfaceList.contains(Serializable.class))) && (interfaceList.contains(List.class))));
    }

    /**
     *
     *
     * @unknown java.lang.Class#getMethod(java.lang.String, java.lang.Class[])
     */
    public void test_getMethodLjava_lang_String$Ljava_lang_Class() throws Exception {
        Method m = ClassTest.TestClass.class.getMethod("pubMethod", new Class[0]);
        TestCase.assertEquals("Returned incorrect method", 2, ((Integer) (m.invoke(new ClassTest.TestClass()))).intValue());
    }

    /**
     *
     *
     * @unknown java.lang.Class#getMethods()
     */
    public void test_getMethods() throws Exception {
        Method[] m = ClassTest.TestClass.class.getMethods();
        TestCase.assertEquals("Returned incorrect number of methods", (2 + (Object.class.getMethods().length)), m.length);
        m = ClassTest.SubTestClass.class.getMethods();
        TestCase.assertEquals("Returned incorrect number of sub-class methods", (2 + (Object.class.getMethods().length)), m.length);
        // Number of inherited methods
    }

    /**
     *
     *
     * @unknown java.lang.Class#getResource(java.lang.String)
     */
    public void test_getResourceLjava_lang_String() {
        final String name = "/org/apache/harmony/tests/test_resource.txt";
        URL res = getClass().getResource(name);
        TestCase.assertNotNull(res);
        final String relativeName = "test_resource.txt";
        res = getClass().getResource(relativeName);
        TestCase.assertNotNull(res);
    }

    /**
     *
     *
     * @unknown java.lang.Class#getResourceAsStream(java.lang.String)
     */
    public void test_getResourceAsStreamLjava_lang_String() throws Exception {
        final String name = "/org/apache/harmony/tests/test_resource.txt";
        TestCase.assertNotNull((("the file " + name) + " can not be found in this directory"), getClass().getResourceAsStream(name));
        final String relativeName = "test_resource.txt";
        TestCase.assertNotNull((("the resource " + relativeName) + " can not be found in this directory"), getClass().getResourceAsStream(relativeName));
        final String nameBadURI = "org/apache/harmony/luni/tests/test_resource.txt2";
        TestCase.assertNull((("the file " + nameBadURI) + " should not be found in this directory"), getClass().getResourceAsStream(nameBadURI));
    }

    /**
     *
     *
     * @unknown java.lang.Class#getSuperclass()
     */
    public void test_getSuperclass() {
        TestCase.assertNull("Object has a superclass???", Object.class.getSuperclass());
        TestCase.assertSame("Normal class has bogus superclass", Writer.class, PrintWriter.class.getSuperclass());
        TestCase.assertSame("Array class has bogus superclass", Object.class, PrintWriter[].class.getSuperclass());
        TestCase.assertNull("Base class has a superclass", int.class.getSuperclass());
        TestCase.assertNull("Interface class has a superclass", Cloneable.class.getSuperclass());
    }

    /**
     *
     *
     * @unknown java.lang.Class#isArray()
     */
    public void test_isArray() throws ClassNotFoundException {
        TestCase.assertTrue("Non-array type claims to be.", (!(int.class.isArray())));
        Class<?> clazz = null;
        clazz = Class.forName("[I");
        TestCase.assertTrue("int Array type claims not to be.", clazz.isArray());
        clazz = Class.forName("[Ljava.lang.Object;");
        TestCase.assertTrue("Object Array type claims not to be.", clazz.isArray());
        clazz = Class.forName("java.lang.Object");
        TestCase.assertTrue("Non-array Object type claims to be.", (!(clazz.isArray())));
    }

    /**
     *
     *
     * @unknown java.lang.Class#isAssignableFrom(java.lang.Class)
     */
    public void test_isAssignableFromLjava_lang_Class() {
        Class<?> clazz1 = null;
        Class<?> clazz2 = null;
        clazz1 = Object.class;
        clazz2 = Class.class;
        TestCase.assertTrue("returned false for superclass", clazz1.isAssignableFrom(clazz2));
        clazz1 = ClassTest.TestClass.class;
        TestCase.assertTrue("returned false for same class", clazz1.isAssignableFrom(clazz1));
        clazz1 = Runnable.class;
        clazz2 = Thread.class;
        TestCase.assertTrue("returned false for implemented interface", clazz1.isAssignableFrom(clazz2));
    }

    /**
     *
     *
     * @unknown java.lang.Class#isInterface()
     */
    public void test_isInterface() throws ClassNotFoundException {
        TestCase.assertTrue("Prim type claims to be interface.", (!(int.class.isInterface())));
        Class<?> clazz = null;
        clazz = Class.forName("[I");
        TestCase.assertTrue("Prim Array type claims to be interface.", (!(clazz.isInterface())));
        clazz = Class.forName("java.lang.Runnable");
        TestCase.assertTrue("Interface type claims not to be interface.", clazz.isInterface());
        clazz = Class.forName("java.lang.Object");
        TestCase.assertTrue("Object type claims to be interface.", (!(clazz.isInterface())));
        clazz = Class.forName("[Ljava.lang.Object;");
        TestCase.assertTrue("Array type claims to be interface.", (!(clazz.isInterface())));
    }

    /**
     *
     *
     * @unknown java.lang.Class#isPrimitive()
     */
    public void test_isPrimitive() {
        TestCase.assertFalse("Interface type claims to be primitive.", Runnable.class.isPrimitive());
        TestCase.assertFalse("Object type claims to be primitive.", Object.class.isPrimitive());
        TestCase.assertFalse("Prim Array type claims to be primitive.", int[].class.isPrimitive());
        TestCase.assertFalse("Array type claims to be primitive.", Object[].class.isPrimitive());
        TestCase.assertTrue("Prim type claims not to be primitive.", int.class.isPrimitive());
        TestCase.assertFalse("Object type claims to be primitive.", Object.class.isPrimitive());
    }

    /**
     *
     *
     * @unknown java.lang.Class#newInstance()
     */
    public void test_newInstance() throws Exception {
        Class<?> clazz = null;
        clazz = Object.class;
        TestCase.assertNotNull("new object instance was null", clazz.newInstance());
        clazz = Throwable.class;
        TestCase.assertSame("new Throwable instance was not a throwable", clazz, clazz.newInstance().getClass());
    }

    /**
     *
     *
     * @unknown java.lang.Class#toString()
     */
    public void test_toString() throws ClassNotFoundException {
        TestCase.assertEquals("Class toString printed wrong value", "int", int.class.toString());
        Class<?> clazz = null;
        clazz = Class.forName("[I");
        TestCase.assertEquals("Class toString printed wrong value", "class [I", clazz.toString());
        clazz = Class.forName("java.lang.Object");
        TestCase.assertEquals("Class toString printed wrong value", "class java.lang.Object", clazz.toString());
        clazz = Class.forName("[Ljava.lang.Object;");
        TestCase.assertEquals("Class toString printed wrong value", "class [Ljava.lang.Object;", clazz.toString());
    }
}

