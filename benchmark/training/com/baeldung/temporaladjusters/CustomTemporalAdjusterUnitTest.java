package com.baeldung.temporaladjusters;


import com.baeldung.temporaladjuster.CustomTemporalAdjuster;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import org.junit.Assert;
import org.junit.Test;


public class CustomTemporalAdjusterUnitTest {
    private static final TemporalAdjuster NEXT_WORKING_DAY = new CustomTemporalAdjuster();

    @Test
    public void whenAdjustAndImplementInterface_thenNextWorkingDay() {
        LocalDate localDate = LocalDate.of(2017, 7, 8);
        CustomTemporalAdjuster temporalAdjuster = new CustomTemporalAdjuster();
        LocalDate nextWorkingDay = localDate.with(temporalAdjuster);
        Assert.assertEquals("2017-07-10", nextWorkingDay.toString());
    }

    @Test
    public void whenAdjust_thenNextWorkingDay() {
        LocalDate localDate = LocalDate.of(2017, 7, 8);
        LocalDate date = localDate.with(CustomTemporalAdjusterUnitTest.NEXT_WORKING_DAY);
        Assert.assertEquals("2017-07-10", date.toString());
    }

    @Test
    public void whenAdjust_thenFourteenDaysAfterDate() {
        LocalDate localDate = LocalDate.of(2017, 7, 8);
        TemporalAdjuster temporalAdjuster = ( t) -> t.plus(Period.ofDays(14));
        LocalDate result = localDate.with(temporalAdjuster);
        String fourteenDaysAfterDate = "2017-07-22";
        Assert.assertEquals(fourteenDaysAfterDate, result.toString());
    }
}

