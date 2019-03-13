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


import org.junit.Assert;
import org.junit.Test;


public class ObservableReduceTests {
    @Test
    public void reduceIntsObservable() {
        Observable<Integer> o = Observable.just(1, 2, 3);
        int value = o.reduce(new io.reactivex.functions.BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).toObservable().blockingSingle();
        Assert.assertEquals(6, value);
    }

    @SuppressWarnings("unused")
    @Test
    public void reduceWithObjectsObservable() {
        Observable<ObservableCovarianceTest.Movie> horrorMovies = Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.Movie> reduceResult = horrorMovies.scan(new io.reactivex.functions.BiFunction<ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie>() {
            @Override
            public ObservableCovarianceTest.Movie apply(ObservableCovarianceTest.Movie t1, ObservableCovarianceTest.Movie t2) {
                return t2;
            }
        }).takeLast(1);
        Observable<ObservableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie>() {
            @Override
            public ObservableCovarianceTest.Movie apply(ObservableCovarianceTest.Movie t1, ObservableCovarianceTest.Movie t2) {
                return t2;
            }
        }).toObservable();
        Assert.assertNotNull(reduceResult2);
    }

    /**
     * Reduce consumes and produces T so can't do covariance.
     *
     * https://github.com/ReactiveX/RxJava/issues/360#issuecomment-24203016
     */
    @Test
    public void reduceWithCovariantObjectsObservable() {
        Observable<ObservableCovarianceTest.Movie> horrorMovies = Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie>() {
            @Override
            public ObservableCovarianceTest.Movie apply(ObservableCovarianceTest.Movie t1, ObservableCovarianceTest.Movie t2) {
                return t2;
            }
        }).toObservable();
        Assert.assertNotNull(reduceResult2);
    }

    @Test
    public void reduceInts() {
        Observable<Integer> o = Observable.just(1, 2, 3);
        int value = o.reduce(new io.reactivex.functions.BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).blockingGet();
        Assert.assertEquals(6, value);
    }

    @SuppressWarnings("unused")
    @Test
    public void reduceWithObjects() {
        Observable<ObservableCovarianceTest.Movie> horrorMovies = Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.Movie> reduceResult = horrorMovies.scan(new io.reactivex.functions.BiFunction<ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie>() {
            @Override
            public ObservableCovarianceTest.Movie apply(ObservableCovarianceTest.Movie t1, ObservableCovarianceTest.Movie t2) {
                return t2;
            }
        }).takeLast(1);
        Maybe<ObservableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie>() {
            @Override
            public ObservableCovarianceTest.Movie apply(ObservableCovarianceTest.Movie t1, ObservableCovarianceTest.Movie t2) {
                return t2;
            }
        });
        Assert.assertNotNull(reduceResult2);
    }

    /**
     * Reduce consumes and produces T so can't do covariance.
     *
     * https://github.com/ReactiveX/RxJava/issues/360#issuecomment-24203016
     */
    @Test
    public void reduceWithCovariantObjects() {
        Observable<ObservableCovarianceTest.Movie> horrorMovies = Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        Maybe<ObservableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie, ObservableCovarianceTest.Movie>() {
            @Override
            public ObservableCovarianceTest.Movie apply(ObservableCovarianceTest.Movie t1, ObservableCovarianceTest.Movie t2) {
                return t2;
            }
        });
        Assert.assertNotNull(reduceResult2);
    }

    /**
     * Reduce consumes and produces T so can't do covariance.
     *
     * https://github.com/ReactiveX/RxJava/issues/360#issuecomment-24203016
     */
    @Test
    public void reduceCovariance() {
        // must type it to <Movie>
        Observable<ObservableCovarianceTest.Movie> horrorMovies = Observable.<ObservableCovarianceTest.Movie>just(new ObservableCovarianceTest.HorrorMovie());
        libraryFunctionActingOnMovieObservables(horrorMovies);
    }
}

