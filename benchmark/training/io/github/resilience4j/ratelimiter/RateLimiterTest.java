/**
 * Copyright 2016 Robert Winkler and Bohdan Storozhuk
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
package io.github.resilience4j.ratelimiter;


import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class RateLimiterTest {
    private static final int LIMIT = 50;

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private static final Duration REFRESH_PERIOD = Duration.ofNanos(500);

    private RateLimiterConfig config;

    private RateLimiter limit;

    @Test
    public void decorateCheckedSupplier() throws Throwable {
        CheckedFunction0 supplier = Mockito.mock(CheckedFunction0.class);
        CheckedFunction0 decorated = RateLimiter.decorateCheckedSupplier(limit, supplier);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        Try decoratedSupplierResult = Try.of(decorated);
        then(decoratedSupplierResult.isFailure()).isTrue();
        then(decoratedSupplierResult.getCause()).isInstanceOf(RequestNotPermitted.class);
        Mockito.verify(supplier, Mockito.never()).apply();
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        Try secondSupplierResult = Try.of(decorated);
        then(secondSupplierResult.isSuccess()).isTrue();
        Mockito.verify(supplier, Mockito.times(1)).apply();
    }

    @Test
    public void decorateCheckedRunnable() throws Throwable {
        CheckedRunnable runnable = Mockito.mock(CheckedRunnable.class);
        CheckedRunnable decorated = RateLimiter.decorateCheckedRunnable(limit, runnable);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        Try decoratedRunnableResult = Try.run(decorated);
        then(decoratedRunnableResult.isFailure()).isTrue();
        then(decoratedRunnableResult.getCause()).isInstanceOf(RequestNotPermitted.class);
        Mockito.verify(runnable, Mockito.never()).run();
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        Try secondRunnableResult = Try.run(decorated);
        then(secondRunnableResult.isSuccess()).isTrue();
        Mockito.verify(runnable, Mockito.times(1)).run();
    }

    @Test
    public void decorateCheckedFunction() throws Throwable {
        CheckedFunction1<Integer, String> function = Mockito.mock(CheckedFunction1.class);
        CheckedFunction1<Integer, String> decorated = RateLimiter.decorateCheckedFunction(limit, function);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        Try<String> decoratedFunctionResult = Try.success(1).mapTry(decorated);
        then(decoratedFunctionResult.isFailure()).isTrue();
        then(decoratedFunctionResult.getCause()).isInstanceOf(RequestNotPermitted.class);
        Mockito.verify(function, Mockito.never()).apply(ArgumentMatchers.any());
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        Try secondFunctionResult = Try.success(1).mapTry(decorated);
        then(secondFunctionResult.isSuccess()).isTrue();
        Mockito.verify(function, Mockito.times(1)).apply(1);
    }

    @Test
    public void decorateSupplier() throws Exception {
        Supplier supplier = Mockito.mock(Supplier.class);
        Supplier decorated = RateLimiter.decorateSupplier(limit, supplier);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        Try decoratedSupplierResult = Try.success(decorated).map(Supplier::get);
        then(decoratedSupplierResult.isFailure()).isTrue();
        then(decoratedSupplierResult.getCause()).isInstanceOf(RequestNotPermitted.class);
        Mockito.verify(supplier, Mockito.never()).get();
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        Try secondSupplierResult = Try.success(decorated).map(Supplier::get);
        then(secondSupplierResult.isSuccess()).isTrue();
        Mockito.verify(supplier, Mockito.times(1)).get();
    }

    @Test
    public void decorateConsumer() throws Exception {
        Consumer<Integer> consumer = Mockito.mock(Consumer.class);
        Consumer<Integer> decorated = RateLimiter.decorateConsumer(limit, consumer);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        Try<Integer> decoratedConsumerResult = Try.success(1).andThen(decorated);
        then(decoratedConsumerResult.isFailure()).isTrue();
        then(decoratedConsumerResult.getCause()).isInstanceOf(RequestNotPermitted.class);
        Mockito.verify(consumer, Mockito.never()).accept(ArgumentMatchers.any());
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        Try secondConsumerResult = Try.success(1).andThen(decorated);
        then(secondConsumerResult.isSuccess()).isTrue();
        Mockito.verify(consumer, Mockito.times(1)).accept(1);
    }

    @Test
    public void decorateRunnable() throws Exception {
        Runnable runnable = Mockito.mock(Runnable.class);
        Runnable decorated = RateLimiter.decorateRunnable(limit, runnable);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        Try decoratedRunnableResult = Try.success(decorated).andThen(Runnable::run);
        then(decoratedRunnableResult.isFailure()).isTrue();
        then(decoratedRunnableResult.getCause()).isInstanceOf(RequestNotPermitted.class);
        Mockito.verify(runnable, Mockito.never()).run();
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        Try secondRunnableResult = Try.success(decorated).andThen(Runnable::run);
        then(secondRunnableResult.isSuccess()).isTrue();
        Mockito.verify(runnable, Mockito.times(1)).run();
    }

    @Test
    public void decorateFunction() throws Exception {
        Function<Integer, String> function = Mockito.mock(Function.class);
        Function<Integer, String> decorated = RateLimiter.decorateFunction(limit, function);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        Try<String> decoratedFunctionResult = Try.success(1).map(decorated);
        then(decoratedFunctionResult.isFailure()).isTrue();
        then(decoratedFunctionResult.getCause()).isInstanceOf(RequestNotPermitted.class);
        Mockito.verify(function, Mockito.never()).apply(ArgumentMatchers.any());
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        Try secondFunctionResult = Try.success(1).map(decorated);
        then(secondFunctionResult.isSuccess()).isTrue();
        Mockito.verify(function, Mockito.times(1)).apply(1);
    }

    @Test
    public void decorateCompletionStage() throws Exception {
        Supplier supplier = Mockito.mock(Supplier.class);
        BDDMockito.given(supplier.get()).willReturn("Resource");
        Supplier<CompletionStage<String>> completionStage = () -> CompletableFuture.supplyAsync(supplier);
        Supplier<CompletionStage<String>> decorated = RateLimiter.decorateCompletionStage(limit, completionStage);
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        AtomicReference<Throwable> error = new AtomicReference<>(null);
        CompletableFuture<String> notPermittedFuture = decorated.get().whenComplete(( v, e) -> error.set(e)).toCompletableFuture();
        Try<String> errorResult = Try.of(notPermittedFuture::get);
        Assert.assertTrue(errorResult.isFailure());
        then(errorResult.getCause()).isInstanceOf(ExecutionException.class);
        then(notPermittedFuture.isCompletedExceptionally()).isTrue();
        then(error.get()).isExactlyInstanceOf(RequestNotPermitted.class);
        Mockito.verify(supplier, Mockito.never()).get();
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        AtomicReference<Throwable> shouldBeEmpty = new AtomicReference<>(null);
        CompletableFuture<String> success = decorated.get().whenComplete(( v, e) -> error.set(e)).toCompletableFuture();
        Try<String> successResult = Try.of(success::get);
        then(successResult.isSuccess()).isTrue();
        then(success.isCompletedExceptionally()).isFalse();
        then(shouldBeEmpty.get()).isNull();
        Mockito.verify(supplier).get();
    }

    @Test
    public void waitForPermissionWithOne() throws Exception {
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(true);
        RateLimiter.waitForPermission(limit);
        Mockito.verify(limit, Mockito.times(1)).getPermission(config.getTimeoutDuration());
    }

    @Test(expected = RequestNotPermitted.class)
    public void waitForPermissionWithoutOne() throws Exception {
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).thenReturn(false);
        RateLimiter.waitForPermission(limit);
        Mockito.verify(limit, Mockito.times(1)).getPermission(config.getTimeoutDuration());
    }

    @Test
    public void waitForPermissionWithInterruption() throws Exception {
        Mockito.when(limit.getPermission(config.getTimeoutDuration())).then(( invocation) -> {
            LockSupport.parkNanos(5000000000L);
            return null;
        });
        AtomicBoolean wasInterrupted = new AtomicBoolean(true);
        Thread thread = new Thread(() -> {
            wasInterrupted.set(false);
            Throwable cause = Try.run(() -> RateLimiter.waitForPermission(limit)).getCause();
            Boolean interrupted = Match(cause).of(Case($(instanceOf(IllegalStateException.class)), true));
            wasInterrupted.set(interrupted);
        });
        thread.setDaemon(true);
        thread.start();
        await().atMost(5, TimeUnit.SECONDS).untilFalse(wasInterrupted);
        thread.interrupt();
        await().atMost(5, TimeUnit.SECONDS).untilTrue(wasInterrupted);
    }

    @Test
    public void construction() throws Exception {
        RateLimiter rateLimiter = RateLimiter.of("test", () -> config);
        then(rateLimiter).isNotNull();
    }
}

