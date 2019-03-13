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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.Publisher;
import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.subscribers.SubscriberFusion;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableDoOnEachTest {
    Subscriber<String> subscribedSubscriber;

    Subscriber<String> sideEffectSubscriber;

    @Test
    public void testDoOnEach() {
        Flowable<String> base = Flowable.just("a", "b", "c");
        Flowable<String> doOnEach = base.doOnEach(sideEffectSubscriber);
        doOnEach.subscribe(subscribedSubscriber);
        // ensure the leaf observer is still getting called
        Mockito.verify(subscribedSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onNext("a");
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onNext("b");
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onNext("c");
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onComplete();
        // ensure our injected observer is getting called
        Mockito.verify(sideEffectSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(sideEffectSubscriber, Mockito.times(1)).onNext("a");
        Mockito.verify(sideEffectSubscriber, Mockito.times(1)).onNext("b");
        Mockito.verify(sideEffectSubscriber, Mockito.times(1)).onNext("c");
        Mockito.verify(sideEffectSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testDoOnEachWithError() {
        Flowable<String> base = Flowable.just("one", "fail", "two", "three", "fail");
        Flowable<String> errs = base.map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                if ("fail".equals(s)) {
                    throw new RuntimeException("Forced Failure");
                }
                return s;
            }
        });
        Flowable<String> doOnEach = errs.doOnEach(sideEffectSubscriber);
        doOnEach.subscribe(subscribedSubscriber);
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscribedSubscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscribedSubscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscribedSubscriber, Mockito.never()).onComplete();
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(sideEffectSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(sideEffectSubscriber, Mockito.never()).onNext("two");
        Mockito.verify(sideEffectSubscriber, Mockito.never()).onNext("three");
        Mockito.verify(sideEffectSubscriber, Mockito.never()).onComplete();
        Mockito.verify(sideEffectSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDoOnEachWithErrorInCallback() {
        Flowable<String> base = Flowable.just("one", "two", "fail", "three");
        Flowable<String> doOnEach = base.doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if ("fail".equals(s)) {
                    throw new RuntimeException("Forced Failure");
                }
            }
        });
        doOnEach.subscribe(subscribedSubscriber);
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscribedSubscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscribedSubscriber, Mockito.never()).onComplete();
        Mockito.verify(subscribedSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testIssue1451Case1() {
        // https://github.com/Netflix/RxJava/issues/1451
        final int expectedCount = 3;
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < expectedCount; i++) {
            Flowable.just(Boolean.TRUE, Boolean.FALSE).takeWhile(new Predicate<Boolean>() {
                @Override
                public boolean test(Boolean value) {
                    return value;
                }
            }).toList().doOnSuccess(new Consumer<java.util.List<Boolean>>() {
                @Override
                public void accept(java.util.List<Boolean> booleans) {
                    count.incrementAndGet();
                }
            }).subscribe();
        }
        Assert.assertEquals(expectedCount, count.get());
    }

    @Test
    public void testIssue1451Case2() {
        // https://github.com/Netflix/RxJava/issues/1451
        final int expectedCount = 3;
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < expectedCount; i++) {
            Flowable.just(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE).takeWhile(new Predicate<Boolean>() {
                @Override
                public boolean test(Boolean value) {
                    return value;
                }
            }).toList().doOnSuccess(new Consumer<java.util.List<Boolean>>() {
                @Override
                public void accept(java.util.List<Boolean> booleans) {
                    count.incrementAndGet();
                }
            }).subscribe();
        }
        Assert.assertEquals(expectedCount, count.get());
    }

    @Test
    public void onErrorThrows() {
        TestSubscriber<Object> ts = TestSubscriber.create();
        Flowable.error(new TestException()).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) {
                throw new TestException();
            }
        }).subscribe(ts);
        ts.assertNoValues();
        ts.assertNotComplete();
        ts.assertError(CompositeException.class);
        CompositeException ex = ((CompositeException) (ts.errors().get(0)));
        java.util.List<Throwable> exceptions = ex.getExceptions();
        Assert.assertEquals(2, exceptions.size());
        Assert.assertTrue(((exceptions.get(0)) instanceof TestException));
        Assert.assertTrue(((exceptions.get(1)) instanceof TestException));
    }

    @Test
    public void ignoreCancel() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Object>() {
                @Override
                public void subscribe(Subscriber<? super Object> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onNext(1);
                    s.onNext(2);
                    s.onError(new IOException());
                    s.onComplete();
                }
            }).doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object e) throws Exception {
                    throw new TestException();
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorAfterCrash() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Object>() {
                @Override
                public void subscribe(Subscriber<? super Object> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onError(new TestException());
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteAfterCrash() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Object>() {
                @Override
                public void subscribe(Subscriber<? super Object> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onComplete();
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteCrash() {
        Flowable.fromPublisher(new Publisher<Object>() {
            @Override
            public void subscribe(Subscriber<? super Object> s) {
                s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                s.onComplete();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                throw new IOException();
            }
        }).test().assertFailure(IOException.class);
    }

    @Test
    public void ignoreCancelConditional() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Object>() {
                @Override
                public void subscribe(Subscriber<? super Object> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onNext(1);
                    s.onNext(2);
                    s.onError(new IOException());
                    s.onComplete();
                }
            }).doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object e) throws Exception {
                    throw new TestException();
                }
            }).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void ignoreCancelConditional2() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Object>() {
                @Override
                public void subscribe(Subscriber<? super Object> s) {
                    ConditionalSubscriber<? super Object> cs = ((ConditionalSubscriber<? super Object>) (s));
                    cs.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    cs.tryOnNext(1);
                    cs.tryOnNext(2);
                    cs.onError(new IOException());
                    cs.onComplete();
                }
            }).doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object e) throws Exception {
                    throw new TestException();
                }
            }).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorAfterCrashConditional() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Object>() {
                @Override
                public void subscribe(Subscriber<? super Object> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onError(new TestException());
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteAfter() {
        final int[] call = new int[]{ 0 };
        Flowable.just(1).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                (call[0])++;
            }
        }).test().assertResult(1);
        Assert.assertEquals(1, call[0]);
    }

    @Test
    public void onCompleteAfterCrashConditional() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Object>() {
                @Override
                public void subscribe(Subscriber<? super Object> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onComplete();
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).filter(Functions.alwaysTrue()).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteCrashConditional() {
        Flowable.fromPublisher(new Publisher<Object>() {
            @Override
            public void subscribe(Subscriber<? super Object> s) {
                s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                s.onComplete();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                throw new IOException();
            }
        }).filter(Functions.alwaysTrue()).test().assertFailure(IOException.class);
    }

    @Test
    public void onErrorOnErrorCrashConditional() {
        TestSubscriber<Object> ts = Flowable.error(new TestException("Outer")).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Inner");
            }
        }).filter(Functions.alwaysTrue()).test().assertFailure(CompositeException.class);
        java.util.List<Throwable> errors = TestHelper.compositeList(ts.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Outer");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void fused() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        final int[] call = new int[]{ 0, 0 };
        Flowable.range(1, 5).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                (call[0])++;
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (call[1])++;
            }
        }).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(SYNC)).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(5, call[0]);
        Assert.assertEquals(1, call[1]);
    }

    @Test
    public void fusedOnErrorCrash() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        final int[] call = new int[]{ 0 };
        Flowable.range(1, 5).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (call[0])++;
            }
        }).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(SYNC)).assertFailure(TestException.class);
        Assert.assertEquals(0, call[0]);
    }

    @Test
    public void fusedConditional() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        final int[] call = new int[]{ 0, 0 };
        Flowable.range(1, 5).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                (call[0])++;
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (call[1])++;
            }
        }).filter(Functions.alwaysTrue()).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(SYNC)).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(5, call[0]);
        Assert.assertEquals(1, call[1]);
    }

    @Test
    public void fusedOnErrorCrashConditional() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        final int[] call = new int[]{ 0 };
        Flowable.range(1, 5).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (call[0])++;
            }
        }).filter(Functions.alwaysTrue()).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(SYNC)).assertFailure(TestException.class);
        Assert.assertEquals(0, call[0]);
    }

    @Test
    public void fusedAsync() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        final int[] call = new int[]{ 0, 0 };
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                (call[0])++;
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (call[1])++;
            }
        }).subscribe(ts);
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(5, call[0]);
        Assert.assertEquals(1, call[1]);
    }

    @Test
    public void fusedAsyncConditional() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        final int[] call = new int[]{ 0, 0 };
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                (call[0])++;
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (call[1])++;
            }
        }).filter(Functions.alwaysTrue()).subscribe(ts);
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(5, call[0]);
        Assert.assertEquals(1, call[1]);
    }

    @Test
    public void fusedAsyncConditional2() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        final int[] call = new int[]{ 0, 0 };
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.hide().doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                (call[0])++;
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (call[1])++;
            }
        }).filter(Functions.alwaysTrue()).subscribe(ts);
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(NONE)).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(5, call[0]);
        Assert.assertEquals(1, call[1]);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).doOnEach(new TestSubscriber<Integer>()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.doOnEach(new TestSubscriber<Object>());
            }
        });
    }

    @Test
    public void doOnNextDoOnErrorFused() {
        ConnectableFlowable<Integer> cf = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException("First");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Second");
            }
        }).publish();
        TestSubscriber<Integer> ts = cf.test();
        cf.connect();
        ts.assertFailure(CompositeException.class);
        TestHelper.assertError(ts, 0, TestException.class, "First");
        TestHelper.assertError(ts, 1, TestException.class, "Second");
    }

    @Test
    public void doOnNextDoOnErrorCombinedFused() {
        ConnectableFlowable<Integer> cf = Flowable.just(1).compose(new FlowableTransformer<Integer, Integer>() {
            @Override
            public Publisher<Integer> apply(Flowable<Integer> v) {
                return new FlowableDoOnEach<Integer>(v, new Consumer<Integer>() {
                    @Override
                    public void accept(Integer v) throws Exception {
                        throw new TestException("First");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        throw new TestException("Second");
                    }
                }, Functions.EMPTY_ACTION, Functions.EMPTY_ACTION);
            }
        }).publish();
        TestSubscriber<Integer> ts = cf.test();
        cf.connect();
        ts.assertFailure(CompositeException.class);
        TestHelper.assertError(ts, 0, TestException.class, "First");
        TestHelper.assertError(ts, 1, TestException.class, "Second");
    }

    @Test
    public void doOnNextDoOnErrorFused2() {
        ConnectableFlowable<Integer> cf = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException("First");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Second");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Third");
            }
        }).publish();
        TestSubscriber<Integer> ts = cf.test();
        cf.connect();
        ts.assertFailure(CompositeException.class);
        TestHelper.assertError(ts, 0, TestException.class, "First");
        TestHelper.assertError(ts, 1, TestException.class, "Second");
        TestHelper.assertError(ts, 2, TestException.class, "Third");
    }

    @Test
    public void doOnNextDoOnErrorFusedConditional() {
        ConnectableFlowable<Integer> cf = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException("First");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Second");
            }
        }).filter(Functions.alwaysTrue()).publish();
        TestSubscriber<Integer> ts = cf.test();
        cf.connect();
        ts.assertFailure(CompositeException.class);
        TestHelper.assertError(ts, 0, TestException.class, "First");
        TestHelper.assertError(ts, 1, TestException.class, "Second");
    }

    @Test
    public void doOnNextDoOnErrorFusedConditional2() {
        ConnectableFlowable<Integer> cf = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException("First");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Second");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Third");
            }
        }).filter(Functions.alwaysTrue()).publish();
        TestSubscriber<Integer> ts = cf.test();
        cf.connect();
        ts.assertFailure(CompositeException.class);
        TestHelper.assertError(ts, 0, TestException.class, "First");
        TestHelper.assertError(ts, 1, TestException.class, "Second");
        TestHelper.assertError(ts, 2, TestException.class, "Third");
    }

    @Test
    public void doOnNextDoOnErrorCombinedFusedConditional() {
        ConnectableFlowable<Integer> cf = Flowable.just(1).compose(new FlowableTransformer<Integer, Integer>() {
            @Override
            public Publisher<Integer> apply(Flowable<Integer> v) {
                return new FlowableDoOnEach<Integer>(v, new Consumer<Integer>() {
                    @Override
                    public void accept(Integer v) throws Exception {
                        throw new TestException("First");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        throw new TestException("Second");
                    }
                }, Functions.EMPTY_ACTION, Functions.EMPTY_ACTION);
            }
        }).filter(Functions.alwaysTrue()).publish();
        TestSubscriber<Integer> ts = cf.test();
        cf.connect();
        ts.assertFailure(CompositeException.class);
        TestHelper.assertError(ts, 0, TestException.class, "First");
        TestHelper.assertError(ts, 1, TestException.class, "Second");
    }
}

