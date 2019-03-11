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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.hotswap.agent.plugin.proxy.test.util.HotSwapTestHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests accessing added method on a proxy.
 *
 * @author Erki Ehtla
 */
public class AddMethodProxyTest {
    public static class DummyHandler implements InvocationHandler {
        private Object a;

        public DummyHandler(Object a) {
            this.a = a;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(a, args);
        }
    }

    // Version 0
    public static class AImpl implements AddMethodProxyTest.A {
        @Override
        public int getValue1() {
            return 1;
        }

        /* (non-Javadoc)

        @see com.github.dcevm.test.methods.AddMethodProxyTest.A#getValue1(java.lang.Object[])
         */
        @Override
        public int getValue3(Object[] o) {
            return 1;
        }
    }

    // Version 0
    public static class AImpl___0 implements AddMethodProxyTest.A___0 {
        @Override
        public int getValue1() {
            return 1;
        }

        /* (non-Javadoc)

        @see com.github.dcevm.test.methods.AddMethodProxyTest.A#getValue1(java.lang.Object[])
         */
        @Override
        public int getValue3(Object[] o) {
            return 1;
        }
    }

    // Version 1
    public static class AImpl___1 implements AddMethodProxyTest.A___1 {
        @Override
        public int getValue2() {
            return 2;
        }

        @Override
        public int getValue33(Object[] o) {
            return 2;
        }
    }

    // Version 0
    public interface A {
        public int getValue1();

        public int getValue3(Object[] o);
    }

    // Version 0
    public interface A___0 {
        public int getValue1();

        public int getValue3(Object[] o);
    }

    // Version 1
    public interface A___1 {
        public int getValue2();

        public int getValue33(Object[] o);
    }

    @Test
    public void addMethodToInterfaceAndImplementation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assert (HotSwapTestHelper.__version__()) == 0;
        final AddMethodProxyTest.A a = new AddMethodProxyTest.AImpl();
        Assert.assertEquals(1, a.getValue1());
        HotSwapTestHelper.__toVersion__Delayed_JavaProxy(1);
        Method method = getMethod(a, "getValue2");
        Assert.assertEquals(2, method.invoke(a, null));
    }

    @Test
    public void accessNewMethodOnProxy() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assert (HotSwapTestHelper.__version__()) == 0;
        final AddMethodProxyTest.A a = ((AddMethodProxyTest.A) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ AddMethodProxyTest.A.class }, new AddMethodProxyTest.DummyHandler(new AddMethodProxyTest.AImpl()))));
        Assert.assertEquals(1, a.getValue1());
        HotSwapTestHelper.__toVersion__Delayed_JavaProxy(1);
        Method method = getMethod(a, "getValue33");
        Assert.assertEquals("getValue33", method.getName());
        Assert.assertEquals(2, method.invoke(a, new Object[]{ new Object[]{ new Object() } }));
    }

    @Test
    public void accessNewMethodOnProxyCreatedAfterSwap() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assert (HotSwapTestHelper.__version__()) == 0;
        AddMethodProxyTest.A a = ((AddMethodProxyTest.A) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ AddMethodProxyTest.A.class }, new AddMethodProxyTest.DummyHandler(new AddMethodProxyTest.AImpl()))));
        Assert.assertEquals(1, a.getValue1());
        HotSwapTestHelper.__toVersion__Delayed_JavaProxy(1);
        a = ((AddMethodProxyTest.A) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ AddMethodProxyTest.A.class }, new AddMethodProxyTest.DummyHandler(new AddMethodProxyTest.AImpl()))));
        Method method = getMethod(a, "getValue2");
        Assert.assertEquals("getValue2", method.getName());
        Assert.assertEquals(2, method.invoke(a, null));
        method = getMethod(a, "getValue33");
        Assert.assertEquals("getValue33", method.getName());
        Assert.assertEquals(2, method.invoke(a, new Object[]{ new Object[]{ new Object() } }));
    }
}

