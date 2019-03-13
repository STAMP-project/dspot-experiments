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
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;


public class FluxDematerializeTest extends FluxOperatorTest<Signal<String>, String> {
    Signal<Integer> error = Signal.error(new RuntimeException("Forced failure"));

    @Test
    public void singleCompletion() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux<Integer> dematerialize = Flux.just(Signal.<Integer>complete()).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void singleError() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux<Integer> dematerialize = Flux.just(error).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertError(RuntimeException.class).assertNotComplete();
    }

    @Test
    public void immediateCompletion() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux<Integer> dematerialize = Flux.just(Signal.<Integer>complete()).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void immediateError() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux<Integer> dematerialize = Flux.just(error).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertError(RuntimeException.class).assertNotComplete();
    }

    @Test
    public void completeAfterSingleSignal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux<Integer> dematerialize = Flux.just(Signal.next(1), Signal.<Integer>complete()).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(1).assertNoError().assertComplete();
    }

    @Test
    public void errorAfterSingleSignal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux<Integer> dematerialize = Flux.just(Signal.next(1), error).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(1).assertError(RuntimeException.class).assertNotComplete();
    }

    @Test
    public void twoSignalsAndComplete() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux<Integer> dematerialize = Flux.just(Signal.next(1), Signal.next(2), Signal.<Integer>complete()).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(1).assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(1, 2).assertNoError().assertComplete();
    }

    @Test
    public void twoSignalsAndError() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux<Integer> dematerialize = Flux.just(Signal.next(1), Signal.next(2), error).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(1).assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(1, 2).assertError(RuntimeException.class).assertNotComplete();
    }

    @Test
    public void neverEnding() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux<Integer> dematerialize = Flux.just(Signal.next(1), Signal.next(2), Signal.next(3), Signal.<Integer>complete()).concatWith(Flux.never()).dematerialize();
        dematerialize.subscribe(ts);
        ts.assertValues(1, 2, 3).assertNoError().assertComplete();
    }

    @Test
    public void dematerialize() {
        StepVerifier.create(Flux.just(Signal.next("Three"), Signal.next("Two"), Signal.next("One"), Signal.complete()).dematerialize()).expectNext("Three").expectNext("Two").expectNext("One").verifyComplete();
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(100));
        FluxDematerialize.DematerializeSubscriber<String> test = new FluxDematerialize.DematerializeSubscriber<>(actual);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(100L);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);
        test.value = "foo";
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        assertThat(test.scan(ERROR)).isNull();
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(ERROR)).hasMessage("boom");
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

