package com.baeldung.datetime;


import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DateExtractYearMonthDayIntegerValuesUnitTest {
    DateExtractYearMonthDayIntegerValues extractYearMonthDateIntegerValues = new DateExtractYearMonthDayIntegerValues();

    Date date;

    @Test
    public void whenGetYear_thenCorrectYear() {
        int actualYear = extractYearMonthDateIntegerValues.getYear(date);
        Assert.assertThat(actualYear, CoreMatchers.is(2018));
    }

    @Test
    public void whenGetMonth_thenCorrectMonth() {
        int actualMonth = extractYearMonthDateIntegerValues.getMonth(date);
        Assert.assertThat(actualMonth, CoreMatchers.is(2));
    }

    @Test
    public void whenGetDay_thenCorrectDay() {
        int actualDayOfMonth = extractYearMonthDateIntegerValues.getDay(date);
        Assert.assertThat(actualDayOfMonth, CoreMatchers.is(1));
    }
}

