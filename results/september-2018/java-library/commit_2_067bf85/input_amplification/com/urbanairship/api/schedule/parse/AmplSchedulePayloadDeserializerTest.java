package com.urbanairship.api.schedule.parse;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.urbanairship.api.common.parse.APIParsingException;
import com.urbanairship.api.common.parse.DateFormats;
import com.urbanairship.api.push.model.DeviceTypeData;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.push.model.audience.Selectors;
import com.urbanairship.api.push.model.notification.Notification;
import com.urbanairship.api.push.parse.PushObjectMapper;
import com.urbanairship.api.schedule.model.BestTime;
import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.SchedulePayload;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class AmplSchedulePayloadDeserializerTest {
    private static final ObjectMapper MAPPER = PushObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testDeserializationnull35365_failAssert288_literalMutationString37702_failAssert322() throws Exception {
        try {
            try {
                String name = RandomStringUtils.randomAlphabetic(5);
                String json = ((((((((("{" + (((("\"schedule\": {" + "\"scheduled_ime\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.of(name);
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime(null);
                List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
                payloadList.size();
                org.junit.Assert.fail("testDeserializationnull35365 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationnull35365_failAssert288_literalMutationString37702 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialization_literalMutationString35296_failAssert235() throws Exception {
        try {
            String name = RandomStringUtils.randomAlphabetic(5);
            String json = ((((((((("{" + (((("\"schedule\": {" + "\"s(cheduled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.of(name);
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
            payloadList.size();
            org.junit.Assert.fail("testDeserialization_literalMutationString35296 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationnull35364_failAssert287_literalMutationString37365_failAssert385() throws Exception {
        try {
            try {
                String name = RandomStringUtils.randomAlphabetic(5);
                String json = ((((((((("{" + (((("\"schedule\": {" + "\"sch2eduled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.of(null);
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
                List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
                payloadList.size();
                org.junit.Assert.fail("testDeserializationnull35364 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationnull35364_failAssert287_literalMutationString37365 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationnull35364_failAssert287_literalMutationString37346_failAssert293() throws Exception {
        try {
            try {
                String name = RandomStringUtils.randomAlphabetic(5);
                String json = ((((((((("{" + (((("\"schedule\": {" + "") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.of(null);
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
                List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
                payloadList.size();
                org.junit.Assert.fail("testDeserializationnull35364 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationnull35364_failAssert287_literalMutationString37346 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationnull35365_failAssert288_literalMutationString37690_failAssert291() throws Exception {
        try {
            try {
                String name = RandomStringUtils.randomAlphabetic(5);
                String json = ((((((((("{" + (((("\"schedule\": {" + "") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.of(name);
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime(null);
                List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
                payloadList.size();
                org.junit.Assert.fail("testDeserializationnull35365 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationnull35365_failAssert288_literalMutationString37690 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialization_literalMutationString35293_failAssert232() throws Exception {
        try {
            String name = RandomStringUtils.randomAlphabetic(5);
            String json = ((((((((("{" + (((("\"schedule\": {" + "") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.of(name);
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
            payloadList.size();
            org.junit.Assert.fail("testDeserialization_literalMutationString35293 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialization_literalMutationString35294_failAssert233() throws Exception {
        try {
            String name = RandomStringUtils.randomAlphabetic(5);
            String json = ((((((((("{" + (((("\"schedule\": {" + "\"scheduld_time\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.of(name);
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
            payloadList.size();
            org.junit.Assert.fail("testDeserialization_literalMutationString35294 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationnull35365_failAssert288_literalMutationString37699_failAssert347() throws Exception {
        try {
            try {
                String name = RandomStringUtils.randomAlphabetic(5);
                String json = ((((((((("{" + (((("\"schedule\": {" + "\"schedu0led_time\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.of(name);
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime(null);
                List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
                payloadList.size();
                org.junit.Assert.fail("testDeserializationnull35365 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationnull35365_failAssert288_literalMutationString37699 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBestTimeDeserializationnull38_failAssert38_add263null1309_failAssert51() throws Exception {
        try {
            try {
                BestTime bestTime = BestTime.newBuilder().setSendDate(DateTime.now()).build();
                Schedule schedule = Schedule.newBuilder().setBestTime(null).build();
                Notification o_testBestTimeDeserializationnull38_failAssert38_add263__12 = Notification.newBuilder().setAlert("Hello Nerds").build();
                SchedulePayload payload = SchedulePayload.newBuilder().setSchedule(null).setName("BestTimePushPayload").setPushPayload(PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.all()).setNotification(Notification.newBuilder().setAlert("Hello Nerds").build()).build()).build();
                String json = AmplSchedulePayloadDeserializerTest.MAPPER.writeValueAsString(payload);
                SchedulePayload fromJson = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                org.junit.Assert.fail("testBestTimeDeserializationnull38 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testBestTimeDeserializationnull38_failAssert38_add263null1309 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBestTimeDeserializationnull38_failAssert38null281_failAssert45() throws Exception {
        try {
            try {
                BestTime bestTime = BestTime.newBuilder().setSendDate(DateTime.now()).build();
                Schedule schedule = Schedule.newBuilder().setBestTime(null).build();
                SchedulePayload payload = SchedulePayload.newBuilder().setSchedule(null).setName("BestTimePushPayload").setPushPayload(PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.all()).setNotification(Notification.newBuilder().setAlert("Hello Nerds").build()).build()).build();
                String json = AmplSchedulePayloadDeserializerTest.MAPPER.writeValueAsString(payload);
                SchedulePayload fromJson = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                org.junit.Assert.fail("testBestTimeDeserializationnull38 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testBestTimeDeserializationnull38_failAssert38null281 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBestTimeDeserializationnull37_failAssert37() throws Exception {
        try {
            BestTime bestTime = BestTime.newBuilder().setSendDate(DateTime.now()).build();
            Schedule schedule = Schedule.newBuilder().setBestTime(null).build();
            SchedulePayload payload = SchedulePayload.newBuilder().setSchedule(schedule).setName("BestTimePushPayload").setPushPayload(PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.all()).setNotification(Notification.newBuilder().setAlert("Hello Nerds").build()).build()).build();
            String json = AmplSchedulePayloadDeserializerTest.MAPPER.writeValueAsString(payload);
            SchedulePayload fromJson = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            org.junit.Assert.fail("testBestTimeDeserializationnull37 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutName_literalMutationString1837_failAssert67() throws Exception {
        try {
            String json = "{" + (((((((("\"schedule\": {" + "\"scheduled_tiYme\": \"2013-05-05 00:00:01\"") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.<String>absent();
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            org.junit.Assert.fail("testDeserializationWithoutName_literalMutationString1837 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutNamenull1885_failAssert105_literalMutationString3544_failAssert147() throws Exception {
        try {
            try {
                String json = "{" + (((((((("\"schedule\": {" + "\"scheduEled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.<String>absent();
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime(null);
                org.junit.Assert.fail("testDeserializationWithoutNamenull1885 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationWithoutNamenull1885_failAssert105_literalMutationString3544 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutName_literalMutationString1833_failAssert63() throws Exception {
        try {
            String json = "{" + (((((((("\"schedule\": {" + "") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.<String>absent();
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            org.junit.Assert.fail("testDeserializationWithoutName_literalMutationString1833 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutNamenull1885_failAssert105_literalMutationString3542_failAssert107() throws Exception {
        try {
            try {
                String json = "{" + (((((((("\"schedule\": {" + "") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.<String>absent();
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime(null);
                org.junit.Assert.fail("testDeserializationWithoutNamenull1885 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationWithoutNamenull1885_failAssert105_literalMutationString3542 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidName_literalMutationString15987_failAssert167() throws Exception {
        try {
            String json = "{" + ((((((((((((("\"schedule\": {" + "\"scheduled_tim\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"") + " ") + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            org.junit.Assert.fail("testInvalidName_literalMutationString15987 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidName_literalMutationString15989_failAssert169() throws Exception {
        try {
            String json = "{" + ((((((((((((("\"schedule\": {" + "\"sUcheduled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"") + " ") + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            org.junit.Assert.fail("testInvalidName_literalMutationString15989 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidName_literalMutationString15985_failAssert165() throws Exception {
        try {
            String json = "{" + ((((((((((((("\"schedule\": {" + "") + "},") + "\"name\": ") + "\"") + " ") + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            org.junit.Assert.fail("testInvalidName_literalMutationString15985 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidName_literalMutationString15986_failAssert166() throws Exception {
        try {
            String json = "{" + ((((((((((((("\"schedule\": {" + "\"sch_duled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"") + " ") + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            org.junit.Assert.fail("testInvalidName_literalMutationString15986 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }
}

