package com.codahale.metrics;


import Timer.Context;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.mockito.Mockito;


public class TimerTest {
    private final Reservoir reservoir = Mockito.mock(Reservoir.class);

    private final Clock clock = new Clock() {
        // a mock clock that increments its ticker by 50msec per call
        private long val = 0;

        @Override
        public long getTick() {
            return val += 50000000;
        }
    };

    private final Timer timer = new Timer(reservoir, clock);

    @Test
    public void hasRates() {
        assertThat(timer.getCount()).isZero();
        assertThat(timer.getMeanRate()).isEqualTo(0.0, offset(0.001));
        assertThat(timer.getOneMinuteRate()).isEqualTo(0.0, offset(0.001));
        assertThat(timer.getFiveMinuteRate()).isEqualTo(0.0, offset(0.001));
        assertThat(timer.getFifteenMinuteRate()).isEqualTo(0.0, offset(0.001));
    }

    @Test
    public void updatesTheCountOnUpdates() {
        assertThat(timer.getCount()).isZero();
        timer.update(1, TimeUnit.SECONDS);
        assertThat(timer.getCount()).isEqualTo(1);
    }

    @Test
    public void timesCallableInstances() throws Exception {
        final String value = timer.time(() -> "one");
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(value).isEqualTo("one");
        Mockito.verify(reservoir).update(50000000);
    }

    @Test
    public void timesSuppliedInstances() {
        final String value = timer.timeSupplier(() -> "one");
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(value).isEqualTo("one");
        Mockito.verify(reservoir).update(50000000);
    }

    @Test
    public void timesRunnableInstances() {
        final AtomicBoolean called = new AtomicBoolean();
        timer.time(() -> called.set(true));
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(called.get()).isTrue();
        Mockito.verify(reservoir).update(50000000);
    }

    @Test
    public void timesContexts() {
        timer.time().stop();
        assertThat(timer.getCount()).isEqualTo(1);
        Mockito.verify(reservoir).update(50000000);
    }

    @Test
    public void returnsTheSnapshotFromTheReservoir() {
        final Snapshot snapshot = Mockito.mock(Snapshot.class);
        Mockito.when(reservoir.getSnapshot()).thenReturn(snapshot);
        assertThat(timer.getSnapshot()).isEqualTo(snapshot);
    }

    @Test
    public void ignoresNegativeValues() {
        timer.update((-1), TimeUnit.SECONDS);
        assertThat(timer.getCount()).isZero();
        Mockito.verifyZeroInteractions(reservoir);
    }

    @Test
    public void java8Duration() {
        timer.update(Duration.ofSeconds(1234));
        assertThat(timer.getCount()).isEqualTo(1);
        Mockito.verify(reservoir).update(((long) (1.234E12)));
    }

    @Test
    public void java8NegativeDuration() {
        timer.update(Duration.ofMillis((-5678)));
        assertThat(timer.getCount()).isZero();
        Mockito.verifyZeroInteractions(reservoir);
    }

    @Test
    public void tryWithResourcesWork() {
        assertThat(timer.getCount()).isZero();
        int dummy = 0;
        try (Timer.Context context = timer.time()) {
            assertThat(context).isNotNull();
            dummy += 1;
        }
        assertThat(dummy).isEqualTo(1);
        assertThat(timer.getCount()).isEqualTo(1);
        Mockito.verify(reservoir).update(50000000);
    }
}

