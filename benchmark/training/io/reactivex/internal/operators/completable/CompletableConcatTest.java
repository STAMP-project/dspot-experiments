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
package io.reactivex.internal.operators.completable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.MissingBackpressureException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


public class CompletableConcatTest {
    @Test
    public void overflowReported() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Completable.concat(Flowable.fromPublisher(new Publisher<Completable>() {
                @Override
                public void subscribe(Subscriber<? super Completable> s) {
                    s.onSubscribe(new BooleanSubscription());
                    s.onNext(Completable.never());
                    s.onNext(Completable.never());
                    s.onNext(Completable.never());
                    s.onNext(Completable.never());
                    s.onComplete();
                }
            }), 1).test().assertFailure(MissingBackpressureException.class);
            TestHelper.assertError(errors, 0, MissingBackpressureException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void invalidPrefetch() {
        try {
            Completable.concat(Flowable.just(Completable.complete()), (-99));
            Assert.fail("Should have thrown IllegalArgumentExceptio");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("prefetch > 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Completable.concat(Flowable.just(Completable.complete())));
    }

    @Test
    public void errorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishProcessor<Integer> pp1 = PublishProcessor.create();
                final PublishProcessor<Integer> pp2 = PublishProcessor.create();
                TestObserver<Void> to = Completable.concat(pp1.map(new Function<Integer, Completable>() {
                    @Override
                    public io.reactivex.Completable apply(Integer v) throws Exception {
                        return pp2.ignoreElements();
                    }
                })).test();
                pp1.onNext(1);
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp1.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        pp2.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                to.assertFailure(TestException.class);
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void synchronousFusedCrash() {
        Completable.concat(Flowable.range(1, 2).map(new Function<Integer, Completable>() {
            @Override
            public io.reactivex.Completable apply(Integer v) throws Exception {
                throw new TestException();
            }
        })).test().assertFailure(TestException.class);
    }

    @Test
    public void unboundedIn() {
        Completable.concat(Flowable.just(Completable.complete()).hide(), Integer.MAX_VALUE).test().assertResult();
    }

    @Test
    public void syncFusedUnboundedIn() {
        Completable.concat(Flowable.just(Completable.complete()), Integer.MAX_VALUE).test().assertResult();
    }

    @Test
    public void asyncFusedUnboundedIn() {
        UnicastProcessor<Completable> up = UnicastProcessor.create();
        up.onNext(Completable.complete());
        up.onComplete();
        Completable.concat(up, Integer.MAX_VALUE).test().assertResult();
    }

    @Test
    public void arrayCancelled() {
        Completable.concatArray(Completable.complete(), Completable.complete()).test(true).assertEmpty();
    }

    @Test
    public void arrayFirstCancels() {
        final TestObserver<Void> to = new TestObserver<Void>();
        Completable.concatArray(new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {
                observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                to.cancel();
                observer.onComplete();
            }
        }, Completable.complete()).subscribe(to);
        to.assertEmpty();
    }

    @Test
    public void iterableCancelled() {
        Completable.concat(Arrays.asList(Completable.complete(), Completable.complete())).test(true).assertEmpty();
    }

    @Test
    public void iterableFirstCancels() {
        final TestObserver<Void> to = new TestObserver<Void>();
        Completable.concat(Arrays.asList(new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {
                observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                to.cancel();
                observer.onComplete();
            }
        }, Completable.complete())).subscribe(to);
        to.assertEmpty();
    }

    @Test
    public void arrayCancelRace() {
        Completable[] a = new Completable[1024];
        Arrays.fill(a, Completable.complete());
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final Completable c = Completable.concatArray(a);
            final TestObserver<Void> to = new TestObserver<Void>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    c.subscribe(to);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void iterableCancelRace() {
        Completable[] a = new Completable[1024];
        Arrays.fill(a, Completable.complete());
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final Completable c = Completable.concat(Arrays.asList(a));
            final TestObserver<Void> to = new TestObserver<Void>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    c.subscribe(to);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void noInterrupt() throws InterruptedException {
        for (int k = 0; k < 100; k++) {
            final int count = 10;
            final CountDownLatch latch = new CountDownLatch(count);
            final boolean[] interrupted = new boolean[]{ false };
            for (int i = 0; i < count; i++) {
                Completable c0 = Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            System.out.println(("Interrupted! " + (Thread.currentThread())));
                            interrupted[0] = true;
                        }
                    }
                });
                Completable.concat(Arrays.asList(Completable.complete().subscribeOn(io.reactivex.schedulers.Schedulers.io()).observeOn(io.reactivex.schedulers.Schedulers.io()), c0)).subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            Assert.assertFalse("The second Completable was interrupted!", interrupted[0]);
        }
    }
}

