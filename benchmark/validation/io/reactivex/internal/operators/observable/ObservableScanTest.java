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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableScanTest {
    @Test
    public void testScanIntegersWithInitialValue() {
        Observer<String> observer = mockObserver();
        Observable<Integer> o = Observable.just(1, 2, 3);
        Observable<String> m = o.scan("", new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) {
                return s + (n.toString());
            }
        });
        m.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onNext("");
        Mockito.verify(observer, Mockito.times(1)).onNext("1");
        Mockito.verify(observer, Mockito.times(1)).onNext("12");
        Mockito.verify(observer, Mockito.times(1)).onNext("123");
        Mockito.verify(observer, Mockito.times(4)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testScanIntegersWithoutInitialValue() {
        Observer<Integer> observer = mockObserver();
        Observable<Integer> o = Observable.just(1, 2, 3);
        Observable<Integer> m = o.scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });
        m.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onNext(0);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext(3);
        Mockito.verify(observer, Mockito.times(1)).onNext(6);
        Mockito.verify(observer, Mockito.times(3)).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testScanIntegersWithoutInitialValueAndOnlyOneValue() {
        Observer<Integer> observer = mockObserver();
        Observable<Integer> o = Observable.just(1);
        Observable<Integer> m = o.scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });
        m.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onNext(0);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void shouldNotEmitUntilAfterSubscription() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        range(1, 100).scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                // this will cause request(1) when 0 is emitted
                return t1 > 0;
            }
        }).subscribe(to);
        Assert.assertEquals(100, to.values().size());
    }

    @Test
    public void testNoBackpressureWithInitialValue() {
        final AtomicInteger count = new AtomicInteger();
        range(1, 100).scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer t) {
                count.incrementAndGet();
            }
        });
        // we only expect to receive 101 as we'll receive all 100 + the initial value
        Assert.assertEquals(101, count.get());
    }

    /**
     * This uses the public API collect which uses scan under the covers.
     */
    @Test
    public void testSeedFactory() {
        Observable<List<Integer>> o = range(1, 10).collect(new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new BiConsumer<List<Integer>, Integer>() {
            @Override
            public void accept(List<Integer> list, Integer t2) {
                list.add(t2);
            }
        }).toObservable().takeLast(1);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), o.blockingSingle());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), o.blockingSingle());
    }

    @Test
    public void testScanWithRequestOne() {
        Observable<Integer> o = just(1, 2).scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).take(1);
        TestObserver<Integer> observer = new TestObserver<Integer>();
        o.subscribe(observer);
        observer.assertValue(0);
        observer.assertTerminated();
        observer.assertNoErrors();
    }

    @Test
    public void testInitialValueEmittedNoProducer() {
        PublishSubject<Integer> source = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(to);
        to.assertNoErrors();
        to.assertNotComplete();
        to.assertValue(0);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().scan(new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }));
        TestHelper.checkDisposed(PublishSubject.<Integer>create().scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.scan(new BiFunction<Object, Object, Object>() {
                    @Override
                    public Object apply(Object a, Object b) throws Exception {
                        return a;
                    }
                });
            }
        });
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.scan(0, new BiFunction<Object, Object, Object>() {
                    @Override
                    public Object apply(Object a, Object b) throws Exception {
                        return a;
                    }
                });
            }
        });
    }

    @Test
    public void error() {
        Observable.error(new TestException()).scan(new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceObservable(new Function<Observable<Object>, Object>() {
            @Override
            public Object apply(Observable<Object> o) throws Exception {
                return o.scan(0, new BiFunction<Object, Object, Object>() {
                    @Override
                    public Object apply(Object a, Object b) throws Exception {
                        return a;
                    }
                });
            }
        }, false, 1, 1, 0, 0);
    }

    @Test
    public void testScanFunctionThrowsAndUpstreamErrorsDoesNotResultInTwoTerminalEvents() {
        final RuntimeException err = new RuntimeException();
        final RuntimeException err2 = new RuntimeException();
        final List<Throwable> list = new CopyOnWriteArrayList<Throwable>();
        final Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                list.add(t);
            }
        };
        try {
            RxJavaPlugins.setErrorHandler(errorConsumer);
            Observable.unsafeCreate(new ObservableSource<Integer>() {
                @Override
                public void subscribe(Observer<? super Integer> o) {
                    io.reactivex.disposables.Disposable d = io.reactivex.disposables.Disposables.empty();
                    o.onSubscribe(d);
                    o.onNext(1);
                    o.onNext(2);
                    o.onError(err2);
                }
            }).scan(new BiFunction<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer t1, Integer t2) throws Exception {
                    throw err;
                }
            }).test().assertError(err).assertValue(1);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void testScanFunctionThrowsAndUpstreamCompletesDoesNotResultInTwoTerminalEvents() {
        final RuntimeException err = new RuntimeException();
        Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> o) {
                io.reactivex.disposables.Disposable d = io.reactivex.disposables.Disposables.empty();
                o.onSubscribe(d);
                o.onNext(1);
                o.onNext(2);
                o.onComplete();
            }
        }).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) throws Exception {
                throw err;
            }
        }).test().assertError(err).assertValue(1);
    }

    @Test
    public void testScanFunctionThrowsAndUpstreamEmitsOnNextResultsInScanFunctionBeingCalledOnlyOnce() {
        final RuntimeException err = new RuntimeException();
        final AtomicInteger count = new AtomicInteger();
        unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> o) {
                io.reactivex.disposables.Disposable d = io.reactivex.disposables.Disposables.empty();
                o.onSubscribe(d);
                o.onNext(1);
                o.onNext(2);
                o.onNext(3);
            }
        }).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) throws Exception {
                count.incrementAndGet();
                throw err;
            }
        }).test().assertError(err).assertValue(1);
        Assert.assertEquals(1, count.get());
    }
}

