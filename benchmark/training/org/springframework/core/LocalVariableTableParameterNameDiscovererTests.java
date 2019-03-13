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
package org.springframework.core;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.objects.TestObject;


/**
 *
 *
 * @author Adrian Colyer
 */
public class LocalVariableTableParameterNameDiscovererTests {
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Test
    public void methodParameterNameDiscoveryNoArgs() throws NoSuchMethodException {
        Method getName = TestObject.class.getMethod("getName");
        String[] names = discoverer.getParameterNames(getName);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("no argument names", 0, names.length);
    }

    @Test
    public void methodParameterNameDiscoveryWithArgs() throws NoSuchMethodException {
        Method setName = TestObject.class.getMethod("setName", String.class);
        String[] names = discoverer.getParameterNames(setName);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("one argument", 1, names.length);
        Assert.assertEquals("name", names[0]);
    }

    @Test
    public void consParameterNameDiscoveryNoArgs() throws NoSuchMethodException {
        Constructor<TestObject> noArgsCons = TestObject.class.getConstructor();
        String[] names = discoverer.getParameterNames(noArgsCons);
        Assert.assertNotNull("should find cons info", names);
        Assert.assertEquals("no argument names", 0, names.length);
    }

    @Test
    public void consParameterNameDiscoveryArgs() throws NoSuchMethodException {
        Constructor<TestObject> twoArgCons = TestObject.class.getConstructor(String.class, int.class);
        String[] names = discoverer.getParameterNames(twoArgCons);
        Assert.assertNotNull("should find cons info", names);
        Assert.assertEquals("one argument", 2, names.length);
        Assert.assertEquals("name", names[0]);
        Assert.assertEquals("age", names[1]);
    }

    @Test
    public void staticMethodParameterNameDiscoveryNoArgs() throws NoSuchMethodException {
        Method m = getClass().getMethod("staticMethodNoLocalVars");
        String[] names = discoverer.getParameterNames(m);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("no argument names", 0, names.length);
    }

    @Test
    public void overloadedStaticMethod() throws Exception {
        Class<? extends LocalVariableTableParameterNameDiscovererTests> clazz = this.getClass();
        Method m1 = clazz.getMethod("staticMethod", Long.TYPE, Long.TYPE);
        String[] names = discoverer.getParameterNames(m1);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("two arguments", 2, names.length);
        Assert.assertEquals("x", names[0]);
        Assert.assertEquals("y", names[1]);
        Method m2 = clazz.getMethod("staticMethod", Long.TYPE, Long.TYPE, Long.TYPE);
        names = discoverer.getParameterNames(m2);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("three arguments", 3, names.length);
        Assert.assertEquals("x", names[0]);
        Assert.assertEquals("y", names[1]);
        Assert.assertEquals("z", names[2]);
    }

    @Test
    public void overloadedStaticMethodInInnerClass() throws Exception {
        Class<LocalVariableTableParameterNameDiscovererTests.InnerClass> clazz = LocalVariableTableParameterNameDiscovererTests.InnerClass.class;
        Method m1 = clazz.getMethod("staticMethod", Long.TYPE);
        String[] names = discoverer.getParameterNames(m1);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("one argument", 1, names.length);
        Assert.assertEquals("x", names[0]);
        Method m2 = clazz.getMethod("staticMethod", Long.TYPE, Long.TYPE);
        names = discoverer.getParameterNames(m2);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("two arguments", 2, names.length);
        Assert.assertEquals("x", names[0]);
        Assert.assertEquals("y", names[1]);
    }

    @Test
    public void overloadedMethod() throws Exception {
        Class<? extends LocalVariableTableParameterNameDiscovererTests> clazz = this.getClass();
        Method m1 = clazz.getMethod("instanceMethod", Double.TYPE, Double.TYPE);
        String[] names = discoverer.getParameterNames(m1);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("two arguments", 2, names.length);
        Assert.assertEquals("x", names[0]);
        Assert.assertEquals("y", names[1]);
        Method m2 = clazz.getMethod("instanceMethod", Double.TYPE, Double.TYPE, Double.TYPE);
        names = discoverer.getParameterNames(m2);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("three arguments", 3, names.length);
        Assert.assertEquals("x", names[0]);
        Assert.assertEquals("y", names[1]);
        Assert.assertEquals("z", names[2]);
    }

