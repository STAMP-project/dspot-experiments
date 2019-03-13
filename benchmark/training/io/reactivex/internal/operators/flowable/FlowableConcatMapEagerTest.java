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
import io.reactivex.processors.MissingBackpressureException;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class FlowableConcatMapEagerTest {
    @Test
    public void normal() {
        Flowable.range(1, 5).concatMapEager(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return Flowable.range(t, 2);
            }
        }).test().assertResult(1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
    }

    @Test
    public void normalBackpressured() {
        TestSubscriber<Integer> ts = Flowable.range(1, 5).concatMapEager(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return Flowable.range(t, 2);
            }
        }).test(3);
        ts.assertValues(1, 2, 2);
        ts.request(1);
        ts.assertValues(1, 2, 2, 3);
        ts.request(1);
        ts.assertValues(1, 2, 2, 3, 3);
        ts.request(5);
        ts.assertResult(1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
    }

    @Test
    public void normalDelayBoundary() {
        Flowable.range(1, 5).concatMapEagerDelayError(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return Flowable.range(t, 2);
            }
        }, false).test().assertResult(1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
    }

    @Test
    public void normalDelayBoundaryBackpressured() {
        TestSubscriber<Integer> ts = Flowable.range(1, 5).concatMapEagerDelayError(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return Flowable.range(t, 2);
            }
        }, false).test(3);
        ts.assertValues(1, 2, 2);
        ts.request(1);
        ts.assertValues(1, 2, 2, 3);
        ts.request(1);
        ts.assertValues(1, 2, 2, 3, 3);
        ts.request(5);
        ts.assertResult(1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
    }

    @Test
    public void normalDelayEnd() {
        Flowable.range(1, 5).concatMapEagerDelayError(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return Flowable.range(t, 2);
            }
        }, true).test().assertResult(1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
    }

    @Test
    public void normalDelayEndBackpressured() {
        TestSubscriber<Integer> ts = Flowable.range(1, 5).concatMapEagerDelayError(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return Flowable.range(t, 2);
            }
        }, true).test(3);
        ts.assertValues(1, 2, 2);
        ts.request(1);
        ts.assertValues(1, 2, 2, 3);
        ts.request(1);
        ts.assertValues(1, 2, 2, 3, 3);
        ts.request(5);
        ts.assertResult(1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
    }

    @Test
    public void mainErrorsDelayBoundary() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        final PublishProcessor<Integer> inner = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.concatMapEagerDelayError(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return inner;
            }
        }, false).test();
        main.onNext(1);
        inner.onNext(2);
        ts.assertValue(2);
        main.onError(new TestException("Forced failure"));
        ts.assertNoErrors();
        inner.onNext(3);
        inner.onComplete();
        ts.assertFailureAndMessage(TestException.class, "Forced failure", 2, 3);
    }

    @Test
    public void mainErrorsDelayEnd() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        final PublishProcessor<Integer> inner = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.concatMapEagerDelayError(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return inner;
            }
        }, true).test();
        main.onNext(1);
        main.onNext(2);
        inner.onNext(2);
        ts.assertValue(2);
        main.onError(new TestException("Forced failure"));
        ts.assertNoErrors();
        inner.onNext(3);
        inner.onComplete();
        ts.assertFailureAndMessage(TestException.class, "Forced failure", 2, 3, 2, 3);
    }

    @Test
    public void mainErrorsImmediate() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        final PublishProcessor<Integer> inner = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.concatMapEager(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                return inner;
            }
        }).test();
        main.onNext(1);
        main.onNext(2);
        inner.onNext(2);
        ts.assertValue(2);
        main.onError(new TestException("Forced failure"));
        Assert.assertFalse("inner has subscribers?", inner.hasSubscribers());
        inner.onNext(3);
        inner.onComplete();
        ts.assertFailureAndMessage(TestException.class, "Forced failure", 2);
    }

    @Test
    public void longEager() {
        Flowable.range(1, (2 * (Flowable.bufferSize()))).concatMapEager(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) {
                return Flowable.just(1);
            }
        }).test().assertValueCount((2 * (Flowable.bufferSize()))).assertNoErrors().assertComplete();
    }

    TestSubscriber<Object> ts;

    TestSubscriber<Object> tsBp;

    io.reactivex.Function<Integer, Flowable<Integer>> toJust = new Function<Integer, Flowable<Integer>>() {
        @Override
        public io.reactivex.Flowable<Integer> apply(Integer t) {
            return Flowable.just(t);
        }
    };

    io.reactivex.Function<Integer, Flowable<Integer>> toRange = new Function<Integer, Flowable<Integer>>() {
        @Override
        public io.reactivex.Flowable<Integer> apply(Integer t) {
            return Flowable.range(t, 2);
        }
    };

    @Test
    public void testSimple() {
        Flowable.range(1, 100).concatMapEager(toJust).subscribe(ts);
        ts.assertNoErrors();
        ts.assertValueCount(100);
        ts.assertComplete();
    }

    @Test
    public void testSimple2() {
        Flowable.range(1, 100).concatMapEager(toRange).subscribe(ts);
        ts.assertNoErrors();
        ts.assertValueCount(200);
        ts.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness2() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source).subscribe(tsBp);
        Assert.assertEquals(2, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness3() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source, source).subscribe(tsBp);
        Assert.assertEquals(3, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness4() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source, source, source).subscribe(tsBp);
        Assert.assertEquals(4, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness5() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source, source, source, source).subscribe(tsBp);
        Assert.assertEquals(5, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness6() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source, source, source, source, source).subscribe(tsBp);
        Assert.assertEquals(6, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness7() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source, source, source, source, source, source).subscribe(tsBp);
        Assert.assertEquals(7, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness8() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source, source, source, source, source, source, source).subscribe(tsBp);
        Assert.assertEquals(8, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEagerness9() {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> source = Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                count.getAndIncrement();
            }
        }).hide();
        Flowable.concatArrayEager(source, source, source, source, source, source, source, source, source).subscribe(tsBp);
        Assert.assertEquals(9, count.get());
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.assertNoValues();
        tsBp.request(Long.MAX_VALUE);
        tsBp.assertValueCount(count.get());
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @Test
    public void testMainError() {
        Flowable.<Integer>error(new TestException()).concatMapEager(toJust).subscribe(ts);
        ts.assertNoValues();
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInnerError() {
        Flowable.concatArrayEager(Flowable.just(1), Flowable.error(new TestException())).subscribe(ts);
        ts.assertValue(1);
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInnerEmpty() {
        Flowable.concatArrayEager(Flowable.empty(), Flowable.empty()).subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void testMapperThrows() {
        Flowable.just(1).concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t) {
                throw new TestException();
            }
        }).subscribe(ts);
        ts.assertNoValues();
        ts.assertNotComplete();
        ts.assertError(TestException.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMaxConcurrent() {
        Flowable.just(1).concatMapEager(toJust, 0, Flowable.bufferSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCapacityHint() {
        Flowable.just(1).concatMapEager(toJust, Flowable.bufferSize(), 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBackpressure() {
        Flowable.concatArrayEager(Flowable.just(1), Flowable.just(1)).subscribe(tsBp);
        tsBp.assertNoErrors();
        tsBp.assertNoValues();
        tsBp.assertNotComplete();
        tsBp.request(1);
        tsBp.assertValue(1);
        tsBp.assertNoErrors();
        tsBp.assertNotComplete();
        tsBp.request(1);
        tsBp.assertValues(1, 1);
        tsBp.assertNoErrors();
        tsBp.assertComplete();
    }

    @Test
    public void testAsynchronousRun() {
        Flowable.range(1, 2).concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t) {
                return Flowable.range(1, 1000).subscribeOn(Schedulers.computation());
            }
        }).observeOn(Schedulers.single()).test().awaitDone(5, TimeUnit.SECONDS).assertNoErrors().assertValueCount(2000).assertComplete();
    }

    @Test
    public void testReentrantWork() {
        final PublishProcessor<Integer> processor = PublishProcessor.create();
        final AtomicBoolean once = new AtomicBoolean();
        processor.concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t) {
                return Flowable.just(t);
            }
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                if (once.compareAndSet(false, true)) {
                    processor.onNext(2);
                }
            }
        }).subscribe(ts);
        processor.onNext(1);
        ts.assertNoErrors();
        ts.assertNotComplete();
        ts.assertValues(1, 2);
    }

    @Test
    public void testPrefetchIsBounded() {
        final AtomicInteger count = new AtomicInteger();
        TestSubscriber<Object> ts = TestSubscriber.create(0);
        Flowable.just(1).concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t) {
                return Flowable.range(1, ((Flowable.bufferSize()) * 2)).doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer t) {
                        count.getAndIncrement();
                    }
                }).hide();
            }
        }).subscribe(ts);
        ts.assertNoErrors();
        ts.assertNoValues();
        ts.assertNotComplete();
        Assert.assertEquals(Flowable.bufferSize(), count.get());
    }

    @Test
    public void testMaxConcurrent5() {
        final List<Long> requests = new ArrayList<Long>();
        Flowable.range(1, 100).doOnRequest(new LongConsumer() {
            @Override
            public void accept(long reqCount) {
                requests.add(reqCount);
            }
        }).concatMapEager(toJust, 5, Flowable.bufferSize()).subscribe(ts);
        ts.assertNoErrors();
        ts.assertValueCount(100);
        ts.assertComplete();
        Assert.assertEquals(5, ((long) (requests.get(0))));
        Assert.assertEquals(1, ((long) (requests.get(1))));
        Assert.assertEquals(1, ((long) (requests.get(2))));
        Assert.assertEquals(1, ((long) (requests.get(3))));
        Assert.assertEquals(1, ((long) (requests.get(4))));
        Assert.assertEquals(1, ((long) (requests.get(5))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void capacityHint() {
        Flowable<Integer> source = Flowable.just(1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.concatEager(Arrays.asList(source, source, source), 1, 1).subscribe(ts);
        ts.assertValues(1, 1, 1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void flowable() {
        Flowable<Integer> source = Flowable.just(1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.concatEager(Flowable.just(source, source, source)).subscribe(ts);
        ts.assertValues(1, 1, 1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void flowableCapacityHint() {
        Flowable<Integer> source = Flowable.just(1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.concatEager(Flowable.just(source, source, source), 1, 1).subscribe(ts);
        ts.assertValues(1, 1, 1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void badCapacityHint() throws Exception {
        Flowable<Integer> source = Flowable.just(1);
        try {
            Flowable.concatEager(Arrays.asList(source, source, source), 1, (-99));
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("prefetch > 0 required but it was -99", ex.getMessage());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void mappingBadCapacityHint() throws Exception {
        Flowable<Integer> source = Flowable.just(1);
        try {
            Flowable.just(source, source, source).concatMapEager(((Function) (Functions.identity())), 10, (-99));
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("prefetch > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void concatEagerZero() {
        Flowable.concatEager(Collections.<Flowable<Integer>>emptyList()).test().assertResult();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatEagerOne() {
        Flowable.concatEager(Arrays.asList(Flowable.just(1))).test().assertResult(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatEagerTwo() {
        Flowable.concatEager(Arrays.asList(Flowable.just(1), Flowable.just(2))).test().assertResult(1, 2);
    }

    @Test
    public void Flowable() {
        Flowable<Integer> source = Flowable.just(1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.concatEager(Flowable.just(source, source, source)).subscribe(ts);
        ts.assertValues(1, 1, 1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void ObservableCapacityHint() {
        Flowable<Integer> source = Flowable.just(1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.concatEager(Flowable.just(source, source, source), 1, 1).subscribe(ts);
        ts.assertValues(1, 1, 1);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatEagerIterable() {
        Flowable.concatEager(Arrays.asList(Flowable.just(1), Flowable.just(2))).test().assertResult(1, 2);
    }

    @Test
    public void empty() {
        Flowable.<Integer>empty().hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.range(1, 2);
            }
        }).test().assertResult();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.range(1, 2);
            }
        }));
    }

    @Test
    public void innerError() {
        Flowable.<Integer>just(1).hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.error(new TestException());
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void innerOuterRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishProcessor<Integer> pp1 = PublishProcessor.create();
                final PublishProcessor<Integer> pp2 = PublishProcessor.create();
                TestSubscriber<Integer> ts = pp1.concatMapEager(new Function<Integer, Flowable<Integer>>() {
                    @Override
                    public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                        return pp2;
                    }
                }).test();
                final TestException ex1 = new TestException();
                final TestException ex2 = new TestException();
                pp1.onNext(1);
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp1.onError(ex1);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        pp2.onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                ts.assertSubscribed().assertNoValues().assertNotComplete();
                Throwable ex = ts.errors().get(0);
                if (ex instanceof CompositeException) {
                    List<Throwable> es = TestHelper.errorList(ts);
                    TestHelper.assertError(es, 0, TestException.class);
                    TestHelper.assertError(es, 1, TestException.class);
                } else {
                    ts.assertError(TestException.class);
                    if (!(errors.isEmpty())) {
                        TestHelper.assertUndeliverable(errors, 0, TestException.class);
                    }
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void innerErrorMaxConcurrency() {
        Flowable.<Integer>just(1).hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.error(new TestException());
            }
        }, 1, 128).test().assertFailure(TestException.class);
    }

    @Test
    public void innerCallableThrows() {
        Flowable.<Integer>just(1).hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.fromCallable(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        throw new TestException();
                    }
                });
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void innerErrorAfterPoll() {
        final UnicastProcessor<Integer> us = UnicastProcessor.create();
        us.onNext(1);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                us.onError(new TestException());
            }
        };
        Flowable.<Integer>just(1).hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return us;
            }
        }, 1, 128).subscribe(ts);
        ts.assertFailure(TestException.class, 1);
    }

    @Test
    public void nextCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp1 = PublishProcessor.create();
            final TestSubscriber<Integer> ts = pp1.concatMapEager(new Function<Integer, Flowable<Integer>>() {
                @Override
                public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                    return Flowable.never();
                }
            }).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp1.onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            TestHelper.race(r1, r2);
            ts.assertEmpty();
        }
    }

    @Test
    public void mapperCancels() {
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.just(1).hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                ts.cancel();
                return Flowable.never();
            }
        }, 1, 128).subscribe(ts);
        ts.assertEmpty();
    }

    @Test
    public void innerErrorFused() {
        Flowable.<Integer>just(1).hide().concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.range(1, 2).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer v) throws Exception {
                        throw new TestException();
                    }
                });
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void fuseAndTake() {
        UnicastProcessor<Integer> us = UnicastProcessor.create();
        us.onNext(1);
        us.onComplete();
        us.concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.just(1);
            }
        }).take(1).test().assertResult(1);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.concatMapEager(new Function<Object, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Object v) throws Exception {
                        return Flowable.just(v);
                    }
                });
            }
        });
    }

    @Test
    public void doubleOnError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            @SuppressWarnings("rawtypes")
            final Subscriber[] sub = new io.reactivex.Subscriber[]{ null };
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> s) {
                    sub[0] = s;
                    s.onSubscribe(new BooleanSubscription());
                    s.onNext(1);
                    s.onError(new TestException("First"));
                }
            }.concatMapEager(Functions.justFunction(Flowable.just(1))).test().assertFailureAndMessage(TestException.class, "First", 1);
            sub[0].onError(new TestException("Second"));
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void innerOverflow() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.just(1).concatMapEager(new Function<Integer, Publisher<Integer>>() {
                @Override
                public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                    return new Flowable<Integer>() {
                        @Override
                        protected void subscribeActual(Subscriber<? super Integer> s) {
                            s.onSubscribe(new BooleanSubscription());
                            s.onNext(1);
                            s.onNext(2);
                            s.onError(new TestException());
                        }
                    };
                }
            }, 1, 1).test(0L).assertFailure(MissingBackpressureException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void unboundedIn() {
        int n = (Flowable.bufferSize()) * 2;
        Flowable.range(1, n).concatMapEager(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.just(1);
            }
        }, Integer.MAX_VALUE, 16).test().assertValueCount(n).assertComplete().assertNoErrors();
    }

    @Test
    public void drainCancelRaceOnEmpty() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
            Flowable.just(1).concatMapEager(Functions.justFunction(pp)).subscribe(ts);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
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

    @Test
    public void innerLong() {
        int n = (Flowable.bufferSize()) * 2;
        Flowable.just(1).hide().concatMapEager(Functions.justFunction(Flowable.range(1, n).hide())).rebatchRequests(1).test().assertValueCount(n).assertComplete().assertNoErrors();
    }

    @Test
    public void oneDelayed() {
        Flowable.just(1, 2, 3, 4, 5).concatMapEager(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer i) throws Exception {
                return i == 3 ? Flowable.just(i) : Flowable.just(i).delay(1, TimeUnit.MILLISECONDS, Schedulers.io());
            }
        }).observeOn(Schedulers.io()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void maxConcurrencyOf2() {
        List<Integer>[] list = new ArrayList[100];
        for (int i = 0; i < 100; i++) {
            List<Integer> lst = new ArrayList<Integer>();
            list[i] = lst;
            for (int k = 1; k <= 10; k++) {
                lst.add(((i * 10) + k));
            }
        }
        Flowable.range(1, 1000).buffer(10).concatMapEager(new Function<List<Integer>, Flowable<List<Integer>>>() {
            @Override
            public io.reactivex.Flowable<List<Integer>> apply(List<Integer> v) throws Exception {
                return Flowable.just(v).subscribeOn(Schedulers.io()).doOnNext(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> v) throws Exception {
                        Thread.sleep(new Random().nextInt(20));
                    }
                });
            }
        }, 2, 3).test().awaitDone(5, TimeUnit.SECONDS).assertResult(list);
    }

    @Test
    public void arrayDelayErrorDefault() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        PublishProcessor<Integer> pp3 = PublishProcessor.create();
        @SuppressWarnings("unchecked")
        TestSubscriber<Integer> ts = Flowable.concatArrayEagerDelayError(pp1, pp2, pp3).test();
        ts.assertEmpty();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        Assert.assertTrue(pp3.hasSubscribers());
        pp2.onNext(2);
        pp2.onComplete();
        ts.assertEmpty();
        pp1.onNext(1);
        ts.assertValuesOnly(1);
        pp1.onComplete();
        ts.assertValuesOnly(1, 2);
        pp3.onComplete();
        ts.assertResult(1, 2);
    }

    @Test
    public void arrayDelayErrorMaxConcurrency() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        PublishProcessor<Integer> pp3 = PublishProcessor.create();
        @SuppressWarnings("unchecked")
        TestSubscriber<Integer> ts = Flowable.concatArrayEagerDelayError(2, 2, pp1, pp2, pp3).test();
        ts.assertEmpty();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        Assert.assertFalse(pp3.hasSubscribers());
        pp2.onNext(2);
        pp2.onComplete();
        ts.assertEmpty();
        pp1.onNext(1);
        ts.assertValuesOnly(1);
        pp1.onComplete();
        Assert.assertTrue(pp3.hasSubscribers());
        ts.assertValuesOnly(1, 2);
        pp3.onComplete();
        ts.assertResult(1, 2);
    }

    @Test
    public void arrayDelayErrorMaxConcurrencyErrorDelayed() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        PublishProcessor<Integer> pp3 = PublishProcessor.create();
        @SuppressWarnings("unchecked")
        TestSubscriber<Integer> ts = Flowable.concatArrayEagerDelayError(2, 2, pp1, pp2, pp3).test();
        ts.assertEmpty();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        Assert.assertFalse(pp3.hasSubscribers());
        pp2.onNext(2);
        pp2.onError(new TestException());
        ts.assertEmpty();
        pp1.onNext(1);
        ts.assertValuesOnly(1);
        pp1.onComplete();
        Assert.assertTrue(pp3.hasSubscribers());
        ts.assertValuesOnly(1, 2);
        pp3.onComplete();
        ts.assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void cancelActive() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.concatEager(Flowable.just(pp1, pp2)).test();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(pp1.hasSubscribers());
        Assert.assertFalse(pp2.hasSubscribers());
    }

    @Test
    public void cancelNoInnerYet() {
        PublishProcessor<Flowable<Integer>> pp1 = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.concatEager(pp1).test();
        Assert.assertTrue(pp1.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(pp1.hasSubscribers());
    }
}

