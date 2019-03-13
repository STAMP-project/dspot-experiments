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
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.MaybeSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class MaybeFlatMapPublisherTest {
    @Test
    public void cancelMain() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = ms.flatMapPublisher(Functions.justFunction(pp)).test();
        Assert.assertTrue(ms.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(ms.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void cancelOther() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = ms.flatMapPublisher(Functions.justFunction(pp)).test();
        Assert.assertTrue(ms.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
        ms.onSuccess(1);
        Assert.assertFalse(ms.hasObservers());
        Assert.assertTrue(pp.hasSubscribers());
        ts.cancel();
        Assert.assertFalse(ms.hasObservers());
        Assert.assertFalse(pp.hasSubscribers());
    }

    @Test
    public void mapperCrash() {
        Maybe.just(1).flatMapPublisher(new io.reactivex.functions.Function<Integer, Publisher<? extends Object>>() {
            @Override
            public Publisher<? extends Object> apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeMaybeToFlowable(new io.reactivex.functions.Function<Maybe<Object>, Publisher<Object>>() {
            @Override
            public Publisher<Object> apply(Maybe<Object> m) throws Exception {
                return m.flatMapPublisher(Functions.justFunction(Flowable.never()));
            }
        });
    }
}

