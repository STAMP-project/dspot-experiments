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
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
public class ObservableDematerializeTest {
    @Test
    public void simpleSelector() {
        Observable<Notification<Integer>> notifications = Observable.just(1, 2).materialize();
        Observable<Integer> dematerialize = notifications.dematerialize(Functions.<Notification<Integer>>identity());
        Observer<Integer> observer = mockObserver();
        dematerialize.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void selectorCrash() {
        Observable.just(1, 2).materialize().dematerialize(new io.reactivex.functions.Function<Notification<Integer>, Notification<Object>>() {
            @Override
            public io.reactivex.Notification<Object> apply(Notification<Integer> v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void selectorNull() {
        Observable.just(1, 2).materialize().dematerialize(new io.reactivex.functions.Function<Notification<Integer>, Notification<Object>>() {
            @Override
            public io.reactivex.Notification<Object> apply(Notification<Integer> v) throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void testDematerialize1() {
        Observable<Notification<Integer>> notifications = Observable.just(1, 2).materialize();
        Observable<Integer> dematerialize = notifications.dematerialize();
        Observer<Integer> observer = mockObserver();
        dematerialize.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDematerialize2() {
        Throwable exception = new Throwable("test");
        Observable<Integer> o = Observable.error(exception);
        Observable<Integer> dematerialize = o.materialize().dematerialize();
        Observer<Integer> observer = mockObserver();
        dematerialize.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onError(exception);
        Mockito.verify(observer, Mockito.times(0)).onComplete();
        Mockito.verify(observer, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testDematerialize3() {
        Exception exception = new Exception("test");
        Observable<Integer> o = Observable.error(exception);
        Observable<Integer> dematerialize = o.materialize().dematerialize();
        Observer<Integer> observer = mockObserver();
        dematerialize.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onError(exception);
        Mockito.verify(observer, Mockito.times(0)).onComplete();
        Mockito.verify(observer, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testErrorPassThru() {
        Exception exception = new Exception("test");
        Observable<Integer> o = Observable.error(exception);
        Observable<Integer> dematerialize = o.dematerialize();
        Observer<Integer> observer = mockObserver();
        dematerialize.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onError(exception);
        Mockito.verify(observer, Mockito.times(0)).onComplete();
        Mockito.verify(observer, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testCompletePassThru() {
        Observable<Integer> o = Observable.empty();
        Observable<Integer> dematerialize = o.dematerialize();
        Observer<Integer> observer = mockObserver();
        TestObserver<Integer> to = new TestObserver<Integer>(observer);
        dematerialize.subscribe(to);
        System.out.println(to.errors());
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testHonorsContractWhenCompleted() {
        Observable<Integer> source = Observable.just(1);
        Observable<Integer> result = source.materialize().dematerialize();
        Observer<Integer> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testHonorsContractWhenThrows() {
        Observable<Integer> source = Observable.error(new TestException());
        Observable<Integer> result = source.materialize().dematerialize();
        Observer<Integer> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any(Integer.class));
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(dematerialize());
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.dematerialize();
            }
        });
    }

    @Test
    public void eventsAfterDematerializedTerminal() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            dematerialize().test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 1, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void nonNotificationInstanceAfterDispose() {
        dematerialize().test().assertResult();
    }
}

