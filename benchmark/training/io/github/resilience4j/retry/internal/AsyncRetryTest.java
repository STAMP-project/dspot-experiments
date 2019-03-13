package io.github.resilience4j.retry.internal;


import io.github.resilience4j.retry.AsyncRetry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.utils.AsyncUtils;
import io.github.resilience4j.test.AsyncHelloWorldService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import javax.xml.ws.WebServiceException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class AsyncRetryTest {
    private AsyncHelloWorldService helloWorldService;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void shouldNotRetry() throws InterruptedException, ExecutionException, TimeoutException {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(CompletableFuture.completedFuture("Hello world"));
        // Create a Retry with default configuration
        AsyncRetry retryContext = AsyncRetry.ofDefaults("id");
        // Decorate the invocation of the HelloWorldService
        Supplier<CompletionStage<String>> supplier = AsyncRetry.decorateCompletionStage(retryContext, scheduler, () -> helloWorldService.returnHelloWorld());
        // When
        String result = AsyncUtils.awaitResult(supplier);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
        Assertions.assertThat(result).isEqualTo("Hello world");
    }

    @Test
    public void shouldNotRetryWithThatResult() throws InterruptedException, ExecutionException, TimeoutException {
        // Given the HelloWorldService returns Hello world
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(CompletableFuture.completedFuture("Hello world"));
        // Create a Retry with default configuration
        final RetryConfig retryConfig = RetryConfig.<String>custom().retryOnResult(( s) -> s.contains("NoRetry")).maxAttempts(1).build();
        AsyncRetry retryContext = AsyncRetry.of("id", retryConfig);
        // Decorate the invocation of the HelloWorldService
        Supplier<CompletionStage<String>> supplier = AsyncRetry.decorateCompletionStage(retryContext, scheduler, () -> helloWorldService.returnHelloWorld());
        // When
        String result = AsyncUtils.awaitResult(supplier);
        // Then the helloWorldService should be invoked 1 time
        BDDMockito.then(helloWorldService).should(Mockito.times(1)).returnHelloWorld();
        Assertions.assertThat(result).isEqualTo("Hello world");
        // for code quality scan , it does not not recognize assertJ do not why
        Assert.assertEquals(result, "Hello world");
    }

    @Test
    public void shouldRetryInCaseOResultRetryMatchAtSyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfRetyOnResultAtAsyncStage(1, "Hello world");
    }

    @Test
    public void shouldRetryTowAttemptsInCaseOResultRetryMatchAtSyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfRetyOnResultAtAsyncStage(2, "Hello world");
    }

    @Test
    public void shouldRetryInCaseOfExceptionAtSyncStage() {
        // Given the HelloWorldService throws an exception
        BDDMockito.given(helloWorldService.returnHelloWorld()).willThrow(new WebServiceException("BAM!")).willReturn(CompletableFuture.completedFuture("Hello world"));
        // Create a Retry with default configuration
        AsyncRetry retryContext = AsyncRetry.ofDefaults("id");
        // Decorate the invocation of the HelloWorldService
        Supplier<CompletionStage<String>> supplier = AsyncRetry.decorateCompletionStage(retryContext, scheduler, () -> helloWorldService.returnHelloWorld());
        // When
        String result = AsyncUtils.awaitResult(supplier.get());
        // Then the helloWorldService should be invoked 2 times
        BDDMockito.then(helloWorldService).should(Mockito.times(2)).returnHelloWorld();
        Assertions.assertThat(result).isEqualTo("Hello world");
    }

    @Test
    public void shouldRetryInCaseOfAnExceptionAtAsyncStage() {
        CompletableFuture<String> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new WebServiceException("BAM!"));
        // Given the HelloWorldService throws an exception
        BDDMockito.given(helloWorldService.returnHelloWorld()).willReturn(failedFuture).willReturn(CompletableFuture.completedFuture("Hello world"));
        // Create a Retry with default configuration
        AsyncRetry retryContext = AsyncRetry.ofDefaults("id");
        // Decorate the invocation of the HelloWorldService
        Supplier<CompletionStage<String>> supplier = AsyncRetry.decorateCompletionStage(retryContext, scheduler, () -> helloWorldService.returnHelloWorld());
        // When
        String result = AsyncUtils.awaitResult(supplier.get());
        // Then the helloWorldService should be invoked 2 times
        BDDMockito.then(helloWorldService).should(Mockito.times(2)).returnHelloWorld();
        Assertions.assertThat(result).isEqualTo("Hello world");
    }

    @Test
    public void shouldCompleteFutureAfterOneAttemptInCaseOfExceptionAtSyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfExceptionAtSyncStage(1);
    }

    @Test
    public void shouldCompleteFutureAfterTwoAttemptsInCaseOfExceptionAtSyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfExceptionAtSyncStage(2);
    }

    @Test
    public void shouldCompleteFutureAfterThreeAttemptsInCaseOfExceptionAtSyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfExceptionAtSyncStage(3);
    }

    @Test
    public void shouldCompleteFutureAfterOneAttemptInCaseOfExceptionAtAsyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfExceptionAtAsyncStage(1);
    }

    @Test
    public void shouldCompleteFutureAfterTwoAttemptsInCaseOfExceptionAtAsyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfExceptionAtAsyncStage(2);
    }

    @Test
    public void shouldCompleteFutureAfterThreeAttemptsInCaseOfExceptionAtAsyncStage() {
        shouldCompleteFutureAfterAttemptsInCaseOfExceptionAtAsyncStage(3);
    }
}

