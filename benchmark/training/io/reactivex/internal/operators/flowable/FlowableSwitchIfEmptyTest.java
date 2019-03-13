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
package io.reactivex.internal.operators.flowable;


import io.reactivex.Flowable;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class FlowableSwitchIfEmptyTest {
    @Test
    public void testSwitchWhenNotEmpty() throws Exception {
        final AtomicBoolean subscribed = new AtomicBoolean(false);
        final Flowable<Integer> flowable = Flowable.just(4).switchIfEmpty(Flowable.just(2).doOnSubscribe(new io.reactivex.functions.Consumer<Subscription>() {
            @Override
            public void accept(Subscription s) {
                subscribed.set(true);
            }
        }));
        Assert.assertEquals(4, flowable.blockingSingle().intValue());
        Assert.assertFalse(subscribed.get());
    }

    @Test
    public void testSwitchWhenEmpty() throws Exception {
        final Flowable<Integer> flowable = Flowable.<Integer>empty().switchIfEmpty(Flowable.fromIterable(Arrays.asList(42)));
        Assert.assertEquals(42, flowable.blockingSingle().intValue());
    }

    @Test
    public void testSwitchWithProducer() throws Exception {
        final AtomicBoolean emitted = new AtomicBoolean(false);
        Flowable<Long> withProducer = Flowable.unsafeCreate(new Publisher<Long>() {
            @Override
            public void subscribe(final Subscriber<? super Long> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        if ((n > 0) && (emitted.compareAndSet(false, true))) {
                            emitted.set(true);
                            subscriber.onNext(42L);
                            subscriber.onComplete();
                        }
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        });
        final Flowable<Long> flowable = Flowable.<Long>empty().switchIfEmpty(withProducer);
        Assert.assertEquals(42, flowable.blockingSingle().intValue());
    }

    @Test
    public void testSwitchTriggerUnsubscribe() throws Exception {
        final BooleanSubscription bs = new BooleanSubscription();
        Flowable<Long> withProducer = Flowable.unsafeCreate(new Publisher<Long>() {
            @Override
            public void subscribe(final Subscriber<? super Long> subscriber) {
                subscriber.onSubscribe(bs);
                subscriber.onNext(42L);
            }
        });
        Flowable.<Long>empty().switchIfEmpty(withProducer).lift(new io.reactivex.FlowableOperator<Long, Long>() {
            @Override
            public Subscriber<? super Long> apply(final Subscriber<? super Long> child) {
                return new DefaultSubscriber<Long>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        cancel();
                    }
                };
            }
        }).subscribe();
        Assert.assertTrue(bs.isCancelled());
        // FIXME no longer assertable
        // assertTrue(sub.isUnsubscribed());
    }

    @Test
    public void testSwitchShouldNotTriggerUnsubscribe() {
        final BooleanSubscription bs = new BooleanSubscription();
        Flowable.unsafeCreate(new Publisher<Long>() {
            @Override
            public void subscribe(final Subscriber<? super Long> subscriber) {
                subscriber.onSubscribe(bs);
                subscriber.onComplete();
            }
        }).switchIfEmpty(Flowable.<Long>never()).subscribe();
        Assert.assertFalse(bs.isCancelled());
    }

    @Test
    public void testSwitchRequestAlternativeObservableWithBackpressure() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1L);
        Flowable.<Integer>empty().switchIfEmpty(Flowable.just(1, 2, 3)).subscribe(ts);
        Assert.assertEquals(Arrays.asList(1), ts.values());
        ts.assertNoErrors();
        ts.request(1);
        ts.assertValueCount(2);
        ts.request(1);
        ts.assertValueCount(3);
    }

    @Test
    public void testBackpressureNoRequest() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        Flowable.<Integer>empty().switchIfEmpty(Flowable.just(1, 2, 3)).subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
    }

    @Test
    public void testBackpressureOnFirstObservable() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        Flowable.just(1, 2, 3).switchIfEmpty(Flowable.just(4, 5, 6)).subscribe(ts);
        ts.assertNotComplete();
        ts.assertNoErrors();
        ts.assertNoValues();
    }

    @Test(timeout = 10000)
    public void testRequestsNotLost() throws InterruptedException {
        final TestSubscriber<Long> ts = new TestSubscriber<Long>(0L);
        Flowable.unsafeCreate(new Publisher<Long>() {
            @Override
            public void subscribe(final Subscriber<? super Long> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    final AtomicBoolean completed = new AtomicBoolean(false);

                    @Override
                    public void request(long n) {
                        if ((n > 0) && (completed.compareAndSet(false, true))) {
                            io.reactivex.schedulers.Schedulers.io().createWorker().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onComplete();
                                }
                            }, 100, MILLISECONDS);
                        }
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        }).switchIfEmpty(Flowable.fromIterable(Arrays.asList(1L, 2L, 3L))).subscribeOn(io.reactivex.schedulers.Schedulers.computation()).subscribe(ts);
        Thread.sleep(50);
        // request while first observable is still finishing (as empty)
        ts.request(1);
        ts.request(1);
        Thread.sleep(500);
        ts.assertNotComplete();
        ts.assertNoErrors();
        ts.assertValueCount(2);
        ts.dispose();
    }
}

