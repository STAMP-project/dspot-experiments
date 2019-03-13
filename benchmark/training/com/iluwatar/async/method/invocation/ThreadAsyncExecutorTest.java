/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.async.method.invocation;


import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Date: 12/6/15 - 10:49 AM
 *
 * @author Jeroen Meulemeester
 */
public class ThreadAsyncExecutorTest {
    /**
     * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable)}
     */
    @Test
    public void testSuccessfulTaskWithoutCallback() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(3000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final Object result = new Object();
            final Callable<Object> task = Mockito.mock(Callable.class);
            Mockito.when(task.call()).thenReturn(result);
            final AsyncResult<Object> asyncResult = executor.startProcess(task);
            Assertions.assertNotNull(asyncResult);
            asyncResult.await();// Prevent timing issues, and wait until the result is available

            Assertions.assertTrue(asyncResult.isCompleted());
            // Our task should only execute once ...
            Mockito.verify(task, VerificationModeFactory.times(1)).call();
            // ... and the result should be exactly the same object
            Assertions.assertSame(result, asyncResult.getValue());
        });
    }

    /**
     * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)}
     */
    @Test
    public void testSuccessfulTaskWithCallback() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(3000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final Object result = new Object();
            final Callable<Object> task = Mockito.mock(Callable.class);
            Mockito.when(task.call()).thenReturn(result);
            final AsyncCallback callback = Mockito.mock(AsyncCallback.class);
            final AsyncResult<Object> asyncResult = executor.startProcess(task, callback);
            Assertions.assertNotNull(asyncResult);
            asyncResult.await();// Prevent timing issues, and wait until the result is available

            Assertions.assertTrue(asyncResult.isCompleted());
            // Our task should only execute once ...
            Mockito.verify(task, VerificationModeFactory.times(1)).call();
            // ... same for the callback, we expect our object
            final ArgumentCaptor<Optional<Exception>> optionalCaptor = ArgumentCaptor.forClass(((Class) (Optional.class)));
            Mockito.verify(callback, VerificationModeFactory.times(1)).onComplete(ArgumentMatchers.eq(result), optionalCaptor.capture());
            final Optional<Exception> optionalException = optionalCaptor.getValue();
            Assertions.assertNotNull(optionalException);
            Assertions.assertFalse(optionalException.isPresent());
            // ... and the result should be exactly the same object
            Assertions.assertSame(result, asyncResult.getValue());
        });
    }

    /**
     * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable)} when a task takes a while
     * to execute
     */
    @Test
    public void testLongRunningTaskWithoutCallback() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final Object result = new Object();
            final Callable<Object> task = Mockito.mock(Callable.class);
            Mockito.when(task.call()).thenAnswer(( i) -> {
                Thread.sleep(1500);
                return result;
            });
            final AsyncResult<Object> asyncResult = executor.startProcess(task);
            Assertions.assertNotNull(asyncResult);
            Assertions.assertFalse(asyncResult.isCompleted());
            try {
                asyncResult.getValue();
                Assertions.fail("Expected IllegalStateException when calling AsyncResult#getValue on a non-completed task");
            } catch (IllegalStateException e) {
                Assertions.assertNotNull(e.getMessage());
            }
            // Our task should only execute once, but it can take a while ...
            Mockito.verify(task, Mockito.timeout(3000).times(1)).call();
            // Prevent timing issues, and wait until the result is available
            asyncResult.await();
            Assertions.assertTrue(asyncResult.isCompleted());
            Mockito.verifyNoMoreInteractions(task);
            // ... and the result should be exactly the same object
            Assertions.assertSame(result, asyncResult.getValue());
        });
    }

    /**
     * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)} when a task
     * takes a while to execute
     */
    @Test
    public void testLongRunningTaskWithCallback() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final Object result = new Object();
            final Callable<Object> task = Mockito.mock(Callable.class);
            Mockito.when(task.call()).thenAnswer(( i) -> {
                Thread.sleep(1500);
                return result;
            });
            final AsyncCallback<Object> callback = Mockito.mock(AsyncCallback.class);
            final AsyncResult<Object> asyncResult = executor.startProcess(task, callback);
            Assertions.assertNotNull(asyncResult);
            Assertions.assertFalse(asyncResult.isCompleted());
            Mockito.verifyZeroInteractions(callback);
            try {
                asyncResult.getValue();
                Assertions.fail("Expected IllegalStateException when calling AsyncResult#getValue on a non-completed task");
            } catch (IllegalStateException e) {
                Assertions.assertNotNull(e.getMessage());
            }
            // Our task should only execute once, but it can take a while ...
            Mockito.verify(task, Mockito.timeout(3000).times(1)).call();
            final ArgumentCaptor<Optional<Exception>> optionalCaptor = ArgumentCaptor.forClass(((Class) (Optional.class)));
            Mockito.verify(callback, Mockito.timeout(3000).times(1)).onComplete(ArgumentMatchers.eq(result), optionalCaptor.capture());
            final Optional<Exception> optionalException = optionalCaptor.getValue();
            Assertions.assertNotNull(optionalException);
            Assertions.assertFalse(optionalException.isPresent());
            // Prevent timing issues, and wait until the result is available
            asyncResult.await();
            Assertions.assertTrue(asyncResult.isCompleted());
            Mockito.verifyNoMoreInteractions(task, callback);
            // ... and the result should be exactly the same object
            Assertions.assertSame(result, asyncResult.getValue());
        });
    }

    /**
     * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable)} when a task takes a while
     * to execute, while waiting on the result using {@link ThreadAsyncExecutor#endProcess(AsyncResult)}
     */
    @Test
    public void testEndProcess() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final Object result = new Object();
            final Callable<Object> task = Mockito.mock(Callable.class);
            Mockito.when(task.call()).thenAnswer(( i) -> {
                Thread.sleep(1500);
                return result;
            });
            final AsyncResult<Object> asyncResult = executor.startProcess(task);
            Assertions.assertNotNull(asyncResult);
            Assertions.assertFalse(asyncResult.isCompleted());
            try {
                asyncResult.getValue();
                Assertions.fail("Expected IllegalStateException when calling AsyncResult#getValue on a non-completed task");
            } catch (IllegalStateException e) {
                Assertions.assertNotNull(e.getMessage());
            }
            Assertions.assertSame(result, executor.endProcess(asyncResult));
            Mockito.verify(task, VerificationModeFactory.times(1)).call();
            Assertions.assertTrue(asyncResult.isCompleted());
            // Calling end process a second time while already finished should give the same result
            Assertions.assertSame(result, executor.endProcess(asyncResult));
            Mockito.verifyNoMoreInteractions(task);
        });
    }

    /**
     * Test used to verify the behaviour of {@link ThreadAsyncExecutor#startProcess(Callable)} when the callable is 'null'
     */
    @Test
    public void testNullTask() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(3000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final AsyncResult<Object> asyncResult = executor.startProcess(null);
            Assertions.assertNotNull(asyncResult, "The AsyncResult should not be 'null', even though the task was 'null'.");
            asyncResult.await();// Prevent timing issues, and wait until the result is available

            Assertions.assertTrue(asyncResult.isCompleted());
            try {
                asyncResult.getValue();
                Assertions.fail("Expected ExecutionException with NPE as cause");
            } catch (final ExecutionException e) {
                Assertions.assertNotNull(e.getMessage());
                Assertions.assertNotNull(e.getCause());
                Assertions.assertEquals(NullPointerException.class, e.getCause().getClass());
            }
        });
    }

    /**
     * Test used to verify the behaviour of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)} when the
     * callable is 'null', but the asynchronous callback is provided
     */
    @Test
    public void testNullTaskWithCallback() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(3000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final AsyncCallback<Object> callback = Mockito.mock(AsyncCallback.class);
            final AsyncResult<Object> asyncResult = executor.startProcess(null, callback);
            Assertions.assertNotNull(asyncResult, "The AsyncResult should not be 'null', even though the task was 'null'.");
            asyncResult.await();// Prevent timing issues, and wait until the result is available

            Assertions.assertTrue(asyncResult.isCompleted());
            final ArgumentCaptor<Optional<Exception>> optionalCaptor = ArgumentCaptor.forClass(((Class) (Optional.class)));
            Mockito.verify(callback, VerificationModeFactory.times(1)).onComplete(Matchers.isNull(), optionalCaptor.capture());
            final Optional<Exception> optionalException = optionalCaptor.getValue();
            Assertions.assertNotNull(optionalException);
            Assertions.assertTrue(optionalException.isPresent());
            final Exception exception = optionalException.get();
            Assertions.assertNotNull(exception);
            Assertions.assertEquals(NullPointerException.class, exception.getClass());
            try {
                asyncResult.getValue();
                Assertions.fail("Expected ExecutionException with NPE as cause");
            } catch (final ExecutionException e) {
                Assertions.assertNotNull(e.getMessage());
                Assertions.assertNotNull(e.getCause());
                Assertions.assertEquals(NullPointerException.class, e.getCause().getClass());
            }
        });
    }

    /**
     * Test used to verify the behaviour of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)} when both
     * the callable and the asynchronous callback are 'null'
     */
    @Test
    public void testNullTaskWithNullCallback() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(3000), () -> {
            // Instantiate a new executor and start a new 'null' task ...
            final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
            final AsyncResult<Object> asyncResult = executor.startProcess(null, null);
            Assertions.assertNotNull(asyncResult, "The AsyncResult should not be 'null', even though the task and callback were 'null'.");
            asyncResult.await();// Prevent timing issues, and wait until the result is available

            Assertions.assertTrue(asyncResult.isCompleted());
            try {
                asyncResult.getValue();
                Assertions.fail("Expected ExecutionException with NPE as cause");
            } catch (final ExecutionException e) {
                Assertions.assertNotNull(e.getMessage());
                Assertions.assertNotNull(e.getCause());
                Assertions.assertEquals(NullPointerException.class, e.getCause().getClass());
            }
        });
    }
}

