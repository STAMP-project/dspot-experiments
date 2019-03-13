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
import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.LongConsumer;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.processors.PublishProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableMergeDelayErrorTest {
    Subscriber<String> stringSubscriber;

    @Test
    public void testErrorDelayed1() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("one", "two", "three"));
        Flowable<String> m = Flowable.mergeDelayError(f1, f2);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("four");
        Mockito.verify(stringSubscriber, Mockito.times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Flowable keeps sending after onError
        // inner Flowable errors are considered terminal for that source
        // verify(stringSubscriber, times(1)).onNext("six");
        // inner Flowable errors are considered terminal for that source
    }

    @Test
    public void testErrorDelayed2() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("one", "two", "three"));
        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Flowable<String> f3 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("seven", "eight", null));
        final Flowable<String> f4 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("nine"));
        Flowable<String> m = Flowable.mergeDelayError(f1, f2, f3, f4);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(CompositeException.class));
        Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("four");
        Mockito.verify(stringSubscriber, Mockito.times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Flowable keeps sending after onError
        // inner Flowable errors are considered terminal for that source
        // verify(stringSubscriber, times(1)).onNext("six");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed3() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("one", "two", "three"));
        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("four", "five", "six"));
        final Flowable<String> f3 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("seven", "eight", null));
        final Flowable<String> f4 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("nine"));
        Flowable<String> m = Flowable.mergeDelayError(f1, f2, f3, f4);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("four");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("five");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("six");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed4() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("one", "two", "three"));
        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("four", "five", "six"));
        final Flowable<String> f3 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("seven", "eight"));
        final Flowable<String> f4 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("nine", null));
        Flowable<String> m = Flowable.mergeDelayError(f1, f2, f3, f4);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("four");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("five");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("six");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed4WithThreading() {
        final FlowableMergeDelayErrorTest.TestAsyncErrorFlowable f1 = new FlowableMergeDelayErrorTest.TestAsyncErrorFlowable("one", "two", "three");
        final FlowableMergeDelayErrorTest.TestAsyncErrorFlowable f2 = new FlowableMergeDelayErrorTest.TestAsyncErrorFlowable("four", "five", "six");
        final FlowableMergeDelayErrorTest.TestAsyncErrorFlowable f3 = new FlowableMergeDelayErrorTest.TestAsyncErrorFlowable("seven", "eight");
        // throw the error at the very end so no onComplete will be called after it
        final FlowableMergeDelayErrorTest.TestAsyncErrorFlowable f4 = new FlowableMergeDelayErrorTest.TestAsyncErrorFlowable("nine", null);
        Flowable<String> m = Flowable.mergeDelayError(Flowable.unsafeCreate(f1), Flowable.unsafeCreate(f2), Flowable.unsafeCreate(f3), Flowable.unsafeCreate(f4));
        m.subscribe(stringSubscriber);
        try {
            f1.t.join();
            f2.t.join();
            f3.t.join();
            f4.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("four");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("five");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("six");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("nine");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testCompositeErrorDelayed1() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("one", "two", null));
        Flowable<String> m = Flowable.mergeDelayError(f1, f2);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(stringSubscriber, Mockito.times(0)).onNext("three");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("four");
        Mockito.verify(stringSubscriber, Mockito.times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Flowable keeps sending after onError
        // inner Flowable errors are considered terminal for that source
        // verify(stringSubscriber, times(1)).onNext("six");
    }

    @Test
    public void testCompositeErrorDelayed2() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestErrorFlowable("one", "two", null));
        Flowable<String> m = Flowable.mergeDelayError(f1, f2);
        FlowableMergeDelayErrorTest.CaptureObserver w = new FlowableMergeDelayErrorTest.CaptureObserver();
        m.subscribe(w);
        Assert.assertNotNull(w.e);
        int size = size();
        if (size != 2) {
            w.e.printStackTrace();
        }
        Assert.assertEquals(2, size);
        // if (w.e instanceof CompositeException) {
        // assertEquals(2, ((CompositeException) w.e).getExceptions().size());
        // w.e.printStackTrace();
        // } else {
        // fail("Expecting CompositeException");
        // }
    }

    /**
     * The unit tests below are from OperationMerge and should ensure the normal merge functionality is correct.
     */
    @Test
    public void testMergeFlowableOfFlowables() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        Flowable<Flowable<String>> flowableOfFlowables = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
            @Override
            public void subscribe(Subscriber<? super Flowable<String>> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                // simulate what would happen in a Flowable
                subscriber.onNext(f1);
                subscriber.onNext(f2);
                subscriber.onComplete();
            }
        });
        Flowable<String> m = Flowable.mergeDelayError(flowableOfFlowables);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(2)).onNext("hello");
    }

    @Test
    public void testMergeArray() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        Flowable<String> m = Flowable.mergeDelayError(f1, f2);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(2)).onNext("hello");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMergeList() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        List<Flowable<String>> listOfFlowables = new ArrayList<Flowable<String>>();
        listOfFlowables.add(f1);
        listOfFlowables.add(f2);
        Flowable<String> m = Flowable.mergeDelayError(Flowable.fromIterable(listOfFlowables));
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(2)).onNext("hello");
    }

    @Test
    public void testMergeArrayWithThreading() {
        final FlowableMergeDelayErrorTest.TestASynchronousFlowable f1 = new FlowableMergeDelayErrorTest.TestASynchronousFlowable();
        final FlowableMergeDelayErrorTest.TestASynchronousFlowable f2 = new FlowableMergeDelayErrorTest.TestASynchronousFlowable();
        Flowable<String> m = Flowable.mergeDelayError(Flowable.unsafeCreate(f1), Flowable.unsafeCreate(f2));
        m.subscribe(stringSubscriber);
        try {
            f1.t.join();
            f2.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(2)).onNext("hello");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
    }

    @Test(timeout = 1000L)
    public void testSynchronousError() {
        final Flowable<Flowable<String>> f1 = Flowable.error(new RuntimeException("unit test"));
        final CountDownLatch latch = new CountDownLatch(1);
        Flowable.mergeDelayError(f1).subscribe(new DefaultSubscriber<String>() {
            @Override
            public void onComplete() {
                Assert.fail("Expected onError path");
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onNext(String s) {
                Assert.fail("Expected onError path");
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Assert.fail("interrupted");
        }
    }

    private static class TestSynchronousFlowable implements Publisher<String> {
        @Override
        public void subscribe(Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            subscriber.onNext("hello");
            subscriber.onComplete();
        }
    }

    private static class TestASynchronousFlowable implements Publisher<String> {
        Thread t;

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    subscriber.onNext("hello");
                    subscriber.onComplete();
                }
            });
            t.start();
        }
    }

    private static class TestErrorFlowable implements Publisher<String> {
        String[] valuesToReturn;

        TestErrorFlowable(String... values) {
            valuesToReturn = values;
        }

        @Override
        public void subscribe(Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            boolean errorThrown = false;
            for (String s : valuesToReturn) {
                if (s == null) {
                    System.out.println("throwing exception");
                    subscriber.onError(new NullPointerException());
                    errorThrown = true;
                    // purposefully not returning here so it will continue calling onNext
                    // so that we also test that we handle bad sequences like this
                } else {
                    subscriber.onNext(s);
                }
            }
            if (!errorThrown) {
                subscriber.onComplete();
            }
        }
    }

    private static class TestAsyncErrorFlowable implements Publisher<String> {
        String[] valuesToReturn;

        TestAsyncErrorFlowable(String... values) {
            valuesToReturn = values;
        }

        Thread t;

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String s : valuesToReturn) {
                        if (s == null) {
                            System.out.println("throwing exception");
                            try {
                                Thread.sleep(100);
                            } catch (Throwable e) {
                            }
                            subscriber.onError(new NullPointerException());
                            return;
                        } else {
                            subscriber.onNext(s);
                        }
                    }
                    System.out.println("subscription complete");
                    subscriber.onComplete();
                }
            });
            t.start();
        }
    }

    private static class CaptureObserver extends DefaultSubscriber<String> {
        volatile Throwable e;

        @Override
        public void onComplete() {
        }

        @Override
        public void onError(Throwable e) {
            this.e = e;
        }

        @Override
        public void onNext(String args) {
        }
    }

    @Test
    public void testErrorInParentFlowable() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.mergeDelayError(Flowable.just(Flowable.just(1), Flowable.just(2)).startWith(Flowable.<Integer>error(new RuntimeException()))).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertTerminated();
        ts.assertValues(1, 2);
        Assert.assertEquals(1, ts.errorCount());
    }

    @Test
    public void testErrorInParentFlowableDelayed() throws Exception {
        for (int i = 0; i < 50; i++) {
            final FlowableMergeDelayErrorTest.TestASynchronous1sDelayedFlowable f1 = new FlowableMergeDelayErrorTest.TestASynchronous1sDelayedFlowable();
            final FlowableMergeDelayErrorTest.TestASynchronous1sDelayedFlowable f2 = new FlowableMergeDelayErrorTest.TestASynchronous1sDelayedFlowable();
            Flowable<Flowable<String>> parentFlowable = Flowable.unsafeCreate(new Publisher<Flowable<String>>() {
                @Override
                public void subscribe(Subscriber<? super Flowable<String>> op) {
                    op.onSubscribe(new BooleanSubscription());
                    op.onNext(Flowable.unsafeCreate(f1));
                    op.onNext(Flowable.unsafeCreate(f2));
                    op.onError(new NullPointerException("throwing exception in parent"));
                }
            });
            stringSubscriber = TestHelper.mockSubscriber();
            TestSubscriber<String> ts = new TestSubscriber<String>(stringSubscriber);
            Flowable<String> m = Flowable.mergeDelayError(parentFlowable);
            m.subscribe(ts);
            System.out.println(("testErrorInParentFlowableDelayed | " + i));
            ts.awaitTerminalEvent(2000, TimeUnit.MILLISECONDS);
            ts.assertTerminated();
            Mockito.verify(stringSubscriber, Mockito.times(2)).onNext("hello");
            Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
            Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
        }
    }

    private static class TestASynchronous1sDelayedFlowable implements Publisher<String> {
        Thread t;

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        subscriber.onError(e);
                    }
                    subscriber.onNext("hello");
                    subscriber.onComplete();
                }
            });
            t.start();
        }
    }

    @Test
    public void testDelayErrorMaxConcurrent() {
        final List<Long> requests = new ArrayList<Long>();
        Flowable<Integer> source = Flowable.mergeDelayError(Flowable.just(Flowable.just(1).hide(), Flowable.<Integer>error(new TestException())).doOnRequest(new LongConsumer() {
            @Override
            public void accept(long t1) {
                requests.add(t1);
            }
        }), 1);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.subscribe(ts);
        ts.assertValue(1);
        ts.assertTerminated();
        ts.assertError(TestException.class);
        Assert.assertEquals(Arrays.asList(1L, 1L, 1L), requests);
    }

    // This is pretty much a clone of testMergeList but with the overloaded MergeDelayError for Iterables
    @Test
    public void mergeIterable() {
        final Flowable<String> f1 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        final Flowable<String> f2 = Flowable.unsafeCreate(new FlowableMergeDelayErrorTest.TestSynchronousFlowable());
        List<Flowable<String>> listOfFlowables = new ArrayList<Flowable<String>>();
        listOfFlowables.add(f1);
        listOfFlowables.add(f2);
        Flowable<String> m = Flowable.mergeDelayError(listOfFlowables);
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(2)).onNext("hello");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void iterableMaxConcurrent() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        Flowable.mergeDelayError(Arrays.asList(pp1, pp2), 1).subscribe(ts);
        Assert.assertTrue("ps1 has no subscribers?!", pp1.hasSubscribers());
        Assert.assertFalse("ps2 has subscribers?!", pp2.hasSubscribers());
        pp1.onNext(1);
        pp1.onComplete();
        Assert.assertFalse("ps1 has subscribers?!", pp1.hasSubscribers());
        Assert.assertTrue("ps2 has no subscribers?!", pp2.hasSubscribers());
        pp2.onNext(2);
        pp2.onComplete();
        ts.assertValues(1, 2);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void iterableMaxConcurrentError() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        Flowable.mergeDelayError(Arrays.asList(pp1, pp2), 1).subscribe(ts);
        Assert.assertTrue("ps1 has no subscribers?!", pp1.hasSubscribers());
        Assert.assertFalse("ps2 has subscribers?!", pp2.hasSubscribers());
        pp1.onNext(1);
        pp1.onError(new TestException());
        Assert.assertFalse("ps1 has subscribers?!", pp1.hasSubscribers());
        Assert.assertTrue("ps2 has no subscribers?!", pp2.hasSubscribers());
        pp2.onNext(2);
        pp2.onError(new TestException());
        ts.assertValues(1, 2);
        ts.assertError(CompositeException.class);
        ts.assertNotComplete();
        CompositeException ce = ((CompositeException) (ts.errors().get(0)));
        Assert.assertEquals(2, ce.getExceptions().size());
    }

    @Test
    public void array() {
        for (int i = 1; i < 100; i++) {
            @SuppressWarnings("unchecked")
            Flowable<Integer>[] sources = new Flowable[i];
            Arrays.fill(sources, Flowable.just(1));
            Integer[] expected = new Integer[i];
            for (int j = 0; j < i; j++) {
                expected[j] = 1;
            }
            Flowable.mergeArrayDelayError(sources).test().assertResult(expected);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeArrayDelayError() {
        Flowable.mergeArrayDelayError(Flowable.just(1), Flowable.just(2)).test().assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayErrorWithError() {
        Flowable.mergeDelayError(Arrays.asList(Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())), Flowable.just(2))).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void mergeDelayError() {
        Flowable.mergeDelayError(Flowable.just(Flowable.just(1), Flowable.just(2))).test().assertResult(1, 2);
    }

    @Test
    public void mergeDelayErrorWithError() {
        Flowable.mergeDelayError(Flowable.just(Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())), Flowable.just(2))).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void mergeDelayErrorMaxConcurrency() {
        Flowable.mergeDelayError(Flowable.just(Flowable.just(1), Flowable.just(2)), 1).test().assertResult(1, 2);
    }

    @Test
    public void mergeDelayErrorWithErrorMaxConcurrency() {
        Flowable.mergeDelayError(Flowable.just(Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())), Flowable.just(2)), 1).test().assertFailure(TestException.class, 1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayErrorMaxConcurrency() {
        Flowable.mergeDelayError(Arrays.asList(Flowable.just(1), Flowable.just(2)), 1).test().assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayErrorWithErrorMaxConcurrency() {
        Flowable.mergeDelayError(Arrays.asList(Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())), Flowable.just(2)), 1).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void mergeDelayError3() {
        Flowable.mergeDelayError(Flowable.just(1), Flowable.just(2), Flowable.just(3)).test().assertResult(1, 2, 3);
    }

    @Test
    public void mergeDelayError3WithError() {
        Flowable.mergeDelayError(Flowable.just(1), Flowable.just(2).concatWith(Flowable.<Integer>error(new TestException())), Flowable.just(3)).test().assertFailure(TestException.class, 1, 2, 3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayError() {
        Flowable.mergeDelayError(Arrays.asList(Flowable.just(1), Flowable.just(2))).test().assertResult(1, 2);
    }
}

