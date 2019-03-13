/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;


public class StrictSubscriberTest {
    @Test
    public void requestDelayed() {
        AtomicBoolean state = new AtomicBoolean();
        AtomicReference<Throwable> e = new AtomicReference<>();
        Flux.just(1).subscribe(new org.reactivestreams.Subscriber<Integer>() {
            boolean open;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
                open = true;
            }

            @Override
            public void onNext(Integer t) {
                state.set(open);
            }

            @Override
            public void onError(Throwable t) {
                e.set(t);
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertNull(("Error: " + (e.get())), e.get());
        Assert.assertTrue("Not open!", state.get());
    }

    @Test
    public void cancelDelayed() {
        AtomicBoolean state1 = new AtomicBoolean();
        AtomicBoolean state2 = new AtomicBoolean();
        AtomicReference<Throwable> e = new AtomicReference<>();
        DirectProcessor<Integer> sp = DirectProcessor.create();
        sp.doOnCancel(() -> state2.set(state1.get())).subscribe(new org.reactivestreams.Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.cancel();
                state1.set(true);
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable t) {
                e.set(t);
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertNull(("Error: " + (e.get())), e.get());
        Assert.assertTrue("Cancel executed before onSubscribe finished", state2.get());
        Assert.assertFalse("Has subscribers?!", sp.hasDownstreams());
    }

    @Test
    public void requestNotDelayed() {
        AtomicBoolean state = new AtomicBoolean();
        AtomicReference<Throwable> e = new AtomicReference<>();
        Flux.just(1).subscribe(new reactor.core.CoreSubscriber<Integer>() {
            boolean open;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
                open = true;
            }

            @Override
            public void onNext(Integer t) {
                state.set(open);
            }

            @Override
            public void onError(Throwable t) {
                e.set(t);
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertNull(("Error: " + (e.get())), e.get());
        Assert.assertFalse("Request delayed!", state.get());
    }

    @Test
    public void cancelNotDelayed() {
        AtomicBoolean state1 = new AtomicBoolean();
        AtomicBoolean state2 = new AtomicBoolean();
        AtomicReference<Throwable> e = new AtomicReference<>();
        Flux.just(1).doOnCancel(() -> state2.set(state1.get())).subscribe(new reactor.core.CoreSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.cancel();
                state1.set(true);
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable t) {
                e.set(t);
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertNull(("Error: " + (e.get())), e.get());
        Assert.assertFalse("Cancel executed before onSubscribe finished", state2.get());
    }

    @Test
    public void scanPostOnSubscribeSubscriber() {
        reactor.core.CoreSubscriber<String> s = new LambdaSubscriber(null, null, null, null);
        StrictSubscriber<String> test = new StrictSubscriber(s);
        assertThat(test.scan(ACTUAL)).isSameAs(s);
        assertThat(test.scan(PARENT)).isNull();
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0L);
        test.request(2);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(2L);
        Subscription parent = Operators.cancelledSubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

