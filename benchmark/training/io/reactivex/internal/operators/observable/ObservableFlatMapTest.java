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
package io.reactivex.internal.operators.observable;


import ObservableFlatMap.MergeObserver;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableFlatMapTest {
    @Test
    public void testNormal() {
        Observer<Object> o = mockObserver();
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
        Observable.fromIterable(source).flatMapIterable(func, resFunc).subscribe(o);
        for (Integer s : source) {
            for (Integer v : list) {
                Mockito.verify(o).onNext((s | v));
            }
        }
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testCollectionFunctionThrows() {
        Observer<Object> o = mockObserver();
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
        Observable.fromIterable(source).flatMapIterable(func, resFunc).subscribe(o);
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testResultFunctionThrows() {
        Observer<Object> o = mockObserver();
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
        Observable.fromIterable(source).flatMapIterable(func, resFunc).subscribe(o);
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testMergeError() {
        Observer<Object> o = mockObserver();
        Function<Integer, Observable<Integer>> func = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return io.reactivex.Observable.error(new TestException());
            }
        };
        BiFunction<Integer, Integer, Integer> resFunc = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 | t2;
            }
        };
        List<Integer> source = Arrays.asList(16, 32, 64);
        Observable.fromIterable(source).flatMap(func, resFunc).subscribe(o);
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void testFlatMapTransformsNormal() {
        Observable<Integer> onNext = fromIterable(Arrays.asList(1, 2, 3));
        Observable<Integer> onComplete = fromIterable(Arrays.asList(4));
        Observable<Integer> onError = fromIterable(Arrays.asList(5));
        Observable<Integer> source = fromIterable(Arrays.asList(10, 20, 30));
        Observer<Object> o = mockObserver();
        source.flatMap(just(onNext), just(onError), just0(onComplete)).subscribe(o);
        Mockito.verify(o, Mockito.times(3)).onNext(1);
        Mockito.verify(o, Mockito.times(3)).onNext(2);
        Mockito.verify(o, Mockito.times(3)).onNext(3);
        Mockito.verify(o).onNext(4);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(5);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFlatMapTransformsException() {
        Observable<Integer> onNext = fromIterable(Arrays.asList(1, 2, 3));
        Observable<Integer> onComplete = fromIterable(Arrays.asList(4));
        Observable<Integer> onError = fromIterable(Arrays.asList(5));
        Observable<Integer> source = Observable.concat(fromIterable(Arrays.asList(10, 20, 30)), Observable.<Integer>error(new RuntimeException("Forced failure!")));
        Observer<Object> o = mockObserver();
        source.flatMap(just(onNext), just(onError), just0(onComplete)).subscribe(o);
        Mockito.verify(o, Mockito.times(3)).onNext(1);
        Mockito.verify(o, Mockito.times(3)).onNext(2);
        Mockito.verify(o, Mockito.times(3)).onNext(3);
        Mockito.verify(o).onNext(5);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(4);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFlatMapTransformsOnNextFuncThrows() {
        Observable<Integer> onComplete = fromIterable(Arrays.asList(4));
        Observable<Integer> onError = fromIterable(Arrays.asList(5));
        Observable<Integer> source = fromIterable(Arrays.asList(10, 20, 30));
        Observer<Object> o = mockObserver();
        source.flatMap(funcThrow(1, onError), just(onError), just0(onComplete)).subscribe(o);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testFlatMapTransformsOnErrorFuncThrows() {
        Observable<Integer> onNext = fromIterable(Arrays.asList(1, 2, 3));
        Observable<Integer> onComplete = fromIterable(Arrays.asList(4));
        Observable<Integer> onError = fromIterable(Arrays.asList(5));
        Observable<Integer> source = error(new TestException());
        Observer<Object> o = mockObserver();
        source.flatMap(just(onNext), funcThrow(((Throwable) (null)), onError), just0(onComplete)).subscribe(o);
        Mockito.verify(o).onError(ArgumentMatchers.any(CompositeException.class));
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testFlatMapTransformsOnCompletedFuncThrows() {
        Observable<Integer> onNext = fromIterable(Arrays.asList(1, 2, 3));
        Observable<Integer> onComplete = fromIterable(Arrays.asList(4));
        Observable<Integer> onError = fromIterable(Arrays.asList(5));
        Observable<Integer> source = Observable.fromIterable(Arrays.<Integer>asList());
        Observer<Object> o = mockObserver();
        source.flatMap(just(onNext), just(onError), funcThrow0(onComplete)).subscribe(o);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testFlatMapTransformsMergeException() {
        Observable<Integer> onNext = error(new TestException());
        Observable<Integer> onComplete = fromIterable(Arrays.asList(4));
        Observable<Integer> onError = fromIterable(Arrays.asList(5));
        Observable<Integer> source = fromIterable(Arrays.asList(10, 20, 30));
        Observer<Object> o = mockObserver();
        source.flatMap(just(onNext), just(onError), funcThrow0(onComplete)).subscribe(o);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testFlatMapMaxConcurrent() {
        final int m = 4;
        final AtomicInteger subscriptionCount = new AtomicInteger();
        Observable<Integer> source = range(1, 10).flatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return composer(io.reactivex.Observable.range((t1 * 10), 2), subscriptionCount, m).subscribeOn(Schedulers.computation());
            }
        }, m);
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Set<Integer> expected = new HashSet<Integer>(Arrays.asList(10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71, 80, 81, 90, 91, 100, 101));
        Assert.assertEquals(expected.size(), to.valueCount());
        Assert.assertTrue(expected.containsAll(to.values()));
    }

    @Test
    public void testFlatMapSelectorMaxConcurrent() {
        final int m = 4;
        final AtomicInteger subscriptionCount = new AtomicInteger();
        Observable<Integer> source = range(1, 10).flatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return composer(io.reactivex.Observable.range((t1 * 10), 2), subscriptionCount, m).subscribeOn(Schedulers.computation());
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return (t1 * 1000) + t2;
            }
        }, m);
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Set<Integer> expected = new HashSet<Integer>(Arrays.asList(1010, 1011, 2020, 2021, 3030, 3031, 4040, 4041, 5050, 5051, 6060, 6061, 7070, 7071, 8080, 8081, 9090, 9091, 10100, 10101));
        Assert.assertEquals(expected.size(), to.valueCount());
        System.out.println(("--> testFlatMapSelectorMaxConcurrent: " + (to.values())));
        Assert.assertTrue(expected.containsAll(to.values()));
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
        Observable<Integer> onNext = composer(fromIterable(Arrays.asList(1, 2, 3)).observeOn(Schedulers.computation()), subscriptionCount, m).subscribeOn(Schedulers.computation());
        Observable<Integer> onComplete = composer(fromIterable(Arrays.asList(4)), subscriptionCount, m).subscribeOn(Schedulers.computation());
        Observable<Integer> onError = fromIterable(Arrays.asList(5));
        Observable<Integer> source = fromIterable(Arrays.asList(10, 20, 30));
        Observer<Object> o = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(o);
        Function<Throwable, Observable<Integer>> just = just(onError);
        source.flatMap(just(onNext), just, just0(onComplete), m).subscribe(to);
        to.awaitTerminalEvent(1, TimeUnit.SECONDS);
        to.assertNoErrors();
        to.assertTerminated();
        Mockito.verify(o, Mockito.times(3)).onNext(1);
        Mockito.verify(o, Mockito.times(3)).onNext(2);
        Mockito.verify(o, Mockito.times(3)).onNext(3);
        Mockito.verify(o).onNext(4);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(5);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(timeout = 30000)
    public void flatMapRangeMixedAsyncLoop() {
        for (int i = 0; i < 2000; i++) {
            if ((i % 10) == 0) {
                System.out.println(("flatMapRangeAsyncLoop > " + i));
            }
            TestObserver<Integer> to = new TestObserver<Integer>();
            range(0, 1000).flatMap(new Function<Integer, Observable<Integer>>() {
                final Random rnd = new Random();

                @Override
                public Observable<Integer> apply(Integer t) {
                    Observable<Integer> r = io.reactivex.Observable.just(t);
                    if (rnd.nextBoolean()) {
                        r = r.hide();
                    }
                    return r;
                }
            }).observeOn(Schedulers.computation()).subscribe(to);
            to.awaitTerminalEvent(2500, TimeUnit.MILLISECONDS);
            if ((to.completions()) == 0) {
                System.out.println(to.valueCount());
            }
            to.assertTerminated();
            to.assertNoErrors();
            List<Integer> list = to.values();
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
            TestObserver<Integer> to = new TestObserver<Integer>();
            range(1, 1000).flatMap(new Function<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> apply(Integer t) {
                    return io.reactivex.Observable.just(1).subscribeOn(Schedulers.computation());
                }
            }).subscribe(to);
            to.awaitTerminalEvent(5, TimeUnit.SECONDS);
            to.assertNoErrors();
            to.assertComplete();
            to.assertValueCount(1000);
        }
    }

    @Test
    public void flatMapTwoNestedSync() {
        for (final int n : new int[]{ 1, 1000, 1000000 }) {
            TestObserver<Integer> to = new TestObserver<Integer>();
            Observable.just(1, 2).flatMap(new Function<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> apply(Integer t) {
                    return io.reactivex.Observable.range(1, n);
                }
            }).subscribe(to);
            System.out.println(("flatMapTwoNestedSync >> @ " + n));
            to.assertNoErrors();
            to.assertComplete();
            to.assertValueCount((n * 2));
        }
    }

    @Test
    public void flatMapBiMapper() {
        just(1).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return Observable.just((v * 10));
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
        just(1).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return just((v * 10)).concatWith(<Integer>error(new TestException()));
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
        Observable.just(1, 2).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return Observable.just((v * 10));
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
        Assert.assertSame(empty(), empty().flatMap(new Function<Object, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Object v) throws Exception {
                return Observable.just(v);
            }
        }));
    }

    @Test
    public void mergeScalar() {
        Observable.merge(Observable.just(just(1))).test().assertResult(1);
    }

    @Test
    public void mergeScalar2() {
        Observable.merge(Observable.just(just(1)).hide()).test().assertResult(1);
    }

    @Test
    public void mergeScalarEmpty() {
        Observable.merge(Observable.just(empty()).hide()).test().assertResult();
    }

    @Test
    public void mergeScalarError() {
        Observable.merge(Observable.just(fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw new TestException();
            }
        })).hide()).test().assertFailure(TestException.class);
    }

    @Test
    public void scalarReentrant() {
        final PublishSubject<Observable<Integer>> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(just(2));
                }
            }
        };
        Observable.merge(ps).subscribe(to);
        ps.onNext(just(1));
        ps.onComplete();
        to.assertResult(1, 2);
    }

    @Test
    public void scalarReentrant2() {
        final PublishSubject<Observable<Integer>> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onNext(just(2));
                }
            }
        };
        Observable.merge(ps, 2).subscribe(to);
        ps.onNext(just(1));
        ps.onComplete();
        to.assertResult(1, 2);
    }

    @Test
    public void innerCompleteCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Integer> to = Observable.merge(Observable.just(ps)).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void fusedInnerThrows() {
        just(1).hide().flatMap(new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer v) throws Exception {
                return range(1, 2).map(new Function<Integer, Object>() {
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
        TestObserver<Integer> to = range(1, 2).hide().flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return range(1, 2).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer w) throws Exception {
                        throw new TestException();
                    }
                });
            }
        }, true).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.errorList(to);
        TestHelper.assertError(errors, 0, TestException.class);
        TestHelper.assertError(errors, 1, TestException.class);
    }

    @Test
    public void noCrossBoundaryFusion() {
        for (int i = 0; i < 500; i++) {
            TestObserver<Object> to = Observable.merge(just(1).observeOn(Schedulers.single()).map(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer v) throws Exception {
                    return Thread.currentThread().getName().substring(0, 4);
                }
            }), just(1).observeOn(Schedulers.computation()).map(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer v) throws Exception {
                    return Thread.currentThread().getName().substring(0, 4);
                }
            })).test().awaitDone(5, TimeUnit.SECONDS).assertValueCount(2);
            List<Object> list = to.values();
            Assert.assertTrue(list.toString(), list.contains("RxSi"));
            Assert.assertTrue(list.toString(), list.contains("RxCo"));
        }
    }

    @Test
    public void cancelScalarDrainRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishSubject<Observable<Integer>> ps = PublishSubject.create();
                final TestObserver<Integer> to = ps.flatMap(Functions.<Observable<Integer>>identity()).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        to.cancel();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps.onComplete();
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
                    final PublishSubject<Observable<Integer>> ps = PublishSubject.create();
                    final TestObserver<Integer> to = ps.flatMap(Functions.<Observable<Integer>>identity()).test();
                    final PublishSubject<Integer> just = PublishSubject.create();
                    final PublishSubject<Integer> just2 = PublishSubject.create();
                    ps.onNext(just);
                    ps.onNext(just2);
                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            just2.onNext(1);
                            to.cancel();
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
        just(1).flatMapIterable(new Function<Integer, Iterable<Object>>() {
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
        just(1).flatMap(new Function<Integer, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Integer v) throws Exception {
                return null;
            }
        }, new BiFunction<Integer, Object, Object>() {
            @Override
            public Object apply(Integer v, Object w) throws Exception {
                return v;
            }
        }).test().assertFailureAndMessage(NullPointerException.class, "The mapper returned a null ObservableSource");
    }

    @Test
    public void failingFusedInnerCancelsSource() {
        final AtomicInteger counter = new AtomicInteger();
        range(1, 5).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                counter.getAndIncrement();
            }
        }).flatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer v) throws Exception {
                return io.reactivex.Observable.<Integer>fromIterable(new Iterable<Integer>() {
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
    public void scalarQueueNoOverflow() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final PublishSubject<Integer> ps = PublishSubject.create();
            TestObserver<Integer> to = ps.flatMap(new Function<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> apply(Integer v) throws Exception {
                    return io.reactivex.Observable.just((v + 1));
                }
            }, 1).subscribeWith(new TestObserver<Integer>() {
                @Override
                public void onNext(Integer t) {
                    super.onNext(t);
                    if (t == 1) {
                        for (int i = 1; i < 10; i++) {
                            ps.onNext(i);
                        }
                        ps.onComplete();
                    }
                }
            });
            ps.onNext(0);
            if (!(errors.isEmpty())) {
                to.onError(new CompositeException(errors));
            }
            to.assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void scalarQueueNoOverflowHidden() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.flatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer v) throws Exception {
                return io.reactivex.Observable.just((v + 1)).hide();
            }
        }, 1).subscribeWith(new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    for (int i = 1; i < 10; i++) {
                        ps.onNext(i);
                    }
                    ps.onComplete();
                }
            }
        });
        ps.onNext(0);
        to.assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void fusedSourceCrashResumeWithNextSource() {
        final UnicastSubject<Integer> fusedSource = UnicastSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>();
        MergeObserver<Integer, Integer> merger = new MergeObserver<Integer, Integer>(to, new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t) throws Exception {
                if (t == 0) {
                    return fusedSource.map(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer v) throws Exception {
                            throw new TestException();
                        }
                    }).compose(TestHelper.<Integer>observableStripBoundary());
                }
                return io.reactivex.Observable.range((10 * t), 5);
            }
        }, true, Integer.MAX_VALUE, 128);
        merger.onSubscribe(Disposables.empty());
        merger.getAndIncrement();
        merger.onNext(0);
        merger.onNext(1);
        merger.onNext(2);
        Assert.assertTrue(fusedSource.hasObservers());
        fusedSource.onNext((-1));
        merger.drainLoop();
        to.assertValuesOnly(10, 11, 12, 13, 14, 20, 21, 22, 23, 24);
    }

    @Test
    public void maxConcurrencySustained() {
        final PublishSubject<Integer> ps1 = PublishSubject.create();
        final PublishSubject<Integer> ps2 = PublishSubject.create();
        PublishSubject<Integer> ps3 = PublishSubject.create();
        PublishSubject<Integer> ps4 = PublishSubject.create();
        TestObserver<Integer> to = Observable.just(ps1, ps2, ps3, ps4).flatMap(new Function<PublishSubject<Integer>, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(PublishSubject<Integer> v) throws Exception {
                return v;
            }
        }, 2).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                if (v == 1) {
                    // this will make sure the drain loop detects two completed
                    // inner sources and replaces them with fresh ones
                    ps1.onComplete();
                    ps2.onComplete();
                }
            }
        }).test();
        ps1.onNext(1);
        Assert.assertFalse(ps1.hasObservers());
        Assert.assertFalse(ps2.hasObservers());
        Assert.assertTrue(ps3.hasObservers());
        Assert.assertTrue(ps4.hasObservers());
        to.dispose();
        Assert.assertFalse(ps3.hasObservers());
        Assert.assertFalse(ps4.hasObservers());
    }
}

