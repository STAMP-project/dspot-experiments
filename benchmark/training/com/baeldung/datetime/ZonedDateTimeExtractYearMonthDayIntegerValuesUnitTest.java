package com.baeldung.datetime;


import java.time.ZonedDateTime;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ZonedDateTimeExtractYearMonthDayIntegerValuesUnitTest {
    ZonedDateTimeExtractYearMonthDayIntegerValues zonedDateTimeExtractYearMonthDayIntegerValues = new ZonedDateTimeExtractYearMonthDayIntegerValues();

    ZonedDateTime zonedDateTime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00");

    @Test
    public void whenGetYear_thenCorrectYear() {
        int actualYear = zonedDateTimeExtractYearMonthDayIntegerValues.getYear(zonedDateTime);
        Assert.assertThat(actualYear, CoreMatchers.is(2007));
    }

    @Test
    public void whenGetMonth_thenCorrectMonth() {
        int actualMonth = zonedDateTimeExtractYearMonthDayIntegerValues.getMonth(zonedDateTime);
        Assert.assertThat(actualMonth, CoreMatchers.is(12));
    }

    @Test
    public void whenGetDay_thenCorrectDay() {
        int actualDayOfMonth = zonedDateTimeExtractYearMonthDayIntegerValues.getDay(zonedDateTime);
        Assert.assertThat(actualDayOfMonth, CoreMatchers.is(3));
    }
}

