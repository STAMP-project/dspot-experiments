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
package io.reactivex.internal.operators.maybe;


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.QueueSubscription;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.operators.maybe.MaybeMergeArray.MergeMaybeObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.SubscriberFusion;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;


public class MaybeMergeArrayTest {
    @SuppressWarnings("unchecked")
    @Test
    public void normal() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(SYNC);
        Maybe.mergeArray(Maybe.just(1), Maybe.just(2)).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(NONE)).assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void fusedPollMixed() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Maybe.mergeArray(Maybe.just(1), Maybe.<Integer>empty(), Maybe.just(2)).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void fusedEmptyCheck() {
        Maybe.mergeArray(Maybe.just(1), Maybe.<Integer>empty(), Maybe.just(2)).subscribe(new FlowableSubscriber<Integer>() {
            QueueSubscription<Integer> qs;

            @Override
            public void onSubscribe(Subscription s) {
                qs = ((QueueSubscription<Integer>) (s));
                Assert.assertEquals(ASYNC, qs.requestFusion(ANY));
            }

            @Override
            public void onNext(Integer value) {
                Assert.assertFalse(qs.isEmpty());
                qs.clear();
                Assert.assertTrue(qs.isEmpty());
                qs.cancel();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void cancel() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        Maybe.mergeArray(Maybe.just(1), Maybe.<Integer>empty(), Maybe.just(2)).subscribe(ts);
        ts.cancel();
        ts.request(10);
        ts.assertEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void firstErrors() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        Maybe.mergeArray(Maybe.<Integer>error(new TestException()), Maybe.<Integer>empty(), Maybe.just(2)).subscribe(ts);
        ts.assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorFused() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Maybe.mergeArray(Maybe.<Integer>error(new TestException()), Maybe.just(2)).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishSubject<Integer> ps1 = PublishSubject.create();
                final PublishSubject<Integer> ps2 = PublishSubject.create();
                TestSubscriber<Integer> ts = Maybe.mergeArray(ps1.singleElement(), ps2.singleElement()).test();
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                ts.assertFailure(Throwable.class);
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeBadSource() {
        Maybe.mergeArray(new Maybe<Integer>() {
            @Override
            protected void subscribeActual(MaybeObserver<? super Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onSuccess(1);
                observer.onSuccess(2);
                observer.onSuccess(3);
            }
        }, Maybe.never()).test().assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void smallOffer2Throws() {
        Maybe.mergeArray(Maybe.never(), Maybe.never()).subscribe(new FlowableSubscriber<Object>() {
            @SuppressWarnings("rawtypes")
            @Override
            public void onSubscribe(Subscription s) {
                MergeMaybeObserver o = ((MergeMaybeObserver) (s));
                try {
                    o.queue.offer(1, 2);
                    Assert.fail("Should have thrown");
                } catch (UnsupportedOperationException ex) {
                    // expected
                }
            }

            @Override
            public void onNext(Object t) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void largeOffer2Throws() {
        io.reactivex.Maybe<Integer>[] a = new Maybe[1024];
        Arrays.fill(a, Maybe.never());
        Maybe.mergeArray(a).subscribe(new FlowableSubscriber<Object>() {
            @SuppressWarnings("rawtypes")
            @Override
            public void onSubscribe(Subscription s) {
                MergeMaybeObserver o = ((MergeMaybeObserver) (s));
                try {
                    o.queue.offer(1, 2);
                    Assert.fail("Should have thrown");
                } catch (UnsupportedOperationException ex) {
                    // expected
                }
                o.queue.drop();
            }

            @Override
            public void onNext(Object t) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
}

