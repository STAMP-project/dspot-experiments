package com.urbanairship.api.schedule.parse;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.push.model.DeviceType;
import com.urbanairship.api.push.model.DeviceTypeData;
import com.urbanairship.api.push.model.PushOptions;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.push.model.audience.Selectors;
import com.urbanairship.api.push.model.notification.Notification;
import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.SchedulePayload;
import org.junit.Assert;
import org.junit.Test;


public class AmplSchedulePayloadSerializerTest {
    private static final ObjectMapper MAPPER = ScheduleObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testNoSchedule_failAssert0() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }
}

