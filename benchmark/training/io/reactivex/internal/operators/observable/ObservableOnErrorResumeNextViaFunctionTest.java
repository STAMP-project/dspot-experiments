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


import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;


public class ObservableOnErrorResumeNextViaFunctionTest {
    @Test
    public void testResumeNextWithSynchronousExecution() {
        final AtomicReference<Throwable> receivedException = new AtomicReference<Throwable>();
        Observable<String> w = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onNext("one");
                observer.onError(new Throwable("injected failure"));
                observer.onNext("two");
                observer.onNext("three");
            }
        });
        Function<Throwable, Observable<String>> resume = new Function<Throwable, Observable<String>>() {
            @Override
            public io.reactivex.Observable<String> apply(Throwable t1) {
                receivedException.set(t1);
                return Observable.just("twoResume", "threeResume");
            }
        };
        Observable<String> observable = w.onErrorResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(observer, Mockito.times(1)).onNext("threeResume");
        Assert.assertNotNull(receivedException.get());
    }

    @Test
    public void testResumeNextWithAsyncExecution() {
        final AtomicReference<Throwable> receivedException = new AtomicReference<Throwable>();
        Subscription s = Mockito.mock(Subscription.class);
        ObservableOnErrorResumeNextViaFunctionTest.TestObservable w = new ObservableOnErrorResumeNextViaFunctionTest.TestObservable(s, "one");
        Function<Throwable, Observable<String>> resume = new Function<Throwable, Observable<String>>() {
            @Override
            public io.reactivex.Observable<String> apply(Throwable t1) {
                receivedException.set(t1);
                return Observable.just("twoResume", "threeResume");
            }
        };
        Observable<String> o = Observable.unsafeCreate(w).onErrorResumeNext(resume);
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        try {
            w.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(observer, Mockito.times(1)).onNext("threeResume");
        Assert.assertNotNull(receivedException.get());
    }

    /**
     * Test that when a function throws an exception this is propagated through onError.
     */
    @Test
    public void testFunctionThrowsError() {
        Subscription s = Mockito.mock(Subscription.class);
        ObservableOnErrorResumeNextViaFunctionTest.TestObservable w = new ObservableOnErrorResumeNextViaFunctionTest.TestObservable(s, "one");
        Function<Throwable, Observable<String>> resume = new Function<Throwable, Observable<String>>() {
            @Override
            public io.reactivex.Observable<String> apply(Throwable t1) {
                throw new RuntimeException("exception from function");
            }
        };
        Observable<String> o = Observable.unsafeCreate(w).onErrorResumeNext(resume);
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        try {
            w.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        // we should get the "one" value before the error
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        // we should have received an onError call on the Observer since the resume function threw an exception
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(0)).onComplete();
    }

    @Test
    public void testMapResumeAsyncNext() {
        // Trigger multiple failures
        Observable<String> w = Observable.just("one", "fail", "two", "three", "fail");
        // Introduce map function that fails intermittently (Map does not prevent this when the Observer is a
        // rx.operator incl onErrorResumeNextViaObservable)
        w = w.map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                if ("fail".equals(s)) {
                    throw new RuntimeException("Forced Failure");
                }
                System.out.println(("BadMapper:" + s));
                return s;
            }
        });
        Observable<String> o = w.onErrorResumeNext(new Function<Throwable, Observable<String>>() {
            @Override
            public io.reactivex.Observable<String> apply(Throwable t1) {
                return Observable.just("twoResume", "threeResume").subscribeOn(Schedulers.computation());
            }
        });
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        o.subscribe(to);
        to.awaitTerminalEvent();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(observer, Mockito.times(1)).onNext("threeResume");
    }

    static class TestObservable implements ObservableSource<String> {
        final String[] values;

        Thread t;

        TestObservable(Subscription s, String... values) {
            this.values = values;
        }

        @Override
        public void subscribe(final Observer<? super String> observer) {
            System.out.println("TestObservable subscribed to ...");
            observer.onSubscribe(Disposables.empty());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestObservable thread");
                        for (String s : values) {
                            System.out.println(("TestObservable onNext: " + s));
                            observer.onNext(s);
                        }
                        throw new RuntimeException("Forced Failure");
                    } catch (Throwable e) {
                        observer.onError(e);
                    }
                }
            });
            System.out.println("starting TestObservable thread");
            t.start();
            System.out.println("done starting TestObservable thread");
        }
    }

    @Test
    public void testBackpressure() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(0, 100000).onErrorResumeNext(new Function<Throwable, Observable<Integer>>() {
            @Override
            public io.reactivex.Observable<Integer> apply(Throwable t1) {
                return Observable.just(1);
            }
        }).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t1) {
                if (((c)++) <= 1) {
                    // slow
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return t1;
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
    }

    @Test
    public void badOtherSource() {
        checkBadSourceObservable(new Function<Observable<Integer>, Object>() {
            @Override
            public Object apply(Observable<Integer> o) throws Exception {
                return Observable.error(new IOException()).onErrorResumeNext(Functions.justFunction(o));
            }
        }, false, 1, 1, 1);
    }
}

