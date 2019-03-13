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
package io.reactivex.flowable;


import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscriber;


public class FlowableErrorHandlingTests {
    /**
     * Test that an error from a user provided Observer.onNext
     * is handled and emitted to the onError.
     *
     * @throws InterruptedException
     * 		if the test is interrupted
     */
    @Test
    public void testOnNextError() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> caughtError = new AtomicReference<Throwable>();
        Flowable<Long> f = Flowable.interval(50, TimeUnit.MILLISECONDS);
        Subscriber<Long> subscriber = new io.reactivex.subscribers.DefaultSubscriber<Long>() {
            @Override
            public void onComplete() {
                System.out.println("completed");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(("error: " + e));
                caughtError.set(e);
                latch.countDown();
            }

            @Override
            public void onNext(Long args) {
                throw new RuntimeException("forced failure");
            }
        };
        f.safeSubscribe(subscriber);
        latch.await(2000, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(caughtError.get());
    }

    /**
     * Test that an error from a user provided Observer.onNext
     * is handled and emitted to the onError.
     * even when done across thread boundaries with observeOn
     *
     * @throws InterruptedException
     * 		if the test is interrupted
     */
    @Test
    public void testOnNextErrorAcrossThread() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> caughtError = new AtomicReference<Throwable>();
        Flowable<Long> f = Flowable.interval(50, TimeUnit.MILLISECONDS);
        Subscriber<Long> subscriber = new io.reactivex.subscribers.DefaultSubscriber<Long>() {
            @Override
            public void onComplete() {
                System.out.println("completed");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(("error: " + e));
                caughtError.set(e);
                latch.countDown();
            }

            @Override
            public void onNext(Long args) {
                throw new RuntimeException("forced failure");
            }
        };
        f.observeOn(Schedulers.newThread()).safeSubscribe(subscriber);
        latch.await(2000, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(caughtError.get());
    }
}

