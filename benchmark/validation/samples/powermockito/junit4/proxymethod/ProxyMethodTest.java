/**
 * Copyright 2009 the original author or authors.
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
package samples.powermockito.junit4.proxymethod;


import SuppressMethod.OBJECT;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.suppressmethod.SuppressMethod;
import samples.suppressmethod.SuppressMethodExample;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressMethod.class)
public class ProxyMethodTest {
    @Test(expected = ArrayStoreException.class)
    public void expectionThrowingMethodProxyWorksForJavaLangReflectMethods() throws Exception {
        replace(method(SuppressMethod.class, "getObject")).with(new ProxyMethodTest.ThrowingInvocationHandler());
        new SuppressMethod().getObject();
    }

    @Test(expected = ArrayStoreException.class)
    public void expectionThrowingMethodProxyWorksForMethodNames() throws Exception {
        replace(method(SuppressMethod.class, "getObject")).with(new ProxyMethodTest.ThrowingInvocationHandler());
        new SuppressMethod().getObject();
    }

    @Test
    public void returnValueChangingMethodProxyWorksForMethodNames() throws Exception {
        replace(method(SuppressMethod.class, "getObject")).with(new ProxyMethodTest.ReturnValueChangingInvocationHandler());
        Assert.assertEquals("hello world", new SuppressMethod().getObject());
    }

    @Test
    public void delegatingMethodProxyWorksForMethodNames() throws Exception {
        replace(method(SuppressMethod.class, "getObject")).with(new ProxyMethodTest.DelegatingInvocationHandler());
        Assert.assertSame(OBJECT, new SuppressMethod().getObject());
    }

    @Test
    public void mockingAndMethodProxyAtTheSameTimeWorks() throws Exception {
        replace(method(SuppressMethod.class, "getObjectStatic")).with(new ProxyMethodTest.DelegatingInvocationHandler());
        SuppressMethod tested = mock(SuppressMethod.class);
        when(tested.getObject()).thenReturn("Hello world");
        Assert.assertSame(OBJECT, SuppressMethod.getObjectStatic());
        Assert.assertEquals("Hello world", tested.getObject());
        Mockito.verify(tested).getObject();
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceInstanceMethodToStaticMethodDoesntWork() throws Exception {
        replace(method(SuppressMethod.class, "getObject")).with(method(SuppressMethodExample.class, "getStringObjectStatic"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceStaticMethodToInstaceMethodDoesntWork() throws Exception {
        replace(method(SuppressMethod.class, "getObjectStatic")).with(method(SuppressMethodExample.class, "getStringObject"));
    }

    @Test
    public void replaceStaticMethodsWork() throws Exception {
        replace(method(SuppressMethod.class, "getObjectStatic")).with(method(SuppressMethodExample.class, "getStringObjectStatic"));
        Assert.assertEquals("test", SuppressMethod.getObjectStatic());
    }

    private final class ThrowingInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
            throw new ArrayStoreException();
        }
    }

    private final class ReturnValueChangingInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
            return "hello world";
        }
    }

    private final class DelegatingInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
            return method.invoke(object, arguments);
        }
    }
}

