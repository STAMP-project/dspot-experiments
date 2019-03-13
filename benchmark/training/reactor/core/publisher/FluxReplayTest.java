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


import FluxReplay.ReplaySubscriber;
import Fuseable.ANY;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.CAPACITY;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.RUN_ON;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.util.function.Tuple2;


public class FluxReplayTest extends FluxOperatorTest<String, String> {
    @Test(expected = IllegalArgumentException.class)
    public void failPrefetch() {
        Flux.never().replay((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failTime() {
        Flux.never().replay(Duration.ofDays((-1)));
    }

    VirtualTimeScheduler vts;

    @Test
    public void cacheFlux() {
        Flux<Tuple2<Long, Integer>> source = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1000)).replay().autoConnect().elapsed();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 3)).verifyComplete();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 3)).verifyComplete();
    }

    @Test
    public void cacheFluxFused() {
        Flux<Tuple2<Long, Integer>> source = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1000)).replay().autoConnect().elapsed();
        StepVerifier.create(source).expectFusion(ANY).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 3)).verifyComplete();
        StepVerifier.create(source).expectFusion(ANY).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 3)).verifyComplete();
    }

    @Test
    public void cacheFluxTTL() {
        Flux<Tuple2<Long, Integer>> source = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1000)).replay(Duration.ofMillis(2000)).autoConnect().elapsed();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 3)).verifyComplete();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 3)).verifyComplete();
    }

    @Test
    public void cacheFluxTTLFused() {
        Flux<Tuple2<Long, Integer>> source = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1000)).replay(Duration.ofMillis(2000)).autoConnect().elapsed();
        StepVerifier.create(source).expectFusion(ANY).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 3)).verifyComplete();
        StepVerifier.create(source).expectFusion(ANY).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 3)).verifyComplete();
    }

    @Test
    public void cacheFluxTTLMillis() {
        Flux<Tuple2<Long, Integer>> source = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1000)).replay(Duration.ofMillis(2000), vts).autoConnect().elapsed();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 3)).verifyComplete();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 3)).verifyComplete();
    }

    @Test
    public void cacheFluxHistoryTTL() {
        Flux<Tuple2<Long, Integer>> source = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1000)).replay(2, Duration.ofMillis(2000)).autoConnect().elapsed();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 3)).verifyComplete();
        StepVerifier.create(source).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 3)).verifyComplete();
    }

    @Test
    public void cacheFluxHistoryTTLFused() {
        Flux<Tuple2<Long, Integer>> source = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1000)).replay(2, Duration.ofMillis(2000)).autoConnect().elapsed().log();
        StepVerifier.create(source).expectFusion(ANY).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 1)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 1000) && ((t.getT2()) == 3)).verifyComplete();
        StepVerifier.create(source).expectFusion(ANY).then(() -> vts.advanceTimeBy(Duration.ofSeconds(3))).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 2)).expectNextMatches(( t) -> ((t.getT1()) == 0) && ((t.getT2()) == 3)).verifyComplete();
    }

    @Test
    public void cancel() {
        ConnectableFlux<Integer> replay = UnicastProcessor.<Integer>create().replay(2);
        replay.subscribe(( v) -> {
        }, ( e) -> {
            throw Exceptions.propagate(e);
        });
        Disposable connected = replay.connect();
        // the lambda subscriber itself is cancelled so it will bubble the exception
        // propagated by connect().dispose()
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(connected::dispose).withMessage("Disconnected");
        boolean cancelled = ((FluxReplay.ReplaySubscriber) (connected)).cancelled;
        assertThat(cancelled).isTrue();
    }

    @Test
    public void scanMain() {
        Flux<Integer> parent = Flux.just(1).map(( i) -> i);
        FluxReplay<Integer> test = new FluxReplay(parent, 25, 1000, Schedulers.single());
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(PREFETCH)).isEqualTo(25);
        Assertions.assertThat(test.scan(RUN_ON)).isSameAs(Schedulers.single());
    }

    @Test
    public void scanInner() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxReplay<Integer> main = new FluxReplay(Flux.just(1), 2, 1000, Schedulers.single());
        FluxReplay.ReplayInner<Integer> test = new FluxReplay.ReplayInner<>(actual);
        FluxReplay.ReplaySubscriber<Integer> parent = new FluxReplay.ReplaySubscriber<>(new FluxReplay.UnboundedReplayBuffer<>(10), main);
        parent.add(test);
        test.parent = parent;
        parent.buffer.replay(test);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(BUFFERED)).isEqualTo(0);// RS: TODO non-zero size

        Assertions.assertThat(test.scan(RUN_ON)).isSameAs(Schedulers.single());
        test.request(35);
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.parent.terminate();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanSubscriber() {
        FluxReplay<Integer> parent = new FluxReplay(Flux.just(1), 2, 1000, Schedulers.single());
        FluxReplay.ReplaySubscriber<Integer> test = new FluxReplay.ReplaySubscriber<>(new FluxReplay.UnboundedReplayBuffer<>(10), parent);
        Subscription sub = Operators.emptySubscription();
        test.onSubscribe(sub);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(sub);
        Assertions.assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        Assertions.assertThat(test.scan(CAPACITY)).isEqualTo(Integer.MAX_VALUE);
        test.buffer.add(1);
        Assertions.assertThat(test.scan(BUFFERED)).isEqualTo(1);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        Assertions.assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        Assertions.assertThat(test.scan(ERROR)).hasMessage("boom");
        test.terminate();
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancelled = true;
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }
}

