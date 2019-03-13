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
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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


public class ObservableZipTest {
    BiFunction<String, String, String> concat2Strings;

    PublishSubject<String> s1;

    PublishSubject<String> s2;

    Observable<String> zipped;

    Observer<String> observer;

    InOrder inOrder;

    @SuppressWarnings("unchecked")
    @Test
    public void testCollectionSizeDifferentThanFunction() {
        Function<Object[], String> zipr = Functions.toFunction(getConcatStringIntegerIntArrayZipr());
        // Function3<String, Integer, int[], String>
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        @SuppressWarnings("rawtypes")
        Collection ws = Collections.singleton(Observable.just("one", "two"));
        Observable<String> w = Observable.zip(ws, zipr);
        w.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
    }

    @Test
    public void testStartpingDifferentLengthObservableSequences1() {
        Observer<String> w = mockObserver();
        ObservableZipTest.TestObservable w1 = new ObservableZipTest.TestObservable();
        ObservableZipTest.TestObservable w2 = new ObservableZipTest.TestObservable();
        ObservableZipTest.TestObservable w3 = new ObservableZipTest.TestObservable();
        Observable<String> zipW = Observable.zip(unsafeCreate(w1), unsafeCreate(w2), unsafeCreate(w3), getConcat3StringsZipr());
        zipW.subscribe(w);
        /* simulate sending data */
        // once for w1
        w1.observer.onNext("1a");
        w1.observer.onComplete();
        // twice for w2
        w2.observer.onNext("2a");
        w2.observer.onNext("2b");
        w2.observer.onComplete();
        // 4 times for w3
        w3.observer.onNext("3a");
        w3.observer.onNext("3b");
        w3.observer.onNext("3c");
        w3.observer.onNext("3d");
        w3.observer.onComplete();
        /* we should have been called 1 time on the Observer */
        InOrder io = Mockito.inOrder(w);
        io.verify(w).onNext("1a2a3a");
        onComplete();
    }

    @Test
    public void testStartpingDifferentLengthObservableSequences2() {
        Observer<String> w = mockObserver();
        ObservableZipTest.TestObservable w1 = new ObservableZipTest.TestObservable();
        ObservableZipTest.TestObservable w2 = new ObservableZipTest.TestObservable();
        ObservableZipTest.TestObservable w3 = new ObservableZipTest.TestObservable();
        Observable<String> zipW = Observable.zip(unsafeCreate(w1), unsafeCreate(w2), unsafeCreate(w3), getConcat3StringsZipr());
        zipW.subscribe(w);
        /* simulate sending data */
        // 4 times for w1
        w1.observer.onNext("1a");
        w1.observer.onNext("1b");
        w1.observer.onNext("1c");
        w1.observer.onNext("1d");
        w1.observer.onComplete();
        // twice for w2
        w2.observer.onNext("2a");
        w2.observer.onNext("2b");
        w2.observer.onComplete();
        // 1 times for w3
        w3.observer.onNext("3a");
        w3.observer.onComplete();
        /* we should have been called 1 time on the Observer */
        InOrder io = Mockito.inOrder(w);
        io.verify(w).onNext("1a2a3a");
        onComplete();
    }

    BiFunction<Object, Object, String> zipr2 = new BiFunction<Object, Object, String>() {
        @Override
        public String apply(Object t1, Object t2) {
            return ("" + t1) + t2;
        }
    };

    Function3<Object, Object, Object, String> zipr3 = new Function3<Object, Object, Object, String>() {
        @Override
        public String apply(Object t1, Object t2, Object t3) {
            return (("" + t1) + t2) + t3;
        }
    };

    /**
     * Testing internal private logic due to the complexity so I want to use TDD to test as a I build it rather than relying purely on the overall functionality expected by the public methods.
     */
    @Test
    public void testAggregatorSimple() {
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<String> r2 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable.zip(r1, r2, zipr2).subscribe(observer);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("hello");
        r2.onNext("world");
        InOrder inOrder = Mockito.inOrder(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        inOrder.verify(observer, Mockito.times(1)).onNext("helloworld");
        r1.onNext("hello ");
        r2.onNext("again");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        inOrder.verify(observer, Mockito.times(1)).onNext("hello again");
        r1.onComplete();
        r2.onComplete();
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testAggregatorDifferentSizedResultsWithOnComplete() {
        /* create the aggregator which will execute the zip function when all Observables provide values */
        /* define an Observer to receive aggregated events */
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<String> r2 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable.zip(r1, r2, zipr2).subscribe(observer);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("hello");
        r2.onNext("world");
        r2.onComplete();
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.times(1)).onNext("helloworld");
        onComplete();
        r1.onNext("hi");
        r1.onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
    }

    @Test
    public void testAggregateMultipleTypes() {
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<Integer> r2 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable.zip(r1, r2, zipr2).subscribe(observer);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("hello");
        r2.onNext(1);
        r2.onComplete();
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.times(1)).onNext("hello1");
        onComplete();
        r1.onNext("hi");
        r1.onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
    }

