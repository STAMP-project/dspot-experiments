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
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableTimeIntervalTest {
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private Subscriber<Timed<Integer>> subscriber;

    private TestScheduler testScheduler;

    private PublishProcessor<Integer> processor;

    private Flowable<Timed<Integer>> flowable;

    @Test
    public void testTimeInterval() {
        InOrder inOrder = Mockito.inOrder(subscriber);
        flowable.subscribe(subscriber);
        testScheduler.advanceTimeBy(1000, FlowableTimeIntervalTest.TIME_UNIT);
        processor.onNext(1);
        testScheduler.advanceTimeBy(2000, FlowableTimeIntervalTest.TIME_UNIT);
        processor.onNext(2);
        testScheduler.advanceTimeBy(3000, FlowableTimeIntervalTest.TIME_UNIT);
        processor.onNext(3);
        processor.onComplete();
        inOrder.verify(subscriber, Mockito.times(1)).onNext(new Timed<Integer>(1, 1000, FlowableTimeIntervalTest.TIME_UNIT));
        inOrder.verify(subscriber, Mockito.times(1)).onNext(new Timed<Integer>(2, 2000, FlowableTimeIntervalTest.TIME_UNIT));
        inOrder.verify(subscriber, Mockito.times(1)).onNext(new Timed<Integer>(3, 3000, FlowableTimeIntervalTest.TIME_UNIT));
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void timeIntervalDefault() {
        final TestScheduler scheduler = new TestScheduler();
        RxJavaPlugins.setComputationSchedulerHandler(new io.reactivex.functions.Function<Scheduler, Scheduler>() {
            @Override
            public io.reactivex.Scheduler apply(Scheduler v) throws Exception {
                return scheduler;
            }
        });
        try {
            Flowable.range(1, 5).timeInterval().map(new io.reactivex.functions.Function<Timed<Integer>, Long>() {
                @Override
                public Long apply(Timed<Integer> v) throws Exception {
                    return v.time();
                }
            }).test().assertResult(0L, 0L, 0L, 0L, 0L);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void timeIntervalDefaultSchedulerCustomUnit() {
        final TestScheduler scheduler = new TestScheduler();
        RxJavaPlugins.setComputationSchedulerHandler(new io.reactivex.functions.Function<Scheduler, Scheduler>() {
            @Override
            public io.reactivex.Scheduler apply(Scheduler v) throws Exception {
                return scheduler;
            }
        });
        try {
            Flowable.range(1, 5).timeInterval(TimeUnit.SECONDS).map(new io.reactivex.functions.Function<Timed<Integer>, Long>() {
                @Override
                public Long apply(Timed<Integer> v) throws Exception {
                    return v.time();
                }
            }).test().assertResult(0L, 0L, 0L, 0L, 0L);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).timeInterval());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void error() {
        Flowable.error(new TestException()).timeInterval().test().assertFailure(TestException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Publisher<Timed<Object>>>() {
            @Override
            public io.reactivex.Publisher<Timed<Object>> apply(Flowable<Object> f) throws Exception {
                return f.timeInterval();
            }
        });
    }
}

