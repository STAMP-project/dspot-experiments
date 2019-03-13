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
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FlowableConcatTests {
    @Test
    public void testConcatSimple() {
        Flowable<String> f1 = Flowable.just("one", "two");
        Flowable<String> f2 = Flowable.just("three", "four");
        List<String> values = Flowable.concat(f1, f2).toList().blockingGet();
        Assert.assertEquals("one", values.get(0));
        Assert.assertEquals("two", values.get(1));
        Assert.assertEquals("three", values.get(2));
        Assert.assertEquals("four", values.get(3));
    }

    @Test
    public void testConcatWithFlowableOfFlowable() {
        Flowable<String> f1 = Flowable.just("one", "two");
        Flowable<String> f2 = Flowable.just("three", "four");
        Flowable<String> f3 = Flowable.just("five", "six");
        Flowable<Flowable<String>> os = Flowable.just(f1, f2, f3);
        List<String> values = Flowable.concat(os).toList().blockingGet();
        Assert.assertEquals("one", values.get(0));
        Assert.assertEquals("two", values.get(1));
        Assert.assertEquals("three", values.get(2));
        Assert.assertEquals("four", values.get(3));
        Assert.assertEquals("five", values.get(4));
        Assert.assertEquals("six", values.get(5));
    }

    @Test
    public void testConcatWithIterableOfFlowable() {
        Flowable<String> f1 = Flowable.just("one", "two");
        Flowable<String> f2 = Flowable.just("three", "four");
        Flowable<String> f3 = Flowable.just("five", "six");
        @SuppressWarnings("unchecked")
        Iterable<Flowable<String>> is = Arrays.asList(f1, f2, f3);
        List<String> values = Flowable.concat(Flowable.fromIterable(is)).toList().blockingGet();
        Assert.assertEquals("one", values.get(0));
        Assert.assertEquals("two", values.get(1));
        Assert.assertEquals("three", values.get(2));
        Assert.assertEquals("four", values.get(3));
        Assert.assertEquals("five", values.get(4));
        Assert.assertEquals("six", values.get(5));
    }

    @Test
    public void testConcatCovariance() {
        FlowableCovarianceTest.HorrorMovie horrorMovie1 = new FlowableCovarianceTest.HorrorMovie();
        FlowableCovarianceTest.Movie movie = new FlowableCovarianceTest.Movie();
        FlowableCovarianceTest.Media media = new FlowableCovarianceTest.Media();
        FlowableCovarianceTest.HorrorMovie horrorMovie2 = new FlowableCovarianceTest.HorrorMovie();
        Flowable<FlowableCovarianceTest.Media> f1 = Flowable.<FlowableCovarianceTest.Media>just(horrorMovie1, movie);
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(media, horrorMovie2);
        Flowable<Flowable<FlowableCovarianceTest.Media>> os = Flowable.just(f1, f2);
        List<FlowableCovarianceTest.Media> values = Flowable.concat(os).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media, values.get(2));
        Assert.assertEquals(horrorMovie2, values.get(3));
        Assert.assertEquals(4, values.size());
    }

    @Test
    public void testConcatCovariance2() {
        FlowableCovarianceTest.HorrorMovie horrorMovie1 = new FlowableCovarianceTest.HorrorMovie();
        FlowableCovarianceTest.Movie movie = new FlowableCovarianceTest.Movie();
        FlowableCovarianceTest.Media media1 = new FlowableCovarianceTest.Media();
        FlowableCovarianceTest.Media media2 = new FlowableCovarianceTest.Media();
        FlowableCovarianceTest.HorrorMovie horrorMovie2 = new FlowableCovarianceTest.HorrorMovie();
        Flowable<FlowableCovarianceTest.Media> f1 = Flowable.just(horrorMovie1, movie, media1);
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(media2, horrorMovie2);
        Flowable<Flowable<FlowableCovarianceTest.Media>> os = Flowable.just(f1, f2);
        List<FlowableCovarianceTest.Media> values = Flowable.concat(os).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media1, values.get(2));
        Assert.assertEquals(media2, values.get(3));
        Assert.assertEquals(horrorMovie2, values.get(4));
        Assert.assertEquals(5, values.size());
    }

    @Test
    public void testConcatCovariance3() {
        FlowableCovarianceTest.HorrorMovie horrorMovie1 = new FlowableCovarianceTest.HorrorMovie();
        FlowableCovarianceTest.Movie movie = new FlowableCovarianceTest.Movie();
        FlowableCovarianceTest.Media media = new FlowableCovarianceTest.Media();
        FlowableCovarianceTest.HorrorMovie horrorMovie2 = new FlowableCovarianceTest.HorrorMovie();
        Flowable<FlowableCovarianceTest.Movie> f1 = Flowable.just(horrorMovie1, movie);
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(media, horrorMovie2);
        List<FlowableCovarianceTest.Media> values = Flowable.concat(f1, f2).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media, values.get(2));
        Assert.assertEquals(horrorMovie2, values.get(3));
        Assert.assertEquals(4, values.size());
    }

    @Test
    public void testConcatCovariance4() {
        final FlowableCovarianceTest.HorrorMovie horrorMovie1 = new FlowableCovarianceTest.HorrorMovie();
        final FlowableCovarianceTest.Movie movie = new FlowableCovarianceTest.Movie();
        FlowableCovarianceTest.Media media = new FlowableCovarianceTest.Media();
        FlowableCovarianceTest.HorrorMovie horrorMovie2 = new FlowableCovarianceTest.HorrorMovie();
        Flowable<FlowableCovarianceTest.Movie> f1 = Flowable.unsafeCreate(new Publisher<FlowableCovarianceTest.Movie>() {
            @Override
            public void subscribe(Subscriber<? super FlowableCovarianceTest.Movie> subscriber) {
                subscriber.onNext(horrorMovie1);
                subscriber.onNext(movie);
                // o.onNext(new Media()); // correctly doesn't compile
                subscriber.onComplete();
            }
        });
        Flowable<FlowableCovarianceTest.Media> f2 = Flowable.just(media, horrorMovie2);
        List<FlowableCovarianceTest.Media> values = Flowable.concat(f1, f2).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media, values.get(2));
        Assert.assertEquals(horrorMovie2, values.get(3));
        Assert.assertEquals(4, values.size());
    }
}

