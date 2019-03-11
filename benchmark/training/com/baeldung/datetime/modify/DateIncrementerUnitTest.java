package com.baeldung.datetime.modify;


import org.junit.Assert;
import org.junit.Test;


public class DateIncrementerUnitTest {
    private static final String DATE_TO_INCREMENT = "2018-07-03";

    private static final String EXPECTED_DATE = "2018-07-04";

    @Test
    public void givenDate_whenUsingJava8_thenAddOneDay() throws Exception {
        String incrementedDate = DateIncrementer.addOneDay(DateIncrementerUnitTest.DATE_TO_INCREMENT);
        Assert.assertEquals(DateIncrementerUnitTest.EXPECTED_DATE, incrementedDate);
    }

    @Test
    public void givenDate_whenUsingJodaTime_thenAddOneDay() throws Exception {
        String incrementedDate = DateIncrementer.addOneDayJodaTime(DateIncrementerUnitTest.DATE_TO_INCREMENT);
        Assert.assertEquals(DateIncrementerUnitTest.EXPECTED_DATE, incrementedDate);
    }

    @Test
    public void givenDate_whenUsingCalendar_thenAddOneDay() throws Exception {
        String incrementedDate = DateIncrementer.addOneDayCalendar(DateIncrementerUnitTest.DATE_TO_INCREMENT);
        Assert.assertEquals(DateIncrementerUnitTest.EXPECTED_DATE, incrementedDate);
    }

    @Test
    public void givenDate_whenUsingApacheCommons_thenAddOneDay() throws Exception {
        String incrementedDate = DateIncrementer.addOneDayApacheCommons(DateIncrementerUnitTest.DATE_TO_INCREMENT);
        Assert.assertEquals(DateIncrementerUnitTest.EXPECTED_DATE, incrementedDate);
    }
}