    @Test
    public void testAggregate3Types() {
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<Integer> r2 = PublishSubject.create();
        PublishSubject<List<Integer>> r3 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable.zip(r1, r2, r3, zipr3).subscribe(observer);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("hello");
        r2.onNext(2);
        r3.onNext(Arrays.asList(5, 6, 7));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("hello2[5, 6, 7]");
    }

    @Test
    public void testAggregatorsWithDifferentSizesAndTiming() {
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<String> r2 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable.zip(r1, r2, zipr2).subscribe(observer);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("one");
        r1.onNext("two");
        r1.onNext("three");
        r2.onNext("A");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("oneA");
        r1.onNext("four");
        r1.onComplete();
        r2.onNext("B");
        Mockito.verify(observer, Mockito.times(1)).onNext("twoB");
        r2.onNext("C");
        Mockito.verify(observer, Mockito.times(1)).onNext("threeC");
        r2.onNext("D");
        Mockito.verify(observer, Mockito.times(1)).onNext("fourD");
        r2.onNext("E");
        Mockito.verify(observer, Mockito.never()).onNext("E");
        r2.onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
    }

    @Test
    public void testAggregatorError() {
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<String> r2 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable.zip(r1, r2, zipr2).subscribe(observer);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("hello");
        r2.onNext("world");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("helloworld");
        r1.onError(new RuntimeException(""));
        r1.onNext("hello");
        r2.onNext("again");
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        // we don't want to be called again after an error
        Mockito.verify(observer, Mockito.times(0)).onNext("helloagain");
    }

    @Test
    public void testAggregatorUnsubscribe() {
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<String> r2 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        Observable.zip(r1, r2, zipr2).subscribe(to);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("hello");
        r2.onNext("world");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("helloworld");
        to.dispose();
        r1.onNext("hello");
        r2.onNext("again");
        Mockito.verify(observer, Mockito.times(0)).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        // we don't want to be called again after an error
        Mockito.verify(observer, Mockito.times(0)).onNext("helloagain");
    }

