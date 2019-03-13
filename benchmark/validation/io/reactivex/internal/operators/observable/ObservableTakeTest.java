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
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableTakeTest {
    @Test
    public void testTake1() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Observable<String> take = w.take(2);
        Observer<String> observer = mockObserver();
        take.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTake2() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Observable<String> take = w.take(1);
        Observer<String> observer = mockObserver();
        take.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeWithError() {
        fromIterable(Arrays.asList(1, 2, 3)).take(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                throw new IllegalArgumentException("some error");
            }
        }).blockingSingle();
    }

    @Test
    public void testTakeWithErrorHappeningInOnNext() {
        Observable<Integer> w = fromIterable(Arrays.asList(1, 2, 3)).take(2).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                throw new IllegalArgumentException("some error");
            }
        });
        Observer<Integer> observer = mockObserver();
        w.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(IllegalArgumentException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testTakeWithErrorHappeningInTheLastOnNext() {
        Observable<Integer> w = fromIterable(Arrays.asList(1, 2, 3)).take(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                throw new IllegalArgumentException("some error");
            }
        });
        Observer<Integer> observer = mockObserver();
        w.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(IllegalArgumentException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testTakeDoesntLeakErrors() {
        Observable<String> source = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onNext("one");
                observer.onError(new Throwable("test failed"));
            }
        });
        Observer<String> observer = mockObserver();
        source.take(1).subscribe(observer);
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        // even though onError is called we take(1) so shouldn't see it
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testUnsubscribeAfterTake() {
        ObservableTakeTest.TestObservableFunc f = new ObservableTakeTest.TestObservableFunc("one", "two", "three");
        Observable<String> w = Observable.unsafeCreate(f);
        Observer<String> observer = mockObserver();
        Observable<String> take = w.take(1);
        take.subscribe(observer);
        // wait for the Observable to complete
        try {
            f.t.join();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        System.out.println("TestObservable thread finished");
        Mockito.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        // FIXME no longer assertable
        // verify(s, times(1)).unsubscribe();
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test(timeout = 2000)
    public void testUnsubscribeFromSynchronousInfiniteObservable() {
        final AtomicLong count = new AtomicLong();
        io.reactivex.internal.operators.observable.INFINITE_OBSERVABLE.take(10).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long l) {
                count.set(l);
            }
        });
        Assert.assertEquals(10, count.get());
    }

    @Test(timeout = 2000)
    public void testMultiTake() {
        final AtomicInteger count = new AtomicInteger();
        Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                Disposable bs = Disposables.empty();
                observer.onSubscribe(bs);
                for (int i = 0; !(bs.isDisposed()); i++) {
                    System.out.println(("Emit: " + i));
                    count.incrementAndGet();
                    observer.onNext(i);
                }
            }
        }).take(100).take(1).blockingForEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                System.out.println(("Receive: " + t1));
            }
        });
        Assert.assertEquals(1, count.get());
    }

    static class TestObservableFunc implements ObservableSource<String> {
        final String[] values;

        Thread t;

        TestObservableFunc(String... values) {
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
                        observer.onComplete();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            System.out.println("starting TestObservable thread");
            t.start();
            System.out.println("done starting TestObservable thread");
        }
    }

    private static Observable<Long> INFINITE_OBSERVABLE = unsafeCreate(new ObservableSource<Long>() {
        @Override
        public void subscribe(Observer<? super Long> op) {
            Disposable d = Disposables.empty();
            op.onSubscribe(d);
            long l = 1;
            while (!(d.isDisposed())) {
                op.onNext((l++));
            } 
            op.onComplete();
        }
    });

    @Test(timeout = 2000)
    public void testTakeObserveOn() {
        Observer<Object> o = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(o);
        io.reactivex.internal.operators.observable.INFINITE_OBSERVABLE.observeOn(io.reactivex.schedulers.Schedulers.newThread()).take(1).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Mockito.verify(o).onNext(1L);
        Mockito.verify(o, Mockito.never()).onNext(2L);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testInterrupt() throws InterruptedException {
        final AtomicReference<Object> exception = new AtomicReference<Object>();
        final CountDownLatch latch = new CountDownLatch(1);
        just(1).subscribeOn(io.reactivex.schedulers.Schedulers.computation()).take(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    exception.set(e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });
        latch.await();
        Assert.assertNull(exception.get());
    }

    @Test
    public void takeFinalValueThrows() {
        Observable<Integer> source = just(1).take(1);
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                throw new TestException();
            }
        };
        source.safeSubscribe(to);
        to.assertNoValues();
        to.assertError(TestException.class);
        to.assertNotComplete();
    }

    @Test
    public void testReentrantTake() {
        final PublishSubject<Integer> source = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.take(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) {
                source.onNext(2);
            }
        }).subscribe(to);
        source.onNext(1);
        to.assertValue(1);
        to.assertNoErrors();
        to.assertComplete();
    }

    @Test
    public void takeNegative() {
        try {
            just(1).take((-99));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("count >= 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void takeZero() {
        just(1).take(0).test().assertResult();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().take(2));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.take(2);
            }
        });
    }

    @Test
    public void errorAfterLimitReached() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            error(new TestException()).take(0).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

