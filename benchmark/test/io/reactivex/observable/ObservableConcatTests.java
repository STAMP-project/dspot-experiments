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


import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.junit.Assert;
import org.junit.Test;


public class ObservableConcatTests {
    @Test
    public void testConcatSimple() {
        Observable<String> o1 = Observable.just("one", "two");
        Observable<String> o2 = Observable.just("three", "four");
        List<String> values = Observable.concat(o1, o2).toList().blockingGet();
        Assert.assertEquals("one", values.get(0));
        Assert.assertEquals("two", values.get(1));
        Assert.assertEquals("three", values.get(2));
        Assert.assertEquals("four", values.get(3));
    }

    @Test
    public void testConcatWithObservableOfObservable() {
        Observable<String> o1 = Observable.just("one", "two");
        Observable<String> o2 = Observable.just("three", "four");
        Observable<String> o3 = Observable.just("five", "six");
        Observable<Observable<String>> os = Observable.just(o1, o2, o3);
        List<String> values = Observable.concat(os).toList().blockingGet();
        Assert.assertEquals("one", values.get(0));
        Assert.assertEquals("two", values.get(1));
        Assert.assertEquals("three", values.get(2));
        Assert.assertEquals("four", values.get(3));
        Assert.assertEquals("five", values.get(4));
        Assert.assertEquals("six", values.get(5));
    }

    @Test
    public void testConcatWithIterableOfObservable() {
        Observable<String> o1 = Observable.just("one", "two");
        Observable<String> o2 = Observable.just("three", "four");
        Observable<String> o3 = Observable.just("five", "six");
        @SuppressWarnings("unchecked")
        Iterable<Observable<String>> is = Arrays.asList(o1, o2, o3);
        List<String> values = Observable.concat(Observable.fromIterable(is)).toList().blockingGet();
        Assert.assertEquals("one", values.get(0));
        Assert.assertEquals("two", values.get(1));
        Assert.assertEquals("three", values.get(2));
        Assert.assertEquals("four", values.get(3));
        Assert.assertEquals("five", values.get(4));
        Assert.assertEquals("six", values.get(5));
    }

    @Test
    public void testConcatCovariance() {
        ObservableCovarianceTest.HorrorMovie horrorMovie1 = new ObservableCovarianceTest.HorrorMovie();
        ObservableCovarianceTest.Movie movie = new ObservableCovarianceTest.Movie();
        ObservableCovarianceTest.Media media = new ObservableCovarianceTest.Media();
        ObservableCovarianceTest.HorrorMovie horrorMovie2 = new ObservableCovarianceTest.HorrorMovie();
        Observable<ObservableCovarianceTest.Media> o1 = Observable.<ObservableCovarianceTest.Media>just(horrorMovie1, movie);
        Observable<ObservableCovarianceTest.Media> o2 = just(media, horrorMovie2);
        Observable<Observable<ObservableCovarianceTest.Media>> os = Observable.just(o1, o2);
        List<ObservableCovarianceTest.Media> values = Observable.concat(os).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media, values.get(2));
        Assert.assertEquals(horrorMovie2, values.get(3));
        Assert.assertEquals(4, values.size());
    }

    @Test
    public void testConcatCovariance2() {
        ObservableCovarianceTest.HorrorMovie horrorMovie1 = new ObservableCovarianceTest.HorrorMovie();
        ObservableCovarianceTest.Movie movie = new ObservableCovarianceTest.Movie();
        ObservableCovarianceTest.Media media1 = new ObservableCovarianceTest.Media();
        ObservableCovarianceTest.Media media2 = new ObservableCovarianceTest.Media();
        ObservableCovarianceTest.HorrorMovie horrorMovie2 = new ObservableCovarianceTest.HorrorMovie();
        Observable<ObservableCovarianceTest.Media> o1 = Observable.just(horrorMovie1, movie, media1);
        Observable<ObservableCovarianceTest.Media> o2 = just(media2, horrorMovie2);
        Observable<Observable<ObservableCovarianceTest.Media>> os = Observable.just(o1, o2);
        List<ObservableCovarianceTest.Media> values = Observable.concat(os).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media1, values.get(2));
        Assert.assertEquals(media2, values.get(3));
        Assert.assertEquals(horrorMovie2, values.get(4));
        Assert.assertEquals(5, values.size());
    }

    @Test
    public void testConcatCovariance3() {
        ObservableCovarianceTest.HorrorMovie horrorMovie1 = new ObservableCovarianceTest.HorrorMovie();
        ObservableCovarianceTest.Movie movie = new ObservableCovarianceTest.Movie();
        ObservableCovarianceTest.Media media = new ObservableCovarianceTest.Media();
        ObservableCovarianceTest.HorrorMovie horrorMovie2 = new ObservableCovarianceTest.HorrorMovie();
        Observable<ObservableCovarianceTest.Movie> o1 = Observable.just(horrorMovie1, movie);
        Observable<ObservableCovarianceTest.Media> o2 = just(media, horrorMovie2);
        List<ObservableCovarianceTest.Media> values = Observable.concat(o1, o2).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media, values.get(2));
        Assert.assertEquals(horrorMovie2, values.get(3));
        Assert.assertEquals(4, values.size());
    }

    @Test
    public void testConcatCovariance4() {
        final ObservableCovarianceTest.HorrorMovie horrorMovie1 = new ObservableCovarianceTest.HorrorMovie();
        final ObservableCovarianceTest.Movie movie = new ObservableCovarianceTest.Movie();
        ObservableCovarianceTest.Media media = new ObservableCovarianceTest.Media();
        ObservableCovarianceTest.HorrorMovie horrorMovie2 = new ObservableCovarianceTest.HorrorMovie();
        Observable<ObservableCovarianceTest.Movie> o1 = unsafeCreate(new io.reactivex.ObservableSource<ObservableCovarianceTest.Movie>() {
            @Override
            public void subscribe(Observer<? super ObservableCovarianceTest.Movie> o) {
                o.onNext(horrorMovie1);
                o.onNext(movie);
                // o.onNext(new Media()); // correctly doesn't compile
                o.onComplete();
            }
        });
        Observable<ObservableCovarianceTest.Media> o2 = just(media, horrorMovie2);
        List<ObservableCovarianceTest.Media> values = Observable.concat(o1, o2).toList().blockingGet();
        Assert.assertEquals(horrorMovie1, values.get(0));
        Assert.assertEquals(movie, values.get(1));
        Assert.assertEquals(media, values.get(2));
        Assert.assertEquals(horrorMovie2, values.get(3));
        Assert.assertEquals(4, values.size());
    }
}

