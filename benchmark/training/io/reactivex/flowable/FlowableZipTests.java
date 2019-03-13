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
package io.reactivex.flowable;


import io.reactivex.Flowable;
import io.reactivex.flowables.GroupedFlowable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class FlowableZipTests {
    @Test
    public void testZipObservableOfObservables() {
        // now we have streams of cluster+instanceId
        FlowableEventStream.getEventStream("HTTP-ClusterB", 20).groupBy(new Function<FlowableEventStream.Event, String>() {
            @Override
            public String apply(FlowableEventStream.Event e) {
                return e.instanceId;
            }
        }).flatMap(new Function<GroupedFlowable<String, FlowableEventStream.Event>, Publisher<HashMap<String, String>>>() {
            @Override
            public Publisher<HashMap<String, String>> apply(final GroupedFlowable<String, FlowableEventStream.Event> ge) {
                return ge.scan(new HashMap<String, String>(), new BiFunction<HashMap<String, String>, FlowableEventStream.Event, HashMap<String, String>>() {
                    @Override
                    public HashMap<String, String> apply(HashMap<String, String> accum, FlowableEventStream.Event perInstanceEvent) {
                        synchronized(accum) {
                            accum.put("instance", ge.getKey());
                        }
                        return accum;
                    }
                });
            }
        }).take(10).blockingForEach(new Consumer<HashMap<String, String>>() {
            @Override
            public void accept(HashMap<String, String> v) {
                synchronized(v) {
                    System.out.println(v);
                }
            }
        });
        System.out.println("**** finished");
    }

    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfZip() {
        Flowable<FlowableCovarianceTest.HorrorMovie> horrors = Flowable.just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.CoolRating> ratings = Flowable.just(new FlowableCovarianceTest.CoolRating());
        Flowable.<FlowableCovarianceTest.Movie, FlowableCovarianceTest.CoolRating, FlowableCovarianceTest.Result>zip(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Movie, FlowableCovarianceTest.CoolRating, FlowableCovarianceTest.Result>zip(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.ExtendedResult>zip(horrors, ratings, combine).blockingForEach(extendedAction);
        Flowable.<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.Result>zip(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.ExtendedResult>zip(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Movie, FlowableCovarianceTest.CoolRating, FlowableCovarianceTest.Result>zip(horrors, ratings, combine);
    }

    /**
     * Occasionally zip may be invoked with 0 observables. Test that we don't block indefinitely instead
     * of immediately invoking zip with 0 argument.
     *
     * We now expect an NoSuchElementException since last() requires at least one value and nothing will be emitted.
     */
    @Test(expected = NoSuchElementException.class)
    public void nonBlockingObservable() {
        final Object invoked = new Object();
        Collection<Flowable<Object>> observables = Collections.emptyList();
        Flowable<Object> result = Flowable.zip(observables, new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] args) {
                System.out.println(("received: " + args));
                Assert.assertEquals("No argument should have been passed", 0, args.length);
                return invoked;
            }
        });
        Assert.assertSame(invoked, result.blockingLast());
    }

    BiFunction<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.ExtendedResult> combine = new BiFunction<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.ExtendedResult>() {
        @Override
        public FlowableCovarianceTest.ExtendedResult apply(FlowableCovarianceTest.Media m, FlowableCovarianceTest.Rating r) {
            return new FlowableCovarianceTest.ExtendedResult();
        }
    };

    Consumer<FlowableCovarianceTest.Result> action = new Consumer<FlowableCovarianceTest.Result>() {
        @Override
        public void accept(FlowableCovarianceTest.Result t1) {
            System.out.println(("Result: " + t1));
        }
    };

    Consumer<FlowableCovarianceTest.ExtendedResult> extendedAction = new Consumer<FlowableCovarianceTest.ExtendedResult>() {
        @Override
        public void accept(FlowableCovarianceTest.ExtendedResult t1) {
            System.out.println(("Result: " + t1));
        }
    };

    @Test
    public void zipWithDelayError() {
        Flowable.just(1).zipWith(Flowable.just(2), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, true).test().assertResult(3);
    }

    @Test
    public void zipWithDelayErrorBufferSize() {
        Flowable.just(1).zipWith(Flowable.just(2), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, true, 16).test().assertResult(3);
    }
}

