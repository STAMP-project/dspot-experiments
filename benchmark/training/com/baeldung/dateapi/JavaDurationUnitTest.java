package com.baeldung.dateapi;


import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.Assert;
import org.junit.Test;


public class JavaDurationUnitTest {
    @Test
    public void test2() {
        Instant start = Instant.parse("2017-10-03T10:15:30.00Z");
        Instant end = Instant.parse("2017-10-03T10:16:30.00Z");
        Duration duration = Duration.between(start, end);
        Assert.assertFalse(duration.isNegative());
        Assert.assertEquals(60, duration.getSeconds());
        Assert.assertEquals(1, duration.toMinutes());
        Duration fromDays = Duration.ofDays(1);
        Assert.assertEquals(86400, fromDays.getSeconds());
        Duration fromMinutes = Duration.ofMinutes(60);
        Assert.assertEquals(1, fromMinutes.toHours());
        Assert.assertEquals(120, duration.plusSeconds(60).getSeconds());
        Assert.assertEquals(30, duration.minusSeconds(30).getSeconds());
        Assert.assertEquals(120, duration.plus(60, ChronoUnit.SECONDS).getSeconds());
        Assert.assertEquals(30, duration.minus(30, ChronoUnit.SECONDS).getSeconds());
        Duration fromChar1 = Duration.parse("P1DT1H10M10.5S");
        Duration fromChar2 = Duration.parse("PT10M");
    }
}

