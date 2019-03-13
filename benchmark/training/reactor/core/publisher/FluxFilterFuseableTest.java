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


import Fuseable.ANY;
import Fuseable.ASYNC;
import Fuseable.NONE;
import Fuseable.SYNC;
import OnNextFailureStrategy.RESUME_DROP;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.core.publisher.FluxFilterFuseable.FilterFuseableConditionalSubscriber;
import reactor.core.publisher.FluxFilterFuseable.FilterFuseableSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.MockUtils;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;


public class FluxFilterFuseableTest extends FluxOperatorTest<String, String> {
    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FilterFuseableSubscriber<String> test = new FilterFuseableSubscriber(actual, ( t) -> true);
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
        FilterFuseableConditionalSubscriber<String> test = new FilterFuseableConditionalSubscriber(actual, ( t) -> true);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void failureStrategyResumeSyncFused() {
        Hooks.onNextError(RESUME_DROP);
        try {
            StepVerifier.create(Flux.range(0, 2).filter(( i) -> (4 / i) == 4)).expectFusion(ANY, SYNC).expectNext(1).expectComplete().verifyThenAssertThat().hasDroppedExactly(0).hasDroppedErrorWithMessage("/ by zero");
        } finally {
            Hooks.resetOnNextError();
        }
    }

    @Test
    public void failureStrategyResumeConditionalSyncFused() {
        Hooks.onNextError(RESUME_DROP);
        try {
            StepVerifier.create(Flux.range(0, 2).filter(( i) -> (4 / i) == 4).filter(( i) -> true)).expectFusion(ANY, SYNC).expectNext(1).expectComplete().verifyThenAssertThat().hasDroppedExactly(0).hasDroppedErrorWithMessage("/ by zero");
        } finally {
            Hooks.resetOnNextError();
        }
    }

    @Test
    public void discardOnNextPredicateFail() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> {
            throw new IllegalStateException("boom");
        })).expectFusion(NONE).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardOnNextPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> (i % 2) == 0)).expectFusion(NONE).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }

    @Test
    public void discardTryOnNextPredicateFail() {
        StepVerifier.create(// range uses tryOnNext
        Flux.range(1, 10).filter(( i) -> {
            throw new IllegalStateException("boom");
        })).expectFusion(NONE).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardTryOnNextPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext
        Flux.range(1, 10).filter(( i) -> (i % 2) == 0)).expectFusion(NONE).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }

    @Test
    public void discardPollAsyncPredicateFail() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).publishOn(Schedulers.newSingle("discardPollAsync"), 1).filter(( i) -> {
            throw new IllegalStateException("boom");
        })).expectFusion(ASYNC).expectErrorMessage("boom").verifyThenAssertThat().hasDiscarded(1);// publishOn also might discard the rest

    }

    @Test
    public void discardPollAsyncPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).publishOn(Schedulers.newSingle("discardPollAsync")).filter(( i) -> (i % 2) == 0)).expectFusion(ASYNC).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }

    @Test
    public void discardPollSyncPredicateFail() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> {
            throw new IllegalStateException("boom");
        })).expectFusion(SYNC).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardPollSyncPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> (i % 2) == 0)).expectFusion(SYNC).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }

    @Test
    public void discardConditionalOnNextPredicateFail() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> {
            throw new IllegalStateException("boom");
        }).filter(( i) -> true)).expectFusion(NONE).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardConditionalOnNextPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> (i % 2) == 0).filter(( i) -> true)).expectFusion(NONE).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }

    @Test
    public void discardConditionalTryOnNextPredicateFail() {
        StepVerifier.create(// range uses tryOnNext
        Flux.range(1, 10).filter(( i) -> {
            throw new IllegalStateException("boom");
        }).filter(( i) -> true)).expectFusion(NONE).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardConditionalTryOnNextPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext
        Flux.range(1, 10).filter(( i) -> (i % 2) == 0).filter(( i) -> true)).expectFusion(NONE).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }

    @Test
    public void discardConditionalPollAsyncPredicateFail() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.range(1, 10).publishOn(Schedulers.newSingle("discardPollAsync")).filter(( i) -> {
            throw new IllegalStateException("boom");
        }).filter(( i) -> true)).expectFusion(ASYNC).expectErrorMessage("boom").verifyThenAssertThat().hasDiscarded(1);// publishOn also discards the rest

    }

    @Test
    public void discardConditionalPollAsyncPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).publishOn(Schedulers.newSingle("discardPollAsync")).filter(( i) -> (i % 2) == 0).filter(( i) -> true)).expectFusion(ASYNC).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }

    @Test
    public void discardConditionalPollSyncPredicateFail() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> {
            throw new IllegalStateException("boom");
        }).filter(( i) -> true)).expectFusion(SYNC).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardConditionalPollSyncPredicateMiss() {
        StepVerifier.create(// range uses tryOnNext, so let's use just instead
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(( i) -> (i % 2) == 0).filter(( i) -> true)).expectFusion(SYNC).expectNextCount(5).expectComplete().verifyThenAssertThat().hasDiscardedExactly(1, 3, 5, 7, 9);
    }
}

