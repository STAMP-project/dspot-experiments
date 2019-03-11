package com.codahale.metrics.health;


import com.codahale.metrics.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HealthCheckTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private static class ExampleHealthCheck extends HealthCheck {
        private final HealthCheck underlying;

        private ExampleHealthCheck(HealthCheck underlying) {
            this.underlying = underlying;
        }

        @Override
        protected Result check() {
            return underlying.execute();
        }
    }

    private final HealthCheck underlying = Mockito.mock(HealthCheck.class);

    private final HealthCheck healthCheck = new HealthCheckTest.ExampleHealthCheck(underlying);

    @Test
    public void canHaveHealthyResults() {
        final HealthCheck.Result result = HealthCheck.Result.healthy();
        assertThat(result.isHealthy()).isTrue();
        assertThat(result.getMessage()).isNull();
        assertThat(result.getError()).isNull();
    }

    @Test
    public void canHaveHealthyResultsWithMessages() {
        final HealthCheck.Result result = HealthCheck.Result.healthy("woo");
        assertThat(result.isHealthy()).isTrue();
        assertThat(result.getMessage()).isEqualTo("woo");
        assertThat(result.getError()).isNull();
    }

    @Test
    public void canHaveHealthyResultsWithFormattedMessages() {
        final HealthCheck.Result result = HealthCheck.Result.healthy("foo %s", "bar");
        assertThat(result.isHealthy()).isTrue();
        assertThat(result.getMessage()).isEqualTo("foo bar");
        assertThat(result.getError()).isNull();
    }

    @Test
    public void canHaveUnhealthyResults() {
        final HealthCheck.Result result = HealthCheck.Result.unhealthy("bad");
        assertThat(result.isHealthy()).isFalse();
        assertThat(result.getMessage()).isEqualTo("bad");
        assertThat(result.getError()).isNull();
    }

    @Test
    public void canHaveUnhealthyResultsWithFormattedMessages() {
        final HealthCheck.Result result = HealthCheck.Result.unhealthy("foo %s %d", "bar", 123);
        assertThat(result.isHealthy()).isFalse();
        assertThat(result.getMessage()).isEqualTo("foo bar 123");
        assertThat(result.getError()).isNull();
    }

    @Test
    public void canHaveUnhealthyResultsWithExceptions() {
        final RuntimeException e = Mockito.mock(RuntimeException.class);
        Mockito.when(e.getMessage()).thenReturn("oh noes");
        final HealthCheck.Result result = HealthCheck.Result.unhealthy(e);
        assertThat(result.isHealthy()).isFalse();
        assertThat(result.getMessage()).isEqualTo("oh noes");
        assertThat(result.getError()).isEqualTo(e);
    }

    @Test
    public void canHaveHealthyBuilderWithFormattedMessage() {
        final HealthCheck.Result result = HealthCheck.Result.builder().healthy().withMessage("There are %d %s in the %s", 42, "foos", "bar").build();
        assertThat(result.isHealthy()).isTrue();
        assertThat(result.getMessage()).isEqualTo("There are 42 foos in the bar");
    }

    @Test
    public void canHaveHealthyBuilderWithDetail() {
        final HealthCheck.Result result = HealthCheck.Result.builder().healthy().withDetail("detail", "value").build();
        assertThat(result.isHealthy()).isTrue();
        assertThat(result.getMessage()).isNull();
        assertThat(result.getError()).isNull();
        assertThat(result.getDetails()).containsEntry("detail", "value");
    }

    @Test
    public void canHaveUnHealthyBuilderWithDetail() {
        final HealthCheck.Result result = HealthCheck.Result.builder().unhealthy().withDetail("detail", "value").build();
        assertThat(result.isHealthy()).isFalse();
        assertThat(result.getMessage()).isNull();
        assertThat(result.getError()).isNull();
        assertThat(result.getDetails()).containsEntry("detail", "value");
    }

    @Test
    public void canHaveUnHealthyBuilderWithDetailAndError() {
        final RuntimeException e = Mockito.mock(RuntimeException.class);
        Mockito.when(e.getMessage()).thenReturn("oh noes");
        final HealthCheck.Result result = HealthCheck.Result.builder().unhealthy(e).withDetail("detail", "value").build();
        assertThat(result.isHealthy()).isFalse();
        assertThat(result.getMessage()).isEqualTo("oh noes");
        assertThat(result.getError()).isEqualTo(e);
        assertThat(result.getDetails()).containsEntry("detail", "value");
    }

    @Test
    public void returnsResultsWhenExecuted() {
        final HealthCheck.Result result = Mockito.mock(HealthCheck.Result.class);
        Mockito.when(underlying.execute()).thenReturn(result);
        assertThat(healthCheck.execute()).isEqualTo(result);
        Mockito.verify(result).setDuration(ArgumentMatchers.anyLong());
    }

    @Test
    public void wrapsExceptionsWhenExecuted() {
        final RuntimeException e = Mockito.mock(RuntimeException.class);
        Mockito.when(e.getMessage()).thenReturn("oh noes");
        Mockito.when(underlying.execute()).thenThrow(e);
        HealthCheck.Result actual = healthCheck.execute();
        assertThat(actual.isHealthy()).isFalse();
        assertThat(actual.getMessage()).isEqualTo("oh noes");
        assertThat(actual.getError()).isEqualTo(e);
        assertThat(actual.getDetails()).isNull();
        assertThat(actual.getDuration()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void canHaveUserSuppliedClockForTimestamp() {
        ZonedDateTime dateTime = ZonedDateTime.now().minusMinutes(10);
        Clock clock = HealthCheckTest.clockWithFixedTime(dateTime);
        HealthCheck.Result result = HealthCheck.Result.builder().healthy().usingClock(clock).build();
        assertThat(result.isHealthy()).isTrue();
        assertThat(result.getTimestamp()).isEqualTo(HealthCheckTest.DATE_TIME_FORMATTER.format(dateTime));
    }

    @Test
    public void toStringWorksEvenForNullAttributes() {
        ZonedDateTime dateTime = ZonedDateTime.now().minusMinutes(25);
        Clock clock = HealthCheckTest.clockWithFixedTime(dateTime);
        final HealthCheck.Result resultWithNullDetailValue = HealthCheck.Result.builder().unhealthy().withDetail("aNullDetail", null).usingClock(clock).build();
        assertThat(resultWithNullDetailValue.toString()).contains(("Result{isHealthy=false, duration=0, timestamp=" + (HealthCheckTest.DATE_TIME_FORMATTER.format(dateTime))), ", aNullDetail=null}");
    }
}

