/**
 * Copyright 2018 Julien Hoarau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.resilience4j.reactor.bulkhead.operator;


import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import java.io.IOException;
import java.time.Duration;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;


public class FluxBulkheadTest {
    private Bulkhead bulkhead = Bulkhead.of("test", BulkheadConfig.custom().maxConcurrentCalls(1).maxWaitTime(0).build());

    @Test
    public void shouldEmitEvent() {
        StepVerifier.create(Flux.just("Event 1", "Event 2").transform(BulkheadOperator.of(bulkhead))).expectNext("Event 1").expectNext("Event 2").verifyComplete();
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldPropagateError() {
        StepVerifier.create(Flux.error(new IOException("BAM!")).transform(BulkheadOperator.of(bulkhead))).expectSubscription().expectError(IOException.class).verify(Duration.ofSeconds(1));
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(1);
    }

    @Test
    public void shouldEmitErrorWithBulkheadFullException() {
        bulkhead.isCallPermitted();
        StepVerifier.create(Flux.just("Event").transform(BulkheadOperator.of(bulkhead))).expectSubscription().expectError(BulkheadFullException.class).verify(Duration.ofSeconds(1));
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }

    @Test
    public void shouldEmitBulkheadFullExceptionEvenWhenErrorDuringSubscribe() {
        bulkhead.isCallPermitted();
        StepVerifier.create(Flux.error(new IOException("BAM!")).transform(BulkheadOperator.of(bulkhead, Schedulers.immediate()))).expectSubscription().expectError(BulkheadFullException.class).verify(Duration.ofSeconds(1));
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }

    @Test
    public void shouldEmitBulkheadFullExceptionEvenWhenErrorNotOnSubscribe() {
        bulkhead.isCallPermitted();
        StepVerifier.create(Flux.error(new IOException("BAM!"), true).transform(BulkheadOperator.of(bulkhead, Schedulers.immediate()))).expectSubscription().expectError(BulkheadFullException.class).verify(Duration.ofSeconds(1));
        assertThat(bulkhead.getMetrics().getAvailableConcurrentCalls()).isEqualTo(0);
    }
}

