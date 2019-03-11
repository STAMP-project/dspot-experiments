package io.dropwizard.db;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.util.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class TimeBoundHealthCheckTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testCheck() throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutorService executorService = Mockito.mock(ExecutorService.class);
        final Duration duration = Mockito.mock(Duration.class);
        Mockito.when(duration.getQuantity()).thenReturn(5L);
        Mockito.when(duration.getUnit()).thenReturn(TimeUnit.SECONDS);
        final Callable<HealthCheck.Result> callable = Mockito.mock(Callable.class);
        final Future<HealthCheck.Result> future = Mockito.mock(Future.class);
        Mockito.when(executorService.submit(callable)).thenReturn(future);
        new TimeBoundHealthCheck(executorService, duration).check(callable);
        Mockito.verify(executorService, Mockito.times(1)).submit(callable);
        Mockito.verify(future, Mockito.times(1)).get(duration.getQuantity(), duration.getUnit());
    }
}

