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
package io.reactivex.internal.operators.mixed;


import io.reactivex.TestHelper;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class CompletableAndThenPublisherTest {
    @Test
    public void cancelMain() {
        CompletableSubject cs = CompletableSubject.create();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = cs.andThen(pp).test();
        Assert.assertTrue(cs.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(cs.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void cancelOther() {
        CompletableSubject cs = CompletableSubject.create();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = cs.andThen(pp).test();
        Assert.assertTrue(cs.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
        cs.onComplete();
        Assert.assertFalse(cs.hasObservers());
        Assert.assertTrue(pp.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(cs.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeCompletableToFlowable(new io.reactivex.functions.Function<Completable, Publisher<Object>>() {
            @Override
            public Publisher<Object> apply(Completable m) throws Exception {
                return m.andThen(Flowable.never());
            }
        });
    }
}

