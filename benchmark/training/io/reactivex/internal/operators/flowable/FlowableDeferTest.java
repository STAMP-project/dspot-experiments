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
import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


@SuppressWarnings("unchecked")
public class FlowableDeferTest {
    @Test
    public void testDefer() throws Throwable {
        Callable<Flowable<String>> factory = Mockito.mock(Callable.class);
        Flowable<String> firstObservable = Flowable.just("one", "two");
        Flowable<String> secondObservable = Flowable.just("three", "four");
        Mockito.when(factory.call()).thenReturn(firstObservable, secondObservable);
        Flowable<String> deferred = Flowable.defer(factory);
        Mockito.verifyZeroInteractions(factory);
        Subscriber<String> firstSubscriber = TestHelper.mockSubscriber();
        deferred.subscribe(firstSubscriber);
        Mockito.verify(factory, Mockito.times(1)).call();
        Mockito.verify(firstSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(firstSubscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(firstSubscriber, Mockito.times(0)).onNext("three");
        Mockito.verify(firstSubscriber, Mockito.times(0)).onNext("four");
        Mockito.verify(firstSubscriber, Mockito.times(1)).onComplete();
        Subscriber<String> secondSubscriber = TestHelper.mockSubscriber();
        deferred.subscribe(secondSubscriber);
        Mockito.verify(factory, Mockito.times(2)).call();
        Mockito.verify(secondSubscriber, Mockito.times(0)).onNext("one");
        Mockito.verify(secondSubscriber, Mockito.times(0)).onNext("two");
        Mockito.verify(secondSubscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(secondSubscriber, Mockito.times(1)).onNext("four");
        Mockito.verify(secondSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testDeferFunctionThrows() throws Exception {
        Callable<Flowable<String>> factory = Mockito.mock(Callable.class);
        Mockito.when(factory.call()).thenThrow(new TestException());
        Flowable<String> result = Flowable.defer(factory);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }
}

