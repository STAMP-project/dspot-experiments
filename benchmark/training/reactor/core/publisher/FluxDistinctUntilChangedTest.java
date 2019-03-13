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


import FluxDistinctUntilChanged.DistinctUntilChangedConditionalSubscriber;
import FluxDistinctUntilChanged.DistinctUntilChangedSubscriber;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.Objects;
import java.util.function.Function;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.core.publisher.FluxDistinctTest.DistinctDefault;
import reactor.core.publisher.FluxDistinctTest.DistinctDefaultCancel;
import reactor.core.publisher.FluxDistinctTest.DistinctDefaultError;
import reactor.test.MemoryUtils;
import reactor.test.MockUtils;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;


public class FluxDistinctUntilChangedTest extends FluxOperatorTest<String, String> {
    @Test(expected = NullPointerException.class)
    public void sourceNull() {
        new FluxDistinctUntilChanged(null, ( v) -> v, ( k1, k2) -> true);
    }

    @Test(expected = NullPointerException.class)
    public void keyExtractorNull() {
        Flux.never().distinctUntilChanged(null);
    }

    @Test(expected = NullPointerException.class)
    public void predicateNull() {
        Flux.never().distinctUntilChanged(( v) -> v, null);
    }

    @Test
    public void allDistinct() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).distinctUntilChanged(( v) -> v).subscribe(ts);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete().assertNoError();
    }

    @Test
    public void allDistinctBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.range(1, 10).distinctUntilChanged(( v) -> v).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(2);
        ts.assertValues(1, 2).assertNoError().assertNotComplete();
        ts.request(5);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7).assertNoError().assertNotComplete();
        ts.request(10);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).assertComplete().assertNoError();
    }

    @Test
    public void someRepetition() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.just(1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 3, 3).distinctUntilChanged(( v) -> v).subscribe(ts);
        ts.assertValues(1, 2, 1, 2, 1, 2, 3).assertComplete().assertNoError();
    }

    @Test
    public void someRepetitionBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.just(1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 3, 3).distinctUntilChanged(( v) -> v).subscribe(ts);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(2);
        ts.assertValues(1, 2).assertNoError().assertNotComplete();
        ts.request(4);
        ts.assertValues(1, 2, 1, 2, 1, 2).assertNoError().assertNotComplete();
        ts.request(10);
        ts.assertValues(1, 2, 1, 2, 1, 2, 3).assertComplete().assertNoError();
    }

    @Test
    public void withKeySelector() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).distinctUntilChanged(( v) -> v / 3).subscribe(ts);
        ts.assertValues(1, 3, 6, 9).assertComplete().assertNoError();
    }

    @Test
    public void withKeyComparator() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).distinctUntilChanged(Function.identity(), ( a, b) -> (b - a) < 4).subscribe(ts);
        ts.assertValues(1, 5, 9).assertComplete().assertNoError();
    }

    @Test
    public void keySelectorThrows() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).distinctUntilChanged(( v) -> {
            throw new RuntimeException("forced failure");
        }).subscribe(ts);
        ts.assertNoValues().assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
    }

    @Test
    public void keyComparatorThrows() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).distinctUntilChanged(Function.identity(), ( v1, v2) -> {
            throw new RuntimeException("forced failure");
        }).subscribe(ts);
        ts.assertValues(1).assertNotComplete().assertError(RuntimeException.class).assertErrorMessage("forced failure");
    }

    @Test
    public void allDistinctConditional() {
        DirectProcessor<Integer> dp = new DirectProcessor();
        AssertSubscriber<Integer> ts = dp.distinctUntilChanged().filter(( v) -> true).subscribeWith(AssertSubscriber.create());
        dp.onNext(1);
        dp.onNext(2);
        dp.onNext(3);
        dp.onComplete();
        ts.assertValues(1, 2, 3).assertComplete();
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        DistinctUntilChangedSubscriber<String, Integer> test = new FluxDistinctUntilChanged.DistinctUntilChangedSubscriber<>(actual, String::hashCode, Objects::equals);
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
        DistinctUntilChangedConditionalSubscriber<String, Integer> test = new FluxDistinctUntilChanged.DistinctUntilChangedConditionalSubscriber<>(actual, String::hashCode, Objects::equals);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void distinctUntilChangedDefaultWithHashcodeCollisions() {
        Object foo = new Object() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        Object bar = new Object() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        assertThat(foo).isNotEqualTo(bar).hasSameHashCodeAs(bar);
        StepVerifier.create(Flux.just(foo, bar).distinctUntilChanged()).expectNext(foo, bar).verifyComplete();
    }

    @Test
    public void distinctUntilChangedDefaultDoesntRetainObjects() throws InterruptedException {
        MemoryUtils.RetainedDetector retainedDetector = new MemoryUtils.RetainedDetector();
        Flux<FluxDistinctTest.DistinctDefault> test = Flux.range(1, 100).map(( i) -> retainedDetector.tracked(new DistinctDefault(i))).distinctUntilChanged();
        StepVerifier.create(test).expectNextCount(100).verifyComplete();
        System.gc();
        await().untilAsserted(() -> {
            assertThat(retainedDetector.finalizedCount()).as("none retained").isEqualTo(100);
        });
    }

    @Test
    public void distinctUntilChangedDefaultErrorDoesntRetainObjects() throws InterruptedException {
        MemoryUtils.RetainedDetector retainedDetector = new MemoryUtils.RetainedDetector();
        Flux<FluxDistinctTest.DistinctDefaultError> test = Flux.range(1, 100).map(( i) -> retainedDetector.tracked(new DistinctDefaultError(i))).concatWith(Mono.error(new IllegalStateException("boom"))).distinctUntilChanged();
        StepVerifier.create(test).expectNextCount(100).verifyErrorMessage("boom");
        System.gc();
        await().untilAsserted(() -> {
            assertThat(retainedDetector.finalizedCount()).as("none retained after error").isEqualTo(100);
        });
    }

    @Test
    public void distinctUntilChangedDefaultCancelDoesntRetainObjects() throws InterruptedException {
        MemoryUtils.RetainedDetector retainedDetector = new MemoryUtils.RetainedDetector();
        Flux<FluxDistinctTest.DistinctDefaultCancel> test = Flux.range(1, 100).map(( i) -> retainedDetector.tracked(new DistinctDefaultCancel(i))).concatWith(Mono.error(new IllegalStateException("boom"))).distinctUntilChanged().take(50);
        StepVerifier.create(test).expectNextCount(50).verifyComplete();
        System.gc();
        await().untilAsserted(() -> {
            assertThat(retainedDetector.finalizedCount()).as("none retained after cancel").isEqualTo(50);
        });
    }
}

