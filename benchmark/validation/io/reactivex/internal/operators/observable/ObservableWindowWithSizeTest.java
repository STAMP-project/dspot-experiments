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
package io.reactivex.internal.operators.observable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ObservableWindowWithSizeTest {
    @Test
    public void testNonOverlappingWindows() {
        Observable<String> subject = Observable.just("one", "two", "three", "four", "five");
        Observable<Observable<String>> windowed = subject.window(3);
        List<List<String>> windows = toLists(windowed);
        Assert.assertEquals(2, windows.size());
        Assert.assertEquals(list("one", "two", "three"), windows.get(0));
        Assert.assertEquals(list("four", "five"), windows.get(1));
    }

    @Test
    public void testSkipAndCountGaplessWindows() {
        Observable<String> subject = Observable.just("one", "two", "three", "four", "five");
        Observable<Observable<String>> windowed = subject.window(3, 3);
        List<List<String>> windows = toLists(windowed);
        Assert.assertEquals(2, windows.size());
        Assert.assertEquals(list("one", "two", "three"), windows.get(0));
        Assert.assertEquals(list("four", "five"), windows.get(1));
    }

    @Test
    public void testOverlappingWindows() {
        Observable<String> subject = fromArray(new String[]{ "zero", "one", "two", "three", "four", "five" });
        Observable<Observable<String>> windowed = subject.window(3, 1);
        List<List<String>> windows = toLists(windowed);
        Assert.assertEquals(6, windows.size());
        Assert.assertEquals(list("zero", "one", "two"), windows.get(0));
        Assert.assertEquals(list("one", "two", "three"), windows.get(1));
        Assert.assertEquals(list("two", "three", "four"), windows.get(2));
        Assert.assertEquals(list("three", "four", "five"), windows.get(3));
        Assert.assertEquals(list("four", "five"), windows.get(4));
        Assert.assertEquals(list("five"), windows.get(5));
    }

    @Test
    public void testSkipAndCountWindowsWithGaps() {
        Observable<String> subject = Observable.just("one", "two", "three", "four", "five");
        Observable<Observable<String>> windowed = subject.window(2, 3);
        List<List<String>> windows = toLists(windowed);
        Assert.assertEquals(2, windows.size());
        Assert.assertEquals(list("one", "two"), windows.get(0));
        Assert.assertEquals(list("four", "five"), windows.get(1));
    }

    @Test
    public void testWindowUnsubscribeNonOverlapping() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        final AtomicInteger count = new AtomicInteger();
        Observable.merge(window(5).take(2)).subscribe(to);
        to.awaitTerminalEvent(500, TimeUnit.MILLISECONDS);
        to.assertTerminated();
        to.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // System.out.println(ts.getOnNextEvents());
        Assert.assertEquals(10, count.get());
    }

    @Test
    public void testWindowUnsubscribeNonOverlappingAsyncSource() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        final AtomicInteger count = new AtomicInteger();
        Observable.merge(window(5).take(2)).subscribe(to);
        to.awaitTerminalEvent(500, TimeUnit.MILLISECONDS);
        to.assertTerminated();
        to.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // make sure we don't emit all values ... the unsubscribe should propagate
        Assert.assertTrue(((count.get()) < 100000));
    }

    @Test
    public void testWindowUnsubscribeOverlapping() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        final AtomicInteger count = new AtomicInteger();
        Observable.merge(range(1, 10000).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                count.incrementAndGet();
            }
        }).window(5, 4).take(2)).subscribe(to);
        to.awaitTerminalEvent(500, TimeUnit.MILLISECONDS);
        to.assertTerminated();
        // System.out.println(ts.getOnNextEvents());
        to.assertValues(1, 2, 3, 4, 5, 5, 6, 7, 8, 9);
        Assert.assertEquals(9, count.get());
    }

    @Test
    public void testWindowUnsubscribeOverlappingAsyncSource() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        final AtomicInteger count = new AtomicInteger();
        Observable.merge(range(1, 100000).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                count.incrementAndGet();
            }
        }).observeOn(Schedulers.computation()).window(5, 4).take(2), 128).subscribe(to);
        to.awaitTerminalEvent(500, TimeUnit.MILLISECONDS);
        to.assertTerminated();
        to.assertValues(1, 2, 3, 4, 5, 5, 6, 7, 8, 9);
        // make sure we don't emit all values ... the unsubscribe should propagate
        // assertTrue(count.get() < 100000); // disabled: a small hiccup in the consumption may allow the source to run to completion
    }

    @Test
    public void testTakeFlatMapCompletes() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        final int indicator = 999999999;
        window(10).take(2).flatMap(new Function<Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Observable<Integer> w) {
                return w.startWith(indicator);
            }
        }).subscribe(to);
        to.awaitTerminalEvent(2, TimeUnit.SECONDS);
        to.assertComplete();
        to.assertValueCount(22);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(window(1));
        TestHelper.checkDisposed(PublishSubject.create().window(2, 1));
        TestHelper.checkDisposed(PublishSubject.create().window(1, 2));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Observable<Object>>>() {
            @Override
            public ObservableSource<Observable<Object>> apply(Observable<Object> o) throws Exception {
                return o.window(1);
            }
        });
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Observable<Object>>>() {
            @Override
            public ObservableSource<Observable<Object>> apply(Observable<Object> o) throws Exception {
                return o.window(2, 1);
            }
        });
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Observable<Object>>>() {
            @Override
            public ObservableSource<Observable<Object>> apply(Observable<Object> o) throws Exception {
                return o.window(1, 2);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorExact() {
        window(1).test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorSkip() {
        error(new TestException()).window(1, 2).test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorOverlap() {
        error(new TestException()).window(2, 1).test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorExactInner() {
        @SuppressWarnings("rawtypes")
        final TestObserver[] to = new TestObserver[]{ null };
        window(2).doOnNext(new Consumer<Observable<Integer>>() {
            @Override
            public void accept(Observable<Integer> w) throws Exception {
                to[0] = w.test();
            }
        }).test().assertError(TestException.class);
        to[0].assertFailure(TestException.class, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorSkipInner() {
        @SuppressWarnings("rawtypes")
        final TestObserver[] to = new TestObserver[]{ null };
        just(1).concatWith(<Integer>error(new TestException())).window(2, 3).doOnNext(new Consumer<Observable<Integer>>() {
            @Override
            public void accept(Observable<Integer> w) throws Exception {
                to[0] = w.test();
            }
        }).test().assertError(TestException.class);
        to[0].assertFailure(TestException.class, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorOverlapInner() {
        @SuppressWarnings("rawtypes")
        final TestObserver[] to = new TestObserver[]{ null };
        just(1).concatWith(<Integer>error(new TestException())).window(3, 2).doOnNext(new Consumer<Observable<Integer>>() {
            @Override
            public void accept(Observable<Integer> w) throws Exception {
                to[0] = w.test();
            }
        }).test().assertError(TestException.class);
        to[0].assertFailure(TestException.class, 1);
    }
}

