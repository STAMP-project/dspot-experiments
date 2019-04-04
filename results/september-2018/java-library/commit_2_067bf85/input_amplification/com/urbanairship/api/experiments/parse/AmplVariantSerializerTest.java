package com.urbanairship.api.experiments.parse;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanairship.api.experiments.model.Variant;
import com.urbanairship.api.experiments.model.VariantPushPayload;
import com.urbanairship.api.push.model.notification.Notification;
import com.urbanairship.api.schedule.model.Schedule;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AmplVariantSerializerTest {
    private static final ObjectMapper MAPPER = ExperimentObjectMapper.getInstance();

    @Test(timeout = 10000)
    public void testVariantSerializer_add14null2148_failAssert20() throws Exception {
        try {
            DateTime dateTime = DateTime.parse("2017-07-27T18:27:25.000Z");
            Schedule o_testVariantSerializer_add14__3 = Schedule.newBuilder().setScheduledTimestamp(null).build();
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).build();
            Variant variant = Variant.newBuilder().setSchedule(schedule).setPushPayload(VariantPushPayload.newBuilder().setNotification(Notification.newBuilder().setAlert("Hello there").build()).build()).build();
            String variantSerialized = AmplVariantSerializerTest.MAPPER.writeValueAsString(variant);
            Variant variantFromJson = AmplVariantSerializerTest.MAPPER.readValue(variantSerialized, Variant.class);
            Assert.assertEquals(variantFromJson, variant);
            org.junit.Assert.fail("testVariantSerializer_add14null2148 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testVariantSerializernull32_failAssert7() throws Exception {
        try {
            DateTime dateTime = DateTime.parse("2017-07-27T18:27:25.000Z");
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(null).build();
            Variant variant = Variant.newBuilder().setSchedule(schedule).setPushPayload(VariantPushPayload.newBuilder().setNotification(Notification.newBuilder().setAlert("Hello there").build()).build()).build();
            String variantSerialized = AmplVariantSerializerTest.MAPPER.writeValueAsString(variant);
            Variant variantFromJson = AmplVariantSerializerTest.MAPPER.readValue(variantSerialized, Variant.class);
            Assert.assertEquals(variantFromJson, variant);
            org.junit.Assert.fail("testVariantSerializernull32 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testVariantSerializer_add14null2149_failAssert19() throws Exception {
        try {
            DateTime dateTime = DateTime.parse("2017-07-27T18:27:25.000Z");
            Schedule o_testVariantSerializer_add14__3 = Schedule.newBuilder().setScheduledTimestamp(dateTime).build();
            Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(null).build();
            Variant variant = Variant.newBuilder().setSchedule(schedule).setPushPayload(VariantPushPayload.newBuilder().setNotification(Notification.newBuilder().setAlert("Hello there").build()).build()).build();
            String variantSerialized = AmplVariantSerializerTest.MAPPER.writeValueAsString(variant);
            Variant variantFromJson = AmplVariantSerializerTest.MAPPER.readValue(variantSerialized, Variant.class);
            Assert.assertEquals(variantFromJson, variant);
            org.junit.Assert.fail("testVariantSerializer_add14null2149 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Either scheduled_time, local_scheduled_time, or best time must be set.", expected.getMessage());
        }
    }
}

