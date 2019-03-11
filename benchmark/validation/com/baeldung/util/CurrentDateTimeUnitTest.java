package com.baeldung.util;


import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import org.junit.Assert;
import org.junit.Test;


public class CurrentDateTimeUnitTest {
    private static final Clock clock = Clock.fixed(Instant.parse("2016-10-09T15:10:30.00Z"), ZoneId.of("UTC"));

    @Test
    public void shouldReturnCurrentDate() {
        final LocalDate now = LocalDate.now(CurrentDateTimeUnitTest.clock);
        Assert.assertEquals(9, now.get(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(10, now.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(2016, now.get(ChronoField.YEAR));
    }

    @Test
    public void shouldReturnCurrentTime() {
        final LocalTime now = LocalTime.now(CurrentDateTimeUnitTest.clock);
        Assert.assertEquals(15, now.get(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(10, now.get(ChronoField.MINUTE_OF_HOUR));
        Assert.assertEquals(30, now.get(ChronoField.SECOND_OF_MINUTE));
    }

    @Test
    public void shouldReturnCurrentTimestamp() {
        final Instant now = Instant.now(CurrentDateTimeUnitTest.clock);
        Assert.assertEquals(CurrentDateTimeUnitTest.clock.instant().getEpochSecond(), now.getEpochSecond());
    }
}

