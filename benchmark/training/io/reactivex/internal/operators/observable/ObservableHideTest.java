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
import io.reactivex.subjects.PublishSubject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableHideTest {
    @Test
    public void testHiding() {
        PublishSubject<Integer> src = PublishSubject.create();
        Observable<Integer> dst = src.hide();
        Assert.assertFalse((dst instanceof PublishSubject));
        Observer<Object> o = mockObserver();
        dst.subscribe(o);
        src.onNext(1);
        src.onComplete();
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testHidingError() {
        PublishSubject<Integer> src = PublishSubject.create();
        Observable<Integer> dst = src.hide();
        Assert.assertFalse((dst instanceof PublishSubject));
        Observer<Object> o = mockObserver();
        dst.subscribe(o);
        src.onError(new TestException());
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.hide();
            }
        });
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(PublishSubject.create().hide());
    }
}

