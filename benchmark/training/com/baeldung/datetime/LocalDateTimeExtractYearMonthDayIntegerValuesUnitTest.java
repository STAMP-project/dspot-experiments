package com.baeldung.datetime;


import java.time.LocalDateTime;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LocalDateTimeExtractYearMonthDayIntegerValuesUnitTest {
    LocalDateTimeExtractYearMonthDayIntegerValues localDateTimeExtractYearMonthDayIntegerValues = new LocalDateTimeExtractYearMonthDayIntegerValues();

    LocalDateTime localDateTime = LocalDateTime.parse("2007-12-03T10:15:30");

    @Test
    public void whenGetYear_thenCorrectYear() {
        int actualYear = localDateTimeExtractYearMonthDayIntegerValues.getYear(localDateTime);
        Assert.assertThat(actualYear, CoreMatchers.is(2007));
    }

    @Test
    public void whenGetMonth_thenCorrectMonth() {
        int actualMonth = localDateTimeExtractYearMonthDayIntegerValues.getMonth(localDateTime);
        Assert.assertThat(actualMonth, CoreMatchers.is(12));
    }

    @Test
    public void whenGetDay_thenCorrectDay() {
        int actualDayOfMonth = localDateTimeExtractYearMonthDayIntegerValues.getDay(localDateTime);
        Assert.assertThat(actualDayOfMonth, CoreMatchers.is(3));
    }
}

