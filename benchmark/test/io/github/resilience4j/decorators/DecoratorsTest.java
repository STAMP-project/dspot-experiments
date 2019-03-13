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
package io.github.resilience4j.decorators;


import CircuitBreaker.Metrics;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.AsyncRetry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.test.HelloWorldService;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.cache.Cache;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class DecoratorsTest {
    public boolean state = false;

    private HelloWorldService helloWorldService;

    @Test
    public void testDecorateSupplier() {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn("Hello world");
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        Supplier<String> decoratedSupplier = Decorators.ofSupplier(() -> helloWorldService.returnHelloWorld()).withCircuitBreaker(circuitBreaker).withRetry(Retry.ofDefaults("id")).withRateLimiter(RateLimiter.ofDefaults("testName")).withBulkhead(Bulkhead.ofDefaults("testName")).decorate();
        String result = decoratedSupplier.get();
        assertThat(result).isEqualTo("Hello world");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
    }

    @Test
    public void testDecorateCheckedSupplier() throws IOException {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorldWithException()).willReturn("Hello world");
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        CheckedFunction0<String> decoratedSupplier = Decorators.ofCheckedSupplier(() -> helloWorldService.returnHelloWorldWithException()).withCircuitBreaker(circuitBreaker).withRetry(Retry.ofDefaults("id")).withRateLimiter(RateLimiter.ofDefaults("testName")).withBulkhead(Bulkhead.ofDefaults("testName")).decorate();
        String result = Try.of(decoratedSupplier).get();
        assertThat(result).isEqualTo("Hello world");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorldWithException();
    }

    @Test
    public void testDecorateRunnable() {
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        Runnable decoratedRunnable = Decorators.ofRunnable(() -> helloWorldService.sayHelloWorld()).withCircuitBreaker(circuitBreaker).withRetry(Retry.ofDefaults("id")).withRateLimiter(RateLimiter.ofDefaults("testName")).withBulkhead(Bulkhead.ofDefaults("testName")).decorate();
        decoratedRunnable.run();
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).sayHelloWorld();
    }

    @Test
    public void testDecorateCheckedRunnable() throws IOException {
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        CheckedRunnable decoratedRunnable = Decorators.ofCheckedRunnable(() -> helloWorldService.sayHelloWorldWithException()).withCircuitBreaker(circuitBreaker).withRetry(Retry.ofDefaults("id")).withRateLimiter(RateLimiter.ofDefaults("testName")).withBulkhead(Bulkhead.ofDefaults("testName")).decorate();
        Try.run(decoratedRunnable);
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).sayHelloWorldWithException();
    }

    @Test
    public void testDecorateCompletionStage() throws InterruptedException, ExecutionException {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn("Hello world");
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        Supplier<CompletionStage<String>> completionStageSupplier = () -> CompletableFuture.supplyAsync(helloWorldService::returnHelloWorld);
        CompletionStage<String> completionStage = Decorators.ofCompletionStage(completionStageSupplier).withCircuitBreaker(circuitBreaker).withRetry(AsyncRetry.ofDefaults("id"), Executors.newSingleThreadScheduledExecutor()).withBulkhead(Bulkhead.ofDefaults("testName")).get();
        String value = completionStage.toCompletableFuture().get();
        assertThat(value).isEqualTo("Hello world");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
    }

    @Test
    public void testExecuteConsumer() throws InterruptedException, ExecutionException {
        // Given the HelloWorldService returns Hello world
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        Decorators.ofConsumer((String input) -> helloWorldService.sayHelloWorldWithName(input)).withCircuitBreaker(circuitBreaker).withBulkhead(Bulkhead.ofDefaults("testName")).withRateLimiter(RateLimiter.ofDefaults("testName")).accept("test");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).sayHelloWorldWithName("test");
    }

    @Test
    public void testDecorateFunction() {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorldWithName("Name")).willReturn("Hello world Name");
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        Function<String, String> decoratedFunction = Decorators.ofFunction(helloWorldService::returnHelloWorldWithName).withCircuitBreaker(circuitBreaker).withRetry(Retry.ofDefaults("id")).withRateLimiter(RateLimiter.ofDefaults("testName")).withBulkhead(Bulkhead.ofDefaults("testName")).decorate();
        String result = decoratedFunction.apply("Name");
        assertThat(result).isEqualTo("Hello world Name");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
    }

    @Test
    public void testDecorateCheckedFunction() throws IOException {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorldWithNameWithException("Name")).willReturn("Hello world Name");
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        CheckedFunction1<String, String> decoratedFunction = Decorators.ofCheckedFunction(helloWorldService::returnHelloWorldWithNameWithException).withCircuitBreaker(circuitBreaker).withRetry(Retry.ofDefaults("id")).withRateLimiter(RateLimiter.ofDefaults("testName")).withBulkhead(Bulkhead.ofDefaults("testName")).decorate();
        String result = Try.of(() -> decoratedFunction.apply("Name")).get();
        assertThat(result).isEqualTo("Hello world Name");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(1);
        assertThat(metrics.getNumberOfSuccessfulCalls()).isEqualTo(1);
    }

    @Test
    public void testDecoratorBuilderWithRetry() {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new RuntimeException("BAM!"));
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("helloBackend");
        Supplier<String> decoratedSupplier = Decorators.ofSupplier(() -> helloWorldService.returnHelloWorld()).withCircuitBreaker(circuitBreaker).withRetry(Retry.ofDefaults("id")).withBulkhead(Bulkhead.ofDefaults("testName")).decorate();
        Try.of(decoratedSupplier::get);
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfBufferedCalls()).isEqualTo(3);
        assertThat(metrics.getNumberOfFailedCalls()).isEqualTo(3);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(3)).returnHelloWorld();
    }

    @Test
    public void testDecoratorBuilderWithRateLimiter() {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn("Hello world");
        // Create a custom RateLimiter configuration
        RateLimiterConfig config = RateLimiterConfig.custom().timeoutDuration(Duration.ofMillis(100)).limitRefreshPeriod(Duration.ofSeconds(1)).limitForPeriod(1).build();
        // Create a RateLimiter
        RateLimiter rateLimiter = RateLimiter.of("backendName", config);
        CheckedFunction0<String> restrictedSupplier = Decorators.ofCheckedSupplier(() -> helloWorldService.returnHelloWorld()).withRateLimiter(rateLimiter).decorate();
        alignTime(rateLimiter);
        Try<String> firstTry = Try.of(restrictedSupplier);
        assertThat(firstTry.isSuccess()).isTrue();
        Try<String> secondTry = Try.of(restrictedSupplier);
        assertThat(secondTry.isFailure()).isTrue();
        assertThat(secondTry.getCause()).isInstanceOf(RequestNotPermitted.class);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDecorateCheckedSupplierWithCache() {
        Cache<String, String> cache = Mockito.mock(io.github.resilience4j.cache.Cache.class);
        // Given the cache contains the key
        BDDMockito.given(cache.containsKey("testKey")).willReturn(true);
        // Return the value from cache
        BDDMockito.given(cache.get("testKey")).willReturn("Hello from cache");
        CheckedFunction1<String, String> cachedFunction = Decorators.ofCheckedSupplier(() -> "Hello world").withCache(io.github.resilience4j.cache.Cache.of(cache)).decorate();
        String value = Try.of(() -> cachedFunction.apply("testKey")).get();
        assertThat(value).isEqualTo("Hello from cache");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDecorateSupplierWithCache() {
        Cache<String, String> cache = Mockito.mock(io.github.resilience4j.cache.Cache.class);
        // Given the cache contains the key
        BDDMockito.given(cache.containsKey("testKey")).willReturn(true);
        // Return the value from cache
        BDDMockito.given(cache.get("testKey")).willReturn("Hello from cache");
        Function<String, String> cachedFunction = Decorators.ofSupplier(() -> "Hello world").withCache(io.github.resilience4j.cache.Cache.of(cache)).decorate();
        String value = cachedFunction.apply("testKey");
        assertThat(value).isEqualTo("Hello from cache");
    }
}

