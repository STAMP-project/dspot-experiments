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
package io.reactivex.observable;


import io.reactivex.observable.ObservableEventStream.Event;
import io.reactivex.observables.GroupedObservable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Observable;
import org.junit.Assert;
import org.junit.Test;


public class ObservableZipTests {
    @Test
    public void testZipObservableOfObservables() throws Exception {
        // now we have streams of cluster+instanceId
        getEventStream("HTTP-ClusterB", 20).groupBy(new Function<ObservableEventStream.Event, String>() {
            @Override
            public String apply(ObservableEventStream.Event e) {
                return e.instanceId;
            }
        }).flatMap(new Function<GroupedObservable<String, ObservableEventStream.Event>, Observable<HashMap<String, String>>>() {
            @Override
            public Observable<HashMap<String, String>> apply(final GroupedObservable<String, Event> ge) {
                return ge.scan(new HashMap<String, String>(), new BiFunction<HashMap<String, String>, Event, HashMap<String, String>>() {
                    @Override
                    public HashMap<String, String> apply(HashMap<String, String> accum, Event perInstanceEvent) {
                        synchronized(accum) {
                            accum.put("instance", ge.getKey());
                        }
                        return accum;
                    }
                });
            }
        }).take(10).blockingForEach(new Consumer<Object>() {
            @Override
            public void accept(Object pv) {
                synchronized(pv) {
                    System.out.println(pv);
                }
            }
        });
        System.out.println("**** finished");
        Thread.sleep(200);// make sure the event streams receive their interrupt

    }

    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfZip() {
        Observable<ObservableCovarianceTest.HorrorMovie> horrors = Observable.just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.CoolRating> ratings = Observable.just(new ObservableCovarianceTest.CoolRating());
        Observable.<ObservableCovarianceTest.Movie, ObservableCovarianceTest.CoolRating, ObservableCovarianceTest.Result>zip(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Movie, ObservableCovarianceTest.CoolRating, ObservableCovarianceTest.Result>zip(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.ExtendedResult>zip(horrors, ratings, combine).blockingForEach(extendedAction);
        Observable.<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.Result>zip(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.ExtendedResult>zip(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Movie, ObservableCovarianceTest.CoolRating, ObservableCovarianceTest.Result>zip(horrors, ratings, combine);
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
        Collection<Observable<Object>> observables = Collections.emptyList();
        Observable<Object> result = Observable.zip(observables, new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] args) {
                System.out.println(("received: " + args));
                Assert.assertEquals("No argument should have been passed", 0, args.length);
                return invoked;
            }
        });
        Assert.assertSame(invoked, result.blockingLast());
    }

    BiFunction<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.ExtendedResult> combine = new BiFunction<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.ExtendedResult>() {
        @Override
        public ObservableCovarianceTest.ExtendedResult apply(ObservableCovarianceTest.Media m, ObservableCovarianceTest.Rating r) {
            return new ObservableCovarianceTest.ExtendedResult();
        }
    };

    Consumer<ObservableCovarianceTest.Result> action = new Consumer<ObservableCovarianceTest.Result>() {
        @Override
        public void accept(ObservableCovarianceTest.Result t1) {
            System.out.println(("Result: " + t1));
        }
    };

    Consumer<ObservableCovarianceTest.ExtendedResult> extendedAction = new Consumer<ObservableCovarianceTest.ExtendedResult>() {
        @Override
        public void accept(ObservableCovarianceTest.ExtendedResult t1) {
            System.out.println(("Result: " + t1));
        }
    };

    @Test
    public void zipWithDelayError() {
        just(1).zipWith(just(2), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, true).test().assertResult(3);
    }

    @Test
    public void zipWithDelayErrorBufferSize() {
        just(1).zipWith(just(2), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, true, 16).test().assertResult(3);
    }
}

