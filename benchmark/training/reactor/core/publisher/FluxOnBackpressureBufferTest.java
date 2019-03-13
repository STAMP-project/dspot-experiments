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
import Scannable.Attr.DELAY_ERROR;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;


public class FluxOnBackpressureBufferTest extends FluxOperatorTest<String, String> {
    @Test(expected = IllegalArgumentException.class)
    public void failNegativeHistory() {
        Flux.never().onBackpressureBuffer((-1));
    }

    @Test
    public void onBackpressureBuffer() {
        StepVerifier.create(Flux.range(1, 100).onBackpressureBuffer(), 0).thenRequest(5).expectNext(1, 2, 3, 4, 5).thenAwait().thenRequest(90).expectNextCount(90).thenAwait().thenRequest(5).expectNext(96, 97, 98, 99, 100).verifyComplete();
    }

    @Test
    public void onBackpressureBufferMax() {
        StepVerifier.create(Flux.range(1, 100).hide().onBackpressureBuffer(8), 0).thenRequest(7).expectNext(1, 2, 3, 4, 5, 6, 7).thenAwait().verifyErrorMatches(Exceptions::isOverflow);
    }

    @Test
    public void onBackpressureBufferMaxCallback() {
        AtomicInteger last = new AtomicInteger();
        StepVerifier.create(Flux.range(1, 100).hide().onBackpressureBuffer(8, last::set), 0).thenRequest(7).expectNext(1, 2, 3, 4, 5, 6, 7).then(() -> assertThat(last.get()).isEqualTo(16)).thenRequest(9).expectNextCount(8).verifyErrorMatches(Exceptions::isOverflow);
    }

    @Test
    public void onBackpressureBufferMaxCallbackOverflow() {
        AtomicInteger last = new AtomicInteger();
        StepVerifier.create(Flux.range(1, 100).hide().onBackpressureBuffer(8, last::set, BufferOverflowStrategy.ERROR), 0).thenRequest(7).expectNext(1, 2, 3, 4, 5, 6, 7).then(() -> assertThat(last.get()).isEqualTo(16)).thenRequest(9).expectNextCount(8).verifyErrorMatches(Exceptions::isOverflow);
    }

    @Test
    public void onBackpressureBufferMaxCallbackOverflow2() {
        AtomicInteger last = new AtomicInteger();
        StepVerifier.create(Flux.range(1, 100).hide().onBackpressureBuffer(8, last::set, BufferOverflowStrategy.DROP_OLDEST), 0).thenRequest(7).expectNext(1, 2, 3, 4, 5, 6, 7).then(() -> assertThat(last.get()).isEqualTo(92)).thenRequest(9).expectNext(93, 94, 95, 96, 97, 98, 99, 100).verifyComplete();
    }

    @Test
    public void onBackpressureBufferMaxCallbackOverflow3() {
        AtomicInteger last = new AtomicInteger();
        StepVerifier.create(Flux.range(1, 100).hide().onBackpressureBuffer(8, last::set, BufferOverflowStrategy.DROP_LATEST), 0).thenRequest(7).expectNext(1, 2, 3, 4, 5, 6, 7).then(() -> assertThat(last.get()).isEqualTo(100)).thenRequest(9).expectNext(8, 9, 10, 11, 12, 13, 14, 15).verifyComplete();
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxOnBackpressureBuffer.BackpressureBufferSubscriber<Integer> test = new FluxOnBackpressureBuffer.BackpressureBufferSubscriber<>(actual, 123, false, true, ( t) -> {
        });
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(DELAY_ERROR)).isTrue();
        test.requested = 35;
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(BUFFERED)).isEqualTo(0);// RS: TODO non-zero size

        assertThat(test.scan(ERROR)).isNull();
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(ERROR)).isSameAs(test.error);
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

