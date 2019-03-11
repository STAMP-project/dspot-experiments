package io.github.resilience4j.timelimiter;


import io.vavr.control.Try;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.junit.Test;
import org.mockito.Mockito;


public class TimeLimiterTest {
    private static final Duration SHORT_TIMEOUT = Duration.ofNanos(1);

    private static final Duration LONG_TIMEOUT = Duration.ofSeconds(15);

    private static final Duration SLEEP_DURATION = Duration.ofSeconds(5);

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private TimeLimiterConfig shortConfig;

    private TimeLimiterConfig longConfig;

    private TimeLimiter timeLimiter;

    @Test
    public void construction() throws Exception {
        TimeLimiter timeLimiter = TimeLimiter.of(shortConfig);
        then(timeLimiter).isNotNull();
    }

    @Test
    public void defaultConstruction() throws Exception {
        TimeLimiter timeLimiter = TimeLimiter.ofDefaults();
        then(timeLimiter).isNotNull();
    }

    @Test
    public void durationConstruction() throws Exception {
        TimeLimiter timeLimiter = TimeLimiter.of(TimeLimiterTest.SHORT_TIMEOUT);
        then(timeLimiter).isNotNull();
        then(timeLimiter.getTimeLimiterConfig().getTimeoutDuration()).isEqualTo(TimeLimiterTest.SHORT_TIMEOUT);
    }

    @Test
    public void decorateFutureSupplier() throws Throwable {
        Mockito.when(timeLimiter.getTimeLimiterConfig()).thenReturn(shortConfig);
        Future<Integer> future = TimeLimiterTest.EXECUTOR_SERVICE.submit(() -> {
            Thread.sleep(TimeLimiterTest.SLEEP_DURATION.toMillis());
            return 1;
        });
        Supplier<Future<Integer>> supplier = () -> future;
        Callable<Integer> decorated = TimeLimiter.decorateFutureSupplier(timeLimiter, supplier);
        Try decoratedResult = Try.success(decorated).mapTry(Callable::call);
        then(decoratedResult.isFailure()).isTrue();
        then(decoratedResult.getCause()).isInstanceOf(TimeoutException.class);
        then(future.isCancelled()).isTrue();
        Mockito.when(timeLimiter.getTimeLimiterConfig()).thenReturn(longConfig);
        Future<Integer> secondFuture = TimeLimiterTest.EXECUTOR_SERVICE.submit(() -> {
            Thread.sleep(TimeLimiterTest.SLEEP_DURATION.toMillis());
            return 1;
        });
        supplier = () -> secondFuture;
        decorated = TimeLimiter.decorateFutureSupplier(timeLimiter, supplier);
        Try secondResult = Try.success(decorated).mapTry(Callable::call);
        then(secondResult.isSuccess()).isTrue();
    }

    @Test
    public void executeFutureSupplier() throws Throwable {
        Future<Integer> future = TimeLimiterTest.EXECUTOR_SERVICE.submit(() -> {
            Thread.sleep(TimeLimiterTest.SLEEP_DURATION.toMillis());
            return 1;
        });
        Supplier<Future<Integer>> supplier = () -> future;
        Try decoratedResult = Try.of(() -> TimeLimiter.of(shortConfig).executeFutureSupplier(supplier));
        then(decoratedResult.isFailure()).isTrue();
        then(decoratedResult.getCause()).isInstanceOf(TimeoutException.class);
        then(future.isCancelled()).isTrue();
        Future<Integer> secondFuture = TimeLimiterTest.EXECUTOR_SERVICE.submit(() -> {
            Thread.sleep(TimeLimiterTest.SLEEP_DURATION.toMillis());
            return 1;
        });
        Supplier<Future<Integer>> secondSupplier = () -> secondFuture;
        Try secondResult = Try.of(() -> TimeLimiter.of(longConfig).executeFutureSupplier(secondSupplier));
        then(secondResult.isSuccess()).isTrue();
    }
}

