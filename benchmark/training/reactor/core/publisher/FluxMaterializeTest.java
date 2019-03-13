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


public class FluxMaterializeTest extends FluxOperatorTest<String, Signal<String>> {
    @Test
    public void completeOnlyBackpressured() {
        AssertSubscriber<Signal<Integer>> ts = AssertSubscriber.create(0L);
        Flux.<Integer>empty().materialize().subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(Signal.complete()).assertNoError().assertComplete();
    }

    @Test
    public void errorOnlyBackpressured() {
        AssertSubscriber<Signal<Integer>> ts = AssertSubscriber.create(0L);
        RuntimeException ex = new RuntimeException();
        Flux.<Integer>error(ex).materialize().subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(1);
        ts.assertValues(Signal.error(ex)).assertNoError().assertComplete();
    }

    @Test
    public void materialize() {
        StepVerifier.create(Flux.just("Three", "Two", "One").materialize()).expectNextMatches(( s) -> (s.isOnNext()) && ("Three".equals(s.get()))).expectNextMatches(( s) -> (s.isOnNext()) && ("Two".equals(s.get()))).expectNextMatches(( s) -> (s.isOnNext()) && ("One".equals(s.get()))).expectNextMatches(Signal::isOnComplete).verifyComplete();
    }

    @Test
    public void materialize2() {
        StepVerifier.create(Flux.just("Three", "Two").concatWith(Flux.error(new RuntimeException("test"))).materialize()).expectNextMatches(( s) -> (s.isOnNext()) && ("Three".equals(s.get()))).expectNextMatches(( s) -> (s.isOnNext()) && ("Two".equals(s.get()))).expectNextMatches(( s) -> ((s.isOnError()) && ((s.getThrowable()) != null)) && ("test".equals(s.getThrowable().getMessage()))).verifyComplete();
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<Signal<String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxMaterialize.MaterializeSubscriber<String> test = new FluxMaterialize.MaterializeSubscriber<String>(actual);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        test.requested = 35;
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);// RS: TODO non-zero size

        assertThat(test.scan(TERMINATED)).isFalse();
        test.terminalSignal = Signal.error(new IllegalStateException("boom"));
        assertThat(test.scan(ERROR)).hasMessage("boom");
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

