/**
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.otto;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.junit.Test;


public class EventHandlerTest {
    private static final Object FIXTURE_ARGUMENT = new Object();

    private boolean methodCalled;

    private Object methodArgument;

    /**
     * Checks that a no-frills, no-issues method call is properly executed.
     *
     * @throws Exception
     * 		if the aforementioned proper execution is not to be had.
     */
    @Test
    public void basicMethodCall() throws Exception {
        Method method = getRecordingMethod();
        EventHandler handler = new EventHandler(this, method);
        handler.handleEvent(EventHandlerTest.FIXTURE_ARGUMENT);
        Assert.assertTrue("Handler must call provided method.", methodCalled);
        Assert.assertSame("Handler argument must be *exactly* the provided object.", methodArgument, EventHandlerTest.FIXTURE_ARGUMENT);
    }

    /**
     * Checks that EventHandler's constructor disallows null methods.
     */
    @Test
    public void rejectionOfNullMethods() {
        try {
            new EventHandler(this, null);
            Assert.fail("EventHandler must immediately reject null methods.");
        } catch (NullPointerException expected) {
            // Hooray!
        }
    }

    /**
     * Checks that EventHandler's constructor disallows null targets.
     */
    @Test
    public void rejectionOfNullTargets() throws NoSuchMethodException {
        Method method = getRecordingMethod();
        try {
            new EventHandler(null, method);
            Assert.fail("EventHandler must immediately reject null targets.");
        } catch (NullPointerException expected) {
            // Huzzah!
        }
    }

    @Test
    public void exceptionWrapping() throws NoSuchMethodException {
        Method method = getExceptionThrowingMethod();
        EventHandler handler = new EventHandler(this, method);
        try {
            handler.handleEvent(new Object());
            Assert.fail("Handlers whose methods throw must throw InvocationTargetException");
        } catch (InvocationTargetException e) {
            Assert.assertTrue("Expected exception must be wrapped.", ((e.getCause()) instanceof EventHandlerTest.IntentionalException));
        }
    }

    @Test
    public void errorPassthrough() throws NoSuchMethodException, InvocationTargetException {
        Method method = getErrorThrowingMethod();
        EventHandler handler = new EventHandler(this, method);
        try {
            handler.handleEvent(new Object());
            Assert.fail("Handlers whose methods throw Errors must rethrow them");
        } catch (EventHandlerTest.JudgmentError expected) {
            // Expected.
        }
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

