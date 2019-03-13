/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.subscribers;


import EmptySubscription.INSTANCE;
import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import TestSubscriber.EmptySubscriber;
import TestWaitStrategy.SLEEP_1000MS;
import TestWaitStrategy.SLEEP_1MS;
import io.reactivex.Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class TestSubscriberTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAssert() {
        Flowable<Integer> oi = Flowable.fromIterable(Arrays.asList(1, 2));
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        oi.subscribe(ts);
        ts.assertValues(1, 2);
        ts.assertValueCount(2);
        ts.assertTerminated();
    }

    @Test
    public void testAssertNotMatchCount() {
        Flowable<Integer> oi = Flowable.fromIterable(Arrays.asList(1, 2));
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        oi.subscribe(ts);
        thrown.expect(AssertionError.class);
        // FIXME different message pattern
        // thrown.expectMessage("Number of items does not match. Provided: 1  Actual: 2");
        ts.assertValues(1);
        ts.assertValueCount(2);
        ts.assertTerminated();
    }

    @Test
    public void testAssertNotMatchValue() {
        Flowable<Integer> oi = Flowable.fromIterable(Arrays.asList(1, 2));
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        oi.subscribe(ts);
        thrown.expect(AssertionError.class);
        // FIXME different message pattern
        // thrown.expectMessage("Value at index: 1 expected to be [3] (Integer) but was: [2] (Integer)");
        ts.assertValues(1, 3);
        ts.assertValueCount(2);
        ts.assertTerminated();
    }

    @Test
    public void assertNeverAtNotMatchingValue() {
        Flowable<Integer> oi = Flowable.fromIterable(Arrays.asList(1, 2));
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        oi.subscribe(ts);
        ts.assertNever(3);
        ts.assertValueCount(2);
        ts.assertTerminated();
    }

    @Test
    public void assertNeverAtMatchingValue() {
        Flowable<Integer> oi = Flowable.fromIterable(Arrays.asList(1, 2));
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        oi.subscribe(ts);
        ts.assertValues(1, 2);
        thrown.expect(AssertionError.class);
        ts.assertNever(2);
        ts.assertValueCount(2);
        ts.assertTerminated();
    }

    @Test
    public void assertNeverAtMatchingPredicate() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1, 2).subscribe(ts);
        ts.assertValues(1, 2);
        thrown.expect(AssertionError.class);
        ts.assertNever(new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o == 1;
            }
        });
    }

    @Test
    public void assertNeverAtNotMatchingPredicate() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(2, 3).subscribe(ts);
        ts.assertNever(new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o == 1;
            }
        });
    }

    @Test
    public void testAssertTerminalEventNotReceived() {
        PublishProcessor<Integer> p = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        p.subscribe(ts);
        p.onNext(1);
        p.onNext(2);
        thrown.expect(AssertionError.class);
        // FIXME different message pattern
        // thrown.expectMessage("No terminal events received.");
        ts.assertValues(1, 2);
        ts.assertValueCount(2);
        ts.assertTerminated();
    }

    @Test
    public void testWrappingMock() {
        Flowable<Integer> oi = Flowable.fromIterable(Arrays.asList(1, 2));
        Subscriber<Integer> mockSubscriber = TestHelper.mockSubscriber();
        oi.subscribe(new TestSubscriber<Integer>(mockSubscriber));
        InOrder inOrder = Mockito.inOrder(mockSubscriber);
        inOrder.verify(mockSubscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(mockSubscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(mockSubscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testWrappingMockWhenUnsubscribeInvolved() {
        Flowable<Integer> oi = Flowable.fromIterable(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)).take(2);
        Subscriber<Integer> mockSubscriber = TestHelper.mockSubscriber();
        oi.subscribe(new TestSubscriber<Integer>(mockSubscriber));
        InOrder inOrder = Mockito.inOrder(mockSubscriber);
        inOrder.verify(mockSubscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(mockSubscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(mockSubscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAssertError() {
        RuntimeException e = new RuntimeException("Oops");
        TestSubscriber<Object> subscriber = new TestSubscriber<Object>();
        Flowable.error(e).subscribe(subscriber);
        subscriber.assertError(e);
    }

    @Test
    public void testAwaitTerminalEventWithDuration() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Flowable.just(1).subscribe(ts);
        ts.awaitTerminalEvent(1, TimeUnit.SECONDS);
        ts.assertTerminated();
    }

    @Test
    public void testAwaitTerminalEventWithDurationAndUnsubscribeOnTimeout() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        final AtomicBoolean unsub = new AtomicBoolean(false);
        // 
        // 
        Flowable.just(1).doOnCancel(new Action() {
            @Override
            public void run() {
                unsub.set(true);
            }
        }).delay(1000, TimeUnit.MILLISECONDS).subscribe(ts);
        ts.awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
        ts.dispose();
        Assert.assertTrue(unsub.get());
    }

    @Test(expected = NullPointerException.class)
    public void testNullDelegate1() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(null);
        ts.onComplete();
    }

    @Test(expected = NullPointerException.class)
    public void testNullDelegate2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(null);
        ts.onComplete();
    }

    @Test(expected = NullPointerException.class)
    public void testNullDelegate3() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(null, 0L);
        ts.onComplete();
    }

    @Test
    public void testDelegate1() {
        TestSubscriber<Integer> ts0 = new TestSubscriber<Integer>();
        ts0.onSubscribe(INSTANCE);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(ts0);
        ts.onComplete();
        ts0.assertTerminated();
    }

    @Test
    public void testDelegate2() {
        TestSubscriber<Integer> ts1 = new TestSubscriber<Integer>();
        TestSubscriber<Integer> ts2 = new TestSubscriber<Integer>(ts1);
        ts2.onComplete();
        ts1.assertComplete();
    }

    @Test
    public void testDelegate3() {
        TestSubscriber<Integer> ts1 = new TestSubscriber<Integer>();
        TestSubscriber<Integer> ts2 = new TestSubscriber<Integer>(ts1, 0L);
        ts2.onComplete();
        ts1.assertComplete();
    }

    @Test
    public void testUnsubscribed() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Assert.assertFalse(ts.isCancelled());
    }

    @Test
    public void testNoErrors() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onError(new TestException());
        try {
            ts.assertNoErrors();
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Error present but no assertion error!");
    }

    @Test
    public void testNotCompleted() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        try {
            ts.assertComplete();
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Not completed and no assertion error!");
    }

    @Test
    public void testMultipleCompletions() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onComplete();
        ts.onComplete();
        try {
            ts.assertComplete();
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Multiple completions and no assertion error!");
    }

    @Test
    public void testCompleted() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onComplete();
        try {
            ts.assertNotComplete();
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Completed and no assertion error!");
    }

    @Test
    public void testMultipleCompletions2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onComplete();
        ts.onComplete();
        try {
            ts.assertNotComplete();
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Multiple completions and no assertion error!");
    }

    @Test
    public void testMultipleErrors() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onSubscribe(INSTANCE);
        ts.onError(new TestException());
        ts.onError(new TestException());
        try {
            ts.assertNoErrors();
        } catch (AssertionError ex) {
            Throwable e = ex.getCause();
            if (!(e instanceof CompositeException)) {
                Assert.fail("Multiple Error present but the reported error doesn't have a composite cause!");
            }
            CompositeException ce = ((CompositeException) (e));
            if ((ce.size()) != 2) {
                ce.printStackTrace();
            }
            Assert.assertEquals(2, ce.size());
            // expected
            return;
        }
        Assert.fail("Multiple Error present but no assertion error!");
    }

    @Test
    public void testMultipleErrors2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onSubscribe(INSTANCE);
        ts.onError(new TestException());
        ts.onError(new TestException());
        try {
            ts.assertError(TestException.class);
        } catch (AssertionError ex) {
            Throwable e = ex.getCause();
            if (!(e instanceof CompositeException)) {
                Assert.fail("Multiple Error present but the reported error doesn't have a composite cause!");
            }
            CompositeException ce = ((CompositeException) (e));
            Assert.assertEquals(2, ce.size());
            // expected
            return;
        }
        Assert.fail("Multiple Error present but no assertion error!");
    }

    @Test
    public void testMultipleErrors3() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onSubscribe(INSTANCE);
        ts.onError(new TestException());
        ts.onError(new TestException());
        try {
            ts.assertError(new TestException());
        } catch (AssertionError ex) {
            Throwable e = ex.getCause();
            if (!(e instanceof CompositeException)) {
                Assert.fail("Multiple Error present but the reported error doesn't have a composite cause!");
            }
            CompositeException ce = ((CompositeException) (e));
            Assert.assertEquals(2, ce.size());
            // expected
            return;
        }
        Assert.fail("Multiple Error present but no assertion error!");
    }

    @Test
    public void testMultipleErrors4() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onSubscribe(INSTANCE);
        ts.onError(new TestException());
        ts.onError(new TestException());
        try {
            ts.assertError(Functions.<Throwable>alwaysTrue());
        } catch (AssertionError ex) {
            Throwable e = ex.getCause();
            if (!(e instanceof CompositeException)) {
                Assert.fail("Multiple Error present but the reported error doesn't have a composite cause!");
            }
            CompositeException ce = ((CompositeException) (e));
            Assert.assertEquals(2, ce.size());
            // expected
            return;
        }
        Assert.fail("Multiple Error present but no assertion error!");
    }

    @Test
    public void testDifferentError() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onError(new TestException());
        try {
            ts.assertError(new TestException());
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Different Error present but no assertion error!");
    }

    @Test
    public void testDifferentError2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onError(new RuntimeException());
        try {
            ts.assertError(new TestException());
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Different Error present but no assertion error!");
    }

    @Test
    public void testDifferentError3() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onError(new RuntimeException());
        try {
            ts.assertError(TestException.class);
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Different Error present but no assertion error!");
    }

    @Test
    public void testDifferentError4() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onError(new RuntimeException());
        try {
            ts.assertError(Functions.<Throwable>alwaysFalse());
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("Different Error present but no assertion error!");
    }

    @Test
    public void testErrorInPredicate() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onError(new RuntimeException());
        try {
            ts.assertError(new Predicate<Throwable>() {
                @Override
                public boolean test(Throwable throwable) throws Exception {
                    throw new TestException();
                }
            });
        } catch (TestException ex) {
            // expected
            return;
        }
        Assert.fail("Error in predicate but not thrown!");
    }

    @Test
    public void testNoError() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        try {
            ts.assertError(TestException.class);
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("No present but no assertion error!");
    }

    @Test
    public void testNoError2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        try {
            ts.assertError(new TestException());
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("No present but no assertion error!");
    }

    @Test
    public void testNoError3() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        try {
            ts.assertError(Functions.<Throwable>alwaysTrue());
        } catch (AssertionError ex) {
            // expected
            return;
        }
        Assert.fail("No present but no assertion error!");
    }

    @Test
    public void testInterruptTerminalEventAwait() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final Thread t0 = Thread.currentThread();
        Worker w = Schedulers.computation().createWorker();
        try {
            w.schedule(new Runnable() {
                @Override
                public void run() {
                    t0.interrupt();
                }
            }, 200, TimeUnit.MILLISECONDS);
            try {
                if (ts.awaitTerminalEvent()) {
                    Assert.fail("Did not interrupt wait!");
                }
            } catch (RuntimeException ex) {
                if (!((ex.getCause()) instanceof InterruptedException)) {
                    Assert.fail(("The cause is not InterruptedException! " + (ex.getCause())));
                }
            }
        } finally {
            w.dispose();
        }
    }

    @Test
    public void testInterruptTerminalEventAwaitTimed() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final Thread t0 = Thread.currentThread();
        Worker w = Schedulers.computation().createWorker();
        try {
            w.schedule(new Runnable() {
                @Override
                public void run() {
                    t0.interrupt();
                }
            }, 200, TimeUnit.MILLISECONDS);
            try {
                if (ts.awaitTerminalEvent(5, TimeUnit.SECONDS)) {
                    Assert.fail("Did not interrupt wait!");
                }
            } catch (RuntimeException ex) {
                if (!((ex.getCause()) instanceof InterruptedException)) {
                    Assert.fail(("The cause is not InterruptedException! " + (ex.getCause())));
                }
            }
        } finally {
            Thread.interrupted();// clear interrupted flag

            w.dispose();
        }
    }

    @Test
    public void testInterruptTerminalEventAwaitAndUnsubscribe() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final Thread t0 = Thread.currentThread();
        Worker w = Schedulers.computation().createWorker();
        try {
            w.schedule(new Runnable() {
                @Override
                public void run() {
                    t0.interrupt();
                }
            }, 200, TimeUnit.MILLISECONDS);
            ts.awaitTerminalEvent(5, TimeUnit.SECONDS);
            ts.dispose();
            if (!(ts.isCancelled())) {
                Assert.fail("Did not unsubscribe!");
            }
        } finally {
            w.dispose();
        }
    }

    @Test
    public void testNoTerminalEventBut1Completed() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onComplete();
        try {
            ts.assertNotTerminated();
            throw new RuntimeException("Failed to report there were terminal event(s)!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void testNoTerminalEventBut1Error() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onError(new TestException());
        try {
            ts.assertNotTerminated();
            throw new RuntimeException("Failed to report there were terminal event(s)!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void testNoTerminalEventBut1Error1Completed() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onComplete();
        ts.onError(new TestException());
        try {
            ts.assertNotTerminated();
            throw new RuntimeException("Failed to report there were terminal event(s)!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void testNoTerminalEventBut2Errors() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onSubscribe(INSTANCE);
        ts.onError(new TestException());
        ts.onError(new TestException());
        try {
            ts.assertNotTerminated();
            throw new RuntimeException("Failed to report there were terminal event(s)!");
        } catch (AssertionError ex) {
            // expected
            Throwable e = ex.getCause();
            if (!(e instanceof CompositeException)) {
                Assert.fail("Multiple Error present but the reported error doesn't have a composite cause!");
            }
            CompositeException ce = ((CompositeException) (e));
            Assert.assertEquals(2, ce.size());
        }
    }

    @Test
    public void testNoValues() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onNext(1);
        try {
            ts.assertNoValues();
            throw new RuntimeException("Failed to report there were values!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void testValueCount() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onNext(1);
        ts.onNext(2);
        try {
            ts.assertValueCount(3);
            throw new RuntimeException("Failed to report there were values!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test(timeout = 1000)
    public void testOnCompletedCrashCountsDownLatch() {
        TestSubscriber<Integer> ts0 = new TestSubscriber<Integer>() {
            @Override
            public void onComplete() {
                throw new TestException();
            }
        };
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(ts0);
        try {
            ts.onComplete();
        } catch (TestException ex) {
            // expected
        }
        ts.awaitTerminalEvent();
    }

    @Test(timeout = 1000)
    public void testOnErrorCrashCountsDownLatch() {
        TestSubscriber<Integer> ts0 = new TestSubscriber<Integer>() {
            @Override
            public void onError(Throwable e) {
                throw new TestException();
            }
        };
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(ts0);
        try {
            ts.onError(new RuntimeException());
        } catch (TestException ex) {
            // expected
        }
        ts.awaitTerminalEvent();
    }

    @Test
    public void createDelegate() {
        TestSubscriber<Integer> ts1 = TestSubscriber.create();
        TestSubscriber<Integer> ts = TestSubscriber.create(ts1);
        ts.assertNotSubscribed();
        Assert.assertFalse(ts.hasSubscription());
        ts.onSubscribe(new BooleanSubscription());
        try {
            ts.assertNotSubscribed();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        Assert.assertTrue(ts.hasSubscription());
        Assert.assertFalse(ts.isDisposed());
        ts.onNext(1);
        ts.onError(new TestException());
        ts.onComplete();
        ts1.assertValue(1).assertError(TestException.class).assertComplete();
        ts.dispose();
        Assert.assertTrue(ts.isDisposed());
        Assert.assertTrue(ts.isTerminated());
        Assert.assertSame(Thread.currentThread(), ts.lastThread());
        try {
            ts.assertNoValues();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        try {
            ts.assertValueCount(0);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        ts.assertValueSequence(Collections.singletonList(1));
        try {
            ts.assertValueSequence(Collections.singletonList(2));
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        ts.assertValueSet(Collections.singleton(1));
        try {
            ts.assertValueSet(Collections.singleton(2));
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
    }

    @Test
    public void assertError() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        try {
            ts.assertError(TestException.class);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertError(new TestException());
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertErrorMessage("");
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        try {
            ts.assertError(Functions.<Throwable>alwaysTrue());
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertSubscribed();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        try {
            ts.assertTerminated();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        ts.onSubscribe(new BooleanSubscription());
        ts.assertSubscribed();
        ts.assertNoErrors();
        TestException ex = new TestException("Forced failure");
        ts.onError(ex);
        ts.assertError(ex);
        ts.assertError(TestException.class);
        ts.assertErrorMessage("Forced failure");
        ts.assertError(Functions.<Throwable>alwaysTrue());
        ts.assertError(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable t) {
                return ((t.getMessage()) != null) && (t.getMessage().contains("Forced"));
            }
        });
        try {
            ts.assertErrorMessage("");
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        try {
            ts.assertError(new RuntimeException());
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        try {
            ts.assertError(IOException.class);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        try {
            ts.assertNoErrors();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        try {
            ts.assertError(Functions.<Throwable>alwaysFalse());
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError exc) {
            // expected
        }
        ts.assertTerminated();
        ts.assertValueCount(0);
        ts.assertNoValues();
    }

    @Test
    public void emptyObserverEnum() {
        Assert.assertEquals(1, EmptySubscriber.values().length);
        Assert.assertNotNull(EmptySubscriber.valueOf("INSTANCE"));
    }

    @Test
    public void valueAndClass() {
        Assert.assertEquals("null", TestSubscriber.valueAndClass(null));
        Assert.assertEquals("1 (class: Integer)", TestSubscriber.valueAndClass(1));
    }

    @Test
    public void assertFailure() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onError(new TestException("Forced failure"));
        ts.assertFailure(TestException.class);
        ts.assertFailure(Functions.<Throwable>alwaysTrue());
        ts.assertFailureAndMessage(TestException.class, "Forced failure");
        ts.onNext(1);
        ts.assertFailure(TestException.class, 1);
        ts.assertFailure(Functions.<Throwable>alwaysTrue(), 1);
        ts.assertFailureAndMessage(TestException.class, "Forced failure", 1);
    }

    @Test
    public void assertFuseable() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertNotFuseable();
        try {
            ts.assertFuseable();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertFusionMode(SYNC);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        ts = TestSubscriber.create();
        ts.setInitialFusionMode(ANY);
        ts.onSubscribe(new ScalarSubscription<Integer>(ts, 1));
        ts.assertFuseable();
        ts.assertFusionMode(SYNC);
        try {
            ts.assertFusionMode(NONE);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertNotFuseable();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertTerminated() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.assertNotTerminated();
        ts.onError(null);
        try {
            ts.assertNotTerminated();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertOf() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.assertOf(new Consumer<TestSubscriber<Integer>>() {
            @Override
            public void accept(TestSubscriber<Integer> f) throws Exception {
                f.assertNotSubscribed();
            }
        });
        try {
            ts.assertOf(new Consumer<TestSubscriber<Integer>>() {
                @Override
                public void accept(TestSubscriber<Integer> f) throws Exception {
                    f.assertSubscribed();
                }
            });
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertOf(new Consumer<TestSubscriber<Integer>>() {
                @Override
                public void accept(TestSubscriber<Integer> f) throws Exception {
                    throw new IllegalArgumentException();
                }
            });
            throw new RuntimeException("Should have thrown");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void assertResult() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onComplete();
        ts.assertResult();
        try {
            ts.assertResult(1);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        ts.onNext(1);
        ts.assertResult(1);
        try {
            ts.assertResult(2);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertResult();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test(timeout = 5000)
    public void await() throws Exception {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        Assert.assertFalse(ts.await(100, TimeUnit.MILLISECONDS));
        ts.awaitDone(100, TimeUnit.MILLISECONDS);
        Assert.assertTrue(ts.isDisposed());
        Assert.assertFalse(ts.awaitTerminalEvent(100, TimeUnit.MILLISECONDS));
        Assert.assertEquals(0, ts.completions());
        Assert.assertEquals(0, ts.errorCount());
        ts.onComplete();
        Assert.assertTrue(ts.await(100, TimeUnit.MILLISECONDS));
        ts.await();
        ts.awaitDone(5, TimeUnit.SECONDS);
        Assert.assertEquals(1, ts.completions());
        Assert.assertEquals(0, ts.errorCount());
        Assert.assertTrue(ts.awaitTerminalEvent());
        final TestSubscriber<Integer> ts1 = TestSubscriber.create();
        ts1.onSubscribe(new BooleanSubscription());
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                ts1.onComplete();
            }
        }, 200, TimeUnit.MILLISECONDS);
        ts1.await();
        ts1.assertValueSet(Collections.<Integer>emptySet());
    }

    @Test
    public void errors() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        Assert.assertEquals(0, ts.errors().size());
        ts.onError(new TestException());
        Assert.assertEquals(1, ts.errors().size());
        TestHelper.assertError(ts.errors(), 0, TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void onNext() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        Assert.assertEquals(0, ts.valueCount());
        Assert.assertEquals(Collections.emptyList(), ts.values());
        ts.onNext(1);
        Assert.assertEquals(Collections.singletonList(1), ts.values());
        ts.cancel();
        Assert.assertTrue(ts.isCancelled());
        Assert.assertTrue(ts.isDisposed());
        ts.assertValue(1);
        Assert.assertEquals(Arrays.asList(Collections.singletonList(1), Collections.emptyList(), Collections.emptyList()), ts.getEvents());
        ts.onComplete();
        Assert.assertEquals(Arrays.asList(Collections.singletonList(1), Collections.emptyList(), Collections.singletonList(Notification.createOnComplete())), ts.getEvents());
    }

    @Test
    public void fusionModeToString() {
        Assert.assertEquals("NONE", TestSubscriber.fusionModeToString(NONE));
        Assert.assertEquals("SYNC", TestSubscriber.fusionModeToString(SYNC));
        Assert.assertEquals("ASYNC", TestSubscriber.fusionModeToString(ASYNC));
        Assert.assertEquals("Unknown(100)", TestSubscriber.fusionModeToString(100));
    }

    @Test
    public void multipleTerminals() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertNotComplete();
        ts.onComplete();
        try {
            ts.assertNotComplete();
            throw new RuntimeException("Should have thrown");
        } catch (Throwable ex) {
            // expected
        }
        ts.assertTerminated();
        ts.onComplete();
        try {
            ts.assertComplete();
            throw new RuntimeException("Should have thrown");
        } catch (Throwable ex) {
            // expected
        }
        try {
            ts.assertTerminated();
            throw new RuntimeException("Should have thrown");
        } catch (Throwable ex) {
            // expected
        }
        try {
            ts.assertNotComplete();
            throw new RuntimeException("Should have thrown");
        } catch (Throwable ex) {
            // expected
        }
    }

    @Test
    public void assertValue() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        try {
            ts.assertValue(1);
            throw new RuntimeException("Should have thrown");
        } catch (Throwable ex) {
            // expected
        }
        ts.onNext(1);
        ts.assertValue(1);
        try {
            ts.assertValue(2);
            throw new RuntimeException("Should have thrown");
        } catch (Throwable ex) {
            // expected
        }
        ts.onNext(2);
        try {
            ts.assertValue(1);
            throw new RuntimeException("Should have thrown");
        } catch (Throwable ex) {
            // expected
        }
    }

    @Test
    public void onNextMisbehave() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onNext(1);
        ts.assertError(IllegalStateException.class);
        ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onNext(null);
        ts.assertFailure(NullPointerException.class, ((Integer) (null)));
    }

    @Test
    public void awaitTerminalEventInterrupt() {
        final TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        Thread.currentThread().interrupt();
        ts.awaitTerminalEvent();
        Assert.assertTrue(Thread.interrupted());
        Thread.currentThread().interrupt();
        ts.awaitTerminalEvent(5, TimeUnit.SECONDS);
        Assert.assertTrue(Thread.interrupted());
    }

    @Test
    public void assertTerminated2() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        Assert.assertFalse(ts.isTerminated());
        ts.onError(new TestException());
        ts.onError(new IOException());
        Assert.assertTrue(ts.isTerminated());
        try {
            ts.assertTerminated();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertError(TestException.class);
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onError(new TestException());
        ts.onComplete();
        try {
            ts.assertTerminated();
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void onSubscribe() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(null);
        ts.assertError(NullPointerException.class);
        ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        BooleanSubscription bs1 = new BooleanSubscription();
        ts.onSubscribe(bs1);
        Assert.assertTrue(bs1.isCancelled());
        ts.assertError(IllegalStateException.class);
        ts = TestSubscriber.create();
        ts.dispose();
        bs1 = new BooleanSubscription();
        ts.onSubscribe(bs1);
        Assert.assertTrue(bs1.isCancelled());
    }

    @Test
    public void assertValueSequence() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onNext(1);
        ts.onNext(2);
        try {
            ts.assertValueSequence(Collections.<Integer>emptyList());
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertValueSequence(Collections.singletonList(1));
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
        ts.assertValueSequence(Arrays.asList(1, 2));
        try {
            ts.assertValueSequence(Arrays.asList(1, 2, 3));
            throw new RuntimeException("Should have thrown");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertEmpty() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        try {
            ts.assertEmpty();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
        ts.onSubscribe(new BooleanSubscription());
        ts.assertEmpty();
        ts.onNext(1);
        try {
            ts.assertEmpty();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void awaitDoneTimed() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Thread.currentThread().interrupt();
        try {
            ts.awaitDone(5, TimeUnit.SECONDS);
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof InterruptedException));
        }
    }

    @Test
    public void assertNotSubscribed() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.assertNotSubscribed();
        ts.errors().add(new TestException());
        try {
            ts.assertNotSubscribed();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertErrorMultiple() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        TestException e = new TestException();
        ts.errors().add(e);
        ts.errors().add(new TestException());
        try {
            ts.assertError(TestException.class);
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertError(e);
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
        try {
            ts.assertErrorMessage("");
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertComplete() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onSubscribe(new BooleanSubscription());
        try {
            ts.assertComplete();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
        ts.onComplete();
        ts.assertComplete();
        ts.onComplete();
        try {
            ts.assertComplete();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void completeWithoutOnSubscribe() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ts.onComplete();
        ts.assertError(IllegalStateException.class);
    }

    @Test
    public void completeDelegateThrows() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(new FlowableSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
            }

            @Override
            public void onNext(Integer value) {
            }

            @Override
            public void onError(Throwable e) {
                throw new TestException();
            }

            @Override
            public void onComplete() {
                throw new TestException();
            }
        });
        ts.onSubscribe(new BooleanSubscription());
        try {
            ts.onComplete();
            throw new RuntimeException("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertTrue(ts.isTerminated());
        }
    }

    @Test
    public void errorDelegateThrows() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(new FlowableSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
            }

            @Override
            public void onNext(Integer value) {
            }

            @Override
            public void onError(Throwable e) {
                throw new TestException();
            }

            @Override
            public void onComplete() {
                throw new TestException();
            }
        });
        ts.onSubscribe(new BooleanSubscription());
        try {
            ts.onError(new IOException());
            throw new RuntimeException("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertTrue(ts.isTerminated());
        }
    }

    @Test
    public void syncQueueThrows() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        ts.setInitialFusionMode(SYNC);
        Flowable.range(1, 5).map(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).subscribe(ts);
        ts.assertSubscribed().assertFuseable().assertFusionMode(SYNC).assertFailure(TestException.class);
    }

    @Test
    public void asyncQueueThrows() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        ts.setInitialFusionMode(ANY);
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.map(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).subscribe(ts);
        up.onNext(1);
        ts.assertSubscribed().assertFuseable().assertFusionMode(ASYNC).assertFailure(TestException.class);
    }

    @Test
    public void assertValuePredicateEmpty() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Flowable.empty().subscribe(ts);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("No values");
        ts.assertValue(new Predicate<Object>() {
            @Override
            public boolean test(final Object o) throws Exception {
                return false;
            }
        });
    }

    @Test
    public void assertValuePredicateMatch() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1).subscribe(ts);
        ts.assertValue(new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o == 1;
            }
        });
    }

    @Test
    public void assertValuePredicateNoMatch() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1).subscribe(ts);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Value not present");
        ts.assertValue(new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o != 1;
            }
        });
    }

    @Test
    public void assertValuePredicateMatchButMore() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1, 2).subscribe(ts);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Value present but other values as well");
        ts.assertValue(new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o == 1;
            }
        });
    }

    @Test
    public void assertValueAtPredicateEmpty() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Flowable.empty().subscribe(ts);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("No values");
        ts.assertValueAt(0, new Predicate<Object>() {
            @Override
            public boolean test(final Object o) throws Exception {
                return false;
            }
        });
    }

    @Test
    public void assertValueAtPredicateMatch() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1, 2).subscribe(ts);
        ts.assertValueAt(1, new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o == 2;
            }
        });
    }

    @Test
    public void assertValueAtPredicateNoMatch() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1, 2, 3).subscribe(ts);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Value not present");
        ts.assertValueAt(2, new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o != 3;
            }
        });
    }

    @Test
    public void assertValueAtInvalidIndex() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1, 2).subscribe(ts);
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Invalid index: 2 (latch = 0, values = 2, errors = 0, completions = 1)");
        ts.assertValueAt(2, new Predicate<Integer>() {
            @Override
            public boolean test(final Integer o) throws Exception {
                return o == 1;
            }
        });
    }

    @Test
    public void requestMore() {
        Flowable.range(1, 5).test(0).requestMore(1).assertValue(1).requestMore(2).assertValues(1, 2, 3).requestMore(3).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void withTag() {
        try {
            for (int i = 1; i < 3; i++) {
                Flowable.just(i).test().withTag(("testing with item=" + i)).assertResult(1);
            }
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            Assert.assertTrue(ex.toString(), ex.toString().contains("testing with item=2"));
        }
    }

    @Test
    public void timeoutIndicated() throws InterruptedException {
        Thread.interrupted();// clear flag

        TestSubscriber<Object> ts = Flowable.never().test();
        Assert.assertFalse(ts.await(1, TimeUnit.MILLISECONDS));
        try {
            ts.assertResult(1);
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            Assert.assertTrue(ex.toString(), ex.toString().contains("timeout!"));
        }
    }

    @Test
    public void timeoutIndicated2() throws InterruptedException {
        try {
            Flowable.never().test().awaitDone(1, TimeUnit.MILLISECONDS).assertResult(1);
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            Assert.assertTrue(ex.toString(), ex.toString().contains("timeout!"));
        }
    }

    @Test
    public void timeoutIndicated3() throws InterruptedException {
        TestSubscriber<Object> ts = Flowable.never().test();
        Assert.assertFalse(ts.awaitTerminalEvent(1, TimeUnit.MILLISECONDS));
        try {
            ts.assertResult(1);
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            Assert.assertTrue(ex.toString(), ex.toString().contains("timeout!"));
        }
    }

    @Test
    public void disposeIndicated() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        ts.cancel();
        try {
            ts.assertResult(1);
            throw new RuntimeException("Should have thrown!");
        } catch (Throwable ex) {
            Assert.assertTrue(ex.toString(), ex.toString().contains("disposed!"));
        }
    }

    @Test
    public void checkTestWaitStrategyEnum() {
        TestHelper.checkEnum(TestWaitStrategy.class);
    }

    @Test
    public void awaitCount() {
        Flowable.range(1, 10).delay(100, TimeUnit.MILLISECONDS).test(5).awaitCount(5).assertValues(1, 2, 3, 4, 5).requestMore(5).awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void awaitCountLess() {
        Flowable.range(1, 4).test().awaitCount(5).assertResult(1, 2, 3, 4);
    }

    @Test
    public void awaitCountLess2() {
        Flowable.range(1, 4).test().awaitCount(5, TestWaitStrategy.YIELD).assertResult(1, 2, 3, 4);
    }

    @Test
    public void awaitCountLess3() {
        Flowable.range(1, 4).delay(50, TimeUnit.MILLISECONDS).test().awaitCount(5, SLEEP_1MS).assertResult(1, 2, 3, 4);
    }

    @Test
    public void interruptTestWaitStrategy() {
        try {
            Thread.currentThread().interrupt();
            SLEEP_1000MS.run();
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof InterruptedException));
        }
    }

    @Test
    public void awaitCountTimeout() {
        TestSubscriber<Object> ts = Flowable.never().test().awaitCount(1, SLEEP_1MS, 50);
        Assert.assertTrue(ts.isTimeout());
        ts.clearTimeout();
        Assert.assertFalse(ts.isTimeout());
    }

    @Test
    public void assertTimeout() {
        Flowable.never().test().awaitCount(1, SLEEP_1MS, 50).assertTimeout();
    }

    @Test
    public void assertTimeout2() {
        try {
            Flowable.empty().test().awaitCount(1, SLEEP_1MS, 50).assertTimeout();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            Assert.assertTrue(ex.toString(), ex.getMessage().contains("No timeout?!"));
        }
    }

    @Test
    public void assertNoTimeout() {
        Flowable.just(1).test().awaitCount(1, SLEEP_1MS, 50).assertNoTimeout();
    }

    @Test
    public void assertNoTimeout2() {
        try {
            Flowable.never().test().awaitCount(1, SLEEP_1MS, 50).assertNoTimeout();
            throw new RuntimeException("Should have thrown!");
        } catch (AssertionError ex) {
            Assert.assertTrue(ex.toString(), ex.getMessage().contains("Timeout?!"));
        }
    }

    @Test
    public void assertNeverPredicateThrows() {
        try {
            Flowable.just(1).test().assertNever(new Predicate<Integer>() {
                @Override
                public boolean test(Integer t) throws Exception {
                    throw new IllegalArgumentException();
                }
            });
            throw new RuntimeException("Should have thrown!");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void assertValueAtPredicateThrows() {
        try {
            Flowable.just(1).test().assertValueAt(0, new Predicate<Integer>() {
                @Override
                public boolean test(Integer t) throws Exception {
                    throw new IllegalArgumentException();
                }
            });
            throw new RuntimeException("Should have thrown!");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void waitStrategyRuns() {
        for (TestWaitStrategy ws : TestWaitStrategy.values()) {
            ws.run();
        }
    }

    @Test
    public void assertValuesOnly() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertValuesOnly();
        ts.onNext(5);
        ts.assertValuesOnly(5);
        ts.onNext((-1));
        ts.assertValuesOnly(5, (-1));
    }

    @Test
    public void assertValuesOnlyThrowsOnUnexpectedValue() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertValuesOnly();
        ts.onNext(5);
        ts.assertValuesOnly(5);
        ts.onNext((-1));
        try {
            ts.assertValuesOnly(5);
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValuesOnlyThrowsWhenCompleted() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onComplete();
        try {
            ts.assertValuesOnly();
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValuesOnlyThrowsWhenErrored() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onError(new TestException());
        try {
            ts.assertValuesOnly();
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValueSetOnly() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertValueSetOnly(Collections.<Integer>emptySet());
        ts.onNext(5);
        ts.assertValueSetOnly(Collections.singleton(5));
        ts.onNext((-1));
        ts.assertValueSetOnly(new HashSet<Integer>(Arrays.asList(5, (-1))));
    }

    @Test
    public void assertValueSetOnlyThrowsOnUnexpectedValue() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertValueSetOnly(Collections.<Integer>emptySet());
        ts.onNext(5);
        ts.assertValueSetOnly(Collections.singleton(5));
        ts.onNext((-1));
        try {
            ts.assertValueSetOnly(Collections.singleton(5));
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValueSetOnlyThrowsWhenCompleted() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onComplete();
        try {
            ts.assertValueSetOnly(Collections.<Integer>emptySet());
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValueSetOnlyThrowsWhenErrored() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onError(new TestException());
        try {
            ts.assertValueSetOnly(Collections.<Integer>emptySet());
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValueSequenceOnly() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertValueSequenceOnly(Collections.<Integer>emptyList());
        ts.onNext(5);
        ts.assertValueSequenceOnly(Collections.singletonList(5));
        ts.onNext((-1));
        ts.assertValueSequenceOnly(Arrays.asList(5, (-1)));
    }

    @Test
    public void assertValueSequenceOnlyThrowsOnUnexpectedValue() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.assertValueSequenceOnly(Collections.<Integer>emptyList());
        ts.onNext(5);
        ts.assertValueSequenceOnly(Collections.singletonList(5));
        ts.onNext((-1));
        try {
            ts.assertValueSequenceOnly(Collections.singletonList(5));
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValueSequenceOnlyThrowsWhenCompleted() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onComplete();
        try {
            ts.assertValueSequenceOnly(Collections.<Integer>emptyList());
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test
    public void assertValueSequenceOnlyThrowsWhenErrored() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.onSubscribe(new BooleanSubscription());
        ts.onError(new TestException());
        try {
            ts.assertValueSequenceOnly(Collections.<Integer>emptyList());
            throw new RuntimeException();
        } catch (AssertionError ex) {
            // expected
        }
    }

    @Test(timeout = 1000)
    public void awaitCount0() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        ts.awaitCount(0, SLEEP_1MS, 0);
    }

    @Test
    public void assertValueSetWiderSet() {
        Set<Integer> set = new HashSet<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
        Flowable.just(4, 5, 1, 3, 2).test().assertValueSet(set);
    }

    @Test
    public void assertValueSetExact() {
        Set<Integer> set = new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        Flowable.just(4, 5, 1, 3, 2).test().assertValueSet(set).assertValueCount(set.size());
    }

    @Test
    public void assertValueSetMissing() {
        Set<Integer> set = new HashSet<Integer>(Arrays.asList(0, 1, 2, 4, 5, 6, 7));
        try {
            Flowable.range(1, 5).test().assertValueSet(set);
            throw new RuntimeException("Should have failed");
        } catch (AssertionError ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().contains(("Value not in the expected collection: " + 3)));
        }
    }
}

