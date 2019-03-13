/**
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.plugin.proxy.test.methods;


import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hotswap.agent.plugin.proxy.test.util.HotSwapTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;


public class AddEnhancerMethodProxyTest {
    // Version 0
    public static class AImpl implements AddEnhancerMethodProxyTest.A {
        @Override
        public int getValue1() {
            return 1;
        }
    }

    // Version 0
    public static class AImpl___0 implements AddEnhancerMethodProxyTest.A___0 {
        @Override
        public int getValue1() {
            return 1;
        }
    }

    // Version 1
    public static class AImpl___1 implements AddEnhancerMethodProxyTest.A___1 {
        @Override
        public int getValue2() {
            return 2;
        }
    }

    // Version 2
    public static class AImpl___2 implements AddEnhancerMethodProxyTest.A___2 {
        @Override
        public int getValue3() {
            return 3;
        }
    }

    // Version 0
    public interface A {
        public int getValue1();
    }

    // Version 0
    public interface A___0 {
        public int getValue1();
    }

    // Version 1
    public interface A___1 {
        public int getValue2();
    }

    // Version 2
    public interface A___2 {
        public int getValue3();
    }

    @Test
    public void addMethodToInterfaceAndImplementation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assert (HotSwapTestHelper.__version__()) == 0;
        final AddEnhancerMethodProxyTest.A a = new AddEnhancerMethodProxyTest.AImpl();
        Assert.assertEquals(1, a.getValue1());
        HotSwapTestHelper.__toVersion__Delayed(1);
        Method method = getMethod(a, "getValue2");
        Assert.assertEquals(2, method.invoke(a, null));
    }

    public static class SerializableNoOp implements Serializable , MethodInterceptor {
        private int count;

        private AddEnhancerMethodProxyTest.AImpl obj = new AddEnhancerMethodProxyTest.AImpl();

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getName().startsWith("getValue"))
                (count)++;

            return method.invoke(obj, args);
        }

        public int getInvocationCount() {
            return count;
        }
    }

    @Test
    public void accessNewMethodOnProxy() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assert (HotSwapTestHelper.__version__()) == 0;
        AddEnhancerMethodProxyTest.SerializableNoOp cb = new AddEnhancerMethodProxyTest.SerializableNoOp();
        final AddEnhancerMethodProxyTest.A a = createEnhancer(cb);
        Assert.assertEquals(0, cb.getInvocationCount());
        Assert.assertEquals(1, a.getValue1());
        Assert.assertEquals(1, cb.getInvocationCount());
        HotSwapTestHelper.__toVersion__Delayed(1);
        Method method = getMethod(a, "getValue2");
        Assert.assertEquals("getValue2", method.getName());
        Assert.assertEquals(1, cb.getInvocationCount());
        Assert.assertEquals(2, method.invoke(a, null));
        Assert.assertEquals(2, cb.getInvocationCount());
        HotSwapTestHelper.__toVersion__Delayed(2);
        method = getMethod(a, "getValue3");
        Assert.assertEquals("getValue3", method.getName());
        Assert.assertEquals(2, cb.getInvocationCount());
        Assert.assertEquals(3, method.invoke(a, null));
        Assert.assertEquals(3, cb.getInvocationCount());
    }

    @Test
    public void accessNewMethodOnProxyCreatedAfterSwap() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assert (HotSwapTestHelper.__version__()) == 0;
        AddEnhancerMethodProxyTest.SerializableNoOp cb = new AddEnhancerMethodProxyTest.SerializableNoOp();
        AddEnhancerMethodProxyTest.A a = createEnhancer(cb);
        Assert.assertEquals(0, cb.getInvocationCount());
        Assert.assertEquals(1, a.getValue1());
        Assert.assertEquals(1, cb.getInvocationCount());
        HotSwapTestHelper.__toVersion__Delayed(1);
        a = createEnhancer(cb);
        Method method = getMethod(a, "getValue2");
        Assert.assertEquals("getValue2", method.getName());
        Assert.assertEquals(1, cb.getInvocationCount());
        Assert.assertEquals(2, method.invoke(a, null));
        Assert.assertEquals(2, cb.getInvocationCount());
    }
}

