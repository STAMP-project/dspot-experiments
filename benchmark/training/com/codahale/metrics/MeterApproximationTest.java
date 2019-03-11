package com.codahale.metrics;


import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MeterApproximationTest {
    private final long ratePerMinute;

    public MeterApproximationTest(long ratePerMinute) {
        this.ratePerMinute = ratePerMinute;
    }

    @Test
    public void controlMeter1MinuteMeanApproximation() {
        final Meter meter = simulateMetronome(62934, TimeUnit.MILLISECONDS, 3, TimeUnit.MINUTES);
        assertThat(((meter.getOneMinuteRate()) * 60.0)).isEqualTo(ratePerMinute, offset((0.1 * (ratePerMinute))));
    }

    @Test
    public void controlMeter5MinuteMeanApproximation() {
        final Meter meter = simulateMetronome(62934, TimeUnit.MILLISECONDS, 13, TimeUnit.MINUTES);
        assertThat(((meter.getFiveMinuteRate()) * 60.0)).isEqualTo(ratePerMinute, offset((0.1 * (ratePerMinute))));
    }

    @Test
    public void controlMeter15MinuteMeanApproximation() {
        final Meter meter = simulateMetronome(62934, TimeUnit.MILLISECONDS, 38, TimeUnit.MINUTES);
        assertThat(((meter.getFifteenMinuteRate()) * 60.0)).isEqualTo(ratePerMinute, offset((0.1 * (ratePerMinute))));
    }
}

