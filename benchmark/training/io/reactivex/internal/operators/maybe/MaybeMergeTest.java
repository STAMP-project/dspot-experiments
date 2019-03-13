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
package io.reactivex.internal.operators.maybe;


import io.reactivex.exceptions.TestException;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;


public class MaybeMergeTest {
    @Test
    public void delayErrorWithMaxConcurrency() {
        Maybe.mergeDelayError(Flowable.just(Maybe.just(1), Maybe.just(2), Maybe.just(3)), 1).test().assertResult(1, 2, 3);
    }

    @Test
    public void delayErrorWithMaxConcurrencyError() {
        Maybe.mergeDelayError(Flowable.just(Maybe.just(1), Maybe.<Integer>error(new TestException()), Maybe.just(3)), 1).test().assertFailure(TestException.class, 1, 3);
    }

    @Test
    public void delayErrorWithMaxConcurrencyAsync() {
        final AtomicInteger count = new AtomicInteger();
        @SuppressWarnings("unchecked")
        io.reactivex.Maybe<Integer>[] sources = new Maybe[3];
        for (int i = 0; i < 3; i++) {
            final int j = i + 1;
            sources[i] = Maybe.fromCallable(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return (count.incrementAndGet()) - j;
                }
            }).subscribeOn(Schedulers.io());
        }
        for (int i = 0; i < 1000; i++) {
            count.set(0);
            Maybe.mergeDelayError(Flowable.fromArray(sources), 1).test().awaitDone(5, TimeUnit.SECONDS).assertResult(0, 0, 0);
        }
    }

    @Test
    public void delayErrorWithMaxConcurrencyAsyncError() {
        final AtomicInteger count = new AtomicInteger();
        @SuppressWarnings("unchecked")
        io.reactivex.Maybe<Integer>[] sources = new Maybe[3];
        for (int i = 0; i < 3; i++) {
            final int j = i + 1;
            sources[i] = Maybe.fromCallable(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return (count.incrementAndGet()) - j;
                }
            }).subscribeOn(Schedulers.io());
        }
        sources[1] = Maybe.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                throw new TestException(("" + (count.incrementAndGet())));
            }
        }).subscribeOn(Schedulers.io());
        for (int i = 0; i < 1000; i++) {
            count.set(0);
            Maybe.mergeDelayError(Flowable.fromArray(sources), 1).test().awaitDone(5, TimeUnit.SECONDS).assertFailureAndMessage(TestException.class, "2", 0, 0);
        }
    }

    @Test
    public void scalar() {
        Maybe.mergeDelayError(Flowable.just(Maybe.just(1))).test().assertResult(1);
    }
}

