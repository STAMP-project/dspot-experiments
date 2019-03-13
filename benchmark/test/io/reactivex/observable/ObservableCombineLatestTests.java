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


import io.reactivex.Observable;
import org.junit.Test;


public class ObservableCombineLatestTests {
    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfCombineLatest() {
        Observable<ObservableCovarianceTest.HorrorMovie> horrors = Observable.just(new ObservableCovarianceTest.HorrorMovie());
        Observable<ObservableCovarianceTest.CoolRating> ratings = Observable.just(new ObservableCovarianceTest.CoolRating());
        Observable.<ObservableCovarianceTest.Movie, ObservableCovarianceTest.CoolRating, ObservableCovarianceTest.Result>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Movie, ObservableCovarianceTest.CoolRating, ObservableCovarianceTest.Result>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.ExtendedResult>combineLatest(horrors, ratings, combine).blockingForEach(extendedAction);
        Observable.<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.Result>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Media, ObservableCovarianceTest.Rating, ObservableCovarianceTest.ExtendedResult>combineLatest(horrors, ratings, combine).blockingForEach(action);
        Observable.<ObservableCovarianceTest.Movie, ObservableCovarianceTest.CoolRating, ObservableCovarianceTest.Result>combineLatest(horrors, ratings, combine);
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
}

