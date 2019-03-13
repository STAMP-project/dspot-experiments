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
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.Subscription;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableTakeWhileTest {
    @Test
    public void testTakeWhile1() {
        Flowable<Integer> w = Flowable.just(1, 2, 3);
        Flowable<Integer> take = w.takeWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer input) {
                return input < 3;
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.never()).onNext(3);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeWhileOnSubject1() {
        FlowableProcessor<Integer> s = PublishProcessor.create();
        Flowable<Integer> take = s.takeWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer input) {
                return input < 3;
            }
        });
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        s.onNext(1);
        s.onNext(2);
        s.onNext(3);
        s.onNext(4);
        s.onNext(5);
        s.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.never()).onNext(3);
        Mockito.verify(subscriber, Mockito.never()).onNext(4);
        Mockito.verify(subscriber, Mockito.never()).onNext(5);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeWhile2() {
        Flowable<String> w = Flowable.just("one", "two", "three");
        Flowable<String> take = w.takeWhile(new Predicate<String>() {
            int index;

            @Override
            public boolean test(String input) {
                return ((index)++) < 2;
            }
        });
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeWhileDoesntLeakErrors() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable<String> source = Flowable.unsafeCreate(new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onNext("one");
                    subscriber.onError(new TestException("test failed"));
                }
            });
            source.takeWhile(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return false;
                }
            }).blockingLast("");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "test failed");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void testTakeWhileProtectsPredicateCall() {
        FlowableTakeWhileTest.TestFlowable source = new FlowableTakeWhileTest.TestFlowable(Mockito.mock(Subscription.class), "one");
        final RuntimeException testException = new RuntimeException("test exception");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> take = Flowable.unsafeCreate(source).takeWhile(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                throw testException;
            }
        });
        take.subscribe(subscriber);
        // wait for the Flowable to complete
        try {
            source.t.join();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.times(1)).onError(testException);
    }

    @Test
    public void testUnsubscribeAfterTake() {
        Subscription s = Mockito.mock(Subscription.class);
        FlowableTakeWhileTest.TestFlowable w = new FlowableTakeWhileTest.TestFlowable(s, "one", "two", "three");
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> take = Flowable.unsafeCreate(w).takeWhile(new Predicate<String>() {
            int index;

            @Override
            public boolean test(String s) {
                return ((index)++) < 1;
            }
        });
        take.subscribe(subscriber);
        // wait for the Flowable to complete
        try {
            w.t.join();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        System.out.println("TestFlowable thread finished");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(s, Mockito.times(1)).cancel();
    }

    private static class TestFlowable implements Publisher<String> {
        final Subscription upstream;

        final String[] values;

        Thread t;

        TestFlowable(Subscription s, String... values) {
            this.upstream = s;
            this.values = values;
        }

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            System.out.println("TestFlowable subscribed to ...");
            subscriber.onSubscribe(upstream);
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestFlowable thread");
                        for (String s : values) {
                            System.out.println(("TestFlowable onNext: " + s));
                            subscriber.onNext(s);
                        }
                        subscriber.onComplete();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            System.out.println("starting TestFlowable thread");
            t.start();
            System.out.println("done starting TestFlowable thread");
        }
    }

    @Test
    public void testBackpressure() {
        Flowable<Integer> source = Flowable.range(1, 1000).takeWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 100;
            }
        });
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(5L);
        source.subscribe(ts);
        ts.assertNoErrors();
        ts.assertValues(1, 2, 3, 4, 5);
        ts.request(5);
        ts.assertNoErrors();
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void testNoUnsubscribeDownstream() {
        Flowable<Integer> source = Flowable.range(1, 1000).takeWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 < 2;
            }
        });
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.subscribe(ts);
        ts.assertNoErrors();
        ts.assertValue(1);
        Assert.assertFalse("Unsubscribed!", ts.isCancelled());
    }

    @Test
    public void testErrorCauseIncludesLastValue() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Flowable.just("abc").takeWhile(new Predicate<String>() {
            @Override
            public boolean test(String t1) {
                throw new TestException();
            }
        }).subscribe(ts);
        ts.assertTerminated();
        ts.assertNoValues();
        ts.assertError(TestException.class);
        // FIXME last cause value not recorded
        // assertTrue(ts.getOnErrorEvents().get(0).getCause().getMessage().contains("abc"));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().takeWhile(Functions.alwaysTrue()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.takeWhile(Functions.alwaysTrue());
            }
        });
    }

    @Test
    public void badSource() {
        new Flowable<Integer>() {
            @Override
            protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                subscriber.onComplete();
                subscriber.onComplete();
            }
        }.takeWhile(Functions.alwaysTrue()).test().assertResult();
    }
}

