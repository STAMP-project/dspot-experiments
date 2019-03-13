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
package io.reactivex.observable;


import io.reactivex.observables.GroupedObservable;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test super/extends of generics.
 *
 * See https://github.com/Netflix/RxJava/pull/331
 */
public class ObservableCovarianceTest {
    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfFrom() {
        Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        <ObservableCovarianceTest.Movie>fromIterable(new ArrayList<ObservableCovarianceTest.HorrorMovie>());
        // Observable.<HorrorMovie>from(new Movie()); // may not compile
    }

    @Test
    public void testSortedList() {
        Comparator<ObservableCovarianceTest.Media> sortFunction = new Comparator<ObservableCovarianceTest.Media>() {
            @Override
            public int compare(ObservableCovarianceTest.Media t1, ObservableCovarianceTest.Media t2) {
                return 1;
            }
        };
        // this one would work without the covariance generics
        Observable<ObservableCovarianceTest.Media> o = Observable.just(new ObservableCovarianceTest.Movie(), new ObservableCovarianceTest.TVSeason(), new ObservableCovarianceTest.Album());
        o.toSortedList(sortFunction);
        // this one would NOT work without the covariance generics
        Observable<ObservableCovarianceTest.Movie> o2 = Observable.just(new ObservableCovarianceTest.Movie(), new ObservableCovarianceTest.ActionMovie(), new ObservableCovarianceTest.HorrorMovie());
        o2.toSortedList(sortFunction);
    }

