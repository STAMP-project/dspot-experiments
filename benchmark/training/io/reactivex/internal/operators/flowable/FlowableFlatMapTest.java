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
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableFlatMapTest {
    @Test
    public void testNormal() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        final List<Integer> list = Arrays.asList(1, 2, 3);
        Function<Integer, List<Integer>> func = new Function<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1) {
                return list;
            }
        };
        BiFunction<Integer, Integer, Integer> resFunc = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 | t2;
            }
        };
        List<Integer> source = Arrays.asList(16, 32, 64);
        Flowable.fromIterable(source).flatMapIterable(func, resFunc).subscribe(subscriber);
        for (Integer s : source) {
            for (Integer v : list) {
                Mockito.verify(subscriber).onNext((s | v));
            }
        }
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testCollectionFunctionThrows() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Function<Integer, List<Integer>> func = new Function<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1) {
                throw new TestException();
            }
        };
        BiFunction<Integer, Integer, Integer> resFunc = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 | t2;
            }
        };
        List<Integer> source = Arrays.asList(16, 32, 64);
        Flowable.fromIterable(source).flatMapIterable(func, resFunc).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testResultFunctionThrows() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        final List<Integer> list = Arrays.asList(1, 2, 3);
        Function<Integer, List<Integer>> func = new Function<Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1) {
                return list;
            }
        };
        BiFunction<Integer, Integer, Integer> resFunc = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                throw new TestException();
            }
        };
        List<Integer> source = Arrays.asList(16, 32, 64);
        Flowable.fromIterable(source).flatMapIterable(func, resFunc).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testMergeError() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Function<Integer, Flowable<Integer>> func = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return Flowable.error(new TestException());
            }
        };
        BiFunction<Integer, Integer, Integer> resFunc = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 | t2;
            }
        };
        List<Integer> source = Arrays.asList(16, 32, 64);
        Flowable.fromIterable(source).flatMap(func, resFunc).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testFlatMapTransformsNormal() {
        Flowable<Integer> onNext = Flowable.fromIterable(Arrays.asList(1, 2, 3));
        Flowable<Integer> onComplete = Flowable.fromIterable(Arrays.asList(4));
        Flowable<Integer> onError = Flowable.fromIterable(Arrays.asList(5));
        Flowable<Integer> source = Flowable.fromIterable(Arrays.asList(10, 20, 30));
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        source.flatMap(just(onNext), just(onError), just0(onComplete)).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(3);
        Mockito.verify(subscriber).onNext(4);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(5);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFlatMapTransformsException() {
        Flowable<Integer> onNext = Flowable.fromIterable(Arrays.asList(1, 2, 3));
        Flowable<Integer> onComplete = Flowable.fromIterable(Arrays.asList(4));
        Flowable<Integer> onError = Flowable.fromIterable(Arrays.asList(5));
        Flowable<Integer> source = Flowable.concat(Flowable.fromIterable(Arrays.asList(10, 20, 30)), Flowable.<Integer>error(new RuntimeException("Forced failure!")));
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        source.flatMap(just(onNext), just(onError), just0(onComplete)).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(3);
        Mockito.verify(subscriber).onNext(5);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(4);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFlatMapTransformsOnNextFuncThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable<Integer> onComplete = Flowable.fromIterable(Arrays.asList(4));
            Flowable<Integer> onError = Flowable.fromIterable(Arrays.asList(5));
            Flowable<Integer> source = Flowable.fromIterable(Arrays.asList(10, 20, 30));
            Subscriber<Object> subscriber = TestHelper.mockSubscriber();
            source.flatMap(funcThrow(1, onError), just(onError), just0(onComplete)).subscribe(subscriber);
            Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
            Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
            Mockito.verify(subscriber, Mockito.never()).onComplete();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void testFlatMapTransformsOnErrorFuncThrows() {
        Flowable<Integer> onNext = Flowable.fromIterable(Arrays.asList(1, 2, 3));
        Flowable<Integer> onComplete = Flowable.fromIterable(Arrays.asList(4));
        Flowable<Integer> onError = Flowable.fromIterable(Arrays.asList(5));
        Flowable<Integer> source = Flowable.error(new TestException());
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        source.flatMap(just(onNext), funcThrow(((Throwable) (null)), onError), just0(onComplete)).subscribe(subscriber);
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(CompositeException.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testFlatMapTransformsOnCompletedFuncThrows() {
        Flowable<Integer> onNext = Flowable.fromIterable(Arrays.asList(1, 2, 3));
        Flowable<Integer> onComplete = Flowable.fromIterable(Arrays.asList(4));
        Flowable<Integer> onError = Flowable.fromIterable(Arrays.asList(5));
        Flowable<Integer> source = Flowable.fromIterable(Arrays.<Integer>asList());
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        source.flatMap(just(onNext), just(onError), funcThrow0(onComplete)).subscribe(subscriber);
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testFlatMapTransformsMergeException() {
        Flowable<Integer> onNext = Flowable.error(new TestException());
        Flowable<Integer> onComplete = Flowable.fromIterable(Arrays.asList(4));
        Flowable<Integer> onError = Flowable.fromIterable(Arrays.asList(5));
        Flowable<Integer> source = Flowable.fromIterable(Arrays.asList(10, 20, 30));
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        source.flatMap(just(onNext), just(onError), funcThrow0(onComplete)).subscribe(subscriber);
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testFlatMapMaxConcurrent() {
        final int m = 4;
        final AtomicInteger subscriptionCount = new AtomicInteger();
        Flowable<Integer> source = Flowable.range(1, 10).flatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return FlowableFlatMapTest.composer(Flowable.range((t1 * 10), 2), subscriptionCount, m).subscribeOn(Schedulers.computation());
            }
        }, m);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Set<Integer> expected = new HashSet<Integer>(Arrays.asList(10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71, 80, 81, 90, 91, 100, 101));
        Assert.assertEquals(expected.size(), ts.valueCount());
        Assert.assertTrue(expected.containsAll(ts.values()));
    }

    @Test
    public void testFlatMapSelectorMaxConcurrent() {
        final int m = 4;
        final AtomicInteger subscriptionCount = new AtomicInteger();
        Flowable<Integer> source = Flowable.range(1, 10).flatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                return FlowableFlatMapTest.composer(Flowable.range((t1 * 10), 2), subscriptionCount, m).subscribeOn(Schedulers.computation());
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return (t1 * 1000) + t2;
            }
        }, m);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Set<Integer> expected = new HashSet<Integer>(Arrays.asList(1010, 1011, 2020, 2021, 3030, 3031, 4040, 4041, 5050, 5051, 6060, 6061, 7070, 7071, 8080, 8081, 9090, 9091, 10100, 10101));
        Assert.assertEquals(expected.size(), ts.valueCount());
        System.out.println(("--> testFlatMapSelectorMaxConcurrent: " + (ts.values())));
        Assert.assertTrue(expected.containsAll(ts.values()));
    }

    @Test
    public void testFlatMapTransformsMaxConcurrentNormalLoop() {
        for (int i = 0; i < 1000; i++) {
            if ((i % 100) == 0) {
                System.out.println(("testFlatMapTransformsMaxConcurrentNormalLoop => " + i));
            }
            testFlatMapTransformsMaxConcurrentNormal();
        }
    }

    @Test
    public void testFlatMapTransformsMaxConcurrentNormal() {
        final int m = 2;
        final AtomicInteger subscriptionCount = new AtomicInteger();
        Flowable<Integer> onNext = FlowableFlatMapTest.composer(Flowable.fromIterable(Arrays.asList(1, 2, 3)).observeOn(Schedulers.computation()), subscriptionCount, m).subscribeOn(Schedulers.computation());
        Flowable<Integer> onComplete = FlowableFlatMapTest.composer(Flowable.fromIterable(Arrays.asList(4)), subscriptionCount, m).subscribeOn(Schedulers.computation());
        Flowable<Integer> onError = Flowable.fromIterable(Arrays.asList(5));
        Flowable<Integer> source = Flowable.fromIterable(Arrays.asList(10, 20, 30));
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Object> ts = new TestSubscriber<Object>(subscriber);
        Function<Integer, Flowable<Integer>> just = just(onNext);
        Function<Throwable, Flowable<Integer>> just2 = just(onError);
        Callable<Flowable<Integer>> just0 = just0(onComplete);
        source.flatMap(just, just2, just0, m).subscribe(ts);
        ts.awaitTerminalEvent(1, TimeUnit.SECONDS);
        ts.assertNoErrors();
        ts.assertTerminated();
        Mockito.verify(subscriber, Mockito.times(3)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(3);
        Mockito.verify(subscriber).onNext(4);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(5);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(timeout = 30000)
    public void flatMapRangeMixedAsyncLoop() {
        for (int i = 0; i < 2000; i++) {
            if ((i % 10) == 0) {
                System.out.println(("flatMapRangeAsyncLoop > " + i));
            }
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            Flowable.range(0, 1000).flatMap(new Function<Integer, Flowable<Integer>>() {
                final Random rnd = new Random();

                @Override
                public io.reactivex.Flowable<Integer> apply(Integer t) {
                    Flowable<Integer> r = Flowable.just(t);
                    if (rnd.nextBoolean()) {
                        r = r.hide();
                    }
                    return r;
                }
            }).observeOn(Schedulers.computation()).subscribe(ts);
            ts.awaitTerminalEvent(2500, TimeUnit.MILLISECONDS);
            if ((ts.completions()) == 0) {
                System.out.println(ts.valueCount());
            }
            ts.assertTerminated();
            ts.assertNoErrors();
            List<Integer> list = ts.values();
            if ((list.size()) < 1000) {
                Set<Integer> set = new HashSet<Integer>(list);
                for (int j = 0; j < 1000; j++) {
                    if (!(set.contains(j))) {
                        System.out.println((j + " missing"));
                    }
                }
            }
            Assert.assertEquals(1000, list.size());
        }
    }

    @Test
    public void flatMapIntPassthruAsync() {
        for (int i = 0; i < 1000; i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            Flowable.range(1, 1000).flatMap(new Function<Integer, Flowable<Integer>>() {
                @Override
                public io.reactivex.Flowable<Integer> apply(Integer t) {
                    return Flowable.just(1).subscribeOn(Schedulers.computation());
                }
            }).subscribe(ts);
            ts.awaitTerminalEvent(5, TimeUnit.SECONDS);
            ts.assertNoErrors();
            ts.assertComplete();
            ts.assertValueCount(1000);
        }
    }

    @Test
    public void flatMapTwoNestedSync() {
        for (final int n : new int[]{ 1, 1000, 1000000 }) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            Flowable.just(1, 2).flatMap(new Function<Integer, Flowable<Integer>>() {
                @Override
                public io.reactivex.Flowable<Integer> apply(Integer t) {
                    return Flowable.range(1, n);
                }
            }).subscribe(ts);
            System.out.println(("flatMapTwoNestedSync >> @ " + n));
            ts.assertNoErrors();
            ts.assertComplete();
            ts.assertValueCount((n * 2));
        }
    }

    @Test
    public void justEmptyMixture() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.range(0, (4 * (Flowable.bufferSize()))).flatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) {
                return (v & 1) == 0 ? Flowable.<Integer>empty() : Flowable.just(v);
            }
        }).subscribe(ts);
        ts.assertValueCount((2 * (Flowable.bufferSize())));
        ts.assertNoErrors();
        ts.assertComplete();
        int j = 1;
        for (Integer v : ts.values()) {
            Assert.assertEquals(j, v.intValue());
            j += 2;
        }
    }

    @Test
    public void rangeEmptyMixture() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.range(0, (4 * (Flowable.bufferSize()))).flatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) {
                return (v & 1) == 0 ? Flowable.<Integer>empty() : Flowable.range(v, 2);
            }
        }).subscribe(ts);
        ts.assertValueCount((4 * (Flowable.bufferSize())));
        ts.assertNoErrors();
        ts.assertComplete();
        int j = 1;
        List<Integer> list = ts.values();
        for (int i = 0; i < (list.size()); i += 2) {
            Assert.assertEquals(j, list.get(i).intValue());
            Assert.assertEquals((j + 1), list.get((i + 1)).intValue());
            j += 2;
        }
    }

    @Test
    public void justEmptyMixtureMaxConcurrent() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.range(0, (4 * (Flowable.bufferSize()))).flatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) {
                return (v & 1) == 0 ? Flowable.<Integer>empty() : Flowable.just(v);
            }
        }, 16).subscribe(ts);
        ts.assertValueCount((2 * (Flowable.bufferSize())));
        ts.assertNoErrors();
        ts.assertComplete();
        int j = 1;
        for (Integer v : ts.values()) {
            Assert.assertEquals(j, v.intValue());
            j += 2;
        }
    }

    @Test
    public void rangeEmptyMixtureMaxConcurrent() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.range(0, (4 * (Flowable.bufferSize()))).flatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) {
                return (v & 1) == 0 ? Flowable.<Integer>empty() : Flowable.range(v, 2);
            }
        }, 16).subscribe(ts);
        ts.assertValueCount((4 * (Flowable.bufferSize())));
        ts.assertNoErrors();
        ts.assertComplete();
        int j = 1;
        List<Integer> list = ts.values();
        for (int i = 0; i < (list.size()); i += 2) {
            Assert.assertEquals(j, list.get(i).intValue());
            Assert.assertEquals((j + 1), list.get((i + 1)).intValue());
            j += 2;
        }
    }

    @Test
    public void castCrashUnsubscribes() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = TestSubscriber.create();
        pp.flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer t) {
                throw new TestException();
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1;
            }
        }).subscribe(ts);
        Assert.assertTrue("Not subscribed?", pp.hasSubscribers());
        pp.onNext(1);
        Assert.assertFalse("Subscribed?", pp.hasSubscribers());
        ts.assertError(TestException.class);
    }

    @Test
    public void flatMapBiMapper() {
        Flowable.just(1).flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.just((v * 10));
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, true).test().assertResult(11);
    }

    @Test
    public void flatMapBiMapperWithError() {
        Flowable.just(1).flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.just((v * 10)).concatWith(Flowable.<Integer>error(new TestException()));
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, true).test().assertFailure(TestException.class, 11);
    }

    @Test
    public void flatMapBiMapperMaxConcurrency() {
        Flowable.just(1, 2).flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.just((v * 10));
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, true, 1).test().assertResult(11, 22);
    }

    @Test
    public void flatMapEmpty() {
        Assert.assertSame(Flowable.empty(), Flowable.empty().flatMap(new Function<Object, Publisher<Object>>() {
            @Override
            public io.reactivex.Publisher<Object> apply(Object v) throws Exception {
                return Flowable.just(v);
            }
        }));
    }

    @Test
    public void mergeScalar() {
        Flowable.merge(Flowable.just(Flowable.just(1))).test().assertResult(1);
    }

    @Test
    public void mergeScalar2() {
        Flowable.merge(Flowable.just(Flowable.just(1)).hide()).test().assertResult(1);
    }

    @Test
    public void mergeScalarEmpty() {
        Flowable.merge(Flowable.just(Flowable.empty()).hide()).test().assertResult();
    }

    @Test
    public void mergeScalarError() {
        Flowable.merge(Flowable.just(Flowable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw new TestException();
            }
        })).hide()).test().assertFailure(TestException.class);
    }

    @Test
    public void scalarReentrant() {
        final PublishProcessor<Flowable<Integer>> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    pp.onNext(Flowable.just(2));
                }
            }
        };
        Flowable.merge(pp).subscribe(ts);
        pp.onNext(Flowable.just(1));
        pp.onComplete();
        ts.assertResult(1, 2);
    }

    @Test
    public void scalarReentrant2() {
        final PublishProcessor<Flowable<Integer>> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    pp.onNext(Flowable.just(2));
                }
            }
        };
        Flowable.merge(pp, 2).subscribe(ts);
        pp.onNext(Flowable.just(1));
        pp.onComplete();
        ts.assertResult(1, 2);
    }

    @Test
    public void innerCompleteCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final TestSubscriber<Integer> ts = Flowable.merge(Flowable.just(pp)).test();
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
    public void fusedInnerThrows() {
        Flowable.just(1).hide().flatMap(new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer v) throws Exception {
                return Flowable.range(1, 2).map(new Function<Integer, Object>() {
                    @Override
                    public Object apply(Integer w) throws Exception {
                        throw new TestException();
                    }
                });
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void fusedInnerThrows2() {
        TestSubscriber<Integer> ts = Flowable.range(1, 2).hide().flatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.range(1, 2).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer w) throws Exception {
                        throw new TestException();
                    }
                });
            }
        }, true).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.errorList(ts);
        TestHelper.assertError(errors, 0, TestException.class);
        TestHelper.assertError(errors, 1, TestException.class);
    }

    @Test
    public void scalarXMap() {
        Flowable.fromCallable(Functions.justCallable(1)).flatMap(Functions.justFunction(Flowable.fromCallable(Functions.justCallable(2)))).test().assertResult(2);
    }

    @Test
    public void noCrossBoundaryFusion() {
        for (int i = 0; i < 500; i++) {
            TestSubscriber<Object> ts = Flowable.merge(Flowable.just(1).observeOn(Schedulers.single()).map(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer v) throws Exception {
                    return Thread.currentThread().getName().substring(0, 4);
                }
            }), Flowable.just(1).observeOn(Schedulers.computation()).map(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer v) throws Exception {
                    return Thread.currentThread().getName().substring(0, 4);
                }
            })).test().awaitDone(5, TimeUnit.SECONDS).assertValueCount(2);
            List<Object> list = ts.values();
            Assert.assertTrue(list.toString(), list.contains("RxSi"));
            Assert.assertTrue(list.toString(), list.contains("RxCo"));
        }
    }

    @Test
    public void cancelScalarDrainRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishProcessor<Flowable<Integer>> pp = PublishProcessor.create();
                final TestSubscriber<Integer> ts = pp.flatMap(Functions.<Flowable<Integer>>identity()).test(0);
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ts.cancel();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        pp.onComplete();
                    }
                };
                TestHelper.race(r1, r2);
                Assert.assertTrue(errors.toString(), errors.isEmpty());
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void cancelDrainRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            for (int j = 1; j < 50; j += 5) {
                List<Throwable> errors = TestHelper.trackPluginErrors();
                try {
                    final PublishProcessor<Flowable<Integer>> pp = PublishProcessor.create();
                    final TestSubscriber<Integer> ts = pp.flatMap(Functions.<Flowable<Integer>>identity()).test(0);
                    final PublishProcessor<Integer> just = PublishProcessor.create();
                    pp.onNext(just);
                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            ts.request(1);
                            ts.cancel();
                        }
                    };
                    Runnable r2 = new Runnable() {
                        @Override
                        public void run() {
                            just.onNext(1);
                        }
                    };
                    TestHelper.race(r1, r2);
                    Assert.assertTrue(errors.toString(), errors.isEmpty());
                } finally {
                    RxJavaPlugins.reset();
                }
            }
        }
    }

    @Test
    public void iterableMapperFunctionReturnsNull() {
        Flowable.just(1).flatMapIterable(new Function<Integer, Iterable<Object>>() {
            @Override
            public Iterable<Object> apply(Integer v) throws Exception {
                return null;
            }
        }, new BiFunction<Integer, Object, Object>() {
            @Override
            public Object apply(Integer v, Object w) throws Exception {
                return v;
            }
        }).test().assertFailureAndMessage(NullPointerException.class, "The mapper returned a null Iterable");
    }

    @Test
    public void combinerMapperFunctionReturnsNull() {
        Flowable.just(1).flatMap(new Function<Integer, Publisher<Object>>() {
            @Override
            public io.reactivex.Publisher<Object> apply(Integer v) throws Exception {
                return null;
            }
        }, new BiFunction<Integer, Object, Object>() {
            @Override
            public Object apply(Integer v, Object w) throws Exception {
                return v;
            }
        }).test().assertFailureAndMessage(NullPointerException.class, "The mapper returned a null Publisher");
    }

    @Test
    public void failingFusedInnerCancelsSource() {
        final AtomicInteger counter = new AtomicInteger();
        Flowable.range(1, 5).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                counter.getAndIncrement();
            }
        }).flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.<Integer>fromIterable(new Iterable<Integer>() {
                    @Override
                    public Iterator<Integer> iterator() {
                        return new Iterator<Integer>() {
                            @Override
                            public boolean hasNext() {
                                return true;
                            }

                            @Override
                            public Integer next() {
                                throw new TestException();
                            }

                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                });
            }
        }).test().assertFailure(TestException.class);
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void maxConcurrencySustained() {
        final PublishProcessor<Integer> pp1 = PublishProcessor.create();
        final PublishProcessor<Integer> pp2 = PublishProcessor.create();
        PublishProcessor<Integer> pp3 = PublishProcessor.create();
        PublishProcessor<Integer> pp4 = PublishProcessor.create();
        TestSubscriber<Integer> ts = Flowable.just(pp1, pp2, pp3, pp4).flatMap(new Function<PublishProcessor<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(PublishProcessor<Integer> v) throws Exception {
                return v;
            }
        }, 2).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                if (v == 1) {
                    // this will make sure the drain loop detects two completed
                    // inner sources and replaces them with fresh ones
                    pp1.onComplete();
                    pp2.onComplete();
                }
            }
        }).test();
        pp1.onNext(1);
        Assert.assertFalse(pp1.hasSubscribers());
        Assert.assertFalse(pp2.hasSubscribers());
        Assert.assertTrue(pp3.hasSubscribers());
        Assert.assertTrue(pp4.hasSubscribers());
        ts.dispose();
        Assert.assertFalse(pp3.hasSubscribers());
        Assert.assertFalse(pp4.hasSubscribers());
    }
}

