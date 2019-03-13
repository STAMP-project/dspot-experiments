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


import Scheduler.Worker;
import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.processors.MissingBackpressureException;
import io.reactivex.processors.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableSampleTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    private Subscriber<Long> subscriber;

    private Subscriber<Object> subscriber2;

    @Test
    public void testSample() {
        Flowable<Long> source = Flowable.unsafeCreate(new Publisher<Long>() {
            @Override
            public void subscribe(final Subscriber<? super Long> subscriber1) {
                subscriber1.onSubscribe(new BooleanSubscription());
                innerScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        subscriber1.onNext(1L);
                    }
                }, 1, TimeUnit.SECONDS);
                innerScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        subscriber1.onNext(2L);
                    }
                }, 2, TimeUnit.SECONDS);
                innerScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        subscriber1.onComplete();
                    }
                }, 3, TimeUnit.SECONDS);
            }
        });
        Flowable<Long> sampled = source.sample(400L, TimeUnit.MILLISECONDS, scheduler);
        sampled.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        scheduler.advanceTimeTo(800L, TimeUnit.MILLISECONDS);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(Long.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1200L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1L);
        Mockito.verify(subscriber, Mockito.never()).onNext(2L);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1600L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(1L);
        Mockito.verify(subscriber, Mockito.never()).onNext(2L);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2000L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(1L);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2L);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3000L, TimeUnit.MILLISECONDS);
        inOrder.verify(subscriber, Mockito.never()).onNext(1L);
        inOrder.verify(subscriber, Mockito.never()).onNext(2L);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerNormal() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> sampler = PublishProcessor.create();
        Flowable<Integer> m = source.sample(sampler);
        m.subscribe(subscriber2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        source.onNext(3);
        source.onNext(4);
        sampler.onNext(2);
        source.onComplete();
        sampler.onNext(3);
        InOrder inOrder = Mockito.inOrder(subscriber2);
        inOrder.verify(subscriber2, Mockito.never()).onNext(1);
        inOrder.verify(subscriber2, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber2, Mockito.never()).onNext(3);
        inOrder.verify(subscriber2, Mockito.times(1)).onNext(4);
        inOrder.verify(subscriber2, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerNoDuplicates() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> sampler = PublishProcessor.create();
        Flowable<Integer> m = source.sample(sampler);
        m.subscribe(subscriber2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        sampler.onNext(1);
        source.onNext(3);
        source.onNext(4);
        sampler.onNext(2);
        sampler.onNext(2);
        source.onComplete();
        sampler.onNext(3);
        InOrder inOrder = Mockito.inOrder(subscriber2);
        inOrder.verify(subscriber2, Mockito.never()).onNext(1);
        inOrder.verify(subscriber2, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber2, Mockito.never()).onNext(3);
        inOrder.verify(subscriber2, Mockito.times(1)).onNext(4);
        inOrder.verify(subscriber2, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerTerminatingEarly() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> sampler = PublishProcessor.create();
        Flowable<Integer> m = source.sample(sampler);
        m.subscribe(subscriber2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        sampler.onComplete();
        source.onNext(3);
        source.onNext(4);
        InOrder inOrder = Mockito.inOrder(subscriber2);
        inOrder.verify(subscriber2, Mockito.never()).onNext(1);
        inOrder.verify(subscriber2, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber2, Mockito.times(1)).onComplete();
        inOrder.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerEmitAndTerminate() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> sampler = PublishProcessor.create();
        Flowable<Integer> m = source.sample(sampler);
        m.subscribe(subscriber2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        source.onNext(3);
        source.onComplete();
        sampler.onNext(2);
        sampler.onComplete();
        InOrder inOrder = Mockito.inOrder(subscriber2);
        inOrder.verify(subscriber2, Mockito.never()).onNext(1);
        inOrder.verify(subscriber2, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber2, Mockito.never()).onNext(3);
        inOrder.verify(subscriber2, Mockito.times(1)).onComplete();
        inOrder.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerEmptySource() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> sampler = PublishProcessor.create();
        Flowable<Integer> m = source.sample(sampler);
        m.subscribe(subscriber2);
        source.onComplete();
        sampler.onNext(1);
        InOrder inOrder = Mockito.inOrder(subscriber2);
        inOrder.verify(subscriber2, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerSourceThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> sampler = PublishProcessor.create();
        Flowable<Integer> m = source.sample(sampler);
        m.subscribe(subscriber2);
        source.onNext(1);
        source.onError(new RuntimeException("Forced failure!"));
        sampler.onNext(1);
        InOrder inOrder = Mockito.inOrder(subscriber2);
        inOrder.verify(subscriber2, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void sampleWithSamplerThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> sampler = PublishProcessor.create();
        Flowable<Integer> m = source.sample(sampler);
        m.subscribe(subscriber2);
        source.onNext(1);
        sampler.onNext(1);
        sampler.onError(new RuntimeException("Forced failure!"));
        InOrder inOrder = Mockito.inOrder(subscriber2);
        inOrder.verify(subscriber2, Mockito.times(1)).onNext(1);
        inOrder.verify(subscriber2, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testSampleUnsubscribe() {
        final Subscription s = Mockito.mock(io.reactivex.exceptions.Subscription.class);
        Flowable<Integer> f = Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(s);
            }
        });
        f.throttleLast(1, TimeUnit.MILLISECONDS).subscribe().dispose();
        Mockito.verify(s).cancel();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().sample(1, TimeUnit.SECONDS, new TestScheduler()));
        TestHelper.checkDisposed(PublishProcessor.create().sample(Flowable.never()));
    }

    @Test
    public void error() {
        Flowable.error(new TestException()).sample(1, TimeUnit.SECONDS).test().assertFailure(TestException.class);
    }

    @Test
    public void backpressureOverflow() {
        BehaviorProcessor.createDefault(1).sample(1, TimeUnit.MILLISECONDS).test(0L).awaitDone(5, TimeUnit.SECONDS).assertFailure(MissingBackpressureException.class);
    }

    @Test
    public void backpressureOverflowWithOtherPublisher() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp1.sample(pp2).test(0L);
        pp1.onNext(1);
        pp2.onNext(2);
        ts.assertFailure(MissingBackpressureException.class);
        Assert.assertFalse(pp1.hasSubscribers());
        Assert.assertFalse(pp2.hasSubscribers());
    }

    @Test
    public void emitLastTimed() {
        Flowable.just(1).sample(1, TimeUnit.DAYS, true).test().assertResult(1);
    }

    @Test
    public void emitLastTimedEmpty() {
        Flowable.empty().sample(1, TimeUnit.DAYS, true).test().assertResult();
    }

    @Test
    public void emitLastTimedCustomScheduler() {
        Flowable.just(1).sample(1, TimeUnit.DAYS, Schedulers.single(), true).test().assertResult(1);
    }

    @Test
    public void emitLastTimedRunCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestScheduler scheduler = new TestScheduler();
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.sample(1, TimeUnit.SECONDS, scheduler, true).test();
            pp.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
                }
            };
            TestHelper.race(r1, r2);
            ts.assertResult(1);
        }
    }

    @Test
    public void emitLastOther() {
        Flowable.just(1).sample(Flowable.timer(1, TimeUnit.DAYS), true).test().assertResult(1);
    }

    @Test
    public void emitLastOtherEmpty() {
        Flowable.empty().sample(Flowable.timer(1, TimeUnit.DAYS), true).test().assertResult();
    }

    @Test
    public void emitLastOtherRunCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final PublishProcessor<Integer> sampler = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.sample(sampler, true).test();
            pp.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sampler.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            ts.assertResult(1);
        }
    }

    @Test
    public void emitLastOtherCompleteCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final PublishProcessor<Integer> sampler = PublishProcessor.create();
            TestSubscriber<Integer> ts = pp.sample(sampler, true).test();
            pp.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sampler.onComplete();
                }
            };
            TestHelper.race(r1, r2);
            ts.assertResult(1);
        }
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.sample(1, TimeUnit.SECONDS);
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.sample(PublishProcessor.create());
            }
        });
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(PublishProcessor.create().sample(PublishProcessor.create()));
    }
}

