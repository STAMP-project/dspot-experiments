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
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;


public class LambdaMonoSubscriberTest {
    @Test
    public void consumeOnSubscriptionNotifiesError() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaMonoSubscriber<String> tested = new LambdaMonoSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, ( subscription) -> {
            throw new IllegalArgumentException();
        });
        LambdaMonoSubscriberTest.TestSubscription testSubscription = new LambdaMonoSubscriberTest.TestSubscription();
        // the error is expected to be propagated through onError
        tested.onSubscribe(testSubscription);
        Assert.assertThat("unexpected exception in onError", errorHolder.get(), is(instanceOf(IllegalArgumentException.class)));
        Assert.assertThat("subscription has not been cancelled", testSubscription.isCancelled, is(true));
        Assert.assertThat("unexpected request", testSubscription.requested, is(equalTo((-1L))));
    }

    @Test
    public void consumeOnSubscriptionThrowsFatal() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaMonoSubscriber<String> tested = new LambdaMonoSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, ( subscription) -> {
            throw new OutOfMemoryError();
        });
        LambdaMonoSubscriberTest.TestSubscription testSubscription = new LambdaMonoSubscriberTest.TestSubscription();
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
        LambdaMonoSubscriber<String> tested = new LambdaMonoSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, ( s) -> {
            subscriptionHolder.set(s);
            s.request(32);
        });
        LambdaMonoSubscriberTest.TestSubscription testSubscription = new LambdaMonoSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        Assert.assertThat("unexpected onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled", testSubscription.isCancelled, is(not(true)));
        Assert.assertThat("didn't consume the subscription", subscriptionHolder.get(), is(equalTo(testSubscription)));
        Assert.assertThat("didn't request the subscription", testSubscription.requested, is(equalTo(32L)));
    }

    @Test
    public void noSubscriptionConsumerTriggersRequestOfMax() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaMonoSubscriber<String> tested = new LambdaMonoSubscriber(( value) -> {
        }, errorHolder::set, () -> {
        }, null);// defaults to initial request of max

        LambdaMonoSubscriberTest.TestSubscription testSubscription = new LambdaMonoSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        Assert.assertThat("unexpected onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled", testSubscription.isCancelled, is(not(true)));
        Assert.assertThat("didn't request the subscription", testSubscription.requested, is(not(equalTo((-1L)))));
        Assert.assertThat("didn't request max", testSubscription.requested, is(equalTo(Long.MAX_VALUE)));
    }

    @Test
    public void onNextConsumerExceptionBubblesUpDoesntTriggerCancellation() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaMonoSubscriber<String> tested = new LambdaMonoSubscriber(( value) -> {
            throw new IllegalArgumentException();
        }, errorHolder::set, () -> {
        }, null);
        LambdaMonoSubscriberTest.TestSubscription testSubscription = new LambdaMonoSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        // as Mono is single-value, it cancels early on onNext. this leads to an exception
        // during onNext to be bubbled up as a BubbledException, not propagated through onNext
        try {
            tested.onNext("foo");
            Assert.fail("Expected a bubbling Exception");
        } catch (RuntimeException e) {
            Assert.assertThat("Expected a bubbling Exception", e.getClass().getName(), containsString("BubblingException"));
            Assert.assertThat("Expected cause to be the IllegalArgumentException", e.getCause(), is(instanceOf(IllegalArgumentException.class)));
        }
        Assert.assertThat("unexpected exception in onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled", testSubscription.isCancelled, is(false));
    }

    @Test
    public void onNextConsumerFatalDoesntTriggerCancellation() {
        AtomicReference<Throwable> errorHolder = new AtomicReference<>(null);
        LambdaMonoSubscriber<String> tested = new LambdaMonoSubscriber(( value) -> {
            throw new OutOfMemoryError();
        }, errorHolder::set, () -> {
        }, null);
        LambdaMonoSubscriberTest.TestSubscription testSubscription = new LambdaMonoSubscriberTest.TestSubscription();
        tested.onSubscribe(testSubscription);
        // the error is expected to be thrown as it is fatal
        try {
            tested.onNext("foo");
            Assert.fail("Expected OutOfMemoryError to be thrown");
        } catch (OutOfMemoryError e) {
            // expected
        }
        Assert.assertThat("unexpected onError", errorHolder.get(), is(nullValue()));
        Assert.assertThat("subscription has been cancelled", testSubscription.isCancelled, is(false));
    }

    @Test
    public void emptyMonoState() {
        Assert.assertTrue(Mono.fromDirect(( s) -> {
            assertTrue((s instanceof LambdaMonoSubscriber));
            LambdaMonoSubscriber<?> bfs = ((LambdaMonoSubscriber<?>) (s));
            assertTrue(((bfs.scan(Scannable.Attr.PREFETCH)) == Integer.MAX_VALUE));
            assertFalse(bfs.scan(Scannable.Attr.TERMINATED));
            bfs.onSubscribe(Operators.emptySubscription());
            bfs.onSubscribe(Operators.emptySubscription());// noop

            s.onComplete();
            assertTrue(bfs.scan(Scannable.Attr.TERMINATED));
            bfs.dispose();
            bfs.dispose();
        }).subscribe(( s) -> {
        }, null, () -> {
        }).isDisposed());
        Assert.assertFalse(Mono.never().subscribe(null, null, () -> {
        }).isDisposed());
    }

    @Test
    public void errorMonoState() {
        Hooks.onErrorDropped(( e) -> assertTrue(e.getMessage().equals("test2")));
        Hooks.onNextDropped(( d) -> assertTrue(d.equals("test2")));
        try {
            Mono.fromDirect(( s) -> {
                assertTrue((s instanceof LambdaMonoSubscriber));
                LambdaMonoSubscriber<?> bfs = ((LambdaMonoSubscriber<?>) (s));
                Operators.error(s, new Exception("test"));
                s.onComplete();
                s.onError(new Exception("test2"));
                s.onNext("test2");
                assertTrue(bfs.scan(Scannable.Attr.TERMINATED));
                bfs.dispose();
            }).subscribe(( s) -> {
            }, ( e) -> {
            }, () -> {
            });
        } finally {
            Hooks.resetOnErrorDropped();
            Hooks.resetOnNextDropped();
        }
    }

    @Test
    public void completeHookErrorDropped() {
        Hooks.onErrorDropped(( e) -> assertTrue(e.getMessage().equals("complete")));
        try {
            Mono.just("foo").subscribe(( v) -> {
            }, ( e) -> {
            }, () -> {
                throw new IllegalStateException("complete");
            });
        } finally {
            Hooks.resetOnErrorDropped();
        }
    }

    @Test
    public void noErrorHookThrowsCallbackNotImplemented() {
        RuntimeException boom = new IllegalArgumentException("boom");
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> Mono.error(boom).subscribe(( v) -> {
        })).withCause(boom).hasToString("reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.IllegalArgumentException: boom");
    }

    @Test
    public void testCancel() {
        AtomicLong cancelCount = new AtomicLong();
        Mono.delay(Duration.ofMillis(500)).doOnCancel(cancelCount::incrementAndGet).subscribe(( v) -> {
        }).dispose();
        Assertions.assertThat(cancelCount.get()).isEqualTo(1);
    }

    @Test
    public void scan() {
        LambdaMonoSubscriber<String> test = new LambdaMonoSubscriber(null, null, null, null);
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

