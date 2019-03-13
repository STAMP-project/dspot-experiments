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
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.processors.PublishProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableHideTest {
    @Test
    public void testHiding() {
        PublishProcessor<Integer> src = PublishProcessor.create();
        Flowable<Integer> dst = src.hide();
        Assert.assertFalse((dst instanceof PublishProcessor));
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        dst.subscribe(subscriber);
        src.onNext(1);
        src.onComplete();
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testHidingError() {
        PublishProcessor<Integer> src = PublishProcessor.create();
        Flowable<Integer> dst = src.hide();
        Assert.assertFalse((dst instanceof PublishProcessor));
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        dst.subscribe(subscriber);
        src.onError(new TestException());
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.hide();
            }
        });
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(PublishProcessor.create().hide());
    }
}

