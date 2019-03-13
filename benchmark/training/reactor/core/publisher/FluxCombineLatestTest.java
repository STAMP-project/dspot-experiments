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


import FluxCombineLatest.CombineLatestCoordinator;
import Fuseable.ANY;
import Fuseable.ASYNC;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.util.Collections;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;


public class FluxCombineLatestTest extends FluxOperatorTest<String, String> {
    @Test
    public void singleSourceIsMapped() {
        AssertSubscriber<String> ts = AssertSubscriber.create();
        Flux.combineLatest(( a) -> a[0].toString(), Flux.just(1)).subscribe(ts);
        ts.assertValues("1").assertNoError().assertComplete();
    }

    @Test
    public void iterableSingleSourceIsMapped() {
        AssertSubscriber<String> ts = AssertSubscriber.create();
        Flux.combineLatest(Collections.singleton(Flux.just(1)), ( a) -> a[0].toString()).subscribe(ts);
        ts.assertValues("1").assertNoError().assertComplete();
    }

    @Test
    public void fused() {
        DirectProcessor<Integer> dp1 = DirectProcessor.create();
        DirectProcessor<Integer> dp2 = DirectProcessor.create();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        ts.requestedFusionMode(ANY);
        Flux.combineLatest(dp1, dp2, ( a, b) -> a + b).subscribe(ts);
        dp1.onNext(1);
        dp1.onNext(2);
        dp2.onNext(10);
        dp2.onNext(20);
        dp2.onNext(30);
        dp1.onNext(3);
        dp1.onComplete();
        dp2.onComplete();
        ts.assertFuseableSource().assertFusionMode(ASYNC).assertValues(12, 22, 32, 33);
    }

    @Test
    public void combineLatest() {
        StepVerifier.create(Flux.combineLatest(( obj) -> ((int) (obj[0])), Flux.just(1))).expectNext(1).verifyComplete();
    }

    @Test
    public void combineLatestEmpty() {
        StepVerifier.create(Flux.combineLatest(( obj) -> ((int) (obj[0])))).verifyComplete();
    }

    @Test
    public void combineLatestHide() {
        StepVerifier.create(Flux.combineLatest(( obj) -> ((int) (obj[0])), Flux.just(1).hide())).expectNext(1).verifyComplete();
    }

    @Test
    public void combineLatest2() {
        StepVerifier.create(Flux.combineLatest(Flux.just(1), Flux.just(2), ( a, b) -> a)).expectNext(1).verifyComplete();
    }

    @Test
    public void combineLatest3() {
        StepVerifier.create(Flux.combineLatest(Flux.just(1), Flux.just(2), Flux.just(3), ( obj) -> ((int) (obj[0])))).expectNext(1).verifyComplete();
    }

    @Test
    public void combineLatest4() {
        StepVerifier.create(Flux.combineLatest(Flux.just(1), Flux.just(2), Flux.just(3), Flux.just(4), ( obj) -> ((int) (obj[0])))).expectNext(1).verifyComplete();
    }

    @Test
    public void combineLatest5() {
        StepVerifier.create(Flux.combineLatest(Flux.just(1), Flux.just(2), Flux.just(3), Flux.just(4), Flux.just(5), ( obj) -> ((int) (obj[0])))).expectNext(1).verifyComplete();
    }

    @Test
    public void combineLatest6() {
        StepVerifier.create(Flux.combineLatest(Flux.just(1), Flux.just(2), Flux.just(3), Flux.just(4), Flux.just(5), Flux.just(6), ( obj) -> ((int) (obj[0])))).expectNext(1).verifyComplete();
    }

    @Test
    public void scanOperator() {
        FluxCombineLatest s = new FluxCombineLatest(Collections.emptyList(), ( v) -> v, Queues.small(), 123);
        assertThat(s.scan(PREFETCH)).isEqualTo(123);
    }

    @Test
    public void scanMain() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        CombineLatestCoordinator<String, Integer> test = new FluxCombineLatest.CombineLatestCoordinator<>(actual, ( arr) -> {
            throw new IllegalStateException("boomArray");
        }, 123, Queues.<FluxCombineLatest.SourceAndArray>one().get(), 456);
        test.request(2L);
        test.error = new IllegalStateException("boom");// most straightforward way to set it as otherwise it is drained

        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(2L);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(ERROR)).isSameAs(test.error);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.innerComplete(1);
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanInner() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        CombineLatestCoordinator<String, Integer> main = new FluxCombineLatest.CombineLatestCoordinator<>(actual, ( arr) -> arr.length, 123, Queues.<FluxCombineLatest.SourceAndArray>one().get(), 456);
        FluxCombineLatest.CombineLatestInner<String> test = new FluxCombineLatest.CombineLatestInner<>(main, 1, 789);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(789);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(main);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void singleSourceNormalWithFuseableDownstream() {
        StepVerifier.create(// the map is Fuseable and sees the combine as fuseable too
        Flux.combineLatest(Collections.singletonList(Flux.just(1, 2, 3).hide()), ( arr) -> arr[0].toString()).map(( x) -> x + "!").collectList()).assertNext(( l) -> assertThat(l).containsExactly("1!", "2!", "3!")).verifyComplete();
    }

    @Test
    public void singleSourceNormalWithoutFuseableDownstream() {
        StepVerifier.create(// the collectList is NOT Fuseable
        Flux.combineLatest(Collections.singletonList(Flux.just(1, 2, 3).hide()), ( arr) -> arr[0].toString()).collectList()).assertNext(( l) -> assertThat(l).containsExactly("1", "2", "3")).verifyComplete();
    }

    @Test
    public void singleSourceFusedWithFuseableDownstream() {
        StepVerifier.create(// the map is Fuseable and sees the combine as fuseable too
        Flux.combineLatest(Collections.singletonList(Flux.just(1, 2, 3)), ( arr) -> arr[0].toString()).map(( x) -> x + "!").collectList()).assertNext(( l) -> assertThat(l).containsExactly("1!", "2!", "3!")).verifyComplete();
    }
}

