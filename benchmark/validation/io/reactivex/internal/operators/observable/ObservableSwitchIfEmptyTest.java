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
package io.reactivex.internal.operators.observable;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class ObservableSwitchIfEmptyTest {
    @Test
    public void testSwitchWhenNotEmpty() throws Exception {
        final AtomicBoolean subscribed = new AtomicBoolean(false);
        final Observable<Integer> o = Observable.just(4).switchIfEmpty(Observable.just(2).doOnSubscribe(new io.reactivex.functions.Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) {
                subscribed.set(true);
            }
        }));
        Assert.assertEquals(4, o.blockingSingle().intValue());
        Assert.assertFalse(subscribed.get());
    }

    @Test
    public void testSwitchWhenEmpty() throws Exception {
        final Observable<Integer> o = Observable.<Integer>empty().switchIfEmpty(Observable.fromIterable(Arrays.asList(42)));
        Assert.assertEquals(42, o.blockingSingle().intValue());
    }

    @Test
    public void testSwitchTriggerUnsubscribe() throws Exception {
        final Disposable d = Disposables.empty();
        Observable<Long> withProducer = Observable.unsafeCreate(new ObservableSource<Long>() {
            @Override
            public void subscribe(final Observer<? super Long> observer) {
                observer.onSubscribe(d);
                observer.onNext(42L);
            }
        });
        Observable.<Long>empty().switchIfEmpty(withProducer).lift(new ObservableOperator<Long, Long>() {
            @Override
            public Observer<? super Long> apply(final Observer<? super Long> child) {
                return new io.reactivex.observers.DefaultObserver<Long>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        cancel();
                    }
                };
            }
        }).subscribe();
        Assert.assertTrue(d.isDisposed());
        // FIXME no longer assertable
        // assertTrue(sub.isUnsubscribed());
    }

    @Test
    public void testSwitchShouldTriggerUnsubscribe() {
        final Disposable d = Disposables.empty();
        Observable.unsafeCreate(new ObservableSource<Long>() {
            @Override
            public void subscribe(final Observer<? super Long> observer) {
                observer.onSubscribe(d);
                observer.onComplete();
            }
        }).switchIfEmpty(Observable.<Long>never()).subscribe();
        Assert.assertTrue(d.isDisposed());
    }
}

