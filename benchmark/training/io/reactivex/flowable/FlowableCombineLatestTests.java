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
import org.junit.Test;


public class FlowableCombineLatestTests {
    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfCombineLatest() {
        Flowable<FlowableCovarianceTest.HorrorMovie> horrors = Flowable.just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<FlowableCovarianceTest.CoolRating> ratings = Flowable.just(new FlowableCovarianceTest.CoolRating());
        Flowable.<FlowableCovarianceTest.Movie, FlowableCovarianceTest.CoolRating, FlowableCovarianceTest.Result>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Movie, FlowableCovarianceTest.CoolRating, FlowableCovarianceTest.Result>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.ExtendedResult>combineLatest(horrors, ratings, combine).blockingForEach(extendedAction);
        Flowable.<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.Result>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Media, FlowableCovarianceTest.Rating, FlowableCovarianceTest.ExtendedResult>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Flowable.<FlowableCovarianceTest.Movie, FlowableCovarianceTest.CoolRating, FlowableCovarianceTest.Result>combineLatest(horrors, ratings, combine);
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
}

