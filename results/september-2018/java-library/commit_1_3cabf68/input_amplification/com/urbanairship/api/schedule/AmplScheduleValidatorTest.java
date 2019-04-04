package com.urbanairship.api.schedule;


import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.ScheduleValidator;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;


public class AmplScheduleValidatorTest {
    ScheduleValidator validator = new ScheduleValidator();

    @Test(timeout = 10000)
    public void testFutureDateTime_add1093_literalMutationNumber1137() throws Exception {
        DateTime o_testFutureDateTime_add1093_literalMutationNumber1137__1 = DateTime.now().plusDays(0);
        Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).toString());
        Assert.assertEquals(41, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getWeekOfWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getYear())));
        Assert.assertEquals(10, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getMonthOfYear())));
        Assert.assertEquals(9, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getDayOfMonth())));
        Assert.assertEquals(11, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getHourOfDay())));
        Assert.assertEquals(2, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getDayOfWeek())));
        Assert.assertEquals(282, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getDayOfYear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getCenturyOfEra())));
        Assert.assertEquals(1, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getEra())));
        Assert.assertTrue(((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).isBeforeNow());
        Assert.assertFalse(((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).isAfterNow());
        Assert.assertFalse(((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).isEqualNow());
        Assert.assertFalse(((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).getID());
        this.validator.validate(Schedule.newBuilder().setScheduledTimestamp(DateTime.now().plusDays(1)).build());
        Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getChronology())).toString());
        Assert.assertEquals(41, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getWeekOfWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getYear())));
        Assert.assertEquals(10, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getMonthOfYear())));
        Assert.assertEquals(9, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getDayOfMonth())));
        Assert.assertEquals(11, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getHourOfDay())));
        Assert.assertEquals(2, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getDayOfWeek())));
        Assert.assertEquals(282, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getDayOfYear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getCenturyOfEra())));
        Assert.assertEquals(1, ((int) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getEra())));
        Assert.assertTrue(((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).isBeforeNow());
        Assert.assertFalse(((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).isAfterNow());
        Assert.assertFalse(((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).isEqualNow());
        Assert.assertFalse(((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testFutureDateTime_add1093_literalMutationNumber1137__1)).getZone())).getID());
    }
}

