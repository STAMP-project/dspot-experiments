/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationFactory;
import org.mockito.invocation.MockHandler;
import org.mockitoutil.TestBase;


/**
 * This test is an experimental use of Mockito API to simulate static mocking.
 * Other frameworks can use it to build good support for static mocking.
 * Keep in mind that clean code never needs to mock static methods.
 * This test is a documentation how it can be done using current public API of Mockito.
 * This test is not only an experiment it also provides coverage for
 * some of the advanced public API exposed for framework integrators.
 * <p>
 * For more rationale behind this experimental test
 * <a href="https://www.linkedin.com/pulse/mockito-vs-powermock-opinionated-dogmatic-static-mocking-faber">see the article</a>.
 */
public class StaticMockingExperimentTest extends TestBase {
    StaticMockingExperimentTest.Foo mock = Mockito.mock(StaticMockingExperimentTest.Foo.class);

    MockHandler handler = Mockito.mockingDetails(mock).getMockHandler();

    Method staticMethod;

    InvocationFactory.RealMethodBehavior realMethod = new InvocationFactory.RealMethodBehavior() {
        @Override
        public Object call() throws Throwable {
            return null;
        }
    };

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void verify_static_method() throws Throwable {
        // register staticMethod call on mock
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "some arg");
        handler.handle(invocation);
        // verify staticMethod on mock
        // Mockito cannot capture static methods so we will simulate this scenario in 3 steps:
        // 1. Call standard 'verify' method. Internally, it will add verificationMode to the thread local state.
        // Effectively, we indicate to Mockito that right now we are about to verify a method call on this mock.
        Mockito.verify(mock);
        // 2. Create the invocation instance using the new public API
        // Mockito cannot capture static methods but we can create an invocation instance of that static invocation
        Invocation verification = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "some arg");
        // 3. Make Mockito handle the static method invocation
        // Mockito will find verification mode in thread local state and will try verify the invocation
        handler.handle(verification);
        // verify zero times, method with different argument
        Mockito.verify(mock, Mockito.times(0));
        Invocation differentArg = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "different arg");
        handler.handle(differentArg);
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void verification_failure_static_method() throws Throwable {
        // register staticMethod call on mock
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "foo");
        handler.handle(invocation);
        // verify staticMethod on mock
        Mockito.verify(mock);
        Invocation differentArg = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "different arg");
        try {
            handler.handle(differentArg);
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
        }
    }

    @Test
    public void stubbing_static_method() throws Throwable {
        // register staticMethod call on mock
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "foo");
        handler.handle(invocation);
        // register stubbing
        Mockito.when(null).thenReturn("hey");
        // validate stubbed return value
        Assert.assertEquals("hey", handler.handle(invocation));
        Assert.assertEquals("hey", handler.handle(invocation));
        // default null value is returned if invoked with different argument
        Invocation differentArg = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "different arg");
        Assert.assertEquals(null, handler.handle(differentArg));
    }

    @Test
    public void do_answer_stubbing_static_method() throws Throwable {
        // register stubbed return value
        Mockito.doReturn("hey").when(mock);
        // complete stubbing by triggering an invocation that needs to be stubbed
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "foo");
        handler.handle(invocation);
        // validate stubbed return value
        Assert.assertEquals("hey", handler.handle(invocation));
        Assert.assertEquals("hey", handler.handle(invocation));
        // default null value is returned if invoked with different argument
        Invocation differentArg = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "different arg");
        Assert.assertEquals(null, handler.handle(differentArg));
    }

    @Test
    public void verify_no_more_interactions() throws Throwable {
        // works for now because there are not interactions
        Mockito.verifyNoMoreInteractions(mock);
        // register staticMethod call on mock
        Invocation invocation = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), staticMethod, realMethod, "foo");
        handler.handle(invocation);
        // fails now because we have one static invocation recorded
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void stubbing_new() throws Throwable {
        Constructor<StaticMockingExperimentTest.Foo> ctr = StaticMockingExperimentTest.Foo.class.getConstructor(String.class);
        Method adapter = StaticMockingExperimentTest.ConstructorMethodAdapter.class.getDeclaredMethods()[0];
        // stub constructor
        Mockito.doReturn(new StaticMockingExperimentTest.Foo("hey!")).when(mock);
        Invocation constructor = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), adapter, realMethod, ctr, "foo");
        handler.handle(constructor);
        // test stubbing
        Object result = handler.handle(constructor);
        Assert.assertEquals("foo:hey!", result.toString());
        // stubbing miss
        Invocation differentArg = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), adapter, realMethod, ctr, "different arg");
        Object result2 = handler.handle(differentArg);
        Assert.assertEquals(null, result2);
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void verifying_new() throws Throwable {
        Constructor<StaticMockingExperimentTest.Foo> ctr = StaticMockingExperimentTest.Foo.class.getConstructor(String.class);
        Method adapter = StaticMockingExperimentTest.ConstructorMethodAdapter.class.getDeclaredMethods()[0];
        // invoke constructor
        Invocation constructor = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), adapter, realMethod, ctr, "matching arg");
        handler.handle(constructor);
        // verify successfully
        Mockito.verify(mock);
        handler.handle(constructor);
        // verification failure
        Mockito.verify(mock);
        Invocation differentArg = Mockito.framework().getInvocationFactory().createInvocation(mock, Mockito.withSettings().build(StaticMockingExperimentTest.Foo.class), adapter, realMethod, ctr, "different arg");
        try {
            handler.handle(differentArg);
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e.getMessage()).contains("matching arg").contains("different arg");
        }
    }

    static class Foo {
        private final String arg;

        public Foo(String arg) {
            this.arg = arg;
        }

        public static String staticMethod(String arg) {
            return "";
        }

        @Override
        public String toString() {
            return "foo:" + (arg);
        }
    }

    /**
     * Adapts constructor to method calls needed to work with Mockito API.
     */
    interface ConstructorMethodAdapter {
        Object construct(Constructor constructor, Object... args);
    }
}

