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


public class AmplSchedulePayloadSerializerTest {
    private static final ObjectMapper MAPPER = ScheduleObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testBestTimeSerialization_add27563null30883_failAssert39() throws Exception {
        try {
            BestTime o_testBestTimeSerialization_add27563__1 = BestTime.newBuilder().setSendDate(new DateTime("2013-05-05T00:00:01", DateTimeZone.UTC)).build();
            BestTime bestTime = BestTime.newBuilder().setSendDate(new DateTime("2013-05-05T00:00:01", DateTimeZone.UTC)).build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().setBestTime(null).build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{\"best_time\":{\"send_date\":\"2013-05-05\"}},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{}}}";
            org.junit.Assert.fail("testBestTimeSerialization_add27563null30883 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBestTimeSerializationnull27591_failAssert26() throws Exception {
        try {
            BestTime bestTime = BestTime.newBuilder().setSendDate(new DateTime("2013-05-05T00:00:01", DateTimeZone.UTC)).build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().setBestTime(null).build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{\"best_time\":{\"send_date\":\"2013-05-05\"}},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{}}}";
            org.junit.Assert.fail("testBestTimeSerializationnull27591 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37777_failAssert79() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            Schedule.newBuilder();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37777 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37740_failAssert42() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("\"scheduled_tim\": \"2013-05-05 00:00:01\"")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37740 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37741_failAssert43() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tg")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37741 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37742_failAssert44() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("t ag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37742 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37750_failAssert52() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("4HrlU").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37750 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedulenull37783_failAssert85() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = null;
            org.junit.Assert.fail("testNoSchedulenull37783 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedulenull37782_failAssert84() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(null);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedulenull37782 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

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

    @Test(timeout = 10000)
    public void testNoSchedule_add37758_failAssert60() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build());
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37758 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37768_failAssert70() throws Exception {
        try {
            Notification.newBuilder().setAlert("alert");
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37768 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedulenull37781_failAssert83() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(null).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedulenull37781 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37759_failAssert61() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build());
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37759 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedulenull37779_failAssert81() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag(null)).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedulenull37779 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37739_failAssert41() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37739 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37769_failAssert71() throws Exception {
        try {
            Notification.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37769 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37749_failAssert51() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alet").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37749 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37760_failAssert62() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build());
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37760 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37748_failAssert50() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("aulert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37748 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37778_failAssert80() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37778 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37746_failAssert48() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("\"scheduled_tim\": \"2013-05-05 00:00:01\"").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37746 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37754_failAssert56() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\"z{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37754 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37761_failAssert63() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag"));
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37761 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37747_failAssert49() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alxrt").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37747 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37755_failAssert57() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\"{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37755 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37762_failAssert64() throws Exception {
        try {
            PushPayload.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37762 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37770_failAssert72() throws Exception {
        try {
            PushOptions.newBuilder().build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37770 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37756_failAssert58() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"dev_ice_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37756 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37763_failAssert65() throws Exception {
        try {
            Selectors.tag("tag");
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37763 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37771_failAssert73() throws Exception {
        try {
            PushOptions.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37771 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37764_failAssert66() throws Exception {
        try {
            DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37764 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37772_failAssert74() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37772 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37757_failAssert59() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37757 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37743_failAssert45() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("kce")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37743 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37751_failAssert53() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37751 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37765_failAssert67() throws Exception {
        try {
            DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS);
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37765 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37773_failAssert75() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload);
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37773 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37744_failAssert46() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("Yag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37744 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37766_failAssert68() throws Exception {
        try {
            DeviceTypeData.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37766 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37774_failAssert76() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build());
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37774 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37752_failAssert54() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "\"scheduled_tim\": \"2013-05-05 00:00:01\"";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37752 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37767_failAssert69() throws Exception {
        try {
            Notification.newBuilder().setAlert("alert").build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37767 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37775_failAssert77() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37775 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37745_failAssert47() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37745 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString37753_failAssert55() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = ">+t{l=jk!|:0do:]Z#kkI]jDcz*LNFRc{5w6p=>{4L)C RCv1QmWW6{ bhO=o0FCL,Ov/pbpf{5,h8PeB@NG<rSr.9;p&QL:J|w1K!P^ @L7UwaWuB}x!r;dRg/?[d+[xiE ";
            org.junit.Assert.fail("testNoSchedule_literalMutationString37753 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add37776_failAssert78() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            Schedule.newBuilder().build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add37776 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }
}

