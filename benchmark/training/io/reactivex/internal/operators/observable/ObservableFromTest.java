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


import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import io.reactivex.TestHelper;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class ObservableFromTest {
    @Test
    public void fromFutureTimeout() throws Exception {
        Observable.fromFuture(Observable.never().toFuture(), 100, TimeUnit.MILLISECONDS, Schedulers.io()).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TimeoutException.class);
    }

    @Test
    public void fromPublisher() {
        Observable.fromPublisher(Flowable.just(1)).test().assertResult(1);
    }

    @Test
    public void just10() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void fromArrayEmpty() {
        Assert.assertSame(Observable.empty(), Observable.fromArray());
    }

    @Test
    public void fromArraySingle() {
        Assert.assertTrue(((Observable.fromArray(1)) instanceof ScalarCallable));
    }

    @Test
    public void fromPublisherDispose() {
        TestHelper.checkDisposed(Flowable.just(1).toObservable());
    }

    @Test
    public void fromPublisherDoubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowableToObservable(new io.reactivex.functions.Function<Flowable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Flowable<Object> f) throws Exception {
                return f.toObservable();
            }
        });
    }

    @Test
    public void fusionRejected() {
        TestObserver<Integer> to = ObserverFusion.newTest(ASYNC);
        Observable.fromArray(1, 2, 3).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3);
    }
}

