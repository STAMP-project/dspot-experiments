package com.urbanairship.api.schedule.parse;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.push.model.DeviceType;
import com.urbanairship.api.push.model.DeviceTypeData;
import com.urbanairship.api.push.model.PushOptions;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.push.model.audience.Selectors;
import com.urbanairship.api.push.model.notification.Notification;
import com.urbanairship.api.schedule.model.BestTime;
import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.SchedulePayload;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;


public class AmplScheduleSerializerTest {
    private static final ObjectMapper MAPPER = ScheduleObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testSerializationnull11247_failAssert24() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().setScheduledTimestamp(new DateTime("2013-05-05T00:00:01", DateTimeZone.UTC)).build()).setPushPayload(pushPayload).build();
            String json = AmplScheduleSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{\"scheduled_time\":\"2013-05-05T00:00:01\"},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{}}}";
            BestTime bestTime = BestTime.newBuilder().setSendDate(DateTime.now()).build();
            Schedule schedule = Schedule.newBuilder().setBestTime(null).build();
            String scheduledBestTimeJson = AmplScheduleSerializerTest.MAPPER.writeValueAsString(schedule);
            System.out.println(scheduledBestTimeJson);
            org.junit.Assert.fail("testSerializationnull11247 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSerializationnull63_failAssert7() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().setScheduledTimestamp(new DateTime("2013-05-05T00:00:01", DateTimeZone.UTC)).build()).setPushPayload(pushPayload).build();
            String json = AmplScheduleSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{\"scheduled_time\":\"2013-05-05T00:00:01\"},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{}}}";
            BestTime bestTime = BestTime.newBuilder().setSendDate(DateTime.now()).build();
            Schedule schedule = Schedule.newBuilder().setBestTime(null).build();
            String scheduledBestTimeJson = AmplScheduleSerializerTest.MAPPER.writeValueAsString(schedule);
            System.out.println(scheduledBestTimeJson);
            org.junit.Assert.fail("testSerializationnull63 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }
}

