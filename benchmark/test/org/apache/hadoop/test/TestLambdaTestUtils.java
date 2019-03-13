/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.test;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the logic in {@link LambdaTestUtils}.
 * This test suite includes Java 8 and Java 7 code; the Java 8 code exists
 * to verify that the API is easily used with Lambda expressions.
 */
public class TestLambdaTestUtils extends Assert {
    public static final int INTERVAL = 10;

    public static final int TIMEOUT = 50;

    private LambdaTestUtils.FixedRetryInterval retry = new LambdaTestUtils.FixedRetryInterval(TestLambdaTestUtils.INTERVAL);

    // counter for lambda expressions to use
    private int count;

    /**
     * Always evaluates to true.
     */
    public static final Callable<Boolean> ALWAYS_TRUE = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return true;
        }
    };

    /**
     * Always evaluates to false.
     */
    public static final Callable<Boolean> ALWAYS_FALSE = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return false;
        }
    };

    /**
     * Text in the raised FNFE.
     */
    public static final String MISSING = "not found";

    /**
     * A predicate that always throws a FileNotFoundException.
     */
    public static final Callable<Boolean> ALWAYS_FNFE = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            throw new FileNotFoundException(TestLambdaTestUtils.MISSING);
        }
    };

    /**
     * reusable timeout handler.
     */
    public static final LambdaTestUtils.GenerateTimeout TIMEOUT_FAILURE_HANDLER = new LambdaTestUtils.GenerateTimeout();

    /**
     * Always evaluates to 3L.
     */
    public static final Callable<Long> EVAL_3L = new Callable<Long>() {
        @Override
        public Long call() throws Exception {
            return 3L;
        }
    };

    /**
     * Always raises a {@code FileNotFoundException}.
     */
    public static final Callable<Long> EVAL_FNFE = new Callable<Long>() {
        @Override
        public Long call() throws Exception {
            throw new FileNotFoundException(TestLambdaTestUtils.MISSING);
        }
    };

    @Test
    public void testAwaitAlwaysTrue() throws Throwable {
        LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, TestLambdaTestUtils.ALWAYS_TRUE, new LambdaTestUtils.FixedRetryInterval(TestLambdaTestUtils.INTERVAL), TestLambdaTestUtils.TIMEOUT_FAILURE_HANDLER);
    }

    @Test
    public void testAwaitAlwaysFalse() throws Throwable {
        try {
            LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, TestLambdaTestUtils.ALWAYS_FALSE, retry, TestLambdaTestUtils.TIMEOUT_FAILURE_HANDLER);
            Assert.fail("should not have got here");
        } catch (TimeoutException e) {
            assertMinRetryCount(1);
        }
    }

    @Test
    public void testAwaitLinearRetry() throws Throwable {
        LambdaTestUtils.ProportionalRetryInterval linearRetry = new LambdaTestUtils.ProportionalRetryInterval(((TestLambdaTestUtils.INTERVAL) * 2), ((TestLambdaTestUtils.TIMEOUT) * 2));
        try {
            LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, TestLambdaTestUtils.ALWAYS_FALSE, linearRetry, TestLambdaTestUtils.TIMEOUT_FAILURE_HANDLER);
            Assert.fail("should not have got here");
        } catch (TimeoutException e) {
            Assert.assertEquals(linearRetry.toString(), 2, linearRetry.getInvocationCount());
        }
    }

    @Test
    public void testAwaitFNFE() throws Throwable {
        try {
            LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, TestLambdaTestUtils.ALWAYS_FNFE, retry, TestLambdaTestUtils.TIMEOUT_FAILURE_HANDLER);
            Assert.fail("should not have got here");
        } catch (TimeoutException e) {
            // inner clause is included
            Assert.assertTrue(((retry.getInvocationCount()) > 0));
            Assert.assertTrue(((e.getCause()) instanceof FileNotFoundException));
            GenericTestUtils.assertExceptionContains(TestLambdaTestUtils.MISSING, e);
        }
    }

    @Test
    public void testRetryInterval() throws Throwable {
        LambdaTestUtils.ProportionalRetryInterval interval = new LambdaTestUtils.ProportionalRetryInterval(200, 1000);
        Assert.assertEquals(200, ((int) (interval.call())));
        Assert.assertEquals(400, ((int) (interval.call())));
        Assert.assertEquals(600, ((int) (interval.call())));
        Assert.assertEquals(800, ((int) (interval.call())));
        Assert.assertEquals(1000, ((int) (interval.call())));
        Assert.assertEquals(1000, ((int) (interval.call())));
        Assert.assertEquals(1000, ((int) (interval.call())));
    }

    @Test
    public void testInterceptSuccess() throws Throwable {
        IOException ioe = LambdaTestUtils.intercept(IOException.class, TestLambdaTestUtils.ALWAYS_FNFE);
        GenericTestUtils.assertExceptionContains(TestLambdaTestUtils.MISSING, ioe);
    }

    @Test
    public void testInterceptContains() throws Throwable {
        LambdaTestUtils.intercept(IOException.class, TestLambdaTestUtils.MISSING, TestLambdaTestUtils.ALWAYS_FNFE);
    }

    @Test
    public void testInterceptContainsWrongString() throws Throwable {
        try {
            FileNotFoundException e = LambdaTestUtils.intercept(FileNotFoundException.class, "404", TestLambdaTestUtils.ALWAYS_FNFE);
            Assert.assertNotNull(e);
            throw e;
        } catch (AssertionError expected) {
            GenericTestUtils.assertExceptionContains(TestLambdaTestUtils.MISSING, expected);
        }
    }

    @Test
    public void testInterceptVoidCallable() throws Throwable {
        LambdaTestUtils.intercept(AssertionError.class, LambdaTestUtils.NULL_RESULT, new Callable<IOException>() {
            @Override
            public IOException call() throws Exception {
                return LambdaTestUtils.intercept(IOException.class, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testEventually() throws Throwable {
        long result = LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, TestLambdaTestUtils.EVAL_3L, retry);
        Assert.assertEquals(3, result);
        Assert.assertEquals(0, retry.getInvocationCount());
    }

    @Test
    public void testEventuallyFailuresRetry() throws Throwable {
        try {
            LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, TestLambdaTestUtils.EVAL_FNFE, retry);
            Assert.fail("should not have got here");
        } catch (IOException expected) {
            // expected
            assertMinRetryCount(1);
        }
    }

    /* Java 8 Examples go below this line. */
    @Test
    public void testInterceptFailure() throws Throwable {
        try {
            IOException ioe = LambdaTestUtils.intercept(IOException.class, () -> "hello");
            Assert.assertNotNull(ioe);
            throw ioe;
        } catch (AssertionError expected) {
            GenericTestUtils.assertExceptionContains("hello", expected);
        }
    }

    @Test
    public void testInterceptInterceptLambda() throws Throwable {
        // here we use intercept() to test itself.
        LambdaTestUtils.intercept(AssertionError.class, TestLambdaTestUtils.MISSING, () -> LambdaTestUtils.intercept(FileNotFoundException.class, "404", TestLambdaTestUtils.ALWAYS_FNFE));
    }

    @Test
    public void testInterceptInterceptVoidResultLambda() throws Throwable {
        // see what happens when a null is returned; type inference -> Void
        LambdaTestUtils.intercept(AssertionError.class, LambdaTestUtils.NULL_RESULT, () -> LambdaTestUtils.intercept(IOException.class, () -> null));
    }

    @Test
    public void testInterceptInterceptStringResultLambda() throws Throwable {
        // see what happens when a string is returned; it should be used
        // in the message
        LambdaTestUtils.intercept(AssertionError.class, "hello, world", () -> LambdaTestUtils.intercept(IOException.class, () -> "hello, world"));
    }

    @Test
    public void testAwaitNoTimeoutLambda() throws Throwable {
        LambdaTestUtils.await(0, () -> true, retry, ( timeout, ex) -> ex != null ? ex : new Exception("timeout"));
        assertRetryCount(0);
    }

    @Test
    public void testAwaitLambdaRepetitions() throws Throwable {
        count = 0;
        // lambda expression which will succeed after exactly 4 probes
        int reps = LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, () -> (++(count)) == 4, () -> 10, ( timeout, ex) -> ex != null ? ex : new Exception("timeout"));
        Assert.assertEquals(4, reps);
    }

    @Test
    public void testInterceptAwaitLambdaException() throws Throwable {
        count = 0;
        IOException ioe = LambdaTestUtils.intercept(IOException.class, () -> LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, () -> r(new IOException(("inner " + (++(count))))), retry, ( timeout, ex) -> ex));
        assertRetryCount(((count) - 1));
        // verify that the exception returned was the last one raised
        GenericTestUtils.assertExceptionContains(Integer.toString(count), ioe);
    }

    @Test
    public void testInterceptAwaitLambdaDiagnostics() throws Throwable {
        LambdaTestUtils.intercept(IOException.class, "generated", () -> // force checks -1 timeout probes
        LambdaTestUtils.await(5, () -> false, () -> -1, ( timeout, ex) -> new IOException("generated")));
    }

    @Test
    public void testInterceptAwaitFailFastLambda() throws Throwable {
        LambdaTestUtils.intercept(LambdaTestUtils.FailFastException.class, () -> LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, () -> r(new LambdaTestUtils.FailFastException("ffe")), retry, ( timeout, ex) -> ex));
        assertRetryCount(0);
    }

    @Test
    public void testEventuallyOnceLambda() throws Throwable {
        String result = LambdaTestUtils.eventually(0, () -> "hello", retry);
        Assert.assertEquals("hello", result);
        Assert.assertEquals(0, retry.getInvocationCount());
    }

    @Test
    public void testEventuallyLambda() throws Throwable {
        long result = LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, () -> 3, retry);
        Assert.assertEquals(3, result);
        assertRetryCount(0);
    }

    @Test
    public void testInterceptEventuallyLambdaFailures() throws Throwable {
        LambdaTestUtils.intercept(IOException.class, "oops", () -> LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, () -> r(new IOException("oops")), retry));
        assertMinRetryCount(1);
    }

    @Test
    public void testInterceptEventuallyambdaFailuresNegativeRetry() throws Throwable {
        LambdaTestUtils.intercept(FileNotFoundException.class, () -> LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, TestLambdaTestUtils.EVAL_FNFE, () -> -1));
    }

    @Test
    public void testInterceptEventuallyLambdaFailFast() throws Throwable {
        LambdaTestUtils.intercept(LambdaTestUtils.FailFastException.class, "oops", () -> LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, () -> r(new LambdaTestUtils.FailFastException("oops")), retry));
        assertRetryCount(0);
    }

    /**
     * Verify that assertions trigger catch and retry.
     *
     * @throws Throwable
     * 		if the code is broken
     */
    @Test
    public void testEventuallySpinsOnAssertions() throws Throwable {
        AtomicInteger counter = new AtomicInteger(0);
        LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, () -> {
            while ((counter.incrementAndGet()) < 5) {
                Assert.fail("if you see this, we are in trouble");
            } 
        }, retry);
        assertMinRetryCount(4);
    }

    /**
     * Verify that VirtualMachineError errors are immediately rethrown.
     *
     * @throws Throwable
     * 		if the code is broken
     */
    @Test
    public void testInterceptEventuallyThrowsVMErrors() throws Throwable {
        LambdaTestUtils.intercept(OutOfMemoryError.class, "OOM", () -> LambdaTestUtils.eventually(TestLambdaTestUtils.TIMEOUT, () -> r(new OutOfMemoryError("OOM")), retry));
        assertRetryCount(0);
    }

    /**
     * Verify that you can declare that an intercept will intercept Errors.
     *
     * @throws Throwable
     * 		if the code is broken
     */
    @Test
    public void testInterceptHandlesErrors() throws Throwable {
        LambdaTestUtils.intercept(OutOfMemoryError.class, "OOM", () -> r(new OutOfMemoryError("OOM")));
    }

    /**
     * Verify that if an Error raised is not the one being intercepted,
     * it gets rethrown.
     *
     * @throws Throwable
     * 		if the code is broken
     */
    @Test
    public void testInterceptRethrowsVMErrors() throws Throwable {
        LambdaTestUtils.intercept(StackOverflowError.class, "", () -> LambdaTestUtils.intercept(OutOfMemoryError.class, "", () -> r(new StackOverflowError())));
    }

    @Test
    public void testAwaitHandlesAssertions() throws Throwable {
        // await a state which is never reached, expect a timeout exception
        // with the text "failure" in it
        TimeoutException ex = LambdaTestUtils.intercept(TimeoutException.class, "failure", () -> LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, () -> r(new AssertionError("failure")), retry, TestLambdaTestUtils.TIMEOUT_FAILURE_HANDLER));
        // the retry handler must have been invoked
        assertMinRetryCount(1);
        // and the nested cause is tha raised assertion
        if (!((ex.getCause()) instanceof AssertionError)) {
            throw ex;
        }
    }

    @Test
    public void testAwaitRethrowsVMErrors() throws Throwable {
        // await a state which is never reached, expect a timeout exception
        // with the text "failure" in it
        LambdaTestUtils.intercept(StackOverflowError.class, () -> LambdaTestUtils.await(TestLambdaTestUtils.TIMEOUT, () -> r(new StackOverflowError()), retry, TestLambdaTestUtils.TIMEOUT_FAILURE_HANDLER));
        // the retry handler must not have been invoked
        assertMinRetryCount(0);
    }

    @Test
    public void testEvalToSuccess() {
        Assert.assertTrue("Eval to success", LambdaTestUtils.eval(() -> true));
    }

    /**
     * There's no attempt to wrap an unchecked exception
     * with an AssertionError.
     */
    @Test
    public void testEvalDoesntWrapRTEs() throws Throwable {
        LambdaTestUtils.intercept(RuntimeException.class, "", () -> LambdaTestUtils.eval(() -> {
            throw new RuntimeException("t");
        }));
    }

    /**
     * Verify that IOEs are caught and wrapped, and that the
     * inner cause is the original IOE.
     */
    @Test
    public void testEvalDoesWrapIOEs() throws Throwable {
        LambdaTestUtils.verifyCause(IOException.class, LambdaTestUtils.intercept(AssertionError.class, "ioe", () -> LambdaTestUtils.eval(() -> {
            throw new IOException("ioe");
        })));
    }

    @Test
    public void testInterceptFutureUnwrapped() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new IOException("oops"));
        LambdaTestUtils.interceptFuture(IOException.class, "oops", future);
    }

    @Test
    public void testInterceptFutureWrongException() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("oops"));
        LambdaTestUtils.intercept(RuntimeException.class, "oops", () -> LambdaTestUtils.interceptFuture(IOException.class, "", future));
    }

    @Test
    public void testInterceptFutureNotAnException() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new Error("oops"));
        LambdaTestUtils.verifyCause(Error.class, LambdaTestUtils.intercept(ExecutionException.class, "oops", () -> LambdaTestUtils.interceptFuture(IOException.class, "", future)));
    }

    /**
     * Variant for exception catching.
     */
    @Test
    public void testInterceptFutureNotAnException2() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new Error("oops"));
        LambdaTestUtils.verifyCause(Error.class, LambdaTestUtils.interceptFuture(ExecutionException.class, "", future));
    }

    @Test
    public void testInterceptFutureNoFailures() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("happy");
        LambdaTestUtils.intercept(AssertionError.class, "happy", () -> LambdaTestUtils.interceptFuture(IOException.class, "oops", future));
    }

    /**
     * This will timeout immediately and raise a TimeoutException.
     */
    @Test
    public void testInterceptFutureTimeout() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        LambdaTestUtils.intercept(TimeoutException.class, "", () -> LambdaTestUtils.interceptFuture(IOException.class, "oops", 1, TimeUnit.NANOSECONDS, future));
    }

    /**
     * This will timeout immediately and raise a TimeoutException.
     */
    @Test
    public void testInterceptFutureTimeout2() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        LambdaTestUtils.interceptFuture(TimeoutException.class, "", 1, TimeUnit.NANOSECONDS, future);
    }

    /**
     * This will timeout immediately and raise a TimeoutException.
     */
    @Test
    public void testInterceptFutureTimeoutSuccess() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new IOException("oops"));
        LambdaTestUtils.interceptFuture(IOException.class, "oops", 1, TimeUnit.NANOSECONDS, future);
    }

    /**
     * This will timeout immediately and raise a TimeoutException.
     */
    @Test
    public void testInterceptFutureCancelled() throws Throwable {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.cancel(false);
        LambdaTestUtils.interceptFuture(CancellationException.class, "", 1, TimeUnit.NANOSECONDS, future);
    }
}

