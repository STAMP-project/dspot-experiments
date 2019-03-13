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
package io.github.resilience4j.reactor.circuitbreaker.operator;


import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import java.io.IOException;
import java.time.Duration;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class FluxCircuitBreakerTest extends CircuitBreakerAssertions {
    @Test
    public void shouldEmitEvent() {
        StepVerifier.create(Flux.just("Event 1", "Event 2").transform(CircuitBreakerOperator.of(circuitBreaker))).expectNext("Event 1").expectNext("Event 2").verifyComplete();
        assertSingleSuccessfulCall();
    }

    @Test
    public void shouldPropagateError() {
        StepVerifier.create(Flux.error(new IOException("BAM!")).transform(CircuitBreakerOperator.of(circuitBreaker))).expectError(IOException.class).verify(Duration.ofSeconds(1));
        assertSingleFailedCall();
    }

    @Test
    public void shouldPropagateErrorWhenErrorNotOnSubscribe() {
        StepVerifier.create(Flux.error(new IOException("BAM!"), true).transform(CircuitBreakerOperator.of(circuitBreaker))).expectError(IOException.class).verify(Duration.ofSeconds(1));
        assertSingleFailedCall();
    }

    @Test
    public void shouldEmitErrorWithCircuitBreakerOpenException() {
        circuitBreaker.transitionToOpenState();
        StepVerifier.create(Flux.just("Event 1", "Event 2").transform(CircuitBreakerOperator.of(circuitBreaker))).expectError(CircuitBreakerOpenException.class).verify(Duration.ofSeconds(1));
        assertNoRegisteredCall();
    }

    @Test
    public void shouldEmitCircuitBreakerOpenExceptionEvenWhenErrorNotOnSubscribe() {
        circuitBreaker.transitionToOpenState();
        StepVerifier.create(Flux.error(new IOException("BAM!"), true).transform(CircuitBreakerOperator.of(circuitBreaker))).expectError(CircuitBreakerOpenException.class).verify(Duration.ofSeconds(1));
        assertNoRegisteredCall();
    }

    @Test
    public void shouldEmitCircuitBreakerOpenExceptionEvenWhenErrorDuringSubscribe() {
        circuitBreaker.transitionToOpenState();
        StepVerifier.create(Flux.error(new IOException("BAM!")).transform(CircuitBreakerOperator.of(circuitBreaker))).expectError(CircuitBreakerOpenException.class).verify(Duration.ofSeconds(1));
        assertNoRegisteredCall();
    }
}

