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
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableOnErrorReturnTest {
    @Test
    public void testResumeNext() {
        ObservableOnErrorReturnTest.TestObservable f = new ObservableOnErrorReturnTest.TestObservable("one");
        Observable<String> w = Observable.unsafeCreate(f);
        final AtomicReference<Throwable> capturedException = new AtomicReference<Throwable>();
        Observable<String> observable = w.onErrorReturn(new io.reactivex.functions.Function<Throwable, String>() {
            @Override
            public String apply(Throwable e) {
                capturedException.set(e);
                return "failure";
            }
        });
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
        Mockito.verify(observer, Mockito.times(1)).onNext("failure");
        Assert.assertNotNull(capturedException.get());
    }

    /**
     * Test that when a function throws an exception this is propagated through onError.
     */
    @Test
    public void testFunctionThrowsError() {
        ObservableOnErrorReturnTest.TestObservable f = new ObservableOnErrorReturnTest.TestObservable("one");
        Observable<String> w = Observable.unsafeCreate(f);
        final AtomicReference<Throwable> capturedException = new AtomicReference<Throwable>();
        Observable<String> observable = w.onErrorReturn(new io.reactivex.functions.Function<Throwable, String>() {
            @Override
            public String apply(Throwable e) {
                capturedException.set(e);
                throw new RuntimeException("exception from function");
            }
        });
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        // we should get the "one" value before the error
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        // we should have received an onError call on the Observer since the resume function threw an exception
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(0)).onComplete();
        Assert.assertNotNull(capturedException.get());
    }

    @Test
    public void testMapResumeAsyncNext() {
        // Trigger multiple failures
        Observable<String> w = Observable.just("one", "fail", "two", "three", "fail");
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
        Observable<String> observable = w.onErrorReturn(new io.reactivex.functions.Function<Throwable, String>() {
            @Override
            public String apply(Throwable t1) {
                return "resume";
            }
        });
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        observable.subscribe(to);
        to.awaitTerminalEvent();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("resume");
    }

    @Test
    public void testBackpressure() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(0, 100000).onErrorReturn(new io.reactivex.functions.Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable t1) {
                return 1;
            }
        }).observeOn(Schedulers.computation()).map(new io.reactivex.functions.Function<Integer, Integer>() {
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
    public void returnItem() {
        Observable.error(new TestException()).onErrorReturnItem(1).test().assertResult(1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).onErrorReturnItem(1));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> f) throws Exception {
                return f.onErrorReturnItem(1);
            }
        });
    }
}

