package org.kairosdb.eventbus;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class FilterSubscriberTest {
    private static final String FIXTURE_ARGUMENT = "fixture argument";

    private FilterEventBus bus;

    private boolean methodCalled;

    private Object methodArgument;

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_prioityNegative_invalid() {
        FilterSubscriber.create(bus, this, getTestSubscriberMethod("recordingMethod"), (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_prioityTooLarge_invalid() {
        FilterSubscriber.create(bus, this, getTestSubscriberMethod("recordingMethod"), 101);
    }

    /* @Test
    public void testCreate() {
    FilterSubscriber s1 = FilterSubscriber.create(bus, this, getTestSubscriberMethod("recordingMethod"), 10);
    assertThat(s1, instanceOf(FilterSubscriber.SynchronizedFilterSubscriber.class));

    // a thread-safe method should not create a synchronized subscriber
    FilterSubscriber s2 = FilterSubscriber.create(bus, this, getTestSubscriberMethod("threadSafeMethod"), 10);
    assertThat(s2, not(instanceOf(FilterSubscriber.SynchronizedFilterSubscriber.class)));
    }
     */
    @Test
    public void testInvokeSubscriberMethod_basicMethodCall() throws Throwable {
        Method method = getTestSubscriberMethod("recordingMethod");
        FilterSubscriber subscriber = FilterSubscriber.create(bus, this, method, 0);
        Object result = subscriber.invokeSubscriberMethod(FilterSubscriberTest.FIXTURE_ARGUMENT);
        TestCase.assertTrue("Subscriber must call provided method", methodCalled);
        TestCase.assertTrue("Subscriber argument must be exactly the provided object.", ((methodArgument) == (FilterSubscriberTest.FIXTURE_ARGUMENT)));
        Assert.assertThat(result, IsInstanceOf.instanceOf(String.class));
        Assert.assertThat(result, CoreMatchers.equalTo(FilterSubscriberTest.FIXTURE_ARGUMENT));
    }

    @Test
    public void testInvokeSubscriberMethod_exceptionWrapping() throws Throwable {
        Method method = getTestSubscriberMethod("exceptionThrowingMethod");
        FilterSubscriber subscriber = FilterSubscriber.create(bus, this, method, 100);
        try {
            subscriber.invokeSubscriberMethod(FilterSubscriberTest.FIXTURE_ARGUMENT);
            Assert.fail("Subscribers whose methods throw must throw InvocationTargetException");
        } catch (InvocationTargetException expected) {
            Assert.assertThat(expected.getCause(), IsInstanceOf.instanceOf(FilterSubscriberTest.IntentionalException.class));
        }
    }

    @SuppressWarnings("EmptyCatchBlock")
    @Test
    public void testInvokeSubscriberMethod_errorPassthrough() throws Throwable {
        Method method = getTestSubscriberMethod("errorThrowingMethod");
        FilterSubscriber subscriber = FilterSubscriber.create(bus, this, method, 10);
        try {
            subscriber.dispatchEvent(FilterSubscriberTest.FIXTURE_ARGUMENT);
            Assert.fail("Subscribers whose methods throw Errors must rethrow them");
        } catch (FilterSubscriberTest.JudgmentError expected) {
        }
    }

    @Test
    public void test_dispatch() {
        Method method = getTestSubscriberMethod("recordingMethod");
        FilterSubscriber subscriber = FilterSubscriber.create(bus, this, method, 10);
        Object result = subscriber.dispatchEvent(FilterSubscriberTest.FIXTURE_ARGUMENT);
        TestCase.assertTrue("Subscriber must call provided method", methodCalled);
        TestCase.assertTrue("Subscriber argument must be exactly the provided object.", ((methodArgument) == (FilterSubscriberTest.FIXTURE_ARGUMENT)));
        Assert.assertThat(result, IsInstanceOf.instanceOf(String.class));
        Assert.assertThat(result, CoreMatchers.equalTo(FilterSubscriberTest.FIXTURE_ARGUMENT));
    }

    /**
     * Local exception subclass to check variety of exception thrown.
     */
    private class IntentionalException extends Exception {
        private static final long serialVersionUID = -2500191180248181379L;
    }

    /**
     * Local Error subclass to check variety of error thrown.
     */
    private class JudgmentError extends Error {
        private static final long serialVersionUID = 634248373797713373L;
    }
}

