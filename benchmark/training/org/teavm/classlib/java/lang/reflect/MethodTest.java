/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang.reflect;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.classlib.support.Reflectable;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class MethodTest {
    @Test
    public void methodsEnumerated() {
        callMethods();
        String text = collectMethods(MethodTest.Foo.class.getDeclaredMethods());
        Assert.assertEquals(("" + ("java.lang.Object Foo.baz();" + "public void Foo.bar(java.lang.Object);")), text);
    }

    @Test
    public void publicMethodsEnumerated() {
        callMethods();
        String text = collectMethods(MethodTest.Foo.class.getMethods());
        Assert.assertEquals("public void Foo.bar(java.lang.Object);", text);
    }

    @Test
    public void inheritedPublicMethodEnumerated() {
        callMethods();
        String text = collectMethods(MethodTest.SubClass.class.getMethods());
        Assert.assertEquals("public void SubClass.g();public void SuperClass.f();", text);
    }

    @Test
    public void bridgeMethodNotFound() throws NoSuchMethodException {
        callMethods();
        Method method = MethodTest.SubClassWithBridge.class.getMethod("f");
        Assert.assertEquals(String.class, method.getReturnType());
    }

    @Test
    public void methodInvoked() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodTest.Foo foo = new MethodTest.Foo();
        Method method = foo.getClass().getMethod("bar", Object.class);
        method.invoke(foo, "23");
        Assert.assertEquals("23", foo.baz());
    }

    @Test
    public void methodInvoked2() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodTest.Foo foo = new MethodTest.Foo();
        foo.bar("42");
        Method method = foo.getClass().getDeclaredMethod("baz");
        Assert.assertEquals("42", method.invoke(foo));
    }

    @Test
    public void staticInitializerCalled() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = MethodTest.WithInitializer.class.getMethod("f");
        method.invoke(null);
        Assert.assertEquals("init;f();", MethodTest.WithInitializer.log);
    }

    static class Foo {
        Object value;

        @Reflectable
        public void bar(Object value) {
            this.value = value;
        }

        @Reflectable
        Object baz() {
            return value;
        }
    }

    static class SuperClass {
        @Reflectable
        public void f() {
        }
    }

    static class SubClass extends MethodTest.SuperClass {
        @Reflectable
        public void g() {
        }
    }

    static class SuperClassWithBridge {
        @Reflectable
        public Object f() {
            return null;
        }
    }

    static class SubClassWithBridge {
        @Reflectable
        public String f() {
            return null;
        }
    }

    static class WithInitializer {
        static String log = "";

        static {
            MethodTest.WithInitializer.log += "init;";
        }

        @Reflectable
        public static void f() {
            MethodTest.WithInitializer.log += "f();";
        }
    }
}

