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
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscriber;


public class FlowableFlatMapMaybeTest {
    @Test
    public void normal() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void normalEmpty() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.empty();
            }
        }).test().assertResult();
    }

    @Test
    public void normalDelayError() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }, true, Integer.MAX_VALUE).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void normalAsync() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v).subscribeOn(Schedulers.computation());
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).assertNoErrors().assertComplete();
    }

    @Test
    public void normalAsyncMaxConcurrency() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v).subscribeOn(Schedulers.computation());
            }
        }, false, 3).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).assertNoErrors().assertComplete();
    }

    @Test
    public void normalAsyncMaxConcurrency1() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v).subscribeOn(Schedulers.computation());
            }
        }, false, 1).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void mapperThrowsFlowable() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test();
        Assert.assertTrue(pp.hasSubscribers());
        pp.onNext(1);
        ts.assertFailure(TestException.class);
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void mapperReturnsNullFlowable() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return null;
            }
        }).test();
        Assert.assertTrue(pp.hasSubscribers());
        pp.onNext(1);
        ts.assertFailure(NullPointerException.class);
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void normalDelayErrorAll() {
        TestSubscriber<Integer> ts = Flowable.range(1, 10).concatWith(Flowable.<Integer>error(new TestException())).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.error(new TestException());
            }
        }, true, Integer.MAX_VALUE).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(ts.errors().get(0));
        for (int i = 0; i < 11; i++) {
            TestHelper.assertError(errors, i, TestException.class);
        }
    }

    @Test
    public void normalBackpressured() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }).rebatchRequests(1).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void normalMaxConcurrent1Backpressured() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }, false, 1).rebatchRequests(1).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void normalMaxConcurrent2Backpressured() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }, false, 2).rebatchRequests(1).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void takeAsync() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v).subscribeOn(Schedulers.computation());
            }
        }).take(2).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(2).assertValueSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).assertNoErrors().assertComplete();
    }

    @Test
    public void take() {
        Flowable.range(1, 10).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(v);
            }
        }).take(2).test().assertResult(1, 2);
    }

    @Test
    public void middleError() {
        Flowable.fromArray(new String[]{ "1", "a", "2" }).flatMapMaybe(new io.reactivex.functions.Function<String, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(final String s) throws NumberFormatException {
                // return Maybe.just(Integer.valueOf(s)); //This works
                return Maybe.fromCallable(new Callable<Integer>() {
                    @Override
                    public Integer call() throws NumberFormatException {
                        return Integer.valueOf(s);
                    }
                });
            }
        }).test().assertFailure(NumberFormatException.class, 1);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(PublishProcessor.<Integer>create().flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.<Integer>empty();
            }
        }));
    }

    @Test
    public void asyncFlatten() {
        Flowable.range(1, 1000).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(1).subscribeOn(Schedulers.computation());
            }
        }).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueCount(500).assertNoErrors().assertComplete();
    }

    @Test
    public void asyncFlattenNone() {
        Flowable.range(1, 1000).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.<Integer>empty().subscribeOn(Schedulers.computation());
            }
        }).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
    }

    @Test
    public void asyncFlattenNoneMaxConcurrency() {
        Flowable.range(1, 1000).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.<Integer>empty().subscribeOn(Schedulers.computation());
            }
        }, false, 128).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
    }

    @Test
    public void asyncFlattenErrorMaxConcurrency() {
        Flowable.range(1, 1000).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.<Integer>error(new TestException()).subscribeOn(Schedulers.computation());
            }
        }, true, 128).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(CompositeException.class);
    }

    @Test
    public void successError() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.range(1, 2).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                if (v == 2) {
                    return pp.singleElement();
                }
                return Maybe.error(new TestException());
            }
        }, true, Integer.MAX_VALUE).test();
        pp.onNext(1);
        pp.onComplete();
        ts.assertFailure(TestException.class, 1);
    }

    @Test
    public void completeError() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.range(1, 2).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                if (v == 2) {
                    return pp.singleElement();
                }
                return Maybe.error(new TestException());
            }
        }, true, Integer.MAX_VALUE).test();
        pp.onComplete();
        ts.assertFailure(TestException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Object> f) throws Exception {
                return f.flatMapMaybe(Functions.justFunction(Maybe.just(2)));
            }
        });
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onError(new TestException("First"));
                    subscriber.onError(new TestException("Second"));
                }
            }.flatMapMaybe(Functions.justFunction(Maybe.just(2))).test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badInnerSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.just(1).flatMapMaybe(Functions.justFunction(new Maybe<Integer>() {
                @Override
                protected void subscribeActual(MaybeObserver<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException("First"));
                    observer.onError(new TestException("Second"));
                }
            })).test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void emissionQueueTrigger() {
        final PublishProcessor<Integer> pp1 = PublishProcessor.create();
        final PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    pp2.onNext(2);
                    pp2.onComplete();
                }
            }
        };
        Flowable.just(pp1, pp2).flatMapMaybe(new io.reactivex.functions.Function<PublishProcessor<Integer>, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(PublishProcessor<Integer> v) throws Exception {
                return v.singleElement();
            }
        }).subscribe(ts);
        pp1.onNext(1);
        pp1.onComplete();
        ts.assertResult(1, 2);
    }

    @Test
    public void emissionQueueTrigger2() {
        final PublishProcessor<Integer> pp1 = PublishProcessor.create();
        final PublishProcessor<Integer> pp2 = PublishProcessor.create();
        final PublishProcessor<Integer> pp3 = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    pp2.onNext(2);
                    pp2.onComplete();
                }
            }
        };
        Flowable.just(pp1, pp2, pp3).flatMapMaybe(new io.reactivex.functions.Function<PublishProcessor<Integer>, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(PublishProcessor<Integer> v) throws Exception {
                return v.singleElement();
            }
        }).subscribe(ts);
        pp1.onNext(1);
        pp1.onComplete();
        pp3.onComplete();
        ts.assertResult(1, 2);
    }

    @Test
    public void disposeInner() {
        final TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Flowable.just(1).flatMapMaybe(new io.reactivex.functions.Function<Integer, MaybeSource<Object>>() {
            @Override
            public io.reactivex.MaybeSource<Object> apply(Integer v) throws Exception {
                return new Maybe<Object>() {
                    @Override
                    protected void subscribeActual(MaybeObserver<? super Object> observer) {
                        observer.onSubscribe(Disposables.empty());
                        Assert.assertFalse(isDisposed());
                        ts.dispose();
                        Assert.assertTrue(isDisposed());
                    }
                };
            }
        }).subscribe(ts);
        ts.assertEmpty();
    }

    @Test
    public void innerSuccessCompletesAfterMain() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.just(1).flatMapMaybe(Functions.justFunction(pp.singleElement())).test();
        pp.onNext(2);
        pp.onComplete();
        ts.assertResult(2);
    }

    @Test
    public void backpressure() {
        TestSubscriber<Integer> ts = Flowable.just(1).flatMapMaybe(Functions.justFunction(Maybe.just(2))).test(0L).assertEmpty();
        ts.request(1);
        ts.assertResult(2);
    }

    @Test
    public void error() {
        Flowable.just(1).flatMapMaybe(Functions.justFunction(Maybe.<Integer>error(new TestException()))).test(0L).assertFailure(TestException.class);
    }

    @Test
    public void errorDelayed() {
        Flowable.just(1).flatMapMaybe(Functions.justFunction(Maybe.<Integer>error(new TestException())), true, 16).test(0L).assertFailure(TestException.class);
    }

    @Test
    public void requestCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestSubscriber<Integer> ts = Flowable.just(1).concatWith(Flowable.<Integer>never()).flatMapMaybe(Functions.justFunction(Maybe.just(2))).test(0);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts.request(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }
}

