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


import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.TERMINATED;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;


public class LambdaSubscriberTest {
    @Test
    public void consumeOnSubscriptionNotifiesError() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaSubscriber<String> tested = new LambdaSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, ( subscription) -> {
            throw new IllegalArgumentException();
        });
        LambdaSubscriberTest.TestSubscription testSubscription = new LambdaSubscriberTest.TestSubscription();
        // the error is expected to be propagated through onError
        tested.onSubscribe(testSubscription);
        Assert.assertThat("unexpected exception in onError", errorHolder.get(), is(instanceOf(IllegalArgumentException.class)));
        Assert.assertThat("subscription has not been cancelled", testSubscription.isCancelled, is(true));
        Assert.assertThat("unexpected request", testSubscription.requested, is(equalTo((-1L))));
    }

    @Test
    public void consumeOnSubscriptionThrowsFatal() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaSubscriber<String> tested = new LambdaSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, ( subscription) -> {
            throw new OutOfMemoryError();
        });
        LambdaSubscriberTest.TestSubscription testSubscription = new LambdaSubscriberTest.TestSubscription();
        // the error is expected to be thrown as it is fatal
        try {
            tested.onSubscribe(testSubscription);
            Assert.fail("Expected OutOfMemoryError to be thrown");
        } catch (OutOfMemoryError e) {
            // expected
        }
        Assert.assertThat("unexpected onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled despite fatal exception", testSubscription.isCancelled, is(not(true)));
        Assert.assertThat("unexpected request", testSubscription.requested, is(equalTo((-1L))));
    }

    @Test
    public void consumeOnSubscriptionReceivesSubscriptionAndRequests32() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        AtomicReference<Subscription> subscriptionHolder = new AtomicReference<>(null);
        LambdaSubscriber<String> tested = new LambdaSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, ( s) -> {
            subscriptionHolder.set(s);
            s.request(32);
        });
        LambdaSubscriberTest.TestSubscription testSubscription = new LambdaSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        Assert.assertThat("unexpected onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled", testSubscription.isCancelled, is(not(true)));
        Assert.assertThat("didn't consume the subscription", subscriptionHolder.get(), is(equalTo(testSubscription)));
        Assert.assertThat("didn't request the subscription", testSubscription.requested, is(equalTo(32L)));
    }

    @Test
    public void noSubscriptionConsumerTriggersRequestOfMax() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaSubscriber<String> tested = new LambdaSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, null);// defaults to initial request of max

        LambdaSubscriberTest.TestSubscription testSubscription = new LambdaSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        Assert.assertThat("unexpected onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled", testSubscription.isCancelled, is(not(true)));
        Assert.assertThat("didn't request the subscription", testSubscription.requested, is(not(equalTo((-1L)))));
        Assert.assertThat("didn't request max", testSubscription.requested, is(equalTo(Long.MAX_VALUE)));
    }

    @Test
    public void onNextConsumerExceptionTriggersCancellation() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaSubscriber<String> tested = new LambdaSubscriber(( value) -> {
            throw new IllegalArgumentException();
        }, errorHolder::set, () -> {
        }, null);
        LambdaSubscriberTest.TestSubscription testSubscription = new LambdaSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        // the error is expected to be propagated through onError
        tested.onNext("foo");
        Assert.assertThat("unexpected exception in onError", errorHolder.get(), is(instanceOf(IllegalArgumentException.class)));
        Assert.assertThat("subscription has not been cancelled", testSubscription.isCancelled, is(true));
    }

    @Test
    public void onNextConsumerFatalDoesntTriggerCancellation() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaSubscriber<String> tested = new LambdaSubscriber(( value) -> {
            throw new OutOfMemoryError();
        }, errorHolder::set, () -> {
        }, null);
        LambdaSubscriberTest.TestSubscription testSubscription = new LambdaSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        // the error is expected to be thrown as it is fatal
        try {
            tested.onNext("foo");
            Assert.fail("Expected OutOfMemoryError to be thrown");
        } catch (OutOfMemoryError e) {
            // expected
        }
        Assert.assertThat("unexpected onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled despite fatal exception", testSubscription.isCancelled, is(not(true)));
    }

    @Test
    public void scan() {
        LambdaSubscriber<String> test = new LambdaSubscriber(null, null, null, null);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.dispose();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    private static class TestSubscription implements Subscription {
        volatile boolean isCancelled = false;

        volatile long requested = -1L;

        @Override
        public void request(long n) {
            this.requested = n;
        }

        @Override
        public void cancel() {
            this.isCancelled = true;
        }
    }
}

