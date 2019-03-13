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
package io.reactivex.single;


import io.reactivex.exceptions.TestException;
import io.reactivex.internal.operators.single.SingleInternalHelper;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class SingleTest {
    @Test
    public void testHelloWorld() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single.just("Hello World!").toFlowable().subscribe(ts);
        ts.assertValueSequence(Arrays.asList("Hello World!"));
    }

    @Test
    public void testHelloWorld2() {
        final AtomicReference<String> v = new AtomicReference<String>();
        Single.just("Hello World!").subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(String value) {
                v.set(value);
            }

            @Override
            public void onError(Throwable error) {
            }
        });
        Assert.assertEquals("Hello World!", v.get());
    }

    @Test
    public void testMap() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single.just("A").map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s + "B";
            }
        }).toFlowable().subscribe(ts);
        ts.assertValueSequence(Arrays.asList("AB"));
    }

    @Test
    public void testZip() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single<String> a = Single.just("A");
        Single<String> b = Single.just("B");
        Single.zip(a, b, new BiFunction<String, String, String>() {
            @Override
            public String apply(String a1, String b1) {
                return a1 + b1;
            }
        }).toFlowable().subscribe(ts);
        ts.assertValueSequence(Arrays.asList("AB"));
    }

    @Test
    public void testZipWith() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single.just("A").zipWith(Single.just("B"), new BiFunction<String, String, String>() {
            @Override
            public String apply(String a1, String b1) {
                return a1 + b1;
            }
        }).toFlowable().subscribe(ts);
        ts.assertValueSequence(Arrays.asList("AB"));
    }

    @Test
    public void testMerge() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single<String> a = Single.just("A");
        Single<String> b = Single.just("B");
        Single.merge(a, b).subscribe(ts);
        ts.assertValueSequence(Arrays.asList("A", "B"));
    }

    @Test
    public void testMergeWith() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single.just("A").mergeWith(Single.just("B")).subscribe(ts);
        ts.assertValueSequence(Arrays.asList("A", "B"));
    }

    @Test
    public void testCreateSuccess() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Single.unsafeCreate(new SingleSource<Object>() {
            @Override
            public void subscribe(SingleObserver<? super Object> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onSuccess("Hello");
            }
        }).toFlowable().subscribe(ts);
        ts.assertValueSequence(Arrays.asList("Hello"));
    }

    @Test
    public void testCreateError() {
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Single.unsafeCreate(new SingleSource<Object>() {
            @Override
            public void subscribe(SingleObserver<? super Object> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onError(new RuntimeException("fail"));
            }
        }).toFlowable().subscribe(ts);
        ts.assertError(RuntimeException.class);
        ts.assertErrorMessage("fail");
    }

    @Test
    public void testAsync() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single.just("Hello").subscribeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String v) {
                System.out.println(("SubscribeOn Thread: " + (Thread.currentThread())));
                return v;
            }
        }).observeOn(Schedulers.computation()).map(new Function<String, String>() {
            @Override
            public String apply(String v) {
                System.out.println(("ObserveOn Thread: " + (Thread.currentThread())));
                return v;
            }
        }).toFlowable().subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertValueSequence(Arrays.asList("Hello"));
    }

    @Test
    public void testFlatMap() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single.just("Hello").flatMap(new Function<String, Single<String>>() {
            @Override
            public io.reactivex.Single<String> apply(String s) {
                return Single.just((s + " World!")).subscribeOn(Schedulers.computation());
            }
        }).toFlowable().subscribe(ts);
        if (!(ts.awaitTerminalEvent(5, TimeUnit.SECONDS))) {
            ts.cancel();
            Assert.fail("TestSubscriber timed out.");
        }
        ts.assertValueSequence(Arrays.asList("Hello World!"));
    }

    @Test
    public void testTimeout() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single<String> s1 = Single.<String>unsafeCreate(new SingleSource<String>() {
            @Override
            public void subscribe(SingleObserver<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // ignore as we expect this for the test
                }
                observer.onSuccess("success");
            }
        }).subscribeOn(Schedulers.io());
        s1.timeout(100, TimeUnit.MILLISECONDS).toFlowable().subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertError(TimeoutException.class);
    }

    @Test
    public void testTimeoutWithFallback() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Single<String> s1 = Single.<String>unsafeCreate(new SingleSource<String>() {
            @Override
            public void subscribe(SingleObserver<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // ignore as we expect this for the test
                }
                observer.onSuccess("success");
            }
        }).subscribeOn(Schedulers.io());
        s1.timeout(100, TimeUnit.MILLISECONDS, Single.just("hello")).toFlowable().subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        ts.assertValue("hello");
    }

    @Test
    public void testUnsubscribe() throws InterruptedException {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        final AtomicBoolean unsubscribed = new AtomicBoolean();
        final AtomicBoolean interrupted = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        Single<String> s1 = Single.<String>unsafeCreate(new SingleSource<String>() {
            @Override
            public void subscribe(final SingleObserver<? super String> observer) {
                SerialDisposable sd = new SerialDisposable();
                observer.onSubscribe(sd);
                final Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            observer.onSuccess("success");
                        } catch (InterruptedException e) {
                            interrupted.set(true);
                            latch.countDown();
                        }
                    }
                });
                sd.replace(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        unsubscribed.set(true);
                        t.interrupt();
                        latch.countDown();
                    }
                }));
                t.start();
            }
        });
        s1.toFlowable().subscribe(ts);
        Thread.sleep(100);
        ts.dispose();
        if (latch.await(1000, TimeUnit.MILLISECONDS)) {
            Assert.assertTrue(unsubscribed.get());
            Assert.assertTrue(interrupted.get());
        } else {
            Assert.fail("timed out waiting for latch");
        }
    }

    /**
     * Assert that unsubscribe propagates when passing in a SingleObserver and not a Subscriber.
     *
     * @throws InterruptedException
     * 		if the test is interrupted
     */
    @Test
    public void testUnsubscribe2() throws InterruptedException {
        final SerialDisposable sd = new SerialDisposable();
        SingleObserver<String> ts = new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                sd.replace(d);
            }

            @Override
            public void onSuccess(String value) {
                // not interested in value
            }

            @Override
            public void onError(Throwable error) {
                // not interested in value
            }
        };
        final AtomicBoolean unsubscribed = new AtomicBoolean();
        final AtomicBoolean interrupted = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        Single<String> s1 = Single.unsafeCreate(new SingleSource<String>() {
            @Override
            public void subscribe(final SingleObserver<? super String> observer) {
                SerialDisposable sd = new SerialDisposable();
                observer.onSubscribe(sd);
                final Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            observer.onSuccess("success");
                        } catch (InterruptedException e) {
                            interrupted.set(true);
                            latch.countDown();
                        }
                    }
                });
                sd.replace(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        unsubscribed.set(true);
                        t.interrupt();
                        latch.countDown();
                    }
                }));
                t.start();
            }
        });
        s1.subscribe(ts);
        Thread.sleep(100);
        sd.dispose();
        if (latch.await(1000, TimeUnit.MILLISECONDS)) {
            Assert.assertTrue(unsubscribed.get());
            Assert.assertTrue(interrupted.get());
        } else {
            Assert.fail("timed out waiting for latch");
        }
    }

    /**
     * Assert that unsubscribe propagates when passing in a SingleObserver and not a Subscriber.
     *
     * @throws InterruptedException
     * 		if the test is interrupted
     */
    @Test
    public void testUnsubscribeViaReturnedSubscription() throws InterruptedException {
        final AtomicBoolean unsubscribed = new AtomicBoolean();
        final AtomicBoolean interrupted = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        Single<String> s1 = Single.unsafeCreate(new SingleSource<String>() {
            @Override
            public void subscribe(final SingleObserver<? super String> observer) {
                SerialDisposable sd = new SerialDisposable();
                observer.onSubscribe(sd);
                final Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            observer.onSuccess("success");
                        } catch (InterruptedException e) {
                            interrupted.set(true);
                            latch.countDown();
                        }
                    }
                });
                sd.replace(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        unsubscribed.set(true);
                        t.interrupt();
                        latch.countDown();
                    }
                }));
                t.start();
            }
        });
        Disposable subscription = s1.subscribe();
        Thread.sleep(100);
        subscription.dispose();
        if (latch.await(1000, TimeUnit.MILLISECONDS)) {
            Assert.assertTrue(unsubscribed.get());
            Assert.assertTrue(interrupted.get());
        } else {
            Assert.fail("timed out waiting for latch");
        }
    }

    @Test
    public void testBackpressureAsObservable() {
        Single<String> s = Single.unsafeCreate(new SingleSource<String>() {
            @Override
            public void subscribe(SingleObserver<? super String> t) {
                t.onSubscribe(Disposables.empty());
                t.onSuccess("hello");
            }
        });
        TestSubscriber<String> ts = new TestSubscriber<String>(0L);
        s.toFlowable().subscribe(ts);
        ts.assertNoValues();
        ts.request(1);
        ts.assertValue("hello");
    }

    @Test
    public void toObservable() {
        Flowable<String> a = Single.just("a").toFlowable();
        TestSubscriber<String> ts = new TestSubscriber<String>();
        a.subscribe(ts);
        ts.assertValue("a");
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test(expected = NullPointerException.class)
    public void doOnEventNullEvent() {
        Single.just(1).doOnEvent(null);
    }

    @Test
    public void doOnEventComplete() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        Single.just(1).doOnEvent(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(final Integer integer, final Throwable throwable) throws Exception {
                if (integer != null) {
                    atomicInteger.incrementAndGet();
                }
            }
        }).subscribe();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void doOnEventError() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        Single.error(new RuntimeException()).doOnEvent(new BiConsumer<Object, Throwable>() {
            @Override
            public void accept(final Object o, final Throwable throwable) throws Exception {
                if (throwable != null) {
                    atomicInteger.incrementAndGet();
                }
            }
        }).subscribe();
        Assert.assertEquals(1, atomicInteger.get());
    }

    // (timeout = 5000)
    @Test
    public void toFuture() throws Exception {
        Assert.assertEquals(1, Single.just(1).toFuture().get().intValue());
    }

    @Test(timeout = 5000)
    public void toFutureThrows() throws Exception {
        try {
            Single.error(new TestException()).toFuture().get();
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof TestException));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void toFlowableIterableRemove() {
        @SuppressWarnings("unchecked")
        Iterable<? extends Flowable<Integer>> f = SingleInternalHelper.iterableToFlowable(Arrays.asList(Single.just(1)));
        Iterator<? extends Flowable<Integer>> iterator = f.iterator();
        iterator.next();
        iterator.remove();
    }

    @Test
    public void zipIterableObject() {
        @SuppressWarnings("unchecked")
        final List<Single<Integer>> singles = Arrays.asList(Single.just(1), Single.just(4));
        test().assertResult(5);
    }

    @Test
    public void to() {
        Assert.assertEquals(1, Single.just(1).to(new Function<Single<Integer>, Integer>() {
            @Override
            public Integer apply(Single<Integer> v) throws Exception {
                return 1;
            }
        }).intValue());
    }

    @Test
    public void as() {
        test().assertResult(1);
    }

    @Test(expected = NullPointerException.class)
    public void fromObservableNull() {
        Single.fromObservable(null);
    }

    @Test
    public void fromObservableEmpty() {
        test().assertFailure(NoSuchElementException.class);
    }

    @Test
    public void fromObservableMoreThan1Elements() {
        test().assertFailure(IllegalArgumentException.class).assertErrorMessage("Sequence contains more than one element!");
    }

    @Test
    public void fromObservableOneElement() {
        test().assertResult(1);
    }

    @Test
    public void fromObservableError() {
        test().assertFailure(RuntimeException.class).assertErrorMessage("some error");
    }

    @Test(expected = NullPointerException.class)
    public void implementationThrows() {
        test();
    }
}

