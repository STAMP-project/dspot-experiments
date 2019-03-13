/**
 * Copyright 2016 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.circuitbreaker;


import CircuitBreaker.Metrics;
import CircuitBreaker.State.OPEN;
import io.github.resilience4j.circuitbreaker.test.VertxHelloWorldService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


@RunWith(VertxUnitRunner.class)
public class VertxCircuitBreakerTest {
    private Vertx vertx;

    private VertxHelloWorldService helloWorldService;

    private static final int DEFAULT_TIMEOUT = 1000;

    @Test
    public void shouldExecuteFutureAndReturnWithSuccess() {
        // Given
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(0);
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(Future.succeededFuture("Hello world"));
        // When
        Future<String> future = VertxCircuitBreaker.executeFuture(circuitBreaker, helloWorldService::returnHelloWorld);
        // Then
        assertThat(future.succeeded()).isTrue();
        assertThat(future.result()).isEqualTo("Hello world");
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(0);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
    }

    @Test
    public void shouldExecuteFutureAndReturnWithException() {
        // Given
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testName");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(0);
        // Given the HelloWorldService throws an exception
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(Future.failedFuture(new RuntimeException("BAM!")));
        // When
        Future<String> future = VertxCircuitBreaker.executeFuture(circuitBreaker, helloWorldService::returnHelloWorld);
        // Then
        assertThat(future.failed()).isTrue();
        assertThat(future.cause()).isInstanceOf(RuntimeException.class);
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(0);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
    }

    @Test
    public void shouldReturnFailureWithCircuitBreakerOpenException() {
        // Given
        // Create a custom configuration for a CircuitBreaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().ringBufferSizeInClosedState(2).ringBufferSizeInHalfOpenState(2).failureRateThreshold(50).waitDurationInOpenState(Duration.ofMillis(1000)).build();
        // Create a CircuitBreakerRegistry with a custom global configuration
        CircuitBreaker circuitBreaker = CircuitBreaker.of("testName", circuitBreakerConfig);
        circuitBreaker.onError(0, new RuntimeException());
        circuitBreaker.onError(0, new RuntimeException());
        assertThat(circuitBreaker.getState()).isEqualTo(OPEN);
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(2);
        assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(2);
        // When
        // When
        Future<String> future = VertxCircuitBreaker.executeFuture(circuitBreaker, helloWorldService::returnHelloWorld);
        // Then
        // Then
        assertThat(future.failed()).isTrue();
        assertThat(future.cause()).isInstanceOf(CircuitBreakerOpenException.class);
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(2);
        assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(2);
        assertThat(metrics.getNumberOfNotPermittedCalls()).isEqualTo(1);
    }
}