    @Test
    public void overloadedMethodInInnerClass() throws Exception {
        Class<LocalVariableTableParameterNameDiscovererTests.InnerClass> clazz = LocalVariableTableParameterNameDiscovererTests.InnerClass.class;
        Method m1 = clazz.getMethod("instanceMethod", String.class);
        String[] names = discoverer.getParameterNames(m1);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("one argument", 1, names.length);
        Assert.assertEquals("aa", names[0]);
        Method m2 = clazz.getMethod("instanceMethod", String.class, String.class);
        names = discoverer.getParameterNames(m2);
        Assert.assertNotNull("should find method info", names);
        Assert.assertEquals("two arguments", 2, names.length);
        Assert.assertEquals("aa", names[0]);
        Assert.assertEquals("bb", names[1]);
    }

    @Test
    public void generifiedClass() throws Exception {
        Class<?> clazz = LocalVariableTableParameterNameDiscovererTests.GenerifiedClass.class;
        Constructor<?> ctor = clazz.getDeclaredConstructor(Object.class);
        String[] names = discoverer.getParameterNames(ctor);
        Assert.assertEquals(1, names.length);
        Assert.assertEquals("key", names[0]);
        ctor = clazz.getDeclaredConstructor(Object.class, Object.class);
        names = discoverer.getParameterNames(ctor);
        Assert.assertEquals(2, names.length);
        Assert.assertEquals("key", names[0]);
        Assert.assertEquals("value", names[1]);
        Method m = clazz.getMethod("generifiedStaticMethod", Object.class);
        names = discoverer.getParameterNames(m);
        Assert.assertEquals(1, names.length);
        Assert.assertEquals("param", names[0]);
        m = clazz.getMethod("generifiedMethod", Object.class, long.class, Object.class, Object.class);
        names = discoverer.getParameterNames(m);
        Assert.assertEquals(4, names.length);
        Assert.assertEquals("param", names[0]);
        Assert.assertEquals("x", names[1]);
        Assert.assertEquals("key", names[2]);
        Assert.assertEquals("value", names[3]);
        m = clazz.getMethod("voidStaticMethod", Object.class, long.class, int.class);
        names = discoverer.getParameterNames(m);
        Assert.assertEquals(3, names.length);
        Assert.assertEquals("obj", names[0]);
        Assert.assertEquals("x", names[1]);
        Assert.assertEquals("i", names[2]);
        m = clazz.getMethod("nonVoidStaticMethod", Object.class, long.class, int.class);
        names = discoverer.getParameterNames(m);
        Assert.assertEquals(3, names.length);
        Assert.assertEquals("obj", names[0]);
        Assert.assertEquals("x", names[1]);
        Assert.assertEquals("i", names[2]);
        m = clazz.getMethod("getDate");
        names = discoverer.getParameterNames(m);
        Assert.assertEquals(0, names.length);
    }

    public static class InnerClass {
        public int waz = 0;

        public InnerClass() {
        }

        public InnerClass(String firstArg, long secondArg, Object thirdArg) {
            long foo = 0;
            short bar = 10;
            this.waz = ((int) (foo + bar));
        }

        public String instanceMethod(String aa) {
            return aa;
        }

        public String instanceMethod(String aa, String bb) {
            return aa + bb;
        }

        public static long staticMethod(long x) {
            long u = x;
            return u;
        }

        public static long staticMethod(long x, long y) {
            long u = x * y;
            return u;
        }
    }

    public static class GenerifiedClass<K, V> {
        private static long date;

        static {
            // some custom static bloc or <clinit>
            LocalVariableTableParameterNameDiscovererTests.GenerifiedClass.date = new Date().getTime();
        }

        public GenerifiedClass() {
            this(null, null);
        }

        public GenerifiedClass(K key) {
            this(key, null);
        }

        public GenerifiedClass(K key, V value) {
        }

        public static <P> long generifiedStaticMethod(P param) {
            return LocalVariableTableParameterNameDiscovererTests.GenerifiedClass.date;
        }

        public <P> void generifiedMethod(P param, long x, K key, V value) {
            // nothing
        }

        public static void voidStaticMethod(Object obj, long x, int i) {
            // nothing
        }

        public static long nonVoidStaticMethod(Object obj, long x, int i) {
            return LocalVariableTableParameterNameDiscovererTests.GenerifiedClass.date;
        }

        public static long getDate() {
            return LocalVariableTableParameterNameDiscovererTests.GenerifiedClass.date;
        }
    }
}

