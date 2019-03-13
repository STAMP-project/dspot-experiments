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
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableCombineLatestTest {
    @Test
    public void testCombineLatestWithFunctionThatThrowsAnException() {
        Observer<String> w = mockObserver();
        PublishSubject<String> w1 = PublishSubject.create();
        PublishSubject<String> w2 = PublishSubject.create();
        Observable<String> combined = Observable.combineLatest(w1, w2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String v1, String v2) {
                throw new RuntimeException("I don't work.");
            }
        });
        combined.subscribe(w);
        w1.onNext("first value of w1");
        w2.onNext("first value of w2");
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(w, Mockito.times(1)).onError(Mockito.<RuntimeException>any());
    }

    @Test
    public void testCombineLatestDifferentLengthObservableSequences1() {
        Observer<String> w = mockObserver();
        PublishSubject<String> w1 = PublishSubject.create();
        PublishSubject<String> w2 = PublishSubject.create();
        PublishSubject<String> w3 = PublishSubject.create();
        Observable<String> combineLatestW = Observable.combineLatest(w1, w2, w3, getConcat3StringsCombineLatestFunction());
        combineLatestW.subscribe(w);
        /* simulate sending data */
        // once for w1
        w1.onNext("1a");
        w2.onNext("2a");
        w3.onNext("3a");
        w1.onComplete();
        // twice for w2
        w2.onNext("2b");
        w2.onComplete();
        // 4 times for w3
        w3.onNext("3b");
        w3.onNext("3c");
        w3.onNext("3d");
        w3.onComplete();
        /* we should have been called 4 times on the Observer */
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w).onNext("1a2a3a");
        inOrder.verify(w).onNext("1a2b3a");
        inOrder.verify(w).onNext("1a2b3b");
        inOrder.verify(w).onNext("1a2b3c");
        inOrder.verify(w).onNext("1a2b3d");
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testCombineLatestDifferentLengthObservableSequences2() {
        Observer<String> w = mockObserver();
        PublishSubject<String> w1 = PublishSubject.create();
        PublishSubject<String> w2 = PublishSubject.create();
        PublishSubject<String> w3 = PublishSubject.create();
        Observable<String> combineLatestW = Observable.combineLatest(w1, w2, w3, getConcat3StringsCombineLatestFunction());
        combineLatestW.subscribe(w);
        /* simulate sending data */
        // 4 times for w1
        w1.onNext("1a");
        w1.onNext("1b");
        w1.onNext("1c");
        w1.onNext("1d");
        w1.onComplete();
        // twice for w2
        w2.onNext("2a");
        w2.onNext("2b");
        w2.onComplete();
        // 1 times for w3
        w3.onNext("3a");
        w3.onComplete();
        /* we should have been called 1 time only on the Observer since we only combine the "latest" we don't go back and loop through others once completed */
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext("1d2b3a");
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testCombineLatestWithInterleavingSequences() {
        Observer<String> w = mockObserver();
        PublishSubject<String> w1 = PublishSubject.create();
        PublishSubject<String> w2 = PublishSubject.create();
        PublishSubject<String> w3 = PublishSubject.create();
        Observable<String> combineLatestW = Observable.combineLatest(w1, w2, w3, getConcat3StringsCombineLatestFunction());
        combineLatestW.subscribe(w);
        /* simulate sending data */
        w1.onNext("1a");
        w2.onNext("2a");
        w2.onNext("2b");
        w3.onNext("3a");
        w1.onNext("1b");
        w2.onNext("2c");
        w2.onNext("2d");
        w3.onNext("3b");
        w1.onComplete();
        w2.onComplete();
        w3.onComplete();
        /* we should have been called 5 times on the Observer */
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w).onNext("1a2b3a");
        inOrder.verify(w).onNext("1b2b3a");
        inOrder.verify(w).onNext("1b2c3a");
        inOrder.verify(w).onNext("1b2d3a");
        inOrder.verify(w).onNext("1b2d3b");
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testCombineLatest2Types() {
        BiFunction<String, Integer, String> combineLatestFunction = getConcatStringIntegerCombineLatestFunction();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable<String> w = Observable.combineLatest(Observable.just("one", "two"), Observable.just(2, 3, 4), combineLatestFunction);
        w.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("two2");
        Mockito.verify(observer, Mockito.times(1)).onNext("two3");
        Mockito.verify(observer, Mockito.times(1)).onNext("two4");
    }

    @Test
    public void testCombineLatest3TypesA() {
        Function3<String, Integer, int[], String> combineLatestFunction = getConcatStringIntegerIntArrayCombineLatestFunction();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable<String> w = Observable.combineLatest(Observable.just("one", "two"), just(2), Observable.just(new int[]{ 4, 5, 6 }), combineLatestFunction);
        w.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("two2[4, 5, 6]");
    }

    @Test
    public void testCombineLatest3TypesB() {
        Function3<String, Integer, int[], String> combineLatestFunction = getConcatStringIntegerIntArrayCombineLatestFunction();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable<String> w = Observable.combineLatest(Observable.just("one"), just(2), Observable.just(new int[]{ 4, 5, 6 }, new int[]{ 7, 8 }), combineLatestFunction);
        w.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("one2[4, 5, 6]");
        Mockito.verify(observer, Mockito.times(1)).onNext("one2[7, 8]");
    }

    BiFunction<Integer, Integer, Integer> or = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            return t1 | t2;
        }
    };

    @Test
    public void combineSimple() {
        PublishSubject<Integer> a = PublishSubject.create();
        PublishSubject<Integer> b = PublishSubject.create();
        Observable<Integer> source = Observable.combineLatest(a, b, or);
        Observer<Object> observer = mockObserver();
        InOrder inOrder = Mockito.inOrder(observer);
        source.subscribe(observer);
        a.onNext(1);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        a.onNext(2);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        b.onNext(16);
        inOrder.verify(observer, Mockito.times(1)).onNext(18);
        b.onNext(32);
        inOrder.verify(observer, Mockito.times(1)).onNext(34);
        b.onComplete();
        onComplete();
        a.onComplete();
        onComplete();
        a.onNext(3);
        b.onNext(48);
        a.onComplete();
        b.onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void combineMultipleObservers() {
        PublishSubject<Integer> a = PublishSubject.create();
        PublishSubject<Integer> b = PublishSubject.create();
        Observable<Integer> source = Observable.combineLatest(a, b, or);
        Observer<Object> observer1 = mockObserver();
        Observer<Object> observer2 = mockObserver();
        InOrder inOrder1 = Mockito.inOrder(observer1);
        InOrder inOrder2 = Mockito.inOrder(observer2);
        source.subscribe(observer1);
        source.subscribe(observer2);
        a.onNext(1);
        inOrder1.verify(observer1, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder2.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.any());
        a.onNext(2);
        inOrder1.verify(observer1, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder2.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.any());
        b.onNext(16);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(18);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(18);
        b.onNext(32);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(34);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(34);
        b.onComplete();
        onComplete();
        onComplete();
        a.onComplete();
        onComplete();
        onComplete();
        a.onNext(3);
        b.onNext(48);
        a.onComplete();
        b.onComplete();
        inOrder1.verifyNoMoreInteractions();
        inOrder2.verifyNoMoreInteractions();
        Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstNeverProduces() {
        PublishSubject<Integer> a = PublishSubject.create();
        PublishSubject<Integer> b = PublishSubject.create();
        Observable<Integer> source = Observable.combineLatest(a, b, or);
        Observer<Object> observer = mockObserver();
        InOrder inOrder = Mockito.inOrder(observer);
        source.subscribe(observer);
        b.onNext(16);
        b.onNext(32);
        a.onComplete();
        onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSecondNeverProduces() {
        PublishSubject<Integer> a = PublishSubject.create();
        PublishSubject<Integer> b = PublishSubject.create();
        Observable<Integer> source = Observable.combineLatest(a, b, or);
        Observer<Object> observer = mockObserver();
        InOrder inOrder = Mockito.inOrder(observer);
        source.subscribe(observer);
        a.onNext(1);
        a.onNext(2);
        b.onComplete();
        a.onComplete();
        onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test1ToNSources() {
        int n = 30;
        Function<Object[], List<Object>> func = new Function<Object[], List<Object>>() {
            @Override
            public List<Object> apply(Object[] args) {
                return Arrays.asList(args);
            }
        };
        for (int i = 1; i <= n; i++) {
            System.out.println((("test1ToNSources: " + i) + " sources"));
            List<Observable<Integer>> sources = new java.util.ArrayList<Observable<Integer>>();
            List<Object> values = new java.util.ArrayList<Object>();
            for (int j = 0; j < i; j++) {
                sources.add(just(j));
                values.add(j);
            }
            Observable<List<Object>> result = Observable.combineLatest(sources, func);
            Observer<List<Object>> o = mockObserver();
            result.subscribe(o);
            Mockito.verify(o).onNext(values);
            onComplete();
            Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test(timeout = 5000)
    public void test1ToNSourcesScheduled() throws InterruptedException {
        int n = 10;
        Function<Object[], List<Object>> func = new Function<Object[], List<Object>>() {
            @Override
            public List<Object> apply(Object[] args) {
                return Arrays.asList(args);
            }
        };
        for (int i = 1; i <= n; i++) {
            System.out.println((("test1ToNSourcesScheduled: " + i) + " sources"));
            List<Observable<Integer>> sources = new java.util.ArrayList<Observable<Integer>>();
            List<Object> values = new java.util.ArrayList<Object>();
            for (int j = 0; j < i; j++) {
                sources.add(just(j).subscribeOn(Schedulers.io()));
                values.add(j);
            }
            Observable<List<Object>> result = Observable.combineLatest(sources, func);
            final Observer<List<Object>> o = mockObserver();
            final CountDownLatch cdl = new CountDownLatch(1);
            Observer<List<Object>> observer = new DefaultObserver<List<Object>>() {
                @Override
                public void onNext(List<Object> t) {
                    o.onNext(t);
                }

                @Override
                public void onError(Throwable e) {
                    o.onError(e);
                    cdl.countDown();
                }

                @Override
                public void onComplete() {
                    o.onComplete();
                    cdl.countDown();
                }
            };
            result.subscribe(observer);
            cdl.await();
            Mockito.verify(o).onNext(values);
            onComplete();
            Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void test2SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, new BiFunction<Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2) {
                return Arrays.asList(t1, t2);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test3SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<Integer> s3 = just(3);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, s3, new Function3<Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3) {
                return Arrays.asList(t1, t2, t3);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2, 3));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test4SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<Integer> s3 = just(3);
        Observable<Integer> s4 = just(4);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, s3, s4, new Function4<Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4) {
                return Arrays.asList(t1, t2, t3, t4);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2, 3, 4));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test5SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<Integer> s3 = just(3);
        Observable<Integer> s4 = just(4);
        Observable<Integer> s5 = just(5);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, s3, s4, s5, new Function5<Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5) {
                return Arrays.asList(t1, t2, t3, t4, t5);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2, 3, 4, 5));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test6SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<Integer> s3 = just(3);
        Observable<Integer> s4 = just(4);
        Observable<Integer> s5 = just(5);
        Observable<Integer> s6 = just(6);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, s3, s4, s5, s6, new Function6<Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2, 3, 4, 5, 6));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test7SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<Integer> s3 = just(3);
        Observable<Integer> s4 = just(4);
        Observable<Integer> s5 = just(5);
        Observable<Integer> s6 = just(6);
        Observable<Integer> s7 = just(7);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, s3, s4, s5, s6, s7, new Function7<Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6, Integer t7) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6, t7);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test8SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<Integer> s3 = just(3);
        Observable<Integer> s4 = just(4);
        Observable<Integer> s5 = just(5);
        Observable<Integer> s6 = just(6);
        Observable<Integer> s7 = just(7);
        Observable<Integer> s8 = just(8);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, s3, s4, s5, s6, s7, s8, new Function8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6, Integer t7, Integer t8) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test9SourcesOverload() {
        Observable<Integer> s1 = just(1);
        Observable<Integer> s2 = just(2);
        Observable<Integer> s3 = just(3);
        Observable<Integer> s4 = just(4);
        Observable<Integer> s5 = just(5);
        Observable<Integer> s6 = just(6);
        Observable<Integer> s7 = just(7);
        Observable<Integer> s8 = just(8);
        Observable<Integer> s9 = just(9);
        Observable<List<Integer>> result = Observable.combineLatest(s1, s2, s3, s4, s5, s6, s7, s8, s9, new Function9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6, Integer t7, Integer t8, Integer t9) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testZeroSources() {
        Observable<Object> result = Observable.combineLatest(Collections.<Observable<Object>>emptyList(), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] args) {
                return args;
            }
        });
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        onComplete();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testWithCombineLatestIssue1717() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger count = new AtomicInteger();
        final int SIZE = 2000;
        Observable<Long> timer = interval(0, 1, TimeUnit.MILLISECONDS).observeOn(Schedulers.newThread()).doOnEach(new Consumer<Notification<Long>>() {
            @Override
            public void accept(Notification<Long> n) {
                // System.out.println(n);
                if ((count.incrementAndGet()) >= SIZE) {
                    latch.countDown();
                }
            }
        }).take(SIZE);
        TestObserver<Long> to = new TestObserver<Long>();
        Observable.combineLatest(timer, <Integer>never(), new BiFunction<Long, Integer, Long>() {
            @Override
            public Long apply(Long t1, Integer t2) {
                return t1;
            }
        }).subscribe(to);
        if (!(latch.await((SIZE + 1000), TimeUnit.MILLISECONDS))) {
            Assert.fail("timed out");
        }
        Assert.assertEquals(SIZE, count.get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestArrayOfSources() {
        Observable.combineLatest(new ObservableSource[]{ Observable.just(1), Observable.just(2) }, new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return Arrays.toString(a);
            }
        }).test().assertResult("[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorArrayOfSources() {
        Observable.combineLatestDelayError(new ObservableSource[]{ Observable.just(1), Observable.just(2) }, new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return Arrays.toString(a);
            }
        }).test().assertResult("[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorArrayOfSourcesWithError() {
        Observable.combineLatestDelayError(new ObservableSource[]{ Observable.just(1), just(2).concatWith(Observable.<Integer>error(new TestException())) }, new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return Arrays.toString(a);
            }
        }).test().assertFailure(TestException.class, "[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorIterableOfSources() {
        Observable.combineLatestDelayError(Arrays.asList(just(1), just(2)), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return Arrays.toString(a);
            }
        }).test().assertResult("[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorIterableOfSourcesWithError() {
        Observable.combineLatestDelayError(Arrays.asList(just(1), just(2).concatWith(Observable.<Integer>error(new TestException()))), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return Arrays.toString(a);
            }
        }).test().assertFailure(TestException.class, "[1, 2]");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestEmpty() {
        Assert.assertSame(empty(), Observable.combineLatest(new ObservableSource[0], Functions.<Object[]>identity(), 16));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestDelayErrorEmpty() {
        Assert.assertSame(empty(), Observable.combineLatestDelayError(new ObservableSource[0], Functions.<Object[]>identity(), 16));
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Observable.combineLatest(never(), never(), new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }));
    }

    @Test
    public void cancelWhileSubscribing() {
        final TestObserver<Object> to = new TestObserver<Object>();
        Observable.combineLatest(just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                to.cancel();
            }
        }), never(), new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }).subscribe(to);
    }

    @Test
    public void combineAsync() {
        Observable<Integer> source = range(1, 1000).subscribeOn(Schedulers.computation());
        Observable.combineLatest(source, source, new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }).take(500).test().awaitDone(5, TimeUnit.SECONDS).assertNoErrors().assertComplete();
    }

    @Test
    public void error() {
        Observable.combineLatest(never(), Observable.error(new TestException()), new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }).test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorDelayed() {
        Observable.combineLatestDelayError(new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return a;
            }
        }, 128, Observable.error(new TestException()), just(1)).test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorDelayed2() {
        Observable.combineLatestDelayError(new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return a;
            }
        }, 128, Observable.error(new TestException()).startWith(1), empty()).test().assertFailure(TestException.class);
    }

    @Test
    public void onErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishSubject<Integer> ps1 = PublishSubject.create();
                final PublishSubject<Integer> ps2 = PublishSubject.create();
                TestObserver<Integer> to = Observable.combineLatest(ps1, ps2, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer a, Integer b) throws Exception {
                        return a;
                    }
                }).test();
                final TestException ex1 = new TestException();
                final TestException ex2 = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onError(ex1);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                if ((to.errorCount()) != 0) {
                    if ((to.errors().get(0)) instanceof CompositeException) {
                        to.assertSubscribed().assertNotComplete().assertNoValues();
                        for (Throwable e : TestHelper.errorList(to)) {
                            Assert.assertTrue(e.toString(), (e instanceof TestException));
                        }
                    } else {
                        to.assertFailure(TestException.class);
                    }
                }
                for (Throwable e : errors) {
                    Assert.assertTrue(e.toString(), ((e.getCause()) instanceof TestException));
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void dontSubscribeIfDone() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final int[] count = new int[]{ 0 };
            Observable.combineLatest(empty(), Observable.error(new TestException()).doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                    (count[0])++;
                }
            }), new BiFunction<Object, Object, Object>() {
                @Override
                public Object apply(Object a, Object b) throws Exception {
                    return 0;
                }
            }).test().assertResult();
            Assert.assertEquals(0, count[0]);
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void dontSubscribeIfDone2() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final int[] count = new int[]{ 0 };
            Observable.combineLatestDelayError(Arrays.asList(empty(), Observable.error(new TestException()).doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                    (count[0])++;
                }
            })), new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] a) throws Exception {
                    return 0;
                }
            }).test().assertResult();
            Assert.assertEquals(0, count[0]);
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combine2Observable2Errors() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Object> testObserver = TestObserver.create();
            TestScheduler testScheduler = new TestScheduler();
            Observable<Integer> emptyObservable = Observable.timer(10, TimeUnit.MILLISECONDS, testScheduler).flatMap(new Function<Long, ObservableSource<Integer>>() {
                @Override
                public io.reactivex.ObservableSource<Integer> apply(Long aLong) throws Exception {
                    return error(new Exception());
                }
            });
            Observable<Object> errorObservable = Observable.timer(100, TimeUnit.MILLISECONDS, testScheduler).map(new Function<Long, Object>() {
                @Override
                public Object apply(Long aLong) throws Exception {
                    throw new Exception();
                }
            });
            Observable.combineLatestDelayError(Arrays.asList(emptyObservable.doOnEach(new Consumer<Notification<Integer>>() {
                @Override
                public void accept(Notification<Integer> integerNotification) throws Exception {
                    System.out.println(("emptyObservable: " + integerNotification));
                }
            }).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    System.out.println("emptyObservable: doFinally");
                }
            }), errorObservable.doOnEach(new Consumer<Notification<Object>>() {
                @Override
                public void accept(Notification<Object> integerNotification) throws Exception {
                    System.out.println(("errorObservable: " + integerNotification));
                }
            }).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    System.out.println("errorObservable: doFinally");
                }
            })), new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    return 0;
                }
            }).doOnEach(new Consumer<Notification<Object>>() {
                @Override
                public void accept(Notification<Object> integerNotification) throws Exception {
                    System.out.println(("combineLatestDelayError: " + integerNotification));
                }
            }).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    System.out.println("combineLatestDelayError: doFinally");
                }
            }).subscribe(testObserver);
            testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            testObserver.awaitTerminalEvent();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void eagerDispose() {
        final PublishSubject<Integer> ps1 = PublishSubject.create();
        final PublishSubject<Integer> ps2 = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                if (ps1.hasObservers()) {
                    onError(new IllegalStateException("ps1 not disposed"));
                } else
                    if (ps2.hasObservers()) {
                        onError(new IllegalStateException("ps2 not disposed"));
                    } else {
                        onComplete();
                    }

            }
        };
        Observable.combineLatest(ps1, ps2, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) throws Exception {
                return t1 + t2;
            }
        }).subscribe(to);
        ps1.onNext(1);
        ps2.onNext(2);
        to.assertResult(3);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void syncFirstErrorsAfterItemDelayError() {
        Observable.combineLatestDelayError(Arrays.asList(just(21).concatWith(Observable.<Integer>error(new TestException())), just(21).delay(100, TimeUnit.MILLISECONDS)), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return ((Integer) (a[0])) + ((Integer) (a[1]));
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class, 42);
    }
}

