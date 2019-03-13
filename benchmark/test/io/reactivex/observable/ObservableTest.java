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
package io.reactivex.observable;


import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.TestHelper;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observables.ConnectableObservable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableTest {
    Observer<Number> w;

    SingleObserver<Number> wo;

    MaybeObserver<Number> wm;

    private static final Predicate<Integer> IS_EVEN = new Predicate<Integer>() {
        @Override
        public boolean test(Integer v) {
            return (v % 2) == 0;
        }
    };

    @Test
    public void fromArray() {
        String[] items = new String[]{ "one", "two", "three" };
        Assert.assertEquals(((Long) (3L)), Observable.fromArray(items).count().blockingGet());
        Assert.assertEquals("two", Observable.fromArray(items).skip(1).take(1).blockingSingle());
        Assert.assertEquals("three", Observable.fromArray(items).takeLast(1).blockingSingle());
    }

    @Test
    public void fromIterable() {
        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");
        Assert.assertEquals(((Long) (3L)), Observable.fromIterable(items).count().blockingGet());
        Assert.assertEquals("two", Observable.fromIterable(items).skip(1).take(1).blockingSingle());
        Assert.assertEquals("three", Observable.fromIterable(items).takeLast(1).blockingSingle());
    }

    @Test
    public void fromArityArgs3() {
        Observable<String> items = Observable.just("one", "two", "three");
        Assert.assertEquals(((Long) (3L)), items.count().blockingGet());
        Assert.assertEquals("two", items.skip(1).take(1).blockingSingle());
        Assert.assertEquals("three", items.takeLast(1).blockingSingle());
    }

    @Test
    public void fromArityArgs1() {
        Observable<String> items = Observable.just("one");
        Assert.assertEquals(((Long) (1L)), items.count().blockingGet());
        Assert.assertEquals("one", items.takeLast(1).blockingSingle());
    }

    @Test
    public void testCreate() {
        Observable<String> o = Observable.just("one", "two", "three");
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testCountAFewItemsObservable() {
        Observable<String> o = Observable.just("a", "b", "c", "d");
        o.count().toObservable().subscribe(w);
        // we should be called only once
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(w).onNext(4L);
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testCountZeroItemsObservable() {
        Observable<String> o = empty();
        o.count().toObservable().subscribe(w);
        // we should be called only once
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(w).onNext(0L);
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testCountErrorObservable() {
        Observable<String> o = Observable.error(new Callable<Throwable>() {
            @Override
            public Throwable call() {
                return new RuntimeException();
            }
        });
        o.count().toObservable().subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w, Mockito.never()).onComplete();
        Mockito.verify(w, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
    }

    @Test
    public void testCountAFewItems() {
        Observable<String> o = Observable.just("a", "b", "c", "d");
        o.count().subscribe(wo);
        // we should be called only once
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyLong());
        Mockito.verify(wo).onSuccess(4L);
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testCountZeroItems() {
        Observable<String> o = empty();
        o.count().subscribe(wo);
        // we should be called only once
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyLong());
        Mockito.verify(wo).onSuccess(0L);
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testCountError() {
        Observable<String> o = Observable.error(new Callable<Throwable>() {
            @Override
            public Throwable call() {
                return new RuntimeException();
            }
        });
        o.count().subscribe(wo);
        Mockito.verify(wo, Mockito.never()).onSuccess(ArgumentMatchers.anyInt());
        Mockito.verify(wo, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
    }

    @Test
    public void testTakeFirstWithPredicateOfSome() {
        Observable<Integer> o = Observable.just(1, 3, 5, 4, 6, 3);
        o.filter(ObservableTest.IS_EVEN).take(1).subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w).onNext(4);
        Mockito.verify(w, Mockito.times(1)).onComplete();
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testTakeFirstWithPredicateOfNoneMatchingThePredicate() {
        Observable<Integer> o = Observable.just(1, 3, 5, 7, 9, 7, 5, 3, 1);
        o.filter(ObservableTest.IS_EVEN).take(1).subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w, Mockito.times(1)).onComplete();
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testTakeFirstOfSome() {
        Observable<Integer> o = just(1, 2, 3);
        o.take(1).subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w).onNext(1);
        Mockito.verify(w, Mockito.times(1)).onComplete();
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testTakeFirstOfNone() {
        Observable<Integer> o = empty();
        o.take(1).subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w, Mockito.times(1)).onComplete();
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstOfNone() {
        Observable<Integer> o = empty();
        o.firstElement().subscribe(wm);
        Mockito.verify(wm, Mockito.never()).onSuccess(ArgumentMatchers.anyInt());
        Mockito.verify(wm).onComplete();
        Mockito.verify(wm, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstWithPredicateOfNoneMatchingThePredicate() {
        Observable<Integer> o = Observable.just(1, 3, 5, 7, 9, 7, 5, 3, 1);
        o.filter(ObservableTest.IS_EVEN).firstElement().subscribe(wm);
        Mockito.verify(wm, Mockito.never()).onSuccess(ArgumentMatchers.anyInt());
        Mockito.verify(wm).onComplete();
        Mockito.verify(wm, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testReduce() {
        Observable<Integer> o = Observable.just(1, 2, 3, 4);
        o.reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(wm);
        // we should be called only once
        Mockito.verify(wm, Mockito.times(1)).onSuccess(ArgumentMatchers.anyInt());
        Mockito.verify(wm).onSuccess(10);
        Mockito.verify(wm, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(wm, Mockito.never()).onComplete();
    }

    @Test
    public void testReduceObservable() {
        Observable<Integer> o = Observable.just(1, 2, 3, 4);
        o.reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).toObservable().subscribe(w);
        // we should be called only once
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w).onNext(10);
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w).onComplete();
    }

    @Test
    public void testReduceWithEmptyObservable() {
        Observable<Integer> o = range(1, 0);
        o.reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).toObservable().test().assertResult();
    }

    /**
     * A reduce on an empty Observable and a seed should just pass the seed through.
     *
     * This is confirmed at https://github.com/ReactiveX/RxJava/issues/423#issuecomment-27642456
     */
    @Test
    public void testReduceWithEmptyObservableAndSeed() {
        Observable<Integer> o = range(1, 0);
        int value = o.reduce(1, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).blockingGet();
        Assert.assertEquals(1, value);
    }

    @Test
    public void testReduceWithInitialValue() {
        Observable<Integer> o = Observable.just(1, 2, 3, 4);
        o.reduce(50, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(wo);
        // we should be called only once
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyInt());
        Mockito.verify(wo).onSuccess(60);
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testReduceWithInitialValueObservable() {
        Observable<Integer> o = Observable.just(1, 2, 3, 4);
        o.reduce(50, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).toObservable().subscribe(w);
        // we should be called only once
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w).onNext(60);
    }

    @Test
    public void testMaterializeDematerializeChaining() {
        Observable<Integer> obs = Observable.just(1);
        Observable<Integer> chained = obs.materialize().dematerialize(Functions.<Notification<Integer>>identity());
        Observer<Integer> observer = mockObserver();
        chained.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.times(0)).onError(ArgumentMatchers.any(Throwable.class));
    }

    /**
     * The error from the user provided Observer is not handled by the subscribe method try/catch.
     *
     * It is handled by the AtomicObserver that wraps the provided Observer.
     *
     * Result: Passes (if AtomicObserver functionality exists)
     *
     * @throws InterruptedException
     * 		if the test is interrupted
     */
    @Test
    public void testCustomObservableWithErrorInObserverAsynchronous() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        // FIXME custom built???
        Observable.just("1", "2", "three", "4").subscribeOn(Schedulers.newThread()).safeSubscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                System.out.println("completed");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onNext(String v) {
                int num = Integer.parseInt(v);
                System.out.println(num);
                // doSomething(num);
                count.incrementAndGet();
            }
        });
        // wait for async sequence to complete
        latch.await();
        Assert.assertEquals(2, count.get());
        Assert.assertNotNull(error.get());
        if (!((error.get()) instanceof NumberFormatException)) {
            Assert.fail("It should be a NumberFormatException");
        }
    }

    /**
     * The error from the user provided Observer is handled by the subscribe try/catch because this is synchronous.
     *
     * Result: Passes
     */
    @Test
    public void testCustomObservableWithErrorInObserverSynchronous() {
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        // FIXME custom built???
        Observable.just("1", "2", "three", "4").safeSubscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                int num = Integer.parseInt(v);
                System.out.println(num);
                // doSomething(num);
                count.incrementAndGet();
            }
        });
        Assert.assertEquals(2, count.get());
        Assert.assertNotNull(error.get());
        if (!((error.get()) instanceof NumberFormatException)) {
            Assert.fail("It should be a NumberFormatException");
        }
    }

    /**
     * The error from the user provided Observable is handled by the subscribe try/catch because this is synchronous.
     *
     *
     * Result: Passes
     */
    @Test
    public void testCustomObservableWithErrorInObservableSynchronous() {
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        // FIXME custom built???
        Observable.just("1", "2").concatWith(Observable.<String>error(new Callable<Throwable>() {
            @Override
            public Throwable call() {
                return new NumberFormatException();
            }
        })).subscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                System.out.println(v);
                count.incrementAndGet();
            }
        });
        Assert.assertEquals(2, count.get());
        Assert.assertNotNull(error.get());
        if (!((error.get()) instanceof NumberFormatException)) {
            Assert.fail("It should be a NumberFormatException");
        }
    }

    @Test
    public void testPublishLast() throws InterruptedException {
        final AtomicInteger count = new AtomicInteger();
        ConnectableObservable<String> connectable = Observable.<String>unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(final Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                count.incrementAndGet();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        observer.onNext("first");
                        observer.onNext("last");
                        observer.onComplete();
                    }
                }).start();
            }
        }).takeLast(1).publish();
        // subscribe once
        final CountDownLatch latch = new CountDownLatch(1);
        connectable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String value) {
                Assert.assertEquals("last", value);
                latch.countDown();
            }
        });
        // subscribe twice
        connectable.subscribe();
        Disposable subscription = connectable.connect();
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, count.get());
        subscription.dispose();
    }

    @Test
    public void testReplay() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        ConnectableObservable<String> o = Observable.<String>unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(final Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        observer.onNext("one");
                        observer.onComplete();
                    }
                }).start();
            }
        }).replay();
        // we connect immediately and it will emit the value
        Disposable connection = o.connect();
        try {
            // we then expect the following 2 subscriptions to get that same value
            final CountDownLatch latch = new CountDownLatch(2);
            // subscribe once
            o.subscribe(new Consumer<String>() {
                @Override
                public void accept(String v) {
                    Assert.assertEquals("one", v);
                    latch.countDown();
                }
            });
            // subscribe again
            o.subscribe(new Consumer<String>() {
                @Override
                public void accept(String v) {
                    Assert.assertEquals("one", v);
                    latch.countDown();
                }
            });
            if (!(latch.await(1000, TimeUnit.MILLISECONDS))) {
                Assert.fail("subscriptions did not receive values");
            }
            Assert.assertEquals(1, counter.get());
        } finally {
            connection.dispose();
        }
    }

    @Test
    public void testCache() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        Observable<String> o = Observable.<String>unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(final Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        observer.onNext("one");
                        observer.onComplete();
                    }
                }).start();
            }
        }).cache();
        // we then expect the following 2 subscriptions to get that same value
        final CountDownLatch latch = new CountDownLatch(2);
        // subscribe once
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                latch.countDown();
            }
        });
        // subscribe again
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                latch.countDown();
            }
        });
        if (!(latch.await(1000, TimeUnit.MILLISECONDS))) {
            Assert.fail("subscriptions did not receive values");
        }
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testCacheWithCapacity() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        Observable<String> o = <String>unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(final Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        observer.onNext("one");
                        observer.onComplete();
                    }
                }).start();
            }
        }).cacheWithInitialCapacity(1);
        // we then expect the following 2 subscriptions to get that same value
        final CountDownLatch latch = new CountDownLatch(2);
        // subscribe once
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                latch.countDown();
            }
        });
        // subscribe again
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                latch.countDown();
            }
        });
        if (!(latch.await(1000, TimeUnit.MILLISECONDS))) {
            Assert.fail("subscriptions did not receive values");
        }
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testTakeWithErrorInObserver() {
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        Observable.just("1", "2", "three", "4").take(3).safeSubscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                int num = Integer.parseInt(v);
                System.out.println(num);
                // doSomething(num);
                count.incrementAndGet();
            }
        });
        Assert.assertEquals(2, count.get());
        Assert.assertNotNull(error.get());
        if (!((error.get()) instanceof NumberFormatException)) {
            Assert.fail("It should be a NumberFormatException");
        }
    }

    @Test
    public void testOfType() {
        Observable<String> o = Observable.just(1, "abc", false, 2L).ofType(String.class);
        Observer<Object> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext("abc");
        Mockito.verify(observer, Mockito.never()).onNext(false);
        Mockito.verify(observer, Mockito.never()).onNext(2L);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testOfTypeWithPolymorphism() {
        ArrayList<Integer> l1 = new ArrayList<Integer>();
        l1.add(1);
        LinkedList<Integer> l2 = new LinkedList<Integer>();
        l2.add(2);
        @SuppressWarnings("rawtypes")
        Observable<List> o = Observable.<Object>just(l1, l2, "123").ofType(List.class);
        Observer<Object> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(l1);
        Mockito.verify(observer, Mockito.times(1)).onNext(l2);
        Mockito.verify(observer, Mockito.never()).onNext("123");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testContainsObservable() {
        Observable<Boolean> o = Observable.just("a", "b", "c").contains("b").toObservable();
        Observer<Boolean> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(true);
        Mockito.verify(observer, Mockito.never()).onNext(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testContainsWithInexistenceObservable() {
        Observable<Boolean> o = Observable.just("a", "b").contains("c").toObservable();
        Observer<Object> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(false);
        Mockito.verify(observer, Mockito.never()).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testContainsWithEmptyObservableObservable() {
        Observable<Boolean> o = <String>empty().contains("a").toObservable();
        Observer<Object> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(false);
        Mockito.verify(observer, Mockito.never()).onNext(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testContains() {
        Single<Boolean> o = Observable.just("a", "b", "c").contains("b");// FIXME nulls not allowed, changed to "c"

        SingleObserver<Boolean> observer = TestHelper.mockSingleObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testContainsWithInexistence() {
        Single<Boolean> o = Observable.just("a", "b").contains("c");// FIXME null values are not allowed, removed

        SingleObserver<Object> observer = TestHelper.mockSingleObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testContainsWithEmptyObservable() {
        Single<Boolean> o = <String>empty().contains("a");
        SingleObserver<Object> observer = TestHelper.mockSingleObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(false);
        Mockito.verify(observer, Mockito.never()).onSuccess(true);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testIgnoreElements() {
        Completable o = just(1, 2, 3).ignoreElements();
        CompletableObserver observer = TestHelper.mockCompletableObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testIgnoreElementsObservable() {
        Observable<Integer> o = just(1, 2, 3).ignoreElements().toObservable();
        Observer<Object> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(Integer.class));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testJustWithScheduler() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Integer> o = Observable.fromArray(1, 2).subscribeOn(scheduler);
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        scheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(1);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testStartWithWithScheduler() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Integer> o = Observable.just(3, 4).startWith(Arrays.asList(1, 2)).subscribeOn(scheduler);
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        scheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(1);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        inOrder.verify(observer, Mockito.times(1)).onNext(3);
        inOrder.verify(observer, Mockito.times(1)).onNext(4);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRangeWithScheduler() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Integer> o = range(3, 4).subscribeOn(scheduler);
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        scheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(3);
        inOrder.verify(observer, Mockito.times(1)).onNext(4);
        inOrder.verify(observer, Mockito.times(1)).onNext(5);
        inOrder.verify(observer, Mockito.times(1)).onNext(6);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testMergeWith() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.just(1).mergeWith(Observable.just(2)).subscribe(to);
        to.assertValues(1, 2);
    }

    @Test
    public void testConcatWith() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.just(1).concatWith(Observable.just(2)).subscribe(to);
        to.assertValues(1, 2);
    }

    @Test
    public void testAmbWith() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.just(1).ambWith(Observable.just(2)).subscribe(to);
        to.assertValue(1);
    }

    @Test
    public void testTakeWhileToList() {
        final int expectedCount = 3;
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < expectedCount; i++) {
            Observable.just(Boolean.TRUE, Boolean.FALSE).takeWhile(new Predicate<Boolean>() {
                @Override
                public boolean test(Boolean v) {
                    return v;
                }
            }).toList().doOnSuccess(new Consumer<List<Boolean>>() {
                @Override
                public void accept(List<Boolean> booleans) {
                    count.incrementAndGet();
                }
            }).subscribe();
        }
        Assert.assertEquals(expectedCount, count.get());
    }

    @Test
    public void testCompose() {
        TestObserver<String> to = new TestObserver<String>();
        just(1, 2, 3).compose(new ObservableTransformer<Integer, String>() {
            @Override
            public Observable<String> apply(Observable<Integer> t1) {
                return t1.map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer v) {
                        return String.valueOf(v);
                    }
                });
            }
        }).subscribe(to);
        to.assertTerminated();
        to.assertNoErrors();
        to.assertValues("1", "2", "3");
    }

    @Test
    public void testErrorThrownIssue1685() {
        Subject<Object> subject = ReplaySubject.create();
        Observable.error(new RuntimeException("oops")).materialize().delay(1, TimeUnit.SECONDS).dematerialize(Functions.<Notification<Object>>identity()).subscribe(subject);
        subject.subscribe();
        subject.materialize().blockingFirst();
        System.out.println("Done");
    }

    @Test
    public void testEmptyIdentity() {
        Assert.assertEquals(empty(), empty());
    }

    @Test
    public void testEmptyIsEmpty() {
        <Integer>empty().subscribe(w);
        Mockito.verify(w).onComplete();
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.any(Integer.class));
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    // FIXME this test doesn't make sense
    // @Test // cf. https://github.com/ReactiveX/RxJava/issues/2599
    // public void testSubscribingSubscriberAsObserverMaintainsSubscriptionChain() {
    // TestObserver<Object> observer = new TestObserver<T>();
    // Subscription subscription = Observable.just("event").subscribe((Observer<Object>) observer);
    // subscription.unsubscribe();
    // 
    // subscriber.assertUnsubscribed();
    // }
    // FIXME subscribers can't throw
    // @Test(expected=OnErrorNotImplementedException.class)
    // public void testForEachWithError() {
    // Observable.error(new Exception("boo"))
    // //
    // .forEach(new Action1<Object>() {
    // @Override
    // public void call(Object t) {
    // //do nothing
    // }});
    // }
    @Test(expected = NullPointerException.class)
    public void testForEachWithNull() {
        // 
        error(new Exception("boo")).forEach(null);
    }

    @Test
    public void testExtend() {
        final TestObserver<Object> to = new TestObserver<Object>();
        final Object value = new Object();
        Object returned = Observable.just(value).to(new Function<Observable<Object>, Object>() {
            @Override
            public Object apply(Observable<Object> onSubscribe) {
                onSubscribe.subscribe(to);
                to.assertNoErrors();
                to.assertComplete();
                to.assertValue(value);
                return to.values().get(0);
            }
        });
        Assert.assertSame(returned, value);
    }

    @Test
    public void testAsExtend() {
        final TestObserver<Object> to = new TestObserver<Object>();
        final Object value = new Object();
        Object returned = Observable.just(value).as(new ObservableConverter<Object, Object>() {
            @Override
            public Object apply(Observable<Object> onSubscribe) {
                onSubscribe.subscribe(to);
                to.assertNoErrors();
                to.assertComplete();
                to.assertValue(value);
                return to.values().get(0);
            }
        });
        Assert.assertSame(returned, value);
    }

    @Test
    public void as() {
        Observable.just(1).as(new ObservableConverter<Integer, Flowable<Integer>>() {
            @Override
            public Flowable<Integer> apply(Observable<Integer> v) {
                return v.toFlowable(BackpressureStrategy.MISSING);
            }
        }).test().assertResult(1);
    }

    @Test
    public void testFlatMap() {
        List<Integer> list = range(1, 5).flatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer v) {
                return io.reactivex.Observable.range(v, 2);
            }
        }).toList().blockingGet();
        Assert.assertEquals(Arrays.asList(1, 2, 2, 3, 3, 4, 4, 5, 5, 6), list);
    }

    @Test
    public void singleDefault() {
        Observable.just(1).single(100).test().assertResult(1);
        empty().single(100).test().assertResult(100);
    }

    @Test
    public void singleDefaultObservable() {
        Observable.just(1).single(100).toObservable().test().assertResult(1);
        empty().single(100).toObservable().test().assertResult(100);
    }

    @Test
    public void zipIterableObject() {
        @SuppressWarnings("unchecked")
        final List<Observable<Integer>> observables = Arrays.asList(just(1, 2, 3), just(1, 2, 3));
        Observable.zip(observables, new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] o) throws Exception {
                int sum = 0;
                for (Object i : o) {
                    sum += ((Integer) (i));
                }
                return sum;
            }
        }).test().assertResult(2, 4, 6);
    }

    @Test
    public void combineLatestObject() {
        @SuppressWarnings("unchecked")
        final List<Observable<Integer>> observables = Arrays.asList(just(1, 2, 3), just(1, 2, 3));
        Observable.combineLatest(observables, new Function<Object[], Object>() {
            @Override
            public Object apply(final Object[] o) throws Exception {
                int sum = 1;
                for (Object i : o) {
                    sum *= ((Integer) (i));
                }
                return sum;
            }
        }).test().assertResult(3, 6, 9);
    }
}

