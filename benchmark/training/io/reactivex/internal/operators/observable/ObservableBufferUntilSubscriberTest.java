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


import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


public class ObservableBufferUntilSubscriberTest {
    @Test
    public void testIssue1677() throws InterruptedException {
        final AtomicLong counter = new AtomicLong();
        final Integer[] numbers = new Integer[5000];
        for (int i = 0; i < (numbers.length); i++) {
            numbers[i] = i + 1;
        }
        final int NITERS = 250;
        final CountDownLatch latch = new CountDownLatch(NITERS);
        for (int iters = 0; iters < NITERS; iters++) {
            final CountDownLatch innerLatch = new CountDownLatch(1);
            final PublishSubject<Void> s = PublishSubject.create();
            final AtomicBoolean completed = new AtomicBoolean();
            Observable.fromArray(numbers).takeUntil(s).window(50).flatMap(new Function<Observable<Integer>, Observable<Object>>() {
                @Override
                public Observable<Object> apply(Observable<Integer> integerObservable) {
                    return integerObservable.subscribeOn(Schedulers.computation()).map(new Function<Integer, Object>() {
                        @Override
                        public Object apply(Integer integer) {
                            if ((integer >= 5) && (completed.compareAndSet(false, true))) {
                                s.onComplete();
                            }
                            // do some work
                            Math.pow(Math.random(), Math.random());
                            return integer * 2;
                        }
                    });
                }
            }).toList().doOnSuccess(new Consumer<java.util.List<Object>>() {
                @Override
                public void accept(java.util.List<Object> integers) {
                    counter.incrementAndGet();
                    latch.countDown();
                    innerLatch.countDown();
                }
            }).subscribe();
            if (!(innerLatch.await(30, TimeUnit.SECONDS))) {
                Assert.fail(("Failed inner latch wait, iteration " + iters));
            }
        }
        if (!(latch.await(30, TimeUnit.SECONDS))) {
            Assert.fail((("Incomplete! Went through " + (latch.getCount())) + " iterations"));
        } else {
            Assert.assertEquals(NITERS, counter.get());
        }
    }
}

