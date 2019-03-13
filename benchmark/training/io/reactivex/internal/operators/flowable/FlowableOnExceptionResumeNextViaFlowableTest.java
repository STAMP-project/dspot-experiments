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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableOnExceptionResumeNextViaFlowableTest {
    @Test
    public void testResumeNextWithException() {
        // Trigger failure on second element
        FlowableOnExceptionResumeNextViaFlowableTest.TestObservable f = new FlowableOnExceptionResumeNextViaFlowableTest.TestObservable("one", "EXCEPTION", "two", "three");
        Flowable<String> w = Flowable.unsafeCreate(f);
        Flowable<String> resume = Flowable.just("twoResume", "threeResume");
        Flowable<String> flowable = w.onExceptionResumeNext(resume);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("threeResume");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testResumeNextWithRuntimeException() {
        // Trigger failure on second element
        FlowableOnExceptionResumeNextViaFlowableTest.TestObservable f = new FlowableOnExceptionResumeNextViaFlowableTest.TestObservable("one", "RUNTIMEEXCEPTION", "two", "three");
        Flowable<String> w = Flowable.unsafeCreate(f);
        Flowable<String> resume = Flowable.just("twoResume", "threeResume");
        Flowable<String> flowable = w.onExceptionResumeNext(resume);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("threeResume");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testThrowablePassesThru() {
        // Trigger failure on second element
        FlowableOnExceptionResumeNextViaFlowableTest.TestObservable f = new FlowableOnExceptionResumeNextViaFlowableTest.TestObservable("one", "THROWABLE", "two", "three");
        Flowable<String> w = Flowable.unsafeCreate(f);
        Flowable<String> resume = Flowable.just("twoResume", "threeResume");
        Flowable<String> flowable = w.onExceptionResumeNext(resume);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onNext("twoResume");
        Mockito.verify(subscriber, Mockito.never()).onNext("threeResume");
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testErrorPassesThru() {
        // Trigger failure on second element
        FlowableOnExceptionResumeNextViaFlowableTest.TestObservable f = new FlowableOnExceptionResumeNextViaFlowableTest.TestObservable("one", "ERROR", "two", "three");
        Flowable<String> w = Flowable.unsafeCreate(f);
        Flowable<String> resume = Flowable.just("twoResume", "threeResume");
        Flowable<String> flowable = w.onExceptionResumeNext(resume);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(subscriber).onSubscribe(((Subscription) (ArgumentMatchers.any())));
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onNext("twoResume");
        Mockito.verify(subscriber, Mockito.never()).onNext("threeResume");
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void testMapResumeAsyncNext() {
        // Trigger multiple failures
        Flowable<String> w = Flowable.just("one", "fail", "two", "three", "fail");
        // Resume Observable is async
        FlowableOnExceptionResumeNextViaFlowableTest.TestObservable f = new FlowableOnExceptionResumeNextViaFlowableTest.TestObservable("twoResume", "threeResume");
        Flowable<String> resume = Flowable.unsafeCreate(f);
        // Introduce map function that fails intermittently (Map does not prevent this when the observer is a
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
        Flowable<String> flowable = w.onExceptionResumeNext(resume);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        try {
            // if the thread gets started (which it shouldn't if it's working correctly)
            if ((f.t) != null) {
                f.t.join();
            }
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("twoResume");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("threeResume");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testBackpressure() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(0, 100000).onExceptionResumeNext(Flowable.just(1)).observeOn(Schedulers.computation()).map(new io.reactivex.functions.Function<Integer, Integer>() {
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
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
    }

    private static class TestObservable implements Publisher<String> {
        final String[] values;

        Thread t;

        TestObservable(String... values) {
            this.values = values;
        }

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            System.out.println("TestObservable subscribed to ...");
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestObservable thread");
                        for (String s : values) {
                            if ("EXCEPTION".equals(s)) {
                                throw new Exception("Forced Exception");
                            } else
                                if ("RUNTIMEEXCEPTION".equals(s)) {
                                    throw new RuntimeException("Forced RuntimeException");
                                } else
                                    if ("ERROR".equals(s)) {
                                        throw new Error("Forced Error");
                                    } else
                                        if ("THROWABLE".equals(s)) {
                                            throw new Throwable("Forced Throwable");
                                        }



                            System.out.println(("TestObservable onNext: " + s));
                            subscriber.onNext(s);
                        }
                        System.out.println("TestObservable onComplete");
                        subscriber.onComplete();
                    } catch (Throwable e) {
                        System.out.println(("TestObservable onError: " + e));
                        subscriber.onError(e);
                    }
                }
            });
            System.out.println("starting TestObservable thread");
            t.start();
            System.out.println("done starting TestObservable thread");
        }
    }

    @Test
    public void normalBackpressure() {
        TestSubscriber<Integer> ts = TestSubscriber.create(0);
        PublishProcessor<Integer> pp = PublishProcessor.create();
        pp.onExceptionResumeNext(Flowable.range(3, 2)).subscribe(ts);
        ts.request(2);
        pp.onNext(1);
        pp.onNext(2);
        pp.onError(new TestException("Forced failure"));
        ts.assertValues(1, 2);
        ts.assertNoErrors();
        ts.assertNotComplete();
        ts.request(2);
        ts.assertValues(1, 2, 3, 4);
        ts.assertNoErrors();
        ts.assertComplete();
    }
}