    @Test
    public void testAggregatorEarlyCompletion() {
        PublishSubject<String> r1 = PublishSubject.create();
        PublishSubject<String> r2 = PublishSubject.create();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable.zip(r1, r2, zipr2).subscribe(observer);
        /* simulate the Observables pushing data into the aggregator */
        r1.onNext("one");
        r1.onNext("two");
        r1.onComplete();
        r2.onNext("A");
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        inOrder.verify(observer, Mockito.times(1)).onNext("oneA");
        r2.onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyString());
    }

    @Test
    public void testStart2Types() {
        BiFunction<String, Integer, String> zipr = getConcatStringIntegerZipr();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable<String> w = Observable.zip(Observable.just("one", "two"), Observable.just(2, 3, 4), zipr);
        w.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("one2");
        Mockito.verify(observer, Mockito.times(1)).onNext("two3");
        Mockito.verify(observer, Mockito.never()).onNext("4");
    }

    @Test
    public void testStart3Types() {
        Function3<String, Integer, int[], String> zipr = getConcatStringIntegerIntArrayZipr();
        /* define an Observer to receive aggregated events */
        Observer<String> observer = mockObserver();
        Observable<String> w = Observable.zip(Observable.just("one", "two"), just(2), Observable.just(new int[]{ 4, 5, 6 }), zipr);
        w.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("one2[4, 5, 6]");
        Mockito.verify(observer, Mockito.never()).onNext("two");
    }

    @Test
    public void testOnNextExceptionInvokesOnError() {
        BiFunction<Integer, Integer, Integer> zipr = getDivideZipr();
        Observer<Integer> observer = mockObserver();
        Observable<Integer> w = Observable.zip(Observable.just(10, 20, 30), Observable.just(0, 1, 2), zipr);
        w.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testOnFirstCompletion() {
        PublishSubject<String> oA = PublishSubject.create();
        PublishSubject<String> oB = PublishSubject.create();
        Observer<String> obs = mockObserver();
        Observable<String> o = Observable.zip(oA, oB, getConcat2Strings());
        o.subscribe(obs);
        InOrder io = Mockito.inOrder(obs);
        oA.onNext("a1");
        io.verify(obs, Mockito.never()).onNext(ArgumentMatchers.anyString());
        oB.onNext("b1");
        io.verify(obs, Mockito.times(1)).onNext("a1-b1");
        oB.onNext("b2");
        io.verify(obs, Mockito.never()).onNext(ArgumentMatchers.anyString());
        oA.onNext("a2");
        io.verify(obs, Mockito.times(1)).onNext("a2-b2");
        oA.onNext("a3");
        oA.onNext("a4");
        oA.onNext("a5");
        oA.onComplete();
        // SHOULD ONCOMPLETE BE EMITTED HERE INSTEAD OF WAITING
        // FOR B3, B4, B5 TO BE EMITTED?
        oB.onNext("b3");
        oB.onNext("b4");
        oB.onNext("b5");
        io.verify(obs, Mockito.times(1)).onNext("a3-b3");
        io.verify(obs, Mockito.times(1)).onNext("a4-b4");
        io.verify(obs, Mockito.times(1)).onNext("a5-b5");
        // WE RECEIVE THE ONCOMPLETE HERE
        onComplete();
        oB.onNext("b6");
        oB.onNext("b7");
        oB.onNext("b8");
        oB.onNext("b9");
        // never completes (infinite stream for example)
        // we should receive nothing else despite oB continuing after oA completed
        io.verifyNoMoreInteractions();
    }

    @Test
    public void testOnErrorTermination() {
        PublishSubject<String> oA = PublishSubject.create();
        PublishSubject<String> oB = PublishSubject.create();
        Observer<String> obs = mockObserver();
        Observable<String> o = Observable.zip(oA, oB, getConcat2Strings());
        o.subscribe(obs);
        InOrder io = Mockito.inOrder(obs);
        oA.onNext("a1");
        io.verify(obs, Mockito.never()).onNext(ArgumentMatchers.anyString());
        oB.onNext("b1");
        io.verify(obs, Mockito.times(1)).onNext("a1-b1");
        oB.onNext("b2");
        io.verify(obs, Mockito.never()).onNext(ArgumentMatchers.anyString());
        oA.onNext("a2");
        io.verify(obs, Mockito.times(1)).onNext("a2-b2");
        oA.onNext("a3");
        oA.onNext("a4");
        oA.onNext("a5");
        oA.onError(new RuntimeException("forced failure"));
        // it should emit failure immediately
        io.verify(obs, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        oB.onNext("b3");
        oB.onNext("b4");
        oB.onNext("b5");
        oB.onNext("b6");
        oB.onNext("b7");
        oB.onNext("b8");
        oB.onNext("b9");
        // never completes (infinite stream for example)
        // we should receive nothing else despite oB continuing after oA completed
        io.verifyNoMoreInteractions();
    }

    private static class TestObservable implements ObservableSource<String> {
        Observer<? super String> observer;

        @Override
        public void subscribe(Observer<? super String> observer) {
            // just store the variable where it can be accessed so we can manually trigger it
            this.observer = observer;
            observer.onSubscribe(Disposables.empty());
        }
    }

    @Test
    public void testFirstCompletesThenSecondInfinite() {
        s1.onNext("a");
        s1.onNext("b");
        s1.onComplete();
        s2.onNext("1");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s2.onNext("2");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSecondInfiniteThenFirstCompletes() {
        s2.onNext("1");
        s2.onNext("2");
        s1.onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s1.onNext("b");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        s1.onComplete();
        onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSecondCompletesThenFirstInfinite() {
        s2.onNext("1");
        s2.onNext("2");
        s2.onComplete();
        s1.onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s1.onNext("b");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstInfiniteThenSecondCompletes() {
        s1.onNext("a");
        s1.onNext("b");
        s2.onNext("1");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s2.onNext("2");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        s2.onComplete();
        onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstFails() {
        s2.onNext("a");
        s1.onError(new RuntimeException("Forced failure"));
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        s2.onNext("b");
        s1.onNext("1");
        s1.onNext("2");
        onComplete();
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSecondFails() {
        s1.onNext("a");
        s1.onNext("b");
        s2.onError(new RuntimeException("Forced failure"));
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        s2.onNext("1");
        s2.onNext("2");
        onComplete();
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testStartWithOnCompletedTwice() {
        // issue: https://groups.google.com/forum/#!topic/rxjava/79cWTv3TFp0
        // The problem is the original "zip" implementation does not wrap
        // an internal observer with a SafeSubscriber. However, in the "zip",
        // it may calls "onComplete" twice. That breaks the Rx contract.
        // This test tries to emulate this case.
        // As "TestHelper.mockObserver()" will create an instance in the package "rx",
        // we need to wrap "TestHelper.mockObserver()" with an observer instance
        // which is in the package "rx.operators".
        final Observer<Integer> observer = mockObserver();
        Observable.zip(just(1), just(1), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) {
                return a + b;
            }
        }).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onComplete() {
                observer.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onNext(Integer args) {
                observer.onNext(args);
            }
        });
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testStart() {
        Observable<String> os = io.reactivex.internal.operators.observable.OBSERVABLE_OF_5_INTEGERS.zipWith(io.reactivex.internal.operators.observable.OBSERVABLE_OF_5_INTEGERS, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer a, Integer b) {
                return (a + "-") + b;
            }
        });
        final ArrayList<String> list = new ArrayList<String>();
        os.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
                list.add(s);
            }
        });
        Assert.assertEquals(5, list.size());
        Assert.assertEquals("1-1", list.get(0));
        Assert.assertEquals("2-2", list.get(1));
        Assert.assertEquals("5-5", list.get(4));
    }

    @Test
    public void testStartAsync() throws InterruptedException {
        Observable<String> os = ASYNC_OBSERVABLE_OF_INFINITE_INTEGERS(new CountDownLatch(1)).zipWith(ASYNC_OBSERVABLE_OF_INFINITE_INTEGERS(new CountDownLatch(1)), new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer a, Integer b) {
                return (a + "-") + b;
            }
        }).take(5);
        TestObserver<String> to = new TestObserver<String>();
        os.subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(5, to.valueCount());
        Assert.assertEquals("1-1", to.values().get(0));
        Assert.assertEquals("2-2", to.values().get(1));
        Assert.assertEquals("5-5", to.values().get(4));
    }

    @Test
    public void testStartInfiniteAndFinite() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch infiniteObservable = new CountDownLatch(1);
        Observable<String> os = io.reactivex.internal.operators.observable.OBSERVABLE_OF_5_INTEGERS.zipWith(ASYNC_OBSERVABLE_OF_INFINITE_INTEGERS(infiniteObservable), new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer a, Integer b) {
                return (a + "-") + b;
            }
        });
        final ArrayList<String> list = new ArrayList<String>();
        os.subscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
                list.add(s);
            }
        });
        latch.await(1000, TimeUnit.MILLISECONDS);
        if (!(infiniteObservable.await(2000, TimeUnit.MILLISECONDS))) {
            throw new RuntimeException("didn't unsubscribe");
        }
        Assert.assertEquals(5, list.size());
        Assert.assertEquals("1-1", list.get(0));
        Assert.assertEquals("2-2", list.get(1));
        Assert.assertEquals("5-5", list.get(4));
    }

    @Test
    public void testEmitMaterializedNotifications() {
        Observable<Notification<Integer>> oi = Observable.just(1, 2, 3).materialize();
        Observable<Notification<String>> os = Observable.just("a", "b", "c").materialize();
        Observable<String> o = Observable.zip(oi, os, new BiFunction<Notification<Integer>, Notification<String>, String>() {
            @Override
            public String apply(Notification<Integer> t1, Notification<String> t2) {
                return ((((((ObservableZipTest.kind(t1)) + "_") + (ObservableZipTest.value(t1))) + "-") + (ObservableZipTest.kind(t2))) + "_") + (ObservableZipTest.value(t2));
            }
        });
        final ArrayList<String> list = new ArrayList<String>();
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
                list.add(s);
            }
        });
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("OnNext_1-OnNext_a", list.get(0));
        Assert.assertEquals("OnNext_2-OnNext_b", list.get(1));
        Assert.assertEquals("OnNext_3-OnNext_c", list.get(2));
        Assert.assertEquals("OnComplete_null-OnComplete_null", list.get(3));
    }

    @Test
    public void testStartEmptyObservables() {
        Observable<String> o = Observable.zip(<Integer>empty(), <String>empty(), new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer t1, String t2) {
                return (t1 + "-") + t2;
            }
        });
        final ArrayList<String> list = new ArrayList<String>();
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
                list.add(s);
            }
        });
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testStartEmptyList() {
        final Object invoked = new Object();
        Collection<Observable<Object>> observables = Collections.emptyList();
        Observable<Object> o = Observable.zip(observables, new Function<Object[], Object>() {
            @Override
            public Object apply(final Object[] args) {
                Assert.assertEquals("No argument should have been passed", 0, args.length);
                return invoked;
            }
        });
        TestObserver<Object> to = new TestObserver<Object>();
        o.subscribe(to);
        to.awaitTerminalEvent(200, TimeUnit.MILLISECONDS);
        to.assertNoValues();
    }

    /**
     * Expect NoSuchElementException instead of blocking forever as zip should emit onComplete and no onNext
     * and last() expects at least a single response.
     */
    @Test(expected = NoSuchElementException.class)
    public void testStartEmptyListBlocking() {
        final Object invoked = new Object();
        Collection<Observable<Object>> observables = Collections.emptyList();
        Observable<Object> o = Observable.zip(observables, new Function<Object[], Object>() {
            @Override
            public Object apply(final Object[] args) {
                Assert.assertEquals("No argument should have been passed", 0, args.length);
                return invoked;
            }
        });
        o.blockingLast();
    }

    @Test
    public void testDownstreamBackpressureRequestsWithFiniteSyncObservables() {
        AtomicInteger generatedA = new AtomicInteger();
        AtomicInteger generatedB = new AtomicInteger();
        Observable<Integer> o1 = createInfiniteObservable(generatedA).take(((bufferSize()) * 2));
        Observable<Integer> o2 = createInfiniteObservable(generatedB).take(((bufferSize()) * 2));
        TestObserver<String> to = new TestObserver<String>();
        Observable.zip(o1, o2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer t1, Integer t2) {
                return (t1 + "-") + t2;
            }
        }).observeOn(Schedulers.computation()).take(((bufferSize()) * 2)).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(((bufferSize()) * 2), to.valueCount());
        System.out.println(((("Generated => A: " + (generatedA.get())) + " B: ") + (generatedB.get())));
        Assert.assertTrue(((generatedA.get()) < ((bufferSize()) * 3)));
        Assert.assertTrue(((generatedB.get()) < ((bufferSize()) * 3)));
    }

    Observable<Integer> OBSERVABLE_OF_5_INTEGERS = OBSERVABLE_OF_5_INTEGERS(new AtomicInteger());

    @Test(timeout = 30000)
    public void testIssue1812() {
        // https://github.com/ReactiveX/RxJava/issues/1812
        Observable<Integer> zip1 = Observable.zip(range(0, 1026), range(0, 1026), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });
        Observable<Integer> zip2 = Observable.zip(zip1, range(0, 1026), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });
        List<Integer> expected = new ArrayList<Integer>();
        for (int i = 0; i < 1026; i++) {
            expected.add((i * 3));
        }
        Assert.assertEquals(expected, zip2.toList().blockingGet());
    }

    @Test(timeout = 10000)
    public void testZipRace() {
        long startTime = System.currentTimeMillis();
        Observable<Integer> src = just(1).subscribeOn(Schedulers.computation());
        // now try and generate a hang by zipping src with itself repeatedly. A
        // time limit of 9 seconds ( 1 second less than the test timeout) is
        // used so that this test will not timeout on slow machines.
        int i = 0;
        while ((((System.currentTimeMillis()) - startTime) < 9000) && ((i++) < 100000)) {
            int value = Observable.zip(src, src, new BiFunction<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer t1, Integer t2) {
                    return t1 + (t2 * 10);
                }
            }).blockingSingle(0);
            Assert.assertEquals(11, value);
        } 
    }

    @Test
    public void zip2() {
        Observable.zip(just(1), just(2), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return ("" + a) + b;
            }
        }).test().assertResult("12");
    }

    @Test
    public void zip3() {
        Observable.zip(just(1), just(2), just(3), new Function3<Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c) throws Exception {
                return (("" + a) + b) + c;
            }
        }).test().assertResult("123");
    }

    @Test
    public void zip4() {
        Observable.zip(just(1), just(2), just(3), just(4), new Function4<Integer, Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c, Integer d) throws Exception {
                return ((("" + a) + b) + c) + d;
            }
        }).test().assertResult("1234");
    }

    @Test
    public void zip5() {
        Observable.zip(just(1), just(2), just(3), just(4), just(5), new Function5<Integer, Integer, Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c, Integer d, Integer e) throws Exception {
                return (((("" + a) + b) + c) + d) + e;
            }
        }).test().assertResult("12345");
    }

    @Test
    public void zip6() {
        Observable.zip(just(1), just(2), just(3), just(4), just(5), just(6), new Function6<Integer, Integer, Integer, Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c, Integer d, Integer e, Integer f) throws Exception {
                return ((((("" + a) + b) + c) + d) + e) + f;
            }
        }).test().assertResult("123456");
    }

    @Test
    public void zip7() {
        Observable.zip(just(1), just(2), just(3), just(4), just(5), just(6), just(7), new Function7<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c, Integer d, Integer e, Integer f, Integer g) throws Exception {
                return (((((("" + a) + b) + c) + d) + e) + f) + g;
            }
        }).test().assertResult("1234567");
    }

    @Test
    public void zip8() {
        Observable.zip(just(1), just(2), just(3), just(4), just(5), just(6), just(7), just(8), new Function8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c, Integer d, Integer e, Integer f, Integer g, Integer h) throws Exception {
                return ((((((("" + a) + b) + c) + d) + e) + f) + g) + h;
            }
        }).test().assertResult("12345678");
    }

    @Test
    public void zip9() {
        Observable.zip(just(1), just(2), just(3), just(4), just(5), just(6), just(7), just(8), just(9), new Function9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c, Integer d, Integer e, Integer f, Integer g, Integer h, Integer i) throws Exception {
                return (((((((("" + a) + b) + c) + d) + e) + f) + g) + h) + i;
            }
        }).test().assertResult("123456789");
    }

    @Test
    public void zip2DelayError() {
        Observable.zip(just(1).concatWith(<Integer>error(new TestException())), just(2), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return ("" + a) + b;
            }
        }, true).test().assertFailure(TestException.class, "12");
    }

    @Test
    public void zip2Prefetch() {
        Observable.zip(range(1, 9), range(21, 9), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return ("" + a) + b;
            }
        }, false, 2).takeLast(1).test().assertResult("929");
    }

    @Test
    public void zip2DelayErrorPrefetch() {
        Observable.zip(range(1, 9).concatWith(<Integer>error(new TestException())), range(21, 9), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return ("" + a) + b;
            }
        }, true, 2).skip(8).test().assertFailure(TestException.class, "929");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void zipArrayEmpty() {
        Assert.assertSame(empty(), Observable.zipArray(Functions.<Object[]>identity(), false, 16));
    }

    @Test
    public void zipArrayMany() {
        @SuppressWarnings("unchecked")
        Observable<Integer>[] arr = new Observable[10];
        Arrays.fill(arr, just(1));
        Observable.zip(Arrays.asList(arr), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return Arrays.toString(a);
            }
        }).test().assertResult("[1, 1, 1, 1, 1, 1, 1, 1, 1, 1]");
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.zip(just(1), just(1), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }));
    }

    @Test
    public void noCrossBoundaryFusion() {
        for (int i = 0; i < 500; i++) {
            TestObserver<List<Object>> to = Observable.zip(just(1).observeOn(Schedulers.single()).map(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer v) throws Exception {
                    return Thread.currentThread().getName().substring(0, 4);
                }
            }), just(1).observeOn(Schedulers.computation()).map(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer v) throws Exception {
                    return Thread.currentThread().getName().substring(0, 4);
                }
            }), new BiFunction<Object, Object, List<Object>>() {
                @Override
                public List<Object> apply(Object t1, Object t2) throws Exception {
                    return Arrays.asList(t1, t2);
                }
            }).test().awaitDone(5, TimeUnit.SECONDS).assertValueCount(1);
            List<Object> list = to.values().get(0);
            Assert.assertTrue(list.toString(), list.contains("RxSi"));
            Assert.assertTrue(list.toString(), list.contains("RxCo"));
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
        Observable.zip(ps1, ps2, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) throws Exception {
                return t1 + t2;
            }
        }).subscribe(to);
        ps1.onNext(1);
        ps2.onNext(2);
        to.assertResult(3);
    }
}

