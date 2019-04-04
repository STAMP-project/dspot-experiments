package com.urbanairship.api.schedule;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.urbanairship.api.client.ResponseParser;
import com.urbanairship.api.push.model.DeviceType;
import com.urbanairship.api.push.model.DeviceTypeData;
import com.urbanairship.api.push.model.PushPayload;
import com.urbanairship.api.push.model.audience.Selectors;
import com.urbanairship.api.push.model.notification.Notifications;
import com.urbanairship.api.schedule.model.Schedule;
import com.urbanairship.api.schedule.model.SchedulePayload;
import com.urbanairship.api.schedule.model.ScheduleResponse;
import com.urbanairship.api.schedule.parse.ScheduleObjectMapper;
import java.io.IOException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;


public class AmplScheduleRequestTest {
    PushPayload pushPayload = PushPayload.newBuilder().setAudience(Selectors.all()).setDeviceTypes(DeviceTypeData.of(DeviceType.IOS)).setNotification(Notifications.alert("Foo")).build();

    DateTime dateTime = DateTime.now(DateTimeZone.UTC).plusSeconds(60);

    Schedule schedule = Schedule.newBuilder().setScheduledTimestamp(dateTime).build();

    SchedulePayload schedulePayload = SchedulePayload.newBuilder().setPushPayload(pushPayload).setSchedule(schedule).build();

    ScheduleRequest scheduleRequest = ScheduleRequest.newRequest(schedule, pushPayload);

    ScheduleRequest updateScheduleRequest = ScheduleRequest.newUpdateRequest(schedule, pushPayload, "id");

    @Test(timeout = 10000)
    public void testScheduleParser_literalMutationString55_failAssert24() throws Exception {
        try {
            ResponseParser responseParser = new ResponseParser<ScheduleResponse>() {
                @Override
                public ScheduleResponse parse(String response) throws IOException {
                    return ScheduleObjectMapper.getInstance().readValue(response, ScheduleResponse.class);
                }
            };
            String response = "{\"ok\" : true, \"operation_id\" : \"OpID\", " + ((((((((((("\"schedule_urls\" : [\"ScheduleURL\"], " + "\"schedule_ids\" : [\"ScheduleID\"], ") + "\"schedules\" : [\n") + "      {\n") + "         \"url\" : \"http://go.urbanairship/api/schedules/2d69320c-3c91-5241-fac4-248269eed109\",\n") + "         \"schedule\" : { \"scheduled_Rtime\": \"2013-04-01T18:45:00\" },\n") + "         \"push\" : { \"audience\":{ \"tag\": \"spoaaaarts\" },\n") + "            \"notification\": { \"alert\": \"Booyah!\" },\n") + "            \"device_types\": \"all\" },\n") + "         \"push_ids\" : [ \"8f18fcb5-e2aa-4b61-b190-43852eadb5ef\" ]\n") + "      }\n") + "   ]}");
            scheduleRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            updateScheduleRequest.getResponseParser().parse(response);
            responseParser.parse(response);
            org.junit.Assert.fail("testScheduleParser_literalMutationString55 should have thrown JsonMappingException");
        } catch (JsonMappingException expected) {
            Assert.assertEquals("Error parsing schedule object. Either scheduled_time or local_scheduled_time must be set. (through reference chain: java.util.ArrayList[0])", expected.getMessage());
        }
    }
}

