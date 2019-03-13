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
package io.reactivex.internal.operators.mixed;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.CompletableSubject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscriber;


public class FlowableConcatMapCompletableTest {
    @Test
    public void simple() {
        Flowable.range(1, 5).concatMapCompletable(Functions.justFunction(Completable.complete())).test().assertResult();
    }

    @Test
    public void simple2() {
        final AtomicInteger counter = new AtomicInteger();
        Flowable.range(1, 5).concatMapCompletable(Functions.justFunction(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }
        }))).test().assertResult();
        Assert.assertEquals(5, counter.get());
    }

    @Test
    public void simpleLongPrefetch() {
        Flowable.range(1, 1024).concatMapCompletable(Functions.justFunction(Completable.complete()), 32).test().assertResult();
    }

    @Test
    public void mainError() {
        Flowable.<Integer>error(new TestException()).concatMapCompletable(Functions.justFunction(Completable.complete())).test().assertFailure(TestException.class);
    }

    @Test
    public void innerError() {
        Flowable.just(1).concatMapCompletable(Functions.justFunction(Completable.error(new TestException()))).test().assertFailure(TestException.class);
    }

    @Test
    public void innerErrorDelayed() {
        Flowable.range(1, 5).concatMapCompletableDelayError(new Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                return Completable.error(new TestException());
            }
        }).test().assertFailure(CompositeException.class).assertOf(new Consumer<io.reactivex.observers.TestObserver<Void>>() {
            @Override
            public void accept(io.reactivex.observers.TestObserver<Void> to) throws Exception {
                Assert.assertEquals(5, getExceptions().size());
            }
        });
    }

    @Test
    public void mapperCrash() {
        Flowable.just(1).concatMapCompletable(new Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void immediateError() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        CompletableSubject cs = CompletableSubject.create();
        io.reactivex.observers.TestObserver<Void> to = pp.concatMapCompletable(Functions.justFunction(cs)).test();
        to.assertEmpty();
        Assert.assertTrue(pp.hasSubscribers());
        Assert.assertFalse(cs.hasObservers());
        pp.onNext(1);
        Assert.assertTrue(cs.hasObservers());
        pp.onError(new TestException());
        Assert.assertFalse(cs.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void immediateError2() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        CompletableSubject cs = CompletableSubject.create();
        io.reactivex.observers.TestObserver<Void> to = pp.concatMapCompletable(Functions.justFunction(cs)).test();
        to.assertEmpty();
        Assert.assertTrue(pp.hasSubscribers());
        Assert.assertFalse(cs.hasObservers());
        pp.onNext(1);
        Assert.assertTrue(cs.hasObservers());
        cs.onError(new TestException());
        Assert.assertFalse(pp.hasSubscribers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void boundaryError() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        CompletableSubject cs = CompletableSubject.create();
        io.reactivex.observers.TestObserver<Void> to = pp.concatMapCompletableDelayError(Functions.justFunction(cs), false).test();
        to.assertEmpty();
        Assert.assertTrue(pp.hasSubscribers());
        Assert.assertFalse(cs.hasObservers());
        pp.onNext(1);
        Assert.assertTrue(cs.hasObservers());
        pp.onError(new TestException());
        Assert.assertTrue(cs.hasObservers());
        to.assertEmpty();
        cs.onComplete();
        to.assertFailure(TestException.class);
    }

    @Test
    public void endError() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        final CompletableSubject cs = CompletableSubject.create();
        final CompletableSubject cs2 = CompletableSubject.create();
        io.reactivex.observers.TestObserver<Void> to = pp.concatMapCompletableDelayError(new Function<Integer, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Integer v) throws Exception {
                if (v == 1) {
                    return cs;
                }
                return cs2;
            }
        }, true, 32).test();
        to.assertEmpty();
        Assert.assertTrue(pp.hasSubscribers());
        Assert.assertFalse(cs.hasObservers());
        pp.onNext(1);
        Assert.assertTrue(cs.hasObservers());
        cs.onError(new TestException());
        Assert.assertTrue(pp.hasSubscribers());
        pp.onNext(2);
        to.assertEmpty();
        cs2.onComplete();
        Assert.assertTrue(pp.hasSubscribers());
        to.assertEmpty();
        pp.onComplete();
        to.assertFailure(TestException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowableToCompletable(new Function<Flowable<Object>, Completable>() {
            @Override
            public io.reactivex.Completable apply(Flowable<Object> f) throws Exception {
                return f.concatMapCompletable(Functions.justFunction(Completable.complete()));
            }
        });
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Flowable.never().concatMapCompletable(Functions.justFunction(Completable.complete())));
    }

    @Test
    public void queueOverflow() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> s) {
                    s.onSubscribe(new BooleanSubscription());
                    s.onNext(1);
                    s.onNext(2);
                    s.onNext(3);
                    s.onError(new TestException());
                }
            }.concatMapCompletable(Functions.justFunction(Completable.never()), 1).test().assertFailure(MissingBackpressureException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void immediateOuterInnerErrorRace() {
        final TestException ex = new TestException();
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishProcessor<Integer> pp = PublishProcessor.create();
                final CompletableSubject cs = CompletableSubject.create();
                io.reactivex.observers.TestObserver<Void> to = pp.concatMapCompletable(Functions.justFunction(cs)).test();
                pp.onNext(1);
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        cs.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                to.assertError(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable e) throws Exception {
                        return (e instanceof TestException) || (e instanceof CompositeException);
                    }
                }).assertNotComplete();
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void disposeInDrainLoop() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final CompletableSubject cs = CompletableSubject.create();
            final io.reactivex.observers.TestObserver<Void> to = pp.concatMapCompletable(Functions.justFunction(cs)).test();
            pp.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onNext(2);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    cs.onComplete();
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
            to.assertEmpty();
        }
    }

    @Test
    public void doneButNotEmpty() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        final CompletableSubject cs = CompletableSubject.create();
        final io.reactivex.observers.TestObserver<Void> to = pp.concatMapCompletable(Functions.justFunction(cs)).test();
        pp.onNext(1);
        pp.onNext(2);
        pp.onComplete();
        cs.onComplete();
        to.assertResult();
    }
}

