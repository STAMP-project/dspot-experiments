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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.internal.functions.Functions;
import io.reactivex.subscribers.SubscriberFusion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableRangeTest {
    @Test
    public void testRangeStartAt2Count3() {
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        Flowable.range(2, 3).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(3);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(4);
        Mockito.verify(subscriber, Mockito.never()).onNext(5);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
    }

    @Test
    public void testRangeUnsubscribe() {
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        final AtomicInteger count = new AtomicInteger();
        Flowable.range(1, 1000).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                count.incrementAndGet();
            }
        }).take(3).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(3);
        Mockito.verify(subscriber, Mockito.never()).onNext(4);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void testRangeWithZero() {
        Flowable.range(1, 0);
    }

    @Test
    public void testRangeWithOverflow2() {
        Flowable.range(Integer.MAX_VALUE, 0);
    }

    @Test
    public void testRangeWithOverflow3() {
        Flowable.range(1, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeWithOverflow4() {
        Flowable.range(2, Integer.MAX_VALUE);
    }

    @Test
    public void testRangeWithOverflow5() {
        Assert.assertFalse(Flowable.range(Integer.MIN_VALUE, 0).blockingIterable().iterator().hasNext());
    }

    @Test
    public void testBackpressureViaRequest() {
        Flowable<Integer> f = Flowable.range(1, Flowable.bufferSize());
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        ts.assertNoValues();
        ts.request(1);
        f.subscribe(ts);
        ts.assertValue(1);
        ts.request(2);
        ts.assertValues(1, 2, 3);
        ts.request(3);
        ts.assertValues(1, 2, 3, 4, 5, 6);
        ts.request(Flowable.bufferSize());
        ts.assertTerminated();
    }

    @Test
    public void testNoBackpressure() {
        ArrayList<Integer> list = new ArrayList<Integer>(((Flowable.bufferSize()) * 2));
        for (int i = 1; i <= (((Flowable.bufferSize()) * 2) + 1); i++) {
            list.add(i);
        }
        Flowable<Integer> f = Flowable.range(1, list.size());
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        ts.assertNoValues();
        ts.request(Long.MAX_VALUE);// infinite

        f.subscribe(ts);
        ts.assertValueSequence(list);
        ts.assertTerminated();
    }

    @Test
    public void testWithBackpressure1() {
        for (int i = 0; i < 100; i++) {
            testWithBackpressureOneByOne(i);
        }
    }

    @Test
    public void testWithBackpressureAllAtOnce() {
        for (int i = 0; i < 100; i++) {
            testWithBackpressureAllAtOnce(i);
        }
    }

    @Test
    public void testWithBackpressureRequestWayMore() {
        Flowable<Integer> source = Flowable.range(50, 100);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        ts.request(150);
        source.subscribe(ts);
        List<Integer> list = new ArrayList<Integer>(100);
        for (int i = 0; i < 100; i++) {
            list.add((i + 50));
        }
        ts.request(50);// and then some

        ts.assertValueSequence(list);
        ts.assertTerminated();
    }

    @Test
    public void testRequestOverflow() {
        final AtomicInteger count = new AtomicInteger();
        int n = 10;
        Flowable.range(1, n).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(2);
            }

            @Override
            public void onComplete() {
                // do nothing
            }

            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onNext(Integer t) {
                count.incrementAndGet();
                request(((Long.MAX_VALUE) - 1));
            }
        });
        Assert.assertEquals(n, count.get());
    }

    @Test
    public void testEmptyRangeSendsOnCompleteEagerlyWithRequestZero() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        Flowable.range(1, 0).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                // request(0);
            }

            @Override
            public void onComplete() {
                completed.set(true);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
            }
        });
        Assert.assertTrue(completed.get());
    }

    @Test(timeout = 1000)
    public void testNearMaxValueWithoutBackpressure() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(((Integer.MAX_VALUE) - 1), 2).subscribe(ts);
        ts.assertComplete();
        ts.assertNoErrors();
        ts.assertValues(((Integer.MAX_VALUE) - 1), Integer.MAX_VALUE);
    }

    @Test(timeout = 1000)
    public void testNearMaxValueWithBackpressure() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(3L);
        Flowable.range(((Integer.MAX_VALUE) - 1), 2).subscribe(ts);
        ts.assertComplete();
        ts.assertNoErrors();
        ts.assertValues(((Integer.MAX_VALUE) - 1), Integer.MAX_VALUE);
    }

    @Test
    public void negativeCount() {
        try {
            Flowable.range(1, (-1));
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("count >= 0 required but it was -1", ex.getMessage());
        }
    }

    @Test
    public void requestWrongFusion() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ASYNC);
        Flowable.range(1, 5).subscribe(ts);
        SubscriberFusion.assertFusion(ts, NONE).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void countOne() {
        Flowable.range(5495454, 1).test().assertResult(5495454);
    }

    @Test
    public void fused() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.range(1, 2).subscribe(ts);
        SubscriberFusion.assertFusion(ts, SYNC).assertResult(1, 2);
    }

    @Test
    public void fusedReject() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ASYNC);
        Flowable.range(1, 2).subscribe(ts);
        SubscriberFusion.assertFusion(ts, NONE).assertResult(1, 2);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Flowable.range(1, 2));
    }

    @Test
    public void fusedClearIsEmpty() {
        TestHelper.checkFusedIsEmptyClear(Flowable.range(1, 2));
    }

    @Test
    public void noOverflow() {
        Flowable.range(((Integer.MAX_VALUE) - 1), 2);
        Flowable.range(Integer.MIN_VALUE, 2);
        Flowable.range(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void conditionalNormal() {
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(Flowable.range(1, 5));
        TestHelper.assertBadRequestReported(Flowable.range(1, 5).filter(Functions.alwaysTrue()));
    }

    @Test
    public void conditionalNormalSlowpath() {
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).test(5).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void conditionalSlowPathTakeExact() {
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).take(5).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void slowPathTakeExact() {
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).take(5).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void conditionalSlowPathRebatch() {
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).rebatchRequests(1).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void slowPathRebatch() {
        Flowable.range(1, 5).rebatchRequests(1).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void slowPathCancel() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(2L) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                onComplete();
            }
        };
        Flowable.range(1, 5).subscribe(ts);
        ts.assertResult(1);
    }

    @Test
    public void fastPathCancel() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                onComplete();
            }
        };
        Flowable.range(1, 5).subscribe(ts);
        ts.assertResult(1);
    }

    @Test
    public void conditionalSlowPathCancel() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1L) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                onComplete();
            }
        };
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).subscribe(ts);
        ts.assertResult(1);
    }

    @Test
    public void conditionalFastPathCancel() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                onComplete();
            }
        };
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).subscribe(ts);
        ts.assertResult(1);
    }

    @Test
    public void conditionalRequestOneByOne() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1L) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                request(1);
            }
        };
        Flowable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return (v % 2) == 0;
            }
        }).subscribe(ts);
        ts.assertResult(2, 4);
    }

    @Test
    public void conditionalRequestOneByOne2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1L) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                request(1);
            }
        };
        Flowable.range(1, 5).filter(Functions.alwaysTrue()).subscribe(ts);
        ts.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void fastPathCancelExact() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 5L) {
                    cancel();
                    onComplete();
                }
            }
        };
        Flowable.range(1, 5).subscribe(ts);
        ts.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void conditionalFastPathCancelExact() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 5L) {
                    cancel();
                    onComplete();
                }
            }
        };
        Flowable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return (v % 2) == 0;
            }
        }).subscribe(ts);
        ts.assertResult(2, 4);
    }

    @Test
    public void conditionalCancel1() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(2L) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    cancel();
                    onComplete();
                }
            }
        };
        Flowable.range(1, 2).filter(Functions.alwaysTrue()).subscribe(ts);
        ts.assertResult(1);
    }

    @Test
    public void conditionalCancel2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(2L) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 2) {
                    cancel();
                    onComplete();
                }
            }
        };
        Flowable.range(1, 2).filter(Functions.alwaysTrue()).subscribe(ts);
        ts.assertResult(1, 2);
    }
}

