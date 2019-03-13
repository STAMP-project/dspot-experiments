/**
 * Copyright 2018 David Rusek
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
package io.github.resilience4j.metrics;


import com.codahale.metrics.MetricRegistry;
import io.github.resilience4j.retry.AsyncRetry;
import io.github.resilience4j.retry.AsyncRetryRegistry;
import io.github.resilience4j.test.AsyncHelloWorldService;
import io.vavr.control.Try;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.xml.ws.WebServiceException;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class AsyncRetryMetricsTest {
    private MetricRegistry metricRegistry;

    private AsyncHelloWorldService helloWorldService;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void shouldRegisterMetricsWithoutRetry() {
        // Given
        AsyncRetryRegistry retryRegistry = AsyncRetryRegistry.ofDefaults();
        AsyncRetry retry = retryRegistry.retry("testName");
        metricRegistry.registerAll(AsyncRetryMetrics.ofAsyncRetryRegistry(retryRegistry));
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(CompletableFuture.completedFuture("Hello world"));
        // Setup circuitbreaker with retry
        String value = AsyncRetryMetricsTest.awaitResult(retry.executeCompletionStage(scheduler, helloWorldService::returnHelloWorld));
        // Then
        assertThat(value).isEqualTo("Hello world");
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
        assertThat(metricRegistry.getMetrics()).hasSize(4);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
    }

    @Test
    public void shouldRegisterMetricsWithRetry() {
        // Given
        AsyncRetryRegistry retryRegistry = AsyncRetryRegistry.ofDefaults();
        AsyncRetry retry = retryRegistry.retry("testName");
        metricRegistry.registerAll(AsyncRetryMetrics.ofAsyncRetryRegistry(retryRegistry));
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(AsyncRetryMetricsTest.failedFuture(new WebServiceException("BAM!"))).willReturn(CompletableFuture.completedFuture("Hello world")).willReturn(AsyncRetryMetricsTest.failedFuture(new WebServiceException("BAM!"))).willReturn(AsyncRetryMetricsTest.failedFuture(new WebServiceException("BAM!"))).willReturn(AsyncRetryMetricsTest.failedFuture(new WebServiceException("BAM!")));
        // Setup circuitbreaker with retry
        String value1 = AsyncRetryMetricsTest.awaitResult(retry.executeCompletionStage(scheduler, helloWorldService::returnHelloWorld));
        Try.ofCallable(() -> awaitResult(AsyncRetry.decorateCompletionStage(retry, scheduler, helloWorldService::returnHelloWorld).get()));
        // Then
        assertThat(value1).isEqualTo("Hello world");
        // Then the helloWorldService should be invoked 5 times
        BDDMockito.then(helloWorldService).should(Mockito.times(5)).returnHelloWorld();
        assertThat(metricRegistry.getMetrics()).hasSize(4);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITH_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (SUCCESSFUL_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITH_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("resilience4j.retry.testName." + (FAILED_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
    }

    @Test
    public void shouldUseCustomPrefix() {
        // Given
        AsyncRetryRegistry retryRegistry = AsyncRetryRegistry.ofDefaults();
        AsyncRetry retry = retryRegistry.retry("testName");
        metricRegistry.registerAll(AsyncRetryMetrics.ofAsyncRetryRegistry("testPrefix", retryRegistry));
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(CompletableFuture.completedFuture("Hello world"));
        String value = AsyncRetryMetricsTest.awaitResult(retry.executeCompletionStage(scheduler, helloWorldService::returnHelloWorld));
        // Then
        assertThat(value).isEqualTo("Hello world");
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
        assertThat(metricRegistry.getMetrics()).hasSize(4);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (SUCCESSFUL_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (SUCCESSFUL_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(1L);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (FAILED_CALLS_WITH_RETRY))).getValue()).isEqualTo(0L);
        assertThat(metricRegistry.getGauges().get(("testPrefix.testName." + (FAILED_CALLS_WITHOUT_RETRY))).getValue()).isEqualTo(0L);
    }

    private static class RuntimeExecutionException extends RuntimeException {
        RuntimeExecutionException(Throwable cause) {
            super(cause);
        }
    }
}

