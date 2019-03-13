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


import io.reactivex.Flowable;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class FlowableDoOnSubscribeTest {
    @Test
    public void testDoOnSubscribe() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> f = Flowable.just(1).doOnSubscribe(new io.reactivex.functions.Consumer<Subscription>() {
            @Override
            public void accept(Subscription s) {
                count.incrementAndGet();
            }
        });
        f.subscribe();
        f.subscribe();
        f.subscribe();
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void testDoOnSubscribe2() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        Flowable<Integer> f = Flowable.just(1).doOnSubscribe(new io.reactivex.functions.Consumer<Subscription>() {
            @Override
            public void accept(Subscription s) {
                count.incrementAndGet();
            }
        }).take(1).doOnSubscribe(new io.reactivex.functions.Consumer<Subscription>() {
            @Override
            public void accept(Subscription s) {
                count.incrementAndGet();
            }
        });
        f.subscribe();
        Assert.assertEquals(2, count.get());
    }

    @Test
    public void testDoOnUnSubscribeWorksWithRefCount() throws Exception {
        final AtomicInteger onSubscribed = new AtomicInteger();
        final AtomicInteger countBefore = new AtomicInteger();
        final AtomicInteger countAfter = new AtomicInteger();
        final AtomicReference<Subscriber<? super Integer>> sref = new AtomicReference<Subscriber<? super Integer>>();
        Flowable<Integer> f = Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                s.onSubscribe(new BooleanSubscription());
                onSubscribed.incrementAndGet();
                sref.set(s);
            }
        }).doOnSubscribe(new io.reactivex.functions.Consumer<Subscription>() {
            @Override
            public void accept(Subscription s) {
                countBefore.incrementAndGet();
            }
        }).publish().refCount().doOnSubscribe(new io.reactivex.functions.Consumer<Subscription>() {
            @Override
            public void accept(Subscription s) {
                countAfter.incrementAndGet();
            }
        });
        f.subscribe();
        f.subscribe();
        f.subscribe();
        Assert.assertEquals(1, countBefore.get());
        Assert.assertEquals(1, onSubscribed.get());
        Assert.assertEquals(3, countAfter.get());
        sref.get().onComplete();
        f.subscribe();
        f.subscribe();
        f.subscribe();
        Assert.assertEquals(2, countBefore.get());
        Assert.assertEquals(2, onSubscribed.get());
        Assert.assertEquals(6, countAfter.get());
    }
}

