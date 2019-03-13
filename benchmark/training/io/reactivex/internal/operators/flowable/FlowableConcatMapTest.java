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
import io.reactivex.internal.operators.flowable.FlowableConcatMap.WeakScalarSubscription;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class FlowableConcatMapTest {
    @Test
    public void weakSubscriptionRequest() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0);
        WeakScalarSubscription<Integer> ws = new WeakScalarSubscription<Integer>(1, ts);
        ts.onSubscribe(ws);
        ws.request(0);
        ts.assertEmpty();
        ws.request(1);
        ts.assertResult(1);
        ws.request(1);
        ts.assertResult(1);
    }

    @Test
    public void boundaryFusion() {
        Flowable.range(1, 10000).observeOn(Schedulers.single()).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer t) throws Exception {
                String name = Thread.currentThread().getName();
                if (name.contains("RxSingleScheduler")) {
                    return "RxSingleScheduler";
                }
                return name;
            }
        }).concatMap(new Function<String, Publisher<? extends Object>>() {
            @Override
            public Publisher<? extends Object> apply(String v) throws Exception {
                return Flowable.just(v);
            }
        }).observeOn(Schedulers.computation()).distinct().test().awaitDone(5, TimeUnit.SECONDS).assertResult("RxSingleScheduler");
    }

    @Test
    public void boundaryFusionDelayError() {
        Flowable.range(1, 10000).observeOn(Schedulers.single()).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer t) throws Exception {
                String name = Thread.currentThread().getName();
                if (name.contains("RxSingleScheduler")) {
                    return "RxSingleScheduler";
                }
                return name;
            }
        }).concatMapDelayError(new Function<String, Publisher<? extends Object>>() {
            @Override
            public Publisher<? extends Object> apply(String v) throws Exception {
                return Flowable.just(v);
            }
        }).observeOn(Schedulers.computation()).distinct().test().awaitDone(5, TimeUnit.SECONDS).assertResult("RxSingleScheduler");
    }

    @Test
    public void pollThrows() {
        Flowable.just(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).compose(TestHelper.<Integer>flowableStripBoundary()).concatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.just(v);
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void pollThrowsDelayError() {
        Flowable.just(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).compose(TestHelper.<Integer>flowableStripBoundary()).concatMapDelayError(new Function<Integer, Publisher<Integer>>() {
            @Override
            public Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.just(v);
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void noCancelPrevious() {
        final AtomicInteger counter = new AtomicInteger();
        Flowable.range(1, 5).concatMap(new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) throws Exception {
                return Flowable.just(v).doOnCancel(new Action() {
                    @Override
                    public void run() throws Exception {
                        counter.getAndIncrement();
                    }
                });
            }
        }).test().assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(0, counter.get());
    }
}

