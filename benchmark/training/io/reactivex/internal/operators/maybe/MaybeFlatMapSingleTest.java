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
import java.util.NoSuchElementException;
import org.junit.Test;


public class MaybeFlatMapSingleTest {
    @Test(expected = NullPointerException.class)
    public void flatMapSingleNull() {
        Maybe.just(1).flatMapSingle(null);
    }

    @Test
    public void flatMapSingleValue() {
        Maybe.just(1).flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(final Integer integer) throws Exception {
                if (integer == 1) {
                    return Single.just(2);
                }
                return Single.just(1);
            }
        }).test().assertResult(2);
    }

    @Test
    public void flatMapSingleValueDifferentType() {
        Maybe.just(1).flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<String>>() {
            @Override
            public io.reactivex.SingleSource<String> apply(final Integer integer) throws Exception {
                if (integer == 1) {
                    return Single.just("2");
                }
                return Single.just("1");
            }
        }).test().assertResult("2");
    }

    @Test
    public void flatMapSingleValueNull() {
        Maybe.just(1).flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(final Integer integer) throws Exception {
                return null;
            }
        }).test().assertNoValues().assertError(NullPointerException.class).assertErrorMessage("The mapper returned a null SingleSource");
    }

    @Test
    public void flatMapSingleValueErrorThrown() {
        Maybe.just(1).flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(final Integer integer) throws Exception {
                throw new RuntimeException("something went terribly wrong!");
            }
        }).test().assertNoValues().assertError(RuntimeException.class).assertErrorMessage("something went terribly wrong!");
    }

    @Test
    public void flatMapSingleError() {
        RuntimeException exception = new RuntimeException("test");
        Maybe.error(exception).flatMapSingle(new io.reactivex.functions.Function<Object, SingleSource<Object>>() {
            @Override
            public io.reactivex.SingleSource<Object> apply(final Object integer) throws Exception {
                return Single.just(new Object());
            }
        }).test().assertError(exception);
    }

    @Test
    public void flatMapSingleEmpty() {
        Maybe.<Integer>empty().flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(final Integer integer) throws Exception {
                return Single.just(2);
            }
        }).test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Maybe.just(1).flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(final Integer integer) throws Exception {
                return Single.just(2);
            }
        }));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeMaybeToSingle(new io.reactivex.functions.Function<Maybe<Integer>, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(Maybe<Integer> m) throws Exception {
                return m.flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<Integer>>() {
                    @Override
                    public io.reactivex.SingleSource<Integer> apply(final Integer integer) throws Exception {
                        return Single.just(2);
                    }
                });
            }
        });
    }

    @Test
    public void singleErrors() {
        Maybe.just(1).flatMapSingle(new io.reactivex.functions.Function<Integer, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(final Integer integer) throws Exception {
                return Single.error(new TestException());
            }
        }).test().assertFailure(TestException.class);
    }
}

