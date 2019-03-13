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
package io.reactivex.processors;


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.subscribers.SubscriberFusion;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class AsyncProcessorTest extends FlowableProcessorTest<Object> {
    private final Throwable testException = new Throwable();

    @Test
    public void testNeverCompleted() {
        AsyncProcessor<String> processor = AsyncProcessor.create();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onNext("two");
        processor.onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(subscriber, Mockito.never()).onError(testException);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testCompleted() {
        AsyncProcessor<String> processor = AsyncProcessor.create();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onNext("two");
        processor.onNext("three");
        processor.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSubscribeAfterCompleted() {
        AsyncProcessor<String> processor = AsyncProcessor.create();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.onNext("one");
        processor.onNext("two");
        processor.onNext("three");
        processor.onComplete();
        processor.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSubscribeAfterError() {
        AsyncProcessor<String> processor = AsyncProcessor.create();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.onNext("one");
        processor.onNext("two");
        processor.onNext("three");
        RuntimeException re = new RuntimeException("failed");
        processor.onError(re);
        processor.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onError(re);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testError() {
        AsyncProcessor<String> processor = AsyncProcessor.create();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onNext("one");
        processor.onNext("two");
        processor.onNext("three");
        processor.onError(testException);
        processor.onNext("four");
        processor.onError(new Throwable());
        processor.onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(subscriber, Mockito.times(1)).onError(testException);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testUnsubscribeBeforeCompleted() {
        AsyncProcessor<String> processor = AsyncProcessor.create();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber);
        processor.subscribe(ts);
        processor.onNext("one");
        processor.onNext("two");
        ts.dispose();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        processor.onNext("three");
        processor.onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testEmptySubjectCompleted() {
        AsyncProcessor<String> processor = AsyncProcessor.create();
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        processor.subscribe(subscriber);
        processor.onComplete();
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.never()).onNext(null);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Can receive timeout if subscribe never receives an onError/onComplete ... which reveals a race condition.
     */
    @Test(timeout = 10000)
    public void testSubscribeCompletionRaceCondition() {
        /* With non-threadsafe code this fails most of the time on my dev laptop and is non-deterministic enough
        to act as a unit test to the race conditions.

        With the synchronization code in place I can not get this to fail on my laptop.
         */
        for (int i = 0; i < 50; i++) {
            final AsyncProcessor<String> processor = AsyncProcessor.create();
            final AtomicReference<String> value1 = new AtomicReference<String>();
            processor.subscribe(new io.reactivex.functions.Consumer<String>() {
                @Override
                public void accept(String t1) {
                    try {
                        // simulate a slow observer
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    value1.set(t1);
                }
            });
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    processor.onNext("value");
                    processor.onComplete();
                }
            });
            AsyncProcessorTest.SubjectSubscriberThread t2 = new AsyncProcessorTest.SubjectSubscriberThread(processor);
            AsyncProcessorTest.SubjectSubscriberThread t3 = new AsyncProcessorTest.SubjectSubscriberThread(processor);
            AsyncProcessorTest.SubjectSubscriberThread t4 = new AsyncProcessorTest.SubjectSubscriberThread(processor);
            AsyncProcessorTest.SubjectSubscriberThread t5 = new AsyncProcessorTest.SubjectSubscriberThread(processor);
            t2.start();
            t3.start();
            t1.start();
            t4.start();
            t5.start();
            try {
                t1.join();
                t2.join();
                t3.join();
                t4.join();
                t5.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Assert.assertEquals("value", value1.get());
            Assert.assertEquals("value", t2.value.get());
            Assert.assertEquals("value", t3.value.get());
            Assert.assertEquals("value", t4.value.get());
            Assert.assertEquals("value", t5.value.get());
        }
    }

    private static class SubjectSubscriberThread extends Thread {
        private final AsyncProcessor<String> processor;

        private final AtomicReference<String> value = new AtomicReference<String>();

        SubjectSubscriberThread(AsyncProcessor<String> processor) {
            this.processor = processor;
        }

        @Override
        public void run() {
            try {
                // a timeout exception will happen if we don't get a terminal state
                String v = processor.timeout(2000, TimeUnit.MILLISECONDS).blockingSingle();
                value.set(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // FIXME subscriber methods are not allowed to throw
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery() {
    // AsyncSubject<String> ps = AsyncSubject.create();
    // 
    // ps.subscribe();
    // TestSubscriber<String> ts = new TestSubscriber<String>();
    // ps.subscribe(ts);
    // 
    // try {
    // ps.onError(new RuntimeException("an exception"));
    // fail("expect OnErrorNotImplementedException");
    // } catch (OnErrorNotImplementedException e) {
    // // ignore
    // }
    // // even though the onError above throws we should still receive it on the other subscriber
    // assertEquals(1, ts.getOnErrorEvents().size());
    // }
    // FIXME subscriber methods are not allowed to throw
    // /**
    // * This one has multiple failures so should get a CompositeException
    // */
    // @Test
    // public void testOnErrorThrowsDoesntPreventDelivery2() {
    // AsyncSubject<String> ps = AsyncSubject.create();
    // 
    // ps.subscribe();
    // ps.subscribe();
    // TestSubscriber<String> ts = new TestSubscriber<String>();
    // ps.subscribe(ts);
    // ps.subscribe();
    // ps.subscribe();
    // ps.subscribe();
    // 
    // try {
    // ps.onError(new RuntimeException("an exception"));
    // fail("expect OnErrorNotImplementedException");
    // } catch (CompositeException e) {
    // // we should have 5 of them
    // assertEquals(5, e.getExceptions().size());
    // }
    // // even though the onError above throws we should still receive it on the other subscriber
    // assertEquals(1, ts.getOnErrorEvents().size());
    // }
    @Test
    public void testCurrentStateMethodsNormal() {
        AsyncProcessor<Object> as = AsyncProcessor.create();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onNext(1);
        Assert.assertFalse(as.hasValue());// AP no longer reports it has a value until it is terminated

        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());// AP no longer reports it has a value until it is terminated

        Assert.assertNull(as.getThrowable());
        as.onComplete();
        Assert.assertTrue(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertTrue(as.hasComplete());
        Assert.assertEquals(1, as.getValue());
        Assert.assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsEmpty() {
        AsyncProcessor<Object> as = AsyncProcessor.create();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onComplete();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertTrue(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsError() {
        AsyncProcessor<Object> as = AsyncProcessor.create();
        Assert.assertFalse(as.hasValue());
        Assert.assertFalse(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertNull(as.getThrowable());
        as.onError(new TestException());
        Assert.assertFalse(as.hasValue());
        Assert.assertTrue(as.hasThrowable());
        Assert.assertFalse(as.hasComplete());
        Assert.assertNull(as.getValue());
        Assert.assertTrue(((as.getThrowable()) instanceof TestException));
    }

    @Test
    public void fusionLive() {
        AsyncProcessor<Integer> ap = new AsyncProcessor<Integer>();
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        ap.subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC));
        ts.assertNoValues().assertNoErrors().assertNotComplete();
        ap.onNext(1);
        ts.assertNoValues().assertNoErrors().assertNotComplete();
        ap.onComplete();
        ts.assertResult(1);
    }

    @Test
    public void fusionOfflie() {
        AsyncProcessor<Integer> ap = new AsyncProcessor<Integer>();
        ap.onNext(1);
        ap.onComplete();
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        ap.subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertResult(1);
    }

    @Test
    public void onSubscribeAfterDone() {
        AsyncProcessor<Object> p = AsyncProcessor.create();
        BooleanSubscription bs = new BooleanSubscription();
        p.onSubscribe(bs);
        Assert.assertFalse(bs.isCancelled());
        p.onComplete();
        bs = new BooleanSubscription();
        p.onSubscribe(bs);
        Assert.assertTrue(bs.isCancelled());
        p.test().assertResult();
    }

    @Test
    public void cancelUpfront() {
        AsyncProcessor<Object> p = AsyncProcessor.create();
        Assert.assertFalse(p.hasSubscribers());
        p.test().assertEmpty();
        p.test().assertEmpty();
        p.test(0L, true).assertEmpty();
        Assert.assertTrue(p.hasSubscribers());
    }

    @Test
    public void cancelRace() {
        AsyncProcessor<Object> p = AsyncProcessor.create();
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestSubscriber<Object> ts1 = p.test();
            final TestSubscriber<Object> ts2 = p.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts1.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts2.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void onErrorCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final AsyncProcessor<Object> p = AsyncProcessor.create();
            final TestSubscriber<Object> ts1 = p.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts1.cancel();
                }
            };
            final TestException ex = new TestException();
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    p.onError(ex);
                }
            };
            TestHelper.race(r1, r2);
            if ((ts1.errorCount()) != 0) {
                ts1.assertFailure(TestException.class);
            } else {
                ts1.assertEmpty();
            }
        }
    }

    @Test
    public void onNextCrossCancel() {
        AsyncProcessor<Object> p = AsyncProcessor.create();
        final TestSubscriber<Object> ts2 = new TestSubscriber<Object>();
        TestSubscriber<Object> ts1 = new TestSubscriber<Object>() {
            @Override
            public void onNext(Object t) {
                ts2.cancel();
                super.onNext(t);
            }
        };
        p.subscribe(ts1);
        p.subscribe(ts2);
        p.onNext(1);
        p.onComplete();
        ts1.assertResult(1);
        ts2.assertEmpty();
    }

    @Test
    public void onErrorCrossCancel() {
        AsyncProcessor<Object> p = AsyncProcessor.create();
        final TestSubscriber<Object> ts2 = new TestSubscriber<Object>();
        TestSubscriber<Object> ts1 = new TestSubscriber<Object>() {
            @Override
            public void onError(Throwable t) {
                ts2.cancel();
                super.onError(t);
            }
        };
        p.subscribe(ts1);
        p.subscribe(ts2);
        p.onError(new TestException());
        ts1.assertFailure(TestException.class);
        ts2.assertEmpty();
    }

    @Test
    public void onCompleteCrossCancel() {
        AsyncProcessor<Object> p = AsyncProcessor.create();
        final TestSubscriber<Object> ts2 = new TestSubscriber<Object>();
        TestSubscriber<Object> ts1 = new TestSubscriber<Object>() {
            @Override
            public void onComplete() {
                ts2.cancel();
                super.onComplete();
            }
        };
        p.subscribe(ts1);
        p.subscribe(ts2);
        p.onComplete();
        ts1.assertResult();
        ts2.assertEmpty();
    }
}

