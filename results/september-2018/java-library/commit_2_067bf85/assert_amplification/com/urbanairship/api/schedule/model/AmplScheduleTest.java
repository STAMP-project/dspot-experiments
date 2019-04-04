package com.urbanairship.api.schedule.model;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplScheduleTest {
    private DateTime dateTime;

    @Before
    public void setUp() {
        dateTime = DateTime.now();
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothAbsent_failAssert1() throws Exception {
        try {
            Schedule schedule = Schedule.newBuilder().build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothAbsent should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothPresent_failAssert0() throws Exception {
        try {
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
        }
    }
}

