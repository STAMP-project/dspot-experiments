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


import org.junit.Assert;
import org.junit.Test;


public class FlowableReduceTests {
    @Test
    public void reduceIntsFlowable() {
        Flowable<Integer> f = Flowable.just(1, 2, 3);
        int value = f.reduce(new io.reactivex.functions.BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).toFlowable().blockingSingle();
        Assert.assertEquals(6, value);
    }

    @SuppressWarnings("unused")
    @Test
    public void reduceWithObjectsFlowable() {
        Flowable<FlowableCovarianceTest.Movie> horrorMovies = Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.Movie> reduceResult = horrorMovies.scan(new io.reactivex.functions.BiFunction<FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie>() {
            @Override
            public FlowableCovarianceTest.Movie apply(FlowableCovarianceTest.Movie t1, FlowableCovarianceTest.Movie t2) {
                return t2;
            }
        }).takeLast(1);
        Flowable<FlowableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie>() {
            @Override
            public FlowableCovarianceTest.Movie apply(FlowableCovarianceTest.Movie t1, FlowableCovarianceTest.Movie t2) {
                return t2;
            }
        }).toFlowable();
        Assert.assertNotNull(reduceResult2);
    }

    /**
     * Reduce consumes and produces T so can't do covariance.
     *
     * https://github.com/ReactiveX/RxJava/issues/360#issuecomment-24203016
     */
    @Test
    public void reduceWithCovariantObjectsFlowable() {
        Flowable<FlowableCovarianceTest.Movie> horrorMovies = Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie>() {
            @Override
            public FlowableCovarianceTest.Movie apply(FlowableCovarianceTest.Movie t1, FlowableCovarianceTest.Movie t2) {
                return t2;
            }
        }).toFlowable();
        Assert.assertNotNull(reduceResult2);
    }

    @Test
    public void reduceInts() {
        Flowable<Integer> f = Flowable.just(1, 2, 3);
        int value = f.reduce(new io.reactivex.functions.BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).toFlowable().blockingSingle();
        Assert.assertEquals(6, value);
    }

    @SuppressWarnings("unused")
    @Test
    public void reduceWithObjects() {
        Flowable<FlowableCovarianceTest.Movie> horrorMovies = Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.Movie> reduceResult = horrorMovies.scan(new io.reactivex.functions.BiFunction<FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie>() {
            @Override
            public FlowableCovarianceTest.Movie apply(FlowableCovarianceTest.Movie t1, FlowableCovarianceTest.Movie t2) {
                return t2;
            }
        }).takeLast(1);
        Maybe<FlowableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie>() {
            @Override
            public FlowableCovarianceTest.Movie apply(FlowableCovarianceTest.Movie t1, FlowableCovarianceTest.Movie t2) {
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
        Flowable<FlowableCovarianceTest.Movie> horrorMovies = Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        Maybe<FlowableCovarianceTest.Movie> reduceResult2 = horrorMovies.reduce(new io.reactivex.functions.BiFunction<FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie, FlowableCovarianceTest.Movie>() {
            @Override
            public FlowableCovarianceTest.Movie apply(FlowableCovarianceTest.Movie t1, FlowableCovarianceTest.Movie t2) {
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
        Flowable<FlowableCovarianceTest.Movie> horrorMovies = Flowable.<FlowableCovarianceTest.Movie>just(new FlowableCovarianceTest.HorrorMovie());
        libraryFunctionActingOnMovieObservables(horrorMovies);
    }
}

