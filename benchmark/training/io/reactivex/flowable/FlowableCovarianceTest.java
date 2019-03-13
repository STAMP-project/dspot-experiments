/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.flowable;


import io.reactivex.Flowable;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


/**
 * Test super/extends of generics.
 *
 * See https://github.com/Netflix/RxJava/pull/331
 */
public class FlowableCovarianceTest {
    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfFrom() {
        Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        Flowable.<FlowableCovarianceTest.Movie>fromIterable(new ArrayList<FlowableCovarianceTest.HorrorMovie>());
        // Observable.<HorrorMovie>from(new Movie()); // may not compile
    }

    @Test
    public void testSortedList() {
        Comparator<FlowableCovarianceTest.Media> sortFunction = new Comparator<FlowableCovarianceTest.Media>() {
            @Override
            public int compare(FlowableCovarianceTest.Media t1, FlowableCovarianceTest.Media t2) {
                return 1;
            }
        };
        // this one would work without the covariance generics
        Flowable<FlowableCovarianceTest.Media> f = Flowable.just(new FlowableCovarianceTest.Movie(), new FlowableCovarianceTest.TVSeason(), new FlowableCovarianceTest.Album());
        f.toSortedList(sortFunction);
        // this one would NOT work without the covariance generics
        Flowable<FlowableCovarianceTest.Movie> f2 = Flowable.just(new FlowableCovarianceTest.Movie(), new FlowableCovarianceTest.ActionMovie(), new FlowableCovarianceTest.HorrorMovie());
        f2.toSortedList(sortFunction);
    }

