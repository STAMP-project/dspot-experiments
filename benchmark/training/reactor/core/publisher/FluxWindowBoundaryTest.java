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


import FluxWindowBoundary.WindowBoundaryMain;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;


public class FluxWindowBoundaryTest {
    @Test
    public void normal() {
        AssertSubscriber<Flux<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        DirectProcessor<Integer> sp2 = DirectProcessor.create();
        sp1.window(sp2).subscribe(ts);
        ts.assertValueCount(1);
        sp1.onNext(1);
        sp1.onNext(2);
        sp1.onNext(3);
        sp2.onNext(1);
        sp1.onNext(4);
        sp1.onNext(5);
        sp1.onComplete();
        ts.assertValueCount(2);
        FluxWindowBoundaryTest.expect(ts, 0, 1, 2, 3);
        FluxWindowBoundaryTest.expect(ts, 1, 4, 5);
        ts.assertNoError().assertComplete();
        Assert.assertFalse("sp1 has subscribers", sp1.hasDownstreams());
        Assert.assertFalse("sp2 has subscribers", sp1.hasDownstreams());
    }

    @Test
    public void normalOtherCompletes() {
        AssertSubscriber<Flux<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        DirectProcessor<Integer> sp2 = DirectProcessor.create();
        sp1.window(sp2).subscribe(ts);
        ts.assertValueCount(1);
        sp1.onNext(1);
        sp1.onNext(2);
        sp1.onNext(3);
        sp2.onNext(1);
        sp1.onNext(4);
        sp1.onNext(5);
        sp2.onComplete();
        ts.assertValueCount(2);
        FluxWindowBoundaryTest.expect(ts, 0, 1, 2, 3);
        FluxWindowBoundaryTest.expect(ts, 1, 4, 5);
        ts.assertNoError().assertComplete();
        Assert.assertFalse("sp1 has subscribers", sp1.hasDownstreams());
        Assert.assertFalse("sp2 has subscribers", sp1.hasDownstreams());
    }

    @Test
    public void mainError() {
        AssertSubscriber<Flux<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        DirectProcessor<Integer> sp2 = DirectProcessor.create();
        sp1.window(sp2).subscribe(ts);
        ts.assertValueCount(1);
        sp1.onNext(1);
        sp1.onNext(2);
        sp1.onNext(3);
        sp2.onNext(1);
        sp1.onNext(4);
        sp1.onNext(5);
        sp1.onError(new RuntimeException("forced failure"));
        ts.assertValueCount(2);
        FluxWindowBoundaryTest.expect(ts, 0, 1, 2, 3);
        FluxWindowBoundaryTest.toList(ts.values().get(1)).assertValues(4, 5).assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        ts.assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        Assert.assertFalse("sp1 has subscribers", sp1.hasDownstreams());
        Assert.assertFalse("sp2 has subscribers", sp1.hasDownstreams());
    }

    @Test
    public void otherError() {
        AssertSubscriber<Flux<Integer>> ts = AssertSubscriber.create();
        DirectProcessor<Integer> sp1 = DirectProcessor.create();
        DirectProcessor<Integer> sp2 = DirectProcessor.create();
        sp1.window(sp2).subscribe(ts);
        ts.assertValueCount(1);
        sp1.onNext(1);
        sp1.onNext(2);
        sp1.onNext(3);
        sp2.onNext(1);
        sp1.onNext(4);
        sp1.onNext(5);
        sp2.onError(new RuntimeException("forced failure"));
        ts.assertValueCount(2);
        FluxWindowBoundaryTest.expect(ts, 0, 1, 2, 3);
        FluxWindowBoundaryTest.toList(ts.values().get(1)).assertValues(4, 5).assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        ts.assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNotComplete();
        Assert.assertFalse("sp1 has subscribers", sp1.hasDownstreams());
        Assert.assertFalse("sp2 has subscribers", sp1.hasDownstreams());
    }

    @Test
    public void windowWillSubdivideAnInputFluxTime() {
        StepVerifier.withVirtualTime(this::scenario_windowWillSubdivideAnInputFluxTime).thenAwait(Duration.ofSeconds(10)).assertNext(( t) -> assertThat(t).containsExactly(1, 2)).assertNext(( t) -> assertThat(t).containsExactly(3, 4)).assertNext(( t) -> assertThat(t).containsExactly(5, 6)).assertNext(( t) -> assertThat(t).containsExactly(7, 8)).verifyComplete();
    }

    @Test
    public void windowWillAccumulateMultipleListsOfValues() {
        // given: "a source and a collected flux"
        EmitterProcessor<Integer> numbers = EmitterProcessor.create();
        // non overlapping buffers
        EmitterProcessor<Integer> boundaryFlux = EmitterProcessor.create();
        MonoProcessor<List<List<Integer>>> res = numbers.window(boundaryFlux).concatMap(Flux::buffer).buffer().publishNext().toProcessor();
        res.subscribe();
        numbers.onNext(1);
        numbers.onNext(2);
        numbers.onNext(3);
        boundaryFlux.onNext(1);
        numbers.onNext(5);
        numbers.onNext(6);
        numbers.onComplete();
        // "the collected lists are available"
        assertThat(res.block()).containsExactly(Arrays.asList(1, 2, 3), Arrays.asList(5, 6));
    }

    @Test
    public void scanMainSubscriber() {
        CoreSubscriber<Flux<Integer>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        WindowBoundaryMain<Integer, Integer> test = new FluxWindowBoundary.WindowBoundaryMain<>(actual, Queues.unbounded(), Queues.<Integer>unbounded().get());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        test.requested = 35;
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        test.queue.offer(37);
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
        WindowBoundaryMain<Integer, Integer> main = new FluxWindowBoundary.WindowBoundaryMain<>(actual, Queues.unbounded(), Queues.<Integer>unbounded().get());
        FluxWindowBoundary.WindowBoundaryOther<Integer> test = new FluxWindowBoundary.WindowBoundaryOther<>(main);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(main);
        test.requested = 35;
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }
}

