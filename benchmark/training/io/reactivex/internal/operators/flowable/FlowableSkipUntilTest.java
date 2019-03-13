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
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableSkipUntilTest {
    Subscriber<Object> subscriber;

    @Test
    public void normal1() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        Flowable<Integer> m = source.skipUntil(other);
        m.subscribe(subscriber);
        source.onNext(0);
        source.onNext(1);
        other.onNext(100);
        source.onNext(2);
        source.onNext(3);
        source.onNext(4);
        source.onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(3);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(4);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void otherNeverFires() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        Flowable<Integer> m = source.skipUntil(Flowable.never());
        m.subscribe(subscriber);
        source.onNext(0);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onNext(4);
        source.onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void otherEmpty() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        Flowable<Integer> m = source.skipUntil(Flowable.empty());
        m.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void otherFiresAndCompletes() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        Flowable<Integer> m = source.skipUntil(other);
        m.subscribe(subscriber);
        source.onNext(0);
        source.onNext(1);
        other.onNext(100);
        other.onComplete();
        source.onNext(2);
        source.onNext(3);
        source.onNext(4);
        source.onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(3);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(4);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void sourceThrows() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        Flowable<Integer> m = source.skipUntil(other);
        m.subscribe(subscriber);
        source.onNext(0);
        source.onNext(1);
        other.onNext(100);
        other.onComplete();
        source.onNext(2);
        source.onError(new RuntimeException("Forced failure"));
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void otherThrowsImmediately() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        Flowable<Integer> m = source.skipUntil(other);
        m.subscribe(subscriber);
        source.onNext(0);
        source.onNext(1);
        other.onError(new RuntimeException("Forced failure"));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().skipUntil(PublishProcessor.create()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.skipUntil(Flowable.never());
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return Flowable.never().skipUntil(f);
            }
        });
    }
}