    @Test
    public void testGroupByCompose() {
        Flowable<FlowableCovarianceTest.Movie> movies = Flowable.just(new FlowableCovarianceTest.HorrorMovie(), new FlowableCovarianceTest.ActionMovie(), new FlowableCovarianceTest.Movie());
        TestSubscriber<String> ts = new TestSubscriber<String>();
        movies.groupBy(new Function<FlowableCovarianceTest.Movie, Object>() {
            @Override
            public Object apply(FlowableCovarianceTest.Movie v) {
                return v.getClass();
            }
        }).doOnNext(new Consumer<GroupedFlowable<Object, FlowableCovarianceTest.Movie>>() {
            @Override
            public void accept(GroupedFlowable<Object, FlowableCovarianceTest.Movie> g) {
                System.out.println(g.getKey());
            }
        }).flatMap(new Function<GroupedFlowable<Object, FlowableCovarianceTest.Movie>, Publisher<String>>() {
            @Override
            public Publisher<String> apply(GroupedFlowable<Object, FlowableCovarianceTest.Movie> g) {
                return g.doOnNext(new Consumer<FlowableCovarianceTest.Movie>() {
                    @Override
                    public void accept(FlowableCovarianceTest.Movie v) {
                        System.out.println(v);
                    }
                }).compose(new io.reactivex.FlowableTransformer<FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie>() {
                    @Override
                    public Publisher<FlowableCovarianceTest.Movie> apply(Flowable<FlowableCovarianceTest.Movie> m) {
                        return m.concatWith(Flowable.just(new FlowableCovarianceTest.ActionMovie()));
                    }
                }).map(new Function<Object, String>() {
                    @Override
                    public String apply(Object v) {
                        return v.toString();
                    }
                });
            }
        }).subscribe(ts);
        ts.assertTerminated();
        ts.assertNoErrors();
        // System.out.println(ts.getOnNextEvents());
        Assert.assertEquals(6, ts.valueCount());
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose() {
        Flowable<FlowableCovarianceTest.HorrorMovie> movie = Flowable.just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.Movie> movie2 = movie.compose(new io.reactivex.FlowableTransformer<FlowableCovarianceTest.HorrorMovie, FlowableCovarianceTest.Movie>() {
            @Override
            public Publisher<FlowableCovarianceTest.Movie> apply(Flowable<FlowableCovarianceTest.HorrorMovie> t) {
                return Flowable.just(new FlowableCovarianceTest.Movie());
            }
        });
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose2() {
        Flowable<FlowableCovarianceTest.Movie> movie = Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.HorrorMovie> movie2 = movie.compose(new io.reactivex.FlowableTransformer<FlowableCovarianceTest.Movie, FlowableCovarianceTest.HorrorMovie>() {
            @Override
            public Publisher<FlowableCovarianceTest.HorrorMovie> apply(Flowable<FlowableCovarianceTest.Movie> t) {
                return Flowable.just(new FlowableCovarianceTest.HorrorMovie());
            }
        });
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose3() {
        Flowable<FlowableCovarianceTest.Movie> movie = Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.HorrorMovie> movie2 = movie.compose(new io.reactivex.FlowableTransformer<FlowableCovarianceTest.Movie, FlowableCovarianceTest.HorrorMovie>() {
            @Override
            public Publisher<FlowableCovarianceTest.HorrorMovie> apply(Flowable<FlowableCovarianceTest.Movie> t) {
                return Flowable.just(new FlowableCovarianceTest.HorrorMovie()).map(new Function<FlowableCovarianceTest.HorrorMovie, FlowableCovarianceTest.HorrorMovie>() {
                    @Override
                    public FlowableCovarianceTest.HorrorMovie apply(FlowableCovarianceTest.HorrorMovie v) {
                        return v;
                    }
                });
            }
        });
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose4() {
        Flowable<FlowableCovarianceTest.HorrorMovie> movie = Flowable.just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.HorrorMovie> movie2 = movie.compose(new io.reactivex.FlowableTransformer<FlowableCovarianceTest.HorrorMovie, FlowableCovarianceTest.HorrorMovie>() {
            @Override
            public Publisher<FlowableCovarianceTest.HorrorMovie> apply(Flowable<FlowableCovarianceTest.HorrorMovie> t1) {
                return t1.map(new Function<FlowableCovarianceTest.HorrorMovie, FlowableCovarianceTest.HorrorMovie>() {
                    @Override
                    public FlowableCovarianceTest.HorrorMovie apply(FlowableCovarianceTest.HorrorMovie v) {
                        return v;
                    }
                });
            }
        });
    }

    @Test
    public void testComposeWithDeltaLogic() {
        List<FlowableCovarianceTest.Movie> list1 = Arrays.asList(new FlowableCovarianceTest.Movie(), new FlowableCovarianceTest.HorrorMovie(), new FlowableCovarianceTest.ActionMovie());
        List<FlowableCovarianceTest.Movie> list2 = Arrays.asList(new FlowableCovarianceTest.ActionMovie(), new FlowableCovarianceTest.Movie(), new FlowableCovarianceTest.HorrorMovie(), new FlowableCovarianceTest.ActionMovie());
        Flowable<List<FlowableCovarianceTest.Movie>> movies = Flowable.just(list1, list2);
        movies.compose(FlowableCovarianceTest.deltaTransformer);
    }

    static Function<List<List<FlowableCovarianceTest.Movie>>, Flowable<FlowableCovarianceTest.Movie>> calculateDelta = new Function<List<List<FlowableCovarianceTest.Movie>>, Flowable<FlowableCovarianceTest.Movie>>() {
        @Override
        public Flowable<FlowableCovarianceTest.Movie> apply(List<List<FlowableCovarianceTest.Movie>> listOfLists) {
            if ((listOfLists.size()) == 1) {
                return Flowable.fromIterable(listOfLists.get(0));
            } else {
                // diff the two
                List<FlowableCovarianceTest.Movie> newList = listOfLists.get(1);
                List<FlowableCovarianceTest.Movie> oldList = new ArrayList<FlowableCovarianceTest.Movie>(listOfLists.get(0));
                Set<FlowableCovarianceTest.Movie> delta = new LinkedHashSet<FlowableCovarianceTest.Movie>();
                delta.addAll(newList);
                // remove all that match in old
                delta.removeAll(oldList);
                // filter oldList to those that aren't in the newList
                oldList.removeAll(newList);
                // for all left in the oldList we'll create DROP events
                for (@SuppressWarnings("unused")
                FlowableCovarianceTest.Movie old : oldList) {
                    delta.add(new FlowableCovarianceTest.Movie());
                }
                return Flowable.fromIterable(delta);
            }
        }
    };

    static io.reactivex.FlowableTransformer<List<FlowableCovarianceTest.Movie>, FlowableCovarianceTest.Movie> deltaTransformer = new io.reactivex.FlowableTransformer<List<FlowableCovarianceTest.Movie>, FlowableCovarianceTest.Movie>() {
        @Override
        public Publisher<FlowableCovarianceTest.Movie> apply(Flowable<List<FlowableCovarianceTest.Movie>> movieList) {
            return movieList.startWith(new ArrayList<FlowableCovarianceTest.Movie>()).buffer(2, 1).skip(1).flatMap(FlowableCovarianceTest.calculateDelta);
        }
    };

    /* Most tests are moved into their applicable classes such as [Operator]Tests.java */
    static class Media {}

    static class Movie extends FlowableCovarianceTest.Media {}

    static class HorrorMovie extends FlowableCovarianceTest.Movie {}

    static class ActionMovie extends FlowableCovarianceTest.Movie {}

    static class Album extends FlowableCovarianceTest.Media {}

    static class TVSeason extends FlowableCovarianceTest.Media {}

    static class Rating {}

    static class CoolRating extends FlowableCovarianceTest.Rating {}

    static class Result {}

    static class ExtendedResult extends FlowableCovarianceTest.Result {}
}

