package com.baeldung.datetime;


import java.time.OffsetDateTime;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OffsetDateTimeExtractYearMonthDayIntegerValuesUnitTest {
    OffsetDateTimeExtractYearMonthDayIntegerValues offsetDateTimeExtractYearMonthDayIntegerValues = new OffsetDateTimeExtractYearMonthDayIntegerValues();

    OffsetDateTime offsetDateTime = OffsetDateTime.parse("2007-12-03T10:15:30+01:00");

    @Test
    public void whenGetYear_thenCorrectYear() {
        int actualYear = offsetDateTimeExtractYearMonthDayIntegerValues.getYear(offsetDateTime);
        Assert.assertThat(actualYear, CoreMatchers.is(2007));
    }

    @Test
    public void whenGetMonth_thenCorrectMonth() {
        int actualMonth = offsetDateTimeExtractYearMonthDayIntegerValues.getMonth(offsetDateTime);
        Assert.assertThat(actualMonth, CoreMatchers.is(12));
    }

    @Test
    public void whenGetDay_thenCorrectDay() {
        int actualDayOfMonth = offsetDateTimeExtractYearMonthDayIntegerValues.getDay(offsetDateTime);
        Assert.assertThat(actualDayOfMonth, CoreMatchers.is(3));
    }
}

