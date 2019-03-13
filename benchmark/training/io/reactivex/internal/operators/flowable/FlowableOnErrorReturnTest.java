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
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableOnErrorReturnTest {
    @Test
    public void testResumeNext() {
        FlowableOnErrorReturnTest.TestFlowable f = new FlowableOnErrorReturnTest.TestFlowable("one");
        Flowable<String> w = Flowable.unsafeCreate(f);
        final AtomicReference<Throwable> capturedException = new AtomicReference<Throwable>();
        Flowable<String> flowable = w.onErrorReturn(new io.reactivex.functions.Function<Throwable, String>() {
            @Override
            public String apply(Throwable e) {
                capturedException.set(e);
                return "failure";
            }
        });
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("failure");
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Assert.assertNotNull(capturedException.get());
    }

    /**
     * Test that when a function throws an exception this is propagated through onError.
     */
    @Test
    public void testFunctionThrowsError() {
        FlowableOnErrorReturnTest.TestFlowable f = new FlowableOnErrorReturnTest.TestFlowable("one");
        Flowable<String> w = Flowable.unsafeCreate(f);
        final AtomicReference<Throwable> capturedException = new AtomicReference<Throwable>();
        Flowable<String> flowable = w.onErrorReturn(new io.reactivex.functions.Function<Throwable, String>() {
            @Override
            public String apply(Throwable e) {
                capturedException.set(e);
                throw new RuntimeException("exception from function");
            }
        });
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        try {
            f.t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        // we should get the "one" value before the error
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        // we should have received an onError call on the Observer since the resume function threw an exception
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(0)).onComplete();
        Assert.assertNotNull(capturedException.get());
    }

    @Test
    public void testMapResumeAsyncNext() {
        // Trigger multiple failures
        Flowable<String> w = Flowable.just("one", "fail", "two", "three", "fail");
        // Introduce map function that fails intermittently (Map does not prevent this when the observer is a
        // rx.operator incl onErrorResumeNextViaFlowable)
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
        Flowable<String> flowable = w.onErrorReturn(new io.reactivex.functions.Function<Throwable, String>() {
            @Override
            public String apply(Throwable t1) {
                return "resume";
            }
        });
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<String> ts = new TestSubscriber<String>(subscriber, Long.MAX_VALUE);
        flowable.subscribe(ts);
        ts.awaitTerminalEvent();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("resume");
    }

    @Test
    public void testBackpressure() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(0, 100000).onErrorReturn(new io.reactivex.functions.Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable t1) {
                return 1;
            }
        }).observeOn(Schedulers.computation()).map(new io.reactivex.functions.Function<Integer, Integer>() {
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

    private static class TestFlowable implements Publisher<String> {
        final String[] values;

        Thread t;

        TestFlowable(String... values) {
            this.values = values;
        }

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            System.out.println("TestFlowable subscribed to ...");
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestFlowable thread");
                        for (String s : values) {
                            System.out.println(("TestFlowable onNext: " + s));
                            subscriber.onNext(s);
                        }
                        throw new RuntimeException("Forced Failure");
                    } catch (Throwable e) {
                        subscriber.onError(e);
                    }
                }
            });
            System.out.println("starting TestFlowable thread");
            t.start();
            System.out.println("done starting TestFlowable thread");
        }
    }

    @Test
    public void normalBackpressure() {
        TestSubscriber<Integer> ts = TestSubscriber.create(0);
        PublishProcessor<Integer> pp = PublishProcessor.create();
        pp.onErrorReturn(new io.reactivex.functions.Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable e) {
                return 3;
            }
        }).subscribe(ts);
        ts.request(2);
        pp.onNext(1);
        pp.onNext(2);
        pp.onError(new TestException("Forced failure"));
        ts.assertValues(1, 2);
        ts.assertNoErrors();
        ts.assertNotComplete();
        ts.request(2);
        ts.assertValues(1, 2, 3);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void returnItem() {
        onErrorReturnItem(1).test().assertResult(1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(onErrorReturnItem(1));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.onErrorReturnItem(1);
            }
        });
    }

    @Test
    public void doubleOnError() {
        onErrorReturnItem(1).test().assertResult(1);
    }
}

