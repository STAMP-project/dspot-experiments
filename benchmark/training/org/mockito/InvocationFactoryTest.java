/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;


import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationFactory;
import org.mockitoutil.TestBase;


public class InvocationFactoryTest extends TestBase {
    static class TestClass {
        public String testMethod() throws Throwable {
            return "un-mocked";
        }
    }

    final InvocationFactoryTest.TestClass mock = Mockito.spy(InvocationFactoryTest.TestClass.class);

    @Test
    public void call_method_that_throws_a_throwable() throws Throwable {
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(InvocationFactoryTest.TestClass.class), InvocationFactoryTest.TestClass.class.getDeclaredMethod("testMethod"), new InvocationFactory.RealMethodBehavior() {
            @Override
            public Object call() throws Throwable {
                throw new Throwable("mocked");
            }
        });
        try {
            Mockito.mockingDetails(mock).getMockHandler().handle(invocation);
        } catch (Throwable t) {
            Assert.assertEquals("mocked", t.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void call_method_that_returns_a_string() throws Throwable {
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(InvocationFactoryTest.TestClass.class), InvocationFactoryTest.TestClass.class.getDeclaredMethod("testMethod"), new InvocationFactory.RealMethodBehavior() {
            @Override
            public Object call() throws Throwable {
                return "mocked";
            }
        });
        Object ret = Mockito.mockingDetails(mock).getMockHandler().handle(invocation);
        Assert.assertEquals("mocked", ret);
    }

    @Test
    public void deprecated_api_still_works() throws Throwable {
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(InvocationFactoryTest.TestClass.class), InvocationFactoryTest.TestClass.class.getDeclaredMethod("testMethod"), new Callable() {
            public Object call() throws Exception {
                return "mocked";
            }
        });
        Object ret = Mockito.mockingDetails(mock).getMockHandler().handle(invocation);
        Assert.assertEquals("mocked", ret);
    }
}

