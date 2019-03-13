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
package io.reactivex.parallel;


import io.reactivex.Publisher;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscribers.BasicFuseableSubscriber;
import io.reactivex.processors.UnicastProcessor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static QueueFuseable.BOUNDARY;
import static QueueFuseable.NONE;


public class ParallelFromPublisherTest {
    @Test
    public void sourceOverflow() {
        parallel(1, 1).sequential(1).test(0).assertFailure(MissingBackpressureException.class);
    }

    @Test
    public void fusedFilterBecomesEmpty() {
        Flowable.just(1).filter(Functions.alwaysFalse()).parallel().sequential().test().assertResult();
    }

    static final class StripBoundary<T> extends Flowable<T> implements FlowableTransformer<T, T> {
        final io.reactivex.Flowable<T> source;

        StripBoundary(Flowable<T> source) {
            this.source = source;
        }

        @Override
        public Publisher<T> apply(Flowable<T> upstream) {
            return new ParallelFromPublisherTest.StripBoundary<T>(upstream);
        }

        @Override
        protected void subscribeActual(Subscriber<? super T> s) {
            source.subscribe(new ParallelFromPublisherTest.StripBoundary.StripBoundarySubscriber<T>(s));
        }

        static final class StripBoundarySubscriber<T> extends BasicFuseableSubscriber<T, T> {
            StripBoundarySubscriber(Subscriber<? super T> downstream) {
                super(downstream);
            }

            @Override
            public void onNext(T t) {
                downstream.onNext(t);
            }

            @Override
            public int requestFusion(int mode) {
                QueueSubscription<T> fs = qs;
                if (fs != null) {
                    int m = fs.requestFusion((mode & (~(BOUNDARY))));
                    this.sourceMode = m;
                    return m;
                }
                return NONE;
            }

            @Override
            public T poll() throws Exception {
                return qs.poll();
            }
        }
    }

    @Test
    public void syncFusedMapCrash() {
        Flowable.just(1).map(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).compose(new ParallelFromPublisherTest.StripBoundary<Object>(null)).parallel().sequential().test().assertFailure(TestException.class);
    }

    @Test
    public void asyncFusedMapCrash() {
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.onNext(1);
        up.map(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).compose(new ParallelFromPublisherTest.StripBoundary<Object>(null)).parallel().sequential().test().assertFailure(TestException.class);
        Assert.assertFalse(up.hasSubscribers());
    }

    @Test
    public void boundaryConfinement() {
        final Set<String> between = new HashSet<String>();
        final ConcurrentHashMap<String, String> processing = new ConcurrentHashMap<String, String>();
        parallel(2, 1).runOn(io.reactivex.schedulers.Schedulers.computation(), 1).map(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) throws Exception {
                processing.putIfAbsent(Thread.currentThread().getName(), "");
                return v;
            }
        }).sequential().test().awaitDone(5, TimeUnit.SECONDS).assertSubscribed().assertValueSet(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).assertComplete().assertNoErrors();
        Assert.assertEquals(between.toString(), 1, between.size());
        Assert.assertTrue(between.toString(), between.iterator().next().contains("RxSingleScheduler"));
        Map<String, String> map = processing;// AnimalSniffer: CHM.keySet() in Java 8 returns KeySetView

        for (String e : map.keySet()) {
            Assert.assertTrue(map.toString(), e.contains("RxComputationThreadPool"));
        }
    }
}

