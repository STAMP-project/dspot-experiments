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


import io.reactivex.disposables.Disposable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableOnErrorResumeNextViaObservableTest {
    @Test
    public void testResumeNext() {
        Disposable upstream = Mockito.mock(Disposable.class);
        // Trigger failure on second element
        ObservableOnErrorResumeNextViaObservableTest.TestObservable f = new ObservableOnErrorResumeNextViaObservableTest.TestObservable(upstream, "one", "fail", "two", "three");
        Observable<String> w = Observable.unsafeCreate(f);
        Observable<String> resume = Observable.just("twoResume", "threeResume");
        Observable<String> observable = w.onErrorResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            f.t.join();
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
    }

    @Test
    public void testMapResumeAsyncNext() {
        Disposable sr = Mockito.mock(Disposable.class);
        // Trigger multiple failures
        Observable<String> w = Observable.just("one", "fail", "two", "three", "fail");
        // Resume Observable is async
        ObservableOnErrorResumeNextViaObservableTest.TestObservable f = new ObservableOnErrorResumeNextViaObservableTest.TestObservable(sr, "twoResume", "threeResume");
        Observable<String> resume = Observable.unsafeCreate(f);
        // Introduce map function that fails intermittently (Map does not prevent this when the Observer is a
        // rx.operator incl onErrorResumeNextViaObservable)
        w = w.map(new io.reactivex.functions.Function<String, String>() {
            @Override
            public String apply(String s) {
                if ("fail".equals(s)) {
                    throw new RuntimeException("Forced Failure");
                }
                System.out.println(("BadMapper:" + s));
                return s;
            }
        });
        Observable<String> observable = w.onErrorResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            f.t.join();
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
    }

    static class TestObservable implements ObservableSource<String> {
        final Disposable upstream;

        final String[] values;

        Thread t;

        TestObservable(Disposable upstream, String... values) {
            this.upstream = upstream;
            this.values = values;
        }

        @Override
        public void subscribe(final Observer<? super String> observer) {
            System.out.println("TestObservable subscribed to ...");
            observer.onSubscribe(upstream);
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestObservable thread");
                        for (String s : values) {
                            if ("fail".equals(s)) {
                                throw new RuntimeException("Forced Failure");
                            }
                            System.out.println(("TestObservable onNext: " + s));
                            observer.onNext(s);
                        }
                        System.out.println("TestObservable onComplete");
                        observer.onComplete();
                    } catch (Throwable e) {
                        System.out.println(("TestObservable onError: " + e));
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
        Observable.range(0, 100000).onErrorResumeNext(Observable.just(1)).observeOn(io.reactivex.schedulers.Schedulers.computation()).map(new io.reactivex.functions.Function<Integer, Integer>() {
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
}

