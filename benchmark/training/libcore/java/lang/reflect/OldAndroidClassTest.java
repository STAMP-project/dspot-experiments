/**
 * Copyright (C) 2008 The Android Open Source Project
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


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public final class OldAndroidClassTest extends TestCase {
    private static final String packageName = "libcore.java.lang.reflect";

    public void testNewInstance() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        Object instance = helloClass.newInstance();
        TestCase.assertNotNull(instance);
    }

    public void testForName() throws Exception {
        try {
            Class.forName("this.class.DoesNotExist");
            TestCase.fail();
        } catch (ClassNotFoundException expected) {
        }
    }

    public void testNewInstancePrivateConstructor() throws Exception {
        try {
            Class.forName(((OldAndroidClassTest.packageName) + ".ClassWithPrivateConstructor")).newInstance();
            TestCase.fail();
        } catch (IllegalAccessException expected) {
        }
    }

    public void testGetDeclaredMethod() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        Method method = helloClass.getDeclaredMethod("method", ((Class[]) (null)));
        method.invoke(new OldAndroidClassTest(), ((Object[]) (null)));
    }

    public void testGetDeclaredMethodWithArgs() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        Method method = helloClass.getDeclaredMethod("methodWithArgs", Object.class);
        Object[] invokeArgs = new Object[1];
        invokeArgs[0] = "Hello";
        Object ret = method.invoke(new OldAndroidClassTest(), invokeArgs);
        TestCase.assertEquals(ret, invokeArgs[0]);
    }

    public void testGetDeclaredMethodPrivate() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        Method method = helloClass.getDeclaredMethod("privateMethod", ((Class[]) (null)));
        method.invoke(new OldAndroidClassTest(), ((Object[]) (null)));
    }

    public void testGetSuperclass() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        Class objectClass = Class.forName("java.lang.Object");
        TestCase.assertEquals(helloClass.getSuperclass().getSuperclass().getSuperclass(), objectClass);
    }

    public void testIsAssignableFrom() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        Class objectClass = Class.forName("java.lang.Object");
        TestCase.assertTrue(objectClass.isAssignableFrom(helloClass));
        TestCase.assertFalse(helloClass.isAssignableFrom(objectClass));
    }

    public void testGetConstructor() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        Constructor constructor = helloClass.getConstructor(((Class[]) (null)));
        TestCase.assertNotNull(constructor);
    }

    public void testGetModifiers() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        TestCase.assertTrue(Modifier.isPublic(helloClass.getModifiers()));
    }

    public void testGetMethod() throws Exception {
        Class helloClass = Class.forName(OldAndroidClassTest.class.getName());
        helloClass.getMethod("method", ((Class[]) (null)));
        try {
            Class[] argTypes = new Class[1];
            argTypes[0] = helloClass;
            helloClass.getMethod("method", argTypes);
            TestCase.fail();
        } catch (NoSuchMethodException expected) {
        }
    }

    // http://code.google.com/p/android/issues/detail?id=14
    public void testFieldSet() throws Exception {
        OldAndroidClassTest.SimpleClass obj = new OldAndroidClassTest.SimpleClass();
        Field field = obj.getClass().getDeclaredField("str");
        field.set(obj, null);
    }

    public class SimpleClass {
        public String str;
    }

    boolean methodInvoked;

    boolean privateMethodInvoked;

    // Regression for 1018067: Class.getMethods() returns the same method over
    // and over again from all base classes
    public void testClassGetMethodsNoDupes() {
        Method[] methods = ArrayList.class.getMethods();
        Set<String> set = new HashSet<String>();
        for (Method method : methods) {
            String signature = method.toString();
            int par = signature.indexOf('(');
            int dot = signature.lastIndexOf('.', par);
            signature = signature.substring((dot + 1));
            TestCase.assertFalse(("Duplicate " + signature), set.contains(signature));
            set.add(signature);
        }
    }

    interface MyInterface {
        void foo();
    }

    interface MyOtherInterface extends OldAndroidClassTest.MyInterface {
        void bar();
    }

    abstract class MyClass implements OldAndroidClassTest.MyOtherInterface {
        public void gabba() {
        }

        public void hey() {
        }
    }

    // Check if we also reflect methods from interfaces
    public void testGetMethodsInterfaces() {
        Method[] methods = OldAndroidClassTest.MyInterface.class.getMethods();
        TestCase.assertTrue(hasMethod(methods, ".foo("));
        methods = OldAndroidClassTest.MyOtherInterface.class.getMethods();
        TestCase.assertTrue(hasMethod(methods, ".foo("));
        TestCase.assertTrue(hasMethod(methods, ".bar("));
        methods = OldAndroidClassTest.MyClass.class.getMethods();
        TestCase.assertTrue(hasMethod(methods, ".foo("));
        TestCase.assertTrue(hasMethod(methods, ".bar("));
        TestCase.assertTrue(hasMethod(methods, ".gabba("));
        TestCase.assertTrue(hasMethod(methods, ".hey("));
        TestCase.assertTrue(hasMethod(methods, ".toString("));
    }

    // Test for Class.getPackage();
    public void testClassGetPackage() {
        TestCase.assertNotNull(getClass().getPackage());
        TestCase.assertEquals(OldAndroidClassTest.packageName, getClass().getPackage().getName());
        TestCase.assertEquals("Unknown", getClass().getPackage().getSpecificationTitle());
        Package p = Object.class.getPackage();
        TestCase.assertNotNull(p);
        TestCase.assertEquals("java.lang", p.getName());
        TestCase.assertSame(p, Object.class.getPackage());
    }

    // Regression test for #1123708: Problem with getCanonicalName(),
    // getSimpleName(), and getPackage().
    // 
    // A couple of interesting cases need to be checked: Top-level classes,
    // member classes, local classes, and anonymous classes. Also, boundary
    // cases with '$' in the class names are checked, since the '$' is used
    // as the separator between outer and inner class, so this might lead
    // to problems (it did in the previous implementation).
    // 
    // Caution: Adding local or anonymous classes elsewhere in this
    // file might affect the test.
    private class MemberClass {}

    private class Mi$o$oup {}

    public void testVariousClassNames() {
        Class<?> clazz = this.getClass();
        String pkg = ((clazz.getPackage()) == null) ? "" : (clazz.getPackage().getName()) + ".";
        // Simple, top-level class
        TestCase.assertEquals((pkg + "OldAndroidClassTest"), clazz.getName());
        TestCase.assertEquals("OldAndroidClassTest", clazz.getSimpleName());
        TestCase.assertEquals((pkg + "OldAndroidClassTest"), clazz.getCanonicalName());
        clazz = OldAndroidClassTest.MemberClass.class;
        TestCase.assertEquals((pkg + "OldAndroidClassTest$MemberClass"), clazz.getName());
        TestCase.assertEquals("MemberClass", clazz.getSimpleName());
        TestCase.assertEquals((pkg + "OldAndroidClassTest.MemberClass"), clazz.getCanonicalName());
        // This space intentionally left blank.
        class LocalClass {}
        clazz = LocalClass.class;
        TestCase.assertEquals((pkg + "OldAndroidClassTest$1LocalClass"), clazz.getName());
        TestCase.assertEquals("LocalClass", clazz.getSimpleName());
        TestCase.assertNull(clazz.getCanonicalName());
        clazz = new Object() {}.getClass();
        TestCase.assertEquals((pkg + "OldAndroidClassTest$1"), clazz.getName());
        TestCase.assertEquals("", clazz.getSimpleName());
        TestCase.assertNull(clazz.getCanonicalName());
        // Weird special cases with dollar in name.
        clazz = Mou$$aka.class;
        TestCase.assertEquals((pkg + "Mou$$aka"), clazz.getName());
        TestCase.assertEquals("Mou$$aka", clazz.getSimpleName());
        TestCase.assertEquals((pkg + "Mou$$aka"), clazz.getCanonicalName());
        clazz = OldAndroidClassTest.Mi$o$oup.class;
        TestCase.assertEquals((pkg + "OldAndroidClassTest$Mi$o$oup"), clazz.getName());
        TestCase.assertEquals("Mi$o$oup", clazz.getSimpleName());
        TestCase.assertEquals((pkg + "OldAndroidClassTest.Mi$o$oup"), clazz.getCanonicalName());
        class  {}
        clazz = .class;
        TestCase.assertEquals((pkg + "OldAndroidClassTest$1Ma$hedPotatoe$"), clazz.getName());
        TestCase.assertEquals("Ma$hedPotatoe$", clazz.getSimpleName());
        TestCase.assertNull(clazz.getCanonicalName());
    }

    public void testLocalMemberClass() {
        Class<?> clazz = this.getClass();
        TestCase.assertFalse(clazz.isMemberClass());
        TestCase.assertFalse(clazz.isLocalClass());
        clazz = OldAndroidClassTest.MemberClass.class;
        TestCase.assertTrue(clazz.isMemberClass());
        TestCase.assertFalse(clazz.isLocalClass());
        class OtherLocalClass {}
        clazz = OtherLocalClass.class;
        TestCase.assertFalse(clazz.isMemberClass());
        TestCase.assertTrue(clazz.isLocalClass());
        clazz = new Object() {}.getClass();
        TestCase.assertFalse(clazz.isMemberClass());
        TestCase.assertFalse(clazz.isLocalClass());
    }
}

