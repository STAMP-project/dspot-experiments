package com.hwangjr.rxbus;


import com.hwangjr.rxbus.entity.ProducerEvent;
import com.hwangjr.rxbus.thread.EventThread;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.junit.Test;
import rx.functions.Action1;


public class EventProducerTest {
    private static final Object FIXTURE_RETURN_VALUE = new Object();

    private boolean methodCalled;

    private Object methodReturnValue;

    /**
     * Checks that a no-frills, no-issues method call is properly executed.
     *
     * @throws Exception
     * 		if the aforementioned proper execution is not to be had.
     */
    @Test
    public void basicMethodCall() throws Exception {
        Method method = getRecordingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);
        producer.produce().subscribe(new Action1() {
            @Override
            public void call(Object methodResult) {
                Assert.assertTrue("Producer must call provided method.", methodCalled);
                Assert.assertSame("Producer result must be *exactly* the specified return value.", methodResult, EventProducerTest.FIXTURE_RETURN_VALUE);
            }
        });
    }

    /**
     * Checks that EventProducer's constructor disallows null methods.
     */
    @Test
    public void rejectionOfNullMethods() {
        try {
            new ProducerEvent(this, null, EventThread.MAIN_THREAD);
            Assert.fail("EventProducer must immediately reject null methods.");
        } catch (NullPointerException expected) {
            // Hooray!
        }
    }

    /**
     * Checks that EventProducer's constructor disallows null targets.
     */
    @Test
    public void rejectionOfNullTargets() throws NoSuchMethodException {
        Method method = getRecordingMethod();
        try {
            new ProducerEvent(null, method, EventThread.MAIN_THREAD);
            Assert.fail("EventProducer must immediately reject null targets.");
        } catch (NullPointerException expected) {
            // Huzzah!
        }
    }

    @Test
    public void testExceptionWrapping() throws NoSuchMethodException {
        Method method = getExceptionThrowingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);
        try {
            producer.produce();
            Assert.fail("Producers whose methods throw must throw RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertTrue("Expected exception must be wrapped.", ((e.getCause()) instanceof EventProducerTest.IntentionalException));
        }
    }

    @Test
    public void errorPassthrough() throws NoSuchMethodException, InvocationTargetException {
        Method method = getErrorThrowingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);
        try {
            producer.produce();
            Assert.fail("Producers whose methods throw Errors must rethrow them");
        } catch (EventProducerTest.JudgmentError expected) {
            // Expected.
        }
    }

    @Test
    public void returnValueNotCached() throws Exception {
        Method method = getRecordingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);
        producer.produce().subscribe();
        methodReturnValue = new Object();
        methodCalled = false;
        producer.produce().subscribe(new Action1() {
            @Override
            public void call(Object secondReturnValue) {
                Assert.assertTrue("Producer must call provided method twice.", methodCalled);
                Assert.assertSame("Producer result must be *exactly* the specified return value on each invocation.", secondReturnValue, methodReturnValue);
            }
        });
    }

    /**
     * Local exception subclass to check variety of exception thrown.
     */
    static class IntentionalException extends Exception {
        private static final long serialVersionUID = -2500191180248181379L;
    }

    /**
     * Local Error subclass to check variety of error thrown.
     */
    static class JudgmentError extends Error {
        private static final long serialVersionUID = 634248373797713373L;
    }
}

