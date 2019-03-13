package com.baeldung.datetime;


import java.time.LocalDate;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LocalDateExtractYearMonthDayIntegerValuesUnitTest {
    LocalDateExtractYearMonthDayIntegerValues localDateExtractYearMonthDayIntegerValues = new LocalDateExtractYearMonthDayIntegerValues();

    LocalDate localDate = LocalDate.parse("2007-12-03");

    @Test
    public void whenGetYear_thenCorrectYear() {
        int actualYear = localDateExtractYearMonthDayIntegerValues.getYear(localDate);
        Assert.assertThat(actualYear, CoreMatchers.is(2007));
    }

    @Test
    public void whenGetMonth_thenCorrectMonth() {
        int actualMonth = localDateExtractYearMonthDayIntegerValues.getMonth(localDate);
        Assert.assertThat(actualMonth, CoreMatchers.is(12));
    }

    @Test
    public void whenGetDay_thenCorrectDay() {
        int actualDayOfMonth = localDateExtractYearMonthDayIntegerValues.getDay(localDate);
        Assert.assertThat(actualDayOfMonth, CoreMatchers.is(3));
    }
}

