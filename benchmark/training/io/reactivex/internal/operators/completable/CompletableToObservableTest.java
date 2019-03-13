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
package io.reactivex.internal.operators.completable;


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.internal.operators.completable.CompletableToObservable.ObserverCompletableObserver;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;


public class CompletableToObservableTest {
    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeCompletableToObservable(new io.reactivex.functions.Function<Completable, Observable<?>>() {
            @Override
            public io.reactivex.Observable<?> apply(Completable c) throws Exception {
                return c.toObservable();
            }
        });
    }

    @Test
    public void fusion() throws Exception {
        TestObserver<Void> to = new TestObserver<Void>();
        ObserverCompletableObserver co = new ObserverCompletableObserver(to);
        Disposable d = Disposables.empty();
        co.onSubscribe(d);
        Assert.assertEquals(NONE, co.requestFusion(SYNC));
        Assert.assertEquals(ASYNC, co.requestFusion(ASYNC));
        Assert.assertEquals(ASYNC, co.requestFusion(ANY));
        Assert.assertTrue(co.isEmpty());
        Assert.assertNull(co.poll());
        co.clear();
        Assert.assertFalse(co.isDisposed());
        co.dispose();
        Assert.assertTrue(d.isDisposed());
        Assert.assertTrue(co.isDisposed());
        TestHelper.assertNoOffer(co);
    }
}

