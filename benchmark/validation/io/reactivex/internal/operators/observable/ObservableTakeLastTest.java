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
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableTakeLastTest {
    @Test
    public void testTakeLastEmpty() {
        Observable<String> w = Observable.empty();
        Observable<String> take = w.takeLast(2);
        Observer<String> observer = mockObserver();
        take.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeLast1() {
        Observable<String> w = Observable.just("one", "two", "three");
        Observable<String> take = w.takeLast(2);
        Observer<String> observer = mockObserver();
        InOrder inOrder = Mockito.inOrder(observer);
        take.subscribe(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onNext("one");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeLast2() {
        Observable<String> w = Observable.just("one");
        Observable<String> take = w.takeLast(10);
        Observer<String> observer = mockObserver();
        take.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeLastWithZeroCount() {
        Observable<String> w = Observable.just("one");
        Observable<String> take = w.takeLast(0);
        Observer<String> observer = mockObserver();
        take.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext("one");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testTakeLastWithNegativeCount() {
        Observable.just("one").takeLast((-1));
    }

    @Test
    public void testBackpressure1() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(1, 100000).takeLast(1).observeOn(Schedulers.newThread()).map(newSlowProcessor()).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        to.assertValue(100000);
    }

    @Test
    public void testBackpressure2() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(1, 100000).takeLast(((Flowable.bufferSize()) * 4)).observeOn(Schedulers.newThread()).map(newSlowProcessor()).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 4), to.valueCount());
    }

    @Test
    public void testIssue1522() {
        // https://github.com/ReactiveX/RxJava/issues/1522
        Assert.assertNull(Observable.empty().count().filter(new Predicate<Long>() {
            @Override
            public boolean test(Long v) {
                return false;
            }
        }).blockingGet());
    }

    @Test
    public void testUnsubscribeTakesEffectEarlyOnFastPath() {
        final AtomicInteger count = new AtomicInteger();
        Observable.range(0, 100000).takeLast(100000).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {
                count.incrementAndGet();
                cancel();
            }
        });
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.range(1, 10).takeLast(5));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.takeLast(5);
            }
        });
    }

    @Test
    public void error() {
        Observable.error(new TestException()).takeLast(5).test().assertFailure(TestException.class);
    }

    @Test
    public void takeLastTake() {
        Observable.range(1, 10).takeLast(5).take(2).test().assertResult(6, 7);
    }
}

