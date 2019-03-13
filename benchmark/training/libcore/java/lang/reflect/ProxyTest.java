/**
 * Copyright (C) 2010 The Android Open Source Project
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


import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import junit.framework.TestCase;
import tests.util.ClassLoaderBuilder;


public final class ProxyTest extends TestCase {
    /**
     * Make sure the proxy's class loader fails if it cannot see the class
     * loaders of its implemented interfaces. http://b/1608481
     */
    public void testClassLoaderMustSeeImplementedInterfaces() throws Exception {
        String prefix = ProxyTest.class.getName();
        ClassLoader loaderA = new ClassLoaderBuilder().withPrivateCopy(prefix).build();
        ClassLoader loaderB = new ClassLoaderBuilder().withPrivateCopy(prefix).build();
        Class[] interfacesA = new Class[]{ loaderA.loadClass((prefix + "$Echo")) };
        try {
            Proxy.newProxyInstance(loaderB, interfacesA, new ProxyTest.TestInvocationHandler());
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testClassLoaderDoesNotNeedToSeeInvocationHandlerLoader() throws Exception {
        String prefix = ProxyTest.class.getName();
        ClassLoader loaderA = new ClassLoaderBuilder().withPrivateCopy(prefix).build();
        ClassLoader loaderB = new ClassLoaderBuilder().withPrivateCopy(prefix).build();
        InvocationHandler invocationHandlerB = ((InvocationHandler) (loaderB.loadClass((prefix + "$TestInvocationHandler")).newInstance()));
        Class[] interfacesA = new Class[]{ loaderA.loadClass((prefix + "$Echo")) };
        Object proxy = Proxy.newProxyInstance(loaderA, interfacesA, invocationHandlerB);
        TestCase.assertEquals(loaderA, proxy.getClass().getClassLoader());
        TestCase.assertEquals("foo", proxy.getClass().getMethod("echo", String.class).invoke(proxy, "foo"));
    }

    public interface Echo {
        String echo(String s);
    }

    public static class TestInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return args[0];
        }
    }

    // https://code.google.com/p/android/issues/detail?id=24846
    public void test24846() throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        Class[] interfaces = new Class[]{ PropertyChangeListener.class };
        Object proxy = Proxy.newProxyInstance(cl, interfaces, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
        for (Field field : proxy.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            TestCase.assertFalse(field.isAnnotationPresent(Deprecated.class));
        }
    }
}

