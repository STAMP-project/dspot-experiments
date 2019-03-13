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
package io.reactivex.internal.subscribers;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscriber;


public class SubscriberResourceWrapperTest {
    TestSubscriber<Integer> ts = new TestSubscriber<Integer>();

    SubscriberResourceWrapper<Integer> s = new SubscriberResourceWrapper<Integer>(ts);

    @Test
    public void cancel() {
        BooleanSubscription bs = new BooleanSubscription();
        Disposable d = Disposables.empty();
        s.setResource(d);
        s.onSubscribe(bs);
        Assert.assertFalse(d.isDisposed());
        Assert.assertFalse(s.isDisposed());
        ts.cancel();
        Assert.assertTrue(bs.isCancelled());
        Assert.assertTrue(d.isDisposed());
        Assert.assertTrue(s.isDisposed());
    }

    @Test
    public void error() {
        BooleanSubscription bs = new BooleanSubscription();
        Disposable d = Disposables.empty();
        s.setResource(d);
        s.onSubscribe(bs);
        s.onError(new TestException());
        Assert.assertTrue(d.isDisposed());
        Assert.assertFalse(bs.isCancelled());
        ts.assertFailure(TestException.class);
    }

    @Test
    public void complete() {
        BooleanSubscription bs = new BooleanSubscription();
        Disposable d = Disposables.empty();
        s.setResource(d);
        s.onSubscribe(bs);
        s.onComplete();
        Assert.assertTrue(d.isDisposed());
        Assert.assertFalse(bs.isCancelled());
        ts.assertResult();
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.lift(new FlowableOperator<Object, Object>() {
                    @Override
                    public Subscriber<? super Object> apply(Subscriber<? super Object> s) throws Exception {
                        return new SubscriberResourceWrapper<Object>(s);
                    }
                });
            }
        });
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(Flowable.never().lift(new FlowableOperator<Object, Object>() {
            @Override
            public Subscriber<? super Object> apply(Subscriber<? super Object> s) throws Exception {
                return new SubscriberResourceWrapper<Object>(s);
            }
        }));
    }
}

