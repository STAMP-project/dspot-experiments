package com.urbanairship.api.schedule;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.urbanairship.api.common.parse.APIParsingException;
import com.urbanairship.api.common.parse.DateFormats;
import com.urbanairship.api.push.parse.PushObjectMapper;
import com.urbanairship.api.schedule.model.SchedulePayload;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;


public class AmplSchedulePayloadDeserializerTest {
    private static final ObjectMapper MAPPER = PushObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testDeserializationnull35579_failAssert249_literalMutationString36531_failAssert251() throws Exception {
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
                org.junit.Assert.fail("testDeserializationnull35579 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationnull35579_failAssert249_literalMutationString36531 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialization_literalMutationString35510_failAssert195() throws Exception {
        try {
            String name = RandomStringUtils.randomAlphabetic(5);
            String json = ((((((((("{" + (((("\"schedule\": {" + "\"schAeduled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.of(name);
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
            payloadList.size();
            org.junit.Assert.fail("testDeserialization_literalMutationString35510 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialization_literalMutationString35511_failAssert196() throws Exception {
        try {
            String name = RandomStringUtils.randomAlphabetic(5);
            String json = ((((((((("{" + (((("\"schedule\": {" + "\"scheduledtime\": \"2013-05-05 00:00:01\"") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.of(name);
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
            payloadList.size();
            org.junit.Assert.fail("testDeserialization_literalMutationString35511 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialization_literalMutationString35562_failAssert243_literalMutationString36525_failAssert351() throws Exception {
        try {
            try {
                String name = RandomStringUtils.randomAlphabetic(5);
                String json = ((((((((("{" + (((("\"schedule\": {" + "") + "},") + "\"name\": ") + "\"")) + name) + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}";
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.of(name);
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime("");
                List<SchedulePayload> payloadList = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, new TypeReference<List<SchedulePayload>>() {});
                payloadList.size();
                org.junit.Assert.fail("testDeserialization_literalMutationString35562 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("testDeserialization_literalMutationString35562_failAssert243_literalMutationString36525 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserialization_literalMutationString35507_failAssert192() throws Exception {
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
            org.junit.Assert.fail("testDeserialization_literalMutationString35507 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationnull35578_failAssert248_literalMutationString36189_failAssert254() throws Exception {
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
                org.junit.Assert.fail("testDeserializationnull35578 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationnull35578_failAssert248_literalMutationString36189 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutNamenull62_failAssert52_literalMutationString1756_failAssert74() throws Exception {
        try {
            try {
                String json = "{" + (((((((("\"schedule\": {" + "\"cheduled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.<String>absent();
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime(null);
                org.junit.Assert.fail("testDeserializationWithoutNamenull62 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationWithoutNamenull62_failAssert52_literalMutationString1756 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutName_literalMutationString10_failAssert10() throws Exception {
        try {
            String json = "{" + (((((((("\"schedule\": {" + "") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.<String>absent();
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            org.junit.Assert.fail("testDeserializationWithoutName_literalMutationString10 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutNamenull62_failAssert52_literalMutationString1737_failAssert54() throws Exception {
        try {
            try {
                String json = "{" + (((((((("\"schedule\": {" + "") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.<String>absent();
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime(null);
                org.junit.Assert.fail("testDeserializationWithoutNamenull62 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testDeserializationWithoutNamenull62_failAssert52_literalMutationString1737 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutName_literalMutationString49_failAssert47_literalMutationString480_failAssert99() throws Exception {
        try {
            try {
                String json = "{" + (((((((("\"schedule\": {" + "") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
                SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
                payload.getName();
                Optional.<String>absent();
                payload.getSchedule().getScheduledTimestamp();
                DateFormats.DATE_PARSER.parseDateTime("");
                org.junit.Assert.fail("testDeserializationWithoutName_literalMutationString49 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("testDeserializationWithoutName_literalMutationString49_failAssert47_literalMutationString480 should have thrown APIParsingException");
        } catch (APIParsingException expected_1) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDeserializationWithoutName_literalMutationString15_failAssert14() throws Exception {
        try {
            String json = "{" + (((((((("\"schedule\": {" + "\"s(heduled_time\": \"2013-05-05 00:00:01\"") + "},") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            SchedulePayload payload = AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            payload.getName();
            Optional.<String>absent();
            payload.getSchedule().getScheduledTimestamp();
            DateFormats.DATE_PARSER.parseDateTime("2013-05-05 00:00:01");
            org.junit.Assert.fail("testDeserializationWithoutName_literalMutationString15 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidName_literalMutationString16137_failAssert124() throws Exception {
        try {
            String json = "{" + ((((((((((((("\"schedule\": {" + "") + "},") + "\"name\": ") + "\"") + " ") + "\"") + ",") + "\"push\": {") + "\"audience\" : \"all\",") + "\"device_types\" : [ \"ios\" ],") + "\"notification\" : { \"alert\" : \"derp\" }") + "}") + "}");
            AmplSchedulePayloadDeserializerTest.MAPPER.readValue(json, SchedulePayload.class);
            org.junit.Assert.fail("testInvalidName_literalMutationString16137 should have thrown APIParsingException");
        } catch (APIParsingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set.", expected.getMessage());
        }
    }
}