    @Test
    public void testGroupByCompose() {
        Observable<ObservableCovarianceTest.Movie> movies = Observable.just(new ObservableCovarianceTest.HorrorMovie(), new ObservableCovarianceTest.ActionMovie(), new ObservableCovarianceTest.Movie());
        TestObserver<String> to = new TestObserver<String>();
        movies.groupBy(new Function<ObservableCovarianceTest.Movie, Object>() {
            @Override
            public Object apply(ObservableCovarianceTest.Movie v) {
                return v.getClass();
            }
        }).doOnNext(new Consumer<GroupedObservable<Object, ObservableCovarianceTest.Movie>>() {
            @Override
            public void accept(GroupedObservable<Object, ObservableCovarianceTest.Movie> g) {
                System.out.println(g.getKey());
            }
        }).flatMap(new Function<GroupedObservable<Object, ObservableCovarianceTest.Movie>, Observable<String>>() {
            @Override
            public Observable<String> apply(GroupedObservable<Object, io.reactivex.observable.Movie> g) {
                return g.doOnNext(new Consumer<io.reactivex.observable.Movie>() {
                    @Override
                    public void accept(io.reactivex.observable.Movie pv) {
                        System.out.println(pv);
                    }
                }).compose(new ObservableTransformer<io.reactivex.observable.Movie, io.reactivex.observable.Movie>() {
                    @Override
                    public Observable<io.reactivex.observable.Movie> apply(Observable<io.reactivex.observable.Movie> m) {
                        return m.concatWith(io.reactivex.Observable.just(new io.reactivex.observable.ActionMovie()));
                    }
                }).map(new Function<io.reactivex.observable.Movie, String>() {
                    @Override
                    public String apply(io.reactivex.observable.Movie v) {
                        return v.toString();
                    }
                });
            }
        }).subscribe(to);
        to.assertTerminated();
        to.assertNoErrors();
        // System.out.println(ts.getOnNextEvents());
        Assert.assertEquals(6, to.valueCount());
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose() {
        Observable<ObservableCovarianceTest.HorrorMovie> movie = Observable.just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.Movie> movie2 = movie.compose(new io.reactivex.ObservableTransformer<ObservableCovarianceTest.HorrorMovie, ObservableCovarianceTest.Movie>() {
            @Override
            public Observable<ObservableCovarianceTest.Movie> apply(Observable<ObservableCovarianceTest.HorrorMovie> t) {
                return Observable.just(new ObservableCovarianceTest.Movie());
            }
        });
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose2() {
        Observable<ObservableCovarianceTest.Movie> movie = Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.HorrorMovie> movie2 = movie.compose(new io.reactivex.ObservableTransformer<ObservableCovarianceTest.Movie, ObservableCovarianceTest.HorrorMovie>() {
            @Override
            public Observable<ObservableCovarianceTest.HorrorMovie> apply(Observable<ObservableCovarianceTest.Movie> t) {
                return Observable.just(new ObservableCovarianceTest.HorrorMovie());
            }
        });
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose3() {
        Observable<ObservableCovarianceTest.Movie> movie = Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.HorrorMovie> movie2 = movie.compose(new io.reactivex.ObservableTransformer<ObservableCovarianceTest.Movie, ObservableCovarianceTest.HorrorMovie>() {
            @Override
            public Observable<ObservableCovarianceTest.HorrorMovie> apply(Observable<ObservableCovarianceTest.Movie> t) {
                return Observable.just(new ObservableCovarianceTest.HorrorMovie()).map(new Function<ObservableCovarianceTest.HorrorMovie, ObservableCovarianceTest.HorrorMovie>() {
                    @Override
                    public ObservableCovarianceTest.HorrorMovie apply(ObservableCovarianceTest.HorrorMovie v) {
                        return v;
                    }
                });
            }
        });
    }

    @SuppressWarnings("unused")
    @Test
    public void testCovarianceOfCompose4() {
        Observable<ObservableCovarianceTest.HorrorMovie> movie = Observable.just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.HorrorMovie> movie2 = movie.compose(new io.reactivex.ObservableTransformer<ObservableCovarianceTest.HorrorMovie, ObservableCovarianceTest.HorrorMovie>() {
            @Override
            public Observable<ObservableCovarianceTest.HorrorMovie> apply(Observable<ObservableCovarianceTest.HorrorMovie> t1) {
                return t1.map(new Function<ObservableCovarianceTest.HorrorMovie, ObservableCovarianceTest.HorrorMovie>() {
                    @Override
                    public ObservableCovarianceTest.HorrorMovie apply(ObservableCovarianceTest.HorrorMovie v) {
                        return v;
                    }
                });
            }
        });
    }

    @Test
    public void testComposeWithDeltaLogic() {
        List<ObservableCovarianceTest.Movie> list1 = Arrays.asList(new ObservableCovarianceTest.Movie(), new ObservableCovarianceTest.HorrorMovie(), new ObservableCovarianceTest.ActionMovie());
        List<ObservableCovarianceTest.Movie> list2 = Arrays.asList(new ObservableCovarianceTest.ActionMovie(), new ObservableCovarianceTest.Movie(), new ObservableCovarianceTest.HorrorMovie(), new ObservableCovarianceTest.ActionMovie());
        Observable<List<ObservableCovarianceTest.Movie>> movies = just(list1, list2);
        movies.compose(ObservableCovarianceTest.deltaTransformer);
    }

    static Function<List<List<ObservableCovarianceTest.Movie>>, Observable<ObservableCovarianceTest.Movie>> calculateDelta = new Function<List<List<ObservableCovarianceTest.Movie>>, Observable<ObservableCovarianceTest.Movie>>() {
        @Override
        public Observable<io.reactivex.observable.Movie> apply(List<List<io.reactivex.observable.Movie>> listOfLists) {
            if ((listOfLists.size()) == 1) {
                return io.reactivex.Observable.fromIterable(listOfLists.get(0));
            } else {
                // diff the two
                List<io.reactivex.observable.Movie> newList = listOfLists.get(1);
                List<io.reactivex.observable.Movie> oldList = new ArrayList<io.reactivex.observable.Movie>(listOfLists.get(0));
                Set<io.reactivex.observable.Movie> delta = new LinkedHashSet<io.reactivex.observable.Movie>();
                delta.addAll(newList);
                // remove all that match in old
                delta.removeAll(oldList);
                // filter oldList to those that aren't in the newList
                oldList.removeAll(newList);
                // for all left in the oldList we'll create DROP events
                for (@SuppressWarnings("unused")
                io.reactivex.observable.Movie old : oldList) {
                    delta.add(new io.reactivex.observable.Movie());
                }
                return io.reactivex.Observable.fromIterable(delta);
            }
        }
    };

    static io.reactivex.ObservableTransformer<List<ObservableCovarianceTest.Movie>, ObservableCovarianceTest.Movie> deltaTransformer = new io.reactivex.ObservableTransformer<List<ObservableCovarianceTest.Movie>, ObservableCovarianceTest.Movie>() {
        @Override
        public Observable<ObservableCovarianceTest.Movie> apply(Observable<List<ObservableCovarianceTest.Movie>> movieList) {
            return movieList.startWith(new ArrayList<ObservableCovarianceTest.Movie>()).buffer(2, 1).skip(1).flatMap(calculateDelta);
        }
    };

    /* Most tests are moved into their applicable classes such as [Operator]Tests.java */
    static class Media {}

    static class Movie extends ObservableCovarianceTest.Media {}

    static class HorrorMovie extends ObservableCovarianceTest.Movie {}

    static class ActionMovie extends ObservableCovarianceTest.Movie {}

    static class Album extends ObservableCovarianceTest.Media {}

    static class TVSeason extends ObservableCovarianceTest.Media {}

    static class Rating {}

    static class CoolRating extends ObservableCovarianceTest.Rating {}

    static class Result {}

    static class ExtendedResult extends ObservableCovarianceTest.Result {}
}

