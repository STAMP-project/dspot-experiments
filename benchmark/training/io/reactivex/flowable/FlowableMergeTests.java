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
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class FlowableMergeTests {
    /**
     * This won't compile if super/extends isn't done correctly on generics.
     */
    @Test
    public void testCovarianceOfMerge() {
        Flowable<FlowableCovarianceTest.HorrorMovie> horrors = Flowable.just(new FlowableCovarianceTest.HorrorMovie());
        Flowable<Flowable<FlowableCovarianceTest.HorrorMovie>> metaHorrors = Flowable.just(horrors);
        Flowable.<FlowableCovarianceTest.Media>merge(metaHorrors);
    }

    @Test
    public void testMergeCovariance() {
        Flowable<FlowableCovarianceTest.Media> f1 = Flowable.<FlowableCovarianceTest.Media>just(new FlowableCovarianceTest.HorrorMovie(), new FlowableCovarianceTest.Movie());
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(new FlowableCovarianceTest.Media(), new FlowableCovarianceTest.HorrorMovie());
        Flowable<Flowable<FlowableCovarianceTest.Media>> os = Flowable.just(f1, f2);
        List<FlowableCovarianceTest.Media> values = Flowable.merge(os).toList().blockingGet();
        Assert.assertEquals(4, values.size());
    }

    @Test
    public void testMergeCovariance2() {
        Flowable<FlowableCovarianceTest.Media> f1 = Flowable.just(new FlowableCovarianceTest.HorrorMovie(), new FlowableCovarianceTest.Movie(), new FlowableCovarianceTest.Media());
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(new FlowableCovarianceTest.Media(), new FlowableCovarianceTest.HorrorMovie());
        Flowable<Flowable<FlowableCovarianceTest.Media>> os = Flowable.just(f1, f2);
        List<FlowableCovarianceTest.Media> values = Flowable.merge(os).toList().blockingGet();
        Assert.assertEquals(5, values.size());
    }

    @Test
    public void testMergeCovariance3() {
        Flowable<FlowableCovarianceTest.Movie> f1 = Flowable.just(new FlowableCovarianceTest.HorrorMovie(), new FlowableCovarianceTest.Movie());
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(new FlowableCovarianceTest.Media(), new FlowableCovarianceTest.HorrorMovie());
        List<FlowableCovarianceTest.Media> values = Flowable.merge(f1, f2).toList().blockingGet();
        Assert.assertTrue(((values.get(0)) instanceof FlowableCovarianceTest.HorrorMovie));
        Assert.assertTrue(((values.get(1)) instanceof FlowableCovarianceTest.Movie));
        Assert.assertTrue(((values.get(2)) != null));
        Assert.assertTrue(((values.get(3)) instanceof FlowableCovarianceTest.HorrorMovie));
    }

    @Test
    public void testMergeCovariance4() {
        Flowable<FlowableCovarianceTest.Movie> f1 = Flowable.defer(new Callable<Publisher<FlowableCovarianceTest.Movie>>() {
            @Override
            public Publisher<FlowableCovarianceTest.Movie> call() {
                return Flowable.just(new FlowableCovarianceTest.HorrorMovie(), new FlowableCovarianceTest.Movie());
            }
        });
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(new FlowableCovarianceTest.Media(), new FlowableCovarianceTest.HorrorMovie());
        List<FlowableCovarianceTest.Media> values = Flowable.merge(f1, f2).toList().blockingGet();
        Assert.assertTrue(((values.get(0)) instanceof FlowableCovarianceTest.HorrorMovie));
        Assert.assertTrue(((values.get(1)) instanceof FlowableCovarianceTest.Movie));
        Assert.assertTrue(((values.get(2)) != null));
        Assert.assertTrue(((values.get(3)) instanceof FlowableCovarianceTest.HorrorMovie));
    }
}

