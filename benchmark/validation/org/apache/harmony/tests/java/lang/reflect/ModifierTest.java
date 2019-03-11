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


import java.lang.reflect.Modifier;
import junit.framework.TestCase;


public class ModifierTest extends TestCase {
    private static final int ALL_FLAGS = 2047;

    /**
     * java.lang.reflect.Modifier#Modifier()
     */
    public void test_Constructor() {
        // Test for method java.lang.reflect.Modifier()
        new Modifier();
    }

    /**
     * java.lang.reflect.Modifier#isAbstract(int)
     */
    public void test_isAbstractI() {
        // Test for method boolean java.lang.reflect.Modifier.isAbstract(int)
        TestCase.assertTrue("ABSTRACT returned false", Modifier.isAbstract(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("ABSTRACT returned false", Modifier.isAbstract(Modifier.ABSTRACT));
        TestCase.assertTrue("Non-ABSTRACT returned true", (!(Modifier.isAbstract(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isFinal(int)
     */
    public void test_isFinalI() {
        // Test for method boolean java.lang.reflect.Modifier.isFinal(int)
        TestCase.assertTrue("FINAL returned false", Modifier.isFinal(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("FINAL returned false", Modifier.isFinal(Modifier.FINAL));
        TestCase.assertTrue("Non-FINAL returned true", (!(Modifier.isFinal(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isInterface(int)
     */
    public void test_isInterfaceI() {
        // Test for method boolean java.lang.reflect.Modifier.isInterface(int)
        TestCase.assertTrue("INTERFACE returned false", Modifier.isInterface(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("INTERFACE returned false", Modifier.isInterface(Modifier.INTERFACE));
        TestCase.assertTrue("Non-INTERFACE returned true", (!(Modifier.isInterface(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isNative(int)
     */
    public void test_isNativeI() {
        // Test for method boolean java.lang.reflect.Modifier.isNative(int)
        TestCase.assertTrue("NATIVE returned false", Modifier.isNative(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("NATIVE returned false", Modifier.isNative(Modifier.NATIVE));
        TestCase.assertTrue("Non-NATIVE returned true", (!(Modifier.isNative(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isPrivate(int)
     */
    public void test_isPrivateI() {
        // Test for method boolean java.lang.reflect.Modifier.isPrivate(int)
        TestCase.assertTrue("PRIVATE returned false", Modifier.isPrivate(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("PRIVATE returned false", Modifier.isPrivate(Modifier.PRIVATE));
        TestCase.assertTrue("Non-PRIVATE returned true", (!(Modifier.isPrivate(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isProtected(int)
     */
    public void test_isProtectedI() {
        // Test for method boolean java.lang.reflect.Modifier.isProtected(int)
        TestCase.assertTrue("PROTECTED returned false", Modifier.isProtected(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("PROTECTED returned false", Modifier.isProtected(Modifier.PROTECTED));
        TestCase.assertTrue("Non-PROTECTED returned true", (!(Modifier.isProtected(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isPublic(int)
     */
    public void test_isPublicI() {
        // Test for method boolean java.lang.reflect.Modifier.isPublic(int)
        TestCase.assertTrue("PUBLIC returned false", Modifier.isPublic(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("PUBLIC returned false", Modifier.isPublic(Modifier.PUBLIC));
        TestCase.assertTrue("Non-PUBLIC returned true", (!(Modifier.isPublic(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isStatic(int)
     */
    public void test_isStaticI() {
        // Test for method boolean java.lang.reflect.Modifier.isStatic(int)
        TestCase.assertTrue("STATIC returned false", Modifier.isStatic(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("STATIC returned false", Modifier.isStatic(Modifier.STATIC));
        TestCase.assertTrue("Non-STATIC returned true", (!(Modifier.isStatic(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isStrict(int)
     */
    public void test_isStrictI() {
        // Test for method boolean java.lang.reflect.Modifier.isStrict(int)
        TestCase.assertTrue("STRICT returned false", Modifier.isStrict(Modifier.STRICT));
        TestCase.assertTrue("Non-STRICT returned true", (!(Modifier.isStrict(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#isSynchronized(int)
     */
    public void test_isSynchronizedI() {
        // Test for method boolean
        // java.lang.reflect.Modifier.isSynchronized(int)
        TestCase.assertTrue("Synchronized returned false", Modifier.isSynchronized(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("Non-Synchronized returned true", (!(Modifier.isSynchronized(Modifier.VOLATILE))));
    }

    /**
     * java.lang.reflect.Modifier#isTransient(int)
     */
    public void test_isTransientI() {
        // Test for method boolean java.lang.reflect.Modifier.isTransient(int)
        TestCase.assertTrue("Transient returned false", Modifier.isTransient(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("Transient returned false", Modifier.isTransient(Modifier.TRANSIENT));
        TestCase.assertTrue("Non-Transient returned true", (!(Modifier.isTransient(Modifier.VOLATILE))));
    }

    /**
     * java.lang.reflect.Modifier#isVolatile(int)
     */
    public void test_isVolatileI() {
        // Test for method boolean java.lang.reflect.Modifier.isVolatile(int)
        TestCase.assertTrue("Volatile returned false", Modifier.isVolatile(ModifierTest.ALL_FLAGS));
        TestCase.assertTrue("Volatile returned false", Modifier.isVolatile(Modifier.VOLATILE));
        TestCase.assertTrue("Non-Volatile returned true", (!(Modifier.isVolatile(Modifier.TRANSIENT))));
    }

    /**
     * java.lang.reflect.Modifier#toString(int)
     */
    public void test_toStringI() {
        // Test for method java.lang.String
        // java.lang.reflect.Modifier.toString(int)
        TestCase.assertTrue(("Returned incorrect string value: " + (Modifier.toString(((Modifier.PUBLIC) + (Modifier.ABSTRACT))))), Modifier.toString(((Modifier.PUBLIC) + (Modifier.ABSTRACT))).equals("public abstract"));
        int i = 4095;
        String modification = "public protected private abstract static final transient " + "volatile synchronized native strictfp interface";
        TestCase.assertTrue("Returned incorrect string value", Modifier.toString(i).equals(modification));
    }

    public void test_Constants_Value() {
        TestCase.assertEquals(1024, Modifier.ABSTRACT);
        TestCase.assertEquals(16, Modifier.FINAL);
        TestCase.assertEquals(512, Modifier.INTERFACE);
        TestCase.assertEquals(256, Modifier.NATIVE);
        TestCase.assertEquals(2, Modifier.PRIVATE);
        TestCase.assertEquals(4, Modifier.PROTECTED);
        TestCase.assertEquals(1, Modifier.PUBLIC);
        TestCase.assertEquals(8, Modifier.STATIC);
        TestCase.assertEquals(2048, Modifier.STRICT);
        TestCase.assertEquals(32, Modifier.SYNCHRONIZED);
        TestCase.assertEquals(128, Modifier.TRANSIENT);
        TestCase.assertEquals(64, Modifier.VOLATILE);
    }

    abstract class AbstractClazz {}

    final class FinalClazz {}

    static class StaticClazz {}

    interface InterfaceClazz {}

    public class PublicClazz {}

    protected class ProtectedClazz {}

    private class PrivateClazz {}

    public abstract class PublicAbstractClazz {}

    protected abstract class ProtectedAbstractClazz {}

    private abstract class PrivateAbstractClazz {}

    public final class PublicFinalClazz {}

    protected final class ProtectedFinalClazz {}

    private final class PrivateFinalClazz {}

    public static class PublicStaticClazz {}

    protected static class ProtectedStaticClazz {}

    private static class PrivateStaticClazz {}

    public interface PublicInterface {}

    protected interface ProtectedInterface {}

    private interface PrivateInterface {}

    abstract static class StaticAbstractClazz {}

    public abstract static class PublicStaticAbstractClazz {}

    protected abstract static class ProtectedStaticAbstractClazz {}

    private abstract static class PrivateStaticAbstractClazz {}

    static final class StaticFinalClazz {}

    public static final class PublicStaticFinalClazz {}

    protected static final class ProtectedStaticFinalClazz {}

    private static final class PrivateStaticFinalClazz {}

    static interface StaticInterface {}

    public static interface PublicStaticInterface {}

    protected static interface ProtectedStaticInterface {}

    private static interface PrivateStaticInterface {}

    abstract static interface StaticAbstractInterface {}

    public abstract static interface PublicStaticAbstractInterface {}

    protected abstract static interface ProtectedStaticAbstractInterface {}

    private abstract static interface PrivateStaticAbstractInterface {}

    public void test_Class_Modifier() {
        TestCase.assertEquals(Modifier.ABSTRACT, ModifierTest.AbstractClazz.class.getModifiers());
        TestCase.assertEquals(Modifier.FINAL, ModifierTest.FinalClazz.class.getModifiers());
        TestCase.assertEquals(Modifier.STATIC, ModifierTest.StaticClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.INTERFACE) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.InterfaceClazz.class.getModifiers());
        TestCase.assertEquals(Modifier.PUBLIC, ModifierTest.PublicClazz.class.getModifiers());
        TestCase.assertEquals(Modifier.PROTECTED, ModifierTest.ProtectedClazz.class.getModifiers());
        TestCase.assertEquals(Modifier.PRIVATE, ModifierTest.PrivateClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PUBLIC) + (Modifier.ABSTRACT)), ModifierTest.PublicAbstractClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PROTECTED) + (Modifier.ABSTRACT)), ModifierTest.ProtectedAbstractClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PRIVATE) + (Modifier.ABSTRACT)), ModifierTest.PrivateAbstractClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PUBLIC) + (Modifier.FINAL)), ModifierTest.PublicFinalClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PROTECTED) + (Modifier.FINAL)), ModifierTest.ProtectedFinalClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PRIVATE) + (Modifier.FINAL)), ModifierTest.PrivateFinalClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PUBLIC) + (Modifier.STATIC)), ModifierTest.PublicStaticClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PROTECTED) + (Modifier.STATIC)), ModifierTest.ProtectedStaticClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.PRIVATE) + (Modifier.STATIC)), ModifierTest.PrivateStaticClazz.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PUBLIC) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PublicInterface.class.getModifiers());
        TestCase.assertEquals(((Modifier.STATIC) + (Modifier.FINAL)), ModifierTest.StaticFinalClazz.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PRIVATE) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PrivateInterface.class.getModifiers());
        TestCase.assertEquals(((Modifier.STATIC) + (Modifier.ABSTRACT)), ModifierTest.StaticAbstractClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.PUBLIC) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PublicStaticAbstractClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.PROTECTED) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.ProtectedStaticAbstractClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.PRIVATE) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PrivateStaticAbstractClazz.class.getModifiers());
        TestCase.assertEquals(((Modifier.STATIC) + (Modifier.FINAL)), ModifierTest.StaticFinalClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.PUBLIC) + (Modifier.STATIC)) + (Modifier.FINAL)), ModifierTest.PublicStaticFinalClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.PROTECTED) + (Modifier.STATIC)) + (Modifier.FINAL)), ModifierTest.ProtectedStaticFinalClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.PRIVATE) + (Modifier.STATIC)) + (Modifier.FINAL)), ModifierTest.PrivateStaticFinalClazz.class.getModifiers());
        TestCase.assertEquals((((Modifier.INTERFACE) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.StaticInterface.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PUBLIC) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PublicStaticInterface.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PROTECTED) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.ProtectedStaticInterface.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PRIVATE) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PrivateStaticInterface.class.getModifiers());
        TestCase.assertEquals((((Modifier.INTERFACE) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.StaticAbstractInterface.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PUBLIC) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PublicStaticAbstractInterface.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PROTECTED) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.ProtectedStaticAbstractInterface.class.getModifiers());
        TestCase.assertEquals(((((Modifier.PRIVATE) + (Modifier.INTERFACE)) + (Modifier.STATIC)) + (Modifier.ABSTRACT)), ModifierTest.PrivateStaticAbstractInterface.class.getModifiers());
    }

    abstract static class MethodClass {
        public abstract void publicAbstractMethod();

        public static void publicStaticMethod() {
        }

        public final void publicFinalMethod() {
        }

        public static final void publicStaticFinalMethod() {
        }
    }

    public void test_Method_Modifier() throws Exception {
        TestCase.assertEquals(((Modifier.PUBLIC) + (Modifier.ABSTRACT)), ModifierTest.MethodClass.class.getMethod("publicAbstractMethod", new Class[0]).getModifiers());
        TestCase.assertEquals(((Modifier.PUBLIC) + (Modifier.STATIC)), ModifierTest.MethodClass.class.getMethod("publicStaticMethod", new Class[0]).getModifiers());
        TestCase.assertEquals(((Modifier.PUBLIC) + (Modifier.FINAL)), ModifierTest.MethodClass.class.getMethod("publicFinalMethod", new Class[0]).getModifiers());
        TestCase.assertEquals((((Modifier.PUBLIC) + (Modifier.STATIC)) + (Modifier.FINAL)), ModifierTest.MethodClass.class.getMethod("publicStaticFinalMethod", new Class[0]).getModifiers());
    }
}

