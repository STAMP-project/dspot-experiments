/**
 * Copyright (c) 2011-2018 Pivotal Software Inc, All Rights Reserved.
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


import Disposable.Swap;
import Hooks.KEY_ON_DISCARD;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.FluxBufferPredicate.Mode;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.publisher.TestPublisher;
import reactor.util.concurrent.Queues;
import reactor.util.context.Context;


public class FluxWindowPredicateTest extends FluxOperatorTest<String, Flux<String>> {
    // see https://github.com/reactor/reactor-core/issues/1452
    @Test
    public void windowWhilePropagatingCancelToSource_disposeOuterFirst() {
        final AtomicBoolean beforeWindowWhileStageCancelled = new AtomicBoolean();
        final AtomicBoolean afterWindowWhileStageCancelled = new AtomicBoolean();
        TestPublisher<String> testPublisher = TestPublisher.create();
        final Flux<String> sourceFlux = testPublisher.flux().doOnCancel(() -> beforeWindowWhileStageCancelled.set(true));
        final AtomicInteger windowCounter = new AtomicInteger();
        final Disposable.Swap innerDisposable = Disposables.swap();
        final Disposable outerDisposable = sourceFlux.windowWhile(( s) -> !("#".equals(s))).doOnCancel(() -> afterWindowWhileStageCancelled.set(true)).subscribe(( next) -> {
            final int windowId = windowCounter.getAndIncrement();
            innerDisposable.update(next.subscribe());
        });
        testPublisher.next("1");
        // Dispose outer subscription; we should see cancellation at stage after windowWhile, but not before
        outerDisposable.dispose();
        assertThat(afterWindowWhileStageCancelled).as("afterWindowWhileStageCancelled cancelled when outer is disposed").isTrue();
        assertThat(beforeWindowWhileStageCancelled).as("beforeWindowWhileStageCancelled cancelled when outer is disposed").isFalse();
        // Dispose inner subscription; we should see cancellation propagates all the way up
        innerDisposable.dispose();
        assertThat(afterWindowWhileStageCancelled).as("afterWindowWhileStageCancelled cancelled when inner is disposed").isTrue();
        assertThat(beforeWindowWhileStageCancelled).as("beforeWindowWhileStageCancelled cancelled when inner is disposed").isTrue();
    }

    // see https://github.com/reactor/reactor-core/issues/1452
    @Test
    public void windowWhileNotPropagatingCancelToSource_disposeInnerFirst() {
        final AtomicBoolean beforeWindowWhileStageCancelled = new AtomicBoolean();
        final AtomicBoolean afterWindowWhileStageCancelled = new AtomicBoolean();
        final Flux<String> sourceFlux = Flux.<String>create(( fluxSink) -> fluxSink.next("0").next("#")).doOnCancel(() -> beforeWindowWhileStageCancelled.set(true));
        final AtomicInteger windowCounter = new AtomicInteger();
        final Disposable.Swap innerDisposable = Disposables.swap();
        final Disposable outerDisposable = sourceFlux.windowWhile(( s) -> !("#".equals(s))).doOnCancel(() -> afterWindowWhileStageCancelled.set(true)).subscribe(( next) -> {
            final int windowId = windowCounter.getAndIncrement();
            innerDisposable.update(next.subscribe());
        });
        // Dispose inner subscription, outer Flux at before/after the windowWhile stage should not be cancelled yet
        innerDisposable.dispose();
        assertThat(afterWindowWhileStageCancelled).as("afterWindowWhileStageCancelled cancelled when inner is disposed").isFalse();
        assertThat(beforeWindowWhileStageCancelled).as("beforeWindowWhileStageCancelled cancelled when inner is disposed").isFalse();
        // Dispose outer subscription; we should see cancellation propagates all the way up
        outerDisposable.dispose();
        assertThat(afterWindowWhileStageCancelled).as("afterWindowWhileStageCancelled cancelled when outer is disposed").isTrue();
        assertThat(beforeWindowWhileStageCancelled).as("beforeWindowWhileStageCancelled cancelled when outer is disposed").isTrue();
    }

    // see https://github.com/reactor/reactor-core/issues/1452
    @Test
    public void windowWhileNotPropagatingCancelToSource_withConcat() {
        // Similar to windowWhileNotPropagatingCancelToSource_disposeOuterFirst
        final AtomicBoolean beforeWindowWhileStageCancelled = new AtomicBoolean();
        final AtomicBoolean afterWindowWhileStageCancelled = new AtomicBoolean();
        final Flux<String> sourceFlux = Flux.<String>create(( fluxSink) -> fluxSink.next("0").next("#")).doOnCancel(() -> beforeWindowWhileStageCancelled.set(true));
        final Disposable disposable = sourceFlux.windowWhile(( s) -> !("#".equals(s))).doOnCancel(() -> afterWindowWhileStageCancelled.set(true)).as(Flux::concat).subscribe();
        disposable.dispose();
        assertThat(afterWindowWhileStageCancelled).as("afterWindowWhileStageCancelled cancelled").isTrue();
        assertThat(beforeWindowWhileStageCancelled).as("beforeWindowWhileStageCancelled cancelled").isTrue();
    }

    @Test
    public void windowWhileNoEmptyWindows() {
        Flux.just("ALPHA", "#", "BETA", "#").windowWhile(( s) -> !("#".equals(s))).flatMap(Flux::collectList).as(StepVerifier::create).assertNext(( w) -> assertThat(w).containsExactly("ALPHA")).assertNext(( w) -> assertThat(w).containsExactly("BETA")).verifyComplete();
    }

    @Test
    public void windowUntilNoEmptyWindows() {
        Flux.just("ALPHA", "#", "BETA", "#").windowUntil("#"::equals).flatMap(Flux::collectList).as(StepVerifier::create).assertNext(( w) -> assertThat(w).containsExactly("ALPHA", "#")).assertNext(( w) -> assertThat(w).containsExactly("BETA", "#")).verifyComplete();
    }

    @Test
    public void windowUntilCutBeforeNoEmptyWindows() {
        Flux.just("ALPHA", "#", "BETA", "#").windowUntil("#"::equals, true).flatMap(Flux::collectList).as(StepVerifier::create).assertNext(( w) -> assertThat(w).containsExactly("ALPHA")).assertNext(( w) -> assertThat(w).containsExactly("#", "BETA")).assertNext(( w) -> assertThat(w).containsExactly("#")).verifyComplete();
    }

    @Test
    public void windowWhileIntentionallyEmptyWindows() {
        Flux.just("ALPHA", "#", "BETA", "#", "#").windowWhile(( s) -> !("#".equals(s))).flatMap(Flux::collectList).as(StepVerifier::create).assertNext(( w) -> assertThat(w).containsExactly("ALPHA")).assertNext(( w) -> assertThat(w).containsExactly("BETA")).assertNext(( w) -> assertThat(w).isEmpty()).verifyComplete();
    }

    @Test
    public void windowUntilIntentionallyEmptyWindows() {
        Flux.just("ALPHA", "#", "BETA", "#", "#").windowUntil("#"::equals).flatMap(Flux::collectList).as(StepVerifier::create).assertNext(( w) -> assertThat(w).containsExactly("ALPHA", "#")).assertNext(( w) -> assertThat(w).containsExactly("BETA", "#")).assertNext(( w) -> assertThat(w).containsExactly("#")).verifyComplete();
    }

    @Test
    public void windowUntilCutBeforeIntentionallyEmptyWindows() {
        Flux.just("ALPHA", "#", "BETA", "#", "#").windowUntil("#"::equals, true).flatMap(Flux::collectList).as(StepVerifier::create).assertNext(( w) -> assertThat(w).containsExactly("ALPHA")).assertNext(( w) -> assertThat(w).containsExactly("#", "BETA")).assertNext(( w) -> assertThat(w).containsExactly("#")).assertNext(( w) -> assertThat(w).containsExactly("#")).verifyComplete();
    }

    @Test
    public void apiUntil() {
        StepVerifier.create(Flux.just("red", "green", "#", "orange", "blue", "#", "black", "white").windowUntil(( color) -> color.equals("#")).flatMap(Flux::materialize).map(( s) -> s.isOnComplete() ? "WINDOW CLOSED" : s.get())).expectNext("red", "green", "#", "WINDOW CLOSED").expectNext("orange", "blue", "#", "WINDOW CLOSED").expectNext("black", "white", "WINDOW CLOSED").verifyComplete();
    }

    @Test
    public void apiUntilCutAfter() {
        StepVerifier.create(Flux.just("red", "green", "#", "orange", "blue", "#", "black", "white").windowUntil(( color) -> color.equals("#"), false).flatMap(Flux::materialize).map(( s) -> s.isOnComplete() ? "WINDOW CLOSED" : s.get())).expectNext("red", "green", "#", "WINDOW CLOSED").expectNext("orange", "blue", "#", "WINDOW CLOSED").expectNext("black", "white", "WINDOW CLOSED").verifyComplete();
    }

    @Test
    public void apiUntilCutBefore() {
        StepVerifier.create(Flux.just("red", "green", "#", "orange", "blue", "#", "black", "white").windowUntil(( color) -> color.equals("#"), true).flatMap(Flux::materialize).map(( s) -> s.isOnComplete() ? "WINDOW CLOSED" : s.get())).expectNext("red", "green", "WINDOW CLOSED", "#").expectNext("orange", "blue", "WINDOW CLOSED", "#").expectNext("black", "white", "WINDOW CLOSED").verifyComplete();
    }

    @Test
    public void apiWhile() {
        StepVerifier.create(Flux.just("red", "green", "#", "orange", "blue", "#", "black", "white").windowWhile(( color) -> !(color.equals("#"))).flatMap(Flux::materialize).map(( s) -> s.isOnComplete() ? "WINDOW CLOSED" : s.get())).expectNext("red", "green", "WINDOW CLOSED").expectNext("orange", "blue", "WINDOW CLOSED").expectNext("black", "white", "WINDOW CLOSED").verifyComplete();
    }

    @Test
    public void normalUntil() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowUntil = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> (i % 3) == 0, Mode.UNTIL);
        StepVerifier.create(windowUntil.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.next(1)).then(() -> sp1.onNext(2)).expectNext(Signal.next(2)).then(() -> sp1.onNext(3)).expectNext(Signal.next(3), Signal.complete()).then(() -> sp1.onNext(4)).expectNext(Signal.next(4)).then(() -> sp1.onNext(5)).expectNext(Signal.next(5)).then(() -> sp1.onNext(6)).expectNext(Signal.next(6), Signal.complete()).then(() -> sp1.onNext(7)).expectNext(Signal.next(7)).then(() -> sp1.onNext(8)).expectNext(Signal.next(8)).then(sp1::onComplete).expectNext(Signal.complete()).verifyComplete();
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void onCompletionBeforeLastBoundaryWindowEmitted() {
        Flux<Integer> source = Flux.just(1, 2);
        FluxWindowPredicate<Integer> windowUntil = new FluxWindowPredicate(source, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> i >= 3, Mode.UNTIL);
        FluxWindowPredicate<Integer> windowUntilCutBefore = new FluxWindowPredicate(source, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> i >= 3, Mode.UNTIL_CUT_BEFORE);
        FluxWindowPredicate<Integer> windowWhile = new FluxWindowPredicate(source, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> i < 3, Mode.WHILE);
        StepVerifier.create(windowUntil.flatMap(Flux::collectList)).expectNext(Arrays.asList(1, 2)).expectComplete().verify();
        StepVerifier.create(windowUntilCutBefore.flatMap(Flux::collectList)).expectNext(Arrays.asList(1, 2)).expectComplete().verify();
        StepVerifier.create(windowWhile.flatMap(Flux::collectList)).expectNext(Arrays.asList(1, 2)).expectComplete().verify();
    }

    @Test
    public void mainErrorUntilIsPropagatedToBothWindowAndMain() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowUntil = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> (i % 3) == 0, Mode.UNTIL);
        // this is the error in the main:
        // this is the error in the window:
        StepVerifier.create(windowUntil.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.next(1)).then(() -> sp1.onNext(2)).expectNext(Signal.next(2)).then(() -> sp1.onNext(3)).expectNext(Signal.next(3), Signal.complete()).then(() -> sp1.onNext(4)).expectNext(Signal.next(4)).then(() -> sp1.onError(new RuntimeException("forced failure"))).expectNextMatches(signalErrorMessage("forced failure")).expectErrorMessage("forced failure").verify();
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void predicateErrorUntil() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowUntil = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> {
            if (i == 5)
                throw new IllegalStateException("predicate failure");

            return (i % 3) == 0;
        }, Mode.UNTIL);
        // error in the window:
        StepVerifier.create(windowUntil.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.next(1)).then(() -> sp1.onNext(2)).expectNext(Signal.next(2)).then(() -> sp1.onNext(3)).expectNext(Signal.next(3), Signal.complete()).then(() -> sp1.onNext(4)).expectNext(Signal.next(4)).then(() -> sp1.onNext(5)).expectNextMatches(signalErrorMessage("predicate failure")).expectErrorMessage("predicate failure").verify();
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void normalUntilCutBefore() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowUntilCutBefore = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> (i % 3) == 0, Mode.UNTIL_CUT_BEFORE);
        StepVerifier.create(windowUntilCutBefore.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.next(1)).then(() -> sp1.onNext(2)).expectNext(Signal.next(2)).then(() -> sp1.onNext(3)).expectNext(Signal.complete(), Signal.next(3)).then(() -> sp1.onNext(4)).expectNext(Signal.next(4)).then(() -> sp1.onNext(5)).expectNext(Signal.next(5)).then(() -> sp1.onNext(6)).expectNext(Signal.complete(), Signal.next(6)).then(() -> sp1.onNext(7)).expectNext(Signal.next(7)).then(() -> sp1.onNext(8)).expectNext(Signal.next(8)).then(sp1::onComplete).expectNext(Signal.complete()).verifyComplete();
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void mainErrorUntilCutBeforeIsPropagatedToBothWindowAndMain() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowUntilCutBefore = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> (i % 3) == 0, Mode.UNTIL_CUT_BEFORE);
        // this is the error in the main:
        // this is the error in the window:
        StepVerifier.create(windowUntilCutBefore.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.next(1)).then(() -> sp1.onNext(2)).expectNext(Signal.next(2)).then(() -> sp1.onNext(3)).expectNext(Signal.complete()).expectNext(Signal.next(3)).then(() -> sp1.onNext(4)).expectNext(Signal.next(4)).then(() -> sp1.onError(new RuntimeException("forced failure"))).expectNextMatches(signalErrorMessage("forced failure")).expectErrorMessage("forced failure").verify();
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void predicateErrorUntilCutBefore() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowUntilCutBefore = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> {
            if (i == 5)
                throw new IllegalStateException("predicate failure");

            return (i % 3) == 0;
        }, Mode.UNTIL_CUT_BEFORE);
        // error in the window:
        StepVerifier.create(windowUntilCutBefore.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.next(1)).then(() -> sp1.onNext(2)).expectNext(Signal.next(2)).then(() -> sp1.onNext(3)).expectNext(Signal.complete(), Signal.next(3)).then(() -> sp1.onNext(4)).expectNext(Signal.next(4)).then(() -> sp1.onNext(5)).expectNextMatches(signalErrorMessage("predicate failure")).expectErrorMessage("predicate failure").verify();
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void normalWhile() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowWhile = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> (i % 3) != 0, Mode.WHILE);
        StepVerifier.create(windowWhile.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.next(1)).then(() -> sp1.onNext(2)).expectNext(Signal.next(2)).then(() -> sp1.onNext(3)).expectNext(Signal.complete()).then(() -> sp1.onNext(4)).expectNext(Signal.next(4)).then(() -> sp1.onNext(5)).expectNext(Signal.next(5)).then(() -> sp1.onNext(6)).expectNext(Signal.complete()).then(() -> sp1.onNext(7)).expectNext(Signal.next(7)).then(() -> sp1.onNext(8)).expectNext(Signal.next(8)).then(sp1::onComplete).expectNext(Signal.complete()).verifyComplete();
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void normalWhileDoesntInitiallyMatch() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowWhile = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> (i % 3) == 0, Mode.WHILE);
        // completion triggers completion of the last window (7th)
        // emits 9
        // closes 6th, open 7th
        // closes 5th, open 6th
        // emits 6
        // closes 4th, open 5th
        // closes 3rd, open 4th
        // emits 3
        // closes second, open 3rd
        // closes initial, open 2nd
        StepVerifier.create(windowWhile.flatMap(Flux::materialize)).expectSubscription().expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).expectNext(Signal.complete()).then(() -> sp1.onNext(2)).expectNext(Signal.complete()).then(() -> sp1.onNext(3)).expectNext(Signal.next(3)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(4)).expectNext(Signal.complete()).then(() -> sp1.onNext(5)).expectNext(Signal.complete()).then(() -> sp1.onNext(6)).expectNext(Signal.next(6)).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(7)).expectNext(Signal.complete()).then(() -> sp1.onNext(8)).expectNext(Signal.complete()).then(() -> sp1.onNext(9)).expectNext(Signal.next(9)).expectNoEvent(Duration.ofMillis(10)).then(sp1::onComplete).expectNext(Signal.complete()).expectComplete().verify(Duration.ofSeconds(1));
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void normalWhileDoesntMatch() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowWhile = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> i > 4, Mode.WHILE);
        // remainder window, not emitted
        // closing window opened by 3
        StepVerifier.create(windowWhile.flatMap(Flux::materialize)).expectSubscription().expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).expectNext(Signal.complete()).then(() -> sp1.onNext(2)).expectNext(Signal.complete()).then(() -> sp1.onNext(3)).expectNext(Signal.complete()).then(() -> sp1.onNext(4)).expectNext(Signal.complete()).expectNoEvent(Duration.ofMillis(10)).then(() -> sp1.onNext(1)).expectNext(Signal.complete()).then(() -> sp1.onNext(2)).expectNext(Signal.complete()).then(() -> sp1.onNext(3)).expectNext(Signal.complete()).then(() -> sp1.onNext(4)).expectNext(Signal.complete()).expectNoEvent(Duration.ofMillis(10)).then(sp1::onComplete).expectComplete().verify(Duration.ofSeconds(1));
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void mainErrorWhileIsPropagatedToBothWindowAndMain() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowWhile = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> (i % 3) == 0, Mode.WHILE);
        // this is the error in the main:
        // at this point, new window, need another data to close it
        StepVerifier.create(windowWhile.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.complete()).then(() -> sp1.onNext(2)).expectNext(Signal.complete()).then(() -> sp1.onNext(3)).then(() -> sp1.onNext(4)).expectNext(Signal.next(3), Signal.complete()).then(() -> sp1.onError(new RuntimeException("forced failure"))).expectErrorMessage("forced failure").verify(Duration.ofMillis(100));
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void whileStartingSeveralSeparatorsEachCreateEmptyWindow() {
        StepVerifier.create(Flux.just("#").repeat(9).concatWith(Flux.just("other", "value")).windowWhile(( s) -> !(s.equals("#"))).flatMap(Flux::count)).expectNext(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L).expectNext(2L).verifyComplete();
    }

    @Test
    public void whileOnlySeparatorsGivesSequenceOfWindows() {
        // no "remainder" window
        StepVerifier.create(Flux.just("#").repeat(9).windowWhile(( s) -> !(s.equals("#"))).flatMap(( w) -> w.count())).expectNext(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L).verifyComplete();
    }

    @Test
    public void predicateErrorWhile() {
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        FluxWindowPredicate<Integer> windowWhile = new FluxWindowPredicate(sp1, Queues.small(), Queues.unbounded(), Queues.SMALL_BUFFER_SIZE, ( i) -> {
            if (i == 3)
                return true;

            if (i == 5)
                throw new IllegalStateException("predicate failure");

            return false;
        }, Mode.WHILE);
        // fails, the empty window receives onError
        // error in the window:
        // previous window closes, new (empty) window
        // window opens
        // empty window
        // empty window
        StepVerifier.create(windowWhile.flatMap(Flux::materialize)).expectSubscription().then(() -> sp1.onNext(1)).expectNext(Signal.complete()).then(() -> sp1.onNext(2)).expectNext(Signal.complete()).then(() -> sp1.onNext(3)).expectNext(Signal.next(3)).then(() -> sp1.onNext(4)).expectNext(Signal.complete()).then(() -> sp1.onNext(5)).expectErrorMessage("predicate failure").verify(Duration.ofMillis(100));
        assertThat(sp1.hasDownstreams()).isFalse();
    }

    @Test
    public void whileRequestOneByOne() {
        StepVerifier.create(Flux.just("red", "green", "#", "orange", "blue", "#", "black", "white").hide().windowWhile(( color) -> !(color.equals("#"))).flatMap(( w) -> w, 1), 1).expectNext("red").thenRequest(1).expectNext("green").thenRequest(1).expectNext("orange").thenRequest(1).expectNext("blue").thenRequest(1).expectNext("black").thenRequest(1).expectNext("white").thenRequest(1).verifyComplete();
    }

    @Test
    public void mismatchAtBeginningUntil() {
        StepVerifier.create(Flux.just("#", "red", "green").windowUntil(( s) -> s.equals("#")).flatMap(Flux::materialize).map(( sig) -> sig.isOnComplete() ? "END" : sig.get())).expectNext("#", "END").expectNext("red", "green", "END").verifyComplete();
    }

    @Test
    public void mismatchAtBeginningUntilCutBefore() {
        StepVerifier.create(Flux.just("#", "red", "green").windowUntil(( s) -> s.equals("#"), true).flatMap(Flux::materialize).map(( sig) -> sig.isOnComplete() ? "END" : sig.get())).expectNext("END").expectNext("#", "red", "green", "END").verifyComplete();
    }

    @Test
    public void mismatchAtBeginningWhile() {
        StepVerifier.create(Flux.just("#", "red", "green").windowWhile(( s) -> !(s.equals("#"))).flatMap(Flux::materialize).map(( sig) -> sig.isOnComplete() ? "END" : sig.get())).expectNext("END").expectNext("red", "green", "END").verifyComplete();
    }

    @Test
    public void innerCancellationCancelsMainSequence() {
        StepVerifier.create(Flux.just("red", "green", "#", "black", "white").log().windowWhile(( s) -> !(s.equals("#"))).flatMap(( w) -> w.take(1))).expectNext("red").thenCancel().verify();
    }

    @Test
    public void prefetchIntegerMaxIsRequestUnboundedUntil() {
        TestPublisher<?> tp = TestPublisher.create();
        tp.flux().windowUntil(( s) -> true, true, Integer.MAX_VALUE).subscribe();
        tp.assertMinRequested(Long.MAX_VALUE);
    }

    @Test
    public void prefetchIntegerMaxIsRequestUnboundedWhile() {
        TestPublisher<?> tp = TestPublisher.create();
        tp.flux().windowWhile(( s) -> true, Integer.MAX_VALUE).subscribe();
        tp.assertMinRequested(Long.MAX_VALUE);
    }

    @Test
    public void manualRequestWindowUntilOverRequestingSourceByPrefetch() {
        AtomicLong req = new AtomicLong();
        int prefetch = 4;
        Flux<Integer> source = Flux.range(1, 20).doOnRequest(req::addAndGet).log("source", Level.FINE).hide();
        StepVerifier.create(source.windowUntil(( i) -> (i % 5) == 0, false, prefetch).concatMap(( w) -> w, 1).log("downstream", Level.FINE), 0).thenRequest(2).expectNext(1, 2).thenRequest(6).expectNext(3, 4, 5, 6, 7, 8).expectNoEvent(Duration.ofMillis(100)).thenCancel().verify();
        assertThat(req.get()).isEqualTo((8 + prefetch));
    }

    @Test
    public void manualRequestWindowWhileOverRequestingSourceByPrefetch() {
        AtomicLong req = new AtomicLong();
        int prefetch = 4;
        Flux<Integer> source = Flux.range(1, 20).doOnRequest(req::addAndGet).log("source", Level.FINE).hide();
        StepVerifier.create(source.windowWhile(( i) -> (i % 5) != 0, prefetch).concatMap(( w) -> w.log("window", Level.FINE), 1).log("downstream", Level.FINE), 0).thenRequest(2).expectNext(1, 2).thenRequest(6).expectNext(3, 4, 6, 7, 8, 9).expectNoEvent(Duration.ofMillis(100)).thenCancel().verify();
        assertThat(req.get()).isEqualTo((12 + prefetch));// 9 forwarded elements, 2

        // delimiters, 1 cancel and prefetch
    }

    // see https://github.com/reactor/reactor-core/issues/477
    @Test
    public void windowWhileOneByOneStartingDelimiterReplenishes() {
        AtomicLong req = new AtomicLong();
        Flux<String> source = Flux.just("#", "1A", "1B", "1C", "#", "2A", "2B", "2C", "2D", "#", "3A").hide();
        StepVerifier.create(source.doOnRequest(( r) -> req.addAndGet(r)).log("source", Level.FINE).windowWhile(( s) -> !("#".equals(s)), 2).log("windowWhile", Level.FINE).concatMap(( w) -> w.collectList().log("window", Level.FINE), 1).log("downstream", Level.FINE), StepVerifierOptions.create().checkUnderRequesting(false).initialRequest(1)).expectNextMatches(List::isEmpty).thenRequest(1).assertNext(( l) -> assertThat(l).containsExactly("1A", "1B", "1C")).thenRequest(1).assertNext(( l) -> assertThat(l).containsExactly("2A", "2B", "2C", "2D")).thenRequest(1).assertNext(( l) -> assertThat(l).containsExactly("3A")).expectComplete().verify(Duration.ofSeconds(1));
        // TODO is there something wrong here? concatMap now falls back to no fusion because of THREAD_BARRIER, and this results in 15 request total, not 13
        assertThat(req.get()).isGreaterThanOrEqualTo(13);// 11 elements + the prefetch

    }

    // see https://github.com/reactor/reactor-core/issues/477
    @Test
    public void windowWhileUnboundedStartingDelimiterReplenishes() {
        AtomicLong req = new AtomicLong();
        Flux<String> source = Flux.just("#", "1A", "1B", "1C", "#", "2A", "2B", "2C", "2D", "#", "3A").hide();
        StepVerifier.create(source.doOnRequest(req::addAndGet).log("source", Level.FINE).windowWhile(( s) -> !("#".equals(s)), 2).log("windowWhile", Level.FINE).concatMap(( w) -> w.collectList().log("window", Level.FINE), 1).log("downstream", Level.FINE)).expectNextMatches(List::isEmpty).assertNext(( l) -> assertThat(l).containsExactly("1A", "1B", "1C")).assertNext(( l) -> assertThat(l).containsExactly("2A", "2B", "2C", "2D")).assertNext(( l) -> assertThat(l).containsExactly("3A")).expectComplete().verify(Duration.ofSeconds(1));
        // TODO is there something wrong here? concatMap now falls back to no fusion because of THREAD_BARRIER, and this results in 15 request total, not 13
        assertThat(req.get()).isGreaterThanOrEqualTo(13);// 11 elements + the prefetch

    }

    @Test
    public void windowUntilUnboundedStartingDelimiterReplenishes() {
        AtomicLong req = new AtomicLong();
        Flux<String> source = Flux.just("#", "1A", "1B", "1C", "#", "2A", "2B", "2C", "2D", "#", "3A").hide();
        StepVerifier.create(source.doOnRequest(req::addAndGet).log("source", Level.FINE).windowUntil(( s) -> "#".equals(s), false, 2).log("windowUntil", Level.FINE).concatMap(( w) -> w.collectList().log("window", Level.FINE), 1).log("downstream", Level.FINE)).assertNext(( l) -> assertThat(l).containsExactly("#")).assertNext(( l) -> assertThat(l).containsExactly("1A", "1B", "1C", "#")).assertNext(( l) -> assertThat(l).containsExactly("2A", "2B", "2C", "2D", "#")).assertNext(( l) -> assertThat(l).containsExactly("3A")).expectComplete().verify(Duration.ofSeconds(1));
        // TODO is there something wrong here? concatMap now falls back to no fusion because of THREAD_BARRIER, and this results in 15 request total, not 13
        assertThat(req.get()).isGreaterThanOrEqualTo(13);// 11 elements + the prefetch

    }

    @Test
    public void discardOnWindowCancel() {
        List<Object> discardMain = new ArrayList<>();
        List<Object> discardWindow = new ArrayList<>();
        StepVerifier.create(Flux.just(1, 2, 3, 0, 4, 5, 0, 0, 6).windowWhile(( i) -> i > 0).flatMap(( w) -> w.take(1).subscriberContext(Context.of(Hooks.KEY_ON_DISCARD, ((Consumer<Object>) (discardWindow::add))))).subscriberContext(Context.of(KEY_ON_DISCARD, ((java.util.function.Consumer<Object>) (discardMain::add))))).expectNext(1, 4, 6).expectComplete().verifyThenAssertThat().hasNotDiscardedElements();
        assertThat(discardWindow).containsExactly(2, 3, 5);
        assertThat(discardMain).containsExactly(0, 0, 0);
    }

    @Test
    public void scanMainSubscriber() {
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxWindowPredicate.WindowPredicateMain<Integer> test = new FluxWindowPredicate.WindowPredicateMain<>(actual, Queues.<Flux<Integer>>unbounded().get(), Queues.unbounded(), 123, ( i) -> true, Mode.WHILE);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(PREFETCH)).isEqualTo(123);
        test.requested = 35;
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        test.queue.offer(Flux.just(1).groupBy(( i) -> i).blockFirst());
        Assertions.assertThat(test.scan(BUFFERED)).isEqualTo(1);
        Assertions.assertThat(test.scan(ERROR)).isNull();
        test.error = new IllegalStateException("boom");
        Assertions.assertThat(test.scan(ERROR)).hasMessage("boom");
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanOtherSubscriber() {
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxWindowPredicate.WindowPredicateMain<Integer> main = new FluxWindowPredicate.WindowPredicateMain<>(actual, Queues.<Flux<Integer>>unbounded().get(), Queues.unbounded(), 123, ( i) -> true, Mode.WHILE);
        FluxWindowPredicate.WindowFlux<Integer> test = new FluxWindowPredicate.WindowFlux<>(Queues.<Integer>unbounded().get(), main);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(main);
        Assertions.assertThat(test.scan(ACTUAL)).isNull();// RS: TODO Need to make actual non-null

        test.requested = 35;
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        test.queue.offer(27);
        Assertions.assertThat(test.scan(BUFFERED)).isEqualTo(1);
        Assertions.assertThat(test.scan(ERROR)).isNull();
        test.error = new IllegalStateException("boom");
        Assertions.assertThat(test.scan(ERROR)).hasMessage("boom");
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onComplete();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }
}

