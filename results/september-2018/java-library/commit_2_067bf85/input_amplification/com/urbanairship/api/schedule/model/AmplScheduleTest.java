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
    public void testScheduledTimeAndBestTimeBothAbsent_add4170_failAssert10() throws Exception {
        try {
            Schedule.newBuilder();
            Schedule schedule = Schedule.newBuilder().build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothAbsent_add4170 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothAbsent_add4169_failAssert9() throws Exception {
        try {
            Schedule.newBuilder().build();
            Schedule schedule = Schedule.newBuilder().build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothAbsent_add4169 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
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
    public void testScheduledTimeAndBestTimeBothPresent_add3837_failAssert3() throws Exception {
        try {
            Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build());
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent_add3837 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothPresent_add3838_failAssert4() throws Exception {
        try {
            Schedule.newBuilder().setScheduledTimestamp(dateTime);
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent_add3838 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothPresent_add3839_failAssert5() throws Exception {
        try {
            Schedule.newBuilder();
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent_add3839 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothPresent_add3836_failAssert2() throws Exception {
        try {
            Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent_add3836 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothPresent_add3840_failAssert6() throws Exception {
        try {
            BestTime.newBuilder().setSendDate(dateTime).build();
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent_add3840 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothPresent_add3842_failAssert8() throws Exception {
        try {
            BestTime.newBuilder();
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent_add3842 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
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

    @Test(timeout = 10000)
    public void testScheduledTimeAndBestTimeBothPresent_add3841_failAssert7() throws Exception {
        try {
            BestTime.newBuilder().setSendDate(dateTime);
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).setBestTime(BestTime.newBuilder().setSendDate(dateTime).build()).build();
            org.junit.Assert.fail("testScheduledTimeAndBestTimeBothPresent_add3841 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("If bestTime is selected, scheduleTimestamp must be null and vice versa.", expected.getMessage());
        }
    }
}

