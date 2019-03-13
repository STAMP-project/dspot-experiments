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
import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;
import org.junit.Assert;
import org.junit.Test;


public class MaybeFlatMapBiSelectorTest {
    @Test
    public void normal() {
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(2);
            }
        }, stringCombine()).test().assertResult("1:2");
    }

    @Test
    public void normalWithEmpty() {
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.empty();
            }
        }, stringCombine()).test().assertResult();
    }

    @Test
    public void emptyWithJust() {
        final int[] call = new int[]{ 0 };
        Maybe.<Integer>empty().flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                (call[0])++;
                return Maybe.just(1);
            }
        }, stringCombine()).test().assertResult();
        Assert.assertEquals(0, call[0]);
    }

    @Test
    public void errorWithJust() {
        final int[] call = new int[]{ 0 };
        Maybe.<Integer>error(new TestException()).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                (call[0])++;
                return Maybe.just(1);
            }
        }, stringCombine()).test().assertFailure(TestException.class);
        Assert.assertEquals(0, call[0]);
    }

    @Test
    public void justWithError() {
        final int[] call = new int[]{ 0 };
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                (call[0])++;
                return Maybe.<Integer>error(new TestException());
            }
        }, stringCombine()).test().assertFailure(TestException.class);
        Assert.assertEquals(1, call[0]);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().singleElement().flatMap(new Function<Object, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Object v) throws Exception {
                return Maybe.just(1);
            }
        }, new BiFunction<Object, Integer, Object>() {
            @Override
            public Object apply(Object a, Integer b) throws Exception {
                return b;
            }
        }));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeMaybe(new Function<Maybe<Object>, MaybeSource<Object>>() {
            @Override
            public io.reactivex.MaybeSource<Object> apply(Maybe<Object> v) throws Exception {
                return v.flatMap(new Function<Object, MaybeSource<Integer>>() {
                    @Override
                    public io.reactivex.MaybeSource<Integer> apply(Object v) throws Exception {
                        return Maybe.just(1);
                    }
                }, new BiFunction<Object, Integer, Object>() {
                    @Override
                    public Object apply(Object a, Integer b) throws Exception {
                        return b;
                    }
                });
            }
        });
    }

    @Test
    public void mapperThrows() {
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                throw new TestException();
            }
        }, stringCombine()).test().assertFailure(TestException.class);
    }

    @Test
    public void mapperReturnsNull() {
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return null;
            }
        }, stringCombine()).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void resultSelectorThrows() {
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(2);
            }
        }, new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void resultSelectorReturnsNull() {
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                return Maybe.just(2);
            }
        }, new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void mapperCancels() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        Maybe.just(1).flatMap(new Function<Integer, MaybeSource<Integer>>() {
            @Override
            public io.reactivex.MaybeSource<Integer> apply(Integer v) throws Exception {
                to.cancel();
                return Maybe.just(2);
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                throw new IllegalStateException();
            }
        }).subscribeWith(to).assertEmpty();
    }
}

