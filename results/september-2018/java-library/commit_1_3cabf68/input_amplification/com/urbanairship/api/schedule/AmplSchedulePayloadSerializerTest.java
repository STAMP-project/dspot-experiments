package com.urbanairship.api.schedule;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.push.model.DeviceType;
import com.urbanairship.api.push.model.DeviceTypeData;
import com.urbanairship.api.push.model.PushOptions;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.push.model.audience.Selectors;
import com.urbanairship.api.push.model.notification.Notification;
import com.urbanairship.api.push.parse.PushObjectMapper;
import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.SchedulePayload;
import org.junit.Assert;
import org.junit.Test;


public class AmplSchedulePayloadSerializerTest {
    private static final ObjectMapper MAPPER = PushObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testNoSchedulenull4035_failAssert68() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(null);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedulenull4035 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3999_failAssert32() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3999 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedulenull4036_failAssert69() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = null;
            org.junit.Assert.fail("testNoSchedulenull4036 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3998_failAssert31() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alSert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3998 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3997_failAssert30() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tjag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3997 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
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
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4001_failAssert34() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("aaert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4001 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4028_failAssert61() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4028 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4000_failAssert33() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4000 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4019_failAssert52() throws Exception {
        try {
            DeviceTypeData.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4019 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4027_failAssert60() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build());
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4027 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4018_failAssert51() throws Exception {
        try {
            DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS);
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4018 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4002_failAssert35() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alet").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4002 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4017_failAssert50() throws Exception {
        try {
            DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4017 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4009_failAssert42() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"sch6dule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4009 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4008_failAssert41() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\"5:\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4008 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4007_failAssert40() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "wl)wp^jIz>^IKBUt/V#sArs-sR!h_ApK!aYn  :f/QxGaC#*E T1GA;4lB1r%km|SwrELo-G)kkaM|Sq5](b!%/!r.gzSgRlSoIuan&SYj([*X[i,N>(ZRCA|Pdj6#+YU9!7";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4007 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4011_failAssert44() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build());
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4011 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4004_failAssert37() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"shedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4004 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4010_failAssert43() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4010 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4003_failAssert36() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("v|cOh").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4003 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4029_failAssert62() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            Schedule.newBuilder().build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4029 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4006_failAssert39() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4006 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4012_failAssert45() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build());
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4012 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4020_failAssert53() throws Exception {
        try {
            Notification.newBuilder().setAlert("alert").build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4020 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString4005_failAssert38() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "";
            org.junit.Assert.fail("testNoSchedule_literalMutationString4005 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4014_failAssert47() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag"));
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4014 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4022_failAssert55() throws Exception {
        try {
            Notification.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4022 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4030_failAssert63() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            Schedule.newBuilder();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4030 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedulenull4032_failAssert65() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag(null)).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedulenull4032 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3996_failAssert29() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("?rg")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3996 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4013_failAssert46() throws Exception {
        try {
            PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build());
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4013 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4021_failAssert54() throws Exception {
        try {
            Notification.newBuilder().setAlert("alert");
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4021 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4024_failAssert57() throws Exception {
        try {
            PushOptions.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4024 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4016_failAssert49() throws Exception {
        try {
            Selectors.tag("tag");
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4016 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4031_failAssert64() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4031 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedulenull4034_failAssert67() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(null).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedulenull4034 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4015_failAssert48() throws Exception {
        try {
            PushPayload.newBuilder();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4015 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4023_failAssert56() throws Exception {
        try {
            PushOptions.newBuilder().build();
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4023 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4026_failAssert59() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload);
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4026 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3993_failAssert26() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("}ag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3993 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_add4025_failAssert58() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tag")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_add4025 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3992_failAssert25() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3992 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3995_failAssert28() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("tg")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3995 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNoSchedule_literalMutationString3994_failAssert27() throws Exception {
        try {
            PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.tag("h_ids\":[\"b217a321-922f-4aee-b239-ca1b58c6b652\"]}]}")).setDeviceTypes(DeviceTypeData.newBuilder().addDeviceType(DeviceType.IOS).build()).setNotification(Notification.newBuilder().setAlert("alert").build()).setPushOptions(PushOptions.newBuilder().build()).build();
            SchedulePayload schedulePayload = SchedulePayload.newBuilder().setSchedule(Schedule.newBuilder().build()).setPushPayload(pushPayload).build();
            String json = AmplSchedulePayloadSerializerTest.MAPPER.writeValueAsString(schedulePayload);
            String properJson = "{\"schedule\":{},\"push\":{\"audience\":{\"tag\":\"tag\"},\"device_types\":[\"ios\"],\"notification\":{\"alert\":\"alert\"},\"options\":{\"present\":true}}}";
            org.junit.Assert.fail("testNoSchedule_literalMutationString3994 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }
}

