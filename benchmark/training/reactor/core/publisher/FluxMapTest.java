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


import FluxMap.MapConditionalSubscriber;
import FluxMap.MapSubscriber;
import FluxMapFuseable.MapFuseableConditionalSubscriber;
import FluxMapFuseable.MapFuseableSubscriber;
import OnNextFailureStrategy.RESUME_DROP;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.test.MockUtils;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;


public class FluxMapTest extends FluxOperatorTest<String, String> {
    Flux<Integer> just = Flux.just(1);

    @Test(expected = NullPointerException.class)
    public void nullSource() {
        new FluxMap<Integer, Integer>(null, ( v) -> v);
    }

    @Test(expected = NullPointerException.class)
    public void nullMapper() {
        just.map(null);
    }

    @Test
    public void simpleMapping() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        just.map(( v) -> v + 1).subscribe(ts);
        ts.assertNoError().assertValues(2).assertComplete();
    }

    @Test
    public void simpleMappingBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        just.map(( v) -> v + 1).subscribe(ts);
        ts.assertNoError().assertNoValues().assertNotComplete();
        ts.request(1);
        ts.assertNoError().assertValues(2).assertComplete();
    }

    @Test
    public void mapperThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        just.map(( v) -> {
            throw new RuntimeException("forced failure");
        }).subscribe(ts);
        ts.assertError(RuntimeException.class).assertErrorMessage("forced failure").assertNoValues().assertNotComplete();
    }

    @Test
    public void mapperReturnsNull() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        just.map(( v) -> null).subscribe(ts);
        ts.assertError(NullPointerException.class).assertNoValues().assertNotComplete();
    }

    @Test
    public void syncFusion() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 10).map(( v) -> v + 1).subscribe(ts);
        ts.assertValues(2, 3, 4, 5, 6, 7, 8, 9, 10, 11).assertNoError().assertComplete();
    }

    @Test
    public void asyncFusion() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        UnicastProcessor<Integer> up = UnicastProcessor.create(new ConcurrentLinkedQueue());
        up.map(( v) -> v + 1).subscribe(ts);
        for (int i = 1; i < 11; i++) {
            up.onNext(i);
        }
        up.onComplete();
        ts.assertValues(2, 3, 4, 5, 6, 7, 8, 9, 10, 11).assertNoError().assertComplete();
    }

    @Test
    public void asyncFusionBackpressured() {
        AssertSubscriber<Object> ts = AssertSubscriber.create(1);
        UnicastProcessor<Integer> up = UnicastProcessor.create(new ConcurrentLinkedQueue());
        Flux.just(1).hide().flatMap(( w) -> up.map(( v) -> v + 1)).subscribe(ts);
        up.onNext(1);
        ts.assertValues(2).assertNoError().assertNotComplete();
        up.onComplete();
        ts.assertValues(2).assertNoError().assertComplete();
    }

    @Test
    public void mapFilter() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(0, 1000000).map(( v) -> v + 1).filter(( v) -> (v & 1) == 0).subscribe(ts);
        ts.assertValueCount(500000).assertNoError().assertComplete();
    }

    @Test
    public void mapFilterBackpressured() {
        AssertSubscriber<Object> ts = AssertSubscriber.create(0);
        Flux.range(0, 1000000).map(( v) -> v + 1).filter(( v) -> (v & 1) == 0).subscribe(ts);
        ts.assertNoError().assertNoValues().assertNotComplete();
        ts.request(250000);
        ts.assertValueCount(250000).assertNoError().assertNotComplete();
        ts.request(250000);
        ts.assertValueCount(500000).assertNoError().assertComplete();
    }

    @Test
    public void hiddenMapFilter() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(0, 1000000).hide().map(( v) -> v + 1).filter(( v) -> (v & 1) == 0).subscribe(ts);
        ts.assertValueCount(500000).assertNoError().assertComplete();
    }

    @Test
    public void hiddenMapFilterBackpressured() {
        AssertSubscriber<Object> ts = AssertSubscriber.create(0);
        Flux.range(0, 1000000).hide().map(( v) -> v + 1).filter(( v) -> (v & 1) == 0).subscribe(ts);
        ts.assertNoError().assertNoValues().assertNotComplete();
        ts.request(250000);
        ts.assertValueCount(250000).assertNoError().assertNotComplete();
        ts.request(250000);
        ts.assertValueCount(500000).assertNoError().assertComplete();
    }

    @Test
    public void hiddenMapHiddenFilterBackpressured() {
        AssertSubscriber<Object> ts = AssertSubscriber.create(0);
        Flux.range(0, 1000000).hide().map(( v) -> v + 1).hide().filter(( v) -> (v & 1) == 0).subscribe(ts);
        ts.assertNoError().assertNoValues().assertNotComplete();
        ts.request(250000);
        ts.assertValueCount(250000).assertNoError().assertNotComplete();
        ts.request(250000);
        ts.assertValueCount(500000).assertNoError().assertComplete();
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        MapSubscriber<Integer, String> test = new FluxMap.MapSubscriber<>(actual, ( i) -> String.valueOf(i));
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanConditionalSubscriber() {
        @SuppressWarnings("unchecked")
        Fuseable.ConditionalSubscriber<String> actual = Mockito.mock(MockUtils.TestScannableConditionalSubscriber.class);
        MapConditionalSubscriber<Integer, String> test = new FluxMap.MapConditionalSubscriber<>(actual, ( i) -> String.valueOf(i));
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanFuseableSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        MapFuseableSubscriber<Integer, String> test = new FluxMapFuseable.MapFuseableSubscriber<>(actual, ( i) -> String.valueOf(i));
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanFuseableConditionalSubscriber() {
        @SuppressWarnings("unchecked")
        Fuseable.ConditionalSubscriber<String> actual = Mockito.mock(MockUtils.TestScannableConditionalSubscriber.class);
        MapFuseableConditionalSubscriber<Integer, String> test = new FluxMapFuseable.MapFuseableConditionalSubscriber<>(actual, ( i) -> String.valueOf(i));
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void mapFailureStrategyResume() {
        Hooks.onNextError(RESUME_DROP);
        try {
            AtomicLong r = new AtomicLong();
            StepVerifier.create(Flux.just(0, 2).doOnRequest(r::addAndGet).hide().map(( i) -> 4 / i), 1).expectNoFusionSupport().expectNext(2).expectComplete().verifyThenAssertThat().hasDroppedExactly(0).hasDroppedErrorWithMessage("/ by zero");
            assertThat(r.get()).as("amount requested").isEqualTo(2L);
        } finally {
            Hooks.resetOnNextError();
        }
    }

    @Test
    public void mapFailureStrategyCustomResume() {
        List<Object> valuesDropped = new ArrayList<>();
        List<Throwable> errorsDropped = new ArrayList<>();
        Hooks.onNextError(OnNextFailureStrategy.resume(( t, s) -> {
            errorsDropped.add(t);
            valuesDropped.add(s);
        }));
        try {
            AtomicLong r = new AtomicLong();
            StepVerifier.create(Flux.just(0, 2).doOnRequest(r::addAndGet).hide().map(( i) -> 4 / i), 1).expectNoFusionSupport().expectNext(2).expectComplete().verifyThenAssertThat().hasNotDroppedElements().hasNotDroppedErrors();
            assertThat(valuesDropped).containsExactly(0);
            assertThat(errorsDropped).hasSize(1).allSatisfy(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("/ by zero"));
            assertThat(r.get()).as("amount requested").isEqualTo(2L);
        } finally {
            Hooks.resetOnNextError();
        }
    }

    @Test
    public void mapTryOnNextFailureStrategyResume() {
        Hooks.onNextError(RESUME_DROP);
        try {
            StepVerifier.create(// distinctUntilChanged is conditional but not fuseable
            Flux.range(0, 2).distinctUntilChanged().map(( i) -> 4 / i).filter(( i) -> true)).expectNoFusionSupport().expectNext(4).expectComplete().verifyThenAssertThat().hasDroppedExactly(0).hasDroppedErrorWithMessage("/ by zero");
        } finally {
            Hooks.resetOnNextError();
        }
    }

    @Test
    public void mapFuseableFailureStrategyResume() {
        Hooks.onNextError(RESUME_DROP);
        try {
            AtomicLong r = new AtomicLong();
            StepVerifier.create(Flux.just(0, 2).doOnRequest(r::addAndGet).map(( i) -> 4 / i), 1).expectFusion().expectNext(2).expectComplete().verifyThenAssertThat().hasDropped(0).hasDroppedErrorWithMessage("/ by zero");
            assertThat(r.get()).as("async/no request").isEqualTo(0L);
        } finally {
            Hooks.resetOnNextError();
        }
    }

    @Test
    public void mapFuseableTryOnNextFailureStrategyResume() {
        Hooks.onNextError(RESUME_DROP);
        try {
            StepVerifier.create(Flux.range(0, 2).map(( i) -> 4 / i).filter(( i) -> true)).expectFusion().expectNext(4).expectComplete().verifyThenAssertThat().hasDroppedExactly(0).hasDroppedErrorWithMessage("/ by zero");
        } finally {
            Hooks.resetOnNextError();
        }
    }
}

