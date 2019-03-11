package com.baeldung.date;


import hirondelle.date4j.DateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.org.joda.time.LocalDate;
import java.time.org.joda.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class DateDiffUnitTest {
    @Test
    public void givenTwoDatesBeforeJava8_whenDifferentiating_thenWeGetSix() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date firstDate = sdf.parse("06/24/2017");
        Date secondDate = sdf.parse("06/30/2017");
        long diffInMillies = Math.abs(((secondDate.getTime()) - (firstDate.getTime())));
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        Assert.assertEquals(diff, 6);
    }

    @Test
    public void givenTwoDatesInJava8_whenDifferentiating_thenWeGetSix() {
        LocalDate now = LocalDate.now();
        LocalDate sixDaysBehind = now.minusDays(6);
        Period period = Period.between(now, sixDaysBehind);
        int diff = period.getDays();
        Assert.assertEquals(diff, 6);
    }

    @Test
    public void givenTwoDateTimesInJava8_whenDifferentiating_thenWeGetSix() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMinutesBehind = now.minusMinutes(6);
        Duration duration = Duration.between(now, sixMinutesBehind);
        long diff = Math.abs(duration.toMinutes());
        Assert.assertEquals(diff, 6);
    }

    @Test
    public void givenTwoDateTimesInJava8_whenDifferentiatingInSeconds_thenWeGetTen() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenSecondsLater = now.plusSeconds(10);
        long diff = ChronoUnit.SECONDS.between(now, tenSecondsLater);
        Assert.assertEquals(diff, 10);
    }

    @Test
    public void givenTwoZonedDateTimesInJava8_whenDifferentiating_thenWeGetSix() {
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime now = ldt.atZone(ZoneId.of("America/Montreal"));
        ZonedDateTime sixDaysBehind = now.withZoneSameInstant(ZoneId.of("Asia/Singapore")).minusDays(6);
        long diff = ChronoUnit.DAYS.between(sixDaysBehind, now);
        Assert.assertEquals(diff, 6);
    }

    @Test
    public void givenTwoDateTimesInJava8_whenDifferentiatingInSecondsUsingUntil_thenWeGetTen() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenSecondsLater = now.plusSeconds(10);
        long diff = now.until(tenSecondsLater, ChronoUnit.SECONDS);
        Assert.assertEquals(diff, 10);
    }

    @Test
    public void givenTwoDatesInJodaTime_whenDifferentiating_thenWeGetSix() {
        org.joda.time.LocalDate now = org.joda.time.LocalDate.now();
        org.joda.time.LocalDate sixDaysBehind = now.minusDays(6);
        org.joda.time.Period period = new org.joda.time.Period(now, sixDaysBehind);
        long diff = Math.abs(period.getDays());
        Assert.assertEquals(diff, 6);
    }

    @Test
    public void givenTwoDateTimesInJodaTime_whenDifferentiating_thenWeGetSix() {
        org.joda.time.LocalDateTime now = org.joda.time.LocalDateTime.now();
        org.joda.time.LocalDateTime sixMinutesBehind = now.minusMinutes(6);
        org.joda.time.Period period = new org.joda.time.Period(now, sixMinutesBehind);
        long diff = Math.abs(period.getDays());
    }

    @Test
    public void givenTwoDatesInDate4j_whenDifferentiating_thenWeGetSix() {
        DateTime now = DateTime.now(TimeZone.getDefault());
        DateTime sixDaysBehind = now.minusDays(6);
        long diff = Math.abs(now.numDaysFrom(sixDaysBehind));
        Assert.assertEquals(diff, 6);
    }
}

