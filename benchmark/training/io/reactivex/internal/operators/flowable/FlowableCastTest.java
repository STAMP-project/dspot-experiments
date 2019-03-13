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
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableCastTest {
    @Test
    public void testCast() {
        Flowable<?> source = Flowable.just(1, 2);
        Flowable<Integer> flowable = source.cast(Integer.class);
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testCastWithWrongType() {
        Flowable<?> source = Flowable.just(1, 2);
        Flowable<Boolean> flowable = source.cast(Boolean.class);
        Subscriber<Boolean> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(ClassCastException.class));
    }

    @Test
    public void castCrashUnsubscribes() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<String> ts = TestSubscriber.create();
        pp.cast(String.class).subscribe(ts);
        Assert.assertTrue("Not subscribed?", pp.hasSubscribers());
        pp.onNext(1);
        Assert.assertFalse("Subscribed?", pp.hasSubscribers());
        ts.assertError(ClassCastException.class);
    }
}

