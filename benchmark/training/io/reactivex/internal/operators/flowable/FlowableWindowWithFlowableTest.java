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
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.MissingBackpressureException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class FlowableWindowWithFlowableTest {
    @Test
    public void testWindowViaFlowableNormal1() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> boundary = PublishProcessor.create();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        final List<Subscriber<Object>> values = new ArrayList<Subscriber<Object>>();
        Subscriber<Flowable<Integer>> wo = new DefaultSubscriber<Flowable<Integer>>() {
            @Override
            public void onNext(Flowable<Integer> args) {
                final Subscriber<Object> mo = TestHelper.mockSubscriber();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        int n = 30;
        for (int i = 0; i < n; i++) {
            source.onNext(i);
            if (((i % 3) == 2) && (i < (n - 1))) {
                boundary.onNext((i / 3));
            }
        }
        source.onComplete();
        org.mockito.Mockito.verify(subscriber, never()).onError(any(Throwable.class));
        Assert.assertEquals((n / 3), values.size());
        int j = 0;
        for (Subscriber<Object> mo : values) {
            org.mockito.Mockito.verify(mo, never()).onError(any(Throwable.class));
            for (int i = 0; i < 3; i++) {
                verify(mo).onNext((j + i));
            }
            verify(mo).onComplete();
            j += 3;
        }
        verify(subscriber).onComplete();
    }

    @Test
    public void testWindowViaFlowableBoundaryCompletes() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> boundary = PublishProcessor.create();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        final List<Subscriber<Object>> values = new ArrayList<Subscriber<Object>>();
        Subscriber<Flowable<Integer>> wo = new DefaultSubscriber<Flowable<Integer>>() {
            @Override
            public void onNext(Flowable<Integer> args) {
                final Subscriber<Object> mo = TestHelper.mockSubscriber();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        int n = 30;
        for (int i = 0; i < n; i++) {
            source.onNext(i);
            if (((i % 3) == 2) && (i < (n - 1))) {
                boundary.onNext((i / 3));
            }
        }
        boundary.onComplete();
        Assert.assertEquals((n / 3), values.size());
        int j = 0;
        for (Subscriber<Object> mo : values) {
            for (int i = 0; i < 3; i++) {
                verify(mo).onNext((j + i));
            }
            verify(mo).onComplete();
            org.mockito.Mockito.verify(mo, never()).onError(any(Throwable.class));
            j += 3;
        }
        verify(subscriber).onComplete();
        org.mockito.Mockito.verify(subscriber, never()).onError(any(Throwable.class));
    }

    @Test
    public void testWindowViaFlowableBoundaryThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> boundary = PublishProcessor.create();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        final List<Subscriber<Object>> values = new ArrayList<Subscriber<Object>>();
        Subscriber<Flowable<Integer>> wo = new DefaultSubscriber<Flowable<Integer>>() {
            @Override
            public void onNext(Flowable<Integer> args) {
                final Subscriber<Object> mo = TestHelper.mockSubscriber();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        boundary.onError(new TestException());
        Assert.assertEquals(1, values.size());
        Subscriber<Object> mo = values.get(0);
        verify(mo).onNext(0);
        verify(mo).onNext(1);
        verify(mo).onNext(2);
        verify(mo).onError(org.mockito.ArgumentMatchers.any(TestException.class));
        org.mockito.Mockito.verify(subscriber, never()).onComplete();
        verify(subscriber).onError(org.mockito.ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testWindowViaFlowableThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> boundary = PublishProcessor.create();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        final List<Subscriber<Object>> values = new ArrayList<Subscriber<Object>>();
        Subscriber<Flowable<Integer>> wo = new DefaultSubscriber<Flowable<Integer>>() {
            @Override
            public void onNext(Flowable<Integer> args) {
                final Subscriber<Object> mo = TestHelper.mockSubscriber();
                values.add(mo);
                args.subscribe(mo);
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        };
        source.window(boundary).subscribe(wo);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        source.onError(new TestException());
        Assert.assertEquals(1, values.size());
        Subscriber<Object> mo = values.get(0);
        verify(mo).onNext(0);
        verify(mo).onNext(1);
        verify(mo).onNext(2);
        verify(mo).onError(org.mockito.ArgumentMatchers.any(TestException.class));
        org.mockito.Mockito.verify(subscriber, never()).onComplete();
        verify(subscriber).onError(org.mockito.ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testWindowNoDuplication() {
        final PublishProcessor<Integer> source = PublishProcessor.create();
        final TestSubscriber<Integer> tsw = new TestSubscriber<Integer>() {
            boolean once;

            @Override
            public void onNext(Integer t) {
                if (!(once)) {
                    once = true;
                    source.onNext(2);
                }
                super.onNext(t);
            }
        };
        TestSubscriber<Flowable<Integer>> ts = new TestSubscriber<Flowable<Integer>>() {
            @Override
            public void onNext(Flowable<Integer> t) {
                t.subscribe(tsw);
                super.onNext(t);
            }
        };
        source.window(new Callable<Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> call() {
                return Flowable.never();
            }
        }).subscribe(ts);
        source.onNext(1);
        source.onComplete();
        ts.assertValueCount(1);
        tsw.assertValues(1, 2);
    }

    @Test
    public void testWindowViaFlowableNoUnsubscribe() {
        Flowable<Integer> source = Flowable.range(1, 10);
        Callable<Flowable<String>> boundary = new Callable<Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> call() {
                return Flowable.empty();
            }
        };
        TestSubscriber<Flowable<Integer>> ts = new TestSubscriber<Flowable<Integer>>();
        source.window(boundary).subscribe(ts);
        Assert.assertFalse(ts.isCancelled());
    }

    @Test
    public void testBoundaryUnsubscribedOnMainCompletion() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> boundary = PublishProcessor.create();
        Callable<Flowable<Integer>> boundaryFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                return boundary;
            }
        };
        TestSubscriber<Flowable<Integer>> ts = new TestSubscriber<Flowable<Integer>>();
        source.window(boundaryFunc).subscribe(ts);
        Assert.assertTrue(source.hasSubscribers());
        Assert.assertTrue(boundary.hasSubscribers());
        source.onComplete();
        Assert.assertFalse(source.hasSubscribers());
        Assert.assertFalse(boundary.hasSubscribers());
        ts.assertComplete();
        ts.assertNoErrors();
        ts.assertValueCount(1);
    }

    @Test
    public void testMainUnsubscribedOnBoundaryCompletion() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> boundary = PublishProcessor.create();
        Callable<Flowable<Integer>> boundaryFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                return boundary;
            }
        };
        TestSubscriber<Flowable<Integer>> ts = new TestSubscriber<Flowable<Integer>>();
        source.window(boundaryFunc).subscribe(ts);
        Assert.assertTrue(source.hasSubscribers());
        Assert.assertTrue(boundary.hasSubscribers());
        boundary.onComplete();
        Assert.assertFalse(source.hasSubscribers());
        Assert.assertFalse(boundary.hasSubscribers());
        ts.assertComplete();
        ts.assertNoErrors();
        ts.assertValueCount(1);
    }

    @Test
    public void testChildUnsubscribed() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> boundary = PublishProcessor.create();
        Callable<Flowable<Integer>> boundaryFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                return boundary;
            }
        };
        TestSubscriber<Flowable<Integer>> ts = new TestSubscriber<Flowable<Integer>>();
        source.window(boundaryFunc).subscribe(ts);
        Assert.assertTrue(source.hasSubscribers());
        Assert.assertTrue(boundary.hasSubscribers());
        ts.dispose();
        Assert.assertTrue(source.hasSubscribers());
        Assert.assertFalse(boundary.hasSubscribers());
        ts.values().get(0).test().cancel();
        Assert.assertFalse(source.hasSubscribers());
        ts.assertNotComplete();
        ts.assertNoErrors();
        ts.assertValueCount(1);
    }

    @Test
    public void testInnerBackpressure() {
        Flowable<Integer> source = Flowable.range(1, 10);
        final PublishProcessor<Integer> boundary = PublishProcessor.create();
        Callable<Flowable<Integer>> boundaryFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                return boundary;
            }
        };
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1L);
        final TestSubscriber<Flowable<Integer>> ts1 = new TestSubscriber<Flowable<Integer>>(1L) {
            @Override
            public void onNext(Flowable<Integer> t) {
                super.onNext(t);
                t.subscribe(ts);
            }
        };
        source.window(boundaryFunc).subscribe(ts1);
        ts1.assertNoErrors();
        ts1.assertComplete();
        ts1.assertValueCount(1);
        ts.assertNoErrors();
        ts.assertNotComplete();
        ts.assertValues(1);
        ts.request(11);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void newBoundaryCalledAfterWindowClosed() {
        final AtomicInteger calls = new AtomicInteger();
        PublishProcessor<Integer> source = PublishProcessor.create();
        final PublishProcessor<Integer> boundary = PublishProcessor.create();
        Callable<Flowable<Integer>> boundaryFunc = new Callable<Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> call() {
                calls.getAndIncrement();
                return boundary;
            }
        };
        TestSubscriber<Flowable<Integer>> ts = new TestSubscriber<Flowable<Integer>>();
        source.window(boundaryFunc).subscribe(ts);
        source.onNext(1);
        boundary.onNext(1);
        Assert.assertTrue(boundary.hasSubscribers());
        source.onNext(2);
        boundary.onNext(2);
        Assert.assertTrue(boundary.hasSubscribers());
        source.onNext(3);
        boundary.onNext(3);
        Assert.assertTrue(boundary.hasSubscribers());
        source.onNext(4);
        source.onComplete();
        ts.assertNoErrors();
        ts.assertValueCount(4);
        ts.assertComplete();
        Assert.assertFalse(source.hasSubscribers());
        Assert.assertFalse(boundary.hasSubscribers());
    }

    @Test
    public void boundaryDispose() {
        TestHelper.checkDisposed(Flowable.never().window(Flowable.never()));
    }

    @Test
    public void boundaryDispose2() {
        TestHelper.checkDisposed(Flowable.never().window(Functions.justCallable(Flowable.never())));
    }

    @Test
    public void boundaryOnError() {
        TestSubscriber<Object> ts = Flowable.error(new TestException()).window(Flowable.never()).flatMap(Functions.<Flowable<Object>>identity(), true).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(ts.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class);
    }

    @Test
    public void mainError() {
        Flowable.error(new TestException()).window(Functions.justCallable(Flowable.never())).test().assertError(TestException.class);
    }

    @Test
    public void innerBadSource() {
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return Flowable.just(1).window(f).flatMap(new io.reactivex.functions.Function<Flowable<Integer>, Flowable<Integer>>() {
                    @Override
                    public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, ((Object[]) (null)));
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(final Flowable<Integer> f) throws Exception {
                return Flowable.just(1).window(new Callable<Publisher<Integer>>() {
                    int count;

                    @Override
                    public io.reactivex.Publisher<Integer> call() throws Exception {
                        if ((++(count)) > 1) {
                            return Flowable.never();
                        }
                        return f;
                    }
                }).flatMap(new io.reactivex.functions.Function<Flowable<Integer>, Flowable<Integer>>() {
                    @Override
                    public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, ((Object[]) (null)));
    }

    @Test
    public void reentrant() {
        final FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(BehaviorProcessor.createDefault(1)).flatMap(new io.reactivex.functions.Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(ts);
        ps.onNext(1);
        ts.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void reentrantCallable() {
        final FlowableProcessor<Integer> ps = PublishProcessor.<Integer>create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
            }
        };
        ps.window(new Callable<Flowable<Integer>>() {
            boolean once;

            @Override
            public io.reactivex.Flowable<Integer> call() throws Exception {
                if (!(once)) {
                    once = true;
                    return BehaviorProcessor.createDefault(1);
                }
                return Flowable.never();
            }
        }).flatMap(new io.reactivex.functions.Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> v) throws Exception {
                return v;
            }
        }).subscribe(ts);
        ps.onNext(1);
        ts.awaitDone(1, TimeUnit.SECONDS).assertResult(1, 2);
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Object>, Object>() {
            @Override
            public Object apply(Flowable<Object> f) throws Exception {
                return f.window(Flowable.never()).flatMap(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Flowable<Object> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, 1);
    }

    @Test
    public void badSourceCallable() {
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Object>, Object>() {
            @Override
            public Object apply(Flowable<Object> f) throws Exception {
                return f.window(Functions.justCallable(Flowable.never())).flatMap(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Flowable<Object> v) throws Exception {
                        return v;
                    }
                });
            }
        }, false, 1, 1, 1);
    }

    @Test
    public void boundaryError() {
        BehaviorProcessor.createDefault(1).window(Functions.justCallable(Flowable.error(new TestException()))).test().assertValueCount(1).assertNotComplete().assertError(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void boundaryMissingBackpressure() {
        BehaviorProcessor.createDefault(1).window(Functions.justCallable(Flowable.error(new TestException()))).test(0).assertFailure(MissingBackpressureException.class);
    }

    @Test
    public void boundaryCallableCrashOnCall2() {
        BehaviorProcessor.createDefault(1).window(new Callable<Flowable<Integer>>() {
            int calls;

            @Override
            public io.reactivex.Flowable<Integer> call() throws Exception {
                if ((++(calls)) == 2) {
                    throw new TestException();
                }
                return Flowable.just(1);
            }
        }).test().assertError(TestException.class).assertNotComplete();
    }

    @Test
    public void boundarySecondMissingBackpressure() {
        BehaviorProcessor.createDefault(1).window(Functions.justCallable(Flowable.just(1))).test(1).assertError(MissingBackpressureException.class).assertNotComplete();
    }

    @Test
    public void oneWindow() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Flowable<Integer>> ts = BehaviorProcessor.createDefault(1).window(Functions.justCallable(pp)).take(1).test();
        pp.onNext(1);
        ts.assertValueCount(1).assertNoErrors().assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void boundaryDirectMissingBackpressure() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            BehaviorProcessor.create().window(Flowable.error(new TestException())).test(0).assertFailure(MissingBackpressureException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void boundaryDirectMissingBackpressureNoNullPointerException() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            BehaviorProcessor.createDefault(1).window(Flowable.error(new TestException())).test(0).assertFailure(MissingBackpressureException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void boundaryDirectSecondMissingBackpressure() {
        BehaviorProcessor.createDefault(1).window(Flowable.just(1)).test(1).assertError(MissingBackpressureException.class).assertNotComplete();
    }

    @Test
    public void boundaryDirectDoubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Publisher<Flowable<Object>>>() {
            @Override
            public io.reactivex.Publisher<Flowable<Object>> apply(Flowable<Object> f) throws Exception {
                return f.window(Flowable.never()).takeLast(1);
            }
        });
    }

    @Test
    public void upstreamDisposedWhenOutputsDisposed() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> boundary = PublishProcessor.create();
        TestSubscriber<Integer> ts = source.window(boundary).take(1).flatMap(new io.reactivex.functions.Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> w) throws Exception {
                return w.take(1);
            }
        }).test();
        source.onNext(1);
        Assert.assertFalse("source not disposed", source.hasSubscribers());
        Assert.assertFalse("boundary not disposed", boundary.hasSubscribers());
        ts.assertResult(1);
    }

    @Test
    public void mainAndBoundaryBothError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
            TestSubscriber<Flowable<Object>> ts = Flowable.error(new TestException("main")).window(new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    ref.set(subscriber);
                }
            }).test();
            ts.assertValueCount(1).assertError(TestException.class).assertErrorMessage("main").assertNotComplete();
            ref.get().onError(new TestException("inner"));
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "inner");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void mainCompleteBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
                final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
                TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
                    @Override
                    protected void subscribeActual(Subscriber<? super Object> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        refMain.set(subscriber);
                    }
                }.window(new Flowable<Object>() {
                    @Override
                    protected void subscribeActual(Subscriber<? super Object> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        ref.set(subscriber);
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        refMain.get().onComplete();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ref.get().onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                ts.assertValueCount(1).assertTerminated();
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void mainNextBoundaryNextRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
            final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
            TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    refMain.set(subscriber);
                }
            }.window(new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    ref.set(subscriber);
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    refMain.get().onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ref.get().onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            ts.assertValueCount(2).assertNotComplete().assertNoErrors();
        }
    }

    @Test
    public void takeOneAnotherBoundary() {
        final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
        final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
        TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
            @Override
            protected void subscribeActual(Subscriber<? super Object> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                refMain.set(subscriber);
            }
        }.window(new Flowable<Object>() {
            @Override
            protected void subscribeActual(Subscriber<? super Object> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                ref.set(subscriber);
            }
        }).test();
        ts.assertValueCount(1).assertNotTerminated().cancel();
        ref.get().onNext(1);
        ts.assertValueCount(1).assertNotTerminated();
    }

    @Test
    public void disposeMainBoundaryCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
            final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
            final TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    refMain.set(subscriber);
                }
            }.window(new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    final AtomicInteger counter = new AtomicInteger();
                    subscriber.onSubscribe(new Subscription() {
                        @Override
                        public void cancel() {
                            // about a microsecond
                            for (int i = 0; i < 100; i++) {
                                counter.incrementAndGet();
                            }
                        }

                        @Override
                        public void request(long n) {
                        }
                    });
                    ref.set(subscriber);
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    Subscriber<Object> subscriber = ref.get();
                    subscriber.onNext(1);
                    subscriber.onComplete();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void disposeMainBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
            final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
            final TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    refMain.set(subscriber);
                }
            }.window(new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    final AtomicInteger counter = new AtomicInteger();
                    subscriber.onSubscribe(new Subscription() {
                        @Override
                        public void cancel() {
                            // about a microsecond
                            for (int i = 0; i < 100; i++) {
                                counter.incrementAndGet();
                            }
                        }

                        @Override
                        public void request(long n) {
                        }
                    });
                    ref.set(subscriber);
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    Subscriber<Object> subscriber = ref.get();
                    subscriber.onNext(1);
                    subscriber.onError(ex);
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void boundarySupplierDoubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Flowable<Object>>>() {
            @Override
            public io.reactivex.Flowable<Flowable<Object>> apply(Flowable<Object> f) throws Exception {
                return f.window(Functions.justCallable(Flowable.never())).takeLast(1);
            }
        });
    }

    @Test
    public void selectorUpstreamDisposedWhenOutputsDisposed() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> boundary = PublishProcessor.create();
        TestSubscriber<Integer> ts = source.window(Functions.justCallable(boundary)).take(1).flatMap(new io.reactivex.functions.Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> w) throws Exception {
                return w.take(1);
            }
        }).test();
        source.onNext(1);
        Assert.assertFalse("source not disposed", source.hasSubscribers());
        Assert.assertFalse("boundary not disposed", boundary.hasSubscribers());
        ts.assertResult(1);
    }

    @Test
    public void supplierMainAndBoundaryBothError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
            TestSubscriber<Flowable<Object>> ts = Flowable.error(new TestException("main")).window(Functions.justCallable(new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    ref.set(subscriber);
                }
            })).test();
            ts.assertValueCount(1).assertError(TestException.class).assertErrorMessage("main").assertNotComplete();
            ref.get().onError(new TestException("inner"));
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "inner");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void supplierMainCompleteBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
                final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
                TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
                    @Override
                    protected void subscribeActual(Subscriber<? super Object> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        refMain.set(subscriber);
                    }
                }.window(Functions.justCallable(new Flowable<Object>() {
                    @Override
                    protected void subscribeActual(Subscriber<? super Object> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        ref.set(subscriber);
                    }
                })).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        refMain.get().onComplete();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ref.get().onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                ts.assertValueCount(1).assertTerminated();
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void supplierMainNextBoundaryNextRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
            final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
            TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    refMain.set(subscriber);
                }
            }.window(Functions.justCallable(new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    ref.set(subscriber);
                }
            })).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    refMain.get().onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ref.get().onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            ts.assertValueCount(2).assertNotComplete().assertNoErrors();
        }
    }

    @Test
    public void supplierTakeOneAnotherBoundary() {
        final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
        final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
        TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
            @Override
            protected void subscribeActual(Subscriber<? super Object> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                refMain.set(subscriber);
            }
        }.window(Functions.justCallable(new Flowable<Object>() {
            @Override
            protected void subscribeActual(Subscriber<? super Object> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                ref.set(subscriber);
            }
        })).test();
        ts.assertValueCount(1).assertNotTerminated().cancel();
        ref.get().onNext(1);
        ts.assertValueCount(1).assertNotTerminated();
    }

    @Test
    public void supplierDisposeMainBoundaryCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
            final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
            final TestSubscriber<Flowable<Object>> ts = new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    refMain.set(subscriber);
                }
            }.window(Functions.justCallable(new Flowable<Object>() {
                @Override
                protected void subscribeActual(Subscriber<? super Object> subscriber) {
                    final AtomicInteger counter = new AtomicInteger();
                    subscriber.onSubscribe(new Subscription() {
                        @Override
                        public void cancel() {
                            // about a microsecond
                            for (int i = 0; i < 100; i++) {
                                counter.incrementAndGet();
                            }
                        }

                        @Override
                        public void request(long n) {
                        }
                    });
                    ref.set(subscriber);
                }
            })).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    Subscriber<Object> subscriber = ref.get();
                    subscriber.onNext(1);
                    subscriber.onComplete();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void supplierDisposeMainBoundaryErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final AtomicReference<Subscriber<? super Object>> refMain = new AtomicReference<Subscriber<? super Object>>();
                final AtomicReference<Subscriber<? super Object>> ref = new AtomicReference<Subscriber<? super Object>>();
                final TestSubscriber<Flowable<Object>> ts = window(new Callable<Flowable<Object>>() {
                    int count;

                    @Override
                    public io.reactivex.Flowable<Object> call() throws Exception {
                        if ((++(count)) > 1) {
                            return Flowable.never();
                        }
                        return new Flowable<Object>() {
                            @Override
                            protected void subscribeActual(Subscriber<? super Object> subscriber) {
                                final AtomicInteger counter = new AtomicInteger();
                                subscriber.onSubscribe(new Subscription() {
                                    @Override
                                    public void cancel() {
                                        // about a microsecond
                                        for (int i = 0; i < 100; i++) {
                                            counter.incrementAndGet();
                                        }
                                    }

                                    @Override
                                    public void request(long n) {
                                    }
                                });
                                ref.set(subscriber);
                            }
                        };
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ts.cancel();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        Subscriber<Object> subscriber = ref.get();
                        subscriber.onNext(1);
                        subscriber.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }
}

