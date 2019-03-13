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
package io.reactivex.internal.operators.single;


import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class SingleMiscTest {
    @Test
    public void never() {
        Single.never().test().assertNoValues().assertNoErrors().assertNotComplete();
    }

    @Test
    public void timer() throws Exception {
        Single.timer(100, TimeUnit.MILLISECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertResult(0L);
    }

    @Test
    public void wrap() {
        Assert.assertSame(Single.never(), Single.wrap(Single.never()));
        Single.wrap(new io.reactivex.SingleSource<Object>() {
            @Override
            public void subscribe(SingleObserver<? super Object> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onSuccess(1);
            }
        }).test().assertResult(1);
    }

    @Test
    public void cast() {
        Single<Number> source = Single.just(1.0).cast(Number.class);
        source.test().assertResult(((Number) (1.0)));
    }

    @Test
    public void contains() {
        Single.just(1).contains(1).test().assertResult(true);
        Single.just(2).contains(1).test().assertResult(false);
    }

    @Test
    public void compose() {
        Single.just(1).compose(new io.reactivex.SingleTransformer<Integer, Object>() {
            @Override
            public io.reactivex.SingleSource<Object> apply(Single<Integer> f) {
                return f.map(new Function<Integer, Object>() {
                    @Override
                    public Object apply(Integer v) throws Exception {
                        return v + 1;
                    }
                });
            }
        }).test().assertResult(2);
    }

    @Test
    public void hide() {
        Assert.assertNotSame(Single.never(), Single.never().hide());
    }

    @Test
    public void onErrorResumeNext() {
        Single.<Integer>error(new TestException()).onErrorResumeNext(Single.just(1)).test().assertResult(1);
    }

    @Test
    public void onErrorReturnValue() {
        Single.<Integer>error(new TestException()).onErrorReturnItem(1).test().assertResult(1);
    }

    @Test
    public void repeat() {
        Single.just(1).repeat().take(5).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void repeatTimes() {
        Single.just(1).repeat(5).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void repeatUntil() {
        final AtomicBoolean flag = new AtomicBoolean();
        Single.just(1).doOnSuccess(new Consumer<Integer>() {
            int c;

            @Override
            public void accept(Integer v) throws Exception {
                if ((++(c)) == 5) {
                    flag.set(true);
                }
            }
        }).repeatUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return flag.get();
            }
        }).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void retry() {
        Single.fromCallable(new java.util.concurrent.Callable<Object>() {
            int c;

            @Override
            public Object call() throws Exception {
                if ((++(c)) != 5) {
                    throw new TestException();
                }
                return 1;
            }
        }).retry().test().assertResult(1);
    }

    @Test
    public void retryBiPredicate() {
        Single.fromCallable(new java.util.concurrent.Callable<Object>() {
            int c;

            @Override
            public Object call() throws Exception {
                if ((++(c)) != 5) {
                    throw new TestException();
                }
                return 1;
            }
        }).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer i, Throwable e) throws Exception {
                return true;
            }
        }).test().assertResult(1);
    }

    @Test
    public void retryTimes() {
        Single.fromCallable(new java.util.concurrent.Callable<Object>() {
            int c;

            @Override
            public Object call() throws Exception {
                if ((++(c)) != 5) {
                    throw new TestException();
                }
                return 1;
            }
        }).retry(5).test().assertResult(1);
    }

    @Test
    public void retryPredicate() {
        Single.fromCallable(new java.util.concurrent.Callable<Object>() {
            int c;

            @Override
            public Object call() throws Exception {
                if ((++(c)) != 5) {
                    throw new TestException();
                }
                return 1;
            }
        }).retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable e) throws Exception {
                return true;
            }
        }).test().assertResult(1);
    }

    @Test
    public void timeout() throws Exception {
        Single.never().timeout(100, TimeUnit.MILLISECONDS, io.reactivex.schedulers.Schedulers.io()).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TimeoutException.class);
    }

    @Test
    public void timeoutOther() throws Exception {
        Single.never().timeout(100, TimeUnit.MILLISECONDS, io.reactivex.schedulers.Schedulers.io(), Single.just(1)).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void toCompletable() {
        Single.just(1).toCompletable().test().assertResult();
        Single.error(new TestException()).toCompletable().test().assertFailure(TestException.class);
    }

    @Test
    public void ignoreElement() {
        Single.just(1).ignoreElement().test().assertResult();
        Single.error(new TestException()).ignoreElement().test().assertFailure(TestException.class);
    }

    @Test
    public void toObservable() {
        Single.just(1).toObservable().test().assertResult(1);
        Single.error(new TestException()).toObservable().test().assertFailure(TestException.class);
    }

    @Test
    public void equals() {
        Single.equals(Single.just(1), Single.just(1).hide()).test().assertResult(true);
        Single.equals(Single.just(1), Single.just(2)).test().assertResult(false);
    }
}

