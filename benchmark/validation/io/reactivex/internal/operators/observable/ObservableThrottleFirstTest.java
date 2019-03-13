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


import Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableThrottleFirstTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    private Observer<String> observer;

    @Test
    public void testThrottlingWithCompleted() {
        Observable<String> source = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> innerObserver) {
                innerObserver.onSubscribe(Disposables.empty());
                publishNext(innerObserver, 100, "one");// publish as it's first

                publishNext(innerObserver, 300, "two");// skip as it's last within the first 400

                publishNext(innerObserver, 900, "three");// publish

                publishNext(innerObserver, 905, "four");// skip

                publishCompleted(innerObserver, 1000);// Should be published as soon as the timeout expires.

            }
        });
        Observable<String> sampled = source.throttleFirst(400, TimeUnit.MILLISECONDS, scheduler);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(0)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(0)).onNext("four");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testThrottlingWithError() {
        Observable<String> source = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> innerObserver) {
                innerObserver.onSubscribe(Disposables.empty());
                Exception error = new TestException();
                publishNext(innerObserver, 100, "one");// Should be published since it is first

                publishNext(innerObserver, 200, "two");// Should be skipped since onError will arrive before the timeout expires

                publishError(innerObserver, 300, error);// Should be published as soon as the timeout expires.

            }
        });
        Observable<String> sampled = source.throttleFirst(400, TimeUnit.MILLISECONDS, scheduler);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(400, TimeUnit.MILLISECONDS);
        inOrder.verify(observer).onNext("one");
        inOrder.verify(observer).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testThrottle() {
        Observer<Integer> observer = mockObserver();
        TestScheduler s = new TestScheduler();
        PublishSubject<Integer> o = PublishSubject.create();
        o.throttleFirst(500, TimeUnit.MILLISECONDS, s).subscribe(observer);
        // send events with simulated time increments
        s.advanceTimeTo(0, TimeUnit.MILLISECONDS);
        o.onNext(1);// deliver

        o.onNext(2);// skip

        s.advanceTimeTo(501, TimeUnit.MILLISECONDS);
        o.onNext(3);// deliver

        s.advanceTimeTo(600, TimeUnit.MILLISECONDS);
        o.onNext(4);// skip

        s.advanceTimeTo(700, TimeUnit.MILLISECONDS);
        o.onNext(5);// skip

        o.onNext(6);// skip

        s.advanceTimeTo(1001, TimeUnit.MILLISECONDS);
        o.onNext(7);// deliver

        s.advanceTimeTo(1501, TimeUnit.MILLISECONDS);
        o.onComplete();
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onNext(1);
        inOrder.verify(observer).onNext(3);
        inOrder.verify(observer).onNext(7);
        inOrder.verify(observer).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void throttleFirstDefaultScheduler() {
        throttleFirst(100, TimeUnit.MILLISECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(throttleFirst(1, TimeUnit.DAYS));
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            throttleFirst(1, TimeUnit.DAYS).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

