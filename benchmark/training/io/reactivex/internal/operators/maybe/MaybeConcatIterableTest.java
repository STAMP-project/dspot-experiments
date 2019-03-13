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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.util.CrashingMappedIterable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;


public class MaybeConcatIterableTest {
    @SuppressWarnings("unchecked")
    @Test
    public void take() {
        Maybe.concat(asList(Maybe.just(1), Maybe.just(2), Maybe.just(3))).take(1).test().assertResult(1);
    }

    @Test
    public void iteratorThrows() {
        Maybe.concat(new Iterable<MaybeSource<Object>>() {
            @Override
            public Iterator<MaybeSource<Object>> iterator() {
                throw new TestException("iterator()");
            }
        }).test().assertFailureAndMessage(TestException.class, "iterator()");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void error() {
        Maybe.concat(asList(Maybe.just(1), Maybe.<Integer>error(new TestException()), Maybe.just(3))).test().assertFailure(TestException.class, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void successCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final TestSubscriber<Integer> ts = Maybe.concat(asList(pp.singleElement())).test();
            pp.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void hasNextThrows() {
        Maybe.concat(new CrashingMappedIterable<Maybe<Integer>>(100, 1, 100, new io.reactivex.functions.Function<Integer, Maybe<Integer>>() {
            @Override
            public io.reactivex.Maybe<Integer> apply(Integer v) throws Exception {
                return Maybe.just(1);
            }
        })).test().assertFailureAndMessage(TestException.class, "hasNext()");
    }

    @Test
    public void nextThrows() {
        Maybe.concat(new CrashingMappedIterable<Maybe<Integer>>(100, 100, 1, new io.reactivex.functions.Function<Integer, Maybe<Integer>>() {
            @Override
            public io.reactivex.Maybe<Integer> apply(Integer v) throws Exception {
                return Maybe.just(1);
            }
        })).test().assertFailureAndMessage(TestException.class, "next()");
    }

    @Test
    public void nextReturnsNull() {
        Maybe.concat(new CrashingMappedIterable<Maybe<Integer>>(100, 100, 100, new io.reactivex.functions.Function<Integer, Maybe<Integer>>() {
            @Override
            public io.reactivex.Maybe<Integer> apply(Integer v) throws Exception {
                return null;
            }
        })).test().assertFailure(NullPointerException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noSubsequentSubscription() {
        final int[] calls = new int[]{ 0 };
        Maybe<Integer> source = Maybe.create(new MaybeOnSubscribe<Integer>() {
            @Override
            public void subscribe(MaybeEmitter<Integer> s) throws Exception {
                (calls[0])++;
                s.onSuccess(1);
            }
        });
        Maybe.concat(asList(source, source)).firstElement().test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noSubsequentSubscriptionDelayError() {
        final int[] calls = new int[]{ 0 };
        Maybe<Integer> source = Maybe.create(new MaybeOnSubscribe<Integer>() {
            @Override
            public void subscribe(MaybeEmitter<Integer> s) throws Exception {
                (calls[0])++;
                s.onSuccess(1);
            }
        });
        Maybe.concatDelayError(asList(source, source)).firstElement().test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }
}

