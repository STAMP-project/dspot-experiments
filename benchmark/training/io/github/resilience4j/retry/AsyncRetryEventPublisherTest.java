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
package io.github.resilience4j.retry;


import AsyncRetry.EventPublisher;
import io.github.resilience4j.retry.utils.AsyncUtils;
import io.github.resilience4j.test.AsyncHelloWorldService;
import io.vavr.control.Try;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.xml.ws.WebServiceException;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class AsyncRetryEventPublisherTest {
    private AsyncHelloWorldService helloWorldService;

    private Logger logger;

    private AsyncRetry retry;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void shouldReturnTheSameConsumer() {
        AsyncRetry.EventPublisher eventPublisher = retry.getEventPublisher();
        AsyncRetry.EventPublisher eventPublisher2 = retry.getEventPublisher();
        assertThat(eventPublisher).isEqualTo(eventPublisher2);
    }

    @Test
    public void shouldConsumeOnSuccessEvent() throws Exception {
        CompletableFuture<String> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new WebServiceException("BAM!"));
        CountDownLatch latch = new CountDownLatch(1);
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(failedFuture).willReturn(CompletableFuture.completedFuture("Hello world"));
        retry.getEventPublisher().onSuccess(( event) -> {
            logger.info(event.getEventType().toString());
            latch.countDown();
        });
        String result = AsyncUtils.awaitResult(retry.executeCompletionStage(scheduler, () -> helloWorldService.returnHelloWorld()));
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        assertThat(result).isEqualTo("Hello world");
        BDDMockito.then(helloWorldService).should(Mockito.times(2)).returnHelloWorld();
        BDDMockito.then(logger).should(Mockito.times(1)).info("SUCCESS");
    }

    @Test
    public void shouldConsumeOnRetryEvent() {
        CompletableFuture<String> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new WebServiceException("BAM!"));
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(failedFuture);
        retry.getEventPublisher().onRetry(( event) -> logger.info(event.getEventType().toString()));
        Try.of(() -> awaitResult(retry.executeCompletionStage(scheduler, () -> helloWorldService.returnHelloWorld())));
        BDDMockito.then(logger).should(Mockito.times(2)).info("RETRY");
        BDDMockito.then(helloWorldService).should(Mockito.times(3)).returnHelloWorld();
    }

    @Test
    public void shouldConsumeOnErrorEvent() {
        CompletableFuture<String> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new WebServiceException("BAM!"));
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(failedFuture);
        retry.getEventPublisher().onError(( event) -> logger.info(event.getEventType().toString()));
        Try.of(() -> awaitResult(retry.executeCompletionStage(scheduler, () -> helloWorldService.returnHelloWorld())));
        BDDMockito.then(logger).should(Mockito.times(1)).info("ERROR");
        BDDMockito.then(helloWorldService).should(Mockito.times(3)).returnHelloWorld();
    }

    @Test
    public void shouldConsumeIgnoredErrorEvent() {
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new WebServiceException("BAM!"));
        RetryConfig retryConfig = RetryConfig.custom().retryOnException(( throwable) -> Match(throwable).of(Case($(instanceOf(.class)), false), Case($(), true))).build();
        retry = AsyncRetry.of("testName", retryConfig);
        retry.getEventPublisher().onIgnoredError(( event) -> logger.info(event.getEventType().toString()));
        Try.of(() -> awaitResult(retry.executeCompletionStage(scheduler, () -> helloWorldService.returnHelloWorld())));
        BDDMockito.then(logger).should(Mockito.times(1)).info("IGNORED_ERROR");
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
    }
}

