/**
 * Copyright (C) 2009 The Android Open Source Project
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
package libcore.java.lang.reflect;


import java.lang.reflect.Method;
import junit.framework.TestCase;


public final class MethodTest extends TestCase {
    // Check that the VM gives useful detail messages.
    public void test_invokeExceptions() throws Exception {
        Method m = String.class.getMethod("charAt", int.class);
        try {
            m.invoke("hello");// Wrong number of arguments.

            TestCase.fail();
        } catch (IllegalArgumentException iae) {
            TestCase.assertEquals("wrong number of arguments; expected 1, got 0", iae.getMessage());
        }
        try {
            m.invoke("hello", "world");// Wrong type.

            TestCase.fail();
        } catch (IllegalArgumentException iae) {
            TestCase.assertEquals("argument 1 should have type int, got java.lang.String", iae.getMessage());
        }
        try {
            m.invoke("hello", ((Object) (null)));// Null for a primitive argument.

            TestCase.fail();
        } catch (IllegalArgumentException iae) {
            TestCase.assertEquals("argument 1 should have type int, got null", iae.getMessage());
        }
        try {
            m.invoke(new Integer(5));// Wrong type for 'this'.

            TestCase.fail();
        } catch (IllegalArgumentException iae) {
            TestCase.assertEquals("expected receiver of type java.lang.String, but got java.lang.Integer", iae.getMessage());
        }
        try {
            m.invoke(null);// Null for 'this'.

            TestCase.fail();
        } catch (NullPointerException npe) {
            TestCase.assertEquals("expected receiver of type java.lang.String, but got null", npe.getMessage());
        }
    }

    public void test_getExceptionTypes() throws Exception {
        Method method = MethodTest.MethodTestHelper.class.getMethod("m1", new Class[0]);
        Class[] exceptions = method.getExceptionTypes();
        TestCase.assertEquals(1, exceptions.length);
        TestCase.assertEquals(IndexOutOfBoundsException.class, exceptions[0]);
        // Check that corrupting our array doesn't affect other callers.
        exceptions[0] = NullPointerException.class;
        exceptions = method.getExceptionTypes();
        TestCase.assertEquals(1, exceptions.length);
        TestCase.assertEquals(IndexOutOfBoundsException.class, exceptions[0]);
    }

    public void test_getParameterTypes() throws Exception {
        Class[] expectedParameters = new Class[]{ Object.class };
        Method method = MethodTest.MethodTestHelper.class.getMethod("m2", expectedParameters);
        Class[] parameters = method.getParameterTypes();
        TestCase.assertEquals(1, parameters.length);
        TestCase.assertEquals(expectedParameters[0], parameters[0]);
        // Check that corrupting our array doesn't affect other callers.
        parameters[0] = String.class;
        parameters = method.getParameterTypes();
        TestCase.assertEquals(1, parameters.length);
        TestCase.assertEquals(expectedParameters[0], parameters[0]);
    }

    public void testGetMethodWithPrivateMethodAndInterfaceMethod() throws Exception {
        TestCase.assertEquals(MethodTest.InterfaceA.class, MethodTest.Sub.class.getMethod("a").getDeclaringClass());
    }

    public void testGetMethodReturnsIndirectlyImplementedInterface() throws Exception {
        TestCase.assertEquals(MethodTest.InterfaceA.class, MethodTest.ImplementsC.class.getMethod("a").getDeclaringClass());
        TestCase.assertEquals(MethodTest.InterfaceA.class, MethodTest.ExtendsImplementsC.class.getMethod("a").getDeclaringClass());
    }

    public void testGetDeclaredMethodReturnsIndirectlyImplementedInterface() throws Exception {
        try {
            MethodTest.ImplementsC.class.getDeclaredMethod("a").getDeclaringClass();
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
        try {
            MethodTest.ExtendsImplementsC.class.getDeclaredMethod("a").getDeclaringClass();
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
    }

    public void testGetMethodWithConstructorName() throws Exception {
        try {
            MethodTest.MethodTestHelper.class.getMethod("<init>");
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
    }

    public void testGetMethodWithNullName() throws Exception {
        try {
            MethodTest.MethodTestHelper.class.getMethod(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetMethodWithNullArgumentsArray() throws Exception {
        Method m1 = MethodTest.MethodTestHelper.class.getMethod("m1", ((Class[]) (null)));
        TestCase.assertEquals(0, m1.getParameterTypes().length);
    }

    public void testGetMethodWithNullArgument() throws Exception {
        try {
            MethodTest.MethodTestHelper.class.getMethod("m2", new Class[]{ null });
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
    }

    public void testGetMethodReturnsInheritedStaticMethod() throws Exception {
        Method b = MethodTest.Sub.class.getMethod("b");
        TestCase.assertEquals(void.class, b.getReturnType());
    }

    public void testGetDeclaredMethodReturnsPrivateMethods() throws Exception {
        Method method = MethodTest.Super.class.getDeclaredMethod("a");
        TestCase.assertEquals(void.class, method.getReturnType());
    }

    public void testGetDeclaredMethodDoesNotReturnSuperclassMethods() throws Exception {
        try {
            MethodTest.Sub.class.getDeclaredMethod("a");
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
    }

    public void testGetDeclaredMethodDoesNotReturnImplementedInterfaceMethods() throws Exception {
        try {
            MethodTest.InterfaceB.class.getDeclaredMethod("a");
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
    }

    public void testImplementedInterfaceMethodOfAnonymousClass() throws Exception {
        Object anonymous = new MethodTest.InterfaceA() {
            @Override
            public void a() {
            }
        };
        Method method = anonymous.getClass().getMethod("a");
        TestCase.assertEquals(anonymous.getClass(), method.getDeclaringClass());
    }

    public void testPublicMethodOfAnonymousClass() throws Exception {
        Object anonymous = new Object() {
            public void a() {
            }
        };
        Method method = anonymous.getClass().getMethod("a");
        TestCase.assertEquals(anonymous.getClass(), method.getDeclaringClass());
    }

    public void testGetMethodDoesNotReturnPrivateMethodOfAnonymousClass() throws Exception {
        Object anonymous = new Object() {
            private void a() {
            }
        };
        try {
            anonymous.getClass().getMethod("a");
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
    }

    public void testGetDeclaredMethodReturnsPrivateMethodOfAnonymousClass() throws Exception {
        Object anonymous = new Object() {
            private void a() {
            }
        };
        Method method = anonymous.getClass().getDeclaredMethod("a");
        TestCase.assertEquals(anonymous.getClass(), method.getDeclaringClass());
    }

    // http://b/1045939
    public void testMethodToString() throws Exception {
        TestCase.assertEquals("public final native void java.lang.Object.notify()", Object.class.getMethod("notify", new Class[]{  }).toString());
        TestCase.assertEquals("public java.lang.String java.lang.Object.toString()", Object.class.getMethod("toString", new Class[]{  }).toString());
        TestCase.assertEquals(("public final native void java.lang.Object.wait(long,int)" + " throws java.lang.InterruptedException"), Object.class.getMethod("wait", new Class[]{ long.class, int.class }).toString());
        TestCase.assertEquals("public boolean java.lang.Object.equals(java.lang.Object)", Object.class.getMethod("equals", new Class[]{ Object.class }).toString());
        TestCase.assertEquals("public static java.lang.String java.lang.String.valueOf(char[])", String.class.getMethod("valueOf", new Class[]{ char[].class }).toString());
        TestCase.assertEquals(("public java.lang.Process java.lang.Runtime.exec(java.lang.String[])" + " throws java.io.IOException"), Runtime.class.getMethod("exec", new Class[]{ String[].class }).toString());
    }

    public static class MethodTestHelper {
        public void m1() throws IndexOutOfBoundsException {
        }

        public void m2(Object o) {
        }
    }

    public static class Super {
        private void a() {
        }

        public static void b() {
        }
    }

    public static interface InterfaceA {
        void a();
    }

    public abstract static class Sub extends MethodTest.Super implements MethodTest.InterfaceA {}

    public static interface InterfaceB extends MethodTest.InterfaceA {}

    public static interface InterfaceC extends MethodTest.InterfaceB {}

    public abstract static class ImplementsC implements MethodTest.InterfaceC {}

    public abstract static class ExtendsImplementsC extends MethodTest.ImplementsC {}
}

