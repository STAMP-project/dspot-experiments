/**
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core;


import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.test.core.VertxTestBase;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FutureTest extends VertxTestBase {
    @Test
    public void testCreateWithHandler() {
        AtomicInteger count = new AtomicInteger();
        AtomicReference<Future<String>> ref = new AtomicReference<>();
        Future<String> f2 = Future.future(( f1) -> {
            assertFalse(f1.isComplete());
            count.incrementAndGet();
            ref.set(f1);
        });
        assertSame(f2, ref.get());
        assertEquals(1, count.get());
        new FutureTest.Checker(f2).assertNotCompleted();
        ref.set(null);
        count.set(0);
        f2 = Future.future(( f1) -> {
            count.incrementAndGet();
            ref.set(f1);
            f1.complete("the-value");
        });
        assertSame(f2, ref.get());
        assertEquals(1, count.get());
        new FutureTest.Checker(f2).assertSucceeded("the-value");
        ref.set(null);
        count.set(0);
        RuntimeException cause = new RuntimeException();
        f2 = Future.future(( f1) -> {
            count.incrementAndGet();
            ref.set(f1);
            f1.fail(cause);
        });
        assertSame(f2, ref.get());
        assertEquals(1, count.get());
        new FutureTest.Checker(f2).assertFailed(cause);
        try {
            Future.future(( f) -> {
                throw cause;
            });
            fail();
        } catch (Exception e) {
            assertSame(cause, e);
        }
    }

    @Test
    public void testStateAfterCompletion() {
        Object foo = new Object();
        Future<Object> future = Future.succeededFuture(foo);
        assertTrue(future.succeeded());
        assertFalse(future.failed());
        assertTrue(future.isComplete());
        assertEquals(foo, future.result());
        assertNull(future.cause());
        Exception cause = new Exception();
        future = Future.failedFuture(cause);
        assertFalse(future.succeeded());
        assertTrue(future.failed());
        assertTrue(future.isComplete());
        assertNull(future.result());
        assertEquals(cause, future.cause());
    }

    @Test
    public void testSetResultOnCompletedFuture() {
        ArrayList<Future<Object>> futures = new ArrayList<>();
        futures.add(Future.succeededFuture());
        futures.add(Future.succeededFuture());
        futures.add(Future.succeededFuture(new Object()));
        futures.add(Future.succeededFuture(new Object()));
        futures.add(Future.failedFuture(new Exception()));
        futures.add(Future.failedFuture(new Exception()));
        for (Future<Object> future : futures) {
            try {
                future.complete(new Object());
                fail();
            } catch (IllegalStateException ignore) {
            }
            assertFalse(future.tryComplete(new Object()));
            try {
                future.complete(null);
                fail();
            } catch (IllegalStateException ignore) {
            }
            assertFalse(future.tryComplete(null));
            try {
                future.fail(new Exception());
                fail();
            } catch (IllegalStateException ignore) {
            }
            assertFalse(future.tryFail(new Exception()));
        }
    }

    @Test
    public void testCallSetHandlerBeforeCompletion() {
        AtomicBoolean called = new AtomicBoolean();
        Future<Object> future = Future.future();
        future.setHandler(( result) -> {
            assertTrue(result.succeeded());
            assertFalse(result.failed());
            assertEquals(null, result.result());
            assertEquals(null, result.cause());
            called.set(true);
        });
        assertFalse(called.get());
        future.complete(null);
        assertTrue(called.get());
        called.set(false);
        Object foo = new Object();
        future = Future.future();
        future.setHandler(( result) -> {
            called.set(true);
            assertTrue(result.succeeded());
            assertFalse(result.failed());
            assertEquals(foo, result.result());
            assertEquals(null, result.cause());
        });
        assertFalse(called.get());
        future.complete(foo);
        assertTrue(called.get());
        called.set(false);
        Exception cause = new Exception();
        future = Future.future();
        future.setHandler(( result) -> {
            called.set(true);
            assertFalse(result.succeeded());
            assertTrue(result.failed());
            assertEquals(null, result.result());
            assertEquals(cause, result.cause());
        });
        assertFalse(called.get());
        future.fail(cause);
        assertTrue(called.get());
    }

    @Test
    public void testCallSetHandlerAfterCompletion() {
        AtomicBoolean called = new AtomicBoolean();
        Future<Object> future = Future.succeededFuture();
        future.setHandler(( result) -> {
            assertTrue(result.succeeded());
            assertFalse(result.failed());
            assertEquals(null, result.result());
            assertEquals(null, result.cause());
            called.set(true);
        });
        assertTrue(called.get());
        called.set(false);
        Object foo = new Object();
        future = Future.succeededFuture(foo);
        future.setHandler(( result) -> {
            assertTrue(result.succeeded());
            assertFalse(result.failed());
            assertEquals(foo, result.result());
            assertEquals(null, result.cause());
            called.set(true);
        });
        assertTrue(called.get());
        called.set(false);
        Exception cause = new Exception();
        future = Future.failedFuture(cause);
        future.setHandler(( result) -> {
            assertFalse(result.succeeded());
            assertTrue(result.failed());
            assertEquals(null, result.result());
            assertEquals(cause, result.cause());
            called.set(true);
        });
        assertTrue(called.get());
    }

    @Test
    public void testResolveFutureToHandler() {
        Consumer<Handler<AsyncResult<String>>> consumer = ( handler) -> {
            handler.handle(Future.succeededFuture("the-result"));
        };
        Future<String> fut = Future.future();
        consumer.accept(fut);
        assertTrue(fut.isComplete());
        assertTrue(fut.succeeded());
        assertEquals("the-result", fut.result());
    }

    @Test
    public void testFailFutureToHandler() {
        Throwable cause = new Throwable();
        Consumer<Handler<AsyncResult<String>>> consumer = ( handler) -> {
            handler.handle(Future.failedFuture(cause));
        };
        Future<String> fut = Future.future();
        consumer.accept(fut);
        assertTrue(fut.isComplete());
        assertTrue(fut.failed());
        assertEquals(cause, fut.cause());
    }

    @Test
    public void testCreateFailedWithNullFailure() {
        Future<String> future = Future.failedFuture(((Throwable) (null)));
        FutureTest.Checker<String> checker = new FutureTest.Checker(future);
        NoStackTraceThrowable failure = ((NoStackTraceThrowable) (checker.assertFailed()));
        assertNull(failure.getMessage());
    }

    @Test
    public void testFailureFutureWithNullFailure() {
        Future<String> future = Future.future();
        future.fail(((Throwable) (null)));
        FutureTest.Checker<String> checker = new FutureTest.Checker(future);
        NoStackTraceThrowable failure = ((NoStackTraceThrowable) (checker.assertFailed()));
        assertNull(failure.getMessage());
    }

    @Test
    public void testCompositeComplete() {
        CompositeFuture composite = CompositeFuture.all(Future.future(), Future.future());
        FutureTest.Checker<CompositeFuture> checker = new FutureTest.Checker(composite);
        composite.complete(composite);
        checker.assertSucceeded(composite);
        composite = CompositeFuture.all(Future.future(), Future.future());
        checker = new FutureTest.Checker(composite);
        composite.complete();
        checker.assertSucceeded(composite);
    }

    @Test
    public void testCompositeFail() {
        Throwable cause = new Throwable();
        Future<Object> f1 = Future.future();
        Future<Object> f2 = Future.future();
        CompositeFuture composite = CompositeFuture.all(f1, f2);
        FutureTest.Checker<CompositeFuture> checker = new FutureTest.Checker(composite);
        composite.fail(cause);
        checker.assertFailed(cause);
        f1.complete();
        f2.complete();
        checker.assertFailed(cause);
    }

    @Test
    public void testAllSucceeded() {
        testAllSucceeded(CompositeFuture::all);
    }

    @Test
    public void testAllSucceededWithList() {
        testAllSucceeded(( f1, f2) -> CompositeFuture.all(Arrays.asList(f1, f2)));
    }

    @Test
    public void testAllWithEmptyList() {
        CompositeFuture composite = CompositeFuture.all(Collections.emptyList());
        assertTrue(composite.isComplete());
    }

    @Test
    public void testAllFailed() {
        testAllFailed(CompositeFuture::all);
    }

    @Test
    public void testAllFailedWithList() {
        testAllFailed(( f1, f2) -> CompositeFuture.all(Arrays.asList(f1, f2)));
    }

    @Test
    public void testAllLargeList() {
        testAllLargeList(63);
        testAllLargeList(64);
        testAllLargeList(65);
        testAllLargeList(100);
    }

    @Test
    public void testAnySucceeded1() {
        testAnySucceeded1(CompositeFuture::any);
    }

    @Test
    public void testAnySucceeded1WithList() {
        testAnySucceeded1(( f1, f2) -> CompositeFuture.any(Arrays.asList(f1, f2)));
    }

    @Test
    public void testAnyWithEmptyList() {
        CompositeFuture composite = CompositeFuture.any(Collections.emptyList());
        assertTrue(composite.isComplete());
    }

    @Test
    public void testAnySucceeded2() {
        testAnySucceeded2(CompositeFuture::any);
    }

    @Test
    public void testAnySucceeded2WithList() {
        testAnySucceeded2(CompositeFuture::any);
    }

    @Test
    public void testAnyFailed() {
        testAnyFailed(CompositeFuture::any);
    }

    @Test
    public void testAnyFailedWithList() {
        testAnyFailed(( f1, f2) -> CompositeFuture.any(Arrays.asList(f1, f2)));
    }

    @Test
    public void testAnyLargeList() {
        testAnyLargeList(63);
        testAnyLargeList(64);
        testAnyLargeList(65);
        testAnyLargeList(100);
    }

    @Test
    public void testJoinSucceeded() {
        testJoinSucceeded(CompositeFuture::join);
    }

    @Test
    public void testJoinSucceededWithList() {
        testJoinSucceeded(( f1, f2) -> CompositeFuture.join(Arrays.asList(f1, f2)));
    }

    @Test
    public void testJoinFailed1() {
        testJoinFailed1(CompositeFuture::join);
    }

    @Test
    public void testJoinFailed1WithList() {
        testJoinFailed1(( f1, f2) -> CompositeFuture.join(Arrays.asList(f1, f2)));
    }

    @Test
    public void testJoinFailed2() {
        testJoinFailed2(CompositeFuture::join);
    }

    @Test
    public void testJoinFailed2WithList() {
        testJoinFailed2(( f1, f2) -> CompositeFuture.join(Arrays.asList(f1, f2)));
    }

    @Test
    public void testJoinFailed3() {
        testJoinFailed3(CompositeFuture::join);
    }

    @Test
    public void testJoinFailed3WithList() {
        testJoinFailed3(( f1, f2) -> CompositeFuture.join(Arrays.asList(f1, f2)));
    }

    @Test
    public void testJoinWithEmptyList() {
        CompositeFuture composite = CompositeFuture.join(Collections.emptyList());
        assertTrue(composite.isComplete());
    }

    @Test
    public void testCompositeFutureToList() {
        Future<String> f1 = Future.future();
        Future<Integer> f2 = Future.future();
        CompositeFuture composite = CompositeFuture.all(f1, f2);
        assertEquals(Arrays.asList(null, null), composite.list());
        f1.complete("foo");
        assertEquals(Arrays.asList("foo", null), composite.list());
        f2.complete(4);
        assertEquals(Arrays.asList("foo", 4), composite.list());
    }

    @Test
    public void testComposeSuccessToSuccess() {
        Future<String> f1 = Future.future();
        Future<Integer> f2 = Future.future();
        FutureTest.Checker<Integer> checker = new FutureTest.Checker(f2);
        f1.compose(( string) -> f2.complete(string.length()), f2);
        checker.assertNotCompleted();
        f1.complete("abcdef");
        checker.assertSucceeded(6);
        AtomicReference<String> ref = new AtomicReference<>();
        Future<Integer> c = Future.future();
        Future<String> f3 = Future.future();
        Future<Integer> f4 = f3.compose(( string) -> {
            ref.set(string);
            return c;
        });
        checker = new FutureTest.Checker(f4);
        f3.complete("abcdef");
        checker.assertNotCompleted();
        assertEquals("abcdef", ref.get());
        c.complete(6);
        checker.assertSucceeded(6);
    }

    @Test
    public void testComposeSuccessToFailure() {
        Throwable cause = new Throwable();
        AtomicReference<String> ref = new AtomicReference<>();
        Future<Integer> c = Future.future();
        Future<String> f3 = Future.future();
        Future<Integer> f4 = f3.compose(( string) -> {
            ref.set(string);
            return c;
        });
        FutureTest.Checker<Integer> checker = new FutureTest.Checker(f4);
        f3.complete("abcdef");
        c.fail(cause);
        checker.assertFailed(cause);
    }

    @Test
    public void testComposeFailure() {
        Exception cause = new Exception();
        Future<String> f1 = Future.future();
        Future<Integer> f2 = Future.future();
        FutureTest.Checker<Integer> checker = new FutureTest.Checker(f2);
        f1.compose(( string) -> f2.complete(string.length()), f2);
        f1.fail(cause);
        checker.assertFailed(cause);
        Future<String> f3 = Future.future();
        Future<Integer> f4 = f3.compose(( string) -> Future.succeededFuture(string.length()));
        checker = new FutureTest.Checker(f4);
        f3.fail(cause);
        checker.assertFailed(cause);
    }

    @Test
    public void testComposeFails() {
        RuntimeException cause = new RuntimeException();
        Future<String> f1 = Future.future();
        Future<Integer> f2 = Future.future();
        FutureTest.Checker<Integer> checker = new FutureTest.Checker(f2);
        f1.compose(( string) -> {
            throw cause;
        }, f2);
        f1.complete("foo");
        checker.assertFailed(cause);
        Future<String> f3 = Future.future();
        Future<Integer> f4 = f3.compose(( string) -> {
            throw cause;
        });
        checker = new FutureTest.Checker(f4);
        f3.complete("foo");
        checker.assertFailed(cause);
    }

    @Test
    public void testComposeFailsAfterCompletion() {
        Future<String> f1 = Future.future();
        Future<Integer> f2 = Future.future();
        FutureTest.Checker<Integer> checker = new FutureTest.Checker(f2);
        RuntimeException cause = new RuntimeException();
        f1.compose(( string) -> {
            f2.complete(46);
            throw cause;
        }, f2);
        try {
            f1.complete("foo");
            fail();
        } catch (Exception e) {
            assertEquals(cause, e);
        }
        checker.assertSucceeded(46);
    }

    @Test
    public void testComposeWithNullFunction() {
        Future<Integer> fut = Future.future();
        try {
            fut.compose(((Function<Integer, Future<Integer>>) (null)));
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void testMapSuccess() {
        Future<Integer> fut = Future.future();
        Future<String> mapped = fut.map(Object::toString);
        FutureTest.Checker<String> checker = new FutureTest.Checker(mapped);
        checker.assertNotCompleted();
        fut.complete(3);
        checker.assertSucceeded("3");
    }

    @Test
    public void testMapFailure() {
        Throwable cause = new Throwable();
        Future<Integer> fut = Future.future();
        Future<String> mapped = fut.map(Object::toString);
        FutureTest.Checker<String> checker = new FutureTest.Checker(mapped);
        checker.assertNotCompleted();
        fut.fail(cause);
        checker.assertFailed(cause);
    }

    @Test
    public void testMapFails() {
        RuntimeException cause = new RuntimeException();
        Future<Integer> fut = Future.future();
        Future<Object> mapped = fut.map(( i) -> {
            throw cause;
        });
        FutureTest.Checker<Object> checker = new FutureTest.Checker(mapped);
        fut.fail(cause);
        checker.assertFailed(cause);
    }

    @Test
    public void testMapWithNullFunction() {
        Future<Integer> fut = Future.future();
        try {
            fut.map(((Function<Integer, String>) (null)));
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            asyncResult(fut).map(((Function<Integer, String>) (null)));
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void testMapEmpty() {
        Future<Integer> fut = Future.future();
        Future<String> mapped = fut.mapEmpty();
        FutureTest.Checker<String> checker = new FutureTest.Checker(mapped);
        checker.assertNotCompleted();
        fut.complete(3);
        checker.assertSucceeded(null);
    }

    @Test
    public void testRecoverSuccessWithSuccess() {
        AtomicBoolean called = new AtomicBoolean();
        Future<String> f = Future.future();
        Future<String> r = f.recover(( t) -> {
            called.set(true);
            throw new AssertionError();
        });
        FutureTest.Checker<String> checker = new FutureTest.Checker(r);
        checker.assertNotCompleted();
        f.complete("yeah");
        assertTrue(r.succeeded());
        checker.assertSucceeded("yeah");
        assertFalse(called.get());
    }

    @Test
    public void testRecoverFailureWithSuccess() {
        Future<String> f = Future.future();
        Future<String> r = f.recover(( t) -> Future.succeededFuture(t.getMessage()));
        FutureTest.Checker<String> checker = new FutureTest.Checker(r);
        checker.assertNotCompleted();
        f.fail("recovered");
        checker.assertSucceeded("recovered");
    }

    @Test
    public void testRecoverFailureWithFailure() {
        Throwable cause = new Throwable();
        Future<String> f = Future.future();
        Future<String> r = f.recover(( t) -> Future.failedFuture(cause));
        FutureTest.Checker<String> checker = new FutureTest.Checker(r);
        checker.assertNotCompleted();
        f.fail("recovered");
        checker.assertFailed(cause);
    }

    @Test
    public void testRecoverFailureFails() {
        RuntimeException cause = new RuntimeException("throw");
        Future<String> f = Future.future();
        Future<String> r = f.recover(( t) -> {
            throw cause;
        });
        FutureTest.Checker<String> checker = new FutureTest.Checker(r);
        checker.assertNotCompleted();
        f.fail("recovered");
        checker.assertFailed(cause);
    }

    @Test
    public void testRecoverWithNullFunction() {
        Future<Integer> fut = Future.future();
        try {
            fut.recover(null);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void testOtherwiseSuccessWithSuccess() {
        AtomicBoolean called = new AtomicBoolean();
        Future<String> f = Future.future();
        Future<String> r = f.otherwise(( t) -> {
            called.set(true);
            throw new AssertionError();
        });
        FutureTest.Checker<String> checker = new FutureTest.Checker(r);
        checker.assertNotCompleted();
        f.complete("yeah");
        assertTrue(r.succeeded());
        checker.assertSucceeded("yeah");
        assertFalse(called.get());
    }

    @Test
    public void testOtherwiseFailureWithSuccess() {
        Future<String> f = Future.future();
        Future<String> r = f.otherwise(( t) -> t.getMessage());
        FutureTest.Checker<String> checker = new FutureTest.Checker(r);
        checker.assertNotCompleted();
        f.fail("recovered");
        checker.assertSucceeded("recovered");
    }

    @Test
    public void testOtherwiseFails() {
        RuntimeException cause = new RuntimeException("throw");
        Future<String> f = Future.future();
        Future<String> r = f.otherwise(( t) -> {
            throw cause;
        });
        FutureTest.Checker<String> checker = new FutureTest.Checker(r);
        checker.assertNotCompleted();
        f.fail("recovered");
        checker.assertFailed(cause);
    }

    @Test
    public void testDefaultCompleter() {
        AsyncResult<Object> succeededAsyncResult = new AsyncResult<Object>() {
            Object result = new Object();

            public Object result() {
                return result;
            }

            public Throwable cause() {
                throw new UnsupportedOperationException();
            }

            public boolean succeeded() {
                return true;
            }

            public boolean failed() {
                throw new UnsupportedOperationException();
            }

            public <U> AsyncResult<U> map(Function<Object, U> mapper) {
                throw new UnsupportedOperationException();
            }

            public <V> AsyncResult<V> map(V value) {
                throw new UnsupportedOperationException();
            }
        };
        AsyncResult<Object> failedAsyncResult = new AsyncResult<Object>() {
            Throwable cause = new Throwable();

            public Object result() {
                throw new UnsupportedOperationException();
            }

            public Throwable cause() {
                return cause;
            }

            public boolean succeeded() {
                return false;
            }

            public boolean failed() {
                throw new UnsupportedOperationException();
            }

            public <U> AsyncResult<U> map(Function<Object, U> mapper) {
                throw new UnsupportedOperationException();
            }

            public <V> AsyncResult<V> map(V value) {
                throw new UnsupportedOperationException();
            }
        };
        class DefaultCompleterTestFuture<T> implements Future<T> {
            boolean succeeded;

            boolean failed;

            T result;

            Throwable cause;

            public boolean isComplete() {
                throw new UnsupportedOperationException();
            }

            public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
                throw new UnsupportedOperationException();
            }

            public Handler<AsyncResult<T>> getHandler() {
                throw new UnsupportedOperationException();
            }

            public void complete(T result) {
                if (!(tryComplete(result))) {
                    throw new IllegalStateException();
                }
            }

            public void complete() {
                if (!(tryComplete())) {
                    throw new IllegalStateException();
                }
            }

            public void fail(Throwable cause) {
                if (!(tryFail(cause))) {
                    throw new IllegalStateException();
                }
            }

            public void fail(String failureMessage) {
                if (!(tryFail(failureMessage))) {
                    throw new IllegalStateException();
                }
            }

            public boolean tryComplete(T result) {
                if ((succeeded) || (failed)) {
                    return false;
                }
                succeeded = true;
                this.result = result;
                return true;
            }

            public boolean tryComplete() {
                throw new UnsupportedOperationException();
            }

            public boolean tryFail(Throwable cause) {
                if ((succeeded) || (failed)) {
                    return false;
                }
                failed = true;
                this.cause = cause;
                return true;
            }

            public boolean tryFail(String failureMessage) {
                throw new UnsupportedOperationException();
            }

            public T result() {
                throw new UnsupportedOperationException();
            }

            public Throwable cause() {
                throw new UnsupportedOperationException();
            }

            public boolean succeeded() {
                throw new UnsupportedOperationException();
            }

            public boolean failed() {
                throw new UnsupportedOperationException();
            }

            public void handle(AsyncResult<T> asyncResult) {
                if (asyncResult.succeeded()) {
                    complete(asyncResult.result());
                } else {
                    fail(asyncResult.cause());
                }
            }
        }
        DefaultCompleterTestFuture<Object> successFuture = new DefaultCompleterTestFuture<>();
        successFuture.handle(succeededAsyncResult);
        assertTrue(successFuture.succeeded);
        assertEquals(succeededAsyncResult.result(), successFuture.result);
        DefaultCompleterTestFuture<Object> failureFuture = new DefaultCompleterTestFuture<>();
        failureFuture.handle(failedAsyncResult);
        assertTrue(failureFuture.failed);
        assertEquals(failedAsyncResult.cause(), failureFuture.cause);
    }

    class Checker<T> {
        private final Future<T> future;

        private final AtomicReference<AsyncResult<T>> result = new AtomicReference<>();

        private final AtomicInteger count = new AtomicInteger();

        Checker(Future<T> future) {
            future.setHandler(( ar) -> {
                count.incrementAndGet();
                result.set(ar);
            });
            this.future = future;
        }

        void assertNotCompleted() {
            assertFalse(future.isComplete());
            assertFalse(future.succeeded());
            assertFalse(future.failed());
            assertNull(future.cause());
            assertNull(future.result());
            assertEquals(0, count.get());
            assertNull(result.get());
        }

        void assertSucceeded(T expected) {
            assertTrue(future.isComplete());
            assertTrue(future.succeeded());
            assertFalse(future.failed());
            assertNull(future.cause());
            assertEquals(expected, future.result());
            assertEquals(1, count.get());
            AsyncResult<T> ar = result.get();
            assertNotNull(ar);
            assertTrue(ar.succeeded());
            assertFalse(ar.failed());
            assertNull(ar.cause());
            assertEquals(expected, future.result());
        }

        void assertFailed(Throwable expected) {
            assertEquals(expected, assertFailed());
        }

        Throwable assertFailed() {
            assertTrue(future.isComplete());
            assertFalse(future.succeeded());
            assertTrue(future.failed());
            assertEquals(null, future.result());
            assertEquals(1, count.get());
            AsyncResult<T> ar = result.get();
            assertNotNull(ar);
            assertFalse(ar.succeeded());
            assertTrue(ar.failed());
            assertNull(ar.result());
            return future.cause();
        }
    }

    @Test
    public void testUncompletedAsyncResultMap() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        AsyncResult<Integer> map1 = res.map(String::length);
        AsyncResult<Integer> map2 = res.map(17);
        assertNull(map1.result());
        assertNull(map1.cause());
        assertNull(map2.result());
        assertNull(map2.cause());
    }

    @Test
    public void testSucceededAsyncResultMap() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        AsyncResult<Integer> map1 = res.map(String::length);
        AsyncResult<Integer> map2 = res.map(17);
        f.complete("foobar");
        assertEquals(6, ((int) (map1.result())));
        assertNull(map1.cause());
        assertEquals(17, ((int) (map2.result())));
        assertNull(map2.cause());
    }

    @Test
    public void testFailedAsyncResultMap() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        AsyncResult<Integer> map1 = res.map(String::length);
        AsyncResult<Integer> map2 = res.map(17);
        Throwable cause = new Throwable();
        f.fail(cause);
        assertNull(map1.result());
        assertSame(cause, map1.cause());
        assertNull(map2.result());
        assertSame(cause, map2.cause());
    }

    @Test
    public void testAsyncResultMapEmpty() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        AsyncResult<Integer> map = res.mapEmpty();
        f.complete("foobar");
        assertNull(null, map.result());
        assertNull(map.cause());
    }

    @Test
    public void testSucceededFutureRecover() {
        Future<String> f = Future.future();
        Future<String> r = f.recover(( t) -> Future.succeededFuture(t.getMessage()));
        f.complete("yeah");
        assertTrue(r.succeeded());
        assertEquals(r.result(), "yeah");
    }

    @Test
    public void testFailedFutureRecover() {
        Future<String> f = Future.future();
        Future<String> r = f.recover(( t) -> Future.succeededFuture(t.getMessage()));
        f.fail("recovered");
        assertTrue(r.succeeded());
        assertEquals(r.result(), "recovered");
    }

    @Test
    public void testFailedMapperFutureRecover() {
        Future<String> f = Future.future();
        Future<String> r = f.recover(( t) -> {
            throw new RuntimeException("throw");
        });
        f.fail("recovered");
        assertTrue(r.failed());
        assertEquals(r.cause().getMessage(), "throw");
    }

    @Test
    public void testUncompletedAsyncResultOtherwise() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        testUncompletedAsyncResultOtherwise(res);
    }

    @Test
    public void testUncompletedFutureOtherwise() {
        Future<String> f = Future.future();
        testUncompletedAsyncResultOtherwise(f);
    }

    @Test
    public void testUncompletedAsyncResultOtherwiseApplyFunction() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        testUncompletedOtherwiseApplyFunction(res);
    }

    @Test
    public void testUncompletedFutureOtherwiseApplyFunction() {
        Future<String> f = Future.future();
        testUncompletedOtherwiseApplyFunction(f);
    }

    @Test
    public void testSucceededAsyncResultOtherwise() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        testSucceededOtherwise(res, f);
    }

    @Test
    public void testSucceededFutureOtherwise() {
        Future<String> f = Future.future();
        testSucceededOtherwise(f, f);
    }

    @Test
    public void testSucceededAsyncResultOtherwiseApplyFunction() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        testSucceededOtherwiseApplyFunction(res, f);
    }

    @Test
    public void testSucceededFutureOtherwiseApplyFunction() {
        Future<String> f = Future.future();
        testSucceededOtherwiseApplyFunction(f, f);
    }

    @Test
    public void testFailedAsyncResultOtherwise() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        testFailedOtherwise(res, f);
    }

    @Test
    public void testFailedFutureOtherwise() {
        Future<String> f = Future.future();
        testFailedOtherwise(f, f);
    }

    @Test
    public void testFailedAsyncResultOtherwiseApplyFunction() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        testFailedOtherwiseApplyFunction(res, f);
    }

    @Test
    public void testFailedFutureOtherwiseApplyFunction() {
        Future<String> f = Future.future();
        testFailedOtherwiseApplyFunction(f, f);
    }

    @Test
    public void testOtherwiseWithNullFunction() {
        Future<Integer> fut = Future.future();
        try {
            fut.otherwise(((Function<Throwable, Integer>) (null)));
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            asyncResult(fut).otherwise(((Function<Throwable, Integer>) (null)));
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void testAsyncResultOtherwiseEmpty() {
        Future<String> f = Future.future();
        AsyncResult<String> res = asyncResult(f);
        testOtherwiseEmpty(res, f);
    }

    @Test
    public void testFutureOtherwiseEmpty() {
        Future<String> f = Future.future();
        testOtherwiseEmpty(f, f);
    }

    @Test
    public void testToString() {
        assertEquals("Future{unresolved}", Future.future().toString());
        assertEquals("Future{result=abc}", Future.succeededFuture("abc").toString());
        assertEquals("Future{cause=It's like that, and that's the way it is}", Future.failedFuture("It's like that, and that's the way it is").toString());
        Future<String> f = Future.future();
        f.complete("abc");
        assertEquals("Future{result=abc}", f.toString());
        f = Future.future();
        f.fail("abc");
        assertEquals("Future{cause=abc}", f.toString());
    }

    @Test
    public void testReleaseHandlerAfterCompletion() throws Exception {
        Future<String> f = Future.future();
        Field handlerField = f.getClass().getDeclaredField("handler");
        handlerField.setAccessible(true);
        f.setHandler(( ar) -> {
        });
        f.complete();
        assertNull(handlerField.get(f));
        f.setHandler(( ar) -> {
        });
        assertNull(handlerField.get(f));
        f = Future.future();
        f.setHandler(( ar) -> {
        });
        f.fail("abc");
        assertNull(handlerField.get(f));
        f.setHandler(( ar) -> {
        });
        assertNull(handlerField.get(f));
    }
}

