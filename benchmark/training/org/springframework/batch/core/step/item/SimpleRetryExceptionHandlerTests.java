/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.step.item;


import java.util.Collections;
import junit.framework.TestCase;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.context.RepeatContextSupport;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;


/**
 *
 *
 * @author Dave Syer
 */
public class SimpleRetryExceptionHandlerTests extends TestCase {
    private RepeatContext context = new RepeatContextSupport(new RepeatContextSupport(null));

    /**
     * Test method for
     * {@link org.springframework.batch.core.step.item.SimpleRetryExceptionHandler#handleException(org.springframework.batch.repeat.RepeatContext, java.lang.Throwable)} .
     */
    public void testRethrowWhenRetryExhausted() throws Throwable {
        RetryPolicy retryPolicy = new NeverRetryPolicy();
        RuntimeException ex = new RuntimeException("foo");
        SimpleRetryExceptionHandler handler = getHandlerAfterRetry(retryPolicy, ex, Collections.<Class<? extends Throwable>>singleton(Error.class));
        // Then pretend to handle the exception in the parent context...
        try {
            handler.handleException(context.getParent(), ex);
            TestCase.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            TestCase.assertEquals(ex, e);
        }
        TestCase.assertEquals(0, context.attributeNames().length);
        // One for the retry exhausted flag and one for the counter in the
        // delegate exception handler
        TestCase.assertEquals(2, context.getParent().attributeNames().length);
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.step.item.SimpleRetryExceptionHandler#handleException(org.springframework.batch.repeat.RepeatContext, java.lang.Throwable)} .
     */
    public void testNoRethrowWhenRetryNotExhausted() throws Throwable {
        RetryPolicy retryPolicy = new AlwaysRetryPolicy();
        RuntimeException ex = new RuntimeException("foo");
        SimpleRetryExceptionHandler handler = getHandlerAfterRetry(retryPolicy, ex, Collections.<Class<? extends Throwable>>singleton(Error.class));
        // Then pretend to handle the exception in the parent context...
        handler.handleException(context.getParent(), ex);
        TestCase.assertEquals(0, context.attributeNames().length);
        TestCase.assertEquals(0, context.getParent().attributeNames().length);
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.step.item.SimpleRetryExceptionHandler#handleException(org.springframework.batch.repeat.RepeatContext, java.lang.Throwable)} .
     */
    public void testRethrowWhenFatal() throws Throwable {
        RetryPolicy retryPolicy = new AlwaysRetryPolicy();
        RuntimeException ex = new RuntimeException("foo");
        SimpleRetryExceptionHandler handler = getHandlerAfterRetry(retryPolicy, ex, Collections.<Class<? extends Throwable>>singleton(RuntimeException.class));
        // Then pretend to handle the exception in the parent context...
        try {
            handler.handleException(context.getParent(), ex);
            TestCase.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            TestCase.assertEquals(ex, e);
        }
        TestCase.assertEquals(0, context.attributeNames().length);
        // One for the counter in the delegate exception handler
        TestCase.assertEquals(1, context.getParent().attributeNames().length);
    }
}

