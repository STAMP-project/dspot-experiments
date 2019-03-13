package com.codahale.metrics;


import Clock.UserTimeClock;
import org.junit.Test;


public class ClockTest {
    @Test
    public void userTimeClock() {
        final Clock.UserTimeClock clock = new Clock.UserTimeClock();
        assertThat(((double) (clock.getTime()))).isEqualTo(System.currentTimeMillis(), offset(100.0));
        assertThat(((double) (clock.getTick()))).isEqualTo(System.nanoTime(), offset(1000000.0));
    }

    @Test
    public void defaultsToUserTime() {
        assertThat(Clock.defaultClock()).isInstanceOf(UserTimeClock.class);
    }
}

