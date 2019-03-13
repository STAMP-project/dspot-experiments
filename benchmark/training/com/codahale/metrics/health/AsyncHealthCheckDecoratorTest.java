package com.codahale.metrics.health;


import Async.InitialState;
import Async.ScheduleType;
import HealthCheck.Result;
import com.codahale.metrics.health.annotation.Async;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AsyncHealthCheckDecorator}.
 */
public class AsyncHealthCheckDecoratorTest {
    private final HealthCheck mockHealthCheck = Mockito.mock(HealthCheck.class);

    private final ScheduledExecutorService mockExecutorService = Mockito.mock(ScheduledExecutorService.class);

    @SuppressWarnings("rawtypes")
    private final ScheduledFuture mockFuture = Mockito.mock(ScheduledFuture.class);

    @Test(expected = IllegalArgumentException.class)
    public void nullHealthCheckTriggersInstantiationFailure() {
        new AsyncHealthCheckDecorator(null, mockExecutorService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullExecutorServiceTriggersInstantiationFailure() {
        new AsyncHealthCheckDecorator(mockHealthCheck, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonAsyncHealthCheckTriggersInstantiationFailure() {
        new AsyncHealthCheckDecorator(mockHealthCheck, mockExecutorService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativePeriodTriggersInstantiationFailure() {
        new AsyncHealthCheckDecorator(new AsyncHealthCheckDecoratorTest.NegativePeriodAsyncHealthCheck(), mockExecutorService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroPeriodTriggersInstantiationFailure() {
        new AsyncHealthCheckDecorator(new AsyncHealthCheckDecoratorTest.ZeroPeriodAsyncHealthCheck(), mockExecutorService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeInitialValueTriggersInstantiationFailure() {
        new AsyncHealthCheckDecorator(new AsyncHealthCheckDecoratorTest.NegativeInitialDelayAsyncHealthCheck(), mockExecutorService);
    }

    @Test
    public void defaultAsyncHealthCheckTriggersSuccessfulInstantiationWithFixedRateAndHealthyState() throws Exception {
        HealthCheck asyncHealthCheck = new AsyncHealthCheckDecoratorTest.DefaultAsyncHealthCheck();
        AsyncHealthCheckDecorator asyncDecorator = new AsyncHealthCheckDecorator(asyncHealthCheck, mockExecutorService);
        Mockito.verify(mockExecutorService, Mockito.times(1)).scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        assertThat(asyncDecorator.getHealthCheck()).isEqualTo(asyncHealthCheck);
        assertThat(asyncDecorator.check().isHealthy()).isTrue();
    }

    @Test
    public void fixedDelayAsyncHealthCheckTriggersSuccessfulInstantiationWithFixedDelay() throws Exception {
        HealthCheck asyncHealthCheck = new AsyncHealthCheckDecoratorTest.FixedDelayAsyncHealthCheck();
        AsyncHealthCheckDecorator asyncDecorator = new AsyncHealthCheckDecorator(asyncHealthCheck, mockExecutorService);
        Mockito.verify(mockExecutorService, Mockito.times(1)).scheduleWithFixedDelay(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        assertThat(asyncDecorator.getHealthCheck()).isEqualTo(asyncHealthCheck);
    }

    @Test
    public void unhealthyAsyncHealthCheckTriggersSuccessfulInstantiationWithUnhealthyState() throws Exception {
        HealthCheck asyncHealthCheck = new AsyncHealthCheckDecoratorTest.UnhealthyAsyncHealthCheck();
        AsyncHealthCheckDecorator asyncDecorator = new AsyncHealthCheckDecorator(asyncHealthCheck, mockExecutorService);
        assertThat(asyncDecorator.check().isHealthy()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void tearDownTriggersCancellation() throws Exception {
        Mockito.when(mockExecutorService.scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(mockFuture);
        Mockito.when(mockFuture.cancel(true)).thenReturn(true);
        AsyncHealthCheckDecorator asyncDecorator = new AsyncHealthCheckDecorator(new AsyncHealthCheckDecoratorTest.DefaultAsyncHealthCheck(), mockExecutorService);
        asyncDecorator.tearDown();
        Mockito.verify(mockExecutorService, Mockito.times(1)).scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        Mockito.verify(mockFuture, Mockito.times(1)).cancel(ArgumentMatchers.eq(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void afterFirstExecutionDecoratedHealthCheckResultIsProvided() throws Exception {
        HealthCheck.Result expectedResult = Result.healthy("AsyncHealthCheckTest");
        Mockito.when(mockExecutorService.scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(mockFuture);
        AsyncHealthCheckDecorator asyncDecorator = new AsyncHealthCheckDecorator(new AsyncHealthCheckDecoratorTest.ConfigurableAsyncHealthCheck(expectedResult), mockExecutorService);
        HealthCheck.Result initialResult = asyncDecorator.check();
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(mockExecutorService, Mockito.times(1)).scheduleAtFixedRate(runnableCaptor.capture(), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        Runnable capturedRunnable = runnableCaptor.getValue();
        capturedRunnable.run();
        HealthCheck.Result actualResult = asyncDecorator.check();
        assertThat(actualResult).isEqualTo(expectedResult);
        assertThat(actualResult).isNotEqualTo(initialResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void exceptionInDecoratedHealthCheckWontAffectAsyncDecorator() throws Exception {
        Exception exception = new Exception("TestException");
        Mockito.when(mockExecutorService.scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS))).thenReturn(mockFuture);
        AsyncHealthCheckDecorator asyncDecorator = new AsyncHealthCheckDecorator(new AsyncHealthCheckDecoratorTest.ConfigurableAsyncHealthCheck(exception), mockExecutorService);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(mockExecutorService, Mockito.times(1)).scheduleAtFixedRate(runnableCaptor.capture(), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        Runnable capturedRunnable = runnableCaptor.getValue();
        capturedRunnable.run();
        HealthCheck.Result result = asyncDecorator.check();
        assertThat(result.isHealthy()).isFalse();
        assertThat(result.getError()).isEqualTo(exception);
    }

    @Async(period = -1)
    private static class NegativePeriodAsyncHealthCheck extends HealthCheck {
        @Override
        protected Result check() {
            return null;
        }
    }

    @Async(period = 0)
    private static class ZeroPeriodAsyncHealthCheck extends HealthCheck {
        @Override
        protected Result check() {
            return null;
        }
    }

    @Async(period = 1, initialDelay = -1)
    private static class NegativeInitialDelayAsyncHealthCheck extends HealthCheck {
        @Override
        protected Result check() {
            return null;
        }
    }

    @Async(period = 1)
    private static class DefaultAsyncHealthCheck extends HealthCheck {
        @Override
        protected Result check() {
            return null;
        }
    }

    @Async(period = 1, scheduleType = ScheduleType.FIXED_DELAY)
    private static class FixedDelayAsyncHealthCheck extends HealthCheck {
        @Override
        protected Result check() {
            return null;
        }
    }

    @Async(period = 1, initialState = InitialState.UNHEALTHY)
    private static class UnhealthyAsyncHealthCheck extends HealthCheck {
        @Override
        protected Result check() {
            return null;
        }
    }

    @Async(period = 1, initialState = InitialState.UNHEALTHY)
    private static class ConfigurableAsyncHealthCheck extends HealthCheck {
        private final Result result;

        private final Exception exception;

        ConfigurableAsyncHealthCheck(Result result) {
            this(result, null);
        }

        ConfigurableAsyncHealthCheck(Exception exception) {
            this(null, exception);
        }

        private ConfigurableAsyncHealthCheck(Result result, Exception exception) {
            this.result = result;
            this.exception = exception;
        }

        @Override
        protected Result check() throws Exception {
            if ((exception) != null) {
                throw exception;
            }
            return result;
        }
    }
}

