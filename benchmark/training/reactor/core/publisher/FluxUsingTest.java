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


import FluxUsing.UsingConditionalSubscriber;
import FluxUsing.UsingFuseableSubscriber;
import FluxUsing.UsingSubscriber;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.test.MockUtils;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;


public class FluxUsingTest extends FluxOperatorTest<String, String> {
    @Test(expected = NullPointerException.class)
    public void resourceSupplierNull() {
        Flux.using(null, ( r) -> Flux.empty(), ( r) -> {
        }, false);
    }

    @Test(expected = NullPointerException.class)
    public void sourceFactoryNull() {
        Flux.using(() -> 1, null, ( r) -> {
        }, false);
    }

    @Test(expected = NullPointerException.class)
    public void resourceCleanupNull() {
        Flux.using(() -> 1, ( r) -> Flux.empty(), null, false);
    }

    @Test
    public void normal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicInteger cleanup = new AtomicInteger();
        Flux.using(() -> 1, ( r) -> Flux.range(r, 10), cleanup::set, false).subscribe(ts);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete().assertNoError();
        Assert.assertEquals(1, cleanup.get());
    }

    @Test
    public void normalEager() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicInteger cleanup = new AtomicInteger();
        Flux.using(() -> 1, ( r) -> Flux.range(r, 10), cleanup::set).subscribe(ts);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete().assertNoError();
        Assert.assertEquals(1, cleanup.get());
    }

    @Test
    public void checkNonEager() {
        checkCleanupExecutionTime(false, false);
    }

    @Test
    public void checkEager() {
        checkCleanupExecutionTime(true, false);
    }

    @Test
    public void checkErrorNonEager() {
        checkCleanupExecutionTime(false, true);
    }

    @Test
    public void checkErrorEager() {
        checkCleanupExecutionTime(true, true);
    }

    @Test
    public void resourceThrowsEager() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        AtomicInteger cleanup = new AtomicInteger();
        Flux.using(() -> {
            throw new RuntimeException("forced failure");
        }, ( r) -> Flux.range(1, 10), cleanup::set, false).subscribe(ts);
        ts.assertNoValues().assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
        Assert.assertEquals(0, cleanup.get());
    }

    @Test
    public void factoryThrowsEager() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        AtomicInteger cleanup = new AtomicInteger();
        Flux.using(() -> 1, ( r) -> {
            throw new RuntimeException("forced failure");
        }, cleanup::set, false).subscribe(ts);
        ts.assertNoValues().assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
        Assert.assertEquals(1, cleanup.get());
    }

    @Test
    public void factoryReturnsNull() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        AtomicInteger cleanup = new AtomicInteger();
        Flux.<Integer, Integer>using(() -> 1, ( r) -> null, cleanup::set, false).subscribe(ts);
        ts.assertNoValues().assertNotComplete().assertError(NullPointerException.class);
        Assert.assertEquals(1, cleanup.get());
    }

    @Test
    public void subscriberCancels() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicInteger cleanup = new AtomicInteger();
        DirectProcessor<Integer> tp = DirectProcessor.create();
        Flux.using(() -> 1, ( r) -> tp, cleanup::set, true).subscribe(ts);
        Assert.assertTrue("No subscriber?", tp.hasDownstreams());
        tp.onNext(1);
        ts.assertValues(1).assertNotComplete().assertNoError();
        ts.cancel();
        tp.onNext(2);
        ts.assertValues(1).assertNotComplete().assertNoError();
        Assert.assertFalse("Has subscriber?", tp.hasDownstreams());
        Assert.assertEquals(1, cleanup.get());
    }

    @Test
    public void sourceFactoryAndResourceCleanupThrow() {
        RuntimeException sourceEx = new IllegalStateException("sourceFactory");
        RuntimeException cleanupEx = new IllegalStateException("resourceCleanup");
        Condition<? super Throwable> suppressingFactory = new Condition(( e) -> {
            Throwable[] suppressed = e.getSuppressed();
            return ((suppressed != null) && (suppressed.length == 1)) && ((suppressed[0]) == sourceEx);
        }, "suppressing <%s>", sourceEx);
        Flux<String> test = new FluxUsing(() -> "foo", ( o) -> {
            throw sourceEx;
        }, ( s) -> {
            throw cleanupEx;
        }, false);
        StepVerifier.create(test).verifyErrorSatisfies(( e) -> assertThat(e).hasMessage("resourceCleanup").is(suppressingFactory));
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        UsingSubscriber<Integer, String> test = new FluxUsing.UsingSubscriber<>(actual, ( s) -> {
        }, "", true);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanConditionalSubscriber() {
        @SuppressWarnings("unchecked")
        Fuseable.ConditionalSubscriber<Integer> actual = Mockito.mock(MockUtils.TestScannableConditionalSubscriber.class);
        UsingConditionalSubscriber<Integer, String> test = new FluxUsing.UsingConditionalSubscriber<>(actual, ( s) -> {
        }, "", true);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanFuseableSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        UsingFuseableSubscriber<Integer, String> test = new FluxUsing.UsingFuseableSubscriber<>(actual, ( s) -> {
        }, "", true);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }
}

