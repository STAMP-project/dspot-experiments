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


import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableOnExceptionResumeNextViaObservableTest {
    @Test
    public void testResumeNextWithException() {
        // Trigger failure on second element
        ObservableOnExceptionResumeNextViaObservableTest.TestObservable f = new ObservableOnExceptionResumeNextViaObservableTest.TestObservable("one", "EXCEPTION", "two", "three");
        Observable<String> w = Observable.unsafeCreate(f);
        Observable<String> resume = Observable.just("twoResume", "threeResume");
        Observable<String> observable = w.onExceptionResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(observer, Mockito.times(1)).onNext("threeResume");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testResumeNextWithRuntimeException() {
        // Trigger failure on second element
        ObservableOnExceptionResumeNextViaObservableTest.TestObservable f = new ObservableOnExceptionResumeNextViaObservableTest.TestObservable("one", "RUNTIMEEXCEPTION", "two", "three");
        Observable<String> w = Observable.unsafeCreate(f);
        Observable<String> resume = Observable.just("twoResume", "threeResume");
        Observable<String> observable = w.onExceptionResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(observer, Mockito.times(1)).onNext("threeResume");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testThrowablePassesThru() {
        // Trigger failure on second element
        ObservableOnExceptionResumeNextViaObservableTest.TestObservable f = new ObservableOnExceptionResumeNextViaObservableTest.TestObservable("one", "THROWABLE", "two", "three");
        Observable<String> w = Observable.unsafeCreate(f);
        Observable<String> resume = Observable.just("twoResume", "threeResume");
        Observable<String> observable = w.onExceptionResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.never()).onNext("twoResume");
        Mockito.verify(observer, Mockito.never()).onNext("threeResume");
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testErrorPassesThru() {
        // Trigger failure on second element
        ObservableOnExceptionResumeNextViaObservableTest.TestObservable f = new ObservableOnExceptionResumeNextViaObservableTest.TestObservable("one", "ERROR", "two", "three");
        Observable<String> w = Observable.unsafeCreate(f);
        Observable<String> resume = Observable.just("twoResume", "threeResume");
        Observable<String> observable = w.onExceptionResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.never()).onNext("twoResume");
        Mockito.verify(observer, Mockito.never()).onNext("threeResume");
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testMapResumeAsyncNext() {
        // Trigger multiple failures
        Observable<String> w = Observable.just("one", "fail", "two", "three", "fail");
        // Resume Observable is async
        ObservableOnExceptionResumeNextViaObservableTest.TestObservable f = new ObservableOnExceptionResumeNextViaObservableTest.TestObservable("twoResume", "threeResume");
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
        Observable<String> observable = w.onExceptionResumeNext(resume);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            // if the thread gets started (which it shouldn't if it's working correctly)
            if ((f.t) != null) {
                f.t.join();
            }
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(observer, Mockito.times(1)).onNext("threeResume");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testBackpressure() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(0, 100000).onExceptionResumeNext(Observable.just(1)).observeOn(Schedulers.computation()).map(new io.reactivex.functions.Function<Integer, Integer>() {
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

    private static class TestObservable implements ObservableSource<String> {
        final String[] values;

        Thread t;

        TestObservable(String... values) {
            this.values = values;
        }

        @Override
        public void subscribe(final Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            System.out.println("TestObservable subscribed to ...");
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestObservable thread");
                        for (String s : values) {
                            if ("EXCEPTION".equals(s)) {
                                throw new Exception("Forced Exception");
                            } else
                                if ("RUNTIMEEXCEPTION".equals(s)) {
                                    throw new RuntimeException("Forced RuntimeException");
                                } else
                                    if ("ERROR".equals(s)) {
                                        throw new Error("Forced Error");
                                    } else
                                        if ("THROWABLE".equals(s)) {
                                            throw new Throwable("Forced Throwable");
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
}

