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


import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


public class ObservableMergeTests {
    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfMerge() {
        Observable<ObservableCovarianceTest.HorrorMovie> horrors = Observable.just(new ObservableCovarianceTest.HorrorMovie());
        Observable<Observable<ObservableCovarianceTest.HorrorMovie>> metaHorrors = Observable.just(horrors);
        Observable.<ObservableCovarianceTest.Media>merge(metaHorrors);
    }

    @Test
    public void testMergeCovariance() {
        Observable<ObservableCovarianceTest.Media> o1 = Observable.<ObservableCovarianceTest.Media>just(new ObservableCovarianceTest.HorrorMovie(), new ObservableCovarianceTest.Movie());
        Observable<ObservableCovarianceTest.Media> o2 = Observable.just(new ObservableCovarianceTest.Media(), new ObservableCovarianceTest.HorrorMovie());
        Observable<Observable<ObservableCovarianceTest.Media>> os = Observable.just(o1, o2);
        List<ObservableCovarianceTest.Media> values = Observable.merge(os).toList().blockingGet();
        Assert.assertEquals(4, values.size());
    }

    @Test
    public void testMergeCovariance2() {
        Observable<ObservableCovarianceTest.Media> o1 = Observable.just(new ObservableCovarianceTest.HorrorMovie(), new ObservableCovarianceTest.Movie(), new ObservableCovarianceTest.Media());
        Observable<ObservableCovarianceTest.Media> o2 = Observable.just(new ObservableCovarianceTest.Media(), new ObservableCovarianceTest.HorrorMovie());
        Observable<Observable<ObservableCovarianceTest.Media>> os = Observable.just(o1, o2);
        List<ObservableCovarianceTest.Media> values = Observable.merge(os).toList().blockingGet();
        Assert.assertEquals(5, values.size());
    }

    @Test
    public void testMergeCovariance3() {
        Observable<ObservableCovarianceTest.Movie> o1 = Observable.just(new ObservableCovarianceTest.HorrorMovie(), new ObservableCovarianceTest.Movie());
        Observable<ObservableCovarianceTest.Media> o2 = Observable.just(new ObservableCovarianceTest.Media(), new ObservableCovarianceTest.HorrorMovie());
        List<ObservableCovarianceTest.Media> values = Observable.merge(o1, o2).toList().blockingGet();
        Assert.assertTrue(((values.get(0)) instanceof ObservableCovarianceTest.HorrorMovie));
        Assert.assertTrue(((values.get(1)) instanceof ObservableCovarianceTest.Movie));
        Assert.assertTrue(((values.get(2)) != null));
        Assert.assertTrue(((values.get(3)) instanceof ObservableCovarianceTest.HorrorMovie));
    }

    @Test
    public void testMergeCovariance4() {
        Observable<ObservableCovarianceTest.Movie> o1 = Observable.defer(new Callable<Observable<ObservableCovarianceTest.Movie>>() {
            @Override
            public Observable<ObservableCovarianceTest.Movie> call() {
                return Observable.just(new ObservableCovarianceTest.HorrorMovie(), new ObservableCovarianceTest.Movie());
            }
        });
        Observable<ObservableCovarianceTest.Media> o2 = Observable.just(new ObservableCovarianceTest.Media(), new ObservableCovarianceTest.HorrorMovie());
        List<ObservableCovarianceTest.Media> values = Observable.merge(o1, o2).toList().blockingGet();
        Assert.assertTrue(((values.get(0)) instanceof ObservableCovarianceTest.HorrorMovie));
        Assert.assertTrue(((values.get(1)) instanceof ObservableCovarianceTest.Movie));
        Assert.assertTrue(((values.get(2)) != null));
        Assert.assertTrue(((values.get(3)) instanceof ObservableCovarianceTest.HorrorMovie));
    }
}

