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


import FluxBufferPredicate.BufferPredicateSubscriber;
import FluxBufferPredicate.Mode;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.CAPACITY;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;


public class FluxBufferPredicateTest {
    @Test
    @SuppressWarnings("unchecked")
    public void normalUntil() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntil = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, Flux.listSupplier(), Mode.UNTIL);
        StepVerifier.create(bufferUntil).expectSubscription().expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(3)).expectNext(Arrays.asList(1, 2, 3)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(4)).then(() -> sp1.onNext(5)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(6)).expectNext(Arrays.asList(4, 5, 6)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(7)).then(() -> sp1.onNext(8)).then(sp1::onComplete).expectNext(Arrays.asList(7, 8)).expectComplete().verify();
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCompletionBeforeLastBoundaryBufferEmitted() {
        Flux<Integer> source = Flux.just(1, 2);
        FluxBufferPredicate<Integer, List<Integer>> bufferUntil = new FluxBufferPredicate(source, ( i) -> i >= 3, Flux.listSupplier(), Mode.UNTIL);
        FluxBufferPredicate<Integer, List<Integer>> bufferUntilOther = new FluxBufferPredicate(source, ( i) -> i >= 3, Flux.listSupplier(), Mode.UNTIL_CUT_BEFORE);
        FluxBufferPredicate<Integer, List<Integer>> bufferWhile = new FluxBufferPredicate(source, ( i) -> i < 3, Flux.listSupplier(), Mode.WHILE);
        StepVerifier.create(bufferUntil).expectNext(Arrays.asList(1, 2)).expectComplete().verify();
        StepVerifier.create(bufferUntilOther).expectNext(Arrays.asList(1, 2)).expectComplete().verify();
        StepVerifier.create(bufferWhile).expectNext(Arrays.asList(1, 2)).expectComplete().verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mainErrorUntil() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntil = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, Flux.listSupplier(), Mode.UNTIL);
        StepVerifier.create(bufferUntil).expectSubscription().then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).expectNext(Arrays.asList(1, 2, 3)).then(() -> sp1.onNext(4)).then(() -> sp1.onError(new RuntimeException("forced failure"))).expectErrorMessage("forced failure").verify();
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void predicateErrorUntil() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntil = new FluxBufferPredicate(sp1, ( i) -> {
            if (i == 5)
                throw new IllegalStateException("predicate failure");

            return (i % 3) == 0;
        }, Flux.listSupplier(), Mode.UNTIL);
        StepVerifier.create(bufferUntil).expectSubscription().then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).expectNext(Arrays.asList(1, 2, 3)).then(() -> sp1.onNext(4)).then(() -> sp1.onNext(5)).expectErrorMessage("predicate failure").verify();
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void normalUntilOther() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntilOther = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, Flux.listSupplier(), Mode.UNTIL_CUT_BEFORE);
        StepVerifier.create(bufferUntilOther).expectSubscription().expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(3)).expectNext(Arrays.asList(1, 2)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(4)).then(() -> sp1.onNext(5)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(6)).expectNext(Arrays.asList(3, 4, 5)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(7)).then(() -> sp1.onNext(8)).then(sp1::onComplete).expectNext(Arrays.asList(6, 7, 8)).expectComplete().verify();
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mainErrorUntilOther() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntilOther = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, Flux.listSupplier(), Mode.UNTIL_CUT_BEFORE);
        StepVerifier.create(bufferUntilOther).expectSubscription().then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).expectNext(Arrays.asList(1, 2)).then(() -> sp1.onNext(4)).then(() -> sp1.onError(new RuntimeException("forced failure"))).expectErrorMessage("forced failure").verify();
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void predicateErrorUntilOther() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntilOther = new FluxBufferPredicate(sp1, ( i) -> {
            if (i == 5)
                throw new IllegalStateException("predicate failure");

            return (i % 3) == 0;
        }, Flux.listSupplier(), Mode.UNTIL_CUT_BEFORE);
        StepVerifier.create(bufferUntilOther).expectSubscription().then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).expectNext(Arrays.asList(1, 2)).then(() -> sp1.onNext(4)).then(() -> sp1.onNext(5)).expectErrorMessage("predicate failure").verify();
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void normalWhile() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferWhile = new FluxBufferPredicate(sp1, ( i) -> (i % 3) != 0, Flux.listSupplier(), Mode.WHILE);
        StepVerifier.create(bufferWhile).expectSubscription().expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(3)).expectNext(Arrays.asList(1, 2)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(4)).then(() -> sp1.onNext(5)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(6)).expectNext(Arrays.asList(4, 5)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(7)).then(() -> sp1.onNext(8)).then(sp1::onComplete).expectNext(Arrays.asList(7, 8)).expectComplete().verify();
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void normalWhileDoesntInitiallyMatch() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferWhile = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, Flux.listSupplier(), Mode.WHILE);
        // completion triggers the buffer emit
        // emission of 7 triggers the buffer emit
        // emission of 4 triggers the buffer emit
        StepVerifier.create(bufferWhile).expectSubscription().expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(4)).expectNext(Arrays.asList(3)).then(() -> sp1.onNext(5)).then(() -> sp1.onNext(6)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(7)).expectNext(Arrays.asList(6)).then(() -> sp1.onNext(8)).then(() -> sp1.onNext(9)).expectNoEvent(Duration.ofMillis(10)).then(sp1::onComplete).expectNext(Collections.singletonList(9)).expectComplete().verify(Duration.ofSeconds(1));
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void normalWhileDoesntMatch() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferWhile = new FluxBufferPredicate(sp1, ( i) -> i > 4, Flux.listSupplier(), Mode.WHILE);
        StepVerifier.create(bufferWhile).expectSubscription().expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).then(() -> sp1.onNext(4)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).then(() -> sp1.onNext(4)).expectNoEvent(Duration.ofMillis(10)).then(sp1::onComplete).expectComplete().verify(Duration.ofSeconds(1));
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mainErrorWhile() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferWhile = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, Flux.listSupplier(), Mode.WHILE);
        StepVerifier.create(bufferWhile).expectSubscription().then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).then(() -> sp1.onNext(4)).expectNext(Arrays.asList(3)).then(() -> sp1.onError(new RuntimeException("forced failure"))).expectErrorMessage("forced failure").verify(Duration.ofMillis(100));
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void predicateErrorWhile() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferWhile = new FluxBufferPredicate(sp1, ( i) -> {
            if (i == 3)
                return true;

            if (i == 5)
                throw new IllegalStateException("predicate failure");

            return false;
        }, Flux.listSupplier(), Mode.WHILE);
        // fails
        // ignored, emits buffer
        // buffered
        // ignored
        // ignored
        StepVerifier.create(bufferWhile).expectSubscription().then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).then(() -> sp1.onNext(4)).expectNext(Arrays.asList(3)).then(() -> sp1.onNext(5)).expectErrorMessage("predicate failure").verify(Duration.ofMillis(100));
        Assert.assertFalse(sp1.hasDownstreams());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void bufferSupplierThrows() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntil = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, () -> {
            throw new RuntimeException("supplier failure");
        }, Mode.UNTIL);
        Assert.assertFalse("sp1 has subscribers?", sp1.hasDownstreams());
        StepVerifier.create(bufferUntil).expectErrorMessage("supplier failure").verify();
    }

    @Test
    public void bufferSupplierThrowsLater() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        int[] count = new int[]{ 1 };
        FluxBufferPredicate<Integer, List<Integer>> bufferUntil = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, () -> {
            if (((count[0])--) > 0) {
                return new ArrayList<>();
            }
            throw new RuntimeException("supplier failure");
        }, Mode.UNTIL);
        Assert.assertFalse("sp1 has subscribers?", sp1.hasDownstreams());
        StepVerifier.create(bufferUntil).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(3)).expectErrorMessage("supplier failure").verify();
    }

    @Test
    public void bufferSupplierReturnsNull() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxBufferPredicate<Integer, List<Integer>> bufferUntil = new FluxBufferPredicate(sp1, ( i) -> (i % 3) == 0, () -> null, Mode.UNTIL);
        Assert.assertFalse("sp1 has subscribers?", sp1.hasDownstreams());
        StepVerifier.create(bufferUntil).then(() -> sp1.onNext(1)).expectErrorMatches(( t) -> (t instanceof NullPointerException) && ("The bufferSupplier returned a null initial buffer".equals(t.getMessage()))).verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void multipleTriggersOfEmptyBufferKeepInitialBuffer() {
        // this is best demonstrated with bufferWhile:
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        LongAdder bufferCount = new LongAdder();
        Supplier<List<Integer>> bufferSupplier = () -> {
            bufferCount.increment();
            return new ArrayList<>();
        };
        FluxBufferPredicate<Integer, List<Integer>> bufferWhile = new FluxBufferPredicate(sp1, ( i) -> i >= 10, bufferSupplier, Mode.WHILE);
        StepVerifier.create(bufferWhile).then(() -> assertThat(bufferCount.intValue(), is(1))).then(() -> sp1.onNext(1)).then(() -> sp1.onNext(2)).then(() -> sp1.onNext(3)).then(() -> assertThat(bufferCount.intValue(), is(1))).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(10)).then(() -> sp1.onNext(11)).then(sp1::onComplete).expectNext(Arrays.asList(10, 11)).then(() -> assertThat(bufferCount.intValue(), is(1))).expectComplete().verify();
        Assert.assertThat(bufferCount.intValue(), Matchers.is(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestBounded() {
        LongAdder requestCallCount = new LongAdder();
        LongAdder totalRequest = new LongAdder();
        Flux<Integer> source = Flux.range(1, 10).hide().doOnRequest(( r) -> requestCallCount.increment()).doOnRequest(totalRequest::add);
        // start with a request for 1 buffer
        StepVerifier.withVirtualTime(() -> new FluxBufferPredicate<>(source, ( i) -> (i % 3) == 0, Flux.listSupplier(), FluxBufferPredicate.Mode.UNTIL), 1).expectSubscription().expectNext(Arrays.asList(1, 2, 3)).expectNoEvent(Duration.ofSeconds(1)).thenRequest(2).expectNext(Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)).expectNoEvent(Duration.ofSeconds(1)).thenRequest(3).expectNext(Collections.singletonList(10)).expectComplete().verify();
        Assert.assertThat(requestCallCount.intValue(), Matchers.is(11));// 10 elements then the completion

        Assert.assertThat(totalRequest.longValue(), Matchers.is(11L));// ignores the main requests

    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestBoundedSeveralInitial() {
        LongAdder requestCallCount = new LongAdder();
        LongAdder totalRequest = new LongAdder();
        Flux<Integer> source = Flux.range(1, 10).hide().doOnRequest(( r) -> requestCallCount.increment()).doOnRequest(totalRequest::add);
        // start with a request for 2 buffers
        StepVerifier.withVirtualTime(() -> new FluxBufferPredicate<>(source, ( i) -> (i % 3) == 0, Flux.listSupplier(), FluxBufferPredicate.Mode.UNTIL), 2).expectSubscription().expectNext(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)).expectNoEvent(Duration.ofSeconds(1)).thenRequest(2).expectNext(Arrays.asList(7, 8, 9)).expectNext(Collections.singletonList(10)).expectComplete().verify(Duration.ofSeconds(1));
        Assert.assertThat(requestCallCount.intValue(), Matchers.is(11));// 10 elements then the completion

        Assert.assertThat(totalRequest.longValue(), Matchers.is(11L));// ignores the main requests

    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestRemainingBuffersAfterBufferEmission() {
        LongAdder requestCallCount = new LongAdder();
        LongAdder totalRequest = new LongAdder();
        Flux<Integer> source = Flux.range(1, 10).hide().doOnRequest(( r) -> requestCallCount.increment()).doOnRequest(totalRequest::add);
        // start with a request for 3 buffers
        StepVerifier.withVirtualTime(() -> new FluxBufferPredicate<>(source, ( i) -> (i % 3) == 0, Flux.listSupplier(), FluxBufferPredicate.Mode.UNTIL), 3).expectSubscription().expectNext(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)).expectNoEvent(Duration.ofSeconds(1)).thenRequest(1).expectNext(Collections.singletonList(10)).expectComplete().verify(Duration.ofSeconds(1));
        Assert.assertThat(requestCallCount.intValue(), Matchers.is(11));// 10 elements then the completion

        Assert.assertThat(totalRequest.longValue(), Matchers.is(11L));// ignores the main requests

    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestUnboundedFromStartRequestsSourceOnce() {
        LongAdder requestCallCount = new LongAdder();
        LongAdder totalRequest = new LongAdder();
        Flux<Integer> source = Flux.range(1, 10).hide().doOnRequest(( r) -> requestCallCount.increment()).doOnRequest(totalRequest::add);
        // start with an unbounded request
        StepVerifier.withVirtualTime(() -> new FluxBufferPredicate<>(source, ( i) -> (i % 3) == 0, Flux.listSupplier(), FluxBufferPredicate.Mode.UNTIL)).expectSubscription().expectNext(Arrays.asList(1, 2, 3)).expectNext(Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)).expectNext(Collections.singletonList(10)).expectComplete().verify();
        Assert.assertThat(requestCallCount.intValue(), Matchers.is(1));
        Assert.assertThat(totalRequest.longValue(), Matchers.is(Long.MAX_VALUE));// also unbounded

    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestSwitchingToMaxRequestsSourceOnlyOnceMore() {
        LongAdder requestCallCount = new LongAdder();
        LongAdder totalRequest = new LongAdder();
        Flux<Integer> source = Flux.range(1, 10).hide().doOnRequest(( r) -> requestCallCount.increment()).doOnRequest(( r) -> totalRequest.add((r < Long.MAX_VALUE ? r : 1000L)));
        // start with a single request
        StepVerifier.withVirtualTime(() -> new FluxBufferPredicate<>(source, ( i) -> (i % 3) == 0, Flux.listSupplier(), FluxBufferPredicate.Mode.UNTIL), 1).expectSubscription().expectNext(Arrays.asList(1, 2, 3)).expectNoEvent(Duration.ofSeconds(1)).then(() -> assertThat(requestCallCount.intValue(), is(3))).thenRequest(Long.MAX_VALUE).expectNext(Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)).expectNext(Collections.singletonList(10)).expectComplete().verify();
        Assert.assertThat(requestCallCount.intValue(), Matchers.is(4));
        Assert.assertThat(totalRequest.longValue(), Matchers.is(1003L));// the switch to unbounded is translated to 1000

    }

    @Test
    @SuppressWarnings("unchecked")
    public void requestBoundedConditionalFusesDemands() {
        LongAdder requestCallCount = new LongAdder();
        LongAdder totalRequest = new LongAdder();
        Flux<Integer> source = Flux.range(1, 10).doOnRequest(( r) -> requestCallCount.increment()).doOnRequest(totalRequest::add);
        // .expectNoEvent(Duration.ofSeconds(1))
        StepVerifier.withVirtualTime(() -> new FluxBufferPredicate<>(source, ( i) -> (i % 3) == 0, Flux.listSupplier(), FluxBufferPredicate.Mode.UNTIL), 1).expectSubscription().expectNext(Arrays.asList(1, 2, 3)).thenRequest(1).expectNext(Arrays.asList(4, 5, 6)).thenRequest(1).expectNext(Arrays.asList(7, 8, 9)).thenRequest(1).expectNext(Collections.singletonList(10)).expectComplete().verify();
        // despite the 1 by 1 demand, only 1 fused request per buffer, 4 buffers
        Assert.assertThat(requestCallCount.intValue(), Matchers.is(4));
        Assert.assertThat(totalRequest.longValue(), Matchers.is(4L));// ignores the main requests

    }

    @Test
    public void testBufferPredicateUntilIncludesBoundaryLast() {
        String[] colorSeparated = new String[]{ "red", "green", "blue", "#", "green", "green", "#", "blue", "cyan" };
        Flux<List<String>> colors = Flux.fromArray(colorSeparated).bufferUntil(( val) -> val.equals("#")).log();
        StepVerifier.create(colors).consumeNextWith(( l1) -> Assert.assertThat(l1, contains("red", "green", "blue", "#"))).consumeNextWith(( l2) -> Assert.assertThat(l2, contains("green", "green", "#"))).consumeNextWith(( l3) -> Assert.assertThat(l3, contains("blue", "cyan"))).expectComplete().verify();
    }

    @Test
    public void testBufferPredicateUntilIncludesBoundaryLastAfter() {
        String[] colorSeparated = new String[]{ "red", "green", "blue", "#", "green", "green", "#", "blue", "cyan" };
        Flux<List<String>> colors = Flux.fromArray(colorSeparated).bufferUntil(( val) -> val.equals("#"), false).log();
        StepVerifier.create(colors).consumeNextWith(( l1) -> Assert.assertThat(l1, contains("red", "green", "blue", "#"))).consumeNextWith(( l2) -> Assert.assertThat(l2, contains("green", "green", "#"))).consumeNextWith(( l3) -> Assert.assertThat(l3, contains("blue", "cyan"))).expectComplete().verify();
    }

    @Test
    public void testBufferPredicateUntilCutBeforeIncludesBoundaryFirst() {
        String[] colorSeparated = new String[]{ "red", "green", "blue", "#", "green", "green", "#", "blue", "cyan" };
        Flux<List<String>> colors = Flux.fromArray(colorSeparated).bufferUntil(( val) -> val.equals("#"), true).log();
        StepVerifier.create(colors).thenRequest(1).consumeNextWith(( l1) -> Assert.assertThat(l1, contains("red", "green", "blue"))).consumeNextWith(( l2) -> Assert.assertThat(l2, contains("#", "green", "green"))).consumeNextWith(( l3) -> Assert.assertThat(l3, contains("#", "blue", "cyan"))).expectComplete().verify();
    }

    @Test
    public void testBufferPredicateWhileDoesntIncludeBoundary() {
        String[] colorSeparated = new String[]{ "red", "green", "blue", "#", "green", "green", "#", "blue", "cyan" };
        Flux<List<String>> colors = Flux.fromArray(colorSeparated).bufferWhile(( val) -> !(val.equals("#"))).log();
        StepVerifier.create(colors).consumeNextWith(( l1) -> Assert.assertThat(l1, contains("red", "green", "blue"))).consumeNextWith(( l2) -> Assert.assertThat(l2, contains("green", "green"))).consumeNextWith(( l3) -> Assert.assertThat(l3, contains("blue", "cyan"))).expectComplete().verify();
    }

    @Test
    public void scanMain() {
        CoreSubscriber<? super List> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(100));
        List<String> initialBuffer = Arrays.asList("foo", "bar");
        BufferPredicateSubscriber<String, List<String>> test = new FluxBufferPredicate.BufferPredicateSubscriber<>(actual, initialBuffer, ArrayList::new, ( s) -> (s.length()) < 5, FluxBufferPredicate.Mode.WHILE);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(CAPACITY)).isEqualTo(2);
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(100L);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanMainCancelled() {
        CoreSubscriber<? super List> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(100));
        List<String> initialBuffer = Arrays.asList("foo", "bar");
        BufferPredicateSubscriber<String, List<String>> test = new FluxBufferPredicate.BufferPredicateSubscriber<>(actual, initialBuffer, ArrayList::new, ( s) -> (s.length()) < 5, FluxBufferPredicate.Mode.WHILE);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void discardOnCancel() {
        StepVerifier.create(Flux.just(1, 2, 3).concatWith(Mono.never()).bufferUntil(( i) -> i > 10)).thenAwait(Duration.ofMillis(10)).thenCancel().verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }

    @Test
    public void discardOnPredicateError() {
        StepVerifier.create(Flux.just(1, 2, 3).bufferUntil(( i) -> {
            if (i == 3)
                throw new IllegalStateException("boom");
            else
                return false;

        })).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }

    @Test
    public void discardOnError() {
        StepVerifier.create(Flux.just(1, 2, 3).concatWith(Mono.error(new IllegalStateException("boom"))).bufferUntil(( i) -> i > 10)).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }
}

