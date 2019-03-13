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
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableSkipUntilTest {
    Observer<Object> observer;

    @Test
    public void normal1() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> m = source.skipUntil(other);
        m.subscribe(observer);
        source.onNext(0);
        source.onNext(1);
        other.onNext(100);
        source.onNext(2);
        source.onNext(3);
        source.onNext(4);
        source.onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onNext(3);
        Mockito.verify(observer, Mockito.times(1)).onNext(4);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void otherNeverFires() {
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> m = source.skipUntil(Observable.never());
        m.subscribe(observer);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onNext(4);
        source.onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void otherEmpty() {
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> m = source.skipUntil(Observable.empty());
        m.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void otherFiresAndCompletes() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> m = source.skipUntil(other);
        m.subscribe(observer);
        source.onNext(0);
        source.onNext(1);
        other.onNext(100);
        other.onComplete();
        source.onNext(2);
        source.onNext(3);
        source.onNext(4);
        source.onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onNext(3);
        Mockito.verify(observer, Mockito.times(1)).onNext(4);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void sourceThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> m = source.skipUntil(other);
        m.subscribe(observer);
        source.onNext(0);
        source.onNext(1);
        other.onNext(100);
        other.onComplete();
        source.onNext(2);
        source.onError(new RuntimeException("Forced failure"));
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void otherThrowsImmediately() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> m = source.skipUntil(other);
        m.subscribe(observer);
        source.onNext(0);
        source.onNext(1);
        other.onError(new RuntimeException("Forced failure"));
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().skipUntil(PublishSubject.create()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.skipUntil(Observable.never());
            }
        });
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return Observable.never().skipUntil(o);
            }
        });
    }
}

