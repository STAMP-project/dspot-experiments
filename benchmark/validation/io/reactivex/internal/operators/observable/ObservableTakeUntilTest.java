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
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ObservableTakeUntilTest {
    @Test
    public void testTakeUntil() {
        Disposable sSource = Mockito.mock(Disposable.class);
        Disposable sOther = Mockito.mock(Disposable.class);
        ObservableTakeUntilTest.TestObservable source = new ObservableTakeUntilTest.TestObservable(sSource);
        ObservableTakeUntilTest.TestObservable other = new ObservableTakeUntilTest.TestObservable(sOther);
        Observer<String> result = mockObserver();
        Observable<String> stringObservable = Observable.unsafeCreate(source).takeUntil(Observable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        other.sendOnNext("three");
        source.sendOnNext("four");
        source.sendOnCompleted();
        other.sendOnCompleted();
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(0)).onNext("four");
        Mockito.verify(sSource, Mockito.times(1)).dispose();
        Mockito.verify(sOther, Mockito.times(1)).dispose();
    }

    @Test
    public void testTakeUntilSourceCompleted() {
        Disposable sSource = Mockito.mock(Disposable.class);
        Disposable sOther = Mockito.mock(Disposable.class);
        ObservableTakeUntilTest.TestObservable source = new ObservableTakeUntilTest.TestObservable(sSource);
        ObservableTakeUntilTest.TestObservable other = new ObservableTakeUntilTest.TestObservable(sOther);
        Observer<String> result = mockObserver();
        Observable<String> stringObservable = Observable.unsafeCreate(source).takeUntil(Observable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        source.sendOnCompleted();
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(sSource, Mockito.never()).dispose();// no longer disposing itself on terminal events

        Mockito.verify(sOther, Mockito.times(1)).dispose();
    }

    @Test
    public void testTakeUntilSourceError() {
        Disposable sSource = Mockito.mock(Disposable.class);
        Disposable sOther = Mockito.mock(Disposable.class);
        ObservableTakeUntilTest.TestObservable source = new ObservableTakeUntilTest.TestObservable(sSource);
        ObservableTakeUntilTest.TestObservable other = new ObservableTakeUntilTest.TestObservable(sOther);
        Throwable error = new Throwable();
        Observer<String> result = mockObserver();
        Observable<String> stringObservable = Observable.unsafeCreate(source).takeUntil(Observable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        source.sendOnError(error);
        source.sendOnNext("three");
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(1)).onError(error);
        Mockito.verify(sSource, Mockito.never()).dispose();// no longer disposing itself on terminal events

        Mockito.verify(sOther, Mockito.times(1)).dispose();
    }

    @Test
    public void testTakeUntilOtherError() {
        Disposable sSource = Mockito.mock(Disposable.class);
        Disposable sOther = Mockito.mock(Disposable.class);
        ObservableTakeUntilTest.TestObservable source = new ObservableTakeUntilTest.TestObservable(sSource);
        ObservableTakeUntilTest.TestObservable other = new ObservableTakeUntilTest.TestObservable(sOther);
        Throwable error = new Throwable();
        Observer<String> result = mockObserver();
        Observable<String> stringObservable = Observable.unsafeCreate(source).takeUntil(Observable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        other.sendOnError(error);
        source.sendOnNext("three");
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(1)).onError(error);
        Mockito.verify(result, Mockito.times(0)).onComplete();
        Mockito.verify(sSource, Mockito.times(1)).dispose();
        Mockito.verify(sOther, Mockito.never()).dispose();// no longer disposing itself on termination

    }

    /**
     * If the 'other' onCompletes then we unsubscribe from the source and onComplete.
     */
    @Test
    public void testTakeUntilOtherCompleted() {
        Disposable sSource = Mockito.mock(Disposable.class);
        Disposable sOther = Mockito.mock(Disposable.class);
        ObservableTakeUntilTest.TestObservable source = new ObservableTakeUntilTest.TestObservable(sSource);
        ObservableTakeUntilTest.TestObservable other = new ObservableTakeUntilTest.TestObservable(sOther);
        Observer<String> result = mockObserver();
        Observable<String> stringObservable = Observable.unsafeCreate(source).takeUntil(Observable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        other.sendOnCompleted();
        source.sendOnNext("three");
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(1)).onComplete();
        Mockito.verify(sSource, Mockito.times(1)).dispose();
        Mockito.verify(sOther, Mockito.never()).dispose();// no longer disposing itself on terminal events

    }

    private static class TestObservable implements ObservableSource<String> {
        Observer<? super String> observer;

        Disposable upstream;

        TestObservable(Disposable d) {
            this.upstream = d;
        }

        /* used to simulate subscription */
        public void sendOnCompleted() {
            observer.onComplete();
        }

        /* used to simulate subscription */
        public void sendOnNext(String value) {
            observer.onNext(value);
        }

        /* used to simulate subscription */
        public void sendOnError(Throwable e) {
            observer.onError(e);
        }

        @Override
        public void subscribe(Observer<? super String> observer) {
            this.observer = observer;
            observer.onSubscribe(upstream);
        }
    }

    @Test
    public void testUntilFires() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> until = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.takeUntil(until).subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(until.hasObservers());
        source.onNext(1);
        to.assertValue(1);
        until.onNext(1);
        to.assertValue(1);
        to.assertNoErrors();
        to.assertTerminated();
        Assert.assertFalse("Source still has observers", source.hasObservers());
        Assert.assertFalse("Until still has observers", until.hasObservers());
        // 2.0.2 - not anymore
        // assertTrue("Not cancelled!", ts.isCancelled());
    }

    @Test
    public void testMainCompletes() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> until = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.takeUntil(until).subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(until.hasObservers());
        source.onNext(1);
        source.onComplete();
        to.assertValue(1);
        to.assertNoErrors();
        to.assertTerminated();
        Assert.assertFalse("Source still has observers", source.hasObservers());
        Assert.assertFalse("Until still has observers", until.hasObservers());
        // 2.0.2 - not anymore
        // assertTrue("Not cancelled!", ts.isCancelled());
    }

    @Test
    public void testDownstreamUnsubscribes() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> until = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.takeUntil(until).take(1).subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(until.hasObservers());
        source.onNext(1);
        to.assertValue(1);
        to.assertNoErrors();
        to.assertTerminated();
        Assert.assertFalse("Source still has observers", source.hasObservers());
        Assert.assertFalse("Until still has observers", until.hasObservers());
        // 2.0.2 - not anymore
        // assertTrue("Not cancelled!", ts.isCancelled());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().takeUntil(Observable.never()));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Integer>, Observable<Integer>>() {
            @Override
            public io.reactivex.Observable<Integer> apply(Observable<Integer> o) throws Exception {
                return o.takeUntil(Observable.never());
            }
        });
    }

    @Test
    public void untilPublisherMainSuccess() {
        PublishSubject<Integer> main = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        TestObserver<Integer> to = main.takeUntil(other).test();
        Assert.assertTrue("Main no observers?", main.hasObservers());
        Assert.assertTrue("Other no observers?", other.hasObservers());
        main.onNext(1);
        main.onNext(2);
        main.onComplete();
        Assert.assertFalse("Main has observers?", main.hasObservers());
        Assert.assertFalse("Other has observers?", other.hasObservers());
        to.assertResult(1, 2);
    }

    @Test
    public void untilPublisherMainComplete() {
        PublishSubject<Integer> main = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        TestObserver<Integer> to = main.takeUntil(other).test();
        Assert.assertTrue("Main no observers?", main.hasObservers());
        Assert.assertTrue("Other no observers?", other.hasObservers());
        main.onComplete();
        Assert.assertFalse("Main has observers?", main.hasObservers());
        Assert.assertFalse("Other has observers?", other.hasObservers());
        to.assertResult();
    }

    @Test
    public void untilPublisherMainError() {
        PublishSubject<Integer> main = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        TestObserver<Integer> to = main.takeUntil(other).test();
        Assert.assertTrue("Main no observers?", main.hasObservers());
        Assert.assertTrue("Other no observers?", other.hasObservers());
        main.onError(new TestException());
        Assert.assertFalse("Main has observers?", main.hasObservers());
        Assert.assertFalse("Other has observers?", other.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void untilPublisherOtherOnNext() {
        PublishSubject<Integer> main = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        TestObserver<Integer> to = main.takeUntil(other).test();
        Assert.assertTrue("Main no observers?", main.hasObservers());
        Assert.assertTrue("Other no observers?", other.hasObservers());
        other.onNext(1);
        Assert.assertFalse("Main has observers?", main.hasObservers());
        Assert.assertFalse("Other has observers?", other.hasObservers());
        to.assertResult();
    }

    @Test
    public void untilPublisherOtherOnComplete() {
        PublishSubject<Integer> main = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        TestObserver<Integer> to = main.takeUntil(other).test();
        Assert.assertTrue("Main no observers?", main.hasObservers());
        Assert.assertTrue("Other no observers?", other.hasObservers());
        other.onComplete();
        Assert.assertFalse("Main has observers?", main.hasObservers());
        Assert.assertFalse("Other has observers?", other.hasObservers());
        to.assertResult();
    }

    @Test
    public void untilPublisherOtherError() {
        PublishSubject<Integer> main = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        TestObserver<Integer> to = main.takeUntil(other).test();
        Assert.assertTrue("Main no observers?", main.hasObservers());
        Assert.assertTrue("Other no observers?", other.hasObservers());
        other.onError(new TestException());
        Assert.assertFalse("Main has observers?", main.hasObservers());
        Assert.assertFalse("Other has observers?", other.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void untilPublisherDispose() {
        PublishSubject<Integer> main = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        TestObserver<Integer> to = main.takeUntil(other).test();
        Assert.assertTrue("Main no observers?", main.hasObservers());
        Assert.assertTrue("Other no observers?", other.hasObservers());
        to.dispose();
        Assert.assertFalse("Main has observers?", main.hasObservers());
        Assert.assertFalse("Other has observers?", other.hasObservers());
        to.assertEmpty();
    }
}

