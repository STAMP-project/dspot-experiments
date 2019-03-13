package com.codahale.metrics;


import org.junit.Test;
import org.mockito.Mockito;


public class MeterTest {
    private final Clock clock = Mockito.mock(Clock.class);

    private final Meter meter = new Meter(clock);

    @Test
    public void startsOutWithNoRatesOrCount() {
        assertThat(meter.getCount()).isZero();
        assertThat(meter.getMeanRate()).isEqualTo(0.0, offset(0.001));
        assertThat(meter.getOneMinuteRate()).isEqualTo(0.0, offset(0.001));
        assertThat(meter.getFiveMinuteRate()).isEqualTo(0.0, offset(0.001));
        assertThat(meter.getFifteenMinuteRate()).isEqualTo(0.0, offset(0.001));
    }

    @Test
    public void marksEventsAndUpdatesRatesAndCount() {
        meter.mark();
        meter.mark(2);
        assertThat(meter.getMeanRate()).isEqualTo(0.3, offset(0.001));
        assertThat(meter.getOneMinuteRate()).isEqualTo(0.184, offset(0.001));
        assertThat(meter.getFiveMinuteRate()).isEqualTo(0.1966, offset(0.001));
        assertThat(meter.getFifteenMinuteRate()).isEqualTo(0.1988, offset(0.001));
    }
}

